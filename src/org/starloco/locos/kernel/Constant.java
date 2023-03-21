package org.starloco.locos.kernel;

import org.apache.commons.lang.ArrayUtils;
import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.ObjectTemplate;
import org.starloco.locos.util.RandomStats;

import java.util.*;

public class Constant {
	
	
	public static final String COLOR_INIT = "630063";
	public static final String COLOR_CHANCE = "408d8d";
	public static final String COLOR_INTEL = "d13802";
	public static final String COLOR_AGI = "296e2b";
	public static final String COLOR_VITA = "ff1f1f";
	public static final String COLOR_FORCE = "815634";
	public static final String COLOR_SAGESSE = "3c136d";
	public static final String COLOR_PROSPECTION = "058de4";
	public static final String COLOR_PA = "d99b0b";
	public static final String COLOR_PM = "96c800";
	public static final String COLOR_PO = "26b38c";
	public static final String COLOR_INVOC = "f6850b";
	
    //DEBUG
    public static final int DEBUG_MAP_LIMIT = 30000;
    //Fight
    public static final int TIME_START_FIGHT = 45000;
    public static final int TIME_BY_TURN = 30000;
    //Phoenix
    public static final String ALL_PHOENIX = "-11;-54|2;-12|-41;-17|5;-9|25;-4|36;5|12;12|10;19|-10;13|-14;31|-43;0|-60;-3|-58;18|24;-43|27;-33";
    //ETAT
    public static final int ETAT_NEUTRE = 0;
    public static final int ETAT_SAOUL = 1;
    public static final int ETAT_CAPT_AME = 2;
    public static final int ETAT_PORTEUR = 3;
    public static final int ETAT_PEUREUX = 4;
    public static final int ETAT_DESORIENTE = 5;
    public static final int ETAT_ENRACINE = 6;
    public static final int ETAT_PESANTEUR = 7;
    public static final int ETAT_PORTE = 8;
    public static final int ETAT_MOTIV_SYLVESTRE = 9;
    public static final int ETAT_APPRIVOISEMENT = 10;
    public static final int ETAT_CHEVAUCHANT = 11;
    public static final int STATE_UNDODGEABLE=73;
    public static final int STATE_SOBER=20;
    public static final int FIGHT_TYPE_CHALLENGE = 0; //Défies
    public static final int FIGHT_TYPE_AGRESSION = 1; //Aggros
    public static final int FIGHT_TYPE_CONQUETE = 2; //Conquete
    public static final int FIGHT_TYPE_DOPEUL = 3; //Dopeuls de temple
    public static final int FIGHT_TYPE_PVM = 4; //PvM
    public static final int FIGHT_TYPE_PVT = 5; //Percepteur
    public static final int FIGHT_TYPE_STAKE=6; //Stakes // FRED .ipdrop
    public static final int FIGHT_STATE_INIT = 1;
    public static final int FIGHT_STATE_PLACE = 2;
    public static final int FIGHT_STATE_ACTIVE = 3;
    public static final int FIGHT_STATE_FINISHED = 4;
    //Items
    //Positions
    public static final int ITEM_POS_NO_EQUIPED = -1;
    public static final int ITEM_POS_AMULETTE = 0;
    public static final int ITEM_POS_ARME = 1;
    public static final int ITEM_POS_ANNEAU1 = 2;
    public static final int ITEM_POS_CEINTURE = 3;
    public static final int ITEM_POS_ANNEAU2 = 4;
    public static final int ITEM_POS_BOTTES = 5;
    public static final int ITEM_POS_COIFFE = 6;
    public static final int ITEM_POS_CAPE = 7;
    public static final int ITEM_POS_FAMILIER = 8;
    public static final int ITEM_POS_DOFUS1 = 9;
    public static final int ITEM_POS_DOFUS2 = 10;
    public static final int ITEM_POS_DOFUS3 = 11;
    public static final int ITEM_POS_DOFUS4 = 12;
    public static final int ITEM_POS_DOFUS5 = 13;
    public static final int ITEM_POS_DOFUS6 = 14;
    public static final int ITEM_POS_BOUCLIER = 15;
    public static final int ITEM_POS_DRAGODINDE = 16;

    // Positions Consommable
    public static final int CONSO_POS_1 = 35;
    public static final int CONSO_POS_14 = 48;
    //Objets dons, mutations, malédiction, ..
    public static final int ITEM_POS_MUTATION = 20;
    public static final int ITEM_POS_ROLEPLAY_BUFF = 21;
    public static final int ITEM_POS_PNJ_SUIVEUR = 24;
    public static final int ITEM_POS_BENEDICTION = 23;
    public static final int ITEM_POS_MALEDICTION = 22;
    public static final int ITEM_POS_BONBON = 25;

    public static final int ITEM_POS_TONIQUE_EQUILIBRAGE = 65;
    public static final int ITEM_POS_TONIQUE1 = 66;
    public static final int ITEM_POS_TONIQUE2 = 67;
    public static final int ITEM_POS_TONIQUE3 = 68;
    public static final int ITEM_POS_TONIQUE4 = 69;
    public static final int ITEM_POS_TONIQUE5 = 70;
    public static final int ITEM_POS_TONIQUE6 = 71;
    public static final int ITEM_POS_TONIQUE7 = 72;
    public static final int ITEM_POS_TONIQUE8 = 73;
    public static final int ITEM_POS_TONIQUE9 = 74;

    //Types
    public static final int ITEM_TYPE_AMULETTE = 1;
    public static final int ITEM_TYPE_ARC = 2;
    public static final int ITEM_TYPE_BAGUETTE = 3;
    public static final int ITEM_TYPE_BATON = 4;
    public static final int ITEM_TYPE_DAGUES = 5;
    public static final int ITEM_TYPE_EPEE = 6;
    public static final int ITEM_TYPE_MARTEAU = 7;
    public static final int ITEM_TYPE_PELLE = 8;
    public static final int ITEM_TYPE_ANNEAU = 9;
    public static final int ITEM_TYPE_CEINTURE = 10;
    public static final int ITEM_TYPE_BOTTES = 11;
    public static final int ITEM_TYPE_POTION = 12;
    public static final int ITEM_TYPE_PARCHO_EXP = 13;
    public static final int ITEM_TYPE_DONS = 14;
    public static final int ITEM_TYPE_RESSOURCE = 15;
    public static final int ITEM_TYPE_COIFFE = 16;
    public static final int ITEM_TYPE_CAPE = 17;
    public static final int ITEM_TYPE_FAMILIER = 18;
    public static final int ITEM_TYPE_HACHE = 19;
    public static final int ITEM_TYPE_OUTIL = 20;
    public static final int ITEM_TYPE_PIOCHE = 21;
    public static final int ITEM_TYPE_FAUX = 22;
    public static final int ITEM_TYPE_DOFUS = 23;
    public static final int ITEM_TYPE_QUETES = 24;
    public static final int ITEM_TYPE_DOCUMENT = 25;
    public static final int ITEM_TYPE_FM_POTION = 26;
    public static final int ITEM_TYPE_TRANSFORM = 27;
    public static final int ITEM_TYPE_BOOST_FOOD = 28;
    public static final int ITEM_TYPE_BENEDICTION = 29;
    public static final int ITEM_TYPE_MALEDICTION = 30;
    public static final int ITEM_TYPE_RP_BUFF = 31;
    public static final int ITEM_TYPE_PERSO_SUIVEUR = 32;
    public static final int ITEM_TYPE_PAIN = 33;
    public static final int ITEM_TYPE_CEREALE = 34;
    public static final int ITEM_TYPE_FLEUR = 35;
    public static final int ITEM_TYPE_PLANTE = 36;
    public static final int ITEM_TYPE_BIERE = 37;
    public static final int ITEM_TYPE_BOIS = 38;
    public static final int ITEM_TYPE_MINERAIS = 39;
    public static final int ITEM_TYPE_ALLIAGE = 40;
    public static final int ITEM_TYPE_POISSON = 41;
    public static final int ITEM_TYPE_BONBON = 42;
    public static final int ITEM_TYPE_POTION_OUBLIE = 43;
    public static final int ITEM_TYPE_POTION_METIER = 44;
    public static final int ITEM_TYPE_POTION_SORT = 45;
    public static final int ITEM_TYPE_FRUIT = 46;
    public static final int ITEM_TYPE_OS = 47;
    public static final int ITEM_TYPE_POUDRE = 48;
    public static final int ITEM_TYPE_COMESTI_POISSON = 49;
    public static final int ITEM_TYPE_PIERRE_PRECIEUSE = 50;
    public static final int ITEM_TYPE_PIERRE_BRUTE = 51;
    public static final int ITEM_TYPE_FARINE = 52;
    public static final int ITEM_TYPE_PLUME = 53;
    public static final int ITEM_TYPE_POIL = 54;
    public static final int ITEM_TYPE_ETOFFE = 55;
    public static final int ITEM_TYPE_CUIR = 56;
    public static final int ITEM_TYPE_LAINE = 57;
    public static final int ITEM_TYPE_GRAINE = 58;
    public static final int ITEM_TYPE_PEAU = 59;
    public static final int ITEM_TYPE_HUILE = 60;
    public static final int ITEM_TYPE_PELUCHE = 61;
    public static final int ITEM_TYPE_POISSON_VIDE = 62;
    public static final int ITEM_TYPE_VIANDE = 63;
    public static final int ITEM_TYPE_VIANDE_CONSERVEE = 64;
    public static final int ITEM_TYPE_QUEUE = 65;
    public static final int ITEM_TYPE_METARIA = 66;
    public static final int ITEM_TYPE_LEGUME = 68;
    public static final int ITEM_TYPE_VIANDE_COMESTIBLE = 69;
    public static final int ITEM_TYPE_TEINTURE = 70;
    public static final int ITEM_TYPE_EQUIP_ALCHIMIE = 71;
    public static final int ITEM_TYPE_OEUF_FAMILIER = 72;
    public static final int ITEM_TYPE_MAITRISE = 73;
    public static final int ITEM_TYPE_FEE_ARTIFICE = 74;
    public static final int ITEM_TYPE_PARCHEMIN_SORT = 75;
    public static final int ITEM_TYPE_PARCHEMIN_CARAC = 76;
    public static final int ITEM_TYPE_CERTIFICAT_CHANIL = 77;
    public static final int ITEM_TYPE_RUNE_FORGEMAGIE = 78;
    public static final int ITEM_TYPE_BOISSON = 79;
    public static final int ITEM_TYPE_OBJET_MISSION = 80;
    public static final int ITEM_TYPE_SAC_DOS = 81;
    public static final int ITEM_TYPE_BOUCLIER = 82;
    public static final int ITEM_TYPE_PIERRE_AME = 83;
    public static final int ITEM_TYPE_CLEFS = 84;
    public static final int ITEM_TYPE_PIERRE_AME_PLEINE = 85;
    public static final int ITEM_TYPE_POPO_OUBLI_PERCEP = 86;
    public static final int ITEM_TYPE_PARCHO_RECHERCHE = 87;
    public static final int ITEM_TYPE_PIERRE_MAGIQUE = 88;
    public static final int ITEM_TYPE_CADEAUX = 89;
    public static final int ITEM_TYPE_FANTOME_FAMILIER = 90;
    public static final int ITEM_TYPE_DRAGODINDE = 91;
    public static final int ITEM_TYPE_BOUFTOU = 92;
    public static final int ITEM_TYPE_OBJET_ELEVAGE = 93;
    public static final int ITEM_TYPE_OBJET_UTILISABLE = 94;
    public static final int ITEM_TYPE_PLANCHE = 95;
    public static final int ITEM_TYPE_ECORCE = 96;
    public static final int ITEM_TYPE_CERTIF_MONTURE = 97;
    public static final int ITEM_TYPE_RACINE = 98;
    public static final int ITEM_TYPE_FILET_CAPTURE = 99;
    public static final int ITEM_TYPE_SAC_RESSOURCE = 100;
    public static final int ITEM_TYPE_ARBALETE = 102;
    public static final int ITEM_TYPE_PATTE = 103;
    public static final int ITEM_TYPE_AILE = 104;
    public static final int ITEM_TYPE_OEUF = 105;
    public static final int ITEM_TYPE_OREILLE = 106;
    public static final int ITEM_TYPE_CARAPACE = 107;
    public static final int ITEM_TYPE_BOURGEON = 108;
    public static final int ITEM_TYPE_OEIL = 109;
    public static final int ITEM_TYPE_GELEE = 110;
    public static final int ITEM_TYPE_COQUILLE = 111;
    public static final int ITEM_TYPE_PRISME = 112;
    public static final int ITEM_TYPE_OBJET_VIVANT = 113;
    public static final int ITEM_TYPE_ARME_MAGIQUE = 114;
    public static final int ITEM_TYPE_FRAGM_AME_SHUSHU = 115;
    public static final int ITEM_TYPE_POTION_FAMILIER = 116;
    public static final int ITEM_TYPE_CARTE_COMMUN = 119;
    public static final int ITEM_TYPE_CARTE_RARE = 120;
    public static final int ITEM_TYPE_CARTE_EPIQUE = 121;
    public static final int ITEM_TYPE_CARTE_ULTIME = 122;
    public static final int ITEM_TYPE_PAQUET_CARTE = 123;
    public static final int ITEM_TYPE_PIERRE_AME_BOSS = 124;
    public static final int ITEM_TYPE_PIERRE_AME_ARCHI = 125;
    public static final int ITEM_TYPE_TONIQUE = 126;
    public static final int ITEM_TYPE_RESERVED = 127;

    //Alignement
    public static final int ALIGNEMENT_NEUTRE = -1;
    public static final int ALIGNEMENT_BONTARIEN = 1;
    public static final int ALIGNEMENT_BRAKMARIEN = 2;
    public static final int ALIGNEMENT_MERCENAIRE = 3;
    //Elements
    public static final int ELEMENT_NULL = -1;
    public static final int ELEMENT_NEUTRE = 0;
    public static final int ELEMENT_TERRE = 1;
    public static final int ELEMENT_EAU = 2;
    public static final int ELEMENT_FEU = 3;
    public static final int ELEMENT_AIR = 4;
    //Classes
    public static final int CLASS_FECA = 1;
    public static final int CLASS_OSAMODAS = 2;
    public static final int CLASS_ENUTROF = 3;
    public static final int CLASS_SRAM = 4;
    public static final int CLASS_XELOR = 5;
    public static final int CLASS_ECAFLIP = 6;
    public static final int CLASS_ENIRIPSA = 7;
    public static final int CLASS_IOP = 8;
    public static final int CLASS_CRA = 9;
    public static final int CLASS_SADIDA = 10;
    public static final int CLASS_SACRIEUR = 11;
    public static final int CLASS_PANDAWA = 12;
    //Sexes
    public static final int SEX_MALE = 0;
    public static final int SEX_FEMALE = 1;
    //GamePlay
    public static final int MAX_EFFECTS_ID = 2200;
    //Buff a vérifier en début de tour
    public static final int[] BEGIN_TURN_BUFF = {91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 108};
    //Buff des Armes
    public static final int[] ARMES_EFFECT_IDS = {91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 108};
    //Buff a ne pas booster en cas de CC
    public static final int[] NO_BOOST_CC_IDS = {101};
    //Invocation Statiques
    public static final int[] STATIC_INVOCATIONS = {282, 556, 2750, 7000};
    //Verif d'Etat au lancement d'un sort {spellID,stateID}, à completer avant d'activer
    public static final int[][] STATE_REQUIRED = {{699, Constant.ETAT_SAOUL}, {690, Constant.ETAT_SAOUL}};
    //Buff déclenché en cas de frappe
    public static final int[] ON_HIT_BUFFS = {9, 79, 107, 220, 788, 606, 607, 608, 609, 611};
    //Effects
    public static final int STATS_ADD_PM2 = 78;
    public static final int STATS_REM_PA = 101;
    public static final int STATS_ADD_VIE = 110;
    public static final int STATS_ADD_PA = 111;
    public static final int STATS_MULTIPLY_DOMMAGE = 114;
    public static final int STATS_ADD_CC = 115;
    public static final int STATS_REM_PO = 116;
    public static final int STATS_ADD_PO = 117;
    public static final int STATS_ADD_FORC = 118;
    public static final int STATS_ADD_AGIL = 119;
    public static final int STATS_ADD_PA2 = 120;
    public static final int STATS_ADD_DOMA = 121;
 //   public static final int STATS_ADD_DOMA2 = 1210;
    public static final int STATS_ADD_EC = 122;
    public static final int STATS_ADD_CHAN = 123;
    public static final int STATS_ADD_SAGE = 124;
    public static final int STATS_ADD_VITA = 125;
    public static final int STATS_ADD_INTE = 126;
    public static final int STATS_REM_PM = 127;
    public static final int STATS_ADD_PM = 128;
    public static final int STATS_ADD_PERDOM = 138;
    public static final int STATS_ADD_PDOM = 142;
    public static final int STATS_REM_DOMA = 145;
    public static final int STATS_REM_CHAN = 152;
    public static final int STATS_REM_VITA = 153;
    public static final int STATS_REM_AGIL = 154;
    public static final int STATS_REM_INTE = 155;
    public static final int STATS_REM_SAGE = 156;
    public static final int STATS_REM_FORC = 157;
    public static final int STATS_ADD_PODS = 158;
    public static final int STATS_REM_PODS = 159;
    public static final int STATS_ADD_AFLEE = 160;
    public static final int STATS_ADD_MFLEE = 161;
    public static final int STATS_REM_AFLEE = 162;
    public static final int STATS_REM_MFLEE = 163;
    public static final int STATS_REM_PERDOM = 164;
    public static final int STATS_ADD_MAITRISE = 165;
    public static final int STATS_REM_PA2 = 168;
    public static final int STATS_REM_PM2 = 169;
    public static final int STATS_REM_CC = 171;
    public static final int STATS_REM_PDOM = 173;
    public static final int STATS_ADD_INIT = 174;
    public static final int STATS_REM_INIT = 175;
    public static final int STATS_ADD_PROS = 176;
    public static final int STATS_REM_PROS = 177;
    public static final int STATS_ADD_SOIN = 178;
    public static final int STATS_REM_SOIN = 179;
    public static final int STATS_ADD_SUM=182;
    public static final int STATS_CREATURE = 182;
    public static final int STATS_ADD_RP_TER = 210;
    public static final int STATS_ADD_RP_EAU = 211;
    public static final int STATS_ADD_RP_AIR = 212;
    public static final int STATS_ADD_RP_FEU = 213;
    public static final int STATS_ADD_RP_NEU = 214;
    public static final int STATS_REM_RP_TER = 215;
    public static final int STATS_REM_RP_EAU = 216;
    public static final int STATS_REM_RP_AIR = 217;
    public static final int STATS_REM_RP_FEU = 218;
    public static final int STATS_REM_RP_NEU = 219;
    public static final int STATS_RETDOM = 220;
    public static final int STATS_TRAPDOM = 225;
    public static final int STATS_TRAPPER = 226;
    public static final int STATS_ADD_R_FEU = 240;
    public static final int STATS_ADD_R_NEU = 241;
    public static final int STATS_ADD_R_TER = 242;
    public static final int STATS_ADD_R_EAU = 243;
    public static final int STATS_ADD_R_AIR = 244;
    public static final int STATS_REM_R_FEU = 245;
    public static final int STATS_REM_R_NEU = 246;
    public static final int STATS_REM_R_TER = 247;
    public static final int STATS_REM_R_EAU = 248;
    public static final int STATS_REM_R_AIR = 249;
    public static final int STATS_ADD_RP_PVP_TER = 250;
    public static final int STATS_ADD_RP_PVP_EAU = 251;
    public static final int STATS_ADD_RP_PVP_AIR = 252;
    public static final int STATS_ADD_RP_PVP_FEU = 253;
    public static final int STATS_ADD_RP_PVP_NEU = 254;
    public static final int STATS_REM_RP_PVP_TER = 255;
    public static final int STATS_REM_RP_PVP_EAU = 256;
    public static final int STATS_REM_RP_PVP_AIR = 257;
    public static final int STATS_REM_RP_PVP_FEU = 258;
    public static final int STATS_REM_RP_PVP_NEU = 259;
    public static final int STATS_ADD_R_PVP_TER = 260;
    public static final int STATS_ADD_R_PVP_EAU = 261;
    public static final int STATS_ADD_R_PVP_AIR = 262;
    public static final int STATS_ADD_R_PVP_FEU = 263;
    public static final int STATS_ADD_R_PVP_NEU = 264;
    public static final int STATS_ADD_ERO=1009;
    public static final int STATS_REM_ERO=1010;
    public static final int STATS_ADD_R_ERO=1011;
    public static final int STATS_REM_R_ERO=1012;
    public static final int STATS_REM_PA3 = 2100;
    public static final int STATS_REM_RENVOI = 2111;
    public static final int STATS_REM_INVO = 2112;
    public static final int STATS_REM_TRAPDOM = 2113;
    public static final int STATS_REM_TRAPPER = 2114;
    public static final int STATS_CHATI_SHARED_DAMAGE = 788;

