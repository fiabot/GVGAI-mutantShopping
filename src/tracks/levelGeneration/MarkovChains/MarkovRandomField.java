package tracks.levelGeneration.MarkovChains;

import java.util.HashMap;

import tools.IO;

public class MarkovRandomField {

    /*Condition: [top, bottom, left, right]
     * Output: array the length of the tiles, representing the prob of getting a tile 
     */
    HashMap<String, float[]> probMap;

    HashMap<String, int[]> countMap; // counts, used to calculate prob 

    HashMap<Character, Integer> tileToIndex; 

    char[] tiles; 

    public MarkovRandomField(char[] tiles){
        this.tiles = tiles; 
        probMap = new HashMap<String, float[]>(); 
        countMap = new HashMap<String, int[]>(); 

        for(char top: tiles){
            for (char bottom: tiles){
                for(char left: tiles){
                    for (char right: tiles){
                        String key = "" + top + bottom + left + right; 
                        probMap.put(key, new float[tiles.length]); 
                        countMap.put(key, new int[tiles.length]); 
                    }
                }
            }
        }

        tileToIndex = new HashMap<Character, Integer>(); 

        for(int i = 0; i < tiles.length; i ++){
            tileToIndex.put(tiles[i], i); 
        }

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
        int up = y > 0 ? y - 1: levelArray.length - 1;
        int down = y < levelArray.length - 1 ? y + 1: 0;
        int left = x > 0 ? x -1 : levelArray[0].length - 1; 
        int right = x < levelArray[0].length - 1 ? x + 1: 0; 
        
        String key = "" + levelArray[up][x] + levelArray[down][x] +levelArray[y][left]+ levelArray[y][right];

        return key; 

    }

    public float getProbability(char tile, String context){
        return probMap.get(context)[tileToIndex.get(tile)]; 
    }

    public void updateCounts(char[][] levelArray){
        for(int y = 0; y < levelArray.length; y++){
			for(int x = 0; x <levelArray[0].length; x++){
                // update counts 
                Character thisChar = Character.valueOf(levelArray[y][x]); 
                
        		countMap.get(getContext(x, y, levelArray))[tileToIndex.get(thisChar)] += 1; 
			}
		}

    }
}
