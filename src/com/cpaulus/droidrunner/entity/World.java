/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cpaulus.droidrunner.entity;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author cyril
 */
public class World {

    public enum WorldState {

        NOTHING, WIN, DEAD
    };
    private int width;
    private int height;
    private Resources r;
    private Camera camera;
    private Player player;
    private LinkedList<Enemy> enemies;
    private LinkedList<Goal> goals;
    private int goalCount;
    private boolean completed = false;
    private boolean isGod = true;
    Block[] blocks;

    public World(Resources r, Camera c) {
        this.r = r;
        blocks = new Block[0];
        camera = c;
        player = new Player(this, r);
        enemies = new LinkedList<Enemy>();
        goals = new LinkedList<Goal>();
        goalCount = 0;
        c.track(player);

    }

    public void clear() {
        goalCount = 0;
        completed = false;
        enemies.clear();
        goals.clear();
    }

    public void draw(Canvas c) {

        if (camera.getPlainScreen()) {
            for (Block b : blocks) {
                b.draw(c);
            }
        } else {
            for (int j = (int) camera.getY() / Block.HEIGHT; j <= (int) (camera.getY() + camera.getHeight()) / Block.HEIGHT; j++) {
                for (int i = (int) camera.getX() / Block.WIDTH; i <= (int) (camera.getX() + camera.getWidth()) / Block.WIDTH; i++) {
                    if (i >= 0 && i < getWidth() && j >= 0 && j < getHeight()) {
                        blocks[j * getWidth() + i].draw(c);
                    }
                }
            }
        }

        for (Enemy e : enemies) {
            e.draw(c);
        }

        for (Goal g : goals) {
            g.draw(c);
        }

        player.draw(c);
    }

    public WorldState update(double frametime, Input input) {
        player.update(frametime, input);
        
        for (Enemy e : enemies) {
            e.update(frametime);
            if (e.intersects(player) && !isGod) {
                return WorldState.DEAD;
            }
        }
        
        for (Block b : blocks) {
            b.update(frametime);            
        }

        for (Goal g : goals) {
            if (g.isActive() && g.intersects(player)) {
                g.setActive(false);
                goalCount--;
            }
        }

        if (!completed && goalCount == 0) {
            completed = true;
            revealLadder();
        }

        if (completed && player.getY() == 30) {
            return WorldState.WIN;
        }
      
        return WorldState.NOTHING;
    }

    public void addBlock(int x, int y, int blockCode) {
        Block b;

        if (blockCode < 0 || blockCode > 6) {
            b = Block.createBlock(Block.BlockType.EMPTY.ordinal(), this, r);
        } else {
            b = Block.createBlock(blockCode, this, r);
        }
        b.setX(x * Block.WIDTH);
        b.setY(y * Block.HEIGHT);

        blocks[y * getWidth() + x] = b;

    }

    public void loadFromMap(InputStream sr) {
        clear();
        Scanner sc = new Scanner(sr);
        width = Integer.parseInt(sc.next()) + 2;

        height = Integer.parseInt(sc.next()) + 2;

        blocks = new Block[getWidth() * getHeight()];

        sc.nextLine();
        for (int i = 0; i < getWidth(); i++) {
            addBlock(i, 0, Block.BlockType.CEMENT.ordinal());
        }

        for (int j = 1; j < getHeight() - 1; j++) {
            addBlock(0, j, Block.BlockType.CEMENT.ordinal());

            for (int i = 1; i < getWidth() - 1; i++) {
                int value = Integer.parseInt(sc.next());
                addBlock(i, j, value);

                if (value == 9) {
                    player.setX(i * Block.WIDTH);
                    player.setY(j * Block.HEIGHT);
                    System.out.println("Position :" + i * Block.WIDTH + " " + j * Block.HEIGHT);
                } else if (value == 8) {
                    addEnemy(i, j);
                } else if (value == 7) {
                    addGoal(i, j);
                }

            }
            addBlock(getWidth() - 1, j, Block.BlockType.CEMENT.ordinal());
            sc.nextLine();
        }

        for (int i = 0; i < getWidth(); i++) {
            addBlock(i, getHeight() - 1, Block.BlockType.CEMENT.ordinal());
        }

        sc.close();
        goalCount = goals.size();
    }

    public Block getCollidingSolid(Entity ent) {
        for (int j = (int) (ent.getTop() / Block.HEIGHT) - 1; j <= (ent.getTop() / Block.HEIGHT) + 1; j++) {
            for (int i = (int) (ent.getLeft() / Block.WIDTH) - 1; i <= (ent.getLeft() / Block.WIDTH) + 1; i++) {
                Block candidate = getBlock(i, j);
                if (candidate != null && candidate.intersects(ent) && candidate.isSolid()) {
                    return candidate;
                }
            }
        }

        return null;
    }

    public Block getCollidingLadder(Entity ent) {

        double min = -1;
        Block minB = null;
        for (int j = (int) (ent.getTop() / Block.HEIGHT) - 1; j <= (ent.getTop() / Block.HEIGHT) + 1; j++) {
            for (int i = (int) (ent.getLeft() / Block.WIDTH) - 1; i <= (ent.getLeft() / Block.WIDTH) + 1; i++) {

                Block candidate = getBlock(i, j);
                if (candidate != null && candidate.intersects(ent) && candidate.isLadder()) {
                    double diffX = Math.pow(candidate.getCenterX() - ent.getLeft() + 0.5 * ent.getWidth(), 2);
                    double diffY = Math.pow(candidate.getCenterY() - ent.getTop() + 0.5 * ent.getHeight(), 2);
                    double dist = diffX + diffY;
                    if (min == -1 || dist < min) {
                        min = dist;
                        minB = candidate;
                    }
                }
            }
        }
        return minB;
    }

