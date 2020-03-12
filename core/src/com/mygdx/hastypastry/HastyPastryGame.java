package com.mygdx.hastypastry;

import com.badlogic.gdx.Game;
import com.mygdx.hastypastry.enums.ScreenEnum;
import com.mygdx.hastypastry.singletons.ScreenManager;

public class HastyPastryGame extends Game {
	// Assets can't be static because of how static references are handled on android :(
	private Assets assets = new Assets();

	@Override
	public void create () {
		ScreenManager.getInstance().initialize(this, assets);
		ScreenManager.getInstance().showScreen( ScreenEnum.MAIN_MENU);
	}

	@Override
	public void dispose() {
		// Disposing AssetManager disposes everything. Easy =)
		assets.dispose();
	}
}