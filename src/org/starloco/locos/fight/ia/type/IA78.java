package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.kernel.Config;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA78 extends AbstractNeedSpell
{

  private int attack=0;
  private boolean boost=false;
  private boolean action2=false;

  public IA78(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      Fighter ally=Function.getInstance().getNearestFriend(this.fight,this.fighter);
      int PA=this.fighter.getCurPa(this.fight),PM=this.fighter.getCurPm(this.fight),time=100,maxPo=1;
      boolean action=false;

      if(this.fighter.getMob().getPa()<PA)
        this.boost=true;

      for(SortStats spellStats : this.highests)
        if(spellStats!=null&&spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      Fighter buffTarget=Function.getInstance().getNearestAllnbrcasemax(this.fight,this.fighter,0,3);
      Fighter target=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,3);

      if(target!=null)
        if(target.isHide())
          target=null;

      PA=this.fighter.getCurPa(this.fight);
      PM=this.fighter.getCurPm(this.fight);

      if(PM>0&&buffTarget==null&&this.attack==0||PM>0&&buffTarget==null&&this.attack==1&&this.boost)
      {
        int num=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ally);
        if(num!=0)
        {
          time=num;
          action=true;
          buffTarget=Function.getInstance().getNearestAllnbrcasemax(this.fight,this.fighter,0,3);
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

      if(PA>0&&target!=null&&!action)
      {
        int beforeAP=this.fighter.getCurPa(this.fight);
        int num=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.cacs);
        int afterAP=this.fighter.getCurPa(this.fight);
        if(beforeAP>afterAP)
        {
          time=num;
          action=true;
          this.attack++;
        }
      }

      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);

      if(this.fighter.getCurPm(this.fight)>0&&!action&&!action2)
      {
        int rng=(int)Math.rint(Math.random()*2);
        if(rng==0)
        {
          this.action2=true;
          action=true;
        } else if(rng==1&&!action) //move far
        {
          int num=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
          if(num!=0)
            time=num;
          this.action2=true;
          action=true;
        } else if(rng==2&&!action) //move close
        {
          int num=Function.getInstance().movediagIfPossible(fight,this.fighter,ennemy);
          if(num!=0)
          {
            time=num;
            action=true;
            target=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,3);
          }
          this.action2=true;
          action=true;
        } else
        {

        }
      }

      if(this.fighter.getCurPa(this.fight)==0&&this.fighter.getCurPm(this.fight)==0)
        this.stop=true;

      addNext(this::decrementCount,time+Config.getInstance().AIDelay);
    } else
    {
      this.stop=true;
    }
  }
}