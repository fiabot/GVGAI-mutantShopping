package tracks.GameMarkovChain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


import tracks.levelGeneration.LevelGenMachine;

public class GrammarMutant extends MutantInterface{
    String game; 
    String level; 
    MarkovChain chain; 
    Random random; 
    int LEVEL_TRIALS = 10; 

    public GrammarMutant(String game, String level, MarkovChain chain){
        this.chain  = chain; 
        this.level = level; 
        this.game = game; 
        this.random = chain.random; 
        setUp();
    }

    public GrammarMutant(String game, MarkovChain chain){
        this.game = game;
        this.chain  = chain; 
        this.random = chain.random; 
        this.level = getLevel(); 
        setUp();
         
    }

    public GrammarMutant(MarkovChain chain){
        this.chain = chain; 
        this.game = chain.buildGame(); 
        this.random = chain.random; 
        this.level = getLevel();
        setUp();  
    }

    public GrammarMutant(String cvsFile) throws IOException{
        this.chain = new MarkovChain(); 
        chain.trainFromCsvFile(cvsFile);
        this.random = chain.random; 
        this.game = chain.buildGame(); 
        this.level = getLevel(); 
        setUp();
    }

    @Override
    public MutantInterface initialMutant() {
       return new GrammarMutant(chain); 
    }

    @Override
    public MutantInterface mutate() {
        if(random.nextDouble() < 0.7){
            int numMutations = random.nextInt(1,5); 
            String newGame = game; 
            for(int i = 0; i < numMutations; i ++){
                newGame = chain.mutateGame(game); 
            }
            
            char[][] generalLevel = LevelChain.generalizedLevel(getGame(), getLevel()); 
            String newLevel = LevelChain.specifyLevel(newGame, generalLevel); 
            return new GrammarMutant(newGame, newLevel, chain); 
        }else{
            int numMutations = random.nextInt(2,10); 
            String newLevel = getLevel(); 
            for(int i = 0; i < numMutations; i ++){
                newLevel = chain.mutateLevel(getGame(), newLevel); 
            }
            return new GrammarMutant(getGame(), newLevel, chain); 
        }
        
    }

    @Override
    public String getGame() {
       return this.game; 
    }

    @Override
    public String getLevel() {
        if (this.level == null){
            level = chain.buildLevel(game, LEVEL_TRIALS); 
        }
        return this.level;
    }

    static int fact(int number) {  
        int f = 1;  
        int j = 1;  
        while(j <= number) {  
           f = f * j;  
           j++;  
        }  
        return f;  
     }  
  

    private int numInteractionPairs(String interaction){
        int n = interaction.split(">")[0].split(" ").length;
        
        return fact(n) / (fact(2) * fact(n-2)); // combination formula 

    }

    @Override
    public int getInteractionSize() {
        ArrayList<ArrayList<String>> parts = MarkovChain.parseInteractions(game); 
        int interactionSum = 0; 
        for(String interaction : parts.get(1)){
            interactionSum += numInteractionPairs(interaction); 
        }
        return interactionSum; 
    }

    public static String getSpriteName(String sprite){
        String[] lines = sprite.split("\n");

        if(lines.length == 1){
            return sprite.split(">")[0].strip(); 
        }else{
            int i = lines.length - 1; 
            while(lines[i].isEmpty() || lines[i].isBlank()){
                i --; 
            }
            return lines[i].split(">")[0].strip(); 
        }
    }

    public static String renameSprite(String gameDescription, String oldName, String newName){
        String[] templates = {" " + oldName + " ", "=" + oldName + " ", "\n" + oldName + " ", "\n" + oldName + "\n",  " " + oldName + "\n", "=" + oldName + "\n", "\t" + oldName + " "}; 
        String[] templates2 = {" " + newName + " ", "=" + newName + " ", "\n" + newName + " ",  "\n" + newName + "\n",  " " + newName + "\n", "=" + newName + "\n",  "\t" + newName + " "}; 
        String newGame = gameDescription; 

        for(int i = 0; i < templates.length; i ++){
            newGame = newGame.replace(templates[i], templates2[i]);
            newGame = newGame.replace(templates[i], templates2[i]); //for back to back sprites 
        }
        return newGame; 
    }

    public static String renameAllSprites(String gameDescription, String starting){
        ArrayList<ArrayList<String>> parts = MarkovChain.parseInteractions(gameDescription); 
        String newGame = gameDescription; 
        int i =0; 
        for(String sprite: parts.get(0)){
            String oldName = getSpriteName(sprite);
            String newName = starting + i; 
            newGame = renameSprite(newGame, oldName, newName); 
            i ++; 
        }

        return newGame; 
    }


