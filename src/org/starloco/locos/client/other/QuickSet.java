package org.starloco.locos.client.other;

import org.starloco.locos.client.Player;
import org.starloco.locos.database.Database;
import org.starloco.locos.fight.spells.SpellEffect;
import org.starloco.locos.game.world.World;
import org.starloco.locos.object.GameObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuickSet {
    private Player player;
    private int pos;
    private String objectsString;
    private Map<Integer, GameObject> objectsArray = new HashMap<>();
    private int iconId;
    private String name;

    public QuickSet(Player owner,int pos,int iconId, String objectsString, String name)
    {
        this.player = owner;
        this.pos = pos;
        this.iconId = iconId;
        this.objectsString = objectsString;
        if(!objectsString.isEmpty()) {
            for (String ObjIdString : objectsString.split(";")) {
                if (!(ObjIdString.isEmpty()) ) {
                    String[] test = ObjIdString.split(",");
                    int Guid = Integer.parseInt(test[0]);
                    int posobj = Integer.parseInt(test[1]);
                    GameObject obj = World.world.getGameObject(Guid);
                    if (obj != null) {
                        objectsArray.put(posobj,obj);
                    }
                }
            }
        }
        this.name = name;
    }

    public String getItemList() {
        StringBuilder items = new StringBuilder();
        if (!this.objectsArray.isEmpty())
            for (GameObject obj : this.objectsArray.values())
                items.append(obj.parseItem());
        return items.toString();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public Map<Integer, GameObject> getObjectsArray() {
        return objectsArray;
    }

    public void setObjectsArray(Map<Integer, GameObject> objectsString) {
        this.objectsArray = objectsString;
    }

    public String getObjectsString() {
        return objectsString;
    }

    public void setObjectsString(String objectsString) {
        this.objectsString = objectsString;
    }

    public int getPosId() {
        return pos;
    }

    public void setPosId(int pos) {
        this.pos = pos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
