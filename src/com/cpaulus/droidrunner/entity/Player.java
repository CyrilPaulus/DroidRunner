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
public class Player extends Entity{
    
    protected double speedX = 250;
    protected double speedY = 150;
    
    boolean canFall;
    boolean isFalling;
    boolean isClimbing;
    boolean isHanging;
    boolean canMove;

    public Player(World w, Resources res) {
        super(w);
        isFalling = false;
        canFall = true;
        canMove = true;
        isHanging = false;
        isClimbing = false;
        width = 24;
        height = 30;
        setImage(res.getDrawable(R.drawable.you));
    }
        
    public void alignToGridX() {
        setX((int) (getCenterX() / Block.WIDTH) * Block.WIDTH + (Block.WIDTH - width) / 2);
    }

    public void alignToGridY() {
        setY((int) (getCenterY() / Block.HEIGHT) * Block.HEIGHT);
    }
    
    public void align(double x, double y) {
        setX(x);        
        if (!isFalling)
            setY(y);   
    }
    
    public void update(double frametime, Input input) {

        if (!canMove) {
            return;
        }

        /*
         * int x0 = (int) (x / Block.WIDTH); int y0 = (int) (y / Block.HEIGHT);
         *
         * //Carve
         *
         * if (input.Car && !lastInput.LeftCarve) { Block* b =
         * world->getBlock(x0 - 1, y0 + 1); Block* c = world->getBlock(x0 - 1,
         * y0); if (b && b->getType() == Block::WALL && !c->isSolid() &&
         * !c->isLadder() && !c->isRope()) b->setActive(false); }
         *
         * if (input.RightCarve && !lastInput.RightCarve) { Block* b =
         * world->getBlock(x0 + 1, y0 + 1); Block* c = world->getBlock(x0 + 1,
         * y0); if (b && b->getType() == Block::WALL && !c->isSolid() &&
         * !c->isLadder() && !c->isRope()) b->setActive(false); } lastInput = input;
         */
        int x0 = (int) (x / Block.WIDTH); 
        int y0 = (int) (y / Block.HEIGHT);
        
        if(input.CarveLeft) {
            world.getBlock(x0 - 1, y0 + 1).carve();
        }
        
        if(input.CarveRight) {
            world.getBlock(x0 + 1, y0 + 1).carve();
        }

        double directionX = 0;
        double directionY = 0;
        Block rope = world.getCollidingRope(this);
        Block ladder = world.getCollidingLadder(this);

        boolean isCentring = false;
        if (ladder != null) {

            isClimbing = true;

            if (input.Up) {
                directionY -= getSpeedY();
            } else if (input.Down) {
                directionY += getSpeedY();
            }

            if (input.Up || input.Down) {
                if (Math.abs(getCenterX() - ladder.getCenterX()) < getSpeedX() * frametime) {
                    alignToGridX();
                } else if (getCenterX() < ladder.getCenterX()) {
                    directionX += getSpeedX();
                } else if (getCenterX() > ladder.getCenterX()) {
                    directionX -= getSpeedX();
                }

                isCentring = true;
            }

        } else {
            isClimbing = false;
        }

        //Align to rope
        if (rope != null && (input.Left || input.Right || isFalling) && !(input.Down)) {
            isHanging = true;

            double deltaY = rope.getY() - getY();
            if (Math.abs(deltaY) < 4) {
                setY(rope.getY());
            }
        }

        if (rope == null || input.Down || rope.getY() != getY()) {
            isHanging = false;
        }

        if (isClimbing && isHanging) {
            isClimbing = false;
        }

        if (!isHanging && !isClimbing) {
            isFalling = true;
        } else {
            isFalling = false;
        }

        if (isFalling) {
            directionY += getSpeedY();
        }

        if (directionY != 0) {
            setY(getY() + directionY * frametime);

            Block b = world.getCollidingSolid(this);
            while (b != null) {

                if (directionY < 0) {
                    setY(b.getBottom());
                } else {
                    setY(b.getTop() - height);
                    isFalling = false;
                }
                b = world.getCollidingSolid(this);
            }

            //Walk on ladder
            b = world.getCollidingLadder(this);
            if (b != null && b.getY() > getTop() && isFalling && !input.Down) {
                setY(b.getTop() - height);
                isFalling = false;
            }
        }

        //Left Right - Up Down (ladder)
        if (!isFalling && !isCentring) {
            if (input.Left) {
                directionX -= getSpeedX();
            }

            if (input.Right) {
                directionX += getSpeedX();
            }
        }

        //Update X pos, and solve collisions
        if (directionX != 0) {
            setX(getX() + directionX * frametime);
    
            Player p = world.getCollidingEnemy(this);
            if (p != null && p != this && p != world.getPlayer() && this != world.getPlayer()) {
                if (p.getX() < getX()) {
                    setX(getX() + speedX / 2 * frametime);
                } else {
                    setX(getX() - speedX / 2 * frametime);
                }
            }
             

            Block b = world.getCollidingSolid(this);
            if (b != null) {
                if (directionX < 0) {
                    setX(b.getX() + b.getWidth());
                } else {
                    setX(b.getLeft() - width);
                }
            }
        }

        if (directionX == 0) {
            alignToGridX();
        }

        if (directionY == 0) {
            alignToGridY();
        }

    }

    /**
     * @return the speedX
     */
    public double getSpeedX() {
        return speedX;
    }

    /**
     * @return the speedY
     */
    public double getSpeedY() {
        return speedY;
    }



}
