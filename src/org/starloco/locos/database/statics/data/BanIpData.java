package org.starloco.locos.database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.statics.AbstractDAO;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BanIpData extends AbstractDAO<Object> {
    public BanIpData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public boolean add(String ip) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `banip` VALUES (?)");
            p.setString(1, ip);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("BanipData add", e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean delete(String ip) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM `banip` WHERE `ip` = ?");
            p.setString(1, ip);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("BanipData delete", e);
        } finally {
            close(p);
        }
        return false;
    }
}
