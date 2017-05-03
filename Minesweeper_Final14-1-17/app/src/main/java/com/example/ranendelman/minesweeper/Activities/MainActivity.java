package com.example.ranendelman.minesweeper.Activities;
/**
 * App by:
 * Ran Endelman ID 305329815
 * Ophir Karako ID 201628724
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.ranendelman.minesweeper.GPS.GPSTracker;
import com.example.ranendelman.minesweeper.Manifest;
import com.example.ranendelman.minesweeper.R;
import com.example.ranendelman.minesweeper.GameLogic.gameLevel;

public class MainActivity extends AppCompatActivity {

    final public static int LEVEL_BEGINNER_BOARD_SIZE = 5;

    final public static int LEVEL_MEDIUM_BOARD_SIZE = 7;

    final public static int LEVEL_HARD_BOARD_SIZE = 9;

    final public static int LEVEL_EASY_PARAMS = 190;

    final public static int LEVEL_MEDIUM_PARAMS = 135;

    final public static int LEVEL_HARD_PARAMS = 103;

    final static int LEVEL_EASY_BOMBS_COUNT = 3;

    final static int LEVEL_MEDIUM_BOMBS_COUNT = 6;

    final static int LEVEL_HARD_BOMBS_COUNT = 9;

    final static int LEVEL_BEGINNER_MAX_FLAG = 3;

    final static int LEVEL_MEDIUM_MAX_FLAG = 6;

    final static int LEVEL_HARD_MAX_FLAG = 9;

    final static String BUNDLE_KEY = "matrixSize";

    final static String BUNDLE_FLAG_COUNT_KEY = "numOfFlag";

    final static String BUNDLE_BOMBS_COUNT_KEY = "numOfBombs";

    final static String LEVEL_KEY = "currentLevel";

    int matrixSize;

    int numOfBombs;

    int numOfFlags;

    boolean isGameSet;

    private MediaPlayer mIntroSound;

    private static com.example.ranendelman.minesweeper.GameLogic.gameLevel gameLevel;

    public static final String PREFS_NAME = "MyPrefsFile";

    public gameLevel getGameLevel() {
        return this.gameLevel;
    }

    public void setGameLevel(gameLevel gameLevel) {
        this.gameLevel = gameLevel;
    }

    public int getMatrixSize() {
        return this.matrixSize;
    }

    public void setMatrixSize(int matrixSize) {
        this.matrixSize = matrixSize;
    }

    public int getNumOfBombs() {
        return this.numOfBombs;
    }

    public void setNumOfBombs(int numOfBombs) {
        this.numOfBombs = numOfBombs;
    }

    public void setNumOfFlags(int numOfFlags) {
        this.numOfFlags = numOfFlags;
    }

    public int getNumOfFlags() {
        return this.numOfFlags;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mIntroSound = MediaPlayer.create(MainActivity.this, R.raw.intro);

        mIntroSound.setLooping(true);

        mIntroSound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mIntroSound.start();
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        setContentView(R.layout.activity_main);
        /**Sets the game level to the last one played*/
        setLastLevel();
        /**Check if it is the first time playing the game in this machine*/
        if (getGameLevel() == null) {
            setGameLevel(gameLevel.BEGINNER);
            RadioGroup rp = (RadioGroup) findViewById(R.id.radioGroup);
            rp.check(R.id.beginner);


        }

        setupStartButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        GPSTracker gps = new GPSTracker(MainActivity.this);
        String latitude = String.valueOf(gps.latitude);
        String longitude = String.valueOf(gps.longitude);
        Log.v("gps check", latitude + " " + longitude);
        mIntroSound.start();
        mIntroSound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mIntroSound.start();
            }
        });


    }

    public void onStart() {
        super.onStart();
        mIntroSound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mIntroSound.start();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveCurrLevel();
    }

    @Override
    protected void onStop() {
        if (mIntroSound.isPlaying())
            mIntroSound.pause();
        super.onStop();
        saveCurrLevel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIntroSound.isPlaying())
            mIntroSound.pause();
        saveCurrLevel();
    }

    public void onRadioButtonClicked(View view) {
        /**Check if the radio button now checked?*/
        isGameSet = true;
        boolean checked = ((RadioButton) view).isChecked();

        /**Check which level chosen by the player*/
        switch (view.getId()) {
            case R.id.beginner:
                if (checked) {
                    setGameLevel(gameLevel.BEGINNER);
                    setGame(gameLevel.BEGINNER);
                }
                break;
            case R.id.medium:
                if (checked) {
                    setGameLevel(gameLevel.MEDIUM);
                    setGame(gameLevel.MEDIUM);
                }
                break;
            case R.id.hard:
                if (checked) {
                    setGameLevel(gameLevel.HARD);
                    setGame(gameLevel.HARD);
                }
                break;
        }
    }

    protected void setupStartButton() {
        ImageButton b = (ImageButton) findViewById(R.id.imageButton3);

        Button scores = (Button) findViewById(R.id.HighScores);

        if (!isGameSet)
            setGame(this.getGameLevel());

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntroSound.pause();
                startNewGame();
            }
        });

        scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntroSound.pause();
                Intent intent = new Intent(MainActivity.this, ScoreTableActivity.class);
                intent.putExtra("GAME LEVEL", getGameLevel());
                startActivity(intent);
            }
        });
    }

    private void startNewGame() {
        /**Create new intent for the PlayGameActivity Activity*/
        Intent intent = new Intent(this, PlayGameActivity.class);

        Bundle bundle = new Bundle();
        /**Insert all the necessary data to the bundle */
        bundle.putInt(BUNDLE_KEY, this.getMatrixSize());

        bundle.putInt(BUNDLE_BOMBS_COUNT_KEY, this.getNumOfBombs());

        bundle.putInt(BUNDLE_FLAG_COUNT_KEY, this.getNumOfFlags());

        intent.putExtra(BUNDLE_KEY, bundle);

        intent.putExtra("GAME LEVEL", getGameLevel());

        startActivity(intent);
    }

    /**
     * This method save the currant level before the user exit the game
     */
    private void saveCurrLevel() {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LEVEL_KEY, getGameLevel().toString());
        editor.commit();
    }

    /**
     * This method save the last played level to the next time
     */
    private void setLastLevel() {
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, 0);

        String level = prefs.getString(LEVEL_KEY, "");

        RadioGroup rp = (RadioGroup) findViewById(R.id.radioGroup);

        if (level.equals(gameLevel.BEGINNER.toString())) {
            rp.check(R.id.beginner);
            setGameLevel(gameLevel.BEGINNER);
        } else if (level.equals(gameLevel.MEDIUM.toString())) {
            rp.check(R.id.medium);
            setGameLevel(MainActivity.gameLevel.MEDIUM);
        } else if (level.equals(gameLevel.HARD.toString())) {
            rp.check(R.id.hard);
            setGameLevel(gameLevel.HARD);
        }
    }

    public void setGame(gameLevel level) {

        switch (level) {
            default:
            case BEGINNER: {
                setMatrixSize(LEVEL_BEGINNER_BOARD_SIZE);
                setNumOfBombs(LEVEL_EASY_BOMBS_COUNT);
                setNumOfFlags(LEVEL_BEGINNER_MAX_FLAG);
                setGameLevel(gameLevel.BEGINNER);
                break;
            }

            case MEDIUM: {
                setMatrixSize(LEVEL_MEDIUM_BOARD_SIZE);
                setNumOfBombs(LEVEL_MEDIUM_BOMBS_COUNT);
                setNumOfFlags(LEVEL_MEDIUM_MAX_FLAG);
                setGameLevel(gameLevel.MEDIUM);
                break;
            }

            case HARD: {
                setMatrixSize(LEVEL_HARD_BOARD_SIZE);
                setNumOfBombs(LEVEL_HARD_BOMBS_COUNT);
                setNumOfFlags(LEVEL_HARD_MAX_FLAG);
                setGameLevel(gameLevel.HARD);
                break;
            }
        }
    }

    /**
     * This method show the Location service permission dialog message to the user
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, getString(R.string.locationEnable), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.locationNotEna), Toast.LENGTH_LONG).show();
                }
                return;
            }
            default:
                return;
        }
    }
}


