package tracks.mutantShopping;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GradeGames {

    ArrayList<EvaluateMutant> mutants;
    String agent;
    int seed;
    JFrame frame;
    JPanel panel;
    EvaluateMutant current;
    GradePlayView currentView; 
    ArrayList<EvaluateListener> listeners;

    public GradeGames(String agent, int seed) {
        mutants = new ArrayList<EvaluateMutant>();
        this.agent = agent;
        this.seed = seed;
        this.frame = new JFrame("Game Generator");
        frame.setSize(800, 800);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                clearDisplay();
                System.exit(0);
            }
        });
        panel = new JPanel();
        frame.add(panel);
        listeners = new ArrayList<EvaluateListener>();
    }

    public void addEvaluateListener(EvaluateListener listener) {
        System.out.println("adding listener:" + listener);
        listeners.add(listener);

    }

    public void addMutant(EvaluateMutant mutant) {
        System.out.println("Adding mutant");
        mutants.add(mutant);
        if (current == null) {
            evaluateNext();
        }

    }

    public void evaluateNext() {
        if (mutants.size() != 0) {
            panel.removeAll();
            EvaluateMutant mutant = mutants.get(0);
            mutants.remove(mutant);
            currentView = new GradePlayView(mutant.mutant.game, mutant.mutant.level, panel, this);
            currentView.playGame();
            current = mutant;
        } else {
            panel.removeAll();
            panel.repaint();
            panel.add(new JLabel("No games to test"));
            
            System.out.println("No more mutants to test");
        }

    }

    public void evaluateCurrent(int value) {
        System.out.println("Current mutant value is:" + value);
        currentView.stopGame();
        
        for (EvaluateListener listener : listeners) {
            listener.evaluate(current, value);
        }
        current = null;

        evaluateNext();
    }

    public String getAgent() {
        return agent;
    }

    public int getSeed() {
        return this.seed;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void clearDisplay() {

    }

    class EvaluateGames implements Runnable {
        GradeGames grader;

        public EvaluateGames(GradeGames grader) {
            grader = grader;
        }

        @Override
        public void run() {
            while (grader.current != null) {

            }
        }

    }

}
