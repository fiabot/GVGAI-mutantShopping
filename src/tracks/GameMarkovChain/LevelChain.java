package tracks.GameMarkovChain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

//import javax.management.openmbean.OpenDataException;

//import javax.swing.text.DefaultStyledDocument.ElementSpec;

import core.game.Game;
import core.game.GameDescription;
import core.game.GameDescription.SpriteData;
import core.vgdl.VGDLFactory;
import core.vgdl.VGDLParser;
import core.vgdl.VGDLRegistry;
import tools.GameAnalyzer;
import tools.IO;

public class LevelChain {

    /*Condition: [top, bottom, left, right]
     * Output: array the length of the tiles, representing the prob of getting a tile 
     */
    HashMap<String, float[]> probMap;

    HashMap<String, int[]> countMap; // counts, used to calculate prob 

    HashMap<Character, Integer> tileToIndex; 
    HashMap<Character, String> charToType; 
    HashMap<String, Character> typeToChar; 

    ArrayList<Character> allCharacters; 
    ArrayList<int[]> levelSizes; 
    Random rand;
    int seed; 

    char[] tiles  = {'A', 'C', 'H', 'O', '.', 'S', '!'}; 
    public static char OOB_SYMBOL = '!'; 

    public LevelChain(int seed){
        this.seed = seed; 
        rand = new Random(seed);
        for(int i = 0; i < tiles.length; i ++){
            this.tiles[i] = tiles[i]; 
        }
        
        probMap = new HashMap<String, float[]>(); 
        countMap = new HashMap<String, int[]>(); 

        for(char top: this.tiles){
            for (char bottom: this.tiles){
                for(char left: this.tiles){
                    for (char right: this.tiles){
                        String key = "" + top + bottom + left + right; 
                        probMap.put(key, new float[this.tiles.length]); 
                        countMap.put(key, new int[this.tiles.length]); 
                    }
                }
            }
        }

        allCharacters = new ArrayList<Character>(); 
        levelSizes = new ArrayList<int[]>(); 

        tileToIndex = new HashMap<Character, Integer>(); 

        for(int i = 0; i < tiles.length; i ++){
            tileToIndex.put(tiles[i], i); 
        }

    }



    public LevelChain(){
        this(new Random().nextInt()); 
    }

    public void trainFromFiles(String game, String[] files) throws IOException{
        String[] levels = new String[files.length]; 
        for(int i = 0; i < files.length; i ++){
            levels[i] = MarkovChain.getFileAsString(files[i]); 
        }

        train(game, levels);
    }

    public void train(String game, String[] levels){

        for (String level : levels){
        
            char[][] level_array = generalizedLevel(game, level); 
            updateCounts(level_array); 
         
           
        }

        // normalize to probabilities 
        for(String key : countMap.keySet()){
            int[] counts = countMap.get(key); 
            float sum = 0; // some probabilty for all 
            for (int i: counts){
                sum += i; 
            }

            if (sum == 0){
                //TODO: What to do if not in training data 
            }else{
                for(int i = 0; i < counts.length; i ++){
                    probMap.get(key)[i] = (counts[i] + 1) / sum;                 
                }
            }
        }    

    }

    public static HashMap<Character, Character> getGeneralizedCharacters(String game){
        ArrayList<ArrayList<String>> parts = MarkovChain.parseInteractions(game); 
        ArrayList<String> levelMapping = parts.get(3); 
        HashMap<Character, Character> map = new  HashMap<Character, Character>();
        map.put(' ', '.');
        Game toPlay = new VGDLParser().parseGameAsString(game); 
        GameDescription gameDescription = new GameDescription(toPlay); 
        GameAnalyzer gameAnalyzer = new GameAnalyzer(gameDescription); 
    

        for(String line: levelMapping){
            line = line.strip();
            char c = line.charAt(0); 
            String[] nameStrings = line.split(">")[1].split(" ");
            ArrayList<Character> allTypes = new ArrayList<Character>(); 

            for(String name: nameStrings){
                //number of solid objects (priority value, if not spawned and 0 otherwise)
                if(gameAnalyzer.getSolidSprites().contains(name)){
                    allTypes.add('S'); 
                }
                //number of harmful sprites (priority value, if not spawned and 0 otherwise)
                if(gameAnalyzer.getHarmfulSprites().contains(name)){
                    allTypes.add('H'); 
                }
                //number of other sprites (priority value, if not spawned and 0 otherwise)
                if(gameAnalyzer.getOtherSprites().contains(name)){
                    allTypes.add('O'); 
                }
                //number of collectable sprites  (priority value, if not spawned and 0 otherwise)
                if(gameAnalyzer.getCollectableSprites().contains(name)){
                    allTypes.add('C'); 
                }

                if(gameAnalyzer.getAvatarSprites().contains(name)){
                    allTypes.add('A'); 
                }
            }

            if (allTypes.contains('A')){
                map.put(c, 'A'); 
            }else if (allTypes.contains('H')){
                map.put(c, 'H'); 
            }else if (allTypes.contains('C')){
                map.put(c, 'C'); 
            }else if (allTypes.contains('S')){
                map.put(c, 'S'); 
            }else if (allTypes.size() > 1){
                map.put(c, 'O');
            }else{
                map.put(c,'.');
            }

        }

        return map; 

    }

