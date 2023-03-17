package org.starloco.locos.client.other;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;

import java.util.ArrayList;

public class Party {

    public final Player chief;
    public final ArrayList<Player> players = new ArrayList<>();
	public Player master;

    public Party(Player p1, Player p2) {
        this.chief = p1;
        this.players.add(p1);
        this.players.add(p2);
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public Player getChief() {
        return this.chief;
    }

    public boolean isChief(int id) {
        return this.chief.getId() == id;
    }
    
    public Player getMaster() {
        return master;
    }
    
    public void setMaster(Player master) {
        this.master = master;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void leave(Player player) {
        if (!this.players.contains(player)) return;

        player.follow = null;
        player.follower.clear();
        player.setParty(null);
        this.players.remove(player);

        for(Player member : this.players) {
            if(member.follow == player) member.follow = null;
            if(member.follower.containsKey(player.getId())) member.follower.remove(player.getId());
        }

        if (this.players.size() == 1) {
            this.players.get(0).setParty(null);
            if (this.players.get(0).getAccount() == null || this.players.get(0).getGameClient() == null)
                return;
            SocketManager.GAME_SEND_PV_PACKET(this.players.get(0).getGameClient(), "");
        } else {
            SocketManager.GAME_SEND_PM_DEL_PACKET_TO_GROUP(this, player.getId());
        }
    }
    
    public void moveAllPlayersToMaster(final GameCase cell) {
        if(this.master != null) {
            this.players.stream().filter((follower1) -> isWithTheMaster(follower1, false)).forEach(follower -> follower.setBlockMovement(true));
            this.players.stream().filter((follower1) -> isWithTheMaster(follower1, false)).forEach(follower -> {
                try {
                    final GameCase newCell = cell != null ? cell : this.master.getCurCell();
                    String path = PathFinding.getShortestStringPathBetween(this.master.getCurMap(), follower.getCurCell().getId(), newCell.getId(), 0);
                    if (path != null) {
                            follower.getCurCell().removePlayer(follower);
                            follower.setCurCell(newCell);
                            follower.getCurCell().addPlayer(follower);

                        SocketManager.GAME_SEND_GA_PACKET_TO_MAP(follower.getCurMap(), "0", 1, String.valueOf(follower.getId()), path);
                    }
                } catch (Exception ignored) {}
            });
            this.players.stream().filter((follower1) -> isWithTheMaster(follower1, false)).forEach(follower -> follower.setBlockMovement(false));
        }
    }
    
    public boolean isWithTheMaster(Player follower, boolean inFight) {
        return follower != null && !follower.getName().equals(this.master.getName()) &&  this.players.contains(follower) && follower.getGameClient()
                != null && this.master.getCurMap().getId() == follower.getCurMap().getId() && (inFight ? follower.getFight() == this.master.getFight() : follower.getFight() == null);
    }
    
    public void teleportAllEsclaves()
	{
    	for (Player follower : players)
		{
		if(follower.getExchangeAction() != null)
		{
            follower.sendMessage("Vous n'avez pas pu être téléporté car vous êtes occupé.");
            master.sendMessage("Le joueur "+follower.getName()+" est occupé et n'a pas pu être téléporté.");
            continue;
		}
		if(master.getCurMap().getId() != follower.getCurMap().getId() && GameMap.IsInDj(master.getCurMap()))
			follower.teleport(master.getCurMap().getId(), master.getCurCell().getId());
		}
		
	}
    
}
