package simulator.utils;

import simulator.internal.*;

public class Phase {
    private final int id;
    private ConcurrentQueue<Task> taskQueue;
    public final String name;
    public Phase(String name, int id) {
        this.name = name;
        this.id = id;
        taskQueue = new ConcurrentQueueImpl<>();
    }

    public int getID() {
        return this.id;
    }

    public void addTask(Task t) {
        taskQueue.put(t);
    }

    public Task getTask() {
        return taskQueue.take();
    }
}
