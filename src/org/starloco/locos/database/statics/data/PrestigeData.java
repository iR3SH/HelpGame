package org.starloco.locos.database.statics.data;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.starloco.locos.area.map.entity.Animation;
import org.starloco.locos.client.Prestige;
import org.starloco.locos.database.statics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import com.zaxxer.hikari.HikariDataSource;

public class PrestigeData extends AbstractDAO<Object> {
    public PrestigeData(HikariDataSource dataSource) {
        super(dataSource);
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM prestiges");
            final ResultSet RS = result.resultSet;
            while (RS.next()) {
            	final short id = RS.getShort("prestige");
            	final int requiredLevel = RS.getInt("requiredLevel");
            	final String condition = RS.getString("condition");
            	final int titleID = RS.getInt("titleID");
            	final String artefact = RS.getString("artefact");
            	final int animationID = RS.getInt("animation");
            	Animation animation;
            	if(animationID == -1 || animationID <= 0) 
            		animation = null;
            	else
            		animation = World.world.getAnimation(animationID);
            	
            	final String[] parcho = RS.getString("parcho").split(";");
            	if(parcho[0].isEmpty() || parcho.length != 2)
            	{
            		parcho[0] = "false";
            		parcho[1] = "false";
            	}
            	final boolean keepOldParcho = parcho[0].equalsIgnoreCase("true");
            	final boolean keepNewParcho = parcho[1].equalsIgnoreCase("true");
            	final String keepSpell = RS.getString("keepSpell");
            	final int levelToDown = RS.getInt("levelToDown");
            	final double malusXp = RS.getDouble("malusXp");
            	final int mapToTp = RS.getInt("mapToTp");
            	final int cellToTp = RS.getInt("cellToTp");
            	final long priceKamas = RS.getLong("priceKamas");
            	final int priceBoutique = RS.getInt("priceBoutique");
            	final String priceItem = RS.getString("priceItem");
            	final String infosCondition = RS.getString("infosCondition");
            	
            	final Prestige prestige = new Prestige(id, requiredLevel, condition, titleID, artefact, animation, keepOldParcho, keepNewParcho, keepSpell, levelToDown, malusXp, mapToTp, cellToTp, priceKamas, priceBoutique, priceItem, infosCondition);
            	World.world.addPrestige(prestige);
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
