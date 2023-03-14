package org.starloco.locos.game.world;

import ch.qos.logback.classic.Level;
import org.apache.commons.lang.ArrayUtils;
import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.labyrinth.Gladiatrool;
import org.starloco.locos.area.map.labyrinth.PigDragon;
import org.starloco.locos.area.map.labyrinth.Minotoror;
import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.Prestige;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.command.PlayerCommand;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.Database;
import org.starloco.locos.entity.Collector;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.entity.npc.NpcAnswer;
import org.starloco.locos.entity.npc.NpcQuestion;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.entity.pet.Pet;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.fight.spells.GladiatroolSpells;
import org.starloco.locos.fight.spells.Spell;
import java.util.concurrent.ConcurrentHashMap;
import org.starloco.locos.hdv.Hdv;
import org.starloco.locos.hdv.HdvEntry;
import org.starloco.locos.job.Job;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.area.map.entity.InteractiveObject.InteractiveObjectTemplate;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectSet;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.object.entity.Fragment;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.area.map.GameMap;
import ch.qos.logback.classic.Logger;

import org.starloco.locos.common.CryptManager;

import org.slf4j.LoggerFactory;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.area.map.entity.*;
import org.starloco.locos.other.Guild;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class World {
    
    public final static World world = new World();

    public Logger logger = (Logger) LoggerFactory.getLogger(World.class);

    private Map<Integer, Account>    accounts    = new HashMap<>();
    private Map<Integer, Player>     players     = new HashMap<>();
    private Map<Short, GameMap>    maps        = new HashMap<>();
    private Map<Integer, GameObject> objects     = new ConcurrentHashMap<>();

    private Map<Integer, ExpLevel> experiences = new HashMap<>();
    private Map<Integer, Spell> spells = new HashMap<>();
    private Map<Integer, ObjectTemplate> ObjTemplates = new HashMap<>();
    private Map<Integer, Monster> MobTemplates = new HashMap<>();
    private Map<Integer, NpcTemplate> npcsTemplate = new HashMap<>();
    private Map<Integer, NpcQuestion> questions = new HashMap<>();
    private Map<Integer, NpcAnswer> answers = new HashMap<>();
    private Map<Integer, InteractiveObjectTemplate> IOTemplate = new HashMap<>();
    private Map<Integer, Mount> Dragodindes = new HashMap<>();
    private Map<Integer, Area> areas = new HashMap<>();
    private Map<Integer, SubArea> subAreas = new HashMap<>();
    private Map<Integer, Job> Jobs = new HashMap<>();
    private Map<Integer, ArrayList<Couple<Integer, Integer>>> Crafts = new HashMap<>();
    private Map<Integer, ObjectSet> ItemSets = new HashMap<>();
    private Map<Integer, Guild> Guildes = new HashMap<>();
    private Map<Integer, Hdv> Hdvs = new HashMap<>();
    private Map<Integer, Map<Integer, ArrayList<HdvEntry>>> hdvsItems = new HashMap<>();
    private Map<Integer, Animation> Animations = new HashMap<>();
    private Map<Short, org.starloco.locos.area.map.entity.MountPark> MountPark = new HashMap<>();
    private Map<Integer, Trunk> Trunks = new HashMap<>();
    private Map<Integer, Collector> collectors = new HashMap<>();
    private Map<Integer, House> Houses = new HashMap<>();
    private Map<Short, Collection<Integer>> Seller = new HashMap<>();
    private StringBuilder Challenges = new StringBuilder();
    private Map<Integer, Prism> Prismes = new HashMap<>();
    private Map<Integer, Map<String, String>> fullmorphs = new HashMap<>();
    private Map<Integer, Pet> Pets = new HashMap<>();
    private Map<Integer, PetEntry> PetsEntry = new HashMap<>();
    private Map<String, Map<String, String>> mobsGroupsFix = new HashMap<>();
    private Map<Integer, Map<String, Map<String, Integer>>> extraMonstre = new HashMap<>();
    private Map<Integer, GameMap> extraMonstreOnMap = new HashMap<>();
    private Map<Integer, org.starloco.locos.area.map.entity.Tutorial> Tutorial = new HashMap<>();
    private CryptManager cryptManager=new CryptManager();
    private Map<Player, List<GladiatroolSpells>> gladiatroolSpells = new HashMap<>();
    private int nextObjectHdvId, nextLineHdvId;
    
    private Map<Short, Prestige> prestiges = new HashMap<>();
    private List<PlayerCommand> playerCommand = new ArrayList<>();

    
    public CryptManager getCryptManager()
    {
      return cryptManager;
    }
    
    // By Coding Mestre - [FIX] - New sets are now properly recognized by the server Close #33
    public Map<Integer, ObjectSet> getItemSets() {
        return ItemSets;
    }

    
    //region Accounts data
    public void addAccount(Account account) {
        accounts.put(account.getId(), account);
    }
    public void removeAccount(Account account)
    {
        accounts.remove(account.getId());
    }

    public Account getAccount(int id) {
        return accounts.get(id);
    }

    public Collection<Account> getAccounts() {
        return accounts.values();
    }

    public Map<Integer, Account> getAccountsByIp(String ip) {
        Map<Integer, Account> newAccounts = new HashMap<>();
        accounts.values().stream().filter(account -> account.getLastIP().equalsIgnoreCase(ip)).forEach(account -> newAccounts.put(newAccounts.size(), account));
        return newAccounts;
    }

    public Account getAccountByPseudo(String pseudo) {
        for (Account account : accounts.values())
            if (account.getPseudo().equals(pseudo))
                return account;
        return null;
    }
    //endregion

    //region Players data
    public Collection<Player> getPlayers() {
        return players.values();
    }

    public void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public Player getPlayerByName(String name) {
        for (Player player : players.values())
            if (player.getName().equalsIgnoreCase(name))
                return player;
        return null;
    }

    public Player getPlayer(int id) {
        return players.get(id);
    }

    public List<Player> getOnlinePlayers() {
        return players.values().stream().filter(player -> player.isOnline() && player.getGameClient() != null).collect(Collectors.toList());
    }
    //endregion

    //region Maps data
    public Collection<GameMap> getMaps() {
        return maps.values();
    }

    public GameMap getMap(short id) {
        return maps.get(id);
    }

    public void addMap(GameMap map) {
        if(map.getSubArea() != null && map.getSubArea().getArea().getId() == 42 && !Config.getInstance().NOEL)
            return;
        maps.put(map.getId(), map);
    }
    //endregion

    //region Objects data
    public CopyOnWriteArrayList<GameObject> getGameObjects() {
        return new CopyOnWriteArrayList<>(objects.values());
    }

    public void addGameObject(GameObject gameObject, boolean saveSQL) {
        if (gameObject != null) {
            objects.put(gameObject.getGuid(), gameObject);
            if (saveSQL)
                gameObject.modification = 0;
        }
    }

    public GameObject getGameObject(int guid) {
        return objects.get(guid);
    }

    public void removeGameObject(int id) {
        if(objects.containsKey(id))
            objects.remove(id);
        
        Database.getDynamics().getObjectData().delete(id);
    }
    
    public void addPlayerCommand(final PlayerCommand pc)
    {
    	this.playerCommand.add(pc);
    }
    
    public List<PlayerCommand> getPlayerCommand() {
		return playerCommand;
	}
    
    public PlayerCommand getPlayerCommandByName(final String searchName)
    {
    	for(final PlayerCommand pc : this.getPlayerCommand())
    		for(final String name : pc.getName()) 
    			if(name.equalsIgnoreCase(searchName))
    				return pc;
    	return null;
    }
    
    //endregion

    public Map<Integer, Spell> getSpells() {
        return spells;
    }

    public Map<Integer, ObjectTemplate> getObjectsTemplates() {
        return ObjTemplates;
    }

    public Map<Integer, NpcAnswer> getAnswers() {
        return answers;
    }

    public Map<Integer, Mount> getMounts() {
        return Dragodindes;
    }

    public Map<Integer, Area> getAreas() {
        return areas;
    }

    public Map<Integer, SubArea> getSubAreas() {
        return subAreas;
    }

    public Map<Integer, Guild> getGuilds() {
        return Guildes;
    }

    public Map<Short, MountPark> getMountparks() {
        return MountPark;
    }

    public Map<Integer, Trunk> getTrunks() {
        return Trunks;
    }

    public Map<Integer, Collector> getCollectors() {
        return collectors;
    }

    public Map<Integer, House> getHouses() {
        return Houses;
    }

    public Map<Integer, Prism> getPrisms() {
        return Prismes;
    }

    public Map<Integer, Map<String, Map<String, Integer>>> getExtraMonsters() {
        return extraMonstre;
    }
    
    public void addPrestige(Prestige prestige)
    {
    	this.prestiges.put(prestige.getId(), prestige);
    }
    
    public Prestige getPrestigeById(final short id)
    {
    	return this.prestiges.get(id);
    }
    
    public Map<Short, Prestige> getPrestiges()
    {
    	return this.prestiges;
    }
    /**
     * end region *
     */

    public void createWorld() {
        logger.info("Loading of data..");
        long time = System.currentTimeMillis();
        
        Database.getDynamics().getPlayerCommandData().load(null);
        logger.debug("The player command data of the logged players were done successfully.");

        Database.getStatics().getServerData().loggedZero();
        logger.debug("The reset of the logged players were done successfully.");

        Database.getStatics().getCommandData().load(null);
        logger.debug("The administration commands were loaded successfully.");

        Database.getStatics().getGroupData().load(null);
        logger.debug("The administration groups were loaded successfully.");

        Database.getStatics().getPubData().load(null);
        logger.debug("The pubs were loaded successfully.");

        Database.getDynamics().getFullMorphData().load();
        logger.debug("The incarnations were loaded successfully.");

        Database.getDynamics().getExtraMonsterData().load();
        logger.debug("The extra-monsters were loaded successfully.");

        Database.getDynamics().getExperienceData().load();
        logger.debug("The experiences were loaded successfully.");

        Database.getDynamics().getSpellData().load();
        logger.debug("The spells were loaded successfully.");

        Database.getDynamics().getMonsterData().load();
        logger.debug("The monsters were loaded successfully.");

        Database.getDynamics().getObjectTemplateData().load();
        logger.debug("The template objects were loaded successfully.");

        Database.getDynamics().getObjectData().load();
        logger.debug("The objects were loaded successfully.");

        Database.getDynamics().getNpcTemplateData().load();
        logger.debug("The non-player characters were loaded successfully.");

        Database.getDynamics().getNpcQuestionData().load();
        logger.debug("The n-p-c questions were loaded successfully.");

        Database.getDynamics().getNpcAnswerData().load();
        logger.debug("The n-p-c answers were loaded successfully.");

        Database.getDynamics().getQuestObjectiveData().load();
        logger.debug("The quest goals were loaded successfully.");

        Database.getDynamics().getQuestStepData().load();
        logger.debug("The quest steps were loaded successfully.");

        Database.getDynamics().getQuestData().load();
        logger.debug("The quests data were loaded successfully.");

        Database.getDynamics().getNpcTemplateData().loadQuest();
        logger.debug("The adding of quests on non-player characters was done successfully.");

        Database.getDynamics().getPrismData().load();
        logger.debug("The prisms were loaded successfully.");

        Database.getStatics().getAreaData().load();
        logger.debug("The statics areas data were loaded successfully.");
        Database.getDynamics().getAreaData().load();
        logger.debug("The dynamics areas data were loaded successfully.");

        Database.getStatics().getSubAreaData().load();
        logger.debug("The statics sub-areas data were loaded successfully.");
        Database.getDynamics().getSubAreaData().load();
        logger.debug("The dynamics sub-areas data were loaded successfully.");

        Database.getDynamics().getInteractiveDoorData().load();
        logger.debug("The templates of interactive doors were loaded successfully.");

        Database.getDynamics().getInteractiveObjectData().load();
        logger.debug("The templates of interactive objects were loaded successfully.");

        Database.getDynamics().getCraftData().load();
        logger.debug("The crafts were loaded successfully.");

        Database.getDynamics().getJobData().load();
        logger.debug("The jobs were loaded successfully.");

        Database.getDynamics().getObjectSetData().load();
        logger.debug("The panoplies were loaded successfully.");

        Database.getDynamics().getMapData().load();
        logger.debug("The maps were loaded successfully.");

        Database.getDynamics().getScriptedCellData().load();
        logger.debug("The scripted cells were loaded successfully.");

        Database.getDynamics().getEndFightActionData().load();
        logger.debug("The end fight actions were loaded successfully.");

        Database.getDynamics().getNpcData().load();
        logger.debug("The placement of non-player character were done successfully.");

        Database.getDynamics().getObjectActionData().load();
        logger.debug("The action of objects were loaded successfully.");

        Database.getDynamics().getDropData().load();
        logger.debug("The drops were loaded successfully.");

        logger.debug("The mounts were loaded successfully.");

        Database.getDynamics().getAnimationData().load();
        logger.debug("The animations were loaded successfully.");

        Database.getStatics().getAccountData().load();
        logger.debug("The accounts were loaded successfully.");

        Database.getStatics().getPrestigeData().load();
        logger.debug("The prestiges were loaded successfully.");
        
        Database.getStatics().getPrestigeBonusData().load();
        logger.debug("The prestiges bonus were loaded successfully.");
        
        Database.getStatics().getPlayerData().load();
        logger.debug("The players were loaded successfully.");

        Database.getDynamics().getGuildMemberData().load();
        logger.debug("The guilds and guild members were loaded successfully.");

        Database.getDynamics().getPetData().load();
        logger.debug("The pets were loaded successfully.");

        Database.getDynamics().getPetTemplateData().load();
        logger.debug("The templates of pets were loaded successfully.");

        Database.getDynamics().getTutorialData().load();
        logger.debug("The tutorials were loaded successfully.");

        Database.getStatics().getMountParkData().load();
        logger.debug("The statics parks of the mounts were loaded successfully.");
        Database.getDynamics().getMountParkData().load();
        logger.debug("The dynamics parks of the mounts were loaded successfully.");

        Database.getDynamics().getCollectorData().load();
        logger.debug("The collectors were loaded successfully.");

        Database.getStatics().getHouseData().load();
        logger.debug("The statics houses were loaded successfully.");
        Database.getDynamics().getHouseData().load();
        logger.debug("The dynamics houses were loaded successfully.");

        Database.getStatics().getTrunkData().load();
        logger.debug("The statics trunks were loaded successfully.");
        Database.getDynamics().getTrunkData().load();
        logger.debug("The dynamics trunks were loaded successfully.");

        Database.getDynamics().getZaapData().load();
        logger.debug("The zaaps were loaded successfully.");

        Database.getDynamics().getZaapiData().load();
        logger.debug("The zappys were loaded successfully.");

        Database.getDynamics().getChallengeData().load();
        logger.debug("The challenges were loaded successfully.");

        Database.getDynamics().getHdvData().load();
        logger.debug("The hotels of sales were loaded successfully.");

        Database.getDynamics().getHdvObjectData().load();
        logger.debug("The objects of hotels were loaded successfully.");

        Database.getDynamics().getDungeonData().load();
        logger.debug("The dungeons were loaded successfully.");

        Database.getDynamics().getRuneData().load(null);
        logger.debug("The runes were loaded successfully.");

        loadExtraMonster();
        logger.debug("The adding of extra-monsters on the maps were done successfully.");

        loadMonsterOnMap();
        logger.debug("The adding of mobs groups on the maps were done successfully.");

        Database.getDynamics().getGangsterData().load();
        logger.debug("The adding of gangsters on the maps were done successfully.");

        logger.debug("Initialization of the dungeon : Dragon Pig.");
        PigDragon.initialize();
        logger.debug("Initialization of the dungeon : Labyrinth of the Minotoror.");
        Minotoror.initialize();
        logger.debug("Initialization of the dungeons : Gladiatrool.");
        Gladiatrool.initialize();

        Database.getDynamics().getGladiatroolSpellsData().load();
        logger.debug("The gladiatrool spells of players were loaded successfully.");

        Database.getStatics().getServerData().updateTime(time);
        logger.info("All data was loaded successfully at "
        + new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.FRANCE).format(new Date()) + " in "
                + new SimpleDateFormat("mm", Locale.FRANCE).format((System.currentTimeMillis() - time)) + " min "
                + new SimpleDateFormat("ss", Locale.FRANCE).format((System.currentTimeMillis() - time)) + " s"
        		+ new SimpleDateFormat("SS", Locale.FRANCE).format((System.currentTimeMillis() - time)) + " m.");
        logger.setLevel(Level.ALL);
        
        // Permet de g�n�rer le fichier itemstats pour les langs
        /*try(PrintWriter p = new PrintWriter(new FileWriter("itemstats.txt"))) {
        	
        	p.print("FILE_BEGIN = true;\r\n"
        			+ "System.security.allowDomain(_parent._url);\r\n"
        			+ "VERSION = 1059;\r\n"
        			+ "ISTA = new Array();\r\n");
        	
        	
        	
            for(ObjectTemplate temp : this.getObjectsTemplates().values()) 
                p.print("ISTA["+temp.getId()+"] = \""+ temp.getStrTemplate() + "\";\n");
            
            p.print("FILE_END = true;");
            
            p.flush();
          } catch (IOException e) {
              System.out.println(e.getMessage());
          }*/
    }

    public void addExtraMonster(int idMob, String superArea,
                                       String subArea, int chances) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Map<String, Integer> _map = new HashMap<>();
        _map.put(subArea, chances);
        map.put(superArea, _map);
        extraMonstre.put(idMob, map);
    }

    public Map<Integer, GameMap> getExtraMonsterOnMap() {
        return extraMonstreOnMap;
    }

    public void loadExtraMonster() {
        ArrayList<GameMap> mapPossible = new ArrayList<>();
        for (Entry<Integer, Map<String, Map<String, Integer>>> i : extraMonstre.entrySet()) {
            try {
                Map<String, Map<String, Integer>> map = i.getValue();

                for (Entry<String, Map<String, Integer>> areaChances : map.entrySet()) {
                    Integer chances = null;
                    for (Entry<String, Integer> _e : areaChances.getValue().entrySet()) {
                        Integer _c = _e.getValue();
                        if (_c != null && _c != -1)
                            chances = _c;
                    }
                    if (!areaChances.getKey().equals("")) {// Si la superArea n'est pas null
                        for (String ar : areaChances.getKey().split(",")) {
                            Area Area = areas.get(Integer.parseInt(ar));
                            for (GameMap Map : Area.getMaps()) {
                                if (Map == null)
                                    continue;
                                if (Map.haveMobFix())
                                    continue;
                                if (!Map.isPossibleToPutMonster())
                                    continue;

                                if (chances != null)
                                    Map.addMobExtra(i.getKey(), chances);
                                else if (!mapPossible.contains(Map))
                                    mapPossible.add(Map);
                            }
                        }
                    }
                    if (areaChances.getValue() != null) // Si l'area n'est pas null
                    {
                        for (Entry<String, Integer> area : areaChances.getValue().entrySet()) {
                            String areas = area.getKey();
                            for (String sub : areas.split(",")) {
                                SubArea subArea = null;
                                try {
                                    subArea = subAreas.get(Integer.parseInt(sub));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (subArea == null)
                                    continue;
                                for (GameMap Map : subArea.getMaps()) {
                                    if (Map == null)
                                        continue;
                                    if (Map.haveMobFix())
                                        continue;
                                    if (!Map.isPossibleToPutMonster())
                                        continue;

                                    if (chances != null)
                                        Map.addMobExtra(i.getKey(), chances);
                                    if (!mapPossible.contains(Map))
                                        mapPossible.add(Map);
                                }
                            }
                        }
                    }
                }
                if (mapPossible.size() <= 0) {
                    throw new Exception(" no maps was found for the extra monster " + i.getKey() +".");
                } else {
                    GameMap randomMap;
                    if (mapPossible.size() == 1)
                        randomMap = mapPossible.get(0);
                    else
                        randomMap = mapPossible.get(Formulas.getRandomValue(0, mapPossible.size() - 1));
                    if (randomMap == null)
                        throw new Exception("the random map is null.");
                    if (getMonstre(i.getKey()) == null)
                        throw new Exception("the monster template of the extra monster is invalid (id : " + i.getKey() + ").");
                    if (randomMap.loadExtraMonsterOnMap(i.getKey()))
                        extraMonstreOnMap.put(i.getKey(), randomMap);
                    else
                        throw new Exception("a empty mobs group or invalid monster.");
                }

                mapPossible.clear();
            } catch(Exception e) {
                e.printStackTrace();
                mapPossible.clear();
                logger.error("An error occurred when the server try to put extra-monster caused by : " + e.getMessage());
            }
        }
    }

    public Map<String, String> getGroupFix(int map, int cell) {
        return mobsGroupsFix.get(map + ";" + cell);
    }

    public void addGroupFix(String str, String mob, int Time) {
        mobsGroupsFix.put(str, new HashMap<>());
        mobsGroupsFix.get(str).put("groupData", mob);
        mobsGroupsFix.get(str).put("timer", Time + "");
    }

    public void loadMonsterOnMap() {
        maps.values().stream().filter(map -> map != null).forEach(map -> {
            try {
                map.loadMonsterOnMap();
            } catch (Exception e) {
                logger.error("An error occurred when the server try to put monster on the map id " + map.getId() + ".");
            }
        });
    }

    public Area getArea(int areaID) {
        return areas.get(areaID);
    }


    public SubArea getSubArea(int areaID) {
        return subAreas.get(areaID);
    }

    public void addArea(Area area) {
        areas.put(area.getId(), area);
    }



    public void addSubArea(SubArea SA) {
        subAreas.put(SA.getId(), SA);
    }

    public String getSousZoneStateString() {
        String str = "";
        boolean first = false;
        for (SubArea subarea : subAreas.values()) {
            if (!subarea.getConquistable())
                continue;
            if (first)
                str += "|";
            str += subarea.getId() + ";" + subarea.getAlignement();
            first = true;
        }
        return str;
    }

    public void addNpcAnswer(NpcAnswer rep) {
        answers.put(rep.getId(), rep);
    }

    public NpcAnswer getNpcAnswer(int guid) {
        return answers.get(guid);
    }

    public double getBalanceArea(Area area, int alignement) {
        int cant = 0;
        for (SubArea subarea : subAreas.values()) {
            if (subarea.getArea() == area
                    && subarea.getAlignement() == alignement)
                cant++;
        }
        if (cant == 0)
            return 0;
        return Math.rint((1000 * cant / (area.getSubAreas().size())) / 10);
    }

    public double getBalanceWorld(int alignement) {
        int cant = 0;
        for (SubArea subarea : subAreas.values()) {
            if (subarea.getAlignement() == alignement)
                cant++;
        }
        if (cant == 0)
            return 0;
        return Math.rint((10 * cant / 4) / 10);
    }

    public double getConquestBonus(Player player) {
        if(player == null) return 1;
        if(player.get_align() == 0) return 1;
        final double factor = 1 + (getBalanceWorld(player.get_align()) * Math.rint((player.getGrade() / 2.5) + 1)) / 100;
        if(factor < 1) return 1;
        return factor;
    }

    public int getExpLevelSize() {
        return experiences.size();
    }

    public void addExpLevel(int lvl, ExpLevel exp) {
        experiences.put(lvl, exp);
    }



    public void addNPCQuestion(NpcQuestion quest) {
        questions.put(quest.getId(), quest);
    }

    public NpcQuestion getNPCQuestion(int guid) {
        return questions.get(guid);
    }

    public NpcTemplate getNPCTemplate(int guid) {
        return npcsTemplate.get(guid);
    }

    public void addNpcTemplate(NpcTemplate temp) {
        npcsTemplate.put(temp.getId(), temp);
    }



    public void removePlayer(Player player) {
        if (player.get_guild() != null) {
            if (player.get_guild().getMembers().size() <= 1) {
                removeGuild(player.get_guild().getId());
            } else if (player.getGuildMember().getRank() == 1) {
                int curMaxRight = 0;
                Player leader = null;

                for (Player newLeader : player.get_guild().getMembers())
                    if (newLeader != player && newLeader.getGuildMember().getRights() < curMaxRight)
                        leader = newLeader;

                player.get_guild().removeMember(player);
                if(leader != null)
                    leader.getGuildMember().setRank(1);
            } else {
                player.get_guild().removeMember(player);
            }
        }
        if(player.getWife() != 0) {
            Player wife = getPlayer(player.getWife());

            if(wife != null) {
                wife.setWife(0);
            }
        }
        player.remove();
        unloadPerso(player.getId());
        players.remove(player.getId());
    }

    public void unloadPerso(Player perso) {
        unloadPerso(perso.getId());//UnLoad du perso+item
        players.remove(perso.getId());
    }

    public long getPersoXpMin(int _lvl) {
        if (_lvl > getExpLevelSize())
            _lvl = getExpLevelSize();
        if (_lvl < 1)
            _lvl = 1;
        return experiences.get(_lvl).perso;
    }

    public long getPersoXpMax(int _lvl) {
        if (_lvl >= getExpLevelSize())
            _lvl = (getExpLevelSize() - 1);
        if (_lvl <= 1)
            _lvl = 1;
        return experiences.get(_lvl + 1).perso;
    }

    public long getTourmenteursXpMax(int _lvl) {
        if (_lvl >= getExpLevelSize())
            _lvl = (getExpLevelSize() - 1);
        if (_lvl <= 1)
            _lvl = 1;
        return experiences.get(_lvl + 1).tourmenteurs;
    }

    public long getBanditsXpMin(int _lvl) {
        if (_lvl > getExpLevelSize())
            _lvl = getExpLevelSize();
        if (_lvl < 1)
            _lvl = 1;
        return experiences.get(_lvl).bandits;
    }

    public long getBanditsXpMax(int _lvl) {
        if (_lvl >= getExpLevelSize())
            _lvl = (getExpLevelSize() - 1);
        if (_lvl <= 1)
            _lvl = 1;
        return experiences.get(_lvl + 1).bandits;
    }

    public void addSort(Spell sort) {
        spells.put(sort.getSpellID(), sort);
    }

    public Spell getSort(int id) {
        return spells.get(id);
    }

    public void addObjTemplate(ObjectTemplate obj) {
        ObjTemplates.put(obj.getId(), obj);
    }

    public ObjectTemplate getObjTemplate(int id) {
        return ObjTemplates.get(id);
    }

    public ArrayList<ObjectTemplate> getEtherealWeapons(int level) {
        ArrayList<ObjectTemplate> array = new ArrayList<>();
        final int levelMin = (level - 5 < 0 ? 0 : level - 5), levelMax = level + 5;
        getObjectsTemplates().values().stream().filter(objectTemplate -> objectTemplate != null && objectTemplate.getStrTemplate().contains("32c#")
                && (levelMin < objectTemplate.getLevel() && objectTemplate.getLevel() < levelMax) && objectTemplate.getType() != 93).forEach(array::add);
        return array;
    }

    public void addMobTemplate(int id, Monster mob) {
        MobTemplates.put(id, mob);
    }

    public Monster getMonstre(int id) {
        return MobTemplates.get(id);
    }

    public Collection<Monster> getMonstres() {
        return MobTemplates.values();
    }


    public String getStatOfAlign() {
        int ange = 0;
        int demon = 0;
        int total = 0;
        for (Player i : getPlayers()) {
            if (i == null)
                continue;
            if (i.get_align() == 1)
                ange++;
            if (i.get_align() == 2)
                demon++;
            total++;
        }
        ange = ange / total;
        demon = demon / total;
        if (ange > demon)
            return "Les Brâkmarien sont actuellement en minorité, je peux donc te proposer de rejoindre les rangs Brâkmarien ?";
        else if (demon > ange)
            return "Les Bontarien sont actuellement en minorité, je peux donc te proposer de rejoindre les rangs Bontarien ?";
        else if (demon == ange)
            return " Aucune milice est actuellement en minorité, je peux donc te proposer de rejoindre aléatoirement une milice ?";
        return "Undefined";
    }





    public void addIOTemplate(InteractiveObjectTemplate IOT) {
        IOTemplate.put(IOT.getId(), IOT);
    }

    public Mount getMountById(int id) {

        Mount mount = Dragodindes.get(id);
        if(mount == null) {
            Database.getDynamics().getMountData().load(id);
            mount = Dragodindes.get(id);
        }
        return mount;
    }

    public void addMount(Mount mount) {
        Dragodindes.put(mount.getId(), mount);
    }

    public void removeMount(int id) {
        Dragodindes.remove(id);
    }

    public void addTutorial(Tutorial tutorial) {
        Tutorial.put(tutorial.getId(), tutorial);
    }

    public Tutorial getTutorial(int id) {
        return Tutorial.get(id);
    }

    public ExpLevel getExpLevel(int lvl) {
        return experiences.get(lvl);
    }

    public InteractiveObjectTemplate getIOTemplate(int id) {
        return IOTemplate.get(id);
    }

    public Job getMetier(int id) {
        return Jobs.get(id);
    }

    public void addJob(Job metier) {
        Jobs.put(metier.getId(), metier);
    }

    public void addCraft(int id, ArrayList<Couple<Integer, Integer>> m) {
        Crafts.put(id, m);
    }

    public ArrayList<Couple<Integer, Integer>> getCraft(int i) {
        return Crafts.get(i);
    }

    public void addGladiatroolSpells(Player owner, GladiatroolSpells gladiatroolSpell)
    {
        if(!gladiatroolSpells.containsKey(owner)){

            List<GladiatroolSpells> list = new ArrayList<>();
            list.add(gladiatroolSpell);
            gladiatroolSpells.put(owner, list);
        }
        else{

            List<GladiatroolSpells> list = gladiatroolSpells.get(owner);

            if(list.contains(gladiatroolSpell)){
                list.remove(gladiatroolSpell);
                list.add(gladiatroolSpell);
            }
            else {
                list.add(gladiatroolSpell);
            }

            gladiatroolSpells.remove(owner);
            gladiatroolSpells.put(owner, list);
        }
    }

    public Map<Player, List<GladiatroolSpells>> getAllGladiatroolSpells()
    {
        return gladiatroolSpells;
    }

    public String getGladiatroolSpells(Player owner, int fullMorpId)
    {
        if(gladiatroolSpells.containsKey(owner))
        {
            List<GladiatroolSpells> list = gladiatroolSpells.get(owner);

            for(GladiatroolSpells gladia : list)
            {
                if(gladia.getFullMorphId() == fullMorpId)
                {
                    return gladia.getSpells();
                }
            }
        }
        return "";
    }
    public GladiatroolSpells getGladiatroolSpellsFromPlayer(Player owner, int fullMorpId)
    {
        if(gladiatroolSpells.containsKey(owner))
        {
            List<GladiatroolSpells> list = gladiatroolSpells.get(owner);

            for(GladiatroolSpells gladia : list)
            {
                if(gladia.getFullMorphId() == fullMorpId)
                {
                    return gladia;
                }
            }
        }
        return null;
    }


    public void addFullMorph(int morphID, String name, int gfxID,
                             String spells, String[] args) {
        if (fullmorphs.get(morphID) != null)
            return;

        fullmorphs.put(morphID, new HashMap<>());
        fullmorphs.get(morphID).put("name", name);
        fullmorphs.get(morphID).put("gfxid", gfxID + "");
        fullmorphs.get(morphID).put("spells", spells);
        if (args != null) {
            fullmorphs.get(morphID).put("vie", args[0]);
            fullmorphs.get(morphID).put("pa", args[1]);
            fullmorphs.get(morphID).put("pm", args[2]);
            fullmorphs.get(morphID).put("vitalite", args[3]);
            fullmorphs.get(morphID).put("sagesse", args[4]);
            fullmorphs.get(morphID).put("terre", args[5]);
            fullmorphs.get(morphID).put("feu", args[6]);
            fullmorphs.get(morphID).put("eau", args[7]);
            fullmorphs.get(morphID).put("air", args[8]);
            fullmorphs.get(morphID).put("initiative", args[9]);
            fullmorphs.get(morphID).put("stats", args[10]);
            fullmorphs.get(morphID).put("donjon", args[11]);
            if(morphID > 100){
                fullmorphs.get(morphID).put("do", args[12]);
                fullmorphs.get(morphID).put("doper", args[13]);
                fullmorphs.get(morphID).put("invo", args[14]);
                fullmorphs.get(morphID).put("esPA", args[15]);
                fullmorphs.get(morphID).put("esPM", args[16]);
                fullmorphs.get(morphID).put("resiNeu", args[17]);
                fullmorphs.get(morphID).put("resiTer", args[18]);
                fullmorphs.get(morphID).put("resiFeu", args[19]);
                fullmorphs.get(morphID).put("resiEau", args[20]);
                fullmorphs.get(morphID).put("resiAir", args[21]);
                fullmorphs.get(morphID).put("PO", args[22]);
                fullmorphs.get(morphID).put("soin", args[23]);
                fullmorphs.get(morphID).put("crit", args[24]);
                fullmorphs.get(morphID).put("rfixNeu", "0");
                fullmorphs.get(morphID).put("rfixTer", "0");
                fullmorphs.get(morphID).put("rfixFeu", "0");
                fullmorphs.get(morphID).put("rfixEau", "0");
                fullmorphs.get(morphID).put("rfixAir", "0");
                fullmorphs.get(morphID).put("renvoie", "0");
                fullmorphs.get(morphID).put("dotrap", "0");
                fullmorphs.get(morphID).put("perdotrap", "0");
                fullmorphs.get(morphID).put("dophysique", "0");

            }
        }
    }

    public Map<String, String> getFullMorph(int morphID) {
        return fullmorphs.get(morphID);
    }

    public int getObjectByIngredientForJob(ArrayList<Integer> list,
                                                  Map<Integer, Integer> ingredients) {
        if (list == null)
            return -1;
        for (int tID : list) {
            ArrayList<Couple<Integer, Integer>> craft = getCraft(tID);
            if (craft == null)
                continue;
            if (craft.size() != ingredients.size())
                continue;
            boolean ok = true;
            for (Couple<Integer, Integer> c : craft) {
                if (!((ingredients.get(c.first) + " ").equals(c.second + " "))) //si ingredient non pr�sent ou mauvaise quantit�
                    ok = false;
            }
            if (ok)
                return tID;
        }
        return -1;
    }



    public void addItemSet(ObjectSet itemSet) {
        ItemSets.put(itemSet.getId(), itemSet);
    }

    public ObjectSet getItemSet(int tID) {
        return ItemSets.get(tID);
    }

    public int getItemSetNumber() {
        return ItemSets.size();
    }

    public ArrayList<GameMap> getMapByPosInArray(int mapX, int mapY) {
        ArrayList<GameMap> i = new ArrayList<>();
        for (GameMap map : maps.values())
            if (map.getX() == mapX && map.getY() == mapY)
                i.add(map);
        return i;
    }

    public ArrayList<GameMap> getMapByPosInArrayPlayer(int mapX, int mapY, Player player) {
        return maps.values().stream().filter(map -> map != null && map.getSubArea() != null && player.getCurMap().getSubArea() != null).filter(map -> map.getX() == mapX && map.getY() == mapY && map.getSubArea().getArea().getSuperArea() == player.getCurMap().getSubArea().getArea().getSuperArea()).collect(Collectors.toCollection(ArrayList::new));
    }

    public void addGuild(Guild g, boolean save) {
        Guildes.put(g.getId(), g);
        if (save)
        	Database.getDynamics().getGuildData().add(g);
    }

    public boolean guildNameIsUsed(String name) {
        for (Guild g : Guildes.values())
            if (g.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    public boolean guildEmblemIsUsed(String emb) {
        for (Guild g : Guildes.values()) {
            if (g.getEmblem().equals(emb))
                return true;
        }
        return false;
    }

    public Guild getGuild(int i) {
        Guild guild = Guildes.get(i);
        if(guild == null) {
        	Database.getDynamics().getGuildData().load(i);
            guild = Guildes.get(i);
        }
        return guild;
    }

    public int getGuildByName(String name) {
        for (Guild g : Guildes.values()) {
            if (g.getName().equalsIgnoreCase(name))
                return g.getId();
        }
        return -1;
    }

    public long getGuildXpMax(int _lvl) {
        if (_lvl >= 200)
            _lvl = 199;
        if (_lvl <= 1)
            _lvl = 1;
        return experiences.get(_lvl + 1).guilde;
    }

    public int getZaapCellIdByMapId(short i) {
        for (Entry<Integer, Integer> zaap : Constant.ZAAPS.entrySet()) {
            if (zaap.getKey() == i)
                return zaap.getValue();
        }
        return -1;
    }

    public int getEncloCellIdByMapId(short i) {
        GameMap map = getMap(i);
        if(map != null && map.getMountPark() != null && map.getMountPark().getCell() > 0)
            return map.getMountPark().getCell();
        return -1;
    }

    public void delDragoByID(int getId) {
        Dragodindes.remove(getId);
    }

    public void removeGuild(int id) {
        House.removeHouseGuild(id);
        GameMap.removeMountPark(id);
        Collector.removeCollector(id);
        Guildes.remove(id);
        Database.getDynamics().getGuildMemberData().deleteAll(id);
        Database.getDynamics().getGuildData().delete(id);
    }

    public void unloadPerso(int g) {
        Player toRem = players.get(g);
        if (!toRem.getItems().isEmpty())
            for (Entry<Integer, GameObject> curObj : toRem.getItems().entrySet())
                objects.remove(curObj.getKey());

    }

    public GameObject newObjet(int Guid, int template, int qua, int pos,
                                  String strStats, int puit) {
        if (getObjTemplate(template) == null) {
            return null;
        }

        if (template == 8378) {
            return new Fragment(Guid, strStats);
        } else if (getObjTemplate(template).getType() == 85) {
            return new SoulStone(Guid, qua, template, pos, strStats);
        } else if (getObjTemplate(template).getType() == 24
                && (Constant.isCertificatDopeuls(getObjTemplate(template).getId()) || getObjTemplate(template).getId() == 6653) || getObjTemplate(template).getId() == 12803 ) {
            try {
                Map<Integer, String> txtStat = new HashMap<>();
                txtStat.put(Constant.STATS_DATE, strStats.substring(3) + "");
                return new GameObject(Guid, template, qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(), new ArrayList<>(), new HashMap<>(), txtStat, puit);
            } catch (Exception e) {
                e.printStackTrace();
                return new GameObject(Guid, template, qua, pos, strStats, 0);
            }
        } else {
            return new GameObject(Guid, template, qua, pos, strStats, 0);
        }
    }

    /*public Map<Integer, Integer> getChangeHdv() {
        Map<Integer, Integer> changeHdv = new HashMap<>();
        changeHdv.put(8753, 8759); // HDV Annimaux
        changeHdv.put(4607, 4271); // HDV Alchimistes
        changeHdv.put(4622, 4216); // HDV Bijoutiers
        changeHdv.put(4627, 4232); // HDV Bricoleurs
        changeHdv.put(5112, 4178); // HDV B�cherons
        changeHdv.put(4562, 4183); // HDV Cordonniers
        changeHdv.put(8754, 8760); // HDV Biblioth�que
        changeHdv.put(5317, 4098); // HDV Forgerons
        changeHdv.put(4615, 4247); // HDV P�cheurs
        changeHdv.put(4646, 4262); // HDV Ressources
        changeHdv.put(8756, 8757); // HDV Forgemagie
        changeHdv.put(4618, 4174); // HDV Sculpteurs
        changeHdv.put(4588, 4172); // HDV Tailleurs
        changeHdv.put(8482, 10129); // HDV �mes
        changeHdv.put(4595, 4287); // HDV Bouchers
        changeHdv.put(4630, 2221); // HDV Boulangers
        changeHdv.put(5311, 4179); // HDV Mineurs
        changeHdv.put(4629, 4299); // HDV Paysans
        return changeHdv;
    }

    // Utilis� deux fois. Pour tous les modes HDV dans la fonction getHdv ci-dessous et dans le mode Vente de GameClient.java
    public int changeHdv(int map) {
        Map<Integer, Integer> changeHdv = getChangeHdv();
        if (changeHdv.containsKey(map)) {
            map = changeHdv.get(map);
        }
        return map;
    }*/ // By Coding Mestre : [FIX] - Bonta's and brakmar's market isn't shared anymore Close #31

    public Hdv getHdv(int map) {
        //return Hdvs.get(changeHdv(map));
        return Hdvs.get(map); // By Coding Mestre
    }

    public synchronized int getNextObjectHdvId() {
        nextObjectHdvId++;
        return nextObjectHdvId;
    }

    public synchronized void setNextObjectHdvId(int id) {
        nextObjectHdvId = id;
    }

    public synchronized int getNextLineHdvId() {
        nextLineHdvId++;
        return nextLineHdvId;
    }

    public void addHdvItem(int compteID, int hdvID, HdvEntry toAdd) {
        if (hdvsItems.get(compteID) == null) //Si le compte n'est pas dans la memoire
            hdvsItems.put(compteID, new HashMap<>()); //Ajout du compte cl�:compteID et un nouveau Map<hdvID,items<>>
        if (hdvsItems.get(compteID).get(hdvID) == null)
            hdvsItems.get(compteID).put(hdvID, new ArrayList<>());
        hdvsItems.get(compteID).get(hdvID).add(toAdd);
    }

    public void removeHdvItem(int compteID, int hdvID, HdvEntry toDel) {
        hdvsItems.get(compteID).get(hdvID).remove(toDel);
    }

    public void addHdv(Hdv toAdd) {
        Hdvs.put(toAdd.getHdvId(), toAdd);
    }

    public Map<Integer, ArrayList<HdvEntry>> getMyItems(
            int compteID) {
        if (hdvsItems.get(compteID) == null)//Si le compte n'est pas dans la memoire
            hdvsItems.put(compteID, new HashMap<>());//Ajout du compte cl�:compteID et un nouveau Map<hdvID,items
        return hdvsItems.get(compteID);
    }

    public Collection<ObjectTemplate> getObjTemplates() {
        return ObjTemplates.values();
    }

    public void priestRequest(Player boy, Player girl, Player asked) {
        if(boy.getSexe() == 0 && girl.getSexe() == 1) {
            final GameMap map = boy.getCurMap();
            if (boy.getWife() != 0) {// 0 : femme | 1 = homme
                SocketManager.GAME_SEND_MESSAGE_TO_MAP(map, boy.getName() + " est d�j� mari� !", Config.getInstance().colorMessage);
                return;
            }
            if (girl.getWife() != 0) {
                SocketManager.GAME_SEND_MESSAGE_TO_MAP(map, girl.getName() + " est d�j� mari� !", Config.getInstance().colorMessage);
                return;
            }
            SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(map, "", -1, "Pr�tre", asked.getName()
                    + " acceptez-vous d'�pouser " + (asked.getSexe() == 1 ? girl : boy).getName() + " ?");
            SocketManager.GAME_SEND_WEDDING(map, 617, (boy == asked ? boy.getId() : girl.getId()), (boy == asked ? girl.getId() : boy.getId()), -1);
        }
    }


    public void wedding(Player boy, Player girl, int isOK) {
        if (isOK > 0) {
            SocketManager.GAME_SEND_cMK_PACKET_TO_MAP(boy.getCurMap(), "", -1, "Pr�tre", "Je d�clare "
                    + boy.getName() + " et " + girl.getName() + " unis par les liens sacr�s du mariage.");
            boy.setWife(girl.getId());
            girl.setWife(boy.getId());
        } else {
            SocketManager.GAME_SEND_Im_PACKET_TO_MAP(boy.getCurMap(), "048;" + boy.getName() + "~" + girl.getName());
        }
        boy.setisOK(0);
        boy.setBlockMovement(false);
        girl.setisOK(0);
        girl.setBlockMovement(false);
    }

    public Animation getAnimation(int AnimationId) {
        return Animations.get(AnimationId);
    }

    public void addAnimation(Animation animation) {
        Animations.put(animation.getId(), animation);
    }

    public void addHouse(House house) {
        Houses.put(house.getId(), house);
    }

    public House getHouse(int id) {
        return Houses.get(id);
    }

    public void addCollector(Collector Collector) {
        collectors.put(Collector.getId(), Collector);
    }

    public Collector getCollector(int CollectorID) {
        return collectors.get(CollectorID);
    }

    public void addTrunk(Trunk trunk) {
        Trunks.put(trunk.getId(), trunk);
    }

    public Trunk getTrunk(int id) {
        return Trunks.get(id);
    }

    public void addMountPark(MountPark mp) {
        MountPark.put(mp.getMap().getId(), mp);
    }

    public Map<Short, MountPark> getMountPark() {
        return MountPark;
    }

    public String parseMPtoGuild(int GuildID) {
        Guild G = getGuild(GuildID);
        byte enclosMax = (byte) Math.floor(G.getLvl() / 10);
        StringBuilder packet = new StringBuilder();
        packet.append(enclosMax);

        for (Entry<Short, MountPark> mp : MountPark.entrySet()) {
            if (mp.getValue().getGuild() != null
                    && mp.getValue().getGuild().getId() == GuildID) {
                packet.append("|").append(mp.getValue().getMap().getId()).append(";").append(mp.getValue().getSize()).append(";").append(mp.getValue().getMaxObject());// Nombre d'objets pour le dernier
                if (mp.getValue().getListOfRaising().size() > 0) {
                    packet.append(";");
                    boolean primero = false;
                    for (Integer id : mp.getValue().getListOfRaising()) {
                        Mount dd = getMountById(id);
                        if (dd != null) {
                            if (primero)
                                packet.append(",");
                            packet.append(dd.getColor()).append(",").append(dd.getName()).append(",");
                            if (getPlayer(dd.getOwner()) == null)
                                packet.append("Sans maitre");
                            else
                                packet.append(getPlayer(dd.getOwner()).getName());
                            primero = true;
                        }
                    }
                }
            }
        }
        return packet.toString();
    }

    public int totalMPGuild(int GuildID) {
        int i = 0;
        for (Entry<Short, MountPark> mp : MountPark.entrySet())
            if (mp.getValue().getGuild() != null && mp.getValue().getGuild().getId() == GuildID)
                i++;
        return i;
    }

    public void addChallenge(String chal) {
        if (!Challenges.toString().isEmpty())
            Challenges.append(";");
        Challenges.append(chal);
    }

    public synchronized void addPrisme(Prism Prisme) {
        Prismes.put(Prisme.getId(), Prisme);
    }

    public Prism getPrisme(int id) {
        return Prismes.get(id);
    }

    public void removePrisme(int id) {
        Prismes.remove(id);
    }

    public Collection<Prism> AllPrisme() {
        if (Prismes.size() > 0)
            return Prismes.values();
        return null;
    }

    public String PrismesGeoposition(int alignement) {
        String str = "";
        boolean first = false;
        int subareas = 0;
        for (SubArea subarea : subAreas.values()) {
            if (!subarea.getConquistable())
                continue;
            if (first)
                str += ";";
            str += subarea.getId()
                    + ","
                    + (subarea.getAlignement() == 0 ? -1 : subarea.getAlignement())
                    + ",0,";
            if (getPrisme(subarea.getPrismId()) == null)
                str += 0 + ",1";
            else
                str += (subarea.getPrismId() == 0 ? 0 : getPrisme(subarea.getPrismId()).getMap())
                        + ",1";
            first = true;
            subareas++;
        }
        if (alignement == 1)
            str += "|" + Area.bontarians;
        else if (alignement == 2)
            str += "|" + Area.brakmarians;
        str += "|" + areas.size() + "|";
        first = false;
        for (Area area : areas.values()) {
            if (area.getAlignement() == 0)
                continue;
            if (first)
                str += ";";
            str += area.getId() + "," + area.getAlignement() + ",1,"
                    + (area.getPrismId() == 0 ? 0 : 1);
            first = true;
        }
        if (alignement == 1)
            str = Area.bontarians + "|" + subareas + "|"
                    + (subareas - (SubArea.bontarians + SubArea.brakmarians)) + "|"
                    + str;
        else if (alignement == 2)
            str = Area.brakmarians + "|" + subareas + "|"
                    + (subareas - (SubArea.bontarians + SubArea.brakmarians)) + "|"
                    + str;
        return str;
    }

    public void showPrismes(Player perso) {
        for (SubArea subarea : subAreas.values()) {
            if (subarea.getAlignement() == 0)
                continue;
            SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(perso, subarea.getId()
                    + "|" + subarea.getAlignement() + "|1");
        }
    }

    public synchronized int getNextIDPrisme() {
        int max = -102;
        for (int a : Prismes.keySet())
            if (a < max)
                max = a;
        return max - 3;
    }

    public void addPets(Pet pets) {
        Pets.put(pets.getTemplateId(), pets);
    }

    public Pet getPets(int Tid) {
        return Pets.get(Tid);
    }

    public Collection<Pet> getPets() {
        return Pets.values();
    }

    public void addPetsEntry(PetEntry pets) {
        PetsEntry.put(pets.getObjectId(), pets);
    }

    public PetEntry getPetsEntry(int guid) {
        return PetsEntry.get(guid);
    }

    public PetEntry removePetsEntry(int guid) {
        return PetsEntry.remove(guid);
    }

    public String getChallengeFromConditions(boolean sevEnn,
                                                    boolean sevAll, boolean bothSex, boolean EvenEnn, boolean MoreEnn,
                                                    boolean hasCaw, boolean hasChaf, boolean hasRoul, boolean hasArak,
                                                    int isBoss, boolean ecartLvlPlayer, boolean hasArround,
                                                    boolean hasDisciple, boolean isSolo) {
        StringBuilder toReturn = new StringBuilder();
        boolean isFirst = true, isGood = false;
        int cond;

        for (String chal : Challenges.toString().split(";")) {
            if (!isFirst && isGood)
                toReturn.append(";");
            isGood = true;
            int id = Integer.parseInt(chal.split(",")[0]);
            cond = Integer.parseInt(chal.split(",")[4]);
            //Necessite plusieurs ennemis
            if (((cond & 1) == 1) && !sevEnn)
                isGood = false;
            //Necessite plusieurs allies
            if ((((cond >> 1) & 1) == 1) && !sevAll)
                isGood = false;
            //Necessite les deux sexes
            if ((((cond >> 2) & 1) == 1) && !bothSex)
                isGood = false;
            //Necessite un nombre pair d'ennemis
            if ((((cond >> 3) & 1) == 1) && !EvenEnn)
                isGood = false;
            //Necessite plus d'ennemis que d'allies
            if ((((cond >> 4) & 1) == 1) && !MoreEnn)
                isGood = false;
            //Jardinier
            if (!hasCaw && (id == 7))
                isGood = false;
            //Fossoyeur
            if (!hasChaf && (id == 12))
                isGood = false;
            //Casino Royal
            if (!hasRoul && (id == 14))
                isGood = false;
            //Araknophile
            if (!hasArak && (id == 15))
                isGood = false;
            //Les mules d'abord
            if (!ecartLvlPlayer && (id == 48))
                isGood = false;
            //Contre un boss de donjon
            if (isBoss != -1 && id == 5)
                isGood = false;
            //Hardi
            if (!hasArround && id == 36)
                isGood = false;
            //Mains propre
            if (!hasDisciple && id == 19)
                isGood = false;

            switch (id) {
                case 47:
                case 46:
                case 45:
                case 44:
                    if (isSolo)
                        isGood = false;
                    break;
            }

            switch (isBoss) {
                case 1045://Kimbo
                    switch (id) {
                        case 37:
                        case 8:
                        case 1:
                        case 2:
                            isGood = false;
                            break;
                    }
                    break;
                case 1072://Tynril
                case 1085://Tynril
                case 1086://Tynril
                case 1087://Tynril
                    switch (id) {
                        case 36:
                        case 20:
                            isGood = false;
                            break;
                    }
                    break;
                case 1071://Rasboul Majeur
                    switch (id) {
                        case 9:
                        case 22:
                        case 17:
                        case 47:
                            isGood = false;
                            break;
                    }
                    break;
                case 780://Skeunk
                    switch (id) {
                        case 35:
                        case 25:
                        case 4:
                        case 32:
                        case 3:
                        case 31:
                        case 34:
                            isGood = false;
                            break;
                    }
                    break;
                case 113://DC
                    switch (id) {
                        case 12:
                        case 15:
                        case 7:
                        case 41:
                            isGood = false;
                            break;
                    }
                    break;
                case 612://Maitre pandore
                    switch (id) {
                        case 20:
                        case 37:
                            isGood = false;
                            break;
                    }
                    break;
                case 478://Bworker
                case 568://Tanukoui san
                case 940://Rat blanc
                    switch (id) {
                        case 20:
                            isGood = false;
                            break;
                    }
                    break;
                case 1188://Blop multi
                    switch (id) {
                        case 20:
                        case 46:
                        case 44:
                            isGood = false;
                            break;
                    }
                    break;

                case 865://Grozila
                case 866://Grasmera
                    switch (id) {
                        case 31:
                        case 32:
                            isGood = false;
                            break;
                    }
                    break;

            }
            if (isGood)
                toReturn.append(chal);
            isFirst = false;
        }
        return toReturn.toString();
    }

    public void verifyClone(Player p) {
        if (p.getCurCell() != null && p.getFight() == null) {
            if (p.getCurCell().getPlayers().contains(p)) {
                p.getCurCell().removePlayer(p);
                Database.getStatics().getPlayerData().update(p);
            }
        }
        if (p.isOnline())
            Database.getStatics().getPlayerData().update(p);
    }

    public ArrayList<String> getRandomChallenge(int nombreChal,
                                                       String challenges) {
        String MovingChals = ";1;2;8;36;37;39;40;";// Challenges de d�placements incompatibles
        boolean hasMovingChal = false;
        String TargetChals = ";3;4;10;25;31;32;34;35;38;42;";// ceux qui ciblent
        boolean hasTargetChal = false;
        String SpellChals = ";5;6;9;11;19;20;24;41;";// ceux qui obligent � caster sp�cialement
        boolean hasSpellChal = false;
        String KillerChals = ";28;29;30;44;45;46;48;";// ceux qui disent qui doit tuer
        boolean hasKillerChal = false;
        String HealChals = ";18;43;";// ceux qui emp�chent de soigner
        boolean hasHealChal = false;

        int compteur = 0, i;
        ArrayList<String> toReturn = new ArrayList<>();
        String chal;
        while (compteur < 100 && toReturn.size() < nombreChal) {
            compteur++;
            i = Formulas.getRandomValue(1, challenges.split(";").length);
            chal = challenges.split(";")[i - 1];// challenge au hasard dans la liste

            if (!toReturn.contains(chal))// si le challenge n'y etait pas encore
            {
                if (MovingChals.contains(";" + chal.split(",")[0] + ";"))// s'il appartient a une liste
                    if (!hasMovingChal)// et qu'aucun de la liste n'a ete choisi deja
                    {
                        hasMovingChal = true;
                        toReturn.add(chal);
                        continue;
                    } else
                        continue;
                if (TargetChals.contains(";" + chal.split(",")[0] + ";"))
                    if (!hasTargetChal) {
                        hasTargetChal = true;
                        toReturn.add(chal);
                        continue;
                    } else
                        continue;
                if (SpellChals.contains(";" + chal.split(",")[0] + ";"))
                    if (!hasSpellChal) {
                        hasSpellChal = true;
                        toReturn.add(chal);
                        continue;
                    } else
                        continue;
                if (KillerChals.contains(";" + chal.split(",")[0] + ";"))
                    if (!hasKillerChal) {
                        hasKillerChal = true;
                        toReturn.add(chal);
                        continue;
                    } else
                        continue;
                if (HealChals.contains(";" + chal.split(",")[0] + ";"))
                    if (!hasHealChal) {
                        hasHealChal = true;
                        toReturn.add(chal);
                        continue;
                    } else
                        continue;
                toReturn.add(chal);
            }
            compteur++;
        }
        return toReturn;
    }

    public Collector getCollectorByMap(int id) {

        for (Entry<Integer, Collector> Collector : getCollectors().entrySet()) {
            GameMap map = getMap(Collector.getValue().getMap());
            if (map.getId() == id) {
                return Collector.getValue();
            }
        }
        return null;
    }

    public void reloadPlayerGroup() {
        Main.gameServer.getClients().stream().filter(client -> client != null && client.getPlayer() != null).forEach(client -> Database.getStatics().getPlayerData().reloadGroup(client.getPlayer()));
    }

    public void reloadDrops() {
        Database.getDynamics().getDropData().reload();
    }

    public void reloadEndFightActions() {
        Database.getDynamics().getEndFightActionData().reload();
    }

    public void reloadNpcs() {
        Database.getDynamics().getNpcTemplateData().reload();
        questions.clear();
        Database.getDynamics().getNpcQuestionData().load();
        answers.clear();
        Database.getDynamics().getNpcAnswerData().load();
    }

    public void reloadHouses() {
        Houses.clear();
        Database.getStatics().getHouseData().load();
        Database.getDynamics().getHouseData().load();
    }

    public void reloadTrunks() {
        Trunks.clear();
        Database.getStatics().getTrunkData().load();
        Database.getDynamics().getTrunkData().load();
    }

    public void reloadMaps() {
        Database.getDynamics().getMapData().reload();
    }

    public void reloadMountParks(int i) {
        Database.getStatics().getMountParkData().reload(i);
        Database.getDynamics().getMountParkData().reload(i);
    }

    public void reloadMonsters() {
        Database.getDynamics().getMonsterData().reload();
    }

    public void reloadQuests() {
        Database.getDynamics().getQuestData().load();
    }

    public void reloadObjectsActions() {
        Database.getDynamics().getObjectActionData().reload();
    }

    public void reloadSpells() {
        Database.getDynamics().getSpellData().load();
    }

    public void reloadItems() {
        Database.getDynamics().getObjectTemplateData().load();
    }

    public void addSeller(Player player) {
        if (player.getStoreItems().isEmpty())
            return;

        short map = player.getCurMap().getId();

        if (Seller.get(map) == null) {
            ArrayList<Integer> players = new ArrayList<>();
            players.add(player.getId());
            Seller.put(map, players);
        } else {
            ArrayList<Integer> players = new ArrayList<>();
            players.add(player.getId());
            players.addAll(Seller.get(map));
            Seller.remove(map);
            Seller.put(map, players);
        }
    }

    public Collection<Integer> getSeller(short map) {
        return Seller.get(map);
    }

    public void removeSeller(int player, short map) {
        if(getSeller(map) != null)
            Seller.get(map).remove(player);
    }

    public static double getPwrPerEffet(int effect) {
        double r = 0.0;
        switch (effect) {
            case Constant.STATS_ADD_PA:
                r = 100.0;
                break;
            case Constant.STATS_ADD_PM2:
                r = 90.0;
                break;
            case Constant.STATS_ADD_VIE:
                r = 0.25;
                break;
            case Constant.STATS_MULTIPLY_DOMMAGE:
                r = 100.0;
                break;
            case Constant.STATS_ADD_CC:
                r = 30.0;
                break;
            case Constant.STATS_ADD_PO:
                r = 51.0;
                break;
            case Constant.STATS_ADD_FORC:
                r = 1.0;
                break;
            case Constant.STATS_ADD_AGIL:
                r = 1.0;
                break;
            case Constant.STATS_ADD_PA2:
                r = 100.0;
                break;
            case Constant.STATS_ADD_DOMA:
                r = 20.0;
                break;
            case Constant.STATS_ADD_EC:
                r = 1.0;
                break;
            case Constant.STATS_ADD_CHAN:
                r = 1.0;
                break;
            case Constant.STATS_ADD_SAGE:
                r = 3.0;
                break;
            case Constant.STATS_ADD_VITA:
                r = 0.25;
                break;
            case Constant.STATS_ADD_INTE:
                r = 1.0;
                break;
            case Constant.STATS_ADD_PM:
                r = 90.0;
                break;
            case Constant.STATS_ADD_PERDOM:
                r = 2.0;
                break;
            case Constant.STATS_ADD_PDOM:
                r = 2.0;
                break;
            case Constant.STATS_ADD_PODS:
                r = 0.25;
                break;
            case Constant.STATS_ADD_AFLEE:
                r = 1.0;
                break;
            case Constant.STATS_ADD_MFLEE:
                r = 1.0;
                break;
            case Constant.STATS_ADD_INIT:
                r = 0.1;
                break;
            case Constant.STATS_ADD_PROS:
                r = 3.0;
                break;
            case Constant.STATS_ADD_SOIN:
                r = 20.0;
                break;
            case Constant.STATS_CREATURE:
                r = 30.0;
                break;
            case Constant.STATS_ADD_RP_TER:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_EAU:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_AIR:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_FEU:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_NEU:
                r = 6.0;
                break;
            case Constant.STATS_TRAPDOM:
                r = 15.0;
                break;
            case Constant.STATS_TRAPPER:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_FEU:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_NEU:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_TER:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_EAU:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_AIR:
                r = 2.0;
                break;
            case Constant.STATS_ADD_RP_PVP_TER:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_PVP_EAU:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_PVP_AIR:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_PVP_FEU:
                r = 6.0;
                break;
            case Constant.STATS_ADD_RP_PVP_NEU:
                r = 6.0;
                break;
            case Constant.STATS_ADD_R_PVP_TER:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_PVP_EAU:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_PVP_AIR:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_PVP_FEU:
                r = 2.0;
                break;
            case Constant.STATS_ADD_R_PVP_NEU:
                r = 2.0;
                break;
        }
        return r;
    }

    public static double getOverPerEffet(int effect) {
        double r = 0.0;
        switch (effect) {
            case Constant.STATS_ADD_PA:
                r = 0.0;
                break;
            case Constant.STATS_ADD_PM2:
                r = 404.0;
                break;
            case Constant.STATS_ADD_VIE:
                r = 404.0;
                break;
            case Constant.STATS_MULTIPLY_DOMMAGE:
                r = 0.0;
                break;
            case Constant.STATS_ADD_CC:
                r = 3.0;
                break;
            case Constant.STATS_ADD_PO:
                r = 0.0;
                break;
            case Constant.STATS_ADD_FORC:
                r = 101.0;
                break;
            case Constant.STATS_ADD_AGIL:
                r = 101.0;
                break;
            case Constant.STATS_ADD_PA2:
                r = 0.0;
                break;
            case Constant.STATS_ADD_DOMA:
                r = 5.0;
                break;
            case Constant.STATS_ADD_EC:
                r = 0.0;
                break;
            case Constant.STATS_ADD_CHAN:
                r = 101.0;
                break;
            case Constant.STATS_ADD_SAGE:
                r = 33.0;
                break;
            case Constant.STATS_ADD_VITA:
                r = 404.0;
                break;
            case Constant.STATS_ADD_INTE:
                r = 101.0;
                break;
            case Constant.STATS_ADD_PM:
                r = 0.0;
                break;
            case Constant.STATS_ADD_PERDOM:
                r = 50.0;
                break;
            case Constant.STATS_ADD_PDOM:
                r = 50.0;
                break;
            case Constant.STATS_ADD_PODS:
                r = 404.0;
                break;
            case Constant.STATS_ADD_AFLEE:
                r = 0.0;
                break;
            case Constant.STATS_ADD_MFLEE:
                r = 0.0;
                break;
            case Constant.STATS_ADD_INIT:
                r = 1010.0;
                break;
            case Constant.STATS_ADD_PROS:
                r = 33.0;
                break;
            case Constant.STATS_ADD_SOIN:
                r = 5.0;
                break;
            case Constant.STATS_CREATURE:
                r = 3.0;
                break;
            case Constant.STATS_ADD_RP_TER:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_EAU:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_AIR:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_FEU:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_NEU:
                r = 16.0;
                break;
            case Constant.STATS_TRAPDOM:
                r = 6.0;
                break;
            case Constant.STATS_TRAPPER:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_FEU:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_NEU:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_TER:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_EAU:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_AIR:
                r = 50.0;
                break;
            case Constant.STATS_ADD_RP_PVP_TER:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_PVP_EAU:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_PVP_AIR:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_PVP_FEU:
                r = 16.0;
                break;
            case Constant.STATS_ADD_RP_PVP_NEU:
                r = 16.0;
                break;
            case Constant.STATS_ADD_R_PVP_TER:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_PVP_EAU:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_PVP_AIR:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_PVP_FEU:
                r = 50.0;
                break;
            case Constant.STATS_ADD_R_PVP_NEU:
                r = 50.0;
                break;
        }
        return r;
    }

    public double getTauxObtentionIntermediaire(double bonus, boolean b1, boolean b2) {
        double taux = bonus;
        // 100.0 + 2*(30.0 + 2*10.0) => true true
        // 30.0 + 2*(10.0 + 2*3.0) => true false
        // 10.0 + 2*(3.0 + 2*1.0) => true true
        if (b1) {
            if (bonus == 100.0)
                taux += 2.0 * getTauxObtentionIntermediaire(30.0, true, b2);
            if (bonus == 30.0)
                taux += 2.0 * getTauxObtentionIntermediaire(10.0, (!b2), b2); // Si b2 est false alors on calculera 2*3.0 dans 10.0
            if (bonus == 10.0)
                taux += 2.0 * getTauxObtentionIntermediaire(3.0, (b2), b2); // Si b2 est true alors on calculera apr�s
            else if (bonus == 3.0)
                taux += 2.0 * getTauxObtentionIntermediaire(1.0, false, b2);
        }

        return taux;
    }

    public int getMetierByMaging(int idMaging) {
        int mId = -1;
        switch (idMaging) {
            case 43: // FM Dagues
                mId = 17;
                break;
            case 44: // FM Ep�es
                mId = 11;
                break;
            case 45: // FM Marteaux
                mId = 14;
                break;
            case 46: // FM Pelles
                mId = 20;
                break;
            case 47: // FM Haches
                mId = 31;
                break;
            case 48: // FM Arcs
                mId = 13;
                break;
            case 49: // FM Baguettes
                mId = 19;
                break;
            case 50: // FM B�tons
                mId = 18;
                break;
            case 62: // Cordo
                mId = 15;
                break;
            case 63: // Jaillo
                mId = 16;
                break;
            case 64: // Costu
                mId = 27;
                break;
        }
        return mId;
    }

    public int getTempleByClasse(int classe) {
        int temple = -1;
        switch (classe) {
            case Constant.CLASS_FECA: // f�ca
                temple = 1554;
                break;
            case Constant.CLASS_OSAMODAS: // osa
                temple = 1546;
                break;
            case Constant.CLASS_ENUTROF: // �nu
                temple = 1470;
                break;
            case Constant.CLASS_SRAM: // sram
                temple = 6926;
                break;
            case Constant.CLASS_XELOR: // xelor
                temple = 1469;
                break;
            case Constant.CLASS_ECAFLIP: // �ca
                temple = 1544;
                break;
            case Constant.CLASS_ENIRIPSA: // �ni
                temple = 6928;
                break;
            case Constant.CLASS_IOP: // iop
                temple = 1549;
                break;
            case Constant.CLASS_CRA: // cra
                temple = 1558;
                break;
            case Constant.CLASS_SADIDA: // sadi
                temple = 1466;
                break;
            case Constant.CLASS_SACRIEUR: // sacri
                temple = 6949;
                break;
            case Constant.CLASS_PANDAWA: // panda
                temple = 8490;
                break;
        }
        return temple;
    }

    public ArrayList<Monster.MobGrade> getMobgradeBetweenLvl(int min, int max){
        ArrayList<Monster> arrayMonstre = new ArrayList<>();
        ArrayList<Monster.MobGrade> arrayMobgrade = new ArrayList<>();
        getMonstres().stream().filter(monster -> monster != null && !(ArrayUtils.contains(Constant.FILTER_MONSTRE_SPE, monster.getType())) && !(monster.getGrade(1).getSpells().keySet().isEmpty()) && (monster.getAlign() == -1)
                && !(ArrayUtils.contains(Constant.BOSS_ID, monster.getId())) && !(ArrayUtils.contains(Constant.EXCEPTION_GLADIATROOL_MONSTRES, monster.getId())) && (getLvlMax(monster) >= min && getLvlMax(monster) < max)).forEach(arrayMonstre::add);

        for(Monster mob : arrayMonstre){
            arrayMobgrade.add(mob.getGrade(5));
        }
        return arrayMobgrade;
    }

    public ArrayList<Monster.MobGrade> getArchiMobgradeBetweenLvl(int min, int max){
        ArrayList<Monster> arrayMonstre = new ArrayList<>();
        ArrayList<Monster.MobGrade> arrayMobgrade = new ArrayList<>();
        getMonstres().stream().filter(monster -> monster != null && (ArrayUtils.contains(Constant.MONSTRE_TYPE_ARCHI, monster.getType())) && !(ArrayUtils.contains(Constant.EXCEPTION_GLADIATROOL_ARCHI, monster.getId())) && !(monster.getGrade(1).getSpells().keySet().isEmpty())
                && (getLvlMax(monster) >= min && getLvlMax(monster) < max)).forEach(arrayMonstre::add);

        for(Monster mob : arrayMonstre){
            arrayMobgrade.add(mob.getGrade(5));
        }
        return arrayMobgrade;
    }

    public ArrayList<Monster.MobGrade> getBossMobgradeBetweenLvl(int min, int max){
        ArrayList<Monster> arrayMonstre = new ArrayList<>();
        ArrayList<Monster.MobGrade> arrayMobgrade = new ArrayList<>();
        getMonstres().stream().filter(monster -> monster != null && (ArrayUtils.contains(Constant.BOSS_ID, monster.getId())) && !(ArrayUtils.contains(Constant.EXCEPTION_GLADIATROOL_BOSS, monster.getId())) && !(monster.getGrade(1).getSpells().keySet().isEmpty()) && (monster.getAlign() == -1)
                && (getLvlMax(monster) >= min && getLvlMax(monster) < max)).forEach(arrayMonstre::add);

        for(Monster mob : arrayMonstre){
            arrayMobgrade.add(mob.getGrade(5));
        }
        return arrayMobgrade;
    }

    public int getLvlMax(Monster monstre){
        int levelmoyen = monstre.getGrade(5).getLevel();
        return levelmoyen;
    }

    public static class Drop {
        private int objectId, ceil, action, level;
        private String condition;
        private ArrayList<Double> percents;
        private double localPercent;

        public Drop(int objectId, ArrayList<Double> percents, int ceil, int action, int level, String condition) {
            this.objectId = objectId;
            this.percents = percents;
            this.ceil = ceil;
            this.action = action;
            this.level = level;
            this.condition = condition;
        }

        public Drop(int objectId, double percent, int ceil) {
            this.objectId = objectId;
            this.localPercent = percent;
            this.ceil = ceil;
            this.action = -1;
            this.level = -1;
            this.condition = "";
        }

        public int getObjectId() {
            return objectId;
        }

        public int getCeil() {
            return ceil;
        }

        public int getAction() {
            return action;
        }

        public int getLevel() {
            return level;
        }

        public String getCondition() {
            return condition;
        }

        public double getLocalPercent() {
            return localPercent;
        }

        public Drop copy(int grade) {
            Drop drop = new Drop(this.objectId, null, this.ceil, this.action, this.level, this.condition);
            if(this.percents == null) return null;
            if(this.percents.isEmpty()) return null;
            try {
                if (this.percents.get(grade - 1) == null) return null;
                drop.localPercent = this.percents.get(grade - 1);
            } catch(IndexOutOfBoundsException ignored) { return null; }
            return drop;
        }
    }

    public static class Couple<L, R> {
        public L first;
        public R second;

        public Couple(L s, R i) {
            this.first = s;
            this.second = i;
        }
    }

    public static class ExpLevel {
        public long perso;
        public int metier;
        public int mount;
        public int pvp;
        public long guilde;
        public long tourmenteurs;
        public long bandits;

        public ExpLevel(long c, int m, int d, int p, long t, long b) {
            perso = c;
            metier = m;
            this.mount = d;
            pvp = p;
            guilde = perso * 10;
            tourmenteurs = t;
            bandits = b;
        }
    }
}
