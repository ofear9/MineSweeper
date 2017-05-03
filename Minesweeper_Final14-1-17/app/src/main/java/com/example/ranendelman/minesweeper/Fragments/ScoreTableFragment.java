package com.example.ranendelman.minesweeper.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ranendelman.minesweeper.Activities.ScoreTableActivity;
import com.example.ranendelman.minesweeper.DBLogic.dataBase;
import com.example.ranendelman.minesweeper.GUI.ScoreTableAdapter;
import com.example.ranendelman.minesweeper.GameLogic.Score;
import com.example.ranendelman.minesweeper.R;
import com.example.ranendelman.minesweeper.GameLogic.gameLevel;

import java.util.ArrayList;

/**
 * Created by ofear on 1/7/2017.
 */

public class ScoreTableFragment extends Fragment {
    private dataBase db;
    private ArrayList<Score> scores;
    private ScoreTableAdapter adapter;
    private ListView scoreListView;
    private gameLevel level;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_score, container, false);
        scoreListView = (ListView) rootView.findViewById(R.id.scoreListView);
        level = ((ScoreTableActivity) getActivity()).getLevel();

        db = new dataBase(getActivity());
        scores = db.getScoreTable(level);
        db.close();

        if (scores != null) {
            adapter = new ScoreTableAdapter(getActivity(), R.layout.fragment_score_table_item, scores);
            scoreListView.setAdapter(adapter);
        }

        scoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Score score = (Score) scoreListView.getItemAtPosition(i);
                Fragment fragment = new ScoreMapTableFragment();
                Bundle pos = new Bundle();
                pos.putSerializable("score", score);
                fragment.setArguments(pos);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();

            }
        });

        return rootView;
    }
}
