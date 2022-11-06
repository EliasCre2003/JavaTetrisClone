package eliascregard;

import javax.swing.JFrame;

public class Main {
    public static GamePanel gamePanel;
    public static JFrame window;
    public static void main(String[] args) {
        window = new JFrame("Asteroid");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setUndecorated(true);

        gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack();
        window.setLocation(0, 0);
        window.setVisible(true);
        gamePanel.startGameThread();
        System.out.println("Game started");
    }
}