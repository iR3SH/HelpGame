package org.starloco.locos.area.map.entity;

import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.ConditionParser;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.Database;
import org.starloco.locos.game.action.ExchangeAction;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Trunk {

    private int id;
    private int houseId;
    private short mapId;
    private int cellId;
    private String key;
    private int ownerId;
    private long kamas;
    private Player player = null;
    private Map<Integer, GameObject> object = new HashMap<>();

    public Trunk(int id, int houseId, short mapId, int cellId) {
        this.id = id;
        this.houseId = houseId;
        this.mapId = mapId;
        this.cellId = cellId;
    }

    public static void closeCode(Player P) {
        SocketManager.GAME_SEND_KODE(P, "V");
    }

    public static Trunk getTrunkIdByCoord(int map_id, int cell_id) {
        for (Entry<Integer, Trunk> trunk : World.world.getTrunks().entrySet())
            if (trunk.getValue().getMapId() == map_id && trunk.getValue().getCellId() == cell_id)
                return trunk.getValue();
        return null;
    }

    public static void lock(Player P, String packet) {
        Trunk t = (Trunk) P.getExchangeAction().getValue();
        if (t == null)
            return;
        if (t.isTrunk(P, t)) {
            Database.getDynamics().getTrunkData().updateCode(P, t, packet); //Change le code
            t.setKey(packet);
            closeCode(P);
        } else {
            closeCode(P);
        }
        P.setExchangeAction(null);
    }

    public static void open(Player P, String packet, boolean isTrunk) {//Ouvrir un coffre
        Trunk t = (Trunk) P.getExchangeAction().getValue();
        if (t == null)
            return;
        if (packet.compareTo(t.getKey()) == 0 || isTrunk)//Si c'est chez lui ou que le mot de passe est bon
        {
            t.player = P;
            SocketManager.GAME_SEND_ECK_PACKET(P.getGameClient(), 5, "");
            SocketManager.GAME_SEND_EL_TRUNK_PACKET(P, t);
            closeCode(P);
            P.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_TRUNK, t));
        } else if (packet.compareTo(t.getKey()) != 0)//Mauvais code
        {
            SocketManager.GAME_SEND_KODE(P, "KE");
            closeCode(P);
            P.setExchangeAction(null);
        }
    }

    public static ArrayList<Trunk> getTrunksByHouse(House h) {
        ArrayList<Trunk> trunks = World.world.getTrunks().entrySet().stream().filter(trunk -> trunk.getValue().getHouseId() == h.getId()).map(Entry::getValue).collect(Collectors.toCollection(ArrayList::new));
        return trunks;
    }

    public void setObjects(String object) {
        for (String item : object.split("\\|")) {
            if (item.equals(""))
                continue;
            String[] infos = item.split(":");
            int guid = Integer.parseInt(infos[0]);

            GameObject obj = World.world.getGameObject(guid);
            if (obj == null)
                continue;
            this.object.put(obj.getGuid(), obj);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public short getMapId() {
        return mapId;
    }

    public void setMapId(short mapId) {
        this.mapId = mapId;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public long getKamas() {
        return kamas;
    }

    public void setKamas(long kamas) {
        this.kamas = kamas;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Map<Integer, GameObject> getObject() {
        return object;
    }

    public void setObject(Map<Integer, GameObject> object) {
        this.object = object;
    }

    public void Lock(Player P) {
        SocketManager.GAME_SEND_KODE(P, "CK1|8");
    }

    public void enter(Player player) {
        if (player.getFight() != null || player.getExchangeAction() != null)
            return;

        House house = World.world.getHouse(getHouseId());

        if(house.getOwnerId() == player.getAccID() && this.getOwnerId() != player.getAccID())
            this.setOwnerId(player.getAccID());
        if (this.getOwnerId() == player.getAccID() || (player.get_guild() != null && player.get_guild().getId() == house.getGuildId() && house.canDo(Constant.C_GNOCODE))) {
            player.setExchangeAction(new ExchangeAction<>(ExchangeAction.IN_TRUNK, this));
            open(player, "-", true);
        } else if (player.get_guild() == null && house.canDo(Constant.C_OCANTOPEN))
            SocketManager.GAME_SEND_MESSAGE(player, "Ce coffre ne peut être ouvert que par les membres de la guilde !");
        else if (this.getOwnerId() > 0)
            SocketManager.GAME_SEND_KODE(player, "CK0|8");
    }

    public boolean isTrunk(Player P, Trunk t)//Savoir si c'est son coffre
    {
        return t.getOwnerId() == P.getAccID();
    }

    public String parseToTrunkPacket() {
        StringBuilder packet = new StringBuilder();

        for (GameObject obj : this.object.values())
            packet.append("O").append(obj.parseItem()).append(";");
        if (getKamas() != 0)
            packet.append("G").append(getKamas());
        return packet.toString();
    }

    public void addInTrunk(int guid, int qua, Player P) {
        if (qua <= 0)
            return;
        if (((Trunk) P.getExchangeAction().getValue()).getId() != getId())
            return;

        if (this.object.size() >= 10000) // Le plus grand c'est pour si un admin ajoute des objets via la bdd...
        {
            SocketManager.GAME_SEND_MESSAGE(P, "Le nombre d'objets maximal de ce coffre a été atteint !");
            return;
        }

        GameObject PersoObj = World.world.getGameObject(guid);
        if (PersoObj == null)
            return;
        if(PersoObj.isAttach()) return;
        //Si le joueur n'a pas l'item dans son sac ...
        if (P.getItems().get(guid) == null)
            return;
        String str = "";

        //Si c'est un item équipé ...
        if (PersoObj.getPosition() != Constant.ITEM_POS_NO_EQUIPED)
            return;

        GameObject TrunkObj = getSimilarTrunkItem(PersoObj);
        int newQua = PersoObj.getQuantity() - qua;
        if (TrunkObj == null)//S'il n'y pas d'item du meme Template
        {
            //S'il ne reste pas d'item dans le sac
            if (newQua <= 0) {
                //On enleve l'objet du sac du joueur
                P.removeItem(PersoObj.getGuid());
                //On met l'objet du sac dans le coffre, avec la meme quantité
                this.object.put(PersoObj.getGuid(), PersoObj);
                str = "O+" + PersoObj.getGuid() + "|" + PersoObj.getQuantity()
                        + "|" + PersoObj.getTemplate().getId() + "|"
                        + PersoObj.parseStatsString();
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(P, guid);
            } else
            //S'il reste des objets au joueur
            {
                //on modifie la quantité d'item du sac
                PersoObj.setQuantity(newQua);
                //On ajoute l'objet au coffre et au monde
                TrunkObj = GameObject.getCloneObjet(PersoObj, qua);
                World.world.addGameObject(TrunkObj, true);
                this.object.put(TrunkObj.getGuid(), TrunkObj);
                //Envoie des packets
                str = "O+" + TrunkObj.getGuid() + "|" + TrunkObj.getQuantity()
                        + "|" + TrunkObj.getTemplate().getId() + "|"
                        + TrunkObj.parseStatsString();
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, PersoObj);
            }
        } else
        // S'il y avait un item du meme template
        {
            //S'il ne reste pas d'item dans le sac
            if (newQua <= 0) {
                //On enleve l'objet du sac du joueur
                P.removeItem(PersoObj.getGuid());
                //On enleve l'objet du monde
                World.world.removeGameObject(PersoObj.getGuid());
                //On ajoute la quantité a l'objet dans le coffre
                TrunkObj.setQuantity(TrunkObj.getQuantity()
                        + PersoObj.getQuantity());
                //on envoie l'ajout au coffre de l'objet
                str = "O+" + TrunkObj.getGuid() + "|" + TrunkObj.getQuantity()
                        + "|" + TrunkObj.getTemplate().getId() + "|"
                        + TrunkObj.parseStatsString();
                //on envoie la supression de l'objet du sac au joueur
                SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(P, guid);
            } else
            //S'il restait des objets
            {
                //on modifie la quantité d'item du sac
                PersoObj.setQuantity(newQua);
                TrunkObj.setQuantity(TrunkObj.getQuantity() + qua);
                str = "O+" + TrunkObj.getGuid() + "|" + TrunkObj.getQuantity()
                        + "|" + TrunkObj.getTemplate().getId() + "|"
                        + TrunkObj.parseStatsString();
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, PersoObj);
            }
        }

        for (Player perso : P.getCurMap().getPlayers())
            if (perso.getExchangeAction() != null && perso.getExchangeAction().getType() == ExchangeAction.IN_TRUNK && getId() == ((Trunk) perso.getExchangeAction().getValue()).getId())
                SocketManager.GAME_SEND_EsK_PACKET(perso, str);

        SocketManager.GAME_SEND_Ow_PACKET(P);
        Database.getDynamics().getTrunkData().update(this);
        Database.getStatics().getPlayerData().update(P);
    }

    public void removeFromTrunk(int guid, int qua, Player P) {
        if (qua <= 0)
            return;
        if (((Trunk) P.getExchangeAction().getValue()).getId() != getId())
            return;
        GameObject TrunkObj = World.world.getGameObject(guid);
        if (TrunkObj == null)
            return;
        //Si le joueur n'a pas l'item dans son coffre

        if (this.object.get(guid) == null)
            return;

        GameObject PersoObj = P.getSimilarItem(TrunkObj);
        String str = "";
        int newQua = TrunkObj.getQuantity() - qua;

        if (PersoObj == null)//Si le joueur n'avait aucun item similaire
        {
            //S'il ne reste rien dans le coffre
            if (newQua <= 0) {
                //On retire l'item du coffre

                this.object.remove(guid);
                //On l'ajoute au joueur
                P.getItems().put(guid, TrunkObj);

                //On envoie les packets
                SocketManager.GAME_SEND_OAKO_PACKET(P, TrunkObj);
                str = "O-" + guid;
            } else
            //S'il reste des objets dans le coffre
            {
                //On crée une copy de l'item dans le coffre
                PersoObj = GameObject.getCloneObjet(TrunkObj, qua);
                //On l'ajoute au monde
                World.world.addGameObject(PersoObj, true);
                //On retire X objet du coffre
                TrunkObj.setQuantity(newQua);
                //On l'ajoute au joueur
                P.getItems().put(PersoObj.getGuid(), PersoObj);

                //On envoie les packets
                SocketManager.GAME_SEND_OAKO_PACKET(P, PersoObj);
                str = "O+" + TrunkObj.getGuid() + "|" + TrunkObj.getQuantity()
                        + "|" + TrunkObj.getTemplate().getId() + "|"
                        + TrunkObj.parseStatsString();
            }
        } else {
            //S'il ne reste rien dans le coffre
            if (newQua <= 0) {
                //On retire l'item du coffre

                this.object.remove(TrunkObj.getGuid());

                World.world.removeGameObject(TrunkObj.getGuid());
                //On Modifie la quantité de l'item du sac du joueur
                PersoObj.setQuantity(PersoObj.getQuantity()
                        + TrunkObj.getQuantity());
                //On envoie les packets
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, PersoObj);
                str = "O-" + guid;
            } else
            //S'il reste des objets dans le coffre
            {
                //On retire X objet du coffre
                TrunkObj.setQuantity(newQua);
                //On ajoute X objets au joueurs
                PersoObj.setQuantity(PersoObj.getQuantity() + qua);
                //On envoie les packets
                SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, PersoObj);
                str = "O+" + TrunkObj.getGuid() + "|" + TrunkObj.getQuantity()
                        + "|" + TrunkObj.getTemplate().getId() + "|"
                        + TrunkObj.parseStatsString();
            }
        }

        for (Player perso : P.getCurMap().getPlayers())
            if (perso.getExchangeAction() != null && perso.getExchangeAction().getType() == ExchangeAction.IN_TRUNK && getId() == ((Trunk) perso.getExchangeAction().getValue()).getId())
                SocketManager.GAME_SEND_EsK_PACKET(perso, str);

        SocketManager.GAME_SEND_Ow_PACKET(P);
        Database.getDynamics().getTrunkData().update(this);
        Database.getStatics().getPlayerData().update(P);
    }

    private GameObject getSimilarTrunkItem(GameObject obj) {
        for (GameObject object : this.object.values())
            if(ConditionParser.stackIfSimilar(object, obj))
                return object;
        return null;
    }

    public String parseTrunkObjetsToDB() {
        StringBuilder str = new StringBuilder();
        for (Entry<Integer, GameObject> entry : this.object.entrySet()) {
            GameObject obj = entry.getValue();
            str.append(obj.getGuid()).append("|");
        }
        return str.toString();
    }

    public void moveTrunkToBank(Account Cbank) {
        for (Entry<Integer, GameObject> obj : this.object.entrySet())
            Cbank.getBank().add(obj.getValue());
        this.object.clear();
        Database.getDynamics().getTrunkData().update(this);
        Database.getDynamics().getBankData().update(Cbank);
    }
}