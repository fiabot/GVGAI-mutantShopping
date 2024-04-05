package tracks.GameMarkovChain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import tools.Utils;
import java.util.Random;
import tracks.mutantShopping.GameBlender;
import tracks.mutantShopping.SpriteCreator;

public class MarkovChain {
    //sprite/interaction/terminan + param -> param value (for stype the sprite type)
    HashMap<String, ArrayList<String>> paramValues; 

    ArrayList<String> startingSpritesTypes; 
    ArrayList<Integer> numSprites; 
    // Previous sprite type -> to next sprite type 
    HashMap<String, ArrayList<String>> spriteType; 
    // sprite type -> list of params for sprite 
    HashMap<String, ArrayList<ArrayList<String>>> spriteParams; 
    
    ArrayList<Integer> numInteractions; 
    //sprite context -> sprite types 
    HashMap<String, ArrayList<ArrayList<String>>> interactionsPairs;
    // sprite types -> interaction type 
    HashMap<String, ArrayList<String>> interactionTypes;  
    // interaction type -> list of params for interaction
    HashMap<String, ArrayList<ArrayList<String>>> interactionParams; 

    ArrayList<Integer> numTerminations; 
    // termination context -> termination type 
    HashMap<String, ArrayList<String>> terminationTypes; 
    //termination type -> list of params for termination 
    HashMap<String, ArrayList<ArrayList<String>>> terminationParams; 


    // dimensions of levels 
    ArrayList<int[]> levelSizes; 
    // sprite context ->  base probabilities of characters 
    HashMap<String, ArrayList<Character>> characterProps; 
    // generatlized level context -> generatlized level char 
    HashMap<String, ArrayList<Character>> levelChain; 

    Random random; 

    int nextSpriteInt; 


    /*
     * Generalized characters: 
     * A-Z: for each sprite Group + 
     * w: wall sprites 
     * .: floor sprites 
     * 
     */

     public MarkovChain (){
        paramValues = new HashMap<String, ArrayList<String>> ();

        startingSpritesTypes = new ArrayList<String>(); 
        numSprites = new ArrayList<Integer>(); 
        spriteType = new HashMap<String, ArrayList<String>>();
        spriteParams = new HashMap<String, ArrayList<ArrayList<String>>>();

        numInteractions = new ArrayList<Integer>(); 
        interactionsPairs = new HashMap<String, ArrayList<ArrayList<String>>>();
        interactionTypes = new HashMap<String, ArrayList<String>>();
        interactionParams = new HashMap<String, ArrayList<ArrayList<String>>>();

        numTerminations = new ArrayList<Integer>(); 
        terminationTypes = new HashMap<String, ArrayList<String>>();
        terminationParams = new HashMap<String, ArrayList<ArrayList<String>>>();

        random = new Random(); 
     }

     private <A> A selectFromList(ArrayList<A> list){
        return list.get(random.nextInt(list.size())); 

     }

     private String addParams(String starting, String context, ArrayList<String> params,  ArrayList<String> sprites){
        HashMap<String, ArrayList<String>> spriteMap = getTypeMapping(sprites); 
        for(String param: params){
            String value = selectFromList(paramValues.get(context + " " + param)); 
            if(param.contains("stype") || param.contains("resource")){
                if(spriteMap.containsKey(value)){
                    value = selectFromList(spriteMap.get(value)); 
                }else if (spriteParams.containsKey(value)){
                    String newSprite = buildSpriteFromType("sprite" + nextSpriteInt, value, sprites); 
                    nextSpriteInt ++; 
                    sprites.add(newSprite); 
                    spriteMap = getTypeMapping(sprites); 
                }
            }
            
            starting += param + "=" + value + " "; 
        }
        return starting; 
     }
     private String buildSpriteFromType(String name, String type, ArrayList<String> sprites){
        String sprite = name + " > " + type + " "; 
        if(!spriteParams.containsKey(type)){
            System.out.println(type);
        }
        ArrayList<String> params = selectFromList(spriteParams.get(type)); 
       
        return  addParams( sprite, type, params, sprites);
     }

     private HashMap<String, ArrayList<String>> getTypeMapping(ArrayList<String> sprites){
        HashMap<String, ArrayList<String>> returnMap = new HashMap<String, ArrayList<String>>();

        for(String sprite: sprites){
            String type = getSpriteType(sprite); 
            if (! returnMap.containsKey(type)){
                returnMap.put(type, new ArrayList<String>()); 
            }
            String name = sprite.split(">")[0].strip(); 
            returnMap.get(type).add(name); 
        }


        return returnMap; 
     }

