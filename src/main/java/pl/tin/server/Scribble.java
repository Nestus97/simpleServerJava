package pl.tin.server;

import lombok.Data;

import java.util.List;

@Data
public class Scribble {
    private int scribberId;
    private List<Pixel> pixels;
    private boolean isCompleted;

    public Scribble(int scribberId, List<Pixel> pixels, boolean isCompleted) {
        this.scribberId = scribberId;
        this.pixels = pixels;
        this.isCompleted = isCompleted;
    }
}