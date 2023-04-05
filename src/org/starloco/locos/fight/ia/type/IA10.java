package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.kernel.Constant;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA10 extends AbstractIA {

  public IA10(Fight fight, Fighter fighter, byte count) {
    super(fight, fighter, count);
  }

  @Override
  public void apply() {
    int time = 100;
    if (count > 0 && this.fighter.canPlay() && !this.stop) {
      Fighter target = Function.getInstance().getNearestEnnemy(fight, this.fighter);
      if (target == null)
        return;

      if (this.fighter.haveState(Constant.ETAT_PORTE)) {
        String result = Function.getInstance().moveToAttackIfPossibleAll(fight, fighter);
        int cellId = Integer.parseInt(result.split(";")[0]);
        SortStats spell = fighter.getMob().getSpells().get(Integer.parseInt(result.split(";")[1]));

        if(fight.canCastSpell1(fighter, spell, fighter.getCell(), cellId)){
          if(fight.tryCastSpell(fighter, spell, cellId) == 0){
            time = spell.getSpell().getDuration();
          }
        }
      }
      else {
        SortStats spell = fighter.getMob().getSpells().get(1675);
        if(fight.canCastSpell1(fighter, spell, fighter.getCell(), fighter.getCell().getId())){
          if(fight.tryCastSpell(fighter, spell, fighter.getCell().getId()) == 0){
            this.stop = true;
          }
        }
      }
      if(!Function.getInstance().ifCanAttack(fight, fighter)){
        this.stop = true;
      }

      addNext(this::decrementCount, time);
    } else {
      this.stop = true;
    }
  }
}