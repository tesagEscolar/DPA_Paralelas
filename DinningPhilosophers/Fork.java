public class Fork {
    private boolean isUsed;

    public synchronized void take() throws InterruptedException {
        while (isUsed) {
            wait();
        }
        isUsed = true;
    }

    public synchronized void putDown() {
        isUsed = false;
        notifyAll(); // Notify all waiting threads to prevent starvation
    }
}
