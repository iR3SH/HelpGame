package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.area.map.entity.InteractiveDoor;
import org.starloco.locos.area.map.entity.InteractiveObject.InteractiveObjectTemplate;
import org.starloco.locos.database.dynamics.AbstractDAO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractiveDoorData extends AbstractDAO<InteractiveObjectTemplate> {

    public InteractiveDoorData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {}

    @Override
    public boolean update(InteractiveObjectTemplate obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * from interactive_doors");
            ResultSet RS = result.resultSet;
            while (RS.next())
                new InteractiveDoor(RS.getString("maps"), RS.getString("doorsEnable"), RS.getString("doorsDisable"), RS.getString("cellsEnable"), RS.getString("cellsDisable"), RS.getString("requiredCells"), RS.getString("button"), RS.getShort("time"));
        } catch (SQLException e) {
            super.sendError("interactive_doors load", e);
        } finally {
            close(result);
        }
    }
}
