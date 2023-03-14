package org.starloco.locos.database.statics.data;

import ch.qos.logback.classic.Level;
import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.client.Account;
import org.starloco.locos.database.statics.AbstractDAO;
import org.starloco.locos.game.world.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountData extends AbstractDAO<Account> {

    public AccountData(HikariDataSource source) {
        super(source);
        logger.setLevel(Level.ALL);
    }

    public void load(Object id) {
        Result result = null;
        try {
            result = super.getData("SELECT * FROM accounts WHERE guid = " + id.toString());
            ResultSet RS = result.resultSet;

            while (RS.next()) {
                Account a = World.world.getAccount(RS.getInt("guid"));
                if (a != null && a.isOnline())
                    continue;

                Account C = new Account(RS.getInt("guid"), RS.getString("account").toLowerCase(), RS.getString("pseudo"), RS.getString("reponse"), (RS.getInt("banned") == 1), RS.getString("lastIP"), RS.getString("lastConnectionDate"), RS.getString("friends"), RS.getString("enemy"), RS.getInt("points"), RS.getLong("subscribe"), RS.getLong("muteTime"), RS.getString("mutePseudo"), RS.getString("lastVoteIP"), RS.getString("heurevote"), RS.getBoolean("vip"), RS.getString("switchPacketKey"));
                World.world.addAccount(C);
            }
        } catch (Exception e) {
            super.sendError("AccountData load id", e);
        } finally {
            close(result);
        }
    }

    public void load() {
        Result result = null;
        try {
            result = super.getData("SELECT * from accounts");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                if(RS.getString("pseudo").isEmpty()) continue;
                Account a = new Account(RS.getInt("guid"), RS.getString("account").toLowerCase(), RS.getString("pseudo"), RS.getString("reponse"), (RS.getInt("banned") == 1), RS.getString("lastIP"), RS.getString("lastConnectionDate"), RS.getString("friends"), RS.getString("enemy"), RS.getInt("points"), RS.getLong("subscribe"), RS.getLong("muteTime"), RS.getString("mutePseudo"), RS.getString("lastVoteIP"), RS.getString("heurevote"), RS.getBoolean("vip"), RS.getString("switchPacketKey"));
                World.world.addAccount(a);
            }
        } catch (Exception e) {
            super.sendError("AccountData load", e);
        } finally {
            close(result);
        }
    }

    public void updateVoteAll() {
        Result result = null;
        Account a = null;
        try {
            result = super.getData("SELECT guid, heurevote, lastVoteIP from accounts");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                a = World.world.getAccount(RS.getInt("guid"));
                if (a == null)
                    continue;
                a.updateVote(RS.getString("heurevote"), RS.getString("lastVoteIP"));
            }
        } catch (SQLException e) {
            super.sendError("AccountData updateVoteAll", e);
        } finally {
            close(result);
        }
    }
    public void reload(Account account) {
        Result result = null;
        try {
            result = super.getData("SELECT * from accounts WHERE guid='"+ account.getId() +"'");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                if(RS.getString("pseudo").isEmpty()) continue;
                Account a = new Account(RS.getInt("guid"), RS.getString("account").toLowerCase(), RS.getString("pseudo"), RS.getString("reponse"), (RS.getInt("banned") == 1), RS.getString("lastIP"), RS.getString("lastConnectionDate"), RS.getString("friends"), RS.getString("enemy"), RS.getInt("points"), RS.getLong("subscribe"), RS.getLong("muteTime"), RS.getString("mutePseudo"), RS.getString("lastVoteIP"), RS.getString("heurevote"), RS.getBoolean("vip"), RS.getString("switchPacketKey"));
                World.world.removeAccount(account);
                World.world.addAccount(a);
            }
        } catch (Exception e) {
            super.sendError("AccountData load", e);
        } finally {
            close(result);
        }
    }

    @Override
    public boolean update(Account acc) {
        PreparedStatement statement = null;
        try {
            statement = getPreparedStatement("UPDATE accounts SET banned = '"
                    + (acc.isBanned() ? 1 : 0) + "', friends = '"
                    + acc.parseFriendListToDB() + "', enemy = '"
                    + acc.parseEnemyListToDB() + "', muteTime = '"
                    + acc.getMuteTime() + "', mutePseudo = '"
                    + acc.getMutePseudo() + "' WHERE guid = '" + acc.getId()
                    + "'");
            execute(statement);
            return true;
        } catch (Exception e) {
            super.sendError("AccountData update", e);
        } finally {
            close(statement);
        }
        return false;
    }

    public void updateLastConnection(Account compte) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE accounts SET `lastIP` = ?, `lastConnectionDate` = ? WHERE `guid` = ?");
            p.setString(1, compte.getCurrentIp());
            p.setString(2, compte.getLastConnectionDate());
            p.setInt(3, compte.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("AccountData updateLastConnection", e);
        } finally {
            close(p);
        }
    }

    public void setLogged(int id, int logged) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `accounts` SET `logged` = ? WHERE `guid` = ?;");
            p.setInt(1, logged);
            p.setInt(2, id);
            execute(p);
        } catch (SQLException e) {
            super.sendError("AccountData setLogged", e);
        } finally {
            close(p);
        }
    }

    public boolean updateBannedTime(Account acc, long time) {
        PreparedStatement statement = null;
        try {
            statement = getPreparedStatement("UPDATE accounts SET banned = '"
                    + (acc.isBanned() ? 1 : 0) + "', bannedTime = '"
                    + time + "' WHERE guid = '" + acc.getId()
                    + "'");
            execute(statement);
            return true;
        } catch (Exception e) {
            super.sendError("AccountData update", e);
        } finally {
            close(statement);
        }
        return false;
    }

    /** Points **/
    public int loadPoints(String user) {
        return this.loadPointsWithoutUsersDb(user);
    }

    public void updatePoints(int id, int points) {
        this.updatePointsWithoutUsersDb(id, points);
    }

    public int loadPointsWithoutUsersDb(String user) {
        Result result = null;
        int points = 0;
        try {
            result = super.getData("SELECT * from accounts WHERE `account` LIKE '"
                    + user + "'");
            ResultSet RS = result.resultSet;
            if (RS.next()) {
                points = RS.getInt("points");
            }
        } catch (SQLException e) {
            super.sendError("AccountData loadPoints", e);
        } finally {
            close(result);
        }
        return points;
    }

    public void updatePointsWithoutUsersDb(int id, int points) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE accounts SET `points` = ? WHERE `guid` = ?");
            p.setInt(1, points);
            p.setInt(2, id);
            execute(p);
        } catch (SQLException e) {
            super.sendError("AccountData updatePoints", e);
        } finally {
            close(p);
        }
    }

    public int loadPointsWithUsersDb(String account) {
        Result result = null;
        int points = 0, user = -1;
        try {
            result = super.getData("SELECT account, users FROM `accounts` WHERE `account` LIKE '" + account + "'");
            ResultSet RS = result.resultSet;
            if (RS.next()) user = RS.getInt("users");
            close(result);

            if(user == -1) {
                result = super.getData("SELECT id, points FROM `users` WHERE `id` = " + user + ";");
                RS = result.resultSet;
                if (RS.next()) points = RS.getInt("users");
            }
        } catch (SQLException e) {
            super.sendError("AccountData loadPoints", e);
        } finally {
            close(result);
        }
        return points;
    }

    public void updatePointsWithUsersDb(int id, int points) {
        PreparedStatement p = null;
        int user = -1;
        try {
            Result result = super.getData("SELECT guid, users FROM `accounts` WHERE `guid` LIKE '" + id + "'");
            ResultSet RS = result.resultSet;
            if (RS.next()) user = RS.getInt("users");
            close(result);

            if(user != -1) {
                p = getPreparedStatement("UPDATE `users` SET `points` = ? WHERE `id` = ?;");
                p.setInt(1, points);
                p.setInt(2, id);
                execute(p);
            }
        } catch (SQLException e) {
            super.sendError("AccountData updatePoints", e);
        } finally {
            close(p);
        }
    }
}