package tracks.mutantShopping;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import tracks.ArcadeMachine;
import tracks.levelGeneration.LevelGenMachine;

public class GameBlender {
    Random random = new Random();

    ArrayList<String> fromSprites;
    ArrayList<String> fromInteractions;
    ArrayList<String> fromTermination;
    ArrayList<String> fromLevelMapping;
    ArrayList<String> addedFromSprites;
    ArrayList<String> addedFromInteractions;
    ArrayList<String> addedFromTerminations; 
    ArrayList<String> addedFromLevelMapping; 

    String stableFrom;

    ArrayList<String> toSprites;
    ArrayList<String> toInteractions;
    ArrayList<String> toTermination;
    ArrayList<String> toLevelMapping;
    ArrayList<String> addedToSprites;
    ArrayList<String> addedToInteractions;
    ArrayList<String> addedToTerminations; 
    ArrayList<String> addedToLevelMapping; 
    String stableTo;

    HashMap<String, String> spriteGrouping;

    HashMap<String, ArrayList<String>> converter;

    public GameBlender(String gameFrom, String gameTo) {
        ArrayList<ArrayList<String>> fromParts = parseInteractions(gameFrom);
        fromSprites = removeNestedSprites(fromParts.get(0));
        fromInteractions = fromParts.get(1);
        fromTermination = fromParts.get(2);
        fromLevelMapping = fromParts.get(3);
        stableFrom = fromParts.get(4).get(0);
        addedFromSprites = new ArrayList<String>();
        addedFromInteractions = new ArrayList<String>();
        addedFromTerminations = new ArrayList<String>();
        addedFromLevelMapping = new ArrayList<String>();

        ArrayList<ArrayList<String>> toParts = parseInteractions(gameTo);
        toSprites = removeNestedSprites(toParts.get(0));
        toInteractions = toParts.get(1);
        toTermination = toParts.get(2);
        toLevelMapping = toParts.get(3);
        stableTo = toParts.get(4).get(0);
        addedToSprites = new ArrayList<String>();
        addedToInteractions = new ArrayList<String>();
        addedToTerminations = new ArrayList<String>();
        addedToLevelMapping = new ArrayList<String>();

        converter = getSpriteConverter(fromSprites, toSprites);
    }

    private static String findSpriteWithName(String name, ArrayList<String> sprites) {
        for (String sprite : sprites) {
            if (sprite.split(">")[0].contains(name)) {
                return sprite;
            }
        }

        return "";
    }

    public boolean replaceOneLine(){
        ArrayList<String> possMutations = new ArrayList<String>(); 

        if(addedFromSprites.size() < toSprites.size()){
            possMutations.add("sprites");
        }

        if(addedFromInteractions.size() < toInteractions.size()){
            possMutations.add("interactions");
        }

        if(possMutations.size() == 0){
            return false; 
        }else{
            String mut = possMutations.get(random.nextInt(possMutations.size())); 

            if(mut.equals("sprites")){
                replaceSprite(); 
            }else{
                replaceInteraction();
            }

            return true; 
        }
    }

    public void replaceInteraction(){
        String toInteraction = toInteractions.get(random.nextInt(toInteractions.size())); 

        while(addedToInteractions.contains(toInteraction)){
            toInteraction = toInteractions.get(random.nextInt(toInteractions.size())); 
        }

        String[] sprites = toInteraction.split(">")[0].split(" "); 
        String sprite1 = null;
        String sprite2 = null;
        
        for(String str: sprites){
            if(str.length() > 0){
                if(sprite1 == null){
                    sprite1 = str.trim();
                }else if (sprite2 == null){
                    sprite2 = str.trim();
                }
            }
        }

        // add sprites if they haven't been added 
        if(findSpriteWithName(sprite1, fromSprites).length() <= 0 ){
            replaceSprite(findSpriteWithName(sprite1, toSprites)); 
        }

        if(findSpriteWithName(sprite2, fromSprites).length() <= 0 ){
            replaceSprite(findSpriteWithName(sprite2, toSprites)); 
        }

        
        String currentInteraction = findInteraction(sprite1, sprite2, fromInteractions); 

        if(currentInteraction.length() > 0){
            fromInteractions.remove(currentInteraction); 
        }

        fromInteractions.add(toInteraction);
        addSpritesInParams(toInteraction);
        addedFromInteractions.add(toInteraction); 
        addedToInteractions.add(toInteraction);

    }

