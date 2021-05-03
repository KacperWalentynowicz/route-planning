package simulator.utils;

import java.util.concurrent.ConcurrentLinkedQueue;

public class GlobalQueue<T> extends ConcurrentLinkedQueue<T> {
    private final Core core;
    public GlobalQueue(Core core) {
        super();
        this.core = core;
    }

    @Override
    public T poll() {
        core.getEnv().getTracker().trackMem(core);
        return super.poll();
    }

    @Override
    public boolean add(T elem) {
        core.getEnv().getTracker().trackMem(core);
        return super.add(elem);
    }

    // a different core adds to the queue than the one on which the queue resides
    public boolean add(Core whoAdds, T elem) {
        core.getEnv().getTracker().trackMem(whoAdds);
        core.getEnv().getTracker().trackMem(core);
        return super.add(elem);
    }


}
