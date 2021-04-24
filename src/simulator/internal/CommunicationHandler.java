package simulator.internal;

import javafx.util.Pair;

import simulator.utils.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommunicationHandler {
    ConcurrentMap <Pair<Core, Core>, SingleConnectionHandler> connections;
    ProcessorArchitecture proc;

    private void initMap(ProcessorArchitecture proc) {
        connections = new ConcurrentHashMap<>();
        List<Core> cores = proc.getCores();
        for (Core c1 : cores) {
            for (Core c2 : cores) {
                connections.put(new Pair<>(c1,c2), new SingleConnectionHandler());
            }
        }
    }
    public CommunicationHandler(ProcessorArchitecture proc) {
        this.proc = proc;
        initMap(proc);
    }

    public float getDistance(Core from, Core to) {
        return proc.getDistance(from, to);
    }

    public void sendMessage(Core from, Core to, Message m) {
        connections.get(new Pair<>(from, to)).addMessage(m);
    }

    public Message receiveMessage(Core from, Core to) {
        return connections.get(new Pair<>(from, to)).getOneMessage();
    }
}
