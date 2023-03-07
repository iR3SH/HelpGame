package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.area.map.GameMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapData extends AbstractDAO<GameMap> {
    public MapData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(GameMap obj) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `maps` SET `places` = ?, `numgroup` = ? WHERE id = ?");
            p.setString(1, obj.getPlaces());
            p.setInt(2, obj.getMaxGroupNumb());
            p.setInt(3, obj.getId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("MapData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean updateGs(GameMap map) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `maps` SET `numgroup` = ?, `minSize` = ?, `fixSize` = ?, `maxSize` = ? WHERE id = ?");
            p.setInt(1, map.getMaxGroupNumb());
            p.setInt(2, map.getMinSize());
            p.setInt(3, map.getFixSize());
            p.setInt(4, map.getMaxSize());
            p.setInt(5, map.getId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("MapData updateGs", e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean updateMonster(GameMap map, String monsters) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `maps` SET `monsters` = ? WHERE id = ?");
            p.setString(1, monsters);
            p.setInt(2, map.getId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("MapData updateMonster", e);
        } finally {
            close(p);
        }
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT  * from maps LIMIT " + Constant.DEBUG_MAP_LIMIT);
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                World.world.addMap(new GameMap(RS.getShort("id"), RS.getString("date"), RS.getByte("width"), RS.getByte("heigth"), RS.getString("key"), RS.getString("places"), RS.getString("mapData"), RS.getString("monsters"), RS.getString("mappos"), RS.getByte("numgroup"), RS.getByte("fixSize"), RS.getByte("minSize"), RS.getByte("maxSize"), RS.getString("forbidden"), RS.getByte("sniffed")));
            }
            close(result);

            result = getData("SELECT  * from mobgroups_fix");
            RS = result.resultSet;
            while (RS.next()) {
                GameMap c = World.world.getMap(RS.getShort("mapid"));
                if (c == null)
                    continue;
                if (c.getCase(RS.getInt("cellid")) == null)
                    continue;
                c.addStaticGroup(RS.getInt("cellid"), RS.getString("groupData"), false);
                World.world.addGroupFix(RS.getInt("mapid") + ";"
                        + RS.getInt("cellid"), RS.getString("groupData"), RS.getInt("Timer"));
            }
        } catch (SQLException e) {
            super.sendError("MapData load", e);
        } finally {
            close(result);
        }
    }

    public void reload() {
        Result result = null;
        try {
            result = getData("SELECT  * from maps LIMIT "
                    + Constant.DEBUG_MAP_LIMIT);
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                GameMap map = World.world.getMap(RS.getShort("id"));
                if (map == null) {
                    World.world.addMap(new GameMap(RS.getShort("id"), RS.getString("date"), RS.getByte("width"), RS.getByte("heigth"), RS.getString("key"), RS.getString("places"), RS.getString("mapData"), RS.getString("monsters"), RS.getString("mappos"), RS.getByte("numgroup"), RS.getByte("fixSize"), RS.getByte("minSize"), RS.getByte("maxSize"), RS.getString("forbidden"), RS.getByte("sniffed")));
                    continue;
                }
                map.setInfos(RS.getString("date"), RS.getString("monsters"), RS.getString("mappos"), RS.getByte("numgroup"), RS.getByte("fixSize"), RS.getByte("minSize"), RS.getByte("maxSize"), RS.getString("forbidden"));
            }
        } catch (SQLException e) {
            super.sendError("MapData reload", e);
        } finally {
            close(result);
        }
    }
}
