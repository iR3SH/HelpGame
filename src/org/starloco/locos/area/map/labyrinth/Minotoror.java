package org.starloco.locos.area.map.labyrinth;

import org.starloco.locos.area.map.GameCase;
import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.world.World;
import org.starloco.locos.util.TimerWaiter;

import java.util.concurrent.TimeUnit;

public class Minotoror {

    private static final long time = 10;
    private static short demi = -1;
    private static short momi = -1;

    public static void initialize() {
        closeAll();
        initializeBoss();
    }

    public static void demi() {
        TimerWaiter.addNext(Minotoror::spawnDemi, 10, TimeUnit.MINUTES, TimerWaiter.DataType.MAP);
    }

    public static void momi() {
        TimerWaiter.addNext(Minotoror::spawnMomi, 10, TimeUnit.MINUTES, TimerWaiter.DataType.MAP);
    }

    private static void initializeBoss() {
        spawnDemi();
        spawnMomi();
    }

    private static void spawnDemi() {
        demi = chooseRandomMap();
        World.world.getMap(demi).spawnGroupWith(World.world.getMonstre(832));
        World.world.logger.trace("   > The 'Deminoboule' was added on the map id " + demi + ".");
    }

    private static void spawnMomi() {
        momi = chooseRandomMap();
        World.world.getMap(momi).spawnGroupWith(World.world.getMonstre(831));
        World.world.logger.trace("   > The 'Minotoror' was added on the map id " + momi + ".");
    }

    private static short chooseRandomMap() {
        short map = 0;
        switch (Formulas.getRandomValue(0, 7)) {
            case 0:
                map = (short) 9575;
                break;
            case 1:
                map = (short) 9576;
                break;
            case 2:
                map = (short) 9577;
                break;
            case 3:
                map = (short) 9556;
                break;
            case 4:
                map = (short) 9560;
                break;
            case 5:
                map = (short) 9561;
                break;
            case 6:
                map = (short) 9562;
                break;
            case 7:
                map = (short) 9563;
                break;
        }
        if((demi != -1 && map == demi) || (momi != -1 && map == momi)) return chooseRandomMap();
        return map;
    }

    public static void ouvrirHaut(GameMap map) {
        closeAll();
        switch (map.getId()) {
            case 9566: // 11�me
                openTimer(World.world.getMap((short) 9560), (short) 302); // 6�me
                break;
            case 9560: // 12�me
                openTimer(World.world.getMap((short) 9555), (short) 332); // 7�me
                break;
            case 9555: // 13�me
                openTimer(World.world.getMap((short) 9574), (short) 273); // 8�me
                break;
            case 9574: // 14�me
                openTimer(World.world.getMap((short) 9553), (short) 288); // 9�me
                break;


            case 9567: // 15�me
                openTimer(World.world.getMap((short) 9561), (short) 317); // 10�me
                openTimer(World.world.getMap((short) 9561), (short) 306); // 5�me
                break;
            case 9561: // 16�me
                openTimer(World.world.getMap((short) 9556), (short) 306); // 11�me
                openTimer(World.world.getMap((short) 9556), (short) 332); // 6�me
                break;
            case 9556: // 17�me
                openTimer(World.world.getMap((short) 9575), (short) 335); // 12�me
                openTimer(World.world.getMap((short) 9575), (short) 331); // 7�me
                break;
            case 9575: // 18�me
                openTimer(World.world.getMap((short) 9564), (short) 335); // 13�me
                openTimer(World.world.getMap((short) 9564), (short) 259); // 8�me
                break;


            case 9568: // 19�me
                openTimer(World.world.getMap((short) 9562), (short) 320); // 14�me
                openTimer(World.world.getMap((short) 9562), (short) 303); // 9�me
                break;
            case 9562: // 20�me
                openTimer(World.world.getMap((short) 9557), (short) 230); // 15�me
                openTimer(World.world.getMap((short) 9557), (short) 306); // 10�me
                break;
            case 9557: // 21�me
                openTimer(World.world.getMap((short) 9576), (short) 306); // 16�me
                openTimer(World.world.getMap((short) 9576), (short) 317); // 11�me
                break;
            case 9576: // 22�me
                openTimer(World.world.getMap((short) 9571), (short) 277); // 17�me
                openTimer(World.world.getMap((short) 9571), (short) 331); // 12�me
                break;


            case 9569: // 23�me
                openTimer(World.world.getMap((short) 9563), (short) 292); // 18�me
                openTimer(World.world.getMap((short) 9563), (short) 288); // 13�me
                break;
            case 9563: // 24�me
                openTimer(World.world.getMap((short) 9558), (short) 277); // 19�me
                openTimer(World.world.getMap((short) 9558), (short) 361); // 14�me
                break;
            case 9558: // 25�me
                openTimer(World.world.getMap((short) 9577), (short) 364); // 20�me
                openTimer(World.world.getMap((short) 9577), (short) 390); // 15�me
                break;
            case 9577: // 25�me
                openTimer(World.world.getMap((short) 9572), (short) 263); // 20�me
                openTimer(World.world.getMap((short) 9572), (short) 346); // 15�me
                break;

            case 9570: // 25�me
                openTimer(World.world.getMap((short) 9565), (short) 262); // 20�me
                break;
            case 9565: // 25�me
                openTimer(World.world.getMap((short) 9559), (short) 277); // 20�me
                break;
            case 9559: // 25�me
                openTimer(World.world.getMap((short) 9554), (short) 306); // 20�me
                break;
            case 9554: // 25�me
                openTimer(World.world.getMap((short) 9573), (short) 219); // 20�me
                break;
        }
    }

