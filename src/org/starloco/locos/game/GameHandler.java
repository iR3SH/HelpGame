package org.starloco.locos.game;

import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Main;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class GameHandler implements IoHandler {

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {
        World.world.logger.info("Session " + ioSession.getId() + " created");
        
        ioSession.setAttribute("client", new GameClient(ioSession));
        Main.refreshTitle();
    }

    @Override
    public void messageReceived(IoSession ioSession, Object response) throws Exception {
        GameClient client = (GameClient) ioSession.getAttribute("client");
        String packet = (String) response;
        String[] s = packet.split("\n");

        int i = 0;
        do {

            if(s[i].contains("ù")){
                s[i] = s[i].split("ù")[2];
            }

            client.parsePacket(s[i]);
            if (Main.modDebug)
                World.world.logger.trace((client.getPlayer() == null ? "" : client.getPlayer().getName()) + " <-- " + s[i]);
            i++;
        } while (i == s.length - 1);
    }


    @Override
    public void sessionClosed(IoSession ioSession) throws Exception {
        GameClient client = (GameClient) ioSession.getAttribute("client");
        if(client != null)
            client.disconnect();
        World.world.logger.info("Session " + ioSession.getId() + " closed");
    }

    @Override
    public void exceptionCaught(IoSession ioSession, Throwable arg1) throws Exception {
        arg1.printStackTrace();
        if (Main.modDebug)
            World.world.logger.error("Exception connexion client : " + arg1.getMessage());
        this.kick(ioSession);
    }

    @Override
    public void messageSent(IoSession ioSession, Object arg1) throws Exception {
        GameClient client = (GameClient) ioSession.getAttribute("client");

        if (client != null) {
            if (Main.modDebug) {
                String packet = (String) arg1;
                if (packet.startsWith("am")) return;
                World.world.logger.trace((client.getPlayer() == null ? "" : client.getPlayer().getName()) + " --> " + packet);
            }
        }
    }

    @Override
    public void inputClosed(IoSession ioSession) throws Exception {
        ioSession.close(true);
    }

    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus arg1) throws Exception {
        World.world.logger.info("Session " + ioSession.getId() + " idle");
}

    @Override
    public void sessionOpened(IoSession ioSession) throws Exception {
        World.world.logger.info("Session " + ioSession.getId() + " opened");
    }

    private void kick(IoSession ioSession) {
        GameClient client = (GameClient) ioSession.getAttribute("client");
        if (client != null) {
            client.kick();
            ioSession.setAttribute("client", null);
        }
        Main.refreshTitle();
    }
}
