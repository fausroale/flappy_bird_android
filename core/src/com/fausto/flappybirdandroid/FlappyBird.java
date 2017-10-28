package com.fausto.flappybirdandroid;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    enum GameState {
        GAME_NOT_STARTED,
        GAME_STARTED,
        GAME_OVER
    }

    SpriteBatch batch;
    Texture background;
    Texture[] bird;
    Texture topTube;
    Texture bottomTube;
    Texture gameOverImg;
    Animation<Texture> flapAnimation;
    GameState gameState;
    ShapeRenderer shapeRenderer;
    Circle birdCircle;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;
    Random random;
    Sound flapSound;
    FreeTypeFontGenerator fontGenerator;
    BitmapFont font;

    float stateTime = 0;
    float birdY;
    float velocity = 0;
    float gravity = 2;

    int score = 0;
    int scoringTube = 0;
    float gap = 450;
    float tubeX[];
    float tubeOffset[];
    float tubeVelocity = 6;
    int numberOfTubes = 4;
    float distanceBetweenTubes;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];
        gameState = GameState.GAME_NOT_STARTED;
        batch = new SpriteBatch();
        background = new Texture("background.png");
        bird = new Texture[2];
        bird[0] = new Texture("bird_1.png");
        bird[1] = new Texture("bird_2.png");
        gameOverImg = new Texture("game_over.png");
        flapAnimation = new Animation<Texture>(0.25f, bird);
        flapSound = Gdx.audio.newSound(Gdx.files.internal("audio/flap_sound.mp3"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) (50 * Gdx.graphics.getDensity());
        parameter.color = Color.WHITE;
        parameter.shadowColor = Color.BLACK;
        parameter.shadowOffsetX = (int) (2f * Gdx.graphics.getDensity());
        parameter.shadowOffsetY = (int) (2f * Gdx.graphics.getDensity());
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto_font.ttf"));
        font = fontGenerator.generateFont(parameter);

        // Tubes
        topTube = new Texture("top_tube.png");
        bottomTube = new Texture("bottom_tube.png");
        random = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

        tubeX = new float[numberOfTubes];
        tubeOffset = new float[numberOfTubes];

        resetValues();
    }

    private void resetValues() {
        score = 0;
        scoringTube = 0;
        velocity = -30;

        birdY = (Gdx.graphics.getHeight() + bird[0].getHeight()) / 2;

        for (int i = 0; i < numberOfTubes; i++) {
            tubeX[i] = (Gdx.graphics.getWidth() - topTube.getWidth()) / 2 + i * distanceBetweenTubes + Gdx.graphics.getWidth();
            tubeOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 100);
        }
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == GameState.GAME_STARTED) {

            // Scoring system
            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2 - topTube.getWidth()) {
                score++;
                Gdx.app.log("Score", String.valueOf(score));
                if (scoringTube < numberOfTubes - 1) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
            }

            // Make bird fly on tap
            if (Gdx.input.justTouched()) {
                velocity = -30;
                stateTime = 0;
                flapSound.play();
            }

            // Set tubes position and wrap them with a rectangle shape
            for (int i = 0; i < numberOfTubes; i++) {
                if (tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 100);
                } else {
                    tubeX[i] -= tubeVelocity;
                }
                batch.draw(topTube, tubeX[i], (Gdx.graphics.getHeight() + gap) / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], (Gdx.graphics.getHeight() - gap) / 2 - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangles[i] = new Rectangle(tubeX[i], (Gdx.graphics.getHeight() + gap) / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], (Gdx.graphics.getHeight() - gap) / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            }

            if (birdY > 0 || velocity < 0) {
                velocity += gravity;
                birdY -= velocity;
            } else {
                gameState = GameState.GAME_OVER;
            }

//            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            shapeRenderer.setColor(Color.RED);
//            shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
//            shapeRenderer.setColor(Color.BLUE);
//
            for (int i = 0; i < numberOfTubes; i++) {
//                shapeRenderer.rect(topTubeRectangles[i].x, topTubeRectangles[i].y, topTubeRectangles[i].getWidth(), topTubeRectangles[i].getHeight());
//                shapeRenderer.rect(bottomTubeRectangles[i].x, bottomTubeRectangles[i].y, bottomTubeRectangles[i].getWidth(), bottomTubeRectangles[i].getHeight());
//
                if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
                    Gdx.app.log("Collision", "Yes!");
                    gameState = GameState.GAME_OVER;
                    break;
                }
            }
//
//            shapeRenderer.end();

        } else if (gameState == GameState.GAME_NOT_STARTED) {
            if (Gdx.input.justTouched()) {
                gameState = GameState.GAME_STARTED;
                resetValues();
                flapSound.play();
            }
        } else if (gameState == GameState.GAME_OVER) {
            batch.draw(gameOverImg, Gdx.graphics.getWidth() / 2 - gameOverImg.getWidth(), Gdx.graphics.getHeight() / 2 - gameOverImg.getHeight(), gameOverImg.getWidth() * 2, gameOverImg.getHeight() * 2);
            if (Gdx.input.justTouched()) {
                gameState = GameState.GAME_STARTED;
                resetValues();
            }
        }

        stateTime = stateTime + Gdx.graphics.getDeltaTime();
        Texture birdCurrentState = flapAnimation.getKeyFrame(stateTime, true);
        batch.draw(birdCurrentState, (Gdx.graphics.getWidth() - birdCurrentState.getWidth()) / 2, birdY);
        font.draw(batch, String.valueOf(score), 100, 200);
        batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birdCurrentState.getHeight() / 2, birdCurrentState.getWidth() / 2);
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
    }
}
