package uk.ac.cam.kpw29;

public abstract class Task {

    /**
     *
     *  @return boolean
     */
    public boolean wantsRepeat() {
        return false;
    }

    public abstract void execute();
}
