package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.entity.npc.NpcQuestion;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NpcQuestionData extends AbstractDAO<NpcQuestion> {
    public NpcQuestionData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(NpcQuestion obj) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `npc_questions` SET `responses` = ?WHERE `ID` = ?");
            p.setString(1, obj.getAnwsers());
            p.setInt(2, obj.getId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("Npc_questionData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public void updateLot() {
        int lot = Integer.parseInt(World.world.getNPCQuestion(1646).getArgs()) + 50;
        World.world.getNPCQuestion(1646).setArgs(lot + "");
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `npc_questions` SET params='"
                    + lot + "' WHERE `id`=1646");
            execute(p);
        } catch (SQLException e) {
            super.sendError("Npc_questionData updateLot", e);
        } finally {
            close(p);
        }
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM npc_questions");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                World.world.addNPCQuestion(new NpcQuestion(RS.getInt("ID"), RS.getString("responses"), RS.getString("params"), RS.getString("cond"), RS.getString("ifFalse")));
            }
        } catch (SQLException e) {
            super.sendError("Npc_questionData load", e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }
}
