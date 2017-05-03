package com.example.ranendelman.minesweeper.Activities;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.example.ranendelman.minesweeper.GPS.GPSTracker;
import com.example.ranendelman.minesweeper.GUI.TileAdapter;
import com.example.ranendelman.minesweeper.DBLogic.dataBase;
import com.example.ranendelman.minesweeper.GameLogic.Game;
import com.example.ranendelman.minesweeper.MovmentService.MovementDetectorService;
import com.example.ranendelman.minesweeper.MovmentService.MovementDetectorServiceListener;
import com.example.ranendelman.minesweeper.R;
import com.example.ranendelman.minesweeper.GameLogic.Score;
import com.example.ranendelman.minesweeper.GameLogic.gameLevel;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * Created by RanEndelman on 26/11/2016.
 */

public class PlayGameActivity extends AppCompatActivity implements MovementDetectorServiceListener {

    public enum gameState {
        NONE, WIN, LOSE
    }

    private Game mGame;

    private gameLevel level;

    private GridView mGrid;

    private TextView timerTextView;

    private int matrixSize;

    private int numOfBombs;

    private int numOfMaxFlags;

    private int currNumOfFlags;

    private int timeCounter = 0;

    private int numOfFlags = 0;

    private Timer timer;

    private dataBase db;

    private GPSTracker gpsTracker;

    private ServiceConnection mConnection;

    private Button btHs;

    private boolean mBound = false;

    private static Vibrator vb;

