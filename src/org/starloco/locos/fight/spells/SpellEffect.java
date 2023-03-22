package org.starloco.locos.fight.spells;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.HashMap;

import org.starloco.locos.client.Player;
import org.starloco.locos.common.CryptManager;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.entity.monster.Monster.MobGrade;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.fight.traps.Glyph;
import org.starloco.locos.fight.traps.Trap;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.util.TimerWaiter;

public class SpellEffect {

	private final int durationFixed;
	private int effectID;
	private int turns = 0;
	private String jet = "0d0+0";
	private int chance = 100;
	private String args;
	private int value = 0;
	private Fighter caster = null;
	private int spell = 0;
	private int spellLvl = 1;
	private boolean debuffable = true;
	private int duration = 0;
	private GameCase cell = null;
    private Map<Fighter, Integer> sacriersToHeal = new HashMap<>();
    
	public static final byte COLOR_NEUTRE = 0;
	 public static final byte COLOR_TERRE = 1;
	 public static final byte COLOR_FEU = 2;
	 public static final byte COLOR_EAU = 3;
	 public static final byte COLOR_AIR = 4;

	public SpellEffect(int aID, String aArgs, int aSpell, int aSpellLevel) {
		effectID = aID;
		args = aArgs;
		spell = aSpell;
		spellLvl = aSpellLevel;
		durationFixed = 0;
		try {
			value = Integer.parseInt(args.split(";")[0]);
			turns = Integer.parseInt(args.split(";")[3]);
			chance = Integer.parseInt(args.split(";")[4]);
			jet = args.split(";")[5];
		} catch (Exception ignored) {
		}
	}

	public SpellEffect(int id, int value2, int aduration, int turns2, boolean debuff, Fighter aCaster, String args2, int aspell) {
		effectID = id;
		value = value2;
		turns = turns2;
		debuffable = debuff;
		caster = aCaster;
		duration = aduration;
		this.durationFixed = duration;
		args = args2;
		spell = aspell;
		try {
			jet = args.split(";")[5];
		} catch (Exception ignored) {
		}
	}

	public static ArrayList<Fighter> getTargets(SpellEffect SE, Fight fight, ArrayList<GameCase> cells) {
		ArrayList<Fighter> cibles = new ArrayList<Fighter>();
		for (GameCase aCell : cells) {
			if (aCell == null) continue;
			Fighter f = aCell.getFirstFighter();
			if (f == null) continue;
			cibles.add(f);
		}
		return cibles;
	}

	//public static int applyOnHitBuffs(int finalDommage, Fighter target, Fighter caster, Fight fight, int elementId) {
	// By Coding Mestre - [FIX] Fixed some issues left unchecked from the previous commit Close #36
	public int applyOnHitBuffs(int finalDommage, Fighter target, Fighter caster, Fight fight, int elementId) {
		for (int id : Constant.ON_HIT_BUFFS) {
			for (SpellEffect buff : target.getBuffsByEffectID(id)) {
				switch (id) {
					case 114:
							finalDommage = finalDommage * value;
						break;
					case 138:
						if (buff.getSpell() == 1039) {
							int stats = 0;
							if (elementId == Constant.ELEMENT_AIR)
								stats = 217;
							else if (elementId == Constant.ELEMENT_EAU)
								stats = 216;
							else if (elementId == Constant.ELEMENT_FEU)
								stats = 218;
							else if (elementId == Constant.ELEMENT_NEUTRE)
								stats = 219;
							else if (elementId == Constant.ELEMENT_TERRE)
								stats = 215;
							int val = target.getBuff(stats).getValue();
							int turns = target.getBuff(stats).getTurn();
							int duration = target.getBuff(stats).getDurationFixed();
							String args = target.getBuff(stats).getArgs();
							int[] oppositeStats = Constant.getOppositeStats(stats);
							if(oppositeStats != null) {
								for (int i : oppositeStats)
									target.addBuff(i, val, turns, duration, true, buff.getSpell(), args, caster, false);
							}
							target.addBuff(stats, val, duration, turns, true, buff.getSpell(), args, caster, false);

						}
						break;
					case 9://Derobade
						//Si pas au cac (distance == 1)
						int d = PathFinding.getDistanceBetween(fight.getMap(), target.getCell().getId(), caster.getCell().getId());
						if (d > 1) continue;
						int chan = buff.getValue();
						int c = Formulas.getRandomValue(0, 99);
						if (c + 1 >= chan) continue;//si le deplacement ne s'applique pas
						int nbrCase = 0;
						try {
							nbrCase = Integer.parseInt(buff.getArgs().split(";")[1]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (nbrCase == 0) continue;
						int exCase = target.getCell().getId();
						int newCellID = PathFinding.newCaseAfterPush(fight, caster.getCell(), target.getCell(), nbrCase);
						if (newCellID < 0)//S'il a été bloqué
						{
							int a = -newCellID;
							a = nbrCase - a;
							newCellID = PathFinding.newCaseAfterPush(fight, caster.getCell(), target.getCell(), a);
							if (newCellID == 0)
								continue;
							if (fight.getMap().getCase(newCellID) == null)
								continue;
						}
						target.getCell().getFighters().clear();
						target.setCell(fight.getMap().getCase(newCellID));
						target.getCell().addFighter(target);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 5, target.getId() + "", target.getId() + "," + newCellID);

						Trap.doTraps(target.getFight(), target);
						//si le joueur a bouger
						if (exCase != newCellID)
							finalDommage = 0;
						break;

					case 79://chance éca
						try {
							String[] infos = buff.getArgs().split(";");
							int coefDom = Integer.parseInt(infos[0]);
							int coefHeal = Integer.parseInt(infos[1]);
							int chance = Integer.parseInt(infos[2]);
							int jet = Formulas.getRandomValue(0, 99);

							if (jet < chance)//Soin
							{
								finalDommage = -(finalDommage * coefHeal);
								if (-finalDommage > (target.getPdvMax() - target.getPdv()))
									finalDommage = -(target.getPdvMax() - target.getPdv());
							} else//Dommage
								finalDommage = finalDommage * coefDom;
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;

					case 107://renvoie Dom
					case 220:
						if (target.getId() == caster.getId()) break;

						if (caster.hasBuff(765))//sacrifice
						{
							if (caster.getBuff(765) != null && !caster.getBuff(765).getCaster().isDead()) {
								buff.applyEffect_765B(fight, caster);
								caster = caster.getBuff(765).getCaster();
							}
						}

						String[] args = buff.getArgs().split(";");
						float coef = 1 + (target.getTotalStats().getEffect(Constant.STATS_ADD_SAGE) / 100);
						int renvoie = 0;
						try {
							if (Integer.parseInt(args[1]) != -1) {
								renvoie = (int) (coef * Formulas.getRandomValue(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
							} else {
								renvoie = (int) (coef * Integer.parseInt(args[0]));
							}
						} catch (Exception e) {
							return finalDommage;
						}
						if (renvoie > finalDommage) renvoie = finalDommage;
						finalDommage -= renvoie;
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 107, "-1", target.getId() + "," + renvoie);
						if (renvoie > caster.getPdv()) renvoie = caster.getPdv();
						if (finalDommage < 0) finalDommage = 0;
						caster.removePdv(caster, renvoie);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId() + "", caster.getId() + ",-" + renvoie);
						break;
					case 606://Chatiment (acncien)
						int stat = buff.getValue();
						int jet = Formulas.getRandomJet(buff.getJet());
						target.addBuff(stat, jet, -1, -1, false, buff.getSpell(), buff.getArgs(), caster, false);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + jet + "," + -1);
						break;
					case 607://Chatiment (acncien)
					case 611:
					case 609:
					case 608:
						stat = buff.getValue();
						jet = Formulas.getRandomJet(buff.getJet());
						target.addBuff(stat, jet, -1, -1, false, buff.getSpell(), buff.getArgs(), caster, false);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + jet + "," + -1);
						break;
					case 788://Chatiments
						//int taux = (caster.getPersonnage() == null ? 1 : 2), gain = finalDommage / taux, max = 0;
						// Bu Coding Mestre : [FIX] Fixed some issues left unchecked from the previous commit Close #36
                        int casterTeam = caster.getTeam();
                        int targetTeam = target.getTeam();
                        // if they are in the same team we must only apply half the buff value, otherwise apply the full buff
                        int taux = (casterTeam != targetTeam ? 1 : 2), gain = finalDommage / taux, max = 0;

						stat = buff.getValue();

						try {
							max = Integer.parseInt(buff.getArgs().split(";")[1]);
						} catch (Exception e) {
							e.printStackTrace();
							continue;
						}

						//on retire au max possible la valeur déjà gagné sur le chati
						int oldValue = (target.getChatiValue().get(stat) == null ? 0 : target.getChatiValue().get(stat));
						max -= oldValue;
						//Si gain trop grand, on le reduit au max
						if (gain > max) gain = max;
						//On met a jour les valeurs des chatis
						int newValue = oldValue + gain;

						if (stat == 125) {
							//SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + gain + "," + 5);
							// By Coding Mestre - [FIX] - Fixed sacrier's spell vital punishment Close #36
							 //SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId() + "", target.getId() + "," + gain + ',' + COLOR_FEU);
							//target.setPdv(target.getPdv() + gain);
							//if(target.getPersonnage() != null) SocketManager.GAME_SEND_STATS_PACKET(target.getPersonnage());
							this.sacriersToHeal.put(target, gain);
						} else {
							target.getChatiValue().put(stat, newValue);
							//target.addBuff(stat, gain, 5, 1, false, buff.getSpell(), buff.getArgs(), caster, false);
							//SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + gain + "," + 5);
							// By Coding Mestre : [FIX] Fixed some issues left unchecked from the previous commit Close #36
							 if (gain > 0) {
	                                target.addBuff(stat, gain, 5, 1, false, buff.getSpell(), buff.getArgs(), caster, false);
	                                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, stat, caster.getId() + "", target.getId() + "," + gain + "," + 5);
	                            }

						}
						target.getChatiValue().put(stat, newValue);
						break;

					default:
						break;
				}
			}
		}