     private ArrayList<String> selectSpriteTypes(String spriteContext){
        if (interactionsPairs.containsKey(spriteContext)){
            return selectFromList(interactionsPairs.get(spriteContext)); 
        }else{ 
            //TODO: find closest vector 
            String randomContext = selectFromList(new ArrayList<String> (interactionsPairs.keySet())); 
            return selectFromList(interactionsPairs.get(randomContext));
        }
     }

     private String selectTerminationType(String interactionContext){
        if (terminationTypes.containsKey(interactionContext)){
            return selectFromList(terminationTypes.get(interactionContext)); 
        }else{ 
            //TODO: find closest vector 
            String randomContext = selectFromList(new ArrayList<String> (terminationTypes.keySet())); 
            return selectFromList(terminationTypes.get(randomContext));
        }
     }

     private String getInteractionStarter(ArrayList<String> types, HashMap<String, ArrayList<String>> spriteMap){
        String returnStr = ""; 

        for(String type: types){
            if(spriteMap.containsKey(type)){
                String name = selectFromList(spriteMap.get(type)); 
                returnStr += name + " ";
            }else{
                returnStr += type;
            }
            
        }


        return returnStr;
     }

     private String buildInteraction(String starter, String type, ArrayList<String> sprites){
        String returnStr = starter + " > " + type + " "; 
        ArrayList<String> params = selectFromList(interactionParams.get(type)); 
        return addParams(returnStr, type, params, sprites); 
     }

    public String buildGame(){
        nextSpriteInt = 0;
        //1: build sprite set 
            ArrayList<String> sprites = new ArrayList<String>(); 
            // 1.1: choose intial sprite 
            String lastSpriteType = selectFromList(startingSpritesTypes); 
            sprites.add(buildSpriteFromType("sprite" + nextSpriteInt, lastSpriteType, sprites)); 

            // 1.2: select number of sprites 
            int amountOfSprites = selectFromList(numSprites); 
            // 1.3 For each sprite:     
            for(int i =1; i < amountOfSprites; i ++){
                // choose type from previous sprite 
                String nextType = selectFromList(spriteType.get(lastSpriteType)); 
                // choose params from type 
                // choose values from type and param 

                sprites.add(buildSpriteFromType("sprite" + nextSpriteInt, nextType, sprites));
                nextSpriteInt ++; 
                lastSpriteType = nextType;
            }

        
        // 2: build interaction set 
        ArrayList<String> interactions = new ArrayList<String>();
        String spriteContext = getSpriteContext(sprites); 
        HashMap<String, ArrayList<String>> spriteMap =getTypeMapping(sprites); 
        // 2.1 select number of interactions 
        int interactionSize = selectFromList(numInteractions); 
            // 2.2 for each interaction 
            for(int i = 0; i < interactionSize; i ++){
                // select sprite types based on sprite context  
                ArrayList<String> spriteTypes = selectSpriteTypes(spriteContext); 
                        // if context doesn't exist, find closest 
                // add any new sprites if need be 
             
                for(String type: spriteTypes){
                    if(!spriteMap.containsKey(type) && spriteParams.containsKey(type)){
                        sprites.add(buildSpriteFromType("sprite" + nextSpriteInt,type, sprites)); 
                        spriteMap =getTypeMapping(sprites); 
                        nextSpriteInt++; 
                    }
                }
                // replace sprite types with real names   
                String interactionSeed = getInteractionStarter(spriteTypes, spriteMap);  
                        
                // select interaction type based on sprites 
                String type  = selectFromList(interactionTypes.get(arrayToString(spriteTypes))); 
                String interaction = buildInteraction(interactionSeed, type, sprites); 
                
                interactions.add(interaction);

                // in case sprites have changed 
                spriteMap =getTypeMapping(sprites); 
                spriteContext = getSpriteContext(sprites); 
            }
        
            for (String interaction: interactions){
                System.out.println(interaction);
            }
        // 3: Build termination set 
        String interactionContext = getInteractionContext(interactions); 
        ArrayList<String> terminations = new ArrayList<String>(); 
            // 3.1 select number of interactions 
            int terminationSize = selectFromList(numTerminations);
            // 3.2 for each interaction 
            for(int i =0; i < terminationSize; i ++){
                // select interaction type based on termination context 
                String type = selectTerminationType(interactionContext);
                
                // select params baased on termination type 
                ArrayList<String> params = selectFromList(terminationParams.get(type));
                
                // select values based on type and param 
                String termination = addParams(type + " ",  type, params, sprites); 
                terminations.add(termination); 
            }
        // 4: repair 
        // 5: Build Level mapping 
            // 4.1 for each sprite 
                // add a level mapping with next char in char list 
                    // if there is a floor sprite, add that before each char 
        
        // 6: Build level 
            // 5.1 Select Level Demensions 
            // 5.2 intialize level based on base probabilities 
            // 5.3 for n cycles swap characters as done previously 
        
        //7: change wall and floor sprites to Immovable 
        

        return buildGameDescription("BasicGame \n", sprites, interactions, terminations, terminations); 
    }