    private MediaPlayer mSoundBomb, mSoundWin, mSoundDuringGame, mSoundClick, mSoundLoose;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_play);

        mSoundDuringGame = MediaPlayer.create(PlayGameActivity.this, R.raw.duringgame);

        mSoundClick = MediaPlayer.create(PlayGameActivity.this, R.raw.click);

        mSoundLoose = MediaPlayer.create(PlayGameActivity.this, R.raw.loose);

        vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

        gameLevel currLevel = (gameLevel) getIntent().getSerializableExtra("GAME LEVEL");
        this.level = currLevel;

        mSoundBomb = MediaPlayer.create(PlayGameActivity.this, R.raw.bomb);

        mSoundWin = MediaPlayer.create(PlayGameActivity.this, R.raw.winsound);

        btHs = (Button) findViewById(R.id.HighScores_play);

        db = new dataBase(getApplicationContext());

        if (!isFinishing()) {
            btHs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(PlayGameActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(getString(R.string.paused))
                            .setMessage(getString(R.string.options))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(PlayGameActivity.this, ScoreTableActivity.class);
                                    intent.putExtra("GAME LEVEL", level);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), null)
                            .show();

                }
            });
        }

        /**
         * Defines callbacks for service binding, passed to bindService()
         */
        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                //LocalService bounded, get LocalService instance
                ((MovementDetectorService.MyBinder) service).getService().setListener(PlayGameActivity.this);
                Log.v("connected", "connected");
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                Log.v("Service Un-Binded", "Service Un-Binded");
                mBound = false;
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSoundDuringGame.start();
        mSoundDuringGame.setLooping(true);
        if (!mBound) {
            Intent intent = new Intent(this, MovementDetectorService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        extractDataFromBundle();

        timer = new Timer();

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                PlayGameActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**Create new game and grid*/
                        mGame = new Game(matrixSize, numOfBombs, numOfMaxFlags);

                        mGrid = (GridView) findViewById(R.id.gridview);

                        mGrid.setNumColumns(matrixSize);

                        mGrid.setAdapter(new TileAdapter(getApplicationContext(), mGame.getmBoard(), matrixSize));

                        mGame.start();

                        setRemainingFlags(numOfMaxFlags);

                        setCurrNumOfFlags(-1);

                        /**For marking flags*/
                        mGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                mSoundClick.start();
                                gameState status = mGame.markFlag(position);

                                numOfFlags = mGame.getNumOfFlags();

                                if (numOfFlags != getCurrNumOfFlags()) {
                                    setRemainingFlags(mGame.getNumOfMaxFlags() - mGame.getNumOfFlags());
                                    setCurrNumOfFlags(mGame.getNumOfFlags());
                                }

                                if (status == gameState.WIN) {
                                    if (mBound) {
                                        unbindService(mConnection);
                                        mBound = false;
                                    }
                                    mSoundDuringGame.stop();
                                    mSoundWin.start();
                                    timer.cancel();
                                    if (db.recordCheck(timeCounter - 1, level))
                                        saveRecord(timeCounter);
                                    else
                                        endOfGameAlertDialog(view, status, timeCounter - 1, true);
                                }
                                ((TileAdapter) mGrid.getAdapter()).notifyDataSetChanged();

                                return true;
                            }
                        });
                        /**For regular clicks*/
                        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                mSoundClick.start();
                                gameState status = mGame.playTile(position);

                                if (status == gameState.WIN) {
                                    if (mBound) {
                                        unbindService(mConnection);
                                        mBound = false;
                                    }
                                    mSoundDuringGame.stop();
                                    mSoundWin.start();
                                    timer.cancel();
                                    if (db.recordCheck(timeCounter, level))
                                        saveRecord(timeCounter);
                                    else
                                        endOfGameAlertDialog(view, status, timeCounter - 1, true);

                                } else if (status == gameState.LOSE) {
                                    if (mBound) {
                                        unbindService(mConnection);
                                        mBound = false;
                                    }
                                    vb.vibrate(800);
                                    mSoundDuringGame.stop();
                                    mSoundBomb.start();
                                    timer.cancel();
                                    endOfGameAlertDialog(view, status, timeCounter - 1, true);
                                }
                                ((TileAdapter) mGrid.getAdapter()).notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        });

        thread.start();
        timerTextView = (TextView) findViewById(R.id.timer);
        startTimer(timerTextView, timer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSoundDuringGame.stop();
    }

    protected void onPause() {
        super.onPause();
        mSoundDuringGame.stop();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mBound)
            unbindService(mConnection);
    }

    /**
     * This method start the timer
     */
    private void startTimer(final TextView tv, Timer timer) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int minutes = (timeCounter % 3600) / 60;
                        int seconds = timeCounter % 60;
                        timeCounter++;
                        tv.setText(String.format("%02d:%02d", minutes, seconds));
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * This method generate new Score object in order to save it to the DB and shows
     * on the UI message for the user to insert his name with 4 seconds delay
     * (in order to let the user see the fire works animation)
     */
    public void saveRecord(final int timeCounter) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String strScore;

                db = new dataBase(getApplicationContext());

                View view = (LayoutInflater.from(PlayGameActivity.this)).inflate(R.layout.user_input_score, null);

                AlertDialog.Builder alert = new AlertDialog.Builder(PlayGameActivity.this);
                alert.setView(view);

                final EditText userInput = (EditText) view.findViewById(R.id.userinput);
                final TextView userScore = (TextView) view.findViewById(R.id.userScore);
                strScore = (String.format("%02d:%02d", (((timeCounter - 1) % 3600) / 60), (timeCounter - 1) % 60));

                userScore.setText(getString(R.string.winAlert) + strScore);
                alert.setCancelable(false).
                        setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Get user name to high score table
                                String userName = userInput.getText().toString();
                                saveScoreToDB(userName, timeCounter);
                                mSoundWin.pause();
                                // Start table score activity
                                Intent intent = new Intent(PlayGameActivity.this, ScoreTableActivity.class);
                                intent.putExtra("GAME LEVEL", level);
                                startActivity(intent);
                                finish();
                            }
                        });
                if (!isFinishing()) {
                    Dialog dialog = alert.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }

            }
        }, 4000);

    }

    /**
     * This method takes the generated Score object saveRecord method
     * and save it to the DB
     */
    private void saveScoreToDB(String name, int timeCounter) {
        try {
            Score score;
            Long s1 = Long.valueOf(timeCounter - 1);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            gpsTracker = new GPSTracker(PlayGameActivity.this);
            if (gpsTracker.getIsGPSTrackingEnabled() && (gpsTracker.latitude != 0.0 || gpsTracker.longitude != 0.0)) {
                String latitude = String.valueOf(gpsTracker.latitude);
                String longitude = String.valueOf(gpsTracker.longitude);
                addresses = geocoder.getFromLocation(gpsTracker.latitude, gpsTracker.longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String country = gpsTracker.getCountryName(PlayGameActivity.this);
                String city = gpsTracker.getLocality(PlayGameActivity.this);
                score = new Score(name, s1, level, latitude, longitude, address, country, city);
                db.creatNewScore(score);
                db.close();
            } else {
                Toast.makeText(PlayGameActivity.this, getString(R.string.noLocation), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            db.close();
            Log.e("Close", "Close");
        }
    }

    /**
     * This method shows the end of game dialog with 4 seconds delay
     * (in order to let the user see the animation)
     */
    private void endOfGameAlertDialog(View view, gameState status, int timeCounter, final boolean flag) {
        final gameState stat = status;
        final int tc = timeCounter;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                String message = "";
                if (stat == gameState.WIN)
                    message = getString(R.string.winGame);
                else if (stat == gameState.LOSE) {
                    if (flag)
                        message = getString(R.string.loseGame);
                    else
                        message = getString(R.string.tooMuchMines);
                }

                final AlertDialog.Builder winAlertDialog = new AlertDialog.Builder(PlayGameActivity.this);
                winAlertDialog.setMessage(getString(R.string.yourTime)
                        + String.format("%02d:%02d", ((tc % 3600) / 60), tc % 60) +
                        "\n\n " + getString(R.string.playAgain))
                        .setTitle(message)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSoundWin.pause();
                                restartGame();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create();
                if (!isFinishing())
                    winAlertDialog.show();
            }
        }, 4000);
    }

    /**
     * This method extract the data from the bundle
     */
    private void extractDataFromBundle() {
        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(MainActivity.BUNDLE_KEY);

        matrixSize = bundle.getInt(MainActivity.BUNDLE_KEY);

        numOfBombs = bundle.getInt(MainActivity.BUNDLE_BOMBS_COUNT_KEY);

        numOfMaxFlags = bundle.getInt(MainActivity.BUNDLE_FLAG_COUNT_KEY);
    }

    /**
     * This method restart the game
     */
    private void restartGame() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    /**
     * This method sets the UI of the remaining flags
     */
    protected void setRemainingFlags(int remainingFlags) {
        TextView remainFlags = (TextView) findViewById(R.id.remainingflags);
        if (remainingFlags == 1)
            remainFlags.setText(String.format("%d %s", remainingFlags, "Flag Remain"));
        else
            remainFlags.setText(String.format("%d %s", remainingFlags, "Flags Remain"));
    }

    /**
     * This method return the current number of available flags
     */
    public int getCurrNumOfFlags() {
        return currNumOfFlags;
    }

    /**
     * This method sets the current number of available flags
     */
    public void setCurrNumOfFlags(int currNumOfFlags) {
        this.currNumOfFlags = currNumOfFlags;

    }

    /**
     * Implement the listener method
     */
    @Override
    public void movementListener() {
        Log.v("MINES ADDED", "MINES ADDED");
        int magicNumber = matrixSize / 3;
        PlayGameActivity.gameState currGameStatus = mGame.getmBoard().shuffleBombs(magicNumber, matrixSize);
        if (currGameStatus == gameState.LOSE) {
            timer.cancel();
            mSoundDuringGame.stop();
            mSoundLoose.start();
            endOfGameAlertDialog(new View(getApplicationContext()), currGameStatus, timeCounter - 1, false);
        }
        mGame.setNumOfMaxFlags(mGame.getNumOfMaxFlags() + magicNumber);
        setRemainingFlags(mGame.getNumOfMaxFlags() - mGame.getNumOfFlags());
        mGame.getmBoard().setTilesRevealed(mGame.getmBoard().getTilesRevealed() - 1);
        mGame.getmBoard().coverRevealedTile(magicNumber);
        if (!isFinishing())
            Toast.makeText(this, magicNumber + " " + getString(R.string.mineAdd) + " " + magicNumber + " " +
                    getString(R.string.tileCover), Toast.LENGTH_SHORT).show();
        vb.vibrate(400);
        ((TileAdapter) mGrid.getAdapter()).notifyDataSetChanged();
    }
}