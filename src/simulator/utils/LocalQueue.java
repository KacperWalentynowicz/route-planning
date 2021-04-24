package simulator.utils;

import java.util.LinkedList;

public class LocalQueue<T> extends LinkedList<T> {
    private final Core core;
    public LocalQueue(Core core) {
        super();
        this.core = core;
    }

    @Override
    public T poll() {
        core.getTracker().trackMem(core);
        return super.poll();
    }

    @Override
    public boolean add(T elem) {
        core.getTracker().trackMem(core);
        return super.add(elem);
    }
}