    public Block getCollidingRope(Entity ent) {

        for (int j = (int) (ent.getTop() / Block.HEIGHT) - 1; j <= (ent.getTop() / Block.HEIGHT) + 1; j++) {
            for (int i = (int) (ent.getLeft() / Block.WIDTH) - 1; i <= (ent.getLeft() / Block.WIDTH) + 1; i++) {
                Block candidate = getBlock(i, j);
                if (candidate != null && candidate.intersects(ent) && candidate.isRope()) {
                    return candidate;
                }
            }
        }
        return null;
    }

    public Block getBlock(int x, int y) {
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()) {
            return blocks[y * getWidth() + x];
        } else {
            return null;
        }
    }

    public void Bundle(Bundle map) {
        map.putDouble("PLAYERX", player.getX());
        map.putDouble("PLAYERY", player.getY());

        int i = 0;
        for (Enemy e : enemies) {
            map.putDouble("ENEMY" + i + "X", e.getX());
            map.putDouble("ENEMY" + i + "Y", e.getY());
            i++;
        }

        map.putBoolean("COMPLETED", completed);
        map.putInt("GOALCOUNT", goalCount);

        i = 0;
        for (Goal g : goals) {
            map.putBoolean("GOAL" + i, g.isActive());
            i++;
        }

    }

    public void unBundle(Bundle map) {
        player.setX(map.getDouble("PLAYERX"));
        player.setY(map.getDouble("PLAYERY"));

        int i = 0;
        for (Enemy e : enemies) {
            e.setX(map.getDouble("ENEMY" + i + "X"));
            e.setY(map.getDouble("ENEMY" + i + "Y"));
            i++;
        }

        completed = map.getBoolean("COMPLETED");
        if (completed) {
            revealLadder();
        }

        goalCount = map.getInt("GOALCOUNT");

        i = 0;
        for (Goal g : goals) {
            g.setActive(map.getBoolean("GOAL" + i));
            i++;
        }
    }

    public void addEnemy(int i, int j) {
        Enemy e = new Enemy(this, r);
        e.setX(i * Block.WIDTH);
        e.setY(j * Block.HEIGHT);
        enemies.add(e);
    }

    public void addGoal(int i, int j) {
        Goal g = new Goal(this, r);
        g.setX(i * Block.WIDTH);
        g.setY(j * Block.HEIGHT);
        goals.add(g);
    }

    public LinkedList<Block> getNeighbors(int x, int y) {
        LinkedList<Block> value = new LinkedList<Block>();

        Block current = getBlock(x, y);
        boolean isSolidUnderCurrent = false;
        if (y + 1 < getHeight()) {
            isSolidUnderCurrent = getBlock(x, y + 1).isAiSolid() || getBlock(x, y + 1).isLadder();
        }

        int xs[] = {x, x - 1, x + 1, x};
        int ys[] = {y - 1, y, y, y + 1};


        for (int i = 0; i < 4; i++) {

            if (xs[i] < 0 || xs[i] >= getWidth() || ys[i] < 0 || ys[i] >= getHeight()) {
                continue;
            }

            Block candidate = getBlock(xs[i], ys[i]);

            if ((candidate.isLadder() || candidate.isRope()) && (i == 1 || i == 2)) {
                value.add(candidate);
            } else if (!candidate.isAiSolid() && (i == 1 || i == 2)
                    && (current.isLadder() || current.isRope() || isSolidUnderCurrent)) {
                value.add(candidate);
            } else if (!candidate.isAiSolid() && i == 3) {
                value.add(candidate);
            } else if (!(candidate.isAiSolid()) && i == 0 && current.isLadder()) {
                value.add(candidate);
            }
        }
        return value;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getCollidingEnemy(Entity ent) {
        for (Enemy e : enemies) {
            if (e.intersects(ent)) {
                return e;
            }
        }
        return null;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the width
     */
    public int getVisibleWidth() {
        return width * Block.WIDTH;
    }

    /**
     * @return the height
     */
    public int getVisibleHeight() {
        return height * Block.HEIGHT;
    }

    private void revealLadder() {
        for (Block b : blocks) {
            if (b.getType() == Block.BlockType.ENDLADDER) {
                ((EndLadder) b).setActive(true);
            }
        }
    }
    
    public Goal getNearestGoals(double x, double y) {
        Goal min = null;
        double distMin = -1;
        for(Goal g : goals) {
            double dist = Math.pow(g.getX() - x, 2) + Math.pow(g.getY() - y, 2);
            if((dist < distMin || distMin == -1) && g.isActive()) {
                distMin = dist;
                min = g;
            }
        }
        return min;       
        
    }
    
    public int getGoalLeft() {
        return goalCount;
    }
    
    public boolean enemyOccupied(int x, int y) {
        for(Enemy e: enemies)
            if((int)(e.getCenterX() / Block.WIDTH) == x 
                    && (int)(e.getCenterY() / Block.HEIGHT) == y)
                return true;            
        return false;
    }
}
