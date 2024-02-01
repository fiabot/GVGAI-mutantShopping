package tracks.mutantShopping;
import tracks.ArcadeMachine;

import java.lang.System.Logger.Level;

import javax.swing.*; 
import java.awt.*;

public class RunGame implements Runnable {

    String game_file;
    String level_file;
    boolean visuals;
    String agentNames;
    String actionFile;
    int randomSeed; int playerID;
    Container container;
    JFrame frame; 
    boolean go; 

    public RunGame(String game_file, String level_file, boolean visuals, String agentNames,
    String actionFile, int randomSeed, int playerID, Container container, JFrame frame){
        this.game_file = game_file; 
        this.level_file = level_file; 
        this.visuals = visuals; 
        this.agentNames = agentNames; 
        this.actionFile = actionFile; 
        this.randomSeed = randomSeed; 
        this.playerID = playerID; 
        this.container = container; 
        this.frame = frame; 
        this.go = true; 
    }

    @Override
    public void run() {
        try{
            ArcadeMachine.runOneGame(game_file,level_file, visuals, agentNames,
            actionFile, randomSeed, playerID, container, frame);

            if(go){
                run();
            }else{
                System.out.println("Ending game loop");
            }
            
            
            
        }catch (Exception e){
            System.out.println("Couldn't play game:" + game_file); 
        }

        
    }
    
    public void end(){
        go = false; 
    }
}
