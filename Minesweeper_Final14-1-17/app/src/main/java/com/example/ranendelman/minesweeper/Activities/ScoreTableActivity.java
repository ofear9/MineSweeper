package com.example.ranendelman.minesweeper.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.ranendelman.minesweeper.R;
import com.example.ranendelman.minesweeper.Fragments.ScoreMapTableFragment;
import com.example.ranendelman.minesweeper.Fragments.ScoreTableFragment;
import com.example.ranendelman.minesweeper.GameLogic.gameLevel;

import java.util.logging.Level;

/**
 * This is the activity that related to the score table and map fragments
 */
public class ScoreTableActivity extends AppCompatActivity {

    private Button btnScoreTable, btnScoreMap;
    private gameLevel level;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_score_table);

        gameLevel currLevel = (gameLevel) getIntent().getSerializableExtra("GAME LEVEL");
        this.level = currLevel;

        setLevelButton();

        btnScoreTable = (Button) findViewById(R.id.btnScore);
        btnScoreMap = (Button) findViewById(R.id.btnScoreMap);

        btnScoreTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = 0;
                displayView(position);
            }
        });

        btnScoreMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = 1;
                displayView(position);
            }
        });

        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;

        if (position == 0) {
            fragment = new ScoreTableFragment();
        } else if (position == 1)
            fragment = new ScoreMapTableFragment();

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();
        }

    }

    public gameLevel getLevel() {
        return level;
    }

    public void onRadioButtonClicked(View view) {
        /**Check if the radio button now checked?*/
        boolean checked = ((RadioButton) view).isChecked();

        /**Check which level chosen by the player*/
        switch (view.getId()) {
            case R.id.beginner:
                if (checked) {
                    level = gameLevel.BEGINNER;
                    displayView(position);
                }
                break;
            case R.id.medium:
                if (checked) {
                    level = gameLevel.MEDIUM;
                    displayView(position);
                }
                break;
            case R.id.hard:
                if (checked) {
                    level = gameLevel.HARD;
                    displayView(position);
                }
                break;
        }
    }

    /**
     * This method set the button according the main activity screen level
     */
    private void setLevelButton() {
        RadioGroup rp = (RadioGroup) findViewById(R.id.highScoresLevels);
        if (level == gameLevel.BEGINNER) {
            rp.check(R.id.beginner);
        } else if (level == gameLevel.MEDIUM) {
            rp.check(R.id.medium);
        } else if (level == gameLevel.HARD) {
            rp.check(R.id.hard);
        }
    }
}
