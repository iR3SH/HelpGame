package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Shortcuts;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.fight.spells.GladiatroolSpells;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.GameObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortcutsData extends AbstractDAO<Object> {
    public ShortcutsData(HikariDataSource dataSource) {
        super(dataSource);
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM `shortcuts`");
            ResultSet RS = result.resultSet;

            while (RS.next()) {
                Player player = World.world.getPlayer(RS.getInt("playerId"));
                GameObject object = World.world.getGameObject(RS.getInt("objectId"));
                if(player != null) {
                    Shortcuts shortcut = new Shortcuts(player, RS.getInt("position"), object);
                    World.world.addShortcut(player, shortcut);
                }
            }
        } catch (SQLException e) {
            super.sendError("ShortcutsData load", e);
        } finally {
            close(result);
        }
    }

    public boolean updateObject(Shortcuts shortcut) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `shortcuts` SET `objectId` = ? WHERE playerId = ? AND position = ? ;");
            p.setInt(1, shortcut.getObject().getGuid());
            p.setInt(2, shortcut.getPlayer().getId());
            p.setInt(3, shortcut.getPosition());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("ShortcutsData update", e);
        } finally {
            close(p);
        }
        return false;
    }
    public boolean updatePosition(Shortcuts shortcut) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `shortcuts` SET `position` = ? WHERE playerId = ? AND objectId = ? ;");
            p.setInt(1, shortcut.getPosition());
            p.setInt(2, shortcut.getPlayer().getId());
            p.setInt(3, shortcut.getObject().getGuid());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("ShortcutsData update", e);
        } finally {
            close(p);
        }
        return false;
    }
    public boolean delete(Shortcuts shortcut)
    {
        PreparedStatement p = null;
        try{
            p = getPreparedStatement("DELETE FROM shortcuts WHERE playerId = ? AND position = ?");
            p.setInt(1, shortcut.getPlayer().getId());
            p.setInt(2, shortcut.getPosition());
            execute(p);
        } catch (SQLException e) {
            super.sendError("ShortcutsData delete", e);
            return false;
        } finally {
            close(p);
        }
        return true;
    }
    public void add(Shortcuts shortcut) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `shortcuts` VALUES (?,?,?);");
            p.setInt(1, shortcut.getPlayer().getId());
            p.setInt(2, shortcut.getPosition());
            p.setInt(3, shortcut.getObject().getGuid());
            execute(p);
        } catch (SQLException e) {
            super.sendError("ShortcutsData add", e);
        } finally {
            close(p);
        }
    }

    @Override
    public void load(Object obj) {

    }

    @Override
    public boolean update(Object obj) {
        return false;
    }
}
