/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.entity;

import android.content.res.Resources;
import com.cpaulus.droidrunner.R;

/**
 *
 * @author cyril
 */
public class CementWall extends Block{

    public CementWall(World w, Resources res) {
        super(w, res);
        setImage(res.getDrawable(R.drawable.cement));
        type = Block.BlockType.CEMENT;
    }
    
    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public boolean isLadder() {
        return false;
    }

    @Override
    public boolean isRope() {
        return false;
    }

    @Override
    public boolean isAiSolid() {
        return true;
    }
    
}
