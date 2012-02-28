/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner;

/**
 *
 * @author cyril
 */

public class Ticker {

    private int ticktime = 50;
    private long lastTime;
    private long elapsedTime;

    public Ticker() {
        lastTime = System.currentTimeMillis();
        elapsedTime = 0;
    }

    boolean tick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime > lastTime + ticktime) {
            elapsedTime = currentTime - lastTime;
            lastTime = currentTime;
            return true;
        } else {
            return false;
        }
    }

    public long getElapsedTime() {
        return 50;
    }
}
