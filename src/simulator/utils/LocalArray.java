package simulator.utils;

import java.util.ArrayList;

public class LocalArray<E> extends ArrayList<E> {
    private final Core core;

    public LocalArray(Core core) {
        super();
        this.core = core;
    }

    public LocalArray(Core core, ArrayList<E> copyFrom) {
        super(copyFrom);
        this.core = core;
    }

    @Override
    public E get(int index) {
        core.getTracker().trackMem(core);
        return super.get(index);
    }

    @Override
    public E set(int index, E element) {
        core.getTracker().trackMem(core);
        return super.set(index, element);
    }
}