    public static void ouvrirBas(GameMap map) {
        closeAll();
        switch (map.getId()) {
            case 9553: // 1�re
                openTimer(World.world.getMap((short) 9574), (short) 273); // 6�me
                break;
            case 9574: // 2�me
                openTimer(World.world.getMap((short) 9555), (short) 332); // 7�me
                break;
            case 9555: // 3�me
                openTimer(World.world.getMap((short) 9560), (short) 302); // 8�me
                break;
            case 9560: // 4�me
                openTimer(World.world.getMap((short) 9566), (short) 332); // 9�me
                break;

            case 9564: // 5�me
                openTimer(World.world.getMap((short) 9575), (short) 335); // 10�me
                openTimer(World.world.getMap((short) 9575), (short) 331); // 15�me
                break;
            case 9575: // 6�me
                openTimer(World.world.getMap((short) 9556), (short) 332); // 11�me
                openTimer(World.world.getMap((short) 9556), (short) 306); // 16�me
                break;
            case 9556: // 7�me
                openTimer(World.world.getMap((short) 9561), (short) 306); // 12�me
                openTimer(World.world.getMap((short) 9561), (short) 317); // 17�me
                break;
            case 9561: // 8�me
                openTimer(World.world.getMap((short) 9567), (short) 277); // 13�me
                openTimer(World.world.getMap((short) 9567), (short) 346); // 18�me
                break;


            case 9571: // 5�me
                openTimer(World.world.getMap((short) 9576), (short) 306); // 10�me
                openTimer(World.world.getMap((short) 9576), (short) 317); // 15�me
                break;
            case 9576: // 6�me
                openTimer(World.world.getMap((short) 9557), (short) 230); // 11�me
                openTimer(World.world.getMap((short) 9557), (short) 306); // 16�me
                break;
            case 9557: // 7�me
                openTimer(World.world.getMap((short) 9562), (short) 320); // 12�me
                openTimer(World.world.getMap((short) 9562), (short) 303); // 17�me
                break;
            case 9562: // 8�me
                openTimer(World.world.getMap((short) 9568), (short) 277); // 13�me
                openTimer(World.world.getMap((short) 9568), (short) 346); // 18�me
                break;


            case 9572: // 5�me
                openTimer(World.world.getMap((short) 9577), (short) 364); // 10�me
                openTimer(World.world.getMap((short) 9577), (short) 390); // 15�me
                break;
            case 9577: // 6�me
                openTimer(World.world.getMap((short) 9558), (short) 361); // 11�me
                openTimer(World.world.getMap((short) 9558), (short) 277); // 16�me
                break;
            case 9558: // 7�me
                openTimer(World.world.getMap((short) 9563), (short) 292); // 12�me
                openTimer(World.world.getMap((short) 9563), (short) 288); // 17�me
                break;
            case 9563: // 8�me
                openTimer(World.world.getMap((short) 9569), (short) 291); // 13�me
                openTimer(World.world.getMap((short) 9569), (short) 317); // 18�me
                break;

            case 9573: // 5�me
                openTimer(World.world.getMap((short) 9554), (short) 306); // 10�me
                break;
            case 9554: // 6�me
                openTimer(World.world.getMap((short) 9559), (short) 277); // 11�me
                break;
            case 9559: // 7�me
                openTimer(World.world.getMap((short) 9565), (short) 262); // 12�me
                break;
            case 9565: // 8�me
                openTimer(World.world.getMap((short) 9570), (short) 306); // 13�me
                break;
        }
    }

