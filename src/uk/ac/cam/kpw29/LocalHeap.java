package uk.ac.cam.kpw29;

public class LocalHeap<T> extends FibonacciHeap<T> {
    private final Core core;

    public LocalHeap(Core core) {
        super();
        this.core = core;
    }

    @Override
    public Entry<T> enqueue(T value, double priority) {
        core.getTracker().trackPQOperation(core);
        return super.enqueue(value, priority);
    }

    @Override
    public Entry<T> dequeueMin() {
        core.getTracker().trackPQOperation(core);
        return super.dequeueMin();
    }

    @Override
    public void decreaseKey(Entry<T> entry, double newPriority) {
        core.getTracker().trackPQOperation(core);
        super.decreaseKey(entry, newPriority);
    }
}
