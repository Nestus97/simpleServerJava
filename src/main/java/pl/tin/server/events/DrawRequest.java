package pl.tin.server.events;

import lombok.Getter;
import pl.tin.server.ScribblePart;

public class DrawRequest implements Request {
    @Getter private ScribblePart scribblePart;

    public DrawRequest(ScribblePart scribblePart) {
        this.scribblePart = scribblePart;
    }
}