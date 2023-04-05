package org.starloco.locos.fight.ia.type;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractNeedSpell;
import org.starloco.locos.fight.ia.util.Function;

import java.util.List;

public class IA13 extends AbstractNeedSpell {
    public IA13(Fight fight, Fighter fighter, byte count) {
        super(fight, fighter, count);
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            int time = 100;

            if(Function.getInstance().ifCanAttack(fight, fighter)){
                if(fight.canCastSpell1(fighter, fighter.getMob().getSpells().get(1687), fighter.getCell(), fighter.getCell().getId())){
                    if(fight.tryCastSpell(fighter, fighter.getMob().getSpells().get(1687), fighter.getCell().getId()) == 0){
                        time = fighter.getMob().getSpells().get(1687).getSpell().getDuration();
                    }
                }
            }

            if (!Function.getInstance().ifCanAttack(fight, fighter)) {
                this.stop = true;
            }
            addNext(this::decrementCount, time);
        }
        else {
            this.stop = true;
        }
    }
}