    public static ArrayList<String> getAllRuleswithSprite(ArrayList<String> rules, String sprite, ArrayList<String> otherRelatedSprites, boolean isInteraction){
        String name = getSpriteName(sprite);  
        String[] templates = {" " + name+ " ", "=" + name + " ", "\n" + name + " ", "\n" + name + "\n",  " " + name + "\n", "=" + name + "\n" + "\t" + name + " "}; 
        ArrayList<String> applicableRules = new ArrayList<String>(); 

        for (String rule: rules){
            boolean add = false;
            
            if(isInteraction){
                add = rule.startsWith(name + " ");
            }

            if (! add){
                for(String template: templates){
                    if(rule.contains(template) && rule != sprite){
                        add = true; 
                    }
                }
            }
         

            if(add){
                applicableRules.add(rule); 
                
                if(rule.contains("stype=") ||  rule.contains("stype1=") || rule.contains("stype2=")  ||rule.contains("resource=")){
                    String[] words = rule.split(" "); 
                    for(String word: words){
                        if(word.contains("stype=") || word.contains("stype1=") || word.contains("stype2=")  || word.contains(" resource=")){
                            String newName = word.split("=")[1].strip(); 
                            if(newName != name){
                                otherRelatedSprites.add(newName); 
                            }
                            
                        }
                    }
                }

                if(isInteraction){
                    String[] spriteName = rule.split(">")[0].split(" "); 
                    for(String newName: spriteName){
                        newName = newName.strip(); 
                        if(!newName.isBlank() && newName != name){
                            otherRelatedSprites.add(newName);
                        }
                    }
                }
            }
        }
       


       return applicableRules; 
    }


