package simulator.utils;

public abstract class Task {

    private final Core core;
    /**
     *
     *  @return boolean
     */

    public Task(Core core) {
        this.core = core;
    }

    public boolean wantsRepeat() {
        return false;
    }

    public abstract void execute();
}
