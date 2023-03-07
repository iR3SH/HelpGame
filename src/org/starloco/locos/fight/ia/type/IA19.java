package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.common.PathFinding;

public class IA19 extends AbstractIA
{

  private boolean tp=false;

  public IA19(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=1000;
      Fighter friend=Function.getInstance().getNearestFriend(this.fight,this.fighter);
      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);

      int dist1=-1;
      if(friend!=null)
        dist1=PathFinding.getDirBetweenTwoCase(friend.getCell().getId(),ennemy.getCell().getId(),this.fight.getMap(),true);
      int dist2=PathFinding.getDirBetweenTwoCase(this.fighter.getCell().getId(),ennemy.getCell().getId(),this.fight.getMap(),true);

      for(Fighter t : this.fight.getFighters(3))
      {
        if(t!=null&&t.getTeam()==this.fighter.getTeam())
        {
          int tDist=PathFinding.getDistanceBetweenTwoCase(this.fight.getMap(),t.getCell(),ennemy.getCell());
          if(dist2>tDist&&dist1>tDist)
          {
            dist1=tDist;
            friend=t;
          }
        }
      }

      boolean needTp=dist2>dist1;

      if(dist2<=3)
      {
        needTp=false;
        this.tp=true;
      }

      if(friend!=null)
      {
        if(needTp&&!this.tp&&Function.getInstance().tpIfPossibleTynril(this.fight,this.fighter,friend)==0)
        {
          this.tp=true;
        }
        else if(!needTp)
        {
          Function.getInstance().moveNearIfPossible(this.fight,this.fighter,ennemy);
          dist1=-5;
        }

        if(this.fighter.getCurPm(this.fight)>0&&dist1!=-5)
        {
          int dist=PathFinding.getDirBetweenTwoCase(this.fighter.getCell().getId(),ennemy.getCell().getId(),this.fight.getMap(),true);
          if(dist>1)
          {
            Function.getInstance().moveNearIfPossible(this.fight,this.fighter,ennemy);
          }
        }

        if(!Function.getInstance().HealIfPossiblefriend(fight,this.fighter,friend))
        {
          Function.getInstance().attackIfPossibleTynril(this.fight,this.fighter,ennemy);
        }
      }
      else
      {
        if(this.fighter.getCurPm(this.fight)>0)
        {
          int num=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
          if(num!=0)
          {
            time=num;
            ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
          }
          else
            Function.getInstance().attackIfPossibleTynril(this.fight,this.fighter,ennemy);
        }
        else
          Function.getInstance().attackIfPossibleTynril(this.fight,this.fighter,ennemy);
      }

      this.addNext(this::decrementCount,time);
    }
    else
    {
      this.stop=true;
    }
  }
}