    private ArrayList<ArrayList<String>>[] addSprite(String sprite, ArrayList<ArrayList<String>> parts, ArrayList<String> newSprites,ArrayList<String> newInteractions, ArrayList<String> newTerminations){
     
            ArrayList<String> namesToAdd = new ArrayList<String>(); 
            String name = getSpriteName(sprite); 

            if(sprite.contains("stype=") ||  sprite.contains("stype1=") || sprite.contains("stype2=")  ||sprite.contains("resource=")){
                String[] words = sprite.split(" "); 
                for(String word: words){
                    if(word.contains("stype=") || word.contains("stype1=") || word.contains("stype2=")  || word.contains(" resource=")){
                        String newName = word.split("=")[1].strip(); 
                        if(newName != name){
                            namesToAdd.add(newName); 
                        }
                        
                    }
                }
            }
            ArrayList<String> relatedSprites = getAllRuleswithSprite(parts.get(0), sprite, namesToAdd, false);
            ArrayList<String> relatedInteractions = getAllRuleswithSprite(parts.get(1), sprite, namesToAdd, true);
            ArrayList<String> relatedTermination = getAllRuleswithSprite(parts.get(2), sprite, namesToAdd, false);
            ArrayList<String> relatedLevelMapping = getAllRuleswithSprite(parts.get(3), sprite, namesToAdd, false);

            ArrayList<String> modSprites = new ArrayList<String>(newSprites); 
            ArrayList<String> modInteractions= new ArrayList<String>(newInteractions); 
            ArrayList<String> modTerminations= new ArrayList<String>(newTerminations); 

            if(newSprites.contains(sprite)){
                ArrayList<ArrayList<String>>[] returnArr =( ArrayList<ArrayList<String>>[]) new ArrayList[2]; 
                ArrayList<ArrayList<String>> newParts = new ArrayList<ArrayList<String>>(); 
                newParts.add(modSprites); 
                newParts.add(modInteractions);
                newParts.add(modTerminations);
                returnArr[0]= newParts; 
                returnArr[1] = parts; 
                return returnArr;
            }

            ArrayList<String> oldSprites = new ArrayList<String>(parts.get(0)); 
            ArrayList<String> oldInteractions = new ArrayList<String>(parts.get(1)); 
            ArrayList<String> oldTerminations = new ArrayList<String>(parts.get(2)); 
            ArrayList<String> oldLevelMapping= new ArrayList<String>(parts.get(3)); 


            //System.out.println(sprite); 
            //System.out.println(relatedInteractions); 
            //System.out.println(namesToAdd); 
            //System.out.println("\n\n");

          


            ArrayList<ArrayList<String>> oldParts = new ArrayList<ArrayList<String>>();
            oldParts.add(oldSprites); 
            oldParts.add(oldInteractions); 
            oldParts.add(oldTerminations);
            oldParts.add(oldLevelMapping);
            oldParts.add(parts.get(4)); 

            
            oldSprites.remove(sprite); 
            //oldSprites.removeAll(relatedSprites); 
            oldInteractions.removeAll(relatedInteractions); 
            oldTerminations.removeAll(relatedTermination); 
            oldLevelMapping.removeAll(relatedLevelMapping); 
            

            modSprites.add(sprite);  
            modInteractions.addAll(relatedInteractions); 
            modTerminations.addAll(relatedTermination); 
            
           

    


           boolean canAdd = true; 

            for(String newName : namesToAdd){

                if(newName.equals("EOS") || newName.equals(name)){
                    continue;
                }
                String newSprite = MarkovChain.findSpriteWithName(newName, oldSprites);
                String spriteInList = MarkovChain.findSpriteWithName(newName, modSprites); 
                if(newSprite == "" && spriteInList == ""){

                    // added to other child 
                    // undo adding sprite 
                    //System.out.println(modSprites);
                 
                    canAdd = false; 
                    break; 

                }else if (newSprite != "" && newSprite != sprite){
                       // can now add sprites 
                    ArrayList<ArrayList<String>>[] returnedArr = addSprite(newSprite, oldParts, modSprites, modInteractions, modTerminations); 
        
                    if(returnedArr == null){
                     
                        canAdd = false; 
                        break; 
                    }else{
                       
                        ArrayList<ArrayList<String>> newParts = returnedArr[0]; 
                        oldParts = returnedArr[1]; 
                        modSprites = newParts.get(0); 
                        modInteractions = newParts.get(1); 
                        modTerminations = newParts.get(2); 
                    }
                }
            }

            if(canAdd){
                for(String newSprite: relatedSprites){
                    ArrayList<ArrayList<String>>[] returnedArr = addSprite(newSprite, oldParts, modSprites, modInteractions, modTerminations); 
                   
        
                    if(returnedArr == null){
                      
                        canAdd = false; 
                        break; 
                    }else{
                       
                        ArrayList<ArrayList<String>> newParts = returnedArr[0]; 
                        oldParts = returnedArr[1]; 
                        modSprites = newParts.get(0); 
                        modInteractions = newParts.get(1); 
                        modTerminations = newParts.get(2); 
                        oldParts.get(0).remove(sprite);
                    }
                }
            }
            

            if(canAdd){
                ArrayList<ArrayList<String>> newParts = new ArrayList<ArrayList<String>>(); 
                newParts.add(modSprites); 
                newParts.add(modInteractions); 
                newParts.add(modTerminations);

                

                // modify old parts 
                parts.get(0).clear();
                parts.get(0).addAll(oldSprites);  

                parts.get(1).clear();
                parts.get(1).addAll(oldInteractions);  

                parts.get(2).clear();
                parts.get(2).addAll(oldTerminations);  

                parts.get(3).clear();
                parts.get(3).addAll(oldLevelMapping);  

                ArrayList<ArrayList<String>>[] returnArr =( ArrayList<ArrayList<String>>[]) new ArrayList[2]; 
                returnArr[0]= newParts; 
                returnArr[1] = oldParts; 
                return returnArr; 
            }else{
                return null; 
            }
      
    }

    @Override
    public MutantInterface[] crossover(MutantInterface other) {

        if(random.nextDouble() > 0.75){
            return crossoverGame(other);
        }else{
            return crossoverLevel(other); 
        }
    }


    public MutantInterface[] crossoverLevel(MutantInterface other){
        String[] newLevels = LevelChain.crossover(getGame(), other.getGame(), getLevel(), other.getLevel());
        GrammarMutant mutant1 = new GrammarMutant(getGame(), newLevels[0], this.chain); 
        GrammarMutant mutant2; 

        try{
            GrammarMutant grammarOther  = (GrammarMutant) other; 
            mutant2 = new GrammarMutant(other.getGame(), newLevels[1], grammarOther.chain); 
        }catch (Throwable t){
            mutant2 = new GrammarMutant(other.getGame(), newLevels[1], this.chain); 
        }
        

        MutantInterface[] children = {mutant1, mutant2}; 
        return children; 
        
    }