    //Effets ID & Buffs
    public static final int EFFECT_PASS_TURN = 140;
    //Capture
    public static final int CAPTURE_MONSTRE = 623;
    public static final int STATS_NIVEAU2 = 669;
    //Familier
    public static final int STATS_PETS_PDV = 800;
    public static final int STATS_PETS_POIDS = 806;
    public static final int STATS_PETS_REPAS = 807;
    public static final int STATS_PETS_DATE = 808;
    public static final int STATS_PETS_EPO = 940;
    public static final int STATS_PETS_SOUL = 717;
    // Objet d'élevage
    public static final int STATS_RESIST = 812;
    // Other
    public static final int STATS_TURN = 811;
    public static final int STATS_EXCHANGE_IN = 983;
    public static final int STATS_CHANGE_BY = 985;
    public static final int STATS_BUILD_BY = 988;
    public static final int STATS_NAME_TRAQUE = 989;
    public static final int STATS_GRADE_TRAQUE = 961;
    public static final int STATS_ALIGNEMENT_TRAQUE = 960;
    public static final int STATS_NIVEAU_TRAQUE = 962;
    public static final int STATS_MIMIBIOTE = 969; // 3C9
    public static final byte ID_TEMPLATE_MIMIBIOTE = 4;
    public static final int STATS_BONUSADD = 2116;
    public static final int STATS_DATE = 805;
    public static final int STATS_NIVEAU = 962;
    public static final int STATS_NAME_DJ = 814;
    public static final int STATS_OWNER_1 = 987;//#4
    public static final int STATS_SIGNATURE = 988;
    public static final int ERR_STATS_XP = 1000;
    public static final int STATS_ADD_FINAL_DOM = 1008;
    public static final int STATS_REM_FINAL_DOM = 1009;

    // Buff de Sort Classe
    public static final int STATS_SPELL_ADD_PO = 281;
    public static final int STATS_SPELL_PO_MODIF = 282;
    public static final int STATS_SPELL_ADD_DOM = 283;
    public static final int STATS_SPELL_ADD_HEAL = 284;
    public static final int STATS_SPELL_REM_PA = 285;
    public static final int STATS_SPELL_REM_DELAY = 286;
    public static final int STATS_SPELL_ADD_CRIT = 287;
    public static final int STATS_SPELL_LINE_LAUNCH = 288;
    public static final int STATS_SPELL_LOS = 289;
    public static final int STATS_SPELL_ADD_LAUNCH = 290;
    public static final int STATS_SPELL_ADD_PER_TARGET = 291;
    public static final int STATS_SPELL_FIXE_DURATION_DELAY = 292;
    public static final int STATS_SPELL_ADD_BASE_DAMAGE = 293;
    public static final int STATS_SPELL_REM_PO = 294;

    public static final List<Integer> DAMAGE_EFFECT = Arrays.asList(91,92,93,94,95,96,97,98,99,100,138);


    public static int[] BOSS_ID = {58,85,86,107,113,121,147,173,180,225,226,230,232,251,252,257,289,295,374,375,377,382,404,423,430,457,478,568,605,612,669,670,673,675,677,681,780,792,797,799,800,827,854,926,939,940,943,1015,1027,1045,1051,1071,1072,1085,1086,1087,1159,1170,1184,1185,1186,1187,1188,1195};
    public static int[] EXCEPTION_GLADIATROOL_MONSTRES = {251,258,260,295,404,423,424,450,1017,1018,1090,1091,1092,1094, 1159};
    public static List<Integer> GLADIATROOL_MAPID = Arrays.asList(15000,15008,15016,15024,15032,15040,15048,15056,15064,15072);

    public static final List<Integer> GLADIATROOL_FULLMORPHID = Arrays.asList(101,102,103,104,105,106,107,108,109,110,111,112);

    //Monstre
    public static final int[] MONSTRE_TYPE_DIVERS = {-1,29,32,18,28,27,0,1,68,50,79};
    public static final int[] MONSTRE_TYPE_CHAMPS = {11,47,9,10,45,46};
    public static final int[] MONSTRE_TYPE_MONTAGNE = {16,12,52,6,13,5};
    public static final int[] MONSTRE_TYPE_FORET = {38,37,49,39,4,22,7};
    public static final int[] MONSTRE_TYPE_PLAINE = {24,23,51,21,2};
    public static final int[] MONSTRE_TYPE_LANDES = {25};
    public static final int[] MONSTRE_TYPE_ILE_MOON = {43,42,41,20,40};
    public static final int[] MONSTRE_TYPE_ILE_WABBIT = {3};
    public static final int[] MONSTRE_TYPE_PANDALA = {36,35,34,57,56,55,59};
    public static final int[] MONSTRE_TYPE_HUMAIN = {26,30,19};
    public static final int[] MONSTRE_TYPE_NUIT = {8,53,54,17};
    public static final int[] MONSTRE_TYPE_MARECAGE = {48,44};
    public static final int[] MONSTRE_TYPE_VILLE = {33,31};
    public static final int[] MONSTRE_TYPE_VILLAGE_ELEVEUR = {60,61};
    public static final int[] MONSTRE_TYPE_RESSOURCE_PROTECTEUR = {62,63,64,65,66};
    public static final int[] MONSTRE_TYPE_ILE_MINO = {67};
    public static final int[] MONSTRE_TYPE_PLAGES = {69};
    public static final int[] MONSTRE_TYPE_INCARNAM ={70};
    public static final int[] MONSTRE_TYPE_ILE_OTO = {71,72,73,74,75,76,77};
    public static final int[] MONSTRE_TYPE_ARCHI = {78};

    public static final int[] MONSTRE_ID_ISBOSS = {78};

    public static final int[] FILTER_MONSTRE_SPE1 = ArrayUtils.addAll(MONSTRE_TYPE_DIVERS,MONSTRE_TYPE_ARCHI);
    public static final int[] FILTER_MONSTRE_SPE2 = ArrayUtils.addAll(FILTER_MONSTRE_SPE1,MONSTRE_TYPE_HUMAIN);
    public static final int[] FILTER_MONSTRE_SPE = ArrayUtils.addAll(FILTER_MONSTRE_SPE2,MONSTRE_TYPE_RESSOURCE_PROTECTEUR);

    public static final int MONSTRES_NON_CLASSE = -1;
    public static final int MONSTRES_INVOCATIONS_DE_CLASSE = 0;
    public static final int MONSTRES_BOSS = 1;
    public static final int MONSTRES_BANDITS = 2;
    public static final int MONSTRES_WABBITS = 3;
    public static final int MONSTRES_DRAGOEUFS = 4;
    public static final int MONSTRES_BWORKS = 5;
    public static final int MONSTRES_GOBELINS = 6;
    public static final int MONSTRES_GELEES = 7;
    public static final int MONSTRES_DE_LA_NUIT = 8;
    public static final int MONSTRES_BOUFTOUS = 9;
    public static final int MONSTRES_PLANTES_DES_CHAMPS = 10;
    public static final int MONSTRES_LARVES = 11;
    public static final int MONSTRES_KWAKS = 12;
    public static final int MONSTRES_CRAQUELEURS = 13;
    public static final int MONSTRES_COCHONS = 16;
    public static final int MONSTRES_CHAFERS = 17;
    public static final int MONSTRES_DOPEULS_TEMPLE = 18;
    public static final int MONSTRES_PNJS = 19;
    public static final int MONSTRES_KANNIBOULS_DE_ILE_DE_MOON = 20;
    public static final int MONSTRES_DRAGODINDE = 21;
    public static final int MONSTRES_ABRAKNYDIEN = 22;
    public static final int MONSTRES_BLOP = 23;
    public static final int MONSTRES_DES_PLAINES_DE_CANIA = 24;
    public static final int MONSTRES_DES_LANDES = 25;
    public static final int MONSTRES_GARDES = 26;
    public static final int MONSTRES_DES_CONQUETES_DE_TERRITOIRES = 27;
    public static final int MONSTRES_DU_VILLAGE_DES_DOPEULS = 28;
    public static final int MONSTRES_TUTORIAL = 29;
    public static final int MONSTRES_BRIGANDINS = 30;
    public static final int MONSTRE_DES_EGOUTS = 31;
    public static final int MONSTRES_AVIS_DE_RECHERCHE = 32;
    public static final int MONSTRES_PIOUS = 33;
    public static final int MONSTRES_DU_VILLAGE_DE_PANDALA = 34;
    public static final int MONSTRES_DE_PANDALA = 35;
    public static final int MONSTRES_FANTOME_DE_PANDALA = 36;
    public static final int MONSTRES_SCARAFEUILLE = 37;
    public static final int MONSTRES_ARAKNE = 38;
    public static final int MONSTRES_MULOU = 39;
    public static final int MONSTRES_TORTUES_DE_MOON = 40;
    public static final int MONSTRES_PIRATES_DE_MOON = 41;
    public static final int MONSTRES_PLANTES_DE_MOON = 42;
    public static final int MONSTRES_DE_MOON = 43;
    public static final int MONSTRES_CROCODAILLES = 44;
    public static final int MONSTRES_CHAMPIGNONS = 45;
    public static final int MONSTRES_TOFUS = 46;
    public static final int MONSTRES_MOSKITOS = 47;
    public static final int MONSTRES_DES_MARECAGES = 48;
    public static final int MONSTRES_ANIMAUX_DE_LA_FORET = 49;
    public static final int MONSTRES_DE_QUETE = 50;
    public static final int MONSTRES_CORBACS = 51;
    public static final int MONSTRES_GARDIENS_DES_VILLAGES_DE_KWAKS = 52;
    public static final int MONSTRES_FANTOMES = 53;
    public static final int MONSTRES_FAMILIERS_FANTOMES = 54;
    public static final int MONSTRES_PLANTES_DE_PANDALA = 55;
    public static final int MONSTRES_KITSOUS = 56;
    public static final int MONSTRES_PANDAWAS = 57;
    public static final int MONSTRES_FIREFOUX = 59;
    public static final int MONSTRES_KOALAKS = 60;
    public static final int MONSTRES_DES_CAVERNES = 61;
    public static final int MONSTRES_PROTECTEURS_DES_CEREALES = 62;
    public static final int MONSTRES_PROTECTEURS_DES_MINERAIS = 63;
    public static final int MONSTRES_PROTECTEURS_DES_ARBRES = 64;
    public static final int MONSTRES_PROTECTEURS_DES_POISSONS = 65;
    public static final int MONSTRES_PROTECTEURS_DES_PLANTES = 66;
    public static final int MONSTRES_MINOS = 67;
    public static final int MONSTRES_DE_NOWEL = 68;
    public static final int MONSTRES_DES_PLAGES = 69;
    public static final int MONSTRES_DE_LA_ZONE_DES_DEBUTANTS = 70;
    public static final int MONSTRES_DES_PLAINES_HERBEUSES = 71;
    public static final int MONSTRES_DE_LA_PLAGE_DE_CORAIL = 72;
    public static final int MONSTRES_DE_LA_TOURBIERE_SANS_FOND = 73;
    public static final int MONSTRES_DE_LA_JUNGLE_SOMBRE = 74;
    public static final int MONSTRES_DE_ARBRE_HAKAM = 75;
    public static final int MONSTRES_DE_ARCHE_DOTOMAI = 76;
    public static final int MONSTRES_DE_LA_CANOPEE_EMBRUMEE = 77;
    public static final int MONSTRES_LES_ARCHIMONSTRES = 78;
    public static final int MONSTRES_TO_VERIF = 79;

    //ZAAPI <alignID,{mapID,mapID,...,mapID}>
    public static Map<Integer, String> ZAAPI = new HashMap<Integer, String>();
    //ZAAP <mapID,cellID>
    public static Map<Integer, Integer> ZAAPS = new HashMap<Integer, Integer>();
    //Valeur des droits de guilde
    public static int G_BOOST = 2; //Gérer les boost
    public static int G_RIGHT = 4;
    public static int G_INVITE = 8;
    public static int G_BAN = 16;
    public static int G_ALLXP = 32;
    public static int G_HISXP = 256;
    public static int G_RANK = 64;
    public static int G_POSPERCO = 128;
    public static int G_COLLPERCO = 512;
    public static int G_USEENCLOS = 4096;
    public static int G_AMENCLOS = 8192;
    public static int G_OTHDINDE = 16384;
    //Valeur des droits de maison
    public static int H_GBLASON = 2;
    public static int H_OBLASON = 4;
    public static int H_GNOCODE = 8;
    public static int H_OCANTOPEN = 16;
    public static int C_GNOCODE = 32;
    public static int C_OCANTOPEN = 64;
    public static int H_GREPOS = 256;
    public static int H_GTELE = 128;
    // Nom des documents (swfs) : Documents d'avis de recherche
    public static String HUNT_DETAILS_DOC = "71_0706251229";
    public static String HUNT_FRAKACIA_DOC = "63_0706251124";
    public static String HUNT_AERMYNE_DOC = "100_0706251214";
    public static String HUNT_MARZWEL_DOC = "96_0706251201";
    public static String HUNT_BRUMEN_DOC = "68_0706251126";
    public static String HUNT_MUSHA_DOC = "94_0706251138";
    public static String HUNT_OGIVOL_DOC = "69_0706251058";
    public static String HUNT_PADGREF_DOC = "61_0802081743";
    public static String HUNT_QILBIL_DOC = "67_0706251223";
    public static String HUNT_ROK_DOC = "93_0706251135";
    public static String HUNT_ZATOISHWAN_DOC = "98_0706251211";
    public static String HUNT_LETHALINE_DOC = "65_0706251123";
    public static String HUNT_FOUDUGLEN_DOC = "70_0706251122";

    // {(int)BorneId, (int)CellId, (str)SwfDocName, (int)MobId, (int)ItemFollow, (int)QuestId, (int)reponseID
    public static String[][] HUNTING_QUESTS = {{"1988", "234", HUNT_DETAILS_DOC, "-1", "-1", "-1", "-1"}, {"1986", "161", HUNT_LETHALINE_DOC, "-1", "-1", "-1", "-1"}, {"1985", "119", HUNT_MARZWEL_DOC, "554", "7353", "117", "2552"}, {"1986", "120", HUNT_PADGREF_DOC, "459", "6870", "29", "2108"}, {"1985", "149", HUNT_FRAKACIA_DOC, "460", "6871", "30", "2109"}, {"1986", "150", HUNT_QILBIL_DOC, "481", "6873", "32", "2111"}, {"1986", "179", HUNT_BRUMEN_DOC, "464", "6874", "33", "2112"}, {"1986", "180", HUNT_OGIVOL_DOC, "462", "6876", "35", "2114"}, {"1985", "269", HUNT_MUSHA_DOC, "552", "7352", "116", "2551"}, {"1986", "270", HUNT_FOUDUGLEN_DOC, "463", "6875", "34", "2113"}, {"1985", "299", HUNT_ROK_DOC, "550", "7351", "115", "2550"}, {"1986", "300", HUNT_AERMYNE_DOC, "446", "7350", "119", "2554"}, {"1985", "329", HUNT_ZATOISHWAN_DOC, "555", "7354", "118", "2553"},};

    public static int getQuestByMobSkin(int mobSkin) {
        for (int v = 0; v < HUNTING_QUESTS.length; v++)
            if (World.world.getMonstre(Integer.parseInt(HUNTING_QUESTS[v][3])) != null
                    && World.world.getMonstre(Integer.parseInt(HUNTING_QUESTS[v][3])).getGfxId() == mobSkin)
                return Integer.parseInt(HUNTING_QUESTS[v][5]);
        return -1;
    }

    public static int getSkinByHuntMob(int mobId) {
        for (int v = 0; v < HUNTING_QUESTS.length; v++)
            if (Integer.parseInt(HUNTING_QUESTS[v][3]) == mobId)
                return World.world.getMonstre(mobId).getGfxId();
        return -1;
    }

    public static int getItemByHuntMob(int mobId) {
        for (int v = 0; v < HUNTING_QUESTS.length; v++)
            if (Integer.parseInt(HUNTING_QUESTS[v][3]) == mobId)
                return Integer.parseInt(HUNTING_QUESTS[v][4]);
        return -1;
    }

    public static int getItemByMobSkin(int mobSkin) {
        for (int v = 0; v < HUNTING_QUESTS.length; v++)
            if (World.world.getMonstre(Integer.parseInt(HUNTING_QUESTS[v][3])) != null
                    && World.world.getMonstre(Integer.parseInt(HUNTING_QUESTS[v][3])).getGfxId() == mobSkin)
                return Integer.parseInt(HUNTING_QUESTS[v][4]);
        return -1;
    }

