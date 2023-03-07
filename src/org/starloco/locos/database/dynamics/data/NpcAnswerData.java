package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.entity.npc.NpcAnswer;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.other.Action;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NpcAnswerData extends AbstractDAO<Object> {
    public NpcAnswerData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM npc_reponses_actions");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                int id = RS.getInt("ID");
                int type = RS.getInt("type");
                String args = RS.getString("args");
                if (World.world.getNpcAnswer(id) == null)
                    World.world.addNpcAnswer(new NpcAnswer(id));
                World.world.getNpcAnswer(id).addAction(new Action(type, args, "", null));

            }
        } catch (SQLException e) {
            super.sendError("Npc_reponses_actionData load", e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }

    public boolean add(int repID, int type, String args) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM `npc_reponses_actions` WHERE `ID` = ? AND `type` = ?");
            p.setInt(1, repID);
            p.setInt(2, type);
            execute(p);
        } catch (SQLException e) {
            super.sendError("Npc_reponses_actionData add", e);
        } finally {
            close(p);
        }
        try {
            p = getPreparedStatement("INSERT INTO `npc_reponses_actions` VALUES (?,?,?)");
            p.setInt(1, repID);
            p.setInt(2, type);
            p.setString(3, args);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("Npc_reponses_actionData add", e);
        } finally {
            close(p);
        }
        return false;
    }
}
