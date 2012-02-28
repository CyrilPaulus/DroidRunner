/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import com.cpaulus.droidrunner.entity.Camera;
import com.cpaulus.droidrunner.entity.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cyril
 */
class DroidView extends SurfaceView implements SurfaceHolder.Callback {

    class DroidThread extends Thread {

        /*
         * State-tracking constants
         */
        public static final int STATE_LOSE = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;
        private long lastFrametime;
        private Handler mHandler;
        private boolean mRun = false;
        private SurfaceHolder mSurfaceHolder;
        private int mMode;
        private Ticker ticker;
        private Camera camera;
        private World world;      
        private static final String KEY_CAMX = "camX";
        private static final String KEY_CAMY = "camY";
        private Paint paint;
        private Input input;
        private int map = 0;
        private int live = 3;
        double fps;
        boolean reloadMap = false;
              
        int screenWidth;
        int screenHeight;
        
        public synchronized void nextMap() {
            map = (map + 1) % 151;
            reloadMap = true;
        }

        public void prevMap() {
            map = (151 + (map - 1)) % 151;
            reloadMap = true;
        }

        public DroidThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {

            fps = 0;
            input = new Input();

            
            
            paint = new TextPaint();
            paint.setTextSize(30);
            paint.setARGB(255, 255, 255, 255);


            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;

            Resources res = context.getResources();
            AssetManager manager = context.getAssets();

            camera = new Camera();
            world = new World(res, camera);
            
            
            
            try {
                world.loadFromMap(manager.open("map/map0"));
            } catch (IOException ex) {
                Logger.getLogger(DroidView.class.getName()).log(Level.SEVERE, null, ex);
            }
            ticker = new Ticker();

        }

        /**
         * Starts the game, setting parameters for the current difficulty.
         */
        public void doStart() {
            synchronized (mSurfaceHolder) {
                setState(STATE_RUNNING);

            }
        }

        /**
         * Pauses the physics update & animation.
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) {
                    setState(STATE_PAUSE);
                }
            }
        }

        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         *
         * @param savedState Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
                setState(STATE_PAUSE);
               
                
                map = savedState.getInt("KEY_MAP");
                try {
                    world.loadFromMap(mContext.getAssets().open("map/map"+map));
                } catch (IOException ex) {
                    Logger.getLogger(DroidView.class.getName()).log(Level.SEVERE, null, ex);
                }
                world.unBundle(savedState);
                camera.setX(savedState.getInt(KEY_CAMX));
                camera.setY(savedState.getInt(KEY_CAMY));
                if(savedState.getBoolean("KEY_PLAINSCREEN"))
                    togglePlainScreen();
                
                /*
                 * mDifficulty = savedState.getInt(KEY_DIFFICULTY); mX =
                 * savedState.getDouble(KEY_X); mY =
                 * savedState.getDouble(KEY_Y); mDX =
                 * savedState.getDouble(KEY_DX); mDY =
                 * savedState.getDouble(KEY_DY); mHeading =
                 * savedState.getDouble(KEY_HEADING);
                 *
                 * mLanderWidth = savedState.getInt(KEY_LANDER_WIDTH);
                 * mLanderHeight = savedState.getInt(KEY_LANDER_HEIGHT); mGoalX
                 * = savedState.getInt(KEY_GOAL_X); mGoalSpeed =
                 * savedState.getInt(KEY_GOAL_SPEED); mGoalAngle =
                 * savedState.getInt(KEY_GOAL_ANGLE); mGoalWidth =
                 * savedState.getInt(KEY_GOAL_WIDTH); mWinsInARow =
                 * savedState.getInt(KEY_WINS); mFuel =
                 * savedState.getDouble(KEY_FUEL);
                *
                 */

            }
        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (ticker.tick()) {
                            update(ticker.getElapsedTime());
                        } else {
                            try {
                                sleep(10);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(DroidView.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        doDraw(c);

                    }
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /**
         * Dump game state to the provided Bundle. Typically called when the
         * Activity is being suspended.
         *
         * @return Bundle with this view's state
         */
        public Bundle saveState(Bundle bundle) {
            synchronized (mSurfaceHolder) {

                if (bundle != null) {
                  world.Bundle(bundle);
                    bundle.putInt("KEY_MAP", map);
                    bundle.putBoolean("KEY_PLAINSCREEN", camera.getPlainScreen());
                    bundle.putDouble(KEY_CAMX, Double.valueOf(camera.getX()));
                    bundle.putDouble(KEY_CAMY, Double.valueOf(camera.getY()));

                }

            }
            return bundle;
        }

        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         *
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            mRun = b;
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @see #setState(int, CharSequence)
         * @param mode one of the STATE_* constants
         */
        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }

        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @param mode one of the STATE_* constants
         * @param message string to add to screen or null
         */
        public void setState(int mode, CharSequence message) {
            /*
             * This method optionally can cause a text message to be displayed
             * to the user when the mode changes. Since the View that actually
             * renders that text is part of the main View hierarchy and not
             * owned by this thread, we can't touch the state of that View.
             * Instead we use a Message + Handler to relay commands to the main
             * thread, which updates the user-text View.
             */
            synchronized (mSurfaceHolder) {
                mMode = mode;
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("text", "");
                b.putInt("viz", View.INVISIBLE);
                msg.setData(b);
                mHandler.sendMessage(msg);
            }
        }

        /*
         * Callback invoked when the surface dimensions change.
         */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {              
                camera.setSize(width, height);
                screenWidth = width;
                screenHeight = height;
            }
        }

