package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.quest.Quest.Quest_Objectif;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestObjectiveData extends AbstractDAO<Quest_Objectif> {
    public QuestObjectiveData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Quest_Objectif obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM quest_objectifs");
            ResultSet loc1 = result.resultSet;
            Quest_Objectif.questObjectifList.clear();
            while (loc1.next()) {
                Quest_Objectif qObjectif = new Quest_Objectif(loc1.getInt("id"), loc1.getInt("xp"), loc1.getInt("kamas"), loc1.getString("item"), loc1.getString("action"));
                Quest_Objectif.setQuest_Objectif(qObjectif);
            }
            close(result);
        } catch (SQLException e) {
            super.sendError("Quest_objectifData load", e);
        } finally {
            close(result);
        }
    }
}
