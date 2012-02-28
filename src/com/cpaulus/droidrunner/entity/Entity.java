/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.entity;


import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import com.cpaulus.droidrunner.R;

/**
 *
 * @author cyril
 */
public class Entity {
    
    protected Drawable image;
    
    
    protected double x;
    protected double y;
    protected int width;
    protected int height;
    protected World world;
    
    public Entity() {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        image = null;
        world = null;
    }
    
    public Entity(World w) {
        this();
        this.world = w;
    }
    
    public Entity(World w, Resources res) {
        this(w);
        setImage(res.getDrawable(R.drawable.you));
    }
    
    public void draw(Canvas c) {
        image.setBounds((int)x, (int)y, width + (int)x, height + (int)y);
        image.draw(c);
    }
    
    public void update(){
        
    }
    
    public boolean intersects(Entity t) {
        
        
    double left   = Math.max(x, t.x);
    double top    = Math.max(y, t.y);
    double right  = Math.min(x + width, t.x + t.width);
    double bottom = Math.min(y + height, t.y + t.height);

    // If the intersection is valid (positive non zero area), then there is an intersection
    if ((left < right) && (top < bottom)) 
        return true;    
    else   
        return false;
    
       
    }
    
    /**
     * @param image the image to set
     */
    public void setImage(Drawable image) {
        this.image = image;
        this.width = image.getMinimumWidth();
        this.height = image.getMinimumHeight();
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }
    
    
    public double getTop() {
        return y;
    }
    
    public double getBottom() {
        return y + height;
    }
    
    public double getLeft() {
        return x;
    }
    
    public double getRight() {
        return x + width;
    }
    
    public double getCenterX() {
        return x + width / 2;
    }
    
    public double getCenterY() {
        return y + height /2;
    }
    
    public void setCenterX(double x) {
        this.x = x - width / 2;
    }
    
    public void setCenterY(double y) {
        this.y = y - height / 2;
    }
 }
    
    

