package com.cpaulus.droidrunner;

import android.app.Activity;
import android.os.Bundle;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.cpaulus.droidrunner.DroidView.DroidThread;


/**
 * This is a simple LunarLander activity that houses a single LunarView. It
 * demonstrates...
 * <ul>
 * <li>animating by calling invalidate() from draw()
 * <li>loading and drawing resources
 * <li>handling onPause() in an animation
 * </ul>
 */
public class DroidRunner extends Activity {
    
    private static final int MENU_MAPNEXT = 0;
    private static final int MENU_MAPPREV = 1;
    private static final int MENU_PLAINSCREEN = 2;

 

    /** A handle to the thread that's actually running the animation. */
    private DroidThread droidThread;

    /** A handle to the View in which the game is running. */
    private DroidView droidView;

    /**
     * Invoked during init to give the Activity a chance to set up its Menu.
     *
     * @param menu the Menu to which entries may be added
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_MAPNEXT, 0, R.string.menu_next);
        menu.add(0, MENU_MAPPREV, 0, R.string.menu_prev);
        menu.add(0, MENU_PLAINSCREEN, 0, R.string.menu_plainscreen);
        

        return true;
    }

    /**
     * Invoked when the user selects an item from the Menu.
     *
     * @param item the Menu entry which was selected
     * @return true if the Menu item was legit (and we consumed it), false
     *         otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_MAPNEXT:
                droidView.getThread().nextMap();
                return true;
            case MENU_MAPPREV:
                droidView.getThread().prevMap();
                return true;
            case MENU_PLAINSCREEN:
                droidView.getThread().togglePlainScreen();
                return true;
        }

        return false;
    }

    /**
     * Invoked when the Activity is created.
     *
     * @param savedInstanceState a Bundle containing state saved from a previous
     *        execution, or null if this is a new execution
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // tell system to use the layout defined in our XML file
        setContentView(R.layout.main);

        // get handles to the LunarView from XML, and its LunarThread
        droidView = (DroidView) findViewById(R.id.lunar);
        droidThread = droidView.getThread();

        // give the LunarView a handle to the TextView used for messages
        droidView.setTextView((TextView) findViewById(R.id.text));

        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            droidThread.setState(DroidThread.STATE_READY);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            droidThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        droidView.getThread().pause(); // pause game when Activity pauses
    }

    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     *
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        droidThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }
}