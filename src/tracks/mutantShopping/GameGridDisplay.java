package tracks.mutantShopping;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;
import tracks.levelGeneration.geneticLevelGenerator.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GameGridDisplay extends JComponent {
    JFrame frame; 
    String agent; 
    String[] games; 
    String[] levels; 
    GamePlayView[] views; 
    JPanel panel; 
    int nextMutant = 0; 
    int seed; 
    public GameGridDisplay(String[] games, String[] levels, String agent, Container container, JFrame frame, int seed){
        this.frame = frame; 
        this.agent = agent; 
        this.games = games; 
        this.levels = levels; 
        this.seed = seed; 

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                clearDisplay();
                System.exit(0);
            }
        });

        ScrollPane scroll = new ScrollPane(); 
		panel = new JPanel(); 
		scroll.add(panel);
		panel.setLayout(new GridLayout(2,2, 10, 10)); 
		container.add(scroll); 

        views = new GamePlayView[games.length]; 

        for(int i = 0; i < games.length; i ++){
            GamePlayView view = new GamePlayView(games[i], levels[i], panel, this); 
            views[i] = view; 
        }


        for(int i = 0; i < views.length; i ++){
            views[i].playGame(); 

            try {
                TimeUnit.MILLISECONDS.sleep(400);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected JFrame getFrame(){
        return this.frame; 
    }

    protected String getAgent(){
        return this.agent;
    }

    protected int getSeed(){
        return this.seed; 
    }

    public void Mutate(String game, String level){
        clearDisplay(); 
        Mutant starting = new Mutant(game, level); 
        nextMutant ++; 

        Mutant[] mutants = new Mutant[4]; 

        for(int i =0; i < games.length; i++){
            Mutant mut = starting.Mutate(10); 
            nextMutant ++; 
            mutants[i] = mut; 
            GamePlayView view = new GamePlayView(mut.game, mut.level, panel, this); 
            views[i] = view; 
            
        }
        

        for(int i = 0; i < views.length; i ++){
            views[i].playGame(); 

            try {
                TimeUnit.MILLISECONDS.sleep(400);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


    private void clearDisplay(){
        for(GamePlayView view: views){
            view.stopGame(); 
        }

        panel.removeAll();
    }
    
}
