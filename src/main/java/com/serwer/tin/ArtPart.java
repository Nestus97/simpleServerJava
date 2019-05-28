package com.serwer.tin;
import com.serwer.tin.Pixel;

import java.util.Vector;

public class ArtPart {
    private boolean isEnd;
    private Vector<Pixel> pixels = new Vector<>();
    private int userID;
    // CONSTRUCTORS
    public ArtPart(int id, boolean isEnd){
        this.userID = id;
        this.isEnd = isEnd;
    }
    public ArtPart(){
        this.userID = 0;
        this.isEnd = false;
    }
    public ArtPart(boolean isEnd){
        this.userID = 0;
        this.isEnd = isEnd;
    }
    //METHODS
    public void addPixel(Pixel p){
        pixels.add(p);
    }
    public Vector<Pixel> getPixels() {
        return pixels;
    }

    public int getUserID() {
        return userID;
    }

    public boolean isEnd() {
        return isEnd;
    }
}
