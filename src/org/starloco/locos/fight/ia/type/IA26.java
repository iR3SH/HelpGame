package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;

public class IA26 extends AbstractIA
{

  public IA26(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=0;
      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      time+=Function.getInstance().attackIfPossibleAll(this.fight,this.fighter,ennemy);

      if(!Function.getInstance().invocIfPossible(this.fight,this.fighter))
      {
        if(!Function.getInstance().moveNearIfPossible(this.fight,this.fighter,ennemy))
        {
          if(!Function.getInstance().buffIfPossibleKitsou(this.fight,this.fighter,this.fighter))
          {
            int oldTime=time;
            time+=Function.getInstance().attackIfPossibleAll(this.fight,this.fighter,ennemy);
            if(oldTime!=time) //kitsou attacked someone
              time+=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
          }
          else
            time+=500;
        }
        else
          time+=1000;
      }
      else
        time+=600;

      addNext(this::decrementCount,time);
    }
    else
    {
      this.stop=true;
    }
  }
}