    public static String getDocNameByBornePos(int borneId, int cellid) {
        for (int v = 0; v < HUNTING_QUESTS.length; v++)
            if (Integer.parseInt(HUNTING_QUESTS[v][0]) == borneId
                    && Integer.parseInt(HUNTING_QUESTS[v][1]) == cellid)
                return HUNTING_QUESTS[v][2];
        return "";
    }

    public static short getClassStatueMap(int classID) {
        short pos = 10298;
        switch (classID) {
            case 1:
                return 7398;
            case 2:
                return 7545;
            case 3:
                return 7442;
            case 4:
                return 7392;
            case 5:
                return 7332;
            case 6:
                return 7446;
            case 7:
                return 7361;
            case 8:
                return 7427;
            case 9:
                return 7378;
            case 10:
                return 7395;
            case 11:
                return 7336;
            case 12:
                return 8035;
            case 13:
                return 7427;
        }
        return pos;
    }

    public static int getClassStatueCell(int classID) {
        int pos = 314;
        switch (classID) {
            case 1:
                return 299;
            case 2:
                return 311;
            case 3:
                return 255;
            case 4:
                return 282;
            case 5:
                return 326;
            case 6:
                return 300;
            case 7:
                return 207;
            case 8:
                return 282;
            case 9:
                return 368;
            case 10:
                return 370;
            case 11:
                return 197;
            case 12:
                return 384;
            case 13:
                return 282;
        }
        return pos;
    }

    public static short getStartMap(int classID) {
        short pos = 10298;
        switch (classID) {
            case Constant.CLASS_FECA:
                pos = 10300;
                break;
            case Constant.CLASS_OSAMODAS:
                pos = 10284;
                break;
            case Constant.CLASS_ENUTROF:
                pos = 10299;
                break;
            case Constant.CLASS_SRAM:
                pos = 10285;
                break;
            case Constant.CLASS_XELOR:
                pos = 10298;
                break;
            case Constant.CLASS_ECAFLIP:
                pos = 10276;
                break;
            case Constant.CLASS_ENIRIPSA:
                pos = 10283;
                break;
            case Constant.CLASS_IOP:
                pos = 10294;
                break;
            case Constant.CLASS_CRA:
                pos = 10292;
                break;
            case Constant.CLASS_SADIDA:
                pos = 10279;
                break;
            case Constant.CLASS_SACRIEUR:
                pos = 10296;
                break;
            case Constant.CLASS_PANDAWA:
                pos = 10289;
                break;
        }

        return pos;
    }

    public static int getStartCell(int classID) {
        int pos = 314;
        switch (classID) {
            case Constant.CLASS_FECA:
                pos = 323;
                break;
            case Constant.CLASS_OSAMODAS:
                pos = 372;
                break;
            case Constant.CLASS_ENUTROF:
                pos = 271;
                break;
            case Constant.CLASS_SRAM:
                pos = 263;
                break;
            case Constant.CLASS_XELOR:
                pos = 300;
                break;
            case Constant.CLASS_ECAFLIP:
                pos = 296;
                break;
            case Constant.CLASS_ENIRIPSA:
                pos = 299;
                break;
            case Constant.CLASS_IOP:
                pos = 280;
                break;
            case Constant.CLASS_CRA:
                pos = 284;
                break;
            case Constant.CLASS_SADIDA:
                pos = 254;
                break;
            case Constant.CLASS_SACRIEUR:
                pos = 243;
                break;
            case Constant.CLASS_PANDAWA:
                pos = 236;
                break;
        }
        return pos;
    }

    public static HashMap<Integer, String> getStartSortsPlaces(int classID) {
        HashMap<Integer, String> start = new HashMap<Integer, String>();
        switch (classID) {
            case CLASS_FECA:
                start.put(3, "1");//Attaque Naturelle
                start.put(6, "2");//Armure Terrestre
                start.put(17, "3");//Glyphe Agressif
                break;
            case CLASS_SRAM:
                start.put(61, "1");//Sournoiserie
                start.put(72, "2");//Invisibilité
                start.put(65, "3");//Piege sournois
                break;
            case CLASS_ENIRIPSA:
                start.put(125, "1");//Mot Interdit
                start.put(128, "2");//Mot de Frayeur
                start.put(121, "3");//Mot Curatif
                break;
            case CLASS_ECAFLIP:
                start.put(102, "1");//Pile ou Face
                start.put(103, "2");//Chance d'ecaflip
                start.put(105, "3");//Bond du felin
                break;
            case CLASS_CRA:
                start.put(161, "1");//Fleche Magique
                start.put(169, "2");//Fleche de Recul
                start.put(164, "3");//Fleche Empoisonnée(ex Fleche chercheuse)
                break;
            case CLASS_IOP:
                start.put(143, "1");//Intimidation
                start.put(141, "2");//Pression
                start.put(142, "3");//Bond
                break;
            case CLASS_SADIDA:
                start.put(183, "1");//Ronce
                start.put(200, "2");//Poison Paralysant
                start.put(193, "3");//La bloqueuse
                break;
            case CLASS_OSAMODAS:
                start.put(34, "1");//Invocation de tofu
                start.put(21, "2");//Griffe Spectrale
                start.put(23, "3");//Cri de l'ours
                break;
            case CLASS_XELOR:
                start.put(82, "1");//Contre
                start.put(81, "2");//Ralentissement
                start.put(83, "3");//Aiguille
                break;
            case CLASS_PANDAWA:
                start.put(686, "1");//Picole
                start.put(692, "2");//Gueule de bois
                start.put(687, "3");//Poing enflammé
                break;
            case CLASS_ENUTROF:
                start.put(51, "1");//Lancer de Piece
                start.put(43, "2");//Lancer de Pelle
                start.put(41, "3");//Sac animé
                break;
            case CLASS_SACRIEUR:
                start.put(432, "1");//Pied du Sacrieur
                start.put(431, "2");//Chatiment Osé
                start.put(434, "3");//Attirance
                break;
        }
        return start;
    }

    public static HashMap<Integer, SortStats> getStartSorts(int classID) {
        HashMap<Integer, SortStats> start = new HashMap<Integer, SortStats>();
        switch (classID) {
            case CLASS_FECA:
                start.put(3, World.world.getSort(3).getStatsByLevel(1));//Attaque Naturelle
                start.put(6, World.world.getSort(6).getStatsByLevel(1));//Armure Terrestre
                start.put(17, World.world.getSort(17).getStatsByLevel(1));//Glyphe Agressif
                break;
            case CLASS_SRAM:
                start.put(61, World.world.getSort(61).getStatsByLevel(1));//Sournoiserie
                start.put(72, World.world.getSort(72).getStatsByLevel(1));//Invisibilité
                start.put(65, World.world.getSort(65).getStatsByLevel(1));//Piege sournois
                break;
            case CLASS_ENIRIPSA:
                start.put(125, World.world.getSort(125).getStatsByLevel(1));//Mot Interdit
                start.put(128, World.world.getSort(128).getStatsByLevel(1));//Mot de Frayeur
                start.put(121, World.world.getSort(121).getStatsByLevel(1));//Mot Curatif
                break;
            case CLASS_ECAFLIP:
                start.put(102, World.world.getSort(102).getStatsByLevel(1));//Pile ou Face
                start.put(103, World.world.getSort(103).getStatsByLevel(1));//Chance d'ecaflip
                start.put(105, World.world.getSort(105).getStatsByLevel(1));//Bond du felin
                break;
            case CLASS_CRA:
                start.put(161, World.world.getSort(161).getStatsByLevel(1));//Fleche Magique
                start.put(169, World.world.getSort(169).getStatsByLevel(1));//Fleche de Recul
                start.put(164, World.world.getSort(164).getStatsByLevel(1));//Fleche Empoisonnée(ex Fleche chercheuse)
                break;
            case CLASS_IOP:
                start.put(143, World.world.getSort(143).getStatsByLevel(1));//Intimidation
                start.put(141, World.world.getSort(141).getStatsByLevel(1));//Pression
                start.put(142, World.world.getSort(142).getStatsByLevel(1));//Bond
                break;
            case CLASS_SADIDA:
                start.put(183, World.world.getSort(183).getStatsByLevel(1));//Ronce
                start.put(200, World.world.getSort(200).getStatsByLevel(1));//Poison Paralysant
                start.put(193, World.world.getSort(193).getStatsByLevel(1));//La bloqueuse
                break;
            case CLASS_OSAMODAS:
                start.put(34, World.world.getSort(34).getStatsByLevel(1));//Invocation de tofu
                start.put(21, World.world.getSort(21).getStatsByLevel(1));//Griffe Spectrale
                start.put(23, World.world.getSort(23).getStatsByLevel(1));//Cri de l'ours
                break;
            case CLASS_XELOR:
                start.put(82, World.world.getSort(82).getStatsByLevel(1));//Contre
                start.put(81, World.world.getSort(81).getStatsByLevel(1));//Ralentissement
                start.put(83, World.world.getSort(83).getStatsByLevel(1));//Aiguille
                break;
            case CLASS_PANDAWA:
                start.put(686, World.world.getSort(686).getStatsByLevel(1));//Picole
                start.put(692, World.world.getSort(692).getStatsByLevel(1));//Gueule de bois
                start.put(687, World.world.getSort(687).getStatsByLevel(1));//Poing enflammé
                break;
            case CLASS_ENUTROF:
                start.put(51, World.world.getSort(51).getStatsByLevel(1));//Lancer de Piece
                start.put(43, World.world.getSort(43).getStatsByLevel(1));//Lancer de Pelle
                start.put(41, World.world.getSort(41).getStatsByLevel(1));//Sac animé
                break;
            case CLASS_SACRIEUR:
                start.put(432, World.world.getSort(432).getStatsByLevel(1));//Pied du Sacrieur
                start.put(431, World.world.getSort(431).getStatsByLevel(1));//Chatiment Forcé
                start.put(434, World.world.getSort(434).getStatsByLevel(1));//Attirance
                break;
        }
        return start;
    }

    public static int getReqPtsToBoostStatsByClass(int classID, int statID,
                                                   int val) {
        switch (statID) {
            case 11://Vita
                return 1;
            case 12://Sage
                return 3;
            case 10://Force
                switch (classID) {
                    case CLASS_SACRIEUR:
                        return 3;

                    case CLASS_FECA:
                    case CLASS_ENIRIPSA:
                    case CLASS_OSAMODAS:
                    case CLASS_XELOR:
                        if (val < 50)
                            return 2;
                        if (val < 150)
                            return 3;
                        if (val < 250)
                            return 4;
                        return 5;

                    case CLASS_SRAM:
                    case CLASS_IOP:
                    case CLASS_ECAFLIP:
                        if (val < 100)
                            return 1;
                        if (val < 200)
                            return 2;
                        if (val < 300)
                            return 3;
                        if (val < 400)
                            return 4;
                        return 5;

                    case CLASS_PANDAWA:
                        if (val < 50)
                            return 1;
                        if (val < 200)
                            return 2;
                        return 3;

                    case CLASS_SADIDA:
                        if (val < 50)
                            return 1;
                        if (val < 250)
                            return 2;
                        if (val < 300)
                            return 3;
                        if (val < 400)
                            return 4;
                        return 5;

                    case CLASS_CRA:
                    case CLASS_ENUTROF:
                        if (val < 50)
                            return 1;
                        if (val < 150)
                            return 2;
                        if (val < 250)
                            return 3;
                        if (val < 350)
                            return 4;
                        return 5;
                }
                break;
            case 13://Chance
                switch (classID) {
                    case CLASS_FECA:
                    case CLASS_XELOR:
                    case CLASS_SRAM:
                    case CLASS_IOP:
                    case CLASS_ECAFLIP:
                    case CLASS_ENIRIPSA:
                    case CLASS_CRA:
                        if (val < 20)
                            return 1;
                        if (val < 40)
                            return 2;
                        if (val < 60)
                            return 3;
                        if (val < 80)
                            return 4;
                        return 5;

                    case CLASS_SACRIEUR:
                        return 3;

                    case CLASS_SADIDA:
                    case CLASS_OSAMODAS:
                        if (val < 100)
                            return 1;
                        if (val < 200)
                            return 2;
                        if (val < 300)
                            return 3;
                        if (val < 400)
                            return 4;
                        return 5;

                    case CLASS_PANDAWA:
                        if (val < 50)
                            return 1;
                        if (val < 200)
                            return 2;
                        return 3;

                    case CLASS_ENUTROF:
                        if (val < 100)
                            return 1;
                        if (val < 150)
                            return 2;
                        if (val < 230)
                            return 3;
                        if (val < 330)
                            return 4;
                        return 5;
                }
                break;
            case 14://Agilité
                switch (classID) {
                    case CLASS_FECA:
                    case CLASS_XELOR:
                    case CLASS_SADIDA:
                    case CLASS_ENIRIPSA:
                    case CLASS_ENUTROF:
                    case CLASS_IOP:
                    case CLASS_OSAMODAS:
                        if (val < 20)
                            return 1;
                        if (val < 40)
                            return 2;
                        if (val < 60)
                            return 3;
                        if (val < 80)
                            return 4;
                        return 5;

                    case CLASS_SACRIEUR:
                        return 3;

                    case CLASS_SRAM:
                        if (val < 100)
                            return 1;
                        if (val < 200)
                            return 2;
                        if (val < 300)
                            return 3;
                        if (val < 400)
                            return 4;
                        return 5;

                    case CLASS_PANDAWA:
                        if (val < 50)
                            return 1;
                        if (val < 200)
                            return 2;
                        return 3;

                    case CLASS_ECAFLIP:

                    case CLASS_CRA:
                        if (val < 50)
                            return 1;
                        if (val < 100)
                            return 2;
                        if (val < 150)
                            return 3;
                        if (val < 200)
                            return 4;
                        return 5;
                }
                break;
            case 15://Intelligence
                switch (classID) {
                    case CLASS_XELOR:
                    case CLASS_FECA:
                    case CLASS_SADIDA:
                    case CLASS_ENIRIPSA:
                    case CLASS_OSAMODAS:
                        if (val < 100)
                            return 1;
                        if (val < 200)
                            return 2;
                        if (val < 300)
                            return 3;
                        if (val < 400)
                            return 4;
                        return 5;

                    case CLASS_SACRIEUR:
                        return 3;

                    case CLASS_SRAM:
                        if (val < 50)
                            return 2;
                        if (val < 150)
                            return 3;
                        if (val < 250)
                            return 4;
                        return 5;

                    case CLASS_ENUTROF:
                        if (val < 20)
                            return 1;
                        if (val < 60)
                            return 2;
                        if (val < 100)
                            return 3;
                        if (val < 140)
                            return 4;
                        return 5;

                    case CLASS_PANDAWA:
                        if (val < 50)
                            return 1;
                        if (val < 200)
                            return 2;
                        return 3;

                    case CLASS_IOP:
                    case CLASS_ECAFLIP:
                        if (val < 20)
                            return 1;
                        if (val < 40)
                            return 2;
                        if (val < 60)
                            return 3;
                        if (val < 80)
                            return 4;
                        return 5;

                    case CLASS_CRA:
                        if (val < 50)
                            return 1;
                        if (val < 150)
                            return 2;
                        if (val < 250)
                            return 3;
                        if (val < 350)
                            return 4;
                        return 5;
                }
                break;
        }
        return 5;
    }

