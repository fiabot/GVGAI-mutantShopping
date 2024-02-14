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
    GamePlayView view; 
    GradePlayView view2;
    boolean humanPlayer; 

    public RunGame(String game_file, String level_file, boolean visuals, String agentNames,
    String actionFile, int randomSeed, int playerID, Container container, JFrame frame, GamePlayView view){
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
        this.view = view; 
        humanPlayer = false; 
    }


    public RunGame(String game_file, String level_file, boolean visuals, String agentNames,
    String actionFile, int randomSeed, int playerID, Container container, JFrame frame, GradePlayView view){
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
        this.view2 = view; 
        humanPlayer = false; 
    }

    public RunGame(String game_file, String level_file, boolean visuals, 
    String actionFile, int randomSeed, int playerID, Container container, JFrame frame, GradePlayView view){
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
        this.view2 = view; 
        humanPlayer = true; 
    }

    @Override
    public void run() {
        try{
            if(humanPlayer){
                ArcadeMachine.playOneGame(game_file, level_file, actionFile, randomSeed); 
            }else{
                ArcadeMachine.runOneGame(game_file,level_file, visuals, agentNames,
            actionFile, randomSeed, playerID, container, frame);

            }
            
           
            if(!humanPlayer){
                if(view != null){
                    view.restart();
                }
    
                if (view2 != null){
                    view2.restart();
                }
                

            }
          
            
            
        }catch (Exception e){
            System.out.println("Couldn't play game:" + game_file + " " + level_file); 
            e.printStackTrace();
        }

        
    }
    
    public void end(){
        go = false; 
        
    }
}
