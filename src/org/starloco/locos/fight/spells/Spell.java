package org.starloco.locos.fight.spells;

import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.fight.Challenge;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.fight.traps.Trap;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.kernel.Main;

import java.util.*;
import java.util.Map.Entry;

public class Spell {

    public static Object SortStats;
    private String nombre;
    private int spellID;
    private int spriteID;
    private String spriteInfos;
    private Map<Integer, SortStats> sortStats = new HashMap<Integer, SortStats>();
    private ArrayList<Integer> effectTargets = new ArrayList<Integer>();
    private ArrayList<Integer> CCeffectTargets = new ArrayList<Integer>();
    private int type, duration;

    public Spell(int aspellID, String aNombre, int aspriteID,
                 String aspriteInfos, String ET, int type, int duration) {
        spellID = aspellID;
        nombre = aNombre;
        spriteID = aspriteID;
        spriteInfos = aspriteInfos;
        this.duration = duration;
        if (ET.equalsIgnoreCase("0")) {
            effectTargets.add(0);
            CCeffectTargets.add(0);
        } else {
            String nET = ET.split(":")[0];
            String ccET = "";
            if (Arrays.stream(ET.split(":")).count() > 1)
                ccET = ET.split(":")[1];

            for (int i = 0; i < Arrays.stream(nET.split(";")).count(); i++){
                effectTargets.add(Integer.parseInt(nET.split(";")[i]));
            }
            if(!ccET.isEmpty()) {
                for (int i = 0; i < Arrays.stream(ccET.split(";")).count(); i++) {
                    CCeffectTargets.add(Integer.parseInt(ccET.split(";")[i]));
                }
            }
        }
        this.type = type;
    }

    public void setInfos(int aspriteID, String aspriteInfos, String ET, int type, int duration) {
        spriteID = aspriteID;
        spriteInfos = aspriteInfos;
        this.type = type;
        this.duration = duration;
        if (ET.equalsIgnoreCase("0")) {
            effectTargets.add(0);
            CCeffectTargets.add(0);
        } else {
            String nET = ET.split(":")[0];
            String ccET = "";
            if (Arrays.stream(ET.split(":")).count() > 1)
                ccET = ET.split(":")[1];

            for (int i = 0; i < Arrays.stream(nET.split(";")).count(); i++){
                effectTargets.add(Integer.parseInt(nET.split(";")[i]));
            }
            if(!ccET.isEmpty()) {
                for (int i = 0; i < Arrays.stream(ccET.split(";")).count(); i++) {
                    CCeffectTargets.add(Integer.parseInt(ccET.split(";")[i]));
                }
            }
        }
    }

    public ArrayList<Integer> getEffectTargets() {
        return effectTargets;
    }
    public ArrayList<Integer> getCCEffectTargets() {
        return CCeffectTargets;
    }

    public int getSpriteID() {
        return spriteID;
    }

    public String getSpriteInfos() {
        return spriteInfos;
    }

    public int getSpellID() {
        return spellID;
    }

    public SortStats getStatsByLevel(int lvl) {
        return sortStats.get(lvl);
    }

    public String getNombre() {
        return nombre;
    }

    public Map<Integer, SortStats> getSortsStats() {
        return sortStats;
    }

