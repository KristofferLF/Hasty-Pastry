package com.mygdx.hastypastry.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygdx.hastypastry.enums.ScreenEnum;
import com.mygdx.hastypastry.singletons.Assets;
import com.mygdx.hastypastry.singletons.ScreenManager;

public class ExitButton extends TextButton {
    public ExitButton(String text /*, final ScreenEnum navigateTo, final Object... params*/) {
        super(text , Assets.instance.getManager().get(Assets.uiSkin), "container_blue");
        this.addListener(
                new InputListener() {
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        System.exit(0);
                        return false;
                    }
                }
        );
    }
}
