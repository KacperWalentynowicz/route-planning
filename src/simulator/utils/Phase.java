package simulator.utils;

import simulator.internal.*;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Phase {
    private final int id;
    private ConcurrentLinkedQueue<Task> taskQueue;
    public final String name;
    public Phase(String name, int id) {
        this.name = name;
        this.id = id;
        taskQueue = new ConcurrentLinkedQueue<>();
    }

    public int getID() {
        return this.id;
    }

    public void addTask(Task t) {
        taskQueue.add(t);
    }

    public Task getTask() {
        return taskQueue.poll();
    }
}
