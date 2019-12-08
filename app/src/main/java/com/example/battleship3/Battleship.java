package com.example.battleship3;

public class Battleship {
    private int size;
    private int health;
    private int[] cells;
    private String team;
    private boolean alive;
    private boolean hit;
    private int id;

    public Battleship(int size, int id, String team) {
        this.size = size;
        this.health = size;
        this.id = id;
        this.team = team;
        this.alive = true;
        this.hit = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setCells(int[] cells) {
        this.cells = cells;
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
    public void attacked() {
        hit = true;
        health--;
        if (health <= 0) {
            alive = false;
        }
    }
}
