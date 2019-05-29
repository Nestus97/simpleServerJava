package pl.tin.server.events;

import lombok.Data;

@Data
public class UndoRequest implements Request {
    private int scribblerId;

    public UndoRequest(int scribblerId) {
        this.scribblerId = scribblerId;
    }
}