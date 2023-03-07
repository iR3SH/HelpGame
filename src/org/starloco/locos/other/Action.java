package org.starloco.locos.other;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.labyrinth.PigDragon;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stalk;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.ConditionParser;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.Database;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.entity.npc.Npc;
import org.starloco.locos.entity.npc.NpcQuestion;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.job.Job;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.entity.Animation;
import org.starloco.locos.area.map.entity.House;
import org.starloco.locos.area.map.entity.Tutorial;
import org.starloco.locos.kernel.Main;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.util.TimerWaiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

public class Action {

    private int id;
    private String args;
    private String cond;
    private GameMap map;

    public Action(int id, String args, String cond, GameMap map) {
        this.setId(id);
        this.setArgs(args);
        this.setCond(cond);
        this.setMap(map);
    }

    public static java.util.Map<Integer, Couple<Integer, Integer>> getDopeul() {
        java.util.Map<Integer, Couple<Integer, Integer>> changeDopeul = new HashMap<Integer, Couple<Integer, Integer>>();
        changeDopeul.put(1549, Couple(167, 460)); // Dopeul iop
        changeDopeul.put(1466, Couple(169, 465)); // Dopeul sadida
        changeDopeul.put(1558, Couple(168, 458)); // Dopeul cra
        changeDopeul.put(1470, Couple(162, 464)); // Dopeul enu
        changeDopeul.put(1469, Couple(164, 468)); // Dopeul xelor
        changeDopeul.put(1546, Couple(161, 461)); // Dopeul osa
        changeDopeul.put(1554, Couple(160, 469)); // Dopeul feca
        changeDopeul.put(6928, Couple(166, 462)); // Dopeul eni
        changeDopeul.put(8490, Couple(2691, 466)); // Dopeul panda
        changeDopeul.put(6926, Couple(163, 467)); // Dopeul sram
        changeDopeul.put(1544, Couple(165, 459)); // Dopeul eca
        changeDopeul.put(6949, Couple(455, 463)); // Dopeul sacri
        return changeDopeul;
    }