    public static void ouvrirGauche(GameMap map) {
        closeAll();
        switch (map.getId()) {
            case 9564: // 2�me
                openTimer(World.world.getMap((short) 9564), (short) 428); // 1�me
                break;
            case 9571: // 3�me
                openTimer(World.world.getMap((short) 9564), (short) 429); // 2�me
                break;
            case 9572: // 4�me
                openTimer(World.world.getMap((short) 9571), (short) 428); // 3�me
                break;
            case 9573: // 5�me
                openTimer(World.world.getMap((short) 9572), (short) 443); // 4�me
                break;
            case 9575: // 7�me
                openTimer(World.world.getMap((short) 9574), (short) 51); // 6�me
                openTimer(World.world.getMap((short) 9574), (short) 425); // 6�me
                break;
            case 9576: // 8�me
                openTimer(World.world.getMap((short) 9575), (short) 94); // 7�me
                openTimer(World.world.getMap((short) 9575), (short) 427); // 7�me
                break;
            case 9577: // 9�me
                openTimer(World.world.getMap((short) 9576), (short) 67); // 8�me
                openTimer(World.world.getMap((short) 9576), (short) 441); // 8�me
                break;
            case 9554: // 10�me
                openTimer(World.world.getMap((short) 9577), (short) 50); // 9�me
                openTimer(World.world.getMap((short) 9577), (short) 426); // 9�me
                break;
            case 9556: // 12�me
                openTimer(World.world.getMap((short) 9555), (short) 64); // 11�me
                openTimer(World.world.getMap((short) 9555), (short) 440); // 11�me
                break;
            case 9557: // 13�me
                openTimer(World.world.getMap((short) 9556), (short) 64); // 12�me
                openTimer(World.world.getMap((short) 9556), (short) 428); // 12�me
                break;
            case 9558: // 14�me
                openTimer(World.world.getMap((short) 9557), (short) 51); // 13�me
                openTimer(World.world.getMap((short) 9557), (short) 413); // 13�me
                break;
            case 9559: // 15�me
                openTimer(World.world.getMap((short) 9558), (short) 77); // 14�me
                openTimer(World.world.getMap((short) 9558), (short) 427); // 14�me
                break;
            case 9561: // 17�me
                openTimer(World.world.getMap((short) 9560), (short) 52); // 16�me
                openTimer(World.world.getMap((short) 9560), (short) 428); // 16�me
                break;
            case 9562: // 18�me
                openTimer(World.world.getMap((short) 9561), (short) 80); // 17�me
                openTimer(World.world.getMap((short) 9561), (short) 441); // 17�me
                break;
            case 9563: // 19�me
                openTimer(World.world.getMap((short) 9562), (short) 52); // 18�me
                openTimer(World.world.getMap((short) 9562), (short) 429); // 18�me
                break;
            case 9565: // 20�me
                openTimer(World.world.getMap((short) 9563), (short) 52); // 19�me
                openTimer(World.world.getMap((short) 9563), (short) 429); // 19�me
                break;
            case 9567: // 22�me
                openTimer(World.world.getMap((short) 9566), (short) 51); // 21�me
                break;
            case 9568: // 23�me
                openTimer(World.world.getMap((short) 9567), (short) 37); // 22�me
                break;
            case 9569: // 24�me
                openTimer(World.world.getMap((short) 9568), (short) 51); // 23�me
                break;
            case 9570: // 25�me
                openTimer(World.world.getMap((short) 9569), (short) 64); // 24�me
                break;
        }
    }

