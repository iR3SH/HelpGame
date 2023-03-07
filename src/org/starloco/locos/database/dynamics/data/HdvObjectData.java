package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.Database;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.hdv.Hdv;
import org.starloco.locos.hdv.HdvEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HdvObjectData extends AbstractDAO<Object> {
    public HdvObjectData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM `hdvs_items`");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                Hdv tempHdv = World.world.getHdv(RS.getInt("map"));
                if (tempHdv == null)
                    continue;
                if (World.world.getGameObject(RS.getInt("itemID")) == null) {
                    Database.getDynamics().getHdvObjectData().delete(RS.getInt("id"));
                    continue;
                }
                tempHdv.addEntry(new HdvEntry(RS.getInt("id"), RS.getInt("price"), RS.getByte("count"), RS.getInt("ownerGuid"), World.world.getGameObject(RS.getInt("itemID"))), true);
                World.world.setNextObjectHdvId(RS.getInt("id"));
            }
        } catch (SQLException e) {
            super.sendError("Hdvs_itemsData load", e);
        } finally {
            close(result);
        }
    }

    public boolean add(HdvEntry toAdd) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `hdvs_items` (`map`,`ownerGuid`,`price`,`count`,`itemID`) VALUES(?,?,?,?,?)");
            p.setInt(1, toAdd.getHdvId());
            p.setInt(2, toAdd.getOwner());
            p.setInt(3, toAdd.getPrice());
            p.setInt(4, toAdd.getAmount(false));
            p.setInt(5, toAdd.getGameObject().getGuid());
            execute(p);
            Database.getDynamics().getObjectTemplateData().saveAvgprice(toAdd.getGameObject().getTemplate());
            return true;
        } catch (SQLException e) {
            super.sendError("Hdvs_itemsData add", e);
        } finally {
            close(p);
        }
        return false;
    }

    public void delete(int id) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM hdvs_items WHERE itemID = ?");
            p.setInt(1, id);
            execute(p);
            if (World.world.getGameObject(id) != null)
                Database.getDynamics().getObjectTemplateData().saveAvgprice(World.world.getGameObject(id).getTemplate());
        } catch (SQLException e) {
            super.sendError("Hdvs_itemsData delete", e);
        } finally {
            close(p);
        }
    }
}
