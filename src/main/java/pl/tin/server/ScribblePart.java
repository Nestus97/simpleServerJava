package pl.tin.server;

import lombok.Data;

import java.util.List;

@Data
public class ScribblePart {
    private int scribblerId;
    private List<Pixel> pixels;
    private boolean isEnd;

    public ScribblePart(int scribblerId, List<Pixel> pixels, boolean isEnd) {
        this.scribblerId = scribblerId;
        this.pixels = pixels;
        this.isEnd = isEnd;
    }
}