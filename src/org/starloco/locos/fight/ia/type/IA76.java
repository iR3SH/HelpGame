package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.kernel.Config;

public class IA76 extends AbstractNeedSpell
{
  private int attack=0;
  private int summon=0;
  private int moveCC=0;
  private boolean boost=false;

  public IA76(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      int PA=this.fighter.getCurPa(this.fight),
          PM=this.fighter.getCurPm(this.fight),time=100,maxPo=1;
      boolean action=false;

      if(this.fighter.getMob().getPa()<PA)
        this.boost=true;

      for(SortStats spellStats : this.highests)
        if(spellStats!=null&&spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      Fighter target=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,3);

      if(target!=null)
        if(target.isHide())
          target=null;

      PA=this.fighter.getCurPa(this.fight);
      PM=this.fighter.getCurPm(this.fight);

      if(PM>0&&target==null&&this.attack==0||PM>0&&target==null&&this.attack==1&&this.boost)
      {
        int num=Function.getInstance().movecacIfPossible(fight,this.fighter,ennemy);
        if(num!=0)
        {
          time=num;
          action=true;
          this.moveCC++;
          target=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,3);
        }
      }

      if(PM>0&&target==null&&this.attack==0&&this.moveCC==0||PM>0&&target==null&&this.attack==1&&this.boost&&this.moveCC==0)
      {
        int num=Function.getInstance().movediagIfPossible(fight,this.fighter,ennemy);
        if(num!=0)
        {
          time=num;
          action=true;
          target=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,3);
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&!action)
      {
        if(Function.getInstance().invocIfPossible(this.fight,this.fighter,this.invocations))
        {
          time=600;
          action=true;
          this.summon++;
        }
      }

      if(PA>0&&target!=null&&!action&&this.moveCC>0)
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

      if(PA>0&&target!=null&&!action)
      {
        int beforeAP=this.fighter.getCurPa(this.fight);
        int num=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        int afterAP=this.fighter.getCurPa(this.fight);
        if(beforeAP>afterAP)
        {
          time=num;
          action=true;
          this.attack++;
        }
      }

      if(PM>0&&!action&&this.attack>0||PM>0&&!action&&this.summon>0)
      {
        int num=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
        if(num!=0)
          time=num;
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