package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.ObjectTemplate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectTemplateData extends AbstractDAO<ObjectTemplate> {
    public ObjectTemplateData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(ObjectTemplate obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM item_template;");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                if (World.world.getObjTemplate(RS.getInt("id")) != null) {
                    World.world.getObjTemplate(RS.getInt("id")).setInfos(RS.getString("statsTemplate"), RS.getString("name"), RS.getInt("type"), RS.getInt("level"), RS.getInt("pod"), RS.getInt("prix"), RS.getInt("panoplie"), RS.getString("conditions"), RS.getString("armesInfos"), RS.getInt("sold"), RS.getInt("avgPrice"), RS.getInt("points"), RS.getInt("newPrice"));
                } else {
                    World.world.addObjTemplate(new ObjectTemplate(RS.getInt("id"), RS.getString("statsTemplate"), RS.getString("name"), RS.getInt("type"), RS.getInt("level"), RS.getInt("pod"), RS.getInt("prix"), RS.getInt("panoplie"), RS.getString("conditions"), RS.getString("armesInfos"), RS.getInt("sold"), RS.getInt("avgPrice"), RS.getInt("points"), RS.getInt("newPrice")));
                }
            }
        } catch (SQLException e) {
            super.sendError("Item_templateData load", e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }

    public void saveAvgprice(ObjectTemplate template) {
        if (template == null)
            return;
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `item_template` SET sold = ?,avgPrice = ? WHERE id = ?");
            p.setLong(1, template.getSold());
            p.setInt(2, template.getAvgPrice());
            p.setInt(3, template.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("Item_templateData saveAvgprice", e);
        } finally {
            close(p);
        }
    }
}
