package uk.ac.cam.kpw29;

import java.util.ArrayList;
import java.util.List;

public class Message {
    private List<Object> contents;

    public Message() {
        contents = new ArrayList<>();
    }

    public void addObject(Object o) {
        contents.add(o);
    }

    public int getSize() {
        return contents.size();
    }

    public List<Object> getContents() {
        return contents;
    }
}
