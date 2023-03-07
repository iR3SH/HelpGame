package org.starloco.locos.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.starloco.locos.game.world.World;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.object.ObjectTemplate;

public class Job {

    private int id;
    private ArrayList<Integer> tools = new ArrayList<>();
    private Map<Integer, ArrayList<Integer>> crafts = new HashMap<>();
    private Map<Integer, ArrayList<Integer>> skills = new HashMap<>();

    public Job(int id, String tools, String crafts, String skills) {
        this.id = id;
        if (!tools.equals("")) {
            for (String str : tools.split(",")) {
                try {
                    this.tools.add(Integer.parseInt(str));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!crafts.equals("")) {
            for (String str : crafts.split("\\|")) {
                try {
                    int skID = Integer.parseInt(str.split(";")[0]);
                    ArrayList<Integer> list = new ArrayList<>();
                    for (String str2 : str.split(";")[1].split(","))
                        list.add(Integer.parseInt(str2));
                    this.crafts.put(skID, list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!skills.isEmpty() || !skills.equals("")) {
            for (String arg0 : skills.split("\\|")) {
                String io = arg0.split(";")[0], skill = arg0.split(";")[1];
                ArrayList<Integer> list = new ArrayList<>();

                for (String arg1 : skill.split(","))
                    list.add(Integer.parseInt(arg1));

                for (String arg1 : io.split(","))
                    this.skills.put(Integer.parseInt(arg1), list);
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public Map<Integer, ArrayList<Integer>> getSkills() {
        return skills;
    }

    public boolean isValidTool(int id1) {
        for (int id : this.tools)
            if (id == id1)
                return true;
        return false;
    }

    public ArrayList<Integer> getListBySkill(int skill) {
        return this.crafts.get(skill);
    }

    public boolean canCraft(int skill, int template) {
        if (this.crafts.get(skill) != null)
            for (int id : this.crafts.get(skill))
                if (id == template)
                    return true;
        return false;
    }

    public boolean isMaging() {
        return (this.id > 42 && this.id < 51) || (this.id > 61 && this.id < 65);
    }
    
    
    public static int getBaseMaxJet(int templateID, String statsModif) {
        ObjectTemplate t = World.world.getObjTemplate(templateID);
        String[] splitted = t.getStrTemplate().split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            if (stats[0].compareTo(statsModif) > 0)//Effets n'existe pas de base
            {
                continue;
            } else if (stats[0].compareTo(statsModif) == 0)//L'effet existe bien !
            {
                int max = Integer.parseInt(stats[2], 16);
                if (max == 0)
                    max = Integer.parseInt(stats[1], 16);//Pas de jet maximum on prend le minimum
                return max;
            }
        }
        return 0;
    }
    
    
    public static int getActualJet(GameObject obj, String statsModif) {
        for (Entry<Integer, Integer> entry : obj.getStats().getMap().entrySet()) {
            if (Integer.toHexString(entry.getKey()).compareTo(statsModif) > 0)//Effets inutiles
            {
                continue;
            } else if (Integer.toHexString(entry.getKey()).compareTo(statsModif) == 0)//L'effet existe bien !
            {
                int JetActual = entry.getValue();
                return JetActual;
            }
        }
        return 0;
    }
    
    
    public static byte viewActualStatsItem(GameObject obj, String stats)//retourne vrai si le stats est actuellement sur l'item
    {
        if (!obj.parseStatsString().isEmpty()) {
            for (Entry<Integer, Integer> entry : obj.getStats().getMap().entrySet()) {
                if (Integer.toHexString(entry.getKey()).compareTo(stats) > 0)//Effets inutiles
                {
                    if (Integer.toHexString(entry.getKey()).compareTo("98") == 0
                            && stats.compareTo("7b") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9a") == 0
                            && stats.compareTo("77") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9b") == 0
                            && stats.compareTo("7e") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("9d") == 0
                            && stats.compareTo("76") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("74") == 0
                            && stats.compareTo("75") == 0) {
                        return 2;
                    } else if (Integer.toHexString(entry.getKey()).compareTo("99") == 0
                            && stats.compareTo("7d") == 0) {
                        return 2;
                    } else {
                        continue;
                    }
                } else if (Integer.toHexString(entry.getKey()).compareTo(stats) == 0)//L'effet existe bien !
                {
                    return 1;
                }
            }
            return 0;
        } else {
            return 0;
        }
    }

    public static byte viewBaseStatsItem(GameObject obj, String ItemStats)//retourne vrai si le stats existe de base sur l'item
    {

        String[] splitted = obj.getTemplate().getStrTemplate().split(",");
        for (String s : splitted) {
            String[] stats = s.split("#");
            if (stats[0].compareTo(ItemStats) > 0)//Effets n'existe pas de base
            {
                if (stats[0].compareTo("98") == 0
                        && ItemStats.compareTo("7b") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9a") == 0
                        && ItemStats.compareTo("77") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9b") == 0
                        && ItemStats.compareTo("7e") == 0) {
                    return 2;
                } else if (stats[0].compareTo("9d") == 0
                        && ItemStats.compareTo("76") == 0) {
                    return 2;
                } else if (stats[0].compareTo("74") == 0
                        && ItemStats.compareTo("75") == 0) {
                    return 2;
                } else if (stats[0].compareTo("99") == 0
                        && ItemStats.compareTo("7d") == 0) {
                    return 2;
                } else {
                    continue;
                }
            } else if (stats[0].compareTo(ItemStats) == 0)//L'effet existe bien !
            {
                return 1;
            }
        }
        return 0;
    }
}

    
    
    
    
    

