package tracks.mutantShopping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.text.DefaultStyledDocument.ElementSpec;


public class SpriteCreator {

    
	private static String[] avatars = new String[] {"MovingAvatar", "HorizontalAvatar", "VerticalAvatar", "OngoingAvatar", "OngoingTurningAvatar", "MissileAvatar", "OrientedAvatar", "ShootAvatar", "FlakAvatar"};
    private static String[] npcs = new String[] {"RandomNPC", "Chaser", "Fleeing", "AlternateChaser", "RandomAltChaser"}; 
    private static String[] spawners = new String[] {"Bomber", "BomberRandomMissile", "RandomBomber", "SpawnPoint", "SpawnPointMultiSprite", "Spreader"}; 
    private static String[] ammos = new String[] {"Missile", "RandomMissile"}; 
    private static String[] others = new String[] {"Immovable", "Passive", "Flicker", "OrientedFlicker", "Door", "Portal", "Resource"}; 

    private static String[] commonParams = new String[] {"shrinkfactor", "invisible", "hidden", "img", "color", "singleton", "cooldown", "speed", "orientation", "rotateInPlace"}; 


    private static String[] colors = new String[] {"GREEN","BLUE", "RED" , "GRAY", "WHITE", "BROWN" , "BLACK" , "ORANGE" , "YELLOW" , "PINK" , "GOLD" , 
                    "LIGHTRED" , "LIGHTORANGE" , "LIGHTBLUE" , "LIGHTGREEN" , "LIGHTYELLOW" , "LIGHTGRAY" , "DARKGRAY" , "DARKBLUE" }; 
    private static String[] orientations = new String[] {"UP", "DOWN", "LEFT", "RIGHT", "NONE"}; 

    private static String[] characterImages = new String[] {"oryx/alien1.png", "oryx/alien2.png",  "oryx/alien3.png", "oryx/angel1_0.png",  "oryx/angel1_1.png",  "oryx/archer1.png",  "oryx/bat1.png",  "oryx/bat2.png", "oryx/bear1.png",  "oryx/bear2.png", "oryx/bear3.png",  "oryx/bee1.png",  "oryx/bee2.png",  "oryx/bird1.png",  "oryx/bird2.png", "oryx/bird3.png",  "oryx/cyclop1.png",  "oryx/cyclop2.png",  "oryx/devil1_0.png",  "oryx/devil1_1.png",  "oryx/devil1.png"}; 
    private static String[] ammoImages = new String[] {"oryx/arrows1.png", "oryx/arrows2.png", "oryx/bullet1.png", "oryx/bullet2.png", "oryx/feather1.png", "oryx/feather2.png", "oryx/fire1.png", "oryx/orb1.png", "oryx/orb2.png", "oryx/orb3.png", "oryx/slime1.png", "oryx/slime2.png", "oryx/sparkle1.png", "oryx/sparkle2.png", "oryx/sparkle3.png"}; 
    private static String[] spawnerImages = new String[] {"oryx/yeti1.png", "oryx/worm1.png", "oryx/wolf1.png", "oryx/vampire1.png", "oryx/sparkle2.png"}; 
    private static String[] itemImages = new String[] {"oryx/wall1.png", "oryx/wall2.png", "oryx/tree1.png", "oryx/treasure1.png", "oryx/tombstone1.png", "oryx/spike1.png"};
    
    ArrayList<String> allImages; 
    Random random; 
    String ammoName; 
    String ammoImg; 
    static int ammoCount = 0; 
    Mutant mutant; 

    public SpriteCreator(Random random, String ammoName, String ammoImg, Mutant mutant){
        this.random = random; 
        this.ammoName = ammoName; 
        this.ammoImg = ammoImg; 
        this.mutant = mutant; 

        allImages = new ArrayList<>(); 
        Collections.addAll(allImages, characterImages);
        Collections.addAll(allImages, ammoImages);
        Collections.addAll(allImages, spawnerImages);
        Collections.addAll(allImages, itemImages);
    }

    public String[] createAmo(){
        ammoCount += 1; 
        String newType = ammos[random.nextInt(ammos.length)]; 
        String color = colors[random.nextInt(colors.length)]; 
        String amo =  ammoName + ammoCount + " > " + newType + " img=" + ammoImages[random.nextInt(ammoImages.length)]+ " color=" + color; 
        
        amo += " orientation=" + orientations[random.nextInt(orientations.length)]; 
        

        return new String[] {ammoName + ammoCount,  amo}; 


    }
    public ArrayList<String> createAvator(String name){
        ArrayList<String> returnList = new ArrayList<String>(); 
        String newAvatarType = avatars[random.nextInt(avatars.length)]; 
        String avatar = name + " > " + newAvatarType + " img=" + characterImages[random.nextInt(characterImages.length)]; 
        

        if (newAvatarType == "ShootAvatar" || newAvatarType == "FlakAvatar"){
            String[] ammo = createAmo(); 
            avatar += " stype=" + ammo[0]; 
            returnList.add(ammo[1]); 
            System.out.println("ammo:" + ammo[1]);

        }
        System.out.println("Avatar:" + avatar);
        returnList.add(avatar);
        return returnList; 
    }

