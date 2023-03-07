package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

public class IA38 extends AbstractNeedSpell
{

  private boolean boost=false;

  public IA38(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=100,maxPo=1,maxPoBuff=1;
      boolean action=false;
      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);

      for(Spell.SortStats S : this.highests)
        if(S.getMaxPO()>maxPo)
          maxPo=S.getMaxPO();
      for(Spell.SortStats S : this.buffs)
        if(S.getMaxPO()>maxPo)
          maxPoBuff=S.getMaxPO();

      Fighter L=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
      Fighter C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
      Fighter A=Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,0,maxPoBuff+1);//2 = po min 1 + 1;

      if(maxPo==1)
        L=null;
      if(C!=null)
        if(C.isHide())
          C=null;
      if(L!=null)
        if(L.isHide())
          L=null;

      if(this.fighter.getCurPm(this.fight)>0&&L==null&&C==null&&A==null&&!this.boost)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,0,100));
        if(value!=0)
        {
          time=value;
          action=true;
          L=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
          C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
          A=Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,0,maxPoBuff+1);//2 = po min 1 + 1;
          if(maxPo==1)
            L=null;
        }
      } else if(this.fighter.getCurPm(this.fight)>0&&L==null&&C==null)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
        if(value!=0)
        {
          time=value;
          action=true;
          L=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,1,maxPo+1);// pomax +1;
          C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
          A=Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,0,maxPoBuff+1);//2 = po min 1 + 1;
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
      if(this.fighter.getCurPa(this.fight)>0&&!action&&A!=null)
      {
        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,A,this.buffs))
        {
          time=400;
          action=true;
          this.boost=true;
        }
      }

      if(this.fighter.getCurPa(this.fight)>0&&L!=null&&C==null&&!action)
      {
        int value=Function.getInstance().attackIfPossible(this.fight,this.fighter,this.highests);
        if(value!=-1)
        {
          time=value;
          action=true;
        }
      } else if(this.fighter.getCurPa(this.fight)>0&&C!=null&&!action)
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
          time=value;
          action=true;
        }
      }

      if(this.fighter.getCurPm(this.fight)>0&&!action)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
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