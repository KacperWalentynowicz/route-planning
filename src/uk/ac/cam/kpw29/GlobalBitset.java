package uk.ac.cam.kpw29;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class GlobalBitset extends AtomicReferenceArray<Boolean> {
    private final AtomicInteger sumOfEntries;

    public GlobalBitset(int N) {
        super(N);
        sumOfEntries = new AtomicInteger(0);
        for (int i=0; i<N; ++i) {
            super.set(i, false);
        }
    }

    public Boolean get(Core core, int i) {
        core.getTracker().trackSharedMem(core);
        return super.get(i);
    }

    public void set(Core core, int i, Boolean b) {
        Boolean prev = super.get(i);
        core.getTracker().trackSharedMem(core);
        if (prev != b) {
            synchronized (sumOfEntries) {
                super.set(i, b);
                if (b) {
                    sumOfEntries.getAndIncrement();
                }
                else {
                    sumOfEntries.getAndDecrement();
                }
            }
        }


    }
}
