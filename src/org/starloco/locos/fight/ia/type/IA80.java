package org.starloco.locos.fight.ia.type;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class IA80 extends AbstractNeedSpell {
    public IA80(Fight fight, Fighter fighter, byte count)
    {
        super(fight,fighter,count);
    }

    @Override
    public void apply() {
        if(!this.stop&&this.fighter.canPlay()&&this.count>0) {
            int time = 100, maxPo = 8;
            boolean action = false;
            Fighter ennemy = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);

            Map<Integer, Spell.SortStats> allSpells = fighter.getMob().getSpells();

            for (Spell.SortStats spellStats : allSpells.values())
                if (spellStats.getMaxPO() > maxPo)
                    maxPo = spellStats.getMaxPO();

            Fighter L = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, maxPo + 1);// pomax +1;
            Fighter C = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 2);//2 = po min 1 + 1;
            Fighter cac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 1);
            Fighter forMythos = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 5 + fighter.getCurPm(fight));
            Fighter longForTofu = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, maxPo + fighter.getCurPm(fight));

            if (maxPo == 1)
                L = null;
            if (C != null)
                if (C.isHide())
                    C = null;
            if (L != null)
                if (L.isHide())
                    L = null;
            if (Function.getInstance().ifCanAttack(fight, fighter)) {
                if (Function.getInstance().buffIfPossible(this.fight, this.fighter, this.fighter)) {
                    time = allSpells.get(261).getSpell().getDuration();
                    action = true;
                }
            }
            if (fighter.getCurPa(fight) >= 15 && !action) {
                if (Function.getInstance().buffIfPossibleMinotot(fight, fighter, fighter, allSpells.get(814))) {
                    time = allSpells.get(814).getSpell().getDuration();
                    action = true;
                }
            }
            /*if (longForTofu != null && !action) {
                if (Function.getInstance().ifCanAttackWithSpell(fight, fighter, longForTofu, allSpells.get(245))) {
                    if (Function.getInstance().moveToAction(fight, fighter, longForTofu, (short) 0, (ArrayList<Integer>) Arrays.asList(261, 812, 813, 814), 0, true) == 3) {
                        time = allSpells.get(245).getSpell().getDuration();
                        action = true;
                        cac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 1);
                        forMythos = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 4 + fighter.getCurPm(fight));
                        longForTofu = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, maxPo + fighter.getCurPm(fight));
                    }
                }
            }*/
           if(forMythos != null && !action){
                if(Function.getInstance().ifCanAttackWithSpell(fight, fighter, forMythos, allSpells.get(813)))
                {
                    String value = Function.getInstance().moveToAttackIfPossible2(this.fight,this.fighter);

                    C = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 2);
                    cac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 1);
                    forMythos = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 4 + fighter.getCurPm(fight));
                    longForTofu = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, maxPo + fighter.getCurPm(fight));

                    if(!value.isEmpty()) {
                        int cellId = Integer.parseInt(value.split(";")[0]), spellId = Integer.parseInt(value.split(";")[1]);
                        Spell.SortStats spellStats = this.fighter.getMob().getSpells().get(spellId);

                        if(spellStats != null) {

                            if (this.fight.canCastSpell1(this.fighter, spellStats, this.fighter.getCell(), cellId)) {

                                if (this.fight.tryCastSpell(this.fighter, spellStats, cellId) == 0) {

                                    time = spellStats.getSpell().getDuration();
                                    action = true;
                                }
                            }
                        }
                    }
                }
            }

            if (C != null && !action) {
                if (Function.getInstance().ifCanAttackWithSpell(fight, fighter, C, allSpells.get(812))) {
                    if(PathFinding.getDistanceBetween(fight.getMap(), fighter.getCell().getId(), C.getCell().getId()) == 1)
                    {
                        if (Function.getInstance().attackIfPossibleMinotot(fight, fighter, fighter) == 0) {
                            time = allSpells.get(812).getSpell().getDuration();
                            action = true;
                        }
                    }
                }
            }

            if (Function.getInstance().ifCanMove(fight, fighter) && !action) {
                Fighter target = null;
                for (Fighter f : fight.getFighters(fighter.getOtherTeam())) {
                    if (target == null) {
                        target = f;
                    } else {
                        if (PathFinding.getDistanceBetween(fight.getMap(), fighter.getCell().getId(), target.getCell().getId())
                                < PathFinding.getDistanceBetween(fight.getMap(), fighter.getCell().getId(), f.getCell().getId())) {
                            target = f;
                        }
                    }
                }
                if(target != null) {
                    if (PathFinding.getDistanceBetween(fight.getMap(), fighter.getCell().getId(), target.getCell().getId()) != 1) {
                        int duration = Function.getInstance().movecacIfPossible(this.fight, this.fighter, target);
                        if(duration != 0){
                            time = duration;
                            C = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 2);
                            cac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 1);
                            forMythos = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 0, 4 + fighter.getCurPm(fight));
                            longForTofu = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, maxPo + fighter.getCurPm(fight));
                        }
                    }
                }
            }

            if (!Function.getInstance().ifCanMove(fight, fighter) && !Function.getInstance().ifCanAttack(fight, fighter))
                this.stop = true;
            addNext(this::decrementCount, time);
        }
        else {
            this.stop=true;
        }
    }
}
