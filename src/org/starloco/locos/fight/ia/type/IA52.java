package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

public class IA52 extends AbstractNeedSpell
{

  public IA52(Fight fight, Fighter fighter, byte count)
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

      for(Spell.SortStats spellStats : this.highests)
        if(spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      Fighter C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,4);//2 = po min 1 + 1;
      Fighter A=Function.getInstance().getNearestAminoinvocnbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;

      if(C!=null&&C.isHide())
        C=null;

      if(this.fighter.getCurPa(this.fight)>0)
      {
        if(Function.getInstance().HealIfPossible(this.fight,this.fighter,false,98)!=0)
        {
          time=1000;
          action=true;
        }
      }
      if(this.fighter.getCurPm(this.fight)>0&&C==null&&!action)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
        if(value!=0)
        {
          time=value;
          action=true;
          Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
          Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,4);//2 = po min 1 + 1;
        }
      }
      if(this.fighter.getCurPa(this.fight)>0&&!action&&A!=null)
      {
        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,A,this.buffs))
        {
          time=1000;
          action=true;
        }
      }
      if(this.fighter.getCurPa(this.fight)>0&&!action)
      {
        if(Function.getInstance().HealIfPossible(this.fight,this.fighter,false,98)!=0)
        {
          time=1000;
          action=true;
        }
      }
      if(this.fighter.getCurPm(this.fight)>0&&!action)
      {
        int value=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
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