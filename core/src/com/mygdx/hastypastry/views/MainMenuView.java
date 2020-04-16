package com.mygdx.hastypastry.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hastypastry.Config;
import com.mygdx.hastypastry.singletons.PlayerPreferences;
import com.mygdx.hastypastry.enums.ScreenEnum;
import com.mygdx.hastypastry.singletons.Assets;
import com.mygdx.hastypastry.ui.ExitButton;
import com.mygdx.hastypastry.ui.MenuButton;

public class MainMenuView extends BaseView {
    private BitmapFont title1Font, title2Font;
    private Label titleLabel1, titleLabel2;
    protected Texture background = Assets.instance.getManager().get(Assets.bgMenu);
    private MenuButton singlePlayerBtn;
    private MenuButton multiPlayerBtn;
    private MenuButton preferenceButton;
    //private Music menuMusic;
    private ExitButton exitButton;
    private PlayerPreferences playerPreferences = new PlayerPreferences();

    public MainMenuView() {
        super();
    }

    @Override
    public void buildStage() {
        // Title generation
        title1Font = generateFont("pixelfont.TTF", 48);
        title2Font = generateFont("pixelfont.TTF", 40);
        Label.LabelStyle title1LabelStyle = new Label.LabelStyle(title1Font, Color.BLACK);
        Label.LabelStyle title2LabelStyle = new Label.LabelStyle(title2Font, Color.BLACK);

        titleLabel1 = new Label("HASTY", title1LabelStyle);
        titleLabel2 = new Label("PASTRY", title2LabelStyle);

        //Create Table
        Table mainTable = new Table();

        //Set table to fill stage
        mainTable.setFillParent(true);

        //Set padding
        mainTable.padTop(25);
        mainTable.padLeft(50);
        mainTable.padRight(50);

        //Set alignment of contents in the table.
        mainTable.top();

        //Create Buttons
        singlePlayerBtn = new MenuButton("Single Player", ScreenEnum.LEVELSELECT);
        multiPlayerBtn = new MenuButton("Multiplayer", ScreenEnum.LOGIN);
        preferenceButton = new MenuButton("Settings", ScreenEnum.PREFERENCES, playerPreferences);
        exitButton = new ExitButton("Exit Game");

        //Add buttons to table
        mainTable.add(titleLabel1);
        mainTable.row();
        mainTable.add(titleLabel2);
        mainTable.row();
        mainTable.add(singlePlayerBtn).growX().pad(20).padTop(25);
        mainTable.row();
        mainTable.add(multiPlayerBtn).growX().pad(20).padTop(5);
        mainTable.row();
        mainTable.add(preferenceButton).growX().pad(20).padTop(5);
        mainTable.row();
        mainTable.add(exitButton).growX().pad(20).padTop(5);

        // Add table to stage
        this.ui.addActor(mainTable);


    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        batch.draw(this.background, 0, 0, Config.WORLD_WIDTH, Config.WORLD_HEIGHT);
        batch.end();
    }
}
