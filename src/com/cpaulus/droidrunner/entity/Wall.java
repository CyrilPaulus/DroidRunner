/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.entity;

import com.cpaulus.droidrunner.R;
import android.content.res.Resources;

/**
 *
 * @author cyril
 */
public class Wall extends Block{

   public Wall(World w, Resources res) {
        super(w, res);
        setImage(res.getDrawable(R.drawable.wall));
        type = Block.BlockType.WALL;
    }
    
    public boolean isSolid() {
        return true;
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
    
}
