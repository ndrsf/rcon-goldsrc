package de.apwolf.rcon;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpServer.class);

    private final byte[] buf = new byte[256];

    private final DatagramSocket socket;

    public UdpServer(DatagramSocket socket) {
        this.socket = socket;
    }

    public byte[] receiveMessage() {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        // socket.setSoTimeout(5000);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            LOGGER.error("Error when receiving packet", e);
            throw new RuntimeException(e);
        }

        byte[] receivedData = ArrayUtils.subarray(packet.getData(), 0, packet.getLength());
        String received = new String(receivedData);
        LOGGER.info("Server received \"" + received + "\"");
        return receivedData;
    }

}