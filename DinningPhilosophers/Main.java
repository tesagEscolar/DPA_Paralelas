import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private VisualManager visualManager;
    private JTextArea logArea;

    public Main() {
        setTitle("Dining Philosopher Problem");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        logArea = new JTextArea(5, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        visualManager = new VisualManager(logArea);

        JButton addButton = new JButton("Add Philosopher");
        addButton.addActionListener(e -> visualManager.addPhilosopher());

        JButton removeButton = new JButton("Remove Philosopher");
        removeButton.addActionListener(e -> visualManager.removePhilosopher());

        JPanel controlPanel = new JPanel();
        controlPanel.add(addButton);
        controlPanel.add(removeButton);

        add(controlPanel, BorderLayout.NORTH);
        add(visualManager, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
