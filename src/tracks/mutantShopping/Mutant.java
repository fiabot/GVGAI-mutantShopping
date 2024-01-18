package tracks.mutantShopping;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import java.util.Set;
import javax.print.attribute.HashAttributeSet;
import javax.swing.Spring;

import core.game.Game;
import core.game.GameDescription;
import core.game.SLDescription;
import core.game.GameDescription.SpriteData;
import core.vgdl.VGDLParser;
import tools.GameAnalyzer;
import tools.IO;
import tracks.levelGeneration.geneticLevelGenerator.Chromosome.SpritePointData;

public class Mutant {

    String game; 
    String level; 

    /**
	 * current level described by the chromosome
	 */
	private char[][] level_array;

    private String[] interactions = new String[] { "killSprite", "killAll", "killIfHasMore", "killIfHasLess",
			"killIfFromAbove", "killIfOtherHasMore", "spawnBehind", "stepBack", "spawnIfHasMore", "spawnIfHasLess",
			"cloneSprite", "transformTo", "undoAll", "flipDirection", "transformToRandomChild", "updateSpawnType",
			"removeScore", "addHealthPoints", "addHealthPointsToMax", "reverseDirection", "subtractHealthPoints",
			"increaseSpeedToAll", "decreaseSpeedToAll", "attractGaze", "align", "turnAround", "wrapAround",
			"pullWithIt", "bounceForward", "teleportToExit", "collectResource", "setSpeedForAll", "undoAll",
			"reverseDirection", "changeResource" };
	/**
	 * A list of all the useful sprites in the game without the avatar
	 */
	private ArrayList<String> usefulSprites;
	/**
	 * the avatar sprite name
	 */
	private String avatar;
	/**
	 * Random object to help in generation
	 */
	private Random random;
    SLDescription sl; 
    Game toPlay; 
    GameDescription description; 
    GameAnalyzer analyzer; 

    Hashtable<String, SpriteData> spriteMapping; 

    public static int next = 0; 
    int id; 


    public Mutant(String game, String level){
        id = next; 
        next++; 

        this.game = game; 
        this.level = level; 
		this.random = new Random();
        
        toPlay = new VGDLParser().parseGame(game);
        description = new GameDescription(toPlay); 
        analyzer = new GameAnalyzer(description); 


    
		String[] lines = new IO().readFile(level);
        /*try {
            this.sl = new SLDescription(toPlay, lines,42);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/ 
        this.level_array = new char[lines.length][lines[0].length()]; 
		for(int y = 0; y < lines.length; y++){
			for(int x = 0; x <lines[0].length(); x++){
				this.level_array[y][x] = lines[y].charAt(x); 
			}
		}

		buildSpriteMapping();
       
		
    }

    /**
     * Build a map between sprite name and data 
     * of sprites 
     */
    private void buildSpriteMapping(){
        spriteMapping = new Hashtable<String,SpriteData>(); 
        ArrayList<SpriteData> sprites = description.getAllSpriteData(); 

        for(SpriteData data: sprites){
            spriteMapping.put(data.name, data); 
        }
    }

    public Mutant Mutate(int mutationAmount){
        Mutant copy = new Mutant(game, level); 
        copy.mutate_level(mutationAmount);
        copy.updateLevel(); 
        return copy; 
    }

    /**
	 * Get the avatar sprite from SLDescription
	 *
	 * @param sl
	 *            SLDescription object contains all the game info
	 * @return the avatar sprite name
	 */
	private String getAvatar(SLDescription sl) {
		SpriteData[] sprites = sl.getGameSprites();
		for (int i = 0; i < this.usefulSprites.size(); i++) {
			SpriteData s = this.getSpriteData(sprites, this.usefulSprites.get(i));
			if (s != null && s.isAvatar) {
				return this.usefulSprites.get(i);
			}
		}
		return "";
	}



    /**
	 * mutate the current chromosome
	 */
	public void mutate_level(int amount){
		getLevelCharacters();
		for(int i = 0; i < amount; i++)
		{
			int solidFrame = 0;
			if(analyzer.getSolidSprites().size() > 0){
				solidFrame = 2;
			}
			int pointX = random.nextInt(level_array[0].length - solidFrame) + solidFrame / 2;
			int pointY = random.nextInt(level_array.length - solidFrame) + solidFrame / 2;
            
            ArrayList<Character> levelChars = getLevelCharacters(); 
            // switch out a piece  
            if( levelChars.contains(level_array[pointY][pointX]) && random.nextFloat() < 0.3){
                
                // choose new piece 
                Character c = levelChars.get(random.nextInt(0, levelChars.size())); 
                level_array[pointY][pointX] = c; 
            }else{
                // swap two pieces 
                int point2X = random.nextInt(level_array[0].length - solidFrame) + solidFrame / 2;
                int point2Y = random.nextInt(level_array.length - solidFrame) + solidFrame / 2;
                
                char temp = level_array[pointY][pointX];
                level_array[pointY][pointX] = level_array[point2Y][point2X];
                level_array[point2Y][point2X] = temp;
            }
            
    
			
            

		}
	
	}

    private ArrayList<Character> getLevelCharacters(){
        HashMap<Character, ArrayList<String>> charMap = toPlay.getCharMapping(); 
        Iterator<Character> chars = charMap.keySet().iterator(); 
        ArrayList<Character> usableCharacters = new ArrayList<Character>();
        
        while(chars.hasNext()){
            Character c = chars.next(); 
            boolean isAvatar = false; 
            for(String sprite : charMap.get(c)){
                if(spriteMapping.get(sprite).isAvatar){
                    isAvatar = true; 
                }
            }

            if(!isAvatar){
                usableCharacters.add(c); 
            }
        }

        
        return  usableCharacters; 
    }

    

    private void updateLevel(){
        String file = "src/tracks/mutantShopping/MutantLevels/mutant_" + Integer.toString(id) + "_level.txt"; 
        try {
            FileWriter myWriter = new FileWriter(file);
            for(int y = 0; y < level_array.length; y++){
                for(int x = 0; x <level_array[0].length; x++){
                    myWriter.write(Character.toString(level_array[y][x]));
                }
                myWriter.write("\n"); 
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }

          level = file; 
        }


        


    /**
	 * get the free positions in the current level (that doesnt contain solid or object from the input list)
	 * @param sprites	list of sprites names to test them
	 * @return			list of all free position points
	 */
	private ArrayList<SpritePointData> getFreePositions(ArrayList<String> sprites){
		ArrayList<SpritePointData> positions = new ArrayList<SpritePointData>();
		
		for(int y = 0; y < level_array.length; y++){
			for(int x = 0; x < level_array[y].length; x++){
				ArrayList<String> tileSprites = level_array[y][x];
				boolean found = false;
				for(String stype:tileSprites){
					found = found || sprites.contains(stype);
                    found = found || (stype.equals(".") || stype.equals("-"));
					//found = found || SharedData.gameAnalyzer.getSolidSprites().contains(stype);
				}
				
				if(!found){
					positions.add(new SpritePointData("", x, y));
				}
			}
		}
		
		return positions;
	}

    /**
	 * helpful data structure to hold information about certain points in the level
	 * @author AhmedKhalifa
	 */
	public class SpritePointData{
		public String name;
		public int x;
		public int y;
		
		public SpritePointData(String name, int x, int y){
			this.name = name;
			this.x = x;
			this.y = y;
		}
	}
    
}
