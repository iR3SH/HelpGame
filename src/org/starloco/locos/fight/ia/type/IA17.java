package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

public class IA17 extends AbstractNeedSpell
{

  public IA17(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      int time=100,maxPo=1;
      boolean action=false;

      for(SortStats spellStats : this.highests)
        if(spellStats!=null&&spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      Fighter target=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);

      if(target!=null)
        if(target.isHide())
          target=null;

      if(this.fighter.getCurPa(this.fight)>0)
      {
        if(Function.getInstance().invocIfPossibleKimbo(this.fight,this.fighter, this.fighter.getMob().getSpells().get(1074)))
        {
          time=3000;
          action=true;
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&target==null)
      {
        int num=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
        if(num!=0)
        {
          time=num;
          action=true;
          target=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&target==null)
      {
        int num=Function.getInstance().attackBondIfPossible(this.fight,this.fighter,ennemy);
        if(num!=0)
        {
          time=num;
          action=true;
          target=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&target==null&&!action)
      {
        int num=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        if(num!=-1)
        {
          time=num;
          action=true;
        }
      }
      else if(this.fighter.getCurPa(this.fight)>0&&target!=null&&!action)
      {
        int num=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.cacs);
        if(num!=-1)
        {
          time=num;
          action=true;
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&!action)
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