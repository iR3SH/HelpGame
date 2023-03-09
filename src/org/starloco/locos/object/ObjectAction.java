package org.starloco.locos.object;

import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.ConditionParser;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.Database;
import org.starloco.locos.dynamic.Noel;
import org.starloco.locos.entity.pet.PetEntry;
import org.starloco.locos.entity.Prism;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.job.JobStat;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.object.entity.Fragment;
import org.starloco.locos.object.entity.SoulStone;
import org.starloco.locos.other.Action;
import org.starloco.locos.area.map.entity.Animation;
import org.starloco.locos.area.map.entity.House;

public class ObjectAction {

    private String type;
    private String args;
    private String cond;
    private boolean send = true;

    public ObjectAction(String type, String args, String cond) {
        this.type = type;
        this.args = args;
        this.cond = cond;
    }

    public void apply(Player player0, Player target, final int objet, int cellid) {
        if (player0 == null || !player0.isOnline() || player0.getDoAction() || player0.getGameClient() == null)
            return;
        if (!this.cond.equalsIgnoreCase("") && !this.cond.equalsIgnoreCase("-1") && !ConditionParser.validConditions(player0, this.cond)) {
            SocketManager.GAME_SEND_Im_PACKET(player0, "119");
            return;
        }
        
        final GameObject gameObject = World.world.getGameObject(objet);
        
        Player player = target != null ? target : player0;
        
        if (gameObject == null) {
        	SocketManager.GAME_SEND_MESSAGE(player, "Error object null. Merci de pr�venir un administrateur est d'indiquer le message.");
        	return;
        }
        
        if (player0.getLevel() < gameObject.getTemplate().getLevel()) {
            SocketManager.GAME_SEND_Im_PACKET(player0, "119");
            return;
        }



        boolean sureIsOk = false, isOk = true;
        int turn = 0;
        String arg = "";
        try {
            for (String type : this.type.split(";")) {
                String[] split = args.split("\\|", 2);
                if (!this.args.isEmpty() && split.length > turn)
                    arg = split[turn];

                switch (Integer.parseInt(type)) {
                    case -1:
                        if (player0.getFight() != null) return;
                        isOk = true;
                        send = false;
                        break;

                    case 0://T�l�portation.
                        if (player0.getFight() != null) return;
                        short mapId = Short.parseShort(arg.split(",", 2)[0]);
                        int cellId = Integer.parseInt(arg.split(",", 2)[1]);
                        if (!player.isInPrison() && !player.cantTP())
                            player.teleport(mapId, cellId);
                        else if (player.getCurCell().getId() == 268)
                            player.teleport(mapId, cellId);
                        break;

                    case 1://T�l�portation au point de sauvegarde.
                        if (player0.getFight() != null) return;
                        if (!player.isInPrison() && !player.cantTP())
                            player.warpToSavePos();
                        break;

                    case 2://Don de Kamas.
                        if (player0.getFight() != null) return;
                        int count = Integer.parseInt(arg);
                        long curKamas = player.getKamas();
                        long newKamas = curKamas + count;
                        if (newKamas < 0)
                            newKamas = 0;
                        player.setKamas(newKamas);
                        if (player.isOnline())
                            SocketManager.GAME_SEND_STATS_PACKET(player);
                        break;

                    case 3://Don de vie.
                        if(this.type.split(";").length > 1 && player.getFight() != null) return;
                        boolean isOk1 = true,
                                isOk2 = true;
                        for (String arg0 : arg.split(",")) {
                            int val, statId1;
                            if (arg.contains(";")) {
                                statId1 = Integer.parseInt(arg.split(";")[0]);
                                val = gameObject.getRandomValue(gameObject.parseStatsString(), Integer.parseInt(arg.split(";")[0]));
                            } else {
                                statId1 = Integer.parseInt(arg0);
                                val = gameObject.getRandomValue(gameObject.parseStatsString(), Integer.parseInt(arg0));
                            }
                            switch (statId1) {
                                case 110://Vie.
                                    if (player.getCurPdv() == player.getMaxPdv()) {
                                        isOk1 = false;
                                        continue;
                                    }
                                    if (player.getCurPdv() + val > player.getMaxPdv())
                                        val = player.getMaxPdv() - player.getCurPdv();
                                    player.setPdv(player.getCurPdv() + val);
                                    if(player.getFight() != null)
                                        player.getFight().getFighterByPerso(player).setPdv(player.getCurPdv());
                                    SocketManager.GAME_SEND_STATS_PACKET(player);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "01;" + val);
                                    sureIsOk = true;
                                    break;
                                case 139://Energie.
                                    if (player.getEnergy() == 10000) {
                                        isOk2 = false;
                                        continue;
                                    }
                                    if (player.getEnergy() + val > 10000)
                                        val = 10000 - player.getEnergy();
                                    player.setEnergy(player.getEnergy() + val);
                                    SocketManager.GAME_SEND_STATS_PACKET(player);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "07;" + val);
                                    sureIsOk = true;
                                    break;
                                case 605://Exp�rience.
                                    player.addXp(val);
                                    SocketManager.GAME_SEND_STATS_PACKET(player);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "08;" + val);
                                    break;
                                case 614://Exp�rience m�tier.
                                    JobStat job = player.getMetierByID(Integer.parseInt(arg0.split(";")[1]));
                                    if (job == null) {
                                        isOk1 = false;
                                        isOk2 = false;
                                        continue;
                                    }
                                    job.addXp(player, val);
                                    SocketManager.GAME_SEND_Im_PACKET(player, "017;" + val + "~" + Integer.parseInt(arg0.split(";")[1]));
                                    sureIsOk = true;
                                    break;
                            }
                        }
                        if (arg.split(",").length == 1)
                            if (!isOk1 || !isOk2)
                                isOk = false;
                            else if (!isOk1 && !isOk2)
                                isOk = false;
                        send = false;
                        break;

                    case 4://Don de Stats.
                        if (player0.getFight() != null) return;
                        for (String arg0 : arg.split(",")) {
                            int statId = Integer.parseInt(arg0.split(";")[0]);
                            int val = Integer.parseInt(arg0.split(";")[1]);
                            switch (statId) {
                                case 1://Vitalit�.
                                    for (int i = 0; i < val; i++) {
                                        player.boostStat(11, false);
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_VITA, 1);
                                    }
                                    break;
                                case 2://Sagesse.
                                    for (int i = 0; i < val; i++) {
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_SAGE, 1);
                                        player.boostStat(12, false);
                                    }
                                    break;
                                case 3://Force.
                                    for (int i = 0; i < val; i++) {
                                        player.boostStat(10, false);
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_FORC, 1);
                                    }
                                    break;
                                case 4://Intelligence.
                                    for (int i = 0; i < val; i++) {
                                        player.boostStat(15, false);
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_INTE, 1);
                                    }
                                    break;
                                case 5://Chance.
                                    for (int i = 0; i < val; i++) {
                                        player.boostStat(13, false);
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_CHAN, 1);
                                    }
                                    break;
                                case 6://Agilit�.
                                    for (int i = 0; i < val; i++) {
                                        player.boostStat(14, false);
                                        player.getStatsParcho().addOneStat(Constant.STATS_ADD_AGIL, 1);
                                    }
                                    break;
                                case 7://Point de Sort.
                                    player.set_spellPts(player.get_spellPts()
                                            + val);
                                    break;
                            }
                        }
                        sureIsOk = true;
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                        break;

                    case 5://F�e d'artifice.
                        if (player0.getFight() != null) return;
                        int id0 = Integer.parseInt(arg);
                        Animation anim = World.world.getAnimation(id0);
                        if (player.getFight() != null)
                            return;
                        player.changeOrientation(1);
                        SocketManager.GAME_SEND_GA_PACKET_TO_MAP(player.getCurMap(), "0", 228, player.getId() + ";" + cellid + "," + Animation.PrepareToGA(anim), "");
                        break;

                    case 6://Apprendre un sort.
                        if (player0.getFight() != null) return;
                        id0 = Integer.parseInt(arg);
                        if (World.world.getSort(id0) == null)
                            return;
                        if (!player.learnSpell(id0, 1, true, true, true))
                            return;
                        send = false;
                        break;

                    case 7://D�sapprendre un sort.
                        if (player0.getFight() != null) return;
                        id0 = Integer.parseInt(arg);
                        int oldLevel = player.getSortStatBySortIfHas(id0).getLevel();
                        if (player.getSortStatBySortIfHas(id0) == null)
                            return;
                        if (oldLevel <= 1)
                            return;
                        player.unlearnSpell(player, id0, 1, oldLevel, true, true);
                        break;

                    case 8://D�sapprendre un sort � un percepteur.
                        if (player0.getFight() != null) return;
                        //TODO
                        isOk = false;
                        send = false;
                        break;

                    case 9://Oubli� un m�tier.
                        if (player0.getFight() != null) return;
                        int job = Integer.parseInt(arg);
                        JobStat jobStats = player.getMetierByID(job);

                        if (jobStats == null) {
                            player.send("Im149" + job);
                            return;
                        }

                        player.unlearnJob(jobStats.getId());
                        SocketManager.GAME_SEND_STATS_PACKET(player);
                        Database.getStatics().getPlayerData().update(player);
                        player.send("JR" + job);
                        break;

                    case 10://EPO.
                        if (player0.getFight() != null) return;
                        GameObject pets = player.getObjetByPos(Constant.ITEM_POS_FAMILIER);
                        if (pets == null)
                            return;
                        PetEntry MyPets = World.world.getPetsEntry(pets.getGuid());
                        if (MyPets == null)
                            return;
                        if (gameObject.getTemplate().getConditions().contains(pets.getTemplate().getId() + ""))
                            MyPets.giveEpo(player);
                        break;

                    case 11://Chang� de Sexe.
                        if (player0.getFight() != null) return;
                        if (player.getSexe() == 0)
                            player.setSexe(1);
                        else
                            player.setSexe(0);

                        SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                        Database.getStatics().getPlayerData().updateInfos(player);
                        break;

                    case 12://Chang� de nom.
                        if (player0.getFight() != null) return;
                        player.setChangeName(true);
                        isOk = false;
                        send = false;
                        break;

                    case 13://Apprendre une �mote.
                        if (player0.getFight() != null) return;
                        int emote = Integer.parseInt(arg);

                        if (player.getEmotes().contains(emote)) {
                            SocketManager.GAME_SEND_MESSAGE(player, "Tu connais d�jà cet aptitude !");
                            return;
                        }

                        player.addStaticEmote(emote);
                        break;

                    case 14://Apprendre un m�tier.
                        if (player0.getFight() != null) return;
                        job = Integer.parseInt(arg);
                        if (World.world.getMetier(job) == null)
                            return;
                        if (player.getMetierByID(job) != null)//M�tier d�j� appris
                        {
                            SocketManager.GAME_SEND_Im_PACKET(player, "111");
                            return;
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
                            SocketManager.GAME_SEND_Im_PACKET(player, "18;30");
                            return;
                        }
                        if (player.totalJobBasic() > 2) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "19");
                            return;
                        } else {
                            if (job == 27) {
                                if (!player.hasItemTemplate(966, 1))
                                    return;
                                SocketManager.GAME_SEND_Im_PACKET(player, "022;"
                                        + 966 + "~" + 1);
                                player.learnJob(World.world.getMetier(job));
                            } else {
                                player.learnJob(World.world.getMetier(job));
                            }
                        }
                        break;

                    case 15://TP au foyer.
                        if (player0.getFight() != null) return;
                        boolean tp = false;
                        for (House i : World.world.getHouses().values()) {
                            if (i.getOwnerId() == player.getAccount().getId()) {
                                player.teleport((short) i.getHouseMapId(), i.getHouseCellId());
                                tp = true;
                                break;
                            }
                        }
                        if(!tp) {
                            player.send("Im161");
                            return;
                        }
                        break;

                    case 16://Pnj Follower.
                        if (player0.getFight() != null) return;
                        // Petite larve dor�e = 7425
                        player.setMascotte(Integer.parseInt(this.args));
                        break;

                    case 17://B�n�diction.
                        if (player0.getFight() != null) return;
                        player.setBenediction(gameObject.getTemplate().getId());
                        break;

                    case 18://Mal�diction.
                        if (player0.getFight() != null) return;
                        player.setMalediction(gameObject.getTemplate().getId());
                        break;

                    case 19://RolePlay Buff.
                        if (player0.getFight() != null) return;
                        player.setRoleplayBuff(gameObject.getTemplate().getId());
                        break;

                    case 20://Bonbon.
                        if (player0.getFight() != null) return;
                        player.setCandy(gameObject.getTemplate().getId());
                        break;

                    case 21://Poser un objet d'�levage.
                        if (player0.getFight() != null) return;
                        GameMap map0 = player.getCurMap();
                        id0 = gameObject.getTemplate().getId();

                        int resist = gameObject.getResistance(gameObject.parseStatsString());
                        int resistMax = gameObject.getResistanceMax(gameObject.getTemplate().getStrTemplate());
                        if (map0.getMountPark() == null)
                            return;
                        MountPark MP = map0.getMountPark();
                        if (player.get_guild() == null) {
                            SocketManager.GAME_SEND_BN(player);
                            return;
                        }
                        if (!player.getGuildMember().canDo(Constant.G_AMENCLOS)) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "193");
                            return;
                        }
                        if (MP.getCellOfObject().size() == 0
                                || !MP.getCellOfObject().contains(cellid)) {
                            SocketManager.GAME_SEND_BN(player);
                            return;
                        }
                        if (MP.getObject().size() < MP.getMaxObject()) {
                            MP.addObject(cellid, id0, player.getId(), resistMax, resist);
                            SocketManager.SEND_GDO_PUT_OBJECT_MOUNT(map0, cellid
                                    + ";"
                                    + id0
                                    + ";1;"
                                    + resist
                                    + ";"
                                    + resistMax);
                        } else {
                            SocketManager.GAME_SEND_Im_PACKET(player, "1107");
                            return;
                        }
                        break;

                    case 22://Poser un prisme.
                        if (player0.getFight() != null) return;
                        map0 = player.getCurMap();
                        int cellId1 = player.getCurCell().getId();
                        SubArea subArea = map0.getSubArea();
                        Area area = subArea.getArea();
                        int alignement = player.get_align();
                        if (cellId1 <= 0)
                            return;
                        if (alignement == 0 || alignement == 3) {
                            SocketManager.GAME_SEND_MESSAGE(player, "Vous ne possedez pas l'alignement n�cessaire pour poser un prisme.");
                            return;
                        }
                        if (!player.is_showWings()) {
                            SocketManager.GAME_SEND_MESSAGE(player, "Vos ailes doivent être activer afin de poser un prisme.");
                            return;
                        }
                        if (map0.noPrism || (subArea != null && (subArea.getId() == 9 || subArea.getId() == 95)) || map0.haveMobFix() || map0.getMobGroups().isEmpty()) {
                            SocketManager.GAME_SEND_MESSAGE(player, "Vous ne pouvez pas poser de prisme sur cette map.");
                            return;
                        }
                        if (subArea.getAlignement() != 0 || !subArea.getConquistable()) {
                            SocketManager.GAME_SEND_MESSAGE(player, "L'alignement de cette sous-zone est en conquête ou n'est pas neutre !");
                            return;
                        }
                        Prism Prisme = new Prism(World.world.getNextIDPrisme(), alignement, 1, map0.getId(), cellId1, player.get_honor(), -1);
                        subArea.setAlignement(alignement);
                        subArea.setPrismId(Prisme.getId());
                        for (Player z : World.world.getOnlinePlayers()) {
                            if (z == null)
                                continue;
                            if (z.get_align() == 0) {
                                SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(z, subArea.getId() + "|" + alignement + "|1");
                                if (area.getAlignement() == 0)
                                    SocketManager.GAME_SEND_aM_ALIGN_PACKET_TO_AREA(z, area.getId() + "|" + alignement);
                                continue;
                            }
                            SocketManager.GAME_SEND_am_ALIGN_PACKET_TO_SUBAREA(z, subArea.getId()
                                    + "|" + alignement + "|0");
                            if (area.getAlignement() == 0)
                                SocketManager.GAME_SEND_aM_ALIGN_PACKET_TO_AREA(z, area.getId()
                                        + "|" + alignement);
                        }
                        if (area.getAlignement() == 0) {
                            area.setPrismId(Prisme.getId());
                            area.setAlignement(alignement);
                            Prisme.setConquestArea(area.getId());
                        }
                        World.world.addPrisme(Prisme);
                        Database.getDynamics().getPrismData().add(Prisme);
                        player.getCurMap().getSubArea().setAlignement(player.get_align());
                        Database.getDynamics().getSubAreaData().update(player.getCurMap().getSubArea());
                        SocketManager.GAME_SEND_PRISME_TO_MAP(map0, Prisme);
                        break;

                    case 23://Rappel Prismatique.
                        if (player0.getFight() != null) return;
                        int dist = 99999, alea;
                        mapId = 0;
                        cellId = 0;
                        for (Prism i : World.world.AllPrisme()) {
                            if (i.getAlignement() != player.get_align())
                                continue;
                            alea = (World.world.getMap(i.getMap()).getX() - player.getCurMap().getX())
                                    * (World.world.getMap(i.getMap()).getX() - player.getCurMap().getX())
                                    + (World.world.getMap(i.getMap()).getY() - player.getCurMap().getY())
                                    * (World.world.getMap(i.getMap()).getY() - player.getCurMap().getY());
                            if (alea < dist) {
                                dist = alea;
                                mapId = i.getMap();
                                cellId = i.getCell();
                            }
                        }
                        if (mapId != 0)
                            player.teleport(mapId, cellId);
                        break;

                    case 24://TP Village align�.
                        if (player0.getFight() != null) return;
                        mapId = (short) Integer.parseInt(arg.split(",")[0]);
                        cellId = Integer.parseInt(arg.split(",")[1]);
                        if (World.world.getMap(mapId).getSubArea().getAlignement() == player.get_align())
                            player.teleport(mapId, cellId);
                        break;

                    case 25://Spawn groupe.
                        if (player0.getFight() != null) return;
                        boolean inArena = arg.split(";")[0].equals("true");
                        String groupData = "";
                        if (inArena && !SoulStone.isInArenaMap(player.getCurMap().getId()))
                            return;
                        if (arg.split(";")[1].equals("1")) {
                            groupData = arg.split(";")[2];
                        } else {
                            SoulStone soulStone = (SoulStone) gameObject;
                            groupData = soulStone.parseGroupData();
                        }
                        String condition = "MiS = " + player.getId();
                        player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), groupData, condition);
                        break;

                    case 26://Ajout d'objet.
                        if (player0.getFight() != null) return;
                        for (String i : arg.split(";")) {
                            final GameObject obj = World.world.getObjTemplate(Integer.parseInt(i.split(",")[0])).createNewItem(Integer.parseInt(i.split(",")[1]), false);
                            if (player.addObjet(obj, true))
                                World.world.addGameObject(obj, true);
                        }
                        SocketManager.GAME_SEND_Ow_PACKET(player);
                        break;

                    case 27://Ajout de titre.
                        if (player0.getFight() != null) return;
                        player.setAllTitle(arg);
                        break;

                    case 28://Ajout de zaap.
                        if (player0.getFight() != null) return;
                        player.verifAndAddZaap((short) Integer.parseInt(arg));
                        break;

                    case 29://Panel d'oubli de sort.
                        if (player0.getFight() != null) return;
                        player.setExchangeAction(new ExchangeAction<>(ExchangeAction.FORGETTING_SPELL, 0));
                        SocketManager.GAME_SEND_FORGETSPELL_INTERFACE('+', player);
                        break;

                    case 31://Cadeau bworker.
                        if (player0.getFight() != null) return;
                        new Action(511, "", "", null).apply(player, null, objet, -1);
                        break;

                    case 32://G�oposition traque.
                        if (player0.getFight() != null) return;
                        String traque = gameObject.getTraquedName();

                        if (traque == null)
                            break;

                        Player cible = World.world.getPlayerByName(traque);

                        if (cible == null)
                            break;

                        if (!cible.isOnline()) {
                            SocketManager.GAME_SEND_Im_PACKET(player, "1198");
                            break;
                        }

                        SocketManager.GAME_SEND_FLAG_PACKET(player, cible);
                        break;

                    case 33://Ajout de points boutique.
                        if (player0.getFight() != null) return;
                        player.getAccount().setPoints(player.getAccount().getPoints() + Integer.parseInt(arg));
                        break;

                    case 34://Fm cac
                        GameObject weapon = player.getObjetByPos(Constant.ITEM_POS_ARME);

                        if(weapon == null) {
                            player.sendMessage("Vous ne portez pas de corps-à-corps.");
                            isOk = false;
                            send = false;
                            return;
                        }

                        boolean containNeutre = false;

                        for(SpellEffect effect : weapon.getEffects())
                            if(effect.getEffectID() == 100 || effect.getEffectID() == 95)
                                containNeutre = true;

                        if(containNeutre) {
                            for(int i = 0; i < weapon.getEffects().size(); i++) {
                                if(weapon.getEffects().get(i).getEffectID() == 100) {
                                    switch(this.args.toUpperCase()) {
                                        case "EAU": weapon.getEffects().get(i).setEffectID(96); break;
                                        case "TERRE": weapon.getEffects().get(i).setEffectID(97); break;
                                        case "AIR": weapon.getEffects().get(i).setEffectID(98); break;
                                        case "FEU": weapon.getEffects().get(i).setEffectID(99); break;
                                    }
                                }
                                if(weapon.getEffects().get(i).getEffectID() == 95) {
                                    switch(this.args.toUpperCase()) {
                                        case "EAU": weapon.getEffects().get(i).setEffectID(91); break;
                                        case "TERRE": weapon.getEffects().get(i).setEffectID(92); break;
                                        case "AIR": weapon.getEffects().get(i).setEffectID(93); break;
                                        case "FEU": weapon.getEffects().get(i).setEffectID(94); break;
                                    }
                                }
                            }

                            SocketManager.GAME_SEND_STATS_PACKET(player);
                            SocketManager.GAME_SEND_UPDATE_ITEM(player, weapon);
                            weapon.setModification();
                            player.sendMessage("Votre corps-corps a �t� modifi� avec succ�s.");
                        } else {
                            player.sendMessage("Votre corps-�-corps ne contient aucun d�g�t de type neutre.");
                            isOk = false;
                            send = false;
                        }
                        break;
                    // Mimibiote
                    case 35:
                    	if(player.getFight() != null) return;
                    	SocketManager.GAME_SEND_UI_MIMIBIOTE(player);
                    	return;
                    // Cameleon dragodinde
                    case 36:
                    	if(player.getFight() != null) return;
                    	final Mount mount = player.getMount();
                    	if(mount == null) return;
                    	if(mount.getCapacitys().contains(9)) 
                    	{
                    		player.sendInformationMessage("Votre monture est d�j� cam�l�onne");
                    		return;
                    	}
                    	mount.getCapacitys().add(9);
                    	SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(player.getCurMap(), player.getId());
                    	SocketManager.GAME_SEND_ADD_PLAYER_TO_MAP(player.getCurMap(), player);
                    	SocketManager.GAME_SEND_Im_PACKET(player, "0105"); // Votre monture appr�cie le repas.
                    	isOk = true;
                    	break;
                        
                    // By Coding Mestre -  [FIX] Professions runes are now properly working Close #35
                    case 90: // profession rune
                        StringBuilder sb = new StringBuilder(args);
                        player.getMetiers()
                                .values()
                                .stream()
                                .filter(js -> js.getTemplate().getId() == Integer.parseInt(sb.toString()))
                                .findFirst()
                                .ifPresent(js -> {
                                    String signOfPacket = js.flipPublicMode();
                                    SocketManager.SEND_Ej_LIVRE(player, signOfPacket.concat(sb.toString()));
                                });
                        break;

                        
                }
                turn++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (type.equalsIgnoreCase("90")) return; // make sure the profession rune is not removed - Coding Mestre
        boolean effect = this.haveEffect(gameObject.getTemplate().getId(), gameObject, player);
        if (effect)
            isOk = true;
            send = true;
        if (isOk)
            effect = true;
        if (this.type.split(";").length > 1)
            isOk = true;
        if (objet != -1) {
            if (send)
                SocketManager.GAME_SEND_Im_PACKET(player, "022;" + 1 + "~" + gameObject.getTemplate().getId());
            if (sureIsOk || (isOk && effect && gameObject.getTemplate().getId() != 7799)) {
            	if(gameObject.getPosition() != Constant.ITEM_POS_NO_EQUIPED)
            		player0.unEquipItem(gameObject.getPosition());
                player0.removeItem(objet, 1, true, true);
                
            }
        }
    }

    private boolean haveEffect(int id, GameObject gameObject, Player player) {
        if (player.getFight() != null) return true;
        switch (id) {
            case 8378://Fragment magique.
                for (World.Couple<Integer, Integer> couple : ((Fragment) gameObject).getRunes()) {
                    ObjectTemplate objectTemplate = World.world.getObjTemplate(couple.first);

                    if (objectTemplate == null)
                        continue;

                    GameObject newGameObject = objectTemplate.createNewItem(couple.second, true);

                    if (newGameObject == null)
                        continue;
                    if(player.addObjet(newGameObject, true))
                    	World.world.addGameObject(newGameObject, true);
                }
                send = true;
                return true;
            case 7799://Le Saut Sifflard
                player.toogleOnMount();
                send = false;
                return false;

            case 10832://Craqueloroche
                player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), "483,1,1000", "MiS="
                        + player.getId());
                return true;

            case 10664://Abragland
                player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), "47,1,1000", "MiS="
                        + player.getId());
                return true;

            case 10665://Coffre de Jorbak
                player.setCandy(10688);
                return true;

            case 10670://Parchemin de persimol
                player.setBenediction(10682);
                return true;

            case 8435://Ballon Rouge Magique
                SocketManager.sendPacketToMap(player.getCurMap(), "GA;208;"
                        + player.getId() + ";" + player.getCurCell().getId()
                        + ",2906,11,8,1");
                return true;

            case 8624://Ballon Bleu Magique
                SocketManager.sendPacketToMap(player.getCurMap(), "GA;208;"
                        + player.getId() + ";" + player.getCurCell().getId()
                        + ",2907,11,8,1");
                return true;

            case 8625://Ballon Vert Magique
                SocketManager.sendPacketToMap(player.getCurMap(), "GA;208;"
                        + player.getId() + ";" + player.getCurCell().getId()
                        + ",2908,11,8,1");
                return true;

            case 8430://Ballon Jaune Magique
                SocketManager.sendPacketToMap(player.getCurMap(), "GA;208;"
                        + player.getId() + ";" + player.getCurCell().getId()
                        + ",2909,11,8,1");
                return true;

            case 8621://Cawotte Maudite
                player.setGfxId(1109);
                SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                return true;

            case 8626://Nisitik Miditik
                player.setGfxId(1046);
                SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                return true;

            case 10833://Chapain
                player.setGfxId(9001);
                SocketManager.GAME_SEND_ALTER_GM_PACKET(player.getCurMap(), player);
                return true;

            case 10839://Monstre Pain
                player.getCurMap().spawnNewGroup(true, player.getCurCell().getId(), "2787,1,1000", "MiS="
                        + player.getId());
                return true;

            case 8335://Cadeau 1
                Noel.getRandomObjectOne(player);
                return true;
            case 8336://Cadeau 2
                Noel.getRandomObjectTwo(player);
                return true;
            case 8337://Cadeau 3
                Noel.getRandomObjectTree(player);
                return true;
            case 8339://Cadeau 4
                Noel.getRandomObjectFour(player);
                return true;
            case 8340://Cadeau 5
                Noel.getRandomObjectFive(player);
                return true;
            case 10912://Cadeau nowel 1
                return false;
            case 10913://Cadeau nowel 2
                return false;
            case 10914://Cadeau nowel 3
                return false;
            case 12839://Gemme Spirituel emballée
                int templateid =  Constant.getRandomGemmesSpritiuels();
                GameObject obj = World.world.getObjTemplate(templateid).createNewItem(1, false);
                if (player.addObjet(obj, true))
                    World.world.addGameObject(obj,true);
                SocketManager.GAME_SEND_Ow_PACKET(player);
                SocketManager.GAME_SEND_Im_PACKET(player, "021;" + 1 + "~" + templateid);
                return true;
        }
        return false;
    }
}