    private String chooseFleeingSprite(String[] avatar){
        String[] parts = avatar[0].split(" "); 

        if(avatar[1] == "ShootAvatar" || avatar[1] == "FlakAvatar"){
            for(String part: parts){
                if(part.startsWith("stype")){
                    return part.split("=")[1]; 
                }
            }
        }

        String sprite = mutant.sprites.get(random.nextInt(mutant.sprites.size())); 
        String spriteName = sprite.split(" ")[0]; 

        while(spriteName == parts[0]){
            sprite = mutant.sprites.get(random.nextInt(mutant.sprites.size())); 
            spriteName = sprite.split(" ")[0]; 
        }

        return spriteName;
        
    }

    private boolean contains(String[] arr, String target){
        for(String s: arr){
            if (s!= null && s.equals(target)){
                return true;
            }
        }

        return false; 
    }
    
    private String randomSprite(String[] avoid){
        if(mutant.sprites.size()> 0){
            String sprite = mutant.sprites.get(random.nextInt(mutant.sprites.size())); 
            String spriteName = sprite.split(" ")[0];  

            int i = 0; 
            while(contains(avoid, sprite) && i < 10000){
                sprite = mutant.sprites.get(random.nextInt(mutant.sprites.size())); 
                spriteName = sprite.split(" ")[0];  
                i++; 
            }

            return spriteName;

        }
        return "none";

    }


    private String[] randomNSprites(String[] avoid, int n){
        String[] sprite = new String[n]; 
        String[] newAvoid = new String[avoid.length + n]; 
        for(int i = 0; i < avoid.length; i ++){
            newAvoid[i] = avoid[i]; 
        }

        for(int i = 0; i < n; i ++){
            sprite[i] = randomSprite(newAvoid); 
            newAvoid[avoid.length + i] = sprite[i]; // update avoid 
        }

        return sprite; 
    }

    public String createNPC(String name){
        ArrayList<String> returnList = new ArrayList<String>(); 
        String newNPCType = npcs[random.nextInt(npcs.length)]; 
        String NPC = name + " > " +  newNPCType + " img=" + characterImages[random.nextInt(characterImages.length)]; 
        String color = colors[random.nextInt(colors.length)]; 
        NPC += " color=" + color; 

        String avatar[] = mutant.getAvatarString(); 
        String[] parts = avatar[0].split(" "); 

        if (newNPCType == "Chaser"){
            
            NPC += " stype=" + randomSprite(new String[] {}); 
        }
        if (newNPCType == "Fleeing"){

            
            NPC += " stype=" +  randomSprite(new String[] {}); 
        }

        if (newNPCType == "AlternateChaser" || newNPCType ==  "RandomAltChaser"){
            String[] stypes = randomNSprites(new String[] {}, 2); 
            NPC += " stype1=" + stypes[0];
            NPC += " stype2=" + stypes[1]; 
        }

        if(newNPCType == "RandomAltChaser"){
            double prob = random.nextDouble(); 
            NPC += " prob=" + prob; 
        }

        returnList.add(NPC); 

        return NPC; 
    }

    public String createSpawner(String name){
        String spawnerType = spawners[random.nextInt(spawners.length)]; 
        String spawnerImage = spawnerImages[random.nextInt(spawnerImages.length)]; 
        String spawner = name + " > " + spawnerType + " img=" + spawnerImage; 

        String avatar[] = mutant.getAvatarString(); 
        String[] parts = avatar[0].split(" "); 

        if (spawnerType.equals("BomberRandomMissile")){
            if(mutant.sprites.size() > 1){
                int amountOfSprites = random.nextInt(mutant.sprites.size() - 1);
                String[] spawns = randomNSprites(new String[] {avatar[0]}, amountOfSprites); 
                spawner += " stypeMissile="; 
                for(int i = 0; i < spawns.length; i++){
                    spawner += spawns[i]; 
                    if(i < spawns.length - 1){
                        spawner += ","; 
                    }
                }

            }
           
        }else{
            spawner += " stype=" + randomSprite(new String[] {avatar[0]});   
        }


        if (spawnerType.equals("Spreader")){
            spawner += " spreadProb=" + random.nextDouble(); 
        }else{
            spawner += " prob=" + random.nextDouble(); 

            // 50% chance of being infinite 
            if (random.nextDouble() > 0.5){
                spawner += " total=0"; 
            }else{
                spawner += " total=" + random.nextInt(100);
            }

            spawner += " spawnOrientation=" + orientations[random.nextInt(orientations.length)];
        }

        return spawner; 

    }

