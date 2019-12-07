package com.example.battleship3;

public class Battleship {
    private int size;
    private int health;
    private int[] cells;
    private String team;
    private boolean alive;
    private boolean hit;

    public Battleship(int size, int[] cells, String team) {
        this.size = size;
        this.health = size;
        this.cells = cells;
        this.team = team;
        this.alive = true;
        this.hit = false;
    }

    public int getSize() {
        return size;
    }

    public int getHealth() {
        return health;
    }

    public int[] getCells() {
        return cells;
    }

    public String getTeam() {
        return team;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
    public boolean contains(int cellIndex) {
        for (int cell : cells) {
            if (cell == cellIndex) {
                return true;
            }
        }
        return false;
    }
    public int attacked() { //RETURNS REMAINING HEALTH
        hit = true;
        health--;
        if (health <= 0) {
            alive = false;
        }
        return health;
    }
}
