package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.client.Player;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.game.world.World;
import org.starloco.locos.other.Guild;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GuildMemberData extends AbstractDAO<Object> {
    public GuildMemberData(HikariDataSource dataSource) {
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
            result = getData("SELECT * FROM guild_members");
            ResultSet RS = result.resultSet;

            while (RS.next()) {
                try {
                    Guild g = World.world.getGuild(RS.getInt("guild"));
                    if (g != null)
                        g.addMember(RS.getInt("guid"), RS.getInt("rank"), RS.getByte("pxp"), RS.getLong("xpdone"), RS.getInt("rights"), RS.getString("lastConnection").replaceAll("-", "~"));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            super.sendError("Guild_memberData load", e);
        } finally {
            close(result);
        }
    }

    public void delete(int id) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM `guild_members` WHERE `guid` = ?");
            p.setInt(1, id);
            execute(p);
        } catch (SQLException e) {
            super.sendError("Guild_memberData delete", e);
        } finally {
            close(p);
        }
    }

    public void deleteAll(int id) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM `guild_members` WHERE `guild` = ?");
            p.setInt(1, id);
            execute(p);
        } catch (SQLException e) {
            super.sendError("Guild_memberData deleteAll", e);
        } finally {
            close(p);
        }
    }

    public void update(Player player) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("REPLACE INTO `guild_members` VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            Guild.GuildMember gm = player.getGuildMember();
            if(gm == null) return;
            p.setInt(1, gm.getGuid());
            p.setInt(2, gm.getGuild().getId());
            p.setString(3, player.getName());
            p.setInt(4, gm.getLvl());
            int gfx = gm.getGfx();
            if (gfx > 121 || gfx < 10)
                gfx = player.getClasse() * 10 + player.getSexe();
            p.setInt(5, gfx);
            p.setInt(6, gm.getRank());
            p.setLong(7, gm.getXpGave());
            p.setInt(8, gm.getPXpGive());
            p.setInt(9, gm.getRights());
            p.setInt(10, gm.getAlign());
            p.setString(11, gm.getLastCo());
            execute(p);
        } catch (SQLException e) {
            super.sendError("Guild_memberData update", e);
        } finally {
            close(p);
        }
    }

    public int isPersoInGuild(int guid) {
        Result result = null;
        int guildId = -1;
        try {
            result = getData("SELECT guild FROM `guild_members` WHERE guid="
                    + guid);
            ResultSet GuildQuery = result.resultSet;
            boolean found = GuildQuery.first();
            if (found)
                guildId = GuildQuery.getInt("guild");
        } catch (SQLException e) {
            super.sendError("Guild_memberData isPersoInGuild", e);
        } finally {
            close(result);
        }
        return guildId;
    }

    public int[] isPersoInGuild(String name) {
        Result result = null;
        int guildId = -1;
        int guid = -1;
        try {
            result = getData("SELECT guild,guid FROM `guild_members` WHERE name='"
                    + name + "'");
            ResultSet GuildQuery = result.resultSet;
            boolean found = GuildQuery.first();
            if (found) {
                guildId = GuildQuery.getInt("guild");
                guid = GuildQuery.getInt("guid");
            }
        } catch (SQLException e) {
            super.sendError("Guild_memberData isPersoInGuild", e);
        } finally {
            close(result);
        }
        int[] toReturn = {guid, guildId};
        return toReturn;
    }
}