    public String createOther(String name){
        if (random.nextDouble() < 0.2){
            return createAmo()[1]; 
        }else{
            String otherType = others[random.nextInt(others.length)]; 
            String otherImage = itemImages[random.nextInt(itemImages.length)]; 
            String other = name + " > " + otherType + " img=" + otherImage; 

            if (otherType == "Flicker" || otherType == "OrientedFlicker"){
                other += " limit=" + random.nextInt(500);
            }else if (otherType == "Portal"){
                other += " stype=" + randomSprite(new String[] {});
            }else if (otherType == "Resource"){
                other += " resource=resource" + random.nextInt(10000); 
                int starting = random.nextInt(100);
                other += " value=" + starting;  
                other += " limit=" + (random.nextInt(100) + starting);
            }

            return other; 
            }
        
    }

    private String getParam(ArrayList<String> parts, String param){
        for(String part: parts){
            if(part.startsWith(param)){
                return part; 
            }
        }
        return null;
    }

    public void changeCommonParam(ArrayList<String> parts){
        String param = commonParams[random.nextInt(commonParams.length)];

        String current = getParam(parts, param); 

        if (current!= null){
            parts.remove(current);
        }

        if (param.equals("img")){
            parts.add(param + "=" +  allImages.get(random.nextInt(allImages.size()))); 
        }else if (param.equals("invisible") || param.equals("hidden")  || param.equals("singleton") || param.equals("rotateInPlace")){
            boolean value = random.nextBoolean(); 
            String strValue = String.valueOf(value); 
            strValue = strValue.substring(0, 1).toUpperCase() + strValue.substring(1);
            parts.add(param + "=" + strValue);
        }else if (param.equals("shrinkfactor") || param.equals("speed")){
            parts.add(param + "=" + random.nextDouble(4));
        }else if (param.equals("color")){
            parts.add(param + "=" + colors[random.nextInt(colors.length)]); 
        }else if (param.equals("orientation")){
           parts.add(param + "=" + orientations[random.nextInt(orientations.length)]); 
        }else if (param.equals("cooldown")){
            parts.add(param + "=" + random.nextInt(4)); 
        }
    }

    public String changeParam(String param){
        String type = param.split("=")[0]; 
        if (type.contains("img")){
            return type + "=" +  allImages.get(random.nextInt(allImages.size())); 
        }else if (type.contains("invisible") || type.contains("hidden")  || type.contains("singleton") || type.contains("rotateInPlace")){
            boolean value = random.nextBoolean(); 
            String strValue = String.valueOf(value); 
            strValue = strValue.substring(0, 1).toUpperCase() + strValue.substring(1);
            return type + "=" + strValue;
        }else if (type.contains("shrinkfactor") || type.contains("speed")){
            return type + "=" + random.nextDouble(4);
        }else if (type.contains("color")){
            return type + "=" + colors[random.nextInt(colors.length)]; 
        }else if (type.contains("orientation")){
           return type + "=" + orientations[random.nextInt(orientations.length)]; 
        }else if (type.contains("cooldown")){
            return type + "=" + random.nextInt(4); 
        }else if (type.contains("stype")){
            return type + "=" + randomSprite(new String[] {});
        }else if (type.contains("prob")){
            return type + "=" + random.nextDouble(1);
        }else if (type.contains("total") || type.contains("limit") || type.contains("value")){
            return type + "=" + random.nextInt(400);
        }

        return "";
    }


    public String mutateSprite(String sprite){
        
        String[] parts = sprite.split(" "); 
        ArrayList<String> newParts = new ArrayList<String>();
        
        for(String part: parts ){
            newParts.add(part);
        }

        if (random.nextDouble() > 0.5){
            changeCommonParam(newParts);
        }else {
            String randomParam = newParts.get(random.nextInt(2, newParts.size())); 
            String newParam = changeParam(randomParam); 
            if(newParam.length() > 0){
                newParts.remove(randomParam); 
                newParts.add(newParam);
            }
        }

        

        String newSprite = ""; 
        for(String str : newParts){
            newSprite += str + " "; 
        }
        System.out.println("Mutating Sprite:" + sprite + ":" + newSprite); 
        return newSprite; 
    }
}
