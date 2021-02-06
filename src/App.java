import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class App {
    private static final MatchmakerAI matchmaker = new MatchmakerAI();

    public static void main(String... args) {
        Thread matchmakerThread = new Thread(matchmaker);
        matchmakerThread.start();

        JFrame frame = new JFrame();
        frame.setTitle("Matchmaker AI");
        frame.setSize(400, 190);

        var inputField = new JTextField("Enter user to match...");
        inputField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputField.setText("");
            }
        });
        inputField.setBounds(20, 20, 210, 30);
        frame.add(inputField);

        var button = new JButton("Matchmake!");
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(Color.BLACK);
        button.setBounds(245, 20, 125, 30);
        frame.add(button);

        var console = new JTextArea("Results...");
        console.setBounds(20, 60, 350, 70);
        console.setEditable(false);
        frame.add(console);

        button.addActionListener((event) -> {
            console.setText("Your match is: ");
            synchronized (matchmaker) {
                while (!matchmaker.isReady()) {
                    try {
                        matchmaker.wait();
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
            try {
                console.append(matchmaker.getMatchFor(inputField.getText()));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

        frame.setLayout(null);
        frame.setVisible(true);
    }
}
