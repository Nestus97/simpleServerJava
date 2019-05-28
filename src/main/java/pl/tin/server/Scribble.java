package pl.tin.server;

import lombok.Data;

import java.util.List;

public class Scribble extends ScribblePart {

    public Scribble(int scribblerId, List<Pixel> pixels, boolean isCompleted) {
        super(scribblerId, pixels, isCompleted);
    }

    public Scribble(ScribblePart scribblePart) {
        super(scribblePart.getScribblerId(), scribblePart.getPixels(), scribblePart.isEnd());
    }

    public void addPixels(List<Pixel> pixels) {
        getPixels().addAll(pixels);
    }

    public boolean isCompleted() {
        return super.isEnd();
    }

    public void setCompleted(boolean value) {
        super.setEnd(value);
    }
}