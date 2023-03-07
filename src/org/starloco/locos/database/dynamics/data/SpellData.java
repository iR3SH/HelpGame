package org.starloco.locos.database.dynamics.data;

import com.zaxxer.hikari.HikariDataSource;
import org.starloco.locos.database.dynamics.AbstractDAO;
import org.starloco.locos.entity.monster.Monster;
import org.starloco.locos.fight.spells.Spell;
import org.starloco.locos.fight.spells.Spell.SortStats;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpellData extends AbstractDAO<Spell> {
    public SpellData(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void load(Object obj) {
    }

    @Override
    public boolean update(Spell obj) {
        return false;
    }

    public void load() {
        Result result = null;
        try {
            result = getData("SELECT  * from sorts");
            ResultSet RS = result.resultSet;
            boolean modif = false;
            while (RS.next()) {
                int id = RS.getInt("id");

                if (World.world.getSort(id) != null) {
                    Spell spell = World.world.getSort(id);
                    spell.setInfos(RS.getInt("sprite"), RS.getString("spriteInfos"), RS.getString("effectTarget"), RS.getInt("type"), RS.getInt("duration"));
                    SortStats l1 = parseSortStats(id, 1, RS.getString("lvl1"));
                    SortStats l2 = parseSortStats(id, 2, RS.getString("lvl2"));
                    SortStats l3 = parseSortStats(id, 3, RS.getString("lvl3"));
                    SortStats l4 = parseSortStats(id, 4, RS.getString("lvl4"));
                    SortStats l5 = null;
                    if (!RS.getString("lvl5").equalsIgnoreCase("-1"))
                        l5 = parseSortStats(id, 5, RS.getString("lvl5"));
                    SortStats l6 = null;
                    if (!RS.getString("lvl6").equalsIgnoreCase("-1"))
                        l6 = parseSortStats(id, 6, RS.getString("lvl6"));
                    spell.getSortsStats().clear();

                    spell.addSortStats(1, l1);
                    spell.addSortStats(2, l2);
                    spell.addSortStats(3, l3);
                    spell.addSortStats(4, l4);
                    spell.addSortStats(5, l5);
                    spell.addSortStats(6, l6);
                    modif = true;
                } else {
                    Spell sort = new Spell(id, RS.getString("nom"), RS.getInt("sprite"), RS.getString("spriteInfos"), RS.getString("effectTarget"), RS.getInt("type"), RS.getInt("duration"));
                    SortStats l1 = parseSortStats(id, 1, RS.getString("lvl1"));
                    SortStats l2 = parseSortStats(id, 2, RS.getString("lvl2"));
                    SortStats l3 = parseSortStats(id, 3, RS.getString("lvl3"));
                    SortStats l4 = parseSortStats(id, 4, RS.getString("lvl4"));
                    SortStats l5 = null;
                    if (!RS.getString("lvl5").equalsIgnoreCase("-1"))
                        l5 = parseSortStats(id, 5, RS.getString("lvl5"));
                    SortStats l6 = null;
                    if (!RS.getString("lvl6").equalsIgnoreCase("-1"))
                        l6 = parseSortStats(id, 6, RS.getString("lvl6"));
                    sort.addSortStats(1, l1);
                    sort.addSortStats(2, l2);
                    sort.addSortStats(3, l3);
                    sort.addSortStats(4, l4);
                    sort.addSortStats(5, l5);
                    sort.addSortStats(6, l6);
                    World.world.addSort(sort);
                }
            }
            if (modif)
                for (Monster monster : World.world.getMonstres())
                    monster.getGrades().values().forEach(Monster.MobGrade::refresh);
        } catch (SQLException e) {
            super.sendError("SortData load", e);
            Main.stop("unknown");
        } finally {
            close(result);
        }
    }

    private SortStats parseSortStats(int id, int lvl, String str) {
        try {
            String[] stat = str.split(",");
            String effets = stat[0], CCeffets = stat[1];
            int PACOST = 6;

            try {
                PACOST = Integer.parseInt(stat[2].trim());
            } catch (NumberFormatException ignored) {}

            int POm = Integer.parseInt(stat[3].trim());
            int POM = Integer.parseInt(stat[4].trim());
            int TCC = Integer.parseInt(stat[5].trim());
            int TEC = Integer.parseInt(stat[6].trim());

            boolean line = stat[7].trim().equalsIgnoreCase("true");
            boolean LDV = stat[8].trim().equalsIgnoreCase("true");
            boolean emptyCell = stat[9].trim().equalsIgnoreCase("true");
            boolean MODPO = stat[10].trim().equalsIgnoreCase("true");

            int MaxByTurn = Integer.parseInt(stat[12].trim());
            int MaxByTarget = Integer.parseInt(stat[13].trim());
            int CoolDown = Integer.parseInt(stat[14].trim());

            String type = stat[15].trim();

            int level = Integer.parseInt(stat[stat.length - 2].trim());
            boolean endTurn = stat[19].trim().equalsIgnoreCase("true");

            return new SortStats(id, lvl, PACOST, POm, POM, TCC, TEC, line, LDV, emptyCell, MODPO, MaxByTurn, MaxByTarget, CoolDown, level, endTurn, effets, CCeffets, type);
        } catch (Exception e) {
            super.sendError("SortData parseSortStats", e);
            return null;
        }
    }
}