    public void addSortStats(Integer lvl, SortStats stats) {
        if (sortStats.get(lvl) != null)
            sortStats.remove(lvl);
        sortStats.put(lvl, stats);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public static class SortStats {

        private int spellID;
        private int level;
        private int PACost;
        private int minPO;
        private int maxPO;
        private int TauxCC;
        private int TauxEC;
        private boolean isLineLaunch;
        private boolean hasLDV;
        private boolean isEmptyCell;
        private boolean isModifPO;
        private int maxLaunchbyTurn;
        private int maxLaunchbyByTarget;
        private int coolDown;
        private int reqLevel;
        private boolean isEcEndTurn;
        private ArrayList<SpellEffect> effects;
        private ArrayList<SpellEffect> CCeffects;
        private String porteeType;
	    private boolean trap;

        public SortStats(int AspellID, int Alevel, int cost, int minPO,
                         int maxPO, int tauxCC, int tauxEC, boolean isLineLaunch,
                         boolean hasLDV, boolean isEmptyCell, boolean isModifPO,
                         int maxLaunchbyTurn, int maxLaunchbyByTarget, int coolDown,
                         int reqLevel, boolean isEcEndTurn, String effects,
                         String ceffects, String typePortee) {
            //effets, effetsCC, PaCost, PO Min, PO Max, Taux CC, Taux EC, line, LDV, emptyCell, PO Modif, maxByTurn, maxByTarget, Cooldown, type, level, endTurn
            this.spellID = AspellID;
            this.level = Alevel;
            this.PACost = cost;
            this.minPO = minPO;
            this.maxPO = maxPO;
            this.TauxCC = tauxCC;
            this.TauxEC = tauxEC;
            this.isLineLaunch = isLineLaunch;
            this.hasLDV = hasLDV;
            this.isEmptyCell = isEmptyCell;
            this.isModifPO = isModifPO;
            this.maxLaunchbyTurn = maxLaunchbyTurn;
            this.maxLaunchbyByTarget = maxLaunchbyByTarget;
            this.coolDown = coolDown;
            this.reqLevel = reqLevel;
            this.isEcEndTurn = isEcEndTurn;
            this.effects = parseEffect(effects);
            this.CCeffects = parseEffect(ceffects);
            this.porteeType = typePortee;
        }

        private ArrayList<SpellEffect> parseEffect(String e) {
            ArrayList<SpellEffect> effets = new ArrayList<SpellEffect>();
            String[] splt = e.split("\\|");
            for (String a : splt) {
                try {
                    if (e.equals("-1"))
                        continue;
                    int id = Integer.parseInt(a.split(";", 2)[0]);
		     if (id == 400) trap = true; // spell effect '400' represents a trap
                    String args = a.split(";", 2)[1];
                    effets.add(new SpellEffect(id, args, spellID, level));
                } catch (Exception f) {
                    f.printStackTrace();
                    Main.stop("parseEffect spell");
                }
            }
            return effets;
        }

        public int getSpellID() {
            return spellID;
        }

        public Spell getSpell() {
            return World.world.getSort(spellID);
        }

        public int getSpriteID() {
            return getSpell().getSpriteID();
        }

        public String getSpriteInfos() {
            return getSpell().getSpriteInfos();
        }

        public int getLevel() {
            return level;
        }

        public int getPACost() {
            return PACost;
        }

        public int getMinPO() {
            return minPO;
        }

        public int getMaxPO() {
            return maxPO;
        }

        public int getTauxCC() {
            return TauxCC;
        }

        public int getTauxEC() {
            return TauxEC;
        }

        public boolean isLineLaunch() {
            return isLineLaunch;
        }

        public boolean hasLDV() {
            return hasLDV;
        }

        public boolean isEmptyCell() {
            return isEmptyCell;
        }

        public boolean isModifPO() {
            return isModifPO;
        }

        public int getMaxLaunchbyTurn() {
            return maxLaunchbyTurn;
        }

        public int getMaxLaunchByTarget() {
            return maxLaunchbyByTarget;
        }

        public int getCoolDown() {
            return coolDown;
        }

        public int getReqLevel() {
            return reqLevel;
        }

        public boolean isEcEndTurn() {
            return isEcEndTurn;
        }

        public ArrayList<SpellEffect> getEffects() {
            return effects;
        }

        public ArrayList<SpellEffect> getCCeffects() {
            return CCeffects;
        }

        public String getPorteeType() {
            return porteeType;
        }
		
		
        public boolean isTrap() {
            return trap;
        }


        public void applySpellEffectToFight(Fight fight, Fighter perso,
                                            GameCase cell, ArrayList<GameCase> cells, boolean isCC) {
            // Seulement appellé par les pieges, or les sorts de piege
            ArrayList<SpellEffect> effets;
            if (isCC)
                effets = CCeffects;
            else
                effets = effects;
            GameServer.a();
            int jetChance = Formulas.getRandomValue(0, 99);
            int curMin = 0;
            for (SpellEffect SE : effets) {
                if (SE.getChance() != 0 && SE.getChance() != 100)// Si pas 100%
                {
                    if (jetChance <= curMin || jetChance >= (SE.getChance() + curMin)) {
                        curMin += SE.getChance();
                        continue;
                    }
                    curMin += SE.getChance();
                }
                ArrayList<Fighter> cibles = SpellEffect.getTargets(SE, fight, cells);

                if ((fight.getType() != Constant.FIGHT_TYPE_CHALLENGE)
                        && (fight.getAllChallenges().size() > 0)) {
                    for (Entry<Integer, Challenge> c : fight.getAllChallenges().entrySet()) {
                        if (c.getValue() == null)
                            continue;
                        c.getValue().onFightersAttacked(cibles, perso, SE, this.getSpellID(), true);
                    }
                }

                SE.applyToFight(fight, perso, cell, cibles);
            }
        }

        public void applySpellEffectToFight(final Fight fight, final Fighter perso,
                                            final GameCase cell, final boolean isCC, final boolean isTrap) {
            ArrayList<SpellEffect> effets;
            if (isCC)
                effets = CCeffects;
            else
                effets = effects;
            int jetChance = 0;
            if (this.getSpell().getSpellID() == 101) // Si c'est roulette
            {
                jetChance = Formulas.getRandomValue(0, 75);
                if (jetChance % 2 == 0)
                    jetChance++;
            } else if (this.getSpell().getSpellID() == 574) // Si c'est Ouverture hasardeuse fantôme
                jetChance = Formulas.getRandomValue(0, 96);
            else if (this.getSpell().getSpellID() == 574) // Si c'est Ouverture hasardeuse
                jetChance = Formulas.getRandomValue(0, 95);
            else
                jetChance = Formulas.getRandomValue(0, 99);
            int curMin = 0;
            int num = 0;
            for (final SpellEffect SE : effets) {
                try {
                    if (fight.getState() >= Constant.FIGHT_STATE_FINISHED)
                        return;
                    if (SE.getChance() != 0 && SE.getChance() != 100)// Si pas 100%
                    {
                        if (jetChance <= curMin
                                || jetChance >= (SE.getChance() + curMin)) {
                            curMin += SE.getChance();
                            num++;
                            continue;
                        }
                        curMin += SE.getChance();
                    }
                    int POnum = num * 2;
                    if (isCC) {
                        POnum += effects.size() * 2;// On zaap la partie du String des effets hors CC
                    }
                    final ArrayList<GameCase> cells = PathFinding.getCellListFromAreaString(fight.getMap(), cell.getId(), perso.getCell().getId(), porteeType, POnum, isCC);
                    final ArrayList<GameCase> finalCells = new ArrayList<GameCase>();
                    int TE = 0;
                    final Spell S = World.world.getSort(spellID);
                    // on prend le targetFlag corespondant au num de l'effet
                    if (S != null && S.getEffectTargets().size() > num) {
                        if (isCC) {
                            if (S.getCCEffectTargets().size() > 0) {
                                if (S.getCCEffectTargets().size() > num) {
                                    TE = S.getCCEffectTargets().get(num);
                                }
                            } else {
                                S.getEffectTargets().get(num);
                            }
                        }
                        else {
                            TE = S.getEffectTargets().get(num);
                        }
                    }
                    if(spellID == 192) { // Fix Ronce Apaisante Level 6
                        if(level == 6 && num == 1){
                            TE = 64;
                        }
                    }

                    for (final GameCase C : cells) {
                        if (C == null)
                            continue;
                        final Fighter F = C.getFirstFighter();
                        if (F == null)
                            continue;
                        // Ne touches pas les alliés : 1
                        if (((TE & 1) == 1) && (F.getTeam() == perso.getTeam()))
                            continue;
                        // Ne touche pas le lanceur : 2
                        if ((((TE >> 1) & 1) == 1) && (F.getId() == perso.getId()))
                            continue;
                        // Ne touche pas les ennemies : 4
                        if ((((TE >> 2) & 1) == 1) && (F.getTeam() != perso.getTeam()))
                            continue;
                        // Ne touche pas les combatants (seulement invocations) : 8
                        if ((((TE >> 3) & 1) == 1) && (!F.isInvocation()))
                            continue;
                        // Ne touche pas les invocations : 16
                        if ((((TE >> 4) & 1) == 1) && (F.isInvocation()))
                            continue;
                        // N'affecte que le lanceur : 32
                        if ((((TE >> 5) & 1) == 1) && (F.getId() != perso.getId()))
                            continue;
                        // N'affecte que les alliés (pas le lanceur) : 64
                        if ((((TE >> 6) & 1) == 1) && (F.getTeam() != perso.getTeam() || F.getId() == perso.getId()))
                            continue;
                        // N'affecte PERSONNE : 1024
                        if ((((TE >> 10) & 1) == 1))
                            continue;
                        // Si pas encore eu de continue, on ajoute la case, tout le monde : 0
                        finalCells.add(C);
                    }
                    // Si le sort n'affecte que le lanceur et que le lanceur n'est
                    // pas dans la zone

                    if (((TE >> 5) & 1) == 1)
                        if (!finalCells.contains(perso.getCell()))
                            finalCells.add(perso.getCell());
                    final ArrayList<Fighter> cibles = SpellEffect.getTargets(SE, fight, finalCells);

                    if(SE.getEffectID() == 202)
                    {
                    	// UnHide Traps
                    	final List<Trap> traps = fight.getAllTraps();
                    	for(final GameCase gameCase : cells) {
                    		if(gameCase == null) continue;
                    		for(final Trap trap : traps)
                    		{
                    			if(trap.isUnHide()) continue;
                    			if(perso.getTeam() == trap.getCaster().getTeam()) continue;
                    			if(PathFinding.getDistanceBetween(fight.getMap(), trap.getCell().getId(), gameCase.getId()) > trap.getSize()) continue;
                    			trap.setIsUnHide((byte) perso.getTeam());
                    			trap.appear((byte) perso.getTeam());
                    		}
                    	}
                    }
                    
                    if ((fight.getType() != Constant.FIGHT_TYPE_CHALLENGE)
                            && (fight.getAllChallenges().size() > 0)) {
                        for (final Entry<Integer, Challenge> c : fight.getAllChallenges().entrySet()) {
                            if (c.getValue() == null)
                                continue;
                            c.getValue().onFightersAttacked(cibles, perso, SE, this.getSpellID(), isTrap);
                        }
                    }
                    SE.applyToFight(fight, perso, cell, cibles);
                    num++;
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
