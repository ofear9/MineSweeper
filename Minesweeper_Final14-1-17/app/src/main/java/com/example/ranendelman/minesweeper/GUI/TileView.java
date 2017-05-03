package com.example.ranendelman.minesweeper.GUI;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ranendelman.minesweeper.Activities.MainActivity;

/**
 * Created by RanEndelman on 21/11/2016.
 */

/**
 * This class define tile view
 */
public class TileView extends LinearLayout {

    public TextView text;
    LayoutParams layoutParams;

    public TileView(Context context, int size) {
        super(context);
        text = new TextView(context);
        this.setOrientation(HORIZONTAL);
        if (size == MainActivity.LEVEL_BEGINNER_BOARD_SIZE)
            layoutParams = new LinearLayout.LayoutParams(MainActivity.LEVEL_EASY_PARAMS, MainActivity.LEVEL_EASY_PARAMS);
        else if (size == MainActivity.LEVEL_MEDIUM_BOARD_SIZE)
            layoutParams = new LinearLayout.LayoutParams(MainActivity.LEVEL_MEDIUM_PARAMS, MainActivity.LEVEL_MEDIUM_PARAMS);
        else if (size == MainActivity.LEVEL_HARD_BOARD_SIZE)
            layoutParams = new LinearLayout.LayoutParams(MainActivity.LEVEL_HARD_PARAMS, MainActivity.LEVEL_HARD_PARAMS);
        this.setLayoutParams(layoutParams);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        text.setLayoutParams(layoutParams);
        text.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setTextSize(30);
        text.setTextColor(Color.BLACK);
        this.addView(text);
    }
}
