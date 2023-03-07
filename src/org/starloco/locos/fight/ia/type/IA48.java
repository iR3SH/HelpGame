package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

public class IA48 extends AbstractNeedSpell
{

  public IA48(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=100,apCost=5;
      boolean action=false;

      for(SortStats spellStats : this.cacs)
        if(spellStats!=null)
          if(spellStats.getPACost()<apCost)
            apCost=spellStats.getPACost();

      Fighter E=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      Fighter C=Function.getInstance().getNearestEnnemymurnbrcasemax(this.fight,this.fighter,0,2); //2 = po min 1 + 1;

      if(C!=null&&C.isHide())
        C=null;

      if(this.fighter.getCurPm(this.fight)>0&&C==null)
      {
        int value=Function.getInstance().moveIfPossiblecontremur(this.fight,this.fighter,E);
        if(value!=0)
        {
          time=value;
          action=true;
          C=Function.getInstance().getNearestEnnemymurnbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
        }
      }
      if(this.fighter.getCurPa(this.fight)>=apCost&&!action)
      {
        if(Function.getInstance().invocIfPossible(this.fight,this.fighter,this.invocations))
        {
          time=600;
          action=true;
        }
      }
      if(this.fighter.getCurPa(this.fight)>=apCost&&C!=null&&!action)
      {
        int value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.cacs);
        if(value!=-1)
        {
          time=value;
          action=true;
        }
      }
      if(this.fighter.getCurPm(this.fight)>0&&!action)
      {
        int value=Function.getInstance().moveIfPossiblecontremur(this.fight,this.fighter,E);
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