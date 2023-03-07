package org.starloco.locos.fight.ia.type;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

/**
 * Created by Locos on 24/01/2017.
 */
public class IA85 extends AbstractNeedSpell
{

  public IA85(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=100,maxPo=1;
      Fighter nearestEnnemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);

      for(Spell.SortStats S : this.highests)
        if(S.getMaxPO()>maxPo)
          maxPo=S.getMaxPO();

      Fighter ennemy1=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,8);// pomax +1;
      Fighter ennemy2=this.getNearestLowerHpEnemy();// low hp enemy

      if(this.fighter.getCurPa(this.fight)>0)
      {
        if(Function.getInstance().HealIfPossible(this.fight,this.fighter,true,40)!=0)
        {
          time=1000;
        }
      }

      if(ennemy1==null)
      {
        Function.getInstance().moveNearIfPossible(this.fight,this.fighter,nearestEnnemy);
      }

      if(this.fighter.getCurPa(this.fight)>0)
      {
        if(Function.getInstance().invocIfPossible(this.fight,this.fighter,this.invocations))
        {
          time=1000;
        }
      }
      if(this.fighter.getCurPa(this.fight)>0)
      {
        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,this.fighter,this.buffs))
        {
          time=1200;
          if(this.fighter.getCurPa(this.fight)>0)
          {
            if(Function.getInstance().buffIfPossible(this.fight,this.fighter,this.fighter,this.buffs))
            {
              time=1200;
            }
          }
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&ennemy2!=null)
      {
        int value=Function.getInstance().attackIfPossibleAll(this.fight,this.fighter,ennemy2);
        if(value!=0)
        {
          time=value;
        }
      }
      else if(this.fighter.getCurPa(this.fight)>0&&ennemy1!=null)
      {
        int value=Function.getInstance().attackIfPossibleAll(this.fight,this.fighter,ennemy1);
        if(value!=0)
        {
          time=value;
        }
      }

      if(this.fighter.getCurPa(this.fight)==0&&this.fighter.getCurPm(this.fight)==0)
        this.stop=true;
      this.addNext(this::decrementCount,time);
    }
    else
    {
      this.stop=true;
    }
  }

  private Fighter getNearestLowerHpEnemy()
  {
    for(Fighter fighter : Function.getInstance().getLowHpEnnemyList(this.fight,this.fighter).values())
      if(fighter!=null&&PathFinding.getDistanceBetweenTwoCase(this.fight.getMap(),this.fighter.getCell(),fighter.getCell())<8)
        return fighter;
    return null;
  }
}