    private void addSpritesInParams(String term){
        ArrayList<String> params = new ArrayList<String>(); 
        for(String line: term.split("\n")){
            if(line.split(">").length > 1){
                for(String param: line.split(">")[1].split(" ")){
                    params.add(param); 
                }
            }
            
        }
        

        for (String param : params) {

            if (param.contains("stype")) {

                String[] parts = param.split("=");
                String spriteToAdd = findSpriteWithName(parts[1].trim(), toSprites);
                if (spriteToAdd.length() > 0 && !addedToSprites.contains(spriteToAdd)) {
                    replaceSprite(spriteToAdd);
            
                }
            }
        }

    }
    private String findInteraction(String sprite1, String sprite2, ArrayList<String> interactions){
        for(String interaction: interactions){
            String[] sprites = interaction.split(">")[0].split(" "); 
            String s1 = null;
            String s2 = null;
            
            for(String str: sprites){
                if(str.length() > 0){
                    if(s1 == null){
                        s1 = str.trim();
                    }else if (s2 == null){
                        s2 = str.trim();
                    }
                }
            }

            if(s1.equals(sprite1) && s2.equals(sprite2)){
                return interaction; 
            }
        }

        return "";
    }

    public void replaceSprite() {
        String toSprite = toSprites.get(random.nextInt(toSprites.size()));

        while (addedToSprites.contains(toSprite)) {
            toSprite = toSprites.get(random.nextInt(toSprites.size()));
        }

        replaceSprite(toSprite);

    }

    public String replaceSprite(String toSprite) {

        String modSprite = toSprite;

        addSpritesInParams(toSprite);

        ArrayList<String> candidates = new ArrayList<String>();

        // only add candiates if they haven't already been added
        for (String sprite : converter.get(toSprite)) {
            if (!addedFromSprites.contains(sprite)) {
                candidates.add(sprite);
            }
        }
        String newName;
        // no possible candiates, must add new sprite
        if (candidates.size() == 0) {
            fromSprites.add(modSprite);
            newName = modSprite.split(">")[0].trim();
            addedFromSprites.add(modSprite);
            addedToSprites.add(toSprite);
        } else {
            String fromSprite = candidates.get(random.nextInt(candidates.size()));
            String[] parts = fromSprite.split(">");
            fromSprites.remove(fromSprite);
            convertSprite(fromSprite, toSprite);
            fromSprites.add(toSprite);

            newName = parts[0];

            addedFromSprites.add(toSprite);
            addedToSprites.add(toSprite);
        }

        converter = getSpriteConverter(fromSprites, toSprites);

        return newName;

    }

    private void renameSprite(String oldName, String newName, String ogSprite){
        // rename strings in sprite 
        ArrayList<String> newSprites = new ArrayList<String>(); 
        for (String sprite: fromSprites){
            if(!sprite.equals(ogSprite)){
                String renamed = sprite.replace(oldName, newName); 
                newSprites.add(renamed); 
            }else{
                newSprites.add(sprite);
            }
        }

        fromSprites = newSprites;


        // rename strings in interactions  
        ArrayList<String> newInteractions = new ArrayList<String>(); 
        for (String interaction: fromInteractions){
                String renamed = interaction.replace(oldName, newName); 
                newInteractions.add(renamed); 
           
        }

        fromInteractions = newInteractions; 

         // rename strings in termination 
         ArrayList<String> newTermination = new ArrayList<String>(); 
         for (String termination: fromTermination){
                 String renamed =termination.replace(oldName, newName); 
                 newTermination.add(renamed); 
            
         }

         fromTermination = newTermination;


         // rename strings in levelmapping 
         ArrayList<String> newMapping= new ArrayList<String>(); 
         for (String mapping: fromLevelMapping){
            String renamed =mapping.replace(oldName, newName); 
            newMapping.add(renamed); 
            
            
         }

    

         fromLevelMapping = newMapping;
    }

    public String getGameDescription(){
        ArrayList<String> renested = reNestSprites(fromSprites);
        return  buildGameDescription(stableFrom, renested, fromInteractions, fromTermination, fromLevelMapping);
    }

