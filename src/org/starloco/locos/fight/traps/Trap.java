/*
 * Decompiled with CFR 0_123.
 */
package org.starloco.locos.fight.traps;

import java.util.ArrayList;
import java.util.List;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.kernel.Constant;

public class Trap {
    private final Fighter caster;
    private final GameCase cell;
    private final byte size;
    private final int spell;
    private final Spell.SortStats trapSpell;
    private final Fight fight;
    private final byte color;
    private boolean isUnHide = false;
    private byte teamUnHide = -1;
    private boolean isPushing = false;
    private final  byte level;
    private final short animationSpell;

    public Trap(Fight fight, Fighter caster, GameCase cell, byte size, Spell.SortStats trapSpell, int spell, byte level) {
        this.fight = fight;
        this.caster = caster;
        this.cell = cell;
        this.spell = spell;
        this.size = size;
        this.trapSpell = trapSpell;
        this.color = Constant.getTrapsColor(spell);
        this.level = level;
        this.animationSpell = Constant.getTrapsAnimation(spell);
        for(final SpellEffect se : trapSpell.getEffects())
        	if(se.getEffectID() == 5)
        	{
        		this.isPushing = true;
        		break;
        	}
    }

    public int getSpell() {
        return this.spell;
    }

    public GameCase getCell() {
        return this.cell;
    }

    public byte getSize() {
        return this.size;
    }

    public Fighter getCaster() {
        return this.caster;
    }
    
    public boolean isUnHide() {
    	return this.isUnHide;
    }

    public void setIsUnHide(final byte team) {
        this.isUnHide = true;
        this.teamUnHide = team;
    }

    public byte getColor() {
        return this.color;
    }

    public void desappear() {
        this.desappear(this.caster.getTeam() + 1);
        if (!this.isUnHide) return;
        this.desappear(this.teamUnHide + 1);
    }
    
    private void desappear(final int team)
    {
    	 final StringBuilder str = new StringBuilder();
         final StringBuilder str2 = new StringBuilder();
         str.append("GDZ-").append(this.cell.getId()).append(";").append(this.size).append(";").append(this.color);
         SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, team, 999, "" + this.caster.getId() + "", str.toString());
         str2.append("GDC").append(this.cell.getId());
         SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, team, 999, "" + this.caster.getId() + "", str2.toString());
    }

    public void appear(byte team) {
        final StringBuilder str = new StringBuilder();
        final StringBuilder str2 = new StringBuilder();
        team += 1;
        str.append("GDZ+").append(this.cell.getId()).append(";").append(this.size).append(";").append(this.color);
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, team, 999, "" + this.caster.getId() + "", str.toString());
        str2.append("GDC").append(this.cell.getId()).append(";Haaaaaaaaz3005;");
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, team, 999, "" + this.caster.getId() + "", str2.toString());
    }

    private  void onTraped(final Fighter target) {    	
        this.fight.getAllTraps().remove(this);
        this.desappear();
        final String str = "" + this.spell + "," + this.cell.getId() + "," + this.animationSpell + "," + this.level + ",1," + this.caster.getId();
        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(this.fight, 7, 306, "" + target.getId() + "", str);
        final Fighter fakeCaster = this.caster.getPlayer() == null ? new Fighter(this.fight, this.caster.getMob()) : new Fighter(this.fight, this.caster.getPlayer());
        fakeCaster.setCell(this.cell);
        final GameCase gc = this.getSize() > 0 ? this.cell : target.getCell();
        this.trapSpell.applySpellEffectToFight(this.fight, fakeCaster, gc, false, true);
        this.fight.verifIfTeamAllDead();
    }

	public boolean isPushing() {
		return isPushing;
	}
	
	public static void doTraps(final Fight fight, final Fighter fighter) {
		if(fighter.isDead()) return;
		final List<Trap> traps = new ArrayList<>(fight.getAllTraps());
		final short currentCell = (short) fighter.getCell().getId();
		short idTrapPushing = -1;
		for (short i = 0; i < traps.size(); ++i) {
			final Trap trap = traps.get(i);
			if(trap.isPushing())
			{
				// On prend le premier piège qui pousse. Cela permet de faire de gros reseau
				if(idTrapPushing == -1 && PathFinding.getDistanceBetween(fight.getMap(), trap.getCell().getId(), currentCell) <= trap.getSize())
					idTrapPushing = i;
				continue;
			}
            if (PathFinding.getDistanceBetween(fight.getMap(), trap.getCell().getId(), currentCell) <= trap.getSize())
                trap.onTraped(fighter);
            if (fighter.isDead() || fight.getState() == Constant.FIGHT_STATE_FINISHED)
                return;
        }
		if(idTrapPushing != -1)
			traps.get(idTrapPushing).onTraped(fighter);
		
	}
	
	public static boolean checkPushingTraps(final Fight fight, final Fighter fighter) {
		if(fighter.isDead()) return false;
		final List<Trap> traps = new ArrayList<>(fight.getAllTraps());
		final short currentCell = (short) fighter.getCell().getId();
		for (short i = 0; i < traps.size(); ++i) {
			final Trap trap = traps.get(i);
            if (trap.isPushing() && PathFinding.getDistanceBetween(fight.getMap(), trap.getCell().getId(), currentCell) <= trap.getSize())
                return true;
        }
		return false;
	}
}

