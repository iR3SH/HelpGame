package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.Database;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.area.map.entity.House;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HouseData extends AbstractDAO<House> {
    public HouseData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(House h) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `houses` SET `owner_id` = ?,`sale` = ?,`guild_id` = ?,`access` = ?,`key` = ?,`guild_rights` = ? WHERE id = ?");
            p.setInt(1, h.getOwnerId());
            p.setInt(2, h.getSale());
            p.setInt(3, h.getGuildId());
            p.setInt(4, h.getAccess());
            p.setString(5, h.getKey());
            p.setInt(6, h.getGuildRights());
            p.setInt(7, h.getId());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("HouseData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean update(int id, long price) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `houses` SET `sale` = ? WHERE id = ?");
            p.setLong(1, price);
            p.setInt(2, id);
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("HouseData update", e);
        } finally {
            close(p);
        }
        return false;
    }

    public int load() {
        Result result = null;
        int nbr = 0;
        try {
            result = getData("SELECT * from houses");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                int id = RS.getInt("id");
                int owner = RS.getInt("owner_id");
                int sale = RS.getInt("sale");
                int guild = RS.getInt("guild_id");
                int access = RS.getInt("access");
                String key = RS.getString("key");
                int guildRights = RS.getInt("guild_rights");
                House house = World.world.getHouse(id);
                if (house == null)
                    continue;
                if (owner != 0 && World.world.getAccount(owner) == null) {
                    (new Exception("La maison " + id
                            + " a un propriétaire inexistant.")).printStackTrace();
                }
                house.setOwnerId(owner);
                house.setSale(sale);
                house.setGuildId(guild);
                house.setAccess(access);
                house.setKey(key);
                house.setGuildRightsWithParse(guildRights);
                nbr++;
            }
        } catch (SQLException e) {
            super.sendError("HouseData load", e);
            nbr = 0;
        } finally {
            close(result);
        }
        return nbr;
    }

    public void buy(Player P, House h) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `houses` SET `sale`='0', `owner_id`=?, `guild_id`='0', `access`='0', `key`='-', `guild_rights`='0' WHERE `id`=?");
            p.setInt(1, P.getAccID());
            p.setInt(2, h.getId());
            execute(p);

            h.setSale(0);
            h.setOwnerId(P.getAccID());
            h.setGuildId(0);
            h.setAccess(0);
            h.setKey("-");
            h.setGuildRights(0);

            Database.getDynamics().getTrunkData().update(P, h);
        } catch (SQLException e) {
            super.sendError("HouseData buy", e);
        } finally {
            close(p);
        }
    }

    public void sell(House h, int price) {
        h.setSale(price);
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `houses` SET `sale`=? WHERE `id`=?");
            p.setInt(1, price);
            p.setInt(2, h.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("HouseData sell", e);
        } finally {
            close(p);
        }
    }

    public void updateCode(Player P, House h, String packet) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `houses` SET `key`=? WHERE `id`=? AND owner_id=?");
            p.setString(1, packet);
            p.setInt(2, h.getId());
            p.setInt(3, P.getAccID());
            execute(p);
            h.setKey(packet);
        } catch (SQLException e) {
            super.sendError("HouseData updateCode", e);
        } finally {
            close(p);
        }
    }

    public void updateGuild(House h, int GuildID, int GuildRights) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `houses` SET `guild_id`=?, `guild_rights`=? WHERE `id`=?");
            p.setInt(1, GuildID);
            p.setInt(2, GuildRights);
            p.setInt(3, h.getId());
            execute(p);
            h.setGuildId(GuildID);
            h.setGuildRights(GuildRights);
        } catch (SQLException e) {
            super.sendError("HouseData updateGuild", e);
        } finally {
            close(p);
        }
    }

    public void removeGuild(int GuildID) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `houses` SET `guild_rights`='0', `guild_id`='0' WHERE `guild_id`=?");
            p.setInt(1, GuildID);
            execute(p);
        } catch (SQLException e) {
            super.sendError("HouseData removeGuild", e);
        } finally {
            close(p);
        }
    }
}
