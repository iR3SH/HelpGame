package org.starloco.locos.object;

import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.Database;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.job.JobAction;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Logging;
import org.starloco.locos.object.entity.Fragment;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class GameObject {

    protected ObjectTemplate template;
    protected int quantity = 1;
    protected int position = Constant.ITEM_POS_NO_EQUIPED;
    protected int guid;
    protected int obvijevanPos;
    protected int obvijevanLook;
    protected int puit;
    private int mimibioteApparence = 0;
    private Stats Stats = new Stats();
    private ArrayList<SpellEffect> Effects = new ArrayList<>();
    private ArrayList<String> SortStats = new ArrayList<>();
    private Map<Integer, String> txtStats = new HashMap<>();
    private Map<Integer, Integer> SoulStats = new HashMap<>();

    public byte modification = -1;

    public GameObject(int Guid, int template, int qua, int pos, String strStats, int puit) {
        this.guid = Guid;
        this.template = World.world.getObjTemplate(template);
        this.quantity = qua;
        this.position = pos;
        this.puit = puit;

        Stats = new Stats();
        this.parseStringToStats(strStats, false, false);
        
        if(getTxtStat().get(Constant.STATS_MIMIBIOTE) != null)
        	this.setMimibioteApparence(Integer.parseInt(getTxtStat().get(Constant.STATS_MIMIBIOTE).split(";")[1], 16));
    }

    public GameObject(int Guid) {
        this.guid = Guid;
        this.template = World.world.getObjTemplate(8378);
        this.quantity = 1;
        this.position = -1;
        this.puit = 0;
    }

    public GameObject(int Guid, int template, int qua, int pos, Stats stats, ArrayList<SpellEffect> effects, Map<Integer, Integer> _SoulStat, Map<Integer, String> _txtStats, int puit) {
        this.guid = Guid;
        this.template = World.world.getObjTemplate(template);
        this.quantity = qua;
        this.position = pos;
        this.Stats = stats;
        this.Effects = effects;
        this.SoulStats = _SoulStat;
        this.txtStats = _txtStats;
        this.obvijevanPos = 0;
        this.obvijevanLook = 0;
        this.puit = puit;
        this.getStatsClass();
    }
    
    private void getStatsClass() {
    	for(final String s : this.getTemplate().getStrTemplate().split(",")) {
    		if(s.isEmpty()) continue;
    		String[] stats = s.split("#");
            final int statID = Integer.parseInt(stats[0], 16);
    		if (statID < 281 || statID > 294)
            	continue;
    		this.SortStats.add(s);
    	}
    }

    public static GameObject getCloneObjet(GameObject obj, int qua) {
        Map<Integer, Integer> maps = new LinkedHashMap<>();
        maps.putAll(obj.getStats().getMap());
        Stats newStats = new Stats(maps);

        GameObject ob = new GameObject(Database.getDynamics().getWorldEntityData().getNextObjectId(), obj.getTemplate().getId(), qua, Constant.ITEM_POS_NO_EQUIPED, newStats, new ArrayList<SpellEffect>(obj.getEffects()), new HashMap<Integer, Integer>(obj.getSoulStat()), new HashMap<Integer, String>(obj.getTxtStat()), obj.getPuit());
        ob.modification = 0;
        if(obj.isMimibiote())
        	ob.setMimibioteApparence(obj.getOATemplateApparence());
        return ob;
    }

    public int setId() {
        this.guid = Database.getDynamics().getWorldEntityData().getNextObjectId();
        return this.getGuid();
    }

    public int getPuit() {
        return this.puit;
    }

    public void setPuit(int puit) {
        this.puit = puit;
    }

    public int getObvijevanPos() {
        return obvijevanPos;
    }

    public void setObvijevanPos(int pos) {
        obvijevanPos = pos;
        this.setModification();
    }

    public int getObvijevanLook() {
        return obvijevanLook;
    }

    public void setObvijevanLook(int look) {
        obvijevanLook = look;
        this.setModification();
    }

    public void setModification() {
        if(this.modification == -1)
            this.modification = 1;
    }

    public void parseStringToStats(final String strStats, final boolean save, final boolean isForFm) {
        if(this.template != null & this.template.getId() == 7010) return;
        
        final StringBuilder statsToOrder = new StringBuilder(); // for fm
        boolean isFirst = true; // for fm
        
        String dj1 = "";
        if (!strStats.equalsIgnoreCase("")) {
            for (String split : strStats.split(",")) {
                try {
                    if (split.equalsIgnoreCase(""))
                        continue;
                    if (split.substring(0, 3).equalsIgnoreCase("325") && (this.getTemplate().getId() == 10207 || this.getTemplate().getId() == 10601)) {
                        txtStats.put(Constant.STATS_DATE, split.substring(3) + "");
                        continue;
                    }
                    if (split.substring(0, 3).equalsIgnoreCase("3dc")) {// Si c'est une rune de signature crée
                        txtStats.put(Constant.STATS_SIGNATURE, split.split("#")[4]);
                        continue;
                    }
                    if (split.substring(0, 3).equalsIgnoreCase("3d9")) {// Si c'est une rune de signature modifié
                        txtStats.put(Constant.STATS_CHANGE_BY, split.split("#")[4]);
                        continue;
                    }
                    if (split.substring(0, 3).equalsIgnoreCase("3d7")) {// Si c'est une rune de signature modifié
                        txtStats.put(Constant.STATS_EXCHANGE_IN, split.split("#")[4]);
                        continue;
                    }
                    if (split.substring(0, 3).equalsIgnoreCase("29d")) {// Si c'est une rune de signature modifié
                        txtStats.put(Constant.STATS_NIVEAU2, split.split("#")[4]);
                        continue;
                    }
                    if (split.substring(0, 3).equalsIgnoreCase("844")) {// Si c'est une rune de signature modifié
                        txtStats.put(Constant.STATS_BONUSADD, split.split("#")[4]);
                        continue;
                    }

                    String[] stats = split.split("#");
                    int id = Integer.parseInt(stats[0], 16);
                    
                    if(id == Constant.STATS_MIMIBIOTE) {
                    	final String[] datas = split.split("#");
                    	// 2 == Apparat
                    	// 3 == Id Template
                    	txtStats.put(id, datas[2]+";"+datas[3]);
                    	continue;
                    }
                    if (id == Constant.STATS_PETS_DATE
                            && this.getTemplate().getType() == Constant.ITEM_TYPE_CERTIFICAT_CHANIL) {
                        txtStats.put(id, split.substring(3));
                        continue;
                    }
                    if (id == Constant.STATS_CHANGE_BY || id == Constant.STATS_NAME_TRAQUE || id == Constant.STATS_OWNER_1) {
                        txtStats.put(id, stats[4]);
                        continue;
                    }
                    if (id == Constant.STATS_GRADE_TRAQUE || id == Constant.STATS_ALIGNEMENT_TRAQUE || id == Constant.STATS_NIVEAU_TRAQUE) {
                        txtStats.put(id, stats[3]);
                        continue;
                    }
                    if (id == Constant.STATS_PETS_SOUL) {
                        SoulStats.put(Integer.parseInt(stats[1], 16), Integer.parseInt(stats[3], 16)); // put(id_monstre, nombre_tué)
                        continue;
                    }
                    if (id == Constant.STATS_NAME_DJ) {
                        dj1 += (!dj1.isEmpty() ? "," : "") + stats[3];
                        txtStats.put(Constant.STATS_NAME_DJ, dj1);
                        continue;
                    }
                    if (id == 997 || id == 996) {
                        txtStats.put(id, stats[4]);
                        continue;
                    }
                    if (this.template != null && this.template.getId() == 77 && id == Constant.STATS_PETS_DATE) {
                        txtStats.put(id, split.substring(3));
                        continue;
                    }
                    if (id == Constant.STATS_DATE) {
                        txtStats.put(id, stats[3]);
                        continue;
                    }
                    
                    if (id >= 281 && id <= 294) {
                    	SortStats.add(split);
                    	continue; 
                    }
                    
                    //Si stats avec Texte (Signature, apartenance, etc)//FIXME
                    if (id != Constant.STATS_RESIST && (!stats[3].equals("") && (!stats[3].equals("0") || id == Constant.STATS_PETS_DATE || id == Constant.STATS_PETS_PDV || id == Constant.STATS_PETS_POIDS || id == Constant.STATS_PETS_EPO || id == Constant.STATS_PETS_REPAS))) {//Si le stats n'est pas vide et (n'est pas égale à 0 ou est de type familier)
                        if (!(this.getTemplate().getType() == Constant.ITEM_TYPE_CERTIFICAT_CHANIL && id == Constant.STATS_PETS_DATE)) {
                            txtStats.put(id, stats[3]);
                            continue;
                        }
                    }
                    if (id == Constant.STATS_RESIST && this.getTemplate() != null && this.getTemplate().getType() == 93) {
                        txtStats.put(id, stats[4]);
                        continue;
                    }
                    if (id == Constant.STATS_RESIST) {
                        txtStats.put(id, stats[4]);
                        continue;
                    }

                    boolean follow1 = true;
                    switch (id) {
                        case 110:
                        case 139:
                        case 605:
                        case 614:
                            String min = stats[1];
                            String max = stats[2];
                            String jet = stats[4];
                            String args = min + ";" + max + ";-1;-1;0;" + jet;
                            Effects.add(new SpellEffect(id, args, 0, -1, true));
                            follow1 = false;
                            break;
                    }
                    if (!follow1) {
                        continue;
                    }

                    boolean follow2 = true;
                    for (int a : Constant.ARMES_EFFECT_IDS) {
                        if (a == id) {
                            Effects.add(new SpellEffect(id, stats[1] + ";" + stats[2] + ";-1;-1;0;" + stats[4], 0, -1, true));
                            follow2 = false;
                        }
                    }
                    if (!follow2)
                        continue;//Si c'était un effet Actif d'arme ou une signature

                    if(!isForFm)
                    	Stats.addOneStat(id, Integer.parseInt(stats[1], 16));
                    else {
                    	if(!isFirst)
                    		statsToOrder.append(",");
                    	isFirst = false;
                    	statsToOrder.append(split);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        final String stringOrder = statsToOrder.toString();
        
        if(isForFm && !stringOrder.isEmpty())
        {
        	for(final String stat : Formulas.sortStatsByOrder(stringOrder).split(",")) {
        		final String[] split = stat.split("#");
        		final int id = Integer.parseInt(split[0], 16);
        		final int value = Integer.parseInt(split[1], 16);
        		Stats.addOneStat(id, value);
        	}
        }
        
        if(save)
            this.setModification();
    }

    
    
    public void addTxtStat(int i, String s) {
        txtStats.put(i, s);
        this.setModification();
    }
    
    public void removeTxtStat(int i) {
        txtStats.remove(i);
        this.setModification();
    }
    
    public void addOneStats(int i, int val) {
        Stats.addOneStat(i, val);
        this.setModification();
    }

    public String getTraquedName() {
        for (Entry<Integer, String> entry : txtStats.entrySet()) {
            if (Integer.toHexString(entry.getKey()).compareTo("3dd") == 0) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Stats getStats() {
        return Stats;
    }

    public void setStats(Stats SS) {
        Stats = SS;
        this.setModification();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0)
            quantity = 0;
        else if (quantity >= 100000)
            if (Logging.USE_LOG)
                Logging.getInstance().write("Object", "Faille : Objet guid : " + guid + " a dépassé 100 000 qua (" + quantity + ") avec comme template : " + template.getName() + " (" + template.getId() + ")");

        this.quantity = quantity;
        this.setModification();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.setModification();
        this.position = position;
    }

    public ObjectTemplate getTemplate() {
        return template;
    }

    public void setTemplate(int Tid) {
        this.setModification();
        this.template = World.world.getObjTemplate(Tid);
    }

    public int getGuid() {
        return guid;
    }

    public Map<Integer, Integer> getSoulStat() {
        return SoulStats;
    }

    public Map<Integer, String> getTxtStat() {
        return txtStats;
    }

    public void setExchangeIn(Player player) {

        this.setModification();
    }

    public void setMountStats(Player player, Mount mount) {
        if(mount == null)
            mount = new Mount(Constant.getMountColorByParchoTemplate(this.getTemplate().getId()), player.getId(), false);
        this.clearStats();
        this.getStats().addOneStat(995, - (mount.getId()));
        this.getTxtStat().put(996, player.getName());
        this.getTxtStat().put(997, mount.getName());
        this.setModification();
    }

    public void attachToPlayer(Player player) {
        this.getTxtStat().put(Constant.STATS_OWNER_1, player.getName());
        SocketManager.GAME_SEND_UPDATE_OBJECT_DISPLAY_PACKET(player, this);
        this.setModification();
    }

    public boolean isAttach() {
        boolean ok = this.getTxtStat().containsKey(Constant.STATS_OWNER_1);

        if(ok) {
            Player player = World.world.getPlayerByName(this.getTxtStat().get(Constant.STATS_OWNER_1));
            if(player != null) player.send("BN");
        }

        return ok;
    }

    public String parseItem() {
        String posi = position == Constant.ITEM_POS_NO_EQUIPED ? "" : Integer.toHexString(position)+"";
        return Integer.toHexString(guid) + "~"
                + Integer.toHexString(template.getId()) + "~"
                + Integer.toHexString(quantity) + "~" + posi + "~"
                + parseStatsString() + ";";
    }

    public String parseStatsString() {
        if (getTemplate().getType() == 83) //Si c'est une pierre d'âme vide
            return getTemplate().getStrTemplate();
        StringBuilder stats = new StringBuilder();
        boolean isFirst = true;

        for (String spell : SortStats) {
            if (!isFirst) {
                stats.append(",");
            }
            stats.append(spell);
            isFirst = false;
        }
        

        for (SpellEffect SE : Effects) {
            if (!isFirst)
                stats.append(",");
            String[] infos = SE.getArgs().split(";");

            try {
                switch (SE.getEffectID()) {
                    case 614:
                        stats.append(Integer.toHexString(SE.getEffectID())).append("#0#0#").append(infos[0]).append("#").append(infos[5]);
                        break;

                    default:
                        stats.append(Integer.toHexString(SE.getEffectID())).append("#").append(infos[0]).append("#").append(infos[1]).append("#").append(infos[1]).append("#").append(infos[5]);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            isFirst = false;
        }

        for (Entry<Integer, String> entry : txtStats.entrySet()) {
            if (!isFirst)
                stats.append(",");
            if (template.getType() == 77 || template.getType() == 90) {
                if (entry.getKey() == Constant.STATS_PETS_PDV)
                    stats.append(Integer.toHexString(entry.getKey())).append("#").append(entry.getValue()).append("#0#").append(entry.getValue());
                if (entry.getKey() == Constant.STATS_PETS_EPO)
                    stats.append(Integer.toHexString(entry.getKey())).append("#").append(entry.getValue()).append("#0#").append(entry.getValue());
                if (entry.getKey() == Constant.STATS_PETS_REPAS)
                    stats.append(Integer.toHexString(entry.getKey())).append("#").append(entry.getValue()).append("#0#").append(entry.getValue());
                if (entry.getKey() == Constant.STATS_PETS_POIDS) {
                    int corpu = 0;
                    int corpulence = 0;
                    String c = entry.getValue();
                    if (c != null && !c.equalsIgnoreCase("")) {
                        try {
                            corpulence = Integer.parseInt(c);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (corpulence > 0 || corpulence < 0)
                        corpu = 7;
                    stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(corpu)).append("#").append(corpulence > 0 ? corpu : 0).append("#").append(Integer.toHexString(corpu));
                }
                if (entry.getKey() == Constant.STATS_PETS_DATE
                        && template.getType() == 77) {
                    if (entry.getValue().contains("#"))
                        stats.append(Integer.toHexString(entry.getKey())).append(entry.getValue());
                    else
                        stats.append(Integer.toHexString(entry.getKey())).append(Formulas.convertToDate(Long.parseLong(entry.getValue())));
                }
            } else if(entry.getKey() == Constant.STATS_MIMIBIOTE) {
            	final String[] datas = entry.getValue().split(";");
            	stats.append(Integer.toHexString(Constant.STATS_MIMIBIOTE)).append("#0#").append(datas[0]).append("#").append(datas[1]);
            }
            
            else if (entry.getKey() == Constant.STATS_CHANGE_BY || entry.getKey() == Constant.STATS_NAME_TRAQUE || entry.getKey() == Constant.STATS_OWNER_1) {
                stats.append(Integer.toHexString(entry.getKey())).append("#0#0#0#").append(entry.getValue());
            } else if (entry.getKey() == Constant.STATS_GRADE_TRAQUE || entry.getKey() == Constant.STATS_ALIGNEMENT_TRAQUE || entry.getKey() == Constant.STATS_NIVEAU_TRAQUE) {
                stats.append(Integer.toHexString(entry.getKey())).append("#0#0#").append(entry.getValue()).append("#0");
            } else if (entry.getKey() == Constant.STATS_NAME_DJ) {
                if (entry.getValue().equals("0d0+0"))
                    continue;
                for (String i : entry.getValue().split(",")) {
                    stats.append(",").append(Integer.toHexString(entry.getKey())).append("#0#0#").append(i);
                }
                continue;
            } else if (entry.getKey() == Constant.STATS_DATE) {
                String item = entry.getValue();
                if (item.contains("#")) {
                    String date = item.split("#")[3];
                    if (date != null && !date.equalsIgnoreCase(""))
                        stats.append(Integer.toHexString(entry.getKey())).append(Formulas.convertToDate(Long.parseLong(date)));
                } else
                    stats.append(Integer.toHexString(entry.getKey())).append(Formulas.convertToDate(Long.parseLong(item)));
            } else if (entry.getKey() == Constant.CAPTURE_MONSTRE) {
                stats.append(Integer.toHexString(entry.getKey())).append("#0#0#").append(entry.getValue());
            } else if (entry.getKey() == Constant.STATS_PETS_PDV
                    || entry.getKey() == Constant.STATS_PETS_POIDS
                    || entry.getKey() == Constant.STATS_PETS_DATE
                    || entry.getKey() == Constant.STATS_PETS_REPAS) {
                PetEntry p = World.world.getPetsEntry(this.getGuid());
                if (p == null) {
                    if (entry.getKey() == Constant.STATS_PETS_PDV)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append("a").append("#0#a");
                    if (entry.getKey() == Constant.STATS_PETS_POIDS)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append("0").append("#0#0");
                    if (entry.getKey() == Constant.STATS_PETS_DATE)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append("0").append("#0#0");
                    if (entry.getKey() == Constant.STATS_PETS_REPAS)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append("0").append("#0#0");
                } else {
                    if (entry.getKey() == Constant.STATS_PETS_PDV)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(p.getPdv())).append("#0#").append(Integer.toHexString(p.getPdv()));
                    if (entry.getKey() == Constant.STATS_PETS_POIDS)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toString(p.parseCorpulence())).append("#").append(p.getCorpulence() > 0 ? p.parseCorpulence() : 0).append("#").append(Integer.toString(p.parseCorpulence()));
                    if (entry.getKey() == Constant.STATS_PETS_DATE)
                        stats.append(Integer.toHexString(entry.getKey())).append(p.parseLastEatDate());
                    if (entry.getKey() == Constant.STATS_PETS_REPAS)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append(entry.getValue()).append("#0#").append(entry.getValue());
                    if (p.getIsEupeoh()
                            && entry.getKey() == Constant.STATS_PETS_EPO)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(p.getIsEupeoh() ? 1 : 0)).append("#0#").append(Integer.toHexString(p.getIsEupeoh() ? 1 : 0));
                }
            } else if (entry.getKey() == Constant.STATS_RESIST
                    && getTemplate().getType() == 93) {
                stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(getResistanceMax(getTemplate().getStrTemplate()))).append("#").append(entry.getValue()).append("#").append(Integer.toHexString(getResistanceMax(getTemplate().getStrTemplate())));
            } else if (entry.getKey() == Constant.STATS_RESIST) {
                stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(getResistanceMax(getTemplate().getStrTemplate()))).append("#").append(entry.getValue()).append("#").append(Integer.toHexString(getResistanceMax(getTemplate().getStrTemplate())));
            } else if (entry.getKey() == Constant.STATS_NIVEAU2) {
                stats.append(Integer.toHexString(entry.getKey())).append("###").append(entry.getValue());
            } else if (entry.getKey() == Constant.STATS_EXCHANGE_IN) {
                stats.append(Integer.toHexString(entry.getKey())).append("#").append(entry.getValue());
            }
            else {
                stats.append(Integer.toHexString(entry.getKey())).append("#0#0#0#").append(entry.getValue());
            }
            isFirst = false;
        }

        for (Entry<Integer, Integer> entry : SoulStats.entrySet()) {
            if (!isFirst)
                stats.append(",");

            if (this.getTemplate().getType() == 18)
                stats.append(Integer.toHexString(Constant.STATS_PETS_SOUL)).append("#").append(Integer.toHexString(entry.getKey())).append("#").append("0").append("#").append(Integer.toHexString(entry.getValue()));
            if (entry.getKey() == Constant.STATS_NIVEAU)
                stats.append(Integer.toHexString(Constant.STATS_NIVEAU)).append("#").append(Integer.toHexString(entry.getKey())).append("#").append("0").append("#").append(Integer.toHexString(entry.getValue()));
            isFirst = false;
        }

        for (Entry<Integer, Integer> entry : Stats.getMap().entrySet()) {

            int statID = entry.getKey();

            if (!isFirst)
                stats.append(",");
            if(statID == 615) {
                stats.append(Integer.toHexString(statID)).append("#0#0#").append(Integer.toHexString(entry.getValue()));
            } else
            if ((statID == 970) || (statID == 971) || (statID == 972)
                    || (statID == 973) || (statID == 974)) {
                int jet = entry.getValue();
                if ((statID == 974) || (statID == 972) || (statID == 970))
                    stats.append(Integer.toHexString(statID)).append("#0#0#").append(Integer.toHexString(jet));
                else
                    stats.append(Integer.toHexString(statID)).append("#0#0#").append(jet);
                if (statID == 973)
                    setObvijevanPos(jet);
                if (statID == 972)
                    setObvijevanLook(jet);
            } else if (statID == Constant.STATS_TURN) {
                String jet = "0d0+" + entry.getValue();
                stats.append(Integer.toHexString(statID)).append("#");
                stats.append("0#0#").append(Integer.toHexString(entry.getValue())).append("#").append(jet);
            }
            else {
                String jet = "0d0+" + entry.getValue();
                stats.append(Integer.toHexString(statID)).append("#");
                stats.append(Integer.toHexString(entry.getValue().intValue())).append("#0#0#").append(jet);
            }
            isFirst = false;
        }
        return stats.toString();
    }

    public String parseStatsStringSansUserObvi() {
        if (getTemplate().getType() == 83) //Si c'est une pierre d'âme vide
            return getTemplate().getStrTemplate();

        StringBuilder stats = new StringBuilder();
        boolean isFirst = true;

        if (this instanceof Fragment) {
            Fragment fragment = (Fragment) this;
            for (Couple<Integer, Integer> couple : fragment.getRunes()) {
                stats.append((stats.toString().isEmpty() ? couple.first : ";"
                        + couple.first)).append(":").append(couple.second);
            }
            return stats.toString();
        }
        for (Entry<Integer, String> entry : txtStats.entrySet()) {
            if (!isFirst)
                stats.append(",");
            if (template.getType() == 77) {
                if (entry.getKey() == Constant.STATS_PETS_PDV)
                    stats.append(Integer.toHexString(entry.getKey())).append("#").append(entry.getValue()).append("#0#").append(entry.getValue());
                if (entry.getKey() == Constant.STATS_PETS_POIDS)
                    stats.append(Integer.toHexString(entry.getKey())).append("#").append(entry.getValue()).append("#").append(entry.getValue()).append("#").append(entry.getValue());
                if (entry.getKey() == Constant.STATS_PETS_DATE) {
                    if (entry.getValue().contains("#"))
                        stats.append(Integer.toHexString(entry.getKey())).append(entry.getValue());
                    else
                        stats.append(Integer.toHexString(entry.getKey())).append(Formulas.convertToDate(Long.parseLong(entry.getValue())));
                }
                //stats.append(Integer.toHexString(entry.getKey())).append(Formulas.convertToDate(Long.parseLong(entry.getValue())));
            } else if (entry.getKey() == Constant.STATS_DATE) {
                if (entry.getValue().contains("#"))
                    stats.append(Integer.toHexString(entry.getKey())).append(entry.getValue());
                else
                    stats.append(Integer.toHexString(entry.getKey())).append("#0#0#").append(Long.parseLong(entry.getValue()));
            } else if (entry.getKey() == Constant.STATS_CHANGE_BY || entry.getKey() == Constant.STATS_NAME_TRAQUE || entry.getKey() == Constant.STATS_OWNER_1) {
                stats.append(Integer.toHexString(entry.getKey())).append("#0#0#0#").append(entry.getValue());
            } else if (entry.getKey() == Constant.STATS_GRADE_TRAQUE || entry.getKey() == Constant.STATS_ALIGNEMENT_TRAQUE || entry.getKey() == Constant.STATS_NIVEAU_TRAQUE) {
                stats.append(Integer.toHexString(entry.getKey())).append("#0#0#").append(entry.getValue()).append("#0");
            } else if (entry.getKey() == Constant.STATS_NAME_DJ) {
                for (String i : entry.getValue().split(","))
                    stats.append(",").append(Integer.toHexString(entry.getKey())).append("#0#0#").append(i);
            } else if (entry.getKey() == Constant.CAPTURE_MONSTRE) {
                stats.append(Integer.toHexString(entry.getKey())).append("#0#0#").append(entry.getValue());
            } else if (entry.getKey() == Constant.STATS_PETS_PDV
                    || entry.getKey() == Constant.STATS_PETS_POIDS
                    || entry.getKey() == Constant.STATS_PETS_DATE) {
                PetEntry p = World.world.getPetsEntry(this.getGuid());
                if (p == null) {
                    if (entry.getKey() == Constant.STATS_PETS_PDV)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append("a").append("#0#a");
                    if (entry.getKey() == Constant.STATS_PETS_POIDS)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append("0").append("#0#0");
                    if (entry.getKey() == Constant.STATS_PETS_DATE)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append("0").append("#0#0");
                } else {
                    if (entry.getKey() == Constant.STATS_PETS_PDV)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(p.getPdv())).append("#0#").append(Integer.toHexString(p.getPdv()));
                    if (entry.getKey() == Constant.STATS_PETS_POIDS)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append(p.parseCorpulence()).append("#").append(p.getCorpulence() > 0 ? p.parseCorpulence() : 0).append("#").append(Integer.toString(p.parseCorpulence()));
                    if (entry.getKey() == Constant.STATS_PETS_DATE)
                        stats.append(Integer.toHexString(entry.getKey())).append(p.parseLastEatDate());
                    if (p.getIsEupeoh()
                            && entry.getKey() == Constant.STATS_PETS_EPO)
                        stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(p.getIsEupeoh() ? 1 : 0)).append("#0#").append(Integer.toHexString(p.getIsEupeoh() ? 1 : 0));
                }
            }else if(entry.getKey() == Constant.STATS_MIMIBIOTE) {
            	final String[] datas = entry.getValue().split(";");
            	stats.append(Integer.toHexString(Constant.STATS_MIMIBIOTE)).append("#0#").append(datas[0]).append("#").append(datas[1]);
            }
            else {
                stats.append(Integer.toHexString(entry.getKey())).append("#0#0#0#").append(entry.getValue());
            }
            isFirst = false;
        }
        /*
		 * if(isCertif && !db) return stats.toString();
		 */

        for (SpellEffect SE : Effects) {
            if (!isFirst)
                stats.append(",");

            String[] infos = SE.getArgs().split(";");
            try {
                stats.append(Integer.toHexString(SE.getEffectID())).append("#").append(infos[0]).append("#").append(infos[1]).append("#0#").append(infos[5]);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            isFirst = false;
        }
        for (Entry<Integer, Integer> entry : SoulStats.entrySet()) {
            if (!isFirst)
                stats.append(",");
            stats.append(Integer.toHexString(Constant.STATS_PETS_SOUL)).append("#").append(Integer.toHexString(entry.getKey())).append("#").append("0").append("#").append(Integer.toHexString(entry.getValue()));
            isFirst = false;
        }
        for (Entry<Integer, Integer> entry : Stats.getMap().entrySet()) {
            if (!isFirst)
                stats.append(",");

            if(entry.getKey() == 615) {
                stats.append(Integer.toHexString(entry.getKey())).append("#0#0#").append(Integer.toHexString(entry.getValue()));
            } else {
                String jet = "0d0+" + entry.getValue();
                stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(entry.getValue()));
                stats.append("#0#0#").append(jet);
            }
            isFirst = false;
        }
        for(final String sort : this.getSortStats()) {
        	if (!isFirst)
                stats.append(",");
        	stats.append(sort);
        	isFirst = false;
        }
        return stats.toString();
    }

    public String parseToSave() {
        return parseStatsStringSansUserObvi();
    }

    public String obvijevanOCO_Packet(int pos) {
        String strPos = String.valueOf(pos);
        if (pos == -1)
            strPos = "";
        String upPacket = "OCO";
        upPacket = upPacket + Integer.toHexString(getGuid()) + "~";
        upPacket = upPacket + Integer.toHexString(getTemplate().getId()) + "~";
        upPacket = upPacket + Integer.toHexString(getQuantity()) + "~";
        upPacket = upPacket + strPos + "~";
        upPacket = upPacket + parseStatsString();
        this.setModification();
        return upPacket;
    }

    public void obvijevanNourir(GameObject obj) {
        if (obj == null)
            return;
        for (Entry<Integer, Integer> entry : Stats.getMap().entrySet()) {
            if (entry.getKey().intValue() != 974) // on ne boost que la stat de l'expérience de l'obvi
                continue;
            if (entry.getValue().intValue() > 500) // si le boost a une valeur supérieure à 500 (irréaliste)
                return;
            entry.setValue(Integer.valueOf(entry.getValue().intValue()
                    + obj.getTemplate().getLevel() / 3));
        }
        this.setModification();
    }

    public void obvijevanChangeStat(int statID, int val) {
        for (Entry<Integer, Integer> entry : Stats.getMap().entrySet()) {
            if (entry.getKey().intValue() != statID)
                continue;
            entry.setValue(Integer.valueOf(val));
        }
        this.setModification();
    }

    public void removeAllObvijevanStats() {
        setObvijevanPos(0);
        org.starloco.locos.client.other.Stats StatsSansObvi = new Stats();
        for (Entry<Integer, Integer> entry : Stats.getMap().entrySet()) {
            int statID = entry.getKey().intValue();
            if ((statID == 970) || (statID == 971) || (statID == 972)
                    || (statID == 973) || (statID == 974))
                continue;
            StatsSansObvi.addOneStat(statID, entry.getValue().intValue());
        }
        Stats = StatsSansObvi;
        this.setModification();
    }

    public void removeAll_ExepteObvijevanStats() {
        setObvijevanPos(0);
        org.starloco.locos.client.other.Stats StatsSansObvi = new Stats();
        for (Entry<Integer, Integer> entry : Stats.getMap().entrySet()) {
            int statID = entry.getKey().intValue();
            if ((statID != 971) && (statID != 972) && (statID != 973)
                    && (statID != 974))
                continue;
            StatsSansObvi.addOneStat(statID, entry.getValue().intValue());
        }
        Stats = StatsSansObvi;
        this.setModification();
    }

    public String getObvijevanStatsOnly() {
        GameObject obj = getCloneObjet(this, 1);
        obj.removeAll_ExepteObvijevanStats();
        this.setModification();
        return obj.parseStatsStringSansUserObvi();
    }

	/* *********FM SYSTEM********* */

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
            boolean follow = true;

            for (int a : Constant.ARMES_EFFECT_IDS)
                //Si c'est un Effet Actif
                if (a == statID)
                    follow = false;
            if (!follow)
                continue;//Si c'était un effet Actif d'arme

            String jet = "";
            int value = 1;
            try {
                jet = stats[4];
                value = Formulas.getRandomJet(jet);
                if (useMax) {
                    try {
                        //on prend le jet max
                        int min = Integer.parseInt(stats[1], 16);
                        int max = Integer.parseInt(stats[2], 16);
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

    public ArrayList<SpellEffect> getEffects() {
        return Effects;
    }

    public ArrayList<SpellEffect> getCritEffects() {
        ArrayList<SpellEffect> effets = new ArrayList<SpellEffect>();
        for (SpellEffect SE : Effects) {
            try {
                boolean boost = true;
                for (int i : Constant.NO_BOOST_CC_IDS)
                    if (i == SE.getEffectID())
                        boost = false;
                String[] infos = SE.getArgs().split(";");
                if (!boost) {
                    effets.add(SE);
                    continue;
                }
                int min = Integer.parseInt(infos[0], 16)
                        + (boost ? template.getBonusCC() : 0);
                int max = Integer.parseInt(infos[1], 16)
                        + (boost ? template.getBonusCC() : 0);
                String jet = "1d" + (max - min + 1) + "+" + (min - 1);
                //exCode: String newArgs = Integer.toHexString(min)+";"+Integer.toHexString(max)+";-1;-1;0;"+jet;
                //osef du minMax, vu qu'on se sert du jet pour calculer les dégats
                String newArgs = "0;0;0;-1;0;" + jet;
                effets.add(new SpellEffect(SE.getEffectID(), newArgs, 0, -1, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return effets;
    }

    public void clearStats() {
        //On vide l'item de tous ces effets
        Stats.getMap().clear();
        Effects.clear();
        txtStats.clear();
        SortStats.clear();
        SoulStats.clear();
        this.setModification();
    }

    public void refreshStatsObjet(String newsStats) {
        parseStringToStats(newsStats, true, true);
        this.setModification();
    }

    public int getResistance(String statsTemplate) {
        int Resistance = 0;

        String[] splitted = statsTemplate.split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            if (Integer.parseInt(stats[0], 16) == Constant.STATS_RESIST) {
                Resistance = Integer.parseInt(stats[2], 16);
            }
        }
        return Resistance;
    }

    public int getResistanceMax(String statsTemplate) {
        int ResistanceMax = 0;

        String[] splitted = statsTemplate.split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            if (Integer.parseInt(stats[0], 16) == Constant.STATS_RESIST) {
                ResistanceMax = Integer.parseInt(stats[1], 16);
            }
        }
        return ResistanceMax;
    }

    public int getRandomValue(String statsTemplate, int statsId) {
        if (statsTemplate.equals(""))
            return 0;

        String[] splitted = statsTemplate.split(",");
        int value = 0;
        for (String s : splitted) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            if (statID != statsId)
                continue;
            String jet;
            try {
                jet = stats[4];
                value = Formulas.getRandomJet(jet);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        return value;
    }

    public String parseFMStatsString(String statsstr, GameObject obj, int add,
                                     boolean negatif) {
        String stats = "";
        boolean isFirst = true;
        
        for(final String sort : obj.getSortStats()) {
        	if (!isFirst)
                stats += ",";
        	
        	stats += sort;
        	
        	isFirst = false;
        }
        
        for (SpellEffect SE : obj.Effects) {
            if (!isFirst)
                stats += ",";

            String[] infos = SE.getArgs().split(";");
            try {
                stats += Integer.toHexString(SE.getEffectID()) + "#" + infos[0]
                        + "#" + infos[1] + "#0#" + infos[5];
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            isFirst = false;
        }
        


        for (Entry<Integer, String> entry : obj.txtStats.entrySet()) {
            if (!isFirst)
                stats += ",";
            if(entry.getKey() == Constant.STATS_MIMIBIOTE) {
            	
            	final String[] datas = entry.getValue().split(";");
            	stats += Integer.toHexString(Constant.STATS_MIMIBIOTE);
            	stats += "#0#";
            	stats += datas[0] + "#" + datas[1];
            	
            }else {
            	
            	stats += Integer.toHexString(entry.getKey()) + "#0#0#0#"
            			+ entry.getValue();
            	
            }
            isFirst = false;
        }

        for (Entry<Integer, Integer> entry : obj.Stats.getMap().entrySet()) {
            if (!isFirst)
                stats += ",";
            if (Integer.toHexString(entry.getKey()).compareTo(statsstr) == 0) {
                int newstats = 0;
                if (negatif) {
                    newstats = entry.getValue() - add;
                    if (newstats < 1)
                        continue;
                } else {
                    newstats = entry.getValue() + add;
                }
                String jet = "0d0+" + newstats;
                stats += Integer.toHexString(entry.getKey()) + "#"
                        + Integer.toHexString(entry.getValue() + add) + "#0#0#"
                        + jet;
            } else {
                String jet = "0d0+" + entry.getValue();
                stats += Integer.toHexString(entry.getKey()) + "#"
                        + Integer.toHexString(entry.getValue()) + "#0#0#" + jet;
            }
            isFirst = false;
        }

        return stats;
    }
    
    
    
    public String parseFMStatsString(String statsstr, int add,
            boolean negatif) {
		String stats = "";
		boolean isFirst = true;
		for (SpellEffect SE : this.Effects) {
		if (!isFirst)
		stats += ",";
		
		String[] infos = SE.getArgs().split(";");
		try {
		stats += Integer.toHexString(SE.getEffectID()) + "#" + infos[0]
		+ "#" + infos[1] + "#0#" + infos[5];
		} catch (Exception e) {
		e.printStackTrace();
		continue;
		}
		isFirst = false;
		}
		
		for (Entry<Integer, Integer> entry : this.Stats.getMap().entrySet()) {
		if (!isFirst)
		stats += ",";
		
		//World.world.logger.trace( ""+Integer.toHexString(entry.getKey())+" "+statsstr +" "+ add+ " "+ entry.getValue());            
		if (Integer.toHexString(entry.getKey()).compareTo(statsstr) == 0) {
		int newstats = 0;
		int newstats2 = 0;
		//World.world.logger.trace( "Negative ? " + negatif);
		if (negatif) {
		newstats = entry.getValue() - add;
		if (add < 1)
		continue;
		} else {
		newstats = entry.getValue() + add;
		}
		String jet = "0d0+" + newstats;
		stats += Integer.toHexString(entry.getKey()) + "#"
		+ Integer.toHexString(newstats) + "#0#0#"
		+ jet;
		} else {
		
		String jet = "0d0+" + entry.getValue();
		stats += Integer.toHexString(entry.getKey()) + "#"
		+ Integer.toHexString(entry.getValue()) + "#0#0#" + jet;
		
		//World.world.logger.trace( ""+entry.getKey()+" "+statsstr +" "+ add+ " "+ entry.getValue());
		}
		isFirst = false;
		}
		
		for (Entry<Integer, String> entry : this.txtStats.entrySet()) {
		if (!isFirst)
		stats += ",";
		stats += Integer.toHexString(entry.getKey()) + "#0#0#0#"
		+ entry.getValue();
		isFirst = false;
		}
		// World.world.logger.trace( ""+stats);
		return stats;
		}
    
    
    
    
    

    public String parseStringStatsEC_FM(GameObject obj, double poid, int carac) { // Ca c'est pas mal mais peut etre vori le jet perdu en soit
        String stats = "";
        boolean first = false;
        double perte = 0.0;
        

        for (final String sort : obj.getSortStats()) {
            if (first)
                stats += ",";
            stats += sort;
            first = true;
        }
        
        for (SpellEffect EH : obj.Effects) {
            if (first)
                stats += ",";
            String[] infos = EH.getArgs().split(";");
            try {
                stats += Integer.toHexString(EH.getEffectID()) + "#" + infos[0]
                        + "#" + infos[1] + "#0#" + infos[5];
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            first = true;
        }
        

        for (Entry<Integer, String> entry : obj.txtStats.entrySet()) {
            if (first)
                stats += ",";
            	if(entry.getKey() == Constant.STATS_MIMIBIOTE) {
            	
	            	final String[] datas = entry.getValue().split(";");
	            	stats += Integer.toHexString(Constant.STATS_MIMIBIOTE);
	            	stats += "#0#";
	            	stats += datas[0] + "#" + datas[1];
            	
            	}else {
            	
	            	stats += Integer.toHexString((entry.getKey())) + "#0#0#0#"
	            			+ entry.getValue();
            	}
            first = true;
        }
        java.util.Map<Integer, Integer> statsObj = new java.util.HashMap<Integer, Integer>(obj.Stats.getMap());
        java.util.ArrayList<Integer> keys = new ArrayList<Integer>(obj.Stats.getMap().keySet());
        Collections.shuffle(keys);
        int p = 0;
        int key = 0;
        if (keys.size() > 1) {
            for (Integer i : keys) // On cherche un OverFM
            {
            	//if(i == 121){
            	//	i = 112;
            	//}
                int value = statsObj.get(i);
                if (this.isOverFm(i, value)) {
                    key = i;
                    //System.out.println(value + " On est over" + i);
                    break;
                }
                p++;
            }
            if (key > 0) // On place l'OverFm en tête de liste pour être niqué
            {
                keys.remove(p);
                keys.add(p, keys.get(0));
                keys.remove(0);
                keys.add(0, key);
            }
        }
        for (Integer i : keys) {
        	//if(i == 121){
         	//	i = 112;
         	//}
        	 //System.out.println(i + " On boucle ??");        	 
            int newstats = 0;
            int statID = i;
            int value = statsObj.get(i);
            
            
            //System.out.println(value + " La valeur de la stats");
            if (perte > poid || statID == carac) {
                newstats = value;
                //System.out.println(newstats  + "On laisse parce qu'on a deja perdu assez");
            } else if ((statID == 152) || (statID == 154) || (statID == 155)
                    || (statID == 157) || (statID == 116) || (statID == 153)) {
                
            	//System.out.println( " des stats négatif qu'on remet au max");
            	float a = (float) (value * poid / 100.0D);
            	
                if (a < 1.0F)
                    a = 1.0F;
                //System.out.println(a  + " une valeur ");
                float chute = value + a;
                //System.out.println(chute + " la chute est de");
                newstats = (int) Math.floor(chute);
                
                if (newstats > JobAction.getBaseMaxJet(obj.getTemplate().getId(), Integer.toHexString(i)))
                    newstats = JobAction.getBaseMaxJet(obj.getTemplate().getId(), Integer.toHexString(i));

            } else {
                if ((statID == 127) || (statID == 101))
                    continue;

                float chute;
                
                if (this.isOverFm(statID, value)){ // Gros kick dans la gueule de l'over FM
                    chute = (float) (value - value
                            * (poid - (int) Math.floor(perte)) * 2 / 100.0D);
                    //System.out.println( " On est over donc chute " + chute );
                }
                else{
                    chute = (float) (value - value
                            * (poid - (int) Math.floor(perte)) / 100.0D);
                    
                    //System.out.println( " On est normal donc chute " + chute );
                }
                if ((chute / (float) value) < 0.75){
                
                    chute = ((float) value) * 0.75F; // On ne peut pas perdre plus de 25% d'une stat d'un coup
                    //System.out.println( " on a trop chuter donc chute " + chute );
                }
                double chutePwr = (value - chute)
                        * JobAction.getPwrPerEffet(statID);
                //System.out.println( " chute final" + chutePwr );
                //int chutePwrFixe = (int) Math.floor(chutePwr);

                perte += chutePwr;

				/*
				 * if (obj.getPuit() > 0 && chutePwrFixe <= obj.getPuit()) //
				 * S'il y a un puit positif, on annule la baisse { perte +=
				 * chutePwr; chute = value; // On réinitialise
				 * obj.setPuit(obj.getPuit() - chutePwrFixe); // On descend le
				 * puit } else if (obj.getPuit() > 0) // Si le puit est positif,
				 * mais pas suffisant pour annuler { double pwr =
				 * obj.getPuit()/World.getPwrPerEffet(statID); // On calcule
				 * l'annulation possible de la chute chute += (int)
				 * Math.floor(pwr); // On l'a réajoute perte +=
				 * (value-chute)*World.getPwrPerEffet(statID); obj.setPuit(0);
				 * // On fixe le puit à 0 } else { perte += chutePwr; }
				 */

                newstats = (int) Math.floor(chute);
                
                //System.out.println( " Nouvelle valur stats " + newstats );
            }
            if (newstats < 1)
                continue;
            String jet = "0d0+" + newstats;
            if (first)
                stats += ",";
            stats += Integer.toHexString(statID) + "#"
                    + Integer.toHexString(newstats) + "#0#0#" + jet;
            first = true;
        }
        return stats;
    }

    public boolean isOverFm(int stat, int val) {
        boolean trouve = false;
        String statsTemplate = "";
        statsTemplate = this.template.getStrTemplate();
        if (statsTemplate == null || statsTemplate.isEmpty())
            return false;
        String[] split = statsTemplate.split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            if (statID != stat)
                continue;

            trouve = true;
            boolean sig = true;
            for (int a : Constant.ARMES_EFFECT_IDS)
                if (a == statID)
                    sig = false;
            if (!sig)
                continue;
            String jet = "";
            int value = 1;
            try {
                jet = stats[4];
                value = Formulas.getRandomJet(jet);
                try {
                    int min = Integer.parseInt(stats[1], 16);
                    int max = Integer.parseInt(stats[2], 16);
                    value = min;
                    if (max != 0)
                        value = max;
                } catch (Exception e) {
                    e.printStackTrace();
                    value = Formulas.getRandomJet(jet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (val > value)
                return true;
        }
        return !trouve;
    }
    
    public static byte viewActualStatsItem(GameObject obj, String stats)//retourne vrai si le stats est actuellement sur l'item
    {
        if (!obj.parseStatsString().isEmpty()) {
            for (Entry<Integer, Integer> entry : obj.getStats().getMap().entrySet()) {
                if (Integer.toHexString(entry.getKey()).compareTo(stats) > 0)//Effets inutiles
                {
                    if (Integer.toHexString(entry.getKey()).compareTo("98") == 0
                            && stats.compareTo("7b") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9a") == 0
                            && stats.compareTo("77") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9b") == 0
                            && stats.compareTo("7e") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9d") == 0
                            && stats.compareTo("76") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("74") == 0
                            && stats.compareTo("75") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("99") == 0
                            && stats.compareTo("7d") == 0) {
                        return 2;
                    } else {
                    }
                } else if (Integer.toHexString(entry.getKey()).compareTo(stats) == 0)//L'effet existe bien !
                {
                    return 1;
                }
            }
            return 0;
        } else {
            return 0;
        }
    }
    
    
    
    public void setNewStats(String statsObjectFm,int statsAdd) {
    	int ItemCurrentStats = viewActualStatsItem(this, statsObjectFm);
    	String statsStr = "";	
    	String statStringObj = this.parseStatsString() ;
        boolean negative = false;
        if (ItemCurrentStats == 2) {
            if (statsObjectFm.compareTo("7b") == 0) {
                statsObjectFm = "98";
                negative = true;
            }
            if (statsObjectFm.compareTo("77") == 0) {
                statsObjectFm = "9a";
                negative = true;
            }
            if (statsObjectFm.compareTo("7e") == 0) {
                statsObjectFm = "9b";
                negative = true;
            }
            if (statsObjectFm.compareTo("76") == 0) {
                statsObjectFm = "9d";
                negative = true;
            }
            if (statsObjectFm.compareTo("7c") == 0) {
                statsObjectFm = "9c";
                negative = true;
            }
            if (statsObjectFm.compareTo("7d") == 0) {
                statsObjectFm = "99";
                negative = true;
            }
        }
	 if (ItemCurrentStats == 1 || ItemCurrentStats == 2) {
		 	//World.world.logger.trace( "On est la normal");
            if (statStringObj.isEmpty()) {
                statsStr = statsObjectFm + "#"
                        + Integer.toHexString(statsAdd) + "#0#0#0d0+"
                        + statsAdd;
                this.clearStats();                   
                this.refreshStatsObjet(statsStr);
                
            } else {
                statsStr = this.parseFMStatsString(statsObjectFm, this, statsAdd, negative);
                //World.world.logger.trace( "Etrange "+statsStr);
                this.clearStats();               
                this.refreshStatsObjet(statsStr);
                //String test = this.parseStatsString() ;
               // World.world.logger.trace( "Test "+test);
            }
        } else {
        	//World.world.logger.trace( "Bizarrement on est la");
            if (statStringObj.isEmpty()) {
                statsStr = statsObjectFm + "#"
                        + Integer.toHexString(statsAdd) + "#0#0#0d0+"
                        + statsAdd;
                this.clearStats();
                this.refreshStatsObjet(statsStr);
            } else {
                statsStr = this.parseFMStatsString(statsObjectFm, this, statsAdd, negative)// Ajouté this
                        + ","
                        + statsObjectFm
                        + "#"
                        + Integer.toHexString(statsAdd)
                        + "#0#0#0d0+"
                        + statsAdd;
                this.clearStats();
                this.refreshStatsObjet(statsStr);
            }
        }
	 this.setModification();
    }
    
    
    public int getOATemplateApparence() {
    	if(this.isMimibiote())
    		return this.mimibioteApparence;
    	return this.getTemplate().getId();
    }
    
    public boolean isMimibiote() {
    	return this.mimibioteApparence != 0;
    }
    
    public void setMimibioteApparence(final int idTemplate) {
    	this.mimibioteApparence = idTemplate;
    }
    
    public ArrayList<String> getSortStats(){
    	return this.SortStats;
    }
    
    
}
