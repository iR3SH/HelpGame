package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GiftData extends AbstractDAO<Object> {
    public GiftData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public boolean create(int guid) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO gifts(`id`, `objects`) VALUES ('"
                    + guid + "', '');");
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("GiftData create", e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean existByAccount(int guid) {
        boolean exist = false;
        Result result = null;
        try {
            result = getData("SELECT * FROM gifts WHERE id = '" + guid
                    + "'");
            ResultSet RS = result.resultSet;
            if (RS.next()) {
                exist = (RS.getInt("id") > 0);
            }
        } catch (SQLException e) {
            super.sendError("GiftData existByAccount", e);
        } finally {
            super.close(result);
        }
        return exist;
    }

    public String getByAccount(int guid) {
        Result result = null;
        String gift = null;
        try {
            result = getData("SELECT * FROM gifts WHERE id = '" + guid
                    + "';");
            ResultSet RS = result.resultSet;
            if (RS.next()) {
                gift = RS.getString("objects");
            }
        } catch (SQLException e) {
            super.sendError("GiftData getByAccount", e);
        } finally {
            super.close(result);
        }
        return gift;
    }

    public void update(int acc, String objects) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `gifts` SET `objects` = ? WHERE `id` = ?");
            p.setString(1, objects);
            p.setInt(2, acc);
            execute(p);
        } catch (SQLException e) {
            super.sendError("GiftData update", e);
        } finally {
            close(p);
        }
    }
}
