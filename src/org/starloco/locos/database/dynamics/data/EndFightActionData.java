package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.other.Action;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EndFightActionData extends AbstractDAO<Object> {
    public EndFightActionData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public int load() {
        Result result = null;
        int nbr = 0;
        try {
            result = getData("SELECT * FROM endfight_action");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                GameMap map = World.world.getMap(RS.getShort("map"));
                if (map == null)
                    continue;
                map.addEndFightAction(RS.getInt("fighttype"), new Action(RS.getInt("action"), RS.getString("args"), RS.getString("cond"), null));
                nbr++;
            }
            return nbr;
        } catch (SQLException e) {
            super.sendError("Endfight_actionData load", e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
        return nbr;
    }

    public int reload() {
        Result result = null;
        int nbr = 0;
        try {
            result = getData("SELECT * FROM endfight_action");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                GameMap map = World.world.getMap(RS.getShort("map"));
                if (map == null)
                    continue;
                map.delAllEndFightAction();
                map.addEndFightAction(RS.getInt("fighttype"), new Action(RS.getInt("action"), RS.getString("args"), RS.getString("cond"), null));
                nbr++;
            }
            return nbr;
        } catch (SQLException e) {
            super.sendError("Endfight_actionData reload", e);
        } finally {
            close(result);
        }
        return nbr;
    }

    public boolean add(int mapID, int type, int Aid, String args, String cond) {
        if (!delete(mapID, type, Aid))
            return false;

        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `endfight_action` VALUES (?,?,?,?,?)");
            p.setInt(1, mapID);
            p.setInt(2, type);
            p.setInt(3, Aid);
            p.setString(4, args);
            p.setString(5, cond);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("Endfight_actionData add", e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean delete(int mapID, int type, int aid) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM `endfight_action` WHERE map = ? AND " + "fighttype = ? AND " + "action = ?");
            p.setInt(1, mapID);
            p.setInt(2, type);
            p.setInt(3, aid);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("Endfight_actionData delete", e);
        } finally {
            close(p);
        }
        return false;
    }
}