    /*
     * get the param names and values from a rule 
     * The first arraylist will be the names, the second will be the values 
     */
    private ArrayList<ArrayList<String>> getParams(String rule, ArrayList<String> sprites){

        ArrayList<ArrayList<String>> params = new  ArrayList<ArrayList<String>>();
        params.add(new ArrayList<String>()); 
        params.add(new ArrayList<String>()); 

        String[] words = rule.split(" "); 

        for (String word : words){
            if (word.contains("=")){
                word = word.strip(); 
                String[] paramParts = word.split("="); 
                params.get(0).add(paramParts[0]);

                String value = paramParts[1]; 

                if(paramParts[0].contains("stype") || paramParts[0].contains("resource")){
                    String spriteName = getSpriteType(findSpriteWithName(value, sprites));
                    if(spriteName != ""){
                        value = spriteName;
                    }
                }
                params.get(1).add(value); 
            }
        }


        return params; 

    }

    /**
     * Update the param hashmap given a context (ex: sprite type), param names, and their values 
     * @param context context of rule, either sprite, interaction, or termination type 
     * @param paramNames an list of the names of params in this rule
     * @param values a list of values of params in same order as names 
     */
    private void updateParams(String context, ArrayList<String> paramNames, ArrayList<String> values){
        // TODO change stype params to general types 
        for(int i =0; i < paramNames.size(); i ++){
            String newContext = context + " " + paramNames.get(i); 
            if (!paramValues.containsKey(newContext)){
                paramValues.put(newContext, new ArrayList<String>()); 
            }
            paramValues.get(newContext).add(values.get(i));
        }
    }

    private int[] getSpriteVector(ArrayList<String> sprites){
        //SpriteContext:[avatar, npc, spawner, ammos, others]
        int[] vector = {0, 0,0,0,0}; 
        HashMap<String, String> spriteGroups = buildSpriteGroup(); 

        for(String sprite: sprites){
            String type = getSpriteType(sprite); 
            String group = spriteGroups.get(type); 
            if(group == "avatar"){
                vector[0] += 1; 
            }else if (group == "npc"){
                vector[1] += 1; 
            }else if (group == "spawner"){
                vector[2] += 1;
            }else if (group == "ammo"){
                vector[3] += 1; 
            }else {
                vector[4] += 1;  
            }
        }
        return vector; 
    }

    private String intArrToString(int[] arr){
        String returnStr = ""; 
        for(int i: arr){
            returnStr += i + ",";
        }
        return returnStr; 
    }

    private String getSpriteContext(ArrayList<String> sprites){
        int[] vector = getSpriteVector(sprites); 
        return intArrToString(vector); 
    }

    private int[] getInteractionsVector(ArrayList<String> interactions){
        //  [kill, spawn, transform, health, movement, direction]
        int[] vector = {0,0,0,0,0,0};

        for(String interaction: interactions){
            String type = getInteractinType(interaction); 
            type = type.toLowerCase();

            if(type.contains("kill")){
                vector[0] += 1;
            }else if (type.contains("spawn")){
                vector[1] += 1; 
            }else if (type.contains("transform")){
                vector[2] += 1; 
            }else if ((type.contains("health")) || (type.contains("resource"))){
                vector[3] += 1; 
            } else if ((type.contains("speed")) || (type.contains("step")) || (type.contains("undo")) || (type.contains("wrap")) || (type.contains("teleport")) || (type.contains("pull")) || (type.contains("bounce"))){
                vector[4] += 1; 
            } else{
                vector[5] += 1; 
            }

        }

        return vector;
    }

