package org.starloco.locos.client.other;

import org.starloco.locos.client.Player;
import org.starloco.locos.object.GameObject;

public class Shortcuts {
    private Player player;
    private int position;
    private GameObject object;

    public Shortcuts(Player player, int position, GameObject object)
    {
        this.player = player;
        this.position = position;
        this.object = object;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public GameObject getObject() {
        return object;
    }

    public void setObject(GameObject object) {
        this.object = object;
    }
}
