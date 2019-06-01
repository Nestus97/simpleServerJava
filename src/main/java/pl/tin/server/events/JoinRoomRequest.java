package pl.tin.server.events;

import lombok.Getter;

public class JoinRoomRequest implements Request {
    @Getter private int clientId;
    @Getter private int roomId;

    public JoinRoomRequest(int clientId, int roomId) {
        this.clientId = clientId;
        this.roomId = roomId;
    }
}
