package org.starloco.locos.common;

import com.singularsys.jep.Jep;
import com.singularsys.jep.JepException;
import org.starloco.locos.client.Player;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.other.Action;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.Quest_Etape;

import java.util.ArrayList;
import java.util.Map.Entry;

public class ConditionParser {

    public static boolean validConditions(Player perso, String req) {
        if (req == null || req.equals(""))
            return true;
        if (req.contains("BI"))
            return false;
        if (perso == null)
            return false;
        Jep jep = new Jep();
        req = req.replace("&", "&&").replace("=", "==").replace("|", "||").replace("!", "!=").replace("~", "==");
        if (req.contains("Sc"))
            return true;
        if (req.contains("Pg")) // C'est les dons que l'on gagne lors des quêtes d'alignement, connaissance des potions etc ... ce n'est pas encore codé !
            return false;
        if (req.contains("RA"))
            return haveRA(req, perso);
        if (req.contains("RO"))
            return haveRO(req, perso);
        if (req.contains("Mph"))
            return haveMorph(req, perso);
        if (req.contains("PO"))
            req = havePO(req, perso);
        if (req.contains("PN"))
            req = canPN(req, perso);
        if (req.contains("PJ"))
            req = canPJ(req, perso);
        if (req.contains("JOB"))
            req = haveJOB(req, perso);
        if (req.contains("NPC"))
            return haveNPC(req, perso);
        if (req.contains("QEt"))
            return haveQEt(req, perso);
        if (req.contains("QE"))
            return haveQE(req, perso);
        if (req.contains("QT"))
            return haveQT(req, perso);
        if (req.contains("Ce"))
            return haveCe(req, perso);
        if (req.contains("TiT"))
            return haveTiT(req, perso);
        if (req.contains("Ti"))
            return haveTi(req, perso);
        if (req.contains("Qa"))
            return haveQa(req, perso);
        if (req.contains("Pj"))
            return havePj(req, perso);
        if (req.contains("AM"))
            return haveMetier(req, perso);

        try {
            //Stats stuff compris
            jep.addVariable("CI", perso.getTotalStats().getEffect(Constant.STATS_ADD_INTE));
            jep.addVariable("CV", perso.getTotalStats().getEffect(Constant.STATS_ADD_VITA));
            jep.addVariable("CA", perso.getTotalStats().getEffect(Constant.STATS_ADD_AGIL));
            jep.addVariable("CW", perso.getTotalStats().getEffect(Constant.STATS_ADD_SAGE));
            jep.addVariable("CC", perso.getTotalStats().getEffect(Constant.STATS_ADD_CHAN));
            jep.addVariable("CS", perso.getTotalStats().getEffect(Constant.STATS_ADD_FORC));
            jep.addVariable("CM", perso.getStats().getEffect(Constant.STATS_ADD_PM));
            //Stats de bases
            jep.addVariable("Ci", perso.getStats().getEffect(Constant.STATS_ADD_INTE));
            jep.addVariable("Cs", perso.getStats().getEffect(Constant.STATS_ADD_FORC));
            jep.addVariable("Cv", perso.getStats().getEffect(Constant.STATS_ADD_VITA));
            jep.addVariable("Ca", perso.getStats().getEffect(Constant.STATS_ADD_AGIL));
            jep.addVariable("Cw", perso.getStats().getEffect(Constant.STATS_ADD_SAGE));
            jep.addVariable("Cc", perso.getStats().getEffect(Constant.STATS_ADD_CHAN));
            //Autre
            jep.addVariable("Ps", perso.get_align());//Alignement
            jep.addVariable("Pa", perso.getALvl());
            jep.addVariable("PP", perso.getGrade());//Grade
            jep.addVariable("PL", perso.getLevel());//Niveau
            jep.addVariable("PK", perso.getKamas());//Kamas
            jep.addVariable("PG", perso.getClasse());//Classe
            jep.addVariable("PS", perso.getSexe());//Sexe
            jep.addVariable("PZ", 1);//Abonnement
            jep.addVariable("PX", (perso.getGroupe() != null));//Niveau GM
            jep.addVariable("PW", perso.getMaxPod());//MaxPod
            if (perso.getCurMap().getSubArea() != null)
                jep.addVariable("PB", perso.getCurMap().getSubArea().getId());//SubArea
            jep.addVariable("PR", (perso.getWife() > 0 ? 1 : 0));//Marié ou pas
            jep.addVariable("SI", perso.getCurMap().getId());//Mapid
            jep.addVariable("MiS", perso.getId());//Les pierres d'ames sont lancables uniquement par le lanceur.
            jep.addVariable("MA", perso.getAlignMap());//Pandala
            jep.addVariable("PSB", perso.getAccount().getPoints());//Points Boutique
            jep.addVariable("CF", (perso.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR) == null ? -1 : perso.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR).getTemplate().getId()));//Personnage suiveur
            jep.addVariable("PRE", perso.getPrestige());
            jep.parse(req);
            Object result = jep.evaluate();
            boolean ok = false;

