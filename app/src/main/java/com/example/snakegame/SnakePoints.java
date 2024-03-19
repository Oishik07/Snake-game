package com.example.snakegame;

public class SnakePoints {

    int positionX,positionY;

    public SnakePoints(int positionX, int PositionY) {
        this.positionX = positionX;
        this.positionY = PositionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
}
