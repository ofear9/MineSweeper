package com.example.ranendelman.minesweeper.GUI;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;

import com.example.ranendelman.minesweeper.GameLogic.Board;
import com.example.ranendelman.minesweeper.GameLogic.Tile;
import com.example.ranendelman.minesweeper.Activities.MainActivity;
import com.example.ranendelman.minesweeper.R;


import java.util.Random;

/**
 * Created by RanEndelman on 21/11/2016.
 */

/**
 * This class define every tile in game board
 */
public class TileAdapter extends BaseAdapter {
    private Board mBoard;
    private Context mContext;
    private int mSize;

    public TileAdapter(Context context, Board board, int size) {
        mSize = size;
        mBoard = board;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mBoard.getBoardSize();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TileView tileView;
        int[] numColor = {Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.MAGENTA, Color.BLACK, Color.CYAN, Color.BLUE};
        if (convertView instanceof GifWebView)
            convertView = null;
        tileView = (TileView) convertView;
        if (tileView == null) {
            tileView = new TileView(mContext, mSize);
            Log.v("Tile Adapter", "creating new view for index " + position);
        } else {
            Log.v("Tile Adapter", "RECYCLING view for index " + position);
        }
        tileView.setBackgroundResource(R.drawable.cell);

        if (mBoard.getTile(position).isWinMode()) {
            if (mBoard.getTile(position).isBomb) {
                return createGifViewForWin(mBoard.getBoardSize());
            } else {
                tileView.setBackgroundColor(Color.BLACK);
                tileView.text.setText("");
                return tileView;
            }
        }

        if (mBoard.getTile(position).isLoseMode()) {

            if (mBoard.getTile(position).isBomb && mBoard.getTile(position).isRevealed
                    && mBoard.getTile(position).isExploaded) {
                return createGifViewForLose(mBoard.getBoardSize());
            }
            /**Perform the falling Tiles animation */
            else if (!mBoard.getTile(position).isBomb) {
                Random rand = new Random();
                int randomNum = 2000 + rand.nextInt((6000 - 2000) + 1);
                Animation slideDown;
                slideDown = AnimationUtils.loadAnimation(mContext, R.anim.slide_down);
                slideDown.setDuration(randomNum);
                slideDown.setFillAfter(true);
                tileView.startAnimation(slideDown);
                return tileView;
            }
        }

        if (!mBoard.getTile(position).isRevealed && mBoard.getTile(position).getmState().toString() == "") {
            tileView.setBackgroundResource(R.drawable.cell);
            tileView.text.setText("");
            return tileView;
        }

        if (!mBoard.getTile(position).isRevealed && mBoard.getTile(position).getmState() == Tile.TileState.BOMB) {
            tileView.setBackgroundResource(R.drawable.cell);
            tileView.text.setText("");
            return tileView;
        }

        if (mBoard.getTile(position).isRevealed && !mBoard.getTile(position).isBomb)
            tileView.setBackgroundColor(Color.GRAY);

        if (!mBoard.getTile(position).isBomb && mBoard.getTile(position).isRevealed &&
                mBoard.getTile(position).getBombNeighborCount() > 0) {
            tileView.text.setText("" + mBoard.getTile(position).getBombNeighborCount());
            tileView.text.setTextColor(numColor[mBoard.getTile(position).getBombNeighborCount()]);
        }

        if (mBoard.getTile(position).isBomb && mBoard.getTile(position).isRevealed) {
            tileView.setBackgroundResource(R.drawable.mine);
        }

        if (mBoard.getTile(position).getmState().toString().equals("markFlag") && !mBoard.getTile(position).isRevealed)
            tileView.setBackgroundResource(R.drawable.flagcell);

        return tileView;
    }

    /**
     * This method perform the mine explosion for losers
     */
    public View createGifViewForLose(int size) {
        /** Level beginner case */
        if (size == MainActivity.LEVEL_BEGINNER_BOARD_SIZE * MainActivity.LEVEL_BEGINNER_BOARD_SIZE) {
            GifWebView view = new GifWebView(mContext, "file:///android_asset/explosionForBeginner.gif");
            view.setBackgroundColor(Color.BLACK);
            return view;

            /** Level medium case */
        } else if (size == MainActivity.LEVEL_MEDIUM_BOARD_SIZE * MainActivity.LEVEL_MEDIUM_BOARD_SIZE) {
            GifWebView view = new GifWebView(mContext, "file:///android_asset/explosionForMedium.gif");
            view.setBackgroundColor(Color.BLACK);
            return view;

            /** Level hard case */
        } else if (size == MainActivity.LEVEL_HARD_BOARD_SIZE * MainActivity.LEVEL_HARD_BOARD_SIZE) {
            GifWebView view = new GifWebView(mContext, "file:///android_asset/explosionForHard.gif");
            view.setBackgroundColor(Color.BLACK);
            return view;
        }
        return null;
    }

    /**
     * This method perform the fire works for winners
     */
    public View createGifViewForWin(int size) {
        /** Level beginner case */
        if (size == MainActivity.LEVEL_BEGINNER_BOARD_SIZE * MainActivity.LEVEL_BEGINNER_BOARD_SIZE) {
            GifWebView view = new GifWebView(mContext, "file:///android_asset/fireworksForBeginner.gif");
            view.setBackgroundColor(Color.BLACK);
            return view;

            /** Level medium case */
        } else if (size == MainActivity.LEVEL_MEDIUM_BOARD_SIZE * MainActivity.LEVEL_MEDIUM_BOARD_SIZE) {
            GifWebView view = new GifWebView(mContext, "file:///android_asset/fireworksForMedium.gif");
            view.setBackgroundColor(Color.BLACK);
            return view;

            /** Level hard case */
        } else if (size == MainActivity.LEVEL_HARD_BOARD_SIZE * MainActivity.LEVEL_HARD_BOARD_SIZE) {
            GifWebView view = new GifWebView(mContext, "file:///android_asset/fireworksForHard.gif");
            view.setBackgroundColor(Color.BLACK);
            return view;
        }
        return null;
    }
}
