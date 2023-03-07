package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

public class IA34 extends AbstractNeedSpell
{

  private int attack=0;

  public IA34(Fight fight, Fighter fighter, byte count)
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

      Fighter ennemy0=Function.getInstance().getNearestAllnbrcasemax(this.fight,this.fighter,0,100);
      if(this.attack>=1)
        ennemy0=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);

      for(Spell.SortStats spellStats : this.highests)
        if(spellStats!=null&&spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      Fighter ennemy1=Function.getInstance().getNearestAllnbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
      Fighter ennemy2=Function.getInstance().getNearestAllnbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;

      if(this.attack>=1)
      {
        ennemy1=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
        ennemy2=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
      }

      if(ennemy2!=null)
        if(ennemy2.isHide())
          ennemy2=null;
      if(ennemy1!=null)
        if(ennemy1.isHide())
          ennemy1=null;
      if(maxPo==1)
        ennemy1=null;

      if(this.fighter.getCurPm(this.fight)>0&&ennemy1==null&&ennemy2==null)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy0);
        if(value!=0)
        {
          time=value;
          action=true;
          ennemy1=Function.getInstance().getNearestAllnbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
          ennemy2=Function.getInstance().getNearestAllnbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
          if(this.attack>=1)
          {
            ennemy1=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
            ennemy2=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
          }
          if(maxPo==1)
            ennemy1=null;
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

      if(this.fighter.getCurPa(this.fight)>0&&ennemy1!=null&&ennemy2==null&&!action)
      {
        int value=Function.getInstance().attackAllIfPossible(this.fight,this.fighter,this.highests);
        if(value!=0)
        {
          time=value;
          action=true;
          this.attack++;
        }
      } else if(this.fighter.getCurPa(this.fight)>0&&ennemy2!=null&&!action)
      {
        int value=Function.getInstance().attackAllIfPossible(this.fight,this.fighter,this.cacs);
        if(value!=0)
        {
          time=value;
          action=true;
          this.attack++;
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&!action)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy0);
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