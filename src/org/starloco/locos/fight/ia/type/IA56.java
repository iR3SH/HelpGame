package org.starloco.locos.fight.ia.type;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

import java.util.ArrayList;
import java.util.Map;

import static org.starloco.locos.fight.spells.Spell.SortStats;

public class IA56 extends AbstractNeedSpell {
  public IA56(Fight fight, Fighter fighter, byte count) {
    super(fight, fighter, count);
  }

  @Override
  public void apply() {
    if (!this.stop && this.fighter.canPlay() && this.count > 0) {
      int time = 100, maxPo = 1, curPa = fighter.getCurPa(fight), curPm = fighter.getCurPm(fight);
      boolean action = false;

      for (SortStats spellStats : this.highests)
        if (spellStats.getMaxPO() > maxPo)
          maxPo = spellStats.getMaxPO();

      Map<Integer, SortStats> allSpells = fighter.getMob().getSpells();

      ArrayList<SortStats> embranchement = new ArrayList<>();
      embranchement.add(allSpells.get(483));
      ArrayList<SortStats> tornadeBranches = new ArrayList<>();
      tornadeBranches.add(allSpells.get(484));

      Fighter ennemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);
      Fighter LongRange = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 1, maxPo + this.fighter.getBuffValue(117));// pomax +1;
      Fighter Cac = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 1, 1);

      if (curPa > 0) {
        if (Function.getInstance().ifCanAttackWithSpell(fight, fighter, fighter, allSpells.get(976))) {
          if (fight.canCastSpell1(fighter, allSpells.get(976), fighter.getCell(), fighter.getCell().getId())) {
            int val = fight.tryCastSpell(fighter, allSpells.get(976), fighter.getCell().getId());
            if (val == 0) {
              time = allSpells.get(976).getSpell().getDuration();
              action = true;
            }
          }
        }
      }

      if (Function.getInstance().ifCanAttack(fight, fighter) && !action && LongRange != null) {
        String values = Function.getInstance().moveToAttackIfPossible2(fight, fighter, embranchement);
        if (!values.isEmpty()) {
          int cellId = Integer.parseInt(values.split(";")[0]);
          SortStats spellStats = allSpells.get(Integer.parseInt(values.split(";")[1]));
          if (fight.canCastSpell1(fighter, spellStats, fighter.getCell(), cellId)) {
            int val = fight.tryCastSpell(fighter, spellStats, cellId);
            if (val != 10) {
              time = spellStats.getSpell().getDuration();
              action = true;
            }
          }
        }
      }

      if (Function.getInstance().ifCanAttack(fight, fighter) && !action) {
        Fighter target = PathFinding.getEnnemyInLine(fight.getMap(), fighter.getCell().getId(), fighter, 1, 12);
        if (target != null) {
          SortStats spellStats = allSpells.get(977);
          if (fight.canCastSpell1(fighter, spellStats, fighter.getCell(), fighter.getCell().getId())) {
            int val = fight.tryCastSpell(fighter, spellStats, fighter.getCell().getId());
            if (val == 0) {
              time = spellStats.getSpell().getDuration();
              action = true;
            }
          }
        }
      }

      /*if(Function.getInstance().ifCanAttack(fight, fighter) && !action && Cac != null){ // Non lancé sur Offi
        if(fight.canCastSpell1(fighter, allSpells.get(1021), fighter.getCell(), fighter.getCell().getId())){
          int val = fight.tryCastSpell(fighter, allSpells.get(1021), fighter.getCell().getId());
          if(val == 0){
            time = allSpells.get(1021).getSpell().getDuration();
            action = true;
          }
        }
      }*/

      if (Function.getInstance().ifCanAttack(fight, fighter) && !action && Cac != null) {
        if (fight.canCastSpell1(fighter, allSpells.get(484), fighter.getCell(), fighter.getCell().getId())) {
          int val = fight.tryCastSpell(fighter, allSpells.get(484), fighter.getCell().getId());
          if (val == 0) {
            time = allSpells.get(484).getSpell().getDuration();
            action = true;
          }
        }
      }

      if (Function.getInstance().ifCanAttack(fight, fighter) && !action) {
        String values = Function.getInstance().moveToAttackIfPossible2(fight, fighter, tornadeBranches);
        if (!values.isEmpty()) {
          SortStats spellStats = allSpells.get(Integer.parseInt(values.split(";")[1]));
          if (fight.canCastSpell1(fighter, spellStats, fighter.getCell(), fighter.getCell().getId())) {
            int val = fight.tryCastSpell(fighter, spellStats, fighter.getCell().getId());
            if (val != 10) {
              time = spellStats.getSpell().getDuration();
              action = true;
            }
          }
        }
      }

      if (Function.getInstance().ifCanMove(fight, fighter) && !action && ennemy != null) {
        int val = Function.getInstance().movecacIfPossible(fight, fighter, ennemy);
        if (val > 0) {
          time = val;
          action = true;
        }
      }

      if (Function.getInstance().ifCanMove(fight, fighter) && !action && ennemy != null) {
        if (Function.getInstance().moveNearIfPossible(fight, fighter, ennemy)) {
          time = 400;
        }
      }


      if (curPa == 0 && curPm == 0)
        this.stop = true;
      addNext(this::decrementCount, time);
    } else {
      this.stop = true;
    }
  }
}