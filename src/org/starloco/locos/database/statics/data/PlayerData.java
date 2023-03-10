package org.starloco.locos.database.statics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.client.Player;
import org.starloco.locos.command.administration.Group;
import org.starloco.locos.database.Database;
import org.starloco.locos.database.statics.AbstractDAO;
import org.starloco.locos.exchange.transfer.DataQueue;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class PlayerData extends AbstractDAO<Player> {

    public PlayerData(HikariDataSource dataSource) {
        super(dataSource);
    }
    
    public int getNextId() {
        final DataQueue.Queue<Integer> queue = new DataQueue.Queue<>((byte) 1);
        try {
            synchronized(queue) {
                long count = DataQueue.count();
                DataQueue.queues.put(count, queue);
                Main.exchangeClient.send("DI" + queue.getType() + count);
                queue.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return queue.getValue();
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT * FROM players");
            this.load(result.resultSet, false);
        } catch (SQLException e) {
            super.sendError("PlayerData load", e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }
    
    private void load(final ResultSet RS, final boolean checkOldPlayer) throws SQLException{
    	 while (RS.next()) {
             if (RS.getInt("server") != Main.serverId)
                 continue;

             HashMap<Integer, Integer> stats = new HashMap<Integer, Integer>();
             stats.put(Constant.STATS_ADD_VITA, RS.getInt("vitalite"));
             stats.put(Constant.STATS_ADD_FORC, RS.getInt("force"));
             stats.put(Constant.STATS_ADD_SAGE, RS.getInt("sagesse"));
             stats.put(Constant.STATS_ADD_INTE, RS.getInt("intelligence"));
             stats.put(Constant.STATS_ADD_CHAN, RS.getInt("chance"));
             stats.put(Constant.STATS_ADD_AGIL, RS.getInt("agilite"));
             final int id = RS.getInt("id");
             final short prestige = Config.getInstance().prestige ? RS.getShort("prestige") : (short) 0;
             Player player = new Player(id, RS.getString("name"), RS.getInt("groupe"), RS.getInt("sexe"), RS.getInt("class"), RS.getInt("color1"), RS.getInt("color2"), RS.getInt("color3"), RS.getLong("kamas"), RS.getInt("spellboost"), RS.getInt("capital"), RS.getInt("energy"), RS.getInt("level"), RS.getLong("xp"), RS.getInt("size"), RS.getInt("gfx"), RS.getByte("alignement"), RS.getInt("account"), stats, RS.getByte("seeFriend"), RS.getByte("seeAlign"), RS.getByte("seeSeller"), RS.getString("canaux"), RS.getShort("map"), RS.getInt("cell"), RS.getString("objets"), RS.getString("storeObjets"), RS.getInt("pdvper"), RS.getString("spells"), RS.getString("savepos"), RS.getString("jobs"), RS.getInt("mountxpgive"), RS.getInt("mount"), RS.getInt("honor"), RS.getInt("deshonor"), RS.getInt("alvl"), RS.getString("zaaps"), RS.getByte("title"), RS.getInt("wife"), RS.getString("morphMode"), RS.getString("allTitle"), RS.getString("emotes"), RS.getLong("prison"), false, RS.getString("parcho"), RS.getLong("timeDeblo"), RS.getBoolean("noall"), RS.getString("deadInformation"), RS.getByte("deathCount"), RS.getLong("totalKills"), prestige, RS.getString("artefact"), RS.getString("saveSpells"), RS.getInt("saveSpellPts"));

             if(checkOldPlayer) {
            	 Player oldPlayer = World.world.getPlayer(id);
            	 if(oldPlayer != null)
            		 player.setNeededEndFight(oldPlayer.needEndFight(), oldPlayer.hasMobGroup());
             }

             player.VerifAndChangeItemPlace();
             World.world.addPlayer(player);
             if (player.isShowSeller())
                 World.world.addSeller(player);

         }
    }

    public Player load(int obj) {
        Result result = null;
        Player player = null;
        try {
            result = getData("SELECT * FROM players WHERE id = '" + obj + "'");
            this.load(result.resultSet, true);
        } catch (SQLException e) {
            super.sendError("PlayerData load id", e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
        return player;
    }

    public String loadTitles(int guid) {
        Result result = null;
        String title = "";
        try {
            result = getData("SELECT * FROM players WHERE id = '" + guid + "';");
            ResultSet RS = result.resultSet;
            if (RS.next()) {
                title = RS.getString("allTitle");
            }
        } catch (SQLException e) {
            super.sendError("PlayerData loadTitles", e);
        } finally {
            close(result);
        }
        return title;
    }

    public boolean add(Player perso) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("INSERT INTO players(`id`, `name`, `sexe`, `class`, `color1`, `color2`, `color3`, `kamas`, `spellboost`, `capital`, `energy`, `level`, `xp`, `size`, `gfx`, `account`, `cell`, `map`, `spells`, `objets`, `storeObjets`, `morphMode`, `server`, `prestige`, `artefact`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'','','0',?,?,?)");
            p.setInt(1, perso.getId());
            p.setString(2, perso.getName());
            p.setInt(3, perso.getSexe());
            p.setInt(4, perso.getClasse());
            p.setInt(5, perso.getColor1());
            p.setInt(6, perso.getColor2());
            p.setInt(7, perso.getColor3());
            p.setLong(8, perso.getKamas());
            p.setInt(9, perso.get_spellPts(false));
            p.setInt(10, perso.get_capital());
            p.setInt(11, perso.getEnergy());
            p.setInt(12, perso.getLevel());
            p.setLong(13, perso.getExp());
            p.setInt(14, perso.get_size());
            p.setInt(15, perso.getGfxId());
            p.setInt(16, perso.getAccID());
            p.setInt(17, perso.getCurCell().getId());
            p.setInt(18, perso.getCurMap().getId());
            p.setString(19, perso.parseSpellToDB(false));
            p.setInt(20, Main.serverId);
            p.setInt(21, perso.getPrestige());
            p.setString(22, perso.getArtefactToString());
            execute(p);
            return true;
        } catch (SQLException e) {
            super.sendError("PlayerData add", e);
        } finally {
            close(p);
        }
        return false;
    }

    public boolean delete(Player perso) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("DELETE FROM players WHERE id = ?");
            p.setInt(1, perso.getId());
            execute(p);
			
			p = getPreparedStatement("DELETE FROM `world.entity.players.quests` where `player` = ?;");
            p.setInt(1, perso.getId()); //Fix quests by Coding Mestre
            execute(p);

            if (!perso.getItemsIDSplitByChar(",").equals(""))
                for(String id : perso.getItemsIDSplitByChar(",").split(","))
                    Database.getDynamics().getObjectData().delete(Integer.parseInt(id));
            if (!perso.getStoreItemsIDSplitByChar(",").equals(""))
                for(String id : perso.getStoreItemsIDSplitByChar(",").split(","))
                    Database.getDynamics().getObjectData().delete(Integer.parseInt(id));
            if (perso.getMount() != null)
                Database.getDynamics().getMountData().update(perso.getMount());
            return true;
        } catch (SQLException e) {
            super.sendError("PlayerData delete", e);
        } finally {
            close(p);
        }
        return false;
    }

    @Override
    public void load(Object obj) {}

    @Override
    public boolean update(Player player) {
        if (player == null) {
            super.sendError("PlayerData update", new Exception("perso is null"));
            return false;
        }

        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `players` SET `kamas`= ?, `spellboost`= ?, `capital`= ?, `energy`= ?, `level`= ?, `xp`= ?, `size` = ?, `gfx`= ?, `alignement`= ?, `honor`= ?, `deshonor`= ?, `alvl`= ?, `vitalite`= ?, `force`= ?, `sagesse`= ?, `intelligence`= ?, `chance`= ?, `agilite`= ?, `seeFriend`= ?, `seeAlign`= ?, `seeSeller`= ?, `canaux`= ?, `map`= ?, `cell`= ?, `pdvper`= ?, `spells`= ?, `objets`= ?, `storeObjets`= ?, `savepos`= ?, `zaaps`= ?, `jobs`= ?, `mountxpgive`= ?, `mount`= ?, `title`= ?, `wife`= ?, `morphMode`= ?, `allTitle` = ?, `emotes` = ?, `prison` = ?, `parcho` = ?, `timeDeblo` = ?, `noall` = ?, `deadInformation` = ?, `deathCount` = ?, `totalKills` = ?, `prestige` = ?, `artefact` = ?, `saveSpells` = ?, `saveSpellPts` = ? WHERE `players`.`id` = ? LIMIT 1");
            p.setLong(1, player.getKamas());
            p.setInt(2, player.get_spellPts(false));
            p.setInt(3, player.get_capital());
            p.setInt(4, player.getEnergy());
            p.setInt(5, player.getLevel());
            p.setLong(6, player.getExp());
            p.setInt(7, player.get_size());
            p.setInt(8, player.getGfxId());
            p.setInt(9, player.get_align());
            p.setInt(10, player.get_honor());
            p.setInt(11, player.getDeshonor());
            p.setInt(12, player.getALvl());
            p.setInt(13, player.stats.getEffect(Constant.STATS_ADD_VITA));
            p.setInt(14, player.stats.getEffect(Constant.STATS_ADD_FORC));
            p.setInt(15, player.stats.getEffect(Constant.STATS_ADD_SAGE));
            p.setInt(16, player.stats.getEffect(Constant.STATS_ADD_INTE));
            p.setInt(17, player.stats.getEffect(Constant.STATS_ADD_CHAN));
            p.setInt(18, player.stats.getEffect(Constant.STATS_ADD_AGIL));
            p.setInt(19, (player.is_showFriendConnection() ? 1 : 0));
            p.setInt(20, (player.is_showWings() ? 1 : 0));
            p.setInt(21, (player.isShowSeller() ? 1 : 0));
            p.setString(22, player.get_canaux());
            if (player.getCurMap() != null)
                p.setInt(23, player.getCurMap().getId());
            else
                p.setInt(23, 7411);
            if (player.getCurCell() != null)
                p.setInt(24, player.getCurCell().getId());
            else
                p.setInt(24, 311);
            p.setInt(25, player.get_pdvper());
            p.setString(26, player.parseSpellToDB(false));
            p.setString(27, player.parseObjetsToDB());
            p.setString(28, player.parseStoreItemstoBD());
            p.setString(29, player.getSavePosition());
            p.setString(30, player.parseZaaps());
            p.setString(31, player.parseJobData());
            p.setInt(32, player.getMountXpGive());
            p.setInt(33, (player.getMount() != null ? player.getMount().getId() : -1));
            p.setByte(34, (player.get_title()));
            p.setInt(35, player.getWife());
            p.setString(36, (player.getMorphMode() ? 1 : 0) + ";"
                    + player.getMorphId());
            p.setString(37, player.getAllTitle());
            p.setString(38, player.parseEmoteToDB());
            p.setLong(39, (player.isInEnnemyFaction ? player.enteredOnEnnemyFaction : 0));
            p.setString(40, player.parseStatsParcho());
            p.setLong(41, player.getTimeTaverne());
            p.setBoolean(42, player.noall);
            p.setString(43, player.getDeathInformation());
            p.setByte(44, player.getDeathCount());
            p.setLong(45, player.getTotalKills());
            p.setInt(46, player.getPrestige());
            p.setString(47, player.getArtefactToString());
            p.setString(48, player.parseSpellToDB(true));
            p.setInt(49, player.get_spellPts(true));
            p.setInt(50, player.getId());
            execute(p);
            if (player.getGuildMember() != null)
                Database.getDynamics().getGuildMemberData().update(player);
            if (player.getMount() != null)
                Database.getDynamics().getMountData().update(player.getMount());
        } catch (Exception e) {
            super.sendError("PlayerData update", e);
        } finally {
            close(p);
        }

        if (player.getQuestPerso() != null && !player.getQuestPerso().isEmpty())
            player.getQuestPerso().values().stream().filter(QP -> QP != null).forEach(QP -> Database.getDynamics().getQuestPlayerData().update(QP, player));

        return true;
    }

    public void updateInfos(Player perso) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `players` SET `name` = ?, `sexe`=?, `class`= ?, `spells`= ? WHERE `id`= ?");
            p.setString(1, perso.getName());
            p.setInt(2, perso.getSexe());
            p.setInt(3, perso.getClasse());
            p.setString(4, perso.parseSpellToDB(false));
            p.setInt(5, perso.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("PlayerData updateInfos", e);
        } finally {
            close(p);
        }
    }
    public void updateSpells(Player perso) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `players` SET `saveSpells` = ?, `saveSpellPts`=? WHERE `id`= ?");
            p.setString(1, perso.parseSpellToDB(true));
            p.setInt(2, perso.get_spellPts(true));
            p.setInt(3, perso.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("PlayerData updateSpells", e);
        } finally {
            close(p);
        }
    }

    public void updateGroupe(int group, String name) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `players` SET `groupe` = ? WHERE `name` = ?;");

            p.setInt(1, group);
            p.setString(2, name);
            execute(p);
        } catch (SQLException e) {
            super.sendError("PlayerData updateGroupe", e);
        } finally {
            close(p);
        }
    }

    public void updateGroupe(Player perso) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `players` SET `groupe` = ? WHERE `id`= ?");
            int id = (perso.getGroupe() != null) ? perso.getGroupe().getId() : -1;
            p.setInt(1, id);
            p.setInt(2, perso.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("PlayerData updateGroupe", e);
        } finally {
            close(p);
        }
    }

    public void updateTimeTaverne(Player player) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE players SET `timeDeblo` = ? WHERE `id` = ?");
            p.setLong(1, player.getTimeTaverne());
            p.setInt(2, player.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("PlayerData updateTimeDeblo", e);
        } finally {
            close(p);
        }
    }

    public void updateTitles(int guid, String title) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE players SET `allTitle` = ? WHERE `id` = ?");
            p.setString(1, title);
            p.setInt(2, guid);
            execute(p);
        } catch (SQLException e) {
            super.sendError("PlayerData updateTitles", e);
        } finally {
            close(p);
        }
    }

    public void updateLogged(int guid, int logged) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE players SET `logged` = ? WHERE `id` = ?");
            p.setInt(1, logged);
            p.setInt(2, guid);
            execute(p);
        } catch (SQLException e) {
            super.sendError("PlayerData updateLogged", e);
        } finally {
            close(p);
        }
    }

    public void updateAllLogged(int guid, int logged) {
        PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `players` SET `logged` = ? WHERE `account` = ?");
            p.setInt(1, logged);
            p.setInt(2, guid);
            execute(p);
        } catch (SQLException e) {
            super.sendError("PlayerData updateAllLogged", e);
        } finally {
            close(p);
        }
    }

    public boolean exist(String name) {
        Result result = null;
        boolean exist = false;
        try {
            result = getData("SELECT COUNT(*) AS exist FROM players WHERE name LIKE '" + name + "';");
            ResultSet RS = result.resultSet;
            if (RS.next()) {
                if (RS.getInt("exist") > 0)
                    exist = true;
            }
            System.out.println(exist);
        } catch (SQLException e) {
            super.sendError("PlayerData exist", e);
        } finally {
            close(result);
        }
        return exist;
    }

    public String haveOtherPlayer(int account) {
        Result result = null;
        String servers = "";
        try {
            result = getData("SELECT server FROM players WHERE account = '"
                    + account + "' AND NOT server = '" + Main.serverId + "'");
            ResultSet RS = result.resultSet;
            while (RS.next()) {
                servers += (servers.isEmpty() ? RS.getInt("server") : ","
                        + RS.getInt("server"));
            }
        } catch (SQLException e) {
            super.sendError("PlayerData haveOtherPlayer", e);
        } finally {
            close(result);
        }
        return servers;
    }

    public void reloadGroup(Player p) {
        Result result = null;
        try {
            result = getData("SELECT groupe FROM players WHERE id = '"
                    + p.getId() + "'");
            ResultSet RS = result.resultSet;
            if (RS.next()) {
                int group = RS.getInt("groupe");
                Group g = Group.getGroupeById(group);
                p.setGroupe(g, false);
            }
        } catch (SQLException e) {
            super.sendError("PlayerData reloadGroup", e);
        } finally {
            close(result);
        }
    }

    public byte canRevive(Player player) {
        Result result = null;
        byte revive = 0;
        try {
            result = getData("SELECT id, revive FROM players WHERE `id` = '"
                    + player.getId() + "';");
            ResultSet RS = result.resultSet;
            while (RS.next())
                revive = RS.getByte("revive");
        } catch (SQLException e) {
            super.sendError("PlayerData canRevive", e);
        } finally {
            close(result);
        }
        return revive;
    }

    public void setRevive(Player player) {
        try {
            PreparedStatement p = getPreparedStatement("UPDATE players SET `revive` = 0 WHERE `id` = '" + player.getId() + "';");
            execute(p);
            close(p);
        } catch (SQLException e) {
            super.sendError("PlayerData setRevive", e);
        }
    }

	public void updateArtefact(final Player perso)
    {
    	PreparedStatement p = null;
        try {
            p = getPreparedStatement("UPDATE `players` SET `artefact`= ? WHERE `id`= ?");
            p.setString(1, perso.getArtefactToString());
            p.setInt(2, perso.getId());
            execute(p);
        } catch (SQLException e) {
            super.sendError("PlayerData updateArtefact", e);
        } finally {
            close(p);
        }
    }
}