    private String getInteractionContext(ArrayList<String> interacitons){
        return intArrToString(getInteractionsVector(interacitons)); 
    }

    private ArrayList<String> getSpriteTypes(String interaction, ArrayList<String> sprites){
        String spritString = interaction.split(">")[0]; 
        String[] includedSprites = spritString.split(" ");
        ArrayList<String> types = new ArrayList<String>(); 

        for (String sprite: includedSprites){
            if(!sprite.isEmpty() && !sprite.isBlank()){
                String type = getSpriteType(findSpriteWithName(sprite, sprites));
                if (type == ""){
                    type = sprite; 
                }
                types.add(type);
            }
         
        }
        return types; 
    }

    private String arrayToString(ArrayList<String> strs){
        String str = ""; 

        for(String s: strs){
            str += s + " "; 
        }

        return str;
    }

    private static String findSpriteWithName(String name, ArrayList<String> sprites) {
        
        for (String sprite : sprites) {
          
            String[] lines = sprite.split("\n"); 

            for(String line: lines){
                if (line.split(">")[0].contains(name)) {
               
                    return sprite;
                }
            }
          
        }

        return "";
    }

    private String getInteractinType(String interaction){
        String section = interaction.split(">")[1];
        String[] words = section.split(" "); 
        int i =0; 
        while(words[i].isBlank()){
            i ++; 
            
        }
        return words[i].strip();
    }

    private String getTerminationType(String termination){
        String[] words = termination.split(" "); 
        int i =0; 
        while(words[i].isBlank()){
            i ++; 
            
        }
        return words[i].strip();
    }
    public void trainFromCsvFile(String csvPath) throws IOException{
        String[][] games = Utils.readGames(csvPath);
        for(String[] game: games){
            String file = game[0]; 
            String desc = getFileAsString(file); 
            trainOneExample(desc, new String[0]);
        }

    }

    /**
     * Update hashmaps based on single example 
     * @param example
     * @param levels
     */
    public void trainOneExample(String example, String[] levels){
        // 1: get sprites, terminations, and interactions from example 
        ArrayList<ArrayList<String>> parts = parseInteractions(example);
        ArrayList<String> sprites = removeNestedSprites(parts.get(0)); 
        ArrayList<String> interactions = parts.get(1); 
        ArrayList<String> terminations = parts.get(2); 
        ArrayList<String> levelMapping = parts.get(3); 

        // 2: update sprite hashmaps 
            // 2.1 denest sprites (so each sprite contains it's parents)
            // 2.2 add the first sprite type to first list 
            startingSpritesTypes.add(getSpriteType(sprites.get(0)));
      
            // 2.3 add the number of sprites to list 
            numSprites.add(sprites.size());

            String lastSprite = getSpriteType(sprites.get(sprites.size() - 1));
            // 2.4 for each sprite 
            for(String sprite: sprites){
                if(sprite == ""){
                    continue;
                }
                String currentType = getSpriteType(sprite); // TODO: change type to wall or floor if applicable 
                // add current sprite type using previous type as context 
                if (!spriteType.containsKey(lastSprite)){ // add key if needed 
                    spriteType.put(lastSprite, new ArrayList<String>());
                }
                spriteType.get(lastSprite).add(currentType);
                    
                
                // add list of params given sprite context 
                ArrayList<ArrayList<String>> params = getParams(sprite, sprites); 

                if (!spriteParams.containsKey(currentType)){ // add key if needed 
                    spriteParams.put(currentType, new ArrayList<ArrayList<String>>());
                }
                spriteParams.get(currentType).add(params.get(0));
                // update param hashmap based on values 
                updateParams(currentType, params.get(0), params.get(1));

                lastSprite = currentType;

                }
        
        // 3: update interaction hashmaps 
            // 3.1 get sprite context (counts of sprite types)
            String spriteContext = getSpriteContext(sprites); 

            // 3.2 add number of interactions to list 
            numInteractions.add(interactions.size()); 

            // 3.3 For each interaction 
            for(String interaction: interactions){
                // add sprite types based on sprite context 
                ArrayList<String> interactionSpriteTypes = getSpriteTypes(interaction, sprites); 
                if(!interactionsPairs.containsKey(spriteContext)){
                    interactionsPairs.put(spriteContext, new ArrayList<ArrayList<String>>()); 
                }
                interactionsPairs.get(spriteContext).add(interactionSpriteTypes); 

                // add interaction type based on sprite types 
                String typeContext = arrayToString(interactionSpriteTypes); 
                String interactionType = getInteractinType(interaction); 
                if(!interactionTypes.containsKey(typeContext)){
                    interactionTypes.put(typeContext, new ArrayList<String>()); 
                }
                interactionTypes.get(typeContext).add(interactionType);

                // add list of params based on interaction type 
                ArrayList<ArrayList<String>> params = getParams(interaction, sprites); 

                if (!interactionParams.containsKey(interactionType)){ // add key if needed 
                    interactionParams.put(interactionType, new ArrayList<ArrayList<String>>());
                }
                interactionParams.get(interactionType).add(params.get(0));

                // update param values 
                updateParams(interactionType, params.get(0), params.get(1));
            }
        
        // 4: update termination hashmaps 
            // 4.1 get interaction context 
            String interactionConext = getInteractionContext(interactions); 
            // 4.2 add number of terminations to list 
            numTerminations.add(terminations.size()); 
            // 4.3 for each termination 
            for(String termination: terminations){
                // add termination type based on interaction context 
                String termType = getTerminationType(termination);
                if(!terminationTypes.containsKey(interactionConext)){
                    terminationTypes.put(interactionConext, new ArrayList<String>()); 
                }
                terminationTypes.get(interactionConext).add(termType);
                // add list of params based on termination type
                ArrayList<ArrayList<String>> params = getParams(termination, sprites); 

                if (!terminationParams.containsKey(termType)){ // add key if needed 
                    terminationParams.put(termType, new ArrayList<ArrayList<String>>());
                }
                terminationParams.get(termType).add(params.get(0));

                // update param values 
                updateParams(termType, params.get(0), params.get(1));
            }
        
        // 5: update level hashmaps 
            // for each level 
                // 5.1 generalize level characters 
                // 5.2 add deminsions to base 
                // 5.3 add base probabilities based on sprite context 
                // 5.4 add neighbor information 
    }

