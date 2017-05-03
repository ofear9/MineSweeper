package com.example.ranendelman.minesweeper.GameLogic;


import com.example.ranendelman.minesweeper.Activities.PlayGameActivity;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by RanEndelman on 26/11/2016.
 */

/**
 * The Board class
 */
public class Board {
    int numOfTiles;
    private int bombCount;
    private int tilesRevealed;
    private boolean bombsAdded = false;
    private ArrayList<Tile> Bombs = new ArrayList<>(bombCount);
    Tile mTile[];

    /**
     * The class constructor
     */
    public Board(int matrixSize, int numOfBombs) {
        numOfTiles = (matrixSize * matrixSize);
        this.bombCount = numOfBombs;
        mTile = new Tile[numOfTiles];
        for (int i = 0; i < numOfTiles; i++)
            mTile[i] = new Tile(i);
    }

    /**
     * Return the number of bombs on the board
     */
    public int getBombCount() {
        return bombCount;
    }

    /**
     * Return the board size (number of tiles)
     */
    public int getBoardSize() {
        return numOfTiles;
    }

    /**
     * Return specific tile from the board
     */
    public Tile getTile(int position) {
        return mTile[position];
    }

    /**
     * This method reset the board
     */
    public void reset(int matrixSize) {
        this.shuffleBombs(this.bombCount, matrixSize);
        setTilesRevealed(0);
    }

    /**
     * This method random the bombs on the board - for both startup and bombs adding duo movement
     */
    public PlayGameActivity.gameState shuffleBombs(int numOfBombsToAdd, int matrixSize) {
        if (getBoardSize() >= getBombCount() + numOfBombsToAdd) {
            Random random = new Random();
            int i = 0;
            while (i < numOfBombsToAdd) {
                int position = random.nextInt(numOfTiles - 1);
                if (!mTile[position].isBomb) {
                    mTile[position].isBomb = true;
                    mTile[position].isRevealed = false;
                    mTile[position].setmState(Tile.TileState.BOMB);
                    Bombs.add(mTile[position]);
                    i++;
                }
            }
            //will be false only for the first shuffle
            if (bombsAdded)
                setBombCount(getBombCount() + numOfBombsToAdd);
            bombsAdded = true;
            calculateTileNeighbors(matrixSize);
            return PlayGameActivity.gameState.NONE;
        } else
            return PlayGameActivity.gameState.LOSE;
    }

    /**
     * This method finds each tile neighbors
     */
    public void calculateTileNeighbors(int matrixSize) {
        for (int i = 0; i < mTile.length; i++)
            mTile[i].resetNeighbors();

        for (int i = 0; i < mTile.length; i++) {
            //if Last in the Row
            boolean flag = (i + 1) % matrixSize != 0;

            //if First in the Row
            boolean flag2 = i % matrixSize != 0;

            if ((i + 1) < mTile.length && flag)
                mTile[i].addNeighbor(mTile[i + 1]);
            if ((i + matrixSize) < mTile.length)
                mTile[i].addNeighbor(mTile[i + matrixSize]);
            if ((i - matrixSize) >= 0)
                mTile[i].addNeighbor(mTile[i - matrixSize]);
            if ((i - 1) >= 0 && flag && flag2)
                mTile[i].addNeighbor(mTile[i - 1]);
            if ((i + matrixSize + 1) < mTile.length && flag)
                mTile[i].addNeighbor(mTile[i + matrixSize + 1]);
            if ((i + matrixSize - 1) < mTile.length && flag && flag2)
                mTile[i].addNeighbor(mTile[i + matrixSize - 1]);
            if ((i - matrixSize + 1) >= 0 && flag)
                mTile[i].addNeighbor(mTile[i - matrixSize + 1]);
            if ((i - matrixSize - 1) >= 0 && flag2)
                mTile[i].addNeighbor(mTile[i - matrixSize - 1]);
        }
    }

    /**
     * This recursive method reveal the relevant tiles
     */
    public boolean reveal(Tile t) {
        t.reveal();
        if (!t.isBomb) {
            setTilesRevealed(getTilesRevealed() + 1);
            if (t.getBombNeighborCount() == 0) {
                ArrayList<Tile> neighbors = t.getNeighbors();
                for (int i = 0; i < neighbors.size(); i++) {
                    if (!neighbors.get(i).isRevealed && neighbors.get(i).getmState() != Tile.TileState.FLAG) {
                        reveal(neighbors.get(i));
                    }
                }
            }
        }
        return t.isBomb;
    }

    /**
     * This method shows all the bombs on the board
     */
    public void showBombs() {
        for (Tile t : Bombs)
            t.reveal();
    }

    /**
     * This method sets all the Tiles to win mode in order to show fireworks
     */
    public void setBoardToWinMode() {
        for (Tile t : mTile)
            t.setWinMode(true);
    }

    /**
     * This method sets all the Tiles to lose mode in order to show falling tiles
     */
    public void setBoardToLoseMode() {
        for (Tile t : mTile)
            t.setLoseMode(true);
    }

    /**
     * This method return the number of revealed tiles on the board
     */
    public int getRevealedCount() {
        return this.tilesRevealed;
    }

    /**
     * This method update the number of bombs on the board
     */
    public void setBombCount(int bombCount) {
        this.bombCount = bombCount;
    }

    /**
     * This method cover revealed tiles duo machine movement
     */
    public void coverRevealedTile(int numOfTilesToCover) {
        if (getRevealedCount() > 0 && getRevealedCount() >= numOfTilesToCover) {
            Random random = new Random();
            int i = 0;
            while (i < numOfTilesToCover) {
                int position = random.nextInt(numOfTiles - 1);
                if (mTile[position].isRevealed) {
                    mTile[position].isRevealed = false;
                    mTile[position].setmState(Tile.TileState.NONE);
                    setTilesRevealed(getTilesRevealed() - 1);
                    i++;
                }
            }
        }

    }

    public int getTilesRevealed() {
        return this.tilesRevealed;
    }

    public void setTilesRevealed(int tilesRevealed) {
        this.tilesRevealed = tilesRevealed;
    }
}
