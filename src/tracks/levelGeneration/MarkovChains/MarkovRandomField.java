package tracks.levelGeneration.MarkovChains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import tools.IO;

public class MarkovRandomField {

    /*Condition: [top, bottom, left, right]
     * Output: array the length of the tiles, representing the prob of getting a tile 
     */
    HashMap<String, float[]> probMap;

    HashMap<String, int[]> countMap; // counts, used to calculate prob 

    HashMap<Character, Integer> tileToIndex; 

    ArrayList<Character> allCharacters; 
    ArrayList<int[]> levelSizes; 
    Random rand;
    int seed; 

    char[] tiles; 
    public static char OOB_SYMBOL = '!'; 

    public MarkovRandomField(char[] tiles, int seed){
        this.seed = seed; 
        rand = new Random(seed);
        this.tiles = new char[tiles.length + 1]; 
        for(int i = 0; i < tiles.length; i ++){
            this.tiles[i] = tiles[i]; 
        }
        this.tiles[tiles.length] = OOB_SYMBOL; 
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

    public MarkovRandomField(char[] tiles){
        this(tiles, new Random().nextInt()); 
    }

    public void train(String[] level_files){

        for (String file: level_files){
            char[][] level_array = getLevelArray(file); 
            updateCounts(level_array); 
        }

        // normalize to probabilities 
        for(String key : countMap.keySet()){
            int[] counts = countMap.get(key); 
            float sum = 0; 
            for (int i: counts){
                sum += i; 
            }

            if (sum == 0){
                //TODO: What to do if not in training data 
            }else{
                for(int i = 0; i < counts.length; i ++){
                    probMap.get(key)[i] = counts[i] / sum;                 
                }
            }
        }    

    }

    public char[][] getLevelArray(String file){
        String[] lines = new IO().readFile(file);
        char[][] level_array = new char[lines.length][lines[0].length()]; 
		for(int y = 0; y < lines.length; y++){
			for(int x = 0; x <lines[0].length(); x++){
				level_array[y][x] = lines[y].charAt(x); 
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

    public float getProbability(char tile, String context){
        return probMap.get(context)[tileToIndex.get(tile)]; 
    }

    public double getProbability(char[][] levelArray){
        double prob = 1; 
        for(int y = 0; y < levelArray.length; y++){
			for(int x = 0; x <levelArray[0].length; x++){
                // update probability 
                float thisProb =  getProbability(levelArray[y][x], getContext(x, y, levelArray)); 
                if(thisProb == 0){
                    thisProb = 0.001f;
                }
        		prob *= thisProb; 
			}
		}
        return prob; 
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
                
        		countMap.get(getContext(x, y, levelArray))[tileToIndex.get(thisChar)] += 1; 
                allCharacters.add(thisChar);
			}
		}

    }
}
