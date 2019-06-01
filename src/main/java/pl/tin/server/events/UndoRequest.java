package pl.tin.server.events;

import lombok.Data;

@Data
public class UndoRequest implements Request {
    private int scribblerId;
    private int roomId;

    public UndoRequest(int scribblerId, int roomId) {
        this.scribblerId = scribblerId;
        this.roomId = roomId;
    }
}