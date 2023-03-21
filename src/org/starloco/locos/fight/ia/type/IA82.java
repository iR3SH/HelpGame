package org.starloco.locos.fight.ia.type;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IA82 extends AbstractNeedSpell {

    public IA82(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 100, maxPo = 1, curPm = fighter.getCurPm(fight), curPa = fighter.getCurPa(fight);
            boolean action = false;

            for (SortStats spellStats : this.highests)
                if (spellStats != null && spellStats.getMaxPO() > maxPo)
                    maxPo = spellStats.getMaxPO();

            Map<Integer, SortStats> allSpells = fighter.getMob().getSpells();

            List<SortStats> coquille = new ArrayList<>();
            coquille.add(allSpells.get(264));

            List<SortStats> mobs = new ArrayList<>();
            mobs.add(allSpells.get(973));

            Fighter Nearest = Function.getInstance().getNearest(fight, fighter);

            if(Function.getInstance().ifCanAttack(fight, fighter)) {
                if (Function.getInstance().invocIfPossibleCroca(fight, fighter, allSpells.get(264))){
                    time = coquille.get(0).getSpell().getDuration();
                    action = true;
                }
            }

            if(Function.getInstance().ifCanAttack(fight, fighter) && !action) {
                if (Function.getInstance().invocIfPossibleCroca(fight, fighter, allSpells.get(973))){
                    time = mobs.get(0).getSpell().getDuration();
                    action = true;
                }
            }

            if(Function.getInstance().ifCanAttackWithSpell(fight, fighter, fighter, allSpells.get(972)) && !action){
                if(fight.canCastSpell1(fighter, allSpells.get(972), fighter.getCell(), fighter.getCell().getId())){
                    if(fight.tryCastSpell(fighter, allSpells.get(972), fighter.getCell().getId()) == 0){
                        time = allSpells.get(972).getSpell().getDuration();
                        action = true;
                    }
                }
            }

            if(Nearest != null && !action){
                if(Function.getInstance().ifCanAttackWithSpell(fight, fighter, Nearest, allSpells.get(971))){
                    String value = Function.getInstance().moveToAttackIfPossible2(fight, fighter);

                    if(!value.isEmpty()){
                        int cellId = Integer.parseInt(value.split(";")[0]), spellId = Integer.parseInt(value.split(";")[1]);
                        SortStats spellStats = allSpells.get(spellId);

                        if(spellStats != null) {

                            if (this.fight.canCastSpell1(fighter, spellStats, fighter.getCell(), cellId)) {

                                if (this.fight.tryCastSpell(this.fighter, spellStats, cellId) == 0) {

                                    time = spellStats.getSpell().getDuration();
                                    action = true;
                                    Nearest = Function.getInstance().getNearest(fight, fighter);
                                }
                            }
                        }
                    }
                }
            }
            if(Nearest != null & !action) {
                if (Function.getInstance().ifCanMove(fight, fighter)) {
                    if(Function.getInstance().moveNearIfPossible(fight, fighter, Nearest)){
                        time = 400;
                        action = true;
                        Nearest = Function.getInstance().getNearest(fight, fighter);
                    }
                }
            }
            if(Nearest != null & !action) {
                if (Function.getInstance().ifCanMove(fight, fighter)) {
                    int timed = Function.getInstance().movecacIfPossible(fight, fighter, Nearest);
                    if(timed > 0){
                        time = 400;
                        action = true;
                        Nearest = Function.getInstance().getNearest(fight, fighter);
                    }
                }
            }

            if(curPa == 0 && curPm == 0)
                stop = true;
            addNext(this::decrementCount,time);
        }
        else
        {
            stop = true;
        }
    }
}
