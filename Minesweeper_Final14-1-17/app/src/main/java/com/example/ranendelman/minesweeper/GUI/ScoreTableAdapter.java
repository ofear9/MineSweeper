package com.example.ranendelman.minesweeper.GUI;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ranendelman.minesweeper.R;
import com.example.ranendelman.minesweeper.GameLogic.Score;

import java.util.ArrayList;

/**
 * Created by ofear on 1/7/2017.
 */

public class ScoreTableAdapter extends ArrayAdapter<Score> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Score> scores;


    public ScoreTableAdapter(Context context, int layoutResourceId, ArrayList<Score> scores) {
        super(context, layoutResourceId, scores);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.scores = scores;
    }

    /**
     * This method desctibe how to show the arraylist on the screen
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.number = (TextView) row.findViewById(R.id.number);
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.score = (TextView) row.findViewById(R.id.score);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.number.setText((position + 1) + ".");
        holder.name.setText(scores.get(position).getName());
        String strScore = (String.format("%02d:%02d", ((scores.get(position).getScore() % 3600) / 60),
                scores.get(position).getScore() % 60));
        holder.score.setText(strScore);


        return row;

    }

    private class ViewHolder {
        TextView number, name, score;
    }
}
