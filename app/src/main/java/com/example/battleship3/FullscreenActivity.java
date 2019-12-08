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

import androidx.gridlayout.widget.GridLayout;
import java.util.Random;
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
    private Battleship[] fleet = new Battleship[5];
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
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
        final GridLayout offenseGrid = findViewById(R.id.offenseGrid);
        final GridLayout defenseGrid = findViewById(R.id.defenseGrid);
        Button[] ships = {(Button) findViewById(R.id.Ship0), (Button) findViewById(R.id.Ship1),
                (Button) findViewById(R.id.Ship2), (Button) findViewById(R.id.Ship3), (Button) findViewById(R.id.Ship4)};
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
            thisColumnO++;
            if (thisColumnO == columnCountO) {
                thisColumnO = 0;
                thisRowO++;
            }
        }
    }

    private void startAttacking(GridLayout offenseGrid) {
        for (int i = 0; i < offenseGrid.getChildCount(); i++) {
            final CardView cell = (CardView) offenseGrid.getChildAt(i);
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cell.setCardBackgroundColor(Color.BLACK);
                }
            });
        }
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
                        if (ship.equals(findViewById(R.id.Ship1))) {
                            color = Color.rgb(255, 0, 0);
                            size = 3;
                            shipVal = R.id.SHIP_1;
                        } else if (ship.equals(findViewById(R.id.Ship2))) {
                            color = Color.rgb(0, 255, 0);
                            size = 3;
                            shipVal = R.id.SHIP_2;
                        } else if (ship.equals(findViewById(R.id.Ship3))) {
                            color = Color.rgb(255, 0, 255);
                            size = 4;
                            shipVal = R.id.SHIP_3;
                        } else if (ship.equals(findViewById(R.id.Ship4))) {
                            color = Color.rgb(0, 255, 255);
                            size = 5;
                            shipVal = R.id.SHIP_4;
                        } else {
                            color = Color.rgb(0, 0, 255);
                            size = 2;
                            shipVal = R.id.SHIP_0;
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
                        if (findViewById(R.id.Ship0).getTag() != null && findViewById(R.id.Ship1).getTag() != null
                                && findViewById(R.id.Ship2).getTag() != null && findViewById(R.id.Ship3).getTag() != null
                                && findViewById(R.id.Ship4).getTag() != null) {
                            Button dummyButton = findViewById(R.id.dummy_button);
                            dummyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (setupPhase) {
                                        setupPhase = false;
                                        final GridLayout offenseGrid = findViewById(R.id.offenseGrid);
                                        offenseSetUp(offenseGrid);
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
    private void offenseSetUp(GridLayout offenseGrid) {
        int totalCellCount = offenseGrid.getChildCount();
        int[] sizes = new int[] {2, 3, 3, 4, 5};
        int[] shipTags = new int[] {R.id.SHIP_0, R.id.SHIP_1, R.id.SHIP_2, R.id.SHIP_3, R.id.SHIP_4};
        Random r = new Random();
        for (int i = 0; i < 5; i++) {
            int randomCell;
            int orientationID;
            int cellIteration;
            do {
                randomCell = r.nextInt(totalCellCount);
                if (r.nextBoolean()) {
                    orientationID = R.id.horizontalButton;
                    cellIteration = 1;
                } else {
                    orientationID = R.id.verticalButton;
                    cellIteration = offenseGrid.getColumnCount();
                }
            } while (!checkSpace(randomCell, sizes[i], orientationID, offenseGrid));
            for (int j = 0; j < sizes[i]; j++) {
                CardView currentChild = (CardView) offenseGrid.getChildAt(randomCell + (j * cellIteration));
                currentChild.setBackgroundColor(Color.rgb(255, 255, 0));
                currentChild.setTag(R.id.SHIP_HERE, shipTags[i]);
            }
        }
        startAttacking(offenseGrid);
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
}
