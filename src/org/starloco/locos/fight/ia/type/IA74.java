package org.starloco.locos.fight.ia.type;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.kernel.Config;

/**
 * Created by Locos on 04/10/2015.
 */
public class IA74 extends AbstractNeedSpell
{

  private int attack=0;
  private boolean hasMovedClose=false;
  private byte hasMovedFar=0;

  public IA74(Fight fight, Fighter fighter, byte count)
  {
    super(fight,fighter,count);
  }

  @Override
  public void apply() {
    if (!this.stop && this.fighter.canPlay() && this.count > 0) {
      int time = 100, maxPo = 1;
      boolean action = false;
      Fighter nearestEnnemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);
      Fighter A = Function.getInstance().getNearestAminbrcasemax(this.fight, this.fighter, 1, 63);// pomax +1;

      for (SortStats S : this.highests)
        if (S != null && S.getMaxPO() > maxPo)
          maxPo = S.getMaxPO();

      Fighter longestEnnemy = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 0, maxPo + 1);//po max+ 1;
      if (longestEnnemy != null)
        if (longestEnnemy.isHide())
          longestEnnemy = null;

      // test triple boost en premier
      if (A != null) {
        for (int i = 0; i < 3; i++) {
          if (Function.getInstance().buffIfPossible(this.fight, this.fighter, A, this.buffs)) {
            time = 400;
            action = true;
          }
        }
      }


      if (this.fighter.getCurPm(this.fight) > 0 && this.attack == 0 && !hasMovedClose) {
        String val = Function.getInstance().moveToAttackIfPossible(this.fight, this.fighter);
        if (!val.isEmpty()) {
          int cellId = Integer.parseInt(val.split(";")[0]);
          SortStats SS = fighter.getMob().getSpells().get(Integer.parseInt(val.split(";")[1]));
          if (fight.canCastSpell1(fighter, SS, fighter.getCell(), cellId)) {
            if (fight.tryCastSpell(fighter, SS, cellId) != 10) {
              time = SS.getSpell().getDuration();
              action = true;
            }
          }
        }
      }
      if (this.fighter.getCurPm(this.fight) > 0 && longestEnnemy == null && this.attack == 0 && !hasMovedClose) {
        int value = Function.getInstance().movediagIfPossible(this.fight, this.fighter, nearestEnnemy);
        if (value != 0) {
          time = value + 1000;
          action = true;
          longestEnnemy = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 0, maxPo + 1);
          A = Function.getInstance().getNearestAminbrcasemax(this.fight, this.fighter, 1, 63);// pomax +1;
          hasMovedClose = true;
        }
      }

      if (this.fighter.getCurPa(this.fight) > 0 && !action && A != null) {
        if (Function.getInstance().buffIfPossible(this.fight, this.fighter, A, this.buffs)) {
          time = 400;
          action = true;
        }
      }

      if (this.fighter.getCurPa(this.fight) > 0 && !action) {
        int beforeAP = this.fighter.getCurPa(this.fight);
        int value = Function.getInstance().attackIfPossible(this.fight, this.fighter, this.highests);
        int afterAP = this.fighter.getCurPa(this.fight);
        if (beforeAP > afterAP) {
          time = value + 200;
          action = true;
          this.attack++;
        } else if (this.fighter.getCurPm(this.fight) > 0 && this.attack == 0) {
          value = Function.getInstance().movediagIfPossible(this.fight, this.fighter, nearestEnnemy);
          if (value != 0) {
            time = value;
            action = true;
            Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 0, maxPo + 1);
          }
        }
      }

      if (this.fighter.getCurPm(this.fight) > 0 && !action && this.attack > 0) {
        if (hasMovedFar == 0) {
          int value = Function.getInstance().moveFarIfPossible(this.fight, this.fighter);
          hasMovedFar++;
          if (value != 0)
            time = value + 1000;
        }
      }

      if (this.fighter.getCurPa(this.fight) == 0 && this.fighter.getCurPm(this.fight) == 0)
        this.stop = true;

      addNext(this::decrementCount, time + Config.getInstance().AIDelay);
    } else {
      this.stop = true;
    }
  }
}