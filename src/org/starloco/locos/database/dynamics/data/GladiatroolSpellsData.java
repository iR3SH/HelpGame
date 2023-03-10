package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.fight.spells.GladiatroolSpells;
import org.starloco.locos.game.world.World;
import org.starloco.locos.other.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GladiatroolSpellsData extends AbstractDAO<Object> {
    public GladiatroolSpellsData(HikariDataSource dataSource) {
        super(dataSource);
    }
    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM `gladiatrool_spells`");
            ResultSet RS = result.resultSet;

            while (RS.next()) {
                Player player = World.world.getPlayer(RS.getInt("playerId"));
                if(player != null) {
                    GladiatroolSpells gladiatroolSpells = new GladiatroolSpells(RS.getInt("Id"), player, RS.getInt("fullMorphId"), RS.getString("spells"));
                    World.world.addGladiatroolSpells(player, gladiatroolSpells);
                }
            }
        } catch (SQLException e) {
            super.sendError("GladiatroolSpellsData load", e);
        } finally {
            close(result);
        }
    }

    @Override
    public void load(Object obj) {

    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public boolean update(GladiatroolSpells gladiatroolSpells) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `gladiatrool_spells` SET `spells` = ? WHERE playerId = ? AND fullMorphId = ? ;");
            p.setString(1, gladiatroolSpells.getSpells());
            p.setInt(2, gladiatroolSpells.getPlayer().getId());
            p.setInt(3, gladiatroolSpells.getFullMorphId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("GladiatroolSpellsData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean delete(GladiatroolSpells gladiatroolSpells)
    {
        PreparedStatement p = null;
        try{
            p = getPreparedStatement("DELETE FROM gladiatrool_spells WHERE id = ?");
            p.setInt(1, gladiatroolSpells.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("GladiatroolSpellsData delete", e);
            return false;
        } finally {
            close(p);
        }
        return true;
    }

    public void add(GladiatroolSpells gladiatroolSpells) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `gladiatrool_spells` VALUES (?,?,?,?);");
            p.setInt(1, gladiatroolSpells.getId());
            p.setInt(2, gladiatroolSpells.getPlayer().getId());
            p.setInt(3, gladiatroolSpells.getFullMorphId());
            p.setString(4, gladiatroolSpells.getSpells());
            execute(p);
        } catch (SQLException e) {
            super.sendError("GladiatroolSpellsData add", e);
        } finally {
            close(p);
        }
    }
}
