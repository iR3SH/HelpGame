package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.client.Account;
import org.starloco.locos.database.dynamics.AbstractDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BankData extends AbstractDAO<Object> {
    public BankData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public boolean add(int guid) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO banks(`id`, `kamas`, `items`) VALUES (?, 0, '')");
            p.setInt(1, guid);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("BankData add", e);
        } finally {
            close(p);
        }
        return false;
    }

    public void update(Account acc) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `banks` SET `kamas` = ?, `items` = ? WHERE `id` = ?");
            p.setLong(1, acc.getBankKamas());
            p.setString(2, acc.parseBankObjectsToDB());
            p.setInt(3, acc.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("BankData update", e);
        } finally {
            close(p);
        }
    }

    public String get(int guid) {
        String get = null;
        Result result = null;
        try {
            result = getData("SELECT * FROM `banks` WHERE id = '" + guid + "'");
            ResultSet RS = result.resultSet;
            if (RS.next()) {
                get = RS.getInt("kamas") + "@" + RS.getString("items");
            }
        } catch (SQLException e) {
            super.sendError("BankData getWaitingAccount", e);
        } finally {
            super.close(result);
        }
        return get;
    }
}
