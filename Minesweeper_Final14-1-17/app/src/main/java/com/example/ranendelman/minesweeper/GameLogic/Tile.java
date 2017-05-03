package com.example.ranendelman.minesweeper.GameLogic;

import java.util.ArrayList;

/**
 * Created by RanEndelman on 26/11/2016.
 */

/**
 * The Tile class
 */
public class Tile {
    public enum TileState {
        BOMB, NONE, NUMBER, FLAG;

        @Override
        public String toString() {
            switch (this) {

                case NONE:
                default:
                    return "";
                case BOMB:
                    return "bomb";
                case NUMBER:
                    return "number";
                case FLAG:
                    return "markFlag";
            }
        }
    }

    public boolean isRevealed;
    public boolean isBomb;
    public boolean isCheat;
    public boolean isExploaded;
    public boolean winMode;
    public boolean loseMode;
    int position;
    private ArrayList<Tile> neighbors;
    private int bombNeighborCount;
    private TileState mState;

    /**
     * The class constructor
     */
    public Tile(int position) {
        super();
        this.position = position;
        this.isBomb = false;
        this.isRevealed = false;
        this.isCheat = false;
        this.bombNeighborCount = 0;
        this.neighbors = new ArrayList<Tile>();
        this.mState = TileState.NONE;

    }

    /**
     * This method return add neighbor to tiles neighbors list
     */
    public void addNeighbor(Tile neighbor) {
        this.neighbors.add(neighbor);
        if (neighbor.isBomb) {
            this.bombNeighborCount++;
        }
    }

    /**
     * This method return list of the tiles neighbors
     */
    public ArrayList<Tile> getNeighbors() {
        return this.neighbors;
    }

    /**
     * This method return the number of bombs around specific tile
     */
    public int getBombNeighborCount() {
        return this.bombNeighborCount;
    }

    /**
     * This method reveal specific tile
     */
    public void reveal() {
        if (this.isBomb)
            this.setmState(TileState.BOMB);
        if (this.getBombNeighborCount() == 0)
            this.setmState(TileState.NONE);
        else
            this.setmState(TileState.NUMBER);
        this.isRevealed = true;
    }

    public TileState getmState() {
        return mState;
    }

    public void setmState(TileState mState) {
        this.mState = mState;
    }

    /**
     * THIS METHOD IS IN USE! don't know why it's grayed
     */
    public boolean isExploaded() {
        return isExploaded;
    }

    public void setExploaded(boolean exploaded) {
        isExploaded = exploaded;
    }

    public boolean isWinMode() {
        return winMode;
    }

    public void setWinMode(boolean winMode) {
        this.winMode = winMode;
    }

    public boolean isLoseMode() {
        return loseMode;
    }

    public void setLoseMode(boolean loseMode) {
        this.loseMode = loseMode;
    }

    public void resetNeighbors() {
        this.neighbors = new ArrayList<Tile>();
        this.bombNeighborCount = 0;
    }
}
