package org.starloco.locos.object.entity;

import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.object.GameObject;

import java.util.ArrayList;
// By Coding Mestre : [FIX] - Fixed a bug that was causing some fights to have two challenges instead of one Close #37
import java.util.Arrays;


public class SoulStone extends GameObject {

    private ArrayList<Couple<Integer, Integer>> monsters;

    public SoulStone(int id, int quantity, int template, int pos, String strStats) {
        super(id, template, quantity, pos, strStats, 0);
        this.monsters = new ArrayList<>();
        this.parseStringToStats(strStats);
    }

    public void parseStringToStats(String m) {
        if (!m.equalsIgnoreCase("")) {
            if (this.monsters == null)
                this.monsters = new ArrayList<>();

            String[] split = m.split("\\|");
            for (String s : split) {
                try {
                    int monstre = Integer.parseInt(s.split(",")[0]);
                    int level = Integer.parseInt(s.split(",")[1]);
                    Couple<Integer, Integer> couple = new Couple<Integer, Integer>(monstre, level);
                    this.monsters.add(couple);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String parseStatsString() {
        StringBuilder stats = new StringBuilder();
        boolean isFirst = true;
        for (Couple<Integer, Integer> coupl : this.monsters) {
            if (!isFirst)
                stats.append(",");
            try {
                stats.append("274#").append(coupl.second).append("#0#").append(Integer.toHexString(coupl.first));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            isFirst = false;
        }
        return stats.toString();
    }

    public String parseGroupData() {
        StringBuilder toReturn = new StringBuilder();
        boolean isFirst = true;
        for (Couple<Integer, Integer> curMob : this.monsters) {
            if (!isFirst)
                toReturn.append(";");
            toReturn.append(curMob.first).append(",").append(curMob.second).append(",").append(curMob.second);
            isFirst = false;
        }
        return toReturn.toString();
    }

    public String parseToSave() {
        StringBuilder toReturn = new StringBuilder();
        boolean isFirst = true;
        for (Couple<Integer, Integer> curMob : this.monsters) {
            if (!isFirst)
                toReturn.append("|");
            toReturn.append(curMob.first).append(",").append(curMob.second);
            isFirst = false;
        }
        return toReturn.toString();
    }

   // public static boolean isInArenaMap(int id) {
       // return "10131,10132,10133,10134,10135,10136,10137,10138".contains(String.valueOf(id));
    // By Coding Mestre - [FIX] - Fixed a bug that was causing some fights to have two challenges instead of one Close #37
    public static boolean isInArenaMap(int currentMapId) {
        return Arrays.asList(10131, 10132, 10133, 10134, 10135, 10136, 10137, 10138).contains(currentMapId);

    }
}
