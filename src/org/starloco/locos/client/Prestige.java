package org.starloco.locos.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.starloco.locos.area.map.entity.Animation;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.ConditionParser;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Logging;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.util.TimerWaiter;
import org.starloco.locos.util.TimerWaiter.DataType;

/**
 * 
 * Date : 09 octobre 2021
 * @author Kevin#6537
 *
 */
public class Prestige {

	private final short id;
	private final int requiredLevel;
	private final String condition;
	private final int titleID;
	private final Map<Integer, Integer> artefact;
	private final Animation animation;
	private final boolean keepOldParcho;
	private final boolean keepNewParcho;
	private final List<Spell> keepSpell = new ArrayList<>();
	private final int levelToDown;
	private final double malusXp;
	private final int mapToTp;
	private final int cellToTp;
	private final long priceKamas;
	private final int priceBoutique;
	private final String priceItem;
	private final String infosCondition;
	private final PrestigeBonus prestigeBonus;
	

	public Prestige(short id, int requiredLevel, String condition, int titleID, String artefact, Animation animation,
			boolean keepOldParcho, boolean keepNewParcho, String keepSpell, int levelToDown, double malusXp, int mapToTp,
			int cellToTp, long priceKamas, int priceBoutique, String priceItem, String infosCondition) {
		this.id = id;
		this.requiredLevel = requiredLevel;
		this.condition = condition == null ? "" : condition;
		this.titleID = titleID;
		this.animation = animation;
		this.keepOldParcho = keepOldParcho;
		this.keepNewParcho = keepNewParcho;
		this.levelToDown = levelToDown;
		this.malusXp = malusXp;
		this.mapToTp = mapToTp;
		this.cellToTp = cellToTp;
		this.priceKamas = priceKamas;
		this.priceBoutique = priceBoutique;
		this.priceItem = priceItem == null ? "" : priceItem;
		this.infosCondition = infosCondition;
		this.prestigeBonus = new PrestigeBonus();
		this.setSpell(keepSpell, this.keepSpell);
		this.artefact = new HashMap<>();
		if(artefact != null && !artefact.isEmpty())
		{
			try {
        		for(final String mob : artefact.split(";"))
        			this.artefact.put(Integer.parseInt(mob.split(",")[0]), Integer.parseInt(mob.split(",")[1]));
        	}catch(NumberFormatException | ArrayIndexOutOfBoundsException e)
        	{
        		e.printStackTrace();
        		Main.stop("Problème lors de la génération des artefact Prestige");
        		return;
        	}
		}
	}

	private byte canPrestige(final Player player)
	{
		if(player.getLevel() < requiredLevel) return 0;
		if(!this.condition.isEmpty())
			for(final String cond : this.condition.split(";"))
				if(!ConditionParser.validConditions(player, cond.trim())) return 1;
			
		final Map<Integer, Integer> artefactPlayer = player.getArtefact();
		for(final Entry<Integer, Integer> entry : this.artefact.entrySet())
		{
			if(!artefactPlayer.containsKey(entry.getKey())) return 6;
			if(artefactPlayer.get(entry.getKey()) < entry.getValue()) return 6;
		}
		
		if(player.getKamas() < this.priceKamas) return 2;
		if(player.getAccount().getPoints() < this.priceBoutique) return 3;
		
		if(!this.priceItem.isEmpty()) {
			final String[] items = this.priceItem.split(";");
			try {	
				for(final String item : items) {
					final int itemID = Integer.parseInt(item.split(",")[0]);
					final short amount = Short.parseShort(item.split(",")[1]);
					if(!player.hasItemTemplate(itemID, amount)) return 4;
				}
			}catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
				player.sendErrorMessage("Un problème avec le Prestige est survenue , merci de contacter un administrateur.");
				e.printStackTrace();
				return 5;
			}
		}
		