    public static void ouvrirDroite(GameMap map) {
        closeAll();
        switch (map.getId()) {
            case 9553: // 1�re
                openTimer(World.world.getMap((short) 9564), (short) 429); // 2�me
                break;
            case 9564: // 2�me
                openTimer(World.world.getMap((short) 9571), (short) 428); // 3�me
                break;
            case 9571: // 3�me
                openTimer(World.world.getMap((short) 9572), (short) 443); // 4�me
                break;
            case 9572: // 4�me
                openTimer(World.world.getMap((short) 9573), (short) 440); // 5�me
                break;


            case 9574: // 6�me
                openTimer(World.world.getMap((short) 9575), (short) 94); // 7me
                openTimer(World.world.getMap((short) 9575), (short) 427); // 7�me
                break;
            case 9575: // 7�me
                openTimer(World.world.getMap((short) 9576), (short) 67); // 8�me
                openTimer(World.world.getMap((short) 9576), (short) 441); // 8�me
                break;
            case 9576: // 8�me
                openTimer(World.world.getMap((short) 9577), (short) 50); // 9�me
                openTimer(World.world.getMap((short) 9577), (short) 426); // 9�me
                break;
            case 9577: // 9�me
                openTimer(World.world.getMap((short) 9554), (short) 138); // 10�me
                openTimer(World.world.getMap((short) 9554), (short) 431); // 10�me
                break;


            case 9555: // 11�me
                openTimer(World.world.getMap((short) 9556), (short) 64); // 12�me
                openTimer(World.world.getMap((short) 9556), (short) 428); // 12�me
                break;
            case 9556: // 12�me
                openTimer(World.world.getMap((short) 9557), (short) 51); // 13�me
                openTimer(World.world.getMap((short) 9557), (short) 413); // 13�me
                break;
            case 9557: // 13�me
                openTimer(World.world.getMap((short) 9558), (short) 77); // 14�me
                openTimer(World.world.getMap((short) 9558), (short) 427); // 14�me
                break;
            case 9558: // 14�me
                openTimer(World.world.getMap((short) 9559), (short) 65); // 15�me
                openTimer(World.world.getMap((short) 9559), (short) 427); // 15�me
                break;


            case 9560: // 16�me
                openTimer(World.world.getMap((short) 9561), (short) 80); // 17�me
                openTimer(World.world.getMap((short) 9561), (short) 441); // 17�me
                break;
            case 9561: // 17�me
                openTimer(World.world.getMap((short) 9562), (short) 52); // 18�me
                openTimer(World.world.getMap((short) 9562), (short) 429); // 18�me
                break;
            case 9562: // 18�me
                openTimer(World.world.getMap((short) 9563), (short) 52); // 19�me
                openTimer(World.world.getMap((short) 9563), (short) 429); // 19�me
                break;
            case 9563: // 19�me
                openTimer(World.world.getMap((short) 9565), (short) 51); // 20�me
                openTimer(World.world.getMap((short) 9565), (short) 414); // 20�me
                break;


            case 9566: // 21�me
                openTimer(World.world.getMap((short) 9567), (short) 37); // 22�me
                break;
            case 9567: // 22�me
                openTimer(World.world.getMap((short) 9568), (short) 51); // 23�me
                break;
            case 9568: // 23�me
                openTimer(World.world.getMap((short) 9569), (short) 64); // 24�me
                break;
            case 9569: // 24�me
                openTimer(World.world.getMap((short) 9570), (short) 51); // 25�me
                break;

        }
    }

