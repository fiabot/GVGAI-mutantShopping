package ontology.sprites.npc;

import core.vgdl.VGDLFactory;
import core.vgdl.VGDLRegistry;
import core.vgdl.VGDLSprite;
import core.content.SpriteContent;
import core.game.Game;
import ontology.Types;
import tools.Direction;
import tools.Vector2d;
import tools.pathfinder.Node;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 21/10/13
 * Time: 18:14
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class PathChaser extends RandomNPC
{
    public boolean fleeing;
    public String stype;
    public int itype;
    public float maxDistance;

    ArrayList<VGDLSprite> targets;
    ArrayList<Direction> actions;
    VGDLRegistry registry;

    public PathChaser(){}

    public PathChaser(Vector2d position, Dimension size, SpriteContent cnt, VGDLFactory factory, VGDLRegistry registry)
    {
        this.registry = registry; 
        //Init the sprite
        this.init(position, size, factory);

        //Specific class default parameter values.
        loadDefaults();

        //Parse the arguments.
        this.parseParameters(cnt);
    }

    protected void loadDefaults()
    {
        super.loadDefaults();
        fleeing = false;
        maxDistance = -1;
        targets = new ArrayList<VGDLSprite>();
        actions = new ArrayList<Direction>();
    }

    public void postProcess()
    {
        super.postProcess();
        //Define actions here.
        itype =  registry.getRegisteredSpriteValue(stype);
    }

    public void update(Game game)
    {
        actions.clear();

        //passive moment.
        super.updatePassive();

        //Get the closest targets
        closestTargets(game);

        Direction act = Types.DNONE;
        if(targets.size() > 0)
        {
            //If there's a target, get the path to it and take the first action.
            VGDLSprite target = targets.get(0);
            ArrayList<Node> path = game.getPath(this.getPosition(), target.getPosition());

            if(path!=null && path.size()>0)
            {
                Vector2d v = path.get(0).comingFrom;
                act = new Direction(v.x, v.y);
            }

            this.counter = this.cons; //Reset the counter of consecutive moves.

        }else
        {
            //No target, just move random.
            act = getRandomMove(game);
        }

        //Apply the action to move.
        this.physics.activeMovement(this, act, this.speed);
    }

    /**
     * Sets a list with the closest targets (sprites with the type 'stype'), by distance
     * @param game game to access all sprites
     */
    protected void closestTargets(Game game)
    {
        targets.clear();
        double bestDist = Double.MAX_VALUE;

        Iterator<VGDLSprite> spriteIt = game.getSpriteGroup(itype);
        if(spriteIt == null) spriteIt = game.getSubSpritesGroup(itype); //Try subtypes

        if(spriteIt != null) while(spriteIt.hasNext())
        {
            VGDLSprite s = spriteIt.next();
            double distance = this.physics.distance(rect, s.rect);
            if(distance < bestDist)
            {
                bestDist = distance;
                targets.clear();
                targets.add(s);
            }else if(distance == bestDist){
                targets.add(s);
            }
        }
    }


    public VGDLSprite copy()
    {
        PathChaser newSprite = new PathChaser();
        this.copyTo(newSprite);
        return newSprite;
    }

    public void copyTo(VGDLSprite target)
    {
        PathChaser targetSprite = (PathChaser) target;
        targetSprite.fleeing = this.fleeing;
        targetSprite.stype = this.stype;
        targetSprite.itype = this.itype;
        targetSprite.maxDistance = this.maxDistance;
        targetSprite.targets = new ArrayList<VGDLSprite>();
        targetSprite.actions = new ArrayList<Direction>();
        targetSprite.registry = this.registry; 
        super.copyTo(targetSprite);
    }
    
    @Override
    public ArrayList<String> getDependentSprites(){
    	ArrayList<String> result = new ArrayList<String>();
    	if(stype != null) result.add(stype);
    	
    	return result;
    }

}