		return 7;
	}
	
	private void payPrice(final Player player)
	{
		if(!this.priceItem.isEmpty()) 
		{
			final String[] items = this.priceItem.split(";");
			for(final String item : items)
			{
				final int itemID = Integer.parseInt(item.split(",")[0]);
				final short amount = Short.parseShort(item.split(",")[1]);
				player.removeByTemplateID(itemID, amount);
			}
		}
			
		player.setKamas(player.getKamas() - this.priceKamas);
		player.getAccount().setPoints(player.getAccount().getPoints() - this.priceBoutique);
	}
	
	private boolean check(final Player player, final boolean passPrice)
	{
		if(this.mapToTp != -1 && this.cellToTp != -1 && player.cantTP())
		{
			player.sendInformationMessage("Tu ne peux pas passer prestige car tu ne peux pas te téléporter.");
			return false;
		}
		if(player.getFight() != null || player.getExchangeAction() != null || player.isAway())
		{
			player.sendInformationMessage("Tu ne peux pas passer prestige car tu es occupé.");
			return false;
		}
		
		if(passPrice) return true; // Pour la commande admin
		
		switch(this.canPrestige(player))
    	{
        	case 0: // level
        		player.sendInformationMessage("Tu n'as pas le level requis.");
        		return false;
        	case 1: // condition
        		player.sendInformationMessage("Tu ne respectes pas certaines conditions.");
        		return false;
        	case 2: // kamas
        		player.sendInformationMessage("Tu n'as pas assez de kamas.");
        		return false;
        	case 3: // boutique
        		player.sendInformationMessage("Tu n'as pas assez de points de boutique.");
        		return false;
        	case 4: // priceItem
        		player.sendInformationMessage("Tu n'as pas l'/les item(s) requis");
        		return false;
        	case 5: // error
        		return false;
        	case 6: // artefact
        		player.sendInformationMessage("Tu n'as pas tué les monstres nécessaire pour pouvoir passer au prestige suivant.");
        		return false;
    	}
		
		return true;
	}
	
	public void apply(final Player player, final short oldPrestige, final boolean passPrice)
	{
		if(!this.check(player, passPrice)) return;			
		if(!passPrice) this.payPrice(player); // Le joueur paye le prestige.
		
		
		// OK
		
		player.DestuffALL();
		
		if (player.getLevel() >= 100)
			player.getStats().addOneStat(Constant.STATS_ADD_PA, -1);
		
		final List<Spell> spellToKeep = new ArrayList<>();
		for(final Spell spell : this.keepSpell)
			if(player.hasSpell(spell.getSpellID())) spellToKeep.add(spell);
		player.setSpells(Constant.getStartSorts(player.getClasse()));
		player.setLevel(this.levelToDown);
		player.setXp((int)World.world.getExpLevel(player.getLevel()).perso);
		
		for(int level = 3; level < player.getLevel(); ++level)
			Constant.onLevelUpSpells(player, level, false);
		for(final Spell spell : spellToKeep)
			if(!player.hasSpell(spell.getSpellID())) player.learnSpell(spell.getSpellID(), 1, false, false, false);	
		player.set_spellPts(player.getLevel() - 1);
		
		if(this.keepNewParcho) 
		{
			player.incrementePrestige();
			player.parcho();
		}
		else if(this.keepOldParcho) player.restatKeepParcho();
		else player.restatAll(0);
		
		if(!this.keepNewParcho) player.incrementePrestige();
		if(this.titleID != -1)
			player.set_title(this.titleID);
		if(oldPrestige > 0)
			World.world.getPrestigeById(oldPrestige).getPrestigeBonus().removeStatsAfterNextPrestige(player);
		this.prestigeBonus.giveBonusToPlayer(player);
		
		for(final Map.Entry<ObjectTemplate, Couple<Short, Boolean>> entry : this.getPrestigeBonus().getItems().entrySet()) {
			final GameObject obj = entry.getKey().createNewItem(entry.getValue().first, entry.getValue().second);
			if(player.addObjet(obj, true)) World.world.addGameObject(obj, true);
		}
		
		if(this.getPrestigeBonus().getItems().size() > 0) 
			player.sendInformationMessage("Des items t'ont été donné suite a ton passement de prestige !");
		
		
		player.save();
		if(this.mapToTp != -1 && this.cellToTp != -1)
			player.teleport((short)this.mapToTp, this.cellToTp);
		else 
			SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
		SocketManager.GAME_SEND_STATS_PACKET(player);
		SocketManager.GAME_SEND_SPELL_LIST(player);
		if(player.getParty() != null)
			SocketManager.GAME_SEND_PM_MOD_PACKET_TO_GROUP(player.getParty(), player);
		if(this.animation != null)
		{
			byte limite = 0;
			for(final Player p : player.getCurMap().getPlayers())
			{
				if(limite > 3) break;
				++limite;
				if(p != player) TimerWaiter.addNext(()->{SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getCurMap(), "0", 228, p.getId() + ";" + player.getCurCell().getId() + "," + Animation.PrepareToGA(this.animation), "");}, 3000, DataType.MAP);
			}
				
			for(final Entry<Integer, Npc> n : player.getCurMap().getNpcs().entrySet())
			{
				if(limite > 3) break;
				++limite;
				TimerWaiter.addNext(()->{SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getCurMap(), "0", 228, n.getKey() + ";" + player.getCurCell().getId() + "," + Animation.PrepareToGA(this.animation), "");}, 2000, DataType.MAP);
			}
		}	
        SocketManager.GAME_SEND_MESSAGE_TO_ALL("Félicitation <b><a href='asfunction:onHref,ShowPlayerPopupMenu," + player.getName() + "'>" + player.getName() + "</a></b> est passé Prestige " + player.getPrestige() + " !", "FF0000");
	}
	
	public void sendInfosCondition(final Player player)
	{
		final String message = "Condition pour passer Prestige " + this.id + " : <br>"
				+ "Level requis : " + this.requiredLevel + "<br>"
				+ "Kamas requis : " + this.priceKamas + "<br>"
				+ "Points Boutique requis : " + this.priceBoutique + "<br>"
				+ "Information supplémentaire : <br>"
				+ this.infosCondition;
		
		SocketManager.send(player, "BAIO"+message);
	}
	
	public void sendBonus(final Player player, final String title)
	{
		final PrestigeBonus pb = this.getPrestigeBonus();
		
		final String message = title + " <b>" + this.id + "</b> : <br>"
				+ "+<b>" + pb.getBaseStats()[0] + "</b> vitalité.<br>"
				+ "+<b>" + pb.getBaseStats()[1] + "</b> sagesse.<br>"
				+ "+<b>" + pb.getBaseStats()[2] + "</b> force.<br>"
				+ "+<b>" + pb.getBaseStats()[3] + "</b> intel.<br>"
				+ "+<b>" + pb.getBaseStats()[4] + "</b> agilité.<br>"
				+ "+<b>" + pb.getBaseStats()[5] + "</b> chance.<br>"
				+ "+<b>" + pb.getPrimaryStats()[0] + "</b> PA.<br>"
				+ "+<b>" + pb.getPrimaryStats()[1] + "</b> PM.<br>"
				+ "+<b>" + pb.getPrimaryStats()[2] + "</b> PO.<br>"
				+ "+<b>" + pb.getPrimaryStats()[3] + "</b> invoc.<br>"
				+ "+<b>" + pb.getAdvancedStats()[0] + "</b> dommage.<br>"
				+ "+<b>" + pb.getAdvancedStats()[1] + "</b> prospection.<br>"
				+ "+<b>" + pb.getAdvancedStats()[2] + "</b> initiative.<br>"
				+ "+<b>" + pb.getAdvancedStats()[3] + "</b> % dommage.<br>"
				+ "+<b>" + pb.getAdvancedStats()[4] + "</b> dommage piège.<br>"
				+ "+<b>" + pb.getAdvancedStats()[5] + "</b> % dommage piège.<br>"
				+ "+<b>" + pb.getAdvancedStats()[6] + "</b> soin.<br>"
				+ "+<b>" + pb.getCapital() + "</b> capital / "
				+ "+<b>" + pb.getPdvMax() + "</b> pdv par level.<br>"
				+ "+<b>" + pb.getParcho() + "</b> max parcho.";
		
		
		SocketManager.send(player, "BAIO"+message);
	}

	public short getId() {
		return id;
	}

	public Map<Integer, Integer> getArtefact() {
		return artefact;
	}

	public double getMalusXp() {
		return malusXp;
	}
	
	public PrestigeBonus getPrestigeBonus() {
		return prestigeBonus;
	}

	private void setSpell(final String spells, final List<Spell> listSpell) {
		if(spells == null) return;
		final Map<Integer, Spell> worldSpells = World.world.getSpells();
		for(final String spellID : spells.split(";"))
		{
			final Spell spell = worldSpells.get(Integer.parseInt(spellID.trim()));
			if(spell == null) {
				Logging.getInstance().write("Error", "keepSpell Prestige error , spell is null");
				continue;
			}
			listSpell.add(spell);
		}
	}


	public class PrestigeBonus{
		
		private final int[] baseStats = new int[6];
		private final int[] primaryStats = new int [4];
		private final int[] advancedStats = new int[7];
		private int parcho;
		private int capital;
		private int pdvMax;
		private final List<Spell> spell = new ArrayList<>();
		private final Map<ObjectTemplate, Couple<Short, Boolean>> items = new HashMap<>();

		private void giveBonusToPlayer(final Player player)
		{
			for(final Spell spell : this.spell)
				player.learnSpell(spell.getSpellID(), 1, false, false, false);
			
			this.giveStatsToConnection(player);
			player.setPdvMaxByLevel(this.pdvMax);
			player.setCapitalByLevel(this.capital);
			
			player.resetCapital();
			player.initialiseMaxPdv();
			
			player.setMaxPdv(player.getMaxPdv());
			player.setPdv(player.getMaxPdv());
			
		}
		
		public void giveStatsToConnection(final Player player)
		{
			final Stats stats = player.getStats();
			
			stats.addOneStat(Constant.STATS_ADD_PA, this.primaryStats[0]);
			stats.addOneStat(Constant.STATS_ADD_PM, this.primaryStats[1]);
			stats.addOneStat(Constant.STATS_ADD_PO, this.primaryStats[2]);
			stats.addOneStat(Constant.STATS_CREATURE, this.primaryStats[3]);
			
			stats.addOneStat(Constant.STATS_ADD_DOMA, this.advancedStats[0]);
			stats.addOneStat(Constant.STATS_ADD_PROS, this.advancedStats[1]);
			stats.addOneStat(Constant.STATS_ADD_INIT, this.advancedStats[2]);
			stats.addOneStat(Constant.STATS_ADD_PERDOM, this.advancedStats[3]);
			stats.addOneStat(Constant.STATS_TRAPDOM, this.advancedStats[4]);
			stats.addOneStat(Constant.STATS_TRAPPER, this.advancedStats[5]);
			stats.addOneStat(Constant.STATS_ADD_SOIN, this.advancedStats[6]);
		}
		
		private void removeStatsAfterNextPrestige(final Player player)
		{
			final Stats stats = player.getStats();
			
			stats.addOneStat(Constant.STATS_ADD_PA, - this.primaryStats[0]);
			stats.addOneStat(Constant.STATS_ADD_PM, - this.primaryStats[1]);
			stats.addOneStat(Constant.STATS_ADD_PO, - this.primaryStats[2]);
			stats.addOneStat(Constant.STATS_CREATURE, - this.primaryStats[3]);
			
			stats.addOneStat(Constant.STATS_ADD_DOMA, - this.advancedStats[0]);
			stats.addOneStat(Constant.STATS_ADD_PROS, - this.advancedStats[1]);
			stats.addOneStat(Constant.STATS_ADD_INIT, - this.advancedStats[2]);
			stats.addOneStat(Constant.STATS_ADD_PERDOM, - this.advancedStats[3]);
			stats.addOneStat(Constant.STATS_TRAPDOM, - this.advancedStats[4]);
			stats.addOneStat(Constant.STATS_TRAPPER, - this.advancedStats[5]);
			stats.addOneStat(Constant.STATS_ADD_SOIN, - this.advancedStats[6]);
		}
		
		public void giveBonusAfterRestat(final Player player)
		{
			final Stats stats = player.getStats();
			
			stats.addOneStat(Constant.STATS_ADD_VITA, this.baseStats[0]);
			stats.addOneStat(Constant.STATS_ADD_SAGE, this.baseStats[1]);
			stats.addOneStat(Constant.STATS_ADD_FORC, this.baseStats[2]);
			stats.addOneStat(Constant.STATS_ADD_INTE, this.baseStats[3]);
			stats.addOneStat(Constant.STATS_ADD_AGIL, this.baseStats[4]);
			stats.addOneStat(Constant.STATS_ADD_CHAN, this.baseStats[5]);
		}
		
		public void setInfos(final String baseStats, final String primaryStats, final String advancedStats, 
				final int parcho, final int capital, final int pdvMax, final String spells, final String items)
		{
			this.setStatsStringToInt(baseStats, this.baseStats);
			this.setStatsStringToInt(primaryStats, this.primaryStats);
			this.setStatsStringToInt(advancedStats, this.advancedStats);
			setSpell(spells, this.spell);
			this.setItems(items);
			this.parcho = parcho;
			this.capital = capital;
			this.pdvMax = pdvMax;
		}
		
		private void setItems(final String items) {
			if(items == null) return;
			for(final String item : items.split(";")) {
				final String[] infos = item.split(",");
				final int idTemplate = Integer.parseInt(infos[0]);
				final short quantity = Short.parseShort(infos[1]);
				final boolean jetMax = infos[2].equalsIgnoreCase("true");
				this.items.put(World.world.getObjTemplate(idTemplate), new Couple<Short, Boolean>(quantity, jetMax));
			}
			
		}
		
		private void setStatsStringToInt(final String stats, final int[] intStats) {
			try {
				final String[] base = stats.split(";");
				for(byte i = 0; i < base.length; ++i)
					intStats[i] = Integer.parseInt(base[i].trim());
			}catch(ArrayIndexOutOfBoundsException | NumberFormatException e)
			{
				e.printStackTrace();
			}
		}

		public int getParcho() {
			return parcho;
		}
		
		public int getCapital() {
			return capital;
		}

		public int getPdvMax() {
			return pdvMax;
		}
		
		public int[] getBaseStats() {
			return baseStats;
		}
		
		public int[] getPrimaryStats() {
			return primaryStats;
		}
		
		public int[] getAdvancedStats() {
			return advancedStats;
		}
		
		public Map<ObjectTemplate, Couple<Short, Boolean>> getItems() {
			return items;
		}
		
	}
	
	
}