    private static void closeAll() {
        close(World.world.getMap((short) 9553), (short) 428);
        close(World.world.getMap((short) 9553), (short) 288);

        close(World.world.getMap((short) 9554), (short) 431);
        close(World.world.getMap((short) 9554), (short) 306);
        close(World.world.getMap((short) 9554), (short) 138);

        close(World.world.getMap((short) 9555), (short) 440);
        close(World.world.getMap((short) 9555), (short) 332);
        close(World.world.getMap((short) 9555), (short) 79);

        close(World.world.getMap((short) 9556), (short) 428);
        close(World.world.getMap((short) 9556), (short) 306);
        close(World.world.getMap((short) 9556), (short) 332);
        close(World.world.getMap((short) 9556), (short) 64);

        close(World.world.getMap((short) 9557), (short) 413);
        close(World.world.getMap((short) 9557), (short) 306);
        close(World.world.getMap((short) 9557), (short) 230);
        close(World.world.getMap((short) 9557), (short) 51);

        close(World.world.getMap((short) 9558), (short) 427);
        close(World.world.getMap((short) 9558), (short) 277);
        close(World.world.getMap((short) 9558), (short) 361);
        close(World.world.getMap((short) 9558), (short) 77);

        close(World.world.getMap((short) 9559), (short) 427);
        close(World.world.getMap((short) 9559), (short) 277);
        close(World.world.getMap((short) 9559), (short) 65);

        close(World.world.getMap((short) 9560), (short) 428);
        close(World.world.getMap((short) 9560), (short) 302);
        close(World.world.getMap((short) 9560), (short) 52);

        close(World.world.getMap((short) 9561), (short) 441);
        close(World.world.getMap((short) 9561), (short) 306);
        close(World.world.getMap((short) 9561), (short) 317);
        close(World.world.getMap((short) 9561), (short) 80);

        close(World.world.getMap((short) 9562), (short) 429);
        close(World.world.getMap((short) 9562), (short) 320);
        close(World.world.getMap((short) 9562), (short) 303);
        close(World.world.getMap((short) 9562), (short) 52);

        close(World.world.getMap((short) 9563), (short) 429);
        close(World.world.getMap((short) 9563), (short) 292);
        close(World.world.getMap((short) 9563), (short) 288);
        close(World.world.getMap((short) 9563), (short) 52);

        close(World.world.getMap((short) 9564), (short) 429);
        close(World.world.getMap((short) 9564), (short) 335);
        close(World.world.getMap((short) 9564), (short) 259);

        close(World.world.getMap((short) 9565), (short) 414);
        close(World.world.getMap((short) 9565), (short) 262);
        close(World.world.getMap((short) 9565), (short) 51);

        close(World.world.getMap((short) 9566), (short) 332);
        close(World.world.getMap((short) 9566), (short) 51);

        close(World.world.getMap((short) 9567), (short) 277);
        close(World.world.getMap((short) 9567), (short) 346);
        close(World.world.getMap((short) 9567), (short) 37);

        close(World.world.getMap((short) 9568), (short) 277);
        close(World.world.getMap((short) 9568), (short) 346);
        close(World.world.getMap((short) 9568), (short) 51);

        close(World.world.getMap((short) 9569), (short) 291);
        close(World.world.getMap((short) 9569), (short) 317);
        close(World.world.getMap((short) 9569), (short) 64);

        close(World.world.getMap((short) 9570), (short) 306);
        close(World.world.getMap((short) 9570), (short) 51);

        close(World.world.getMap((short) 9571), (short) 428);
        close(World.world.getMap((short) 9571), (short) 277);
        close(World.world.getMap((short) 9571), (short) 331);

        close(World.world.getMap((short) 9572), (short) 443);
        close(World.world.getMap((short) 9572), (short) 263);
        close(World.world.getMap((short) 9572), (short) 346);

        close(World.world.getMap((short) 9573), (short) 440);
        close(World.world.getMap((short) 9573), (short) 219);

        close(World.world.getMap((short) 9574), (short) 425);
        close(World.world.getMap((short) 9574), (short) 273);
        close(World.world.getMap((short) 9574), (short) 51);

        close(World.world.getMap((short) 9575), (short) 427);
        close(World.world.getMap((short) 9575), (short) 335);
        close(World.world.getMap((short) 9575), (short) 331);
        close(World.world.getMap((short) 9575), (short) 94);

        close(World.world.getMap((short) 9576), (short) 441);
        close(World.world.getMap((short) 9576), (short) 306);
        close(World.world.getMap((short) 9576), (short) 317);
        close(World.world.getMap((short) 9576), (short) 67);

        close(World.world.getMap((short) 9577), (short) 426);
        close(World.world.getMap((short) 9577), (short) 364);
        close(World.world.getMap((short) 9577), (short) 390);
        close(World.world.getMap((short) 9577), (short) 50);
    }