    public String manualRepair(String game){
        // 1. check if there is an avatar sprite 
            // if not add one 
        // 2. for each termination 
            // If timeout, ignore 
            // If less then sprite counter 
                // check if sprite can be removed 
                // if not do one of the following 
                        // add a interaction that removes sprite 
                        // change to a sprite that is removed 
            // if greater then sprite counter 
                // check if sprite cann be added 
                // if not do one of the following 
                    // add an interaction that adds sprite 
                    // change to a sprite that is added 
            // if equal to sprite counter 
                // if value is 0, do the same as less then 
                // other wise do both less then and greater then 
        // 3. if avatar is a shooter 
            // if avatar bullet is not in interaction set > add an interaction 
        return game; 
    }
    

    public String mutateGame(String Game){
        // choose mutation type 
            // 1 mutate sprites 
                // 1.1 add sprite 
                    // add sprite as done in game generation 
                // 1.2 modifify sprite 
                    // 1.2.1 change sprite type 
                    // 1.2.2 change sprite params 
                    // 1.2.3 change param value 
                // 1.3 remove sprite 
            // 2 mutate interaction 
                // 2.1 add interaction 
                // 2.2 modify interaction 
                    // 2.2.1 change sprites in interaction 
                    // 2.2.2 change interaction type 
                    // 2.2.3 change params 
                    // 2.2.4 change param values 
                // 2.3 delete interaction 
            // 3 mutate terminations 
                // 3.1 add termination 
                // 3.2 modify termination 
                    // 3.2.1 change termination type 
                    // 3.2.2 change termination params 
                    // 3.2.3 change param value 

         return "";
    }
    public static String getSpriteType(String sprite) {
        for (String line : sprite.split("\n")) {
            String[] parts = line.split(">");
            if (parts.length > 1) {
                for (String part : parts[1].split(" ")) {
                    part = part.trim();
                    if (part.length() > 0) {

                        if (Character.isUpperCase(part.charAt(0))) {
                            return part;
                        }

                        break;
                    }
                }
            }

        }

        return "";
    }
    private static ArrayList<String> removeNestedSprites(ArrayList<String> spriteList, int startingIndent) {
        ArrayList<String> newSprites = new ArrayList<String>();
        ArrayList<String> children = new ArrayList<String>();
        String lastSprite = "";

        for (String sprite : spriteList) {
            if (sprite.length() > 0) {

                String content = sprite.trim();
                char firstChar = content.charAt(0);
                // figure out the indent of the line.
                int spriteIndex = sprite.indexOf(firstChar);

                // sprite is nested
                if (spriteIndex > startingIndent) {
                    children.add(sprite);

                } else {
                    if (children.size() > 0) {
                        content = children.get(0).trim();
                        firstChar = content.charAt(0);
                        // figure out the indent of the line.
                        int index = children.get(0).indexOf(firstChar);
                        ArrayList<String> toAdd = removeNestedSprites(children, index);

                        for (String s : toAdd) {
                            if (s.length() > 0) {
                                s = lastSprite + "\n" + s;
                                newSprites.add(s);
                            }

                        }
                    } else {
                        newSprites.add(lastSprite);

                    }
                    children = new ArrayList<String>();
                    lastSprite = sprite;

                }

            }

        }
        if (lastSprite.length() > 0) {

            if (children.size() > 0) {
                String content = children.get(0).trim();
                char firstChar = content.charAt(0);
                // figure out the indent of the line.
                int index = children.get(0).indexOf(firstChar);

                ArrayList<String> toAdd = removeNestedSprites(children, index);

                for (String s : toAdd) {
                    if (s.length() > 0) {
                        s = lastSprite + "\n" + s;
                        newSprites.add(s);
                    }
                }
            } else if (lastSprite.length() > 0) {
                newSprites.add(lastSprite);

            }

        }

        return  cleanList(newSprites);
    }

