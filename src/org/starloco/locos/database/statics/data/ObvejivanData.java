package org.starloco.locos.database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.statics.AbstractDAO;
import org.starloco.locos.object.GameObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObvejivanData extends AbstractDAO<GameObject> {

    public ObvejivanData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {}

    @Override
    public boolean update(GameObject obj) {
        return false;
    }

    public void add(GameObject obvi, GameObject victime) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO `world.entity.obvijevans`(`id`, `template`) VALUES(?, ?);");
            p.setInt(1, obvi.getTemplate().getId());
            p.setInt(2, victime.getGuid());
            execute(p);
        } catch (Exception e) {
            super.sendError("ObvejivanData add", e);
        } finally {
            close(p);
        }
    }

    public int getAndDelete(GameObject object, boolean delete) {
        Result result = null;
        int template = -1;
        try {
            //result = getData("SELECT * FROM `world.entity.obvijevans` WHERE `id` = '" + object.getGuid() + "';");
            result = getData("SELECT * FROM `world.entity.obvijevans` WHERE `template` = '" + object.getGuid() + "';");
            ResultSet resultSet = result.resultSet;

            if (resultSet.next()) {
                template = resultSet.getInt("id"); // template
                if (delete) {
                    PreparedStatement ps = getPreparedStatement("DELETE FROM `world.entity.obvijevans` WHERE template = '" + object.getGuid() + "';"); // id
                    execute(ps);
                }
            }
        } catch (SQLException e) {
            super.sendError("ObvejivanData getAndDelete", e);
        } finally {
            close(result);
        }
        return template;
    }
}
