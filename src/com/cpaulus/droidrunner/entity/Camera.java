/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.entity;

/**
 *
 * @author cyril
 */
public class Camera extends Entity{
    
    private final double VISX = 0.2;
    private final double VISY = 0.2;
    private boolean plainScreen;
    
    Entity tracked;
    
    public Camera() {      
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        plainScreen = false;
    }
       
    public void moveX(double offset) {
        this.x += offset;
    }
    
    public void moveY(double offset) {
        this.y += offset;
    }
    
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }
    
    public void update() {
        if(tracked != null && ! plainScreen) {
            if(tracked.getCenterX() > getCenterX() + VISX * width)
                moveX(tracked.getCenterX() - getCenterX() - VISX * width);
            else if(tracked.getCenterX() < getCenterX() - VISX * width)
                moveX(-(getCenterX() - VISX * width - tracked.getCenterX()));
            
            if(tracked.getCenterY() > getCenterY() + VISY * height)
                moveY(tracked.getCenterY() - getCenterY() - VISY * height);
            else if(tracked.getCenterY() < getCenterY() - VISY * height)
                moveY(-(getCenterY() - VISY * height - tracked.getCenterY()));
        }
    }
    
    public double globalToLocalX(double xIn) {
        return xIn - x;
    }
    
    public double globalToLocalY(double yIn) {
        return yIn - y;
    }
        
    public void track(Entity ent) {
        tracked = ent;
    }
    
    public void untrack() {
        tracked = null;
    }
    
    public void setPlainScreen(boolean pl) {
        plainScreen = pl;
    }
    
    public boolean getPlainScreen() {
        return plainScreen;
    }
    
    
}
