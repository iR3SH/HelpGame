package org.starloco.locos.entity;

import org.starloco.locos.area.Area;
import org.starloco.locos.area.SubArea;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.fight.Fight;
import org.starloco.locos.fight.Fighter;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;

import java.util.HashMap;

public class Prism {

    private int id;
    private int alignement;
    private int level;
    private short Map;
    private int cell;
    private int dir;
    private int name;
    private int gfx;
    private int inFight;
    private int fightId;
    private int turnTime = 45000;
    private int honor = 0;
    private int area = -1;
    private Fight fight;
    private java.util.Map<Integer, Integer> stats = new HashMap<Integer, Integer>();

    public Prism(int id, int alignement, int level, short Map, int cell,
                 int honor, int area) {
        this.id = id;
        this.alignement = alignement;
        this.level = level;
        this.Map = Map;
        this.cell = cell;
        this.dir = 1;
        if (alignement == 1) {
            this.name = 1111;
            this.gfx = 8101;
        } else {
            this.name = 1112;
            this.gfx = 8100;
        }
        this.inFight = -1;
        this.fightId = -1;
        this.honor = honor;
        this.area = area;
        this.fight = null;
    }

    public static void parseAttack(Player perso) {
        for (Prism Prisme : World.world.AllPrisme())
            if ((Prisme.inFight == 0 || Prisme.inFight == -2)
                    && perso.get_align() == Prisme.getAlignement())
                SocketManager.SEND_Cp_INFO_ATTAQUANT_PRISME(perso, attackerOfPrisme(Prisme.id, Prisme.Map, Prisme.fightId));
    }

    public static void parseDefense(Player perso) {
        for (Prism Prisme : World.world.AllPrisme())
            if ((Prisme.inFight == 0 || Prisme.inFight == -2)
                    && perso.get_align() == Prisme.getAlignement())
                SocketManager.SEND_CP_INFO_DEFENSEURS_PRISME(perso, defenderOfPrisme(Prisme.id, Prisme.Map, Prisme.fightId));
    }

    public static String attackerOfPrisme(int id, short MapId, int FightId) {
        String str = "+";
        str += Integer.toString(id, 36);
        GameMap gameMap = World.world.getMap(MapId);
        if(gameMap != null) {
            for (Fight fight : gameMap.getFights()) {
                if (fight.getId() == FightId) {
                    for (Fighter fighter : fight.getFighters(1)) {
                        if (fighter.getPersonnage() == null)
                            continue;
                        str += "|";
                        str += Integer.toString(fighter.getPersonnage().getId(), 36) + ";";
                        str += fighter.getPersonnage().getName() + ";";
                        str += fighter.getPersonnage().getLevel() + ";";
                        str += "0;";
                    }
                }
            }
        }
        return str;
    }

    public static String defenderOfPrisme(int id, short MapId, int FightId) {
        String str = "+";
        String stra = "";
        str += Integer.toString(id, 36);
        GameMap gameMap = World.world.getMap(MapId);
        if(gameMap != null) {
            for (Fight fight : gameMap.getFights()) {
                if (fight.getId() == FightId) {
                    for (Fighter fighter : fight.getFighters(2)) {
                        if (fighter.getPersonnage() == null)
                            continue;
                        str += "|";
                        str += Integer.toString(fighter.getPersonnage().getId(), 36)
                                + ";";
                        str += fighter.getPersonnage().getName() + ";";
                        str += fighter.getPersonnage().getGfxId() + ";";
                        str += fighter.getPersonnage().getLevel() + ";";
                        str += Integer.toString(fighter.getPersonnage().getColor1(), 36)
                                + ";";
                        str += Integer.toString(fighter.getPersonnage().getColor2(), 36)
                                + ";";
                        str += Integer.toString(fighter.getPersonnage().getColor3(), 36)
                                + ";";
                        if (fight.getFighters(2).size() > 7)
                            str += "1;";
                        else
                            str += "0;";
                    }
                    stra = str.substring(1);
                    stra = "-" + stra;
                    fight.setDefenders(stra);
                }
            }
        }
        return str;
    }

    public int getId() {
        return this.id;
    }

    public int getAlignement() {
        return this.alignement;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int i) {
        this.level = i;
    }

    public short getMap() {
        return this.Map;
    }

    public int getCell() {
        return this.cell;
    }

    public void setCell(int i) {
        this.cell = i;
    }

    public int getInFight() {
        return this.inFight;
    }

    public void setInFight(int i) {
        this.inFight = i;
    }

    public int getFightId() {
        return this.fightId;
    }

    public void setFightId(int i) {
        this.fightId = i;
    }

    public int getTurnTime() {
        return this.turnTime;
    }

    public int getHonor() {
        return this.honor;
    }

    public void addHonor(int i) {
        this.honor += i;
    }

    public int getGrade() {
        int g = 1;
        for (int n = 1; n <= 10; n++) {
            if (this.honor < World.world.getExpLevel(n).pvp) {
                g = n - 1;
                break;
            }
        }
        return g;
    }

    public int getConquestArea() {
        return this.area;
    }

    public void setConquestArea(int i) {
        this.area = i;
    }

    public Fight getFight() {
        return this.fight;
    }

    public void setFight(Fight fight) { this.fight = fight; }

    public Stats getStats() {
        return new Stats(this.stats);
    }

    public void refreshStats() {
        int feu = 1000 + (500 * this.level);
        int intel = 1000 + (500 * this.level);
        int agi = 1000 + (500 * this.level);
        int sagesse = 1000 + (500 * this.level);
        int chance = 1000 + (500 * this.level);
        int resistance = 9 * this.level;
        this.stats.clear();
        this.stats.put(Constant.STATS_ADD_FORC, feu);
        this.stats.put(Constant.STATS_ADD_INTE, intel);
        this.stats.put(Constant.STATS_ADD_AGIL, agi);
        this.stats.put(Constant.STATS_ADD_SAGE, sagesse);
        this.stats.put(Constant.STATS_ADD_CHAN, chance);
        this.stats.put(Constant.STATS_ADD_RP_NEU, resistance);
        this.stats.put(Constant.STATS_ADD_RP_FEU, resistance);
        this.stats.put(Constant.STATS_ADD_RP_EAU, resistance);
        this.stats.put(Constant.STATS_ADD_RP_AIR, resistance);
        this.stats.put(Constant.STATS_ADD_RP_TER, resistance);
        this.stats.put(Constant.STATS_ADD_AFLEE, resistance);
        this.stats.put(Constant.STATS_ADD_MFLEE, resistance);
        this.stats.put(Constant.STATS_ADD_PA, 6);
        this.stats.put(Constant.STATS_ADD_PM, 0);
    }

    public int getX() {
        GameMap Map = World.world.getMap(this.Map);
        return Map.getX();
    }

    public int getY() {
        GameMap Map = World.world.getMap(this.Map);
        return Map.getY();
    }

    public SubArea getSubArea() {
        GameMap Map = World.world.getMap(this.Map);
        return Map.getSubArea();
    }

    public Area getArea() {
        GameMap Map = World.world.getMap(this.Map);
        return Map.getSubArea().getArea();
    }

    public String getGMPrisme() {
        if (this.inFight != -1)
            return "";
        String str = "GM|+";
        str += this.cell + ";";
        str += this.dir + ";0;" + this.id + ";" + this.name + ";-10;"
                + this.gfx + "^100;" + this.level + ";" + getGrade() + ";"
                + this.alignement;
        return str;
    }
}
