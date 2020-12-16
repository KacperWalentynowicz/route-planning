package uk.ac.cam.kpw29;

import javafx.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CommunicationHandler {
    ConcurrentMap <Pair<Core, Core>, SingleConnectionHandler> connections;

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
        initMap(proc);
    }

    void sendMessage(Core from, Core to, Message m) {
        connections.get(new Pair<>(from, to)).addMessage(m);
    }

    Message receiveMessage(Core from, Core to) {
        return connections.get(new Pair<>(from, to)).getOneMessage();
    }
}
