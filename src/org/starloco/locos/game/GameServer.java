package org.starloco.locos.game;

import org.apache.mina.core.session.IdleStatus;
import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class GameServer {

    public static short MAX_PLAYERS = 700;

    private final ArrayList<Account> waitingClients = new ArrayList<>();
    private IoAcceptor acceptor;

    public GameServer() {
        Main.gameServer = this;
        this.acceptor = new NioSocketAcceptor();
        this.acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF8"), LineDelimiter.NUL, new LineDelimiter("\n\0"))));
        this.acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 60 * 10 /*10 Minutes*/);
        this.acceptor.setHandler(new GameHandler());
    }
    public void initialize() {
        if (this.acceptor.isActive())
            return;

        try {
            this.acceptor.bind(new InetSocketAddress(Main.gamePort));
        } catch (IOException e) {
            Main.logger.error("The address '" + Main.gamePort + "' is already in use..");
            this.close();
            try { Thread.sleep(3000); } catch(Exception ignored) {}
            this.initialize();
        } finally {
            Main.logger.info("The game server started on address : " + Main.Ip + ":" + Main.gamePort);
        }
    }

    public void close() {
        if (!this.acceptor.isActive())
            return;

        this.acceptor.getManagedSessions().values().stream().filter(session -> session.isConnected() || !session.isClosing()).forEach(session -> session.close(true));
        this.acceptor.dispose();
        this.acceptor.unbind();
        Main.logger.error("The game server was stopped.");
    }

    public ArrayList<GameClient> getClients() {
        return acceptor.getManagedSessions().values().stream().filter(session -> session.getAttribute("client") != null).map(session -> (GameClient) session.getAttribute("client")).collect(Collectors.toCollection(ArrayList::new));
    }

    public int getPlayersNumberByIp() {
        ArrayList<String> IPS = new ArrayList<>();
        this.getClients().stream().filter(client -> client != null && client.getAccount() != null).forEach(client -> {
            String IP = client.getAccount().getCurrentIp();
            if (!IP.equalsIgnoreCase("") && !IPS.contains(IP)) IPS.add(IP);
        });
        return IPS.size();
    }

    public static String getServerTime() {
        return "BT" + (new Date().getTime() + 3600000 * 2);
    }

    public static void setState(int state) {
        if (Main.exchangeClient != null && Main.exchangeClient.getConnectFuture() != null && !Main.exchangeClient.getConnectFuture().isCanceled() && Main.exchangeClient.getConnectFuture().isConnected())
            Main.exchangeClient.send("SS" + state);
    }

    public Account getWaitingAccount(int id) {
        for (Account account : this.waitingClients)
            if (account.getId() == id)
                return account;
        return null;
    }

    public void deleteWaitingAccount(Account account) {
        if(this.waitingClients.contains(account)) this.waitingClients.remove(account);
    }

    public void addWaitingAccount(Account account) {
        if(!this.waitingClients.contains(account)) this.waitingClients.add(account);
    }

    public static void a() {}

    public void kickAll(boolean kickGm) {
        for (Player player : World.world.getOnlinePlayers()) {
            if (player != null && player.getGameClient() != null) {
                if (player.getGroupe() != null && !player.getGroupe().isPlayer() && kickGm)
                    continue;
                player.send("M04");
                player.getGameClient().kick();
            }
        }
    }
}
