package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CraftData extends AbstractDAO<Object> {
    public CraftData(HikariDataSource dataSource) {
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
            result = getData("SELECT * from crafts");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                ArrayList<World.Couple<Integer, Integer>> m = new ArrayList<>();

                boolean cont = true;
                for (String str : RS.getString("craft").split(";")) {
                    try {
                        int tID = Integer.parseInt(str.split("\\*")[0]);
                        int qua = Integer.parseInt(str.split("\\*")[1]);
                        m.add(new World.Couple<>(tID, qua));
                    } catch (Exception e) {
                        e.printStackTrace();
                        cont = false;
                    }
                }
                if (!cont) // S'il y a eu une erreur de parsing, on ignore cette recette
                    continue;

                World.world.addCraft(RS.getInt("id"), m);
            }
        } catch (SQLException e) {
            super.sendError("CraftData load", e);
        } finally {
            close(result);
        }
    }
}