    private static Couple<Integer, Integer> Couple(int i, int j) {
        return new Couple<Integer, Integer>(i, j);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public boolean apply(final Player player, Player target, int itemID,
                         int cellid) {

        if (player == null)
            return true;
        if (player.getFight() != null) {
            SocketManager.GAME_SEND_MESSAGE(player, "<b>Action impossible,</b> vous êtes en combat !", "000000");
            return true;
        }
        if (!cond.equalsIgnoreCase("") && !cond.equalsIgnoreCase("-1") && !ConditionParser.validConditions(player, cond)) {
            SocketManager.GAME_SEND_Im_PACKET(player, "119");
            return true;
        }

        GameClient client = player.getGameClient();
        switch (id) {
            case -22: //Remettre prisonnier
                if (player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR) != null) {
                    int skinFollower = player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR).getTemplate().getId();
                    int questId = Constant.getQuestByMobSkin(skinFollower);
                    if (questId != -1) {
                        //perso.upgradeQuest(questId);
                        player.setMascotte(1);
                        int itemFollow = Constant.getItemByMobSkin(skinFollower);
                        player.removeByTemplateID(itemFollow, 1);
                    }
                }
                break;
            case -11: //Téléportation map départ é la création d'un personnage (?)
                player.teleport(Constant.getStartMap(player.getClasse()), Constant.getStartCell(player.getClasse()));
                SocketManager.GAME_SEND_WELCOME(player);
                break;
            case -10: //Alignement ange si plus de demon et vice-versa sinon random
                if (player.get_align() == 1 || player.get_align() == 2
                        || player.get_align() == 3)
                    return true;
                int ange = 0;
                int demon = 0;
                int total = 0;
                for (Player i : World.world.getPlayers()) {
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
                    player.modifAlignement(2);
                else if (demon > ange)
                    player.modifAlignement(1);
                else if (demon == ange)
                    player.modifAlignement(Formulas.getRandomValue(1, 2));
                break;
            case -9: //Mettre un titre
                player.setAllTitle(args);
                break;

            case -8: //Ajouter un zaap
                player.verifAndAddZaap(Short.parseShort(args));
                break;

            case -7://Echange doplon		
                Dopeul.getReward(player, Integer.parseInt(args));
                break;

            case -6://Dopeuls
                GameMap mapActuel = player.getCurMap();
                java.util.Map<Integer, Couple<Integer, Integer>> dopeuls = Action.getDopeul();
                Integer IDmob = null;
                if (dopeuls.containsKey((int) mapActuel.getId())) {
                    IDmob = dopeuls.get((int) mapActuel.getId()).first;
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, "Erreur de dopeul, veuillez nous avertir sur le forum.");
                    return true;
                }

                int LVLmob = Formulas.getLvlDopeuls(player.getLevel());
                if (player.getLevel() < 11) {
                    SocketManager.GAME_SEND_MESSAGE(player, "Il faut être niveau 11 minimum pour combattre les dopeuls des temples.");
                    return true;
                }
                int certificat = Constant.getCertificatByDopeuls(IDmob);
                if (certificat == -1)
                    return true;
                if (player.hasItemTemplate(certificat, 1)) {
                    String date = player.getItemTemplate(certificat, 1).getTxtStat().get(Constant.STATS_DATE);
                    long timeStamp = Long.parseLong(date.split("#")[3]);
                    if (System.currentTimeMillis() - timeStamp <= 86400000) {
                        SocketManager.GAME_SEND_MESSAGE(player, "Il faut que tu attendes 24 heures avant de pouvoir combattre ce dopeul.");
                        return true;
                    } else
                        player.removeByTemplateID(certificat, 1);
                }
                boolean b = true;
                if (player.getQuestPerso() != null
                        && !player.getQuestPerso().isEmpty()) {
                    for (Entry<Integer, Quest.QuestPlayer> entry : new HashMap<>(player.getQuestPerso()).entrySet()) {
                        Quest.QuestPlayer qa = entry.getValue();
                        if (qa.getQuest().getId() == dopeuls.get((int) mapActuel.getId()).second) {
                            b = false;
                            if (qa.isFinish()) {
                                player.delQuestPerso(entry.getKey());
                                if (qa.deleteQuestPerso()) {
                                    Quest q = Quest.getQuestById(dopeuls.get((int) mapActuel.getId()).second);
                                    q.applyQuest(player);
                                }
                            }
                        }
                    }
                }
                if (b) {
                    Quest q = Quest.getQuestById(dopeuls.get((int) mapActuel.getId()).second);
                    q.applyQuest(player);
                }
                String grp = IDmob + "," + LVLmob + "," + LVLmob + ";";
                Monster.MobGroup MG = new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurCell().getId(), grp);
                player.getCurMap().startFigthVersusDopeuls(player, MG);
                break;
            case -5://Apprendre un sort
                try {
                    int sID = Integer.parseInt(args);
                    if (World.world.getSort(sID) == null)
                        return true;
                    player.learnSpell(sID, 1, true, true, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case -4://Prison
                switch (Short.parseShort(args)) {
                    case 1://Payer
                        player.leaveEnnemyFactionAndPay(player);
                        break;
                    case 2: //Attendre 10minutes
                        player.leaveEnnemyFaction();
                        break;
                }
                break;
            case -3://Mascotte
                int idMascotte = Integer.parseInt(args);

                if (player.hasItemTemplate(itemID, 1)) {
                    player.removeByTemplateID(itemID, 1);
                    player.setMascotte(idMascotte);
                    Database.getStatics().getPlayerData().update(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + itemID);
                    SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                }
                break;
            case -2://créer guilde
                if (player.isAway())
                    return true;
                if (player.get_guild() != null || player.getGuildMember() != null) {
                    SocketManager.GAME_SEND_gC_PACKET(player, "Ea");
                    return true;
                }
                if (player.hasItemTemplate(1575, 1)) {
                    SocketManager.GAME_SEND_gn_PACKET(player);
                    player.removeByTemplateID(1575, -1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + -1 + "~"
                            + 1575);
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, "Pour pouvoir créer une guilde, il faut posséder une Guildalogemme.");
                }
                break;

            case -1://Ouvrir banque
                //Sauvagarde du perso et des item avant.  
                boolean ok = false;
                for (Npc npc : player.getCurMap().getNpcs().values())
                    if (npc.getTemplate().getGfxId() == 9048)
                        ok = true;

                if (ok) {
                    Database.getStatics().getPlayerData().update(player);
                    if (player.getDeshonor() >= 1) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "183");
                        return true;
                    }
                    final int cost = player.getBankCost();
                    if (cost > 0) {
                        final long playerKamas = player.getKamas();
                        final long kamasRemaining = playerKamas - cost;
                        final long bankKamas = player.getAccount().getBankKamas();
                        final long totalKamas = bankKamas + playerKamas;
                        if (kamasRemaining < 0)//Si le joueur n'a pas assez de kamas SUR LUI pour ouvrir la banque
                        {
                            if (bankKamas >= cost) {
                                player.setBankKamas(bankKamas - cost); //On modifie les kamas de la banque
                            } else if (totalKamas >= cost) {
                                player.setKamas(0); //On puise l'entiéreter des kamas du joueurs. Ankalike ?
                                player.setBankKamas(totalKamas - cost); //On modifie les kamas de la banque
                                SocketManager.GAME_SEND_STATS_PACKET(player);
                                SocketManager.GAME_SEND_Im_PACKET(player, "020;"
                                        + playerKamas);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE_SERVER(player, "10|"
                                        + cost);
                                return true;
                            }
                        } else
                        //Si le joueur a les kamas sur lui on lui retire directement
                        {
                            player.setKamas(kamasRemaining);
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                            SocketManager.GAME_SEND_Im_PACKET(player, "020;"
                                    + cost);
                        }
                    }
                    SocketManager.GAME_SEND_ECK_PACKET(player.getGameClient(), 5, "");
                    SocketManager.GAME_SEND_EL_BANK_PACKET(player);
                    player.setAway(true);
                    player.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_BANK, 0));
                }
                break;

            case 0://Téléportation
                try {
                    short newMapID = Short.parseShort(args.split(",", 2)[0]);
                    int newCellID = Integer.parseInt(args.split(",", 2)[1]);
                    if (!player.isInPrison()) {
                        player.teleport(newMapID, newCellID);
                    } else {
                        if (player.getCurCell().getId() == 268) {
                            player.teleport(newMapID, newCellID);
                        }
                    }
                } catch (Exception e) {
                    // Pas ok, mais il y a trop de dialogue de PNJ buggé pour laisser cette erreur flood.
                    // e.printStackTrace();
                    return true;
                }
                break;

            case 1://Discours NPC
                if(client == null) return true;
                if (args.equalsIgnoreCase("DV")) {
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                } else {
                    int qID = -1;
                    try {
                        qID = Integer.parseInt(args);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    NpcQuestion quest = World.world.getNPCQuestion(qID);
                    if (quest == null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        return true;
                    }
                    try {
                        SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 2://Téléportation
                try {
                    short newMapID = Short.parseShort(args.split(",")[0]);
                    int newCellID = Integer.parseInt(args.split(",")[1]);
                    int verifMapID = Integer.parseInt(args.split(",")[2]);
                    if (player.getCurMap().getId() == verifMapID)
                        player.teleport(newMapID, newCellID);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                break;

            case 4://Kamas
                try {
                    int count = Integer.parseInt(args);
                    long curKamas = player.getKamas();
                    long newKamas = curKamas + count;
                    if (newKamas < 0) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "084;1");
                        return true;
                    } else {
                        player.setKamas(newKamas);
                        SocketManager.GAME_SEND_Im_PACKET(player, "046;" + count);
                        if (player.isOnline())
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;
            case 5://objet
                try {
                    int tID = Integer.parseInt(args.split(",")[0]);
                    int count = Integer.parseInt(args.split(",")[1]);
                    boolean send = true;
                    if (args.split(",").length > 2)
                        send = args.split(",")[2].equals("1");

                    //Si on ajoute
                    if (count > 0) {
                        ObjectTemplate T = World.world.getObjTemplate(tID);
                        if (T == null)
                            return true;
                        GameObject O = T.createNewItem(count, false);
                        //Si retourne true, on l'ajoute au monde
                        if (player.addObjet(O, true))
                            World.world.addGameObject(O, true);
                    } else {
                        player.removeByTemplateID(tID, -count);
                    }
                    //Si en ligne (normalement oui)
                    if (player.isOnline())//on envoie le packet qui indique l'ajout//retrait d'un item
                    {
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                        if (send) {
                            if (count >= 0) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                        + count + "~" + tID);
                            } else if (count < 0) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + -count + "~" + tID);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;
            case 6://Apprendre un métier
                try {
                    if(client == null) return true;
                    player.setIsOnDialogAction(1);
                    int mID = Integer.parseInt(args.split(",")[0]);
                    int mapId = Integer.parseInt(args.split(",")[1]);
                    int sucess = Integer.parseInt(args.split(",")[2]);
                    int fail = Integer.parseInt(args.split(",")[3]);
                    if (World.world.getMetier(mID) == null)
                        return true;
                    // Si c'est un métier 'basic' :
                    if (mID == 2 || mID == 11 || mID == 13 || mID == 14
                            || mID == 15 || mID == 16 || mID == 17 || mID == 18
                            || mID == 19 || mID == 20 || mID == 24 || mID == 25
                            || mID == 26 || mID == 27 || mID == 28 || mID == 31
                            || mID == 36 || mID == 41 || mID == 56 || mID == 58
                            || mID == 60 || mID == 65) {
                        if (player.getMetierByID(mID) != null)//Métier déjé appris
                        {
                            SocketManager.GAME_SEND_Im_PACKET(player, "111");
                            SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                            player.setExchangeAction(null);
                            player.setIsOnDialogAction(-1);
                            return true;
                        }
                        if (player.getMetierByID(2) != null
                                && player.getMetierByID(2).get_lvl() < 30
                                || player.getMetierByID(11) != null
                                && player.getMetierByID(11).get_lvl() < 30
                                || player.getMetierByID(13) != null
                                && player.getMetierByID(13).get_lvl() < 30
                                || player.getMetierByID(14) != null
                                && player.getMetierByID(14).get_lvl() < 30
                                || player.getMetierByID(15) != null
                                && player.getMetierByID(15).get_lvl() < 30
                                || player.getMetierByID(16) != null
                                && player.getMetierByID(16).get_lvl() < 30
                                || player.getMetierByID(17) != null
                                && player.getMetierByID(17).get_lvl() < 30
                                || player.getMetierByID(18) != null
                                && player.getMetierByID(18).get_lvl() < 30
                                || player.getMetierByID(19) != null
                                && player.getMetierByID(19).get_lvl() < 30
                                || player.getMetierByID(20) != null
                                && player.getMetierByID(20).get_lvl() < 30
                                || player.getMetierByID(24) != null
                                && player.getMetierByID(24).get_lvl() < 30
                                || player.getMetierByID(25) != null
                                && player.getMetierByID(25).get_lvl() < 30
                                || player.getMetierByID(26) != null
                                && player.getMetierByID(26).get_lvl() < 30
                                || player.getMetierByID(27) != null
                                && player.getMetierByID(27).get_lvl() < 30
                                || player.getMetierByID(28) != null
                                && player.getMetierByID(28).get_lvl() < 30
                                || player.getMetierByID(31) != null
                                && player.getMetierByID(31).get_lvl() < 30
                                || player.getMetierByID(36) != null
                                && player.getMetierByID(36).get_lvl() < 30
                                || player.getMetierByID(41) != null
                                && player.getMetierByID(41).get_lvl() < 30
                                || player.getMetierByID(56) != null
                                && player.getMetierByID(56).get_lvl() < 30
                                || player.getMetierByID(58) != null
                                && player.getMetierByID(58).get_lvl() < 30
                                || player.getMetierByID(60) != null
                                && player.getMetierByID(60).get_lvl() < 30
                                || player.getMetierByID(65) != null
                                && player.getMetierByID(65).get_lvl() < 30) {
                            if (sucess == -1 || fail == -1) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                player.setExchangeAction(null);
                                player.setIsOnDialogAction(-1);
                                SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                            } else
                                SocketManager.send(client, "DQ" + fail + "|4840");
                            return true;
                        }
                        if (player.totalJobBasic() > 2)//On compte les métiers déja acquis si c'est supérieur a 2 on ignore
                        {
                            SocketManager.GAME_SEND_Im_PACKET(player, "19");
                            SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                            player.setExchangeAction(null);
                            player.setIsOnDialogAction(-1);
                            return true;
                        } else
                        //Si c'est < ou = é 2 on apprend
                        {
                            if (mID == 27) {
                                if (!player.hasItemTemplate(966, 1))
                                    return true;
                                player.removeByTemplateID(966, 1);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + 966 + "~" + 1);
                                player.learnJob(World.world.getMetier(mID));
                            } else {
                                if (player.getCurMap().getId() != mapId)
                                    return true;
                                player.learnJob(World.world.getMetier(mID));
                                if (sucess == -1 || fail == -1) {
                                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                    player.setExchangeAction(null);
                                    player.setIsOnDialogAction(-1);
                                } else
                                    SocketManager.send(client, "DQ" + sucess
                                            + "|4840");
                            }
                        }
                    }
                    // Si c'est une specialisations 'FM' :
                    /*
					 * if(mID == 43 || mID == 44 || mID == 45 || mID == 46 ||
					 * mID == 47 || mID == 48 || mID == 49 || mID == 50 || mID
					 * == 62 || mID == 63 || mID == 64) { //Si necessaire lvl
					 * 65, enlevé les hide si ankalike
					 * if(perso.getMetierByID(17) != null &&
					 * perso.getMetierByID(17).get_lvl() < 65 && mID == 43 ||
					 * perso.getMetierByID(11) != null &&
					 * perso.getMetierByID(11).get_lvl() < 65 && mID == 44 ||
					 * perso.getMetierByID(14) != null &&
					 * perso.getMetierByID(14).get_lvl() < 65 && mID == 45 ||
					 * perso.getMetierByID(20) != null &&
					 * perso.getMetierByID(20).get_lvl() < 65 && mID == 46 ||
					 * perso.getMetierByID(31) != null &&
					 * perso.getMetierByID(31).get_lvl() < 65 && mID == 47 ||
					 * perso.getMetierByID(13) != null &&
					 * perso.getMetierByID(13).get_lvl() < 65 && mID == 48 ||
					 * perso.getMetierByID(19) != null &&
					 * perso.getMetierByID(19).get_lvl() < 65 && mID == 49 ||
					 * perso.getMetierByID(18) != null &&
					 * perso.getMetierByID(18).get_lvl() < 65 && mID == 50 ||
					 * perso.getMetierByID(15) != null &&
					 * perso.getMetierByID(15).get_lvl() < 65 && mID == 62 ||
					 * perso.getMetierByID(16) != null &&
					 * perso.getMetierByID(16).get_lvl() < 65 && mID == 63 ||
					 * perso.getMetierByID(27) != null &&
					 * perso.getMetierByID(27).get_lvl() < 65 && mID == 64) {
					 * //On compte les specialisations déja acquis si c'est
					 * supérieur a 2 on ignore if(perso.getMetierByID(mID) !=
					 * null)//Métier déjé appris
					 * SocketManager.GAME_SEND_Im_PACKET(perso, "111");
					 * if(perso.totalJobFM() > 2)//On compte les métiers déja
					 * acquis si c'est supérieur a 2 on ignore
					 * SocketManager.GAME_SEND_Im_PACKET(perso, "19"); else//Si
					 * c'est < ou = é 2 on apprend
					 * perso.learnJob(World.world.getMetier(mID)); }else {
					 * SocketManager.GAME_SEND_Im_PACKET(perso, "12"); } }
					 */
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 7://retour au point de sauvegarde
                if (!player.isInPrison())
                    player.warpToSavePos();
                break;

            case 8://Ajouter une Stat
                try {
                    int statID = Integer.parseInt(args.split(",", 2)[0]);
                    int number = Integer.parseInt(args.split(",", 2)[1]);
                    player.getStats().addOneStat(statID, number);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                    int messID = 0;
                    switch (statID) {
                        case Constant.STATS_ADD_INTE:
                            messID = 14;
                            break;
                    }
                    if (messID > 0)
                        SocketManager.GAME_SEND_Im_PACKET(player, "0" + messID
                                + ";" + number);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                break;

            case 9://Apprendre un sort
                try {
                    int sID = Integer.parseInt(args.split(",", 2)[0]);
                    int mapId = Integer.parseInt(args.split(",", 2)[1]);
                    if (World.world.getSort(sID) == null)
                        return true;
                    if (player.getCurMap().getId() != mapId)
                        return true;
                    player.learnSpell(sID, 1, true, true, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 10://Pain/potion/viande/poisson
                try {
                    int min = Integer.parseInt(args.split(",", 2)[0]);
                    int max = Integer.parseInt(args.split(",", 2)[1]);
                    if (max == 0)
                        max = min;
                    int val = Formulas.getRandomValue(min, max);
                    if (target != null) {
                        if (target.getCurPdv() + val > target.getMaxPdv())
                            val = target.getMaxPdv() - target.getCurPdv();
                        target.setPdv(target.getCurPdv() + val);
                        SocketManager.GAME_SEND_STATS_PACKET(target);
                    } else {
                        if (player.getCurPdv() + val > player.getMaxPdv())
                            val = player.getMaxPdv() - player.getCurPdv();
                        player.setPdv(player.getCurPdv() + val);
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 11://Definir l'alignement
                try {
                    byte newAlign = Byte.parseByte(args.split(",", 2)[0]);
                    boolean replace = Integer.parseInt(args.split(",", 2)[1]) == 1;
                    if (player.get_align() != Constant.ALIGNEMENT_NEUTRE && !replace)
                        return true;
                    player.modifAlignement(newAlign);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 12://Spawn d'un groupe de monstre
                try {
                    boolean delObj = args.split(",")[0].equals("true");
                    boolean inArena = args.split(",")[1].equals("true");

                    if (inArena && SoulStone.isInArenaMap(player.getCurMap().getId()))
                        return true;

                    SoulStone pierrePleine = (SoulStone) World.world.getGameObject(itemID);

                    String groupData = pierrePleine.parseGroupData();
                    String condition = "MiS = " + player.getId(); //Condition pour que le groupe ne soit lanéable que par le personnage qui é utiliser l'objet
                    player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), groupData, condition);

                    if (delObj)
                        player.removeItem(itemID, 1, true, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 13: //Reset Carac incarnam
            	player.restatKeepParcho();
                SocketManager.GAME_SEND_STATS_PACKET(player);
                break;

            case 14://Ouvrir l'interface d'oublie de sort incarnam
                if (player.getLevel() <= 1000 || Main.serverId == 38) {
                    player.setExchangeAction(new ExchangeAction<>(ExchangeAction.FORGETTING_SPELL, 0));
                    SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', player);
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, "Ton niveau est supérieur à 30. Tu ne peux donc pas te restaurer !");
                }
                break;

            case 15://Téléportation donjon
                try {
                    short newMapID = Short.parseShort(args.split(",")[0]);
                    int newCellID = Integer.parseInt(args.split(",")[1]);
                    int ObjetNeed = Integer.parseInt(args.split(",")[2]);
                    int MapNeed = Integer.parseInt(args.split(",")[3]);
                    if (ObjetNeed == 0) {
                        //Téléportation sans objets
                        player.teleport(newMapID, newCellID);
                    } else if (ObjetNeed > 0) {
                        if (MapNeed == 0) {
                            //Téléportation sans map
                            player.teleport(newMapID, newCellID);
                        } else if (MapNeed > 0) {
                            if (player.hasItemTemplate(ObjetNeed, 1)
                                    && player.getCurMap().getId() == MapNeed) {
                                //Le perso a l'item
                                //Le perso est sur la bonne map
                                //On téléporte, on supprime aprés
                                player.teleport(newMapID, newCellID);
                                player.removeByTemplateID(ObjetNeed, 1);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                            } else if (player.getCurMap().getId() != MapNeed) {
                                //Le perso n'est pas sur la bonne map
                                SocketManager.GAME_SEND_MESSAGE(player, "Vous n'êtes pas sur la bonne map du donjon pour être téléporté.", "009900");
                            } else {
                                //Le perso ne posséde pas l'item
                                SocketManager.GAME_SEND_MESSAGE(player, "Vous ne possédez pas la clef nécessaire.", "009900");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 16://Téléportation donjon sans perte de clef
                try {
                    short newMapID = Short.parseShort(args.split(",")[0]);
                    int newCellID = Integer.parseInt(args.split(",")[1]);
                    int ObjetNeed = Integer.parseInt(args.split(",")[2]);
                    int MapNeed = Integer.parseInt(args.split(",")[3]);
                    if (ObjetNeed == 0) {
                        //Téléportation sans objets
                        player.teleport(newMapID, newCellID);
                    } else if (ObjetNeed > 0) {
                        if (MapNeed == 0) {
                            //Téléportation sans map
                            player.teleport(newMapID, newCellID);
                        } else if (MapNeed > 0) {
                            if (player.hasItemTemplate(ObjetNeed, 1)
                                    && player.getCurMap().getId() == MapNeed) {
                                //Le perso a l'item
                                //Le perso est sur la bonne map
                                //On téléporte
                                player.teleport(newMapID, newCellID);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                            } else if (player.getCurMap().getId() != MapNeed) {
                                //Le perso n'est pas sur la bonne map
                                SocketManager.GAME_SEND_MESSAGE(player, "Vous n'êtes pas sur la bonne map du donjon pour être téléporté.", "009900");
                            } else {
                                //Le perso ne posséde pas l'item
                                SocketManager.GAME_SEND_MESSAGE(player, "Vous ne possedez pas la clef nécessaire.", "009900");
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 17://Xp métier JobID,XpValue
                try {
                    int JobID = Integer.parseInt(args.split(",")[0]);
                    int XpValue = Integer.parseInt(args.split(",")[1]);
                    if (player.getMetierByID(JobID) != null) {
                        player.getMetierByID(JobID).addXp(player, XpValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 18://Téléportation chez soi
                if (House.alreadyHaveHouse(player))//Si il a une maison
                {
                    GameObject obj2 = World.world.getGameObject(itemID);
                    if (player.hasItemTemplate(obj2.getTemplate().getId(), 1)) {
                        player.removeByTemplateID(obj2.getTemplate().getId(), 1);
                        House h = House.getHouseByPerso(player);
                        if (h == null)
                            return true;
                        player.teleport((short) h.getHouseMapId(), h.getHouseCellId());
                    }
                }
                break;

            case 19://Téléportation maison de guilde (ouverture du panneau de guilde)
                SocketManager.GAME_SEND_GUILDHOUSE_PACKET(player);
                break;

            case 20://+Points de sorts
                try {
                    int pts = Integer.parseInt(args);
                    if (pts < 1)
                        return true;
                    player.addSpellPoint(pts);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 21://+Energie
                try {
                    int energyMin = Integer.parseInt(args.split(",", 2)[0]);
                    int energyMax = Integer.parseInt(args.split(",", 2)[1]);
                    if (energyMax == 0)
                        energyMax = energyMin;
                    int val = Formulas.getRandomValue(energyMin, energyMax);
                    int EnergyTotal = player.getEnergy() + val;
                    if (EnergyTotal > 10000)
                        EnergyTotal = 10000;
                    player.setEnergy(EnergyTotal);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 22://+Xp
                try {
                    long XpAdd = Integer.parseInt(args);
                    if (XpAdd < 1)
                        return true;

                    long TotalXp = player.getExp() + XpAdd;
                    player.setExp(TotalXp);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 23://UnlearnJob
                int Job = Integer.parseInt(args.split(",", 2)[0]);
                int mapId = Integer.parseInt(args.split(",", 2)[1]);
                if (player.getCurMap().getId() != mapId)
                    return true;
                if (Job < 1)
                    return true;
                JobStat m2 = player.getMetierByID(Job);
                if (m2 == null)
                    return true;
                player.unlearnJob(m2.getId());
                SocketManager.GAME_SEND_STATS_PACKET(player);
                Database.getStatics().getPlayerData().update(player);
                break;

            case 24://Morph
                try {
                    int morphID = Integer.parseInt(args);
                    if (morphID < 0)
                        return true;
                    player.setGfxId(morphID);
                    SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), player.getId());
                    SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(player.getCurMap(), player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 25://SimpleUnMorph
                int UnMorphID = player.getClasse() * 10 + player.getSexe();
                player.setGfxId(UnMorphID);
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), player.getId());
                SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(player.getCurMap(), player);
                break;

            case 26://Téléportation enclos de guilde (ouverture du panneau de guilde)
                SocketManager.GAME_SEND_GUILDENCLO_PACKET(player);
                break;

            case 27://Lancement de combat : startFigthVersusMonstres args : monsterID,monsterLevel| ...
                // id,lvl|id,lvl:mapid
                String ValidMobGroup = "";
                if (player.getFight() != null)
                    return true;
                try {
                    int mapId1 = Integer.parseInt(args.split(":", 2)[1]);
                    if (player.getCurMap().getId() != mapId1)
                        return true;
                    for (String MobAndLevel : args.split(":", 2)[0].split("\\|")) {
                        int monsterID = -1;
                        int monsterLevel = -1;
                        String[] MobOrLevel = MobAndLevel.split(",");
                        monsterID = Integer.parseInt(MobOrLevel[0]);
                        monsterLevel = Integer.parseInt(MobOrLevel[1]);

                        if (World.world.getMonstre(monsterID) == null
                                || World.world.getMonstre(monsterID).getGradeByLevel(monsterLevel) == null) {
                            continue;
                        }
                        ValidMobGroup += monsterID + "," + monsterLevel + ","
                                + monsterLevel + ";";
                    }
                    if (ValidMobGroup.isEmpty())
                        return true;
                    Monster.MobGroup group = new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurCell().getId(), ValidMobGroup);
                    player.getCurMap().startFightVersusMonstres(player, group); // Si bug startfight, voir "//Respawn d'un groupe fix" dans fight.java
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 28://Desapprendre un sort
                try {
                    int sID = Integer.parseInt(args);
                    int AncLevel = player.getSortStatBySortIfHas(sID).getLevel();
                    if (player.getSortStatBySortIfHas(sID) == null)
                        return true;
                    if (AncLevel <= 1)
                        return true;
                    player.unlearnSpell(player, sID, 1, AncLevel, true, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 29://Desapprendre un sort avec kamas
                long pKamas3 = player.getKamas();
                int payKamas = player.getLevel() * player.getLevel() * 25;

                if (pKamas3 >= payKamas) {
                    long pNewKamas3 = pKamas3 - payKamas;
                    if (pNewKamas3 < 0)
                        pNewKamas3 = 0;
                    int sID = Integer.parseInt(args);
                    int AncLevel = player.getSortStatBySortIfHas(sID).getLevel();
                    if (player.getSortStatBySortIfHas(sID) == null)
                        return true;
                    if (AncLevel <= 1)
                        return true;
                    player.unlearnSpell(player, sID, 1, AncLevel, true, true);
                    SocketManager.GAME_SEND_MESSAGE(player, "Tu as perdu "
                            + payKamas + "Kamas.");
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, "Tu n'as pas assez de kamas pour effectuer cette action.");
                    return true;
                }
                break;

            case 30: //Change la taille d'un personnage size
                int size = Integer.parseInt(args);
                player.set_size(size);
                SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), player.getId());
                SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(player.getCurMap(), player);
                break;

            case 31:// change classe(zobal)
                SocketManager.GAME_SEND_MESSAGE(player, "Vous vous êtes fait enculer. Cordialement, le staff.");
				/*
				 * try { int classe = Integer.parseInt(args); if (classe ==
				 * perso.getClasse()) { SocketManager.GAME_SEND_MESSAGE(perso,
				 * "Vous étes déjé de cette classe."); return true; } int level
				 * = perso.getLevel(); perso.setClasse(classe); Stats baseStats
				 * = perso.getStats(); baseStats.addOneStat(125,
				 * -perso.getStats().getEffect(125)); baseStats.addOneStat(124,
				 * -perso.getStats().getEffect(124)); baseStats.addOneStat(118,
				 * -perso.getStats().getEffect(118)); baseStats.addOneStat(123,
				 * -perso.getStats().getEffect(123)); baseStats.addOneStat(119,
				 * -perso.getStats().getEffect(119)); baseStats.addOneStat(126,
				 * -perso.getStats().getEffect(126)); perso.setCapital(0);
				 * perso.set_spellPts(0);
				 * perso.setSpells(Constant.getStartSorts(classe));
				 * perso.setLevel(1); while (perso.getLevel() < level) {
				 * perso.levelUp(false, false); } int morph = classe * 10 +
				 * perso.getSexe(); perso.setGfxId(morph);
				 * SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP
				 * (perso.getCurMap(), perso.getId());
				 * SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(perso.getCurMap(),
				 * perso); SocketManager.GAME_SEND_STATS_PACKET(perso);
				 * SocketManager.GAME_SEND_ASK(client, perso);
				 * SocketManager.GAME_SEND_SPELL_LIST(perso);
				 * SqlPlayer.updateInfos(perso); } catch (Exception e) {
				 * e.printStackTrace(); }
				 */
                break;

            case 33:// Stat max a un obj pos
                int posItem = Integer.parseInt(args);
                GameObject itemPos = player.getObjetByPos(posItem);
                if (itemPos != null) {
                    itemPos.clearStats();
                    Stats maxStats = itemPos.generateNewStatsFromTemplate(itemPos.getTemplate().getStrTemplate(), true);
                    itemPos.setStats(maxStats);
                    int idObjPos = itemPos.getGuid();
                    player.removeItem(itemID, 1, true, true);
                    SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(player, idObjPos);
                    SocketManager.GAME_SEND_OAKO_PACKET(player, itemPos);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "Il n'y a aucune item dans la pos choisi.");
                }
                break;

            case 34://Loterie
                int idLot = Integer.parseInt(args.split(",", 2)[0]);
                //int mapId1 = Integer.parseInt(args.split(",", 2)[1]);
                Loterie.startLoterie(player, idLot);
                break;

            case 35: //Reset Carac condition : Map xélor 741 et l'obre de recons 10563
                try {
                    if (player.getCurMap().getId() != 741
                            || !player.hasItemTemplate(10563, 1))
                        return true;
                    player.removeByTemplateID(10563, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10563
                            + "~" + 1);
                    player.restatAll(0);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 36: //Cout d'un jeu
                try {
                    long price = Integer.parseInt(args.split(";")[0]);
                    int tutorial = Integer.parseInt(args.split(";")[1]);
                    if (tutorial == 30) {
                        int random = Formulas.getRandomValue(1, 200);
                        if (random == 100)
                            tutorial = 31;
                        else
                            Database.getDynamics().getNpcQuestionData().updateLot();
                    }
                    final Tutorial tuto = World.world.getTutorial(tutorial);
                    if (tuto == null)
                        return true;
                    if (player.getKamas() >= price) {
                        if (price != 0L) {
                            player.setKamas(player.getKamas() - price);
                            if (player.isOnline())
                                SocketManager.GAME_SEND_STATS_PACKET(player);
                            SocketManager.GAME_SEND_Im_PACKET(player, "046;"
                                    + price);
                        }
                        try {
                            tuto.getStart().apply(player, null, -1, (short) -1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        TimerWaiter.addNext(() -> {
                            SocketManager.send(player, "TC" + tuto.getId() + "|7001010000");
                            player.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_TUTORIAL, tuto));
                            player.setAway(true);
                        }, 1500, TimerWaiter.DataType.CLIENT);
                        return true;
                    }
                    SocketManager.GAME_SEND_Im_PACKET(player, "182");
                } catch (Exception e23) {
                    e23.printStackTrace();
                }
                break;

            case 37://Loterie pioute
                Loterie.startLoteriePioute(player);
                break;

            case 38://Apprendre une émote
                player.addStaticEmote(Integer.parseInt(args));
                break;

            case 40: //Donner une quéte
                int QuestID = Integer.parseInt(args);
                boolean problem = false;
                Quest quest0 = Quest.getQuestById(QuestID);
                if (quest0 == null) {
                    SocketManager.GAME_SEND_MESSAGE(player, "La quête est introuvable.");
                    problem = true;
                    break;
                }
                for (Quest.QuestPlayer qPerso : player.getQuestPerso().values()) {
                    if (qPerso.getQuest().getId() == QuestID) {
                        SocketManager.GAME_SEND_MESSAGE(player, "Vous connaissez déjà  cette quête.");
                        problem = true;
                        break;
                    }
                }

                if (!problem)
                    quest0.applyQuest(player);
                break;

            case 41: //Confirm objective
                break;

            case 42: //Monte prochaine étape quete ou termine
                break;

            case 43: //Téléportation de quéte
                String[] split = args.split(";");
                int mapid = Integer.parseInt(split[0].split(",")[0]);
                cellid = Integer.parseInt(split[0].split(",")[1]);
                int mapsecu = Integer.parseInt(split[1]);
                int questId = Integer.parseInt(split[2]);

                if (player.getCurMap().getId() != mapsecu)
                    return true;
                Quest.QuestPlayer questt = player.getQuestPersoByQuestId(questId);
                if (questt == null || !questt.isFinish())
                    return true;
                player.teleport((short) mapid, cellid);
                break;

            case 44: //Commande admin level up \ niveau
                int count = Integer.parseInt(args);
                if (player.getLevel() < count) {
                    while (player.getLevel() < count)
                        player.levelUp(false, true);
                    if (player.isOnline()) {
                        SocketManager.GAME_SEND_SPELL_LIST(player);
                        SocketManager.GAME_SEND_NEW_LVL_PACKET(player.getGameClient(), player.getLevel());
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    }
                }
                break;

            case 50: //Traque
                if (player.get_align() == 0 || player.get_align() == 3)
                    return true;

                if (player.get_traque() != null && player.get_traque().getTime() == -2) {
                    long xp = Formulas.getXpStalk(player.getLevel());
                    player.addXp(xp);
                    player.set_traque(null);//On supprime la traque
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous venez de recevoir " + xp + " points d'experiences.", "000000");
                    return true;
                } else if(player.get_traque() != null) {
                    SocketManager.GAME_SEND_MESSAGE(player, "Thomas Sacre : Reviens me voir quand tu auras abattu un ennemi.", "000000");
                    return true;
                }

                if (player.get_traque() == null) {
                    Stalk t = new Stalk(0, null);
                    player.set_traque(t);
                }
                if (player.get_traque().getTime() < System.currentTimeMillis() - 600000 || player.get_traque().getTime() == 0) {
                    Player tempP = null;
                    ArrayList<Player> victimes = new ArrayList<Player>();
                    for (Player victime : World.world.getOnlinePlayers()) {
                        if (victime == null || victime == player)
                            continue;
						if (victime.getAccount().getCurrentIp().compareTo(player.getAccount().getCurrentIp()) == 0)
                            continue;
                        if(player.restriction.aggros.containsKey(victime.getAccount().getCurrentIp()))
                            continue;
                        if (victime.get_align() == player.get_align() || victime.get_align() == 0 || victime.get_align() == 3 || !victime.is_showWings())
                            continue;
                        if (((player.getLevel() + 20) >= victime.getLevel()) && ((player.getLevel() - 20) <= victime.getLevel()))
                            victimes.add(victime);
                    }
                    if (victimes.size() == 0) {
                        SocketManager.GAME_SEND_MESSAGE(player, "Nous n'avons pas trouver de cible à  ta hauteur, reviens plus tard.", "000000");
                        player.set_traque(null);
                        return true;
                    }
                    if (victimes.size() == 1)
                        tempP = victimes.get(0);
                    else
                        tempP = victimes.get(Formulas.getRandomValue(0, victimes.size() - 1));
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous êtes désormais en chasse de : " + tempP.getName(), "000000");
                    player.get_traque().setTraque(tempP);
                    player.get_traque().setTime(System.currentTimeMillis());
                    GameObject object = player.getItemTemplate(10085);
                    if(object != null)
                        player.removeItem(object.getGuid(), player.getNbItemTemplate(10085), true, true);
                    ObjectTemplate T = World.world.getObjTemplate(10085);
                    GameObject newObj = T.createNewItem(20, false);
                    newObj.addTxtStat(Constant.STATS_NAME_TRAQUE, tempP.getName());
                    newObj.addTxtStat(Constant.STATS_ALIGNEMENT_TRAQUE, Integer.toHexString(tempP.get_align()) + "");
                    newObj.addTxtStat(Constant.STATS_GRADE_TRAQUE, Integer.toHexString(tempP.getALvl()) + "");
                    newObj.addTxtStat(Constant.STATS_NIVEAU_TRAQUE, Integer.toHexString(tempP.getLevel()) + "");

                    if (player.addObjet(newObj, true))
                        World.world.addGameObject(newObj, true);
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous venez juste de signer un contrat, vous devez vous reposez !", "000000");
                }
                break;

            case 53: //Suivre le déplacement pour une map présice
                if (args == null)
                    break;
                if (World.world.getMap(Short.parseShort(args)) == null)
                    break;
                GameMap CurMap = World.world.getMap(Short.parseShort(args));
                if (player.getFight() == null) {
                    SocketManager.GAME_SEND_FLAG_PACKET(player, CurMap);
                }
                break;

            case 60: //Combattre un protecteur
                String ValidMobGroup1 = "";
                if (player.getFight() != null)
                    return true;
                try {
                    for (String MobAndLevel : args.split("\\|")) {
                        int monsterID = -1;
                        int lvlMin = -1;
                        int lvlMax = -1;
                        String[] MobOrLevel = MobAndLevel.split(",");
                        monsterID = Integer.parseInt(MobOrLevel[0]);
                        lvlMin = Integer.parseInt(MobOrLevel[1]);
                        lvlMax = Integer.parseInt(MobOrLevel[2]);

                        if (World.world.getMonstre(monsterID) == null
                                || World.world.getMonstre(monsterID).getGradeByLevel(lvlMin) == null
                                || World.world.getMonstre(monsterID).getGradeByLevel(lvlMax) == null) {
                            continue;
                        }
                        ValidMobGroup1 += monsterID + "," + lvlMin + ","
                                + lvlMax + ";";
                    }
                    if (ValidMobGroup1.isEmpty())
                        return true;
                    Monster.MobGroup group = new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurCell().getId(), ValidMobGroup1);
                    player.getCurMap().startFightVersusProtectors(player, group);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;
                
                // By Coding Mestre -  [IMPROV] - Added server logic to send transport animation when going to temporis map Ref #43
            	case 99: // animation to temporis map
                SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                        + "", "12");
                player.teleport((short) 14000, 295);
                break;


			/*
			 * case 100://Donner l'abilité 'args' é une dragodinde Dragodinde
			 * mount = perso.getMount(); World.world.addDragodinde(new
			 * Dragodinde(mount
			 * .getId(),mount.get_color(),mount.get_sexe(),mount
			 * .get_amour(),mount
			 * .get_endurance(),mount.get_level(),mount.get_exp
			 * (),mount.get_nom()
			 * ,mount.get_fatigue(),mount.get_energie(),mount.get_reprod
			 * (),mount.
			 * get_maturite(),mount.get_serenite(),mount.getItemsId(),mount
			 * .get_ancetres(),args));
			 * perso.setMount(World.world.getMountById(mount.getId()));
			 * SocketManager.GAME_SEND_Re_PACKET(perso, "+",
			 * World.world.getMountById(mount.getId()));
			 * SQLManager.UPDATE_MOUNT_INFOS(mount, false); break;
			 */

            case 100: //Donner l'abilité 'args' é une dragodinde
                if (player.hasItemTemplate(361, 100)) {
                    player.removeByTemplateID(361, 100);
                    GameObject newObjAdded = World.world.getObjTemplate(9201).createNewItem(1, false);
                    if(player.addObjet(newObjAdded, true))
                    	World.world.addGameObject(newObjAdded, true);
                }
                break;

            case 102: //Marier des personnages
                GameMap map0 = player.getCurMap();
                if(map0.getCase(297).getPlayers() != null && map0.getCase(282).getPlayers() != null) {
                    if (map0.getCase(297).getPlayers().size() == 1 && map0.getCase(282).getPlayers().size() == 1) {
                        Player boy = (Player) map0.getCase(282).getPlayers().toArray()[0], girl = (Player) map0.getCase(297).getPlayers().toArray()[0];
                        boy.setBlockMovement(true);
                        girl.setBlockMovement(true);
                        World.world.priestRequest(boy, girl, player);
                    }
                }
                break;

            case 103: //Divorce
                if (player.getKamas() < 50000) {
                    return true;
                } else {
                    player.setKamas(player.getKamas() - 50000);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    Player wife = World.world.getPlayer(player.getWife());
                    wife.Divorce();
                    player.Divorce();
                }
                break;

            case 104: //Téléportation mine + Animation
                if (player.getCurMap().getId() != (short) 10257)
                    return true;

                ArrayList<Couple<Short, Integer>> arrays = new ArrayList<>();
                for (String i : args.split("\\;"))
                    arrays.add(new Couple<>(Short.parseShort(i.split("\\,")[0]), Integer.parseInt(i.split("\\,")[1])));

                Couple<Short, Integer> couple = arrays.get(new Random().nextInt(arrays.size()));
                SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                        + "", "6");
                player.teleport(couple.first, couple.second);
                break;

            case 105: //Restat carac second pouvoir
                if (!player.hasItemTemplate(10563, 1)) {
                    player.sendMessage("Tu ne posséde pas l'orbe reconstituant.");
                    return true;
                }
                
                final Stats statsParcho = player.getStatsParcho();

                if (statsParcho.getEffect(Constant.STATS_ADD_VITA) == 0
                        && statsParcho.getEffect(Constant.STATS_ADD_SAGE) == 0
                        && statsParcho.getEffect(Constant.STATS_ADD_FORC) == 0
                        && statsParcho.getEffect(Constant.STATS_ADD_AGIL) == 0
                        && statsParcho.getEffect(Constant.STATS_ADD_INTE) == 0
                        && statsParcho.getEffect(Constant.STATS_ADD_CHAN) == 0) {
                	player.sendMessage("Tu ne peux pas restaurer tes caractéristiques avec ce pouvoir car tu ne possédes pas de parchotage.");
                	return true;
                }
                player.restatKeepParcho();
                SocketManager.GAME_SEND_STATS_PACKET(player);
                player.sendMessage("Tu viens de restaurer tes caractéristiques en conservant tes parchotages.");
                player.removeByTemplateID(10563, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10563 + "~" + 1);
                break;

            case 106:
                switch(this.args) {
                    case "1"://Remove spell
                        if(player.hasItemTemplate(15004, 1)) {
                            player.removeByTemplateID(15004, 1);
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 15004 + "~" + 1);
                            player.setExchangeAction(new ExchangeAction<>(ExchangeAction.FORGETTING_SPELL, 0));
                            SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', player);
                        }
                        break;
                    case "2"://Restat without
                        if(player.hasItemTemplate(15006, 1)) {
                            player.removeByTemplateID(15006, 1);
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 15006 + "~" + 1);
                            player.restatAll(0);
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                        }
                        break;
                    case "3"://Restat with
                        if(player.hasItemTemplate(15005, 1)) {
                            player.removeByTemplateID(15005, 1);
                            player.restatKeepParcho();
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 15005 + "~" + 1);
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                        }
                        break;
                }
                break;

            case 116://EPO donner de la nourriture é son familier
                GameObject EPO = World.world.getGameObject(itemID);
                if (EPO == null)
                    return true;
                GameObject pets = player.getObjetByPos(Constant.ITEM_POS_FAMILIER);
                if (pets == null)
                    return true;
                PetEntry MyPets = World.world.getPetsEntry(pets.getGuid());
                if (MyPets == null)
                    return true;
                if (EPO.getTemplate().getConditions().contains(pets.getTemplate().getId()
                        + ""))
                    MyPets.giveEpo(player);
                break;

            case 170:// Donner titre
                try {
                    byte title1 = (byte) Integer.parseInt(args);
                    target = World.world.getPlayerByName(player.getName());
                    target.set_title(title1);
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous avez désormais un nouveau titre.");
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                    Database.getStatics().getPlayerData().update(player);
                    if (target.getFight() == null)
                        SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 171: // Alignement avec condition
                short type2 = (short) Integer.parseInt(args.split(",")[0]);
                int mapId2 = Integer.parseInt(args.split(",")[1]);
                if (player.get_align() > 0)
                    return true;
                if (type2 == 1 && (player.getCurMap().getId() == mapId2 || Main.serverId == 38)) {
                    if (player.hasItemTemplate(42, 10)) {
                        player.removeByTemplateID(42, 10);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 42 + "~" + 10);
                        player.modifAlignement((byte) 1);
                    }
                }
                if (type2 == 2 && (player.getCurMap().getId() == mapId2 || Main.serverId == 38)) {
                    if (player.hasItemTemplate(95, 10)) {
                        player.removeByTemplateID(95, 10);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 95 + "~" + 10);
                        player.modifAlignement((byte) 2);
                    }
                }
                break;

            case 172: //Bricoleur avec condition
                int mapId4 = Integer.parseInt(args);
                if (player.getCurMap().getId() != mapId4)
                    return true;
                if (player.totalJobBasic() > 2)//On compte les métiers déja acquis si c'est supérieur a 2 on ignore
                {
                    SocketManager.GAME_SEND_Im_PACKET(player, "19");
                    return true;
                }
                if (player.hasItemTemplate(459, 20)
                        && player.hasItemTemplate(7657, 15)) {
                    player.removeByTemplateID(459, 20);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 459 + "~"
                            + 20);
                    player.removeByTemplateID(7657, 15);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 7657
                            + "~" + 15);
                    player.learnJob(World.world.getMetier(65));
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    return true;
                }
                break;

            case 200: //Animation + Condition voyage Ile Minotor
                long pKamas2 = player.getKamas();
                if (pKamas2 >= 100 && player.getCurMap().getId() == 9520) {
                    long pNewKamas2 = pKamas2 - 100;
                    if (pNewKamas2 < 0)
                        pNewKamas2 = 0;
                    player.setKamas(pNewKamas2);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "046;" + 100);
                    SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                            + "", "2");
                    player.teleport((short) 9541, 407);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "182");
                }
                break;

            case 219://Sortie donjon wabbit
                if (player.getCurMap().getId() != 1780)
                    return true;
                int type11 = Integer.parseInt(args);
                if (type11 == 1) {
                    GameObject newObjAdded = World.world.getObjTemplate(970).createNewItem(1, false);
                    if(player.addObjet(newObjAdded, true))
                    	World.world.addGameObject(newObjAdded, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 970);
                    player.teleport((short) 844, 212);
                } else if (type11 == 2) {
                    GameObject newObjAdded = World.world.getObjTemplate(969).createNewItem(1, false);
                    if(player.addObjet(newObjAdded, true))
                    	World.world.addGameObject(newObjAdded, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 969);
                    player.teleport((short) 844, 212);
                } else if (type11 == 3) {
                    GameObject newObjAdded = World.world.getObjTemplate(971).createNewItem(1, false);
                    if(player.addObjet(newObjAdded, true))
                    	World.world.addGameObject(newObjAdded, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 971);
                    player.teleport((short) 844, 212);
                }
                break;

            case 220:// IDitem,quantité pour IDitem's,quantité pour teleport
                try {
                    String remove0 = args.split(";")[0];
                    String add0 = args.split(";")[1];
                    String add1 = args.split(";")[4];
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int newObj1 = Integer.parseInt(add0.split(",")[0]);
                    int newQua1 = Integer.parseInt(add0.split(",")[1]);
                    int newObj2 = Integer.parseInt(add1.split(",")[0]);
                    int newQua2 = Integer.parseInt(add1.split(",")[1]);
                    if (player.hasItemTemplate(obj0, qua0)) {
                        player.removeByTemplateID(obj0, qua0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + newQua1 + "~" + newObj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + newQua2 + "~" + newObj2);
                        GameObject newObjAdded = World.world.getObjTemplate(newObj1).createNewItem(newQua1, false);
                        if(player.addObjet(newObjAdded, true))
                        	World.world.addGameObject(newObjAdded, true);
                        GameObject newObjAdded1 = World.world.getObjTemplate(newObj2).createNewItem(newQua2, false);
                        if(player.addObjet(newObjAdded1, true))
                        	World.world.addGameObject(newObjAdded1, true);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                return true;

            case 221:// IDitem,quantité pour IDitem's,quantité pour teleport
                try {
                    String remove0 = args.split(";")[0];
                    String remove1 = args.split(";")[1];
                    String add = args.split(";")[4];
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int obj1 = Integer.parseInt(remove1.split(",")[0]);
                    int qua1 = Integer.parseInt(remove1.split(",")[1]);
                    int newObj1 = Integer.parseInt(add.split(",")[0]);
                    int newQua1 = Integer.parseInt(add.split(",")[1]);
                    if (player.hasItemTemplate(obj0, qua0)
                            && player.hasItemTemplate(obj1, qua1)) {
                        player.removeByTemplateID(obj0, qua0);
                        player.removeByTemplateID(obj1, qua1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua1
                                + "~" + obj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + newQua1 + "~" + newObj1);
                        GameObject newObjAdded = World.world.getObjTemplate(newObj1).createNewItem(newQua1, false);
                        if(player.addObjet(newObjAdded, true))
                        	World.world.addGameObject(newObjAdded, true);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                return true;

            case 222:// IDitem,quantité pour IDitem's,quantité pour teleport
                try {
                    String remove0 = args.split(";")[0];
                    String remove1 = args.split(";")[1];
                    String remove2 = args.split(";")[2];
                    String remove3 = args.split(";")[3];
                    String add = args.split(";")[4];
                    int verifMapId = Integer.parseInt(args.split(";")[5]);
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int obj1 = Integer.parseInt(remove1.split(",")[0]);
                    int qua1 = Integer.parseInt(remove1.split(",")[1]);
                    int obj2 = Integer.parseInt(remove2.split(",")[0]);
                    int qua2 = Integer.parseInt(remove2.split(",")[1]);
                    int obj3 = Integer.parseInt(remove3.split(",")[0]);
                    int qua3 = Integer.parseInt(remove3.split(",")[1]);
                    int mapID = Integer.parseInt(add.split(",")[0]);
                    int cellID = Integer.parseInt(add.split(",")[1]);

                    if (player.hasItemTemplate(obj0, qua0)
                            && player.hasItemTemplate(obj1, qua1)
                            && player.hasItemTemplate(obj2, qua2)
                            && player.hasItemTemplate(obj3, qua3)) {
                        player.removeByTemplateID(obj0, qua0);
                        player.removeByTemplateID(obj1, qua1);
                        player.removeByTemplateID(obj2, qua2);
                        player.removeByTemplateID(obj3, qua3);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua1
                                + "~" + obj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua2
                                + "~" + obj2);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua3
                                + "~" + obj3);
                        if (player.getFight() != null
                                || player.getCurMap().getId() != verifMapId)
                            return true;
                        player.teleport((short) mapID, cellID);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                return true;

            case 223:// IDitem,quantité pour IDitem's,quantité
                try {
                    String remove0 = args.split(";")[0];
                    String remove1 = args.split(";")[1];
                    String remove2 = args.split(";")[2];
                    String remove3 = args.split(";")[3];
                    String remove4 = args.split(";")[4];
                    String add = args.split(";")[5];
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int obj1 = Integer.parseInt(remove1.split(",")[0]);
                    int qua1 = Integer.parseInt(remove1.split(",")[1]);
                    int obj2 = Integer.parseInt(remove2.split(",")[0]);
                    int qua2 = Integer.parseInt(remove2.split(",")[1]);
                    int obj3 = Integer.parseInt(remove3.split(",")[0]);
                    int qua3 = Integer.parseInt(remove3.split(",")[1]);
                    int obj4 = Integer.parseInt(remove4.split(",")[0]);
                    int qua4 = Integer.parseInt(remove4.split(",")[1]);
                    int newItem = Integer.parseInt(add.split(",")[0]);
                    int quaNewItem = Integer.parseInt(add.split(",")[1]);
                    if (player.hasItemTemplate(obj0, qua0)
                            && player.hasItemTemplate(obj1, qua1)
                            && player.hasItemTemplate(obj2, qua2)
                            && player.hasItemTemplate(obj3, qua3)
                            && player.hasItemTemplate(obj4, qua4)) {
                        player.removeByTemplateID(obj0, qua0);
                        player.removeByTemplateID(obj1, qua1);
                        player.removeByTemplateID(obj2, qua2);
                        player.removeByTemplateID(obj3, qua3);
                        player.removeByTemplateID(obj4, qua4);
                        GameObject newObjAdded = World.world.getObjTemplate(newItem).createNewItem(quaNewItem, false);
                        if(player.addObjet(newObjAdded, true))
                        	World.world.addGameObject(newObjAdded, true);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua1
                                + "~" + obj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua2
                                + "~" + obj2);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua3
                                + "~" + obj3);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua4
                                + "~" + obj4);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + quaNewItem + "~" + newItem);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                return true;

            case 224:// IDitem,quantité pour IDitem's,quantité
                try {
                    String remove0 = args.split(";")[0];
                    String remove1 = args.split(";")[1];
                    String remove2 = args.split(";")[2];
                    String remove3 = args.split(";")[3];
                    String add = args.split(";")[4];
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int obj1 = Integer.parseInt(remove1.split(",")[0]);
                    int qua1 = Integer.parseInt(remove1.split(",")[1]);
                    int obj2 = Integer.parseInt(remove2.split(",")[0]);
                    int qua2 = Integer.parseInt(remove2.split(",")[1]);
                    int obj3 = Integer.parseInt(remove3.split(",")[0]);
                    int qua3 = Integer.parseInt(remove3.split(",")[1]);
                    int newItem = Integer.parseInt(add.split(",")[0]);
                    int quaNewItem = Integer.parseInt(add.split(",")[1]);
                    if (player.hasItemTemplate(obj0, qua0)
                            && player.hasItemTemplate(obj1, qua1)
                            && player.hasItemTemplate(obj2, qua2)
                            && player.hasItemTemplate(obj3, qua3)) {
                        player.removeByTemplateID(obj0, qua0);
                        player.removeByTemplateID(obj1, qua1);
                        player.removeByTemplateID(obj2, qua2);
                        player.removeByTemplateID(obj3, qua3);
                        GameObject newObjAdded = World.world.getObjTemplate(newItem).createNewItem(quaNewItem, false);
                        if(player.addObjet(newObjAdded, true))
                        	World.world.addGameObject(newObjAdded, true);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua1
                                + "~" + obj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua2
                                + "~" + obj2);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua3
                                + "~" + obj3);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + quaNewItem + "~" + newItem);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                return true;

            case 225:// IDitem,quantité pour IDitem's,quantité
                try {
                    String remove0 = args.split(";")[0];
                    String remove1 = args.split(";")[1];
                    String remove2 = args.split(";")[2];
                    String add = args.split(";")[3];
                    int obj0 = Integer.parseInt(remove0.split(",")[0]);
                    int qua0 = Integer.parseInt(remove0.split(",")[1]);
                    int obj1 = Integer.parseInt(remove1.split(",")[0]);
                    int qua1 = Integer.parseInt(remove1.split(",")[1]);
                    int obj2 = Integer.parseInt(remove2.split(",")[0]);
                    int qua2 = Integer.parseInt(remove2.split(",")[1]);
                    int newItem = Integer.parseInt(add.split(",")[0]);
                    int quaNewItem = Integer.parseInt(add.split(",")[1]);
                    if (player.hasItemTemplate(obj0, qua0)
                            && player.hasItemTemplate(obj1, qua1)
                            && player.hasItemTemplate(obj2, qua2)) {
                        player.removeByTemplateID(obj0, qua0);
                        player.removeByTemplateID(obj1, qua1);
                        player.removeByTemplateID(obj2, qua2);
                        GameObject newObjAdded = World.world.getObjTemplate(newItem).createNewItem(quaNewItem, false);
                        if(player.addObjet(newObjAdded, true))
                        	World.world.addGameObject(newObjAdded, true);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua0
                                + "~" + obj0);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua1
                                + "~" + obj1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua2
                                + "~" + obj2);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + quaNewItem + "~" + newItem);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 226://Animation + Condition voyage Ile Moon
                if (player.hasItemTemplate(1089, 1) && player.hasEquiped(1021)
                        && player.hasEquiped(1019)
                        && player.getCurMap().getId() == 1014) {
                    player.removeByTemplateID(1019, 1);
                    player.removeByTemplateID(1021, 1);
                    player.removeByTemplateID(1089, 1);
                    GameObject newObj1 = World.world.getObjTemplate(1020).createNewItem(1, false);
                    if(player.addObjet(newObj1, true))
                    	World.world.addGameObject(newObj1, true);
                    GameObject newObj2 = World.world.getObjTemplate(1022).createNewItem(1, false);
                    if(player.addObjet(newObj2, true))
                    	World.world.addGameObject(newObj2, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1089);
                    SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                            + "", "1");
                    player.teleport((short) 437, 411);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    return true;
                }
                break;

            case 227://Animation + Condition voyage Ile Wabbit
                long pKamas = player.getKamas();
                if (pKamas >= 500 && player.getCurMap().getId() == 167) {
                    long pNewKamas = pKamas - 500;
                    if (pNewKamas < 0)
                        pNewKamas = 0;
                    player.setKamas(pNewKamas);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "046;" + 500);
                    SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                            + "", "2");
                    player.teleport((short) 833, 141);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "182");
                }
                break;

            case 228://Faire animation Hors Combat
                try {
                    int AnimationId = Integer.parseInt(args);
                    Animation animation = World.world.getAnimation(AnimationId);
                    if (player.getFight() != null)
                        return true;
                    player.changeOrientation(1);
                    SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getCurMap(), "0", 228, player.getId()
                            + ";"
                            + cellid
                            + ","
                            + Animation.PrepareToGA(animation), "");
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 229://Animation d'incarnam é astrub
                short map = Constant.getClassStatueMap(player.getClasse());
                int cell = Constant.getClassStatueCell(player.getClasse());
                SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                        + "", "7");
                player.teleport(map, cell);
                player.set_savePos(map + "," + cell);
                SocketManager.GAME_SEND_Im_PACKET(player, "06");
                break;

            case 230://Point Boutique  
                try {
                    int pts = Integer.parseInt(args);
                    int ptsTotal = player.getAccount().getPoints() + pts;
                    if (ptsTotal < 0)
                        ptsTotal = 0;
                    if (ptsTotal > 50000)
                        ptsTotal = 50000;
                    player.getAccount().setPoints(ptsTotal);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_MESSAGE(player, "Tu viens d'acquérir "
                            + pts
                            + " Point(s). Tu possèdes donc "
                            + ptsTotal
                            + "Point(s).");
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 231:// IDitem,quantité pour IDitem's,quantité
                try {
                    String remove = args.split(";")[0];
                    String add = args.split(";")[1];
                    int obj = Integer.parseInt(remove.split(",")[0]);
                    int qua = Integer.parseInt(remove.split(",")[1]);
                    int newItem = Integer.parseInt(add.split(",")[0]);
                    int quaNewItem = Integer.parseInt(add.split(",")[1]);
                    if (player.hasItemTemplate(obj, qua)) {
                        player.removeByTemplateID(obj, qua);
                        GameObject newObjAdded = World.world.getObjTemplate(newItem).createNewItem(quaNewItem, false);
                        if(player.addObjet(newObjAdded, true))
                        	World.world.addGameObject(newObjAdded, true);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + qua
                                + "~" + obj);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                + quaNewItem + "~" + newItem);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 232://startFigthVersusMonstres args : monsterID,monsterLevel| ...
                if (player.getFight() != null)
                    return true;
                String ValidMobGroup2 = "";
                int pMap = player.getCurMap().getId();
                if (pMap == 10131 || pMap == 10132 || pMap == 10133
                        || pMap == 10134 || pMap == 10135 || pMap == 10136
                        || pMap == 10137 || pMap == 10138) {
                    try {
                        for (String MobAndLevel : args.split("\\|")) {
                            int monsterID = -1;
                            int monsterLevel = -1;
                            String[] MobOrLevel = MobAndLevel.split(",");
                            monsterID = Integer.parseInt(MobOrLevel[0]);
                            monsterLevel = Integer.parseInt(MobOrLevel[1]);

                            if (World.world.getMonstre(monsterID) == null
                                    || World.world.getMonstre(monsterID).getGradeByLevel(monsterLevel) == null) {
                                continue;
                            }
                            ValidMobGroup2 += monsterID + "," + monsterLevel
                                    + "," + monsterLevel + ";";
                        }
                        if (ValidMobGroup2.isEmpty())
                            return true;
                        Monster.MobGroup group = new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurCell().getId(), ValidMobGroup2);
                        player.getCurMap().startFightVersusMonstres(player, group);// Si bug startfight, voir "//Respawn d'un groupe fix" dans fight.java
                    } catch (Exception e) {
                        e.printStackTrace();
                        GameServer.a();
                    }
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous ne pouvez pas vous battre ici. Allez en arène !");
                }
                break;

            case 233: //Pierre d'ame en aréne
                try {
                    int tID = Integer.parseInt(args.split(",")[0]);
                    count = Integer.parseInt(args.split(",")[1]);
                    boolean send = true;
                    if (args.split(",").length > 2)
                        send = args.split(",")[2].equals("1");
                    int pMap2 = player.getCurMap().getId();
                    if (pMap2 == 10131 || pMap2 == 10132 || pMap2 == 10133
                            || pMap2 == 10134 || pMap2 == 10135
                            || pMap2 == 10136 || pMap2 == 10137
                            || pMap2 == 10138) {
                        //Si on ajoute
                        if (count > 0) {
                            ObjectTemplate T = World.world.getObjTemplate(tID);
                            if (T == null)
                                return true;
                            GameObject O = T.createNewItem(count, false);
                            //Si retourne true, on l'ajoute au monde
                            if (player.addObjet(O, true))
                                World.world.addGameObject(O, true);
                        } else {
                            player.removeByTemplateID(tID, -count);
                        }
                        //Si en ligne (normalement oui)
                        if (player.isOnline())//on envoie le packet qui indique l'ajout//retrait d'un item
                        {
                            SocketManager.GAME_SEND_Ow_PACKET(player);
                            if (send) {
                                if (count >= 0) {
                                    SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                            + count + "~" + tID);
                                } else if (count < 0) {
                                    SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                            + -count + "~" + tID);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 234: //Ajout d'un objet en fonction de la map
                int IdObj = Short.parseShort(args.split(";")[0]);
                int MapId = Integer.parseInt(args.split(";")[1]);
                if (player.getCurMap().getId() != MapId)
                    return true;
                if (!player.hasItemTemplate(IdObj, 1)) {
                    GameObject newObjAdded = World.world.getObjTemplate(IdObj).createNewItem(1, false);
                    if(player.addObjet(newObjAdded, true))
                    	World.world.addGameObject(newObjAdded, true);
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, "Tu possèdes déjà  l'objet.");
                }
                break;

            case 235:// IDitem,quantité pour IDitem's,quantité
                if (player.getCurMap().getId() == 713) {
                    if (player.hasItemTemplate(757, 1)
                            && player.hasItemTemplate(368, 1)
                            && player.hasItemTemplate(369, 1)
                            && !player.hasItemTemplate(960, 1)) {
                        player.removeByTemplateID(757, 1);
                        player.removeByTemplateID(368, 1);
                        player.removeByTemplateID(369, 1);

                        GameObject newObjAdded = World.world.getObjTemplate(960).createNewItem(1, false);
                        if(player.addObjet(newObjAdded, true))
                        	World.world.addGameObject(newObjAdded, true);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 757);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 368);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 369);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + 960);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                    }
                }
                break;

            case 300://Sort arakne
                if (player.getCurMap().getId() == 1559 && player.hasItemTemplate(973, 1)) {
                    player.removeByTemplateID(973, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 973);
                    player.learnSpell(370, 1, true, true, true);
                }
                break;

			/*
			 * case 92://bonbon try { if(perso.getCandyId() != 0)
			 * SocketManager.send(perso, "OR1"); int t =
			 * World.world.getGameObject(itemID).getTemplate().getId();
			 * perso.removeByTemplateID(t, 1); perso.setCandy(t);
			 * SocketManager.GAME_SEND_STATS_PACKET(perso); }catch (Exception e)
			 * {} break;
			 */

            case 239://Ouvrir l'interface d'oublie de sort
                player.setExchangeAction(new ExchangeAction<>(ExchangeAction.FORGETTING_SPELL, 0));
                SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', player);
                break;

            case 241:
                if (player.getKamas() >= 10
                        && player.getCurMap().getId() == 6863) {
                    if (player.hasItemTemplate(6653, 1)) {
                        String date = player.getItemTemplate(6653, 1).getTxtStat().get(Constant.STATS_DATE);
                        long timeStamp = Long.parseLong(date.split("#")[3]);
                        if (System.currentTimeMillis() - timeStamp <= 86400000) {
                            SocketManager.GAME_SEND_MESSAGE(player, "Ton ticket est bon.");
                            return true;
                        } else {
                            SocketManager.GAME_SEND_MESSAGE(player, "Ton ticket est dépassé, il faut que tu en rachète un.");
                            player.removeByTemplateID(6653, 1);
                        }
                    }
                    long rK = player.getKamas() - 10;
                    if (rK < 0)
                        rK = 0;
                    player.setKamas(rK);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    ObjectTemplate OT = World.world.getObjTemplate(6653);
                    GameObject obj = OT.createNewItem(1, false);
                    if (player.addObjet(obj, true))//Si le joueur n'avait pas d'item similaire
                        World.world.addGameObject(obj, true);
                    obj.refreshStatsObjet("325#0#0#"
                            + System.currentTimeMillis());
                    Database.getStatics().getPlayerData().update(player);
                    SocketManager.GAME_SEND_Ow_PACKET(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 6653);
                }
                break;

            /** Ile Moon **/
            case 450: //Ortimus
                if (player.getCurMap().getId() == 1844
                        && player.getKamas() >= 5000
                        && player.hasItemTemplate(363, 5)) {
                    player.removeByTemplateID(363, 5);
                    long rK = player.getKamas() - 5000;
                    if (rK < 0)
                        rK = 0;
                    player.setKamas(rK);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    GameObject newObjAdded = World.world.getObjTemplate(998).createNewItem(1, false);
                    if(player.addObjet(newObjAdded, true))
                    	World.world.addGameObject(newObjAdded, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 998);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 5 + "~"
                            + 363);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 451: //Employé de l'agence Touriste
                if (player.getKamas() >= 200
                        && player.getCurMap().getId() == 436) {
                    long rK = player.getKamas() - 200;
                    if (rK < 0)
                        rK = 0;
                    player.setKamas(rK);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    GameObject newObjAdded = World.world.getObjTemplate(1004).createNewItem(1, false);
                    if(player.addObjet(newObjAdded, true))
                    	World.world.addGameObject(newObjAdded, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 1004);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 452: //Kib Roche
                if (player.hasItemTemplate(1000, 6)
                        && player.hasItemTemplate(1003, 1)
                        && player.hasItemTemplate(1018, 10)
                        && player.hasItemTemplate(998, 1)
                        && player.hasItemTemplate(1002, 1)
                        && player.hasItemTemplate(999, 1)
                        && player.hasItemTemplate(1004, 4)
                        && player.hasItemTemplate(1001, 2)
                        && player.getCurMap().getId() == 437) {
                    player.removeByTemplateID(1000, 6);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 6 + "~"
                            + 1000);
                    player.removeByTemplateID(1003, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1003);
                    player.removeByTemplateID(1018, 10);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~"
                            + 1018);
                    player.removeByTemplateID(998, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 998);
                    player.removeByTemplateID(1002, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1002);
                    player.removeByTemplateID(999, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 99);
                    player.removeByTemplateID(1004, 4);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 4 + "~"
                            + 1004);
                    player.removeByTemplateID(1001, 2);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 2 + "~"
                            + 1001);
                    GameObject newObjAdded = World.world.getObjTemplate(6716).createNewItem(1, false);
                    if(player.addObjet(newObjAdded, true))
                    	World.world.addGameObject(newObjAdded, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 6716);
                    player.teleport((short) 1701, 247);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 453: //Kanniboul cuisininer : fonctionnel avec les 4 carapaces
                if (player.hasItemTemplate(1010, 1)
                        && player.hasItemTemplate(1011, 1)
                        && player.hasItemTemplate(1012, 1)
                        && player.hasItemTemplate(1013, 1)
                        && player.getCurMap().getId() == 1714) {
                    player.removeByTemplateID(1010, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1010);
                    player.removeByTemplateID(1011, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1011);
                    player.removeByTemplateID(1012, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1012);
                    player.removeByTemplateID(1013, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1013);
                    player.teleport((short) 1766, 332);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 454: //masque kanniboul : fonctionnel
                if (player.hasEquiped(1088) && player.getCurMap().getId() == 1764) {
                    player.teleport((short) 1765, 226);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 455://Issé Heau : peinture noire : fonctionnel
                if (player.hasItemTemplate(1006, 1)
                        && player.hasItemTemplate(1007, 1)
                        && player.hasItemTemplate(1008, 1)
                        && player.hasItemTemplate(1009, 1)
                        && player.getCurMap().getId() == 1838) {
                    player.removeByTemplateID(1006, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1006);
                    player.removeByTemplateID(1007, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1007);
                    player.removeByTemplateID(1008, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1008);
                    player.removeByTemplateID(1009, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1009);
                    GameObject newObjAdded = World.world.getObjTemplate(1086).createNewItem(1, false);

                    if(player.addObjet(newObjAdded, true))
                    	World.world.addGameObject(newObjAdded, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 1086);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 456: //Obtention du masque kanniboul : fonctionnel
                if (player.hasItemTemplate(1014, 1)
                        && player.hasItemTemplate(1015, 1)
                        && player.hasItemTemplate(1016, 1)
                        && player.hasItemTemplate(1017, 1)
                        && player.hasItemTemplate(1086, 1)
                        && player.getCurMap().getId() == 425) {
                    player.removeByTemplateID(1014, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1014);
                    player.removeByTemplateID(1015, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1015);
                    player.removeByTemplateID(1016, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1016);
                    player.removeByTemplateID(1017, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1017);
                    player.removeByTemplateID(1086, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 1086);
                    GameObject newObjAdded = World.world.getObjTemplate(1088).createNewItem(1, false);
                    if(player.addObjet(newObjAdded, true))
                    	World.world.addGameObject(newObjAdded, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 1088);

                    NpcQuestion quest = World.world.getNPCQuestion(577);
                    if (quest == null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(player.getGameClient());
                        player.setExchangeAction(null);
                        return true;
                    }
                    try {
                        SocketManager.GAME_SEND_QUESTION_PACKET(player.getGameClient(), quest.parse(player));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14|43");
                }
                break;

            case 457://Vente ticket éle moon
                if (player.getKamas() >= 1000
                        && player.getCurMap().getId() == 1014) {
                    player.setKamas(player.getKamas() - 1000);
                    GameObject newObjAdded11 = World.world.getObjTemplate(1089).createNewItem(1, false);
                    if(player.addObjet(newObjAdded11, true))
                    	World.world.addGameObject(newObjAdded11, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 1089);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                }
                break;
            /** Fin Ile Moon **/

            /** Donjon Condition **/
            case 500:// Donjon Bouftou : Récompense.
                if (player.getCurMap().getId() != 2084)
                    return true;
                player.teleport((short) 1856, 226);
                GameObject newObjAdded = World.world.getObjTemplate(1728).createNewItem(1, false);
                if(player.addObjet(newObjAdded, true))
                	World.world.addGameObject(newObjAdded, true);
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                        + 1728);
                break;

            case 501:// Donjon Bwork : Récompense.
                if (player.getCurMap().getId() != 9767)
                    return true;
                player.teleport((short) 9470, 198);
                GameObject newObjAdded1 = World.world.getObjTemplate(8000).createNewItem(1, false);

                if(player.addObjet(newObjAdded1, true))
                	World.world.addGameObject(newObjAdded1, true);
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                        + 8000);
                break;

            case 502://Entré Donjon Cawotte
                if (player.hasEquiped(969) && player.hasEquiped(970)
                        && player.hasEquiped(971)
                        && player.getCurMap().getId() == 1781) {
                    player.teleport((short) 1783, 114);
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "114;");
                }
                break;

            case 503://Sortie Donjon Cawotte
                if (player.getCurMap().getId() != 1795)
                    return true;
                if (player.hasItemTemplate(969, 1)
                        && player.hasItemTemplate(970, 1)
                        && player.hasItemTemplate(971, 1)) {
                    player.removeByTemplateID(969, 1);
                    player.removeByTemplateID(970, 1);
                    player.removeByTemplateID(971, 1);
                    GameObject newObjAdded11 = World.world.getObjTemplate(972).createNewItem(1, false);
                    if(player.addObjet(newObjAdded11, true))
                    	World.world.addGameObject(newObjAdded11, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 972);
                    player.teleport((short) 1781, 227);
                }
                break;

            case 504://Sortie Donjon Koulosse
                if (player.getCurMap().getId() == 9717) {
                    int type111 = 0;
                    try {
                        type111 = Integer.parseInt(args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (type111 == 1) {
                        GameObject newObjAdded11 = World.world.getObjTemplate(7890).createNewItem(1, false);
                        if(player.addObjet(newObjAdded11, true))
                        	World.world.addGameObject(newObjAdded11, true);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + 7890);
                    }
                    if (type111 == 2) {
                        GameObject newObjAdded11 = World.world.getObjTemplate(7889).createNewItem(1, false);
                        if(player.addObjet(newObjAdded11, true))
                        	World.world.addGameObject(newObjAdded11, true);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + 7889);
                    }
                    if (type111 == 3) {
                        GameObject newObjAdded11 = World.world.getObjTemplate(7888).createNewItem(1, false);
                        if(player.addObjet(newObjAdded11, true))
                        	World.world.addGameObject(newObjAdded11, true);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + 7888);
                    }
                    if (type111 == 4) {
                        GameObject newObjAdded11 = World.world.getObjTemplate(7887).createNewItem(1, false);
                        if(player.addObjet(newObjAdded11, true))
                        	World.world.addGameObject(newObjAdded11, true);
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + 7887);
                    }
                    player.teleport((short) 8905, 431);
                }
                break;

            case 505://Learn spell Apprivoisement
                if (player.getCurMap().getId() == 9717) {
                    if (player.hasItemTemplate(7904, 50)
                            && player.hasItemTemplate(7903, 50)) {
                        player.removeByTemplateID(7904, 50);
                        player.removeByTemplateID(7903, 50);
                        player.learnSpell(414, 1, true, true, true);
                    }
                }
                break;

            case 506://Entré Donjon Koulosse
                if (player.getCurMap().getId() == 8905
                        && player.getCurCell().getId() == 213) {
                    if (player.hasItemTemplate(7908, 1)) {
                        player.removeByTemplateID(7908, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 7908);
                        player.teleport((short) 8950, 408);
                    } else {
                        SocketManager.GAME_SEND_MESSAGE(player, "Vous ne possédez pas la clef neccéssaire.");
                    }
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous n'êtes pas devant le PNJ.");
                }
                break;

            case 507://Donjon Gelée
                int type3 = 0;
                if (player.getCurMap().getId() == 6823) {
                    try {
                        type3 = Integer.parseInt(args);
                        if (type3 < 6) {
                            if (player.hasItemTemplate(2433, 15)
                                    && player.hasItemTemplate(2432, 15)
                                    && player.hasItemTemplate(2431, 15)
                                    && player.hasItemTemplate(2430, 15)) {
                                type3 = 6;
                            } else if (player.hasItemTemplate(2433, 10)
                                    && player.hasItemTemplate(2432, 10)
                                    && player.hasItemTemplate(2431, 10)
                                    && player.hasItemTemplate(2430, 10)) {
                                type3 = 5;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    switch (type3) {
                        case 1://Menthe -> 2433
                            if (player.hasItemTemplate(2433, 10)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6834, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, "Vous n'avez pas le bon nombre de flaque de Menthe.");
                            }
                            break;
                        case 2://Fraise -> 2432
                            if (player.hasItemTemplate(2432, 10)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6833, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, "Vous n'avez pas le bon nombre de flaque de Fraise.");
                            }
                            break;
                        case 3://Citron -> 2431
                            if (player.hasItemTemplate(2431, 10)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6832, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, "Vous n'avez pas le bon nombre de flaque de Citron.");
                            }
                            break;
                        case 4://Bleue -> 2430
                            if (player.hasItemTemplate(2430, 10)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6831, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, "Vous n'avez pas le bon nombre de flaque de Bleue.");
                            }
                            break;
                        case 5://Gelée x10 => 2433,2432,2431,2430
                            if (player.hasItemTemplate(2433, 10)
                                    && player.hasItemTemplate(2432, 10)
                                    && player.hasItemTemplate(2431, 10)
                                    && player.hasItemTemplate(2430, 10)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6835, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, "Vous n'avez pas le bon nombre de flaque pour combattre 2 gelées royales.");
                            }
                            break;
                        case 6://Menthe -> 2433
                            if (player.hasItemTemplate(2433, 15)
                                    && player.hasItemTemplate(2432, 15)
                                    && player.hasItemTemplate(2431, 15)
                                    && player.hasItemTemplate(2430, 15)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2433) + "~"
                                        + 2433);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2432) + "~"
                                        + 2432);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2431) + "~"
                                        + 2431);
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + player.getNbItemTemplate(2430) + "~"
                                        + 2430);
                                player.removeByTemplateID(2430, player.getNbItemTemplate(2430));
                                player.removeByTemplateID(2431, player.getNbItemTemplate(2431));
                                player.removeByTemplateID(2432, player.getNbItemTemplate(2432));
                                player.removeByTemplateID(2433, player.getNbItemTemplate(2433));
                                player.teleport((short) 6836, 422);
                            } else {
                                SocketManager.GAME_SEND_MESSAGE(player, "Vous n'avez pas le bon nombre de flaque pour combattre 4 gelées royales.");
                            }
                            break;
                    }
                }
                break;

            case 508://Donjon Kitsoune : Récompense
                if (player.getCurMap().getId() == 8317) {
                    player.teleport((short) 8236, 370);
                    GameObject newObjAdded11 = World.world.getObjTemplate(7415).createNewItem(1, false);
                    if(player.addObjet(newObjAdded11, true))
                    	World.world.addGameObject(newObjAdded11, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 7415);
                }
                break;

            case 509://Donjon Bworker : Récompense
                player.teleport((short) 4786, 300);
                GameObject newObjAdded11 = World.world.getObjTemplate(6885).createNewItem(1, false);
                if(player.addObjet(newObjAdded11, true))
                	World.world.addGameObject(newObjAdded11, true);
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                        + 6885);
                GameObject newObjAdded12 = World.world.getObjTemplate(8388).createNewItem(1, false);
                if(player.addObjet(newObjAdded12, true))
                	World.world.addGameObject(newObjAdded12, true);
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                        + 8388);
                break;

            case 510://Jeton Bwroker Vs Packet
                if (player.getCurMap().getId() == 3373
                        && player.hasItemTemplate(6885, 1)) {
                    player.removeByTemplateID(6885, 1);
                    GameObject newObjAdded121 = World.world.getObjTemplate(6887).createNewItem(1, false);
                    if(player.addObjet(newObjAdded121, true))
                    	World.world.addGameObject(newObjAdded121, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 6885);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 6887);
                }
                break;

            case 511://Cadeau Bworker
                int cadeau = Loterie.getCadeauBworker();
                GameObject newObjAdded121 = World.world.getObjTemplate(cadeau).createNewItem(1, false);
                if(player.addObjet(newObjAdded121, true))
                	World.world.addGameObject(newObjAdded121, true);
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                        + cadeau);
                break;

            case 512://Rat Blanc : Récompense
                if (player.getCurMap().getId() == 10213) {
                    player.teleport((short) 6536, 273);
                    GameObject newObjAdded111 = World.world.getObjTemplate(8476).createNewItem(1, false);
                    if(player.addObjet(newObjAdded111, true))
                    	World.world.addGameObject(newObjAdded111, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 8476);
                }
                break;

            case 513://Rat noir : Récompense
                if (player.getCurMap().getId() == 10199) {
                    player.teleport((short) 6738, 213);
                    GameObject newObjAdded111 = World.world.getObjTemplate(8477).createNewItem(1, false);
                    if(player.addObjet(newObjAdded111, true))
                    	World.world.addGameObject(newObjAdded111, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 8477);
                }
                break;

            case 514://Splinter Cell : Entré
                if (player.getCurMap().getId() == 9638
                        && player.hasItemTemplate(8476, 1)
                        && player.hasItemTemplate(8477, 1)) {
                    player.teleport((short) 10141, 448);
                    player.removeByTemplateID(8476, 1);
                    player.removeByTemplateID(8477, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 8476);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 8477);
                }
                break;

            case 515://Récompense Pandikaze
                if (player.getCurMap().getId() == 8497) {
                    player.teleport((short) 8167, 252);
                    GameObject newObjAdded111 = World.world.getObjTemplate(7414).createNewItem(1, false);
                    if(player.addObjet(newObjAdded111, true))
                    	World.world.addGameObject(newObjAdded111, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 7414);
                }
                break;

            case 516://Chenil achat poudre eniripsa
                if (player.getCurMap().getId() == 1140
                        && player.getKamas() >= 1000) {
                    GameObject newObjAdded111 = World.world.getObjTemplate(2239).createNewItem(1, false);
                    if(player.addObjet(newObjAdded111, true))
                    	World.world.addGameObject(newObjAdded111, true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~"
                            + 2239);
                }
                break;

            case 517://Entrée donjon familier 9052,268 - O: 7 - Transformation donjon abra
                mapId = Integer.parseInt(args.split(";")[0].split(",")[0]);
                int cellId = Integer.parseInt(args.split(";")[0].split(",")[1]);
                short mapSecu = Short.parseShort(args.split(";")[1]);
                int id = Integer.parseInt(args.split(";")[2]);
                if (player.getCurMap().getId() != mapSecu)
                    return true;
                if (player.getCurMap().getId() == 9052) {
                    if (player.getCurCell().getId() != 268
                            || player.get_orientation() != 7)
                        return true;
                    if (!player.hasItemType(90)) {
                        SocketManager.GAME_SEND_MESSAGE(player, "Vous n'avez pas de fantôme de familier.");
                        return true;
                    }
                }
                player.teleport((short) mapId, cellId);
                player.setFullMorph(id, false, false);
                break;

            case 518://Démorph + TP : Donjon abra, familier
                mapId = Integer.parseInt(args.split(";")[0].split(",")[0]);
                cellId = Integer.parseInt(args.split(";")[0].split(",")[1]);
                mapSecu = Short.parseShort(args.split(";")[1]);
                if (player.getCurMap().getId() != mapSecu)
                    return true;
                player.unsetFullMorph();
                player.teleport((short) mapId, cellId);
                break;

            case 519://Donjon Grotte Hesque, Arche, Rasboul, Tynril
                mapId = Integer.parseInt(args.split(";")[0].split(",")[0]);
                cellId = Integer.parseInt(args.split(";")[0].split(",")[1]);
                mapSecu = Short.parseShort(args.split(";")[1]);
                if (player.getCurMap().getId() != mapSecu)
                    return true;
                GameObject obj1 = World.world.getObjTemplate(Integer.parseInt(args.split(";")[2])).createNewItem(1, false);
                if (obj1 != null)
                    if (player.addObjet(obj1, true))
                        World.world.addGameObject(obj1, true);
                player.send("Im021;1~" + args.split(";")[2]);
                obj1 = World.world.getObjTemplate(Integer.parseInt(args.split(";")[3])).createNewItem(1, false);
                if (obj1 != null)
                    if (player.addObjet(obj1, true))
                        World.world.addGameObject(obj1, true);
                player.send("Im021;1~" + args.split(";")[3]);
                player.teleport((short) mapId, cellId);
                break;

            case 520://Dj pandikaze
                if (player.getCurMap().getId() != 8497)
                    return true;

                obj1 = World.world.getObjTemplate(7414).createNewItem(1, false);
                if (player.addObjet(obj1, true))
                    World.world.addGameObject(obj1, true);
                player.send("Im021;1~7414");
                if (!player.getEmotes().contains(15)) {
                    obj1 = World.world.getObjTemplate(7413).createNewItem(1, false);
                    if (player.addObjet(obj1, true))
                        World.world.addGameObject(obj1, true);
                    player.send("Im021;1~7413");
                }

                player.teleport((short) 8167, 252);
                break;

            case 521://Echange clef skeunk
                if (player.getCurMap().getId() != 9248)
                    return true;

                if (player.hasItemTemplate(7887, 1) && player.hasItemTemplate(7888, 1) && player.hasItemTemplate(7889, 1) && player.hasItemTemplate(7890, 1)) {
                    player.removeByTemplateID(7887, 1);
                    player.removeByTemplateID(7888, 1);
                    player.removeByTemplateID(7889, 1);
                    player.removeByTemplateID(7890, 1);
                    player.send("Im022;1~7887");
                    player.send("Im022;1~7888");
                    player.send("Im022;1~7889");
                    player.send("Im022;1~7890");

                    obj1 = World.world.getObjTemplate(8073).createNewItem(1, false);
                    if (player.addObjet(obj1, true))
                        World.world.addGameObject(obj1, true);
                    player.send("Im021;1~8073");
                } else {
                    player.send("Im119|45");
                }
                break;

            case 522://Péki péki
                if (player.getCurMap().getId() != 8349)
                    return true;

                obj1 = World.world.getObjTemplate(6978).createNewItem(1, false);
                if (player.addObjet(obj1, true))
                    World.world.addGameObject(obj1, true);
                player.send("Im021;1~6978");
                player.teleport((short) 8467, 227);
                break;

            case 523://Cawotte vs spell
                if (player.getCurMap().getId() != 1779)
                    return true;
                if(client == null) return true;

                if (player.hasItemTemplate(361, 100)) {
                    player.removeByTemplateID(361, 100);
                    player.send("Im022;100~361");
                    player.learnSpell(367, 1, true, true, true);

                    NpcQuestion quest = World.world.getNPCQuestion(473);
                    if (quest == null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        return true;
                    }
                    SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                    return false;
                } else {
                    player.send("Im14");
                }
                break;

            case 524://Réponse Maitre corbac
                if(client == null) return true;
                int qID = Monster.MobGroup.MAITRE_CORBAC.check();
                NpcQuestion quest = World.world.getNPCQuestion(qID);
                if (quest == null) {
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                    return true;
                }
                SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                return false;

            case 525://EndFight Action Maitre Corbac
                Monster.MobGroup group = player.hasMobGroup();

                split = args.split(";");

                for (Monster.MobGrade mb : group.getMobs().values()) {
                    switch (mb.getTemplate().getId()) {
                        case 289:
                            player.teleport((short) 9604, 403);
                            return true;
                        case 819:
                            player.teleport(Short.parseShort(split[0].split(",")[0]), Integer.parseInt(split[0].split(",")[1]));
                            return true;
                        case 820:
                            player.teleport(Short.parseShort(split[1].split(",")[0]), Integer.parseInt(split[1].split(",")[1]));
                            return true;
                    }
                }
                break;

            case 526://Fin donjon maitre corbac
                if (player.getCurMap().getId() != 9604)
                    return true;

                obj1 = World.world.getObjTemplate(7703).createNewItem(1, false);
                if (player.addObjet(obj1, true))
                    World.world.addGameObject(obj1, true);
                player.send("Im021;1~7703");
                player.teleport((short) 2985, 279);
                break;

            case 527://Donjon ensablé fin
                if (player.getCurMap().getId() != 10165)
                    return true;

                player.addStaticEmote(19);
                player.teleport((short) 10155, 210);
                break;

            case 964://Signer le registre
                if(client == null) return true;
                if (player.getCurMap().getId() != 10255)
                    return true;
                if (player.get_align() != 1 && player.get_align() != 2)
                    return true;
                if (player.hasItemTemplate(9487, 1)) {
                    String date = player.getItemTemplate(9487, 1).getTxtStat().get(Constant.STATS_DATE);
                    long timeStamp = Long.parseLong(date);
                    if (System.currentTimeMillis() - timeStamp <= 1209600000) // 14 jours
                    {
                        return true;
                    } else {
                        player.removeByTemplateID(9487, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 9487);
                    }
                }

                if (player.hasItemTemplate(9811, 1)) // Formulaire de neutralité
                {
                    player.removeByTemplateID(9811, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 9811);
                    player.modifAlignement(0);
                } else if (player.hasItemTemplate(9812, 1)) // Formulaire de désertion
                {
                    if (player.hasItemTemplate(9488, 1)) {
                        player.removeByTemplateID(9488, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 9488);
                        player.modifAlignement(1);
                    } else if (player.hasItemTemplate(9489, 1)) {
                        player.removeByTemplateID(9489, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 9489);
                        player.modifAlignement(2);
                    }
                    player.removeByTemplateID(9812, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 9812);
                }

                ObjectTemplate t2 = World.world.getObjTemplate(9487);
                GameObject obj2 = t2.createNewItem(1, false);
                obj2.refreshStatsObjet("325#0#0#"
                        + System.currentTimeMillis());
                if (player.addObjet(obj2, false)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                            + "~" + obj2.getTemplate().getId());
                    World.world.addGameObject(obj2, true);
                }

                quest = World.world.getNPCQuestion(Integer.parseInt(this.args));
                if (quest == null) {
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                    return true;
                }
                try {
                    SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 965://Signer le document officiel
                if(client == null) return true;
                if (player.getCurMap().getId() != 10255)
                    return true;
                if (player.get_align() != 1 && player.get_align() != 2)
                    return true;
                if (player.hasItemTemplate(9487, 1)) {
                    String date = player.getItemTemplate(9487, 1).getTxtStat().get(Constant.STATS_DATE);
                    long timeStamp = Long.parseLong(date);
                    if (System.currentTimeMillis() - timeStamp <= 1209600000) // 14 jours
                    {
                        return true;
                    }
                }

                boolean next = false;
                if (player.hasItemTemplate(9811, 1)) // Formulaire de neutralité
                {
                    next = true;
                } else if (player.hasItemTemplate(9812, 1)) // Formulaire de désertion
                {
                    int idTemp = -1;
                    if (player.get_align() == 2) // Brak, donc passer bont
                        idTemp = 9488;
                    else
                        idTemp = 9489;

                    ObjectTemplate t = World.world.getObjTemplate(idTemp);
                    GameObject obj = t.createNewItem(1, false);
                    obj.refreshStatsObjet("325#0#0#"
                            + System.currentTimeMillis());
                    if (player.addObjet(obj, false)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + obj.getTemplate().getId());
                        World.world.addGameObject(obj, true);
                    }
                    next = true;
                }

                if (next) {
                    quest = World.world.getNPCQuestion(Integer.parseInt(this.args));
                    if (quest == null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        return true;
                    }
                    try {
                        SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 963://Formulaire de désertion
                if(client == null) return true;
                if (player.getCurMap().getId() != 10255)
                    return true;
                if (player.get_align() != 1 && player.get_align() != 2)
                    return true;
                if (player.hasItemTemplate(9487, 1)) {
                    String date = player.getItemTemplate(9487, 1).getTxtStat().get(Constant.STATS_DATE);
                    long timeStamp = Long.parseLong(date);
                    if (System.currentTimeMillis() - timeStamp <= 1209600000) // 14 jours
                    {
                        return true;
                    }
                }

                t2 = World.world.getObjTemplate(9812);
                obj2 = t2.createNewItem(1, false);
                obj2.refreshStatsObjet("325#0#0#"
                        + System.currentTimeMillis());
                if (player.addObjet(obj2, false)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                            + "~" + obj2.getTemplate().getId());
                    World.world.addGameObject(obj2, true);
                }

                quest = World.world.getNPCQuestion(Integer.parseInt(this.args));
                if (quest == null) {
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                    return true;
                }
                try {
                    SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 966://Formulaire de neutralité
                if(client == null) return true;
                if (player.getCurMap().getId() != 10255)
                    return true;
                if (player.get_align() != 1 && player.get_align() != 2)
                    return true;
                if (player.hasItemTemplate(9487, 1)) {
                    String date = player.getItemTemplate(9487, 1).getTxtStat().get(Constant.STATS_DATE);
                    long timeStamp = Long.parseLong(date);
                    if (System.currentTimeMillis() - timeStamp <= 1209600000) // 14 jours
                    {
                        return true;
                    }
                }

                int kamas = 256000;
                if (player.getALvl() <= 10)
                    kamas = 500;
                else if (player.getALvl() <= 20)
                    kamas = 1000;
                else if (player.getALvl() <= 30)
                    kamas = 2000;
                else if (player.getALvl() <= 40)
                    kamas = 4000;
                else if (player.getALvl() <= 50)
                    kamas = 8000;
                else if (player.getALvl() <= 60)
                    kamas = 16000;
                else if (player.getALvl() <= 70)
                    kamas = 32000;
                else if (player.getALvl() <= 80)
                    kamas = 64000;
                else if (player.getALvl() <= 90)
                    kamas = 128000;
                else if (player.getALvl() <= 100)
                    kamas = 256000;

                if (player.getKamas() < kamas) {
                    SocketManager.GAME_SEND_MESSAGE_SERVER(player, "10|" + kamas);
                    return true;
                } else {
                    player.setKamas(player.getKamas() - kamas);
                    SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "046;" + kamas);

                    if (player.hasItemTemplate(9811, 1)) {
                        player.removeByTemplateID(9811, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 9811);
                    }

                    ObjectTemplate t = World.world.getObjTemplate(9811);
                    GameObject obj = t.createNewItem(1, false);
                    obj.refreshStatsObjet("325#0#0#"
                            + System.currentTimeMillis());
                    if (player.addObjet(obj, false)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                + "~" + obj.getTemplate().getId());
                        World.world.addGameObject(obj, true);
                    }

                    quest = World.world.getNPCQuestion(Integer.parseInt(this.args));
                    if (quest == null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        return true;
                    }
                    try {
                        SocketManager.GAME_SEND_QUESTION_PACKET(client, quest.parse(player));
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 967://Apprendre bricoleur
                if(client == null) return true;
                if (player.getCurMap().getId() != 8736 && player.getCurMap().getId() != 8737) return true;

                Job job = World.world.getMetier(65);
                if (job == null) return true;

                if (player.getMetierByID(job.getId()) != null) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "111");
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                    player.setIsOnDialogAction(-1);
                    return true;
                }

                if (player.getMetierByID(2) != null && player.getMetierByID(2).get_lvl() < 30 || player.getMetierByID(11) != null && player.getMetierByID(11).get_lvl() < 30 || player.getMetierByID(13) != null && player.getMetierByID(13).get_lvl() < 30 || player.getMetierByID(14) != null && player.getMetierByID(14).get_lvl() < 30 || player.getMetierByID(15) != null && player.getMetierByID(15).get_lvl() < 30 || player.getMetierByID(16) != null && player.getMetierByID(16).get_lvl() < 30 || player.getMetierByID(17) != null && player.getMetierByID(17).get_lvl() < 30 || player.getMetierByID(18) != null && player.getMetierByID(18).get_lvl() < 30 || player.getMetierByID(19) != null && player.getMetierByID(19).get_lvl() < 30 || player.getMetierByID(20) != null && player.getMetierByID(20).get_lvl() < 30 || player.getMetierByID(24) != null && player.getMetierByID(24).get_lvl() < 30 || player.getMetierByID(25) != null && player.getMetierByID(25).get_lvl() < 30 || player.getMetierByID(26) != null && player.getMetierByID(26).get_lvl() < 30 || player.getMetierByID(27) != null && player.getMetierByID(27).get_lvl() < 30 || player.getMetierByID(28) != null && player.getMetierByID(28).get_lvl() < 30 || player.getMetierByID(31) != null && player.getMetierByID(31).get_lvl() < 30 || player.getMetierByID(36) != null && player.getMetierByID(36).get_lvl() < 30 || player.getMetierByID(41) != null && player.getMetierByID(41).get_lvl() < 30 || player.getMetierByID(56) != null && player.getMetierByID(56).get_lvl() < 30 || player.getMetierByID(58) != null && player.getMetierByID(58).get_lvl() < 30 || player.getMetierByID(60) != null && player.getMetierByID(60).get_lvl() < 30) {
                    SocketManager.send(client, "DQ336|4840");
                    return false;
                }

                if (player.totalJobBasic() > 2) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "19");
                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                    player.setExchangeAction(null);
                    player.setIsOnDialogAction(-1);
                } else {
                    if (player.hasItemTemplate(459, 20) && player.hasItemTemplate(7657, 15)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 20 + "~" + 459);
                        player.removeByTemplateID(459, 20);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 15 + "~" + 7657);
                        player.removeByTemplateID(7657, 15);
                        player.learnJob(job);
                        SocketManager.send(client, "DQ3153|4840");
                        return false;
                    } else {
                        SocketManager.send(client, "DQ3151|4840");
                        return false;
                    }
                }
                return true;

            case 968://Fin de donjon toror & tot
                if (player.getCurMap().getId() == (short) 9877
                        || player.getCurMap().getId() == (short) 9881) {
                    player.teleport((short) 9538, 186);
                }
                break;

            case 969://Transformation donjon abra & CM
                if (player.getCurMap().getId() == (short) 8715) // Abra
                {
                    player.teleport((short) 8716, 366);
                    player.setFullMorph(11, false, false);
                } else if (player.getCurMap().getId() == (short) 9120) // CM
                {
                    player.teleport((short) 9121, 69);
                    player.setFullMorph(11, false, false);
                }
                break;

            case 970://Démorph + TP : Donjon abra & CM
                if (player.getCurMap().getId() == (short) 8719) // Abra
                {
                    player.unsetFullMorph();
                    player.teleport((short) 10154, 335);
                } else if (player.getCurMap().getId() == (short) 9123) // CM
                {
                    player.unsetFullMorph();
                    player.teleport((short) 9125, 71);
                }
                break;

            case 971://Entrée du donjon dragoeufs
                if (player.getCurMap().getId() == (short) 9788) {
                    boolean key0 = player.hasItemTemplate(8342, 1) && player.hasItemTemplate(8343, 1), key1 = false, key2 = false;
                    if (key0 || player.hasItemTemplate(10207, 1)) {

                        if(player.hasItemTemplate(10207, 1)) {
                            String stats = player.getItemTemplate(10207).getTxtStat().get(Constant.STATS_NAME_DJ);
                            for(String key : stats.split(",")) {
                                id = Integer.parseInt(key, 16);
                                if (id == 8342) key1 = true;
                                if (id == 8343) key2 = true;
                            }

                            if(key1 && key2){
                                String replace1 = Integer.toHexString(8342), replace2 = Integer.toHexString(8343), newStats = "";
                                for (String i : stats.split(","))
                                    if (!i.equals(replace1) || !i.equals(replace2))
                                        newStats += (newStats.isEmpty() ? i : "," + i);
                                player.getItemTemplate(10207).getTxtStat().remove(Constant.STATS_NAME_DJ);
                                player.getItemTemplate(10207).getTxtStat().put(Constant.STATS_NAME_DJ, newStats);
                                SocketManager.GAME_SEND_UPDATE_ITEM(player, player.getItemTemplate(10207));
                            }
                        }
                        if(key0 && (!key1 || !key2)) {
                            player.removeByTemplateID(8342, 1);
                            player.removeByTemplateID(8343, 1);
                        }
                        if(key0 || (key1 && key2)) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 8342);
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 8343);
                            player.teleport((short) 10098, 407);
                            return true;
                        }
                    }
                }
                SocketManager.GAME_SEND_Im_PACKET(player, "119");
                break;

            case 972://Sortir du donjon skeunk avec le trousseau
                if (player.getCurMap().getId() != (short) 8978)
                    return true;
                if (!player.hasItemTemplate(7935, 1))
                    return true;
                if (!player.hasItemTemplate(7936, 1))
                    return true;
                if (!player.hasItemTemplate(7937, 1))
                    return true;
                if (!player.hasItemTemplate(7938, 1))
                    return true;

                boolean key0 = false;
                if(player.hasItemTemplate(10207, 1)) {
                    String stats = player.getItemTemplate(10207).getTxtStat().get(Constant.STATS_NAME_DJ);
                    for(String key : stats.split(",")) {
                        if (Integer.parseInt(key, 16) == 8073) key0 = true;
                    }

                    if(key0){
                        String replace = Integer.toHexString(8073), newStats = "";
                        for (String i : stats.split(","))
                            if (!i.equals(replace))
                                newStats += (newStats.isEmpty() ? i : "," + i);
                        player.getItemTemplate(10207).getTxtStat().remove(Constant.STATS_NAME_DJ);
                        player.getItemTemplate(10207).getTxtStat().put(Constant.STATS_NAME_DJ, newStats);
                        SocketManager.GAME_SEND_UPDATE_ITEM(player, player.getItemTemplate(10207));
                    }
                } else return true;

                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7935);
                player.removeByTemplateID(7935, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7936);
                player.removeByTemplateID(7936, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7937);
                player.removeByTemplateID(7937, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7938);
                player.removeByTemplateID(7938, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 8073);

                GameObject object = World.world.getObjTemplate(8072).createNewItem(1, false);

                if (player.addObjet(object, false)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~" + object.getTemplate().getId());
                    World.world.addGameObject(object, true);
                }

                player.teleport((short) 9503, 357);
                break;

            case 973://Sortir du donjon skeunk avec la clef
                if (player.getCurMap().getId() != (short) 8978)
                    return true;
                if (!player.hasItemTemplate(7935, 1))
                    return true;
                if (!player.hasItemTemplate(7936, 1))
                    return true;
                if (!player.hasItemTemplate(7937, 1))
                    return true;
                if (!player.hasItemTemplate(7938, 1))
                    return true;
                if (!player.hasItemTemplate(8073, 1))
                    return true;

                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7935);
                player.removeByTemplateID(7935, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7936);
                player.removeByTemplateID(7936, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7937);
                player.removeByTemplateID(7937, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 7938);
                player.removeByTemplateID(7938, 1);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + 8073);
                player.removeByTemplateID(8073, 1);

                ObjectTemplate dofus = World.world.getObjTemplate(8072);
                GameObject obj = dofus.createNewItem(1, false);
                if (player.addObjet(obj, false)) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~" + obj.getTemplate().getId());
                    World.world.addGameObject(obj, true);
                }

                player.teleport((short) 9503, 357);
                break;

            case 974://Sort boomerang perfide
                if (player.getCurMap().getId() != (short) 8978)
                    return true;
                if (!player.hasItemTemplate(8075, 10))
                    return true;
                if (!player.hasItemTemplate(8076, 10))
                    return true;
                if (!player.hasItemTemplate(8077, 10))
                    return true;
                if (!player.hasItemTemplate(8064, 10))
                    return true;
                if (player.hasSpell(364))
                    return true;

                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~"
                        + 8075);
                player.removeByTemplateID(8075, 10);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~"
                        + 8076);
                player.removeByTemplateID(8076, 10);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~"
                        + 8077);
                player.removeByTemplateID(8077, 10);
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 10 + "~"
                        + 8064);
                player.removeByTemplateID(8064, 10);

                player.learnSpell(364, 1, true, true, true);
                break;

            case 975://Entrée salle skeunk
                if (player.getCurMap().getId() != (short) 8973)
                    return true;
                if (!player.hasItemTemplate(7935, 1) || !player.hasItemTemplate(7936, 1) || !player.hasItemTemplate(7937, 1) || !player.hasItemTemplate(7938, 1))
                    return true;

                player.teleport((short) 8977, 448);
                break;
            case 976://Téléportation en Minotoror
                try {
                    if (player.getCurMap().getId() != (short) 9557)
                        return true;
                    if (!player.hasItemTemplate(8305, 1))
                        return true;
                    if (!player.hasItemTemplate(8306, 1))
                        return true;
                    if (!player.hasItemTemplate(7924, 1))
                        return true;

                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 8305);
                    player.removeByTemplateID(8305, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 8306);
                    player.removeByTemplateID(8306, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~"
                            + 7924);
                    player.removeByTemplateID(7924, 1);

                    player.teleport((short) 9880, 399);
                } catch (Exception e) {
                    return true;
                }
                break;

            case 977://Téléportation en salle des dalles Toror
                try {
                    switch (player.getCurMap().getId()) {
                        case 9553:
                        case 9554:
                        case 9555:
                        case 9556:
                        case 9557:
                        case 9558:
                        case 9559:
                        case 9560:
                        case 9561:
                        case 9562:
                        case 9563:
                        case 9564:
                        case 9565:
                        case 9566:
                        case 9567:
                        case 9568:
                        case 9569:
                        case 9570:
                        case 9571:
                        case 9572:
                        case 9573:
                        case 9574:
                        case 9575:
                        case 9576:
                        case 9577:
                            player.teleport((short) 9876, 287);
                            break;
                    }
                } catch (Exception e) {
                    return true;
                }
                break;

            case 978://Téléportation en salle des dalles DC
                try {
                    switch (player.getCurMap().getId()) {
                        case 9372:
                        case 9384:
                        case 9380:
                        case 9381:
                        case 9382:
                        case 9383:
                        case 9393:
                        case 9374:
                        case 9394:
                        case 9390:
                        case 9391:
                        case 9392:
                        case 9373:
                        case 9389:
                        case 9385:
                        case 9386:
                        case 9387:
                        case 9388:
                        case 9371:
                        case 9375:
                        case 9376:
                        case 9377:
                        case 9378:
                        case 9379:
                            player.teleport((short) 9396, 387);
                            break;
                    }
                } catch (Exception e) {
                    return true;
                }
                break;
            case 979://Téléportation labyrinth DC
                try {
                    short newMapID = Short.parseShort(args.split(",", 2)[0]);
                    final GameMap newMap = World.world.getMap(newMapID);
                    int newCellID = Integer.parseInt(args.split(",", 2)[1]);
                    final GameCase curCase = player.getCurCell();
                    final GameMap curMap = player.getCurMap();
                    int idCurCase = curCase.getId();
                    if (idCurCase < 52 || idCurCase > 412) // On monte ou on descend
                    {
                        player.teleportLaby(newMapID, newCellID);
                        TimerWaiter.addNext(() -> {
                            PigDragon.close(curMap, curCase);
                            PigDragon.close(newMap, PigDragon.getDownCell(newMap));
                            PigDragon.close(newMap, PigDragon.getUpCell(newMap));
                            PigDragon.open(newMap, PigDragon.getRightCell(newMap));
                            PigDragon.open(newMap, PigDragon.getLeftCell(newMap));
                        }, 1000, TimerWaiter.DataType.MAP);
                    } else if (idCurCase == 262 || idCurCase == 320 || idCurCase == 144 || idCurCase == 216 || idCurCase == 231 || idCurCase == 274) // A gauche ou a droite
                    {
                        player.teleportLaby(newMapID, newCellID);

                        TimerWaiter.addNext(() -> {
                            PigDragon.close(curMap, curCase);
                            PigDragon.close(newMap, PigDragon.getLeftCell(newMap));
                            PigDragon.close(newMap, PigDragon.getRightCell(newMap));
                            PigDragon.open(newMap, PigDragon.getUpCell(newMap));
                            PigDragon.open(newMap, PigDragon.getDownCell(newMap));
                        }, 1000, TimerWaiter.DataType.MAP);
                    } else {
                        SocketManager.GAME_SEND_MESSAGE(player, "Cette porte n'est pas fonctionnelle. Veuillez reporter la map et la porte sur le forum.");
                        return true;
                    }
                } catch (Exception e) {
                    return true;
                }
                break;

            case 980: // téléportation avec mapsecu, et deux itemssecu supprimés : donjon dc
                try {
                    mapId = Integer.parseInt(args.split(",")[0]);
                    cellId = Integer.parseInt(args.split(",")[1]);
                    int item = Integer.parseInt(args.split(",")[2]);
                    int item2 = Integer.parseInt(args.split(",")[3]);
                    mapSecu = Short.parseShort(args.split(",")[4]);

                    if (player.getCurMap().getId() != mapSecu)
                        return true;
                    if (!player.hasItemTemplate(item, 1) && !player.hasItemTemplate(item2, 1))
                        return true;

                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + item);
                    player.removeByTemplateID(item, 1);
                    SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + item2);
                    player.removeByTemplateID(item2, 1);
                    player.teleport((short) mapId, cellId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 981: // téléportation avec mapsecu et itemsecu : donjon dc
                try {
                    mapId = Integer.parseInt(args.split(",")[0]);
                    cellId = Integer.parseInt(args.split(",")[1]);
                    int item = Integer.parseInt(args.split(",")[2]);
                    mapSecu = Short.parseShort(args.split(",")[3]);

                    if (player.getCurMap().getId() != mapSecu)
                        return true;

                    if (!player.hasItemTemplate(item, 1))
                        return true;

                    player.teleport((short) mapId, cellId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 982: // Mort
                try {
                    player.setFuneral();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 983:
                try {
                    Quest q = Quest.getQuestById(193);
                    if (q == null)
                        return true;
                    GameMap curMap = player.getCurMap();
                    if (curMap.getId() != (short) 10332)
                        return true;
                    if (player.getQuestPersoByQuest(q) == null)
                        q.applyQuest(player);
                    else if (q.getQuestEtapeCurrent(player.getQuestPersoByQuest(q)).getId() != 793)
                        return true;

                    Monster petitChef = World.world.getMonstre(984);
                    if (petitChef == null)
                        return true;
                    Monster.MobGrade mg = petitChef.getGradeByLevel(10);
                    if (mg == null)
                        return true;
                    Monster.MobGroup _mg = new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurCell().getId(), petitChef.getId()
                            + "," + mg.getLevel() + "," + mg.getLevel() + ";");
                    player.getCurMap().startFightVersusMonstres(player, _mg);// Si bug startfight, voir "//Respawn d'un groupe fix" dans fight.java
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 984:
                try {
                    int xp = Integer.parseInt(args.split(",")[0]);
                    int mapCurId = Integer.parseInt(args.split(",")[1]);
                    int idQuest = Integer.parseInt(args.split(",")[2]);

                    if (player.getCurMap().getId() != (short) mapCurId)
                        return true;

                    Quest.QuestPlayer qp = player.getQuestPersoByQuestId(idQuest);
                    if (qp == null)
                        return true;
                    if (qp.isFinish())
                        return true;

                    player.addXp((long) xp);
                    SocketManager.GAME_SEND_Im_PACKET(player, "08;" + xp);
                    qp.setFinish(true);
                    SocketManager.GAME_SEND_Im_PACKET(player, "055;" + idQuest);
                    SocketManager.GAME_SEND_Im_PACKET(player, "056;" + idQuest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 985:
                if(client == null) return true;
                try {
                    int item = Integer.parseInt(args.split(",")[0]);
                    int item2 = Integer.parseInt(args.split(",")[1]);
                    int mapCurId = Integer.parseInt(args.split(",")[2]);
                    int metierId = Integer.parseInt(args.split(",")[3]);

                    if (player.getCurMap().getId() != (short) mapCurId)
                        return true;
                    Job metierArgs = World.world.getMetier(metierId);
                    if (metierArgs == null)
                        return true;

                    if (player.getMetierByID(metierId) != null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        player.setIsOnDialogAction(-1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true; // Si on a déjà le métier
                    }

                    ObjectTemplate t = World.world.getObjTemplate(item2);
                    if (t == null)
                        return true;

                    if (player.hasItemTemplate(item, 1)) {

                        for (Entry<Integer, JobStat> entry : player.getMetiers().entrySet()) {
                            if (entry.getValue().get_lvl() < 30
                                    && !entry.getValue().getTemplate().isMaging()) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                player.setExchangeAction(null);
                                player.setIsOnDialogAction(-1);
                                SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                                return true;
                            }
                        }

                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item);
                        player.removeByTemplateID(item, 1);
                        obj = t.createNewItem(1, false);
                        obj.refreshStatsObjet("325#0#0#"
                                + System.currentTimeMillis());
                        if (player.addObjet(obj, false)) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1
                                    + "~" + obj.getTemplate().getId());
                            World.world.addGameObject(obj, true);
                        }

                        player.learnJob(World.world.getMetier(metierId));
                        Database.getStatics().getPlayerData().update(player);
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 986:
                if(client == null) return true;
                try {
                    int mapCurId = Integer.parseInt(args.split(",")[0]);
                    int item = Integer.parseInt(args.split(",")[1]);
                    int item2 = Integer.parseInt(args.split(",")[2]);
                    int metierId = Integer.parseInt(args.split(",")[3]);

                    if (player.getCurMap().getId() != (short) mapCurId)
                        return true;
                    Job metierArgs = World.world.getMetier(metierId);
                    if (metierArgs == null)
                        return true;

                    if (player.getMetierByID(metierId) != null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        player.setIsOnDialogAction(-1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true; // Si on a déjé le métier
                    }

                    if (player.hasItemTemplate(item, 1)) {
                        player.removeByTemplateID(item, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item);
                        ObjectTemplate t = World.world.getObjTemplate(item2);
                        if (t != null) {
                            obj = t.createNewItem(1, false);
                            obj.refreshStatsObjet("325#0#0#"
                                    + System.currentTimeMillis());
                            if (player.addObjet(obj, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                        + 1 + "~" + obj.getTemplate().getId());
                                World.world.addGameObject(obj, true);
                                Database.getStatics().getPlayerData().update(player);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                                return false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 987:
                if(client == null) return true;
                try {
                    int item = Integer.parseInt(args.split(",")[0]);
                    int item2 = Integer.parseInt(args.split(",")[1]);
                    int item3 = Integer.parseInt(args.split(",")[2]);
                    int mapCurId = Integer.parseInt(args.split(",")[3]);
                    int metierId = Integer.parseInt(args.split(",")[4]);

                    if (player.getCurMap().getId() != (short) mapCurId)
                        return true;
                    Job metierArgs = World.world.getMetier(metierId);
                    if (metierArgs == null)
                        return true;

                    if (player.getMetierByID(metierId) != null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        player.setIsOnDialogAction(-1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true; // Si on a déjé le métier
                    }

                    if (player.hasItemTemplate(item, 1)
                            && player.hasItemTemplate(item2, 1)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item);
                        player.removeByTemplateID(item, 1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item2);
                        player.removeByTemplateID(item2, 1);

                        ObjectTemplate t = World.world.getObjTemplate(item3);
                        if (t != null) {
                            obj = t.createNewItem(1, false);
                            if (player.addObjet(obj, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                        + 1 + "~" + obj.getTemplate().getId());
                                World.world.addGameObject(obj, true);
                                Database.getStatics().getPlayerData().update(player);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                                return false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 988: // devenir pécheur
                if(client == null) return true;
                try {
                    if (player.hasItemTemplate(2107, 1)) {
                        long timeStamp = Long.parseLong(player.getItemTemplate(2107, 1).getTxtStat().get(Constant.STATS_DATE));
                        boolean success = (System.currentTimeMillis()
                                - timeStamp <= 2 * 60 * 1000);
                        NpcQuestion qQuest = World.world.getNPCQuestion(success ? 1171 : 1172);

                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + 2107);
                        player.removeByTemplateID(2107, 1);

                        if (qQuest == null) {
                            SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                            player.setExchangeAction(null);
                            return true;
                        }

                        if (success) {
                            Job metierArgs = World.world.getMetier(36);
                            if (metierArgs == null)
                                return true; // Si le métier n'existe pas
                            if (player.getMetierByID(36) != null) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                player.setExchangeAction(null);
                                player.setIsOnDialogAction(-1);
                                SocketManager.GAME_SEND_Im_PACKET(player, "111");
                                return true; // Si on a déjé le métier
                            }

                            for (Entry<Integer, JobStat> entry : player.getMetiers().entrySet()) {
                                if (entry.getValue().get_lvl() < 30
                                        && !entry.getValue().getTemplate().isMaging()) {
                                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                    player.setExchangeAction(null);
                                    player.setIsOnDialogAction(-1);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                                    return true;
                                }
                            }

                            player.learnJob(World.world.getMetier(36));
                            Database.getStatics().getPlayerData().update(player);
                            SocketManager.GAME_SEND_Ow_PACKET(player);
                        }

                        SocketManager.GAME_SEND_QUESTION_PACKET(client, qQuest.parse(player));
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 989:
                if(client == null) return true;
                try {
                    int mapCurId = Integer.parseInt(args.split(",")[0]);
                    int item = Integer.parseInt(args.split(",")[1]);
                    int item2 = Integer.parseInt(args.split(",")[2]);
                    int metierId = Integer.parseInt(args.split(",")[3]);

                    if (player.getCurMap().getId() != (short) mapCurId)
                        return true;
                    Job metierArgs = World.world.getMetier(metierId);
                    if (metierArgs == null)
                        return true;

                    if (player.getMetierByID(metierId) != null) {
                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                        player.setExchangeAction(null);
                        player.setIsOnDialogAction(-1);
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true; // Si on a déjé le métier
                    }

                    if (player.hasItemTemplate(item, 1)) {
                        ObjectTemplate t = World.world.getObjTemplate(item2);
                        if (t != null) {
                            obj = t.createNewItem(1, false);
                            if (player.addObjet(obj, false)) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                        + 1 + "~" + obj.getTemplate().getId());
                                World.world.addGameObject(obj, true);
                                Database.getStatics().getPlayerData().update(player);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                                return false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 990:
                if(client == null) return true;
                try {
                    if (player.getCurMap().getId() == (short) 7388) {
                        if (player.hasItemTemplate(2039, 1)
                                && player.hasItemTemplate(2041, 1)) {
                            long timeStamp = Long.parseLong(player.getItemTemplate(2039, 1).getTxtStat().get(Constant.STATS_DATE));
                            boolean success = (System.currentTimeMillis()
                                    - timeStamp <= 2 * 60 * 1000);
                            NpcQuestion qQuest = World.world.getNPCQuestion(success ? 2364 : 1175);

                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                    + "~" + 2039);
                            player.removeByTemplateID(2039, 1);
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                    + "~" + 2041);
                            player.removeByTemplateID(2041, 1);

                            if (qQuest == null) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                player.setExchangeAction(null);
                                return true;
                            }

                            if (success) {
                                Job metierArgs = World.world.getMetier(41);
                                if (metierArgs == null)
                                    return true; // Si le métier n'existe pas
                                if (player.getMetierByID(41) != null) {
                                    SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                    player.setExchangeAction(null);
                                    player.setIsOnDialogAction(-1);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "111");
                                    return true; // Si on a déjé le métier
                                }

                                for (Entry<Integer, JobStat> entry : player.getMetiers().entrySet()) {
                                    if (entry.getValue().get_lvl() < 30
                                            && !entry.getValue().getTemplate().isMaging()) {
                                        SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                        player.setExchangeAction(null);
                                        player.setIsOnDialogAction(-1);
                                        SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                                        return true;
                                    }
                                }

                                player.learnJob(World.world.getMetier(41));
                                Database.getStatics().getPlayerData().update(player);
                                SocketManager.GAME_SEND_Ow_PACKET(player);
                            }

                            SocketManager.GAME_SEND_QUESTION_PACKET(client, qQuest.parse(player));
                            return false;
                        } else {
                            player.send("Im14");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 991: // Posséder un item, lancer un combat contre un monstre
                try {
                    int mapCurId = Integer.parseInt(args.split(",")[0]);
                    int item = Integer.parseInt(args.split(",")[1]);
                    int monstre = Integer.parseInt(args.split(",")[2]);
                    int grade = Integer.parseInt(args.split(",")[3]);
                    if (player.getCurMap().getId() == (short) mapCurId) {
                        if (player.hasItemTemplate(item, 1)) {
                            String groupe = monstre + "," + grade + "," + grade
                                    + ";";
                            Monster.MobGroup Mgroupe = new Monster.MobGroup(player.getCurMap().nextObjectId, player.getCurCell().getId(), groupe);
                            player.getCurMap().startFightVersusMonstres(player, Mgroupe); // Si bug startfight, voir "//Respawn d'un groupe fix" dans fight.java
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 992: // Supprime deux items & apprends un métier
                if(client == null) return true;
                try {
                    int item1 = Integer.parseInt(args.split(",")[0]);
                    int item2 = Integer.parseInt(args.split(",")[1]);
                    int mapCurId = Integer.parseInt(args.split(",")[2]);
                    int mId = Integer.parseInt(args.split(",")[3]);
                    if (player.getCurMap().getId() == (short) mapCurId) {
                        if (player.hasItemTemplate(item1, 1)) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                    + "~" + item1);
                            player.removeByTemplateID(item1, 1);
                        }
                        if (player.hasItemTemplate(item2, 1)) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                    + "~" + item2);
                            player.removeByTemplateID(item2, 1);
                        }

                        Job metierArgs = World.world.getMetier(mId);
                        if (metierArgs == null)
                            return true;
                        if (player.getMetierByID(mId) != null) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "111");
                            return true;
                        }

                        for (Entry<Integer, JobStat> entry : player.getMetiers().entrySet()) {
                            if (entry.getValue().get_lvl() < 30
                                    && !entry.getValue().getTemplate().isMaging()) {
                                SocketManager.GAME_SEND_END_DIALOG_PACKET(client);
                                player.setExchangeAction(null);
                                player.setIsOnDialogAction(-1);
                                SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                                return true;
                            }
                        }

                        player.learnJob(World.world.getMetier(mId));
                        Database.getStatics().getPlayerData().update(player);
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 993: // Supprime deux items
                try {
                    int item1 = Integer.parseInt(args.split(",")[0]);
                    int item2 = Integer.parseInt(args.split(",")[1]);
                    if (player.hasItemTemplate(item1, 1)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item1);
                        player.removeByTemplateID(item1, 1);
                    }
                    if (player.hasItemTemplate(item2, 1)) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1
                                + "~" + item2);
                        player.removeByTemplateID(item2, 1);
                    }
                    Database.getStatics().getPlayerData().update(player);
                    SocketManager.GAME_SEND_Ow_PACKET(player);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case 994: // donné un item si on ne l'a pas déjé
                try {
                    int mapID = Integer.parseInt(args.split(",")[0]);
                    int item = Integer.parseInt(args.split(",")[1]);
                    int metierId = Integer.parseInt(args.split(",")[2]);
                    Job metierArgs = World.world.getMetier(metierId);

                    if (metierArgs == null)
                        return true;
                    if (player.getMetierByID(metierId) != null) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true;
                    }

                    GameMap curMapP = player.getCurMap();
                    if (curMapP.getId() == (short) mapID) {
                        if (!player.hasItemTemplate(item, 1)) {
                            if (player.getMetierByID(41) != null) {
                                SocketManager.GAME_SEND_Im_PACKET(player, "182");
                                return true;
                            }
                            ObjectTemplate t = World.world.getObjTemplate(item);
                            if (t != null) {
                                obj = t.createNewItem(1, false);
                                obj.refreshStatsObjet("325#0#0#"
                                        + System.currentTimeMillis());
                                if (player.addObjet(obj, false)) {
                                    SocketManager.GAME_SEND_Im_PACKET(player, "021;"
                                            + 1
                                            + "~"
                                            + obj.getTemplate().getId());
                                    World.world.addGameObject(obj, true);
                                    Database.getStatics().getPlayerData().update(player);
                                    SocketManager.GAME_SEND_Ow_PACKET(player);
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 995: // téléportation passage vers brakmar
                GameMap curMap2 = player.getCurMap();
                if (!player.isInPrison()) {
                    if (curMap2.getId() == (short) 11866) {
                        SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                                + "", "6");
                        player.teleport((short) 11862, 253);
                    } else if (curMap2.getId() == (short) 11862) {
                        SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                                + "", "6");
                        player.teleport((short) 11866, 344);
                    } else {
                        SocketManager.GAME_SEND_Im_PACKET(player, "182");
                        return true;
                    }
                }
                break;

            case 996: // téléportation mine chariot + Animation
                GameMap curMap = player.getCurMap();
                ArrayList<Integer> mapSecure = new ArrayList<Integer>();
                for (String i : args.split("\\,"))
                    mapSecure.add(Integer.parseInt(i));

                if (!mapSecure.contains((int) curMap.getId())) {
                    SocketManager.GAME_SEND_Im_PACKET(player, "182");
                    return true;
                }

                long pKamas4 = player.getKamas();
                if (pKamas4 < 50) {
                    player.teleport((short) 11862, 253);
                    return true;
                }

                if (!player.isInPrison()) {
                    SocketManager.GAME_SEND_GA_PACKET(player.getGameClient(), "", "2", player.getId()
                            + "", "6");
                    long pNewKamas4 = pKamas4 - 50;
                    if (pNewKamas4 < 0)
                        pNewKamas4 = 0;
                    player.setKamas(pNewKamas4);
                    if (player.isOnline())
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                    SocketManager.GAME_SEND_Im_PACKET(player, "046;" + 50);
                    player.teleport((short) 10256, 211);
                }
                break;

            case 997: // Apprendre un métier de forgemagie
                try {
                    int metierID = Integer.parseInt(args.split(",")[0]);
                    int mapIdargs = Integer.parseInt(args.split(",")[1]);
                    Job metierArgs = World.world.getMetier(metierID);

                    if (metierArgs == null)
                        return true; // Si le métier n'existe pas
                    if (player.getMetierByID(metierID) != null) {
                        SocketManager.GAME_SEND_Im_PACKET(player, "111");
                        return true; // Si on a déjé le métier
                    }

                    GameMap curMapPerso = player.getCurMap();
                    if (curMapPerso.getId() != (short) mapIdargs)
                        return true; // Map secure

                    if (metierArgs.isMaging()) // Si c'est du FM
                    {
                        JobStat metierBase = player.getMetierByID(World.world.getMetierByMaging(metierID));
                        if (metierBase == null)
                            return true; // Si la base n'existe pas
                        if (metierBase.get_lvl() < 65) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "111");
                            return true; // Si la base n'est pas assez hl
                        } else if (player.totalJobFM() > 2) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "19");
                            return true; // On compte les métiers déja acquis si c'est supérieur a 2 on ignore
                        } else {
                            player.learnJob(World.world.getMetier(metierID));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    GameServer.a();
                }
                break;

            case 998://Donjon abraknyde salle des cases
                if (player.getCurMap().getId() == 10154
                        && player.getCurCell().getId() == 142) {
                    player.teleport((short) 8721, 395);
                } else {
                    SocketManager.GAME_SEND_MESSAGE(player, "Vous n'êtes pas devant le PNJ.");
                }
                break;

            /** Fin Donjon **/
            case 999:
                player.teleport(this.map, Integer.parseInt(this.args));
                break;

            case 1000:
                map = Short.parseShort(this.args.split(",")[0]);
                cell = Integer.parseInt(this.args.split(",")[1]);
                player.teleport(map, cell);
                player.set_savePos(map+","+cell);
                SocketManager.GAME_SEND_Im_PACKET(player, "06");
                break;

            case 1001:
                map = Short.parseShort(this.args.split(",")[0]);
                cell = Integer.parseInt(this.args.split(",")[1]);
                player.teleport(map, cell);
                break;
                
            case 1002:						// George Cash, don de 500.000 kamas
            	long yKamas = player.getKamas();
            	long ynewKamas = yKamas + 500000;
            	player.setKamas(ynewKamas);
            	SocketManager.GAME_SEND_MESSAGE(player, "Tu as gagné 500000 kamas.", "009900");
            	 if (player.isOnline())
                     SocketManager.GAME_SEND_STATS_PACKET(player);
                break;

            default:
                break;
        }
        return true;
    }
}
