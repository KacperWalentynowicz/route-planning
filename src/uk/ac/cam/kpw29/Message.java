package uk.ac.cam.kpw29;

import java.util.ArrayList;
import java.util.List;

public class Message {
    private ArrayList<Object> contents;
    private float timeSend;

    public Message() {
        contents = new ArrayList<>();
        timeSend = 0.0f;
    }

    public Message(Object o) {
        contents = new ArrayList<>();
        contents.add(o);
        timeSend = 0.0f;
    }

    public void addObject(Object o) {
        contents.add(o);
    }

    public int getSize() {
        return contents.size();
    }

    public ArrayList<Object> getContents() {
        return contents;
    }

    public void setTimeSend(float f) {
        timeSend = f;
    }

    public float getTimeSend(){ return timeSend; }
}
