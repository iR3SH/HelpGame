package org.starloco.locos.database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.command.administration.Command;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.kernel.Main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CommandData extends AbstractDAO<Command> {

    public CommandData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
        Result result = null;
        try {
            result = getData("SELECT * FROM `administration.commands`;");
            ResultSet RS = result.resultSet;

            while (RS.next())
                new Command(RS.getInt("id"), RS.getString("command"), RS.getString("args"), RS.getString("description"));
        } catch (SQLException e) {
            super.sendError("CommandData load", e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }

    @Override
    public boolean update(Command obj) {
        return false;
    }
}
