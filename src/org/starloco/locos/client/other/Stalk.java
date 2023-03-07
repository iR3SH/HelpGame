package org.starloco.locos.client.other;

import org.starloco.locos.client.Player;

public class Stalk {
    private long time;
    private Player tracked;

    public Stalk(long time, Player p) {
        this.time = time;
        this.tracked = p;
    }

    public Player getTraque() {
        return this.tracked;
    }

    public void setTraque(Player p) {
        this.tracked = p;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long t) {
        this.time = t;
    }
}