    public static HashMap<Character, ArrayList<Character>> getSpecifyMap(String game){
        HashMap<Character, Character> generaMap = getGeneralizedCharacters(game); 
        HashMap<Character, ArrayList<Character>> specificMap = new HashMap<Character, ArrayList<Character>>();

        for(Character c : generaMap.keySet()){
            Character g = generaMap.get(c); 
            if(!specificMap.containsKey(g)){
                specificMap.put(g, new ArrayList<Character>());
                
            }
            specificMap.get(g).add(c); 
        }


        return specificMap; 
    }

    public static char[][] generalizedLevel(String game, String levelString){
        char[][] level = getLevelArrayFromString(levelString); 
        HashMap<Character, Character> generalizedChars = getGeneralizedCharacters(game); 
        for(int y = 0; y < level.length; y ++){
            for(int x = 0; x < level[0].length; x ++){
                char c = level[y][x]; 
                if(generalizedChars.containsKey(c)){
                    level[y][x] = generalizedChars.get(c);
                }else{
                    level[y][x] = 'O';
                }
                
            }
        }
        return level; 
    }

    public static String specifyLevel(String game, char[][] generalLevel){
        Random random = new Random(); 
        HashMap<Character, ArrayList<Character>> specificMap = getSpecifyMap(game); 
        char[][] level = new char[generalLevel.length][generalLevel[0].length]; 
        for(int y = 0; y < level.length; y ++){
            for(int x = 0; x < level[0].length; x ++){
                char c = generalLevel[y][x]; 

                if(specificMap.containsKey(c) && c != '.'){
                   
                    ArrayList<Character> options = specificMap.get(c);
                    Character newChar = options.get(random.nextInt(options.size()));
                    
                    level[y][x] = newChar;

                    
                }else{
                    level[y][x] = ' ';
                }
                
            }
        }

        String levelString = ""; 
        for(int y = 0; y < level.length; y ++){
            for(int x = 0; x < level[0].length; x ++){
                levelString += level[y][x]; 
            }
            levelString += "\n";
        }
        return levelString; 
    }

    public static char[][] getLevelArray(String file){
        String[] lines = new IO().readFile(file);
        char[][] level_array = new char[lines.length][lines[0].length()]; 
		for(int y = 0; y < lines.length; y++){
			for(int x = 0; x <lines[0].length(); x++){
				level_array[y][x] = lines[y].charAt(x); 
			}
		}
        return level_array;
    }

    public static char[][] getLevelArrayFromString(String level){
        String[] lines = level.split("\n");
        char[][] level_array = new char[lines.length][lines[0].length()]; 
		for(int y = 0; y < lines.length; y++){
			for(int x = 0; x <lines[0].length(); x++){
                try{
                    level_array[y][x] = lines[y].charAt(x); 
                }catch(Throwable t){
                    level_array[y][x] = ' '; 
                }
				
			}
		}
        return level_array;
    }

    public String getContext(int x, int y, char[][] levelArray){
        // find positions, assuming wrapping 
        char up = y > 0 ? levelArray[y-1][x] : OOB_SYMBOL;
        char down = y < levelArray.length - 1 ? levelArray[y+1][x]: OOB_SYMBOL;
        char left = x > 0 ? levelArray[y][x-1] : OOB_SYMBOL; 
        char right = x < levelArray[0].length - 1 ?levelArray[y][x+1] : OOB_SYMBOL; 
        
        String key = "" + up + down +left + right;

        return key; 

    }

    public String getGeneralContext(int x, int y, char[][] levelArray, HashMap<Character, Character> generalMap){
        // find positions and generalize characters, assuming wrapping 
        char up = y > 0 ? generalMap.get(levelArray[y-1][x]) : OOB_SYMBOL;
        char down = y < levelArray.length - 1 ? generalMap.get(levelArray[y+1][x]): OOB_SYMBOL;
        char left = x > 0 ? generalMap.get(levelArray[y][x-1]) : OOB_SYMBOL; 
        char right = x < levelArray[0].length - 1 ? generalMap.get(levelArray[y][x+1]): OOB_SYMBOL; 
        
        String key = "" + up + down +left + right;

        return key; 

    }

