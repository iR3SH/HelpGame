package org.starloco.locos.database.statics.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.starloco.locos.database.Database;
import org.starloco.locos.database.statics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.area.map.GameMap;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.area.map.entity.House;

public class HouseData extends AbstractDAO<House>
{
	public HouseData(HikariDataSource dataSource)
	{
		super(dataSource);
	}

	@Override
	public void load(Object obj)
	{
    }

	@Override
	public boolean update(House h)
	{
		return false;
	}

	public int load()
	{
		Result result = null;
		int nbr = 0;
		try
		{
			result = getData("SELECT * from houses");
			ResultSet RS = result.resultSet;
			while (RS.next())
			{
                GameMap map = World.world.getMap(RS.getShort("map_id"));
				if (map == null)
					continue;

				World.world.addHouse(new House(RS.getInt("id"), RS.getShort("map_id"), RS.getInt("cell_id"), RS.getInt("mapid"), RS.getInt("caseid")));
				/*long saleBase = RS.getLong("saleBase");
				Database.getDynamics().getHouseData().update(RS.getInt("id"), saleBase);*/
				nbr++;
			}
		}
		catch (SQLException e)
		{
			super.sendError("HouseData load", e);
			nbr = 0;
		}
		finally
		{
			close(result);
		}
		return nbr;
	}
}
