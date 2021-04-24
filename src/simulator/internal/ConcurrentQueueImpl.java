package simulator.internal;

public class ConcurrentQueueImpl<T> implements ConcurrentQueue<T> {
    private static class Link<L> {
        L val;
        Link<L> next;

        Link(L val) {
            this.val = val;
            this.next = null;
        }
    }

    private Link<T> first = null;
    private Link<T> last = null;

    public synchronized void put(T val) {
        Link<T> elem = new Link<T>(val);
        if (first == null) {
            first = elem;
        }

        if (last == null) {
            last = elem;
        }
        else {
            last.next = elem;
            last = elem;
        }
    }

    public synchronized T take() {
        if (first == null) {
            return null;
        }

        T val = first.val;
        first = first.next;
        return val;
    }
}