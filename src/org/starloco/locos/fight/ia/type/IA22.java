package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.Spell.SortStats;

import java.util.Map;

public class IA22 extends AbstractIA
{
  private boolean attack=false;

  public IA22(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply()
  {
    if(!this.stop&&this.fighter.canPlay()&&this.count>0)
    {
      int time=600;
      boolean action = false;
      Fighter ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
      Map<Integer, SortStats> allSpells = fighter.getMob().getSpells();

      if(Function.getInstance().ifCanAttack(fight, fighter)) {
        if(fight.canCastSpell1(fighter, allSpells.get(1038), fighter.getCell(), fighter.getCell().getId())){
          if(fight.tryCastSpell(fighter, allSpells.get(1038), fighter.getCell().getId()) == 0){
            time = allSpells.get(1038).getSpell().getDuration();
            action = true;
          }
        }
      }

      if(Function.getInstance().IfPossibleRasboulvulner(this.fight,this.fighter,this.fighter)==0 && !action)
      {
        if(attack) //has already attacked, allow it to move away
        {
          if(Function.getInstance().tpIfPossibleRasboul(this.fight,this.fighter,ennemy)==0) //tele again if possible
          {
            //nothing
          }
          else
          {
            int num=Function.getInstance().moveFarIfPossible(this.fight,this.fighter);
            if(num!=0)
              time=num;
            else
              Function.getInstance().invocIfPossible(this.fight,this.fighter);
          }
        }
        else //hasnt attacked yet, dont allow to move away
        {
          if(Function.getInstance().tpIfPossibleRasboul(this.fight,this.fighter,ennemy)==0) //attack worked
            attack=true;
          else
          {
            int num=Function.getInstance().moveautourIfPossible(this.fight,this.fighter,ennemy);
            if(num!=0)
            {
              time=num;
              ennemy=Function.getInstance().getNearestEnnemy(this.fight,this.fighter);
            }
            else
              Function.getInstance().invocIfPossible(this.fight,this.fighter);
          }
        }
      }

      addNext(this::decrementCount,time);
    }
    else
    {
      this.stop=true;
    }
  }
}