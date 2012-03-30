/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.ai;

import com.cpaulus.droidrunner.entity.Block;
import com.cpaulus.droidrunner.entity.Input;
import com.cpaulus.droidrunner.entity.Player;
import com.cpaulus.droidrunner.entity.World;

/**
 *
 * @author cyril
 */
public class AiAgent {
    private Player c;

    
    private World world;
    private long lastUpdate;
    
    public AiAgent(World w, Player c) {
        this.c = c;
        this.world = w;   
        lastUpdate = 0;
    }
    
    public static enum Direction {STAND, LEFT, RIGHT, UP, DOWN};
    
   
    public Input update(double frametime) {
        Input rtn = new Input();        
        
        
        Direction dir = bestPath();
        
        if(dir == Direction.UP)
            rtn.Up = true;
        else if(dir == Direction.DOWN)
            rtn.Down = true;
        else if(dir == Direction.LEFT)
            rtn.Left = true;
        else if(dir == Direction.RIGHT)
            rtn.Right = true;
               
        return rtn;
    }
    
    //AI based on KgoldRunner one
    
    private Direction bestPath() {
        Direction dirn = Direction.STAND;
        
        int x0 = (int)(c.getCenterX() / Block.WIDTH);
        int y0 = (int)(c.getCenterY() / Block.HEIGHT);

        int x1 = (int)(world.getPlayer().getCenterX() / Block.WIDTH);
        int y1 = (int)(world.getPlayer().getCenterY() / Block.HEIGHT);
        
        //Both on same row
        if(y0 == y1) {
            dirn = getHero(x0, y0, x1);
            if(dirn != Direction.STAND)
                return dirn;
        }
        
        if(y0 >= y1)
            dirn = searchUp(x0, y0, y1);
        else {
            dirn = searchDown(x0, y0, y1);
            if(dirn == Direction.STAND) {
                dirn = searchUp(x0, y0, y1);
            }
        }
        
        if(dirn == Direction.STAND) {
            dirn = searchDown(x0, y0, y0 - 1);
        }
        
        return dirn;
    }
    
    private Direction getHero(int x0, int y0, int x1) {
        int i, inc, returnValue;
        
        inc = (x0 > x1) ? -1 : +1;
        i = x0;
        
        while(i != x1) {
            returnValue = canWalkLR(inc, i, y0);
            if(returnValue > 0)
                i += inc;
            else if (returnValue < 0)
                break;
            else 
                return Direction.STAND;
        }
        
        if(i < x0) 
            return Direction.LEFT;
        else if (i > x0)
            return Direction.RIGHT;
        else
            return Direction.STAND;
    }
    
    private int canWalkLR(int direction, int x, int y) {
        if(willNotFall(x, y)) {
            Block b = world.getBlock(x + direction, y);
            if(b.isAiSolid())
                return -1;
            else
                return 1;
        } else 
            return 0;
        
        
    }
    
    private boolean willNotFall(int x, int y) {
        Block b = world.getBlock(x, y);
        if (b.isLadder() || b.isRope()) {
            return true;
        }

        b = world.getBlock(x, y + 1);

        if (!b.isAiSolid() && !b.isLadder()) {
            return false;
        }
        return true;
    }
    
    private Direction searchUp(int x0, int y0, int y1) {
        int i, iLen, iPos, j, jLen, jPos, deltaH, rungs;
        
        deltaH = y0 - y1;
        
        //Search for the best ladder on the left
        i = x0; iLen = 0; iPos = -1;
        while(i >= 1) {
            rungs = distanceUp(i, y0, deltaH);
            if(rungs > iLen) {
                iLen = rungs;
                iPos = i;
            }
            
            if(searchOk(-1, i, y0))
                i--;
            else
                i = -1;
        }
        
        //Search for the best ladder on the right
        j = x0; jLen = 0; jPos = -1;
        while(j < world.getWidth()) {
            if(searchOk(+1, j, y0)) {
                j++;
                rungs = distanceUp(j, y0, deltaH);
                if(rungs > jLen) {
                    jLen = rungs;
                    jPos = j;
                }
            }
            else
                j = world.getWidth() + 1;
        }
        
        if(iLen == 0 && jLen == 0)
            return Direction.STAND;
        
        if(iLen != jLen) {
            if(iLen > jLen) {
                if(iPos == x0)
                    return Direction.UP;
                else
                    return Direction.LEFT;
            }
            else
                return Direction.RIGHT;
        } else {
            if(iPos == x0)
                return Direction.UP;
            else if (iLen == deltaH) {
                if ((x0 - iPos <= jPos - x0))
                    return Direction.LEFT;
                else
                    return Direction.RIGHT;
            }
            else return Direction.LEFT;
        }       
        
    }
    
