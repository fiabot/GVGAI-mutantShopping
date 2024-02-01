package tracks.levelGeneration.MarkovChains;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class LevelGenerator {
    MarkovRandomField mrf; 
    Random random; 
    public LevelGenerator(MarkovRandomField mrf){
        this.mrf = mrf; 
        random = new Random(mrf.seed);
    }

    public LevelGenerator(char[] tiles, String[] levels, int seed){
        mrf = new MarkovRandomField(tiles, seed); 
        random = new Random(seed);
        mrf.train(levels);
    }

    private void swap(int x1, int y1, int x2, int y2, char[][] level){
        char temp = level[y1][x1]; 
        level[y1][x1] = level[y2][x2];
        level[y2][x2] = temp; 
    }

    private int[][] createEmptyBoard(int[]size){
        return new int[size[0]][size[1]]; 

    }

    private boolean hasUnselectedPositions(int[][] board){
        for(int y = 0; y < board.length; y++){
			for(int x = 0; x < board[0].length; x++){
                if (board[y][x] == 0){
                    return true; 
                }
            }
        }
        return false; 
    }

    public char[][] generate(int trials){
        // chooose size 
        int[] size = mrf.sampleLevelSizes(); 
        char[][] level = new char[size[0]][size[1]]; 

        // randomly select characters (except avatars)
        for(int y = 0; y < level.length; y++){
			for(int x = 0; x <level[0].length; x++){
                char symbol = mrf.sample(); 
                while (symbol == 'A'){
                    symbol = mrf.sample(); 
                }
                level[y][x] = symbol; 
            }
        }

        //randomly place avatar 
        int x = random.nextInt(size[1]); 
        int y = random.nextInt(size[0]); 
        level[y][x] = 'A';

        for (int trial = 0; trial < trials; trial ++){
           int[][] board = createEmptyBoard(size); 
           while (hasUnselectedPositions(board)){
            // randomly select positions 
            int x1 = random.nextInt(size[1]); 
            int x2 = random.nextInt(size[1]); 
            int y1 = random.nextInt(size[0]); 
            int y2 = random.nextInt(size[0]); 

            //update board 
            board[y1][x1] = 1; 
            board[y2][x2] = 1; 

             // get probabilty before 
             double pre =  mrf.getProbability(level[y1][x1], mrf.getContext(x1, y1, level)) * mrf.getProbability(level[y2][x2], mrf.getContext(x2, y2, level)); 

            // swap positions 
            swap(x1, y1, x2,y2, level); 
            
            // get new probability 
            double post =  mrf.getProbability(level[y1][x1], mrf.getContext(x1, y1, level)) * mrf.getProbability(level[y2][x2], mrf.getContext(x2, y2, level)); 

            //double acceptProb = Math.min(1, Math.pow(Math.E,  (Math.log(post) - Math.log(pre))));
            double acceptProb = 0.1; 
            if (!(pre+ post == 0)){
                acceptProb = post / (pre + post); 
            }

            boolean accept = random.nextDouble() < acceptProb; 
            if(!accept){
                // return to orginal state 
                swap(x1, y1, x2,y2, level); 
            }


        }
           

            
            //System.out.println(pre + " "  + post + " " + acceptProb);
        }
        return level; 
    }

    public static void saveLevel(String filePath, char[][] level){
        
            try {
                FileWriter myWriter = new FileWriter(filePath);
                for(int y = 0; y < level.length; y++){
                    for(int x = 0; x <level[0].length; x++){
                        myWriter.write(Character.toString(level[y][x]));
                    }
                    myWriter.write("\n"); 
                }
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
              }
     
              
            
    }
}
