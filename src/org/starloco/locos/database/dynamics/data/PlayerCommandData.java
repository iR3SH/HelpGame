package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.starloco.locos.command.PlayerCommand;
import org.starloco.locos.command.administration.Command;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

public class PlayerCommandData extends AbstractDAO<Command> {

    public PlayerCommandData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(final Object obj) {
        Result result = null;
        try {
            result = getData("SELECT * FROM `playercommands`;");
            final ResultSet RS = result.resultSet;

            while (RS.next())
            	World.world.addPlayerCommand(new PlayerCommand(RS.getString("name"), RS.getInt("type"), RS.getString("args"), 
            			RS.getInt("price"), RS.getBoolean("vip"), RS.getString("condition"), RS.getString("description")));
            
        } catch (SQLException e) {
            super.sendError("PlayerCommandData load", e);
            Main.stop("PlayerCommandData");
        } finally {
            close(result);
        }
    }

    @Override
    public boolean update(Command obj) {
        return false;
    }
}
