package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

public class IA30 extends AbstractNeedSpell
{

  public IA30(Fight fight, Fighter fighter, byte count)
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
      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      //Fighter A=Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,1,63);// pomax +1;

      for(SortStats S : this.highests)
        if(S!=null&&S.getMaxPO()>maxPo)
          maxPo=S.getMaxPO();

      Fighter longestEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
      Fighter nearestEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;

      if(maxPo==1)
        longestEnnemy=null;
      if(nearestEnnemy!=null)
        if(nearestEnnemy.isHide())
          nearestEnnemy=null;
      if(longestEnnemy!=null)
        if(longestEnnemy.isHide())
          longestEnnemy=null;

      if(this.fighter.getCurPm(this.fight)>0&&longestEnnemy==null&&nearestEnnemy==null)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
        if(value!=0)
        {
          time=value;
          action=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&this.fighter.nbInvocation()<2)
      {
        if(Function.getInstance().invocIfPossible(this.fight,this.fighter,this.invocations))
        {
          time=600;
          action=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&!action)
      {
        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,this.fighter,this.buffs))
        {
          time=400;
          action=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&longestEnnemy!=null&&nearestEnnemy==null&&!action)
      {
        int value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        if(value!=-1)
        {
          time=value;
          action=true;
        }
      } else if(this.fighter.getCurPa(this.fight)>0&&nearestEnnemy!=null&&!action)
      {
        int value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.cacs);
        if(value!=-1)
        {
          time=value;
          action=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&nearestEnnemy!=null&&!action)
      {
        int value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        if(value!=-1)
        {
          time=value;
          action=true;
        }
      }
      if(this.fighter.getCurPa(this.fight)>0&&nearestEnnemy!=null&&!action)
      {
        String value = Function.getInstance().moveToAttackIfPossible2(this.fight,this.fighter);
        if(!value.isEmpty())
        {
          int cellId = Integer.parseInt(value.split(";")[0]);
          SortStats spellStats = fighter.getMob().getSpells().get(Integer.parseInt(value.split(";")[1]));

          if(fight.canCastSpell1(fighter, spellStats, fight.getMap().getCase(cellId), cellId)){
            int val = fight.tryCastSpell(fighter, spellStats, cellId);
            if(val != 10) {
              time = spellStats.getSpell().getDuration();
              action = true;
            }
          }
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&!action)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
        if(value!=0)
          time=value;
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