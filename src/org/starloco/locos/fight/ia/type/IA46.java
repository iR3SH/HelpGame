package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

public class IA46 extends AbstractNeedSpell
{

  private boolean boost=false, heal=false;

  public IA46(Fight fight, Fighter fighter, byte count)
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
      Fighter A=Function.getInstance().getNearestFriend(this.fight,this.fighter);

      for(Spell.SortStats spellStats : this.highests)
        if(spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      Fighter L=Function.getInstance().getNearestAminoinvocnbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
      if(this.fighter.getCurPa(this.fight)>0&&(L!=null||A!=null)&&!this.boost)
      {
        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,this.fighter.getInvocator(),this.buffs))
        {
          time=1000;
          action=true;
          this.boost=true;
        }
      }
      if(this.fighter.getCurPa(this.fight)>0&&!action&&!this.heal)
      {
        if(Function.getInstance().HealIfPossible(this.fight,this.fighter,true,50)!=0)
        {
          time=2000;
          action=true;
          this.heal=true;
        }
      }

      if(L!=null&&(L.getPdv()*100)/L.getPdvMax()>99)
        L=Function.getInstance().getNearestAminoinvocnbrcasemax(this.fight,this.fighter,1,maxPo);
      if(L!=null)
        if(L.isHide())
          L=null;

      if(this.fighter.getCurPm(this.fight)>0&&L==null)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,A);
        if(value!=0)
        {
          time=value;
          action=true;
          L=Function.getInstance().getNearestAminoinvocnbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
          if(maxPo==1)
            L=null;
        }
      }
      if(this.fighter.getCurPm(this.fight)>0&&!action&&L!=null&&!this.heal)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,A);
        if(value!=0)
        {
          time=value;
          action=true;
          L=Function.getInstance().getNearestinvocateurnbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
          if(maxPo==1)
            L=null;
        }
      }
      if(this.fighter.getCurPa(this.fight)>0&&!action&&!this.heal)
      {
        if(Function.getInstance().HealIfPossible(this.fight,this.fighter,true,50)!=0)
        {
          time=2000;
          action=true;
          this.heal=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&!action)
      {
        if(Function.getInstance().HealIfPossible(this.fight,this.fighter,false,99)!=0)
        {
          time=2000;
          action=true;
          this.heal=true;
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&!action&&this.heal||this.fighter.getCurPm(this.fight)>0&&!action&&this.boost)
      {
        int value=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
        if(value!=0)
          time=value;
      }

      if(this.fighter.getCurPa(this.fight)==0&&this.fighter.getCurPm(this.fight)==0||this.heal&&this.boost&&this.fighter.getCurPm(this.fight)==0)
        this.stop=true;
      addNext(this::decrementCount,time);
    } else
    {
      this.stop=true;
    }
  }
}