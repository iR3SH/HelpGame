package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.kernel.Constant;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ZaapiData extends AbstractDAO<Object> {
    public ZaapiData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Object obj) {
        return false;
    }

    public int load() {
        Result result = null;
        int i = 0;
        String Bonta = "";
        String Brak = "";
        String Neutre = "";
        try {
            result = getData("SELECT mapid, align from zaapi");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                if (RS.getInt("align") == Constant.ALIGNEMENT_BONTARIEN) {
                    Bonta += RS.getString("mapid");
                    if (!RS.isLast())
                        Bonta += ",";
                } else if (RS.getInt("align") == Constant.ALIGNEMENT_BRAKMARIEN) {
                    Brak += RS.getString("mapid");
                    if (!RS.isLast())
                        Brak += ",";
                } else {
                    Neutre += RS.getString("mapid");
                    if (!RS.isLast())
                        Neutre += ",";
                }
                i++;
            }
            Constant.ZAAPI.put(Constant.ALIGNEMENT_BONTARIEN, Bonta);
            Constant.ZAAPI.put(Constant.ALIGNEMENT_BRAKMARIEN, Brak);
            Constant.ZAAPI.put(Constant.ALIGNEMENT_NEUTRE, Neutre);
        } catch (SQLException e) {
            super.sendError("ZaapiData load", e);
        } finally {
            close(result);
        }
        return i;
    }
}
