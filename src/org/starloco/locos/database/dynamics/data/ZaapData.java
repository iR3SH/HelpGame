package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.kernel.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ZaapData extends AbstractDAO<Object> {
    public ZaapData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public int load() {
        Result result = null;
        int i = 0;
        try {
            result = getData("SELECT mapID, cellID from zaaps");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                Constant.ZAAPS.put(RS.getInt("mapID"), RS.getInt("cellID"));
                i++;
            }
        } catch (SQLException e) {
            super.sendError("ZaapData load", e);
        } finally {
            close(result);
        }
        return i;
    }
}
