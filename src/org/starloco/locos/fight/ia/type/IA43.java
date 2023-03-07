package org.starloco.locos.fight.ia.type;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;

public class IA43 extends AbstractNeedSpell
{

  public IA43(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=100;
      boolean action=false;

      Fighter E=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,50);// pomax +1;
      Fighter L=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,5);// pomax +1;

      if(PathFinding.getcasebetwenenemie(this.fighter.getCell().getId(),this.fight.getMap(),this.fight,this.fighter))
      {
        action=true;
        this.stop=true;
      }
      if(L!=null&&L.isHide())
        L=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,10);// pomax +1;
      if(this.fighter.getCurPm(this.fight)>0&&L!=null&&!action)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,L);
        if(value!=0)
        {
          time=value;
          Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,5);// pomax +1;
        }
      } else if(this.fighter.getCurPm(this.fight)>0&&L==null&&!action)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,E);
        if(value!=0)
        {
          time=value;
          Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,5);// pomax +1;
        }
      }

      if(this.fighter.getCurPm(this.fight)==0)
        this.stop=true;

      addNext(this::decrementCount,time);
    } else
    {
      this.stop=true;
    }
  }
}