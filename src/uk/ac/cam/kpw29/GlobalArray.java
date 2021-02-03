package uk.ac.cam.kpw29;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.UnaryOperator;
import java.lang.Float;

import static java.lang.Float.min;

public class GlobalArray extends AtomicReferenceArray<Float> {
    public GlobalArray(int N) {
        super(N);
        for (int i=0; i<N; ++i) {
            super.set(i, (Float)1e9f);
        }
    }

    public Float get(Core core, int i) {
        core.getTracker().trackSharedMem(core);
        return super.get(i);
    }

    public void minimize(Core core, int i, Float value) {
        core.getTracker().trackSharedMem(core);
        super.getAndUpdate(i, aFloat -> ((float)value <= (float)aFloat) ? value : aFloat);
    }

    public void maximize(Core core, int i, Float value) {
        core.getTracker().trackSharedMem(core);
        super.getAndUpdate(i, aFloat -> ((float)value >= (float)aFloat) ? value : aFloat);
    }

}