    private void convertSprite(String fromSprite, String toSprite) {
        String[] fromLines = fromSprite.split("\n");
        String[] toLines = toSprite.split("\n");

        for (String from : fromLines) {
            System.out.println(from);
        }

        if (toLines.length > fromLines.length) {
            int offset = toLines.length - fromLines.length;
            for(int i = 0; i < fromLines.length; i++){
                String oldName = fromLines[i].split(">")[0].trim(); 
                String newName = toLines[i+offset].split(">")[0].trim(); 

                renameSprite(oldName, newName, fromSprite);
            }
            
        } else if (toLines.length < fromLines.length) {
            int offset = fromLines.length - toLines.length;
            for(int i = offset; i < fromLines.length; i++){
                String oldName = fromLines[i].split(">")[0].trim(); 
                String newName = toLines[i-offset].split(">")[0].trim(); 

                renameSprite(oldName, newName, fromSprite);
            }
            
        }else{
            for(int i = 0; i < fromLines.length; i++){
                String oldName = fromLines[i].split(">")[0].trim(); 
                String newName = toLines[i].split(">")[0].trim(); 

                renameSprite(oldName, newName, fromSprite);
            }
        }

        

    }

    // for each sprite in the to sprite, get a list of candidate spries in the form
    // sprites
    public static HashMap<String, ArrayList<String>> getSpriteConverter(ArrayList<String> fromSprites,
            ArrayList<String> toSprites) {

        HashMap<String, String> groupMap = buildSpriteGroup();
        HashMap<String, ArrayList<String>> converter = new HashMap<String, ArrayList<String>>();

        for (String sprite : toSprites) {
            String group = getSpriteGroup(sprite, groupMap);

            converter.put(sprite, new ArrayList<String>());

            for (String fromSprite : fromSprites) {
                String fromGroup = getSpriteGroup(fromSprite, groupMap);
                if (group.equals(fromGroup)) {
                    converter.get(sprite).add(fromSprite);
                }
            }
        }

        return converter;

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

    public static String getSpriteGroup(String sprite, HashMap<String, String> groups) {
        String type = getSpriteType(sprite);
        if (groups.containsKey(type)) {
            return groups.get(type);
        } else {
            return "NaN";
        }
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

        return newSprites;
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
        returnLi.add(sprites);
        returnLi.add(interaction);
        returnLi.add(termination);
        returnLi.add(levelMapping);
        returnLi.add(stableList);

        return returnLi;
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

    public static void main(String[] args) {
        String file = "examples/gridphysics/aliens.txt";
        String file2 = "examples/gridphysics/zelda.txt";
        String level = "examples/gridphysics/aliens_lvl0.txt";
        String constructiveLevelGenerator = "tracks.levelGeneration.constructiveLevelGenerator.LevelGenerator";
        String geneticGenerator = "tracks.levelGeneration.geneticLevelGenerator.LevelGenerator";
        try {
            String game = getFileAsString(file);

            String game2 = getFileAsString(file2);

            ArrayList<ArrayList<String>> parts = parseInteractions(game);

            ArrayList<String> denested = GameBlender.removeNestedSprites(parts.get(0));
            for(String str: denested){
                System.out.println("NEW SPRITE"); 
                System.out.println(str);
            }

            /*ArrayList<String> denested = removeNestedSprites(parts.get(0));

            ArrayList<String> renested = reNestSprites(denested); 

            for(String sprite: renested){
                System.out.println(sprite);
            }

            GameBlender blender = new GameBlender(game, game2);

            boolean canBlend = true;
            int i = 0; 
            while(canBlend) {
                canBlend = blender.replaceOneLine();

                System.out.println(blender.getGameDescription());
                
                String recordLevelFile =  "gameBlend_" + i + "glvl.txt";
                String gameFile = "gameBlen_game_" + i +".txt"; 
                BufferedWriter writer = new BufferedWriter(new FileWriter(gameFile));
                writer.write(blender.getGameDescription());
    
                writer.close();
                if(LevelGenMachine.generateOneLevel(gameFile, constructiveLevelGenerator, recordLevelFile)){
                    LevelGenMachine.playOneGeneratedLevel(gameFile, null, recordLevelFile, 42);
                }
        

                System.out.println("\n\n\n");

                i ++;
            }*/ 

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
