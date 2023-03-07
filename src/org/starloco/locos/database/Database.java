package org.starloco.locos.database;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.DynamicsDatabase;
import org.starloco.locos.database.statics.StaticsDatabase;
import org.starloco.locos.kernel.Main;

import java.sql.Connection;

public class Database {
    private final static DynamicsDatabase dynamics = new DynamicsDatabase();
    private final static StaticsDatabase statics = new StaticsDatabase();

    public static boolean launchDatabase() {
        if (!statics.initializeConnection() || !dynamics.initializeConnection()) {
            Main.stop("Initialization of database connection failed");
            return false;
        }
        return true;
    }

    public static DynamicsDatabase getDynamics() {
        return dynamics;
    }

    public static StaticsDatabase getStatics() {
        return statics;
    }

    public static boolean tryConnection(HikariDataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            connection.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
