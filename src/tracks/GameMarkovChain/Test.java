package tracks.GameMarkovChain;

import java.io.IOException;
import java.util.ArrayList;

import tracks.levelGeneration.LevelGenMachine;

public class Test {

    public static void main(String[] args) throws IOException {
    
        MarkovChain chain = new MarkovChain(); 
        chain.trainFromCsvFile( "examples/all_games_sp.csv");
        
        for(int i = 0; i < 500; i ++){
            String game = chain.buildGame(); 
            String mutant1 = chain.mutateGame(game);
            String mutant2 = chain.mutateGame(mutant1);
        } 
       

    }

    
}
