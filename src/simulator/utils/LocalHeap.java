package simulator.utils;

public class LocalHeap<T> extends FibonacciHeap<T> {
    private final Core core;

    public LocalHeap(Core core) {
        super();
        this.core = core;
    }

    @Override
    public Entry<T> enqueue(T value, double priority) {
        core.getEnv().getTracker().trackPQOperation(core);
        return super.enqueue(value, priority);
    }

    @Override
    public Entry<T> dequeueMin() {
        core.getEnv().getTracker().trackPQOperation(core);
        return super.dequeueMin();
    }

    @Override
    public void decreaseKey(Entry<T> entry, double newPriority) {
        core.getEnv().getTracker().trackPQOperation(core);
        super.decreaseKey(entry, newPriority);
    }
}
