package tracks.mutantShopping;

import java.io.BufferedReader;
import java.io.FileReader;
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

    public static String[] interactionParams = new String[] {
        "scoreChange", "stype", "limit", "resource", "stype_other", "forceOrientation", "spawnPoint",
        "value", "geq", "leq"};
    public static String[] terminationParams = new String[] {
        "stype", "stype1", "stype2", "stype3"
    };

    public static final int NUMERICAL_VALUE_PARAM = 2000;
        

    private ArrayList<String> termination; 
    int terminationIndent; 
    int interactionIndent; 
    private ArrayList<String> interaction; 

    String stableGame; //partof game description that remains constant 
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

        parseFiles();
        
       
       
		
    }

    private void parseFiles(){
        toPlay = new VGDLParser().parseGame(game);
        description = new GameDescription(toPlay); 
        analyzer = new GameAnalyzer(description); 
        try {
            parseInteractions(game);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    
    
		String[] lines = new IO().readFile(level);
        try {
            this.sl = new SLDescription(toPlay, lines,42);
            this.usefulSprites = new ArrayList<String>();
            this.random = new Random();
            String[][] currentLevel = sl.getCurrentLevel();


           

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

         // Just get the useful sprites from the current level
         for (SpriteData data : description.getAllSpriteData()){
                usefulSprites.add(data.name);
            }
        
        this.usefulSprites.add("EOS");
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
        copy.mutate_game(mutationAmount / 2);
        copy.updateGame();
        copy.parseFiles();
        return copy; 
    }

    /**
	 * Get the avatar sprite from SLDescription
	 *
	 * @param sl
	 *            SLDescription object contains all the game info
	 * @return the avatar sprite name
	 */
	/*private String getAvatar(SLDescription sl) {
		SpriteData[] sprites = sl.getGameSprites();
		for (int i = 0; i < this.usefulSprites.size(); i++) {
			SpriteData s = this.getSpriteData(sprites, this.usefulSprites.get(i));
			if (s != null && s.isAvatar) {
				return this.usefulSprites.get(i);
			}
		}
		return "";
	}*/ 
    public void  addRandomInteraction(ArrayList<String> interaction, ArrayList<String> termination){
        String nInteraction = interactions[random.nextInt(interactions.length)];
        int i1 = random.nextInt(usefulSprites.size());
        int i2 = (i1 + 1 + random.nextInt(usefulSprites.size() - 1)) % usefulSprites.size();
        
        String newInteraction = usefulSprites.get(i1) + " " + usefulSprites.get(i2) + " > " + nInteraction;
        // roll to see if you insert a parameter into this interaction
        double roll = random.nextDouble();
        
        if(roll < 0.5) {
            String nParam = interactionParams[random.nextInt(interactionParams.length)];
            nParam += "=";
            
            // there are two types of parameters, ones that take sprites and ones that take values
            if(nParam.equals("scoreChange=") || nParam.equals("limit=") || nParam.equals("value=") || nParam.equals("geq=")
                    || nParam.equals("leq=")) {
                int val = random.nextInt(NUMERICAL_VALUE_PARAM) - 1000;
                nParam += val;
            } else {
                String nSprite = usefulSprites.get(random.nextInt(usefulSprites.size()));
                nParam += nSprite;
            }
            newInteraction += " " + nParam;
        }
        // add the new interaction to the interaction arraylist 
        interaction.add(newInteraction);
        // remove weird space from the arrayList
        interaction.removeIf(s -> s == null);
        
    }

    public void  removeRandomInteraction(ArrayList<String> interaction, ArrayList<String> termination){
        // get two random indeces for the two sprites in the interaction
           int i = this.random.nextInt(interaction.size()); 
           String copy = interaction.get(i); 
           interaction.remove(i); 
           /*sl.testRules(getArray(interaction), getArray(termination));
           while(sl.getErrors().size() > 0){
               interaction.add(i, copy);
               i = this.random.nextInt(interaction.size()); 
               copy = interaction.get(i); 
               interaction.remove(i);
           }*/ 
   }

   public void changeWin(){
   // Add a winning termination condition
        String curWin = null; 
        for(String term: termination){
            if (term.contains("win=True")){
                curWin = term; 
            }
        }
        if (curWin != null){
            termination.remove(curWin); 
        }
        
		
		    // Add a winning termination condition
		if (this.random.nextBoolean()) {
		    termination.add("Timeout limit=" + (800 + this.random.nextInt(500)) + " win=True");
		} else {
		    String chosen = this.usefulSprites.get(this.random.nextInt(this.usefulSprites.size()));
		    
			termination.add("SpriteCounter stype=" + chosen + " limit=0 win=True");
			
		}
   }
	

   public void mutate_game(int amount){
    for(int i = 0; i < amount; i++)
		{
            
            // add a interaction rule 
            if(random.nextFloat() < 0.4 || interaction.size() < 2){
                addRandomInteraction(interaction, termination);
                
            }else if (random.nextFloat() < 0.8){
                // remove a interaction rule 
                removeRandomInteraction(interaction, termination);
            }else{
                changeWin(); 
            }
    

		}
   }

    /**
	 * convert the arraylist of string to a normal array of string
	 *
	 * @param list
	 *            input arraylist
	 * @return string array
	 */
	private String[] getArray(ArrayList<String> list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}

		return array;
	}
   

    /**
	 * mutate the current chromosome
	 */
	public void mutate_level(int amount){
		ArrayList<Character> levelChars = getLevelCharacters(); 
		for(int i = 0; i < amount; i++)
		{
			int solidFrame = 0;
			if(analyzer.getSolidSprites().size() > 0){
				solidFrame = 2;
			}
			int pointX = random.nextInt(level_array[0].length - solidFrame) + solidFrame / 2;
			int pointY = random.nextInt(level_array.length - solidFrame) + solidFrame / 2;
            
            
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
                if(spriteMapping.get(sprite) == null ||spriteMapping.get(sprite).isAvatar){
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

    private void parseInteractions(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader (file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        interaction = new ArrayList<String>();
        termination = new ArrayList<String>(); 

        boolean inTerminat = false; 
        boolean inInteration = false; 
        String tabTemplate = "    ";
        int lastIndex = -1; 
    
        try {
            while((line = reader.readLine()) != null) {
                if (inTerminat || inInteration){
                    line.replaceAll("\t", tabTemplate);
                    // remove comments starting with "#"
                    if (line.contains("#"))
                        line = line.split("#")[0];

                    // handle whitespace and indentation
                    String content = line.trim();
                    if(content.length() > 0){
                        char firstChar = content.charAt(0);
                        // figure out the indent of the line.
                        int index = line.indexOf(firstChar);

                        if (lastIndex == -1 && content.length() > 0) {
                            
                            lastIndex =  index; 
                            if (inInteration){
                                interaction.add(content);
                                System.out.println(content);
                            }else if (inTerminat){
                                termination.add(content); 
                                System.out.println(content);
                            }
                            
                        }else if (lastIndex > index){ //de-indended 
                            inInteration = false; 
                            inTerminat = false; 
                        }else{
                            if (inInteration){
                                interaction.add(content);
                                System.out.println(content);
                            }else if (inTerminat){
                                termination.add(content); 
                                System.out.println(content);
                            }
                        }
                    }
                    
                }
                if (line.contains("TerminationSet")){
                    inTerminat = true; 
                    String content = line.trim();
                   
                    char firstChar = content.charAt(0);
                    // figure out the indent of the line.
                    terminationIndent = line.indexOf(firstChar);
                    
                }

                else if(line.contains("InteractionSet")){
                    inInteration = true; 
                    String content = line.trim();
                   
                    char firstChar = content.charAt(0);
                    // figure out the indent of the line.
                    interactionIndent = line.indexOf(firstChar);
                }
                else if (!inTerminat && !inInteration){
                    stringBuilder.append(line);
                    stringBuilder.append(ls);
                }
               
            }
    
            stableGame = stringBuilder.toString();
        } finally {
            reader.close();
        }
    }

    private void updateGame(){
        String file = "src/tracks/mutantShopping/MutantGames/mutant_" + Integer.toString(id) + "_game.txt"; 
        String interactionSpace = new String(new char[interactionIndent]).replace('\0', ' ');
        String terminationSpace = new String(new char[interactionIndent]).replace('\0', ' ');

        try {
            FileWriter myWriter = new FileWriter(file);
            //System.out.println(stableGame);
            myWriter.write(stableGame);
            myWriter.write(interactionSpace + "InteractionSet\n");
            for(String line: interaction){
                myWriter.write(interactionSpace + "\t" + line + "\n"); 
            }
            myWriter.write(interactionSpace + "TerminationSet\n");
            for(String line: termination){
                myWriter.write(terminationSpace + "\t" + line + "\n"); 
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }

          game = file; 
        }
    
}
