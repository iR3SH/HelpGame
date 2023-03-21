package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

public class IA27 extends AbstractNeedSpell
{

  public IA27(Fight fight, Fighter fighter, byte count)
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
      Fighter E=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);

      for(SortStats spellStats : this.highests)
        if(spellStats!=null&&spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      Fighter firstEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1);
      Fighter secondEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);

      if(maxPo==1)
        firstEnnemy=null;
      if(secondEnnemy!=null)
        if(secondEnnemy.isHide())
          secondEnnemy=null;
      if(firstEnnemy!=null)
        if(firstEnnemy.isHide())
          firstEnnemy=null;

      if(this.fighter.getCurPm(this.fight)>0&&firstEnnemy==null&&secondEnnemy==null)
      {
        int num=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,E);
        if(num!=0)
        {
          time=num;
          action=true;
          firstEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1);
          secondEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);
          if(maxPo==1)
            firstEnnemy=null;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&!action)
      {
        if(Function.getInstance().invocIfPossible(this.fight,this.fighter,this.invocations))
        {
          time=600;
          action=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&firstEnnemy!=null&&secondEnnemy==null&&!action)
      {
        int num=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        if(num!=-1)
        {
          time=num;
          action=true;
        }
      } else if(this.fighter.getCurPa(this.fight)>0&&secondEnnemy!=null&&!action)
      {
        int num=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.cacs);
        if(num!=-1)
        {
          time=num;
          action=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&secondEnnemy!=null&&!action)
      {
        int num=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        if(num!=-1)
        {
          time=num;
          action=true;
        }
      }
      if(this.fighter.getCurPa(this.fight)>0&&E!=null&&!action)
      {
        String value = Function.getInstance().moveToAttackIfPossible2(this.fight,this.fighter);
        if(!value.isEmpty())
        {
          int cellId = Integer.parseInt(value.split(";")[0]);
          SortStats spellStats = fighter.getMob().getSpells().get(Integer.parseInt(value.split(";")[1]));

          if(fight.canCastSpell1(fighter, spellStats, fight.getMap().getCase(cellId), cellId)){
            int val = fight.tryCastSpell(fighter, spellStats, cellId);
            if(val == 0) {
              time = spellStats.getSpell().getDuration();
              action = true;
            }
          }
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&!action)
      {
        int num=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,E);
        if(num!=0)
          time=num;
      }

      if(this.fighter.getCurPa(this.fight)==0&&this.fighter.getCurPm(this.fight)==0)
        this.stop=true;
      addNext(this::decrementCount,time);
    } else
    {
      this.stop=true;
    }
  }
}