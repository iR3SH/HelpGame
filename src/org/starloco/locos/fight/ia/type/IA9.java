package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

public class IA9 extends AbstractIA
{

  public IA9(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply() {
    if (this.count > 0 && this.fighter.canPlay() && !this.stop) {
      int time = 800;
      Fighter target = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);
      if (target == null)
        return;

      String value = Function.getInstance().moveToAttackIfPossible(this.fight, this.fighter);
      if (!value.isEmpty()) {
        int cellId = Integer.parseInt(value.split(";")[0]);
        SortStats spellStats = this.fighter.getMob().getSpells().get(Integer.parseInt(value.split(";")[1]));

        if (cellId != -1) {
          if (this.fight.canCastSpell1(this.fighter, spellStats, this.fighter.getCell(), cellId)) {
            if (this.fight.tryCastSpell(this.fighter, spellStats, cellId) != 10) {
              time = spellStats.getSpell().getDuration();
            }
          }
        } else if (Function.getInstance().moveFarIfPossible(this.fight, this.fighter) != 0) {
          this.stop = true;
        }
      }

      addNext(this::decrementCount, time);
    } else {
      this.stop = true;
    }
  }
}