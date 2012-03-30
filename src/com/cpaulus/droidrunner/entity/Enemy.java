/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.entity;

import android.content.res.Resources;
import com.cpaulus.droidrunner.R;
import com.cpaulus.droidrunner.ai.AiAgent;

/**
 *
 * @author cyril
 */
public class Enemy extends Player {
    
    private AiAgent e;
    
    public Enemy(World w, Resources res) {
        super(w, res);
        setImage(res.getDrawable(R.drawable.them));
        speedX = 92;
        speedY = 75;
        e = new AiAgent(w, this);
    }
    
    public void update(double frametime) {
        Input i = e.update(frametime);
        this.update(frametime, i);
    }
}