		return finalDommage;
	}
	
	
	// By Coding Mestre - [FIX] - Xelor's clock spell can now also remove 2 AP on critical strikes Close #54
    public int applyReflectDamage(Fighter target, Fighter caster, int currentDamage) {
        int newDamage = currentDamage;

        for (SpellEffect buff : target.getBuffsByEffectID(107)) {
            if (target.getId() == caster.getId()) break;

            String[] args = buff.getArgs().split(";");
            float coef = 1 + (target.getTotalStats().getEffect(Constant.STATS_ADD_SAGE) / 100);
            int renvoie = 0;
            try {
                if (Integer.parseInt(args[1]) != -1) {
                    renvoie = (int) (coef * Formulas.getRandomValue(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
                } else {
                    renvoie = (int) (coef * Integer.parseInt(args[0]));
                }
            } catch (Exception e) {
                return newDamage;
            }
            if (renvoie > newDamage) renvoie = newDamage;
            newDamage -= renvoie;
            if (newDamage < 0) newDamage = 0;
        }
        return newDamage;
    }


	public int getDuration() {
		return duration;
	}

	public int getTurn() {
		return turns;
	}

	public void setTurn(int turn) {
		this.turns = turn;
	}

	public boolean isDebuffabe() {
		return debuffable;
	}

	public int getEffectID() {
		return effectID;
	}

	public void setEffectID(int id) {
		effectID = id;
	}

	public String getJet() {
		return jet;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int i) {
		value = i;
	}

	public int getChance() {
		return chance;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String newArgs) {
		args = newArgs;
	}

	public int getMaxMinSpell(Fighter fighter, int value) {
		int val = value;
		if (fighter.hasBuff(782)) {
			int max = Integer.parseInt(args.split(";")[1]);
			if (max == -1)
				max = Integer.parseInt(args.split(";")[0]);
			val = max;
		} else if (fighter.hasBuff(781))
			val = Integer.parseInt(args.split(";")[0]);
		return val;
	}

	public int decrementDuration() {
		duration -= 1;
		return duration;
	}

	public void applyBeginingBuff(Fight fight, Fighter fighter) {
		ArrayList<Fighter> targets = new ArrayList<>();
		targets.add(fighter);
		this.turns = -1;
		this.applyToFight(fight, this.caster, targets, false);
	}

	public void applyToFight(Fight fight, Fighter perso, GameCase Cell, ArrayList<Fighter> cibles) {
		cell = Cell;
		applyToFight(fight, perso, cibles, false);
	}

	private int getDurationFixed() {
		return this.durationFixed;
	}

	public Fighter getCaster() {
		return caster;
	}

	public int getSpell() {
		return spell;
	}

	public void applyToFight(Fight fight, Fighter acaster, ArrayList<Fighter> cibles, boolean isCaC) {
		try {
			if (turns != -1)//Si ce n'est pas un buff qu'on applique en début de tour
				turns = Integer.parseInt(args.split(";")[3]);
		} catch (NumberFormatException ignored) {}
		if(acaster != null) {
			if (acaster.isDead()) {
				return;
			}
		}
		else {
			return;
		}
		if(cibles != null) {
			ArrayList<Fighter> verifiedFighter = new ArrayList<>();
			for (Fighter fighter : cibles) {
				if (!fighter.isDead())
					verifiedFighter.add(fighter);
			}
			cibles = verifiedFighter;
		}
		else{
			return;
		}
		caster = acaster;
		try {
			jet = args.split(";")[5];
		} catch (Exception ignored) {}

		if (caster.getPersonnage() != null) {
			Player perso = caster.getPersonnage();
			if (perso.getItemClasseSpell().containsKey(spell)) {
				int modi = 0;
				if (effectID == 108)
					modi = perso.getItemClasseModif(spell, Constant.STATS_SPELL_ADD_HEAL);
				else if (effectID >= 91 && effectID <= 100)
					modi = perso.getItemClasseModif(spell, Constant.STATS_SPELL_ADD_DOM);
				String jeta = jet.split("\\+")[0];
				int bonus = Integer.parseInt(jet.split("\\+")[1]) + modi;
				jet = jeta + "+" + bonus;
			}
		}

		switch (effectID) {
			case 4://Fuite/Bond du félin/ Bond du iop / téléport
				applyEffect_4(fight, cibles);
				break;
			case 5://Repousse de X case
				applyEffect_5(cibles, fight);
				break;
			case 6://Attire de X case
				applyEffect_6(cibles, fight);
				break;
			case 8://Echange les place de 2 joueur
				applyEffect_8(cibles, fight);
				break;
			case 9://Esquive une attaque en reculant de 1 case
				applyEffect_9(cibles, fight);
				break;
			case 50://Porter
				applyEffect_50(fight);
				break;
			case 51://jeter
				applyEffect_51(fight);
				break;
			case 77://Vol de PM
				applyEffect_77(cibles, fight);
				break;
			case 78://Bonus PM
				applyEffect_78(cibles, fight);
				break;
			case 79:// + X chance(%) dommage subis * Y sinon soigné de dommage *Z
				applyEffect_79(cibles, fight);
				break;
			case 81:// Soin
				applyEffect_81(cibles, fight);
				break;
			case 82://Vol de Vie fixe
				applyEffect_82(cibles, fight);
				break;
			case 84://Vol de PA
				applyEffect_84(cibles, fight);
				break;
			case 85://Dommage Eau %vie
				applyEffect_85(cibles, fight);
				break;
			case 86://Dommage Terre %vie
				applyEffect_86(cibles, fight);
				break;
			case 87://Dommage Air %vie
				applyEffect_87(cibles, fight);
				break;
			case 88://Dommage feu %vie
				applyEffect_88(cibles, fight);
				break;
			case 89://Dommage neutre %vie
				applyEffect_89(cibles, fight);
				break;
			case 90://Donne X% de sa vie
				applyEffect_90(cibles, fight);
				break;
			case 91://Vol de Vie Eau
				applyEffect_91(cibles, fight, isCaC);
				break;
			case 92://Vol de Vie Terre
				applyEffect_92(cibles, fight, isCaC);
				break;
			case 93://Vol de Vie Air
				applyEffect_93(cibles, fight, isCaC);
				break;
			case 94://Vol de Vie feu
				applyEffect_94(cibles, fight, isCaC);
				break;
			case 95://Vol de Vie neutre
				applyEffect_95(cibles, fight, isCaC);
				break;
			case 96://Dommage Eau
				applyEffect_96(cibles, fight, isCaC);
				break;
			case 97://Dommage Terre
				applyEffect_97(cibles, fight, isCaC);
				break;
			case 98://Dommage Air
				applyEffect_98(cibles, fight, isCaC);
				break;
			case 99://Dommage feu
				applyEffect_99(cibles, fight, isCaC);
				break;
			case 100://Dommage neutre
				applyEffect_100(cibles, fight, isCaC);
				break;
			case 101://Retrait PA
				applyEffect_101(cibles, fight);
				break;
			case 105://Dommages réduits de X
				applyEffect_105(cibles, fight);
				break;
			case 106://Renvoie de sort
				applyEffect_106(cibles, fight);
				break;
			case 107://Renvoie de dom
				applyEffect_107(cibles, fight);
				break;
			case 108://Soin
				applyEffect_108(cibles, fight, isCaC);
				break;
			case 109://Dommage pour le lanceur
				applyEffect_109(fight);
				break;
			case 110://+ X vie
				applyEffect_110(cibles, fight);
				break;
			case 111://+ X PA
				applyEffect_111(cibles, fight);
				break;
			case 112://+Dom
				applyEffect_112(cibles, fight);
				break;
			case 114://Multiplie les dommages par X
				applyEffect_114(cibles, fight);
				break;
			case 115://+Cc
				applyEffect_115(cibles, fight);
				break;
			case 116://Malus PO
				applyEffect_116(cibles, fight);
				break;
			case 117://Bonus PO
				applyEffect_117(cibles, fight);
				break;
			case 118://Bonus force
				applyEffect_118(cibles, fight);
				break;
			case 119://Bonus Agilité
				applyEffect_119(cibles, fight);
				break;
			case 120://Bonus PA
				applyEffect_120(cibles, fight);
				break;
			case 121://+Dom
				applyEffect_121(cibles, fight);
				break;
			case 122://+EC
				applyEffect_122(cibles, fight);
				break;
			case 123://+Chance
				applyEffect_123(cibles, fight);
				break;
			case 124://+Sagesse
				applyEffect_124(cibles, fight);
				break;
			case 125://+Vitalité
				applyEffect_125(cibles, fight);
				break;
			case 126://+Intelligence
				applyEffect_126(cibles, fight);
				break;
			case 127://Retrait PM
				applyEffect_127(cibles, fight);
				break;
			case 128://+PM
				applyEffect_128(cibles, fight);
				break;
			case 130://Vol de kamas
				applyEffect_130(fight, cibles);
				break;
			case 131://Poison : X Pdv  par PA
				applyEffect_131(cibles, fight);
				break;
			case 132://Enleve les envoutements
				applyEffect_132(cibles, fight);
				break;
			case 138://%dom
				applyEffect_138(cibles, fight);
				break;
			case 140://Passer le tour
				applyEffect_140(cibles, fight);
				break;
			case 141://Tue la cible
				applyEffect_141(fight, cibles);
				break;
			case 142://Dommages physique
				applyEffect_142(fight, cibles);
				break;
			case 143:// PDV rendu
				applyEffect_143(cibles, fight);
				break;
			case 144:// - Dommages (pas boosté)
				applyEffect_144(fight, cibles);
			case 145://Malus Dommage
				applyEffect_145(fight, cibles);
				break;
			case 149://Change l'apparence
				applyEffect_149(fight, cibles);
				break;
			case 150://Invisibilité
				applyEffect_150(fight, cibles);
				break;
			case 152:// - Chance
				applyEffect_152(fight, cibles);
				break;
			case 153:// - Vita
				applyEffect_153(fight, cibles);
				break;
			case 154:// - Agi
				applyEffect_154(fight, cibles);
				break;
			case 155:// - Intel
				applyEffect_155(fight, cibles);
				break;
			case 156:// - Sagesse
				applyEffect_156(fight, cibles);
				break;
			case 157:// - Force
				applyEffect_157(fight, cibles);
				break;
			case 160:// + Esquive PA
				applyEffect_160(fight, cibles);
				break;
			case 161:// + Esquive PM
				applyEffect_161(fight, cibles);
				break;
			case 162:// - Esquive PA
				applyEffect_162(fight, cibles);
				break;
			case 163:// - Esquive PM
				applyEffect_163(fight, cibles);
				break;
			case 164:// Réduction de dégâts en x%
				applyEffect_164(cibles, fight);
				break;
			case 165:// Maîtrises
				applyEffect_165(fight, cibles);
				break;
			case 168://Perte PA non esquivable
				applyEffect_168(cibles, fight);
				break;
			case 169://Perte PM non esquivable
				applyEffect_169(cibles, fight);
				break;
			case 171://Malus CC
				applyEffect_171(fight, cibles);
				break;
			case 176:// + prospection
				applyEffect_176(cibles, fight);
				break;
			case 177:// - prospection
				applyEffect_177(cibles, fight);
				break;
			case 178:// + soin
				applyEffect_178(cibles, fight);
				break;
			case 179:// - soin
				applyEffect_179(cibles, fight);
				break;
			case 180://Double du sram
				applyEffect_180(fight);
				break;
			case 181://Invoque une créature
				applyEffect_181(fight);
				break;
			case 182://+ Crea Invoc
				applyEffect_182(fight, cibles);
				break;
			case 183://Resist Magique
				applyEffect_183(fight, cibles);
				break;
			case 184://Resist Physique
				applyEffect_184(fight, cibles);
				break;
			case 185://Invoque une creature statique
				applyEffect_185(fight);
				break;
			case 186://Diminue les dommages %
				applyEffect_186(fight, cibles);
				break;
			case 202://Perception
				applyEffect_202(fight, cibles);
				break;
			case 210://Resist % terre
				applyEffect_210(fight, cibles);
				break;
			case 211://Resist % eau
				applyEffect_211(fight, cibles);
				break;
			case 212://Resist % air
				applyEffect_212(fight, cibles);
				break;
			case 213://Resist % feu
				applyEffect_213(fight, cibles);
				break;
			case 214://Resist % neutre
				applyEffect_214(fight, cibles);
				break;
			case 215://Faiblesse % terre
				applyEffect_215(fight, cibles);
				break;
			case 216://Faiblesse % eau
				applyEffect_216(fight, cibles);
				break;
			case 217://Faiblesse % air
				applyEffect_217(fight, cibles);
				break;
			case 218://Faiblesse % feu
				applyEffect_218(fight, cibles);
				break;
			case 219://Faiblesse % neutre
				applyEffect_219(fight, cibles);
				break;
			case 220:// Renvoie dommage
				applyEffect_220(cibles, fight);
				break;
			case 265://Reduit les Dom de X
				applyEffect_265(fight, cibles);
				break;
			case 266://Vol Chance
				applyEffect_266(fight, cibles);
				break;
			case 267://Vol vitalité
				applyEffect_267(fight, cibles);
				break;
			case 268://Vol agitlité
				applyEffect_268(fight, cibles);
				break;
			case 269://Vol intell
				applyEffect_269(fight, cibles);
				break;
			case 270://Vol sagesse
				applyEffect_270(fight, cibles);
				break;
			case 271://Vol force
				applyEffect_271(fight, cibles);
				break;
			case 279: // Dommage vie manquante attaquant%
				applyEffect_279(fight, cibles);
			case 293://Augmente les dégâts de base du sort X de Y
				applyEffect_293(fight);
				break;
			case 320://Vol de PO
				applyEffect_320(fight, cibles);
				break;
			case 400://Créer un  piège
				applyEffect_400(fight);
				break;
			case 401://Créer une glyphe
				applyEffect_401(fight);
				break;
			case 402://Glyphe des Blop
				applyEffect_402(fight);
				break;
			/*case 606://Ancien chati
			case 607:
			case 608:
			case 609:
			case 611:
				applyEffect_606To611(cibles, fight);
				break;*/
			case 666://Pas d'effet complémentaire
				break;
			case 671://Dommages : X% de la vie de l'attaquant (neutre)
				applyEffect_671(cibles, fight);
				break;
			case 672://Dommages : X% de la vie de l'attaquant (neutre)
				applyEffect_672(cibles, fight);
				break;
			case 765://Sacrifice
				applyEffect_765(cibles, fight);
				break;
			case 776://Enleve %vita pendant l'attaque
				applyEffect_776(cibles, fight);
				break;
			case 780://laisse spirituelle
				applyEffect_780(fight);
				break;
			case 781://Minimize les effets aléatoires
				applyEffect_781(cibles, fight);
				break;
			case 782://Maximise les effets aléatoires
				applyEffect_782(cibles, fight);
				break;
			case 783://Pousse jusqu'a la case visé
				applyEffect_783(cibles, fight);
				break;
			case 784://Raulebaque
				applyEffect_784(cibles, fight);
				break;
			case 786://Soin pendant l'attaque
				applyEffect_786(cibles, fight);
				break;
			case 787://Change etat
				applyEffect_787(cibles, fight);
				break;
			case 788://Chatiment de X sur Y tours
				applyEffect_788(cibles, fight);
				break;
			case 950://Etat X
				applyEffect_950(fight, cibles);
				break;
			case 951://Enleve l'Etat X
				applyEffect_951(fight, cibles);
				break;
			case 1000: // Glyph pair
				applyEffect_1000(fight, cibles);
				break;
			case 1001: // Glyph impair
				applyEffect_1001(fight, cibles);
				break;
			case 1002: // Glyph kaskargo
				applyEffect_1002(fight, cibles);
				break;
			default:
				GameServer.a();
				break;
		}
		 postProcessSpellEffect(fight);
    }

    private void postProcessSpellEffect(Fight fight) {
        checkSacriersVitalPunishmentHeal(fight);

	}


    private void checkSacriersVitalPunishmentHeal(Fight fight) {
        sacriersToHeal.forEach((sacrier, amountToHeal) -> {
            if (amountToHeal > 0) {
                sacrier.setPdv(sacrier.getPdv() + amountToHeal);
                SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId() + "", sacrier.getId() + "," + amountToHeal + ',' + COLOR_FEU);
                //    if (sacrierToHeal.getPersonnage() != null)
                //        SocketManager.GAME_SEND_STATS_PACKET(sacrierToHeal.getPersonnage());
            }
        });
        sacriersToHeal.clear();
    }



	
	private void applyEffect_4(Fight fight, ArrayList<Fighter> cibles) {
		if (turns > 1)
			return;//Olol bondir 3 tours apres ?

		if (cell.isWalkable(true) && !fight.isOccuped(cell.getId()))//Si la case est prise, on va eviter que les joueurs se montent dessus *-*
		{
			caster.getCell().getFighters().clear();
			caster.setCell(cell);
			caster.getCell().addFighter(caster);
			
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, caster.getId()
					+ "", caster.getId() + "," + cell.getId());
			
			this.checkTraps(fight, caster, (short) 1200);
		}
	}

	private void applyEffect_5(ArrayList<Fighter> cibles, Fight fight) {
		if (cibles.size() == 1 && spell == 120 || spell == 310)
			if (!cibles.get(0).isDead())
				caster.setOldCible(cibles.get(0));

		if (turns <= 0) {
			switch (spell) {
				case 73://Piège répulsif
				case 418://Flèche de dispersion
				case 151://Soufle
				case 165://Flèche enflammé
					cibles = this.trierCibles(cibles, fight);
					break;
			}


			for (Fighter target : cibles) {
				boolean next = false;
				if (target.getMob() != null)
					for (int i : Constant.STATIC_INVOCATIONS)
						if (i == target.getMob().getTemplate().getId())
							next = true;

				if (target.haveState(6) || next)
					continue;

				GameCase cell = this.cell;

				if (target.getCell().getId() == this.cell.getId() || spell == 73)
					cell = caster.getCell();

				int newCellId = PathFinding.newCaseAfterPush(fight, cell, target.getCell(), value);

				if (newCellId == 0)
					return;
				if (newCellId < 0) {
					int a = -newCellId, factor = Formulas.getRandomJet("1d8+1"); // 2 à 9
					double b = (caster.isInvocation() ? caster.getInvocator().getLvl() : caster.getLvl()) / 50;
					if (b < 0.1) b = 0.1;

					int finalDmg = (int) (factor * b * a);

					if (finalDmg < 1) finalDmg = 1;
					if (finalDmg > target.getPdv()) finalDmg = target.getPdv();

					if (target.hasBuff(184)) {
						finalDmg = finalDmg - target.getBuff(184).getValue();//Réduction physique
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(184).getValue());
					}
					if (target.hasBuff(105)) {
						finalDmg = finalDmg - target.getBuff(105).getValue();//Immu
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 105, caster.getId() + "", target.getId() + "," + target.getBuff(105).getValue());
					}
					if (finalDmg > 0) {
						if(finalDmg > 200) finalDmg = Formulas.getRandomValue(189, 211);
						target.removePdv(caster, finalDmg);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId() + "", target.getId() + ",-" + finalDmg);
						if (target.getPdv() <= 0) {
							fight.onFighterDie(target, caster);
							if (target.canPlay() && target.getPersonnage() != null) fight.endTurn(false);
							else if (target.canPlay()) target.setCanPlay(false);
							return;
						}
					}
					a = value - a;
					newCellId = PathFinding.newCaseAfterPush(fight, caster.getCell(), target.getCell(), a);

					char dir = PathFinding.getDirBetweenTwoCase(cell.getId(), target.getCell().getId(), fight.getMap(), true);
					GameCase nextCase = fight.getMap().getCase(PathFinding.GetCaseIDFromDirrection(target.getCell().getId(), dir, fight.getMap(), true));

					if (nextCase != null && nextCase.getFirstFighter() != null) {
						Fighter wallTarget = nextCase.getFirstFighter();
						finalDmg = finalDmg / 2;
						if (finalDmg < 1) finalDmg = 1;
						if (finalDmg > 0) {
							wallTarget.removePdv(caster, finalDmg);
							SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId() + "", wallTarget.getId() + ",-" + finalDmg);
							if (wallTarget.getPdv() <= 0)
								fight.onFighterDie(wallTarget, caster);
						}
					}

					if (newCellId == 0)
						continue;
					if (fight.getMap().getCase(newCellId) == null)
						continue;
				}

				target.getCell().getFighters().clear();
				target.setCell(fight.getMap().getCase(newCellId));
				target.getCell().addFighter(target);

				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 5, caster.getId() + "", target.getId() + "," + newCellId);
				
				Trap.doTraps(fight, target);
			}
		}
	}

	private void applyEffect_6(ArrayList<Fighter> cibles, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : cibles) {
				if (target.getMob() != null)
					if (282 == target.getMob().getTemplate().getId() ||
							556 == target.getMob().getTemplate().getId()
							|| 2750 == target.getMob().getTemplate().getId()
							|| 7000 == target.getMob().getTemplate().getId())
						continue;
				if (target.haveState(6))
					continue;
				GameCase eCell = cell;
				//Si meme case
				if (target.getCell().getId() == cell.getId()) {
					//on prend la cellule caster
					eCell = caster.getCell();
				}
				int newCellID = PathFinding.newCaseAfterPush(fight, eCell, target.getCell(), -value);
				if (newCellID == 0)
					continue;

				if (newCellID < 0)//S'il a été bloqué
				{
					int a = -(value + newCellID);
					newCellID = PathFinding.newCaseAfterPush(fight, caster.getCell(), target.getCell(), a);
					if (newCellID == 0)
						continue;
					if (fight.getMap().getCase(newCellID) == null)
						continue;
				}

				target.getCell().getFighters().clear();
				target.setCell(fight.getMap().getCase(newCellID));
				target.getCell().addFighter(target);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 5, caster.getId()
						+ "", target.getId() + "," + newCellID);
				
				this.checkTraps(fight, target, (short) 1500);
			}
		}
	}

	private void applyEffect_8(ArrayList<Fighter> cibles, Fight fight) {
		if (cibles.isEmpty())
			return;
		Fighter target = cibles.get(0);
		if (target == null)
			return;//ne devrait pas arriver
		if (target.haveState(6))
			return;//Stabilisation
		switch (spell) {
			case 438://Transpo
				//si les 2 joueurs ne sont pas dans la meme team, on ignore
				if (target.getTeam() != caster.getTeam())
					return;
				break;

			case 445://Coop
				//si les 2 joueurs sont dans la meme team, on ignore
				if (target.getTeam() == caster.getTeam())
					return;
				break;

			case 449://Détour
			default:
				break;
		}
		//on enleve les persos des cases
		target.getCell().getFighters().clear();
		caster.getCell().getFighters().clear();
		//on retient les cases
		GameCase exTarget = target.getCell();
		GameCase exCaster = caster.getCell();
		//on échange les cases
		target.setCell(exCaster);
		caster.setCell(exTarget);
		//on ajoute les fighters aux cases
		target.getCell().addFighter(target);
		caster.getCell().addFighter(caster);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, caster.getId()
				+ "", target.getId() + "," + exCaster.getId());
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, caster.getId()
				+ "", caster.getId() + "," + exTarget.getId());
		
		this.checkTraps(fight, target, (short) 1200);
		this.checkTraps(fight, caster, (short) 1200);

	}

	private void applyEffect_9(ArrayList<Fighter> cibles, Fight fight) {
		for (Fighter target : cibles) {
			target.addBuff(effectID, value, turns, 1, true, spell, args, caster, true);
		}
	}

	  private void applyEffect_50(Fight fight) //Karcham
	  {
	    Fighter target=cell.getFirstFighter();
	    if(target==null||target.isDead()||target.haveState(Constant.ETAT_PORTEUR)||target.haveState(Constant.ETAT_PORTE)||target.haveState(Constant.ETAT_ENRACINE))
	      return;
	    if(target.getMob()!=null)
	      for(int i : Constant.STATIC_INVOCATIONS)
	        if(i==target.getMob().getTemplate().getId())
	          return;

	    target.getCell().getFighters().clear();
	    target.setCell(caster.getCell());
	    target.setState(Constant.ETAT_PORTE,-1,caster.getId()); //infinite duration
	    caster.setState(Constant.ETAT_PORTEUR,-1,caster.getId()); //infinite duration
	    target.setHoldedBy(caster);
	    caster.setIsHolding(target);
	    if(target.haveState(Constant.STATE_SOBER))
	    {
	      target.setHadSober(true);
	      target.setState(Constant.STATE_SOBER,0,caster.getId()); //duration 0, remove state
	    }
	    SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight,7,950,target.getId()+"",target.getId()+","+Constant.ETAT_PORTE+",1");
	    SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight,7,950,caster.getId()+"",caster.getId()+","+Constant.ETAT_PORTEUR+",1");
	    SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight,7,50,caster.getId()+"",""+target.getId());
	  }

	private void applyEffect_51(final Fight fight) {
		//Si case pas libre
		if (!cell.isWalkable(true) || cell.getFighters().size() > 0) return;
		Fighter target = caster.getIsHolding();
		if (target == null) return;
		
		//on ajoute le porté a sa case
		target.setCell(cell);
		target.getCell().addFighter(target);
		
		//on enleve les états
		target.setState(Constant.ETAT_PORTE,0,caster.getId()); //infinite duration
	    caster.setState(Constant.ETAT_PORTEUR,0,caster.getId()); //infinite duration
	    
		//on dé-lie les 2 Fighter
		target.setHoldedBy(null);
		caster.setIsHolding(null);
		
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 51, caster.getId() + "", cell.getId() + "");
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, target.getId() + "", target.getId() + "," + Constant.ETAT_PORTE + ",0");
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", caster.getId() + "," + Constant.ETAT_PORTEUR + ",0");
		
		this.checkTraps(fight, target, (short) 1500);
	}

	private void applyEffect_77(ArrayList<Fighter> cibles, Fight fight) {
		int value = 1;
		try {
			value = Integer.parseInt(args.split(";")[0]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		int num = 0;
		for (Fighter target : cibles) {
			int val = Formulas.getPointsLost('m', value, caster, target);

			if (val < value)
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 309, caster.getId() + "", target.getId() + "," + (value - val));
			if (val < 1)
				continue;
			if(turns <= 0) {
				target.addBuff(Constant.STATS_REM_PM, val, 1, 1, true, spell, args, caster, false);
			}
			else {
				target.addBuff(Constant.STATS_REM_PM, val, turns, turns, true, spell, args, caster, false);
			}
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_PM, caster.getId() + "", target.getId() + ",-" + val + "," + turns);
			num += val;
		}
		if (num != 0) {
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_ADD_PM, caster.getId() + "", caster.getId() + "," + num + "," + turns);
			if(turns <= 0) {
				caster.addBuff(Constant.STATS_ADD_PM, num, 1, 1, true, spell, args, caster, false);
			}
			else {
				caster.addBuff(Constant.STATS_ADD_PM, num, turns, turns, true, spell, args, caster, false);
			}
			//Gain de PM pendant le tour de jeu
			if (caster.canPlay())
				caster.setCurPm(fight, num);
		}
	}

	private void applyEffect_78(ArrayList<Fighter> cibles, Fight fight)//Bonus PA
	{
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_79(ArrayList<Fighter> cibles, Fight fight) {
		if (turns < 1)
			return;//Je vois pas comment, vraiment ...
		else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, -1, turns, 0, true, spell, args, caster, true);//on applique un buff
			}
		}
	}

	private void applyEffect_81(ArrayList<Fighter> cibles, Fight fight) {// Heal
		if (turns <= 0) {
			String[] jet = args.split(";");
			int heal = 0;
			if (jet.length < 6) {
				heal = 1;
			} else {
				heal = Formulas.getRandomJet(jet[5]);
			}
			int heal2 = heal;
			for (Fighter cible : cibles) {
				if (cible.isDead())
					continue;
				if (spell == 521)// ruse kistoune
					if (cible.getTeam2() != caster.getTeam2())
						continue;
				if(cible.hasBuff(87)){
					if(cible.getBuff(87).spell == 1009) {
						cible.addBuff(87, 50, 0, 0, false, 1009, args, cible.getBuff(87).getCaster(), false);
					}
				}
				heal = getMaxMinSpell(cible, heal);
				int pdvMax = cible.getPdvMax();
				int healFinal = Formulas.calculFinalHealCac(caster, heal, false);
				if ((healFinal + cible.getPdv()) > pdvMax)
					healFinal = pdvMax - cible.getPdv();
				if (healFinal < 1)
					healFinal = 0;
				cible.removePdv(caster, -healFinal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId()
						+ "", cible.getId() + "," + healFinal+ "," + COLOR_FEU);
				heal = heal2;
			}
		} else {
			for (Fighter cible : cibles) {
				if (cible.isDead())
					continue;
				cible.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);
			}
		}
	}

	private void applyEffect_82(ArrayList<Fighter> cibles, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : cibles) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				//si la cible a le buff renvoie de sort et que le sort peut etre renvoyer
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				//int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_NULL, dmg, false, false, spell);
				int finalDommage = dmg;
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs spéciaux
				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage);
				//Vol de vie
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
						+ "", caster.getId() + "," + heal+ "," + COLOR_FEU);

				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, true);//on applique un buff
			}
		}
	}

	private void applyEffect_84(ArrayList<Fighter> cibles, Fight fight) {
		int value = 1;
		// By Coding Mestre - [FIX] - Xelor's clock spell can now also remove 2 AP on critical strikes Close #54
		int secondaryValue = -1;
	    String[] argsSplit = args.split(";");
	    boolean hasSecondaryValue = argsSplit.length > 1;
		try {
			//value = Integer.parseInt(args.split(";")[0]);
			// By Coding Mestre - [FIX] - Xelor's clock spell can now also remove 2 AP on critical strikes Close #54
			secondaryValue = hasSecondaryValue && !argsSplit[1].equalsIgnoreCase("-1")
                    ? Integer.parseInt(argsSplit[1]) : secondaryValue;
            if (secondaryValue != -1) value = Formulas.getRandomValue(value, secondaryValue);
            else value = Integer.parseInt(argsSplit[0]);

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		int num = 0;
		for (Fighter target : cibles) {
			int val = Formulas.getPointsLost('a', value, caster, target); // m to a Coding MEstre
			if (val < value)
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 308, caster.getId()
						+ "", target.getId() + "," + (value - val));

			if (val < 1)
				continue;
			if(turns <= 0) {
				target.addBuff(Constant.STATS_REM_PA, val, 1, 1, true, spell, args, caster, false);
			}
			else {
				target.addBuff(Constant.STATS_REM_PA, val, turns, turns, true, spell, args, caster, false);
			}
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_PA, caster.getId()
					+ "", target.getId() + ",-" + val + "," + turns);
			if(target.getMob() != null){
				verifmobs(fight, target, Constant.STATS_REM_PA, 0);
			}
			num += val;
		}
		if (num != 0) {
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_ADD_PA, caster.getId()
					+ "", caster.getId() + "," + num + "," + turns);
			if(turns <= 0) {
				caster.addBuff(Constant.STATS_ADD_PA, num, 1, 1, true, spell, args, caster, false);
			}
			else {
				caster.addBuff(Constant.STATS_ADD_PA, num, turns, turns, true, spell, args, caster, false);
			}
			//Gain de PA pendant le tour de jeu
			if (caster.canPlay())
				caster.setCurPa(fight, num);
		}
	}

	private void applyEffect_85(ArrayList<Fighter> cibles, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : cibles) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_EAU);
				int resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_EAU);
				if (target.getPersonnage() != null)//Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_EAU);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_EAU);
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);//%age de pdv infligé
				int val = caster.getPdv() / 100 * dmg;//Valeur des dégats
				//retrait de la résist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;//Reduc %resis
				val -= reduc;
				if (val < 0)
					val = 0;

				val = applyFinalDamage(caster, val);
				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs spéciaux

				if (val > target.getPdv())
					val = target.getPdv();//Target va mourrir
				target.removePdv(caster, val);
				int cura = val;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura + ',' + COLOR_FEU);
				}
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + val + ',' + COLOR_EAU);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_86(ArrayList<Fighter> cibles, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : cibles) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_TER);
				int resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_TER);
				if (target.getPersonnage() != null)//Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_TER);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_TER);
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);//%age de pdv infligé
				int val = caster.getPdv() / 100 * dmg;//Valeur des dégats
				//retrait de la résist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;//Reduc %resis
				val -= reduc;
				if (val < 0)
					val = 0;

				val = applyFinalDamage(caster, val);
				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs spéciaux

				if (val > target.getPdv())
					val = target.getPdv();//Target va mourrir
				target.removePdv(caster, val);
				int cura = val;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura + ',' + COLOR_FEU);
				}
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + val + ',' + COLOR_TERRE);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_87(ArrayList<Fighter> cibles, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : cibles) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				if (spell == 1009)
					if (LaunchedSpell.haveEffectTarget(fight.getTeam0(), target, 108) <= 0)
						continue;

				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_AIR);
				int resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_AIR);
				if (target.getPersonnage() != null)//Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_AIR);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_AIR);
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);//%age de pdv infligé
				int val = caster.getPdv() / 100 * dmg;//Valeur des dégats
				//retrait de la résist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;//Reduc %resis
				val -= reduc;
				if (val < 0)
					val = 0;

				val = applyFinalDamage(caster, val);
				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs spéciaux

				if (val > target.getPdv())
					val = target.getPdv();//Target va mourrir
				target.removePdv(caster, val);
				int cura = val;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura + ',' + COLOR_FEU);
				}
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + val + ',' + COLOR_AIR);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_88(ArrayList<Fighter> cibles, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : cibles) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_FEU);
				int resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_FEU);
				if (target.getPersonnage() != null)//Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_FEU);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_FEU);
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);//%age de pdv infligé
				int val = caster.getPdv() / 100 * dmg;//Valeur des dégats
				//retrait de la résist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;//Reduc %resis
				val -= reduc;
				if (val < 0)
					val = 0;

				val = applyFinalDamage(caster, val);
				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs spéciaux

				if (val > target.getPdv())
					val = target.getPdv();//Target va mourrir
				target.removePdv(caster, val);
				int cura = val;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura + ',' + COLOR_FEU);
				}
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + val + ',' + COLOR_FEU);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_89(ArrayList<Fighter> cibles, Fight fight) {
		if (turns <= 0) {
			for (Fighter target : cibles) {

				if (spell == 106) target = caster; //QUICKFIX Ecaflip's wheel of fortune spell - By Coding Mestre
				
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_NEU);
				int resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_NEU);
				if (target.getPersonnage() != null)//Si c'est un joueur, on ajoute les resists bouclier
				{
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_NEU);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_NEU);
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);//%age de pdv infligé
				int val = caster.getPdv() / 100 * dmg;//Valeur des dégats
				//retrait de la résist fixe
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;//Reduc %resis
				val -= reduc;

				if (val < 0)
					val = 0;

				val = applyFinalDamage(caster, val);
				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs spéciaux

				if (val > target.getPdv())
					val = target.getPdv();//Target va mourrir
				target.removePdv(caster, val);
				int cura = val;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura + ',' + COLOR_FEU);
				}
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + val + ',' + COLOR_NEUTRE);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_90(ArrayList<Fighter> cibles, Fight fight) {
		if (turns <= 0)//Si Direct
		{
			int pAge = Formulas.getRandomJet(args.split(";")[5]);
			int val = pAge * (caster.getPdv() / 100);
			//Calcul des Doms recus par le lanceur
			int finalDommage = applyOnHitBuffs(val, caster, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs spéciaux

			if (finalDommage > caster.getPdv())
				finalDommage = caster.getPdv();//Caster va mourrir
			caster.removePdv(caster, finalDommage);
			finalDommage = -(finalDommage);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
					+ "", caster.getId() + "," + finalDommage + ',' + COLOR_NEUTRE);

			//Application du soin
			for (Fighter target : cibles) {
				if ((val + target.getPdv()) > target.getPdvMax())
					val = target.getPdvMax() - target.getPdv();//Target va mourrir
				target.removePdv(caster, -val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + ",+" + val + ',' + COLOR_FEU);
			}
			if (caster.getPdv() <= 0)
				fight.onFighterDie(caster, caster);
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_91(ArrayList<Fighter> cibles, Fight fight,
								boolean isCaC)//vole eau
	{
		if (isCaC) {
			for (Fighter target : cibles) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_EAU, dmg, false, true, spell);

				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_EAU);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir

				finalDommage = applyFinalDamage(caster, finalDommage);

				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_EAU);
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
						+ "", caster.getId() + "," + heal+ "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 91, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : cibles) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_EAU, dmg, false, false, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_EAU);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_EAU);
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
						+ "", caster.getId() + "," + heal+ "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 91, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_92(ArrayList<Fighter> cibles, Fight fight,
								boolean isCaC)//vole terre
	{
		if (caster.isHide())
			caster.unHide(spell);
		if (isCaC) {
			for (Fighter target : cibles) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_TERRE, dmg, false, true, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_TERRE);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir

				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_TERRE );
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
						+ "", caster.getId() + "," + heal+ "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 92, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : cibles) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_TERRE, dmg, false, false, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_TERRE);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_TERRE);
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
						+ "", caster.getId() + "," + heal+ "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 92, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_93(ArrayList<Fighter> cibles, Fight fight,
								boolean isCaC)//vole air
	{
		if (caster.isHide())
			caster.unHide(spell);
		if (isCaC) {
			for (Fighter target : cibles) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_AIR, dmg, false, true, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_AIR);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir

				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_AIR );
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
						+ "", caster.getId() + "," + heal+ "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 93, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : cibles) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_AIR, dmg, false, false, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_AIR);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_AIR);

				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
						+ "", caster.getId() + "," + heal+ "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 93, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_94(ArrayList<Fighter> cibles, Fight fight,
								boolean isCaC) {
		if (caster.isHide())
			caster.unHide(spell);
		if (isCaC)//CaC feu
		{
			for (Fighter target : cibles) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_FEU, dmg, false, true, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_FEU);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir

				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_FEU);
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
						+ "", caster.getId() + "," + heal+ "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 94, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : cibles) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_FEU, dmg, false, false, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_FEU);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_FEU);
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
						+ "", caster.getId() + "," + heal+ "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 94, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_95(ArrayList<Fighter> cibles, Fight fight,
								boolean isCaC) {
		if (caster.isHide())
			caster.unHide(spell);
		if (isCaC)//CaC Eau
		{
			for (Fighter target : cibles) {
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_NEUTRE, dmg, false, true, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);

				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_NEUTRE);
				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
						+ "", caster.getId() + "," + heal+ "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 95, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : cibles) {

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_NEUTRE, dmg, false, false, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_NEUTRE);

				int heal = (int) (-finalDommage) / 2;
				if ((caster.getPdv() + heal) > caster.getPdvMax())
					heal = caster.getPdvMax() - caster.getPdv();
				caster.removePdv(caster, -heal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
						+ "", caster.getId() + "," + heal);
				if (target.getMob() != null)
					verifmobs(fight, target, 95, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, target);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_96(ArrayList<Fighter> cibles, Fight fight,
								boolean isCaC)//dmg eau
	{
		if (isCaC)//CaC Eau
		{
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : cibles) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2())
						&& !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(args.split(";")[5]);

				//Si le sort est boosté par un buff spécifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_EAU, dmg, false, true, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_EAU);//S'il y a des buffs spéciaux
				if(target.hasBuff(138)){
					SpellEffect renDo = target.getBuff(138);
					int jet = Formulas.getRandomJet(renDo.getJet());

				}

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				int cura = finalDommage;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_EAU );
				if (target.getMob() != null)
					verifmobs(fight, target, 96, cura);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : cibles) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2())
						&& !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(args.split(";")[5]);

				//Si le sort est boosté par un buff spécifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_EAU, dmg, false, false, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_EAU);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				int cura = finalDommage;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_EAU);
				if (target.getMob() != null)
					verifmobs(fight, target, 96, cura);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles)
				target.addBuff(effectID, 0, turns, 1, true, spell, args, caster, false);//on applique un buff
		}
	}

	private void applyEffect_97(ArrayList<Fighter> cibles, Fight fight,
								boolean isCaC)//dmg terre
	{
		if (isCaC)//CaC Terre
		{
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : cibles) {
				if (caster.isMob()) {
					if (caster.getTeam2() == target.getTeam2() && !caster.isInvocation())
						continue; // Les monstres de s'entretuent pas
				}

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null && !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(args.split(";")[5]);

				//Si le sort est boosté par un buff spécifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_TERRE, dmg, false, true, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_TERRE);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				int cura = finalDommage;

				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}

				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_TERRE);
				if (target.getMob() != null)
					verifmobs(fight, target, 97, cura);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : cibles) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2()) && !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort

				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}

				int dmg = Formulas.getRandomJet(this.jet);

				//Si le sort est boosté par un buff spécifique
				if (caster.hasBuff(293) || caster.haveState(300)) {
					if (caster.haveState(300))
						caster.setState(300,0,caster.getId());
					for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
						if (SE == null)
							continue;
						if (SE.getValue() == spell) {
							int add = -1;
							try {
								add = Integer.parseInt(SE.getArgs().split(";")[2]);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (add <= 0)
								continue;
							dmg += add;
						}
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_TERRE, dmg, false, false, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_TERRE);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				int cura = finalDommage;

				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_TERRE );
				if (target.getMob() != null)
					verifmobs(fight, target, 97, cura);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}

			}
		} else {
			if (spell == 470) {
				for (Fighter target : cibles) {
					if (target.getTeam() == caster.getTeam())
						continue;
					target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
				}
			}
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_98(ArrayList<Fighter> cibles, Fight fight,
								boolean isCaC)//dmg air
	{
		if (isCaC)//CaC Air
		{
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : cibles) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2())
						&& !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(this.jet);

				//Si le sort est boosté par un buff spécifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}

				// applyEffect_142

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_AIR, dmg, false, true, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_AIR);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				int cura = finalDommage;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_AIR);
				if (target.getMob() != null)
					verifmobs(fight, target, 98, cura);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			if (caster.isHide())
				caster.unHide(spell);
			for (Fighter target : cibles) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2())
						&& !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(this.jet);

				//Si le sort est boosté par un buff spécifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_AIR, dmg, false, false, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_AIR);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir

				target.removePdv(caster, finalDommage);
				int cura = finalDommage;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_AIR );
				if (target.getMob() != null)
					verifmobs(fight, target, 98, cura);

				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_99(ArrayList<Fighter> cibles, Fight fight,
								boolean isCaC)//dmg feu
	{
		if (caster.isHide())
			caster.unHide(spell);

		if (isCaC)//CaC Feu
		{
			for (Fighter target : cibles) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2())
						&& !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(this.jet);

				//Si le sort est boosté par un buff spécifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_FEU, dmg, false, true, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_FEU);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				int cura = finalDommage;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 99, cura);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : cibles) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2())
						&& !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas
				if (spell == 36 && target == caster)//Frappe du Craqueleur ne tape pas l'osa
				{
					continue;
				}

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1");
					//le lanceur devient donc la cible
					target = caster;
				}
				int dmg = Formulas.getRandomJet(this.jet);

				//Si le sort est boosté par un buff spécifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_FEU, dmg, false, false, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_FEU);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				int cura = finalDommage;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_FEU);
				if (target.getMob() != null)
					verifmobs(fight, target, 99, cura);
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_100(ArrayList<Fighter> cibles, Fight fight,
								 boolean isCaC) { //Dommages Neutre
		if (caster.isHide())
			caster.unHide(spell);
		if (fight.getType() == 7)
			return;
		if (isCaC)//CaC Neutre
		{
			for (Fighter target : cibles) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2())
						&& !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas
				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}

				int dmg = Formulas.getRandomJet(this.jet);

				//Si le sort est boosté par un buff spécifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}
				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_NEUTRE, dmg, false, true, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				int cura = finalDommage;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_NEUTRE);
				if (target.getMob() != null)
					verifmobs(fight, target, 100, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else if (turns <= 0) {
			for (Fighter target : cibles) {
				if (caster.isMob() && (caster.getTeam2() == target.getTeam2())
						&& !caster.isInvocation())
					continue; // Les monstres de s'entretuent pas

				if (target.hasBuff(765))//sacrifice
				{
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				//si la cible a le buff renvoie de sort
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl
						&& spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
							+ "", target.getId() + ",1"); // le lanceur devient donc la cible
					target = caster;
				}
				if (spell == 2000 && caster.isInvocation()) {
					target.setState(7, 1,caster.getId());
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
							+ "", target.getId() + "," + 7 + ",1");
					target.addBuff(950, 1, 1, 1, false, spell, args, target, true);
				}

				int dmg = Formulas.getRandomJet(this.jet);

				//Si le sort est boosté par un buff spécifique
				for (SpellEffect SE : caster.getBuffsByEffectID(293)) {
					if (SE.getValue() == spell) {
						int add = -1;
						try {
							add = Integer.parseInt(SE.getArgs().split(";")[2]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (add <= 0)
							continue;
						dmg += add;
					}
				}

				int finalDommage = Formulas.calculFinalDommage(fight, caster, target, Constant.ELEMENT_NEUTRE, dmg, false, false, spell);

				finalDommage = applyFinalDamage(caster, finalDommage);
				finalDommage = applyOnHitBuffs(finalDommage, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs spéciaux

				if (finalDommage > target.getPdv())
					finalDommage = target.getPdv();//Target va mourrir
				target.removePdv(caster, finalDommage);
				int cura = finalDommage;
				if (target.hasBuff(786)) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}
				finalDommage = -(finalDommage);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + finalDommage + "," + COLOR_NEUTRE);
				if (target.getMob() != null)
					verifmobs(fight, target, 100, (-finalDommage));
				if (target.getPdv() <= 0) {
					fight.onFighterDie(target, caster);
					if (target.canPlay() && target.getPersonnage() != null)
						fight.endTurn(false);
					else if (target.canPlay())
						target.setCanPlay(false);
				}
			}
		} else {
			for (Fighter target : cibles) {
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
			}
		}
	}

	private void applyEffect_101(ArrayList<Fighter> cibles, Fight fight) {
		if(spell != 470) {
			for (Fighter target : cibles) {
				int remove = Formulas.getPointsLost('a', value, caster, target);
				if ((value - remove) > 0)
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 308, caster.getId() + "", target.getId() + "," + (value - remove));
				if (remove > 0) {
					if (turns <= 0)
						target.addBuff(Constant.STATS_REM_PA, remove, 1, 1, false, spell, args, caster, false);
					else
						target.addBuff(Constant.STATS_REM_PA, remove, turns, turns, false, spell, args, caster, false);
					if (turns <= 1 || duration <= 1)
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_PA, target.getId() + "", target.getId() + ",-" + remove);
				}

				if (fight.getFighterByOrdreJeu() == target)
					fight.setCurFighterPa(fight.getCurFighterPa() - remove);

				if (target.getMob() != null) {
					this.verifmobs(fight, target, Constant.STATS_REM_PA, 0);
				}
			}
		}
		else {
			if (turns < 4) {
				int secondaryValue = -1;
				String[] argsSplit = args.split(";");
				int pointLost = Integer.parseInt(argsSplit[0]);
				int firstValue = Integer.parseInt(argsSplit[0]);
				boolean hasSecondaryValue = argsSplit.length > 1;
				try {
					if(hasSecondaryValue){
						if(!argsSplit[1].equalsIgnoreCase("-1")){
							secondaryValue = Integer.parseInt(argsSplit[1]);
						}
					}
					if (secondaryValue > pointLost){
						pointLost = Formulas.getRandomValue(firstValue, secondaryValue);
						if(pointLost > secondaryValue){
							pointLost = secondaryValue;
						}
					}
					else {
						pointLost = firstValue;
					}
				} catch (Exception ex) {
					pointLost = firstValue;
				}
				for(Fighter target : cibles) {
					int remove = Formulas.getPointsLost('a', pointLost, caster, target);
					if ((pointLost - remove) > 0)
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 308, caster.getId() + "", target.getId() + "," + (pointLost - remove));
					if(remove > 0) {
						target.addBuff(Constant.STATS_REM_PA, remove, 1, 1, true, spell, args, caster, false);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_PA, target.getId() + "", target.getId() + ",-" + remove);
						if (fight.getFighterByOrdreJeu() == target)
							fight.setCurFighterPa(fight.getCurFighterPa() - remove);
					}
					if (target.getMob() != null) {
						this.verifmobs(fight, target, Constant.STATS_REM_PA, 0);
					}
				}

			}
			else {
				for (Fighter target : cibles) {
					target.addBuff(effectID, 0, turns, -1, true, spell, args, caster, false);//on applique un buff
				}
			}
		}
	}

	private void applyEffect_105(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
		}
	}

	private void applyEffect_106(ArrayList<Fighter> cibles, Fight fight) {
		int val = -1;
		try {
			val = Integer.parseInt(args.split(";")[1]);//Niveau de sort max
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (val == -1)
			return;

		this.duration = turns;
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
		}
	}

	private void applyEffect_107(ArrayList<Fighter> cibles, Fight fight) {
		if (turns < 1)
			return;//Je vois pas comment, vraiment ...
		else {
			for (Fighter target : cibles)
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, true);//on applique un buff
		}
	}

	private void applyEffect_108(ArrayList<Fighter> cibles, Fight fight, boolean isCaC) {// healcion
		if (spell == 441) return;
		if (isCaC) return;
		if (turns <= 0) {
			String[] jet = args.split(";");
			int heal = 0;
			if (jet.length < 6) {
				heal = 1;
			} else {
				heal = Formulas.getRandomJet(jet[5]);
			}
			int heal2 = heal;
			for (Fighter cible : cibles) {
				if (cible.isDead())
					continue;
				if (caster.hasBuff(178))
					heal += caster.getBuffValue(178);
				if (caster.hasBuff(179))
					heal = heal - caster.getBuffValue(179);
				heal = getMaxMinSpell(cible, heal);
				int pdvMax = cible.getPdvMax();
				int healFinal = Formulas.calculFinalHealCac(caster, heal, isCaC);
				if ((healFinal + cible.getPdv()) > pdvMax)
					healFinal = pdvMax - cible.getPdv();
				if (healFinal < 1)
					healFinal = 0;
				cible.removePdv(caster, -healFinal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId() + "", cible.getId() + "," + healFinal + ',' + COLOR_FEU);
				heal = heal2;
				if(cible.hasBuff(87)){
					if(cible.getBuff(87).spell == 1009) {
						cible.addBuff(87, 50, 0, 0, false, 1009, cible.getBuff(87).args, cible.getBuff(87).getCaster(), false);
					}
				}
			}
		} else {
			cibles.stream().filter(target -> !target.isDead()).forEach(target -> target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false));
		}
	}

	private void applyEffect_109(Fight fight)//Dommage pour le lanceur (fixes)
	{
		if (turns <= 0) {
			int dmg = Formulas.getRandomJet(this.jet);
			int finalDommage = Formulas.calculFinalDommage(fight, caster, caster, Constant.ELEMENT_NULL, dmg, false, false, spell);

			finalDommage = applyFinalDamage(caster, finalDommage);
			finalDommage = applyOnHitBuffs(finalDommage, caster, caster, fight, Constant.ELEMENT_NULL);//S'il y a des buffs spéciaux
			if (finalDommage > caster.getPdv())
				finalDommage = caster.getPdv();//Caster va mourrir
			caster.removePdv(caster, finalDommage);
			finalDommage = -(finalDommage);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
					+ "", caster.getId() + "," + finalDommage + "," + COLOR_NEUTRE);

			if (caster.getPdv() <= 0) {
				fight.onFighterDie(caster, caster);

			}
		} else {
			caster.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
		}
	}

	private void applyEffect_110(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			if(turns <= 0) {
				target.addBuff(effectID, val, 1, 1, true, spell, args, caster, false);
			}
			else {
				target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			}
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_111(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		boolean repetibles = false;
		int lostPA = 0;
		for (Fighter target : cibles) {
			if (spell == 89 && target.getTeam() != caster.getTeam()) {
				continue;
			}
			if (spell == 101 && target != caster) {
				continue;
			}
			if (spell == 115) {// odorat
				if (!repetibles) {
					lostPA = Formulas.getRandomJet(jet);
					if (lostPA == -1)
						continue;
					value = lostPA;
				}
				target.addBuff(effectID, value, turns, turns, true, spell, args, caster, false);
				repetibles = true;
				if (target.canPlay() && target == caster)
					target.setCurPa(fight, val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
						+ "", target.getId() + "," + val + "," + turns);
				continue;
			}

			if (spell == 101)
				turns = 1;
			if (spell == 521)// ruse kistoune
				if (target.getTeam2() != caster.getTeam2())
					continue;
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			//Gain de PA pendant le tour de jeu
			if (target.canPlay() && target == caster)
				target.setCurPa(fight, val);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_112(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		if (duration < 1)
			duration = 1;
		if (spell == 1090) {
			caster.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", caster.getId() + "," + val + "," + turns);
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_114(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		boolean modif = false;
		/* (spell) {
			case 521:
				for (Fighter target : cibles) {
					if (target.getTeam2() != caster.getTeam2())
						continue;
					target.addBuff(effectID, 2, turns, 1, true, spell, args, caster, true);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
							+ "", target.getId() + "," + 2 + "," + turns);
				}
				modif = true;
				break;
			case 542:
				for (Fighter target : cibles) {
					target.addBuff(effectID, 2, turns, 1, true, spell, args, caster, true);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
							+ "", target.getId() + "," + 2 + "," + turns);
				}
				modif = true;
				break;
		}*/

		if (!modif)
			for (Fighter target : cibles) {
				target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
						+ "", target.getId() + "," + val + "," + turns);
			}
	}

	private void applyEffect_115(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_116(ArrayList<Fighter> cibles, Fight fight)//Malus PO
	{
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_117(ArrayList<Fighter> cibles, Fight fight)//Bonus PO
	{
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
			//Gain de PO pendant le tour de jeu
			if (target.canPlay() && target == caster)
				target.getTotalStats().addOneStat(Constant.STATS_ADD_PO, val);
		}
	}

	private void applyEffect_118(ArrayList<Fighter> cibles, Fight fight)//Bonus Force
	{
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		if (spell == 52)//cupiditer
			cibles = fight.getFighters(3);

		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId() + "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_119(ArrayList<Fighter> cibles, Fight fight)//Bonus Agilité
	{
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_120(ArrayList<Fighter> cibles, Fight fight)//Bonus PA
	{
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		caster.addBuff(111, val, turns, 1, true, spell, args, caster, false);
		caster.setCurPa(fight, val);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 111, caster.getId()
				+ "", caster.getId() + "," + val + "," + turns);
	}

	private void applyEffect_121(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_122(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_123(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_124(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_125(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) return;



		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_126(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		if (spell == 52)//cupiditer
			cibles = fight.getFighters(3);

		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_127(ArrayList<Fighter> cibles, Fight fight) {
		for (Fighter target : cibles) {
			int retrait = Formulas.getPointsLost('m', value, caster, target);
			if ((value - retrait) > 0)
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 309, caster.getId()
						+ "", target.getId() + "," + (value - retrait));
			if (retrait > 0) {
				if (turns <= 0) {
					target.addBuff(effectID, retrait, 1, 1, false, spell, args, caster, false);
				} else {
					target.addBuff(effectID, retrait, turns, turns, false, spell, args, caster, false);
				}
				if (turns <= 1 || duration <= 1)
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_PM, target.getId()
							+ "", target.getId() + ",-" + retrait);
			}
			if (retrait > 0)
				if (target.getMob() != null)
					this.verifmobs(fight, target, Constant.STATS_REM_PM, 0);
		}
	}

	private void applyEffect_128(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		boolean repetibles = false;
		int lostPM = 0;
		for (Fighter target : cibles) {
			if (spell == 115) {// odorat
				if (!repetibles) {
					lostPM = Formulas.getRandomJet(jet);
					if (lostPM == -1)
						continue;
					value = lostPM;
				}
				target.addBuff(effectID, value, turns, turns, true, spell, args, caster, false);
				repetibles = true;
				if (target.canPlay() && target == caster)
					target.setCurPm(fight, val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
						+ "", target.getId() + "," + val + "," + turns);
				continue;
			} else if (spell == 521)// ruse kistoune
				if (target.getTeam2() != caster.getTeam2())
					continue;
			if(turns <= 0) {
				target.addBuff(effectID, val, 1, 1, true, spell, args, caster, false);
			}
			else {
				target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			}
			//Gain de PM pendant le tour de jeu
			if (target.canPlay() && target == caster)
				target.setCurPm(fight, val);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_130(Fight fight, ArrayList<Fighter> cibles) {
		if (turns <= 0) {
			for (Fighter target : cibles) {
				int kamas = Formulas.getRandomJet(this.jet);
				if (caster.getPersonnage() == null) break;
				if (target.getPersonnage() != null) {
					target.getPersonnage().addKamas(-kamas);
					if (target.getPersonnage().getKamas() < 0)
						target.getPersonnage().setKamas(0);
				}
				if (target.getMob() != null) {
					switch (target.getMob().getTemplate().getId()) {
						case 494:
							break;

						default:
							caster.getPersonnage().addKamas(kamas);
							break;
					}
				} else {
					caster.getPersonnage().addKamas(kamas);
				}

				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 130, caster.getId() + "", kamas + "");
			}
		} else {
			for (Fighter target : cibles)
				target.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);//on applique un buff
		}
	}

	private void applyEffect_131(ArrayList<Fighter> cibles, Fight fight) {
		for (Fighter target : cibles) {
			target.addBuff(effectID, value, turns, 1, true, spell, args, caster, false);
		}
	}

	private void applyEffect_132(ArrayList<Fighter> cibles, Fight fight) {
		for (Fighter target : cibles) {
			target.debuff();
			if (target.isHide()) target.unHide(spell);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 132, caster.getId() + "", target.getId() + "");
		}
	}

	private void applyEffect_138(ArrayList<Fighter> cibles, Fight fight) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_140(ArrayList<Fighter> cibles, Fight fight) {
		for (Fighter target : cibles) {
			target.addBuff(effectID, 0, 1, 0, true, spell, args, caster, false);
		}
	}

	private void applyEffect_141(Fight fight, ArrayList<Fighter> cibles) {
		for (Fighter target : cibles) {
			if (target.hasBuff(765))//sacrifice
			{
				if (target.getBuff(765) != null
						&& !target.getBuff(765).getCaster().isDead()) {
					applyEffect_765B(fight, target);
					target = target.getBuff(765).getCaster();
				}
			}
			fight.onFighterDie(target, target);
		}
	}

	private void applyEffect_142(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_143(ArrayList<Fighter> cibles, Fight fight) {
		if (spell == 470) {
			String[] jet = args.split(";");
			int heal = 0;
			if (jet.length < 6) {
				heal = 1;
			} else {
				heal = Formulas.getRandomJet(jet[5]);
			}
			int dmg2 = heal;
			for (Fighter cible : cibles) {
				if (cible.getTeam() != caster.getTeam())
					continue;
				if (cible.isDead())
					continue;
				heal = getMaxMinSpell(cible, heal);
				int healFinal = Formulas.calculFinalHealCac(caster, heal, false);
				if (spell == 450) {
					healFinal = heal;
				}
				if ((healFinal + cible.getPdv()) > cible.getPdvMax())
					healFinal = cible.getPdvMax() - cible.getPdv();
				if (healFinal < 1)
					healFinal = 0;
				cible.removePdv(caster, -healFinal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId()
						+ "", cible.getId() + "," + healFinal+ "," + COLOR_FEU);
				heal = dmg2;
			}
			return;
		}
		if (turns <= 0) {
			String[] jet = args.split(";");
			int heal = 0;
			if (jet.length < 6) {
				heal = 1;
			} else {
				heal = Formulas.getRandomJet(jet[5]);
			}
			int dmg2 = heal;
			for (Fighter cible : cibles) {
				if (cible.isDead())
					continue;
				heal = getMaxMinSpell(cible, heal);
				int healFinal = Formulas.calculFinalHealCac(caster, heal, false);
				if (spell == 450) {
					healFinal = heal;
				}
				if ((healFinal + cible.getPdv()) > cible.getPdvMax())
					healFinal = cible.getPdvMax() - cible.getPdv();
				if (healFinal < 1)
					healFinal = 0;
				cible.removePdv(caster, -healFinal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId()
						+ "", cible.getId() + "," + healFinal+ "," + COLOR_FEU);
				heal = dmg2;
			}
		} else {
			for (Fighter cible : cibles) {
				if (cible.isDead())
					continue;
				cible.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);
			}
		}
	}

	private void applyEffect_144(Fight pelea, ArrayList<Fighter> objetivos) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1)
			return;
		int val2 = val;
		for (Fighter objetivo : objetivos) {
			val = getMaxMinSpell(objetivo, val);
			objetivo.addBuff(145, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, 145, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
			val = val2;
		}
	}

	private void applyEffect_145(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_149(Fight fight, ArrayList<Fighter> cibles) {
		int id = -1;

		try {
			id = Integer.parseInt(args.split(";")[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Fighter target : cibles) {
			if (target.isDead())
				continue;
			if (spell == 686)
				if (target.getPersonnage() != null
						&& target.getPersonnage().getSexe() == 1
						|| target.getMob() != null
						&& target.getMob().getTemplate().getId() == 547)
					id = 8011;
			if (id == -1)
				id = target.getDefaultGfx();

			target.addBuff(effectID, id, turns, 1, true, spell, args, caster, true);
			int defaut = target.getDefaultGfx();
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + defaut + "," + id + ","
					+ (target.canPlay() ? turns + 1 : turns));
		}
	}

	private void applyEffect_150(Fight fight, ArrayList<Fighter> cibles) {
		if (turns == 0)
			return;

		if (spell == 547 || spell == 546 || spell == 548 || spell == 525) {
			caster.addBuff(effectID, 0, 3, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", caster.getId() + "," + (3 - 1));
			return;
		}

		for (Fighter target : cibles) {
			target.addBuff(effectID, 0, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId() + "", target.getId() + "," + (turns - 1));
		}
	}

	private void applyEffect_152(Fight pelea, ArrayList<Fighter> objetivos) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1)
			return;
		int val2 = val;
		for (Fighter objetivo : objetivos) {
			val = getMaxMinSpell(objetivo, val);
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, effectID, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
			val = val2;
		}
	}

	private void applyEffect_153(Fight pelea, ArrayList<Fighter> objetivos) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) return;

		int val2 = val;
		for (Fighter objetivo : objetivos) {
			val = getMaxMinSpell(objetivo, val);
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, effectID, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
			val = val2;
		}
	}

	private void applyEffect_154(Fight pelea, ArrayList<Fighter> objetivos) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) return;

		int val2 = val;
		for (Fighter objetivo : objetivos) {
			val = getMaxMinSpell(objetivo, val);
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, effectID, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
			val = val2;
		}
	}

	private void applyEffect_155(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}


	private void applyEffect_156(Fight pelea, ArrayList<Fighter> objetivos) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) return;

		int val2 = val;
		for (Fighter objetivo : objetivos) {
			val = getMaxMinSpell(objetivo, val);
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, effectID, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
			val = val2;
		}
	}

	private void applyEffect_157(Fight pelea, ArrayList<Fighter> objetivos) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) return;

		int val2 = val;
		for (Fighter objetivo : objetivos) {
			val = getMaxMinSpell(objetivo, val);
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, effectID, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
			val = val2;
		}
	}

	private void applyEffect_160(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_161(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_162(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_163(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		if (cibles.isEmpty() && spell == 310 && caster.getOldCible() != null) {
			caster.getOldCible().addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getOldCible().getId()
					+ "", caster.getOldCible().getId() + "," + turns);
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_164(ArrayList<Fighter> objetivos, Fight pelea) {
		int val = value;
		if (val == -1) return;

		for (Fighter objetivo : objetivos) {
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
		}
	}

	private void applyEffect_165(Fight fight, ArrayList<Fighter> cibles) {
		int value = -1;
		try {
			value = Integer.parseInt(args.split(";")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (value == -1)
			return;
		caster.addBuff(effectID, value, turns, 1, true, spell, args, caster, true);
	}

	private void applyEffect_168(ArrayList<Fighter> cibles, Fight fight) {// - PA, no esquivables
		if (turns <= 0) {
			for (Fighter cible : cibles) {
				if (cible.isDead())
					continue;
				cible.addBuff(effectID, value, 1, 1, true, spell, args, caster, false);
				if (turns <= 1 || duration <= 1) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 168, cible.getId()
							+ "", cible.getId() + ",-" + value);
				}
				if (fight.getFighterByOrdreJeu() == cible)
					fight.setCurFighterPa(fight.getCurFighterPa() - value);
				if (cible.getMob() != null)
					verifmobs(fight, cible, 168, 0);
			}
		} else {
			boolean repetibles = false;
			int lostPA = 0;

			for (Fighter cible : cibles) {
				if (cible.isDead())
					continue;
				if (spell == 197 || spell == 112) { // potencia silvestre, garra - ceangal (critico)
					cible.addBuff(effectID, value, turns, turns, true, spell, args, caster, false);
				} else if (spell == 115) {// Odorat
					if (!repetibles) {
						lostPA = Formulas.getRandomJet(jet);
						if (lostPA == -1)
							continue;
						value = lostPA;
					}
					cible.addBuff(effectID, value, turns, turns, true, spell, args, caster, false);
					repetibles = true;
				} else {
					//cible.addBuff(effectID, value, 1, 1, true, spell, args, caster, false); previous
					cible.addBuff(effectID, value, turns, turns, true, spell, args, caster, false); // Coding Mestre
				}
				if (turns <= 1 || duration <= 1)
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 168, cible.getId()
							+ "", cible.getId() + ",-" + value);

				if (fight.getFighterByOrdreJeu() == cible)
					fight.setCurFighterPa(fight.getCurFighterPa() - value);

				if (cible.getMob() != null)
					verifmobs(fight, cible, 168, 0);
			}
		}
	}

	private void applyEffect_169(ArrayList<Fighter> cibles, Fight fight) { // - PM, no esquivables

		if (spell == 686 && caster.haveState(1))//anti bug saoul
			return;
		if (turns <= 0) {
			for (Fighter cible : cibles) {
				if (cible.isDead())
					continue;
				cible.addBuff(effectID, value, 1, 1, true, spell, args, caster, false);
				if (turns <= 1 || duration <= 1)
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 169, cible.getId()
							+ "", cible.getId() + ",-" + value);
				if (cible.getMob() != null)
					verifmobs(fight, cible, 169, 0);
			}
		} else {
			if (cibles.isEmpty() && spell == 120
					&& caster.getOldCible() != null) {
				caster.getOldCible().addBuff(effectID, value, turns, 1, false, spell, args, caster, false);
				if (turns <= 1 || duration <= 1)
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 169, caster.getOldCible().getId()
							+ "", caster.getOldCible().getId() + ",-" + value);
			}
			boolean repetibles = false;
			int lostPM = 0;
			for (Fighter cible : cibles) {
				if (cible.isDead())
					continue;
				if (spell == 192) {// zarza tranquilizadora
					cible.addBuff(effectID, value, turns, 0, true, spell, args, caster, false);
				} else if (spell == 115) {// odorat
					if (!repetibles) {
						lostPM = Formulas.getRandomJet(jet);
						if (lostPM == -1)
							continue;
						value = lostPM;
					}
					cible.addBuff(effectID, value, turns, turns, true, spell, args, caster, false);
					repetibles = true;
				} else if (spell == 197) {// portencia sivelstre
					cible.addBuff(effectID, value, turns, turns, true, spell, args, caster, false);
				} else if (spell == 686 || spell == 99 || spell == 1026) {// picole & momification
					cible.addBuff(effectID, value, turns, turns, true, spell, args, caster, false);
				} else {
					cible.addBuff(effectID, value, 1, 1, true, spell, args, caster, false);
				}
				if (turns <= 1 || duration <= 1)
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 169, cible.getId()
							+ "", cible.getId() + ",-" + value);
				if (cible.getMob() != null)
					verifmobs(fight, cible, 168, 0);
			}
		}
	}

	private void applyEffect_171(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_176(ArrayList<Fighter> objetivos, Fight pelea) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			return;
		}
		for (Fighter objetivo : objetivos) {
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, Constant.STATS_ADD_PROS, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_177(ArrayList<Fighter> objetivos, Fight pelea) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			return;
		}
		for (Fighter objetivo : objetivos) {
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, Constant.STATS_REM_PROS, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_178(ArrayList<Fighter> objetivos, Fight pelea) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			return;
		}
		int val2 = val;
		for (Fighter objetivo : objetivos) {
			val = getMaxMinSpell(objetivo, val);
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, effectID, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
			val = val2;
		}
	}

	private void applyEffect_179(ArrayList<Fighter> objetivos, Fight pelea) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			return;
		}
		int val2 = val;
		for (Fighter objetivo : objetivos) {
			val = getMaxMinSpell(objetivo, val);
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, effectID, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
			val = val2;
		}
	}

	private void applyEffect_180(Fight fight)//invocation
	{
		int cell = this.cell.getId();
		if (!this.cell.getFighters().isEmpty())
			return;
		int id = fight.getNextLowerFighterGuid();
		Player clone = Player.ClonePerso(caster.getPersonnage(), -id - 10000, (caster.getPersonnage().getMaxPdv() - ((caster.getLvl() - 1) * 5 + 50)));
		clone.setFight(fight);

		Fighter fighter = new Fighter(fight, clone);
		fighter.fullPdv();
		fighter.setTeam(caster.getTeam());
		fighter.setInvocator(caster);

		fight.getMap().getCase(cell).addFighter(fighter);
		fighter.setCell(fight.getMap().getCase(cell));

		fight.getOrderPlaying().add((fight.getOrderPlaying().indexOf(caster) + 1), fighter);
		fight.addFighterInTeam(fighter, caster.getTeam());

		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 180, caster.getId() + "", fighter.getGmPacket('+', true).substring(3));
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", fight.getGTL());

		Trap.doTraps(fight, fighter);
	}

	private void applyEffect_181(Fight fight)//invocation
	{
		int cell = this.cell.getId();

		if (!this.cell.getFighters().isEmpty())
			return;

		int id = -1, level = -1;

		try {
			String mobs = args.split(";")[0], levels = args.split(";")[1];

			if (mobs.contains(":")) {
				String[] split = mobs.split(":");
				id = Integer.parseInt(split[Formulas.getRandomValue(0, split.length - 1)]);
			} else {
				id = Integer.parseInt(mobs);
			}

			if (levels.contains(":")) {
				String[] split = levels.split(":");
				level = Integer.parseInt(split[Formulas.getRandomValue(0, split.length - 1)]);
			} else {
				level = Integer.parseInt(levels);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		MobGrade MG = null;
		try {
			MG = World.world.getMonstre(id).getGradeByLevel(level);
			if(MG == null){
				MG = World.world.getMonstre(id).getGrade(level);
				if(MG == null)
				{
					MG = World.world.getMonstre(id).getRandomGrade().getCopy();
				}
			}
			if(MG != null) {
				MG = MG.getCopy();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (id == -1 || level == -1 || MG == null)
			return;

		MG.setInFightID(fight.getNextLowerFighterGuid());
		if (caster.getPersonnage() != null)
			MG.modifStatByInvocator(caster); // Augmenter les statistiques uniquement pour les invocations de personnages
		Fighter F = new Fighter(fight, MG);
		F.setTeam(caster.getTeam());
		F.setInvocator(caster);
		fight.getMap().getCase(cell).addFighter(F);
		F.setCell(fight.getMap().getCase(cell));
		fight.getOrderPlaying().add((fight.getOrderPlaying().indexOf(caster) + 1), F);
		fight.addFighterInTeam(F, caster.getTeam());
		String gm = F.getGmPacket('+', true).substring(3);
		String gtl = fight.getGTL();
		try {
			if (this.caster.getMob() != null)
				Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 181, caster.getId() + "", gm);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", gtl);
		caster.nbrInvoc++;

		this.checkTraps(fight, F, (short) 1200);
		
	}

	private void applyEffect_182(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_183(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_184(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_185(Fight fight) {
		int monster = -1, level = -1;

		try {
			monster = Integer.parseInt(args.split(";")[0]);
			level = Integer.parseInt(args.split(";")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		MobGrade mobGrade;

		try {
			mobGrade = World.world.getMonstre(monster).getGradeByLevel(level).getCopy();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		if (monster == -1 || level == -1 || mobGrade == null)
			return;
		if (monster == 556 && this.caster.getPersonnage() != null)
			mobGrade.modifStatByInvocator(this.caster);

		int id = fight.getNextLowerFighterGuid();
		mobGrade.setInFightID(id);

		Fighter fighter = new Fighter(fight, mobGrade);
		fighter.setTeam(this.caster.getTeam());
		fighter.setInvocator(this.caster);

		fight.getMap().getCase(this.cell.getId()).addFighter(fighter);
		fighter.setCell(fight.getMap().getCase(this.cell.getId()));
		fight.addFighterInTeam(fighter, this.caster.getTeam());
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 181, this.caster.getId() + "", fighter.getGmPacket('+', true).substring(3));
	}

	private void applyEffect_186(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		int val2 = val;
		for (Fighter f : cibles) {
			val = getMaxMinSpell(f, val);
			f.addBuff(effectID, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", f.getId() + "," + val + "," + turns);
			val = val2;
		}
	}

	private void applyEffect_202(Fight fight, ArrayList<Fighter> cibles) {
		for (final Fighter target : cibles) 
			if (target.isHide() && target != caster)
					target.unHide(spell);		
	}

	private void applyEffect_210(Fight fight, ArrayList<Fighter> cibles) {
		if (spell == 686 && caster.haveState(1))//anti bug saoul
		{
			int pa = 1;
			if (this.spellLvl == 5)
				pa = 2;
			else if (this.spellLvl == 4)
				pa = 3;
			else if (this.spellLvl == 3 || this.spellLvl == 2)
				pa = 4;
			else if (this.spellLvl == 1)
				pa = 5;

			caster.addBuff(111, pa, -1, 1, true, spell, args, caster, false);
			//Gain de PA pendant le tour de jeu
			caster.setCurPa(fight, pa);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 111, caster.getId()
					+ "", caster.getId() + "," + pa + "," + -1);
			return;
		}
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_211(Fight fight, ArrayList<Fighter> cibles) {
		if (spell == 686 && caster.haveState(1))//anti bug saoul
			return;
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_212(Fight fight, ArrayList<Fighter> cibles) {
		if (spell == 686 && caster.haveState(1))//anti bug saoul
			return;
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_213(Fight fight, ArrayList<Fighter> cibles) {
		if (spell == 686 && caster.haveState(1))//anti bug saoul
			return;
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_214(Fight fight, ArrayList<Fighter> cibles) {
		if (spell == 686 && caster.haveState(1))//anti bug saoul
			return;
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}

	}

	private void applyEffect_215(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_216(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_217(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_218(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_219(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_220(ArrayList<Fighter> cibles, Fight pelea) {
		if (turns < 1)
			return;
		else {
			for (Fighter cible : cibles)
				cible.addBuff(effectID, 0, turns, 0, true, spell, args, caster, true);
		}
	}

	private void applyEffect_265(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			GameServer.a();
			return;
		}
		for (Fighter target : cibles) {
			target.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_266(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		int vol = 0;
		for (Fighter target : cibles) {
			target.addBuff(Constant.STATS_REM_CHAN, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_CHAN, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
			vol += val;
		}
		if (vol == 0)
			return;
		//on ajoute le buff
		caster.addBuff(Constant.STATS_ADD_CHAN, vol, turns, 1, true, spell, args, caster, false);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_ADD_CHAN, caster.getId()
				+ "", caster.getId() + "," + vol + "," + turns);
	}

	private void applyEffect_267(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		int vol = 0;
		for (Fighter target : cibles) {
			target.addBuff(Constant.STATS_REM_VITA, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_VITA, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
			vol += val;
		}
		if (vol == 0)
			return;
		//on ajoute le buff
		caster.addBuff(Constant.STATS_ADD_VITA, vol, turns, 1, true, spell, args, caster, false);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_ADD_VITA, caster.getId()
				+ "", caster.getId() + "," + vol + "," + turns);
	}

	private void applyEffect_268(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		int vol = 0;
		for (Fighter target : cibles) {
			target.addBuff(Constant.STATS_REM_AGIL, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_AGIL, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
			vol += val;
		}
		if (vol == 0)
			return;
		//on ajoute le buff
		caster.addBuff(Constant.STATS_ADD_AGIL, vol, turns, 1, true, spell, args, caster, false);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_ADD_AGIL, caster.getId()
				+ "", caster.getId() + "," + vol + "," + turns);
	}

	private void applyEffect_269(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		int vol = 0;
		for (Fighter target : cibles) {
			target.addBuff(Constant.STATS_REM_INTE, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_INTE, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
			vol += val;
		}
		if (vol == 0)
			return;
		//on ajoute le buff
		caster.addBuff(Constant.STATS_ADD_INTE, vol, turns, 1, true, spell, args, caster, false);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_ADD_INTE, caster.getId()
				+ "", caster.getId() + "," + vol + "," + turns);
	}

	private void applyEffect_270(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		int vol = 0;
		for (Fighter target : cibles) {
			target.addBuff(Constant.STATS_REM_SAGE, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_SAGE, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
			vol += val;
		}
		if (vol == 0)
			return;
		//on ajoute le buff
		caster.addBuff(Constant.STATS_ADD_SAGE, vol, turns, 1, true, spell, args, caster, false);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_ADD_SAGE, caster.getId()
				+ "", caster.getId() + "," + vol + "," + turns);
	}

	private void applyEffect_271(Fight fight, ArrayList<Fighter> cibles) {
		int val = Formulas.getRandomJet(jet);
		int vol = 0;
		for (Fighter target : cibles) {
			target.addBuff(Constant.STATS_REM_FORC, val, turns, 1, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_FORC, caster.getId()
					+ "", target.getId() + "," + val + "," + turns);
			vol += val;
		}
		if (vol == 0)
			return;
		//on ajoute le buff
		caster.addBuff(Constant.STATS_ADD_FORC, vol, turns, 1, true, spell, args, caster, false);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_ADD_FORC, caster.getId()
				+ "", caster.getId() + "," + vol + "," + turns);
	}
	private void applyEffect_279(Fight fight, ArrayList<Fighter> cibles) {
		int damage = ((caster.getPdvMax() - caster.getPdv()) * value) / 100;
		for (Fighter target : cibles) {
			if(target.getTotalStats().get(Constant.STATS_ADD_RP_NEU) != 0){
				damage = damage - (damage * target.getTotalStats().get(Constant.STATS_ADD_RP_NEU)) / 100;
			}
			damage = applyFinalDamage(caster, damage);
			damage = -(damage);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
					+ "", target.getId() + "," + damage + ',' + COLOR_NEUTRE);
			if (target.getPdv() <= 0) {
				fight.onFighterDie(target, caster);
				if (target.canPlay() && target.getPersonnage() != null)
					fight.endTurn(false);
				else if (target.canPlay())
					target.setCanPlay(false);
			}
		}
	}

	private void applyEffect_293(Fight fight) {
		caster.addBuff(effectID, value, turns, 1, false, spell, args, caster, false);
		caster.setState(300, turns + 1,caster.getId());
	}

	private void applyEffect_320(Fight fight, ArrayList<Fighter> cibles) {
		int value = 1;
		try {
			value = Integer.parseInt(args.split(";")[0]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		int num = 0;
		for (Fighter target : cibles) {
			target.addBuff(Constant.STATS_REM_PO, value, turns, 0, true, spell, args, caster, false);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_REM_PO, caster.getId()
					+ "", target.getId() + "," + value + "," + turns);
			num += value;
		}
		if (num != 0) {
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, Constant.STATS_ADD_PO, caster.getId()
					+ "", caster.getId() + "," + num + "," + turns);
			caster.addBuff(Constant.STATS_ADD_PO, num, 1, 0, true, spell, args, caster, false);
			//Gain de PO pendant le tour de jeu
			if (caster.canPlay())
				caster.getTotalStats().addOneStat(Constant.STATS_ADD_PO, num);
		}
	}

	private void applyEffect_400(Fight fight) {
		if (!cell.isWalkable(true))
			return;//Si case pas marchable
		if (cell.getFirstFighter() != null)
			return;//Si la case est prise par un joueur

		//Si la case est prise par le centre d'un piege
		for (final Trap p : fight.getAllTraps())
			if (p.getCell().getId() == cell.getId())
				return;

		final String[] infos = args.split(";");
		final short spellID = Short.parseShort(infos[0]);
		final byte level = Byte.parseByte(infos[1]);
		final String po = World.world.getSort(spell).getStatsByLevel(spellLvl).getPorteeType();
		final byte size = (byte) CryptManager.getIntByHashedValue(po.charAt(1));
		final SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);
		final Trap g = new Trap(fight, caster, cell, size, TS, spell, (byte) level);
		fight.getAllTraps().add(g);
		final byte unk = g.getColor();
		final byte team = (byte) (caster.getTeam() + 1);
		String str = "GDZ+" + cell.getId() + ";" + size + ";" + unk;
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, team, 999, caster.getId() + "", str);
		str = "GDC" + cell.getId() + ";Haaaaaaaaz3005;";
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, team, 999, caster.getId() + "", str);
	}

	private void applyEffect_401(Fight fight) {
		if (!cell.isWalkable(false))
			return;//Si case pas marchable
		if (cell.getFirstFighter() != null)
			return;//Si la case est prise par un joueur

		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		byte duration = Byte.parseByte(infos[3]);
		String po = World.world.getSort(spell).getStatsByLevel(spellLvl).getPorteeType();
		byte size = (byte) CryptManager.getIntByHashedValue(po.charAt(1));
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);
		Glyph g = new Glyph(fight, caster, cell, size, TS, duration, spell);
		fight.getAllGlyphs().add(g);
		int unk = g.getColor();
		String str = "GDZ+" + cell.getId() + ";" + size + ";" + unk;
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
		str = "GDC" + cell.getId() + ";Haaaaaaaaa3005;";
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
	}

	private void applyEffect_402(Fight fight) {
		if (!cell.isWalkable(true))
			return;//Si case pas marchable

		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		byte duration = Byte.parseByte(infos[3]);
		String po = World.world.getSort(spell).getStatsByLevel(spellLvl).getPorteeType();
		byte size = (byte) CryptManager.getIntByHashedValue(po.charAt(1));
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);
		Glyph g = new Glyph(fight, caster, cell, size, TS, duration, spell);
		fight.getAllGlyphs().add(g);
		int unk = g.getColor();
		String str = "GDZ+" + cell.getId() + ";" + size + ";" + unk;
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId()
				+ "", str);
		str = "GDC" + cell.getId() + ";Haaaaaaaaa3005;";
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId()
				+ "", str);
	}

	private void applyEffect_671(ArrayList<Fighter> cibles, Fight fight) {
		if (turns <= 0) {
			System.out.println("CIBLES : " + cibles.size());
			for(Fighter target : cibles) {
				if (target.hasBuff(765)) {
					if (target.getBuff(765) != null
							&& !target.getBuff(765).getCaster().isDead()) {
						applyEffect_765B(fight, target);
						target = target.getBuff(765).getCaster();
					}
				}
				if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl && spell != 0) {
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId() + "", target.getId() + ",1");
					target = caster;
				}
				int resP = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_NEU), resF = target.getTotalStats().getEffect(Constant.STATS_ADD_R_NEU);

				if (target.getPersonnage() != null) {
					resP += target.getTotalStats().getEffect(Constant.STATS_ADD_RP_PVP_NEU);
					resF += target.getTotalStats().getEffect(Constant.STATS_ADD_R_PVP_NEU);
				}

				int dmg = Formulas.getRandomJet(this.jet);// % de pdv
				dmg = getMaxMinSpell(target, dmg);
				int val = caster.getPdv() / 100 * dmg;// Valeur des dégâts
				val -= resF;
				int reduc = (int) (((float) val) / (float) 100) * resP;// Reduc
				// %resis
				val -= reduc;
				if (val < 0)
					val = 0;
				val = applyOnHitBuffs(val, target, caster, fight, Constant.ELEMENT_NULL);
				if (val > target.getPdv())
					val = target.getPdv();
				target.removePdv(caster, val);
				int cura = val;
				if (target.hasBuff(786) && target.getBuff(786) != null) {
					if ((cura + caster.getPdv()) > caster.getPdvMax())
						cura = caster.getPdvMax() - caster.getPdv();
					caster.removePdv(caster, -cura);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, target.getId()
							+ "", caster.getId() + ",+" + cura);
				}
				val = -(val);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
						+ "", target.getId() + "," + val);
				if (target.getPdv() <= 0)
					fight.getDeadList().remove(target);
			}
		} else {
			caster.addBuff(effectID, 0, turns, 0, true, spell, args, caster, false);
		}
	}

	private void applyEffect_672(ArrayList<Fighter> cibles, Fight fight) {
		//Punition
		//Formule de barge ? :/ Clair que ca punie ceux qui veulent l'utiliser x_x
		double val = ((double) Formulas.getRandomJet(jet) / (double) 100);
		int pdvMax = caster.getPdvMaxOutFight();
		double pVie = (double) caster.getPdv() / (double) caster.getPdvMax();
		double rad = (double) 2 * Math.PI * (double) (pVie - 0.5);
		double cos = Math.cos(rad);
		double taux = (Math.pow((cos + 1), 2)) / (double) 4;
		double dgtMax = val * pdvMax;
		int dgt = (int) (taux * dgtMax);

		for (Fighter target : cibles) {

			if (target.hasBuff(765))//sacrifice
			{
				if (target.getBuff(765) != null
						&& !target.getBuff(765).getCaster().isDead()) {
					applyEffect_765B(fight, target);
					target = target.getBuff(765).getCaster();
				}
			}
			//si la cible a le buff renvoie de sort
			if (target.hasBuff(106) && target.getBuffValue(106) >= spellLvl) {
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 106, target.getId()
						+ "", target.getId() + ",1");
				//le lanceur devient donc la cible
				target = caster;
			}
			int finalDommage = applyOnHitBuffs(dgt, target, caster, fight, Constant.ELEMENT_NEUTRE);//S'il y a des buffs spéciaux
			int resi = target.getTotalStats().getEffect(Constant.STATS_ADD_RP_NEU);
			int retir = 0;
			if (resi > 2) {
				retir = (finalDommage * resi) / 100;
				finalDommage = finalDommage - retir;
			}
			if (resi < -2) {
				retir = ((-finalDommage) * (-resi)) / 100;
				finalDommage = finalDommage + retir;
			}
			if (finalDommage > target.getPdv())
				finalDommage = target.getPdv();//Target va mourrir
			target.removePdv(caster, finalDommage);
			finalDommage = -(finalDommage);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 100, caster.getId()
					+ "", target.getId() + "," + finalDommage + "," + COLOR_NEUTRE);

			if (target.getPdv() <= 0) {
				fight.onFighterDie(target, target);
				if (target.canPlay() && target.getPersonnage() != null)
					fight.endTurn(false);
				else if (target.canPlay())
					target.setCanPlay(false);
			}
		}
	}

	private void applyEffect_765(ArrayList<Fighter> cibles, Fight fight) {
		for (Fighter target : cibles) {
			target.addBuff(effectID, 0, turns, 1, true, spell, args, caster, true);
		}
	}

	private void applyEffect_765B(Fight fight, Fighter target) {
		Fighter sacrified = target.getBuff(765).getCaster();
		GameCase cell1 = sacrified.getCell();
		GameCase cell2 = target.getCell();

		sacrified.getCell().getFighters().clear();
		target.getCell().getFighters().clear();
		sacrified.setCell(cell2);
		sacrified.getCell().addFighter(sacrified);
		target.setCell(cell1);
		target.getCell().addFighter(target);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, target.getId() + "", target.getId() + "," + cell1.getId());
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, sacrified.getId() + "", sacrified.getId() + "," + cell2.getId());
	}

	private void applyEffect_776(ArrayList<Fighter> objetivos, Fight pelea) {
		int val = Formulas.getRandomJet(jet);
		if (val == -1) {
			return;
		}
		for (Fighter objetivo : objetivos) {
			objetivo.addBuff(effectID, val, turns, 1, true, spell, args, caster, true);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(pelea, 7, effectID, caster.getId()
					+ "", objetivo.getId() + "," + val + "," + turns);
		}
	}

	private void applyEffect_780(Fight fight) {
		Fighter target = null;

		for (Fighter fighter : fight.getDeadList().values())
			if (!fighter.hasLeft() && fighter.getTeam() == caster.getTeam())
				target = fighter;
		if (target == null) return;

		fight.addFighterInTeam(target, target.getTeam());
		target.setIsDead(false);
		target.getFightBuff().clear();
		if (target.isInvocation())
			fight.getOrderPlaying().add((fight.getOrderPlaying().indexOf(target.getInvocator()) + 1), target);

		target.setCell(cell);
		target.getCell().addFighter(target);

		target.fullPdv();
		int percent = (100 - value) * target.getPdvMax() / 100;
		target.removePdv(caster, percent);

		String gm = target.getGmPacket('+', true).substring(3);
		String gtl = fight.getGTL();
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 181, target.getId() + "", gm);
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, target.getId() + "", gtl);
		if (!target.isInvocation())
			SocketManager.GAME_SEND_STATS_PACKET(target.getPersonnage());

		fight.removeDead(target);
		
		this.checkTraps(fight, target, (short) 1200);
		
	}

	private void applyEffect_781(ArrayList<Fighter> cibles, Fight fight) {
		for (Fighter target : cibles) {
			target.addBuff(effectID, value, turns, 1, debuffable, spell, args, caster, true);
		}
	}

	private void applyEffect_782(ArrayList<Fighter> cibles, Fight fight) {
		for (Fighter target : cibles) {
			target.addBuff(effectID, value, turns, 1, debuffable, spell, args, caster, true);
		}
	}

	private void applyEffect_783(ArrayList<Fighter> cibles, Fight fight) {
		//Pousse jusqu'a la case visée
		GameCase ccase = caster.getCell();
		//On calcule l'orientation entre les 2 cases
		char dir = PathFinding.getDirBetweenTwoCase(ccase.getId(), cell.getId(), fight.getMap(), true);
		//On calcule l'id de la case a coté du lanceur dans la direction obtenue
		int tcellID = PathFinding.GetCaseIDFromDirrection(ccase.getId(), dir, fight.getMap(), true);
		//on prend la case corespondante
		GameCase tcase = fight.getMap().getCase(tcellID);

		if (tcase == null)
			return;
		//S'il n'y a personne sur la case, on arrete
		if (tcase.getFighters().isEmpty())
			return;
		//On prend le Fighter ciblé
		Fighter target = tcase.getFirstFighter();
		//On verifie qu'il peut aller sur la case ciblé en ligne droite
		int c1 = tcellID, limite = 0;
		if (target.getMob() != null)
			for (int i : Constant.STATIC_INVOCATIONS)
				if (i == target.getMob().getTemplate().getId())
					return;

		while (true) {
			if (PathFinding.GetCaseIDFromDirrection(c1, dir, fight.getMap(), true) == cell.getId())
				break;
			if (PathFinding.GetCaseIDFromDirrection(c1, dir, fight.getMap(), true) == -1)
				return;
			c1 = PathFinding.GetCaseIDFromDirrection(c1, dir, fight.getMap(), true);
			limite++;
			if (limite > 50)
				return;
		}
		GameCase newCell = PathFinding.checkIfCanPushEntity(fight, ccase.getId(), cell.getId(), dir);

		if (newCell != null)
			cell = newCell;

		target.getCell().getFighters().clear();
		target.setCell(cell);
		target.getCell().addFighter(target);

		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 5, caster.getId() + "", target.getId() + "," + cell.getId());
		Trap.doTraps(fight, target);
	}

	private void applyEffect_784(ArrayList<Fighter> cibles, Fight fight) {
		Map<Integer, GameCase> origPos = fight.getRholBack(); // les positions de début de combat

		ArrayList<Fighter> list = fight.getFighters(3); // on copie la liste des fighters
		for (int i = 1; i < list.size(); i++)   // on boucle si tout le monde est à la place
			if (!list.isEmpty())                 // d'un autre
				for (Fighter F : list) {
					if (F == null || F.isDead() || !origPos.containsKey(F.getId())) {
						continue;
					}
					if (F.getCell().getId() == origPos.get(F.getId()).getId()) {
						continue;
					}
					if (origPos.get(F.getId()).getFirstFighter() == null) {
						F.getCell().getFighters().clear();
						F.setCell(origPos.get(F.getId()));
						F.getCell().addFighter(F);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, F.getId() + "", F.getId() + "," + F.getCell().getId());
					}
				}
	}

	private void applyEffect_786(ArrayList<Fighter> objetivos, Fight pelea) {
		for (Fighter objetivo : objetivos)
			objetivo.addBuff(effectID, value, turns, 1, true, spell, args, caster, true);
	}

	private void applyEffect_787(ArrayList<Fighter> objetivos, Fight pelea) {
		int hechizoID = -1;
		int hechizoNivel = -1;
		try {
			hechizoID = Integer.parseInt(args.split(";")[0]);
			hechizoNivel = Integer.parseInt(args.split(";")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Spell hechizo = World.world.getSort(hechizoID);
		ArrayList<SpellEffect> EH = hechizo.getStatsByLevel(hechizoNivel).getEffects();
		for (SpellEffect eh : EH) {
			for (Fighter objetivo : objetivos) {
				objetivo.addBuff(eh.effectID, eh.value, 1, 1, true, eh.spell, eh.args, caster, true);
			}
		}
	}

	private void applyEffect_788(ArrayList<Fighter> cibles, Fight fight) {
		for (Fighter target : cibles) {
			target.addBuff(effectID, value, turns, 1, false, spell, args, target, true);
		}
	}

	private void applyEffect_950(Fight fight, ArrayList<Fighter> cibles) {
		int id = -1;
		try {
			id = Integer.parseInt(args.split(";")[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (id == -1)
			return;
		if (id == 31 || id == 32 || id == 33 || id == 34) {
			turns = 2;
			for (Entry<Integer, Fighter> entry : fight.getTeam1().entrySet()) {
				Fighter mob = entry.getValue();
				mob.setState(id, turns,caster.getId());
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
						+ "", mob.getId() + "," + id + ",1");
				mob.addBuff(effectID, value, turns, 1, false, spell, args, mob, true);
			}

			if (id == 34)
				for (Fighter target : cibles)
					target.addBuff(140, 0, 1, 0, true, 1102, "", caster, false);
		}

		for (Fighter target : cibles) {
			if (spell == 139 && target.getTeam() != caster.getTeam())//Mot d'altruisme on saute les ennemis ?
			{
				continue;
			}
			if (turns <= 0) {
				target.setState(id, turns,caster.getId());
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
						+ "", target.getId() + "," + id + ",1");
			} else {
				target.setState(id, turns,caster.getId());
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
						+ "", target.getId() + "," + id + ",1");
				target.addBuff(effectID, value, turns, 1, false, spell, args, target, true);
			}
			if (spell == 686) {
				target.unHide(686);
			}
			if(id == 2){
				fight.setCapturer(caster);
			}

		}
	}

	private void applyEffect_951(Fight fight, ArrayList<Fighter> cibles) {
		int id = -1;
		try {
			id = Integer.parseInt(args.split(";")[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (id == -1)
			return;

		for (Fighter target : cibles) {
			//Si la cible n'a pas l'état
			if (!target.haveState(id))
				continue;
			//on enleve l'état
			target.setState(id, 0,caster.getId());
			//on envoie le packet
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId() + "", target.getId() + "," + id + ",0");
		}
	}

	private void applyEffect_1000(Fight fight, ArrayList<Fighter> cibles) {
		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		byte duration = 1;
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);
		GameCase celll = null;
		int casenbr = 0;
		boolean quatorze = false;
		for (GameCase entry : fight.getMap().getCases()) {
			casenbr = casenbr + 1;

			if (casenbr == 14 && quatorze) {
				quatorze = false;
				casenbr = 0;
			}
			if (casenbr == 15) {
				quatorze = true;
				casenbr = 0;
			}
			if (quatorze)
				continue;
			celll = entry;
			if (celll == null)
				continue;
			switch (celll.getId()) {
				case 28:
				case 57:
				case 86:
				case 115:
				case 144:
				case 173:
				case 202:
				case 231:
				case 260:
				case 289:
				case 318:
				case 347:
				case 376:
				case 405:
				case 434:
				case 463:
					continue;
			}
			if (!celll.isWalkable(false))
				continue;
			Glyph g = new Glyph(fight, caster, celll, (byte) 0, TS, duration, spell);
			fight.getAllGlyphs().add(g);

			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", "GDZ+" + celll.getId() + ";" + 0 + ";" + g.getColor());
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", "GDC" + celll.getId() + ";Haaaaaaaaa3005;");
		}
	}

	private void applyEffect_1001(Fight fight, ArrayList<Fighter> cibles) {
		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		byte duration = 1;
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);
		GameCase celll = null;
		int casenbr = 0;
		boolean quatorze = false;
		for (GameCase entry : fight.getMap().getCases()) {
			casenbr = casenbr + 1;

			if (casenbr == 14 && quatorze == true) {
				quatorze = false;
				casenbr = 0;
			}
			if (casenbr == 15) {
				quatorze = true;
				casenbr = 0;
			}
			if (quatorze == false)
				continue;
			celll = entry;
			if (celll == null)
				continue;
			if (!celll.isWalkable(false))
				continue;
			Glyph g = new Glyph(fight, caster, celll, (byte) 0, TS, duration, spell);
			fight.getAllGlyphs().add(g);
			int unk = g.getColor();
			String str = "GDZ+" + celll.getId() + ";" + 0 + ";" + unk;
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
			str = "GDC" + celll.getId() + ";Haaaaaaaaa3005;";
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
			str = "GDC" + celll.getId() + ";Haaaaaaaaa3005;";
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
		}
	}

	private void applyEffect_1002(Fight fight, ArrayList<Fighter> cibles) {
		String[] infos = args.split(";");
		int spellID = Short.parseShort(infos[0]);
		int level = Byte.parseByte(infos[1]);
		byte duration = 100;
		SortStats TS = World.world.getSort(spellID).getStatsByLevel(level);

		if (cell.isWalkable(true) && !fight.isOccuped(cell.getId())) {
			caster.getCell().getFighters().clear();
			caster.setCell(cell);
			caster.getCell().addFighter(caster);
			Trap.doTraps(caster.getFight(), caster);
			SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 4, caster.getId() + "", caster.getId() + "," + cell.getId());
		}

		Glyph g = new Glyph(fight, caster, cell, (byte) 0, TS, duration, spell);
		fight.getAllGlyphs().add(g);
		int unk = g.getColor();
		String str = "GDZ+" + cell.getId() + ";" + 0 + ";" + unk;
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
		str = "GDC" + cell.getId() + ";Haaaaaaaaa3005;";
		SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 999, caster.getId() + "", str);
	}

	private ArrayList<Fighter> trierCibles(ArrayList<Fighter> cibles, Fight fight) {
		ArrayList<Fighter> array = new ArrayList<>();
		int max = -1;
		int distance;

		for (Fighter f : cibles) {
			distance = PathFinding.getDistanceBetween(fight.getMap(), this.cell.getId(), f.getCell().getId());
			if (distance > max)
				max = distance;
		}

		for (int i = max; i >= 0; i--) {
			Iterator<Fighter> it = cibles.iterator();
			while (it.hasNext()) {
				Fighter f = it.next();
				distance = PathFinding.getDistanceBetween(fight.getMap(), this.cell.getId(), f.getCell().getId());
				if (distance == i) {
					array.add(f);
					it.remove();
				}
			}
		}

		return array;
	}

	public void verifmobs(Fight fight, Fighter target, int effet, int cura) {
		if(Constant.DAMAGE_EFFECT.contains(effet)) {
			if (target.hasBuff(114) && target.hasBuff(112)) {
				SpellEffect addDom = target.getBuff(112);
				SpellEffect multiDom = target.getBuff(114);
				//Radicelle
				if (multiDom.getSpell() == 1069 && addDom.getSpell() == 1069) {
					Spell radecielle = World.world.getSort(addDom.getSpell());
					target.addBuff(112, addDom.getValue(), 2, 1, true, radecielle.getSpellID(), args, caster, false);
					target.addBuff(114, multiDom.getValue(), 2, 1, true, radecielle.getSpellID(), args, caster, false);
				}
			}
			else if(target.hasBuff(138)) {
				SpellEffect percentDom = target.getBuff(138);
				//Cri du Croca
				if(percentDom.getSpell() == 972) {
					Spell CriCroca = World.world.getSort(percentDom.getSpell());
					int val = Integer.parseInt(percentDom.args.split(";")[0]);
					int valMax5 = Integer.parseInt(percentDom.args.split(";")[1]);
					int jetAleatoire = Formulas.getRandomValue(val, valMax5);
					target.addBuff(138, jetAleatoire, 2, 1, true, CriCroca.getSpellID(), args, caster, false);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
							+ "", target.getId() + "," + jetAleatoire + "," + -1);
				}
			}
		}
		switch (target.getMob().getTemplate().getId()) {
			case 232://meulou
				if (target.hasBuff(112)) {
					target.addBuff(112, 5, -1, 1, true, spell, args, caster, false);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 112, caster.getId()
							+ "", target.getId() + "," + 5 + "," + -1);
				}
				break;
			case 233: // Trool
				if(target.hasBuffFromSpell(307)) {
					if (effet == 168 || effet == 101) {
						target.addBuff(128, 1, 2, 1, true, spell, args, target, false);
						//Gain de PM pendant le tour de jeu
						target.setCurPm(fight, 1);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
								+ "", target.getId() + "," + 1 + "," + 2);
					}
					if (effet == 169 || effet == 127)//rall pm don pa
					{
						target.addBuff(111, 1, 2, 1, true, spell, args, target, false);
						//Gain de PA pendant le tour de jeu
						target.setCurPa(fight, 1);
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, effectID, caster.getId()
								+ "", target.getId() + "," + 1 + "," + 2);
					}
				}
				if(target.hasBuffFromSpell(308)){
					int healFinal = 200 + target.getPdv();
					if (healFinal > target.getPdvMax())
						healFinal = target.getPdvMax();
					if (healFinal < 1)
						healFinal = 0;
					target.setPdv(healFinal);
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, target.getId()
							+ "", target.getId() + "," + healFinal+ "," + COLOR_FEU);

				}
				break;
			case 423://kralamour
				if (effet == 99 || effet == 94) {
					target.setState(37, 1,caster.getId());//etat tersiaire feu
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
							+ "", target.getId() + "," + 37 + ",1");
					if (target.haveState(38))//secondaire terre
					{
						target.setState(38, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 38 + ",0");
					}
					if (target.haveState(36))//quanternaire eau
					{
						target.setState(36, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 36 + ",0");
					}
					if (target.haveState(35))//primaire air
					{
						target.setState(35, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 35 + ",0");
					}
				} else if (effet == 98 || effet == 93) {
					target.setState(35, 1,caster.getId());//etat primaire air
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
							+ "", target.getId() + "," + 35 + ",1");
					if (target.haveState(38))//secondaire terre
					{
						target.setState(38, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 38 + ",0");
					}
					if (target.haveState(36))//quanternaire eau
					{
						target.setState(36, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 36 + ",0");
					}
					if (target.haveState(37))//tersiaire feu
					{
						target.setState(37, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 37 + ",0");
					}
				} else if (effet == 97 || effet == 92) {
					target.setState(38, 1,caster.getId());//etat secondaire terre
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
							+ "", target.getId() + "," + 38 + ",1");
					if (target.haveState(35))//primaire air
					{
						target.setState(35, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 35 + ",0");
					}
					if (target.haveState(36))//quanternaire eau
					{
						target.setState(36, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 36 + ",0");
					}
					if (target.haveState(37))//tersiaire feu
					{
						target.setState(37, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 37 + ",0");
					}
				} else if (effet == 96 || effet == 91) {
					target.setState(36, 1,caster.getId());//etat quanternaire eau
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
							+ "", target.getId() + "," + 36 + ",1");
					if (target.haveState(35))//primaire air
					{
						target.setState(35, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 35 + ",0");
					}
					if (target.haveState(38))//secondaire terre
					{
						target.setState(38, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 38 + ",0");
					}
					if (target.haveState(37))//tersiaire feu
					{
						target.setState(37, 0,caster.getId());
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 37 + ",0");
					}
				}

				break;
			case 940: // Rat Blanc
				if(Constant.DAMAGE_EFFECT.contains(effectID)){
					if(target.getSpellsFromFighterBuff().containsKey(World.world.getSort(1010))){
						if (effet == 99 || effet == 94) {
							target.addBuff(213, 50, 1, 1, false, 1010, "", target, true);// + 50 %feu
							SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1010, caster.getId()
									+ "", target.getId() + "," + "" + "," + 1);
						} else if (effet == 98 || effet == 93) {
							target.addBuff(212, 50, 1, 1, false, 1010, "", target, true);// + 50 %air
							SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1010, caster.getId()
									+ "", target.getId() + "," + "" + "," + 1);
						} else if (effet == 97 || effet == 92) {
							target.addBuff(210, 50, 1, 1, false, 1010, "", target, true);// + 50 %terre
							SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1010, caster.getId()
									+ "", target.getId() + "," + "" + "," + 1);
						} else if (effet == 96 || effet == 91) {
							target.addBuff(211, 50, 1, 1, false, 1010, "", target, true);// + 50 %eau
							SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1010, caster.getId()
									+ "", target.getId() + "," + "" + "," + 1);
						}
					}
				}
				break;
			case 1045://kimbo
				if (effet == 99 || effet == 98 || effet == 94 || effet == 93) {
					target.setState(30, 1,caster.getId());//etat pair
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
							+ "", target.getId() + "," + 30 + ",1");
					if (target.haveState(29)) {
						target.setState(29, 0,caster.getId());//etat 29 impair
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 29 + ",0");
					}
				} else if (effet == 97 || effet == 96 || effet == 92 || effet == 91) {
					target.setState(29, 1,caster.getId());//etat etat si pair
					SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
							+ "", target.getId() + "," + 29 + ",1");
					if (target.haveState(30)) {
						target.setState(30, 0,caster.getId());//etat 29 si pair
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 950, caster.getId()
								+ "", target.getId() + "," + 30 + ",0");
					}
				}
				break;
			case 1071://Rasboul
				if (effet == 99 || effet == 94) {
					if (target.hasBuff(214)) {
						target.addBuff(218, 50, 4, -1, false, 1039, "", target, true);// - 50 feu
						target.addBuff(210, 50, 4, -1, false, 1039, "", target, true);// + 50 terre
						target.addBuff(211, 50, 4, -1, false, 1039, "", target, true);// + 50 eau
						target.addBuff(212, 50, 4, -1, false, 1039, "", target, true);// + 50 air
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1039, caster.getId()
								+ "", target.getId() + "," + "" + "," + 1);
					}
				} else if (effet == 98 || effet == 93) {
					if (target.hasBuff(214)) {
						target.addBuff(217, 50, 4, -1, false, 1039, "", target, true);// - 50 air
						target.addBuff(210, 50, 4, -1, false, 1039, "", target, true);// + 50 terre
						target.addBuff(211, 50, 4, -1, false, 1039, "", target, true);// + 50 eau
						target.addBuff(213, 50, 4, -1, false, 1039, "", target, true);// + 50 feu
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1039, caster.getId()
								+ "", target.getId() + "," + "" + "," + -1);
					}
				} else if (effet == 97 || effet == 92) {
					if (target.hasBuff(214)) {
						target.addBuff(215, 50, 4, -1, false, 1039, "", target, true);// - 50 terre
						target.addBuff(212, 50, 4, -1, false, 1039, "", target, true);// + 50 air
						target.addBuff(211, 50, 4, -1, false, 1039, "", target, true);// + 50 eau
						target.addBuff(213, 50, 4, -1, false, 1039, "", target, true);// + 50 feu
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1039, caster.getId()
								+ "", target.getId() + "," + "" + "," + 1);
					}
				} else if (effet == 96 || effet == 91) {
					if (target.hasBuff(214)) {
						target.addBuff(216, 50, 4, -1, false, 1039, "", target, true);// - 50 eau
						target.addBuff(212, 50, 4, -1, false, 1039, "", target, true);// + 50 air
						target.addBuff(210, 50, 4, -1, false, 1039, "", target, true);// + 50 terre
						target.addBuff(213, 50, 4, -1, false, 1039, "", target, true);// + 50 feu
						SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 1039, caster.getId()
								+ "", target.getId() + "," + "" + "," + 1);
					}
				}
				if (effet == Constant.STATS_REM_PA || effet == 84) {
					if(target.hasBuff(Constant.STATS_ADD_PA)) {
						SpellEffect effect = target.getBuff(Constant.STATS_ADD_PA);
						if(effect.getSpell() == 1038) {
							target.addBuff(Constant.STATS_ADD_PA, 2, 4, 1, true, spell, args, target, false);
							SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 111, target.getId()
									+ "", target.getId() + ",+" + value);
						}
					}
				}
				break;
			case 2750: // Arbre de Vie
				int healFinal = cura;
				if ((healFinal + caster.getPdv()) > caster.getPdvMax())
					healFinal = caster.getPdvMax() - caster.getPdv();
				if (healFinal < 1)
					healFinal = 0;
				caster.removePdv(caster, -healFinal);
				SocketManager.GAME_SEND_GA_PACKET_TO_FIGHT(fight, 7, 108, caster.getId()
						+ "", caster.getId() + "," + healFinal+ "," + COLOR_FEU);
				break;
		}
	}
	
	/**
	 * Cette fonction doit etre utilise <b>uniquement lorsque</b> il faut attendre la fin d'une animation niveau client.<br>
	 * Exemple une <b>teleportation , attirance , invocation ou bien le fait d'etre jete par un panda.</b><br>
	 * Lorsque quelqu'un est pousse il n'est pas necessaire d'appeler cette fonction car le client prend en charge la possibilite de pousser a plusieurs repprises.
	 * 
	 * @param fight L'instance de la class Fight
	 * @param fighter Le joueur qu'il faut appliquer le reseau de pieges ou bien juste les pieges
	 * @param time Temps que mets l'animation de tp/jeter/attirer niveau client
	 * 
	 * @author Sarazar928Ghost
	 */
	private void checkTraps(Fight fight, Fighter fighter, short time) {
		// Il est sur un piege qui pousse ?
		final boolean isPushing = Trap.checkPushingTraps(fight, fighter);
		
		// Si il n'est pas dans un reseau
		if(!isPushing) {
			Trap.doTraps(fight, fighter);
			return;
		}
		
		// Il est dans un reseau
		fight.setTraped(true);
		
		TimerWaiter.addNext(() -> {
			Trap.doTraps(fight, fighter);
			fight.removeTraped();
		}, time, TimerWaiter.DataType.FIGHT);
	}

	private int applyFinalDamage(Fighter caster, int damage){
		int finalDamage =  damage;
		if(caster.getTotalStats().hasEffect(Constant.STATS_ADD_FINAL_DOM)) {
			finalDamage += (finalDamage * caster.getTotalStats().get(Constant.STATS_ADD_FINAL_DOM)) / 100;
		}
		if(caster.getTotalStats().hasEffect(Constant.STATS_REM_FINAL_DOM)) {
			finalDamage -= (finalDamage * caster.getTotalStats().get(Constant.STATS_REM_FINAL_DOM)) / 100;
		}
		return finalDamage;
	}
}