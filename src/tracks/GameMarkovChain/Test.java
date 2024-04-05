package tracks.GameMarkovChain;

import java.io.IOException;

public class Test {

    public static void main(String[] args) {
        String gameFile = "examples/gridphysics/aliens.txt";
        //gameFile = "Chopper.txt";
        try {
            
            MarkovChain chain = new MarkovChain(); 
            chain.trainFromCsvFile( "examples/all_games_sp.csv");
            System.out.println(chain.buildGame());
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

    
}
