package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;

public class IA6 extends AbstractIA
{

  public IA6(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      if(!Function.getInstance().invocIfPossible(this.fight,this.fighter))
      {
        Fighter friend=Function.getInstance().getNearestFriend(this.fight,this.fighter);
        Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);

        if(!Function.getInstance().HealIfPossible(this.fight,this.fighter,false))
        {
          if(!Function.getInstance().buffIfPossible(this.fight,this.fighter,friend))
          {
            if(!Function.getInstance().buffIfPossible(this.fight,this.fighter,this.fighter))
            {
              if(!Function.getInstance().HealIfPossible(this.fight,this.fighter,true))
              {
                int attack=Function.getInstance().attackIfPossibleAll(fight,this.fighter,ennemy);

                if(attack!=0)
                {
                  if(attack==5)
                    this.stop=true;
                  if(Function.getInstance().moveFarIfPossible(this.fight,this.fighter)!=0)
                    this.stop=true;
                }
              }
            }
          }
        }
      }

      addNext(this::decrementCount,1000);
    }
    else
    {
      this.stop=true;
    }
  }
}