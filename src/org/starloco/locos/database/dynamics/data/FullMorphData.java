package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FullMorphData extends AbstractDAO<Object> {
    public FullMorphData(HikariDataSource dataSource) {
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
            result = getData("SELECT * FROM `full_morphs`");
            ResultSet RS = result.resultSet;

            while (RS.next()) {
                String[] args = null;
                if (!RS.getString("args").equals("0")) {
                    args = RS.getString("args").split("@")[1].split(",");
                }

                World.world.addFullMorph(RS.getInt("id"), RS.getString("name"), RS.getInt("gfxId"), RS.getString("spells"), args);
            }
        } catch (SQLException e) {
            super.sendError("Full_morphData load", e);
        } finally {
            close(result);
        }
    }
}