    public static ArrayList<String> removeNestedSprites(ArrayList<String> spriteList) {
        return removeNestedSprites(spriteList, 0);
    }

    public static ArrayList<String> reNestSprites(ArrayList<String> spriteList) {
        ArrayList<String> returnli = new ArrayList<String>();
        HashMap<String, ArrayList<String>> parents = new HashMap<String, ArrayList<String>>();

        for (String sprite : spriteList) {
            String[] elements = sprite.split("\n");
            if (elements.length == 1) {
                returnli.add(sprite.replaceAll("\n", ""));
            } else {
                String child = "";
                for (int c = 1; c < elements.length; c++) {
                    child += elements[c] + "\n";
                }

                if (!parents.containsKey(elements[0])) {
                    parents.put(elements[0], new ArrayList<String>());
                }

                parents.get(elements[0]).add(child);
            }
        }

        for (String parent : parents.keySet()) {
            returnli.add(parent);
            ArrayList<String> children = reNestSprites(parents.get(parent));
            for (String child : children) {
                returnli.add(child);
            }
        }

        return returnli;
    }

    public static ArrayList<ArrayList<String>> parseInteractions(String game) {
        String[] lines = game.split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        ArrayList<String> interaction = new ArrayList<String>();
        ArrayList<String> termination = new ArrayList<String>();
        ArrayList<String> sprites = new ArrayList<String>();
        ArrayList<String> levelMapping = new ArrayList<String>();

        boolean inTerminat = false;
        boolean inInteration = false;
        boolean inSprites = false;
        boolean inMap = false;
        String tabTemplate = "    ";
        int lastIndex = -1;

        ArrayList<String> spriteParent = new ArrayList<String>();

        for (String line : lines) {
            if (inTerminat || inInteration || inSprites || inMap) {
                line.replaceAll("\t", tabTemplate);
                // remove comments starting with "#"
                if (line.contains("#"))
                    line = line.split("#")[0];

                // handle whitespace and indentation
                String content = line.trim();
                if (content.length() > 0) {
                    char firstChar = content.charAt(0);
                    // figure out the indent of the line.
                    int index = line.indexOf(firstChar);

                    if (lastIndex == -1 && content.length() > 0) {

                        lastIndex = index;
                        if (inInteration) {
                            interaction.add(content);
                        } else if (inTerminat) {
                            termination.add(content);

                        } else if (inSprites) {
                            sprites.add(content);

                        } else if (inMap) {
                            levelMapping.add(content);

                        }

                    } else if (lastIndex > index) { // de-indended
                        inInteration = false;
                        inTerminat = false;
                        inSprites = false;
                        inMap = false;
                    } else {
                        if (lastIndex < index) {
                            String indent = new String(new char[index - lastIndex]).replace('\0', ' ');
                            content = indent + content;
                        }
                        if (inInteration) {
                            interaction.add(content);

                        } else if (inTerminat) {
                            termination.add(content);

                        } else if (inSprites) {
                            sprites.add(content);

                        } else if (inMap) {
                            levelMapping.add(content);

                        }
                    }
                }

            }
            if (line.contains("TerminationSet")) {
                inTerminat = true;
                String content = line.trim();

                char firstChar = content.charAt(0);
                // figure out the indent of the line.
                // terminationIndent = line.indexOf(firstChar);

            }

            else if (line.contains("InteractionSet")) {
                inInteration = true;
                String content = line.trim();

                char firstChar = content.charAt(0);
                // figure out the indent of the line.
                // interactionIndent = line.indexOf(firstChar);
            } else if (line.contains("SpriteSet")) {
                inSprites = true;
                String content = line.trim();

                char firstChar = content.charAt(0);
                // figure out the indent of the line.
                // spriteIndent = line.indexOf(firstChar);
            } else if (line.contains("LevelMapping")) {
                inMap = true;
                // String content = line.trim();

                // char firstChar = content.charAt(0);
                // figure out the indent of the line.
                /// spriteIndent = line.indexOf(firstChar);
            } else if (!inTerminat && !inInteration && !inSprites && !inMap) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

        }

        String stableGame = stringBuilder.toString();
        ArrayList<String> stableList = new ArrayList<String>();
        stableList.add(stableGame);

        ArrayList<ArrayList<String>> returnLi = new ArrayList<ArrayList<String>>();
        returnLi.add(cleanList(sprites));
        returnLi.add(interaction);
        returnLi.add(termination);
        returnLi.add(levelMapping);
        returnLi.add(stableList);

        return returnLi;
    }

