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

    // a different core adds to the queue than the one on which the queue resides - need to pass a message
    // the messages need to be tracked in the system, too.
    public boolean add(Core whoAdds, T elem) {
        core.getEnv().getTracker().trackMem(whoAdds);
        core.getEnv().getTracker().trackMem(core);

        if (whoAdds != core) {
            Message msg = new Message(elem);
            msg.setTimeSend(whoAdds.getCurrentTime());
            core.getEnv().getTracker().trackSend(whoAdds, msg);
            core.getEnv().getTracker().trackReceive(core, whoAdds, msg);
        }

        return super.add(elem);
    }


}
