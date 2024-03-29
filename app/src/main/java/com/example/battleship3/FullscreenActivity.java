package com.example.battleship3;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;


import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import android.content.Intent;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private boolean setupPhase = true;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private Battleship[] geoffFleet = new Battleship[5];
    private Battleship[] userFleet = new Battleship[5];
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private SoundPool soundPool;
    private int fireHit, fireMiss, gqShort, alarm, pacmandies, steamSiren, sonarPing, splash2,
    gameover;

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(4)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        }

        alarm = soundPool.load(this, R.raw.alarm,1);
        fireHit = soundPool.load(this, R.raw.firehit, 1);
        fireMiss = soundPool.load(this, R.raw.firemiss, 1);
        gameover = soundPool.load(this, R.raw.gameover, 1);
        gqShort = soundPool.load(this, R.raw.gqshort, 1);
        pacmandies = soundPool.load(this, R.raw.pacmandies,1);
        sonarPing = soundPool.load(this, R.raw.sonarping,1);
        splash2 = soundPool.load(this, R.raw.splash2,1);
        steamSiren = soundPool.load(this, R.raw.steamsiren,1);


        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        Button dummyButton = findViewById(R.id.dummy_button);
        dummyButton.setOnTouchListener(mDelayHideTouchListener);
        Button dummyButton2 = findViewById(R.id.dummy_button);
        final GridLayout offenseGrid = findViewById(R.id.offenseGrid);
        final GridLayout defenseGrid = findViewById(R.id.defenseGrid);
        Button[] ships = {(Button) findViewById(R.id.Ship0), (Button) findViewById(R.id.Ship1),
                (Button) findViewById(R.id.Ship2), (Button) findViewById(R.id.Ship3), (Button) findViewById(R.id.Ship4)};
        ships[0].setBackgroundColor(Color.parseColor("#326ada"));
        ships[1].setBackgroundColor(Color.parseColor("#d4d8d4"));
        ships[2].setBackgroundColor(Color.parseColor("#433e90"));
        ships[3].setBackgroundColor(Color.parseColor("#a19c9c"));
        ships[4].setBackgroundColor(Color.parseColor("#d2d2d2"));

        geoffFleet[0] = new Battleship(2, R.id.SHIP_0, "geoff", R.id.geoffPrarieLearn);
        geoffFleet[1] = new Battleship(3, R.id.SHIP_1, "geoff", R.id.geoffCoders);
        geoffFleet[2] = new Battleship(3, R.id.SHIP_2, "geoff", R.id.geoffFoelinger);
        geoffFleet[3] = new Battleship(4, R.id.SHIP_3, "geoff", R.id.geoffCBTF);
        geoffFleet[4] = new Battleship(5, R.id.SHIP_4, "geoff", R.id.geoffMP);
        userFleet[0] = new Battleship(2, R.id.SHIP_0, "user", R.id.userPrarieLearn);
        userFleet[1] = new Battleship(3, R.id.SHIP_1, "user", R.id.userCoders);
        userFleet[2] = new Battleship(3, R.id.SHIP_2, "user", R.id.userFoelinger);
        userFleet[3] = new Battleship(4, R.id.SHIP_3, "user", R.id.userCBTF);
        userFleet[4] = new Battleship(5, R.id.SHIP_4, "user", R.id.userMP);
        for (final Button ship : ships) {
            ship.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    defenseSetUp(ship, defenseGrid);
                }
            });
        }
        int columnCount = defenseGrid.getColumnCount();
        int thisRow = 0;
        int thisColumn = 0;
        for (int i = 0; i < defenseGrid.getChildCount(); i++) {
            defenseGrid.getChildAt(i).setTag(R.id.SHIP_HERE, R.id.VACANT);
            defenseGrid.getChildAt(i).setTag(R.id.MY_ROW, thisRow);
            defenseGrid.getChildAt(i).setTag(R.id.MY_COLUMN, thisColumn);
            defenseGrid.getChildAt(i).setTag(R.id.ATTACKED_HERE, R.id.OPEN);
            defenseGrid.getChildAt(i).setTag(R.id.MY_CELL, i);
            thisColumn++;
            if (thisColumn == columnCount) {
                thisColumn = 0;
                thisRow++;
            }
        }
        int columnCountO = defenseGrid.getColumnCount();
        int thisRowO = 0;
        int thisColumnO = 0;
        for (int i = 0; i < offenseGrid.getChildCount(); i++) {
            offenseGrid.getChildAt(i).setTag(R.id.SHIP_HERE, R.id.VACANT);
            offenseGrid.getChildAt(i).setTag(R.id.MY_ROW, thisRowO);
            offenseGrid.getChildAt(i).setTag(R.id.MY_COLUMN, thisColumnO);
            offenseGrid.getChildAt(i).setTag(R.id.ATTACKED_HERE, R.id.OPEN);
            offenseGrid.getChildAt(i).setTag(R.id.MY_CELL, i);
            thisColumnO++;
            if (thisColumnO == columnCountO) {
                thisColumnO = 0;
                thisRowO++;
            }
        }
        ((TextView) findViewById(R.id.Endgame)).setText("Place your ships in the bottom grid to begin");
    }

    private void startAttacking(final GridLayout offenseGrid, final GridLayout defenseGrid) {
        for (int i = 0; i < offenseGrid.getChildCount(); i++) {
            final CardView cell = (CardView) offenseGrid.getChildAt(i);
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int shipPresent = (int) cell.getTag(R.id.SHIP_HERE);
                    if (shipPresent == R.id.VACANT) {
                        //Miss sound here
                        cell.setTag(R.id.ATTACKED_HERE, R.id.MISS);
                        soundPool.play(fireMiss, 1, 1, 0, 0, 1);
                        timeDelay(2);
                        soundPool.play(sonarPing, 1, 1, 0, 0, 1);
                        cell.setBackgroundColor(Color.rgb(0,0,255));
                        //timeDelay(5);
                    } else {
                        //Hit Sound Here
                        soundPool.play(fireHit, 1, 1, 0, 0, 1);
                        timeDelay(2);
                        cell.setBackgroundColor(Color.rgb(255, 0,0));
                        //timeDelay(5);
                        cell.setTag(R.id.ATTACKED_HERE, R.id.HIT);
                        Battleship wounded = findShipByID(shipPresent, "geoff");
                        wounded.attacked();
                        if (!wounded.isAlive()) { //Ship is sinking
                            soundPool.play(splash2, 1, 1, 0, 0, 1);
                            for(int cell : wounded.getCells()) {
                                offenseGrid.getChildAt(cell).setBackgroundColor(Color.rgb(0,0,0));
                            }
                            findViewById(wounded.getSurvivalListViewID()).setVisibility(View.GONE);
                        }
                        if ((!geoffFleet[0].isAlive() && !geoffFleet[1].isAlive() //Game Over
                                && !geoffFleet[2].isAlive() && !geoffFleet[3].isAlive()
                                && !geoffFleet[4].isAlive())) {
                            //timeDelay(2);
                            ((TextView) findViewById(R.id.Endgame)).setText("YOU WIN!");

                            soundPool.play(steamSiren, 1, 1, 0, 0, 1);
                            timeDelay(2);
                            soundPool.play(steamSiren, 1, 1, 0, 0, 1);
                            ((TextView) findViewById(R.id.Endgame)).setText("YOU WIN! Click New Game to start another");
                            Button dummyButton = (Button) findViewById(R.id.dummy_button);
                            dummyButton.setText("New Game");
                            //dummyButton.setVisibility(View.VISIBLE);
                            dummyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    newGame();
                                }
                            });
                        }
                    }
                    timeDelay(1);
                    counterAttack(defenseGrid);
                }
            });
        }
    }
    private void newGame() {
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
        finish();
    }
    private void counterAttack(GridLayout defenseGrid) {
        int targetIndex = attackAI(defenseGrid);
        CardView targetCell = (CardView) defenseGrid.getChildAt(targetIndex);
        if(((int) targetCell.getTag(R.id.SHIP_HERE)) == R.id.VACANT) {
            //MISS
            //ADD SOUNDS HERE
            soundPool.play(fireMiss, 1, 1, 0, 0, 1);
            timeDelay(2);
            soundPool.play(sonarPing, 1, 1, 0, 0, 1);
            targetCell.setTag(R.id.ATTACKED_HERE, R.id.MISS);
            targetCell.setBackgroundColor(Color.rgb(0,0,255));
        } else {
            //HIT
            soundPool.play(fireHit, 1, 1, 0, 0, 1);
            timeDelay(2);
            targetCell.setBackgroundColor(Color.rgb(255,0,0));
            targetCell.setTag(R.id.ATTACKED_HERE, R.id.HIT);
            Battleship wounded = findShipByID((int) targetCell.getTag(R.id.SHIP_HERE), "user");
            wounded.attacked();
            if (!wounded.isAlive()) { //Ship is sinking
                soundPool.play(splash2, 1, 1, 0, 0, 1);
                for(int cell : wounded.getCells()) {
                    defenseGrid.getChildAt(cell).setBackgroundColor(Color.rgb(0,0,0)); //FIGURE OUT COLORS!!!
                    defenseGrid.getChildAt(cell).setTag(R.id.ATTACKED_HERE, R.id.SUNK);
                }
                findViewById(wounded.getSurvivalListViewID()).setVisibility(View.GONE);
            }
            if ((!userFleet[0].isAlive() && !userFleet[1].isAlive() //Game Over
                    && !userFleet[2].isAlive() && !userFleet[3].isAlive()
                    && !userFleet[4].isAlive())) {
                soundPool.play(pacmandies, 1, 1, 0, 0, 1);
                ((TextView) findViewById(R.id.Endgame)).setText("YOU LOSE");
                Button dummyButton = findViewById(R.id.dummy_button);
                ((TextView) findViewById(R.id.Endgame)).setText("YOU LOSE! Click New Game For Another!");
                dummyButton.setText("New Game");
                //dummyButton.setVisibility(View.VISIBLE);
                dummyButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        newGame();
                    }
                });
            }
        }
    }
    // RETURNS the targetCell
    private int attackAI (GridLayout defenseGrid) {
        Random r = new Random();
        List<Integer> hits = new ArrayList<>();
        for (int i = 0; i < defenseGrid.getChildCount(); i++) {
            if ((int) defenseGrid.getChildAt(i).getTag(R.id.ATTACKED_HERE) == R.id.HIT) {
                hits.add(i);
            }
        }
        if (hits.size() > 0) {
            for (int i = 0; i < hits.size() - 1; i++) {
                for (int j = i; j < hits.size(); j++) {
                    int hitIndexA = hits.get(i);
                    int hitIndexB = hits.get(j);
                    if (checkAdjacency(defenseGrid, hitIndexA, hitIndexB)) {
                        int iterationRelationship = Math.abs(hitIndexB - hitIndexA);
                        boolean sameRowRelationShip = false;
                        if (iterationRelationship == 1) {
                            sameRowRelationShip = true;
                        }
                        if (hitIndexA > hitIndexB) {
                            int temp = hitIndexB;
                            hitIndexB = hitIndexA;
                            hitIndexA = temp;
                        }
                        int analysisIndex = hitIndexA;
                        //The lesser loop
                        do {
                            analysisIndex -= iterationRelationship;
                            if (analysisIndex < 0
                                    || (sameRowRelationShip
                                        && defenseGrid.getChildAt(analysisIndex).getTag(R.id.MY_ROW)
                                            != defenseGrid.getChildAt(hitIndexA).getTag(R.id.MY_ROW))) {
                                break;
                            }
                            if((int) defenseGrid.getChildAt(analysisIndex).getTag(R.id.ATTACKED_HERE) == R.id.OPEN) {
                                return analysisIndex;
                            }
                        } while((int) defenseGrid.getChildAt(analysisIndex).getTag(R.id.ATTACKED_HERE) == R.id.HIT);
                        analysisIndex = hitIndexB;
                        //The greater loop
                        do {
                            analysisIndex += iterationRelationship;
                            if (analysisIndex >= defenseGrid.getChildCount()
                                    || (sameRowRelationShip
                                    && defenseGrid.getChildAt(analysisIndex).getTag(R.id.MY_ROW)
                                    != defenseGrid.getChildAt(hitIndexA).getTag(R.id.MY_ROW))) {
                                break;
                            }
                            if((int) defenseGrid.getChildAt(analysisIndex).getTag(R.id.ATTACKED_HERE) == R.id.OPEN) {
                                return analysisIndex;
                            }
                        } while((int) defenseGrid.getChildAt(analysisIndex).getTag(R.id.ATTACKED_HERE) == R.id.HIT);
                    } //CheckAdjacency close
                }
            }// Double for loop close
            List<Integer> potentialHits = new ArrayList<>();
            do {
                int focusCellIndex = hits.get(r.nextInt(hits.size())); //Setup dowhile in case their are no opens
                if (checkTargetNextToPreviousHitValidity(defenseGrid, focusCellIndex, focusCellIndex + 1, true)) {
                    potentialHits.add(focusCellIndex + 1);
                }
                if (checkTargetNextToPreviousHitValidity(defenseGrid, focusCellIndex, focusCellIndex - 1, true)) {
                    potentialHits.add(focusCellIndex - 1);
                }
                if (checkTargetNextToPreviousHitValidity(defenseGrid, focusCellIndex, focusCellIndex + defenseGrid.getColumnCount(), false)) {
                    potentialHits.add(focusCellIndex + defenseGrid.getColumnCount());
                }
                if (checkTargetNextToPreviousHitValidity(defenseGrid, focusCellIndex, focusCellIndex - defenseGrid.getColumnCount(), false)) {
                    potentialHits.add(focusCellIndex - defenseGrid.getColumnCount());
                }
            } while (potentialHits.size() == 0);
            return potentialHits.get(r.nextInt(potentialHits.size()));
        } else {// No hits at all - Full Random
            int targetIndex;
            do {
                targetIndex = r.nextInt(defenseGrid.getChildCount());
            } while ((int) defenseGrid.getChildAt(targetIndex).getTag(R.id.ATTACKED_HERE) != R.id.OPEN);
            return targetIndex;
        }
    }
    private boolean checkTargetNextToPreviousHitValidity(GridLayout defenseGrid, int previousHitIndex, int potentialIndex, boolean sameRow) {
        if (potentialIndex >= defenseGrid.getChildCount() || potentialIndex < 0
                || (sameRow && defenseGrid.getChildAt(previousHitIndex).getTag(R.id.MY_ROW) != defenseGrid.getChildAt(potentialIndex).getTag(R.id.MY_ROW))
                || (int) defenseGrid.getChildAt(potentialIndex).getTag(R.id.ATTACKED_HERE) != R.id.OPEN) {
            return false;
        } else {
            return true;
        }
    }
    private boolean checkAdjacency(GridLayout defenseGrid, int indexA, int indexB) {
        if ((defenseGrid.getChildAt(indexA).getTag(R.id.MY_ROW) == defenseGrid.getChildAt(indexB).getTag(R.id.MY_ROW)
                && (indexA == indexB + 1 || indexA == indexB - 1))
                || ((defenseGrid.getChildAt(indexA).getTag(R.id.MY_COLUMN) == defenseGrid.getChildAt(indexB).getTag(R.id.MY_COLUMN))
                    && (indexA == indexB + defenseGrid.getColumnCount() || indexA == indexB - defenseGrid.getColumnCount()))) {
            return true;
        } else {
            return false;
        }
    }
    private Battleship findShipByID(int id, String team) {
        Battleship[] fleet;
        if (team.equals("geoff")) {
            fleet = geoffFleet;
        } else {
            fleet = userFleet;
        }
        for (Battleship i : fleet) {
            if (i.getId() == id) {
                return i;
            }
        }
        return fleet[0];
    }
    private void defenseSetUp(final Button ship, final GridLayout defenseGrid) {
        for (int i = 0; i < defenseGrid.getChildCount(); i++) {
            final int k = i;
            final CardView cell = (CardView) defenseGrid.getChildAt(i);
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (setupPhase) {
                        System.out.println(k);
                        RadioGroup orientation = (RadioGroup) findViewById(R.id.orientationGroup);
                        int orientationID = orientation.getCheckedRadioButtonId();
                        if (orientationID == -1) {
                            return;
                        }
                        int color;
                        int size;
                        int shipVal;
                        int fleetIndex;
                        if (ship.equals(findViewById(R.id.Ship1))) {
                            color = Color.parseColor("#d4d8d4");
                            size = 3;
                            shipVal = R.id.SHIP_1;
                            fleetIndex = 1;
                        } else if (ship.equals(findViewById(R.id.Ship2))) {
                            color = Color.parseColor("#433e90");
                            size = 3;
                            shipVal = R.id.SHIP_2;
                            fleetIndex = 2;
                        } else if (ship.equals(findViewById(R.id.Ship3))) {
                            color = Color.parseColor("#a19c9c");
                            size = 4;
                            shipVal = R.id.SHIP_3;
                            fleetIndex = 3;
                        } else if (ship.equals(findViewById(R.id.Ship4))) {
                            color = Color.parseColor("#d2d2d2");
                            size = 5;
                            shipVal = R.id.SHIP_4;
                            fleetIndex = 4;
                        } else {
                            color = Color.parseColor("#326ada");
                            size = 2;
                            shipVal = R.id.SHIP_0;
                            fleetIndex = 0;
                        }
                        int[] previousCells;
                        if (ship.getTag() == null) {
                            previousCells = new int[0];
                        } else {
                            previousCells = (int[]) ship.getTag();
                        }
                        if (previousCells.length > 0) { //Allows spaces where the ship used to be to be used
                            for (int previousIndex : previousCells) {
                                CardView abandonedChild = (CardView) defenseGrid.getChildAt(previousIndex);
                                abandonedChild.setTag(R.id.SHIP_HERE, R.id.GHOST_SHIP);
                            }
                        }
                        if (!checkSpace(k, size, orientationID, defenseGrid)) { //quit if you outta room
                            return;
                        }
                        int[] cells = new int[size];
                        int childiteration;
                        if (orientationID == R.id.horizontalButton) {
                            childiteration = 1;
                        } else {
                            childiteration = defenseGrid.getColumnCount();
                        }
                        for (int i = 0; i < size; i++) {
                            int currentCellIndex = k + (i * childiteration);
                            cells[i] = currentCellIndex;
                            defenseGrid.getChildAt(currentCellIndex).setBackgroundColor(color);
                            defenseGrid.getChildAt(currentCellIndex).setTag(R.id.SHIP_HERE, shipVal);
                        }
                        if (previousCells.length > 0) {
                            for (int previousIndex : previousCells) {
                                CardView abandonedChild = (CardView) defenseGrid.getChildAt(previousIndex);
                                if ((int) abandonedChild.getTag(R.id.SHIP_HERE) == R.id.GHOST_SHIP) { //Only change it if it's not reused
                                    abandonedChild.setBackgroundColor(Color.rgb(255, 255, 255));
                                    abandonedChild.setTag(R.id.SHIP_HERE, R.id.VACANT);
                                }
                            }
                        }
                        ship.setTag(cells);
                        userFleet[fleetIndex].setCells(cells);
                        if (findViewById(R.id.Ship0).getTag() != null && findViewById(R.id.Ship1).getTag() != null
                                && findViewById(R.id.Ship2).getTag() != null && findViewById(R.id.Ship3).getTag() != null
                                && findViewById(R.id.Ship4).getTag() != null) {
                            final Button dummyButton = findViewById(R.id.dummy_button);
                            ((TextView) findViewById(R.id.Endgame)).setText("Press Start Game to begin");
                            dummyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (setupPhase) {
                                        setupPhase = false;
                                        final GridLayout offenseGrid = findViewById(R.id.offenseGrid);
                                        offenseSetUp(offenseGrid, defenseGrid);
                                        //dummyButton.setVisibility(View.INVISIBLE);
                                        dummyButton.setText("");
                                        ((TextView) findViewById(R.id.Endgame)).setText("Fight!");
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    /**
     *
     * @param startCell
     * @param length
     * @param orientationId
     * @param defenseGrid
     * @return TRUE if their is space FALSE if their isn't.
     */
    private boolean checkSpace(int startCell, int length, int orientationId, GridLayout defenseGrid) {
        int childIncrement;
        if (orientationId == R.id.horizontalButton) {
            childIncrement = 1;
        } else {
            childIncrement = defenseGrid.getColumnCount();
        }
        int homeRow = (int) defenseGrid.getChildAt(startCell).getTag(R.id.MY_ROW);
        for (int i = 0; i < length; i++) {
            int currentCell = startCell + (i * childIncrement);
            if (currentCell >= defenseGrid.getChildCount()
                    || ((orientationId == R.id.horizontalButton)
                        && ((int) defenseGrid.getChildAt(currentCell).getTag(R.id.MY_ROW) != homeRow))
                    || !(((int) defenseGrid.getChildAt(currentCell).getTag(R.id.SHIP_HERE) == R.id.VACANT)
                        || (int) defenseGrid.getChildAt(currentCell).getTag(R.id.SHIP_HERE) == R.id.GHOST_SHIP)) {
                return false;
            }
        }
        return true;
    }
    private void offenseSetUp(GridLayout offenseGrid, GridLayout defenseGrid) {
        int totalCellCount = offenseGrid.getChildCount();
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            int randomCell;
            int orientationID;
            int cellIteration;
            int[] cells = new int[geoffFleet[i].getSize()];
            do {
                randomCell = r.nextInt(totalCellCount);
                if (r.nextBoolean()) {
                    orientationID = R.id.horizontalButton;
                    cellIteration = 1;
                } else {
                    orientationID = R.id.verticalButton;
                    cellIteration = offenseGrid.getColumnCount();
                }
            } while (!checkSpace(randomCell, geoffFleet[i].getSize(), orientationID, offenseGrid));
            for (int j = 0; j < geoffFleet[i].getSize(); j++) {
                cells[j] = randomCell + (j*cellIteration);
                CardView currentChild = (CardView) offenseGrid.getChildAt(cells[j]);
                //currentChild.setBackgroundColor(Color.rgb(255, 255, 0));
                //MAKE GEOFF SHIPS YELLOW
                currentChild.setTag(R.id.SHIP_HERE, geoffFleet[i].getId());
            }
            geoffFleet[i].setCells(cells);
        }
        soundPool.play(gqShort, 1, 1, 0, 0, 1);
        startAttacking(offenseGrid, defenseGrid);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * Timer in seconds for pause.
     */
    public void timeDelay(int delay) {
        try {
            TimeUnit.SECONDS.sleep(delay);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

}
