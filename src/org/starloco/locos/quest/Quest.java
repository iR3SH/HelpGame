package org.starloco.locos.quest;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.Database;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.other.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Quest {

    /* Static List */
    public static Map<Integer, Quest> questDataList = new HashMap<Integer, Quest>();

    private int id;
    private ArrayList<Quest_Etape> questEtapeList = new ArrayList<Quest_Etape>();
    private ArrayList<Quest_Objectif> questObjectifList = new ArrayList<Quest_Objectif>();
    private NpcTemplate npc = null;
    private ArrayList<Action> actions = new ArrayList<Action>();
    private boolean delete;
    private Couple<Integer, Integer> condition = null;

    public Quest(int aId, String questEtape, String aObjectif, int aNpc,
                 String action, String args, boolean delete, String condition) {
        this.id = aId;
        this.delete = delete;
        try {
            if (!questEtape.equalsIgnoreCase("")) {
                String[] split = questEtape.split(";");

                if (split != null && split.length > 0) {
                    for (String qEtape : split) {
                        Quest_Etape q_Etape = Quest_Etape.getQuestEtapeById(Integer.parseInt(qEtape));
                        q_Etape.setQuestData(this);
                        questEtapeList.add(q_Etape);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!aObjectif.equalsIgnoreCase("")) {
                String[] split = aObjectif.split(";");

                if (split != null && split.length > 0) {
                    for (String qObjectif : split) {
                        questObjectifList.add(Quest_Objectif.getQuestObjectifById(Integer.parseInt(qObjectif)));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!condition.equalsIgnoreCase("")) {
            try {
                String[] split = condition.split(":");
                if (split != null && split.length > 0) {
                    this.condition = new Couple<Integer, Integer>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.npc = World.world.getNPCTemplate(aNpc);
        try {
            if (!action.equalsIgnoreCase("") && !args.equalsIgnoreCase("")) {
                String[] arguments = args.split(";");
                int nbr = 0;
                for (String loc0 : action.split(",")) {
                    int actionId = Integer.parseInt(loc0);
                    String arg = arguments[nbr];
                    actions.add(new Action(actionId, arg, -1 + "", null));
                    nbr++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            World.world.logger.error("Erreur avec l action et les args de la quete " + this.id + ".");
        }
    }

    /**
     * Static function *
     */
    public static Map<Integer, Quest> getQuestDataList() {
        return questDataList;
    }

    public static Quest getQuestById(int id) {
        return questDataList.get(id);
    }

    public static void setQuestInList(Quest quest) {
        questDataList.put(quest.getId(), quest);
    }

    public boolean isDelete() {
        return this.delete;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Quest_Objectif> getObjectifList() {
        return questObjectifList;
    }

    public NpcTemplate getNpc_Tmpl() {
        return npc;
    }

    public ArrayList<Quest_Etape> getQuestEtapeList() {
        return questEtapeList;
    }

    public boolean haveRespectCondition(QuestPlayer qPerso, Quest_Etape qEtape) {
        switch (qEtape.getCondition()) {
            case "1": //Valider les étapes d'avant
                boolean loc2 = true;
                for (Quest_Etape aEtape : questEtapeList) {
                    if (aEtape == null)
                        continue;
                    if (aEtape.getId() == qEtape.getId())
                        continue;
                    if (!qPerso.isQuestEtapeIsValidate(aEtape))
                        loc2 = false;
                }
                return loc2;

            case "0":
                return true;
        }
        return false;
    }

    public String getGmQuestDataPacket(Player perso) {
        QuestPlayer qPerso = perso.getQuestPersoByQuest(this);
        int loc1 = getObjectifCurrent(qPerso);
        int loc2 = getObjectifPrevious(qPerso);
        int loc3 = getNextObjectif(Quest_Objectif.getQuestObjectifById(getObjectifCurrent(qPerso)));
        StringBuilder str = new StringBuilder();
        str.append(id).append("|");
        str.append(loc1 > 0 ? loc1 : "");
        str.append("|");

        StringBuilder str_prev = new StringBuilder();
        boolean loc4 = true;
        // Il y a une exception dans le code ici pour la seconde étape de papotage
        for (Quest_Etape qEtape : questEtapeList) {
            if (qEtape.getObjectif() != loc1)
                continue;
            if (!haveRespectCondition(qPerso, qEtape))
                continue;
            /*
			 * if(!loc4 && (getNextObjectif(Quest_Objectif.getQuestObjectifById(
			 * getObjectifCurrent(qPerso))) == qEtape.getObjectif() ||
			 * questObjectifList.size() < 2)) { if(qEtape.getType() == 10 &&
			 * qEtape.getId() == getQuestEtapeCurrent(qPerso).getId() && !loc4)
			 * str_prev.append(";"); else if(qEtape.getType() != 10)
			 * str_prev.append(";"); } if(qEtape.getType() == 10 &&
			 * getQuestEtapeCurrent(qPerso).getId() == qEtape.getId()) //On
			 * efface toute les questEtape passé avant str_prev.delete(0,
			 * str_prev.length());
			 * str_prev.append(qEtape.getId()).append(",").append
			 * (qPerso.isQuestEtapeIsValidate(qEtape) ? 1 : 0); if(loc4) loc4 =
			 * false;
			 */
            if (!loc4)
                str_prev.append(";");
            str_prev.append(qEtape.getId());
            str_prev.append(",");
            str_prev.append(qPerso.isQuestEtapeIsValidate(qEtape) ? 1 : 0);
            loc4 = false;
        }
        str.append(str_prev);
        str.append("|");
        str.append(loc2 > 0 ? loc2 : "").append("|");
        str.append(loc3 > 0 ? loc3 : "");
        if (npc != null) {
            str.append("|");
            str.append(npc.getInitQuestionId(perso.getCurMap().getId())).append("|");
        }
        return str.toString();
    }

    public Quest_Etape getQuestEtapeCurrent(QuestPlayer qPerso) {
        for (Quest_Etape qEtape : getQuestEtapeList()) {
            if (!qPerso.isQuestEtapeIsValidate(qEtape))
                return qEtape;
        }
        return null;
    }

    public int getObjectifCurrent(QuestPlayer qPerso) {
        for (Quest_Etape qEtape : questEtapeList) {
            if (qPerso.isQuestEtapeIsValidate(qEtape))
                continue;
            return qEtape.getObjectif();
        }
        return 0;
    }

    public int getObjectifPrevious(QuestPlayer qPerso) {
        if (questObjectifList.size() == 1)
            return 0;
        else {
            int previousqObjectif = 0;
            for (Quest_Objectif qObjectif : questObjectifList) {
                if (qObjectif.getId() == getObjectifCurrent(qPerso))
                    return previousqObjectif;
                else
                    previousqObjectif = qObjectif.getId();
            }
        }
        return 0;
    }

    public int getNextObjectif(Quest_Objectif qO) {
        if (qO == null)
            return 0;
        for (Quest_Objectif qObjectif : questObjectifList) {
            if (qObjectif.getId() == qO.getId()) {
                int index = questObjectifList.indexOf(qObjectif);
                if (questObjectifList.size() <= index + 1)
                    return 0;
                return questObjectifList.get(index + 1).getId();
            }
        }
        return 0;
    }

    public void applyQuest(Player perso) {
        if (this.condition != null) {
            switch (this.condition.first) {
                case 1: // Niveau
                    if (perso.getLevel() < this.condition.second) {
                        SocketManager.GAME_SEND_MESSAGE(perso, "Votre niveau est insuffisant pour apprendre la quête.");
                        return;
                    }
                    break;
            }
        }
        QuestPlayer qPerso = new QuestPlayer(Database.getDynamics().getWorldEntityData().getNextQuestId(), id, false, perso.getId(), "");
        perso.addQuestPerso(qPerso);
        SocketManager.GAME_SEND_Im_PACKET(perso, "054;" + id);
        Database.getDynamics().getQuestPlayerData().add(qPerso);
        //SocketManager.GAME_SEND_MAP_NPCS_GMS_PACKETS(perso.getGameClient(), perso.getCurMap());
        // By Coding Mestre - [FIX] - Fixed a bug that was causing quest's update to kick the player if the quest has no npc assigned
        SocketManager.GAME_SEND_MAP_NPCS_QUEST_UPDATE_SPRITE_PACKETS(perso.getGameClient(), perso.getCurMap(), npc != null ? npc.getId() : -1);

        if (!actions.isEmpty())
            for (Action aAction : actions)
                aAction.apply(perso, perso, -1, -1);
        Database.getStatics().getPlayerData().update(perso);
    }

    public void updateQuestData(Player perso, boolean validation, int type) {
        QuestPlayer qPerso = perso.getQuestPersoByQuest(this);
        for (Quest_Etape qEtape : questEtapeList) {
            if (qEtape.getValidationType() != type)
                continue;

            boolean refresh = false;
            if (qPerso.isQuestEtapeIsValidate(qEtape)) //On a déjà validé la questEtape on passe
                continue;

            if (qEtape.getObjectif() != getObjectifCurrent(qPerso))
                continue;

            if (!haveRespectCondition(qPerso, qEtape))
                continue;

            if (validation)
                refresh = true;
            switch (qEtape.getType()) {

                case 3://Donner item
                    if (perso.getExchangeAction() != null && perso.getExchangeAction().getType() == ExchangeAction.TALKING_WITH && perso.getCurMap().getNpc((Integer) perso.getExchangeAction().getValue()).getTemplate().getId() == qEtape.getNpc().getId()) {
                        for (Entry<Integer, Integer> entry : qEtape.getItemNecessaryList().entrySet()) {
                            if (perso.hasItemTemplate(entry.getKey(), entry.getValue())) { //Il a l'item et la quantité
                                perso.removeByTemplateID(entry.getKey(), entry.getValue()); //On supprime donc
                                refresh = true;
                            }
                        }
                    }
                    break;

                case 0:
                case 1://Aller voir %
                case 9://Retourner voir %
                    if (qEtape.getCondition().equalsIgnoreCase("1")) { //Valider les questEtape avant
                        if (perso.getExchangeAction() != null && perso.getExchangeAction().getType() == ExchangeAction.TALKING_WITH && perso.getCurMap().getNpc((Integer) perso.getExchangeAction().getValue()).getTemplate().getId() == qEtape.getNpc().getId()) {
                            if (haveRespectCondition(qPerso, qEtape)) {
                                refresh = true;
                            }
                        }
                    } else {
                        if (perso.getExchangeAction() != null && perso.getExchangeAction().getType() == ExchangeAction.TALKING_WITH && perso.getCurMap().getNpc((Integer) perso.getExchangeAction().getValue()).getTemplate().getId() == qEtape.getNpc().getId())
                            refresh = true;
                    }
                    break;

                case 6: // monstres
                    for (Entry<Integer, Short> entry : qPerso.getMonsterKill().entrySet())
                        if (entry.getKey() == qEtape.getMonsterId() && entry.getValue() >= qEtape.getQua())
                            refresh = true;
                    break;

                case 10://Ramener prisonnier
                    if (perso.getExchangeAction() != null && perso.getExchangeAction().getType() == ExchangeAction.TALKING_WITH && perso.getCurMap().getNpc((Integer) perso.getExchangeAction().getValue()).getTemplate().getId() == qEtape.getNpc().getId()) {
                        GameObject follower = perso.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR);
                        if (follower != null) {
                            Map<Integer, Integer> itemNecessaryList = qEtape.getItemNecessaryList();
                            for (Entry<Integer, Integer> entry2 : itemNecessaryList.entrySet()) {
                                if (entry2.getKey() == follower.getTemplate().getId()) {
                                    refresh = true;
                                    perso.setMascotte(0);
                                }
                            }
                        }
                    }
                    break;
            }

            if (refresh) {
                Quest_Objectif ansObjectif = Quest_Objectif.getQuestObjectifById(getObjectifCurrent(qPerso));
                qPerso.setQuestEtapeValidate(qEtape);
                SocketManager.GAME_SEND_Im_PACKET(perso, "055;" + id);
                if (haveFinish(qPerso, ansObjectif)) {
                    SocketManager.GAME_SEND_Im_PACKET(perso, "056;" + id);
                    applyButinOfQuest(perso, qPerso, ansObjectif);
                    qPerso.setFinish(true);
                } else {
                    if (getNextObjectif(ansObjectif) != 0) {
                        if (qPerso.overQuestEtape(ansObjectif))
                            applyButinOfQuest(perso, qPerso, ansObjectif);
                    }
                }
                Database.getStatics().getPlayerData().update(perso);
            }
        }
    }

    public boolean haveFinish(QuestPlayer qPerso, Quest_Objectif qO) {
        return qPerso.overQuestEtape(qO) && getNextObjectif(qO) == 0;
    }

    public void applyButinOfQuest(Player perso, QuestPlayer qPerso,
                                  Quest_Objectif ansObjectif) {
        long aXp = 0;

        if ((aXp = ansObjectif.getXp()) > 0) { //Xp a donner
            perso.addXp(aXp * ((int) Config.getInstance().rateXp));
            SocketManager.GAME_SEND_Im_PACKET(perso, "08;"
                    + (aXp * ((int) Config.getInstance().rateXp)));
            SocketManager.GAME_SEND_STATS_PACKET(perso);
        }

        if (ansObjectif.getItem().size() > 0) { //Item a donner
            for (Entry<Integer, Integer> entry : ansObjectif.getItem().entrySet()) {
                ObjectTemplate objT = World.world.getObjTemplate(entry.getKey());
                int qua = entry.getValue();
                GameObject obj = objT.createNewItem(qua, false);
                if (perso.addObjet(obj, true))
                    World.world.addGameObject(obj, true);
                SocketManager.GAME_SEND_Im_PACKET(perso, "021;" + qua + "~"
                        + objT.getId());
            }
        }

        int aKamas = 0;
        if ((aKamas = ansObjectif.getKamas()) > 0) { //Kams a donner
            perso.setKamas(perso.getKamas() + (long) aKamas);
            SocketManager.GAME_SEND_Im_PACKET(perso, "045;" + aKamas);
            SocketManager.GAME_SEND_STATS_PACKET(perso);
        }

        if (getNextObjectif(ansObjectif) != ansObjectif.getId()) { //On passe au nouveau objectif on applique les actions
            for (Action a : ansObjectif.getAction()) {
                a.apply(perso, null, 0, 0);
            }
        }

    }

    public int getQuestEtapeByObjectif(Quest_Objectif qObjectif) {
        int nbr = 0;
        for (Quest_Etape qEtape : getQuestEtapeList()) {
            if (qEtape.getObjectif() == qObjectif.getId())
                nbr++;
        }
        return nbr;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }

    public static class QuestPlayer {
        private int id;
        private Quest quest = null;
        private boolean finish;
        private Player player;
        private Map<Integer, Quest_Etape> questEtapeListValidate = new HashMap<Integer, Quest_Etape>();
        private Map<Integer, Short> monsterKill = new HashMap<Integer, Short>();

        public QuestPlayer(int aId, int qId, boolean aFinish, int pId,
                           String qEtapeV) {
            this.id = aId;
            this.quest = Quest.getQuestById(qId);
            this.finish = aFinish;
            this.player = World.world.getPlayer(pId);
            try {
                String[] split = qEtapeV.split(";");
                if (split != null && split.length > 0) {
                    for (String loc1 : split) {
                        if (loc1.equalsIgnoreCase(""))
                            continue;
                        Quest_Etape qEtape = Quest_Etape.getQuestEtapeById(Integer.parseInt(loc1));
                        questEtapeListValidate.put(qEtape.getId(), qEtape);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public int getId() {
            return id;
        }

        public Quest getQuest() {
            return quest;
        }

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
            if (this.getQuest() != null && this.getQuest().isDelete()) {
                if (this.player != null && this.player.getQuestPerso() != null && this.player.getQuestPerso().containsKey(this.getId())) {
                    this.player.delQuestPerso(this.getId());
                    this.deleteQuestPerso();
                }
            } else if(this.getQuest() == null) {
                if (this.player.getQuestPerso().containsKey(this.getId())) {
                    this.player.delQuestPerso(this.getId());
                    this.deleteQuestPerso();
                }
            }
        }

        public Player getPlayer() {
            return player;
        }

        public boolean isQuestEtapeIsValidate(Quest_Etape qEtape) {
            return questEtapeListValidate.containsKey(qEtape.getId());
        }

        public void setQuestEtapeValidate(Quest_Etape qEtape) {
            if (!questEtapeListValidate.containsKey(qEtape.getId()))
                questEtapeListValidate.put(qEtape.getId(), qEtape);
        }

        public String getQuestEtapeString() {
            StringBuilder str = new StringBuilder();
            int nb = 0;
            for (Quest_Etape qEtape : questEtapeListValidate.values()) {
                nb++;
                str.append(qEtape.getId());
                if (nb < questEtapeListValidate.size())
                    str.append(";");
            }
            return str.toString();
        }

        public Map<Integer, Short> getMonsterKill() {
            return monsterKill;
        }

        public boolean overQuestEtape(Quest_Objectif qObjectif) {
            int nbrQuest = 0;
            for (Quest_Etape qEtape : questEtapeListValidate.values()) {
                if (qEtape.getObjectif() == qObjectif.getId())
                    nbrQuest++;
            }
            return qObjectif.getSizeUnique() == nbrQuest;
        }

        public boolean deleteQuestPerso() {
            return Database.getDynamics().getQuestPlayerData().delete(this.id);
        }
    }

    public static class Quest_Objectif {

        public static Map<Integer, Quest_Objectif> questObjectifList = new HashMap<Integer, Quest_Objectif>();

        private int id;

        /* Butin */
        private int xp;
        private int kamas;
        private Map<Integer, Integer> items = new HashMap<Integer, Integer>();
        private ArrayList<Action> actionList = new ArrayList<Action>();
        private ArrayList<Quest_Etape> questEtape = new ArrayList<Quest_Etape>();

        public Quest_Objectif(int aId, int aXp, int aKamas, String aItems,
                              String aAction) {
            this.id = aId;
            this.xp = aXp;
            this.kamas = aKamas;
            try {
                if (!aItems.equalsIgnoreCase("")) {
                    String[] split = aItems.split(";");
                    if (split != null && split.length > 0) {
                        for (String loc1 : split) {
                            if (loc1.equalsIgnoreCase(""))
                                continue;
                            if (loc1.contains(",")) {
                                String[] loc2 = loc1.split(",");
                                this.items.put(Integer.parseInt(loc2[0]), Integer.parseInt(loc2[1]));
                            } else {
                                this.items.put(Integer.parseInt(loc1), 1);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (aAction != null && !aAction.equalsIgnoreCase("")) {
                    String[] split = aAction.split(";");
                    if (split != null & split.length > 0) {
                        for (String loc1 : split) {
                            String[] loc2 = loc1.split("\\|");
                            int actionId = Integer.parseInt(loc2[0]);
                            String args = loc2[1];
                            Action action = new Action(actionId, args, "-1", null);
                            actionList.add(action);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static Quest_Objectif getQuestObjectifById(int id) {
            return questObjectifList.get(id);
        }

        public static Map<Integer, Quest_Objectif> getQuestObjectifList() {
            return questObjectifList;
        }

        public static void setQuest_Objectif(Quest_Objectif qObjectif) {
            if (!questObjectifList.containsKey(qObjectif.getId())
                    && !questObjectifList.containsValue(qObjectif))
                questObjectifList.put(qObjectif.getId(), qObjectif);
        }

        public int getId() {
            return id;
        }

        public int getXp() {
            return xp;
        }

        public int getKamas() {
            return kamas;
        }

        public Map<Integer, Integer> getItem() {
            return items;
        }

        public ArrayList<Action> getAction() {
            return actionList;
        }

        public int getSizeUnique() {
            int cpt = 0;
            ArrayList<Integer> id = new ArrayList<Integer>();
            for (Quest_Etape qe : questEtape) {
                if (!id.contains(qe.getId())) {
                    id.add(qe.getId());
                    cpt++;
                }
            }
            return cpt;
        }

        public ArrayList<Quest_Etape> getQuestEtapeList() {
            return questEtape;
        }

        public void setEtape(Quest_Etape qEtape) {
            if (!questEtape.contains(qEtape))
                questEtape.add(qEtape);
        }
    }

}
