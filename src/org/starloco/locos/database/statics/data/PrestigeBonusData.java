package org.starloco.locos.database.statics.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.starloco.locos.client.Prestige;
import org.starloco.locos.database.statics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import com.zaxxer.hikari.HikariDataSource;

public class PrestigeBonusData extends AbstractDAO<Object> {
    public PrestigeBonusData(HikariDataSource dataSource) {
        super(dataSource);
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM prestiges_bonus");
            final ResultSet RS = result.resultSet;
            while (RS.next()) {
            	final short id = RS.getShort("prestige");
            	final String baseStats = RS.getString("baseStats");
            	final String primaryStats = RS.getString("primaryStats");
            	final String advancedStats = RS.getString("advancedStats");
            	final int parcho = RS.getInt("parcho");
            	final int capital = RS.getInt("capitalByLevel");
            	final int pdvMax = RS.getInt("pdvMaxByLevel");
            	final String spells = RS.getString("spells");
            	final String items = RS.getString("items");
            	
            	final Prestige prestige = World.world.getPrestigeById(id);
            	if(prestige == null)
            	{
            		super.sendError("id prestige inséré dans prestiges_bonus est incorect", new NullPointerException());
            		Main.stop("id error prestiges_bonus");
            		return;
            	}
            	prestige.getPrestigeBonus().setInfos(baseStats, primaryStats, advancedStats, parcho, capital, pdvMax, spells, items);
            }
        } catch (SQLException e) {
            super.sendError("Prestige load", e);
            Main.stop("Prestige load");
        } finally {
            close(result);
        }
    }
    
	@Override
	public void load(Object obj) {
	}

	@Override
	public boolean update(Object obj) {
		return false;
	}



}
