package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.kernel.Config;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA8 extends AbstractNeedSpell
{

  public IA8(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      boolean action=false;
      int PA=0,PM=this.fighter.getCurPm(this.fight),maxPo=1,time=100;

      for(SortStats spellStats : this.buffs)
        if(spellStats!=null&&spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      Fighter target=Function.getInstance().getNearestInvocnbrcasemax(this.fight,this.fighter,0,maxPo); //2 = po min 1 + 1;

      if(PM>0&&target==null)
      {
        int num=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
        if(num!=0)
        {
          time=num;
          action=true;
          target=Function.getInstance().getNearestInvocnbrcasemax(this.fight,this.fighter,0,maxPo);//2 = po min 1 + 1;
        }
      }

      PA=this.fighter.getCurPa(this.fight);
      PM=this.fighter.getCurPm(this.fight);

      if(PA>0&&!action)
      {
        if(Function.getInstance().invocIfPossibleloin(this.fight,this.fighter,this.invocations))
        {
          time=400;
          action=true;
        }
      }
      if(PA>0&&!action&&target!=null)
      {
        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,target,this.buffs))
        {
          time=400;
          action=true;
        }
      }

      if(PM>0&&!action)
      {
        int num=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
        if(num!=0)
          time=num;
      }

      if(this.fighter.getCurPa(fight)==0&&this.fighter.getCurPm(fight)==0)
        this.stop=true;
      addNext(this::decrementCount,time+Config.getInstance().AIDelay);
    }
    else
    {
      this.stop=true;
    }
  }
}