    public static void onLevelUpSpells(final Player perso, final int lvl, final boolean save) {

        switch (perso.getClasse()) {

            case CLASS_FECA:

                if (lvl == 3)

                    perso.learnSpell(4, 1, save, false, false);//Renvoie de sort

                else if (lvl == 6)

                    perso.learnSpell(2, 1, save, false, false);//Aveuglement

                else if (lvl == 9)

                    perso.learnSpell(1, 1, save, false, false);//Armure Incandescente

                else if (lvl == 13)

                    perso.learnSpell(9, 1, save, false, false);//Attaque nuageuse

                else if (lvl == 17)

                    perso.learnSpell(18, 1, save, false, false);//Armure Aqueuse

                else if (lvl == 21)

                    perso.learnSpell(20, 1, save, false, false);//Immunité

                else if (lvl == 26)

                    perso.learnSpell(14, 1, save, false, false);//Armure Venteuse

                else if (lvl == 31)

                    perso.learnSpell(19, 1, save, false, false);//Bulle

                else if (lvl == 36)

                    perso.learnSpell(5, 1, save, false, false);//Trêve

                else if (lvl == 42)

                    perso.learnSpell(16, 1, save, false, false);//Science du bâton

                else if (lvl == 48)

                    perso.learnSpell(8, 1, save, false, false);// falseur du bâton

                else if (lvl == 54)

                    perso.learnSpell(12, 1, save, false, false);//glyphe d'Aveuglement

                else if (lvl == 60)

                    perso.learnSpell(11, 1, save, false, false);//Téléportation

                else if (lvl == 70)

                    perso.learnSpell(10, 1, save, false, false);//Glyphe Enflammé

                else if (lvl == 80)

                    perso.learnSpell(7, 1, save, false, false);//Bouclier Féca

                else if (lvl == 90)

                    perso.learnSpell(15, 1, save, false, false);//Glyphe d'Immobilisation

                else if (lvl == 100)

                    perso.learnSpell(13, 1, save, false, false);//Glyphe de Silence

                else if (lvl == 200)

                    perso.learnSpell(1901, 1, save, false, false);//Invocation de Dopeul Féca

                break;



            case CLASS_OSAMODAS:

                if (lvl == 3)

                    perso.learnSpell(26, 1, save, false, false);//Bénédiction Animale

                else if (lvl == 6)

                    perso.learnSpell(22, 1, save, false, false);//Déplacement Félin

                else if (lvl == 9)

                    perso.learnSpell(35, 1, save, false, false);//Invocation de Bouftou

                else if (lvl == 13)

                    perso.learnSpell(28, 1, save, false, false);//Crapaud

                else if (lvl == 17)

                    perso.learnSpell(37, 1, save, false, false);//Invocation de Prespic

                else if (lvl == 21)

                    perso.learnSpell(30, 1, save, false, false);//Fouet

                else if (lvl == 26)

                    perso.learnSpell(27, 1, save, false, false);//Piqûre Motivante

                else if (lvl == 31)

                    perso.learnSpell(24, 1, save, false, false);//Corbeau

                else if (lvl == 36)

                    perso.learnSpell(33, 1, save, false, false);//Grelse iffe Cinglante

                else if (lvl == 42)

                    perso.learnSpell(25, 1, save, false, false);//Soin Animal

                else if (lvl == 48)

                    perso.learnSpell(38, 1, save, false, false);//Invocation de Sanglier

                else if (lvl == 54)

                    perso.learnSpell(36, 1, save, false, false);//Frappe du Craqueleur

                else if (lvl == 60)

                    perso.learnSpell(32, 1, save, false, false);//Résistance Naturelle

                else if (lvl == 70)

                    perso.learnSpell(29, 1, save, false, false);//Crocs du Mulou

                else if (lvl == 80)

                    perso.learnSpell(39, 1, save, false, false);//Invocation de Bwork Mage

                else if (lvl == 90)

                    perso.learnSpell(40, 1, save, false, false);//Invocation de Craqueleur

                else if (lvl == 100)

                    perso.learnSpell(31, 1, save, false, false);//Invocation de Dragonnet Rouge

                else if (lvl == 200)

                    perso.learnSpell(1902, 1, save, false, false);//Invocation de Dopeul Osamodas

                break;



            case CLASS_ENUTROF:

                if (lvl == 3)

                    perso.learnSpell(49, 1, save, false, false);//Pelle Fantomatique

                else if (lvl == 6)

                    perso.learnSpell(42, 1, save, false, false);//Chance

                else if (lvl == 9)

                    perso.learnSpell(47, 1, save, false, false);//Boîte de Pandore

                else if (lvl == 13)

                    perso.learnSpell(48, 1, save, false, false);//Remblai

                else if (lvl == 17)

                    perso.learnSpell(45, 1, save, false, false);//Clé Réductrice

                else if (lvl == 21)

                    perso.learnSpell(53, 1, save, false, false);//Force de l'Age

                else if (lvl == 26)

                    perso.learnSpell(46, 1, save, false, false);//Désinvocation

                else if (lvl == 31)

                    perso.learnSpell(52, 1, save, false, false);//Cupidité

                else if (lvl == 36)

                    perso.learnSpell(44, 1, save, false, false);//Roulage de Pelle

                else if (lvl == 42)

                    perso.learnSpell(50, 1, save, false, false);//Maladresse

                else if (lvl == 48)

                    perso.learnSpell(54, 1, save, false, false);//Maladresse de Masse

                else if (lvl == 54)

                    perso.learnSpell(55, 1, save, false, false);//Accélération

                else if (lvl == 60)

                    perso.learnSpell(56, 1, save, false, false);//Pelle du Jugement

                else if (lvl == 70)

                    perso.learnSpell(58, 1, save, false, false);//Pelle Massacrante

                else if (lvl == 80)

                    perso.learnSpell(59, 1, save, false, false);//Corruption

                else if (lvl == 90)

                    perso.learnSpell(57, 1, save, false, false);//Pelle Animée

                else if (lvl == 100)

                    perso.learnSpell(60, 1, save, false, false);//Coffre Animé

                else if (lvl == 200)

                    perso.learnSpell(1903, 1, save, false, false);//Invocation de Dopeul Enutrof

                break;



            case CLASS_SRAM:

                if (lvl == 3)

                    perso.learnSpell(66, 1, save, false, false);//Poison insidieux

                else if (lvl == 6)

                    perso.learnSpell(68, 1, save, false, false);//Fourvoiement

                else if (lvl == 9)

                    perso.learnSpell(63, 1, save, false, false);//Coup Sournois

                else if (lvl == 13)

                    perso.learnSpell(74, 1, save, false, false);//Double

                else if (lvl == 17)

                    perso.learnSpell(64, 1, save, false, false);//Repérage

                else if (lvl == 21)

                    perso.learnSpell(79, 1, save, false, false);//Piège de Masse

                else if (lvl == 26)

                    perso.learnSpell(78, 1, save, false, false);//Invisibilité d'Autrui

                else if (lvl == 31)

                    perso.learnSpell(71, 1, save, false, false);//Piège Empoisonné

                else if (lvl == 36)

                    perso.learnSpell(62, 1, save, false, false);//Concentration de Chakra

                else if (lvl == 42)

                    perso.learnSpell(69, 1, save, false, false);//Piège d'Immobilisation

                else if (lvl == 48)

                    perso.learnSpell(77, 1, save, false, false);//Piège de Silence

                else if (lvl == 54)

                    perso.learnSpell(73, 1, save, false, false);//Piège répulsif

                else if (lvl == 60)

                    perso.learnSpell(67, 1, save, false, false);//Peur

                else if (lvl == 70)

                    perso.learnSpell(70, 1, save, false, false);//Arnaque

                else if (lvl == 80)

                    perso.learnSpell(75, 1, save, false, false);//Pulsion de Chakra

                else if (lvl == 90)

                    perso.learnSpell(76, 1, save, false, false);//Attaque Mortelle

                else if (lvl == 100)

                    perso.learnSpell(80, 1, save, false, false);//Piège Mortel

                else if (lvl == 200)

                    perso.learnSpell(1904, 1, save, false, false);//Invocation de Dopeul Sram

                break;



            case CLASS_XELOR:

                if (lvl == 3)

                    perso.learnSpell(84, 1, save, false, false);//Gelure

                else if (lvl == 6)

                    perso.learnSpell(100, 1, save, false, false);//Sablier de Xélor

                else if (lvl == 9)

                    perso.learnSpell(92, 1, save, false, false);//Rayon Obscur

                else if (lvl == 13)

                    perso.learnSpell(88, 1, save, false, false);//Téléportation

                else if (lvl == 17)

                    perso.learnSpell(93, 1, save, false, false);//Flétrissement

                else if (lvl == 21)

                    perso.learnSpell(85, 1, save, false, false);//Flou

                else if (lvl == 26)

                    perso.learnSpell(96, 1, save, false, false);//Poussière Temporelle

                else if (lvl == 31)

                    perso.learnSpell(98, 1, save, false, false);//Vol du Temps

                else if (lvl == 36)

                    perso.learnSpell(86, 1, save, false, false);//Aiguille Chercheuse

                else if (lvl == 42)

                    perso.learnSpell(89, 1, save, false, false);//Dévouement

                else if (lvl == 48)

                    perso.learnSpell(90, 1, save, false, false);//Fuite

                else if (lvl == 54)

                    perso.learnSpell(87, 1, save, false, false);//Démotivation

                else if (lvl == 60)

                    perso.learnSpell(94, 1, save, false, false);//Protection Aveuglante

                else if (lvl == 70)

                    perso.learnSpell(99, 1, save, false, false);//Momelse ification

                else if (lvl == 80)

                    perso.learnSpell(95, 1, save, false, false);//Horloge

                else if (lvl == 90)

                    perso.learnSpell(91, 1, save, false, false);//Frappe de Xélor

                else if (lvl == 100)

                    perso.learnSpell(97, 1, save, false, false);//Cadran de Xélor

                else if (lvl == 200)

                    perso.learnSpell(1905, 1, save, false, false);//Invocation de Dopeul Xélor

                break;



            case CLASS_ECAFLIP:

                if (lvl == 3)

                    perso.learnSpell(109, 1, save, false, false);//Bluff

                else if (lvl == 6)

                    perso.learnSpell(113, 1, save, false, false);//Perception

                else if (lvl == 9)

                    perso.learnSpell(111, 1, save, false, false);//Contrecoup

                else if (lvl == 13)

                    perso.learnSpell(104, 1, save, false, false);//Trèfle

                else if (lvl == 17)

                    perso.learnSpell(119, 1, save, false, false);//Tout ou rien

                else if (lvl == 21)

                    perso.learnSpell(101, 1, save, false, false);//Roulette

                else if (lvl == 26)

                    perso.learnSpell(107, 1, save, false, false);//Topkaj

                else if (lvl == 31)

                    perso.learnSpell(116, 1, save, false, false);//Langue Râpeuse

                else if (lvl == 36)

                    perso.learnSpell(106, 1, save, false, false);//Roue de la Fortune

                else if (lvl == 42)

                    perso.learnSpell(117, 1, save, false, false);//Grelse iffe Invocatrice

                else if (lvl == 48)

                    perso.learnSpell(108, 1, save, false, false);//Esprit Félin

                else if (lvl == 54)

                    perso.learnSpell(115, 1, save, false, false);//Odorat

                else if (lvl == 60)

                    perso.learnSpell(118, 1, save, false, false);//Réflexes

                else if (lvl == 70)

                    perso.learnSpell(110, 1, save, false, false);//Grelse iffe Joueuse

                else if (lvl == 80)

                    perso.learnSpell(112, 1, save, false, false);//Grelse iffe de Ceangal

                else if (lvl == 90)

                    perso.learnSpell(114, 1, save, false, false);//Rekop

                else if (lvl == 100)

                    perso.learnSpell(120, 1, save, false, false);//Destin d'Ecaflip

                else if (lvl == 200)

                    perso.learnSpell(1906, 1, save, false, false);//Invocation de Dopeul Ecaflip

                break;



            case CLASS_ENIRIPSA:

                if (lvl == 3)

                    perso.learnSpell(124, 1, save, false, false);//Mot Soignant

                else if (lvl == 6)

                    perso.learnSpell(122, 1, save, false, false);//Mot Blessant

                else if (lvl == 9)

                    perso.learnSpell(126, 1, save, false, false);//Mot Stimulant

                else if (lvl == 13)

                    perso.learnSpell(127, 1, save, false, false);//Mot de Prévention

                else if (lvl == 17)

                    perso.learnSpell(123, 1, save, false, false);//Mot Drainant

                else if (lvl == 21)

                    perso.learnSpell(130, 1, save, false, false);//Mot Revitalisant

                else if (lvl == 26)

                    perso.learnSpell(131, 1, save, false, false);//Mot de Régénération

                else if (lvl == 31)

                    perso.learnSpell(132, 1, save, false, false);//Mot d'Epine

                else if (lvl == 36)

                    perso.learnSpell(133, 1, save, false, false);//Mot de Jouvence

                else if (lvl == 42)

                    perso.learnSpell(134, 1, save, false, false);//Mot Vampirique

                else if (lvl == 48)

                    perso.learnSpell(135, 1, save, false, false);//Mot de Sacrelse ifice

                else if (lvl == 54)

                    perso.learnSpell(129, 1, save, false, false);//Mot d'Amitié

                else if (lvl == 60)

                    perso.learnSpell(136, 1, save, false, false);//Mot d'Immobilisation

                else if (lvl == 70)

                    perso.learnSpell(137, 1, save, false, false);//Mot d'Envol

                else if (lvl == 80)

                    perso.learnSpell(138, 1, save, false, false);//Mot de Silence

                else if (lvl == 90)

                    perso.learnSpell(139, 1, save, false, false);//Mot d'Altruisme

                else if (lvl == 100)

                    perso.learnSpell(140, 1, save, false, false);//Mot de Reconstitution

                else if (lvl == 200)

                    perso.learnSpell(1907, 1, save, false, false);//Invocation de Dopeul Eniripsa

                break;



            case CLASS_IOP:

                if (lvl == 3)

                    perso.learnSpell(144, 1, save, false, false);//Compulsion

                else if (lvl == 6)

                    perso.learnSpell(145, 1, save, false, false);//Epée Divine

                else if (lvl == 9)

                    perso.learnSpell(146, 1, save, false, false);//Epée du Destin

                else if (lvl == 13)

                    perso.learnSpell(147, 1, save, false, false);//Guide de Bravoure

                else if (lvl == 17)

                    perso.learnSpell(148, 1, save, false, false);//Amplelse ification

                else if (lvl == 21)

                    perso.learnSpell(154, 1, save, false, false);//Epée Destructrice

                else if (lvl == 26)

                    perso.learnSpell(150, 1, save, false, false);//Couper

                else if (lvl == 31)

                    perso.learnSpell(151, 1, save, false, false);//Souffle

                else if (lvl == 36)

                    perso.learnSpell(155, 1, save, false, false);//Vitalité

                else if (lvl == 42)

                    perso.learnSpell(152, 1, save, false, false);//Epée du Jugement

                else if (lvl == 48)

                    perso.learnSpell(153, 1, save, false, false);//Puissance

                else if (lvl == 54)

                    perso.learnSpell(149, 1, save, false, false);//Mutilation

                else if (lvl == 60)

                    perso.learnSpell(156, 1, save, false, false);//Tempête de Puissance

                else if (lvl == 70)

                    perso.learnSpell(157, 1, save, false, false);//Epée Céleste

                else if (lvl == 80)

                    perso.learnSpell(158, 1, save, false, false);//Concentration

                else if (lvl == 90)

                    perso.learnSpell(160, 1, save, false, false);//Epée de Iop

                else if (lvl == 100)

                    perso.learnSpell(159, 1, save, false, false);//Colère de Iop

                else if (lvl == 200)

                    perso.learnSpell(1908, 1, save, false, false);//Invocation de Dopeul Iop

                break;



            case CLASS_CRA:

                if (lvl == 3)

                    perso.learnSpell(163, 1, save, false, false);//Flèche Glacée

                else if (lvl == 6)

                    perso.learnSpell(165, 1, save, false, false);//Flèche enflammée

                else if (lvl == 9)

                    perso.learnSpell(172, 1, save, false, false);//Tir Eloigné

                else if (lvl == 13)

                    perso.learnSpell(167, 1, save, false, false);//Flèche d'Expiation

                else if (lvl == 17)

                    perso.learnSpell(168, 1, save, false, false);//Oeil de Taupe

                else if (lvl == 21)

                    perso.learnSpell(162, 1, save, false, false);//Tir Critique

                else if (lvl == 26)

                    perso.learnSpell(170, 1, save, false, false);//Flèche d'Immobilisation

                else if (lvl == 31)

                    perso.learnSpell(171, 1, save, false, false);//Flèche Punitive

                else if (lvl == 36)

                    perso.learnSpell(166, 1, save, false, false);//Tir Puissant

                else if (lvl == 42)

                    perso.learnSpell(173, 1, save, false, false);//Flèche Harcelante

                else if (lvl == 48)

                    perso.learnSpell(174, 1, save, false, false);//Flèche Cinglante

                else if (lvl == 54)


                    perso.learnSpell(176, 1, save, false, false);//Flèche Persécutrice

                else if (lvl == 60)

                    perso.learnSpell(175, 1, save, false, false);//Flèche Destructrice

                else if (lvl == 70)

                    perso.learnSpell(178, 1, save, false, false);//Flèche Absorbante

                else if (lvl == 80)

                    perso.learnSpell(177, 1, save, false, false);//Flèche Ralentissante

                else if (lvl == 90)

                    perso.learnSpell(179, 1, save, false, false);//Flèche Explosive

                else if (lvl == 100)

                    perso.learnSpell(180, 1, save, false, false);//Maîtrise de l'Arc

                else if (lvl == 200)

                    perso.learnSpell(1909, 1, save, false, false);//Invocation de Dopeul Cra

                break;



            case CLASS_SADIDA:

                if (lvl == 3)

                    perso.learnSpell(198, 1, save, false, false);//Sacrifice Poupesque

                else if (lvl == 6)

                    perso.learnSpell(195, 1, save, false, false);//Larme

                else if (lvl == 9)

                    perso.learnSpell(182, 1, save, false, false);//Invocation de la Folle

                else if (lvl == 13)

                    perso.learnSpell(192, 1, save, false, false);//Ronce Apaisante

                else if (lvl == 17)

                    perso.learnSpell(197, 1, save, false, false);//Puissance Sylvestre

                else if (lvl == 21)

                    perso.learnSpell(189, 1, save, false, false);//Invocation de la Sacrifiée

                else if (lvl == 26)

                    perso.learnSpell(181, 1, save, false, false);//Tremblement

                else if (lvl == 31)

                    perso.learnSpell(199, 1, save, false, false);//Connaissance des Poupées

                else if (lvl == 36)

                    perso.learnSpell(191, 1, save, false, false);//Ronce Multiples

                else if (lvl == 42)

                    perso.learnSpell(186, 1, save, false, false);//Arbre

                else if (lvl == 48)

                    perso.learnSpell(196, 1, save, false, false);//Vent Empoisonné

                else if (lvl == 54)

                    perso.learnSpell(190, 1, save, false, false);//Invocation de la Gonflable

                else if (lvl == 60)

                    perso.learnSpell(194, 1, save, false, false);//Ronces Agressives

                else if (lvl == 70)

                    perso.learnSpell(185, 1, save, false, false);//Herbe Folle

                else if (lvl == 80)

                    perso.learnSpell(184, 1, save, false, false);//Feu de Brousse

                else if (lvl == 90)

                    perso.learnSpell(188, 1, save, false, false);//Ronce Insolente

                else if (lvl == 100)

                    perso.learnSpell(187, 1, save, false, false);//Invocation de la Surpuissante

                else if (lvl == 200)

                    perso.learnSpell(1910, 1, save, false, false);//Invocation de Dopeul Sadida

                break;



            case CLASS_SACRIEUR:

                if (lvl == 3)

                    perso.learnSpell(444, 1, save, false, false);//Dérobade

                else if (lvl == 6)

                    perso.learnSpell(449, 1, save, false, false);//Détour

                else if (lvl == 9)

                    perso.learnSpell(436, 1, save, false, false);//Assaut

                else if (lvl == 13)

                    perso.learnSpell(437, 1, save, false, false);//Châtiment Agile

                else if (lvl == 17)

                    perso.learnSpell(439, 1, save, false, false);//Dissolution

                else if (lvl == 21)

                    perso.learnSpell(433, 1, save, false, false);//Châtiment Osé

                else if (lvl == 26)

                    perso.learnSpell(443, 1, save, false, false);//Châtiment Spirituel

                else if (lvl == 31)

                    perso.learnSpell(440, 1, save, false, false);//Sacrelse ifice

                else if (lvl == 36)

                    perso.learnSpell(442, 1, save, false, false);//Absorption

                else if (lvl == 42)

                    perso.learnSpell(441, 1, save, false, false);//Châtiment Vilatesque

                else if (lvl == 48)

                    perso.learnSpell(445, 1, save, false, false);//Coopération

                else if (lvl == 54)

                    perso.learnSpell(438, 1, save, false, false);//Transposition

                else if (lvl == 60)

                    perso.learnSpell(446, 1, save, false, false);//Punition

                else if (lvl == 70)

                    perso.learnSpell(447, 1, save, false, false);//Furie

                else if (lvl == 80)

                    perso.learnSpell(448, 1, save, false, false);//Epée Volante

                else if (lvl == 90)

                    perso.learnSpell(435, 1, save, false, false);//Tansfert de Vie

                else if (lvl == 100)

                    perso.learnSpell(450, 1, save, false, false);//Folie Sanguinaire

                else if (lvl == 200)

                    perso.learnSpell(1911, 1, save, false, false);//Invocation de Dopeul Sacrieur

                break;



            case CLASS_PANDAWA:

                if (lvl == 3)

                    perso.learnSpell(689, 1, save, false, false);//Epouvante

                else if (lvl == 6)

                    perso.learnSpell(690, 1, save, false, false);//Souffle Alcoolisé

                else if (lvl == 9)

                    perso.learnSpell(691, 1, save, false, false);//Vulnérabilité Aqueuse

                else if (lvl == 13)

                    perso.learnSpell(688, 1, save, false, false);//Vulnérabilité Incandescente

                else if (lvl == 17)

                    perso.learnSpell(693, 1, save, false, false);//Karcham

                else if (lvl == 21)

                    perso.learnSpell(694, 1, save, false, false);//Vulnérabilité Venteuse

                else if (lvl == 26)

                    perso.learnSpell(695, 1, save, false, false);//Stabilisation

                else if (lvl == 31)

                    perso.learnSpell(696, 1, save, false, false);//Chamrak

                else if (lvl == 36)

                    perso.learnSpell(697, 1, save, false, false);//Vulnérabilité Terrestre

                else if (lvl == 42)

                    perso.learnSpell(698, 1, save, false, false);//Souillure

                else if (lvl == 48)

                    perso.learnSpell(699, 1, save, false, false);//Lait de Bambou

                else if (lvl == 54)

                    perso.learnSpell(700, 1, save, false, false);//Vague à Lame

                else if (lvl == 60)

                    perso.learnSpell(701, 1, save, false, false);//Colère de Zatoïshwan

                else if (lvl == 70)

                    perso.learnSpell(702, 1, save, false, false);//Flasque Explosive

                else if (lvl == 80)

                    perso.learnSpell(703, 1, save, false, false);//Pandatak

                else if (lvl == 90)

                    perso.learnSpell(704, 1, save, false, false);//Pandanlku

                else if (lvl == 100)

                    perso.learnSpell(705, 1, save, false, false);//Lien Spiritueux

                else if (lvl == 200)

                    perso.learnSpell(1912, 1, save, false, false);//Invocation de Dopeul Pandawa

                break;

        }

    }

