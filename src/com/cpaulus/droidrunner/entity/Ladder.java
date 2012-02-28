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
public class Ladder extends Block {
    
    public Ladder(World w, Resources res) {
        super(w, res);
        setImage(res.getDrawable(R.drawable.ladder));
        type = Block.BlockType.LADDER;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isLadder() {
        return true;
    }

    @Override
    public boolean isRope() {
        return false;
    }

    @Override
    public boolean isAiSolid() {
        return false;
    }
    
}
