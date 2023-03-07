package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

public class IA39 extends AbstractNeedSpell
{

  private byte attack=0;

  public IA39(Fight fight, Fighter fighter, byte count)
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
      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);

      for(Spell.SortStats spellStats : this.highests)
        if(spellStats.getMaxPO()>maxPo)
          maxPo=spellStats.getMaxPO();

      Fighter C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2); //2 = po min 1 + 1;
      Fighter L=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1); // pomax +1;
      Fighter L2=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,2,5);
      Fighter L3=Function.getInstance().getNearEnnemylignenbrcasemax(this.fight,this.fighter,0,maxPo);

      if(maxPo==1)
        L=null;
      if(C!=null)
        if(C.isHide())
          C=null;
      if(L!=null)
        if(L.isHide())
          L=null;
      
      if(this.fighter.getCurPa(this.fight)>0&&L3!=null&&C==null&&!action)
      {
        int value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        if(value!=-1)
        {
          this.attack++;
          time=value;
          action=true;
        }
      }
      
      if(this.fighter.getCurPm(this.fight)>0&&C==null||this.attack==1&&this.fighter.getCurPm(this.fight)>0)
      {
        int value=Function.getInstance().moveenfaceIfPossible(this.fight,this.fighter,ennemy,maxPo+1);
        if(value!=0)
        {
          time=value;
          action=true;
          L=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
          C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
          if(maxPo==1)
            L=null;
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
          time=1000;
          action=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&L!=null&&C==null&&!action)
      {
        int value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        if(value!=-1)
        {
          this.attack++;
          time=value;
          action=true;
        }
      }
      else if(this.fighter.getCurPa(this.fight)>0&&C!=null&&!action)
      {
        int value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.cacs);
        if(value!=-1)
        {
          time=value;
          action=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&C!=null&&!action)
      {
        int value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        if(value!=-1)
        {
          this.attack++;
          time=value;
          action=true;
        }
        else if(this.fighter.getCurPm(this.fight)>0)
        {
          value=Function.getInstance().moveenfaceIfPossible(this.fight,this.fighter,L2,maxPo+1);
          if(value!=0)
          {
            time=value;
            action=true;
          }
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&!action&&C!=null)
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