    @SuppressWarnings("unused")
    private static void openAll() {
        open(World.world.getMap((short) 9553), (short) 428);
        open(World.world.getMap((short) 9553), (short) 288);

        open(World.world.getMap((short) 9554), (short) 431);
        open(World.world.getMap((short) 9554), (short) 306);
        open(World.world.getMap((short) 9554), (short) 138);

        open(World.world.getMap((short) 9555), (short) 440);
        open(World.world.getMap((short) 9555), (short) 332);
        open(World.world.getMap((short) 9555), (short) 79);

        open(World.world.getMap((short) 9556), (short) 428);
        open(World.world.getMap((short) 9556), (short) 306);
        open(World.world.getMap((short) 9556), (short) 332);
        open(World.world.getMap((short) 9556), (short) 64);

        open(World.world.getMap((short) 9557), (short) 413);
        open(World.world.getMap((short) 9557), (short) 306);
        open(World.world.getMap((short) 9557), (short) 230);
        open(World.world.getMap((short) 9557), (short) 51);

        open(World.world.getMap((short) 9558), (short) 427);
        open(World.world.getMap((short) 9558), (short) 277);
        open(World.world.getMap((short) 9558), (short) 361);
        open(World.world.getMap((short) 9558), (short) 77);

        open(World.world.getMap((short) 9559), (short) 427);
        open(World.world.getMap((short) 9559), (short) 277);
        open(World.world.getMap((short) 9559), (short) 65);

        open(World.world.getMap((short) 9560), (short) 428);
        open(World.world.getMap((short) 9560), (short) 302);
        open(World.world.getMap((short) 9560), (short) 52);

        open(World.world.getMap((short) 9561), (short) 441);
        open(World.world.getMap((short) 9561), (short) 306);
        open(World.world.getMap((short) 9561), (short) 317);
        open(World.world.getMap((short) 9561), (short) 80);

        open(World.world.getMap((short) 9562), (short) 429);
        open(World.world.getMap((short) 9562), (short) 320);
        open(World.world.getMap((short) 9562), (short) 303);
        open(World.world.getMap((short) 9562), (short) 52);

        open(World.world.getMap((short) 9563), (short) 429);
        open(World.world.getMap((short) 9563), (short) 292);
        open(World.world.getMap((short) 9563), (short) 288);
        open(World.world.getMap((short) 9563), (short) 52);

        open(World.world.getMap((short) 9564), (short) 429);
        open(World.world.getMap((short) 9564), (short) 335);
        open(World.world.getMap((short) 9564), (short) 259);

        open(World.world.getMap((short) 9565), (short) 414);
        open(World.world.getMap((short) 9565), (short) 262);
        open(World.world.getMap((short) 9565), (short) 51);

        open(World.world.getMap((short) 9566), (short) 332);
        open(World.world.getMap((short) 9566), (short) 51);

        open(World.world.getMap((short) 9567), (short) 277);
        open(World.world.getMap((short) 9567), (short) 346);
        open(World.world.getMap((short) 9567), (short) 37);

        open(World.world.getMap((short) 9568), (short) 277);
        open(World.world.getMap((short) 9568), (short) 346);
        open(World.world.getMap((short) 9568), (short) 51);

        open(World.world.getMap((short) 9569), (short) 291);
        open(World.world.getMap((short) 9569), (short) 317);
        open(World.world.getMap((short) 9569), (short) 64);

        open(World.world.getMap((short) 9570), (short) 306);
        open(World.world.getMap((short) 9570), (short) 51);

        open(World.world.getMap((short) 9571), (short) 428);
        open(World.world.getMap((short) 9571), (short) 277);
        open(World.world.getMap((short) 9571), (short) 331);

        open(World.world.getMap((short) 9572), (short) 443);
        open(World.world.getMap((short) 9572), (short) 263);
        open(World.world.getMap((short) 9572), (short) 346);

        open(World.world.getMap((short) 9573), (short) 440);
        open(World.world.getMap((short) 9573), (short) 219);

        open(World.world.getMap((short) 9574), (short) 425);
        open(World.world.getMap((short) 9574), (short) 273);
        open(World.world.getMap((short) 9574), (short) 51);

        open(World.world.getMap((short) 9575), (short) 427);
        open(World.world.getMap((short) 9575), (short) 335);
        open(World.world.getMap((short) 9575), (short) 331);
        open(World.world.getMap((short) 9575), (short) 94);

        open(World.world.getMap((short) 9576), (short) 441);
        open(World.world.getMap((short) 9576), (short) 306);
        open(World.world.getMap((short) 9576), (short) 317);
        open(World.world.getMap((short) 9576), (short) 67);

        open(World.world.getMap((short) 9577), (short) 426);
        open(World.world.getMap((short) 9577), (short) 364);
        open(World.world.getMap((short) 9577), (short) 390);
        open(World.world.getMap((short) 9577), (short) 50);
    }

