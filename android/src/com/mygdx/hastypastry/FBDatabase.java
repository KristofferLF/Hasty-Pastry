package com.mygdx.hastypastry;

import com.badlogic.gdx.Gdx;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mygdx.hastypastry.interfaces.HastyPastryDatabase;
import com.mygdx.hastypastry.models.Game;
import com.mygdx.hastypastry.models.Lobby;
import com.mygdx.hastypastry.models.Match;
import com.mygdx.hastypastry.models.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FBDatabase implements HastyPastryDatabase {
    private final DatabaseReference lobbyRef = FirebaseDatabase.getInstance().getReference("lobby");
    private final DatabaseReference matchesRef = FirebaseDatabase.getInstance().getReference("match");
    private ChildEventListener lobbyListener;
    private ValueEventListener challengeListener;
    private ValueEventListener responseListener;
    private ValueEventListener drawingListener;


    public void subscribeLobbyList(final Lobby lobby) {
        lobbyListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User newUser = dataSnapshot.getValue(User.class);
                // FBID is used to remove the user from FB when exiting the lobby.
                newUser.setFBID(dataSnapshot.getKey());
                lobby.addUser(newUser);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String leavingUserFBID = dataSnapshot.getKey();
                for (User u : lobby.getLobbyList()) {
                    if (u.getFBID().equals(leavingUserFBID)) {
                        lobby.removeUser(u);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        lobbyRef.addChildEventListener(lobbyListener);
    }

    @Override
    public void joinLobby(final Lobby lobby, User user) {
        DatabaseReference userRef = lobbyRef.push();
        user.setFBID(userRef.getKey());
        userRef.setValue(user);
        challengeListener = new ValueEventListener() {
            // Listen for challenges
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Match challenge = dataSnapshot.getValue(Match.class);
                if (challenge != null) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            lobby.receivedChallenge(challenge);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        userRef.child("challenge").addValueEventListener(challengeListener);
    }

    public void exitLobby(String FBID) {
        lobbyRef.removeEventListener(lobbyListener);
        lobbyRef.child(FBID).child("challenge").removeEventListener(challengeListener);
        lobbyRef.child(FBID).removeValue();
    }

    @Override
    public void challengePlayer(final Lobby lobby, final User opponent, User player) {
        // Creates a new match and adds it to firebase.
        final String matchID = player.getFBID(); // We use challengers ID as matchID.
        Match match = new Match(matchID, player.getName(), opponent.getName());
        matchesRef.child(matchID).setValue(match);

        // Setting player's ready field to false to prevent new challenges.
        lobbyRef.child(player.getFBID()).child("ready").setValue(false);

        // Updating the challenged players ready and challenger fields.
        lobbyRef.child(opponent.getFBID()).child("ready").setValue(false);
        lobbyRef.child(opponent.getFBID()).child("challenge").setValue(match);


        responseListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Opponent accepts challange, by selecting a Level or declines by removing the match.
                final Match match = dataSnapshot.getValue(Match.class);
                if (match != null) {
                    if (match.getLevel() != null) {
                        if (match.getLevel().matches("Level \\d+")) {
                            // Opponent accepts
                            System.out.println("Opponent accepts!");
                            matchesRef.child(matchID).removeEventListener(responseListener);
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    lobby.startGame(match, true);
                                }
                            });
                        } else {
                            // Invalid level
                            System.out.println("Error: Invalid level");
                        }
                    } else {
                        System.out.println("Initializing responseListener");
                    }
                } else {
                    // Opponent declines
                    System.out.println("Opponent declines!");
                    matchesRef.child(matchID).removeEventListener(responseListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        matchesRef.child(matchID).addValueEventListener(responseListener);
    }

    @Override
    public void acceptChallenge(Match match) {
        System.out.println("ACCEPT!");
        matchesRef.child(match.getMatchID()).setValue(match);
    }

    @Override
    public void ready(final Game game) {
        // Refrence to match
        DatabaseReference matchRef = matchesRef.child(game.getMatch().getMatchID());

        // Drawing references
        final DatabaseReference playerDrawingRef, opponentDrawingRef;
//        if (game.getPlayer().getUser().isChallenger()) {
        if (game.playerIsChallenger()) {
            playerDrawingRef = matchRef.child("challengerDrawing");
            opponentDrawingRef = matchRef.child("challengedDrawing");
        } else {
            opponentDrawingRef = matchRef.child("challengerDrawing");
            playerDrawingRef = matchRef.child("challengedDrawing");
        }

        // Upload drawing
        playerDrawingRef.setValue(game.getPlayer().getDrawing().serializedLines());

        // Listen for opponents drawing
        drawingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<List<String>>> genericTypeIndicator = new GenericTypeIndicator<List<List<String>>>() {};
                final List<List<String>> opponentDrawing = dataSnapshot.getValue(genericTypeIndicator);
                if (opponentDrawing != null) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            opponentDrawingRef.removeEventListener(drawingListener);
                            game.receivedDrawing(opponentDrawing);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        opponentDrawingRef.addValueEventListener(drawingListener);
    }

    @Override
    public void exitMatch(final Game game) {
        DatabaseReference matchRef = matchesRef.child(game.getMatch().getMatchID());
        matchRef.removeValue();
    }

    @Override
    public void declineChallenge(Match match, User player) {
        System.out.println("DECLINE!");
        // Remove match.
        matchesRef.child(match.getMatchID()).removeValue();

        // Update opponent: Set opponent to ready
        lobbyRef.child(match.getMatchID()).child("ready").setValue(true);

        // Update player: Remove challenger and set ready to true.
        player.setChallenger(null);
        player.setReady(true);
        lobbyRef.child(player.getFBID()).setValue(player);
    }
}