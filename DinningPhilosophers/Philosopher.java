import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Philosopher implements Runnable {
    private final int id;
    private Fork leftFork;
    private Fork rightFork;
    private volatile boolean running = true;
    private final Random random = new Random();
    private final VisualManager visualManager;
    private static final Lock lock = new ReentrantLock();
    private static final Condition canEat = lock.newCondition();
    private static boolean[] eatingStates;

    public Philosopher(int id, Fork leftFork, Fork rightFork, VisualManager visualManager, int philSize) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.visualManager = visualManager;
        eatingStates = new boolean[philSize];
    }

    public void stop() {
        running = false;
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        try {
            while (running) {
                think();
                visualManager.updateState(id, "HUNGRY");
                pickUpForks();
                eat();
                putDownForks();
            }
        } catch (InterruptedException e) {
            releaseForksIfEating();
            Thread.currentThread().interrupt();
        }
    }

    private void think() throws InterruptedException {
        visualManager.updateState(id, "THINKING");
        Thread.sleep(random.nextInt(3000) + 2000);
    }

    private void pickUpForks() throws InterruptedException {
        lock.lock();
        try {
            waitForForks();
            if (id % 2 == 0) {
                takeForks(leftFork, rightFork, "LIFTING_LEFT_FORK", "LIFTING_RIGHT_FORK");
            } else {
                takeForks(rightFork, leftFork, "LIFTING_RIGHT_FORK", "LIFTING_LEFT_FORK");
            }
            eatingStates[id] = true;
        } finally {
            lock.unlock();
        }
    }

    private void waitForForks() throws InterruptedException {
        while (eatingStates[id] || eatingStates[(id + 1) % eatingStates.length] || eatingStates[(id - 1 + eatingStates.length) % eatingStates.length]) {
            canEat.await(); // Wait until both forks are available and neither adjacent philosopher is eating
        }
    }

    private void takeForks(Fork firstFork, Fork secondFork, String firstState, String secondState) throws InterruptedException {
        firstFork.take();
        visualManager.updateState(id, firstState);
        Thread.sleep(1000);
        secondFork.take();
        visualManager.updateState(id, secondState);
        Thread.sleep(1000);
    }

    private void eat() throws InterruptedException {
        visualManager.updateState(id, "EATING");
        Thread.sleep(random.nextInt(3000) + 2000);
    }

    public void putDownForks() throws InterruptedException {
        lock.lock();
        try {
            putDownFork(leftFork, "PUTTING_DOWN_LEFT_FORK");
            putDownFork(rightFork, "PUTTING_DOWN_RIGHT_FORK");
            eatingStates[id] = false;
            canEat.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private void putDownFork(Fork fork, String state) throws InterruptedException {
        fork.putDown();
        visualManager.updateState(id, state);
        Thread.sleep(1000);
    }

    private void releaseForksIfEating() {
        if (eatingStates[id]) {
            releaseForks();
        }
    }

    public void releaseForks() {
        lock.lock();
        try {
            rightFork.putDown();
            leftFork.putDown();
            eatingStates[id] = false;
            canEat.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static void updateEatingStatesSize(int newSize) {
        lock.lock();
        try {
            boolean[] newEatingStates = new boolean[newSize];
            System.arraycopy(eatingStates, 0, newEatingStates, 0, Math.min(eatingStates.length, newSize));
            eatingStates = newEatingStates;
        } finally {
            lock.unlock();
        }
    }
}
