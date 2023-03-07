package org.starloco.locos.database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.statics.AbstractDAO;
import org.starloco.locos.game.scheduler.entity.WorldPub;
import org.starloco.locos.kernel.Main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PubData extends AbstractDAO<Object> {

    public PubData(HikariDataSource dataSource)
	{
		super(dataSource);
	}

	@Override
	public void load(Object obj) {
        Result result = null;
        try {
            result = getData("SELECT * FROM `pubs` WHERE `server` LIKE '" + Main.serverId + "|';");
            ResultSet RS = result.resultSet;
            while (RS.next())
                WorldPub.pubs.add(RS.getString("data"));
        } catch (SQLException e) {
            super.sendError("PubData load", e);
        } finally {
            close(result);
        }
    }

	@Override
	public boolean update(Object t)	{
		return false;
	}
}