    private static void openTimer(final GameMap map, final short cellId) {
        if (map.getCase(cellId).isWalkable(false)) // Elle est d�j� ouverte
            return;
        open(map, cellId);
        TimerWaiter.addNext(() -> close(map, cellId), time, TimeUnit.MINUTES, TimerWaiter.DataType.MAP);
    }

    private static void open(GameMap map, short cellId) {
        sendOpen(map, cellId);
        map.removeCase(cellId);
        map.getCases().add(new GameCase(map,cellId,true,(byte)0,(byte)0,(byte)0,true,true,-1));
    }

    private static void close(GameMap map, short cellId) {
        if (!map.getCase(cellId).isWalkable(false)) // Elle est d�j� ferm�.
            return;
        sendClose(map, cellId);
        map.removeCase(cellId);
        map.getCases().add(new GameCase(map,cellId,true,(byte)0,(byte)0,(byte)0,false,false,-1));
    }

    private static void sendOpen(GameMap map, int cellId) {
        SocketManager.GAME_UPDATE_CELL(map, cellId + ";aaGaaaaaaa801;1");
        SocketManager.GAME_SEND_ACTION_TO_DOOR(map, cellId, true);
    }

    private static void sendOpen(Player p, int cellId) {
        SocketManager.GAME_UPDATE_CELL(p, cellId + ";aaGaaaaaaa801;1");
        SocketManager.GAME_SEND_ACTION_TO_DOOR(p, cellId, true);
    }

    private static void sendClose(GameMap map, int cellId) {
        SocketManager.GAME_UPDATE_CELL(map, cellId + ";aaaaaaaaaa801;1");
        SocketManager.GAME_SEND_ACTION_TO_DOOR(map, cellId, false);
    }

    private static void sendClose(Player p, int cellId) {
        SocketManager.GAME_UPDATE_CELL(p, cellId + ";aaaaaaaaaa801;1");
        SocketManager.GAME_SEND_ACTION_TO_DOOR(p, cellId, false);
    }

