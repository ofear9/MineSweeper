package com.example.ranendelman.minesweeper.GameLogic;

import com.example.ranendelman.minesweeper.Activities.PlayGameActivity;


/**
 * Created by RanEndelman on 26/11/2016.
 */

/**
 * The Game class
 */
public class Game {
    int matrixSize;
    private Board mBoard;
    int numOfFlags;
    private int numOfMaxFlags;

    /**
     * The class constructor
     */
    public Game(int matrixSize, int numOfBombs, int numOfMaxFlags) {
        this.matrixSize = matrixSize;
        this.mBoard = new Board(matrixSize, numOfBombs);
        this.numOfFlags = 0;
        this.numOfMaxFlags = numOfMaxFlags;
    }

    /**
     * This method mark flag on the board and return the state of the game - the marking flag
     * may be leads to a WIN
     */
    public PlayGameActivity.gameState markFlag(int position) {

        if ((mBoard.mTile[position].getmState() == Tile.TileState.NONE ||
                mBoard.mTile[position].getmState() == Tile.TileState.BOMB) &&
                !mBoard.mTile[position].isRevealed && (numOfFlags < numOfMaxFlags)) {
            mBoard.mTile[position].setmState(Tile.TileState.FLAG);
            this.numOfFlags++;
        } else if (mBoard.mTile[position].getmState() == Tile.TileState.FLAG) {
            mBoard.mTile[position].setmState(Tile.TileState.NONE);
            this.numOfFlags--;
        }

        if (mBoard.numOfTiles - mBoard.getRevealedCount() == mBoard.getBombCount() &&
                numOfFlags == mBoard.getBombCount()) {
            mBoard.setBoardToWinMode();
            return PlayGameActivity.gameState.WIN;
        }

        return PlayGameActivity.gameState.NONE;
    }

    /**
     * This method reveal tile on the board and return the state of the game - the reveal
     * may be leads to a WIN or to a LOST
     */
    public PlayGameActivity.gameState playTile(int position) {

        if (!mBoard.mTile[position].isRevealed && !mBoard.mTile[position].isBomb) {
            mBoard.reveal(mBoard.mTile[position]);
        }

        if (mBoard.numOfTiles - mBoard.getRevealedCount() == mBoard.getBombCount() &&
                numOfFlags == mBoard.getBombCount()) {
            mBoard.setBoardToWinMode();
            return PlayGameActivity.gameState.WIN;
        }

        if (mBoard.mTile[position].isBomb) {
            mBoard.mTile[position].setExploaded(true);
            mBoard.setBoardToLoseMode();
            mBoard.showBombs();
            return PlayGameActivity.gameState.LOSE;
        }
        return PlayGameActivity.gameState.NONE;
    }

    /**
     * This method return the board
     */
    public Board getmBoard() {
        return mBoard;
    }

    /**
     * This method generate new board
     */
    public void start() {
        this.mBoard.reset(matrixSize);
    }

    /**
     * This method return the number of flags available
     */
    public int getNumOfFlags() {
        return this.numOfFlags;
    }

    /**
     * This method sets the number of MAX flags available
     */
    public void setNumOfMaxFlags(int numOfMaxFlags) {
        this.numOfMaxFlags = numOfMaxFlags;
    }

    /**
     * This method gets the number of MAX flags available
     */
    public int getNumOfMaxFlags() {
        return this.numOfMaxFlags;
    }
}
