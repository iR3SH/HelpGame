package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.LaunchedSpell;

public class IA42 extends AbstractNeedSpell
{
  private boolean boost=false, heal=false;

  public IA42(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&fighter.canPlay()&&this.count>0)
    {
      int time=100,maxPo=1;
      boolean action=false;
      boolean canBoost=LaunchedSpell.cooldownGood(fighter,284);
      if(count==6) //first loop
      {
        boost=false;
        heal=false;
      }
      
      for(Spell.SortStats spellStats : this.buffs)
        if(spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();
      
      Fighter L=Function.getInstance().getSummoner(this.fight,fighter,63);
      Fighter spellTarget=Function.getInstance().getSummoner(this.fight,fighter,maxPo+1);
      
      if(L!=null)
        if(L.isHide())
          L=null;
      if(spellTarget!=null)
        if(spellTarget.isHide())
          spellTarget=null;

      if(fighter.getCurPa(this.fight)>0&&!action&&spellTarget!=null&&!this.boost)
      {
        if(Function.getInstance().pmgongon(this.fight,fighter,spellTarget)!=0)
        {
          time=1000;
          action=true;
          this.boost=true;
        }
      }
      if(fighter.getCurPa(this.fight)>0&&!action&&!this.heal)
      {
        if(Function.getInstance().HealIfPossible(this.fight,fighter)!=0)
        {
          time=1500;
          action=true;
          this.heal=true;
        }
      }
      
      if(fighter.getCurPm(this.fight)>0&&spellTarget==null&&canBoost&&!this.boost)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,fighter,L);
        if(value!=0)
        {
          time=value;
          action=true;
          spellTarget=Function.getInstance().getSummoner(this.fight,fighter,maxPo+1);// pomax +1;
          if(maxPo==1)
            spellTarget=null;
        }
      }
      if(this.fighter.getCurPm(this.fight)>0&&!action&&!canBoost)
      {
        int value=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
        if(value!=0)
          time=value;
      }

      if(this.fighter.getCurPa(this.fight)==0&&this.fighter.getCurPm(this.fight)==0||heal&&boost&&this.fighter.getCurPm(this.fight)==0)
        this.stop=true;
      addNext(this::decrementCount,time);
    }
    else
    {
      this.stop=true;
    }
  }
}