package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

public class IA32 extends AbstractNeedSpell
{
  private int movedDiag=0;
  private int attack=0;

  public IA32(Fight fight, Fighter fighter, byte count)
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
      Fighter nearestEnnemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);

      for(Spell.SortStats S : this.highests)
        if(S!=null&&S.getMaxPO()>maxPo)
          maxPo=S.getMaxPO();

      Fighter longestEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,maxPo+1);//po max+ 1;
      if(longestEnnemy!=null)
        if(longestEnnemy.isHide())
          longestEnnemy=null;
      
      if(this.fighter.getCurPm(this.fight)>0&&longestEnnemy==null&&this.attack==0&&this.movedDiag<1)
      {
        int value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,nearestEnnemy);
        if(value!=0)
        {
          movedDiag++;
          time=value;
          action=true;
          //longestEnnemy=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,maxPo+1);
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

      if(this.fighter.getCurPa(this.fight)>0&&!action)
      {
        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,this.fighter,this.buffs))
        {
          time=400;
          action=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&longestEnnemy!=null&&!action)
      {
        int value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        if(value!=-1)
        {
          this.movedDiag=0;
          time=value;
          action=true;
          this.attack++;
        }
        else if(this.fighter.getCurPm(this.fight)>0&&this.attack==0&&this.movedDiag<1)
        {
          value=Function.getInstance().movediagIfPossible(this.fight,this.fighter,nearestEnnemy);
          if(value!=0)
          {
            movedDiag++;
            time=value;
            action=true;
            //Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,maxPo+1);
          }
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&!action&&this.attack>0)
      {
        int value=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
        if(value!=0)
          time=value;
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