    public char sampleFromContext(String context, char toAvoid){
        float[] probabilities = probMap.get(context); 
        double randDoub = rand.nextDouble();
        double sum = 0; 
        
        for(int i = 0; i < probabilities.length; i ++){
            sum += probabilities[i]; 
            if(randDoub < sum && tiles[i] != toAvoid && tiles[i] != 'A' && tiles[i] != '!'){
                return tiles[i];  
            }
        }

        char randTile = tiles[rand.nextInt(tiles.length - 1)]; 

        while(randTile == 'A'){
            randTile = tiles[rand.nextInt(tiles.length - 1)]; 
        }

        return randTile; 
    }

    public float getProbability(char tile, String context){
        return probMap.get(context)[tileToIndex.get(tile)]; 
    }

    public double getProbability(char[][] levelArray){
        double prob = 0; 
        for(int y = 0; y < levelArray.length; y++){
			for(int x = 0; x <levelArray[0].length; x++){
                // update probability 
                float thisProb =  getProbability(levelArray[y][x], getContext(x, y, levelArray)); 
                if(thisProb == 0){
                    thisProb = 0.001f;
                }
        		prob += Math.log(thisProb); 
                //System.out.println(thisProb + " " + Math.log(thisProb));
			}
		}
        return prob; 
    }

 

    public String mutateLevel(String game, String level){
        char[][] levelArr = getLevelArrayFromString(level);  
        int[] size = {levelArr.length,  levelArr[0].length};
        double roll = rand.nextDouble(); 
        
        // get a new character 
        if(roll < 0.5){
            System.out.println("New Character");
            HashMap<Character, Character> generalMap = getGeneralizedCharacters(game); 
            HashMap<Character, ArrayList<Character>> specifyMap = getSpecifyMap(game);
            int x = rand.nextInt(size[1]); 
            int y = rand.nextInt(size[0]); 
            // don't change avatar 
            while(generalMap.get(levelArr[y][x]) == 'A'){
                x = rand.nextInt(size[1]); 
                y = rand.nextInt(size[0]); 
            }

            System.out.println("selected: " + x + "," + y + " = " + levelArr[y][x]); 

            String context  = getGeneralContext(x, y, levelArr, generalMap); 
            char generalChar = sampleFromContext(context, generalMap.get(levelArr[y][x]));
            System.out.println("selected char: " + generalChar);  
            if(specifyMap.containsKey(generalChar)){
                ArrayList<Character> options = specifyMap.get(generalChar); 
                levelArr[y][x] = options.get(rand.nextInt(options.size()));
                
            }else{
                levelArr[y][x] = ' ';
            }

            System.out.println("changed to: " + levelArr[y][x]); 
        }else{ //swap 
            System.out.println("swapping");
            char[][] generalLevel = generalizedLevel(game, level);
            int x1 = rand.nextInt(size[1]); 
            int x2 = rand.nextInt(size[1]); 
            int y1 = rand.nextInt(size[0]); 
            int y2 = rand.nextInt(size[0]); 
            int i = 0; 
            while(levelArr[y1][x1] == levelArr[y2][x2] && i < 1000){
                x1 = rand.nextInt(size[1]); 
                x2 = rand.nextInt(size[1]); 
                y1 = rand.nextInt(size[0]); 
                y2 = rand.nextInt(size[0]); 
                i++; 
            }

            System.out.println("Selected: " + levelArr[y1][x1]  + "," + levelArr[y2][x2]);

      
             // get probabilty before 
             double pre = getProbability(generalLevel); 

            // swap positions 
            swap(x1, y1, x2,y2, generalLevel); 
     
            double post = getProbability(generalLevel); 
            double acceptProb = Math.pow(Math.E, post - pre); 

            acceptProb = Math.min(1, acceptProb * 10); // inflating prob
            System.out.println(acceptProb);
            boolean accept = rand.nextDouble() < acceptProb; 
            if(!accept){
                System.out.println("rejected");
                // select another mutation
                return (mutateLevel(game, level)); 
            }else{ // swap on real level 
                System.out.println("accepted");
                swap(x1, y1, x2,y2, levelArr); 
            }


        }
        
        String newLevel = ""; 
        for(char[] row: levelArr){
            for(char c: row){
                newLevel += c; 
            }
            newLevel += "\n";
        }

        return newLevel;
    }

    public static void printLevel(char[][] level){
        for(char[] row: level){
            for(char c: row){
                System.out.print(c); 
            }
            System.out.print("\n");
        }
    }
    public static boolean rowHasAvatar(char[] row){
        for(char c : row){
            if(c == 'A'){
                return true; 
            }
        }
        return false; 
    }

