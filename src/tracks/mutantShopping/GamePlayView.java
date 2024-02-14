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
import java.util.*;
public class GamePlayView{
    JPanel panel; 
    RunGame runnable; 
    Thread thread; 
    GameGridDisplay parent; 
    String game; 
    String level; 
    boolean play = true; 

    public GamePlayView(String game, String level, Container container, GameGridDisplay parent){
        this.game = game; 
        this.level = level;
        this.parent = parent; 
        panel = new JPanel(); 
        panel.setLayout(new BorderLayout()); 
        
        //panel.setSize(800, 500);
        JPanel gamePanel = new JPanel(); 
        //gamePanel.setSize(800, 500);
        gamePanel.setBackground(Color.GRAY);
        JButton button = new JButton("Mutate");
        button.addActionListener(new MutateEvent(game, level, parent)); 
        JPanel helperPanel = new JPanel();
        //helperPanel.setSize(800, 50);
        button.setSize(200, 50);
       
        helperPanel.add(button); 
        

        panel.add(gamePanel, BorderLayout.NORTH); 
        panel.add(new Label(game)); 

        panel.add(helperPanel, BorderLayout.SOUTH); 
        

        container.add(panel); 
        String recordActionsFile = null;


        runnable = new RunGame(game, level, true, parent.getAgent(), recordActionsFile, parent.seed, 0, gamePanel, parent.getFrame(), this); 
        thread = new Thread(runnable); 

    }

    public void playGame(){
        if(play){
            System.out.println("Threads:" + Thread.activeCount());
            try{
                thread.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
       
        
    }

    public void restart(){
        thread.interrupt();
        thread = new Thread(runnable); 
        
        playGame();
    }

    public void stopGame(){
        play = false; 
    }

    class MutateEvent implements ActionListener{
        String game; 
        String level; 
        GameGridDisplay grid; 
        public MutateEvent(String game, String level, GameGridDisplay grid){
            this.game = game; 
            this.level = level; 
            this.grid = grid; 
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            this.grid.Mutate(game, level);
        }
 }

   
}

