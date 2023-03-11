package org.starloco.locos.fight.spells;

import org.starloco.locos.client.Player;
import org.starloco.locos.database.Database;

public class GladiatroolSpells {
    private int Id;
    private Player player;
    private int fullMorphId;
    private String spells;

    public GladiatroolSpells(int Id, Player owner, int fullMorphId, String spells)
    {
        this.Id = Id;
        player = owner;
        this.fullMorphId = fullMorphId;
        this.spells = spells;
    }
    public GladiatroolSpells(Player owner, int fullMorphId, String spells)
    {
        Id = Database.getDynamics().getWorldEntityData().getNextGladiatroolSpellsId();
        player = owner;
        this.fullMorphId = fullMorphId;
        this.spells = spells;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getFullMorphId() {
        return fullMorphId;
    }

    public void setFullMorphId(int fullMorphId) {
        this.fullMorphId = fullMorphId;
    }

    public String getSpells() {
        return spells;
    }

    public void setSpells(String spells) {
        this.spells = spells;
    }
}
