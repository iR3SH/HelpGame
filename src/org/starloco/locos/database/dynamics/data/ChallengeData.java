package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChallengeData extends AbstractDAO<Object> {
    public ChallengeData(HikariDataSource dataSource) {
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
            result = getData("SELECT * from challenge");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                World.world.addChallenge(String.valueOf(RS.getInt("id")) + "," + RS.getInt("gainXP") + "," + RS.getInt("gainDrop") + "," + RS.getInt("gainParMob") + "," + RS.getInt("conditions"));
            }
        } catch (SQLException e) {
            super.sendError("ChallengeData load", e);
        } finally {
            close(result);
        }
    }
}