            if (result != null)
                ok = Boolean.valueOf(result.toString());
            return ok;
        } catch (JepException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean haveMorph(String c, Player p) {
        if (c.equalsIgnoreCase(""))
            return false;
        int morph = -1;
        try {
            morph = Integer.parseInt((c.contains("==") ? c.split("==")[1] : c.split("!=")[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (p.getMorphId() == morph)
            return c.contains("==");
        else
            return !c.contains("==");
    }

    private static boolean haveMetier(String c, Player p) {
        if (p.getMetiers() == null || p.getMetiers().isEmpty())
            return false;
        for (Entry<Integer, JobStat> entry : p.getMetiers().entrySet()) {
            if (entry.getValue() != null)
                return true;
        }
        return false;
    }

    private static boolean havePj(String c, Player p) {
        if (c.equalsIgnoreCase(""))
            return false;
        for (String s : c.split("\\|\\|")) {
            String[] k = s.split("==");
            int id;
            try {
                id = Integer.parseInt(k[1]);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (p.getMetierByID(id) != null)
                return true;
        }
        return false;
    }

    //Avoir la quête en cours
    private static boolean haveQa(String req, Player player) {
        int id = Integer.parseInt((req.contains("==") ? req.split("==")[1] : req.split("!=")[1]));
        Quest q = Quest.getQuestById(id);
        if (q == null)
            return (!req.contains("=="));

        Quest.QuestPlayer qp = player.getQuestPersoByQuest(q);
        if (qp == null)
            return (!req.contains("=="));

        return !qp.isFinish() || (!req.contains("=="));

    }

    // être à l'étape id. Elle ne doit pas être validé et celle d'avant doivent l'être.
    private static boolean haveQEt(String req, Player player) {
        int id = Integer.parseInt((req.contains("==") ? req.split("==")[1] : req.split("!=")[1]));
        Quest_Etape qe = Quest_Etape.getQuestEtapeById(id);
        if (qe != null) {
            Quest q = qe.getQuestData();
            if (q != null) {
                Quest.QuestPlayer qp = player.getQuestPersoByQuest(q);
                if (qp != null) {
                    Quest_Etape current = q.getQuestEtapeCurrent(qp);
                    if (current == null)
                        return false;
                    if (current.getId() == qe.getId())
                        return (req.contains("=="));
                }
            }
        }
        return false;
    }

    private static boolean haveTiT(String req, Player player) {
        if (req.contains("==")) {
            String split = req.split("==")[1];
            if (split.contains("&&")) {
                int item = Integer.parseInt(split.split("&&")[0]);
                int time = Integer.parseInt(split.split("&&")[1]);
                int item2 = Integer.parseInt(split.split("&&")[2]);
                if (player.hasItemTemplate(item2, 1)
                        && player.hasItemTemplate(item, 1)) {
                    long timeStamp = Long.parseLong(player.getItemTemplate(item, 1).getTxtStat().get(Constant.STATS_DATE));
                    if (System.currentTimeMillis() - timeStamp <= time)
                        return true;
                }
            }
        }
        return false;
    }

    private static boolean haveTi(String req, Player player) {
        if (req.contains("==")) {
            String split = req.split("==")[1];
            if (split.contains(",")) {
                String[] split2 = split.split(",");
                int item = Integer.parseInt(split2[0]);
                int time = Integer.parseInt(split2[1]) * 60 * 1000;
                if (player.hasItemTemplate(item, 1)) {
                    long timeStamp = Long.parseLong(player.getItemTemplate(item, 1).getTxtStat().get(Constant.STATS_DATE));
                    if (System.currentTimeMillis() - timeStamp > time)
                        return true;
                }
            }
        }
        return false;
    }

    private static boolean haveCe(String req, Player player) {
        java.util.Map<Integer, Couple<Integer, Integer>> dopeuls = Action.getDopeul();
        GameMap map = player.getCurMap();
        if (dopeuls.containsKey((int) map.getId())) {
            Couple<Integer, Integer> couple = dopeuls.get((int) map.getId());
            if (couple == null)
                return false;

            int IDmob = couple.first;
            int certificat = Constant.getCertificatByDopeuls(IDmob);

            if (certificat == -1)
                return false;

            if (player.hasItemTemplate(certificat, 1)) {
                String txt = player.getItemTemplate(certificat, 1).getTxtStat().get(Constant.STATS_DATE);
                if (txt.contains("#"))
                    txt = txt.split("#")[3];
                long timeStamp = Long.parseLong(txt);
                return System.currentTimeMillis() - timeStamp > 86400000;
            } else
                return true;
        }
        return false;
    }

    // Avoir la quête en cours.
    private static boolean haveQE(String req, Player player) {
        if (player == null)
            return false;
        int id = Integer.parseInt((req.contains("==") ? req.split("==")[1] : req.split("!=")[1]));
        Quest.QuestPlayer qp = player.getQuestPersoByQuestId(id);
        if (req.contains("==")) {
            return qp != null && !qp.isFinish();
        } else {
            return qp == null || qp.isFinish();
        }
    }

    private static boolean haveQT(String req, Player player) {
        int id = Integer.parseInt((req.contains("==") ? req.split("==")[1] : req.split("!=")[1]));

        Quest.QuestPlayer quest = player.getQuestPersoByQuestId(id);
        if (req.contains("=="))
            return (quest != null && quest.isFinish());
        else
            return (quest == null || !quest.isFinish());
    }

    private static boolean haveNPC(String req, Player perso) {
        switch (perso.getCurMap().getId()) {
            case 9052:
                if (perso.getCurCell().getId() == 268
                        && perso.get_orientation() == 7)//TODO
                    return true;
            case 8905:
                ArrayList<Integer> cell = new ArrayList<Integer>();
                for (String i : "168,197,212,227,242,183,213,214,229,244,245,259".split("\\,"))
                    cell.add(Integer.parseInt(i));
                if (cell.contains(perso.getCurCell().getId()))
                    return true;
        }
        return false;
    }

    public static boolean haveRO(String condition, Player player) {
        try {
            for (String cond : condition.split("&&")) {
                String[] split = cond.split("==")[1].split(",");
                int id = Integer.parseInt(split[0]), qua = Integer.parseInt(split[1]);

                if (player.hasItemTemplate(id, qua)) {
                    player.removeByTemplateID(id, qua);
                    return true;
                } else {
                    SocketManager.GAME_SEND_Im_PACKET(player, "14");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean haveRA(String condition, Player player) {
        try {
            for (String cond : condition.split("&&")) {
                String[] split = cond.split("==")[1].split(",");
                int id = Integer.parseInt(split[0]), qua = Integer.parseInt(split[1]);

                if (!player.hasItemTemplate(id, qua))
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static String havePO(String cond, Player perso)//On remplace les PO par leurs valeurs si possession de l'item
    {
        boolean Jump = false;
        boolean ContainsPO = false;
        boolean CutFinalLenght = true;
        String copyCond = "";
        int finalLength = 0;

        if (cond.contains("&&")) {
            for (String cur : cond.split("&&")) {
                if (cond.contains("==")) {
                    for (String cur2 : cur.split("==")) {
                        if (cur2.contains("PO")) {
                            ContainsPO = true;
                            continue;
                        }
                        if (Jump) {
                            copyCond += cur2;
                            Jump = false;
                            continue;
                        }
                        if (!cur2.contains("PO") && !ContainsPO) {
                            copyCond += cur2 + "==";
                            Jump = true;
                            continue;
                        }
                        if (cur2.contains("!="))
                            continue;
                        ContainsPO = false;
                        if (perso.hasItemTemplate(Integer.parseInt(cur2), 1)) {
                            copyCond += Integer.parseInt(cur2) + "=="
                                    + Integer.parseInt(cur2);
                        } else {
                            copyCond += Integer.parseInt(cur2) + "==" + 0;
                        }
                    }
                }
                if (cond.contains("!=")) {
                    for (String cur2 : cur.split("!=")) {
                        if (cur2.contains("PO")) {
                            ContainsPO = true;
                            continue;
                        }
                        if (Jump) {
                            copyCond += cur2;
                            Jump = false;
                            continue;
                        }
                        if (!cur2.contains("PO") && !ContainsPO) {
                            copyCond += cur2 + "!=";
                            Jump = true;
                            continue;
                        }
                        if (cur2.contains("=="))
                            continue;
                        ContainsPO = false;
                        if (perso.hasItemTemplate(Integer.parseInt(cur2), 1)) {
                            copyCond += Integer.parseInt(cur2) + "!="
                                    + Integer.parseInt(cur2);
                        } else {
                            copyCond += Integer.parseInt(cur2) + "!=" + 0;
                        }
                    }
                }
                copyCond += "&&";
            }
        } else if (cond.contains("||")) {
            for (String cur : cond.split("\\|\\|")) {
                if (cond.contains("==")) {
                    for (String cur2 : cur.split("==")) {
                        if (cur2.contains("PO")) {
                            ContainsPO = true;
                            continue;
                        }
                        if (Jump) {
                            copyCond += cur2;
                            Jump = false;
                            continue;
                        }
                        if (!cur2.contains("PO") && !ContainsPO) {
                            copyCond += cur2 + "==";
                            Jump = true;
                            continue;
                        }
                        if (cur2.contains("!="))
                            continue;
                        ContainsPO = false;
                        if (perso.hasItemTemplate(Integer.parseInt(cur2), 1)) {
                            copyCond += Integer.parseInt(cur2) + "=="
                                    + Integer.parseInt(cur2);
                        } else {
                            copyCond += Integer.parseInt(cur2) + "==" + 0;
                        }
                    }
                }
                if (cond.contains("!=")) {
                    for (String cur2 : cur.split("!=")) {
                        if (cur2.contains("PO")) {
                            ContainsPO = true;
                            continue;
                        }
                        if (Jump) {
                            copyCond += cur2;
                            Jump = false;
                            continue;
                        }
                        if (!cur2.contains("PO") && !ContainsPO) {
                            copyCond += cur2 + "!=";
                            Jump = true;
                            continue;
                        }
                        if (cur2.contains("=="))
                            continue;
                        ContainsPO = false;
                        if (perso.hasItemTemplate(Integer.parseInt(cur2), 1)) {
                            copyCond += Integer.parseInt(cur2) + "!="
                                    + Integer.parseInt(cur2);
                        } else {
                            copyCond += Integer.parseInt(cur2) + "!=" + 0;
                        }
                    }
                }
                copyCond += "||";
            }
        } else {
            CutFinalLenght = false;
            if (cond.contains("==")) {
                for (String cur : cond.split("==")) {
                    if (cur.contains("PO"))
                        continue;
                    if (cur.contains("!="))
                        continue;
                    if (perso.hasItemTemplate(Integer.parseInt(cur), 1))
                        copyCond += Integer.parseInt(cur) + "=="
                                + Integer.parseInt(cur);
                    else
                        copyCond += Integer.parseInt(cur) + "==" + 0;
                }
            }
            if (cond.contains("!=")) {
                for (String cur : cond.split("!=")) {
                    if (cur.contains("PO"))
                        continue;
                    if (cur.contains("=="))
                        continue;
                    if (perso.hasItemTemplate(Integer.parseInt(cur), 1))
                        copyCond += Integer.parseInt(cur) + "!="
                                + Integer.parseInt(cur);
                    else
                        copyCond += Integer.parseInt(cur) + "!=" + 0;
                }
            }
        }
        if (CutFinalLenght) {
            finalLength = (copyCond.length() - 2);//On retire les deux derniers carractères (|| ou &&)
            copyCond = copyCond.substring(0, finalLength);
        }
        return copyCond;
    }

    public static String canPN(String cond, Player perso)//On remplace le PN par 1 et si le nom correspond == 1 sinon == 0
    {
        String copyCond = "";
        for (String cur : cond.split("==")) {
            if (cur.contains("PN")) {
                copyCond += "1==";
                continue;
            }
            if (perso.getName().toLowerCase().compareTo(cur) == 0)
                copyCond += "1";
            else
                copyCond += "0";
        }
        return copyCond;
    }

    public static String canPJ(String cond, Player perso)//On remplace le PJ par 1 et si le metier correspond == 1 sinon == 0
    {
        String copyCond = "";
        if (cond.contains("==")) {
            String[] cur = cond.split("==");
            if (perso.getMetierByID(Integer.parseInt(cur[1])) != null)
                copyCond = "1==1";
            else
                copyCond = "1==0";
        } else if (cond.contains(">")) {
            if (cond.contains("||")) {
                for (String cur : cond.split("\\|\\|")) {
                    if (!cur.contains(">"))
                        continue;
                    String[] _cur = cur.split(">");
                    if (!_cur[1].contains(","))
                        continue;
                    String[] m = _cur[1].split(",");
                    JobStat js = perso.getMetierByID(Integer.parseInt(m[0]));
                    if (!copyCond.equalsIgnoreCase(""))
                        copyCond += "||";
                    if (js != null)
                        copyCond += js.get_lvl() + ">" + m[1];
                    else
                        copyCond += "1==0";
                }
            } else {
                String[] cur = cond.split(">");
                String[] m = cur[1].split(",");
                JobStat js = perso.getMetierByID(Integer.parseInt(m[0]));
                if (js != null)
                    copyCond = js.get_lvl() + ">" + m[1];
                else
                    copyCond = "1==0";
            }
        }
        return copyCond;
    }

    public static String haveJOB(String cond, Player perso) {
        String copyCond = "";
        if (perso.getMetierByID(Integer.parseInt(cond.split("==")[1])) != null)
            copyCond = "1==1";
        else
            copyCond = "0==1";
        return copyCond;
    }

    public static boolean stackIfSimilar(GameObject obj, GameObject newObj) {
    	
    	if(obj.getTxtStat().get(Constant.STATS_MIMIBIOTE) != null) return false;
    	if(newObj.getTxtStat().get(Constant.STATS_MIMIBIOTE) != null) return false;
    	
        switch(obj.getTemplate().getId()) {
            case 10275:
            case 8378:
                if(obj.getTemplate().getId() == newObj.getTemplate().getId())
                    return false;
        }
        return obj.getTemplate().getId() == newObj.getTemplate().getId() && obj.getStats().isSameStats(newObj.getStats()) && !Constant.isIncarnationWeapon(newObj.getTemplate().getId())
                && newObj.getTemplate().getType() != Constant.ITEM_TYPE_CERTIFICAT_CHANIL
                && newObj.getTemplate().getType() != Constant.ITEM_TYPE_FAMILIER
                && newObj.getTemplate().getType() != Constant.ITEM_TYPE_PIERRE_AME_PLEINE
                && newObj.getTemplate().getType() != Constant.ITEM_TYPE_OBJET_ELEVAGE
                && newObj.getTemplate().getType() != Constant.ITEM_TYPE_CERTIF_MONTURE
                && newObj.getTemplate().getType() != Constant.ITEM_TYPE_OBJET_VIVANT
                &&(newObj.getTemplate().getType() != Constant.ITEM_TYPE_QUETES || Constant.isFlacGelee(obj.getTemplate().getId()) || Constant.isDoplon(obj.getTemplate().getId()))
                && obj.getPosition() == Constant.ITEM_POS_NO_EQUIPED;
    }
}
