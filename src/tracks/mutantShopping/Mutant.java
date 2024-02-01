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
import java.util.stream.Collectors;

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

	private String[] avatars = new String[] {"MovingAvatar", "HorizontalAvatar", "VerticalAvatar", "OngoingAvatar", "OngoingTurningAvatar", "OngoingShootingAvatar", "MissileAvatar", "OrientedAvatar", "ShootAvatar", "FlakAvatar"};

    public static String[] interactionParams = new String[] {
        "scoreChange", "stype", "limit", "resource", "stype_other", "forceOrientation", "spawnPoint",
        "value", "geq", "leq"};
    public static String[] terminationParams = new String[] {
        "stype", "stype1", "stype2", "stype3"
    };

    public static String[] terminations = new String[] {
		"SpriteCounter", "SpriteCounterMore", "MultiSpriteCounter",
		"StopCounter", "Timeout"};

    public static final int NUMERICAL_VALUE_PARAM = 2000;

    public static final int TERMINATION_LIMIT_PARAM = 1000;
        

    private ArrayList<String> termination; 
    int terminationIndent; 
    int interactionIndent; 
	
    private ArrayList<String> interaction; 
	private ArrayList<String> sprites; 
	
	int spriteIndent; 
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
        
        try {
			toPlay = new VGDLParser().parseGame(game);
        	description = new GameDescription(toPlay); 
        	analyzer = new GameAnalyzer(description); 
            parseInteractions(game);
        } catch (Exception e) {
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

	public String[] getAvatarString(){
		for(String sprite: sprites){
			if (sprite.contains("Avatar")){
				for(String type: avatars){
					if(sprite.contains(type)){
						return new String[] {sprite, type}; 
					}
				}
			}
		}

		return new String[2]; 
		
	}

	public void changeAvatar(){
		String[] avatar = getAvatarString(); 
		if (avatar[0] != null){
			String newAvatarType = avatars[random.nextInt(avatars.length)]; 
			String newAvatarString  = avatar[0].replace(avatar[1], newAvatarType); 
			sprites.remove(avatar[0]); 
			sprites.add(newAvatarString);
		}
	}

    public void mutateTermination(){
		double mutationType = random.nextDouble();
		// we do an insertion
		if(mutationType < 0.33) {
			// roll dice to see if we will insert a new rule altogether or a new parameter into an existing rule
			double roll = random.nextDouble();
			// insert a new parameter onto an existing rule
			if(roll < 0.2) {
				// grab a random existing rule
				int point = random.nextInt(termination.size());
				String addToMe = termination.get(point);
				// insert a new parameter into it
				String nParam = terminationParams[random.nextInt(terminationParams.length)];
				nParam += "=";
				// add either a number or a sprite to the parameter
				double roll1 = random.nextDouble();
				// insert a sprite
				if(roll1 <0.5) {
					String nSprite = usefulSprites.get(random.nextInt(usefulSprites.size()));
					nParam += nSprite;
				}
				// insert a numerical value
				else {
					int val = random.nextInt(NUMERICAL_VALUE_PARAM);
					nParam += val;
				}
				addToMe += " " + nParam;
		
                termination.set(point, addToMe); 
				
				// DEBUG CODE loop through terminations and find a bug
				for(int i = 0; i < this.termination.size(); i++) {
					if(termination.get(i).contains("limit= ")) {
						System.out.println("Broken");
					}
				
				}
			}
			// insert an entirely new rule, possibly with a parameter in it
			else {
				String nTermination = terminations[random.nextInt(terminations.length)];    
				
				
				// roll to see if we include a parameter from the termination parameter set
				double roll1 = random.nextDouble();
				if(roll <0.33) {
					String nParam = terminationParams[random.nextInt(terminationParams.length)];
					nParam += "=";
					// add either a number or a sprite to the parameter only two types
					double roll2 = random.nextDouble();
					// insert a sprite
					String nSprite = usefulSprites.get(random.nextInt(usefulSprites.size()));
					nParam += nSprite;
					
					nTermination+= " " + nParam;
				}
				// add win and limit
				nTermination += " win=";
				
				double roll2 = random.nextDouble();
				if(roll2 < 0.5){
					nTermination += "True";
				} else {
					nTermination += "False";
				}
				// special rules for Timeout rule
				if(nTermination.contains("Timeout")) {
					int val = random.nextInt(TERMINATION_LIMIT_PARAM) + 500;
					nTermination += " limit="+val;
				} else{
					int val = random.nextInt(TERMINATION_LIMIT_PARAM);
					nTermination += " limit="+val;
				}
			    // add the new termination to the termination set
			    termination.add(nTermination);
			    // remove weird space from the arrayList
			    termination.removeIf(s -> s == null);
			    // stream the list back into itself to avoid duplicate rules from having been created
				termination = (ArrayList<String>) termination.stream().distinct().collect(Collectors.toList());
		
				
				
				// DEBUG CODE loop through terminations and find a bug
				for(int i = 0; i < termination.size(); i++) {
					if(termination.get(i).contains("limit= ")) {
						System.out.println("Broken");
					}
				
				}
			}
		} 
		// we do a deletion
		else if(mutationType <0.33 + 0.33) {
			// roll dice to see if we will delete a rule altogether or a parameter of an existing rule
			double roll = random.nextDouble();
			// delete a parameter from an existing rule
			if(roll < 0.33) {
				int point = random.nextInt(termination.size());
				String deleteFromMe = termination.get(point);
				// find all parameters for this rule, note: there may be none.  In that case we do nothing.
				String[] splitDeleteFromMe = deleteFromMe.split("\\s+");
				ArrayList<String> params = new ArrayList<String>();
				for(String param : splitDeleteFromMe) {
					// we can assume that if one of the split strings contains an = sign that it is a parameter
					// the extra rule here is that it is not a "limit" or a "win" param. We cannot remove those!
					if(param.contains("=") && !param.contains("limit") && !param.contains("win")){
						params.add(param);
					}
				}
				// if no params do nothing
				if(params.size() == 0) {
					
				} 
				else {
					// pick one of the rules and don't include it, but include the others
					int rule = random.nextInt(params.size());
					String fixedRule = "";
					for(String part : splitDeleteFromMe) {
						if(!part.equals(params.get(rule))) {
							fixedRule += part + " ";
						}
					}
					termination.set(point, fixedRule);
				}
			    // remove weird space from the arrayList
				termination.removeIf(s -> s == null);
			    // stream the list back into itself to avoid duplicate rules from having been created
				termination = (ArrayList<String>) termination.stream().distinct().collect(Collectors.toList());
				
				
			}
			// delete an entire rule from the interaction set
			else{
				int point = random.nextInt(termination.size());
				// dont try to delete from an empty interaction set
				if (termination.size() > 1) {
					termination.remove(point);
				}
			    // remove weird space from the arrayList
				termination.removeIf(s -> s == null);
			    // stream the list back into itself to avoid duplicate rules from having been created
				termination = (ArrayList<String>) termination.stream().distinct().collect(Collectors.toList());
		
				
				
			}
		} 
		// modify a rule from the interaction set by changing its parameters
		else if (mutationType < 0.1+ 0.33+0.33) {
			// pick our modified rule
			int point = random.nextInt(termination.size());
			
			// roll to see what kind of modification, either a rule change or a parameter change
			double roll = random.nextDouble();
			// modify a parameter of a rule completely
			if(roll < 0.33) {
				String modifyFromMe = termination.get(point);
				// find all parameters for this rule, note: there may be none.  In that case we do nothing.
				String[] splitModifyFromMe = modifyFromMe.split("\\s+");
				ArrayList<String> ps = new ArrayList<String>();
				for(String param : splitModifyFromMe) {
					// we can assume that if one of the split strings contains an = sign that it is a parameter
					// we can change limit and win parameters now (but this will cause us to have special rules)!
					if(param.contains("=")){
						ps.add(param);
					}
				}
				// if no params do nothing
				if(ps.size() == 0) {
					
				} else {
					// pick one of the rules and don't include it, but include the others
					int rule = random.nextInt(ps.size());
					String fixedRule = "";
					for(String part : splitModifyFromMe) {
						if(!part.equals(ps.get(rule))) {
							fixedRule += part + " ";
						} 
						// we are on the parameter we want to modify
						else {
							String nParam = ""; 
							if(part.contains("win")) {
								nParam = "win=";
								// roll dice to see if true or false
								double roll2 = random.nextDouble();
								if(roll2 < 0.5) {
									nParam += "True";
								} else {
									nParam += "False";
								}
							} else if(part.contains("limit")) {
								nParam = "limit=";
								// if this is a timeout rule, special conditions apply,  make so limit is at least 500
								if(fixedRule.contains("Timeout")) {
									int roll2 = random.nextInt(TERMINATION_LIMIT_PARAM) + 500;
									nParam += roll2;
								}
								else{
									// roll dice to see how high the new limit is
									int roll2 =random.nextInt(TERMINATION_LIMIT_PARAM);
									nParam += roll2;
								}
							} else {
								// pick a new parameter
								nParam = terminationParams[random.nextInt(terminationParams.length)] + "=";
								// insert a sprite
								String nSprite = usefulSprites.get(random.nextInt(usefulSprites.size()));
								nParam += nSprite;
							}
							fixedRule += nParam + " ";
						}
					}
					termination.set(point, fixedRule);
				}
			    // remove weird space from the arrayList
				termination.removeIf(s -> s == null);
			    // stream the list back into itself to avoid duplicate rules from having been created
				termination = (ArrayList<String>) termination.stream().distinct().collect(Collectors.toList());
			
				
			} 
			// modify a rule, but leave the parameters and sprites
			else {
				String newRule = terminations[random.nextInt(terminations.length)];
				String modRule = termination.get(point);
				
				String[] splitModRule = modRule.split("\\s+");
				// replace old rule with new one
				splitModRule[0] = newRule;
				newRule = "";
				for(String part : splitModRule) {
					newRule += part + " ";
				}
				termination.set(point, newRule);
				
				
			}
		} 
		// we should never ever reach this point
		else {
			System.err.println("What?! Howd we even get here!?");
		}
		
    }
    public Mutant Mutate(int mutationAmount){
        Mutant copy = new Mutant(game, level); 
        copy.mutate_level(mutationAmount);
        copy.updateLevel(); 
        copy.mutate_game(5);
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
    public void mutateInteraction(){
        double mutationType = random.nextDouble();
		// we do an insertion
		if(mutationType < 0.33) {
			// roll dice to see if we will insert a new rule altogether or a new parameter into an existing rule
			double roll = random.nextDouble();
			// insert a new parameter onto an existing rule
			if(roll < 0.2) {
				// grab a random existing rule
				int point = random.nextInt(interaction.size());
				String addToMe = interaction.get(point);
                interaction.remove(addToMe);
				// insert a new parameter into it
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
				addToMe += " " + nParam;
				// replace the old rule with the modified one
				interaction.add(addToMe); 
			}
			// insert an entirely new rule, possibly with a parameter in it
			else {
				String nInteraction = interactions[random.nextInt(interactions.length)];
				int i1 = random.nextInt(usefulSprites.size());
			    int i2 = (i1 + 1 + random.nextInt(usefulSprites.size() - 1)) % usefulSprites.size();
			    
			    String newInteraction = usefulSprites.get(i1) + " " + usefulSprites.get(i2) + " > " + nInteraction;
			    // roll to see if you insert a parameter into this interaction
			    roll = random.nextDouble();
			    
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
			    // add the new interaction to the interaction set
			    interaction.add(newInteraction);
			    // remove weird space from the arrayList
			    interaction.removeIf(s -> s == null);
			    // stream the list back into itself to avoid duplicate rules from having been created
				interaction = (ArrayList<String>) interaction.stream().distinct().collect(Collectors.toList());
				
			}
		} 
		// we do a deletion
		else if(mutationType < 0.33 + 0.33) {
			// roll dice to see if we will delete a rule altogether or a parameter of an existing rule
			double roll = random.nextDouble();
			// delete a parameter from an existing rule
			if(roll < 0.5) {
				int point = random.nextInt(interaction.size());
				String deleteFromMe = interaction.get(point);
				// find all parameters for this rule, note: there may be none.  In that case we do nothing.
				String[] splitDeleteFromMe = deleteFromMe.split("\\s+");
				ArrayList<String> params = new ArrayList<String>();
				for(String param : splitDeleteFromMe) {
					// we can assume that if one of the split strings contains an = sign that it is a parameter
					if(param.contains("=")){
						params.add(param);
					}
				}
				// if no params do nothing
				if(params.size() == 0) {
					
				} 
				// if one param, remove it
				else if(params.size() == 1) {
					String fixedRule = "";
					for(String part : splitDeleteFromMe) {
						if(!part.contains("=")) {
							fixedRule += part + " ";
						}
					}
					interaction.set(point, fixedRule);
				}
				else {
					// pick one of the rules and don't include it, but include the others
					int rule = random.nextInt(params.size());
					String fixedRule = "";
					for(String part : splitDeleteFromMe) {
						if(!part.equals(params.get(rule))) {
							fixedRule += part + " ";
						}
					}
					interaction.set(point, fixedRule);
				}
			    // remove weird space from the arrayList
			    interaction.removeIf(s -> s == null);
			    // stream the list back into itself to avoid duplicate rules from having been created
				interaction = (ArrayList<String>) interaction.stream().distinct().collect(Collectors.toList());
				
			}
			// delete an entire rule from the interaction set
			else{
				int point = random.nextInt(interaction.size());
				// dont try to delete from an empty interaction set
				if (interaction.size() > 1) {
					interaction.remove(point);
				}
			    // remove weird space from the arrayList
			    interaction.removeIf(s -> s == null);
			    // stream the list back into itself to avoid duplicate rules from having been created
				interaction = (ArrayList<String>) interaction.stream().distinct().collect(Collectors.toList());
				
			}
		} 
		// modify a rule from the interaction set by changing its parameters
		else if (mutationType < 0.2 + 0.33 + 0.33) {
			// pick our modified rule
			int point = random.nextInt(interaction.size());
			
			// roll to see what kind of modification, either a rule change or a parameter change
			double roll = random.nextDouble();
			// modify a parameter of a rule completely
			if(roll < 0.5) {
				String modifyFromMe = interaction.get(point);
				// find all parameters for this rule, note: there may be none.  In that case we do nothing.
				String[] splitModifyFromMe = modifyFromMe.split("\\s+");
				ArrayList<String> ps = new ArrayList<String>();
				for(String param : splitModifyFromMe) {
					// we can assume that if one of the split strings contains an = sign that it is a parameter
					if(param.contains("=")){
						ps.add(param);
					}
				}
				// if no params do nothing
				if(ps.size() == 0) {
					
				} else {
					// pick one of the rules and don't include it, but include the others
					int rule = random.nextInt(ps.size());
					String fixedRule = "";
					for(String part : splitModifyFromMe) {
						if(!part.equals(ps.get(rule))) {
							fixedRule += part + " ";
						} 
						// we are on the parameter we want to replace
						else {
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
							
							fixedRule += nParam + " ";
						}
					}
					interaction.set(point, fixedRule);
				}
			    // remove weird space from the arrayList
			    interaction.removeIf(s -> s == null);
			    // stream the list back into itself to avoid duplicate rules from having been created
				interaction = (ArrayList<String>) interaction.stream().distinct().collect(Collectors.toList());
				
			} 
			// modify a rule, but leave the parameters and sprites
			else {
				String newRule = interactions[random.nextInt(interactions.length)];
				String modRule = interaction.get(point);

                interaction.remove(modRule); 
				
				String[] splitModRule = modRule.split("\\s+");
				// replace old rule with new one
				splitModRule[3] = newRule;
				newRule = "";
				for(String part : splitModRule) {
					newRule += part + " ";
				}
				interaction.add(newRule);
			}
        }
    }
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
            float roll = random.nextFloat(); 
            // add a interaction rule 
            if(roll < 0.50){
                mutateInteraction();
            }else if (roll < 0.85){
                mutateTermination();
            }else{
				changeAvatar();
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
		sprites = new ArrayList<String>();

        boolean inTerminat = false; 
        boolean inInteration = false; 
		boolean inSprites = false; 
        String tabTemplate = "    ";
        int lastIndex = -1; 
    
        try {
            while((line = reader.readLine()) != null) {
                if (inTerminat || inInteration ||inSprites){
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
                            }else if (inTerminat){
                                termination.add(content); 
                        
                            }else if (inSprites){
                                sprites.add(content); 
                        
                            }
                            
                        }else if (lastIndex > index){ //de-indended 
                            inInteration = false; 
                            inTerminat = false; 
							inSprites = false; 
                        }else{
							if (lastIndex < index){
								String indent = new String(new char[index - lastIndex]).replace('\0', ' ');
								content = indent + content; 
							}
                            if (inInteration){
                                interaction.add(content);
                        
                            }else if (inTerminat){
                                termination.add(content); 
                                
                            }else if (inSprites){
                                sprites.add(content); 
                        
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
				else if(line.contains("SpriteSet")){
                    inSprites = true; 
                    String content = line.trim();
                   
                    char firstChar = content.charAt(0);
                    // figure out the indent of the line.
                    spriteIndent = line.indexOf(firstChar);
                }
                else if (!inTerminat && !inInteration && !inSprites){
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
		String spriteSpace = new String(new char[interactionIndent]).replace('\0', ' ');

        try {
            FileWriter myWriter = new FileWriter(file);
            //System.out.println(stableGame);
            myWriter.write(stableGame);
			myWriter.write(interactionSpace + "SpriteSet\n");
            for(String line: sprites){
                myWriter.write(spriteSpace + "\t" + line + "\n"); 
            }
			
            myWriter.write("\n" + interactionSpace + "InteractionSet\n");
            for(String line: interaction){
                myWriter.write(interactionSpace + "\t" + line + "\n"); 
            }
            myWriter.write("\n" + interactionSpace + "TerminationSet\n");
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
