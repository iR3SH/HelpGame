package org.starloco.locos.entity.npc;

import org.starloco.locos.client.Player;
import org.starloco.locos.quest.Quest;

public class Npc {
    private int id, cellid;
    private byte orientation;
    private NpcTemplate template;

    public Npc(int id, int cellid, byte orientation, NpcTemplate template) {
        this.id = id;
        this.cellid = cellid;
        this.orientation = orientation;
        this.template = template;
    }

    public int getId() {
        return id;
    }

    public int getCellid() {
        return cellid;
    }

    public void setCellid(int cellid) {
        this.cellid = cellid;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(byte orientation) {
        this.orientation = orientation;
    }

    public NpcTemplate getTemplate() {
        return this.template;
    }

    public String parse(boolean alter, Player p) {
        StringBuilder sock = new StringBuilder();
        sock.append((alter ? "~" : "+"));
        sock.append(this.cellid).append(";");
        sock.append(this.orientation).append(";");
        sock.append("0").append(";");
        sock.append(this.id).append(";");
        sock.append(this.template.getId()).append(";");
        sock.append("-4").append(";");//type = NPC
        sock.append(this.template.getGfxId()).append("^");
        if (this.template.getScaleX() == this.template.getScaleY())
            sock.append(this.template.getScaleY()).append(";");
        else
            sock.append(this.template.getScaleX()).append("x").append(this.template.getScaleY()).append(";");
        sock.append(this.template.getSex()).append(";");
        sock.append((this.template.getColor1() != -1 ? Integer.toHexString(this.template.getColor1()) : "-1")).append(";");
        sock.append((this.template.getColor2() != -1 ? Integer.toHexString(this.template.getColor2()) : "-1")).append(";");
        sock.append((this.template.getColor3() != -1 ? Integer.toHexString(this.template.getColor3()) : "-1")).append(";");
        sock.append(this.template.getAccessories()).append(";");
        Quest q = this.template.getQuest();
        if (q == null)
            sock.append(-1).append(";");
        else if (p.getQuestPersoByQuest(q) == null)
            sock.append((this.template.getExtraClip() != -1 ? (this.template.getExtraClip()) : (""))).append(";");
        else
            sock.append(-1).append(";");
        sock.append(this.template.getCustomArtWork());
        return sock.toString();
    }
}