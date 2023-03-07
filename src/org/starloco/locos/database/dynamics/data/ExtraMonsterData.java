package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExtraMonsterData extends AbstractDAO<Object> {
    public ExtraMonsterData(HikariDataSource dataSource) {
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
            result = getData("SELECT * from extra_monster");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                World.world.addExtraMonster(RS.getInt("idMob"), RS.getString("superArea"), RS.getString("subArea"), RS.getInt("chances"));
            }
        } catch (SQLException e) {
            super.sendError("Extra_monsterData load", e);
        } finally {
            close(result);
        }
    }
}
