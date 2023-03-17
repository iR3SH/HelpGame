package org.starloco.locos.exchange;

import org.starloco.locos.kernel.Main;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ExchangeHandler extends IoHandlerAdapter {

    @Override
    public void sessionCreated(IoSession arg0) throws Exception {
        Main.exchangeClient.setIoSession(arg0);
    }

    @Override
    public void messageReceived(IoSession arg0, Object arg1) throws Exception {
        String packet = ioBufferToString(arg1);
        ExchangeClient.logger.info(packet);
        ExchangePacketHandler.parser(packet);
    }

    @Override
    public void messageSent(IoSession arg0, Object arg1) throws Exception {
        ExchangeClient.logger.info(ioBufferToString(arg1));
    }

    @Override
    public void sessionClosed(IoSession arg0) throws Exception {
        Main.exchangeClient.restart();
    }

    @Override
    public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {
        arg1.printStackTrace();
    }

    public static String ioBufferToString(Object o) {
        IoBuffer ioBuffer = IoBuffer.allocate(((IoBuffer) o).capacity());
        ioBuffer.put((IoBuffer) o);
        ioBuffer.flip();

        try {
            return ioBuffer.getString(StandardCharsets.UTF_8.newDecoder());
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }
        return "undefined";
    }
}
