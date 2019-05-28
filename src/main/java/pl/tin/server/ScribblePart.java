package pl.tin.server;

import lombok.Data;

import java.util.List;

@Data
public class ScribblePart {
    private int scribberId;
    private List<Pixel> pixels;
    private boolean isEnd;

    public ScribblePart(int scribberId, List<Pixel> pixels, boolean isEnd) {
        this.scribberId = scribberId;
        this.pixels = pixels;
        this.isEnd = isEnd;
    }
}