/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.entity;

import android.content.res.Resources;
import android.graphics.Canvas;

/**
 *
 * @author cyril
 */
public abstract class Block extends Entity {
    
    public static enum BlockType {EMPTY, WALL, CEMENT, LADDER, ROPE, FALSE, ENDLADDER};
    
    protected BlockType type;
    
    public static Block createBlock(int code, World w, Resources r) {
        switch(code) {
            case 0:
                return new EmptyWall(w, r);
            case 1:
                return new Wall(w, r);
            case 2:
                return new CementWall(w, r);
            case 3:
                return new Ladder(w, r);
            case 4:
                return new Rope(w, r);
            case 5:
                return new FalseWall(w, r);
            case 6:
                return new EndLadder(w, r);
            default:
                return null;            
        }
    }
    
    public static final int WIDTH = 36;
    public static final int HEIGHT = 30;
    
    
    public Block(World w, Resources res) {
        super(w, res);
    }
    
    public void update(double frametime){};
    public void carve(){};
    
    abstract public boolean isSolid();
    abstract public boolean isLadder();
    abstract public boolean isRope();
    abstract public boolean isAiSolid();
    
    public BlockType getType() {
        return type;
    }
    
    
    
}
