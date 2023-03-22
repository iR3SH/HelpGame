package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell.SortStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IA83 extends AbstractNeedSpell {
    public IA83(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 100, maxPo = 1, curPm = fighter.getCurPm(fight), curPa = fighter.getCurPa(fight);
            boolean action = false;

            Fighter allie = Function.getInstance().getNearestFriend(fight, fighter);
            Fighter ennemi = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, curPm + 1);

            for (SortStats spellStats : this.highests)
                if (spellStats != null && spellStats.getMaxPO() > maxPo)
                    maxPo = spellStats.getMaxPO();

            Map<Integer, SortStats> allSpells = fighter.getMob().getSpells();

            List<SortStats> invoc = new ArrayList<>();
            invoc.add(allSpells.get(601));
            ArrayList<SortStats> buff = new ArrayList<>();
            buff.add(allSpells.get(602));
            ArrayList<SortStats> frappeCraq = new ArrayList<>();
            frappeCraq.add(allSpells.get(36));

            if(Function.getInstance().ifCanAttackWithSpell(fight, fighter, fighter, allSpells.get(603))){
                if(fight.canCastSpell1(fighter, allSpells.get(603), fighter.getCell(), fighter.getCell().getId())){
                    int timeToWait = fight.tryCastSpell(fighter, allSpells.get(603), fighter.getCell().getId());
                    if(timeToWait > 0){
                        time = timeToWait;
                        action = true;
                    }
                }
            }

            if(Function.getInstance().ifCanAttackWithSpell(fight, fighter, fighter, allSpells.get(512)) && !action){
                if(fight.canCastSpell1(fighter, allSpells.get(512), fighter.getCell(), fighter.getCell().getId())){
                    int timeToWait = fight.tryCastSpell(fighter, allSpells.get(512), fighter.getCell().getId());
                    if(timeToWait > 0){
                        time = timeToWait;
                        action = true;
                    }
                }
            }

            if(Function.getInstance().ifCanAttack(fight, fighter) && !action){
                if(Function.getInstance().invocIfPossible(fight, fighter, invoc)){
                    time = invoc.get(0).getSpell().getDuration();
                    action = true;
                }
            }

            if(Function.getInstance().ifCanAttackWithSpell(fight, fighter, fighter, allSpells.get(511)) && !action){
                if(fight.canCastSpell1(fighter, allSpells.get(511), fighter.getCell(), fighter.getCell().getId())){
                    int timeToWait = fight.tryCastSpell(fighter, allSpells.get(511), fighter.getCell().getId());
                    if(timeToWait > 0){
                        time = timeToWait;
                        action = true;
                    }
                }
            }

            if(Function.getInstance().ifCanAttackWithSpell(fight, fighter, fighter, allSpells.get(513)) && !action){
                if(fight.canCastSpell1(fighter, allSpells.get(513), fighter.getCell(), fighter.getCell().getId())){
                    int timeToWait = fight.tryCastSpell(fighter, allSpells.get(513), fighter.getCell().getId());
                    if(timeToWait > 0){
                        time = timeToWait;
                        action = true;
                    }
                }
            }

            if(allie != null && !action && Function.getInstance().ifCanAttack(fight, fighter)){
                if(Function.getInstance().buffIfPossible(fight, fighter, allie, buff)) {
                    time = buff.get(0).getSpell().getDuration();
                    action = true;
                }
            }

            if(Function.getInstance().ifCanMove(fight, fighter) && Function.getInstance().ifCanAttack(fight, fighter) && !action){
                String value = Function.getInstance().moveToAttackIfPossible2(fight, fighter, frappeCraq);
                if(!value.isEmpty()){
                    int cellId = Integer.parseInt(value.split(";")[0]);
                    SortStats spellStats = allSpells.get(Integer.parseInt(value.split(";")[1]));
                    if(fight.canCastSpell1(fighter, spellStats, fighter.getCell(), cellId)){
                        int timeToWait = fight.tryCastSpell(fighter, spellStats, cellId);
                        if(timeToWait > 0){
                            time = timeToWait;
                            action = true;
                        }
                    }
                }
            }

            if(Function.getInstance().ifCanMove(fight, fighter) && ennemi != null  && !action){
                int timeToWait = Function.getInstance().movecacIfPossible(fight, fighter, ennemi);
                if(timeToWait > 0){
                    time = timeToWait;
                    action = true;
                }
            }

            if(Function.getInstance().ifCanMove(fight, fighter) && ennemi == null && !action){
                int timeToWait = Function.getInstance().moveFarIfPossible(fight, fighter);
                if(timeToWait > 0) {
                    time = 400;
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
