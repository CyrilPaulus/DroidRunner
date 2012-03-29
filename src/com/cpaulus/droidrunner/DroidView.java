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
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import com.cpaulus.droidrunner.entity.Camera;
import com.cpaulus.droidrunner.entity.*;
import java.io.IOException;
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
        long lastDownEvent = 0;
        boolean isTouchDown  = false;
        boolean isLeftDown = false;
        boolean isRightDown = false;
        boolean isUpDown = false;
        boolean isDownDown = false;
        boolean isCarveLeftDown = false;
        boolean isCarveRightDown = false;
        double touchX = 0;
        double touchY = 0;
      
              
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
            lastFrametime = System.currentTimeMillis();
            
              
            
            
            try {
                world.loadFromMap(manager.open("map/map0"));
            } catch (IOException ex) {
                Logger.getLogger(DroidView.class.getName()).log(Level.SEVERE, null, ex);
            }         

        }

        /**
         * Starts the game, setting parameters for the current difficulty.
         */
        public void doStart() {
            synchronized (mSurfaceHolder) {
                

            }
        }

        /**
         * Pauses the physics update & animation.
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) {
                    
                    
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
                        update();
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
         * Handles a key-down event.
         *
         * @param keyCode the key that was pressed
         * @param msg the original event object
         * @return true
         */
        boolean doKeyDown(int keyCode, KeyEvent msg) {
            synchronized (mSurfaceHolder) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    isUpDown = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    isDownDown = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    isLeftDown = true;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    isRightDown = true;
                } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    isCarveLeftDown = true;
                } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    isCarveRightDown = true;
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
                    isUpDown = false;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    isDownDown = false;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    isLeftDown = false;
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    isRightDown = false;
                } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    isCarveLeftDown = false;
                } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    isCarveRightDown = false;
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
            String liveString = "Lives:" + live;
            String goalCountString = "Goals: " + world.getGoalLeft();
            
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
            canvas.drawText(liveString, 0, screenHeight, paint);
            canvas.drawText(goalCountString, screenWidth - paint.measureText(goalCountString), screenHeight, paint);
                    
        }

        /**
         * Figures the lander state (x, y, fuel, ...) based on the passage of
         * realtime. Does not invalidate(). Called at the start of draw().
         * Detects the end-of-game and sets the UI to the next state.
         */
        private void update() {
            updateInput();
            long now = System.currentTimeMillis();
            
            if(reloadMap) {
                reloadMap = false;
                try {
                    world.loadFromMap(mContext.getAssets().open("map/map" + map));
                } catch (IOException ex) {
                    Logger.getLogger(DroidView.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
            
            if(lastFrametime > now)
                return;
            
            double seconds = Math.min((now - lastFrametime) / 1000.0, 0.5);
                     
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
            lastFrametime = now;

        }
        
        public void togglePlainScreen() {
            camera.setPlainScreen(!camera.getPlainScreen());            
        }

        private void updateInput() {
            input.Left = isLeftDown;
            input.Right = isRightDown;
            input.Up = isUpDown;
            input.Down = isDownDown;
            input.CarveLeft = isCarveLeftDown;
            input.CarveRight = isCarveRightDown;
            
            
            long now = System.currentTimeMillis();
            if(isTouchDown && now - lastDownEvent > 100) {
                  if(touchX > camera.globalToLocalX(world.getPlayer().getRight())) {
                    input.Right = true;
                    input.Left = false;
                }            
                else if(touchX < camera.globalToLocalX(world.getPlayer().getX())) {
                    input.Left = true;
                    input.Right = false;
                } else {
                    input.Left = false;
                    input.Right = false;
                }
                
                if(touchY > camera.globalToLocalY(world.getPlayer().getBottom())) {
                    input.Up = false;
                    input.Down = true;
                } else if (touchY < camera.globalToLocalY(world.getPlayer().getY())){
                    input.Down = false;
                    input.Up = true;
                } else {
                    input.Up = false;
                    input.Down = false;
                }
                
            } else if(!isTouchDown && now - lastDownEvent < 200) {
                 if(touchX > camera.globalToLocalX(world.getPlayer().getRight())) 
                     input.CarveRight = true;
                 else if(touchX < camera.globalToLocalX(world.getPlayer().getX())) {
                     input.CarveLeft = true;
                 }
            }             
            
        }
        private boolean doTouch(MotionEvent evt) {
            long now = System.currentTimeMillis();
            
            if(evt.getAction() == MotionEvent.ACTION_DOWN) {
                lastDownEvent = now;
                isTouchDown = true;
            }
            
            if(evt.getAction() == MotionEvent.ACTION_UP) {
                isTouchDown = false;
            }
            
            touchX = evt.getX();
            touchY = evt.getY();              
            
            return true;
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
    
    @Override
    public boolean onTouchEvent(MotionEvent evt) {
        return thread.doTouch(evt);
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
