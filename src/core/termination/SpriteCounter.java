package core.termination;

import java.util.ArrayList;

import core.vgdl.VGDLFactory;
import core.vgdl.VGDLRegistry;
import core.content.TerminationContent;
import core.game.Game;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 22/10/13
 * Time: 18:52
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class SpriteCounter extends Termination
{
    public String stype;
    public int itype;

    public SpriteCounter(){}

    public SpriteCounter(TerminationContent cnt, VGDLFactory factory) throws Exception
    {
        //Parse the arguments.
        this.parseParameters(cnt, factory);
        itype = VGDLRegistry.GetInstance().getRegisteredSpriteValue(stype);
        if(itype == -1){
            String[] className = this.getClass().getName().split("\\.");
            throw new Exception("[" + className[className.length - 1] + "] Undefined sprite " + stype);
        }
    }

    @Override
    public boolean isDone(Game game) {

        boolean ended = super.isFinished(game);
        if(ended)
            return true;

        if(itype != -1 && game.getNumSprites(itype) - game.getNumDisabledSprites(itype) <= limit && canEnd) {
            countScore(game);
            return true;
        }

        return false;
    }

    @Override
    public ArrayList<String> getTerminationSprites() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(stype);

        return result;
    }

}