    private Direction searchDown(int x0, int y0, int y1) {
        int i, ilen, ipos, j, jlen, jpos, deltaY, rungs, path;
        
        deltaY = y1 - y0;
        
        ilen = 0; ipos = -1;
        i = (willNotFall(x0, y0)) ? x0 : -1;
        rungs = distanceDown(x0, y0, deltaY);
        
        if(rungs > 0) {
            ilen = rungs; ipos = x0;
        }
        
        while(i >= 1) {
            rungs = distanceDown(i - 1, y0, deltaY);
            if ((rungs > 0 && ilen == 0) ||
                (deltaY > 0 && rungs > ilen) ||
                (deltaY <= 0 && rungs < ilen && rungs != 0)) {
                ilen = rungs;
                ipos = i - 1;
            }
            if (searchOk(-1, i, y0))
                i--;
            else
                i = -1;            
        }
        
        j = x0;
        jlen = 0;
        jpos = -1;
        while (j < world.getWidth()) {
            rungs = distanceDown(j + 1, y0, deltaY);
            if ((rungs > 0 && jlen == 0) || 
                (deltaY > 0 && rungs < jlen) ||
                (deltaY <= 0 && rungs < jlen && rungs != 0)) {
                jlen = rungs;
                jpos = j + 1;
            }
            if(searchOk(1, j, y0))
                j++;
            else
                j = world.getWidth() + 1;
        }
        
        if(ilen == 0 && jlen == 0)
            return Direction.STAND;
        
        if(ilen == 0)
            path = jpos;
        else if (jlen == 0)
            path = ipos;
        else if (ilen != jlen) {
            
            if(deltaY > 0) {
                if (jlen > ilen)
                    path = jpos;
                else
                    path = ipos;
            } else {
                if(jlen > ilen)
                    path = ipos;
                else
                    path = jpos;
            }
        } else {
            if (deltaY > 0 && ilen == deltaY) {
                if (x0 - ipos <= jpos - x0)
                    path = ipos;
                else 
                    path = jpos;
            }
            else path = ipos;
        }
        
        if(path == x0)
            return Direction.DOWN;
        else if (path < x0)
            return Direction.LEFT;
        else
            return Direction.RIGHT;        
    }
    
    private int distanceUp(int x, int y, int deltaY) {
        int rungs = 0;
        while(world.getBlock(x, y - rungs).isLadder()) {
            rungs++;
            if(rungs >= deltaY)
                break;
        }
        return rungs;
    }
    
    private int distanceDown(int x, int y, int deltaY) {
        int rungs = -1;
        int exitRung = 0;
        boolean canGoThru = true;
        
        while (canGoThru) {
            Block b = world.getBlock(x, y + rungs + 1);
            if (b.isAiSolid()) {
                if (deltaY > 0 && rungs <= deltaY) {
                    exitRung = rungs;
                }
                if (!b.isSolid() && rungs < 0) {
                    rungs = 0;
                } else {
                    canGoThru = false;
                }
            } else if (b.isLadder() || b.isRope()) {
                rungs++;
                if (deltaY > 0 && rungs >= 0) {
                    if (rungs - 1 <= deltaY) {
                        if (b.isLadder() && (searchOk(-1, x, y + rungs - 1)
                                || searchOk(1, x, y + rungs - 1))) {
                            exitRung = rungs - 1;
                        }
                        if (rungs <= deltaY && (searchOk(-1, x, y + rungs)
                                || searchOk(1, x, y + rungs))) {
                            exitRung = rungs;
                        }
                    } else {
                        canGoThru = false;
                    }
                }
            } else {
                rungs++;
            }
        }
        
        if(rungs == 1)
            if(world.enemyOccupied(x, y + 1))
                rungs = 0;
        
        if(rungs <= 0)
            return 0;
        else if (deltaY > 0)
            return exitRung;
        else
            return rungs;        
    }
    
    private boolean searchOk(int direction, int x, int y) {
        if(canWalkLR(direction, x, y) > 0)
            if(willNotFall(x+direction, y))
                return true;
        return false;
    }
    
}
