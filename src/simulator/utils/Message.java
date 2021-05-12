package simulator.utils;

import graphs.Matrix;

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
        int res = 0;
        for (Object o : contents) {
            if (o instanceof Matrix) {
                res += ((Matrix) o).n_elements();
            }
            else {
                res += 1;
            }
        }

        return res;
    }

    public ArrayList<Object> getContents() {
        return contents;
    }

    public void setTimeSend(float f) {
        timeSend = f;
    }

    public float getTimeSend(){ return timeSend; }
}
