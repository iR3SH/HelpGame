package org.starloco.locos.database.statics.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.starloco.locos.area.SubArea;
import org.starloco.locos.database.statics.AbstractDAO;
import org.starloco.locos.game.world.World;

import com.zaxxer.hikari.HikariDataSource;

public class SubAreaData extends AbstractDAO<SubArea>
{
	public SubAreaData(HikariDataSource dataSource)
	{
		super(dataSource);
	}

	@Override
	public void load(Object obj)
	{
    }

	@Override
	public boolean update(SubArea subarea)
	{
		return false;
	}
	
	public void load()
	{
		Result result = null;
		try
		{
			result = getData("SELECT * from subarea_data");
			ResultSet RS = result.resultSet;
			while (RS.next())
			{
				SubArea SA = new SubArea(RS.getInt("id"), RS.getInt("area"));
				World.world.addSubArea(SA);
				if (SA.getArea() != null)
					SA.getArea().addSubArea(SA); //on ajoute la sous zone a la zone
			}
		}
		catch (SQLException e)
		{
			super.sendError("Subarea_dataData load", e);
		}
		finally
		{
			close(result);
		}
	}
}
