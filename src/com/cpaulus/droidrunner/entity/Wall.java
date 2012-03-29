/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.entity;

import com.cpaulus.droidrunner.R;
import android.content.res.Resources;
import android.graphics.Canvas;

/**
 *
 * @author cyril
 */
public class Wall extends Block{

    private boolean carved;
    private double carvedTime;
   public Wall(World w, Resources res) {
        super(w, res);
        setImage(res.getDrawable(R.drawable.wall));
        type = Block.BlockType.WALL;
        carved = false;
        carvedTime = 0;
    }
    
    public boolean isSolid() {
        return !carved;
    }
   
    public boolean isLadder() {
       return false;
    }
 
    public boolean isRope() {
        return false;
    }

    @Override
    public boolean isAiSolid() {
        return true;
    }
    
    @Override
    public void carve() {
        Block c = world.getBlock((int)(x / Block.WIDTH), (int)(y / Block.HEIGHT) - 1);
        if(!c.isRope() && !c.isSolid() && !c.isLadder()) {
            carved = true;
            carvedTime = 0;
        }
    }
    
    @Override
    public void update(double frametime) {
        if(carved) {
            carvedTime += frametime;
            if(carvedTime > 10) {
                carved = false;
            }
        }
    }
    
    @Override
    public void draw(Canvas c) {
        if(!carved)
            super.draw(c);
    }
}