    public static int getGlyphColor(int spell) {
        switch (spell) {
            case 10://Enflammé
            case 2033://Dopeul
                return 4;//Rouge
            case 12://Aveuglement
            case 2034://Dopeul
                return 3;
            case 13://Silence
            case 2035://Dopeul
                return 6;//Bleu
            case 15://Immobilisation
            case 2036://Dopeul
                return 5;//Vert
            case 17://Aggressif
            case 2037://Dopeul
                return 2;
            case 949://Karkargo
                return 0;//Blanc
            //case 476://Blop
            default:
                return 4;
        }
    }

    public static byte getTrapsColor(int spell) {
        switch (spell) {
            case 65://Sournois
                return 7;
            case 69://Immobilisation
                return 10;
            case 71://Empoisonnée
            case 2068://Dopeul
                return 9;
            case 73://Repulsif
                return 12;
            case 77://Silence
            case 2071://Dopeul
                return 11;
            case 79://Masse
            case 2072://Dopeul
                return 8;
            case 80://Mortel
                return 13;
            default:
                return 7;
        }
    }
    
    public static short getTrapsAnimation(int spell) {
    	switch(spell) {
    		case 73: // Repulsif
    			return 409;
    		case 79: // Masse
    			return 410;
    		case 77: // Silence
    			return 411;
    		default:
    			return 407;
    	}
    }

