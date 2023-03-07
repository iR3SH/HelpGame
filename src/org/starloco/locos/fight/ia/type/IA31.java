package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;

public class IA31 extends AbstractIA
{

  public IA31(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      Fighter nearestEnnemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      Fighter longestEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,3);

      if(!Function.getInstance().moveNearIfPossible(this.fight,this.fighter,nearestEnnemy))
        if(Function.getInstance().attackIfPossiblerat(this.fight,this.fighter,nearestEnnemy,longestEnnemy==null)==0)
          Function.getInstance().moveNearIfPossible(this.fight,this.fighter,nearestEnnemy);

      addNext(this::decrementCount,800);
    } else
    {
      this.stop=true;
    }
  }
}