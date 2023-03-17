package org.starloco.locos.entity.npc;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.ConditionParser;
import org.starloco.locos.game.world.World;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.other.Action;
import org.starloco.locos.other.Dopeul;
import org.starloco.locos.quest.Quest;
import org.starloco.locos.quest.Quest.QuestPlayer;
import org.starloco.locos.quest.Quest_Etape;

import java.util.Map.Entry;

public class NpcQuestion {

    private int id;
    private String answers, args, condition, falseQuestion;

    public NpcQuestion(int id, String answers, String args, String condition,
                       String falseQuestion) {
        this.id = id;
        this.answers = answers;
        this.args = args;
        this.condition = condition;
        this.falseQuestion = falseQuestion;
    }

    public int getId() {
        return id;
    }

    public String getAnwsers() {
        return answers;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String conditionsReponse(Player player) {
        String _str = "";
        try {
            String[] split = this.answers.split(";");
            boolean first = true;
            if (split != null && split.length > 0) {
                for (String loc1 : split) {
                    if (loc1.equalsIgnoreCase(""))
                        continue;
                    Integer ans = Integer.parseInt(loc1);
                    NpcAnswer answer = World.world.getAnswers().get(ans);
                    // answer.getActions() avec id = 40, on récupère l'id de quête. Si on a la quête alors on affiche pas !
                    if (answer == null)
                        continue;
                    boolean ok = true;
                    for (Action action : answer.getActions()) // par action
                    {
                        switch (action.getId()) {
                            case 15: // Si on donne une clef
                                String args = action.getArgs();
                                int clef = Integer.parseInt(args.split(",")[2]);
                                if (!player.hasItemTemplate(clef, 1))
                                    ok = false;
                                break;
                            case 16: // Si on montre une clef
                                args = action.getArgs();
                                clef = Integer.parseInt(args.split(",")[2]);
                                if (!player.hasItemTemplate(clef, 1))
                                    ok = false;
                                break;
                            case 6: // Si on apprend un métier
                                int mId = Integer.parseInt(action.getArgs().split(",")[0]);
                                int cId = Integer.parseInt(action.getArgs().split(",")[1]);
                                if (player.getCurMap().getId() != (short) cId)
                                    ok = false;
                                else if (player.totalJobBasic() > 2)
                                    ok = false;
                                else if (player.getMetierByID(mId) != null)
                                    ok = false; // S'il a déjà le métier, alors on dégage
                                break;

                            case 40: // Si on apprend une quête
                                if (!player.getQuestPerso().isEmpty()) {
                                    for (QuestPlayer QP : player.getQuestPerso().values()) {
                                        if (QP.getQuest().getId() == Integer.parseInt(action.getArgs()))
                                            ok = false; // S'il a la quête on dégage
                                    }
                                }
                                break;

                            case 997:
                                int mId2 = Integer.parseInt(action.getArgs().split(",")[0]);
                                int cId2 = Integer.parseInt(action.getArgs().split(",")[1]);
                                if (player.getCurMap().getId() != (short) cId2)
                                    ok = false;
                                else if (player.getMetierByID(mId2) != null)
                                    ok = false; // S'il a déjà le métier, alors on dégage
                                else if (player.totalJobFM() > 2)
                                    ok = false;
                                else if (World.world.getMetier(mId2).isMaging()) // Sinon si c'est un métier de FM
                                {
                                    JobStat metier = player.getMetierByID(World.world.getMetierByMaging(mId2)); // On récupère le métier associé
                                    if (metier != null) // S'il existe
                                    {
                                        if (metier.get_lvl() < 65)
                                            ok = false; // S'il n'a pas le niveau on dégage
                                    } else
                                        ok = false; // S'il n'existe pas on dégage
                                }
                                break;
                        }
                    }

                    if (!player.getQuestPerso().isEmpty() && ok) // par quête
                    {
                        for (QuestPlayer QP : player.getQuestPerso().values()) {
                            if (QP.isFinish() || QP.getQuest() == null)
                                continue;
                            for (Quest_Etape q : QP.getQuest().getQuestEtapeList()) {
                                if (q == null)
                                    continue;
                                if (QP.isQuestEtapeIsValidate(q))
                                    continue;
                                if (q.getValidationType() == ans) {
                                    switch (q.getType()) {
                                        case 3: // Si on doit donner des items
                                            for (Entry<Integer, Integer> _entry : q.getItemNecessaryList().entrySet()) {
                                                if (!player.hasItemTemplate(_entry.getKey(), _entry.getValue())) {
                                                    ok = false;
                                                }
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    }

                    if (ok) {
                        String[][] s = Constant.HUNTING_QUESTS;
                        for (int v = 0; v < s.length; v++) {
                            if (Integer.parseInt(s[v][6]) == answer.getId()) // Si la réponse est une traque de monstres
                            {
                                for (QuestPlayer QP : player.getQuestPerso().values()) {
                                    boolean k = true;
                                    if (QP.getQuest().getId() == Integer.parseInt(s[v][5])) // S'il a la quête
                                    {
                                        k = false;
                                        GameObject suiveur = player.getObjetByPos(Constant.ITEM_POS_PNJ_SUIVEUR);
                                        if (suiveur != null) // S'il a un pnj suiveur
                                        {
                                            ok = suiveur.getTemplate().getId() == Integer.parseInt(s[v][4]);
                                            break;
                                        } else
                                            ok = false;
                                    }
                                    if (k)
                                        ok = false;
                                }
                            }
                        }
                    }

                    if (ok) // En fonction des réponses
                    {
                        GameMap mapActuel = player.getCurMap();
                        Integer IDmob = null;
                        int certificat = -1;
                        java.util.Map<Integer, Couple<Integer, Integer>> dopeuls = Action.getDopeul();
                        switch (answer.getId()) {
                            case 4643:
                                if (player.getALvl() > 10)
                                    ok = false;
                                break;
                            case 4644:
                                if (player.getALvl() <= 10 || player.getALvl() > 20)
                                    ok = false;
                                break;
                            case 4645:
                                if (player.getALvl() <= 20 || player.getALvl() > 30)
                                    ok = false;
                                break;
                            case 4646:
                                if (player.getALvl() <= 30 || player.getALvl() > 40)
                                    ok = false;
                                break;
                            case 4647:
                                if (player.getALvl() <= 40 || player.getALvl() > 50)
                                    ok = false;
                                break;
                            case 4648:
                                if (player.getALvl() <= 50 || player.getALvl() > 60)
                                    ok = false;
                                break;
                            case 4649:
                                if (player.getALvl() <= 60 || player.getALvl() > 70)
                                    ok = false;
                                break;
                            case 4650:
                                if (player.getALvl() <= 70 || player.getALvl() > 80)
                                    ok = false;
                                break;
                            case 4651:
                                if (player.getALvl() <= 80 || player.getALvl() > 90)
                                    ok = false;
                                break;
                            case 4652:
                                if (player.getALvl() <= 90)
                                    ok = false;
                                break;
                            case 4639:
                                if (player.get_align() != 2)
                                    ok = false;
                                break;
                            case 4637:
                                if (player.get_align() != 2)
                                    ok = false;
                                break;
                            case 4641:
                                if (player.get_align() != 1)
                                    ok = false;
                                break;
                            case 4638:
                                if (player.get_align() != 1)
                                    ok = false;
                                break;
                            case 4653:
                                if (!player.hasItemTemplate(9811, 1))
                                    ok = false;
                                break;
                            case 4654:
                                if (!player.hasItemTemplate(9812, 1))
                                    ok = false;
                                break;
                            case 4655:
                                if (!player.hasItemTemplate(9811, 1))
                                    ok = false;
                                break;
                            case 4656:
                                if (!player.hasItemTemplate(9812, 1) || player.get_align() != 2)
                                    ok = false;
                                break;
                            case 4657:
                                if (!player.hasItemTemplate(9812, 1) || player.get_align() != 1)
                                    ok = false;
                                break;
                            case 7453:
                                if (!player.hasItemTemplate(10563, 1))
                                    ok = false;
                                break;
                            case 2769:
                                if (!player.hasItemTemplate(8077, 10)
                                        || !player.hasItemTemplate(8076, 10)
                                        || !player.hasItemTemplate(8075, 10)
                                        || !player.hasItemTemplate(8064, 10))
                                    ok = false;
                                break;
                            case 2754:
                                if (player.getCurMap().getId() != (short) 9717)
                                    ok = false;
                                else if (player.hasSpell(414))
                                    ok = false;
                                else if (!player.hasItemTemplate(7904, 50)
                                        || !player.hasItemTemplate(7903, 50))
                                    ok = false;
                                break;
                            case 2962:
                                if (player.getCurMap().getId() != (short) 10199)
                                    ok = false;
                                break;
                            case 2963:
                                if (player.getCurMap().getId() != (short) 10213)
                                    ok = false;
                                break;
                            case 3355:
                                Quest q = Quest.getQuestById(198);
                                if (q != null)
                                    if (player.getQuestPersoByQuest(q) != null)
                                        ok = false;
                                break;
                            case 528:
                                if (player.hasItemTemplate(1469, 1))
                                    ok = false;
                                else if (player.getMetierByID(26) != null)
                                    ok = false;
                                break;
                            case 530:
                                if (!player.hasItemTemplate(1469, 1))
                                    ok = false;
                                break;
                            case 531:
                                if (!player.hasItemTemplate(1470, 1))
                                    ok = false;
                                break;
                            case 532:
                                if (!player.hasItemTemplate(1471, 1))
                                    ok = false;
                                break;
                            case 534:
                                if (!player.hasItemTemplate(1472, 1))
                                    ok = false;
                                break;
                            case 2047:
                                boolean metier30 = true;
                                for (Entry<Integer, JobStat> entry : player.getMetiers().entrySet()) {
                                    if (entry.getValue().get_lvl() < 30)
                                        metier30 = false;
                                }
                                if (player.hasItemTemplate(2107, 1))
                                    ok = false;
                                else if (!player.hasItemTemplate(2106, 1))
                                    ok = false;
                                else if (player.getMetierByID(36) != null)
                                    ok = false;
                                else if (!metier30)
                                    ok = false;
                                break;
                            case 2037:
                                if (player.hasItemTemplate(2106, 1))
                                    ok = false;
                                else if (!player.hasItemTemplate(2107, 1))
                                    ok = false;
                                break;
                            case 2013:
                                if (player.hasItemTemplate(2106, 1))
                                    ok = false;
                                else if (player.hasItemTemplate(2107, 1))
                                    ok = false;
                                break;
                            case 1968:
                                if (!player.hasItemTemplate(2039, 1))
                                    ok = false;
                                else if (!player.hasItemTemplate(2041, 1))
                                    ok = false;
                                break;
                            case 1962:
                                if (player.hasItemTemplate(2039, 1))
                                    ok = false;
                                break;
                            case 1967:
                                if ((!player.hasItemTemplate(2039, 1))
                                        || (player.hasItemTemplate(2041, 1)))
                                    ok = false;
                                break;
                            case 1509: // S'entrainer avec un dopeul
                                if (dopeuls.containsKey((int) mapActuel.getId())) {
                                    IDmob = dopeuls.get((int) mapActuel.getId()).first;
                                } else
                                    break;

                                certificat = Constant.getCertificatByDopeuls(IDmob);
                                if (certificat == -1)
                                    break;

                                if (player.hasItemTemplate(certificat, 1)) {
                                    String date = player.getItemTemplate(certificat, 1).getTxtStat().get(Constant.STATS_DATE);
                                    long timeStamp = Long.parseLong(date.split("#")[3]);
                                    if (System.currentTimeMillis() - timeStamp <= 86400000) {
                                        ok = false;
                                    }
                                }
                                break;

                            case 1419: // se renseigner avec un dopeul
                                if (dopeuls.containsKey((int) mapActuel.getId())) {
                                    IDmob = dopeuls.get((int) mapActuel.getId()).first;
                                } else
                                    break;

                                certificat = Constant.getCertificatByDopeuls(IDmob);
                                if (certificat == -1)
                                    break;

                                if (player.hasItemTemplate(certificat, 1)) {
                                    String date = player.getItemTemplate(certificat, 1).getTxtStat().get(Constant.STATS_DATE);
                                    long timeStamp = System.currentTimeMillis();
                                    try {
                                        timeStamp = Long.parseLong(date.split("#")[3]);
                                    } catch (Exception ignored) {

                                        World.world.logger.error("!A REPPORTER! Erreur de date avec l'item : "
                                                 + player.getItemTemplate(certificat, 1).getGuid()
                                                 + ".");
                                        return "";
                                    }
                                    if (System.currentTimeMillis() - timeStamp <= 86400000) {
                                        ok = false;
                                    }
                                }
                                break;

                            case 6772: // Combattre chaque dopeul
                                if (!player.getQuestPerso().isEmpty()) {
                                    for (QuestPlayer QP : player.getQuestPerso().values()) {
                                        if (QP.getQuest().getId() == 470) {
                                            ok = false;
                                        }
                                    }
                                }
                                break;

                            case 3627: // Donner les objets, mapid 10437
                                if (!player.getQuestPerso().isEmpty()) {
                                    for (QuestPlayer QP : player.getQuestPerso().values()) {
                                        if (QP.getQuest().getId() == 232) {
                                            ok = false;
                                        }
                                    }
                                    ok = !ok;
                                } else {
                                    ok = false;
                                }
                                break;

                            case 6701: // Si on a déjà le trousseau de clef
                                if (player.hasItemTemplate(10207, 1))
                                    ok = false;
                                else if (Dopeul.hasOneDoplon(player) == -1)
                                    ok = false;
                                break;

                            case 6699: // Apprendre le sort de sa classe
                                GameMap curMap = player.getCurMap();
                                int idMap = World.world.getTempleByClasse(player.getClasse());
                                if (curMap.getId() == (short) idMap) // Si on est dans le temple de notre classe
                                {
                                    // si on a le doplon de classe
                                    ok = player.hasItemTemplate(Dopeul.getDoplonByClasse(player.getClasse()), 1) && !player.hasSpell(Constant.getSpecialSpellByClasse(player.getClasse()));
                                } else
                                    ok = false;
                                break; // Faire sur l'action id
                            case 6599://Oublier un sort

                                break;
                            case 7326: // Reset caractéristique
                                GameMap curMap2 = player.getCurMap();
                                int idMap2 = World.world.getTempleByClasse(player.getClasse());
                                if (curMap2.getId() == (short) idMap2) // Si on est dans le temple de notre classe
                                {
                                    if (!player.hasItemTemplate(Dopeul.getDoplonByClasse(player.getClasse()), 1))
                                        ok = false; // si on a le doplon de classe
                                    if (player.hasItemTemplate(10601, 1))
                                        ok = false; // Si on a le certificat de restat
                                } else
                                    ok = false;
                                break; // Faire sur l'action id
                        }
                    }

                    if (ok) {
                        if (!first)
                            _str += ";";
                        _str += answer.getId();
                        first = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _str;
    }

    public String parse(Player player) {
        if (!this.condition.equals("")) {
            if (!ConditionParser.validConditions(player, this.condition)) {
                if (this.falseQuestion.contains("|")) {
                    return World.world.getNPCQuestion(Integer.parseInt(this.falseQuestion.split("|")[0])).parse(player);
                } else {
                    return World.world.getNPCQuestion(Integer.parseInt(this.falseQuestion)).parse(player);
                }
            }
        }

        String str = String.valueOf(this.id);

        if (!this.args.equals("")) str += ";" + parseArgs(this.args, player);
        if (!this.answers.equals("")) {
            String arg = this.conditionsReponse(player);
            if (!arg.isEmpty()) str += "|" + arg;
        }

        if (player.getItemTemplate(10207) != null) {
            for (String i : player.getItemTemplate(10207).getTxtStat().values().toString().split(",")) {
                GameMap map = player.getCurMap();
                if (map == null) continue;

                Npc npc = map.getNpc((Integer) player.getExchangeAction().getValue());
                if (npc == null) continue;

                NpcTemplate template = npc.getTemplate();
                if (template == null) continue;

                if (Dopeul.parseConditionTrousseau(i.replace(" ", ""), template.getId(), map.getId())) {
                    for (String rep : this.getAnwsers().split(";")) {
                        if(rep.isEmpty()) continue;
                        NpcAnswer answer = World.world.getNpcAnswer(Integer.parseInt(rep));
                        if (answer == null) continue;

                        for (Action action : answer.getActions()) {
                            if (action.getId() == 15) {
                                str += (str.contains("|") ? ";6605" : "|6605");
                                break;
                            }
                            if (action.getId() == 16) {
                                str += (str.contains("|") ? ";6604" : "|6604");
                                break;
                            }
                        }

                        if(str.contains("6604") || str.contains("6605")) break;
                    }
                }
            }
        }

        return str;
    }

    private String parseArgs(String args, Player player) {
        String arg = args;
        arg = arg.replace("[name]", player.getStringVar("name"));
        arg = arg.replace("[bankCost]", player.getStringVar("bankCost"));
        arg = arg.replace("[points]", player.getStringVar("points"));
        arg = arg.replace("[pointsVote]", player.getStringVar("pointsVote"));
        arg = arg.replace("[nbrOnline]", player.getStringVar("nbrOnline"));
        arg = arg.replace("[align]", player.getStringVar("align"));
        return arg;
    }
}