        /**
         * Resumes from a pause.
         */
        public void unpause() {
            // Move the real time clock up to now
            synchronized (mSurfaceHolder) {
            }
            setState(STATE_RUNNING);
        }

        /**
         * Handles a key-down event.
         *
         * @param keyCode the key that was pressed
         * @param msg the original event object
         * @return true
         */
        boolean doKeyDown(int keyCode, KeyEvent msg) {
            synchronized (mSurfaceHolder) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    input.Up = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    input.Down = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    input.Left = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    input.Right = true;
                } else {
                    return false;
                }


                return true;
            }

        }

        /**
         * Handles a key-up event.
         *
         * @param keyCode the key that was pressed
         * @param msg the original event object
         * @return true if the key was handled and consumed, or else false
         */
        boolean doKeyUp(int keyCode, KeyEvent msg) {

            synchronized (mSurfaceHolder) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    input.Up = false;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    input.Down = false;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    input.Left = false;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    input.Right = false;
                } else {
                    return false;
                }
                return true;
            }

        }

        /**
         * Draws the ship, fuel/speed bars, and background to the provided
         * Canvas.
         */
        private void doDraw(Canvas canvas) {
            canvas.drawRGB(100, 149, 237);
            
            
            canvas.save();
            if(camera.getPlainScreen()){
                float ratio = (float) screenWidth / world.getVisibleWidth();
                float delta = screenHeight / 2 - world.getVisibleHeight() * 0.5f * ratio;
                canvas.translate(0, delta);
                canvas.scale(ratio, ratio);                
            }
            else
                canvas.translate(-(int) camera.getX(), -(int) camera.getY());
            
            
            
            world.draw(canvas);
            canvas.restore();
            canvas.drawText("Lives:" + live, 0, screenHeight, paint);
                    
        }

        /**
         * Figures the lander state (x, y, fuel, ...) based on the passage of
         * realtime. Does not invalidate(). Called at the start of draw().
         * Detects the end-of-game and sets the UI to the next state.
         */
        private void update(long milli) {
            
            if(reloadMap) {
                reloadMap = false;
                try {
                    world.loadFromMap(mContext.getAssets().open("map/map" + map));
                } catch (IOException ex) {
                    Logger.getLogger(DroidView.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
            
            double seconds = milli / 1000.0;            
            switch(world.update(seconds, input)) {
                case DEAD:
                    reloadMap = true;
                    live--;
                    break;
                case WIN:
                    nextMap();
                    live++;
                    break;
                case NOTHING:
                    break;
            }
            camera.update();

        }
        
        public void togglePlainScreen() {
            camera.setPlainScreen(!camera.getPlainScreen());            
        }
    }
    /**
     * Handle to the application context, used to e.g. fetch Drawables.
     */
    private Context mContext;
    /**
     * Pointer to the text view to display "Paused.." etc.
     */
    private TextView mStatusText;
    /**
     * The thread that actually draws the animation
     */
    private DroidThread thread;

    public DroidView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new DroidThread(holder, context, new Handler() {

            @Override
            public void handleMessage(Message m) {
                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        });

        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public DroidThread getThread() {
        return thread;
    }

    /**
     * Standard override to get key-press events.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        return thread.doKeyDown(keyCode, msg);
    }

    /**
     * Standard override for key-up. We actually care about these, so we can
     * turn off the engine or stop rotating.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent msg) {
        return thread.doKeyUp(keyCode, msg);
    }

    /**
     * Standard window-focus override. Notice focus lost so we can pause on
     * focus lost. e.g. user switches to take a call.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
            thread.pause();
        }
    }

    /**
     * Installs a pointer to the text view used for messages.
     */
    public void setTextView(TextView textView) {
        mStatusText = textView;
    }

    /*
     * Callback invoked when the surface dimensions change.
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
    
   
}