    public MutantInterface[] crossoverGame(MutantInterface other) {
        // TODO Auto-generated method stub
        String thisGame = renameAllSprites(getGame(), "a"); 
        String thatGame = renameAllSprites(other.getGame(),"b"); 
        //String thisGame = getGame(); 
        //String thatGame = other.getGame(); 

        ArrayList<ArrayList<String>> partsA = MarkovChain.parseInteractions(thisGame); 
        partsA.set(0, MarkovChain.removeNestedSprites(partsA.get(0)));
        ArrayList<ArrayList<String>> partsB = MarkovChain.parseInteractions(thatGame); 
        partsB.set(0, MarkovChain.removeNestedSprites(partsB.get(0)));

        ArrayList<String> child1Sprites = new ArrayList<>(); 
        ArrayList<String> child1Interactions = new ArrayList<>(); 
        ArrayList<String> child1Terminations = new ArrayList<>(); 
        ArrayList<String> child1LevelMapping= new ArrayList<>(); 

        ArrayList<String> child2Sprites = new ArrayList<>(); 
        ArrayList<String> child2Interactions = new ArrayList<>(); 
        ArrayList<String> child2Terminations = new ArrayList<>(); 
        ArrayList<String> child2LevelMapping= new ArrayList<>(); 

        double rate = 0.5; 
        boolean child1HasAvatar = false; 
        boolean child2HasAvatar = false; 

        //System.out.println(partsA.get(2));
        

        //distrbiute sprites 
        while(partsA.get(0).size() > 0){
            String sprite = partsA.get(0).get(0); // next sprite; 

            
            int childToGive = -1; 
            // make sure both games get an avatar 
            if(sprite.contains("Avatar")){
                
                if((child1HasAvatar && child2HasAvatar) || (!child1HasAvatar && !child2HasAvatar)){
                    childToGive = random.nextDouble() < rate ? 1 : 2; 
                }else if(!child1HasAvatar){
                    childToGive = 1;
                }else{
                    childToGive = 2; 
                }
            }else{
                childToGive = random.nextDouble() < rate ? 1 : 2; 
            }

            if(childToGive == 1){
                ArrayList<ArrayList<String>>[] returnArr = addSprite(sprite, partsA, child1Sprites, child1Interactions, child1Terminations); 
                
                if(returnArr == null){
                    returnArr = addSprite(sprite, partsA, child2Sprites, child2Interactions, child2Terminations); 
                    if(returnArr == null){ // couldn't be added to either 
                        partsA.get(0).remove(sprite); 
                    }else{
                        ArrayList<ArrayList<String>> newParts = returnArr[0];
                        child2Sprites = newParts.get(0); 
                        child2Interactions = newParts.get(1); 
                        child2Terminations = newParts.get(2);

                        partsA = returnArr[1];
                    }
                }else{
                    ArrayList<ArrayList<String>> newParts = returnArr[0];
                    child1Sprites = newParts.get(0); 
                    child1Interactions = newParts.get(1); 
                    child1Terminations = newParts.get(2);
                    
                    partsA = returnArr[1];
            
                }
                
            }else {
                ArrayList<ArrayList<String>>[] returnArr = addSprite(sprite, partsA, child2Sprites, child2Interactions, child2Terminations); 
                
                if(returnArr == null){
                    returnArr = addSprite(sprite, partsA, child1Sprites, child1Interactions, child1Terminations); 
                    if(returnArr == null){ // couldn't be added to either 
                        partsA.get(0).remove(sprite); 
                        //System.out.println("couldnt add sprite: " + sprite); 
                    }else{
                        ArrayList<ArrayList<String>> newParts = returnArr[0];
                        child1Sprites = newParts.get(0); 
                        child1Interactions = newParts.get(1); 
                        child1Terminations = newParts.get(2);

                        partsA = returnArr[1];

                    }
                }else{
                    ArrayList<ArrayList<String>> newParts = returnArr[0];
                    child2Sprites = newParts.get(0); 
                    child2Interactions = newParts.get(1); 
                    child2Terminations = newParts.get(2);
                    
                    partsA = returnArr[1];
                }
            }
        }
        while(partsB.get(0).size() > 0){
            String sprite = partsB.get(0).get(0); // next sprite; 
            
            int childToGive = -1; 
            // make sure both games get an avatar 
            if(sprite.contains("Avatar")){
                
                if((child1HasAvatar && child2HasAvatar) || (!child1HasAvatar && !child2HasAvatar)){
                    childToGive = random.nextDouble() < rate ? 1 : 2; 
                }else if(!child1HasAvatar){
                    childToGive = 1;
                }else{
                    childToGive = 2; 
                }
            }else{
                childToGive = random.nextDouble() < (1 - rate) ? 1 : 2; 
            }

            if(childToGive == 1){
                ArrayList<ArrayList<String>>[] returnArr = addSprite(sprite, partsB, child1Sprites, child1Interactions, child1Terminations); 
                
                if(returnArr == null){
                    returnArr = addSprite(sprite, partsB, child2Sprites, child2Interactions, child2Terminations); 
                    if(returnArr == null){ // couldn't be added to either 
                        partsB.get(0).remove(sprite); 
                        //System.out.println("couldnt add sprite: " + sprite); 
                    }else{
                        ArrayList<ArrayList<String>> newParts = returnArr[0];
                        child2Sprites = newParts.get(0); 
                        child2Interactions = newParts.get(1); 
                        child2Terminations = newParts.get(2);

                        partsB = returnArr[1];
                    }
                }else{
                    ArrayList<ArrayList<String>> newParts = returnArr[0];
                    child1Sprites = newParts.get(0); 
                    child1Interactions = newParts.get(1); 
                    child1Terminations = newParts.get(2);
                    
                    partsB = returnArr[1];

                }
                
            }else {
                ArrayList<ArrayList<String>>[] returnArr = addSprite(sprite, partsB, child2Sprites, child2Interactions, child2Terminations); 
                
                if(returnArr == null){
                    returnArr = addSprite(sprite, partsB, child1Sprites, child1Interactions, child1Terminations); 
                    if(returnArr == null){ // couldn't be added to either 
                        partsB.get(0).remove(sprite); 
                        //System.out.println("couldnt add sprite: " + sprite); 
                    }else{
                        ArrayList<ArrayList<String>> newParts = returnArr[0];
                        child1Sprites = newParts.get(0); 
                        child1Interactions = newParts.get(1); 
                        child1Terminations = newParts.get(2);

                        partsB = returnArr[1];
                    }
                }else{
                    ArrayList<ArrayList<String>> newParts = returnArr[0];
                    child2Sprites = newParts.get(0); 
                    child2Interactions = newParts.get(1); 
                    child2Terminations = newParts.get(2);
                    
                    partsB = returnArr[1];
                }
            }
        }

        // distbute remaining termination 
        while(partsA.get(2).size() > 0){
            String termination = partsA.get(2).get(0); 
            partsA.get(2).remove(termination);  
            int childToGive = random.nextDouble() < (rate) ? 1 : 2; 

            if(!termination.contains("stype")){
                if(childToGive == 1){
                    child1Terminations.add(termination); 
                }else{
                    child2Terminations.add(termination);
                }
            }
          
        }

        while(partsB.get(2).size() > 0){
            String termination = partsB.get(2).get(0); 
            partsB.get(2).remove(termination);  
            int childToGive = random.nextDouble() < (1 -rate) ? 1 : 2; 
            if(!termination.contains("stype")){
                if(childToGive == 1){
                    child1Terminations.add(termination); 
                }else{
                    child2Terminations.add(termination);
                }
            }
        }


        child1Sprites = MarkovChain.reNestSprites(child1Sprites);
        child2Sprites = MarkovChain.reNestSprites(child2Sprites);


        child1LevelMapping = MarkovChain.buildLevelMapping(child1Sprites); 
        child2LevelMapping = MarkovChain.buildLevelMapping(child2Sprites); 

        String gameChild1 = MarkovChain.buildGameDescription(partsA.get(4).get(0), child1Sprites, child1Interactions, child1Terminations, child1LevelMapping);
        gameChild1 = renameAllSprites(gameChild1, "sprite");
        String gameChild2 = MarkovChain.buildGameDescription(partsB.get(4).get(0), child2Sprites, child2Interactions, child2Terminations, child2LevelMapping);
        gameChild2 = renameAllSprites(gameChild2, "sprite");

        char[][] generalLevel1 = LevelChain.generalizedLevel(thisGame, this.getLevel()); 
        char[][] generalLevel2 = LevelChain.generalizedLevel(thatGame, other.getLevel()); 

        String level1 = LevelChain.specifyLevel(gameChild1, generalLevel1); 
        String level2 = LevelChain.specifyLevel(gameChild2, generalLevel2); 

        //String level1 = this.getLevel(); 
        //String level2 = other.getLevel();

        GrammarMutant mutant1 = new GrammarMutant(gameChild1, level1, this.chain); 
        GrammarMutant mutant2; 

        try{
            GrammarMutant grammarOther  = (GrammarMutant) other; 
            mutant2 = new GrammarMutant(gameChild2, level2, grammarOther.chain); 
        }catch (Throwable t){
            mutant2 = new GrammarMutant(gameChild2, level2, this.chain); 
        }
        

        MutantInterface[] children = {mutant1, mutant2}; 
        return children; 
    }


    
}
