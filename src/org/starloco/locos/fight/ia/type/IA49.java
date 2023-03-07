package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;
import org.starloco.locos.fight.spells.Spell;

public class IA49 extends AbstractNeedSpell  {

    private byte attack = 0;
    private boolean boost = false;

    public IA49(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 100, maxPo = 1;
            boolean action = false;

            Fighter E = Function.getInstance().getNearestAllnbrcasemax(this.fight, this.fighter, 0, 100);
            if(this.attack >= 1) E = Function.getInstance().getNearestEnnemy(this.fight, this.fighter);

            for(Spell.SortStats spellStats : this.highests)
                if(spellStats.getMaxPO() > maxPo)
                    maxPo = spellStats.getMaxPO();

            Fighter L = Function.getInstance().getNearestAllnbrcasemax(this.fight, this.fighter, 1, maxPo + 1);// pomax +1;
            Fighter C = Function.getInstance().getNearestAllnbrcasemax(this.fight, this.fighter, 0, 2);//2 = po min 1 + 1;

            if(this.attack >= 1) {
                L = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 1, maxPo + 1);// pomax +1;
                C = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 0, 2);//2 = po min 1 + 1;
            }

            if(C != null) if(C.isHide()) C = null;
            if(L != null) if(L.isHide()) L = null;
            if(maxPo == 1) L = null;

            if(this.fighter.getCurPm(this.fight) > 0 && L == null && C == null) {
                int value = Function.getInstance().moveautourIfPossible(this.fight, this.fighter, E);
                if(value != 0) {
                    time = value;
                    action = true;
                    L = Function.getInstance().getNearestAllnbrcasemax(this.fight, this.fighter, 1, maxPo + 1);// pomax +1;
                    C = Function.getInstance().getNearestAllnbrcasemax(this.fight, this.fighter, 0, 2);//2 = po min 1 + 1;
                    if(this.attack >= 1) {
                        L = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 1, maxPo + 1);// pomax +1;
                        C = Function.getInstance().getNearestEnnemynbrcasemax(this.fight, this.fighter, 0, 2);//2 = po min 1 + 1;
                    }
                    if(maxPo == 1) L = null;
                }
            }
            if(this.fighter.getCurPa(this.fight) > 0 && !action && !this.boost) {
                if (Function.getInstance().buffIfPossible(this.fight, this.fighter, this.fighter, this.buffs)) {
                    time = 1000;
                    action = true;
                    boost = true;
                }
            }
            if(this.fighter.getCurPa(this.fight) > 0 && L != null && C == null && !action) {
                int value = Function.getInstance().attackAllIfPossible(this.fight, this.fighter, this.highests);
                if(value != 0) {
                    time = value;
                    action = true;
                    this.attack++;
                }
            } else if(this.fighter.getCurPa(this.fight) > 0 && C != null && !action) {
                int value = Function.getInstance().attackAllIfPossible(this.fight, this.fighter, this.cacs);
                if(value != 0) {
                    time = value;
                    action = true;
                    this.attack++;
                }
            }
            if(this.fighter.getCurPm(this.fight) > 0 && !action) {
                int value = Function.getInstance().moveautourIfPossible(this.fight, this.fighter, E);
                if(value != 0) time = value;
            }

            if(this.fighter.getCurPa(this.fight) == 0 && this.fighter.getCurPm(this.fight) == 0) this.stop = true;
            addNext(this::decrementCount, time);
        } else {
            this.stop = true;
        }
    }
}