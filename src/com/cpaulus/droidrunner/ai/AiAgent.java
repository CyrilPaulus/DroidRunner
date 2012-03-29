/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.ai;

import com.cpaulus.droidrunner.entity.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author cyril
 */
public class AiAgent {
    private Player c;
    private Block current;
    private LinkedList<Block> path;
    private World world;
    private long lastUpdate;
    
    public AiAgent(World w, Player c) {
        this.c = c;
        this.world = w;
        current = null;
        path = new LinkedList<Block>();
        lastUpdate = 0;
    }
    
    private int distM(int x0, int y0, int x1, int y1) {
        return Math.abs(x0 - x1) + Math.abs (y0 - y1);
    }
    
    private LinkedList<Block> computePath(int x0, int y0, int x1, int y1) {
        LinkedList<Block> result = new LinkedList<Block>();
        
        HashSet<ANode> closedset = new HashSet<ANode>();
        HashSet<ANode> openset = new HashSet<ANode>();
        
        ANode start = new ANode();
        start.block = world.getBlock(x0, y0);
        start.x = x0;
        start.y = y0;
        start.g_score = 0;
        start.f_score = distM(x0, y0, x1, y1);
        start.h_score = start.g_score + start.f_score;
        
        openset.add(start);
        
        while(!openset.isEmpty()) {
            ANode x = null;
            double f_min = -1;
            
            for(ANode n : openset) {
                if(f_min == -1 || n.f_score < f_min) {
                    x = n;
                    f_min = x.f_score;
                }
            }
            
            if(x.x == x1 && x.y == y1) {
                while(x.x != x0 || x.y != y0) {
                    result.push(x.block);
                    x = x.from;
                }
                break;
            }
            
            openset.remove(x);
            closedset.add(x);
            
            LinkedList<Block> neighbours = world.getNeighbors(x.x, x.y);
            for(Block b : neighbours) {
                ANode y = new ANode();
                y.block = b;
                y.from = x;
                y.x = (int)(b.getX() / Block.WIDTH);
                y.y = (int)(b.getY() / Block.HEIGHT);
                y.g_score = x.g_score + 1;
                y.h_score = distM(y.x, y.y, x1, y1);
                y.f_score = y.g_score + y.h_score;
                
                boolean cont = false;
                
                for(ANode c : closedset) {
                    if(c.equals(y)) {
                        cont = true;
                        break;
                    }
                }
                
                if(cont)
                    continue;
                
                boolean found = false;
                for(ANode c : openset) {
                    if(c.equals(y)) {
                        found = true;
                        if(y.g_score < c.g_score){
                            openset.remove(c);
                            openset.add(y);
                        }
                        break;
                    }
                }
                if(!found)
                    openset.add(y);
            }
        }
        
        
        return result;
    }
    
    class ANode {
        public ANode from;
        public Block block;
        public int x, y;
        public double g_score, h_score, f_score;
        
        public ANode() {
            from = null;
            block = null;
            g_score = h_score = f_score = 0;
            x = y = -1;
        }
        
        boolean equals(ANode n) {
            return block == n.block;
        }
        
        void copy(ANode n) {
            from = n.from;
            block = n.block;
            x = n.x;
            y = n.y;
            g_score = n.g_score;
            h_score = n.h_score;
            f_score = n.f_score;            
        }
        
    }
    
    public Input update(double frametime) {
        Input rtn = new Input();
        
      /*  int x0 = (int)(c.getX() / Block.WIDTH);
        int y0 = (int)(c.getY() / Block.HEIGHT);

        int x1 = (int)(world.getPlayer().getX() / Block.WIDTH);
        int y1 = (int)(world.getPlayer().getY() / Block.HEIGHT);
        
        long now = System.currentTimeMillis();
        if (now > lastUpdate + 1000) {
            path = computePath(x0, y0, x1, y1);
        }
        
        if (!path.isEmpty()) {
            Block first = path.getFirst();

            if (Math.abs(first.getY() - c.getY()) <= c.getSpeedY() * frametime) {
                c.align(c.getX(), first.getY());
            }

            if (first.getX() + 0.5 * Block.WIDTH < c.getX() + 0.5 * c.getWidth()) {
                rtn.Left = true;
            }

            if (first.getX() + 0.5 * Block.WIDTH > c.getX() + 0.5 * c.getWidth()) {
                rtn.Right = true;
            }

            if (first.getY() + 0.5 * Block.HEIGHT < c.getY() + 0.5 * c.getHeight()) {
                rtn.Up = true;
            }

            if (first.getY() + 0.5 * Block.HEIGHT > c.getY() + 0.5 * c.getHeight()) {
                rtn.Down = true;
            }
            
            double distX = c.getCenterX() - first.getCenterX();
            double distY = c.getCenterY() - first.getCenterY();
            
            if(distX * distX + distY * distY <= 64 )
                path.pop();
        }*/
        
        return rtn;
    }
    
    
}