    public static char[][][] crossOver(char[][] general_a, char[][] general_b){
        Random rand = new Random(); 
        boolean a1_to_c1 = rand.nextDouble() > 0.5; 

        char[][] child1 = new char[general_a.length][general_a[0].length]; 
        char[][] child2 = new char[general_a.length][general_a[0].length]; 

        for(int i = 0; i < general_a.length; i ++){
            if(i < general_b.length && general_a[i].length == general_b[i].length){
                if(rowHasAvatar( general_a[i]) || rowHasAvatar( general_b[i]) ){
                    if(a1_to_c1){
                        child1[i] = general_a[i]; 
                        child2[i] = general_b[i];  
                    }else{
                        child1[i] = general_b[i]; 
                        child2[i] = general_a[i]; 
                    }
                }
          
                else if(rand.nextDouble() < 0.5){
                    child1[i] = general_a[i]; 
                    child2[i] = general_b[i];
                }else{
                    child1[i] = general_b[i]; 
                    child2[i] = general_a[i];
                }
            }else{
                child1[i] = general_a[i]; 
                child2[i] = general_a[i];
            }
        }

        char[][][] returnArr = new  char[2][general_a.length][general_a[0].length];
        returnArr[0] = child1; 
        returnArr[1] = child2; 
        
        return returnArr; 

    }

    public static String[] crossover(String game1, String game2, String level_a, String level_b){
        char[][] general_a = generalizedLevel(game1, level_a); 
        char[][] general_b = generalizedLevel(game2, level_b); 

        char[][][] children = crossOver(general_a, general_b);

        
        String[] returnStr = {specifyLevel(game1, children[0]), specifyLevel(game2, children[1])}; 
        return returnStr; 
    }
    /*
     * Get a character with 
     * frequencies seen in the 
     * training data 
     */
    public char sample(){
        
        return  allCharacters.get(rand.nextInt(allCharacters.size()));
    }

    public int[] sampleLevelSizes(){
        return levelSizes.get(rand.nextInt(levelSizes.size())); 
    }


    public void updateCounts(char[][] levelArray){
        levelSizes.add(new int[] {levelArray.length, levelArray[0].length});
        for(int y = 0; y < levelArray.length; y++){
			for(int x = 0; x <levelArray[0].length; x++){
                // update counts 
                Character thisChar = Character.valueOf(levelArray[y][x]); 
                int[] currentCounts = countMap.get(getContext(x, y, levelArray)); 
                currentCounts[tileToIndex.get(thisChar)] +=1;  
                allCharacters.add(thisChar);
			}
		}

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
        //int[] size = sampleLevelSizes(); 
        int[] size = {10, 20};
        char[][] level = new char[size[0]][size[1]]; 

        // randomly select characters (except avatars)
        for(int y = 0; y < level.length; y++){
			for(int x = 0; x <level[0].length; x++){
                char symbol = sample(); 
                while (symbol == 'A'){
                    symbol = sample(); 
                }
                level[y][x] = symbol; 
            }
        }

        //randomly place avatar 
        int x = rand.nextInt(size[1]); 
        int y = rand.nextInt(size[0]); 
        level[y][x] = 'A';

        for (int trial = 0; trial < trials; trial ++){
           int[][] board = createEmptyBoard(size); 
           while (hasUnselectedPositions(board)){
            // randomly select positions 
            int x1 = rand.nextInt(size[1]); 
            int x2 = rand.nextInt(size[1]); 
            int y1 = rand.nextInt(size[0]); 
            int y2 = rand.nextInt(size[0]); 

            //update board 
            board[y1][x1] = 1; 
            board[y2][x2] = 1; 

             // get probabilty before 
             //double pre =  mrf.getProbability(level[y1][x1], mrf.getContext(x1, y1, level)) * mrf.getProbability(level[y2][x2], mrf.getContext(x2, y2, level)); 
             double pre = getProbability(level); 

            // swap positions 
            swap(x1, y1, x2,y2, level); 
            
            // get new probability 
            //double post =  mrf.getProbability(level[y1][x1], mrf.getContext(x1, y1, level)) * mrf.getProbability(level[y2][x2], mrf.getContext(x2, y2, level)); 

            //double acceptProb = Math.min(1, Math.pow(Math.E,  (Math.log(post) - Math.log(pre))));
            /*double acceptProb = 0.1; 
            if (!(pre+ post == 0)){
                acceptProb = post / (pre + post); 
            } */

            double post = getProbability(level); 
            double acceptProb = Math.min(1, Math.pow(Math.E, post - pre)); 
            //System.out.println(pre + " " + post + " " + acceptProb);

            boolean accept = rand.nextDouble() < acceptProb; 
            if(!accept){
                // return to orginal state 
                swap(x1, y1, x2,y2, level); 
            }


        }
           

            
            //System.out.println(pre + " "  + post + " " + acceptProb);
        }
        return level; 
    }
    
}