    public static void sendPacketMap(Player perso) {
        GameMap map = perso.getCurMap();
        GameCase c1 = null; // bas
        GameCase c2 = null; // gauche
        GameCase c3 = null; // droite
        GameCase c4 = null; // haut
        switch (map.getId()) {
            case 9553:
                c1 = map.getCase(428);
                c3 = map.getCase(288);
                break;
            case 9554:
                c1 = map.getCase(431);
                c2 = map.getCase(306);
                c4 = map.getCase(138);
                break;
            case 9555:
                c1 = map.getCase(440);
                c3 = map.getCase(332);
                c4 = map.getCase(79);
                break;
            case 9556:
                c1 = map.getCase(428);
                c2 = map.getCase(306);
                c3 = map.getCase(332);
                c4 = map.getCase(64);
                break;
            case 9557:
                c1 = map.getCase(413);
                c2 = map.getCase(306);
                c3 = map.getCase(230);
                c4 = map.getCase(51);
                break;
            case 9558:
                c1 = map.getCase(427);
                c2 = map.getCase(277);
                c3 = map.getCase(361);
                c4 = map.getCase(77);
                break;
            case 9559:
                c1 = map.getCase(427);
                c2 = map.getCase(277);
                c4 = map.getCase(65);
                break;
            case 9560:
                c1 = map.getCase(428);
                c3 = map.getCase(302);
                c4 = map.getCase(52);
                break;
            case 9561:
                c1 = map.getCase(441);
                c2 = map.getCase(306);
                c3 = map.getCase(317);
                c4 = map.getCase(80);
                break;
            case 9562:
                c1 = map.getCase(429);
                c2 = map.getCase(320);
                c3 = map.getCase(303);
                c4 = map.getCase(52);
                break;
            case 9563:
                c1 = map.getCase(429);
                c2 = map.getCase(292);
                c3 = map.getCase(288);
                c4 = map.getCase(52);
                break;
            case 9564:
                c1 = map.getCase(429);
                c2 = map.getCase(335);
                c3 = map.getCase(259);
                break;
            case 9565:
                c1 = map.getCase(414);
                c2 = map.getCase(262);
                c4 = map.getCase(51);
                break;
            case 9566:
                c3 = map.getCase(332);
                c4 = map.getCase(51);
                break;
            case 9567:
                c2 = map.getCase(277);
                c3 = map.getCase(346);
                c4 = map.getCase(37);
                break;
            case 9568:
                c2 = map.getCase(277);
                c3 = map.getCase(346);
                c4 = map.getCase(51);
                break;
            case 9569:
                c2 = map.getCase(291);
                c3 = map.getCase(317);
                c4 = map.getCase(64);
                break;
            case 9570:
                c2 = map.getCase(306);
                c4 = map.getCase(51);
                break;
            case 9571:
                c1 = map.getCase(428);
                c2 = map.getCase(277);
                c3 = map.getCase(331);
                break;
            case 9572:
                c1 = map.getCase(443);
                c2 = map.getCase(263);
                c3 = map.getCase(346);
                break;
            case 9573:
                c1 = map.getCase(440);
                c2 = map.getCase(219);
                break;
            case 9574:
                c1 = map.getCase(425);
                c3 = map.getCase(273);
                c4 = map.getCase(51);
                break;
            case 9575:
                c1 = map.getCase(427);
                c2 = map.getCase(335);
                c3 = map.getCase(331);
                c4 = map.getCase(94);
                break;
            case 9576:
                c1 = map.getCase(441);
                c2 = map.getCase(306);
                c3 = map.getCase(317);
                c4 = map.getCase(67);
                break;
            case 9577:
                c1 = map.getCase(426);
                c2 = map.getCase(364);
                c3 = map.getCase(390);
                c4 = map.getCase(50);
                break;
        }

        if (c1 != null) {
            if (c1.isWalkable(false))
                sendOpen(perso, c1.getId());
            else
                sendClose(perso, c1.getId());
        }

        if (c2 != null) {
            if (c2.isWalkable(false))
                sendOpen(perso, c2.getId());
            else
                sendClose(perso, c2.getId());
        }

        if (c3 != null) {
            if (c3.isWalkable(false))
                sendOpen(perso, c3.getId());
            else
                sendClose(perso, c3.getId());
        }

        if (c4 != null) {
            if (c4.isWalkable(false))
                sendOpen(perso, c4.getId());
            else
                sendClose(perso, c4.getId());
        }
    }

    public static boolean isValidMap(GameMap map) {
        if(map == null) return false;
        switch(map.getId()) {
            case 9553: case 9564: case 9571: case 9572: case 9573:
            case 9574: case 9575: case 9576: case 9577: case 9554:
            case 9555: case 9556: case 9557: case 9558: case 9559:
            case 9560: case 9561: case 9562: case 9563: case 9565:
            case 9566: case 9567: case 9568: case 9569: case 9570:
                return true;
        }
        return false;
    }
}
