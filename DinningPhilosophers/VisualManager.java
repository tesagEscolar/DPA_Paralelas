import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VisualManager extends JPanel {
    private final List<PhilosopherState> philosopherStates = new ArrayList<>();
    private final List<Fork> forks = new ArrayList<>();
    private final List<Philosopher> philosophers = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();
    private final JTextArea logArea;
    private int philosopherCount = 2;
    private final int tableRadius = 150; // Radius of the circular table

    public VisualManager(JTextArea logArea) {
        this.logArea = logArea;
        initializePhilosophers(philosopherCount);
    }

    private void initializePhilosophers(int count) {
        forks.clear();
        philosopherStates.clear();
        philosophers.clear();
        threads.clear();

        for(int i = 0; i < count; i++){
            Fork fork = new Fork();
            forks.add(fork);
        }
        for (int i = 0; i < count; i++) {
            Philosopher philosopher = new Philosopher(i, forks.get(i), forks.get((i + 1) % count), this, count);
            philosopherStates.add(new PhilosopherState(i, "THINKING"));
            philosophers.add(philosopher);
            threads.add(new Thread(philosopher));
            threads.get(i).start();
        }
    }

    public void addPhilosopher() {
        stopPhilosophers();
        initializePhilosophers(++philosopherCount);
        refreshUI();

        // var fork = new Fork();
        // forks.add(fork);
        // stopPhilosopher(0);
        // philosophers.set(0, new Philosopher(0, fork, forks.get(0), this, forks.size()));
        // philosophers.add(new Philosopher(forks.size()-1, fork, forks.get(0), this, forks.size()));
        // philosopherStates.add(new PhilosopherState(forks.size()-1, "THINKING"));
        // threads.set(0, new Thread(philosophers.get(0)));
        // threads.add(new Thread(philosophers.get(forks.size()-1)));
        // threads.get(0).start();
        // threads.get(forks.size()-1).start();
        // philosopherCount++;
        // refreshUI();
    }

    public void removePhilosopher() {
        if (philosopherCount > 1) {
            stopPhilosophers();
            initializePhilosophers(--philosopherCount);
            refreshUI();
        }
    }

    private void stopPhilosophers() {
        threads.forEach(Thread::interrupt);
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void updateLog(String message) {
        logArea.setText(message + "\n" + logArea.getText());
    }

    public void updateState(int id, String state) {
        philosopherStates.get(id).setState(state);
        updateLog("Philosopher " + id + " is " + state);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        g.setColor(Color.ORANGE);
        g.fillOval(centerX - tableRadius, centerY - tableRadius, tableRadius * 2, tableRadius * 2);

        double angleStep = 2 * Math.PI / philosopherCount;
        for (int i = 0; i < philosopherCount; i++) {
            int x = (int) (centerX + (tableRadius + 50) * Math.cos(i * angleStep)) - 50;
            int y = (int) (centerY + (tableRadius + 50) * Math.sin(i * angleStep)) - 50;

            PhilosopherState state = philosopherStates.get(i);
            String path = switch (state.getState()) {
                case "LIFTING_LEFT_FORK" -> "LIFTING";
                case "LIFTING_RIGHT_FORK" -> "LIFTING_RIGHT";
                case "PUTTING_DOWN_LEFT_FORK" -> "PUTTING_LEFT";
                case "PUTTING_DOWN_RIGHT_FORK" -> "PUTTING";
                default -> state.getState();
            };

            new ImageIcon("gifs/" + path + ".gif").paintIcon(this, g, x, y);
            g.setColor(Color.BLACK);
            g.drawString("Philosopher " + state.getId(), x + 10, y + 15 + new ImageIcon("gifs/" + path + ".gif").getIconHeight());
        }
    }

    private void refreshUI() {
        revalidate();
        repaint();
    }

    private void stopPhilosopher(int id) {
        if (id >= 0 && id < threads.size()) {
            philosophers.get(id).stop(); // Signal the philosopher to stop
            threads.get(id).interrupt(); // Interrupt the thread
        }
    }
}

class PhilosopherState {
    private int id;
    private String state;

    public PhilosopherState(int id, String state) {
        this.id = id;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
