package org.starloco.locos.object;

import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.Database;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.other.Dopeul;

import java.util.*;

public class ObjectTemplate {

    private int id;
    private String strTemplate;
    private String name;
    private int type;
    private int level;
    private int pod;
    private int price;
    private int panoId;
    private String conditions;
    private int PACost, POmin, POmax, tauxCC, tauxEC,
            bonusCC;
    private boolean isTwoHanded;
    private long sold;
    private int avgPrice;
    private int points, newPrice;
    private ArrayList<ObjectAction> onUseActions;
    private int money=-1;
    public String toString() {
        return id + "";
    }

    public ObjectTemplate(int id, String strTemplate, String name, int type,
                          int level, int pod, int price, int panoId, String conditions,
                          String armesInfos, int sold, int avgPrice, int points, int newPrice) {
        this.id = id;
        try {
            this.strTemplate = this.initStrTemplate(strTemplate);
        }
        catch (Exception e ){
            System.out.println(e);
        }
        this.name = name;
        this.type = type;
        this.level = level;
        this.pod = pod;
        this.price = price;
        this.panoId = panoId;
        this.conditions = conditions;
        this.PACost = -1;
        this.POmin = 1;
        this.POmax = 1;
        this.tauxCC = 100;
        this.tauxEC = 2;
        this.bonusCC = 0;
        this.sold = sold;
        this.avgPrice = avgPrice;
        this.points = points;
        this.newPrice = newPrice;
        this.money=-1;
        if(armesInfos.isEmpty()) return;
        try {
            String[] infos = armesInfos.split(";");
            PACost = Integer.parseInt(infos[0]);
            POmin = Integer.parseInt(infos[1]);
            POmax = Integer.parseInt(infos[2]);
            tauxCC = Integer.parseInt(infos[3]);
            tauxEC = Integer.parseInt(infos[4]);
            bonusCC = Integer.parseInt(infos[5]);
            isTwoHanded = infos[6].equals("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setInfos(String strTemplate, String name, int type, int level, int pod, int price, int panoId, String conditions, String armesInfos, int sold, int avgPrice, int points, int newPrice) {
        this.strTemplate = this.initStrTemplate(strTemplate);
        this.name = name;
        this.type = type;
        this.level = level;
        this.pod = pod;
        this.price = price;
        this.panoId = panoId;
        this.conditions = conditions;
        this.PACost = -1;
        this.POmin = 1;
        this.POmax = 1;
        this.tauxCC = 100;
        this.tauxEC = 2;
        this.bonusCC = 0;
        this.sold = sold;
        this.avgPrice = avgPrice;
        this.points = points;
        this.newPrice = newPrice;
        try {
            String[] infos = armesInfos.split(";");
            PACost = Integer.parseInt(infos[0]);
            POmin = Integer.parseInt(infos[1]);
            POmax = Integer.parseInt(infos[2]);
            tauxCC = Integer.parseInt(infos[3]);
            tauxEC = Integer.parseInt(infos[4]);
            bonusCC = Integer.parseInt(infos[5]);
            isTwoHanded = infos[6].equals("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int id) {
        this.money = id;
    }

    public int getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(int id) {
        this.newPrice = id;
    }

    /**
     * 
     * Permet de trier l'ordre des stats des items templates. En premier c'est les stats de class , ensuite les effects ( donc les degats d armes ) 
     * , et par la suite les stats normaux qui seront trier par ordre defini. A la fin se situeront les stats de textes si il y en a.
     * 
     * @author Sarazar928Ghost Kevin#6537
     * @param strTemplate
     * @return sort strTemplate
     */
    private String initStrTemplate(final String strTemplate) {
    	
    	if(strTemplate.isEmpty()) return strTemplate;
    	
    	final StringBuilder sortsBuilder = new StringBuilder();
    	final StringBuilder effectsBuilder = new StringBuilder();
    	final StringBuilder statsBuilder = new StringBuilder();
    	
    	boolean firstSorts = true;
    	boolean firstEffects = true;
    	boolean firstStats = true;
    	
    	for(final String split : strTemplate.split(",")) {
            if(split.length() == 0){
                continue;
            }
    		final String[] datas = split.split("#");
            if(datas.length == 0){
                continue;
            }
    		final int id = Integer.parseInt(datas[0], 16);
    		if (id >= 281 && id <= 294) { // Stats class item
    			if(!firstSorts)
    				sortsBuilder.append(",");
    			firstSorts = false;
    			sortsBuilder.append(split);
    			continue;
    		} 
    		boolean follow1 = true;
            switch (id) {
                case 110:
                case 139:
                case 605:
                case 614:
                	if(!firstEffects)
        				effectsBuilder.append(",");
                	firstEffects = false;
                	effectsBuilder.append(split);
                    follow1 = false;
            }
            if (!follow1) continue;
            

            boolean follow2 = true;
            for (int a : Constant.ARMES_EFFECT_IDS) {
                if (a == id) {
                	if(!firstEffects)
        				effectsBuilder.append(",");
                	firstEffects = false;
                	effectsBuilder.append(split);
                    follow2 = false;
                    break;
                }
            }
            if (!follow2) continue; //Si c'�tait un effet Actif d'arme ou une signature
            
            if(!firstStats)
            	statsBuilder.append(",");
            firstStats = false;
            statsBuilder.append(split);
    	}
    	
    	final String orderStats = Formulas.sortStatsByOrder(statsBuilder.toString());
    	final String sorts = sortsBuilder.toString();
    	final String effects = effectsBuilder.toString();
    	
    	
    	final StringBuilder theReturn = new StringBuilder();
    	
    	theReturn.append(sorts);
    	if(!effects.isEmpty() && !sorts.isEmpty())
    		theReturn.append(",");
    	theReturn.append(effects);
    	if(!effects.isEmpty() && !orderStats.isEmpty())
    		theReturn.append(",");
    	theReturn.append(orderStats);
    	
    	return theReturn.toString();
    }
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStrTemplate() {
        return strTemplate;
    }

    public void setStrTemplate(String strTemplate) {
        this.strTemplate = strTemplate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPod() {
        return pod;
    }

    public void setPod(int pod) {
        this.pod = pod;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPanoId() {
        return panoId;
    }

    public void setPanoId(int panoId) {
        this.panoId = panoId;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public int getPACost() {
        return PACost;
    }

    public void setPACost(int pACost) {
        PACost = pACost;
    }

    public int getPOmin() {
        return POmin;
    }

    public void setPOmin(int pOmin) {
        POmin = pOmin;
    }

    public int getPOmax() {
        return POmax;
    }

    public void setPOmax(int pOmax) {
        POmax = pOmax;
    }

    public int getTauxCC() {
        return tauxCC;
    }

    public void setTauxCC(int tauxCC) {
        this.tauxCC = tauxCC;
    }

    public int getTauxEC() {
        return tauxEC;
    }

    public void setTauxEC(int tauxEC) {
        this.tauxEC = tauxEC;
    }

    public int getBonusCC() {
        return bonusCC;
    }

    public void setBonusCC(int bonusCC) {
        this.bonusCC = bonusCC;
    }

    public boolean isTwoHanded() {
        return isTwoHanded;
    }

    public void setTwoHanded(boolean isTwoHanded) {
        this.isTwoHanded = isTwoHanded;
    }

    public int getAvgPrice() {
        return avgPrice;
    }

    public long getSold() {
        return this.sold;
    }

    public int getPoints() {
        return this.points;
    }

    public void addAction(ObjectAction A) {
        if(this.onUseActions == null)
            this.onUseActions = new ArrayList<>();
        this.onUseActions.add(A);
    }

    public ArrayList<ObjectAction> getOnUseActions() {
        return onUseActions == null ? new ArrayList<>() : onUseActions;
    }

    public GameObject createNewCertificat(GameObject obj) {
        int id = Database.getDynamics().getWorldEntityData().getNextObjectId();
        GameObject item = null;
        if (getType() == Constant.ITEM_TYPE_CERTIFICAT_CHANIL) {
            PetEntry myPets = World.world.getPetsEntry(obj.getGuid());
            Map<Integer, String> txtStat = new HashMap<Integer, String>();
            Map<Integer, String> actualStat = new HashMap<Integer, String>();
            actualStat = obj.getTxtStat();
            if (actualStat.containsKey(Constant.STATS_PETS_PDV))
                txtStat.put(Constant.STATS_PETS_PDV, actualStat.get(Constant.STATS_PETS_PDV));
            if (actualStat.containsKey(Constant.STATS_PETS_DATE))
                txtStat.put(Constant.STATS_PETS_DATE, myPets.getLastEatDate()
                        + "");
            if (actualStat.containsKey(Constant.STATS_PETS_POIDS))
                txtStat.put(Constant.STATS_PETS_POIDS, actualStat.get(Constant.STATS_PETS_POIDS));
            if (actualStat.containsKey(Constant.STATS_PETS_EPO))
                txtStat.put(Constant.STATS_PETS_EPO, actualStat.get(Constant.STATS_PETS_EPO));
            if (actualStat.containsKey(Constant.STATS_PETS_REPAS))
                txtStat.put(Constant.STATS_PETS_REPAS, actualStat.get(Constant.STATS_PETS_REPAS));
            item = new GameObject(id, getId(), 1, Constant.ITEM_POS_NO_EQUIPED, obj.getStats(), new ArrayList<SpellEffect>(), new HashMap<Integer, Integer>(), txtStat, 0);
            World.world.removePetsEntry(obj.getGuid());
            Database.getDynamics().getPetData().delete(obj.getGuid());
        }
        return item;
    }

    public GameObject createNewFamilier(GameObject obj) {
        int id = Database.getDynamics().getWorldEntityData().getNextObjectId();
        Map<Integer, String> stats = new HashMap<>();
        stats.putAll(obj.getTxtStat());

        GameObject object = new GameObject(id, getId(), 1, Constant.ITEM_POS_NO_EQUIPED, obj.getStats(), new ArrayList<>(), new HashMap<>(), stats, 0);

        long time = System.currentTimeMillis();
        World.world.addPetsEntry(new PetEntry(id, getId(), time, 0, Integer.parseInt(stats.get(Constant.STATS_PETS_PDV), 16), Integer.parseInt(stats.get(Constant.STATS_PETS_POIDS), 16), !stats.containsKey(Constant.STATS_PETS_EPO)));
        Database.getDynamics().getPetData().add(id, time, getId());
        return object;
    }

    public GameObject createNewBenediction(int turn) {
        int id = Database.getDynamics().getWorldEntityData().getNextObjectId();
        GameObject item = null;
        Stats stats = generateNewStatsFromTemplate(getStrTemplate(), true);
        stats.addOneStat(Constant.STATS_TURN, turn);
        item = new GameObject(id, getId(), 1, Constant.ITEM_POS_BENEDICTION, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        return item;
    }

    public GameObject createNewMalediction() {
        int id = Database.getDynamics().getWorldEntityData().getNextObjectId();
        Stats stats = generateNewStatsFromTemplate(getStrTemplate(), true);
        stats.addOneStat(Constant.STATS_TURN, 1);
        return new GameObject(id, getId(), 1, Constant.ITEM_POS_MALEDICTION, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
    }

    public GameObject createNewRoleplayBuff() {
        int id = Database.getDynamics().getWorldEntityData().getNextObjectId();
        Stats stats = generateNewStatsFromTemplate(getStrTemplate(), true);
        stats.addOneStat(Constant.STATS_TURN, 1);
        return new GameObject(id, getId(), 1, Constant.ITEM_POS_ROLEPLAY_BUFF, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
    }

    public GameObject createNewCandy(int turn) {
        int id = Database.getDynamics().getWorldEntityData().getNextObjectId();
        GameObject item = null;
        Stats stats = generateNewStatsFromTemplate(getStrTemplate(), true);
        stats.addOneStat(Constant.STATS_TURN, turn);
        item = new GameObject(id, getId(), 1, Constant.ITEM_POS_BONBON, stats, new ArrayList<SpellEffect>(), new HashMap<Integer, Integer>(), new HashMap<Integer, String>(), 0);
        return item;
    }

    public GameObject createNewFollowPnj(int turn) {
        int id = Database.getDynamics().getWorldEntityData().getNextObjectId();
        GameObject item = null;
        Stats stats = generateNewStatsFromTemplate(getStrTemplate(), true);
        stats.addOneStat(Constant.STATS_TURN, turn);
        stats.addOneStat(148, 0);
        item = new GameObject(id, getId(), 1, Constant.ITEM_POS_PNJ_SUIVEUR, stats, new ArrayList<SpellEffect>(), new HashMap<Integer, Integer>(), new HashMap<Integer, String>(), 0);
        return item;
    }
    
    public GameObject createNewItem(int qua, boolean useMax) {
    	int id = -1;
        GameObject item;
        if (getType() == Constant.ITEM_TYPE_QUETES && (Constant.isCertificatDopeuls(getId()) || getId() == 6653 || getId() == 12803)  ) {
            Map<Integer, String> txtStat = new HashMap<Integer, String>();
            txtStat.put(Constant.STATS_DATE, System.currentTimeMillis() + "");
            item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(), new ArrayList<SpellEffect>(), new HashMap<Integer, Integer>(), txtStat, 0);
        } else if (this.getId() == 10207) {
            item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(), new ArrayList<SpellEffect>(), new HashMap<Integer, Integer>(), Dopeul.generateStatsTrousseau(), 0);
        } else if (getType() == Constant.ITEM_TYPE_FAMILIER) {
        	id = Database.getDynamics().getWorldEntityData().getNextObjectId();
            item = new GameObject(id, getId(), 1, Constant.ITEM_POS_NO_EQUIPED, (useMax ? generateNewStatsFromTemplate(World.world.getPets(this.getId()).getJet(), false) : new Stats()), new ArrayList<>(), new HashMap<>(), World.world.getPets(getId()).generateNewtxtStatsForPets(), 0);
            //Ajouter du Pets_data SQL et World
            long time = System.currentTimeMillis();
            World.world.addPetsEntry(new PetEntry(id, getId(), time, 0, 10, 0, false));
            Database.getDynamics().getPetData().add(id, time, getId());
        } else if(getType() == Constant.ITEM_TYPE_CERTIF_MONTURE) {
            item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, generateNewStatsFromTemplate(getStrTemplate(), useMax), getEffectTemplate(getStrTemplate()), new HashMap<>(), new HashMap<>(), 0);
        } else {
            if (getType() == Constant.ITEM_TYPE_OBJET_ELEVAGE) {
                item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(), new ArrayList<>(), new HashMap<>(), getStringResistance(getStrTemplate()), 0);
            } else if (Constant.isIncarnationWeapon(getId())) {
                Map<Integer, Integer> Stats = new HashMap<>();
                Stats.put(Constant.ERR_STATS_XP, 0);
                Stats.put(Constant.STATS_NIVEAU, 1);
                item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, generateNewStatsFromTemplate(getStrTemplate(), useMax), getEffectTemplate(getStrTemplate()), Stats, new HashMap<Integer, String>(), 0);
            } else if (Constant.isGladiatroolWeapon(getId())) {
                Map<Integer, String> Stats = new HashMap<>();
                Stats.put(Constant.STATS_EXCHANGE_IN, -1+"");
                Stats.put(Constant.STATS_NIVEAU2, 1+"");
                item = new GameObject(-1, getId(), qua, 1, generateNewStatsFromTemplate(getStrTemplate(), useMax), getEffectTemplate(getStrTemplate()),new HashMap<>() , Stats, 0);
            } else if (getId()==16001) {
                Map<Integer, String> Stats = new HashMap<>();
                Stats.put(Constant.STATS_EXCHANGE_IN, -1+"");
                item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, new Stats(), new ArrayList<SpellEffect>(), new HashMap<Integer, Integer>(), Stats, 0);
            }
            else {
                Map<Integer, String> Stat = new HashMap<>();
                switch (getType()) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        String[] splitted = getStrTemplate().split(",");
                        for (String s : splitted) {
                            String[] stats = s.split("#");
                            int statID = Integer.parseInt(stats[0], 16);
                            if (statID == Constant.STATS_RESIST) {
                                String ResistanceIni = stats[1];
                                Stat.put(statID, ResistanceIni);
                            }
                        }
                        break;
                }
                item = new GameObject(id, getId(), qua, Constant.ITEM_POS_NO_EQUIPED, generateNewStatsFromTemplate(getStrTemplate(), useMax), getEffectTemplate(getStrTemplate()), new HashMap<Integer, Integer>(), Stat, 0);
            }
        }
        return item;
    }

    private Map<Integer, String> getStringResistance(String statsTemplate) {
        Map<Integer, String> Stat = new HashMap<Integer, String>();
        String[] splitted = statsTemplate.split(",");

        for (String s : splitted) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            String ResistanceIni = stats[1];
            Stat.put(statID, ResistanceIni);
        }
        return Stat;
    }

    public Stats generateNewStatsFromTemplate(String statsTemplate,
                                              boolean useMax) {
        Stats itemStats = new Stats();
        //Si stats Vides
        if (statsTemplate.equals("") || statsTemplate == null)
            return itemStats;

        String[] splitted = statsTemplate.split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            
            if (statID >= 281 && statID <= 294) // Stat item class
            	continue;
            
            boolean follow = true;

            for (int a : Constant.ARMES_EFFECT_IDS)
                //Si c'est un Effet Actif
                if (a == statID)
                    follow = false;
            if (!follow)//Si c'�tait un effet Actif d'arme
                continue;
            if (statID == Constant.STATS_RESIST)
                continue;
            boolean isStatsInvalid = false;
            switch (statID) {
                case 110:
                case 139:
                case 605:
                case 614:
                case Constant.STATS_EXCHANGE_IN:
                    isStatsInvalid = true;
                    break;
                case 615:
                    itemStats.addOneStat(statID, Integer.parseInt(stats[3], 16));
                    break;
            }
            if (isStatsInvalid)
                continue;
            String jet = "";
            int value = 1;
            try {
                jet = stats[4];
                value = Formulas.getRandomJet(jet);
                if (useMax) {
                    try {
                        //on prend le jet max
                        int min = Integer.parseInt(stats[1], 16);
                        int max = 0;
                        try {
                            max = Integer.parseInt(stats[2], 16);
                        }
                        catch(Exception e){}

                        value = min;
                        if (max != 0)
                            value = max;
                    } catch (Exception e) {
                        e.printStackTrace();
                        value = Formulas.getRandomJet(jet);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            itemStats.addOneStat(statID, value);
        }
        return itemStats;
    }

    private ArrayList<SpellEffect> getEffectTemplate(String statsTemplate) {
        ArrayList<SpellEffect> Effets = new ArrayList<SpellEffect>();
        if (statsTemplate.equals(""))
            return Effets;

        String[] splitted = statsTemplate.split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            for (int a : Constant.ARMES_EFFECT_IDS) {
                if (a == statID) {
                    int id = statID;
                    String min = stats[1];
                    String max = stats[2];
                    String jet = stats[4];
                    String args = min + ";" + max + ";-1;-1;0;" + jet;
                    Effets.add(new SpellEffect(id, args, 0, -1));
                }
            }
            switch (statID) {
                case 110:
                case 139:
                case 605:
                case 614:
                    String min = stats[1];
                    String max = stats[2];
                    String jet = stats[4];
                    String args = min + ";" + max + ";-1;-1;0;" + jet;
                    Effets.add(new SpellEffect(statID, args, 0, -1));
                    break;
            }
        }
        return Effets;
    }

    public String parseItemTemplateStats() {
        if(this.money == -1) {
            return getId() + ";" + getStrTemplate() + (this.newPrice > 0 ? ";" + this.newPrice : "");
        }
        else{
            return getId() + ";" + getStrTemplate() + ";" + this.money + ";" + (this.newPrice > 0 ? ";" + this.newPrice : "") +";;";
        }
    }

    /*public void applyAction(Player player, Player target, int objectId, short cellId) {
        if (World.world.getGameObject(objectId) == null) return;
        if (World.world.getGameObject(objectId).getTemplate().getType() == 85) {
            if (!SoulStone.isInArenaMap(player.getCurMap().getId()))
                return;

            SoulStone soulStone = (SoulStone) World.world.getGameObject(objectId);

            player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), soulStone.parseGroupData(), "MiS=" + player.getId());
            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + World.world.getGameObject(objectId).getTemplate().getId());
            player.removeItem(objectId, 1, true, true);
        } else {
            for (ObjectAction action : this.getOnUseActions())
                action.apply(player, target, objectId, cellId);*/ // Previous
            // By Coding Mestre : [FIX] - Players can now use multiple consumables at once (shift + double click) Close #27
            public void applyAction(Player player, Player target, int objectId, short cellId, int quantity) {
                for (int i = 0; i < quantity; i++) {
                    if (World.world.getGameObject(objectId) == null) return;
                    if (World.world.getGameObject(objectId).getTemplate().getType() == 85) {
                        if (!SoulStone.isInArenaMap(player.getCurMap().getId()))
                            return;

                        SoulStone soulStone = (SoulStone) World.world.getGameObject(objectId);

                        player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), soulStone.parseGroupData(), "MiS=" + player.getId());
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + World.world.getGameObject(objectId).getTemplate().getId());
                        player.removeItem(objectId, 1, true, true);
                    } else {
                        for (ObjectAction action : this.getOnUseActions())
                            action.apply(player, target, objectId, cellId);
                    }

        }
    }

    public synchronized void newSold(int amount, int price) {
        long oldSold = getSold();
        sold += amount;
        avgPrice = (int) ((getAvgPrice() * oldSold + price) / getSold());
    }

    public GameObject createNewTonique(int posTonique,String StatsToadd) {
        Stats stats = generateNewStatsFromTemplate(StatsToadd, true);
        GameObject item = new GameObject(-1, getId(), 1, posTonique, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        return item;

    }

    public GameObject createNewToniqueEquilibrage(Stats stats) {
        GameObject item = new GameObject(-1, getId(), 1, Constant.ITEM_POS_TONIQUE_EQUILIBRAGE, stats, new ArrayList<>(), new HashMap<>(), new HashMap<>(), 0);
        return item;
    }
}