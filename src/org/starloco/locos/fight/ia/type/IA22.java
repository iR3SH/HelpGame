package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;

public class IA22 extends AbstractIA
{
  private boolean attack=false;

  public IA22(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=600;
      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);

      if(Function.getInstance().IfPossibleRasboulvulner(this.fight,this.fighter,this.fighter)==0)
      {
        if(attack) //has already attacked, allow it to move away
        {
          if(Function.getInstance().tpIfPossibleRasboul(this.fight,this.fighter,ennemy)==0) //tele again if possible
          {
            //nothing
          }
          else
          {
            int num=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
            if(num!=0)
              time=num;
            else
              Function.getInstance().invocIfPossible(this.fight,this.fighter);
          }
        }
        else //hasnt attacked yet, dont allow to move away
        {
          if(Function.getInstance().tpIfPossibleRasboul(this.fight,this.fighter,ennemy)==0) //attack worked
            attack=true;
          else
          {
            int num=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
            if(num!=0)
            {
              time=num;
              ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
            }
            else
              Function.getInstance().invocIfPossible(this.fight,this.fighter);
          }
        }
      }

      addNext(this::decrementCount,time);
    }
    else
    {
      this.stop=true;
    }
  }
}