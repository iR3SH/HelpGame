package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PrismData extends AbstractDAO<Prism> {
    public PrismData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Prism Prisme) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE prismes SET `level` = ?, `honor` = ?, `area`= ? WHERE `id` = ?");
            p.setInt(1, Prisme.getLevel());
            p.setInt(2, Prisme.getHonor());
            p.setInt(3, Prisme.getConquestArea());
            p.setInt(4, Prisme.getId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("PrismeData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public int load() {
        Result result = null;
        int numero = 0;
        try {
            result = getData("SELECT * from prismes");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                World.world.addPrisme(new Prism(RS.getInt("id"), RS.getInt("alignement"), RS.getInt("level"), RS.getShort("carte"), RS.getInt("celda"), RS.getInt("honor"), RS.getInt("area")));
                numero++;
            }
        } catch (SQLException e) {
            super.sendError("PrismeData load", e);
        } finally {
            close(result);
        }
        return numero;
    }

    public void add(Prism Prisme) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("REPLACE INTO `prismes` VALUES(?,?,?,?,?,?,?)");
            p.setInt(1, Prisme.getId());
            p.setInt(2, Prisme.getAlignement());
            p.setInt(3, Prisme.getLevel());
            p.setInt(4, Prisme.getMap());
            p.setInt(5, Prisme.getCell());
            p.setInt(6, Prisme.getConquestArea());
            p.setInt(7, Prisme.getHonor());
            execute(p);
        } catch (SQLException e) {
            super.sendError("PrismeData add", e);
        } finally {
            close(p);
        }
    }

    public void delete(int id) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM prismes WHERE id = ?");
            p.setInt(1, id);
            execute(p);
        } catch (SQLException e) {
            super.sendError("PrismeData delete", e);
        } finally {
            close(p);
        }
    }
}
