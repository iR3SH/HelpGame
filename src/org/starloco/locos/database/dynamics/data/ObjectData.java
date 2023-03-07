package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.GameObject;

public class ObjectData extends AbstractDAO<GameObject> {

    public ObjectData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
        Result result = null;
        try {
            result = getData("SELECT * FROM `objects` WHERE `id` IN (" + obj + ");");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                int id = RS.getInt("id");
                int template = RS.getInt("template");
                int quantity = RS.getInt("quantity");
                int position = RS.getInt("position");
                String stats = RS.getString("stats");
                int puit = RS.getInt("puit");

                if(quantity == 0) continue;
                World.world.addGameObject(World.world.newObjet(id, template, quantity, position, stats, puit), false);
            }
        } catch (SQLException e) {
            super.sendError("ObjectData load", e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM `objects`;");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                int id = RS.getInt("id");
                int template = RS.getInt("template");
                int quantity = RS.getInt("quantity");
                int position = RS.getInt("position");
                String stats = RS.getString("stats");
                int puit = RS.getInt("puit");

                if(quantity == 0) continue;
                World.world.addGameObject(World.world.newObjet(id, template, quantity, position, stats, puit), false);
            }
        } catch (SQLException e) {
            super.sendError("ObjectData load", e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }

    @Override
    public boolean update(GameObject object) {
        if (object == null)
            return false;
        if (object.getTemplate() == null)
            return false;

        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `objects` SET `template` = ?, `quantity` = ?, `position` = ?, `puit` = ?, `stats` = ? WHERE `id` = ?;");
            p.setInt(1, object.getTemplate().getId());
            p.setInt(2, object.getQuantity());
            p.setInt(3, object.getPosition());
            p.setInt(4, object.getPuit());
            p.setString(5, object.parseToSave());
            p.setInt(6, object.getGuid());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("ObjectData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public void insert(GameObject object) {
        if (object == null) {
            super.sendError("ObjectData insert", new Exception("Object null"));
            return;
        } else if (object.getTemplate() == null) {
            super.sendError("ObjectData insert", new Exception("Template null"));
            return;
        }
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("REPLACE INTO `objects` VALUES (?, ?, ?, ?, ?, ?);");
            p.setInt(1, object.getGuid());
            p.setInt(2, object.getTemplate().getId());
            p.setInt(3, object.getQuantity());
            p.setInt(4, object.getPosition());
            p.setString(5, object.parseToSave());
            p.setInt(6, object.getPuit());
            execute(p);
        } catch (SQLException e) {
            super.sendError("ObjectData insert", e);
        } finally {
            close(p);
        }
    }

    public void delete(int id) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM `objects` WHERE id = ?;");
            p.setInt(1, id);
            execute(p);
        } catch (SQLException e) {
            super.sendError("ObjectData delete", e);
        } finally {
            close(p);
        }
    }
}
