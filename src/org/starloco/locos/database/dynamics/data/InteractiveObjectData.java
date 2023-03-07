package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.area.map.entity.InteractiveObject.InteractiveObjectTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractiveObjectData extends
        AbstractDAO<InteractiveObjectTemplate> {
    public InteractiveObjectData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(InteractiveObjectTemplate obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * from interactive_objects_data");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                World.world.addIOTemplate(new InteractiveObjectTemplate(RS.getInt("id"), RS.getInt("respawn"), RS.getInt("duration"), RS.getInt("unknow"), RS.getInt("walkable") == 1));
            }
        } catch (SQLException e) {
            super.sendError("Interactive_objects_dataData load", e);
        } finally {
            close(result);
        }
    }
}
