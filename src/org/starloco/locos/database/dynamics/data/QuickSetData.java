package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.QuickSet;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.fight.spells.GladiatroolSpells;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuickSetData extends AbstractDAO<Object> {
    public QuickSetData(HikariDataSource dataSource) {
        super(dataSource);
    }
    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM `quicksets`");
            ResultSet RS = result.resultSet;

            while (RS.next()) {
                Player player = World.world.getPlayer(RS.getInt("playerId"));
                if(player != null) {
                    QuickSet quickset = new QuickSet(player, RS.getInt("pos"), RS.getInt("iconId"), RS.getString("objects"), RS.getString("name"));
                    World.world.addQuickSet(player, quickset);
                }
            }
        } catch (SQLException e) {
            super.sendError("QuickSetData load", e);
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

    public boolean update(QuickSet quickset) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `quicksets` SET `name` = ?, `objects` = ?, `iconId` = ? WHERE playerId = ? AND pos = ? ;");
            p.setString(1, quickset.getName());
            p.setString(2, quickset.getObjectsString());
            p.setInt(3, quickset.getIconId());
            p.setInt(4, quickset.getPlayer().getId());
            p.setInt(5, quickset.getPosId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("QuickSetData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean delete(QuickSet quickset)
    {
        PreparedStatement p = null;
        try{
            p = getPreparedStatement("DELETE FROM quicksets WHERE playerId = ? AND pos = ? ;");
            p.setInt(1, quickset.getPlayer().getId());
            p.setInt(2, quickset.getPosId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("QuickSetData delete", e);
            return false;
        } finally {
            close(p);
        }
        return true;
    }

    public void add(QuickSet quickset) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `quicksets` VALUES (?,?,?,?,?);");
            p.setInt(1, quickset.getPlayer().getId());
            p.setInt(2, quickset.getPosId());
            p.setString(3, quickset.getName());
            p.setString(4, quickset.getObjectsString());
            p.setInt(5, quickset.getIconId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("QuickSetData add", e);
        } finally {
            close(p);
        }
    }
}
