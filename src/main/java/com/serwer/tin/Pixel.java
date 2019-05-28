package com.serwer.tin;

public class Pixel {
    private int x;
    private int y;
    private byte r;
    private byte g;
    private byte b;

    public Pixel(int x, int y, byte r, byte g, byte b) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public byte getR() {
        return r;
    }

    public byte getG() {
        return g;
    }

    public byte getB() {
        return b;
    }
}
