package org.starloco.locos.job;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.action.GameAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.kernel.Logging;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.entity.InteractiveObject;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import java.util.*;
import java.util.Map.Entry;

public class JobAction {

    public Map<Integer, Integer> ingredients = new TreeMap<>(), lastCraft = new TreeMap<>();
    public Player player;
    public String data = "";
    public boolean broke = false;
    public boolean broken = false;
    public boolean isRepeat = false;
    private int id;
    private int min = 1;
    private int max = 1;
    private boolean isCraft;
    private int chan = 100;
    private int time = 0;
    private int xpWin = 0;
    private JobStat SM;
    private JobCraft jobCraft;
    public JobCraft oldJobCraft;

    public JobAction(int sk, int min, int max, boolean craft, int arg, int xpWin) {
        this.id = sk;
        this.min = min;
        this.max = max;
        this.isCraft = craft;
        this.xpWin = xpWin;
        if (craft) this.chan = arg;
        else this.time = arg;
    }

    public int getId() {
        return this.id;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public boolean isCraft() {
        return this.isCraft;
    }

    public int getChance() {
        return this.chan;
    }

    public int getTime() {
        return this.time;
    }

    public int getXpWin() {
        return this.xpWin;
    }

    public JobStat getJobStat() {
        return this.SM;
    }

    public JobCraft getJobCraft() {
        return this.jobCraft;
    }

    public void setJobCraft(JobCraft jobCraft) {
        this.jobCraft = jobCraft;
    }

    public void startCraft(Player P) {
        this.jobCraft = new JobCraft(this, P);
    }

    public void startAction(Player P, InteractiveObject IO, GameAction GA, GameCase cell, JobStat SM) {
        this.SM = SM;
        this.player = P;

        if (P.getObjetByPos(Constant.ITEM_POS_ARME) != null && SM.getTemplate().getId() == 36) {
            if (World.world.getMetier(36).isValidTool(P.getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getId())) {
                int dist = PathFinding.getDistanceBetween(P.getCurMap(), P.getCurCell().getId(), cell.getId());
                int distItem = JobConstant.getDistCanne(P.getObjetByPos(Constant.ITEM_POS_ARME).getTemplate().getId());
                if (distItem < dist) {
                    SocketManager.GAME_SEND_MESSAGE(P, "Vous étes trop loin pour pouvoir pécher ce poisson !");
                    SocketManager.GAME_SEND_GA_PACKET(P.getGameClient(), "", "0", "", "");
                    P.setExchangeAction(null);
                    P.setDoAction(false);
                    return;
                }
            }
        }
        if (!this.isCraft) {
            P.getGameClient().action = System.currentTimeMillis();
            IO.setInteractive(false);
            IO.setState(JobConstant.IOBJECT_STATE_EMPTYING);
            SocketManager.GAME_SEND_GA_PACKET_TO_MAP(P.getCurMap(), "" + GA.id, 501, P.getId() + "", cell.getId() + "," + this.time);
            SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.getCurMap(), cell);
        } else {
            P.setAway(true);
            IO.setState(JobConstant.IOBJECT_STATE_EMPTYING);
            P.setExchangeAction(new ExchangeAction<>(ExchangeAction.CRAFTING, this));
            SocketManager.GAME_SEND_ECK_PACKET(P, 3, this.min + ";" + this.id);
            SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.getCurMap(), cell);
        }
    }

    public void startAction(Player P, InteractiveObject IO, GameAction GA, GameCase cell) {
        this.player = P;
        P.setAway(true);
        IO.setState(JobConstant.IOBJECT_STATE_EMPTYING);//FIXME trouver la bonne valeur
        P.setExchangeAction(new ExchangeAction<>(ExchangeAction.CRAFTING, this));
        SocketManager.GAME_SEND_ECK_PACKET(P, 3, this.min + ";" + this.id);//this.min => Nbr de Case de l'interface
        SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.getCurMap(), cell);
    }

    public void endAction(Player player, InteractiveObject IO, GameAction GA, GameCase cell) {
        if(!this.isCraft && player.getGameClient().action != 0) {
            if(System.currentTimeMillis() - player.getGameClient().action < this.time - 500) {
                player.getGameClient().kick();//FIXME: Ajouté le ban si aucune plainte.
                return;
            }
        }

        player.setDoAction(false);
        if (IO == null)
            return;
        if (!this.isCraft) {
            IO.setState(3);
            IO.desactive();
            SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(player.getCurMap(), cell);
            int qua = (this.max > this.min ? Formulas.getRandomValue(this.min, this.max) : this.min);

            if (SM.getTemplate().getId() == 36) {
                if (qua > 0)
                    SM.addXp(player, (long) (this.getXpWin() * Config.getInstance().rateJob * World.world.getConquestBonus(player)));
            } else
                SM.addXp(player, (long) (this.getXpWin() * Config.getInstance().rateJob  * World.world.getConquestBonus(player)));

            int tID = JobConstant.getObjectByJobSkill(this.id);

            if (SM.getTemplate().getId() == 36 && qua > 0) {
                if (Formulas.getRandomValue(1, 1000) <= 2) {
                    int _tID = JobConstant.getPoissonRare(tID);
                    if (_tID != -1) {
                        ObjectTemplate _T = World.world.getObjTemplate(_tID);
                        if (_T != null) {
                            GameObject _O = _T.createNewItem(qua, true);
                            if (player.addObjet(_O, true))
                                World.world.addGameObject(_O, true);
                        }
                    }
                }
            }


            ObjectTemplate T = World.world.getObjTemplate(tID);
            if (T == null)
                return;
            GameObject O = T.createNewItem(qua, true);

            if (player.addObjet(O, true))
                World.world.addGameObject(O, true);
            SocketManager.GAME_SEND_IQ_PACKET(player, player.getId(), qua);
            SocketManager.GAME_SEND_Ow_PACKET(player);

            if (player.getMetierBySkill(this.id).get_lvl() >= 30 && Formulas.getRandomValue(1, 40) > 39) {
                for (int[] protector : JobConstant.JOB_PROTECTORS) {
                    if (tID == protector[1]) {
                        int monsterLvl = JobConstant.getProtectorLvl(player.getLevel());
                        player.getCurMap().startFightVersusProtectors(player, new Monster.MobGroup(player.getCurMap().nextObjectId, cell.getId(), protector[0] + "," + monsterLvl + "," + monsterLvl));
                        break;
                    }
                }
            }
        }
        player.setAway(false);
    }

    public synchronized void craft(boolean isRepeat, int repeat) {
        if (!this.isCraft) return;

        if (this.id == 1 || this.id == 113 || this.id == 115 || this.id == 116 || this.id == 117 || this.id == 118 || this.id == 119 || this.id == 120 || (this.id >= 163 && this.id <= 169)) {
            this.craftMaging(isRepeat, repeat);
            return;
        }

        //this.ingredients.putAll(this.lastCraft);
        if(isRepeat){
            this.ingredients.putAll(this.lastCraft);
        } // Coding Mestre - [FIX] - It is now possible to craft multiple items without having the close...


        Map<Integer, Integer> items = new HashMap<>();
        //on retire les items mis en ingrédients
        for (Entry<Integer, Integer> e : this.ingredients.entrySet()) {
            if (!this.player.hasItemGuid(e.getKey())) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                return;
            }

            GameObject obj = World.world.getGameObject(e.getKey());

            if (obj == null) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                return;
            }
            if (obj.getQuantity() < e.getValue()) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                return;
            }

            int newQua = obj.getQuantity() - e.getValue();
            if (newQua < 0) return;
            
            // By Coding Mestre - [FIX] - Player's UI is now properly updated when crafting several items Close #26
            if(isRepeat && repeat > 0)
                SocketManager.SEND_EMK_MOVE_ITEM(this.player.getGameClient(), 'O', "+", e.getKey().toString().concat("|").concat(e.getValue().toString()));


            if (newQua == 0) {
                this.player.removeItem(e.getKey());
                World.world.removeGameObject(e.getKey());
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, e.getKey());
            } else {
                obj.setQuantity(newQua);
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, obj);
            }

            items.put(obj.getTemplate().getId(), e.getValue());
        }

        boolean signed = false;

        if (items.containsKey(7508)) {
            signed = true;
            items.remove(7508);
        }

        SocketManager.GAME_SEND_Ow_PACKET(this.player);

        boolean isUnjobSkill = this.getJobStat() == null;

        if (!isUnjobSkill) {
            JobStat SM = this.player.getMetierBySkill(this.id);
            int templateId = World.world.getObjectByIngredientForJob(SM.getTemplate().getListBySkill(this.id), items);
            //Recette non existante ou pas adapté au métier
            if (templateId == -1 || !SM.getTemplate().canCraft(this.id, templateId)) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");
                this.ingredients.clear();
                return;
            }

            int chan = JobConstant.getChanceByNbrCaseByLvl(SM.get_lvl(), this.ingredients.size());
            int jet = Formulas.getRandomValue(1, 100);
            boolean success = chan >= jet;

            switch (this.id) {
                case 109:
                    success = true;
                    break;
            }

            if (Logging.USE_LOG)
                Logging.getInstance().write("Craft", this.player.getName() + " é crafter avec " + (success ? "SUCCES" : "ECHEC") + " l'item " + templateId + " (" + World.world.getObjTemplate(templateId).getName() + ")");
            if (!success) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EF");
                SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-" + templateId);
                SocketManager.GAME_SEND_Im_PACKET(this.player, "0118");
            } else {
                GameObject newObj = World.world.getObjTemplate(templateId).createNewItem(1, false);
                if(this.player.addObjet(newObj, true))
                	World.world.addGameObject(newObj, true);
                SocketManager.GAME_SEND_Ow_PACKET(this.player);

                if (signed || templateId >= 15000) newObj.addTxtStat(988, this.player.getName());


                SocketManager.GAME_SEND_Em_PACKET(this.player, "KO+" + newObj.getGuid() + "|1|" + templateId + "|" + newObj.parseStatsString().replace(";", "#"));
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + templateId);
                SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + templateId);
            }

            int winXP = 0;
            if (success)
                winXP = Formulas.calculXpWinCraft(SM.get_lvl(), this.ingredients.size()) * Config.getInstance().rateJob;
            else if (!SM.getTemplate().isMaging())
                winXP = Formulas.calculXpWinCraft(SM.get_lvl(), this.ingredients.size()) * Config.getInstance().rateJob;

            if (winXP > 0) {
                SM.addXp(this.player, winXP);
                ArrayList<JobStat> SMs = new ArrayList<>();
                SMs.add(SM);
                SocketManager.GAME_SEND_JX_PACKET(this.player, SMs);
            }
        } else {
            int templateId = World.world.getObjectByIngredientForJob(World.world.getMetier(this.id).getListBySkill(this.id), items);

            if (templateId == -1 || !World.world.getMetier(this.id).canCraft(this.id, templateId)) {
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");
                this.ingredients.clear();
                return;
            }

            GameObject newObj = World.world.getObjTemplate(templateId).createNewItem(1, false);
            if(this.player.addObjet(newObj, true))
            	World.world.addGameObject(newObj, true);

            if (signed || templateId >= 15000) newObj.addTxtStat(988, this.player.getName());

            SocketManager.GAME_SEND_Ow_PACKET(this.player);
            SocketManager.GAME_SEND_Em_PACKET(this.player, "KO+" + newObj.getGuid() + "|1|" + templateId + "|" + newObj.parseStatsString().replace(";", "#"));
            SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + templateId);
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + templateId);
        }
        this.lastCraft.clear();
        this.lastCraft.putAll(this.ingredients);
        this.ingredients.clear();

        if(!isRepeat) {
            this.oldJobCraft = this.jobCraft;
            this.jobCraft = null;
        }
    }

    public boolean craftPublicMode(Player crafter, Player receiver, Map<Player, ArrayList<Couple<Integer, Integer>>> list) {
        if (!this.isCraft) return false;

        this.player = crafter;
        boolean signed = false;

        Map<Integer, Integer> items = new HashMap<>();

        for (Entry<Player, ArrayList<Couple<Integer, Integer>>> entry : list.entrySet()) {
            Player player = entry.getKey();
            Map<Integer, Integer> playerItems = new HashMap<>();

            for (Couple<Integer, Integer> couple : entry.getValue())
                playerItems.put(couple.first, couple.second);

            for (Entry<Integer, Integer> e : playerItems.entrySet()) {
                if (!player.hasItemGuid(e.getKey())) {
                    SocketManager.GAME_SEND_Ec_PACKET(player, "EI");
                    SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                    return false;
                }

                GameObject gameObject = World.world.getGameObject(e.getKey());
                if (gameObject == null) {
                    SocketManager.GAME_SEND_Ec_PACKET(player, "EI");
                    SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                    return false;
                }
                if (gameObject.getQuantity() < e.getValue()) {
                    SocketManager.GAME_SEND_Ec_PACKET(player, "EI");
                    SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
                    return false;
                }

                int newQua = gameObject.getQuantity() - e.getValue();

                if (newQua < 0)
                    return false;

                if (newQua == 0) {
                    player.removeItem(e.getKey());
                    World.world.removeGameObject(e.getKey());
                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, e.getKey());
                } else {
                    gameObject.setQuantity(newQua);
                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(player, gameObject);
                }

                items.put(gameObject.getTemplate().getId(), e.getValue());
            }
        }

        SocketManager.GAME_SEND_Ow_PACKET(this.player);
        JobStat SM = this.player.getMetierBySkill(this.id);

        //Rune de signature
        if (items.containsKey(7508))
            if (SM.get_lvl() == 100)
                signed = true;
        items.remove(7508);

        int template = World.world.getObjectByIngredientForJob(SM.getTemplate().getListBySkill(this.id), items);

        if (template == -1 || !SM.getTemplate().canCraft(this.id, template)) {
            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI");
            receiver.send("EcEI");
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");
            items.clear();
            return false;
        }

        boolean success = JobConstant.getChanceByNbrCaseByLvl(SM.get_lvl(), items.size()) >= Formulas.getRandomValue(1, 100);

        if (Logging.USE_LOG)
            Logging.getInstance().write("SecureCraft", this.player.getName() + " é crafter avec " + (success ? "SUCCES" : "ECHEC") + " l'item " + template + " (" + World.world.getObjTemplate(template).getName() + ") pour " + receiver.getName());
        if (!success) {
            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EF");
            SocketManager.GAME_SEND_Ec_PACKET(receiver, "EF");
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-" + template);
            SocketManager.GAME_SEND_Im_PACKET(this.player, "0118");
        } else {
            GameObject newObj = World.world.getObjTemplate(template).createNewItem(1, true);
            if (signed) newObj.addTxtStat(988, this.player.getName());
            if(receiver.addObjet(newObj, true))
            	World.world.addGameObject(newObj, true);
            String stats = newObj.parseStatsString();

            this.player.send("EcK;" + template + ";T" + receiver.getName() + ";" + stats);
            receiver.send("EcK;" + template + ";B" + crafter.getName() + ";" + stats);

            SocketManager.GAME_SEND_Ow_PACKET(this.player);
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + template);
        }

        int winXP = Formulas.calculXpWinCraft(SM.get_lvl(), this.ingredients.size()) * Config.getInstance().rateJob;
        if (SM.getTemplate().getId() == 28 && winXP == 1)
            winXP = 10;
        if (success) {
            SM.addXp(this.player, winXP);
            ArrayList<JobStat> SMs = new ArrayList<>();
            SMs.add(SM);
            SocketManager.GAME_SEND_JX_PACKET(this.player, SMs);
        }

        this.ingredients.clear();
        return success;
    }

    public void addIngredient(Player player, int id, int quantity) {
        int oldQuantity = this.ingredients.get(id) == null ? 0 : this.ingredients.get(id);
        if(quantity < 0) if(- quantity > oldQuantity) return;

        this.ingredients.remove(id);
        oldQuantity += quantity;

        if (oldQuantity > 0) {
            this.ingredients.put(id, oldQuantity);
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(player, 'O', "+", id + "|" + oldQuantity);
        } else {
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(player, 'O', "-", id + "");
        }
    }

    public byte sizeList(Map<Player, ArrayList<Couple<Integer, Integer>>> list) {
        byte size = 0;

        for (ArrayList<Couple<Integer, Integer>> entry : list.values()) {
            for (Couple<Integer, Integer> couple : entry) {
                GameObject object = World.world.getGameObject(couple.first);
                if (object != null) {
                    ObjectTemplate objectTemplate = object.getTemplate();
                    if (objectTemplate != null && objectTemplate.getId() != 7508) size++;
                }
            }
        }
        return size;
    }

    public void putLastCraftIngredients() {
        if (this.player == null || this.lastCraft == null || !this.ingredients.isEmpty()) return;

        this.ingredients.clear();
        this.ingredients.putAll(this.lastCraft);
        this.ingredients.entrySet().stream().filter(e -> World.world.getGameObject(e.getKey()) != null)
                .filter(e -> !(World.world.getGameObject(e.getKey()).getQuantity() < e.getValue()))
                .forEach(e -> SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(this.player, 'O', "+", e.getKey() + "|" + e.getValue()));
    }

    public void resetCraft() {
        this.ingredients.clear();
        this.lastCraft.clear();
        this.oldJobCraft = null;
        this.jobCraft = null;
    }

    //FM commence a etre OK 

    private int reConfigingRunes = -1;

    public void modifIngredient(Player P, int guid, int qua) {
        //on prend l'ancienne valeur
        int q = this.ingredients.get(guid) == null ? 0 : this.ingredients.get(guid);
        if(qua < 0) if(-qua > q) return;
        //on enleve l'entrée dans la Map
        this.ingredients.remove(guid);
        //on ajoute (ou retire, en fct du signe) X objet
        q += qua;
        if (q > 0) {
            this.ingredients.put(guid, q);
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(P, 'O', "+", guid + "|"
                    + q);
        } else
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(P, 'O', "-", guid + "");
    }
    
	public void modifIngredient2(final Player P, final int guid, final int qua) {
		int q = (this.ingredients.get(guid) == null) ? 0 : this.ingredients.get(guid);
		this.ingredients.remove(guid);
		q += qua;
		if (q > 0) {
			this.ingredients.put(guid, q);
			SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(P, 'O', "+", String.valueOf(guid) + "|" + q);
		} else {
			SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(P, 'O', "-", new StringBuilder(String.valueOf(guid)).toString());
		}
	}
	public int lastDigit(int number) { return Math.abs(number) % 10; }
	
    private synchronized void craftMaging(boolean isReapeat, int repeat) {
    	// On commence le fm
    	
    	//Definition des variables
    	int limitPerLigne = 151; // Max par ligne (101 non ?)
    	boolean isRestating = false;
        boolean isSigningRune = false; // Si on utilise une rune de signature
        GameObject objectFm = null, signingRune = null, runeOrPotion = null; // Initalisation pour potentiellement les récuperer derriere et vide le cache
        int lvlElementRune = 0, statsID = -1, lvlQuaStatsRune = 0, statsAdd = 0, deleteID = -1, poidRune = 0, idRune = 0; // De meme
        boolean bonusRune = false; // Rune "Bonus" je suppose que c'est pour les serveurs cheat
        String statsObjectFm = "-1";  // La stats que l'on veut FM
        String loi = ""; // La loi de FM qui va être utilisé par l'algo (Potentiellement inutile si corrigé totalement)
        
        for (int idIngredient : this.ingredients.keySet()) { // On boucle sur Les ingrédients Pour différencié la Rune de l'objet
            GameObject ing = World.world.getGameObject(idIngredient); // On récupère l'ingrédient
            if (ing == null || !this.player.hasItemGuid(idIngredient)) { // On check s'il existe et si le User a bien l'item
                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI"); // PACKET DE RETOUR POUR AFFICHER L'ERREUR CHEZ LE JOUEUR
                SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");
                this.ingredients.clear(); 
                return; // ECHEC car pas d'ingrédient ou inexistant
            }
            int templateID = ing.getTemplate().getId(); // On récupère le template de la rune
            if (ing.getTemplate().getType() == 78)	// Si le type d'obj est une rune on le place dans une Var 
                idRune = idIngredient; 
            switch (templateID) { 	// Longue serie de Switch Case pour déterminé si c'est la rune on récupère ces infos SI c'est l'object c'est le cas par défault        
                	// ca c'est pour reformer un item
            	case 17200:
            		runeOrPotion = ing;
            		isRestating = true;
            		break;
            	case 17202:
            		runeOrPotion = ing;
            		isRestating = true;
            		break;
            	case 17203:
            		runeOrPotion = ing;
            		isRestating = true;
            		break;
            	case 17204:
            		runeOrPotion = ing;
            		isRestating = true;
            		break;
            	case 17205:
            		runeOrPotion = ing;
            		isRestating = true;
            		break;
            		// Ca c'est les potion de tempete (Pour FM Arme)
            	case 1333:
                    statsID = 99;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1335:
                    statsID = 96;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1337:
                    statsID = 98;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1338:
                    statsID = 97;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1340:
                    statsID = 97;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1341:
                    statsID = 96;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1342:
                    statsID = 98;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1343:
                    statsID = 99;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1345:
                    statsID = 99;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1346:
                    statsID = 96;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1347:
                    statsID = 98;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                case 1348:
                    statsID = 97;
                    lvlElementRune = ing.getTemplate().getLevel();
                    runeOrPotion = ing;
                    break;
                 // Ca c'est les runes d'ajout de stats
                case 1519:
                    runeOrPotion = ing;
                    statsObjectFm = "76";
                    statsAdd = 1;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1521:
                    runeOrPotion = ing;
                    statsObjectFm = "7c";
                    statsAdd = 1;
                    poidRune = 3;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1522:
                    runeOrPotion = ing;
                    statsObjectFm = "7e";
                    statsAdd = 1;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1523:
                    runeOrPotion = ing;
                    statsObjectFm = "7d";
                    statsAdd = 3;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1524:
                    runeOrPotion = ing;
                    statsObjectFm = "77";
                    statsAdd = 1;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1525:
                    runeOrPotion = ing;
                    statsObjectFm = "7b";
                    statsAdd = 1;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1545:
                    runeOrPotion = ing;
                    statsObjectFm = "76";
                    statsAdd = 3;
                    poidRune = 3;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1546:
                    runeOrPotion = ing;
                    statsObjectFm = "7c";
                    statsAdd = 3;
                    poidRune = 9;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1547:
                    runeOrPotion = ing;
                    statsObjectFm = "7e";
                    statsAdd = 3;
                    poidRune = 3;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1548:
                    runeOrPotion = ing;
                    statsObjectFm = "7d";
                    statsAdd = 10;
                    poidRune = 3;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1549:
                    runeOrPotion = ing;
                    statsObjectFm = "77";
                    statsAdd = 3;
                    poidRune = 3;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1550:
                    runeOrPotion = ing;
                    statsObjectFm = "7b";
                    statsAdd = 3;
                    poidRune = 3;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1551:
                    runeOrPotion = ing;
                    statsObjectFm = "76";
                    statsAdd = 10;
                    poidRune = 10;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1552:
                    runeOrPotion = ing;
                    statsObjectFm = "7c";
                    statsAdd = 10;
                    poidRune = 30;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1553:
                    runeOrPotion = ing;
                    statsObjectFm = "7e";
                    statsAdd = 10;
                    poidRune = 10;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1554:
                    runeOrPotion = ing;
                    statsObjectFm = "7d";
                    statsAdd = 30;
                    poidRune = 8;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1555:
                    runeOrPotion = ing;
                    statsObjectFm = "77";
                    statsAdd = 10;
                    poidRune = 10;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1556:
                    runeOrPotion = ing;
                    statsObjectFm = "7b";
                    statsAdd = 10;
                    poidRune = 10;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1557:
                    runeOrPotion = ing;
                    statsObjectFm = "6f";
                    statsAdd = 1;
                    poidRune = 100;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 1558:
                    runeOrPotion = ing;
                    statsObjectFm = "80";
                    statsAdd = 1;
                    poidRune = 90;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7433:
                    runeOrPotion = ing;
                    statsObjectFm = "73";
                    statsAdd = 1;
                    poidRune = 30;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7434:
                    runeOrPotion = ing;
                    statsObjectFm = "b2";
                    statsAdd = 1;
                    poidRune = 20;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7435:
                    runeOrPotion = ing;
                    statsObjectFm = "70";
                    statsAdd = 1;
                    poidRune = 20;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7436:
                    runeOrPotion = ing;
                    statsObjectFm = "8a";
                    statsAdd = 1;
                    poidRune = 2;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7437:
                    runeOrPotion = ing;
                    statsObjectFm = "dc";
                    statsAdd = 1;
                    poidRune = 30;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7438:
                    runeOrPotion = ing;
                    statsObjectFm = "75";
                    statsAdd = 1;
                    poidRune = 51;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7442:
                    runeOrPotion = ing;
                    statsObjectFm = "b6";
                    statsAdd = 1;
                    poidRune = 30;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7443:
                    runeOrPotion = ing;
                    statsObjectFm = "9e";
                    statsAdd = 10;
                    poidRune = 3;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7444:
                    runeOrPotion = ing;
                    statsObjectFm = "9e";
                    statsAdd = 30;
                    poidRune = 8;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7445:
                    runeOrPotion = ing;
                    statsObjectFm = "9e";
                    statsAdd = 100;
                    poidRune = 25;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7446:
                    runeOrPotion = ing;
                    statsObjectFm = "e1";
                    statsAdd = 1;
                    poidRune = 15;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7447:
                    runeOrPotion = ing;
                    statsObjectFm = "e2";
                    statsAdd = 1;
                    poidRune = 2;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7448:
                    runeOrPotion = ing;
                    statsObjectFm = "ae";
                    statsAdd = 10;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7449:
                    runeOrPotion = ing;
                    statsObjectFm = "ae";
                    statsAdd = 30;
                    poidRune = 3;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7450:
                    runeOrPotion = ing;
                    statsObjectFm = "ae";
                    statsAdd = 100;
                    poidRune = 10;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7451:
                    runeOrPotion = ing;
                    statsObjectFm = "b0";
                    statsAdd = 1;
                    poidRune = 3;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7452:
                    runeOrPotion = ing;
                    statsObjectFm = "f3";
                    statsAdd = 1;
                    poidRune = 5;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7453:
                    runeOrPotion = ing;
                    statsObjectFm = "f2";
                    statsAdd = 1;
                    poidRune = 5;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7454:
                    runeOrPotion = ing;
                    statsObjectFm = "f1";
                    statsAdd = 1;
                    poidRune = 5;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7455:
                    runeOrPotion = ing;
                    statsObjectFm = "f0";
                    statsAdd = 1;
                    poidRune = 5;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7456:
                    runeOrPotion = ing;
                    statsObjectFm = "f4";
                    statsAdd = 1;
                    poidRune = 5;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7457:
                    runeOrPotion = ing;
                    statsObjectFm = "d5";
                    statsAdd = 1;
                    poidRune = 4;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7458:
                    runeOrPotion = ing;
                    statsObjectFm = "d4";
                    statsAdd = 1;
                    poidRune = 4;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7459:
                    runeOrPotion = ing;
                    statsObjectFm = "d2";
                    statsAdd = 1;
                    poidRune = 4;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7460:
                    runeOrPotion = ing;
                    statsObjectFm = "d6";
                    statsAdd = 1;
                    poidRune = 4;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7560:
                    runeOrPotion = ing;
                    statsObjectFm = "d3";
                    statsAdd = 1;
                    poidRune = 4;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 8379:
                    runeOrPotion = ing;
                    statsObjectFm = "7d";
                    statsAdd = 10;
                    poidRune = 10;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 10662:
                    runeOrPotion = ing;
                    statsObjectFm = "b0";
                    statsAdd = 3;
                    poidRune = 9;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 7508:
                    isSigningRune = true;
                    signingRune = ing;
                    break;
                case 11118:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "76";
                    statsAdd = 15;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11119:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "7c";
                    statsAdd = 15;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11120:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "7e";
                    statsAdd = 15;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11121:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "7d";
                    statsAdd = 45;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11122:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "77";
                    statsAdd = 15;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11123:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "7b";
                    statsAdd = 15;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11124:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "b0";
                    statsAdd = 10;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11125:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "73";
                    statsAdd = 3;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11126:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "b2";
                    statsAdd = 5;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11127:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "70";
                    statsAdd = 5;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11128:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "8a";
                    statsAdd = 10;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 11129:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "dc";
                    statsAdd = 5;
                    poidRune = 1;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                case 10057:
                    bonusRune = true;
                    runeOrPotion = ing;
                    statsObjectFm = "31b";
                    statsAdd = 1;
                    poidRune = 0;
                    lvlQuaStatsRune = ing.getTemplate().getLevel();
                    break;
                // CA c'est l'objet
                default:
                    int type = ing.getTemplate().getType(); // On récupère son type
                    if ((type >= 1 && type <= 11) || (type >= 16 && type <= 22)
                            || type == 81 || type == 102 || type == 114
                            || ing.getTemplate().getPACost() > 0) { // Si c'est un obj avec des stats ou avec des PA d'utilisation 
                        objectFm = ing;
                        SocketManager.GAME_SEND_EXCHANGE_OTHER_MOVE_OK_FM(this.player.getGameClient(), 'O', "+", objectFm.getGuid()
                                + "|" + 1); // On envoie un packet de validation au joueur il me semble
                        deleteID = idIngredient; // On récupère l'id de l'ingrédient pour le supprimer plus tard
                        GameObject newObj = GameObject.getCloneObjet(objectFm, 1); // Création d'un clone avec un nouveau identifiant
                        if (objectFm.getQuantity() > 1) { // S'il y avait plus d'un objet
                            int newQuant = objectFm.getQuantity() - 1; // On supprime le cloné
                            objectFm.setQuantity(newQuant); // on actualise les objets du serveur allumé
                            SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, objectFm); // on actualise les objets sur le joueur
                        } else {
                            World.world.removeGameObject(idIngredient);
                            this.player.removeItem(idIngredient);
                            SocketManager.GAME_SEND_DELETE_STATS_ITEM_FM(this.player, idIngredient);
                        }
                        objectFm = newObj; // Tout neuf avec un nouveau identifiant
                        break;
                    }
            }
        }
       // System.out.println("La :" + objectFm.getTemplate().getId() + " " + runeOrPotion + " " + SM );
        if( objectFm.getTemplate() == null || runeOrPotion == null ) {
        	this.player.sendMessage("Aucune rune détecté"); 
        	if (objectFm != null) {
                World.world.addGameObject(objectFm, true);
                this.player.addObjet(objectFm, false);
                //SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, objectFm);
            }
            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI"); // packet d'echec
            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");  // packet d'icone rouge je crois      
             // On nettoie les ingrédients
            final String data = String.valueOf(objectFm.getGuid()) + "|1|" + objectFm.getTemplate().getId() + "|"
					+ objectFm.parseStatsString();
            SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data);
            this.ingredients.clear();
            return;
        }
        
        
        if(runeOrPotion.getTemplate().getId() == 17200){
        	
        	if (SM == null || objectFm == null) {
	        	this.player.sendMessage("Vous ne possedez pas Ou de metier approprié, Ou de rune, ou d'objet"); 
	            if (objectFm != null) {
	                World.world.addGameObject(objectFm, true);
	                this.player.addObjet(objectFm, false);
	                //SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, objectFm);
	            }
	            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI"); // packet d'echec
	            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");  // packet d'icone rouge je crois
	            
	           
	            this.ingredients.clear(); // On nettoie les ingrédients
	            

	            return;
        	}
        	//int rarity = objectFm.getRarity();
        	 ObjectTemplate objTemplate = objectFm.getTemplate();
        	 
        	 GameObject newObj = World.world.getObjTemplate(objTemplate.getId()).createNewItem(1, false);
        	                           
        	 objectFm = newObj;
        	 if(this.player.addObjet(newObj, true))
             	World.world.addGameObject(newObj, true);
             
        	 SocketManager.GAME_SEND_Ow_PACKET(this.player);
				
             final String data = String.valueOf(newObj.getGuid()) + "|1|" + newObj.getTemplate().getId() + "|"
					+ newObj.parseStatsString();
             	
             
             	
            if (deleteID != -1) { 
            	
                this.ingredients.remove(deleteID);
            } 
            if (runeOrPotion != null) {
                int newQua = runeOrPotion.getQuantity() - 1;
                if (newQua <= 0) {
                    this.player.removeItem(runeOrPotion.getGuid());
                    World.world.removeGameObject(runeOrPotion.getGuid());
                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, runeOrPotion.getGuid());
                } else {
                    runeOrPotion.setQuantity(newQua);
                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, runeOrPotion);
                }
            }
             	//World.world.addGameObject(objectFm, true);
				//this.player.addObjet(objectFm);
				//SocketManager.GAME_SEND_Ow_PACKET(this.player);
				//final String data = String.valueOf(objectFm.getGuid()) + "|1|" + objectFm.getTemplate().getId() + "|"
				//		+ objectFm.parseStatsString()+ "|"+objectFm.getRarity();
				if (!this.isRepeat) {
					this.reConfigingRunes = -1;
				}
				if (this.reConfigingRunes != 0 || this.broken) {
					SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data);
				}
				
				//this.player.sendMessage(""+data);
				//this.data = data;
				SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + objTemplate.getId());
				SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + objTemplate.getId());    

             //SocketManager.GAME_SEND_Em_PACKET(this.player, "KO+" + guid + "|1|" + objTemplate.getId() + "|" + newObj.parseStatsString().replace(";", "#"));
             //SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + objTemplate.getId());
             //SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + objTemplate.getId());
             
        }
        else if(runeOrPotion.getTemplate().getId() == 17202 || runeOrPotion.getTemplate().getId() == 17203
        		|| runeOrPotion.getTemplate().getId() == 17204 || runeOrPotion.getTemplate().getId() == 17205){
        	
        	if (SM == null || objectFm == null) {
	        	this.player.sendMessage("Vous ne possedez pas Ou de metier approprié, Ou de rune, ou d'objet"); 
	            if (objectFm != null) {
	                World.world.addGameObject(objectFm, true);
	                this.player.addObjet(objectFm, false);
	                //SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, objectFm);
	            }
	            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI"); // packet d'echec
	            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");  // packet d'icone rouge je crois
	            
	           
	            this.ingredients.clear(); // On nettoie les ingrédients
	            

	            return;
        	}
        	//int rarity = objectFm.getRarity();
       	 	ObjectTemplate objTemplate = objectFm.getTemplate();
       	 	int lastDigit = lastDigit(runeOrPotion.getTemplate().getId());
       	 	System.out.println("lastDigit" + lastDigit);
       	 	/*
       	 	if(lastDigit <= rarity){
       	 		
	       	 	this.player.sendMessage("L'objet est déjà de rareté supérieure ou égale"); 
	            if (objectFm != null) {
	                World.world.addGameObject(objectFm, true);
	                this.player.addObjet(objectFm);
	                //SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, objectFm);
	            }
	            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI"); // packet d'echec
	            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");  // packet d'icone rouge je crois
	            
	           
	            this.ingredients.clear(); // On nettoie les ingrédients
       	 		return;
       	 	}
       	 
       	 	if(lastDigit-1 != rarity){
	       	 	this.player.sendMessage("L'objet ne possède pas la rareté suffisante pour être augmenté avec cette rune"); 
	            if (objectFm != null) {
	                World.world.addGameObject(objectFm, true);
	                this.player.addObjet(objectFm);
	                //SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, objectFm);
	            }
	            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI"); // packet d'echec
	            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");  // packet d'icone rouge je crois
	            
	           
	            this.ingredients.clear(); // On nettoie les ingrédients
    	 		
    	 		return;
    	 	}
        	*/
        	 
        	 GameObject newObj = World.world.getObjTemplate(objTemplate.getId()).createNewItem(1, false);
        	 //GameObject newObj = new GameObject(id, getId(), 1, Constant.ITEM_POS_NO_EQUIPED, ObjectTemplate.generateNewStatsFromTemplate(objTemplate.getStrTemplate(), false,rarity), ObjectTemplate.getEffectTemplate(objTemplate.getStrTemplate()), new HashMap<Integer, Integer>(), Stat, 0,rarity);                                
        	 objectFm = newObj;
        	 if(this.player.addObjet(newObj, true))
        		 World.world.addGameObject(newObj, true);
        	 
         	SocketManager.GAME_SEND_Ow_PACKET(this.player);
			
         	final String data = String.valueOf(newObj.getGuid()) + "|1|" + newObj.getTemplate().getId() + "|"
					+ newObj.parseStatsString();
         	
         	
            if (deleteID != -1) { 
            	//this.player.sendMessage("On retire l'ingrédient (Rune) :"+deleteID); 
                this.ingredients.remove(deleteID);
            } 
            if (runeOrPotion != null) {
                int newQua = runeOrPotion.getQuantity() - 1;
                if (newQua <= 0) {
                    this.player.removeItem(runeOrPotion.getGuid());
                    World.world.removeGameObject(runeOrPotion.getGuid());
                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, runeOrPotion.getGuid());
                } else {
                    runeOrPotion.setQuantity(newQua);
                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, runeOrPotion);
                }
            }
         	//World.world.addGameObject(objectFm, true);
			//this.player.addObjet(objectFm);
			//SocketManager.GAME_SEND_Ow_PACKET(this.player);
			//final String data = String.valueOf(objectFm.getGuid()) + "|1|" + objectFm.getTemplate().getId() + "|"
			//		+ objectFm.parseStatsString()+ "|"+objectFm.getRarity();
			if (!this.isRepeat) {
				this.reConfigingRunes = -1;
			}
			if (this.reConfigingRunes != 0 || this.broken) {
				SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data);
			}
			
			//this.player.sendMessage(""+data);
			//this.data = data;
			SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + objTemplate.getId());
			SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + objTemplate.getId());    
			
        }
	    else{
	        
	        int StatEnInt = Integer.parseInt(statsObjectFm, 16);
	        double poidUnitaire = getPwrPerEffet(StatEnInt); // On calcul le poid unitaire de la rune
	        //double poidStatsAfter =0;
	        //double poidStatsBefore =0; // Inutilisé
	        
	        if (poidUnitaire > 0.0) {
	            poidRune = (int)Math.round(statsAdd * poidUnitaire); // On le multiplie par sa valur logiquement pour avoir le poid total     
	        }
	        //this.player.sendMessage("poid de la stat a l'unité :"+poidUnitaire); 
	        //this.player.sendMessage("poid de la rune :"+poidRune);
	    	
	        if (SM == null || objectFm == null || runeOrPotion == null) { // On check avant de commencer le traitement si tous les pré-requis et recupération ont fonctionné
	        	this.player.sendMessage("Vous ne possedez pas Ou de metier approprié, Ou de rune, ou d'objet"); 
	        	
	        	 //System.out.println("On rentre forcement la " );
	            if (objectFm != null) {
	                World.world.addGameObject(objectFm, true);
	                this.player.addObjet(objectFm, false);
	            }
	            
	            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EI"); // packet d'echec
	            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-");  // packet d'icone rouge je crois
	            //this.ingredients.clear(); // On nettoie les ingrédients
	            return;
	        }
	        
	        //int rarity = objectFm.getRarity();
	        ObjectTemplate objTemplate = objectFm.getTemplate(); // On récupère le template de l'objet
	        int chance = 0;			
	        int lvlJob = SM.get_lvl();	// le level du métier
	        int PoidTotItemActuel = 0;	
	        int pwrPerte = 0;
	        java.util.ArrayList<Integer> chances = new ArrayList<Integer>(); // Le tableau des chances
	        int objTemplateID = objTemplate.getId(); 	// L'id du template
	        String statStringObj = objectFm.parseStatsString();  // Les stats de l'objects en format String
	        
	        if (lvlElementRune > 0 && lvlQuaStatsRune == 0) { // Le cas des runes élémentaire est unique, Le level du métier défini la réussite
	        	//this.player.sendMessage("C'est une rune élémentaire (pour CAC)"+lvlElementRune); 
	            chance = Formulas.calculChanceByElement(lvlJob, objTemplate.getLevel(), lvlElementRune);
	            if (chance > 100 - (lvlJob / 20))
	                chance = 100 - (lvlJob / 20);
	            if (chance < (lvlJob / 20))
	                chance = (lvlJob / 20);
	            chances.add(0, chance);
	            chances.add(1, 0);
	            chances.add(2, 100 - chance);
	        } else if (lvlQuaStatsRune > 0 && lvlElementRune == 0) { // Le cas du FM normal est très complèxe !
	        	//this.player.sendMessage("C'est une rune normal (pour buff de stats)"+lvlQuaStatsRune); 
	            int PoidActuelStatAFm = 1;
	            int PoidTotStatsExoItemActuel = 1;
	            
	            if (!statStringObj.isEmpty()) {   
	            	//this.player.sendMessage("L'objet n'est pas vide"); 
	                PoidTotItemActuel = currentTotalWeigthBase(statStringObj, objectFm); // Poids total de l'objet 
	              //this.player.sendMessage("Poid de l'obj (Repris)  "+PoidTotItemActuel); 
	                PoidTotStatsExoItemActuel = currentWeithStatsExo(statStringObj, objectFm); // Poid des stats EXO (car si ca dépasse 101 ca echec)
	              //this.player.sendMessage("Le poid des Exo :"+PoidTotStatsExoItemActuel); 
	            }
	            else {
	            	PoidActuelStatAFm = 0;
	            	PoidTotStatsExoItemActuel = 0;
	            }
	            
	            
	            
	            
	            int PoidMaxItem =  WeithTotalBase(objTemplateID); // Poids de l'objet en stat Max (donc théoriquement le poid max)
	            /*
	            if(rarity>3){
	               PoidMaxItem = WeithTotalBaseLegendary(objTemplateID) ; // Poids de l'objet en stat Max et légendaire (donc théoriquement le poid max)
	            }
	            else{
	            	PoidMaxItem = WeithTotalBase(objTemplateID); // Poids de l'objet en stat Max (donc théoriquement le poid max)
	            }
	            */
	            int PoidMiniItem = WeithTotalBaseMin(objTemplateID); // Poids de l'objet en stat Mini (donc théoriquement le poid minimum)
	
	            // Bon si les poid sont négatifs, on les positionne a 0 pour éviter les bugs, meme si faudrait gérer le cas différemment
	            if (PoidMaxItem < 0) {
	            	PoidMaxItem = 0;
	            }
	            if (PoidTotItemActuel < 0) {
	            	PoidTotItemActuel = 0;
	            }
	            if (PoidActuelStatAFm < 0) {
	            	PoidActuelStatAFm = 0;
	            }
	            if (PoidTotStatsExoItemActuel < 0) {       
	            	PoidTotStatsExoItemActuel = 0;
	            }
	            
	            float coef = 1;
	                               
	            int CheckStatItemTemplate = viewBaseStatsItem(objectFm, statsObjectFm); // Check si La stats est sur l'item de base 
	            int CheckStatItemActuel = viewActualStatsItem(objectFm, statsObjectFm); // Check si La stats est sur l'item 1 = oui 2 = oui 0 = non
	            
	               // A FAIRE !!!!      
		         // Trouver un moment pour diminuer une stats négative plutot que la considéré comme un stat différente 
	            int statMax = getStatBaseMax(objTemplate, statsObjectFm); // stat maximum de l'obj intéressant pour les cas ou les stats dépasse le poid théorique max
	            /*
	            if(rarity > 3){
	            	statMax = getStatBaseMaxLegendaire(objTemplate, statsObjectFm);// stat maximum de l'obj Legendaire intéressant pour les cas ou les stats dépasse le poid théorique max 
	            }
	            else{
	            	statMax = getStatBaseMax(objTemplate, statsObjectFm); // stat maximum de l'obj intéressant pour les cas ou les stats dépasse le poid théorique max
	            }
	            */
	            int statMin = getStatBaseMin(objTemplate, statsObjectFm); // stat Minimum de l'obj intéressant pour les cas ou les stats dépasse le poid théorique max
	            
	            int statJetActuel = 0;
	            
	            // Gestion des dommages bizarres
	            if(statsObjectFm == "70" || statsObjectFm == "79" ) {
	            	 int statJetActuel1 = getActualJet(objectFm, "79");
	            	 int statJetActuel2 = getActualJet(objectFm, "70");// Jet actuel de l'item pour rendre plus compliqué si on approche du poid théorique ou de la stats max si ca dépasse le poid théorique
	            	 if(statJetActuel1>=statJetActuel2) {
	            		 statJetActuel = statJetActuel1;
	            	 }
	            	 else {
	            		 statJetActuel = statJetActuel2;
	            	 }
	            }
	            else {
	            	 statJetActuel = getActualJet(objectFm, statsObjectFm);
	            }
	            int statJetFutur = statJetActuel + statsAdd;
	            
	            
	            PoidActuelStatAFm = (int)Math.floor(statJetActuel*poidUnitaire); // Poid des stats de base
	            //this.player.sendMessage("Poid de la stat actuel ? "+PoidActuelStatAFm); 
	            
	            //this.player.sendMessage("Le poid max de la ligne hors limite :" + statMax*poidUnitaire);
	            
	            // La on fait des controles pour savoir si le FM théorique est possible ou non 
	            boolean canFM = true;
	            boolean exception = false;
	            float x = 1;
	            
	            if(statMax*poidUnitaire > limitPerLigne){ // Si le poid de la ligne de stats de base de l'item supérieur au maximum théorique par ligne de 101
	            	// On autorise quand même le fm si on dépasse pas le jet max
	            	//this.player.sendMessage("On est la  :" + (statJetActuel+statsAdd) + " max: " + statMax);
	            	
	            	if (statJetActuel+statsAdd > statMax) { // On compare en statistique car le poid ne compte pas pour ces cas
	            		this.player.sendMessage("Cette statistique ne montra pas plus haut");  
	            		canFM = false;
	            	}
	            	else {
	            	
	            		coef = 1f; // Coef a 0.8 parce que la c'est quand meme chaud a faire
	            		exception = true;
	            	}
	            }
	            else {     	
	            	// Si la stats qui veut faire passer, dépasse la limite théorique
	        		if( (statJetActuel*poidUnitaire)+poidRune > limitPerLigne ) {
	        			this.player.sendMessage("Cette statistique ne montra pas plus haut");
	                	canFM = false;
	                }   
	            }
	            
	        	if((CheckStatItemTemplate == 0 && CheckStatItemActuel == 0) || (CheckStatItemTemplate == 0 && CheckStatItemActuel == 1)) {
	        		// La stat qu'on ajoute est un over et son poid est de PoidRune
	        		if( PoidTotStatsExoItemActuel + poidRune > limitPerLigne) {
	        			canFM = false;
	        			this.player.sendMessage("Tu ne peux pas ajouter plus d'Exo");
	        			this.player.sendMessage("Le poid des Exo :"+PoidTotStatsExoItemActuel); 
	        		}    		
	        	}
	        	else {
	                // TIEN ici on va utiliser le X pour plutot simplifier si on est loin de la limite théorique mais que c'est pas un exo bien entendu                
	                if( statJetActuel != 0 ) {
	                	x = (float) (limitPerLigne / (statJetActuel*poidUnitaire));
	                	if(x > 5.0f) {
	                		x = 5.0f;
	                	}
	                }
	                else {
	                	x = 5.0f;
	                }
	        	}
	            
	            // La c'est les contraintes de métier ETC
	            if (lvlJob < (int) Math.floor(objTemplate.getLevel() / 2)) {
	            	this.player.sendMessage("Ton métier n'est pas suffisant pour améliorer cet objet !");
	            	canFM = false; // On rate le FM si le métier n'est pas suffisant
	            }
	            
	            // La notion de loi + permet de cibler le coef
	            if(poidRune > 30) {
	            	if( statMax < statJetFutur) {
	            		loi = "exo";
	            		coef = 0.25f;
	            	}          
	            	else {
	            		loi = "normal";
	            		coef = 1.0f;
	            	}
	            }
	            else {
	            	if ( statMax*1.26 < statJetFutur) {
	            		loi = "over";
	            		coef = 0.8f;
	            	}
	            	else {
	            		loi = "normal";
	            		coef = 1.0f;
	            	}
	            }
	
	            
	            this.player.sendMessage("Loi appliquée " + loi);
	            
	            if (canFM) {
	            	int diff = (int) Math.abs((PoidMaxItem * coef)  //*1.3f initiallement
		                    - PoidTotItemActuel);
	                    //chances = Formulas.chanceFM2(PoidMaxItem, PoidMiniItem, PoidTotItemActuel, PoidActuelStatAFm,PoidTotStatsExoItemActuel , poidRune, statMax, statMin, statJetActuel,statsAdd,poidUnitaire,statJetFutur, x , coef, this.player, objectFm.getPuit(), loi );
	                    chances = Formulas.chanceFM(PoidMaxItem, PoidMiniItem, PoidTotItemActuel, PoidActuelStatAFm, poidRune, diff, coef, statMax, statMin, statJetActuel, x, bonusRune, statsAdd, loi);	
	                    // On retire la rune car on peut FM
	                    if (deleteID != -1) { 
	                    	//this.player.sendMessage("On retire l'ingrédient (Rune) :"+deleteID); 
	                        this.ingredients.remove(deleteID);
	                    }  
	            } 
	            else
	            {	// CORRIGE UN TRUC LA POUR QUE L'UTILISATEUR PERDRE PAS SES ITEMS MALGRES UNE TENTATIVE DE FM NON LEGAL
	            	  World.world.addGameObject(objectFm, true);
	                  this.player.addObjet(objectFm, false);
	                  
	                  int nbRunes = this.ingredients.get(idRune);
	                  if (nbRunes > 0) // On remet la rune
	                      this.modifIngredient(this.player, idRune, nbRunes); // Rajout des runes moins une
	                  
	                  try {
	                  	this.player.getCurJobAction().modifIngredient2(this.player, objectFm.getGuid(), 1); // On remet l"item
	                  }
	                  catch(Exception e){
	                  	 //this.player.sendMessage("On est la  :"+ e );
	                  	((JobAction) this.player.getExchangeAction().getValue()).modifIngredient2(this.player, objectFm.getGuid(), 1); // On remet l'item dans la case de FM        
	                  }
	                  
	                  return;
	            }            
	        }
	        
	
	        int aleatoryChance = Formulas.getRandomValue(1, 100);
	        int SC = chances.get(0);
	        int SN = chances.get(1);
	        boolean successC = (aleatoryChance <= SC);
	        boolean successN = (aleatoryChance <= (SC + SN));
	        //this.player.sendMessage("le Jet :"+aleatoryChance);
	        if (successC || successN) {	
	        	if(successN) {
		        	if(objectFm.getPuit() > 0 || !statStringObj.isEmpty()) {
			            int winXP = Formulas.calculXpWinFm(objectFm.getTemplate().getLevel(), poidRune)
			                    * Config.getInstance().rateJob;
			            if (winXP > 0) {
			                SM.addXp(this.player, winXP);
			                ArrayList<JobStat> SMs = new ArrayList<JobStat>();
			                SMs.add(SM);
			                SocketManager.GAME_SEND_JX_PACKET(this.player, SMs);
			            }
		        	}
		        	else {
		        		successN = false;
		        		SN = 0;        		
		        	}
	        	}
	        	else {
		            int winXP = Formulas.calculXpWinFm(objectFm.getTemplate().getLevel(), poidRune)
		                    * Config.getInstance().rateJob;
		            if (winXP > 0) {
		                SM.addXp(this.player, winXP);
		                ArrayList<JobStat> SMs = new ArrayList<JobStat>();
		                SMs.add(SM);
		                SocketManager.GAME_SEND_JX_PACKET(this.player, SMs);
		            }
	        		
	        	}
	        }
	        this.player.sendMessage("Chances : [SC "+SC + "%| SN " + SN+ "%| EC " + (100 - (SC + SN)) + "%]");
	        
	        if (successC) // SC
	        {
	        	this.player.sendMessage("Succes critique !");
	            int coef = 0;
	            pwrPerte = 0;
	            if (lvlElementRune == 1)
	                coef = 50;
	            else if (lvlElementRune == 25)
	                coef = 65;
	            else if (lvlElementRune == 50)
	                coef = 85;
	            if (isSigningRune) {
	                objectFm.addTxtStat(985, this.player.getName());
	            }
	            if (lvlElementRune > 0 && lvlQuaStatsRune == 0) {
	                for (SpellEffect effect : objectFm.getEffects()) {
	                    if (effect.getEffectID() != 100)
	                        continue;
	                    String[] infos = effect.getArgs().split(";");
	                    try {
	                        int min = Integer.parseInt(infos[0], 16);
	                        int max = Integer.parseInt(infos[1], 16);
	                        int newMin = (min * coef) / 100;
	                        int newMax = (max * coef) / 100;
	                        if (newMin == 0)
	                            newMin = 1;
	                        String newRange = "1d" + (newMax - newMin + 1) + "+"
	                                + (newMin - 1);
	                        String newArgs = Integer.toHexString(newMin) + ";"
	                                + Integer.toHexString(newMax) + ";-1;-1;0;"
	                                + newRange;
	                        effect.setArgs(newArgs);
	                        effect.setEffectID(statsID);
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }
	            } else if (lvlQuaStatsRune > 0 && lvlElementRune == 0) {           	
	            	objectFm.setNewStats(statsObjectFm,statsAdd);
	            	objectFm.setModification();
	            	//String test = objectFm.parseStatsString();
	            	//this.player.sendMessage("Succes critique !"+ test);
	            }
	            if (signingRune != null) {
	                int newQua = signingRune.getQuantity() - 1;
	                if (newQua <= 0) {
	                    this.player.removeItem(signingRune.getGuid());
	                    World.world.removeGameObject(signingRune.getGuid());
	                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, signingRune.getGuid());
	                } else {
	                    signingRune.setQuantity(newQua);
	                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, signingRune);
	                }
	            }
	            if (runeOrPotion != null) {
	                int newQua = runeOrPotion.getQuantity() - 1;
	                if (newQua <= 0) {
	                    this.player.removeItem(runeOrPotion.getGuid());
	                    World.world.removeGameObject(runeOrPotion.getGuid());
	                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, runeOrPotion.getGuid());
	                } else {
	                    runeOrPotion.setQuantity(newQua);
	                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, runeOrPotion);
	                }
	            }
	            //this.player.sendMessage("Obj créé " +  objectFm); 
	            World.world.addGameObject(objectFm, true);
				this.player.addObjet(objectFm, false);
				SocketManager.GAME_SEND_Ow_PACKET(this.player);
				final String data = String.valueOf(objectFm.getGuid()) + "|1|" + objectFm.getTemplate().getId() + "|"
						+ objectFm.parseStatsString();
				if (!this.isRepeat) {
					this.reConfigingRunes = -1;
				}
				if (this.reConfigingRunes != 0 || this.broken) {
					SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data);
				}
				//this.player.sendMessage(""+data);
				this.data = data;
				SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+" + objTemplateID);
				SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;" + objTemplateID);    
	            
	        } else if (successN) {
	        	this.player.sendMessage("Succes Neutre");
	            pwrPerte = 0;
	            if (isSigningRune) {
	                objectFm.addTxtStat(985, this.player.getName());
	            }
	                       
	            // GESTION DES STATS NEGATIVE A REPRENDRE
	            objectFm = CalculPerteAndPuit(successN , statsAdd , statsObjectFm, objectFm, poidRune, poidUnitaire, loi,PoidTotItemActuel, this.player);
	                       
	            if (signingRune != null) {
	                int newQua = signingRune.getQuantity() - 1;
	                if (newQua <= 0) {
	                    this.player.removeItem(signingRune.getGuid());
	                    World.world.removeGameObject(signingRune.getGuid());
	                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, signingRune.getGuid());
	                } else {
	                    signingRune.setQuantity(newQua);
	                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, signingRune);
	                }
	            }
	            if (runeOrPotion != null) {
	                int newQua = runeOrPotion.getQuantity() - 1;
	                if (newQua <= 0) {
	                    this.player.removeItem(runeOrPotion.getGuid());
	                    World.world.removeGameObject(runeOrPotion.getGuid());
	                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(this.player, runeOrPotion.getGuid());
	                } else {
	                    runeOrPotion.setQuantity(newQua);
	                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, runeOrPotion);
	                }
	            }
	
	            World.world.addGameObject(objectFm, true);
	            this.player.addObjet(objectFm, false);
	            SocketManager.GAME_SEND_Ow_PACKET(this.player);
	            String data = objectFm.getGuid() + "|1|" + objectFm.getTemplate().getId() + "|"+ objectFm.parseStatsString();
	            if (!this.isRepeat)
	                this.reConfigingRunes = -1;
	            if (this.reConfigingRunes != 0 || this.broken)
	                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data);
	            this.data = data;
	            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "+"
	                    + objTemplateID);
	            if (pwrPerte > 0) {
	                SocketManager.GAME_SEND_Ec_PACKET(this.player, "EF");
	                SocketManager.GAME_SEND_Im_PACKET(this.player, "0194");
	            } else
	                SocketManager.GAME_SEND_Ec_PACKET(this.player, "K;"
	                        + objTemplateID);
	        } else
	        // EC
	        {
	        	this.player.sendMessage("Echec !");
	            pwrPerte = 0;
	            if (signingRune != null) { // On perd la rune signature
	                int newQua = signingRune.getQuantity() - 1;
	                if (newQua <= 0) {
	                    this.player.removeItem(signingRune.getGuid());
	                    World.world.removeGameObject(signingRune.getGuid());
	                    SocketManager.GAME_SEND_DELETE_STATS_ITEM_FM(this.player, signingRune.getGuid());
	                } else {
	                    signingRune.setQuantity(newQua);
	                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, signingRune);
	                }
	            }
	            if (runeOrPotion != null) { // On perd la potion
	                int newQua = runeOrPotion.getQuantity() - 1;
	                if (newQua <= 0) {
	                    this.player.removeItem(runeOrPotion.getGuid());
	                    World.world.removeGameObject(runeOrPotion.getGuid());
	                    SocketManager.GAME_SEND_DELETE_STATS_ITEM_FM(this.player, runeOrPotion.getGuid());
	                } else {
	                    runeOrPotion.setQuantity(newQua);
	                    SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(this.player, runeOrPotion);
	                }
	            }
	          
	            objectFm = CalculPerteAndPuit(successN , statsAdd , statsObjectFm, objectFm, poidRune, poidUnitaire, loi,PoidTotItemActuel, this.player);            
	          
	            //this.player.sendMessage("On est la l'objet a pas perdu de stat :"+ objectFm.getPuit() );
	            World.world.addGameObject(objectFm, true); // On ajoute l'obj a la map
	            this.player.addObjet(objectFm, false); // On ajoute l'obj au joueur
	            SocketManager.GAME_SEND_Ow_PACKET(this.player); // Ca je vois pas trop a part l'echec
	
	            String data = objectFm.getGuid() + "|1|"+ objectFm.getTemplate().getId() + "|"+ objectFm.parseStatsString(); // Ca non plus mais ca met un obj undefined ?
	            
	            if (!this.isRepeat)
	                this.reConfigingRunes = -1;
	            
	            if (this.reConfigingRunes != 0 || this.broken)
	                SocketManager.GAME_SEND_EXCHANGE_MOVE_OK_FM(this.player, 'O', "+", data); // Ca je ne sais pas       
	                
	            this.data = data;
	
	            SocketManager.GAME_SEND_IO_PACKET_TO_MAP(this.player.getCurMap(), this.player.getId(), "-"
	                    + objTemplateID); // Ca c'est l'echec sur l'icone je opense
	            SocketManager.GAME_SEND_Ec_PACKET(this.player, "EF"); // Ca c'est l'echec sur le joueur ?
	            
	            if (pwrPerte > 0)
	                SocketManager.GAME_SEND_Im_PACKET(this.player, "0117"); // Ca c'est gain de reliquat
	            else
	                SocketManager.GAME_SEND_Im_PACKET(this.player, "0183"); // Ca c'est perte ? 
	
	        	}  
	                     
	        	this.player.sendMessage("Puit libre sur l'item :"+ objectFm.getPuit() );
	        }
        
        this.lastCraft.clear();
        this.lastCraft.putAll(this.ingredients);
        this.lastCraft.put(objectFm.getGuid(), 1);
        int nbRunes = 0;
		if (!this.ingredients.isEmpty() && this.ingredients.get(idRune) != null) {
			if (this.isRepeat) {
				nbRunes = this.ingredients.get(idRune) - 1;
			} else {
				nbRunes = this.ingredients.get(idRune) - 1;
			}
		}
		
        this.ingredients.clear(); // ON RAFRAICHIT LES INGREDIENTS (enleve)
        
        if (nbRunes > 0) // On remet la rune
            this.modifIngredient(this.player, idRune, nbRunes); // Rajout des runes moins une
        
        try {
        	this.player.getCurJobAction().modifIngredient2(this.player, objectFm.getGuid(), 1); // On remet l"item
        }
        catch(Exception e){
        	 //this.player.sendMessage("On est la  :"+ e );
        	 
        	((JobAction) this.player.getExchangeAction().getValue()).modifIngredient2(this.player, objectFm.getGuid(), 1); // On remet l'item dans la case de FM        
        }
        
    }
    
    // NOUVELLE FONCTION DE GESTON PERTE + PUITS ENCORE A CORRIGER UN PEU
    public static GameObject CalculPerteAndPuit (boolean succesN , int statsAdd , String statsObjectFm ,GameObject objectFm, int poidTotal, double poidUnitaire, String loi, int currentWeightTotal, Player player ) {
    	String statsStr = "";
    	String statStringObj = objectFm.parseStatsString() ;
    	int pwrPerte = 0;
    	int puit = objectFm.getPuit();
    	switch (loi) {
    		case "exo" :
    		case "normal" :
			case "over" : { 
				if(succesN)
				{				
					// le cas de l'over dépend du puit restant
					if(poidTotal >= puit) { // Si le puit peut absorber le SN il le prend   puit >= poidTotal
						 //player.sendMessage("On a du puit, on prend dedant et on ajoute "+statsAdd+" de stats" );
						 objectFm.setNewStats(statsObjectFm,statsAdd);
						 
		                 // Si ca suffit On ajoute juste en perdant du reliquat	      						 
		                 objectFm.setPuit(objectFm.getPuit() - poidTotal); 
						 player.sendMessage("- Reliquat");
						 return objectFm;
					}  
					
					else {       
						// S'il reste du puits malgré que ca soit pas suffisant on prend dedans pour limité la perte					
						if(puit > 0) {
							poidTotal -= puit;
							objectFm.setPuit(0);
							player.sendMessage("- Reliquat");
							//player.sendMessage("On a du puit, on prend dedant");
						}
						
						
						// On perd des caractéristiques également (si c'est possible, avant d'ajouté la stats) (Caract adapté avec le puit restant)
						if (!statStringObj.isEmpty()) { 
							statsStr = objectFm.parseStringStatsEC_FM(objectFm, poidTotal, -1);
				            objectFm.clearStats();
				            objectFm.refreshStatsObjet(statsStr);
				            pwrPerte = currentWeightTotal
				                    - currentTotalWeigthBase(statsStr, objectFm);
				            
							// On ajoute la stats du coup
				            objectFm.setNewStats(statsObjectFm,statsAdd);		
				            
						}
						else { // Si c'est pas possible, on ajoute uniquement le nombre de stat avec le puit, arrondi au sup
							
							
					
								
							int StatToAdd = (int)  Math.floor((poidUnitaire)); // PAS FINI   (poidTotal*poidUnitaire) initialement
							if(StatToAdd == 0)
								StatToAdd=1;
							// On ajoute la stats du coup
							System.out.println(poidTotal + " " + poidUnitaire ) ; 
							System.out.println(StatToAdd) ; 
							
							
							 objectFm.setNewStats(statsObjectFm,StatToAdd);	
						}	
						
						if( ( (objectFm.getPuit() + pwrPerte) - poidTotal ) < 0) { // Le puit est négatif
							objectFm.setPuit(0);
						}
						else { // Sinon on applique le puit récupéré avec la baisse des stats
							objectFm.setPuit((objectFm.getPuit() + pwrPerte) - poidTotal);
						}
						return objectFm;
					}
				}
				else {
					// le cas de l'over dépend du puit restant
					if(puit >= poidTotal) { // Si le puit peut absorber l'echec il le prend
						//player.sendMessage("On a du puit, on prend dedant");
						objectFm.setPuit(objectFm.getPuit() - poidTotal); 
						return objectFm;
					}  
					else { 
						
						// Sinon on retire le puit + On perd des caractéristiques (Caract adapté avec le puit restant)
						poidTotal -= puit;
						objectFm.setPuit(0);
						
						// Si les caracteristique ne sont pas vide
						if (!statStringObj.isEmpty()) { 
							statsStr = objectFm.parseStringStatsEC_FM(objectFm, poidTotal, -1);
				            objectFm.clearStats();
				            objectFm.refreshStatsObjet(statsStr);
				            pwrPerte = currentWeightTotal
				                    - currentTotalWeigthBase(statsStr, objectFm);
						}
						
						if( (objectFm.getPuit() + pwrPerte - poidTotal) < 0 ) { // Le puit est négatif
							objectFm.setPuit(0);
						}
						else { // Sinon on applique le puit récupéré avec la baisse des stats
							objectFm.setPuit((objectFm.getPuit() + pwrPerte - poidTotal));
						}
					}
									
				}
			}
			case "autre" : {
				// LE cas de l'exo c'est critique obligatoire ou echec, si les stats ne peuvent pas absorber l'echec on retire toutes les stats mais on ne change pas le puit
				//player.sendMessage("Pas de perte de puit car EXO tenté");
				
				// On perd quand meme des caractérique s'il y en as				
				if (!statStringObj.isEmpty()) {
		            statsStr = objectFm.parseStringStatsEC_FM(objectFm, poidTotal, -1);
		            objectFm.clearStats();
		            objectFm.refreshStatsObjet(statsStr);
		            pwrPerte = currentWeightTotal
		                    - currentTotalWeigthBase(statsStr, objectFm);
		        }
				
			}
    	}
    	return objectFm;
	
    }
     
    // FONCTION PAS UTILISE PEUT ETRE A RETIRER
    /*
    public static void setNewPuit(boolean success, String loi, Player player, GameObject objectFm, int poid, int pwrPerte) {        // La gestion du puit était codé avec le cu

        if( !success ) {
        	switch (loi) {
        		case "over" : { 
        			// le cas de l'over dépend du puit restant
        			if(objectFm.getPuit() > poid) { // Si le puit peut absorber l'echec ou le SN il le prend
        				objectFm.setPuit(objectFm.getPuit() - poid); 
        			}  
        			else { // Sinon on retire le puit + Les caractéristiques perdu (Caract adapté avec le puit restant)
        				if ( ((objectFm.getPuit() + pwrPerte) - poid) < 0) { // Si le puit + carac est tombé en dessous de 0 on le met a 0
        					objectFm.setPuit(0);
        				}
        				else {
        					objectFm.setPuit((objectFm.getPuit() + pwrPerte) - poid); // Si le puit + Carac est positif on recupère le puit restant
        				}
        			}
        		}
        		case "exo" : {
        			// LE cas de l'exo c'est critique obligatoire ou echec, si les stats ne peuevent pas absorber l'echec on retire toutes les stats mais on ne change pas le puit
        			//player.sendMessage("Pas de perte de puit car EXO tenté");
        			
        		}
        		case "normal" : {
        			// le cas normal dépend du puit restant aussi 
        			if(objectFm.getPuit() > poid) { // Si le puit peut absorber l'echec ou le SN il le prend
        				objectFm.setPuit(objectFm.getPuit() - poid); 
        			}  
        			else { // Sinon on retire le puit + Les caractéristiques perdu (Caract adapté avec le puit restant)
        				if ( ((objectFm.getPuit() + pwrPerte) - poid) < 0) { // Si le puit + carac est tombé en dessous de 0 on le met a 0
        					objectFm.setPuit(0);
        				}
        				else {
        					objectFm.setPuit((objectFm.getPuit() + pwrPerte) - poid); // Si le puit + Carac est positif on recupère le puit restant
        				}
        			}
        		}
        	}
        }
        else {
        	 player.sendMessage("Pas de perte de puit car SC");
        }
    }   
    */
    // On donne le max pour une ligne (Attention a bien gerer les négatif dans le futur)
    public static int getStatBaseMax(ObjectTemplate objMod, String statsModif) {
        String[] split = objMod.getStrTemplate().split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) > 0) {
            } else if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) == 0) {
                //World.world.logger.trace(stats[0].toLowerCase()+" ICI/ "+statsModif.toLowerCase());
                int max = Integer.parseInt(stats[2], 16);
                if (max == 0)
                    max = Integer.parseInt(stats[1], 16);
                return max;
            }
        }
        return 0;
    }
    
    // On donne le max pour une ligne avec le legendaire (Attention a bien gerer les négatif dans le futur)
    public static int getStatBaseMaxLegendaire(ObjectTemplate objMod, String statsModif) {   	
    	//System.out.println(statsModif);
    	
        String[] split = objMod.getStrTemplate().split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) > 0) {
            	//System.out.println(stats[0].toLowerCase()+" pas ici/ "+statsModif.toLowerCase());
            	
            } else if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) == 0) {
               //System.out.println(stats[0].toLowerCase()+" ICI/ "+statsModif.toLowerCase());
                int max = Integer.parseInt(stats[2], 16);
                if (max == 0)
                    max = Integer.parseInt(stats[1], 16);
                
                max = (int) Math.floor(max + ((max)*0.5));
                return max;
            }
        }
        return 0;
    }

    // On donne le min pour une ligne de stat donné (Attention a bien gerer les négatif dans le futur)
    public static int getStatBaseMin(ObjectTemplate objMod, String statsModif) {
        String[] split = objMod.getStrTemplate().split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) > 0) {
            } else if (stats[0].toLowerCase().compareTo(statsModif.toLowerCase()) == 0) {
                return Integer.parseInt(stats[1], 16);
            }
        }
        return 0;
    }

    public static int WeithTotalBaseMin(int objTemplateID) {  // Le poid de l'item en Mini
        int weight = 0;
        int alt = 0;
        String statsTemplate = "";
        statsTemplate = World.world.getObjTemplate(objTemplateID).getStrTemplate();
        if (statsTemplate == null || statsTemplate.isEmpty())
            return 0;
        String[] split = statsTemplate.split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
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
                    value = min;
                } catch (Exception e) {
                    value = Formulas.getRandomJet(jet);
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            double coef = getPwrPerEffet(statID);   // On recupère le poid de la stat a l'unité            
            weight = (int) Math.floor(value * coef);
            alt += weight;
        }
        return alt;
    }
  
    public static int WeithTotalBase(int objTemplateID) {   // Poid max de l'item de base
        int weight = 0;
        int alt = 0;
        String statsTemplate = "";
        statsTemplate = World.world.getObjTemplate(objTemplateID).getStrTemplate();
        if (statsTemplate == null || statsTemplate.isEmpty())
            return 0;
        String[] split = statsTemplate.split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
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
            double coef = getPwrPerEffet(statID);   // On recupère le poid de la stat a l'unité            
            weight = (int) Math.floor(value * coef);
            alt += weight;
        }
        return alt;
    }
    
    public static int WeithTotalBaseLegendary(int objTemplateID) {   // Poid max de l'item de base
        int weight = 0;
        int alt = 0;
        String statsTemplate = "";
        statsTemplate = World.world.getObjTemplate(objTemplateID).getStrTemplate();
        if (statsTemplate == null || statsTemplate.isEmpty())
            return 0;
        String[] split = statsTemplate.split(",");
        for (String s : split) {
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
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
            double coef = getPwrPerEffet(statID);   // On recupère le poid de la stat a l'unité            
            weight = (int) Math.floor( (value + ((value)*0.5)) * coef);
            alt += weight;
        }
        return alt;
    }
   
    public static int currentWeithStatsExo(String statsModelo,GameObject obj) {	 // Poid des exos
    	 if (statsModelo.equalsIgnoreCase(""))
             return 0;
         int Weigth = 0;
         int Alto = 0;
         //int rarity = obj.getRarity();
         String[] split = statsModelo.split(",");
         for (String s : split) { // On boucle sur toutes les stats de l'item de base
             String[] stats = s.split("#");
             int statID = Integer.parseInt(stats[0], 16);
             
             
             String StatsHex = Integer.toHexString(statID);
         	if( StatsHex.equals("79") ){
         		StatsHex = "70";
        	}
         	//System.out.println("On test :" + StatsHex);
             int BaseStats = viewBaseStatsItem(obj, StatsHex);
             if (BaseStats == 2 ||  BaseStats == 1) {
             	continue;
             }
             
             if (statID == 985 || statID == 988)
                 continue;
             boolean xy = false;
             for (int a : Constant.ARMES_EFFECT_IDS)
                 if (a == statID)
                     xy = true;
             if (xy)
                 continue;
             String jet = "";
             int qua = 1;
             
             //System.out.println("La stat :" + statID + "considéré comme exo");
             try {
                 jet = stats[4];
                 try {
                     int min = Integer.parseInt(stats[1], 16);
                     int max = Integer.parseInt(stats[2], 16);
                     qua = min;
                     if (max != 0)
                         qua = max; // on prend la statMAX
                 } catch (Exception e) {
                     e.printStackTrace();
                     qua = Formulas.getRandomJet(jet);
                 }
             } catch (Exception e) {
                 // Ok :/
             }
             //World.world.logger.trace("Etrange cette stats"+statID);
             double coef = getPwrPerEffet(statID);   // On recupère le poid de la stat a l'unité            
             Weigth = (int) Math.floor(qua * coef); // On multiplie par le jet
             Alto += Weigth;
         } 
         return Alto;      
    }
    
    public static int currentTotalWeigthBase(String statsModelo, GameObject obj) { // On récupère le poid total de l'item actuel
        if (statsModelo.equalsIgnoreCase(""))
            return 0;
        int Weigth = 0;
        int Alto = 0;
        String[] split = statsModelo.split(",");
        for (String s : split) { // On boucle sur toutes les stats de l'item de base
            String[] stats = s.split("#");
            int statID = Integer.parseInt(stats[0], 16);
            if (statID == 985 || statID == 988)
                continue;
            boolean xy = false;
            for (int a : Constant.ARMES_EFFECT_IDS)
                if (a == statID)
                    xy = true;
            if (xy)
                continue;
            String jet = "";
            int qua = 1;
            try {
                jet = stats[4];
                try {
                    int min = Integer.parseInt(stats[1], 16);
                    int max = Integer.parseInt(stats[2], 16);
                    qua = min;
                    if (max != 0)
                        qua = max; // on prend la statMAX
                } catch (Exception e) {
                    e.printStackTrace();
                    qua = Formulas.getRandomJet(jet);
                }
            } catch (Exception e) {
                // Ok :/
            }
            
            double coef = getPwrPerEffet(statID);   // On recupère le poid de la stat a l'unité            
            Weigth = (int) Math.floor(qua * coef); // On multiplie par le jet
            Alto += Weigth;
        } 
        return Alto;  
    }

    public static double getPwrPerEffet(int effect) {
    	//System.out.println("On test "+effect);
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
                /*
            case Constant.STATS_ADD_DOMA2:
                r = 20.0;
                break;
                */
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
        //System.out.println("On retourne "+r);
        return r;
    }
 
    // Nul a chier cette fonction, on va utiliser le poid max d'une ligne
   /* public static double getOverPerEffet(int effect) {
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
    	//System.out.println("LE poid trouvé "+r);
        return r;
    }
*/
    public static int getBaseMaxJet(int templateID, String statsModif) {
        ObjectTemplate t = World.world.getObjTemplate(templateID);
        String[] splitted = t.getStrTemplate().split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            if (stats[0].compareTo(statsModif) > 0)//Effets n'existe pas de base
            {
            } else if (stats[0].compareTo(statsModif) == 0)//L'effet existe bien !
            {
            	
                int max = Integer.parseInt(stats[2], 16);
                
                if (max == 0)
                    max = Integer.parseInt(stats[1], 16);//Pas de jet maximum on prend le minimum
                return max;
            }
        }
        return 0;
    }
      
    public static int getActualJet(GameObject obj, String statsModif) {
        for (Entry<Integer, Integer> entry : obj.getStats().getMap().entrySet()) {
        	//World.world.logger.trace(Integer.toHexString(entry.getKey())+" / "+statsModif);

            if (Integer.toHexString(entry.getKey()).compareTo(statsModif) > 0)//Effets inutiles
            {
            	
            } 
            else if (Integer.toHexString(entry.getKey()).compareTo(statsModif) == 0)//L'effet existe bien !
            {
                int JetActual = entry.getValue();
                return JetActual;
            }
        }
        return 0;
    }

    public static byte viewActualStatsItem(GameObject obj, String stats)//retourne vrai si le stats est actuellement sur l'item
    {
        if (!obj.parseStatsString().isEmpty()) { // si l'obj est pas vide
            for (Entry<Integer, Integer> entry : obj.getStats().getMap().entrySet()) { // Toutes les entrées 
                if (Integer.toHexString(entry.getKey()).compareTo(stats) > 0)//Effets inutiles
                {
                    if (Integer.toHexString(entry.getKey()).compareTo("98") == 0  // C'est un cas négatif 
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
                    }
                } 
                else if (Integer.toHexString(entry.getKey()).compareTo(stats) == 0)//L'effet existe bien !
                {
                    return 1;
                }
            }
            return 0;
        } else {
            return 0;
        }
    }

    public static byte viewBaseStatsItem(GameObject obj, String ItemStats)//retourne vrai si la stats existe de base sur l'item
    {
    	

        String[] splitted = obj.getTemplate().getStrTemplate().split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");

            //System.out.println("On compare :" + stats[0] + "a " + ItemStats);
            
            if (stats[0].compareTo(ItemStats) > 0)//Effets n'existe pas de base
            {
                if (stats[0].compareTo("98") == 0
                        && ItemStats.compareTo("7b") == 0) { 
                    return 2; // 
                } else if (stats[0].compareTo("9a") == 0
                        && ItemStats.compareTo("77") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9b") == 0
                        && ItemStats.compareTo("7e") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9d") == 0
                        && ItemStats.compareTo("76") == 0) {
                    return 2;
                } else if (stats[0].compareTo("74") == 0
                        && ItemStats.compareTo("75") == 0) {
                    return 2;
                } else if (stats[0].compareTo("99") == 0
                        && ItemStats.compareTo("7d") == 0) {
                    return 2; // Retourne oui mais c'est un négatif
                } else if (stats[0].compareTo("99") == 0
                        && ItemStats.compareTo("7d") == 0) {
                    return 2; // Retourne oui mais c'est un négatif
                } else {
                }
            } else if (stats[0].compareTo(ItemStats) == 0)//L'effet existe bien !
            {
                return 1;
            }
        }
        return 0; // Retourne faux
    }
}