package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.kernel.Config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NpcData extends AbstractDAO<Object> {
    public NpcData(HikariDataSource dataSource) {
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
            result = getData("SELECT * from npcs");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                GameMap map = World.world.getMap(RS.getShort("mapid"));
                if (map == null) continue;
                if(!Config.getInstance().NOEL && RS.getInt("npcid") == 795) continue;
                map.addNpc(RS.getInt("npcid"), RS.getShort("cellid"), RS.getInt("orientation"));
                nbr++;
            }
        } catch (SQLException e) {
            super.sendError("NpcData load", e);
        } finally {
            close(result);
        }
        return nbr;
    }

    public boolean delete(int m, int c) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM npcs WHERE mapid = ? AND cellid = ?");
            p.setInt(1, m);
            p.setInt(2, c);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("NpcData delete", e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean addOnMap(int m, int id, int c, int o, boolean mo) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `npcs` VALUES (?,?,?,?,?)");
            p.setInt(1, m);
            p.setInt(2, id);
            p.setInt(3, c);
            p.setInt(4, o);
            p.setBoolean(5, mo);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("NpcData addOnMap", e);
        } finally {
            close(p);
        }
        return false;
    }
}
