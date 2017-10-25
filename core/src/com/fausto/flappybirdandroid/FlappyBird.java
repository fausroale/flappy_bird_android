package com.fausto.flappybirdandroid;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyBird extends ApplicationAdapter {

	enum GameState {
		GAME_NOT_STARTED,
		GAME_STARTED
	}

	SpriteBatch batch;
	Texture background;
	Texture[] bird;
	Animation<Texture> flapAnimation;
	GameState gameState;

	float stateTime = 0;
	float birdY;
	float velocity = 0;
	float gravity = 2;
	
	@Override
	public void create () {
		gameState = GameState.GAME_NOT_STARTED;
		batch = new SpriteBatch();
		background = new Texture("background.png");
		bird = new Texture[2];

		bird[0] = new Texture("bird_1.png");
		bird[1] = new Texture("bird_2.png");
		flapAnimation = new Animation<Texture>(0.25f, bird);
		birdY = (Gdx.graphics.getHeight() - bird[0].getHeight())/2;
	}

	@Override
	public void render () {
		stateTime = stateTime + Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == GameState.GAME_STARTED) {

			if(Gdx.input.justTouched()) {
				velocity = -30;
			}

			if (birdY > 0 || velocity < 0) {
				velocity += gravity;
				birdY -= velocity;
			}

		} else {
			if(Gdx.input.justTouched()) {
				gameState = GameState.GAME_STARTED;
				velocity = -30;
			}
		}


		Texture birdCurrentState = flapAnimation.getKeyFrame(stateTime, true);
		batch.draw(birdCurrentState, (Gdx.graphics.getWidth() - birdCurrentState.getWidth())/2, birdY);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
