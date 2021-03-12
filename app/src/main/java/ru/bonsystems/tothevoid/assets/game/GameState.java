package ru.bonsystems.tothevoid.assets.game;

import ru.bonsystems.tothevoid.platform.extend.DataStorage;

/**
 * Created by Kolomeytsev Anton on 23.02.2016.
 */
public class GameState {
    public static float gameSpeed = 1f;
    public static float score = 0;
    public static int highscore = 0;
    private static boolean isHighscoreNow = false;
    private static ScoreHalfThousandListener scoreHalfThousandListener;

    private static GameState ourInstance;
    private static float accumulatedScore;
    public static boolean jokeDoge;

    public static GameState getInstance() {
        if (ourInstance == null) {
            ourInstance = new GameState();
        }
        return ourInstance;
    }

    private GameState() {
    }

    public static void reset() {
        gameSpeed = 1f;
        score = 0f;
        jokeDoge = false;
        String someHighscore = DataStorage.getInstance().get("highscore");
        highscore = (someHighscore == null) ? 0 : Integer.parseInt(someHighscore);
        accumulatedScore = 0f;
        isHighscoreNow = false;
    }

    public static void incrementScore(float increment) {
        GameState.score += increment;
        if (score > highscore) {
            highscore = (int) score;
            isHighscoreNow = true;
        }

        accumulatedScore += increment;
        if (accumulatedScore > 500f) {
            if (scoreHalfThousandListener != null)
                scoreHalfThousandListener.OnScoreEqualsHalfThousand();
            accumulatedScore = 0f;
        }
    }

    public static void setScoreHalfThousandListener(ScoreHalfThousandListener scoreHalfThousandListener) {
        GameState.scoreHalfThousandListener = scoreHalfThousandListener;
    }

    interface ScoreHalfThousandListener {
        void OnScoreEqualsHalfThousand();
    }

    public static boolean isHighscoreNow() {
        return isHighscoreNow;
    }
}
