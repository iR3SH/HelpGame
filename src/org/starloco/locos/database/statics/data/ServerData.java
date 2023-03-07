package org.starloco.locos.database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.statics.AbstractDAO;
import org.starloco.locos.kernel.Main;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServerData extends AbstractDAO<Object> {
    public ServerData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public void updateTime(long time) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE servers SET `uptime` = ? WHERE `id` = ?");
            p.setLong(1, time);
            p.setInt(2, Main.serverId);
            execute(p);
        } catch (SQLException e) {
            super.sendError("ServerData updateTime", e);
        } finally {
            close(p);
        }
    }

    public void loggedZero() {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE players SET `logged` = 0 WHERE `server` = '" + Main.serverId + "'");
            execute(p);
        } catch (SQLException e) {
            super.sendError("ServerData loggedZero", e);
        } finally {
            close(p);
        }
    }
}
