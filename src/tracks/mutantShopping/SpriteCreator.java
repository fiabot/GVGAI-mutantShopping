package tracks.mutantShopping;

import java.util.ArrayList;
import java.util.Random;

public class SpriteCreator {

    
	private static String[] avatars = new String[] {"MovingAvatar", "HorizontalAvatar", "VerticalAvatar", "OngoingAvatar", "OngoingTurningAvatar", "OngoingShootingAvatar", "MissileAvatar", "OrientedAvatar", "ShootAvatar", "FlakAvatar"};
    private static String[] npcs = new String[] {"RandomNPC", "Chaser", "Fleeing", "AlternateChaser", "RandAltChaser" }; 
    private static String[] ammos = new String[] {"Missile", "RandomMissile"}; 
    private static String[] colors = new String[] {"GREEN","BLUE", "RED" , "GRAY", "WHITE", "BROWN" , "BLACK" , "ORANGE" , "YELLOW" , "PINK" , "GOLD" , 
                    "LIGHTRED" , "LIGHTORANGE" , "LIGHTBLUE" , "LIGHTGREEN" , "LIGHTYELLOW" , "LIGHTGRAY" , "DARKGRAY" , "DARKBLUE" }; 
    private static String[] orientations = new String[] {"UP", "DOWN", "LEFT", "RIGHT", "NONE"}; 
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
    }

    public String[] createAmo(){
        ammoCount += 1; 
        String newType = ammos[random.nextInt(ammos.length)]; 
        String color = colors[random.nextInt(colors.length)]; 
        String amo =  ammoName + ammoCount + " > " + newType + " img=" + ammoImg + " color=" + color; 
        
        amo += " orientation=" + orientations[random.nextInt(orientations.length)]; 
        

        return new String[] {ammoName + ammoCount,  amo}; 


    }
    public ArrayList<String> createAvator(String name, String image){
        ArrayList<String> returnList = new ArrayList<String>(); 
        String newAvatarType = avatars[random.nextInt(avatars.length)]; 
        String avatar = name + " > " + newAvatarType + " img=" + image; 
        

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

    public String createNPC(String name, String image){
        ArrayList<String> returnList = new ArrayList<String>(); 
        String newNPCType = npcs[random.nextInt(npcs.length)]; 
        String NPC = name + " > " +  newNPCType + " img=" + image; 
        String color = colors[random.nextInt(colors.length)]; 
        NPC += " color=" + color; 

        String avatar[] = mutant.getAvatarString(); 
        String[] parts = avatar[0].split(" "); 

        if (newNPCType == "Chaser"){
            
            NPC += " stype=" + parts[0]; 
        }
        if (newNPCType == "Fleeing"){

            
            NPC += " stype=" + chooseFleeingSprite(avatar); 
        }

        if (newNPCType == "AlternateChaser" || newNPCType ==  "RandAltChaser"){
            NPC += " stype1=" + parts[1];
            NPC += " stype2=" + chooseFleeingSprite(avatar); 
        }

        if(newNPCType == "RandAltChaser"){
            double prob = random.nextDouble(); 
            NPC += " prob=" + prob; 
        }

        returnList.add(NPC); 

        return NPC; 
    }
}
