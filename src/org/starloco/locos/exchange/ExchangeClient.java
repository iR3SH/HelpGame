package org.starloco.locos.exchange;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.slf4j.LoggerFactory;
import org.starloco.locos.game.GameServer;
import org.starloco.locos.kernel.Main;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class ExchangeClient {

    public static Logger logger = (Logger) LoggerFactory.getLogger(ExchangeClient.class);

    private IoSession ioSession;
    private ConnectFuture connectFuture;
    private IoConnector ioConnector = new NioSocketConnector();

    public ExchangeClient() {
        this.ioConnector.setHandler(new ExchangeHandler());
        Main.exchangeClient = this;
        ExchangeClient.logger.setLevel(Level.ALL);
    }

    public void setIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    public IoSession getIoSession() {
        return ioSession;
    }

    public ConnectFuture getConnectFuture() {
        return connectFuture;
    }

    public void initialize() {
        try {
            this.connectFuture = this.ioConnector.connect(new InetSocketAddress(Main.exchangeIp, Main.exchangePort));
        } catch (Exception e) {
            ExchangeClient.logger.error("The game server don't found the login server. Exception : " + e.getMessage());
            try { Thread.sleep(2000); } catch(Exception ignored) {}
            return;
        }

        try { Thread.sleep(3000); } catch(Exception ignored) {}

        if (!ioConnector.isActive()) {
            if (!Main.isRunning) return;

            ExchangeClient.logger.error("Try to connect to the login server..");
            restart();
            return;
        }
        ExchangeClient.logger.info("The exchange client was connected on address : " + Main.exchangeIp + ":" + Main.exchangePort);
    }

    public void restart() {
        if (!Main.isRunning) return;

        ExchangeClient.logger.error("The login server was not found..");

        this.stop();
        this.connectFuture = null;
        this.ioConnector = new NioSocketConnector();
        this.ioConnector.setHandler(new ExchangeHandler());
        this.initialize();
    }

    public void stop() {
        if(this.ioSession != null)
            this.ioSession.close(true);
        if (this.connectFuture != null)
            this.connectFuture.cancel();

        this.connectFuture = null;
        this.ioConnector.dispose();
        ExchangeClient.logger.info("The exchange client was stopped.");
    }

    public void send(String packet) {
        if(this.ioSession != null && !this.ioSession.isClosing() && this.ioSession.isConnected())
            this.getIoSession().write(StringToIoBuffer(packet));
    }
    public static IoBuffer StringToIoBuffer(String packet) {
        IoBuffer ioBuffer = IoBuffer.allocate(30000);
        ioBuffer.put(packet.getBytes());
        return ioBuffer.flip();
    }
}
