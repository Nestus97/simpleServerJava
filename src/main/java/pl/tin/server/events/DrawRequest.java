package pl.tin.server.events;

import lombok.Getter;
import pl.tin.server.ScribblePart;

public class DrawRequest implements Request {
    @Getter private ScribblePart scribblePart;
    @Getter private int roomId;

    public DrawRequest(ScribblePart scribblePart, int roomId) {
        this.scribblePart = scribblePart;
        this.roomId = roomId;
    }
}