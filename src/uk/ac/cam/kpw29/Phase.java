package uk.ac.cam.kpw29;

public class Phase {
    private final int id;
    private ConcurrentQueue<Task> taskQueue;
    public Phase(int id) {
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
