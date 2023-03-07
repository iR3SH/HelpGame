package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.entity.pet.Pet;
import org.starloco.locos.game.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PetTemplateData extends AbstractDAO<Pet> {
    public PetTemplateData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Pet obj) {
        return false;
    }

    public int load() {
        Result result = null;
        int i = 0;
        try {
            result = getData("SELECT * from pets");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                i++;

                World.world.addPets(new Pet(RS.getInt("TemplateID"), RS.getInt("Type"), RS.getString("Gap"), RS.getString("StatsUp"), RS.getString("StatsMax"), RS.getInt("Max"), RS.getInt("Gain"), RS.getInt("DeadTemplate"), RS.getInt("Epo"), RS.getString("jet")));
            }
        } catch (SQLException e) {
            super.sendError("PetData load", e);
        } finally {
            close(result);
        }
        return i;
    }
}
