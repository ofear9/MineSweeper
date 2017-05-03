package com.example.ranendelman.minesweeper.GameLogic;

/**
 * Created by ofear on 1/12/2017.
 */

public enum gameLevel {
    BEGINNER, MEDIUM, HARD;

    @Override
    public String toString() {
        switch (this) {
            default:
                return null;
            case BEGINNER:
                return "beginner";

            case MEDIUM:
                return "medium";

            case HARD:
                return "hard";
        }
    }
}