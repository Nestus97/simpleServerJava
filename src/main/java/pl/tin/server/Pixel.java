package pl.tin.server;

import lombok.Data;

@Data
public class Pixel {
    private int x;
    private int y;
    private int r;
    private int g;
    private int b;

    public Pixel(int x, int y, int r, int g, int b) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
