package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.job.Job;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JobData extends AbstractDAO<Job> {
    public JobData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Job obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * from jobs_data");
            ResultSet RS = result.resultSet;
            while (RS.next()) {

                String skills = "";
                if (RS.getString("skills") != null)
                    skills = RS.getString("skills");
                World.world.addJob(new Job(RS.getInt("id"), RS.getString("tools"), RS.getString("crafts"), skills));
            }
        } catch (SQLException e) {
            super.sendError("Jobs_dataData load", e);
        } finally {
            close(result);
        }
    }
}
