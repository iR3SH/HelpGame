package org.starloco.locos.game.scheduler.entity;

import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.util.TimerWaiter;

import java.util.ArrayList;

public class WorldPub extends Updatable {

    public final static ArrayList<String> pubs = new ArrayList<>();
    public final static Updatable updatable = new WorldPub(1200000);

    public WorldPub(int wait) {
        super(wait);
    }

    @Override
    public void update() {
        if(!WorldPub.pubs.isEmpty()) {
            if (this.verify()) {
                String pub = WorldPub.pubs.get(Formulas.getRandomValue(0, WorldPub.pubs.size() - 1));
                SocketManager.GAME_SEND_MESSAGE_TO_ALL("(Message Auto) : " + pub, "046380");
                TimerWaiter.purge();
            }
        }
    }

    @Override
    public Object get() {
        return null;
    }
}