package org.starloco.locos.database.dynamics.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.starloco.locos.database.dynamics.AbstractDAO;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Created by Locos on 15/09/2015.
 */
public class WorldEntityData extends AbstractDAO<Object> {

    private int nextMountId, nextObjectId, nextGuidId, nextQuestId;

    public WorldEntityData(HikariDataSource dataSource) {
        super(dataSource);
        this.load(null);
    }

    @Override
    public void load(Object obj) {
        Result result = null;
        try {
            result = getData("SELECT MIN(id) AS min FROM `mounts`;");
            ResultSet RS = result.resultSet;
            boolean found = RS.first();
            if (found) this.nextMountId = RS.getInt("min");
            else this.nextMountId = -1;
        } catch (SQLException e) {
            logger.error("WorldEntityData load", e);
        } finally {
            close(result);
        }
        try {
            result = getData("SELECT MAX(id) AS max FROM `objects`;");
            ResultSet RS = result.resultSet;
            boolean found = RS.first();
            if (found) this.nextObjectId = RS.getInt("max");
            else this.nextObjectId = 1;
        } catch (SQLException e) {
            logger.error("WorldEntityData load", e);
        } finally {
            close(result);
        }
        try {
            result = getData("SELECT MAX(id) AS max FROM `guilds`;");
            ResultSet RS = result.resultSet;
            boolean found = RS.first();
            if (found) this.nextGuidId = RS.getInt("max");
            else this.nextGuidId = 1;
        } catch (SQLException e) {
            logger.error("WorldEntityData load", e);
        } finally {
            close(result);
        }
        try {
            result = getData("SELECT MAX(id) AS max FROM `quests_players`;");
            ResultSet RS = result.resultSet;
            boolean found = RS.first();
            if (found) this.nextQuestId = RS.getInt("max");
            else this.nextQuestId = 1;
        } catch (SQLException e) {
            logger.error("WorldEntityData load", e);
        } finally {
            close(result);
        }
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public synchronized int getNextMountId() {
        return --nextMountId;
    }

    public synchronized int getNextObjectId() {
        return ++nextObjectId;
    }

	public synchronized int getNextGuidId() {
        return ++nextGuidId;
    }

	public synchronized int getNextQuestId() {
        return ++nextQuestId;
    }
}
