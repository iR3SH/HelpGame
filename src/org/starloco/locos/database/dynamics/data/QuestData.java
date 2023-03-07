package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.quest.Quest;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestData extends AbstractDAO<Quest> {
    public QuestData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Quest obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM quest_data");
            ResultSet RS = result.resultSet;
            Quest.questDataList.clear();
            while (RS.next()) {
                Quest quest = new Quest(RS.getInt("id"), RS.getString("etapes"), RS.getString("objectif"), RS.getInt("npc"), RS.getString("action"), RS.getString("args"), (RS.getInt("deleteFinish") == 1), RS.getString("condition"));
                if (quest.getNpc_Tmpl() != null) {
                    quest.getNpc_Tmpl().setQuest(quest);
                    quest.getNpc_Tmpl().setExtraClip(4);
                }
                Quest.setQuestInList(quest);
            }
        } catch (SQLException e) {
            super.sendError("Quest_dataData load", e);
        } finally {
            close(result);
        }
    }
}
