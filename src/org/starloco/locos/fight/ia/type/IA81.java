package org.starloco.locos.fight.ia.type;

import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.Spell.SortStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IA81 extends AbstractNeedSpell {
    public IA81(Fight fight, Fighter fighter, byte count)
    {
        super(fight,fighter,count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 100, maxPo = 1;
            boolean action = false;

            for (SortStats spellStats : this.highests)
                if (spellStats.getMaxPO() > maxPo)
                    maxPo = spellStats.getMaxPO();

            int curPm = fighter.getCurPm(fight), curPa = fighter.getCurPa(fight);
            Map<Integer, SortStats> allSpells = fighter.getMob().getSpells();

            Fighter Nearest = Function.getInstance().getNearestEnnemy(fight, fighter);
            Fighter Cac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, 1);
            Fighter DeplaceToCac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, 1 + curPm);
            Fighter ForTofu = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, maxPo + curPm);
            Fighter Allies = Function.getInstance().getNearestFriend(fight, fighter);

            if(Allies != null) {
                if (Function.getInstance().ifCanAttackWithSpell(fight, fighter, Allies, allSpells.get(246))) {
                    SortStats spellStats = allSpells.get(246);
                    if (spellStats != null) {

                        if (fight.canCastSpell1(fighter, spellStats, fighter.getCell(), Allies.getCell().getId())) {

                            if (fight.tryCastSpell(fighter, spellStats, Allies.getCell().getId()) == 0) {

                                time = spellStats.getSpell().getDuration();
                                action = true;
                            }
                        }
                    }
                }
            }

            if(Cac == null && DeplaceToCac == null && ForTofu != null && !action) {
                if (Function.getInstance().ifCanAttackWithSpell(fight, fighter, ForTofu, allSpells.get(245))) {
                    ArrayList<SortStats> sortStats = new ArrayList<>();
                    sortStats.add(allSpells.get(245));
                    String value = Function.getInstance().moveToAttackIfPossible2(fight, fighter, sortStats);

                    Nearest = Function.getInstance().getNearestEnnemy(fight, fighter);
                    Cac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, 1);
                    DeplaceToCac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, 1 + curPm);
                    ForTofu = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, maxPo + curPm);
                    Allies = Function.getInstance().getNearestFriend(fight, fighter);

                    if(!value.isEmpty()) {
                        int cellId = Integer.parseInt(value.split(";")[0]), spellId = Integer.parseInt(value.split(";")[1]);
                        SortStats spellStats = allSpells.get(spellId);
                        char dir = PathFinding.getDirBetweenTwoCase(ForTofu.getCell().getId(), fighter.getCell().getId(), fight.getMap(), true);
                        cellId = PathFinding.getCaseIDFromDirrection(cellId, dir, fight.getMap());
                        if(spellStats != null) {

                            if (this.fight.canCastSpell1(fighter, spellStats, fighter.getCell(), cellId)) {

                                if (this.fight.tryCastSpell(this.fighter, spellStats, cellId) != 10) {

                                    time = spellStats.getSpell().getDuration();
                                    action = true;
                                }
                            }
                        }
                    }
                }
            }

            if(DeplaceToCac != null && !action) {
                if(Function.getInstance().ifCanAttackWithSpell(fight, fighter, DeplaceToCac, allSpells.get(229)))
                {
                    ArrayList<SortStats> sortStats = new ArrayList<>();
                    sortStats.add(allSpells.get(229));
                    String value = Function.getInstance().moveToAttackIfPossible2(fight, fighter, sortStats);

                    Nearest = Function.getInstance().getNearestEnnemy(fight, fighter);
                    Cac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, 1);
                    DeplaceToCac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, 1 + curPm);
                    ForTofu = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, maxPo + curPm);
                    Allies = Function.getInstance().getNearestFriend(fight, fighter);

                    if(!value.isEmpty()) {
                        int cellId = Integer.parseInt(value.split(";")[0]);
                        SortStats spellStats = allSpells.get(229);

                        if(spellStats != null) {

                            if (this.fight.canCastSpell1(this.fighter, spellStats, fighter.getCell(), cellId)) {

                                if (this.fight.tryCastSpell(this.fighter, spellStats, cellId) != 10) {

                                    time = spellStats.getSpell().getDuration();
                                    action = true;
                                }
                            }
                        }
                    }
                }
            }
            if(DeplaceToCac != null && !action){
                if(Function.getInstance().ifCanMove(fight, fighter)){
                    int temps = Function.getInstance().movecacIfPossible(fight, fighter, DeplaceToCac);
                    if(temps != 0) {
                        time = temps;
                        action = true;

                        Nearest = Function.getInstance().getNearestEnnemy(fight, fighter);
                        Cac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, 1);
                        DeplaceToCac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, 1 + curPm);
                        ForTofu = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, maxPo + curPm);
                        Allies = Function.getInstance().getNearestFriend(fight, fighter);
                    }
                }
            }
            if(Cac == null && DeplaceToCac == null && Nearest != null){
                if(Function.getInstance().ifCanMove(fight, fighter)){
                    if(Function.getInstance().moveNearIfPossible(fight, fighter, Nearest)) {
                        time = 800;
                        action = true;

                        Nearest = Function.getInstance().getNearestEnnemy(fight, fighter);
                        Cac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, 1);
                        DeplaceToCac = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, 1 + curPm);
                        ForTofu = Function.getInstance().getNearestEnnemynbrcasemax(fight, fighter, 1, maxPo + curPm);
                        Allies = Function.getInstance().getNearestFriend(fight, fighter);
                    }
                }
            }

            if(curPa == 0 && curPm == 0)
                stop = true;
            addNext(this::decrementCount, time);
        }
        else {
            stop=true;
        }
    }
}
