package org.starloco.locos.area.map.entity;

import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.Database;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.other.Guild;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class House {
    private int id;
    private short mapId;
    private int cellId;
    private int ownerId;
    private int sale;
    private int guildId;
    private int guildRights;
    private int access;
    private String key;
    private int houseMapId;
    private int houseCellId;
    //Droits de chaques maisons
    private Map<Integer, Boolean> haveRight = new TreeMap<>();

    public House(int id, short mapId, int cellId, int houseMapId, int houseCellId) {
        this.id = id;
        this.mapId = mapId;
        this.cellId = cellId;
        this.houseMapId = houseMapId;
        this.houseCellId = houseCellId;
    }

    public static House getHouseIdByCoord(int map_id, int cell_id) {
        for (Entry<Integer, House> house : World.world.getHouses().entrySet())
            if (house.getValue().getMapId() == map_id
                    && house.getValue().getCellId() == cell_id)
                return house.getValue();
        return null;
    }

    public static void load(Player player, int newMapID) {
        World.world.getHouses().entrySet().stream().filter(house -> house.getValue().getMapId() == newMapID).forEach(house -> {
            StringBuilder packet = new StringBuilder();
            packet.append("P").append(house.getValue().getId()).append("|");
            if (house.getValue().getOwnerId() > 0) {
                Account C = World.world.getAccount(house.getValue().getOwnerId());
                if (C == null)//Ne devrait pas arriver
                    packet.append("undefined;");
                else
                    packet.append(World.world.getAccount(house.getValue().getOwnerId()).getPseudo()).append(";");
            } else {
                packet.append(";");
            }

            if (house.getValue().getSale() > 0)//Si prix > 0
                packet.append("1");//Achetable
            else
                packet.append("0");//Non achetable

            if (house.getValue().getGuildId() > 0) //Maison de guilde
            {
                Guild G = World.world.getGuild(house.getValue().getGuildId());
                if (G != null) {
                    String Gname = G.getName();
                    String Gemblem = G.getEmblem();
                    if (G.getMembers().size() < 10 && G.getId() > 2)//Ce n'est plus une maison de guilde
                    {
                        Database.getDynamics().getHouseData().updateGuild(house.getValue(), 0, 0);
                    } else {
                        //Affiche le blason pour les membre de guilde OU Affiche le blason pour les non membre de guilde
                        if (player.get_guild() != null
                                && player.get_guild().getId() == house.getValue().getGuildId()
                                && house.getValue().canDo(Constant.H_GBLASON))//meme guilde
                        {
                            packet.append(";").append(Gname).append(";").append(Gemblem);
                        } else if (house.getValue().canDo(Constant.H_OBLASON))//Pas de guilde/guilde-diffï¿½rente
                        {
                            packet.append(";").append(Gname).append(";").append(Gemblem);
                        }
                    }
                }
            }
            SocketManager.GAME_SEND_hOUSE(player, packet.toString());

            if (house.getValue().getOwnerId() == player.getAccID()) {
                StringBuilder packet1 = new StringBuilder();
                packet1.append("L+|").append(house.getValue().getId()).append(";").append(house.getValue().getAccess()).append(";");

                if (house.getValue().getSale() <= 0) {
                    packet1.append("0;").append(house.getValue().getSale());
                } else if (house.getValue().getSale() > 0) {
                    packet1.append("1;").append(house.getValue().getSale());
                }
                SocketManager.GAME_SEND_hOUSE(player, packet1.toString());
            }
        });
    }

    public void open(Player P, String packet, boolean isHome)//Ouvrir une maison ;o
    {
        if ((!this.canDo(Constant.H_OCANTOPEN) && (packet.compareTo(this.getKey()) == 0))
                || isHome)//Si c'est chez lui ou que le mot de passe est bon
        {
            P.teleport((short) this.getHouseMapId(), this.getHouseCellId());
            closeCode(P);
        } else if ((packet.compareTo(this.getKey()) != 0)
                || this.canDo(Constant.H_OCANTOPEN))//Mauvais code
        {
            SocketManager.GAME_SEND_KODE(P, "KE");
            SocketManager.GAME_SEND_KODE(P, "V");
        }
    }

    public static void buy(Player player)//Acheter une maison
    {
        House house = player.getInHouse();

        if (House.alreadyHaveHouse(player)) {
            SocketManager.GAME_SEND_Im_PACKET(player, "132;1");
            return;
        }

        if (player.getKamas() < house.getSale())
            return;

        player.setKamas(player.getKamas() - house.getSale());

        int kamas = 0;
        for (Trunk trunk : Trunk.getTrunksByHouse(house)) {
            if (house.getOwnerId() > 0)
                trunk.moveTrunkToBank(World.world.getAccount(house.getOwnerId()));//Dï¿½placement des items vers la banque

            kamas += trunk.getKamas();
            trunk.setKamas(0);//Retrait kamas
            trunk.setKey("-");//ResetPass
            trunk.setOwnerId(player.getAccID());//ResetOwner
            Database.getDynamics().getTrunkData().update(trunk);
        }

        //Ajoute des kamas dans la banque du vendeur
        if (house.getOwnerId() > 0) {
            Account seller = World.world.getAccount(house.getOwnerId());
            seller.setBankKamas(seller.getBankKamas() + house.getSale() + kamas);

            if (seller.getCurrentPlayer() != null)//FIXME: change the packet (Im)
                SocketManager.GAME_SEND_MESSAGE(seller.getCurrentPlayer(), "Une maison vous appartenant a été vendue " + house.getSale() + " kamas.");
            Database.getStatics().getAccountData().update(seller);
        }

        closeBuy(player);
        SocketManager.GAME_SEND_STATS_PACKET(player);
        Database.getDynamics().getHouseData().buy(player, house);

        for (Player viewer : player.getCurMap().getPlayers())
            House.load(viewer, viewer.getCurMap().getId());

        Database.getStatics().getPlayerData().update(player);
    }

    public static void sell(Player P, String packet)//Vendre une maison
    {
        House h = P.getInHouse();
        int price = Integer.parseInt(packet);
        if (h.isHouse(P, h)) {
            SocketManager.GAME_SEND_hOUSE(P, "V");
            SocketManager.GAME_SEND_hOUSE(P, "SK" + h.getId() + "|" + price);
            //Vente de la maison
            Database.getDynamics().getHouseData().sell(h, price);
            //Rafraichir la map aprï¿½s la mise en vente
            for (Player z : P.getCurMap().getPlayers())
                load(z, z.getCurMap().getId());
        }
    }

    public static void closeCode(Player P) {
        SocketManager.GAME_SEND_KODE(P, "V");
        P.setInHouse(null);
    }

    public static void closeBuy(Player P) {
        SocketManager.GAME_SEND_hOUSE(P, "V");
    }

    public static void lockIt(Player P, String packet) {
        House h = P.getInHouse();
        if (h.isHouse(P, h)) {
            Database.getDynamics().getHouseData().updateCode(P, h, packet);//Change le code
            closeCode(P);
        } else {
            closeCode(P);
        }
    }

    public static String parseHouseToGuild(Player P) {
        boolean isFirst = true;
        String packet = "+";
        for (Entry<Integer, House> house : World.world.getHouses().entrySet()) {
            if (house.getValue().getGuildId() == P.get_guild().getId()
                    && house.getValue().getGuildRights() > 0) {
                String name = "";
                int id = house.getValue().getOwnerId();
                if (id != -1) {
                    Account a = World.world.getAccount(id);
                    if (a != null) {
                        name = a.getPseudo();
                    }
                }
                if (isFirst) {
                    packet += house.getKey() + ";";
                    if (World.world.getPlayer(house.getValue().getOwnerId()) == null)
                        packet += name + ";";
                    else
                        packet += World.world.getPlayer(house.getValue().getOwnerId()).getAccount().getPseudo()
                                + ";";
                    packet += World.world.getMap((short) house.getValue().getHouseMapId()).getX()
                            + ","
                            + World.world.getMap((short) house.getValue().getHouseMapId()).getY()
                            + ";";
                    packet += "0;";
                    packet += house.getValue().getGuildRights();
                    isFirst = false;
                } else {
                    packet += "|";
                    packet += house.getKey() + ";";
                    if (World.world.getPlayer(house.getValue().getOwnerId()) == null)
                        packet += name + ";";
                    else
                        packet += World.world.getPlayer(house.getValue().getOwnerId()).getAccount().getPseudo()
                                + ";";
                    packet += World.world.getMap((short) house.getValue().getHouseMapId()).getX()
                            + ","
                            + World.world.getMap((short) house.getValue().getHouseMapId()).getY()
                            + ";";
                    packet += "0;";
                    packet += house.getValue().getGuildRights();
                }
            }
        }
        return packet;
    }

    public static boolean alreadyHaveHouse(Player P) {
        for (Entry<Integer, House> house : World.world.getHouses().entrySet())
            if (house.getValue().getOwnerId() == P.getAccID())
                return true;
        return false;
    }

    public static void parseHG(Player P, String packet) {
        House h = P.getInHouse();
        if (P.get_guild() == null)
            return;
        if (packet != null) {
            if (packet.charAt(0) == '+') {
                //Ajoute en guilde
                byte HouseMaxOnGuild = (byte) Math.floor(P.get_guild().getLvl() / 10);
                if (houseOnGuild(P.get_guild().getId()) >= HouseMaxOnGuild && P.get_guild().getId() > 2) {
                    P.send("Im1151");
                    return;
                }
                if (P.get_guild().getMembers().size() < 10 && P.get_guild().getId() > 2) {
                    return;
                }
                Database.getDynamics().getHouseData().updateGuild(h, P.get_guild().getId(), 0);
                parseHG(P, null);
            } else if (packet.charAt(0) == '-') {
                //Retire de la guilde
                Database.getDynamics().getHouseData().updateGuild(h, 0, 0);
                parseHG(P, null);
            } else {
                Database.getDynamics().getHouseData().updateGuild(h, h.getGuildId(), Integer.parseInt(packet));
                h.parseIntToRight(Integer.parseInt(packet));
            }
        } else if (packet == null) {
            if (h.getGuildId() <= 0) {
                SocketManager.GAME_SEND_hOUSE(P, "G" + h.getId());
            } else if (h.getGuildId() > 0) {
                SocketManager.GAME_SEND_hOUSE(P, "G" + h.getId() + ";"
                        + P.get_guild().getName() + ";"
                        + P.get_guild().getEmblem() + ";" + h.getGuildRights());
            }
        }
    }

    public static byte houseOnGuild(int GuildID) {
        byte i = 0;
        for (Entry<Integer, House> house : World.world.getHouses().entrySet())
            if (house.getValue().getGuildId() == GuildID)
                i++;
        return i;
    }

    public static void leave(Player P, String packet) {
        House h = P.getInHouse();
        if (!h.isHouse(P, h))
            return;
        int Pguid = Integer.parseInt(packet);
        Player Target = World.world.getPlayer(Pguid);
        if (Target == null || !Target.isOnline() || Target.getFight() != null
                || Target.getCurMap().getId() != P.getCurMap().getId())
            return;
        Target.teleport(h.getMapId(), h.getCellId());
        SocketManager.GAME_SEND_Im_PACKET(Target, "018;" + P.getName());
    }

    public static House getHouseByPerso(Player P)//Connaitre la MAPID + CELLID de sa maison
    {
        for (Entry<Integer, House> house : World.world.getHouses().entrySet())
            if (house.getValue().getOwnerId() == P.getAccID())
                return house.getValue();
        return null;
    }

    public static void removeHouseGuild(int GuildID) {
        for (Entry<Integer, House> h : World.world.getHouses().entrySet()) {
            if (h.getValue().getGuildId() == GuildID) {
                h.getValue().setGuildRights(0);
                h.getValue().setGuildId(0);
            } else {
            }
        }
        Database.getDynamics().getHouseData().removeGuild(GuildID); //Supprime les maisons de guilde
    }

    public void setGuildRightsWithParse(int guildRights) {
        this.guildRights = guildRights;
        parseIntToRight(guildRights);
    }

    public int getId() {
        return this.id;
    }

    public short getMapId() {
        return this.mapId;
    }

    public int getCellId() {
        return this.cellId;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(int id) {
        this.ownerId = id;
    }

    public int getSale() {
        return this.sale;
    }

    public void setSale(int price) {
        this.sale = price;
    }

    public int getGuildId() {
        return this.guildId;
    }

    public void setGuildId(int guildId) {
        this.guildId = guildId;
    }

    public int getGuildRights() {
        return this.guildRights;
    }

    public void setGuildRights(int guildRights) {
        this.guildRights = guildRights;
    }

    public int getAccess() {
        return this.access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getHouseMapId() {
        return this.houseMapId;
    }

    public int getHouseCellId() {
        return this.houseCellId;
    }

    public void enter(Player P) {//Entrer dans la maison
        if (P.getFight() != null || P.getExchangeAction() != null)
            return;
        if (this.getOwnerId() == P.getAccID() || (P.get_guild() != null && P.get_guild().getId() == this.getGuildId() && canDo(Constant.H_GNOCODE)))//C'est sa maison ou mï¿½me guilde + droits entrer sans pass
            open(P, "-", true);
        else if (this.getOwnerId() > 0) //Une personne autre la acheter, il faut le code pour rentrer
            SocketManager.GAME_SEND_KODE(P, "CK0|8");//8 ï¿½tant le nombre de chiffre du code
        else if (this.getOwnerId() == 0)//Maison non acheter, mais achetable, on peut rentrer sans code
            open(P, "-", false);
    }

    public void buyIt(Player P)//Acheter une maison
    {
        House h = P.getInHouse();
        String str = "CK" + h.getId() + "|" + h.getSale();//ID + Prix
        SocketManager.GAME_SEND_hOUSE(P, str);
    }

    public void sellIt(Player P)//Vendre une maison
    {
        House h = P.getInHouse();
        if (isHouse(P, h)) {
            String str = "CK" + h.getId() + "|" + h.getSale();//ID + Prix
            SocketManager.GAME_SEND_hOUSE(P, str);
        }
    }

    public boolean isHouse(Player P, House h)//Savoir si c'est sa maison
    {
        return h.getOwnerId() == P.getAccID();
    }

    public void lock(Player P) {
        SocketManager.GAME_SEND_KODE(P, "CK1|8");
    }

    public boolean canDo(int rightValue) {
        return haveRight.get(rightValue);
    }

    public void initRight() {
        haveRight.put(Constant.H_GBLASON, false);
        haveRight.put(Constant.H_OBLASON, false);
        haveRight.put(Constant.H_GNOCODE, false);
        haveRight.put(Constant.H_OCANTOPEN, false);
        haveRight.put(Constant.C_GNOCODE, false);
        haveRight.put(Constant.C_OCANTOPEN, false);
        haveRight.put(Constant.H_GREPOS, false);
        haveRight.put(Constant.H_GTELE, false);
    }

    public void parseIntToRight(int total) {
        if (haveRight.isEmpty()) {
            initRight();
        }
        if (total == 1)
            return;

        if (haveRight.size() > 0) //Si les droits contiennent quelque chose -> Vidage (Mï¿½me si le HashMap supprimerais les entrï¿½es doublon lors de l'ajout)
            haveRight.clear();

        initRight(); //Remplissage des droits

        Integer[] mapKey = haveRight.keySet().toArray(new Integer[haveRight.size()]); //Rï¿½cupï¿½re les clef de map dans un tableau d'Integer

        while (total > 0) {
            for (int i = haveRight.size() - 1; i < haveRight.size(); i--) {
                if (mapKey[i].intValue() <= total) {
                    total ^= mapKey[i].intValue();
                    haveRight.put(mapKey[i], true);
                    break;
                }
            }
        }
    }
}