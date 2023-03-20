package org.starloco.locos.common;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.other.Guild;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

public class Formulas {

    public final static Random random = new Random();
    
    
    private final static short[] order = new short[] {
    		
    		Constant.STATS_ADD_PA,
    		Constant.STATS_ADD_PM,
    		Constant.STATS_ADD_PO,
    		Constant.STATS_ADD_VITA,
    		Constant.STATS_ADD_AGIL,
    		Constant.STATS_ADD_CHAN,
    		Constant.STATS_ADD_FORC,
    		Constant.STATS_ADD_INTE,
    		Constant.STATS_ADD_SAGE,
    		Constant.STATS_ADD_CC,
    		Constant.STATS_ADD_DOMA,
    		Constant.STATS_ADD_PERDOM,
    		Constant.STATS_MULTIPLY_DOMMAGE,
    		Constant.STATS_ADD_INIT,
    		
    		
    };

    public static int countCell(int i) {
        if (i > 64)
            i = 64;
        return 2 * (i) * (i + 1);
    }

    public static int getRandomValue(int minValue, int maxValue) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("Min " + minValue + " greater than max " + maxValue);
        }
        return (int) ( (long) minValue + Math.random() * ((long)maxValue - minValue + 1));
    }

    public static int getMinJet(String jet) {
        int num = 0;
        try {
            int des = Integer.parseInt(jet.split("d")[0]);
            int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
            num = des + add;
            return num;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getMaxJet(String jet) {
        int num = 0;
        try {
            int des = Integer.parseInt(jet.split("d")[0]);
            int faces = Integer.parseInt(jet.split("d")[1].split("\\+")[0]);
            int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
            for (int a = 0; a < des; a++) {
                num += faces;
            }
            num += add;
            return num;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getRandomJet(String jet)//1d5+6
    {
        try {
            int num = 0;
            int des = Integer.parseInt(jet.split("d")[0]);
            int faces = Integer.parseInt(jet.split("d")[1].split("\\+")[0]);
            int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
            if (faces == 0 && add == 0) {
                num = getRandomValue(0, des);
            } else {
                for (int a = 0; a < des; a++) {
                    num += getRandomValue(1, faces);
                }
            }
            num += add;
            return num;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getRandomJet(String jet, Fighter target, Fighter caster)//1d5+6
    {
        try {
            if(target != null)
                if(target.hasBuff(782))
                    return Formulas.getMaxJet(jet);
            if(caster != null)
                if(caster.hasBuff(781))
                    return Formulas.getMinJet(jet);
            int num = 0, des, faces, add;

            des = Integer.parseInt(jet.split("d")[0]);
            faces = Integer.parseInt(jet.split("d")[1].split("\\+")[0]);
            add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);

            if (faces == 0 && add == 0) {
                num = getRandomValue(0, des);
            } else {
                for (int a = 0; a < des; a++) {
                    num += getRandomValue(1, faces);
                }
            }
            num += add;
            return num;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getMiddleJet(String jet)//1d5+6
    {
        try {
            int num = 0;
            int des = Integer.parseInt(jet.split("d")[0]);
            int faces = Integer.parseInt(jet.split("d")[1].split("\\+")[0]);
            int add = Integer.parseInt(jet.split("d")[1].split("\\+")[1]);
            num += ((1 + faces) / 2) * des;//on calcule moyenne
            num += add;
            return num;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getTacleChance(Fighter fight, Fighter fighter) {
        int agiTacleur = fight.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
        int agiEnemi = fighter.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
        int div = agiTacleur + agiEnemi + 50;
        if (div == 0)
            div = 1;
        int esquive = 300 * (agiTacleur + 25) / div - 100;
        return esquive;
    }

    public static int calculFinalHeal(Player caster, int jet) {
        int statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
        int soins = caster.getTotalStats().getEffect(Constant.STATS_ADD_SOIN);
        if (statC < 0)
            statC = 0;
        return jet * (100 + statC) / 100 + soins;
    }

    public static int calculFinalHealCac(Fighter healer, int rank, boolean isCac) {
        int intel = healer.getTotalStats().getEffect(126);
        int heals = healer.getTotalStats().getEffect(178);
        if (intel < 0)
            intel = 0;
        float adic = 100;
        if (isCac)
            adic = 105;
        return (int) (rank * ((100.00 + intel) / adic) + heals / 2);
    }

    public static int calculXpWinCraft(int lvl, int numCase) {
        if (lvl == 100)
            return 0;
        switch (numCase) {
            case 1:
                if (lvl < 40)
                    return 1;
                return 0;
            case 2:
                if (lvl < 60)
                    return 10;
                return 0;
            case 3:
                if (lvl > 9 && lvl < 80)
                    return 25;
                return 0;
            case 4:
                if (lvl > 19)
                    return 50;
                return 0;
            case 5:
                if (lvl > 39)
                    return 100;
                return 0;
            case 6:
                if (lvl > 59)
                    return 250;
                return 0;
            case 7:
                if (lvl > 79)
                    return 500;
                return 0;
            case 8:
                if (lvl > 99)
                    return 1000;
                return 0;
        }
        return 0;
    }

    public static int calculXpWinFm(int lvl, int poid) {
        if (lvl <= 1) {
            if (poid <= 10)
                return 10;
            else if (poid <= 50)
                return 25;
            else
                return 50;
        }
        if (lvl <= 25) {
            if (poid <= 10)
                return 10;
            else
                return 50;
        } else if (lvl <= 50) {
            if (poid <= 1)
                return 10;
            if (poid <= 10)
                return 25;
            if (poid <= 50)
                return 50;
            else
                return 100;
        } else if (lvl <= 75) {
            if (poid <= 3)
                return 25;
            if (poid <= 10)
                return 50;
            if (poid <= 50)
                return 100;
            else
                return 250;
        } else if (lvl <= 100) {
            if (poid <= 3)
                return 50;
            if (poid <= 10)
                return 100;
            if (poid <= 50)
                return 250;
            else
                return 500;
        } else if (lvl <= 125) {
            if (poid <= 3)
                return 100;
            if (poid <= 10)
                return 250;
            if (poid <= 50)
                return 500;
            else
                return 1000;
        } else if (lvl <= 150) {
            if (poid <= 10)
                return 250;
            else
                return 1000;
        } else if (lvl <= 175) {
            if (poid <= 1)
                return 250;
            if (poid <= 10)
                return 500;
            else
                return 1000;
        } else {
            if (poid <= 1)
                return 500;
            else
                return 1000;
        }
    }

    public static int calculXpLooseCraft(int lvl, int numCase) {
        if (lvl == 100)
            return 0;
        switch (numCase) {
            case 1:
                if (lvl < 40)
                    return 1;
                return 0;
            case 2:
                if (lvl < 60)
                    return 5;
                return 0;
            case 3:
                if (lvl > 9 && lvl < 80)
                    return 12;
                return 0;
            case 4:
                if (lvl > 19)
                    return 25;
                return 0;
            case 5:
                if (lvl > 39)
                    return 50;
                return 0;
            case 6:
                if (lvl > 59)
                    return 125;
                return 0;
            case 7:
                if (lvl > 79)
                    return 250;
                return 0;
            case 8:
                if (lvl > 99)
                    return 500;
                return 0;
        }
        return 0;
    }

    public static int calculHonorWin(ArrayList<Fighter> winner,
                                     ArrayList<Fighter> looser, Fighter F) {
        float totalGradeWin = 0;
        float totalLevelWin = 0;
        float totalGradeLoose = 0;
        float totalLevelLoose = 0;
        boolean Prisme = false;
        int fighters = 0;
        for (Fighter f : winner) {
            if (f.getPersonnage() == null && f.getPrism() == null)
                continue;
            if (f.getPersonnage() != null) {
                totalLevelWin += f.getLvl();
                totalGradeWin += f.getPersonnage().getGrade();
            } else {
                Prisme = true;
                totalLevelWin += (f.getPrism().getLevel() * 15 + 80);
                totalGradeWin += f.getPrism().getLevel();
            }
        }
        for (Fighter f : looser) {
            if (f.getPersonnage() == null && f.getPrism() == null)
                continue;
            if (f.getPersonnage() != null) {
                totalLevelLoose += f.getLvl();
                totalGradeLoose += f.getPersonnage().getGrade();
                fighters++;
            } else {
                Prisme = true;
                totalLevelLoose += (f.getPrism().getLevel() * 15 + 80);
                totalGradeLoose += f.getPrism().getLevel();
            }
        }
        if (!Prisme)
            if (totalLevelWin - totalLevelLoose > 15 * fighters)
                return 0;
        int base = (int) (100 * ((totalGradeLoose * totalLevelLoose) / (totalGradeWin * totalLevelWin)))
                / winner.size();
        if (Prisme && base <= 0)
            return 100;
        if (looser.contains(F))
            base = -base;
        return base * Config.getInstance().rateHonor;
    }

    public static int calculFinalDommage(Fight fight, Fighter caster, Fighter target, int statID, int jet, boolean isHeal, boolean isCaC, int spellid) {
        int value = calculFinalDommagee(fight, caster, target, statID, jet, isHeal, isCaC, spellid);
        return value > 0 ? value : 0;
    }

    public static int calculFinalDommagee(Fight fight, Fighter caster,
                                         Fighter target, int statID, int jet, boolean isHeal, boolean isCaC,
                                         int spellid) {
        // if (target.hasBuff(788) && target.getBuff(788).getValue() == 101)
        float i = 0;//Bonus maitrise
        float j = 100; //Bonus de Classe
        float a = 1;//Calcul
        float num = 0;
        float statC = 0, domC = 0, perdomC = 0, resfT = 0, respT = 0, mulT = 1;
        int multiplier = 0;
        if (!isHeal) {
            domC = caster.getTotalStats().getEffect(Constant.STATS_ADD_DOMA);
            perdomC = caster.getTotalStats().getEffect(Constant.STATS_ADD_PERDOM);
            multiplier = caster.getTotalStats().getEffect(Constant.STATS_MULTIPLY_DOMMAGE);
            if (caster.hasBuff(114))
                mulT = caster.getBuffValue(114);
        } else {
            domC = caster.getTotalStats().getEffect(Constant.STATS_ADD_SOIN);
        }

        switch (statID) {
            case Constant.ELEMENT_NULL://Fixe
                statC = 0;
                resfT = 0;
                respT = 0;
                respT = 0;
                mulT = 1;
                break;
            case Constant.ELEMENT_NEUTRE://neutre
                statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
                resfT = target.getTotalStats().getEffect(Constant.STATS_ADD_R_NEU);
                respT = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_NEU);
                if (caster.getPersonnage() != null)//Si c'est un joueur
                {
                    respT += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_NEU);
                    resfT += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_NEU);
                }
                //on ajoute les dom Physique
                domC += caster.getTotalStats().getEffect(142);
                //Ajout de la resist Physique
                resfT = target.getTotalStats().getEffect(184);
                break;
            case Constant.ELEMENT_TERRE://force
                statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
                resfT = target.getTotalStats().getEffect(Constant.STATS_ADD_R_TER);
                respT = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_TER);
                if (caster.getPersonnage() != null)//Si c'est un joueur
                {
                    respT += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_TER);
                    resfT += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_TER);
                }
                //on ajout les dom Physique
                domC += caster.getTotalStats().getEffect(142);
                //Ajout de la resist Physique
                resfT = target.getTotalStats().getEffect(184);
                break;
            case Constant.ELEMENT_EAU://chance
                statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_CHAN);
                resfT = target.getTotalStats().getEffect(Constant.STATS_ADD_R_EAU);
                respT = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_EAU);
                if (caster.getPersonnage() != null)//Si c'est un joueur
                {
                    respT += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_EAU);
                    resfT += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_EAU);
                }
                //Ajout de la resist Magique
                resfT = target.getTotalStats().getEffect(183);
                break;
            case Constant.ELEMENT_FEU://intell
                statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
                resfT = target.getTotalStats().getEffect(Constant.STATS_ADD_R_FEU);
                respT = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_FEU);
                if (caster.getPersonnage() != null)//Si c'est un joueur
                {
                    respT += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_FEU);
                    resfT += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_FEU);
                }
                //Ajout de la resist Magique
                resfT = target.getTotalStats().getEffect(183);
                break;
            case Constant.ELEMENT_AIR://agilité
                statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
                resfT = target.getTotalStats().getEffect(Constant.STATS_ADD_R_AIR);
                respT = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_AIR);
                if (caster.getPersonnage() != null)//Si c'est un joueur
                {
                    respT += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_AIR);
                    resfT += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_AIR);
                }
                //Ajout de la resist Magique
                resfT = target.getTotalStats().getEffect(183);
                break;
        }
        //On bride la resistance a 50% si c'est un joueur
        if (target.getMob() == null && respT > 50)
            respT = 50;

        if (statC < 0)
            statC = 0;
        if (caster.getPersonnage() != null && isCaC) {
            int ArmeType = caster.getPersonnage().getObjetByPos(1).getTemplate().getType();

            if ((caster.getSpellValueBool(392)) && ArmeType == 2)//ARC
            {
                i = caster.getMaitriseDmg(392);
            }
            if ((caster.getSpellValueBool(390)) && ArmeType == 4)//BATON
            {
                i = caster.getMaitriseDmg(390);
            }
            if ((caster.getSpellValueBool(391)) && ArmeType == 6)//EPEE
            {
                i = caster.getMaitriseDmg(391);
            }
            if ((caster.getSpellValueBool(393)) && ArmeType == 7)//MARTEAUX
            {
                i = caster.getMaitriseDmg(393);
            }
            if ((caster.getSpellValueBool(394)) && ArmeType == 3)//BAGUETTE
            {
                i = caster.getMaitriseDmg(394);
            }
            if ((caster.getSpellValueBool(395)) && ArmeType == 5)//DAGUES
            {
                i = caster.getMaitriseDmg(395);
            }
            if ((caster.getSpellValueBool(396)) && ArmeType == 8)//PELLE
            {
                i = caster.getMaitriseDmg(396);
            }
            if ((caster.getSpellValueBool(397)) && ArmeType == 19)//HACHE
            {
                i = caster.getMaitriseDmg(397);
            }
            a = (((100 + i) / 100) * (j / 100));
        }

        num = a * mulT * (jet * ((100 + statC + perdomC + (multiplier * 100)) / 100))
                + domC;//dégats bruts
        //Poisons
        if (spellid != -1) {
            switch (spellid) {

                case 66:
                    statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
                    num = (jet * ((100 + statC + perdomC + (multiplier * 100)) / 100)) + domC;
                    if (target.hasBuff(105) && spellid != 71) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(105).getValue());
                        int value = (int) num - target.getBuff(105).getValue();
                        return value > 0 ? value : 0 ;
                    }
                    if (target.hasBuff(184) && spellid != 71) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(184).getValue());
                        int value = (int) num - target.getBuff(184).getValue();
                        return value > 0 ? value : 0 ;
                    }
                    return (int) num;

                case 71:
                case 196:
                case 219:
                    statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
                    num = (jet * ((100 + statC + perdomC + (multiplier * 100)) / 100)) + domC;
                    if (target.hasBuff(105) && spellid != 71) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(105).getValue());
                        int value = (int) num - target.getBuff(105).getValue();
                        return value > 0 ? value : 0 ;
                    }
                    if (target.hasBuff(184) && spellid != 71) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(184).getValue());
                        int value = (int) num - target.getBuff(184).getValue();
                        return value > 0 ? value : 0 ;
                    }
                    return (int) num;

                case 181:
                case 200:
                    statC = caster.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
                    num = (jet * ((100 + statC + perdomC + (multiplier * 100)) / 100)) + domC;
                    if (target.hasBuff(105) && spellid != 71) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(105).getValue());
                        int value = (int) num - target.getBuff(105).getValue();
                        return value > 0 ? value : 0 ;
                    }
                    if (target.hasBuff(184) && spellid != 71) {
                        SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(184).getValue());
                        int value = (int) num - target.getBuff(184).getValue();
                        return value > 0 ? value : 0 ;
                    }
                    return (int) num;
            }
        }
        //Renvoie
        if (caster.getId() != target.getId()) {
            int renvoie = target.getTotalStatsLessBuff().getEffect(Constant.STATS_RETDOM);
            if (renvoie > 0 && !isHeal) {
                if (renvoie > num)
                    renvoie = (int) num;
                num -= renvoie;
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 107, "-1", target.getId()
                        + "," + renvoie);
                if (renvoie > caster.getPdv())
                    renvoie = caster.getPdv();
                if (num < 1)
                    num = 0;

                if (caster.getPdv() <= renvoie) {
                    caster.removePdv(caster, renvoie);
                    fight.onFighterDie(caster, caster);
                } else
                    caster.removePdv(caster, renvoie);

                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
                        + "", caster.getId() + ",-" + renvoie);
            }
        }
        int reduc = (int) ((num / (float) 100) * respT);//Reduc %resis
        if (!isHeal)
            num -= reduc;
        int armor = getArmorResist(target, statID);
        if (!isHeal)
            num -= armor;
        if (!isHeal)
            if (armor > 0)
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId()
                        + "", target.getId() + "," + armor);
        if (!isHeal)
            num -= resfT;//resis fixe
        //dégats finaux
        if (num < 1)
            num = 0;
        //Perte de 10% des PDV MAX par points de degat 10 PDV = 1PDV max en moins
        if (target.getPersonnage() != null)
            target.removePdvMax((int) Math.floor(num / 10));
        // Début Formule pour les MOBs
        if (caster.getPersonnage() == null && !caster.isCollector()) {
            if (caster.getMob().getTemplate().getId() == 116)//Sacrifié Dommage = PDV*2
            {
                return (int) ((num / 25) * caster.getPdvMax());
            } else {
                int niveauMob = caster.getLvl();
                double CalculCoef = ((niveauMob * 0.5) / 100);
                int Multiplicateur = (int) Math.ceil(CalculCoef);
                return (int) num * Multiplicateur;
            }
        }
        return (int) num;
    }

    public static int calculZaapCost(GameMap map1, GameMap map2) {
        return 10 * (Math.abs(map2.getX() - map1.getX())
                + Math.abs(map2.getY() - map1.getY()) - 1);
    }

    private static int getArmorResist(Fighter target, int statID) {
        int armor = 0;
        for (SpellEffect SE : target.getBuffsByEffectID(265)) {
            Fighter fighter;

            switch (SE.getSpell()) {
                case 1://Armure incandescente
                    //Si pas element feu, on ignore l'armure
                    if (statID != Constant.ELEMENT_FEU)
                        continue;
                    //Les stats du féca sont prises en compte
                    fighter = SE.getCaster();
                    break;
                case 6://Armure Terrestre
                    //Si pas element terre/neutre, on ignore l'armure
                    if (statID != Constant.ELEMENT_TERRE
                            && statID != Constant.ELEMENT_NEUTRE)
                        continue;
                    //Les stats du féca sont prises en compte
                    fighter = SE.getCaster();
                    break;
                case 14://Armure Venteuse
                    //Si pas element air, on ignore l'armure
                    if (statID != Constant.ELEMENT_AIR)
                        continue;
                    //Les stats du féca sont prises en compte
                    fighter = SE.getCaster();
                    break;
                case 18://Armure aqueuse
                    //Si pas element eau, on ignore l'armure
                    if (statID != Constant.ELEMENT_EAU)
                        continue;
                    //Les stats du féca sont prises en compte
                    fighter = SE.getCaster();
                    break;

                default://Dans les autres cas on prend les stats de la cible et on ignore l'element de l'attaque
                    fighter = target;
                    break;
            }
            int intell = fighter.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
            int carac = 0;
            switch (statID) {
                case Constant.ELEMENT_AIR:
                    carac = fighter.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
                    break;
                case Constant.ELEMENT_FEU:
                    carac = fighter.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
                    break;
                case Constant.ELEMENT_EAU:
                    carac = fighter.getTotalStats().getEffect(Constant.STATS_ADD_CHAN);
                    break;
                case Constant.ELEMENT_NEUTRE:
                case Constant.ELEMENT_TERRE:
                    carac = fighter.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
                    break;
            }
            int value = SE.getValue();
            int a = value * (100 + intell / 2 + carac / 2)
                    / 100;
            armor += a;
        }
        for (SpellEffect SE : target.getBuffsByEffectID(105)) {
            int intell = target.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
            int carac = 0;
            switch (statID) {
                case Constant.ELEMENT_AIR:
                    carac = target.getTotalStats().getEffect(Constant.STATS_ADD_AGIL);
                    break;
                case Constant.ELEMENT_FEU:
                    carac = target.getTotalStats().getEffect(Constant.STATS_ADD_INTE);
                    break;
                case Constant.ELEMENT_EAU:
                    carac = target.getTotalStats().getEffect(Constant.STATS_ADD_CHAN);
                    break;
                case Constant.ELEMENT_NEUTRE:
                case Constant.ELEMENT_TERRE:
                    carac = target.getTotalStats().getEffect(Constant.STATS_ADD_FORC);
                    break;
            }
            int value = SE.getValue();
            int a = value * (100 + intell / 2 + carac / 2)
                    / 100;
            armor += a;
        }
        return armor;
    }

    public static int getPointsLost(final char type, final int value, final Fighter caster, final Fighter target) {
        if(type == 'a') {
            if(target.getCurPa(target.getFight()) == 0){
                return 0;
            }
        }
        else{
            if(target.getCurPm(target.getFight()) == 0){
                return 0;
            }
        }
        float esquiveC;
        float esquiveT;
        final float ptsMax = type == 'a' ? target.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_PA) : target.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_PM);
        
        if(caster.getMob() != null) esquiveC = type == 'a' ? caster.getTotalStatsLessBuff().get(Constant.STATS_ADD_AFLEE) : caster.getTotalStatsLessBuff().get(Constant.STATS_ADD_MFLEE);
        else esquiveC = type == 'a' ? caster.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_AFLEE) : caster.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_MFLEE);
        
        if(target.getMob() != null) esquiveT = type == 'a' ? target.getTotalStatsLessBuff().get(Constant.STATS_ADD_AFLEE) : target.getTotalStatsLessBuff().get(Constant.STATS_ADD_MFLEE);
        else esquiveT = type == 'a' ? target.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_AFLEE) : target.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_MFLEE);
        if(ptsMax == 0) return 0;
        if (esquiveT <= 0) esquiveT = 1f;
        if (esquiveC <= 0) esquiveC = 1f;
        int loose = 0;
        for (short i = 0; i < value; ++i) {
            final float pts = (type == 'a' ? target.getPa() : target.getPm()) - loose;
            // Probabilité de retirer 1 PA = 50 * (Retrait lanceur / esquive cible)* (PA actuels cible / PA totaux cibles) %
            float chance = 50f * (esquiveC / esquiveT) * (pts / ptsMax);
            if(chance > 90f) chance = 90f;
            else if(chance < 10f) chance = 10f;
            
            if(Math.round(chance) >= random.nextInt(101)) ++loose;
        }
        if(type == 'a') {
            if(target.getCurPa(target.getFight()) < loose){
                loose = target.getCurPa(target.getFight());
            }
        }
        else{
            if(target.getCurPm(target.getFight()) < loose){
                loose = target.getCurPm(target.getFight());
            }
        }
        return loose;
    }
    
    /*
    public static int getPointsLost(char z, int value, Fighter caster,
                                    Fighter target) {
        float esquiveC = z == 'a' ? caster.getTotalStats().getEffect(Constant.STATS_ADD_AFLEE) : caster.getTotalStats().getEffect(Constant.STATS_ADD_MFLEE);
        float esquiveT = z == 'a' ? target.getTotalStats().getEffect(Constant.STATS_ADD_AFLEE) : target.getTotalStats().getEffect(Constant.STATS_ADD_MFLEE);
        float ptsMax = z == 'a' ? target.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_PA) : target.getTotalStatsLessBuff().getEffect(Constant.STATS_ADD_PM);

        int retrait = 0;

        for (int i = 0; i < value; i++) {
            if (ptsMax == 0 && target.getMob() != null) {
                ptsMax = z == 'a' ? target.getMob().getPa() : target.getMob().getPm();
            }

            float pts = z == 'a' ? target.getPa() : target.getPm();
            float ptsAct = pts - retrait;

            if (esquiveT <= 0)
                esquiveT = 1;
            if (esquiveC <= 0)
                esquiveC = 1;

            float a = esquiveC / esquiveT;
            float b = (ptsAct / ptsMax);

            float pourcentage = a * b * 50;
            int chance = (int) Math.ceil(pourcentage);

            if (chance < 0)
                chance = 0;
            if (chance > 100)
                chance = 100;

            int jet = getRandomValue(0, 99);
            if (jet < chance) {
                retrait++;
            }
        }
        return retrait;
    }*/

    public static long getGuildXpWin(Fighter perso, AtomicReference<Long> xpWin) {
        if (perso.getPersonnage() == null)
            return 0;
        if (perso.getPersonnage().getGuildMember() == null)
            return 0;

        Guild.GuildMember gm = perso.getPersonnage().getGuildMember();

        double xp = (double) xpWin.get(),
                Lvl = perso.getLvl(),
                LvlGuild = perso.getPersonnage().get_guild().getLvl(),
                pXpGive = (double) gm.getPXpGive() / 100;

        double maxP = xp * pXpGive * 0.10; //Le maximum donné à la guilde est 10% du montant prélevé sur l'xp du combat
        double diff = Math.abs(Lvl - LvlGuild); //Calcul l'écart entre le niveau du personnage et le niveau de la guilde
        double toGuild;
        if (diff >= 70) {
            toGuild = maxP * 0.10; //Si l'écart entre les deux level est de 70 ou plus, l'experience donnée a la guilde est de 10% la valeur maximum de don
        } else if (diff >= 31 && diff <= 69) {
            toGuild = maxP - ((maxP * 0.10) * (Math.floor((diff + 30) / 10)));
        } else if (diff >= 10 && diff <= 30) {
            toGuild = maxP - ((maxP * 0.20) * (Math.floor(diff / 10)));
        } else
        //Si la différence est [0,9]
        {
            toGuild = maxP;
        }
        xpWin.set((long) (xp - xp * pXpGive));
        return Math.round(toGuild);
    }

    public static long getMountXpWin(Fighter perso, AtomicReference<Long> xpWin) {
        if (perso.getPersonnage() == null)
            return 0;
        if (perso.getPersonnage().getMount() == null)
            return 0;

        int diff = Math.abs(perso.getLvl()
                - perso.getPersonnage().getMount().getLevel());

        double coeff = 0;
        double xp = (double) xpWin.get();
        double pToMount = (double) perso.getPersonnage().getMountXpGive() / 100 + 0.2;

        if (diff >= 0 && diff <= 9)
            coeff = 0.1;
        else if (diff >= 10 && diff <= 19)
            coeff = 0.08;
        else if (diff >= 20 && diff <= 29)
            coeff = 0.06;
        else if (diff >= 30 && diff <= 39)
            coeff = 0.04;
        else if (diff >= 40 && diff <= 49)
            coeff = 0.03;
        else if (diff >= 50 && diff <= 59)
            coeff = 0.02;
        else if (diff >= 60 && diff <= 69)
            coeff = 0.015;
        else
            coeff = 0.01;

        if (pToMount > 0.2)
            xpWin.set((long) (xp - (xp * (pToMount - 0.2))));

        return Math.round(xp * pToMount * coeff);
    }

    public static int getKamasWin(Fighter i, ArrayList<Fighter> winners,
                                  int maxk, int mink) {
        maxk++;
        int rkamas = (int) (Math.random() * (maxk - mink)) + mink;
        return rkamas * Config.getInstance().rateKamas;
    }

    public static int getKamasWinPerco(int maxk, int mink) {
        maxk++;
        int rkamas = (int) (Math.random() * (maxk - mink)) + mink;
        return rkamas * Config.getInstance().rateKamas;
    }

    public static Couple<Integer, Integer> decompPierreAme(GameObject toDecomp) {
        Couple<Integer, Integer> toReturn;
        String[] stats = toDecomp.parseStatsString().split("#");
        int lvlMax = Integer.parseInt(stats[3], 16);
        int chance = Integer.parseInt(stats[1], 16);
        toReturn = new Couple<Integer, Integer>(chance, lvlMax);

        return toReturn;
    }

    public static int totalCaptChance(int pierreChance, Player p) {
        int sortChance = 0;

        switch (p.getSortStatBySortIfHas(413).getLevel()) {
            case 1:
                sortChance = 1;
                break;
            case 2:
                sortChance = 3;
                break;
            case 3:
                sortChance = 6;
                break;
            case 4:
                sortChance = 10;
                break;
            case 5:
                sortChance = 15;
                break;
            case 6:
                sortChance = 25;
                break;
        }

        return sortChance + pierreChance;
    }

    public static int spellCost(int nb) {
        int total = 0;
        for (int i = 1; i < nb; i++) {
            total += i;
        }

        return total;
    }

    public static int getLoosEnergy(int lvl, boolean isAgression,
                                    boolean isPerco) {

        int returned = 5 * lvl;
        if (isAgression)
            returned *= (7 / 4);
        if (isPerco)
            returned *= (3 / 2);
        return returned;
    }

    public static int totalAppriChance(boolean Amande, boolean Rousse,
                                       boolean Doree, Player p) {
        int sortChance = 0;
        int ddChance = 0;
        switch (p.getSortStatBySortIfHas(414).getLevel()) {
            case 1:
                sortChance = 15;
                break;
            case 2:
                sortChance = 20;
                break;
            case 3:
                sortChance = 25;
                break;
            case 4:
                sortChance = 30;
                break;
            case 5:
                sortChance = 35;
                break;
            case 6:
                sortChance = 45;
                break;
        }
        if (Amande || Rousse)
            ddChance = 15;
        if (Doree)
            ddChance = 5;
        return sortChance + ddChance;
    }

    public static int getCouleur(boolean Amande, boolean Rousse, boolean Doree) {
        int Couleur = 0;
        if (Amande && !Rousse && !Doree)
            return 20;
        if (Rousse && !Amande && !Doree)
            return 10;
        if (Doree && !Amande && !Rousse)
            return 18;

        if (Amande && Rousse && !Doree) {
            int Chance = Formulas.getRandomValue(1, 2);
            if (Chance == 1)
                return 20;
            if (Chance == 2)
                return 10;
        }
        if (Amande && !Rousse && Doree) {
            int Chance = Formulas.getRandomValue(1, 2);
            if (Chance == 1)
                return 20;
            if (Chance == 2)
                return 18;
        }
        if (!Amande && Rousse && Doree) {
            int Chance = Formulas.getRandomValue(1, 2);
            if (Chance == 1)
                return 18;
            if (Chance == 2)
                return 10;
        }
        if (Amande && Rousse && Doree) {
            int Chance = Formulas.getRandomValue(1, 3);
            if (Chance == 1)
                return 20;
            if (Chance == 2)
                return 10;
            if (Chance == 3)
                return 18;
        }
        return Couleur;
    }

    public static int calculEnergieLooseForToogleMount(int pts) {
        if (pts <= 170)
            return 4;
        if (pts >= 171 && pts < 180)
            return 5;
        if (pts >= 180 && pts < 200)
            return 6;
        if (pts >= 200 && pts < 210)
            return 7;
        if (pts >= 210 && pts < 220)
            return 8;
        if (pts >= 220 && pts < 230)
            return 10;
        if (pts >= 230 && pts <= 240)
            return 12;
        return 10;
    }

    public static int getLvlDopeuls(int lvl)//Niveau du dopeul à combattre
    {
        if (lvl < 20)
            return 20;
        if (lvl > 19 && lvl < 40)
            return 40;
        if (lvl > 39 && lvl < 60)
            return 60;
        if (lvl > 59 && lvl < 80)
            return 80;
        if (lvl > 79 && lvl < 100)
            return 100;
        if (lvl > 99 && lvl < 120)
            return 120;
        if (lvl > 119 && lvl < 140)
            return 140;
        if (lvl > 139 && lvl < 160)
            return 160;
        if (lvl > 159 && lvl < 180)
            return 180;
        if (lvl > 180)
            return 200;
        return 200;
    }

    public static int calculChanceByElement(int lvlJob, int lvlObject,
                                            int lvlRune) {
        int K = 1;
        if (lvlRune == 1)
            K = 100;
        else if (lvlRune == 25)
            K = 175;
        else if (lvlRune == 50)
            K = 350;
        return lvlJob * 100 / (K + lvlObject);
    }

    public static ArrayList<Integer> chanceFM(int WeightTotalBase,
                                                        int WeightTotalBaseMin, int currentWeithTotal,
                                                        int currentWeightStats, int weight, int diff, float coef,
                                                        int maxStat, int minStat, int actualStat, float x,
                                                        boolean bonusRune, int statsAdd, String loi) {
        ArrayList<Integer> chances = new ArrayList<Integer>();
        float c = 1, m1 = (maxStat - (actualStat + statsAdd)), m2 = (maxStat - minStat);
        if ((1 - (m1 / m2)) > 1.0)
            c = (1 - ((1 - (m1 / m2)) / 2)) / 2;
        else if ((1 - (m1 / m2)) > 0.8)
            c = 1 - ((1 - (m1 / m2)) / 2);
        if (c < 0)
            c = 0;
        // la variable c reste à 1 si le jet ne depasse pas 80% sinon il diminue très fortement. Si le jet dépasse 100% alors il diminue encore plus.

        int moyenne = (int) Math.floor(WeightTotalBase
                - ((WeightTotalBase - WeightTotalBaseMin) / 2));

        float mStat = ((float) moyenne / (float) currentWeithTotal); // Si l'item est un bon jet dans l'ensemble, diminue les chances sinon l'inverse.

        if (mStat > 1.2)
            mStat = 1.2F;
        float a = ((((((WeightTotalBase + diff) * coef) * mStat) * c) * x) * Config.getInstance().rateFm);
        float b = (float) (Math.sqrt(currentWeithTotal + currentWeightStats) + weight);
        if (b < 1.0)
            b = 1.0F;

        int p1 = (int) Math.floor(a / b); // Succes critique
        int p2 = 0; // Succes neutre
        int p3 = 0; // Echec critique
        if (bonusRune)
            p1 += 20;
        if (p1 < 1 && loi == "exo") { // Cas d'un exo PA/PM
            p1 = 1;
            p2 = 0;
            p3 = 99;
        } else if(p1 < 1 && loi == "over") {	// Cas d'un over en carac
        	if(currentWeithTotal >= WeightTotalBase) { // Tant que le PA est sur le Gelano les chances sont de 10% en Crit
        		p1 = 10;
        		p2 = 0;
        		p3 = 90;
        	} else {
            	p1 = 2; 							// On propose 2% de SC
            	p2 = 78 - actualStat*217*weight/(statsAdd*100); 		// Base 80% de succes avec difficulté croissante -> Cale sur 25
            	// Cas d'un gelano + carac
            	// On cherche que la proba tende vers 0 vers +36 dans un élément (216/100 * 36 = 78 pour p2(SN) qui tend vers 0)
            	// Le calcul a été arrangé pour ne pas être éronné malgré les int
            	// On y ajoute la constante poidRune/StatsAjouté pour permettre aux gelano vita d'être + cohérants
            	// FIXME: Ajouter la difficulté en f(poid des runes)
            	// Le cas des runes éléments et vie a été géré, il faut ajouter les autres runes
            	// Par exemple les runes vi passent + facilement de 0 à 30 en carac etc
            	if(p2<0) {
            		p2 = 0;
            	}
            	p3 = 100-(p1+p2);
        	}
    	} if (p1 > 100) {
            p1 = 66;
            p2 = 34;
        } else if (p1 > 66) {
            p1 = 66;
        } 

        if (p2 == 0 && p3 == 0) {
            p2 = (int) Math.floor(a
                    / (Math.sqrt(currentWeithTotal + currentWeightStats)));
            if (p2 > (100 - p1))
                p2 = (100 - p1);
            if (p2 > 50)
                p2 = 50;
        }
        chances.add(0, p1);
        chances.add(1, p2);
        chances.add(2, p3);
        return chances;
    }

    public static String convertToDate(long time) {
        String hexDate = "#";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = formatter.format(time);

        String[] split = date.split("\\s");

        String[] split0 = split[0].split("-");
        hexDate += Integer.toHexString(Integer.parseInt(split0[0])) + "#";
        int mois = Integer.parseInt(split0[1]) - 1;
        int jour = Integer.parseInt(split0[2]);
        hexDate += Integer.toHexString(Integer.parseInt((mois < 10 ? "0" + mois : mois)
                + "" + (jour < 10 ? "0" + jour : jour)))
                + "#";

        String[] split1 = split[1].split(":");
        String heure = split1[0] + split1[1];
        hexDate += Integer.toHexString(Integer.parseInt(heure));
        return hexDate;
    }

    public static int getXpStalk(int lvl) {
        switch (lvl) {
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
                return 65000;
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
                return 90000;
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
                return 120000;
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
                return 160000;
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 98:
            case 99:
                return 210000;
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
                return 270000;
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
                return 350000;
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
                return 440000;
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
                return 540000;
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
                return 650000;
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
                return 760000;
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
                return 880000;
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
                return 1000000;
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
                return 1130000;
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
                return 1300000;
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
                return 1500000;
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
                return 1700000;
            case 185:
            case 186:
            case 187:
            case 188:
            case 189:
                return 2000000;
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
                return 2500000;
            case 195:
            case 196:
            case 197:
            case 198:
            case 199:
            case 200:
                return 3000000;

        }
        return 65000;
    }

    public static String translateMsg(String msg) {
        String alpha = "a b c d e f g h i j k l n o p q r s t u v w x y z é è à ç & û â ê ô î ä ë ü ï ö";
        for (String i : alpha.split(" "))
            msg = msg.replace(i, "m");
        alpha = "A B C D E F G H I J K L M N O P Q R S T U V W X Y Z Ë Ü Ä Ï Ö Â Ê Û Î Ô";
        for (String i : alpha.split(" "))
            msg = msg.replace(i, "H");
        return msg;
    }
    
    //v2.8 - Critical fail system
    public static boolean isCriticalFail(int spellRate, Fighter fighter)
    {
      if(spellRate==0)
        return false;
      int buffRate=fighter.getBuffValue(Constant.STATS_ADD_EC);
      int CFRate=spellRate-buffRate;
      if(CFRate<2)
        CFRate=2;
      return Formulas.getRandomValue(1,CFRate)==CFRate;
    }
    
    /**
     * Permet de trier les stats d'un item template et d'un item cree. Il faut lui donner uniquement les stats normaux et non les autres.
     * Les stats qui ne sont pas specifie dans le tableaux order seront mis a la fin par ordre croissant selon leur stat ID
     * 
     * @author Sarazar928Ghost Kevin#6537
     * @param strStats Stats de l'objet
     * @return Stats sort by order
     */
    public static String sortStatsByOrder(final String strStats) {
    		
		if(strStats.isEmpty()) return strStats;
		
		final Map<Byte, String> orderStats = new TreeMap<>();
		final Map<Integer, String> unknowStats = new TreeMap<>();
		
		for(final String stat : strStats.split(",")) {
			final int id = Integer.parseInt(stat.split("#")[0], 16);
			
			boolean ok = false;
			
			for(byte i = 0; i < order.length; ++i) {
				if(order[i] != id) continue;
				orderStats.put(i, stat);
				ok = true;
				break;
			}
			
			if(ok) continue;
			
			unknowStats.put(id, stat);
			
		}
		
		
		final StringBuilder theReturn = new StringBuilder();
		boolean isFirst = true;
		
		for(final byte number : orderStats.keySet())
		{
			
			if(!isFirst)
				theReturn.append(",");
			isFirst = false;
			
			theReturn.append(orderStats.get(number));
			
		}
		
		for(final String stat : unknowStats.values()) {
			
			if(!isFirst)
				theReturn.append(",");
			isFirst = false;
			
			theReturn.append(stat);
		}
    	
    	return theReturn.toString();
    	
    }

    public static char[] shuffleCharArray(char[] ar)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            char a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
        return ar;
    }

    public static int[] getRandomsInt(int[] table,int nb)
    {
        int[] randomInts = new int[nb];
        Random random = new Random();

        // Shuffle the array using Fisher-Yates algorithm
        for (int i = table.length - 1; i >= 1; i--) {
            int j = random.nextInt(i + 1);
            int temp = table[j];
            table[j] = table[i];
            table[i] = temp;
        }

        // Copy the first 7 elements of the shuffled array to the randomInts array
        System.arraycopy(table, 0, randomInts, 0, nb);
        return randomInts;
    }
}
