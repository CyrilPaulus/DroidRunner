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
public class Rope extends Block{

    public Rope(World w, Resources res) {
        super(w, res);
        setImage(res.getDrawable(R.drawable.rope));
        type = Block.BlockType.ROPE;
    }
    
    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isLadder() {
        return false;
    }

    @Override
    public boolean isRope() {
        return true;
    }

    @Override
    public boolean isAiSolid() {
        return false;
    }
    
}
