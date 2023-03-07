package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

public class IA25 extends AbstractNeedSpell
{

  public IA25(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=100,maxPo=1;
      boolean action=false;

      for(SortStats spellStats : this.highests)
        if(spellStats!=null&&spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      Fighter secondEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(fight,this.fighter,0,5);
      Fighter target=Function.getInstance().getNearestEnnemynbrcasemax(fight,this.fighter,0,2);

      if(target!=null)
        if(target.isHide())
          target=null;

      if(this.fighter.getCurPm(this.fight)>0&&secondEnnemy==null&&target==null) //no target, move to nearest enemy
      {
        int num=Function.getInstance().moveautourIfPossible(fight,this.fighter,ennemy);
        if(num!=0)
        {
          time=num;
          action=true;
          ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
          secondEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(fight,this.fighter,0,5);
          target=Function.getInstance().getNearestEnnemynbrcasemax(fight,this.fighter,0,2);
        }
      }
      else if(this.fighter.getCurPm(this.fight)>0&&secondEnnemy!=null&&target==null) //enemy in 0-5 range (reachable by moving)
      {
        int num=Function.getInstance().moveautourIfPossible(fight,this.fighter,secondEnnemy);
        if(num!=0)
        {
          time=num;
          action=true;
          ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
          secondEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(fight,this.fighter,0,5);
          target=Function.getInstance().getNearestEnnemynbrcasemax(fight,this.fighter,0,2);
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&target!=null&&!action) //attack if next to enemy
      {
        int num=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.cacs);
        if(num!=-1)
        {
          time=num;
          action=true;
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&!action) //move to nearest enemy
      {
        int num=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
        if(num!=0)
          time=num;
      }

      if(this.fighter.getCurPa(this.fight)==0&&this.fighter.getCurPm(this.fight)==0)
        this.stop=true;
      addNext(this::decrementCount,time);
    }
    else
    {
      this.stop=true;
    }
  }
}