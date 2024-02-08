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

public class SingleMutation extends JComponent {
    JFrame frame; 
    String agent; 
    String game; 
    String level; 
    SingleGameDisplay view; 
    JPanel panel; 
    int nextMutant = 0; 
    int seed; 
    public SingleMutation (String game, String level, String agent, Container container, JFrame frame, int seed){
        this.frame = frame; 
        this.agent = agent; 
        this.game = game; 
        this.level = level; 
        this.seed = seed; 

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                clearDisplay();
                System.exit(0);
            }
        });

        //ScrollPane scroll = new ScrollPane(); 
        
		panel = new JPanel(); 
		//scroll.add(panel);
		panel.setLayout(new GridLayout(1,3, 10, 10)); 
        container.add(panel);

        
        view = new SingleGameDisplay(game, level, panel, this); 
        view.playGame(); 
            

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
        System.out.println("Mutating");
        clearDisplay(); 
        Mutant starting = new Mutant(game, level); 
        nextMutant ++; 

     

        Mutant mut = starting.Mutate(1); 
        nextMutant ++; 
          
        view = new SingleGameDisplay(mut.game, mut.level, panel, this); 
        view.playGame();
        System.out.println("Finished Mutating");
         
        

    }


    private void clearDisplay(){
        panel.removeAll();
        panel.repaint();
        view.stopGame();

    }
    
}
