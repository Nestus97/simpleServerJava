package pl.tin.server.events;

import lombok.Getter;

public class CreateRoomRequest implements Request {
    @Getter private int clientId;
    @Getter private String roomName;

    public CreateRoomRequest(int clientId, String roomName) {
        this.clientId = clientId;
        this.roomName = roomName;
    }
}
