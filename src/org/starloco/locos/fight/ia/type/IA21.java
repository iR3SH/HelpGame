package org.starloco.locos.fight.ia.type;

import java.util.ArrayList;
import java.util.List;

import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.ia.AbstractIA;
import org.starloco.locos.fight.ia.util.Function;

/** 5 years later
 * corrected by AT... ATCHOUM!.......... SNURF
 */

public class IA21 extends AbstractIA  {
    private List<Fighter> firstAttack = new ArrayList<Fighter>();
    private List<Fighter> secondAttack = new ArrayList<Fighter>();
    
    public IA21(final Fight fight, final Fighter fighter, final byte count) {
        super(fight, fighter, count);
        
    }

    @Override
    public void apply() {
        if (!this.stop && this.fighter.canPlay() && this.count > 0) {
            boolean action = false;
            
            if(Function.getInstance().buffIfPossibleKrala(this.fight, this.fighter, this.fighter))
                action = true;
            
            if(!action && Function.getInstance().invoctantaIfPossible(this.fight, this.fighter))
                action = true;
            
            if(!action)
            {
                final Fighter nearEnnemy = Function.getEnnemyWithDistance(this.fight, this.fighter, 1, 8,
                        this.firstAttack);
                
                final Fighter farEnnemy = Function.getEnnemyWithDistance(this.fight, this.fighter, 3, 60,
                        this.secondAttack);
                if (nearEnnemy != null && this.attackNearIfPossible(this.fight, this.fighter, nearEnnemy) == 0) {
                    this.firstAttack.add(nearEnnemy);
                    action = true;
                }
                if (!action && farEnnemy != null && this.attackFarIfPossible(this.fight, this.fighter, farEnnemy) == 0) {
                    this.secondAttack.add(farEnnemy);
                    action = true;
                }
                if (!action && !this.fighter.haveState(7)) {
                    this.fight.tryCastSpell(this.fighter, Function.findSpell(this.fighter, 1279), this.fighter.getCell().getId());
                    action = true;
                }
            }
            if (!action)this.stop=true;
            addNext(this::decrementCount, 1250);
        } else {
            this.stop = true;
        }
        
    }
    
    public int attackNearIfPossible(Fight fight, Fighter fighter, Fighter target) {
        if (fight == null || fighter == null || target == null) {
            return 10;
        }
        return fight.tryCastSpell(fighter, Function.findSpell(fighter, 1104), target.getCell().getId());
    }

    public int attackFarIfPossible(Fight fight, Fighter fighter, Fighter target) {
        if (fight == null || fighter == null || target == null) {
            return 10;
        }
        return fight.tryCastSpell(fighter, Function.findSpell(fighter, 1105), target.getCell().getId());
    }
}