    private static  ArrayList<String> cleanList(ArrayList<String> list){
        ArrayList<String> newList = new ArrayList<String>();

        for (String str: list){
            if (str!= null && !str.isBlank() && !str.isEmpty() && str.length()!=0){
                newList.add(str);
            }
        }

        return newList;
    }
    public static String getFileAsString(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        // delete the last new line separator
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();

        String content = stringBuilder.toString();
        return content;
    }

    private static int getIndent(String str){
        String content = str.trim();
            
        char firstChar = content.charAt(0);
        // figure out the indent of the line.
        int index = str.indexOf(firstChar);
        return index;
    }

    public static String buildGameDescription(String stableGame, ArrayList<String> sprites, ArrayList<String> interaction, ArrayList<String> termination, ArrayList<String> levelMapping){
        //String file = "src/tracks/mutantShopping/MutantGames/mutant_" + Integer.toString(id) + "_game.txt"; 
        
        
        
        
        String interactionSpace = new String(new char[4]).replace('\0', ' ');
        String terminationSpace = new String(new char[4]).replace('\0', ' ');
		String spriteSpace = new String(new char[4]).replace('\0', ' ');
		StringBuilder stringBuilder = new StringBuilder();
		//System.out.println(stableGame);
		stringBuilder.append(stableGame);
		stringBuilder.append(interactionSpace + "SpriteSet\n");
		for(String line: sprites){
			stringBuilder.append(spriteSpace + "\t" + line + "\n"); 
		}
		stringBuilder.append("\n" +  interactionSpace + "LevelMapping\n");
		for(String line: levelMapping){
			stringBuilder.append(spriteSpace + "\t" + line + "\n"); 
		}
		
		stringBuilder.append("\n" + interactionSpace + "InteractionSet\n");
		for(String line: interaction){
			stringBuilder.append(interactionSpace + "\t" + line + "\n"); 
		}
        
		stringBuilder.append("\n" + interactionSpace + "TerminationSet\n");
		for(String line: termination){
			stringBuilder.append(terminationSpace + "\t" + line + "\n"); 
		}
        stringBuilder.append(terminationSpace + "\t" + "Timeout win=True limit=50" + "\n");
	

          return stringBuilder.toString();
        }


        public static HashMap<String, String> buildSpriteGroup() {
            HashMap<String, String> group = new HashMap<String, String>();
    
            for (String sprite : SpriteCreator.avatars) {
                group.put(sprite, "avatar");
            }
    
            for (String sprite : SpriteCreator.npcs) {
                group.put(sprite, "npc");
            }
    
            for (String sprite : SpriteCreator.ammos) {
                group.put(sprite, "ammo");
            }
    
            for (String sprite : SpriteCreator.spawners) {
                group.put(sprite, "spawner");
            }
    
            for (String sprite : SpriteCreator.others) {
                group.put(sprite, "other");
            }
    
            return group;
        }
}
