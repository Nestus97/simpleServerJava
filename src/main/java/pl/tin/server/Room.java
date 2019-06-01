package pl.tin.server;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class Room {
    @Getter private int roomId;
    @Getter private String roomName;
    @Getter public List<Scribble> scribblesHistory = new LinkedList<>();

    public Room(int roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
    }


}
