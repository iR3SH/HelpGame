package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.quest.Quest_Etape;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestStepData extends AbstractDAO<Quest_Etape> {
    public QuestStepData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Quest_Etape obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM quest_etapes");
            ResultSet RST = result.resultSet;
            Quest_Etape.questEtapeList.clear();
            while (RST.next()) {
                Quest_Etape QE = new Quest_Etape(RST.getInt("id"), RST.getInt("type"), RST.getInt("objectif"), RST.getString("item"), RST.getInt("npc"), RST.getString("monster"), RST.getString("conditions"), RST.getInt("validationType"));
                Quest_Etape.setQuestEtape(QE);
            }
        } catch (SQLException e) {
            super.sendError("Quest_etapeData load", e);
        } finally {
            close(result);
        }
    }
}
