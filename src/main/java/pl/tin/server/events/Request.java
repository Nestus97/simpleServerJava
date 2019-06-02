package pl.tin.server.events;

public interface Request {
    int LOGIN_USER = 0;
    int DRAW_REQUEST = 1;
    int UNDO_REQUEST = 2;
    int CREATE_ROOM_REQUEST = 3;
    int JOIN_ROOM_REQUEST = 4;
}