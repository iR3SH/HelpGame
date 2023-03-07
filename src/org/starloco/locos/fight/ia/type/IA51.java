package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;

public class IA51 extends AbstractNeedSpell
{

  public IA51(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=100;
      boolean action=false;

      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      Fighter C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
      Fighter A=Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,0,5);//2 = po min 1 + 1;
      Fighter A1=Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;

      if(C!=null&&C.isHide())
        C=null;
      if(this.fighter.getCurPm(this.fight)>0&&C==null&&A==null)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,A);
        if(value!=0)
        {
          time=value;
          action=true;
          C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
          A=Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,0,5);//2 = po min 1 + 1;
          A1=Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
        }
      } else if(this.fighter.getCurPm(this.fight)>0&&C==null)
      {
        int value=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
        if(value!=0)
        {
          time=value;
          action=true;
          C=Function.getInstance().getNearestEnnemynbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
          A=Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,0,5);//2 = po min 1 + 1;
          A1=Function.getInstance().getNearestAminbrcasemax(this.fight,this.fighter,0,2);//2 = po min 1 + 1;
        }
      }
      if(this.fighter.getCurPa(this.fight)>0&&!action&&A1!=null)
      {
        if(Function.getInstance().buffIfPossible(this.fight,this.fighter,A1,this.buffs))
        {
          time=1000;
          action=true;
        }
      }
      if(this.fighter.getCurPa(this.fight)>0&&C!=null&&!action)
      {
        int value=Function.getInstance().attackIfPossibleBuveur(this.fight,this.fighter,C);
        if(value!=0)
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