package io.github.alyphen.ld32;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class LD32 extends Game {

	private Music introMusic;
	private Music gameMusic;
	
	@Override
	public void create () {
		introMusic = Gdx.audio.newMusic(Gdx.files.internal("reaches_of_space.ogg"));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("they_come.ogg"));
		setScreen(new IntroScreen1(this));
	}

	public Music getIntroMusic() {
		return introMusic;
	}

	public Music getGameMusic() {
		return gameMusic;
	}

	@Override
	public void dispose() {
		super.dispose();
		getIntroMusic().dispose();
		getGameMusic().dispose();
	}
}
