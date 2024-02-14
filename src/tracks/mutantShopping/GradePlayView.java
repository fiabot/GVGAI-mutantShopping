package tracks.mutantShopping;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GradePlayView {

    String game;
    String level;
    JPanel panel;
    RunGame runnable;
    Thread thread;
    GradeGames parent;
    JPanel gamePanel;

    boolean play = true;

    public GradePlayView(String game, String level, Container container, GradeGames parent) {
        this.game = game;
        this.level = level;
        this.parent = parent;
        panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();

        // panel.setSize(800, 500);
        gamePanel = new JPanel();
        gamePanel.setSize(400, 500);
        gamePanel.setBackground(Color.GRAY);

        String gameInstruction;
        try {
            gameInstruction = getGameInstructions();

        } catch (Exception e) {
            gameInstruction = "Cannot read game file";
        }

        ScrollPane scroll = new ScrollPane();
        scroll.setSize(400, 500);
        JPanel labelPanel = new JPanel();
        labelPanel.setSize(800, 500);
        JLabel label = new JLabel();

        label.setText("<html> <body width='%1s'>" + gameInstruction + "</html>");
        scroll.add(label);

        JButton playbutton = new JButton("Play Game");
        playbutton.addActionListener(new PlayGame(this));
        JPanel playhelperPanel = new JPanel();
        playhelperPanel.setSize(800, 50);
        playbutton.setSize(200, 50);
        playhelperPanel.add(playbutton);

        JSlider slider = new JSlider(0, 100, 50);
        JButton gradeButton = new JButton("Evaluate");
        gradeButton.addActionListener(new EvaluateGame(slider, parent));
        JPanel gradehelperPanel = new JPanel();
        gradehelperPanel.setSize(800, 50);
        gradehelperPanel.add(slider);
        gradehelperPanel.add(gradeButton);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(gamePanel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(scroll, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 20;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(playhelperPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(gradehelperPanel, gbc);

        container.add(panel);
        String recordActionsFile = null;

        runnable = new RunGame(game, level, true, parent.getAgent(), recordActionsFile, parent.seed, 0, gamePanel,
                parent.getFrame(), this);
        thread = new Thread(runnable);

    }

    public String getGameInstructions() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(game));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append("<br>");
            stringBuilder.append(line);
        }
        // delete the last new line separator
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();

        String content = stringBuilder.toString();
        return content;
    }

    public void playGame() {
        gamePanel.removeAll();
        if (play) {
            System.out.println("Threads:" + Thread.activeCount());
            try {
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void restart() {
        thread.interrupt();
        thread = new Thread(runnable);

        playGame();
    }

    public void stopGame() {
        play = false;
    }

    class PlayGame implements ActionListener {
        GradePlayView view;

        public PlayGame(GradePlayView view) {
            this.view = view;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // view.gamePanel.removeAll();
            view.stopGame();
            RunGame runnable = new RunGame(game, level, true, "", parent.seed, 0, gamePanel, parent.getFrame(), view);
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    class EvaluateGame implements ActionListener {
        GradeGames grader;
        JSlider slider;

        public EvaluateGame(JSlider slider, GradeGames grader) {
            this.slider = slider;
            this.grader = grader;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            grader.evaluateCurrent(slider.getValue());
        }
    }

}