    public static Stats getMountStats(int color, int lvl) {
        Stats stats = new Stats();
        switch (color) {
            //Amande sauvage
            case 1:
                break;
            //Ebene
            case 3:
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                stats.addOneStat(STATS_ADD_AGIL, (int) (lvl / 1.25));//100/1.25 = 80
                break;
            //Rousse |
            case 10:
                stats.addOneStat(STATS_ADD_VITA, lvl); //100/1 = 100
                break;
            //Amande
            case 20:
                stats.addOneStat(STATS_ADD_INIT, lvl * 10); // 100*10 = 1000
                break;
            //Dorée
            case 18:
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                stats.addOneStat(STATS_ADD_SAGE, (int) (lvl / 2.50)); // 100/2.50 = 40
                break;
            //Rousse-Amande
            case 38:
                stats.addOneStat(STATS_ADD_INIT, lvl * 5); // 100*5 = 500
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_CREATURE, lvl / 50); // 100/50 = 2
                break;
            //Rousse-Dorée
            case 46:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_SAGE, lvl / 4); //100/4 = 25
                break;
            //Amande-Dorée
            case 33:
                stats.addOneStat(STATS_ADD_INIT, lvl * 5);
                stats.addOneStat(STATS_ADD_SAGE, lvl / 4);
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                stats.addOneStat(STATS_CREATURE, lvl / 100); // 100/100 = 1
                break;
            //Indigo |
            case 17:
                stats.addOneStat(STATS_ADD_CHAN, (int) (lvl / 1.25));
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                break;
            //Rousse-Indigo
            case 62:
                stats.addOneStat(STATS_ADD_VITA, (int) (lvl * 1.50)); // 100*1.50 = 150
                stats.addOneStat(STATS_ADD_CHAN, (int) (lvl / 1.65));
                break;
            //Rousse-Ebène
            case 12:
                stats.addOneStat(STATS_ADD_VITA, (int) (lvl * 1.50));
                stats.addOneStat(STATS_ADD_AGIL, (int) (lvl / 1.65));
                break;
            //Amande-Indigo
            case 36:
                stats.addOneStat(STATS_ADD_INIT, lvl * 5);
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                stats.addOneStat(STATS_ADD_CHAN, (int) (lvl / 1.65));
                stats.addOneStat(STATS_CREATURE, lvl / 100);
                break;
            //Pourpre | Stade 4
            case 19:
                stats.addOneStat(STATS_ADD_FORC, (int) (lvl / 1.25));
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                break;
            //Orchidée
            case 22:
                stats.addOneStat(STATS_ADD_INTE, (int) (lvl / 1.25));
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                break;
            //Dorée-Orchidée |
            case 48:
                stats.addOneStat(STATS_ADD_VITA, (lvl));
                stats.addOneStat(STATS_ADD_SAGE, lvl / 4);
                stats.addOneStat(STATS_ADD_INTE, (int) (lvl / 1.65));
                break;
            //Indigo-Pourpre
            case 65:
                stats.addOneStat(STATS_ADD_VITA, (lvl));
                stats.addOneStat(STATS_ADD_CHAN, lvl / 2);
                stats.addOneStat(STATS_ADD_FORC, lvl / 2);
                break;
            //Ivoire-Orchidée
            case 67:
                stats.addOneStat(STATS_ADD_VITA, (lvl));
                stats.addOneStat(STATS_ADD_PERDOM, lvl / 2);
                stats.addOneStat(STATS_ADD_INTE, lvl / 2);
                break;
            //Ebène-Pourpre
            case 54:
                stats.addOneStat(STATS_ADD_VITA, (lvl));
                stats.addOneStat(STATS_ADD_FORC, lvl / 2);
                stats.addOneStat(STATS_ADD_AGIL, lvl / 2);
                break;
            //Ebène-Orchidée
            case 53:
                stats.addOneStat(STATS_ADD_VITA, (lvl));
                stats.addOneStat(STATS_ADD_AGIL, lvl / 2);
                stats.addOneStat(STATS_ADD_INTE, lvl / 2);
                break;
            //Pourpre-Orchidée
            case 76:
                stats.addOneStat(STATS_ADD_VITA, (lvl));
                stats.addOneStat(STATS_ADD_INTE, lvl / 2);
                stats.addOneStat(STATS_ADD_FORC, lvl / 2);
                break;
            // Amande-Ebene	| Nami-start
            case 37:
                stats.addOneStat(STATS_ADD_INIT, lvl * 5);
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                stats.addOneStat(STATS_ADD_AGIL, (int) (lvl / 1.65));
                stats.addOneStat(STATS_CREATURE, lvl / 100);
                break;
            // Amande-Rousse
            case 44:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_SAGE, lvl / 4);
                stats.addOneStat(STATS_ADD_CHAN, (int) (lvl / 1.65));
                break;
            // Dorée-Ebène
            case 42:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_SAGE, lvl / 4);
                stats.addOneStat(STATS_ADD_AGIL, (int) (lvl / 1.65));
                break;
            // Indigo-Ebène
            case 51:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_CHAN, lvl / 2);
                stats.addOneStat(STATS_ADD_AGIL, lvl / 2);
                break;
            // Rousse-Pourpre
            case 71:
                stats.addOneStat(STATS_ADD_VITA, (int) (lvl * 1.5));
                stats.addOneStat(STATS_ADD_FORC, (int) (lvl / 1.65));
                break;
            // Rousse-Orchidée
            case 70:
                stats.addOneStat(STATS_ADD_VITA, (int) (lvl * 1.5));
                stats.addOneStat(STATS_ADD_INTE, (int) (lvl / 1.65));
                break;
            // Amande-Pourpre
            case 41:
                stats.addOneStat(STATS_ADD_INIT, lvl * 5);
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                stats.addOneStat(STATS_ADD_FORC, (int) (lvl / 1.65));
                stats.addOneStat(STATS_CREATURE, lvl / 100);
                break;
            // Amande-Orchidée
            case 40:
                stats.addOneStat(STATS_ADD_INIT, lvl * 5);
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                stats.addOneStat(STATS_ADD_INTE, (int) (lvl / 1.65));
                stats.addOneStat(STATS_CREATURE, lvl / 100);
                break;
            // Dorée-Pourpre
            case 49:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_SAGE, lvl / 4);
                stats.addOneStat(STATS_ADD_FORC, (int) (lvl / 1.65));
                break;
            // Ivoire
            case 16:
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                stats.addOneStat(STATS_ADD_PERDOM, lvl / 2);
                break;
            // Turquoise
            case 15:
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                stats.addOneStat(STATS_ADD_PROS, (int) (lvl / 1.25));
                break;
            //Rousse-Ivoire
            case 11:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2); // 100*2 = 200
                stats.addOneStat(STATS_ADD_PERDOM, (int) (lvl / 2.5)); // = 40
                break;
            //Rousse-Turquoise
            case 69:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_PROS, (int) (lvl / 2.50));
                break;
            //Amande-Turquoise
            case 39:
                stats.addOneStat(STATS_ADD_INIT, lvl * 5);
                stats.addOneStat(STATS_ADD_VITA, lvl / 2);
                stats.addOneStat(STATS_ADD_PROS, (int) (lvl / 2.50));
                stats.addOneStat(STATS_CREATURE, lvl / 100);
                break;
            //Dorée-Ivoire
            case 45:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_PERDOM, (int) (lvl / 2.5));
                stats.addOneStat(STATS_ADD_SAGE, lvl / 4);
                break;
            //Dorée-Turquoise
            case 47:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_PROS, (int) (lvl / 2.50));
                stats.addOneStat(STATS_ADD_SAGE, lvl / 4);
                break;
            //Indigo-Ivoire
            case 61:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_CHAN, (int) (lvl / 2.50));
                stats.addOneStat(STATS_ADD_PERDOM, (int) (lvl / 2.5));
                break;
            //Indigo-Turquoise
            case 63:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_CHAN, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PROS, (int) (lvl / 2.5));
                break;
            //Ebène-Ivoire
            case 9:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_AGIL, (int) (lvl / 2.50));
                stats.addOneStat(STATS_ADD_PERDOM, (int) (lvl / 2.5));
                break;
            //Ebène-Turquoise
            case 52:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_AGIL, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PROS, (int) (lvl / 2.50));
                break;
            //Pourpre-Ivoire
            case 68:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_FORC, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PERDOM, (int) (lvl / 2.5));
                break;
            //Pourpre-Turquoise
            case 73:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_FORC, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PROS, (int) (lvl / 2.50));
                break;
            //Orchidée-Turquoise
            case 72:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_INTE, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PERDOM, (int) (lvl / 2.5));
                break;
            //Ivoire-Turquoise
            case 66:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_PERDOM, (int) (lvl / 2.5));
                stats.addOneStat(STATS_ADD_PROS, (int) (lvl / 2.50));
                break;
            // Emeraude
            case 21:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                break;
            // Prune
            case 23:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2); // 100*2 = 200
                stats.addOneStat(STATS_ADD_PO, lvl / 50);
                break;
            //Emeraude-Rousse
            case 57:
                stats.addOneStat(STATS_ADD_VITA, lvl * 3); // 100*3 = 300
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                break;
            //Rousse-Prune
            case 84:
                stats.addOneStat(STATS_ADD_VITA, lvl * 3);
                stats.addOneStat(STATS_ADD_PO, lvl / 100);
                break;
            //Amande-Emeraude
            case 35:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                stats.addOneStat(STATS_CREATURE, lvl / 100);
                stats.addOneStat(STATS_ADD_INIT, lvl * 5);
                break;
            //Amande-Prune
            case 77:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_INIT, lvl * 5);
                stats.addOneStat(STATS_ADD_PO, lvl / 100);
                stats.addOneStat(STATS_CREATURE, lvl / 100);
                break;
            //Dorée-Emeraude
            case 43:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_SAGE, lvl / 4);
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                break;
            //Dorée-Prune
            case 78:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_SAGE, lvl / 4);
                stats.addOneStat(STATS_ADD_PO, lvl / 100);
                break;
            //Indigo-Emeraude
            case 55:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_CHAN, (int) (lvl / 3.33));
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                break;
            //Indigo-Prune
            case 82:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_CHAN, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PO, lvl / 100);
                break;
            //Ebène-Emeraude
            case 50:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_AGIL, (int) (lvl / 3.33));
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                break;
            //Ebène-Prune
            case 79:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_AGIL, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PO, lvl / 100);
                break;
            //Pourpre-Emeraude
            case 60:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_FORC, (int) (lvl / 3.33));
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                break;
            //Pourpre-Prune
            case 87:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_FORC, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PO, lvl / 100);
                break;
            //Orchidée-Emeraude
            case 59:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_INTE, (int) (lvl / 3.33));
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                break;
            //Orchidée-Prune
            case 86:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_INTE, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PO, lvl / 100);
                break;
            //Ivoire-Emeraude
            case 56:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_PERDOM, (int) (lvl / 3.33));
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                break;
            //Ivoire-Prune
            case 83:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_PERDOM, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PO, lvl / 100);
                break;
            //Turquoise-Emeraude
            case 58:
                stats.addOneStat(STATS_ADD_VITA, lvl);
                stats.addOneStat(STATS_ADD_PROS, (int) (lvl / 3.33));
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                break;
            //Turquoise-Prune
            case 85:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_PROS, (int) (lvl / 1.65));
                stats.addOneStat(STATS_ADD_PO, lvl / 100);
                break;
            //Emeraude-Prune
            case 80:
                stats.addOneStat(STATS_ADD_VITA, lvl * 2);
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                stats.addOneStat(STATS_ADD_PO, lvl / 100);
                break;
            //Armure
            case 88:
            case 75:
                stats.addOneStat(STATS_ADD_PERDOM, lvl / 2);
                stats.addOneStat(STATS_ADD_RP_AIR, lvl / 20);
                stats.addOneStat(STATS_ADD_RP_EAU, lvl / 20);
                stats.addOneStat(STATS_ADD_RP_TER, lvl / 20);
                stats.addOneStat(STATS_ADD_RP_FEU, lvl / 20);
                stats.addOneStat(STATS_ADD_RP_NEU, lvl / 20);
                break;
            //Tabi
            case 90:
                stats.addOneStat(STATS_ADD_PERDOM, lvl / 2);
                stats.addOneStat(STATS_ADD_PA, lvl / 100);
                break;
            //Karnage
            case 91:
                stats.addOneStat(STATS_ADD_DOMA, lvl / 10);
                stats.addOneStat(STATS_ADD_PM, lvl / 100);
                break;
        }
        return stats;
    }

    public static ObjectTemplate getParchoTemplateByMountColor(int color) {
        switch (color) {
            //Ammande sauvage
            case 2:
                return World.world.getObjTemplate(7807);
            //Ebene | Page 1
            case 3:
                return World.world.getObjTemplate(7808);
            //Rousse sauvage
            case 4:
                return World.world.getObjTemplate(7809);
            //Ebene-ivoire
            case 9:
                return World.world.getObjTemplate(7810);
            //Rousse
            case 10:
                return World.world.getObjTemplate(7811);
            //Ivoire-Rousse
            case 11:
                return World.world.getObjTemplate(7812);
            //Ebene-rousse
            case 12:
                return World.world.getObjTemplate(7813);
            //Turquoise
            case 15:
                return World.world.getObjTemplate(7814);
            //Ivoire
            case 16:
                return World.world.getObjTemplate(7815);
            //Indigo
            case 17:
                return World.world.getObjTemplate(7816);
            //Dorée
            case 18:
                return World.world.getObjTemplate(7817);
            //Pourpre
            case 19:
                return World.world.getObjTemplate(7818);
            //Amande
            case 20:
                return World.world.getObjTemplate(7819);
            //Emeraude
            case 21:
                return World.world.getObjTemplate(7820);
            //Orchidée
            case 22:
                return World.world.getObjTemplate(7821);
            //Prune
            case 23:
                return World.world.getObjTemplate(7822);
            //Amande-Dorée
            case 33:
                return World.world.getObjTemplate(7823);
            //Amande-Ebene
            case 34:
                return World.world.getObjTemplate(7824);
            //Amande-Emeraude
            case 35:
                return World.world.getObjTemplate(7825);
            //Amande-Indigo
            case 36:
                return World.world.getObjTemplate(7826);
            //Amande-Ivoire
            case 37:
                return World.world.getObjTemplate(7827);
            //Amande-Rousse
            case 38:
                return World.world.getObjTemplate(7828);
            //Amande-Turquoise
            case 39:
                return World.world.getObjTemplate(7829);
            //Amande-Orchidée
            case 40:
                return World.world.getObjTemplate(7830);
            //Amande-Pourpre
            case 41:
                return World.world.getObjTemplate(7831);
            //Dorée-Ebène
            case 42:
                return World.world.getObjTemplate(7832);
            //Dorée-Emeraude
            case 43:
                return World.world.getObjTemplate(7833);
            //Dorée-Indigo
            case 44:
                return World.world.getObjTemplate(7834);
            //Dorée-Ivoire
            case 45:
                return World.world.getObjTemplate(7835);
            //Dorée-Rousse | Page 2
            case 46:
                return World.world.getObjTemplate(7836);
            //Dorée-Turquoise
            case 47:
                return World.world.getObjTemplate(7837);
            //Dorée-Orchidée
            case 48:
                return World.world.getObjTemplate(7838);
            //Dorée-Pourpre
            case 49:
                return World.world.getObjTemplate(7839);
            //Ebène-Emeraude
            case 50:
                return World.world.getObjTemplate(7840);
            //Ebène-Indigo
            case 51:
                return World.world.getObjTemplate(7841);
            //Ebène-Turquoise
            case 52:
                return World.world.getObjTemplate(7842);
            //Ebène-Orchidée
            case 53:
                return World.world.getObjTemplate(7843);
            //Ebène-Pourpre
            case 54:
                return World.world.getObjTemplate(7844);
            //Emeraude-Indigo
            case 55:
                return World.world.getObjTemplate(7845);
            //Emeraude-Ivoire
            case 56:
                return World.world.getObjTemplate(7846);
            //Emeraude-Rousse
            case 57:
                return World.world.getObjTemplate(7847);
            //Emeraude-Turquoise
            case 58:
                return World.world.getObjTemplate(7848);
            //Emeraude-Orchidée
            case 59:
                return World.world.getObjTemplate(7849);
            //Emeraude-Pourpre
            case 60:
                return World.world.getObjTemplate(7850);
            //Indigo-Ivoire
            case 61:
                return World.world.getObjTemplate(7851);
            //Indigo-Rousse
            case 62:
                return World.world.getObjTemplate(7852);
            //Indigo-Turquoise
            case 63:
                return World.world.getObjTemplate(7853);
            //Indigo-Orchidée
            case 64:
                return World.world.getObjTemplate(7854);
            //Indigo-Pourpre
            case 65:
                return World.world.getObjTemplate(7855);
            //Ivoire-Turquoise
            case 66:
                return World.world.getObjTemplate(7856);
            //Ivoire-Ochidée
            case 67:
                return World.world.getObjTemplate(7857);
            //Ivoire-Pourpre
            case 68:
                return World.world.getObjTemplate(7858);
            //Turquoise-Rousse
            case 69:
                return World.world.getObjTemplate(7859);
            //Ochidée-Rousse
            case 70:
                return World.world.getObjTemplate(7860);
            //Pourpre-Rousse
            case 71:
                return World.world.getObjTemplate(7861);
            //Turquoise-Orchidée
            case 72:
                return World.world.getObjTemplate(7862);
            //Turquoise-Pourpre
            case 73:
                return World.world.getObjTemplate(7863);
            //Dorée sauvage
            case 74:
                return World.world.getObjTemplate(7864);
            //Squelette
            case 75:
                return World.world.getObjTemplate(7865);
            //Orchidée-Pourpre
            case 76:
                return World.world.getObjTemplate(7866);
            //Prune-Amande
            case 77:
                return World.world.getObjTemplate(7867);
            //Prune-Dorée
            case 78:
                return World.world.getObjTemplate(7868);
            //Prune-Ebène
            case 79:
                return World.world.getObjTemplate(7869);
            //Prune-Emeraude
            case 80:
                return World.world.getObjTemplate(7870);
            //Prune et Indigo
            case 82:
                return World.world.getObjTemplate(7871);
            //Prune-Ivoire
            case 83:
                return World.world.getObjTemplate(7872);
            //Prune-Rousse
            case 84:
                return World.world.getObjTemplate(7873);
            //Prune-Turquoise
            case 85:
                return World.world.getObjTemplate(7874);
            //Prune-Orchidée
            case 86:
                return World.world.getObjTemplate(7875);
            //Prune-Pourpre
            case 87:
                return World.world.getObjTemplate(7876);
            //Armure
            case 88:
                return World.world.getObjTemplate(9582);
            //Dragodinde du Paladin
            case 89:
                return World.world.getObjTemplate(12776);
            //Tabi
            case 90:
                return World.world.getObjTemplate(12780);
            //Karnage
            case 91:
                return World.world.getObjTemplate(12827);
        }
        return null;
    }

    public static int getMountColorByParchoTemplate(int tID) {
        for (int a = 1; a < 100; a++)
            if (getParchoTemplateByMountColor(a) != null)
                if (getParchoTemplateByMountColor(a).getId() == tID)
                    return a;
        return -1;
    }

    public static int getNearCellidUnused(Player _perso) {
        int cellFront = 0;
        int cellBack = 0;
        int cellRight = 0;
        int cellLeft = 0;
        int cell = 0;
        int calcul = 0;
        GameMap map = _perso.getCurMap();
        if (map == null)
            return -1;
        SubArea sub = map.getSubArea();
        if (sub == null)
            return -1;
        Area area = sub.getArea();
        if (area == null)
            return -1;
        if ((area.getId() == 7 || area.getId() == 11) && map.getW() == 19
                && map.getH() == 22) {
            cellFront = 19;
            cellBack = 19;
            cellRight = 18;
            cellLeft = 18;
        } else {
            cellFront = 15;
            cellBack = 15;
            cellRight = 14;
            cellLeft = 14;
        }
        cell = _perso.getCurCell().getId();
        calcul = cell + cellFront;
        if (map.getCase(calcul).getDroppedItem(false) == null
                && map.getCases().get(calcul).getPlayers().isEmpty()
                && map.getCases().get(calcul).isWalkable(false)
                && map.getCases().get(calcul).getObject() == null) {
            return calcul;
        } else
            calcul = 0;
        calcul = cell - cellBack;
        if (map.getCase(calcul).getDroppedItem(false) == null
                && map.getCases().get(calcul).getPlayers().isEmpty()
                && map.getCases().get(calcul).isWalkable(false)
                && map.getCases().get(calcul).getObject() == null) {
            return calcul;
        } else
            calcul = 0;
        calcul = cell + cellRight;
        if (map.getCase(calcul).getDroppedItem(false) == null
                && map.getCases().get(calcul).getPlayers().isEmpty()
                && map.getCases().get(calcul).isWalkable(false)
                && map.getCases().get(calcul).getObject() == null) {
            return calcul;
        } else
            calcul = 0;
        calcul = cell - cellLeft;
        if (map.getCase(calcul).getDroppedItem(false) == null
                && map.getCases().get(calcul).getPlayers().isEmpty()
                && map.getCases().get(calcul).isWalkable(false)
                && map.getCases().get(calcul).getObject() == null) {
            return calcul;
        }

        return -1;
    }
    
    
    public static boolean isAValidConsumableItem(final ObjectTemplate template) {
    	switch (template.getType()) {
	    	case ITEM_TYPE_POTION:
	    	case ITEM_TYPE_PARCHO_EXP:
	    	case ITEM_TYPE_BOOST_FOOD:
	    	case ITEM_TYPE_PAIN:
	    	case ITEM_TYPE_BIERE:
	    	case ITEM_TYPE_POISSON:
	    	case ITEM_TYPE_BONBON:
	    	case ITEM_TYPE_COMESTI_POISSON:
	    	case ITEM_TYPE_VIANDE:
	    	case ITEM_TYPE_VIANDE_CONSERVEE:
	    	case ITEM_TYPE_VIANDE_COMESTIBLE:
	    	case ITEM_TYPE_TEINTURE:
	    	case ITEM_TYPE_MAITRISE:
	    	case ITEM_TYPE_BOISSON:
	    	case ITEM_TYPE_PIERRE_AME_PLEINE:
	    	case ITEM_TYPE_PARCHO_RECHERCHE:
	    	case ITEM_TYPE_CADEAUX:
	    	case ITEM_TYPE_OBJET_ELEVAGE:
	    	case ITEM_TYPE_OBJET_UTILISABLE:
	    	case ITEM_TYPE_PRISME:
	    	case ITEM_TYPE_FEE_ARTIFICE:
	    	case ITEM_TYPE_DONS:
	    	case ITEM_TYPE_PIERRE_MAGIQUE:
	    		return true;
    	}
    	return false;
    }

    public static boolean isTypeWeapon(final int type) {
    	switch(type) {
	    	case 114: // tourmenteurs
	        case ITEM_TYPE_ARC:
	        case ITEM_TYPE_BAGUETTE:
	        case ITEM_TYPE_BATON:
	        case ITEM_TYPE_DAGUES:
	        case ITEM_TYPE_EPEE:
	        case ITEM_TYPE_MARTEAU:
	        case ITEM_TYPE_PELLE:
	        case ITEM_TYPE_HACHE:
	        case ITEM_TYPE_OUTIL:
	        case ITEM_TYPE_PIOCHE:
	        case ITEM_TYPE_FAUX:
	        	return true;
    	}
    	return false;
    }
    
    public static boolean isValidPlaceForItem(final ObjectTemplate template, final int place) {
        if(isTypeWeapon(template.getType()) && place == ITEM_POS_ARME) return true;
        switch (template.getType()) {
            case ITEM_TYPE_AMULETTE:
                if (place == ITEM_POS_AMULETTE)
                    return true;
                break;
            case 113: // Obvi
                if ((template.getId() == 9233) && (place == Constant.ITEM_POS_CAPE))
                    return true;
                if ((template.getId() == 9234) && (place == Constant.ITEM_POS_COIFFE))
                    return true;
                if ((template.getId() == 9255) && (place == Constant.ITEM_POS_AMULETTE))
                    return true;
                if ((template.getId() == 9256) && (place == Constant.ITEM_POS_ANNEAU1 || place == Constant.ITEM_POS_ANNEAU1))
                    return true;
                break;
            case ITEM_TYPE_PIERRE_AME:
            case ITEM_TYPE_FILET_CAPTURE:
                if (place == ITEM_POS_ARME)
                    return true;
                break;

            case ITEM_TYPE_ANNEAU:
                if (place == ITEM_POS_ANNEAU1 || place == ITEM_POS_ANNEAU2)
                    return true;
                break;

            case ITEM_TYPE_CEINTURE:
                if (place == ITEM_POS_CEINTURE)
                    return true;
                break;

            case ITEM_TYPE_BOTTES:
                if (place == ITEM_POS_BOTTES)
                    return true;
                break;

            case ITEM_TYPE_COIFFE:
                if (place == ITEM_POS_COIFFE)
                    return true;
                break;

            case ITEM_TYPE_CAPE:
            case ITEM_TYPE_SAC_DOS:
                if (place == ITEM_POS_CAPE)
                    return true;
                break;

            case ITEM_TYPE_FAMILIER:
                if (place == ITEM_POS_FAMILIER)
                    return true;
                break;

            case ITEM_TYPE_DOFUS:
                if (isPlaceDofus(place))
                    return true;
                break;

            case ITEM_TYPE_BOUCLIER:
                if (place == ITEM_POS_BOUCLIER)
                    return true;
                break;

        }
        
        return (isAValidConsumableItem(template) && (place >= CONSO_POS_1 && place <= CONSO_POS_14));
    }
    
    public static boolean isPlaceDofus(final int place) {
    	if (place == ITEM_POS_DOFUS1 || place == ITEM_POS_DOFUS2
                || place == ITEM_POS_DOFUS3 || place == ITEM_POS_DOFUS4
                || place == ITEM_POS_DOFUS5 || place == ITEM_POS_DOFUS6)
            return true;
    	return false;
    }

	/*
     * public static boolean feedMount(int type) { for (Integer feed :
	 * Main.itemFeedMount) { if (type == feed) return true; } return false; }
	 */

    public static void tpCim(int idArea, Player perso) {
        switch (idArea) {
            case 45:
                perso.teleport((short) 10342, 222);
                break;

            case 0:
            case 5:
            case 29:
            case 39:
            case 40:
            case 43:
            case 44:
                perso.teleport((short) 1174, 279);
                break;

            case 3:
            case 4:
            case 6:
            case 18:
            case 25:
            case 27:
            case 41:
                perso.teleport((short) 8534, 196);
                break;

            case 2:
                perso.teleport((short) 420, 408);
                break;

            case 1:
                perso.teleport((short) 844, 370);
                break;

            case 7:
                perso.teleport((short) 4285, 572);
                break;

            case 8:
            case 14:
            case 15:
            case 16:
            case 32:
                perso.teleport((short) 4748, 133);
                break;

            case 11:
            case 12:
            case 13:
            case 33:
                perso.teleport((short) 5719, 196);
                break;

            case 19:
            case 22:
            case 23:
                perso.teleport((short) 7910, 381);
                break;

            case 20:
            case 21:
            case 24:
                perso.teleport((short) 8054, 115);
                break;

            case 28:
            case 34:
            case 35:
            case 36:
                perso.teleport((short) 9231, 257);
                break;

            case 30:
                perso.teleport((short) 9539, 128);
                break;

            case 31:
                if (perso.isGhost())
                    perso.teleport((short) 9558, 268);
                else
                    perso.teleport((short) 9558, 224);
                break;

            case 37:
                perso.teleport((short) 7796, 433);
                break;

            case 42:
                perso.teleport((short) 8534, 196);
                break;

            case 46:
                perso.teleport((short) 10422, 327);
                break;
            case 47:
                perso.teleport((short) 10590, 302);
                break;

            case 26:
                perso.teleport((short) 9398, 268);

            default:
                perso.teleport((short) 8534, 196);
                break;
        }
    }

    public static boolean isTaverne(GameMap map) {
        switch (map.getId()) {
            case 7573:
            case 7572:
            case 7574:
            case 465:
            case 463:
            case 6064:
            case 461:
            case 462:
            case 5867:
            case 6197:
            case 6021:
            case 6044:
            case 8196:
            case 6055:
            case 8195:
            case 1905:
            case 1907:
            case 6049:
                return true;
        }
        return false;
    }

    public static int getLevelForChevalier(Player target) {
        int lvl = target.getLevel();
        if (lvl <= 50)
            return 50;
        else if (lvl <= 80)
            return 80;
        else if (lvl <= 110)
            return 110;
        else if (lvl <= 140)
            return 140;
        else if (lvl <= 170)
            return 170;
        else if (lvl <= 500)
            return 200;
        else
            return 200;
    }

    public static String getStatsOfCandy(int id, int turn) {
        String a = World.world.getObjTemplate(id).getStrTemplate();
        a += ",32b#64#0#" + Integer.toHexString(turn) + "#0d0+1;";
        return a;
    }

    public static String getStatsOfMascotte() {
        String a = Integer.toHexString(148) + "#0#0#0#0d0+1,";
        a += "32b#64#0#" + Integer.toHexString(1) + "#0d0+1;";
        return a;
    }


    public static String getStringColorDragodinde(int color) {
        switch (color) {
            case 1: // Dragodinde Amande Sauvage
                return "16772045,-1,16772045";
            case 3: // Dragodinde Ebène
                return "1245184,393216,1245184";
            case 6: // Dragodinde Rousse Sauvage
                return "16747520,-1,16747520";
            case 9: // Dragodinde Ebène et Ivoire
                return "1182992,16777200,16777200";
            case 10: // Dragodinde Rousse
                return "16747520,-1,16747520";
            case 11: // Dragodinde Ivoire et Rousse
                return "16747520,16777200,16777200";
            case 12: // Dragodinde Ebène et Rousse
                return "16747520,1703936,1774084";
            case 15: // Dragodinde Turquoise
                return "4251856,-1,4251856";
            case 16: // Dragodinde Ivoire
                return "16777200,16777200,16777200";
            case 17: // Dragodinde Indigo
                return "4915330,-1,4915330";
            case 18: // Dragodinde Dorée
                return "16766720,16766720,16766720";
            case 19: // Dragodinde Pourpre
                return "14423100,-1,14423100";
            case 20: // Dragodinde Amande
                return "16772045,-1,16772045";
            case 21: // Dragodinde Emeraude
                return "3329330,-1,3329330";
            case 22: // Dragodinde Orchidée
                return "15859954,16777200,15859954";
            case 23: // Dragodinde Prune
                return "14524637,-1,14524637";
            case 33: // Dragodinde Amande et Dorée
                return "16772045,16766720,16766720";
            case 34: // Dragodinde Amande et Ebène
                return "16772045,1245184,1245184";
            case 35: // Dragodinde Amande et Emeraude
                return "16772045,3329330,3329330";
            case 36: // Dragodinde Amande et Indigo
                return "16772045,4915330,4915330";
            case 37: // Dragodinde Amande et Ivoire
                return "16772045,16777200,16777200";
            case 38: // Dragodinde Amande et Rousse
                return "16772045,16747520,16747520";
            case 39: // Dragodinde Amande et Turquoise
                return "16772045,4251856,4251856";
            case 40: // Dragodinde Amande et Orchidée
                return "16772045,15859954,15859954";
            case 41: // Dragodinde Amande et Pourpre
                return "16772045,14423100,14423100";
            case 42: // Dragodinde Dorée et Ebène
                return "1245184,16766720,16766720";
            case 43: // Dragodinde Dorée et Emeraude
                return "16766720,3329330,3329330";
            case 44: // Dragodinde Dorée et Indigo
                return "16766720,4915330,4915330";
            case 45: // Dragodinde Dorée et Ivoire
                return "16766720,16777200,16777200";
            case 46: // Dragodinde Dorée et Rousse
                return "16766720,16747520,16747520";
            case 47: // Dragodinde Dorée et Turquoise
                return "16766720,4251856,4251856";
            case 48: // Dragodinde Dorée et Orchidée
                return "16766720,15859954,15859954";
            case 49: // Dragodinde Dorée et Pourpre
                return "16766720,14423100,14423100";
            case 50: // Dragodinde Ebène et Emeraude
                return "1245184,3329330,3329330";
            case 51: // Dragodinde Ebène et Indigo
                return "4915330,4915330,1245184";
            case 52: // Dragodinde Ebène et Turquoise
                return "1245184,4251856,4251856";
            case 53: // Dragodinde Ebène et Orchidée
                return "15859954,0,0";
            case 54: // Dragodinde Ebène et Pourpre
                return "14423100,14423100,1245184";
            case 55: // Dragodinde Emeraude et Indigo
                return "3329330,4915330,4915330";
            case 56: // Dragodinde Emeraude et Ivoire
                return "3329330,16777200,16777200";
            case 57: // Dragodinde Emeraude et Rousse
                return "3329330,16747520,16747520";
            case 58: // Dragodinde Emeraude et Turquoise
                return "3329330,4251856,4251856";
            case 59: // Dragodinde Emeraude et Orchidée
                return "3329330,15859954,15859954";
            case 60: // Dragodinde Emeraude et Pourpre
                return "3329330,14423100,14423100";
            case 61: // Dragodinde Indigo et Ivoire
                return "4915330,16777200,16777200";
            case 62: // Dragodinde Indigo et Rousse
                return "4915330,16747520,16747520";
            case 63: // Dragodinde Indigo et Turquoise
                return "4915330,4251856,4251856";
            case 64: // Dragodinde Indigo et Orchidée
                return "4915330,15859954,15859954";
            case 65: // Dragodinde Indigo et Pourpre
                return "14423100,4915330,4915330";
            case 66: // Dragodinde Ivoire et Turquoise
                return "16777200,4251856,4251856";
            case 67: // Dragodinde Ivoire et Orchidée
                return "16777200,16731355,16711910";
            case 68: // Dragodinde Ivoire et Pourpre
                return "14423100,16777200,16777200";
            case 69: // Dragodinde Ivoire et Rousse
                return "4251856,16747520,16747520";
            case 70: // Dragodinde Orchidée et Rousse
                return "14315734,16747520,16747520";
            case 71: // Dragodinde Pourpre et Rousse
                return "14423100,16747520,16747520";
            case 72: // Dragodinde Turquoise et Orchidée
                return "15859954,4251856,4251856";
            case 73: // Dragodinde Turquoise et Pourpre
                return "14423100,4251856,4251856";
            case 74: // Dragodinde Dorée et Rousse
                return "16766720,16766720,16766720";
            case 76: // Dragodinde Orchidée et Pourpre
                return "14315734,14423100,14423100";
            case 77: // Dragodinde Prune et Amande
                return "14524637,16772045,16772045";
            case 78: // Dragodinde Prune et Dorée
                return "14524637,16766720,16766720";
            case 79: // Dragodinde Prune et Ebène
                return "14524637,1245184,1245184";
            case 80: // Dragodinde Prune et Emeraude
                return "14524637,3329330,3329330";
            case 82: // Dragodinde Prune et Indigo
                return "14524637,4915330,4915330";
            case 83: // Dragodinde Prune et Ivoire
                return "14524637,16777200,16777200";
            case 84: // Dragodinde Prune et Rousse
                return "14524637,16747520,16747520";
            case 85: // Dragodinde Prune et Turquoise
                return "14524637,4251856,4251856";
            case 86: // Dragodinde Prune et Orchidée
                return "14524637,15859954,15859954";
            case 87: // Dragodinde Prune et Pourpre
                return "14524637,14423100,14423100";
            default:
                return "-1,-1,-1";
        }
    }

    public static int getGeneration(int color) {
        switch (color) {
            case 10: // Rousse
            case 18: // Dorée
            case 20: // Amande
                return 1;
            case 33: // Amande - Dorée
            case 38: // Amande - Rousse
            case 46: // Dorée - Rousse
                return 2;
            case 3: // Ebène
            case 17: // Indigo
                return 3;
            case 62: // Indigo - Rousse
            case 12: // Ebène - Rousse
            case 36: // Amande - Indigo
            case 34: // Amande - Ebène
            case 44: // Dorée - Indigo
            case 42: // Dorée - Ebène
            case 51: // Ebène - Indigo
                return 4;
            case 19: // Purpre
            case 22: // Orchidée
                return 5;
            case 71: // Purpre - Rousse
            case 70: // Orchidée - Rousse
            case 41: // Amande - Purpre
            case 40: // Amande - Orchidée
            case 49: // Dorée - Purpre
            case 48: // Dorée - Orchidée
            case 65: // Indigo - Purpre
            case 64: // Indigo - Orchidée
            case 54: // Ebène - Purpre
            case 53: // Ebène - Orchidée
            case 76: // Orchidée - Purpre
                return 6;
            case 15: // Turquoise
            case 16: // Ivoire
                return 7;
            case 11: // Ivoire - Rousse
            case 69: // Turquoise - Rousse
            case 37: // Amande - Ivoire
            case 39: // Amande - Turquoise
            case 45: // Dorée - Ivoire
            case 47: // Dorée - Turquoise
            case 61: // Indigo - Ivoire
            case 63: // Indigo - Turquoise
            case 9: // Ebène - Ivoire
            case 52: // Ebène - Turquoise
            case 68: // Ivoire - Purpre
            case 73: // Turquoise - Purpre
            case 67: // Ivoire - Orchidée
            case 72: // Orchidée - Turquoise
            case 66: // Ivoire - Turquoise
                return 8;
            case 21: // Emeraude
            case 23: // Prune
                return 9;
            case 57:// Emeraude - Rousse
            case 35: // Amande - Emeraude
            case 43: // Dorée - Emeraude
            case 50: // Ebène - Emeraude
            case 55: // Emeraude - Indigo
            case 56: // Emeraude - Ivoire
            case 58: // Emeraude - Turquoise
            case 59: // Emeraude - Orchidée
            case 60: // Emeraude - Purpre
            case 77: // Prune - Amande
            case 78: // Prune - Dorée
            case 79: // Prune - Ebène
            case 80: // Prune - Emeraude
            case 82: // Prune - Indigo
            case 83: // Prune - Ivoire
            case 84: // Prune - Rousse
            case 85: // Prune - Turquoise
            case 86: // Prune - Orchidée
                return 10;
            default:
                return 1;
        }
    }

    public static int colorToEtable(Player player, Mount mother, Mount father) {
        int color1, color2;
        int A = 0, B = 0, C = 0;

        String[] splitM = mother.getAncestors().split(","), splitF = father.getAncestors().split(",");
        RandomStats<Integer> random = new RandomStats<>();

        short i = 0;
        for(String str : splitM) {
            i++;
            if(str.equals("?")) continue;

            int pct = 1;

            switch(i) {
                case 1: case 2: pct = 25; break;
                case 3: case 4: case 5: case 6: pct = 10;
            }

            random.add(pct, Integer.parseInt(str));
        }

        random.add(random.size() == 0 ? 100 : 33, mother.getColor());
        color1 = random.get();

        random = new RandomStats<>();
        i = 0;
        for(String str : splitF) {
            i++;
            if(str.equals("?")) continue;

            int pct = 1;

            switch(i) {
                case 1: case 2: pct = 25; break;
                case 3: case 4: case 5: case 6: pct = 10;
            }

            random.add(pct, Integer.parseInt(str));
        }

        random.add(random.size() == 0 ? 100 : 33, father.getColor());
        color2 = random.get();

        if(color1 == 75)
            color1 = 10;
        if(color2 == 75)
            color2 = 10;

        if (color1 >= color2) {
            A = color2;// moins
            B = color1;// supérieur
        } else {
            A = color1;// moins
            B = color2;// supérieur
        }
        if (A == 10 && B == 18)
            C = 46; // Rousse y Dorée
        else if (A == 10 && B == 20)
            C = 38; // Rousse y Amande
        else if (A == 18 && B == 20)
            C = 33; // Amande y Dorée
        else if (A == 33 && B == 38)
            C = 17; // Indigo
        else if (A == 33 && B == 46)
            C = 3;// Ebène
        else if (A == 10 && B == 17)
            C = 62; // Rousse e Indigo
        else if (A == 10 && B == 3)
            C = 12; // Ebène y Rousse
        else if (A == 17 && B == 20)
            C = 36; // Amande - Indigo
        else if (A == 3 && B == 20)
            C = 34; // Amande - Ebène
        else if (A == 17 && B == 18)
            C = 44; // Dorée - Indigo
        else if (A == 3 && B == 18)
            C = 42; // Dorée - Ebène
        else if (A == 3 && B == 17)
            C = 51; // Ebène - Indigo
        else if (A == 38 && B == 51)
            C = 19; // Purpre
        else if (A == 46 && B == 51)
            C = 22; // Orchidée
        else if (A == 10 && B == 19)
            C = 71; // Purpre - Rousse
        else if (A == 10 && B == 22)
            C = 70; // Orchidée - Rousse
        else if (A == 19 && B == 20)
            C = 41; // Amande - Purpre
        else if (A == 20 && B == 22)
            C = 40; // Amande - Orchidée
        else if (A == 18 && B == 19)
            C = 49; // Dorée - Purpre
        else if (A == 18 && B == 22)
            C = 48; // Dorée - Orchidée
        else if (A == 17 && B == 19)
            C = 65; // Indigo - Purpre
        else if (A == 17 && B == 22)
            C = 64; // Indigo - Orchidée
        else if (A == 3 && B == 19)
            C = 54; // Ebène - Purpre
        else if (A == 3 && B == 22)
            C = 53; // Ebène - Orchidée
        else if (A == 19 && B == 22)
            C = 76; // Orchidée - Purpre
        else if (A == 53 && B == 76)
            C = 15; // Turquoise
        else if (A == 65 && B == 76)
            C = 16; // Ivoire
        else if (A == 10 && B == 16)
            C = 11; // Ivoire - Rousse
        else if (A == 10 && B == 15)
            C = 69; // Turquoise - Rousse
        else if (A == 16 && B == 20)
            C = 37; // Amande - Ivoire
        else if (A == 15 && B == 20)
            C = 39; // Amande - Turquoise
        else if (A == 16 && B == 18)
            C = 45; // Dorée - Ivoire
        else if (A == 15 && B == 18)
            C = 47; // Dorée - Turquoise
        else if (A == 16 && B == 17)
            C = 61; // Indigo - Ivoire
        else if (A == 15 && B == 17)
            C = 63; // Indigo - Turquoise
        else if (A == 3 && B == 16)
            C = 9; // Ebène - Ivoire
        else if (A == 3 && B == 15)
            C = 52; // Ebène - Turquoise
        else if (A == 16 && B == 19)
            C = 68; // Ivoire - Purpre
        else if (A == 15 && B == 19)
            C = 73; // Turquoise - Purpre
        else if (A == 16 && B == 22)
            C = 67; // Ivoire - Orchidée
        else if (A == 15 && B == 22)
            C = 72; // Orchidée - Turquoise
        else if (A == 15 && B == 16)
            C = 66; // Ivoire - Turquoise
        else if (A == 66 && B == 68)
            C = 21; // Emeraude
        else if (A == 66 && B == 72)
            C = 23; // Prune
        else if (A == 10 && B == 21)
            C = 57;// Emeraude - Rousse
        else if (A == 20 && B == 21)
            C = 35; // Amande - Emeraude
        else if (A == 18 && B == 21)
            C = 43; // Dorée - Emeraude
        else if (A == 3 && B == 21)
            C = 50; // Ebène - Emeraude
        else if (A == 17 && B == 21)
            C = 55; // Emeraude - Indigo
        else if (A == 16 && B == 21)
            C = 56; // Emeraude - Ivoire
        else if (A == 15 && B == 21)
            C = 58; // Emeraude - Turquoise
        else if (A == 21 && B == 22)
            C = 59; // Emeraude - Orchidée
        else if (A == 19 && B == 21)
            C = 60; // Emeraude - Purpre
        else if (A == 20 && B == 23)
            C = 77; // Prune - Amande
        else if (A == 18 && B == 23)
            C = 78; // Prune - Dorée
        else if (A == 3 && B == 23)
            C = 79; // Prune - Ebène
        else if (A == 21 && B == 23)
            C = 80; // Prune - Emeraude
        else if (A == 17 && B == 23)
            C = 82; // Prune - Indigo
        else if (A == 16 && B == 23)
            C = 83; // Prune - Ivoire
        else if (A == 10 && B == 23)
            C = 84; // Prune - Rousse
        else if (A == 15 && B == 23)
            C = 85; // Prune - Turquoise
        else if (A == 22 && B == 23)
            C = 86; // Prune - Orchidée
        else if (A == 19 && B == 23)
            C = 87; // Prune - Purpre
        else if (A == B)
            C = A = B;

        if(C == 0) {

            random = new RandomStats<>();
            i = 0;
            for(String str : splitF) {
                i++;
                if(str.equals("?")) continue;

                int pct = 1;

                switch(i) {
                    case 1: case 2: pct = 25; break;
                    case 3: case 4: case 5: case 6: pct = 10;
                }

                random.add(pct, Integer.parseInt(str));
            }
            i = 0;
            for(String str : splitM) {
                i++;
                if(str.equals("?")) continue;

                int pct = 1;

                switch(i) {
                    case 1: case 2: pct = 25; break;
                    case 3: case 4: case 5: case 6: pct = 10;
                }

                random.add(pct, Integer.parseInt(str));
            }
            C = random.get();
            player.sendMessage("Merci de crier auprès de Locos que C = 0, A = " + A + ", et B = " + B + ". Valeur finale : " + C + ". Message bien évidement sérieux.");

            return C;
        }
        random = new RandomStats<>();
        random.add(33, A);
        random.add(33, B);
        random.add(33, C);
        return random.get();
    }

    public static int getParchoByIdPets(int id) {
        switch (id) {
            case 10802:
                return 10806;
            case 10107:
                return 10135;
            case 10106:
                return 10134;
            case 9795:
                return 9810;
            case 9624:
                return 9685;
            case 9623:
                return 9684;
            case 9620:
                return 9683;
            case 9619:
                return 9682;
            case 9617:
                return 9675;
            case 9594:
                return 9598;
            case 8693:
                return 8707;
            case 8677:
                return 8684;
            case 8561:
                return 8564;
            case 8211:
                return 8544;
            case 8155:
                return 8179;
            case 8154:
                return 8178;
            case 8153:
                return 8175;
            case 8151:
                return 8176;
            case 8000:
                return 8180;
            case 7911:
                return 8526;
            case 7892:
                return 7896;
            case 7891:
                return 7895;
            case 7714:
                return 8708;
            case 7713:
                return 9681;
            case 7712:
                return 9680;
            case 7711:
                return 9679;
            case 7710:
                return 9678;
            case 7709:
                return 9677;
            case 7708:
                return 9676;
            case 7707:
                return 9674;
            case 7706:
                return 8685;
            case 7705:
                return 8889;
            case 7704:
                return 8888;
            case 7703:
                return 8421;
            case 7524:
                return 8887;
            case 7522:
                return 7535;
            case 7520:
                return 7533;
            case 7519:
                return 7534;
            case 7518:
                return 7532;
            case 7415:
                return 7419;
            case 7414:
                return 7418;
            case 6978:
                return 7417;
            case 6716:
                return 7420;
            case 2077:
                return 2098;
            case 2076:
                return 2101;
            case 2075:
                return 2100;
            case 2074:
                return 2099;
            case 1748:
                return 2102;
            case 1728:
                return 1735;
        }
        return -1;
    }

    public static int getPetsByIdParcho(int id) {
        switch (id) {
            case 10806:
                return 10802;
            case 10135:
                return 10107;
            case 10134:
                return 10106;
            case 9810:
                return 9795;
            case 9685:
                return 9624;
            case 9684:
                return 9623;
            case 9683:
                return 9620;
            case 9682:
                return 9619;
            case 9675:
                return 9617;
            case 9598:
                return 9594;
            case 8707:
                return 8693;
            case 8684:
                return 8677;
            case 8564:
                return 8561;
            case 8544:
                return 8211;
            case 8179:
                return 8155;
            case 8178:
                return 8154;
            case 8175:
                return 8153;
            case 8176:
                return 8151;
            case 8180:
                return 8000;
            case 8526:
                return 7911;
            case 7896:
                return 7892;
            case 7895:
                return 7891;
            case 8708:
                return 7714;
            case 9681:
                return 7713;
            case 9680:
                return 7712;
            case 9679:
                return 7711;
            case 9678:
                return 7710;
            case 9677:
                return 7709;
            case 9676:
                return 7708;
            case 9674:
                return 7707;
            case 8685:
                return 7706;
            case 8889:
                return 7705;
            case 8888:
                return 7704;
            case 8421:
                return 7703;
            case 8887:
                return 7524;
            case 7535:
                return 7522;
            case 7533:
                return 7520;
            case 7534:
                return 7519;
            case 7532:
                return 7518;
            case 7419:
                return 7415;
            case 7418:
                return 7414;
            case 7417:
                return 6978;
            case 7420:
                return 6716;
            case 2098:
                return 2077;
            case 2101:
                return 2076;
            case 2100:
                return 2075;
            case 2099:
                return 2074;
            case 2102:
                return 1748;
            case 1735:
                return 1728;
        }
        return -1;
    }

    public static int getDoplonDopeul(int IDmob) {
        switch (IDmob) {
            case 168:
                return 10302;
            case 165:
                return 10303;
            case 166:
                return 10304;
            case 162:
                return 10305;
            case 160:
                return 10306;
            case 167:
                return 10307;
            case 161:
                return 10308;
            case 2691:
                return 10309;
            case 455:
                return 10310;
            case 169:
                return 10311;
            case 163:
                return 10312;
            case 164:
                return 10313;
        }
        return -1;
    }

    public static int getIDdoplonByMapID(int IDmap) {
        switch (IDmap) {
            case 6926: //Sram
                return 10312;
            case 1470: //Enutrof
                return 10305;
            case 1461: //Ecaflip (map de dessous, puisque l'autre n'est pas dans l'emu)
                return 10303;
            case 6949: //Sacrieur
                return 10310;
            case 1556: //Cra (map en bas dans la maison, celle dans haut n'est pas dans l'emu)
                return 10302;
            case 1549: //Iop
                return 10307;
            case 1469: //Xel
                return 10313;
            case 487: //Eniripsa (dehors, puisque l'intérieur n'est pas présent dans l'emu)
                return 10304;
            case 490: //Osamodas (idem qu'eniripsa)
                return 10308;
            case 177: //Feca (idem ...)
                return 10306;
            case 1466: //Sadida
                return 10311;
            case 8207: //Panda (idem que nini ...)
                return 10309;
        }
        return -1;
    }

    public static int getStarAlea() {
        int rand = Formulas.getRandomValue(1, 100);
        if (rand > 54 && rand < 90) {
            int i = Formulas.getRandomValue(1, 15);
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    return 15;
                case 6:
                case 7:
                case 8:
                case 9:
                    return 30;
                case 10:
                case 11:
                case 12:
                    return 45;
                case 13:
                case 14:
                    return 60;
                case 15:
                    return 75;
            }
        }
        if (rand >= 90) {
            int i = Formulas.getRandomValue(1, 15);
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    return 90;
                case 6:
                case 7:
                case 8:
                case 9:
                    return 105;
                case 10:
                case 11:
                case 12:
                    return 120;
                case 13:
                case 14:
                    return 135;
                case 15:
                    return 150;
            }
        }
        return 0;
    }

    public static int getArmeSoin(int idArme) {
        switch (idArme) {
            case 7172:
            case 7182:
                return 100;
            case 7156:
            case 6539:
                return 80;
            case 1355:
                return 42;
            case 7040:
                return 10;
            case 6519:
                return 23;
            case 8118:
                return 30;
            default:
                return -1;
        }
    }

    public static int getSectionByDopeuls(int id) {
        switch (id) {
            case 160:
                return 1;
            case 161:
                return 2;
            case 162:
                return 3;
            case 163:
                return 4;
            case 164:
                return 5;
            case 165:
                return 6;
            case 166:
                return 7;
            case 167:
                return 8;
            case 168:
                return 9;
            case 169:
                return 10;
            case 455:
                return 11;
            case 2691:
                return 12;
        }
        return -1;
    }

    public static int getCertificatByDopeuls(int id) {
        switch (id) {
            case 160:
                return 10293;
            case 161:
                return 10295;
            case 162:
                return 10292;
            case 163:
                return 10299;
            case 164:
                return 10300;
            case 165:
                return 10290;
            case 166:
                return 10291;
            case 167:
                return 10294;
            case 168:
                return 10289;
            case 169:
                return 10298;
            case 455:
                return 10297;
            case 2691:
                return 10296;
        }
        return -1;
    }

    public static boolean isCertificatDopeuls(int id) {
        switch (id) {
            case 10293:
            case 10295:
            case 10292:
            case 10299:
            case 10300:
            case 10290:
            case 10291:
            case 10294:
            case 10289:
            case 10298:
            case 10297:
            case 10296:
                return true;
        }
        return false;
    }

    public static int getItemIdByMascotteId(int id) {
        switch (id) {
            case 10118:
                return 1498;//Croc blanc
            case 10078:
                return 70;//Eni Hoube
            case 10077:
                return -1;//Terra Cogita
            case 10009:
                return 90;//Xephirés
            case 9993:
                return 71;//Sabine
            case 9096:
                return 30;//Tacticien
            case 9061:
                return 40;//Exoram
            case 8563:
                return 1076;//Titi gobelait
            case 7425:
                return 1588;//Petite Larve Dorée
            case 7354:
                return 1264;//Zatoïshwan
            case 7353:
                return 1076;//Marzwell Le Gobelin
            case 7352:
                return 1153;//Musha l'Oni
            case 7351:
                return 1248;//Rok Gnorok
            case 7350:
                return 1228;//Aermyne Braco Scalptaras
            case 7062:
                return 9001;//Poochan
            case 6876:
                return 1245;//Ogivol Scarlarcin
            case 6875:
                return 1249;//Fouduglen
            case 6874:
                return 70;//Brumen Tinctorias
            case 6873:
                return 1243;//Qil Bil
            case 6872:
                return 50;//Nervoes Brakdoun
            case 6871:
                return 1247;//Frakacia Leukocytine
            case 6870:
                return 1246;//Padgref Demoël
            case 6869:
                return 9043;//Pleur Nycheuz
            case 6832:
                return -1;//Livreur de Bière
            case 6768:
                return 9001;//Soki
            case 2272:
                return 1577;//Larve Dorée
            case 2169:
                return 1205;//Raaga
            case 2152:
                return 1001;//Colonel Lyeno
            case 2134:
                return 1205;//Trof Hapyus
            case 2132:
                return 9004;//Houé Dapyus
            case 2130:
                return 1001;//Colonel Lyeno
            case 2082:
                return 1208;//Marcassin
        }
        return -1;
    }

    public static boolean isIncarnationWeapon(int id) {
        switch (id) {
            case 9544:
            case 9545:
            case 9546:
            case 9547:
            case 9548:
            case 10133:
            case 10127:
            case 10126:
            case 10125:
                return true;
        }
        return false;
    }

    public static boolean isTourmenteurWeapon(int id) {
        switch (id) {
            case 9544:
            case 9545:
            case 9546:
            case 9547:
            case 9548:
                return true;
        }
        return false;
    }

    public static boolean isBanditsWeapon(int id) {
        switch (id) {
            case 10133:
            case 10127:
            case 10126:
            case 10125:
                return true;
        }
        return false;
    }

    public static int getSpecialSpellByClasse(int classe) {
        switch (classe) {
            case Constant.CLASS_FECA:
                return 422;
            case Constant.CLASS_OSAMODAS:
                return 420;
            case Constant.CLASS_ENUTROF:
                return 425;
            case Constant.CLASS_SRAM:
                return 416;
            case Constant.CLASS_XELOR:
                return 424;
            case Constant.CLASS_ECAFLIP:
                return 412;
            case Constant.CLASS_ENIRIPSA:
                return 427;
            case Constant.CLASS_IOP:
                return 410;
            case Constant.CLASS_CRA:
                return 418;
            case Constant.CLASS_SADIDA:
                return 426;
            case Constant.CLASS_SACRIEUR:
                return 421;
            case Constant.CLASS_PANDAWA:
                return 423;
        }
        return 0;
    }

    public static boolean isFlacGelee(int id) {
        switch (id) {
            case 2430:
            case 2431:
            case 2432:
            case 2433:
                return true;
        }
        return false;
    }

    public static boolean isDoplon(int id) {
        switch (id) {
            case 10302:
            case 10303:
            case 10304:
            case 10305:
            case 10306:
            case 10307:
            case 10308:
            case 10309:
            case 10310:
            case 10311:
            case 10312:
            case 10313:
                return true;
        }
        return false;
    }

    public static boolean isInMorphDonjon(int id) {
        switch (id) {
            case 8716:
            case 8718:
            case 8719:
            case 9121:
            case 9122:
            case 9123:
            case 8979:
            case 8980:
            case 8981:
            case 8982:
            case 8983:
            case 8984:
            case 9716:
                return true;
        }
        return false;
    }

    public static int[] getOppositeStats(int statsId) {
        if (statsId == 217)
            return new int[]{210, 211, 213, 214};
        else if (statsId == 216)
            return new int[]{210, 212, 213, 214};
        else if (statsId == 218)
            return new int[]{210, 211, 212, 214};
        else if (statsId == 219)
            return new int[]{210, 211, 212, 214};
        else if (statsId == 215)
            return new int[]{211, 212, 213, 214};
        return null;
    }
    
    public static boolean isPanoIdClass(final int ObjPanoID) {
    	if(ObjPanoID >= 81 && ObjPanoID <= 92) return true;
    	else return ObjPanoID >= 201 && ObjPanoID <= 212;
    }
    
    public static boolean isTypeForMimibiote(final int type) {
    	if(type == Constant.ITEM_TYPE_COIFFE) return true;
    	else if(type == Constant.ITEM_TYPE_CAPE) return true;
    	else if(type == Constant.ITEM_TYPE_BOUCLIER) return true;
    	else if(type == Constant.ITEM_TYPE_SAC_DOS) return true;
    	else if(isTypeWeapon(type)) return true;
    	return false;
    }

    public static boolean isGladiatroolWeapon(int id) {
        switch (id) {
            case 12782:
            case 12783:
            case 12784:
            case 12785:
            case 12786:
            case 12787:
            case 12788:
            case 12789:
            case 12790:
            case 12791:
            case 12792:
            case 12793:
                return true;
        }
        return false;
    }

    public static boolean isInGladiatorDonjon(int id) {
        return GLADIATROOL_MAPID.contains(id);
    }

    public static int getPalierByNewMap(int Mapid) {
        switch (Mapid) {
            case 12277:
            case 15000:
                return 1;
            case 15008:
                return 2;
            case 15016:
                return 3;
            case 15024:
                return 4;
            case 15032:
                return 5;
            case 15040:
                return 6;
            case 15048:
                return 7;
            case 15056:
                return 8;
            case 15064:
                return 9;
            case 15072:
                return 10;
        }
        return 0;
    }

    public static String getStatStringbyPalier(Player player,int palier) {
        if(player.getClasse() == CLASS_SACRIEUR){
            switch (palier - 1) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 6:
                case 8:
                    return "7d#190#0#0#0d0+400,7c#32#0#0#0d0+50,76#28#0#0#0d0+40,7e#28#0#0#0d0+40,7b#28#0#0#0d0+40,77#28#0#0#0d0+40,70#3#0#0#0d0+3,8a#1e#0#0#0d0+30,b2#5#0#0#0d0+5,73#3#0#0#0d0+3,d6#1#0#0#0d0+1,d2#1#0#0#0d0+1,d5#1#0#0#0d0+1,d3#1#0#0#0d0+1,d4#1#0#0#0d0+1";
                case 5:
                    return "7d#190#0#0#0d0+400,7c#32#0#0#0d0+50,76#28#0#0#0d0+40,7e#28#0#0#0d0+40,7b#28#0#0#0d0+40,77#28#0#0#0d0+40,70#3#0#0#0d0+3,8a#1e#0#0#0d0+30,b2#5#0#0#0d0+5,73#3#0#0#0d0+3,d6#5#0#0#0d0+5,d2#5#0#0#0d0+5,d5#5#0#0#0d0+5,d3#5#0#0#0d0+5,d4#5#0#0#0d0+5,6f#1#0#0#0d0+1,75#2#0#0#0d0+2,b6#1#0#0#0d0+1";
                case 7:
                    return "7d#190#0#0#0d0+400,7c#32#0#0#0d0+50,76#28#0#0#0d0+40,7e#28#0#0#0d0+40,7b#28#0#0#0d0+40,77#28#0#0#0d0+40,70#3#0#0#0d0+3,8a#1e#0#0#0d0+30,b2#5#0#0#0d0+5,73#3#0#0#0d0+3,d6#5#0#0#0d0+5,d2#5#0#0#0d0+5,d5#5#0#0#0d0+5,d3#5#0#0#0d0+5,d4#5#0#0#0d0+5,6f#1#0#0#0d0+1,75#2#0#0#0d0+2,b6#1#0#0#0d0+1,80#1#0#0#0d0+1";
                case 9:
                    return "7d#4e2#0#0#0d0+1250,7c#32#0#0#0d0+50,76#28#0#0#0d0+40,7e#28#0#0#0d0+40,7b#28#0#0#0d0+40,77#28#0#0#0d0+40,70#3#0#0#0d0+3,8a#1e#0#0#0d0+30,b2#5#0#0#0d0+5,73#3#0#0#0d0+3,d6#1#0#0#0d0+1,d2#1#0#0#0d0+1,d5#1#0#0#0d0+1,d3#1#0#0#0d0+1,d4#1#0#0#0d0+1,6f#1#0#0#0d0+1,7d8#64#0#0#0d0+100";
            }
        }
        else {
            switch (palier - 1) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 6:
                case 8:
                    return "7d#fa#0#0#0d0+250,7c#32#0#0#0d0+50,76#32#0#0#0d0+50,7e#32#0#0#0d0+50,7b#32#0#0#0d0+50,77#32#0#0#0d0+50,70#3#0#0#0d0+3,8a#32#0#0#0d0+50,b2#5#0#0#0d0+5,73#3#0#0#0d0+3,d6#1#0#0#0d0+1,d2#1#0#0#0d0+1,d5#1#0#0#0d0+1,d3#1#0#0#0d0+1,d4#1#0#0#0d0+1";
                case 5:
                    return "7d#fa#0#0#0d0+250,7c#32#0#0#0d0+50,76#32#0#0#0d0+50,7e#32#0#0#0d0+50,7b#32#0#0#0d0+50,77#32#0#0#0d0+50,70#3#0#0#0d0+3,8a#1e#0#0#0d0+30,b2#5#0#0#0d0+5,73#3#0#0#0d0+3,d6#5#0#0#0d0+5,d2#5#0#0#0d0+5,d5#5#0#0#0d0+5,d3#5#0#0#0d0+5,d4#5#0#0#0d0+5,6f#1#0#0#0d0+1,b6#1#0#0#0d0+1,75#2#0#0#0d0+2";
                case 7:
                    return "7d#fa#0#0#0d0+250,7c#32#0#0#0d0+50,76#32#0#0#0d0+50,7e#32#0#0#0d0+50,7b#32#0#0#0d0+50,77#32#0#0#0d0+50,70#3#0#0#0d0+3,8a#1e#0#0#0d0+30,b2#5#0#0#0d0+5,73#3#0#0#0d0+3,d6#5#0#0#0d0+5,d2#5#0#0#0d0+5,d5#5#0#0#0d0+5,d3#5#0#0#0d0+5,d4#5#0#0#0d0+5,6f#1#0#0#0d0+1,80#1#0#0#0d0+1,b6#1#0#0#0d0+1,75#2#0#0#0d0+2";
                case 9:
                    return "7d#fa#0#0#0d0+250,7c#32#0#0#0d0+50,76#32#0#0#0d0+50,7e#32#0#0#0d0+50,7b#32#0#0#0d0+50,77#32#0#0#0d0+50,70#3#0#0#0d0+3,8a#1e#0#0#0d0+30,b2#5#0#0#0d0+5,73#3#0#0#0d0+3,d6#1#0#0#0d0+1,d2#1#0#0#0d0+1,d5#1#0#0#0d0+1,d3#1#0#0#0d0+1,d4#1#0#0#0d0+1,6f#1#0#0#0d0+1,7d8#64#0#0#0d0+100";
            }
        }
        return "";
    }


    public static int getClasseByMorphWeapon(int MorphWeapon) {
        int classe = MorphWeapon-12781;
        return classe;
    }


    public static final int[] TONIQUE1 = {16002,16003,16004,16005,16006,16007,16008,16009,16010,16011,16012};
    public static final int[] TONIQUE2 = {16013,16014,16015,16016,16017,16018,16019,16020,16021,16022,16023};

    public static int[] getToniques3byclasse(int classeid) {
        int[] tonique3 = new int[]{};
        switch (classeid){
            case Constant.CLASS_FECA:
                tonique3 = new int[]{16027,16028,16029,16030,16031,16032,16033,16034,16035,16036,16037,16038,16039,16040,16041,16042,16043,16044,16045,16046,16024,16025,16026};
                break;
            case Constant.CLASS_OSAMODAS:
                tonique3 = new int[]{16047,16048,16049,16050,16051,16052,16053,16054,16055,16056,16057,16058,16059,16060,16061,16062,16063,16064,16065,16066,16024,16025,16026};
                break;
            case Constant.CLASS_ENUTROF:
                tonique3 = new int[]{16067,16068,16069,16070,16071,16072,16073,16074,16075,16076,16077,16078,16079,16080,16081,16082,16083,16084,16085,16086,16024,16025,16026};
                break;
            case Constant.CLASS_SRAM:
                tonique3 = new int[]{16087,16088,16089,16090,16091,16092,16093,16094,16095,16096,16097,16098,16099,16100,16101,16102,16103,16104,16105,16106,16024,16025,16026};
                break;
            case Constant.CLASS_XELOR:
                tonique3 = new int[]{16107,16108,16109,16110,16111,16112,16113,16114,16115,16116,16117,16118,16119,16120,16121,16122,16123,16124,16125,16126,16024,16025,16026};
                break;
            case Constant.CLASS_ECAFLIP:
                tonique3 = new int[]{16127,16128,16129,16130,16131,16132,16133,16134,16135,16136,16137,16138,16139,16140,16141,16142,16143,16144,16145,16146,16024,16025,16026};
                break;
            case Constant.CLASS_ENIRIPSA:
                tonique3 = new int[]{16147,16148,16149,16150,16151,16152,16153,16154,16155,16156,16157,16158,16159,16160,16161,16162,16163,16164,16165,16166,16024,16025,16026};
                break;
            case Constant.CLASS_IOP:
                tonique3 = new int[]{16167,16168,16169,16170,16171,16172,16173,16174,16175,16176,16177,16178,16179,16180,16181,16182,16183,16184,16185,16186,16024,16025,16026};
                break;
            case Constant.CLASS_CRA:
                tonique3 = new int[]{16187,16188,16189,16190,16191,16192,16193,16194,16195,16196,16197,16198,16199,16200,16201,16202,16203,16204,16205,16206,16024,16025,16026};
                break;
            case Constant.CLASS_SADIDA:
                tonique3 = new int[]{16207,16208,16209,16210,16211,16212,16213,16214,16215,16216,16217,16218,16219,16220,16221,16222,16223,16224,16225,16226,16024,16025,16026};
                break;
            case Constant.CLASS_SACRIEUR:
                tonique3 = new int[]{16227,16228,16229,16230,16231,16232,16233,16234,16235,16236,16237,16238,16239,16240,16241,16242,16243,16244,16245,16246,16024,16025,16026};
                break;
            case Constant.CLASS_PANDAWA:
                tonique3 = new int[]{16247,16248,16249,16250,16251,16252,16253,16254,16255,16256,16257,16258,16259,16260,16261,16262,16263,16264,16265,16266,16024,16025,16026};
                break;
        }

        return tonique3;
    }

    public static Integer getRandomGemmesSpritiuels() {
        ArrayList<Integer> gemmespi = new ArrayList<>();

        for(int i= 10227; i<=10270;i++){
            gemmespi.add(i);
        }
        gemmespi.add(10278);
        gemmespi.add(10606);
        gemmespi.add(11567);
        gemmespi.add(11568);

        Random rand = new Random();
        int randomIndex = rand.nextInt(gemmespi.size());
        int randomNum = gemmespi.get(randomIndex);

        return randomNum;
    }

    public static int[] getPositionByItemType(int type) {
        switch (type) {
            case Constant.ITEM_TYPE_FAMILIER:
                return new int[]{Constant.ITEM_POS_FAMILIER};
            case Constant.ITEM_TYPE_COIFFE:
                return new int[]{Constant.ITEM_POS_COIFFE};
            case Constant.ITEM_TYPE_CAPE:
                return new int[]{Constant.ITEM_POS_CAPE};
            case Constant.ITEM_TYPE_ANNEAU:
                return new int[]{Constant.ITEM_POS_ANNEAU1, Constant.ITEM_POS_ANNEAU2};
            case Constant.ITEM_TYPE_CEINTURE:
                return new int[]{Constant.ITEM_POS_CEINTURE};
            case Constant.ITEM_TYPE_AMULETTE:
                return new int[]{Constant.ITEM_POS_AMULETTE};
            case Constant.ITEM_TYPE_BOTTES:
                return new int[]{Constant.ITEM_POS_BOTTES};

            case Constant.ITEM_TYPE_BOUCLIER:
                return new int[]{Constant.ITEM_POS_BOUCLIER};

            case Constant.ITEM_TYPE_DOFUS:
                return new int[]{Constant.ITEM_POS_DOFUS1, Constant.ITEM_POS_DOFUS2, Constant.ITEM_POS_DOFUS3,
                        Constant.ITEM_POS_DOFUS4, Constant.ITEM_POS_DOFUS5, Constant.ITEM_POS_DOFUS6};

            case Constant.ITEM_TYPE_ARC:
            case Constant.ITEM_TYPE_EPEE:
            case Constant.ITEM_TYPE_DAGUES:
            case Constant.ITEM_TYPE_BATON:
            case Constant.ITEM_TYPE_FAUX:
            case Constant.ITEM_TYPE_PELLE:
            case Constant.ITEM_TYPE_HACHE:
            case Constant.ITEM_TYPE_BAGUETTE:
                return new int[]{Constant.ITEM_POS_ARME};
            case Constant.ITEM_TYPE_TONIQUE:
                return new int[]{Constant.ITEM_POS_TONIQUE_EQUILIBRAGE,Constant.ITEM_POS_TONIQUE1,Constant.ITEM_POS_TONIQUE2,Constant.ITEM_POS_TONIQUE3,Constant.ITEM_POS_TONIQUE4,Constant.ITEM_POS_TONIQUE5,Constant.ITEM_POS_TONIQUE6,Constant.ITEM_POS_TONIQUE7,Constant.ITEM_POS_TONIQUE8,Constant.ITEM_POS_TONIQUE9};
            default:
                return new int[]{};
        }
    }

}