package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

import java.util.ArrayList;
import java.util.Map;

public class IA84 extends AbstractNeedSpell {
    public IA84(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 100, maxPo = 1, curPm = fighter.getCurPm(fight), curPa = fighter.getCurPa(fight);
            boolean action = false;

            Fighter ennemi = Function.getInstance().getNearestEnnemy(fight, fighter);

            for (Spell.SortStats spellStats : this.highests)
                if (spellStats != null && spellStats.getMaxPO() > maxPo)
                    maxPo = spellStats.getMaxPO();

            Map<Integer, Spell.SortStats> allSpells = fighter.getMob().getSpells();
            ArrayList<Spell.SortStats> lancerMagistrale = new ArrayList<>();
            lancerMagistrale.add(allSpells.get(1027));
            ArrayList<Spell.SortStats> frappeMagistrale = new ArrayList<>();
            frappeMagistrale.add(allSpells.get(1028));

            if(Function.getInstance().ifCanAttackWithSpell(fight, fighter, fighter, allSpells.get(1026))){
                if(fight.canCastSpell1(fighter, allSpells.get(1026), fighter.getCell(), fighter.getCell().getId())){
                    int timeToWait = fight.tryCastSpell(fighter, allSpells.get(1026), fighter.getCell().getId());
                    if(timeToWait > 0){
                        time = timeToWait;
                        action = true;
                    }
                }
            }

            if(fighter.hasBuffFromSpell(1026) && Function.getInstance().ifCanAttackWithSpell(fight, fighter, fighter, allSpells.get(1028)) && !action){
                String value = Function.getInstance().moveToAttackIfPossible2(fight, fighter, frappeMagistrale);
                if(!value.isEmpty()){
                    int cellId = Integer.parseInt(value.split(";")[0]);
                    Spell.SortStats spellStats = allSpells.get(Integer.parseInt(value.split(";")[1]));
                    if(fight.canCastSpell1(fighter, spellStats, fighter.getCell(), cellId)){
                        int timeToWait = fight.tryCastSpell(fighter, spellStats, cellId);
                        if(timeToWait > 0){
                            time = timeToWait;
                            action = true;
                        }
                    }
                }
            }

            if(Function.getInstance().ifCanAttackWithSpell(fight, fighter, fighter, allSpells.get(1027)) && !action){
                String value = Function.getInstance().moveToAttackIfPossible2(fight, fighter, lancerMagistrale);
                if(!value.isEmpty()){
                    int cellId = Integer.parseInt(value.split(";")[0]);
                    Spell.SortStats spellStats = allSpells.get(Integer.parseInt(value.split(";")[1]));
                    if(fight.canCastSpell1(fighter, spellStats, fighter.getCell(), cellId)){
                        int timeToWait = fight.tryCastSpell(fighter, spellStats, cellId);
                        if(timeToWait > 0){
                            time = timeToWait;
                            action = true;
                        }
                    }
                }
            }

            if(Function.getInstance().ifCanMove(fight, fighter) && ennemi != null && !action){
                if(Function.getInstance().moveNearIfPossible(fight, fighter, ennemi)){
                    time = 400;
                    action = true;
                }
            }

            if(Function.getInstance().ifCanMove(fight, fighter) && ennemi != null && !action){
                int timeToWait = Function.getInstance().movecacIfPossible(fight, fighter, ennemi);
                if(timeToWait > 0) {
                    time = 400;
                }
            }


            if(!Function.getInstance().ifCanAttack(fight, fighter) && !Function.getInstance().ifCanMove(fight, fighter))
                stop = true;
            addNext(this::decrementCount,time);
        }
        else
        {
            stop = true;
        }
    }
}
