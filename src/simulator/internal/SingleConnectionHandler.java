package simulator.internal;

import simulator.utils.Message;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SingleConnectionHandler {
    ConcurrentLinkedQueue <Message> msgs;
    public SingleConnectionHandler() {
        msgs = new ConcurrentLinkedQueue<>();
    }

    void addMessage(Message m) {
        msgs.add(m);
    }

    Message getOneMessage() {
        return msgs.poll();
    }
}
