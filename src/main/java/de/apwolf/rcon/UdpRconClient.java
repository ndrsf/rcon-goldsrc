package de.apwolf.rcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UdpRconClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(UdpRconClient.class);

    private final DatagramSocket socket;

    private final String destinationAddress;

    private final int destinationPort;

    public UdpRconClient(DatagramSocket socket, String serverIpAndPort) throws UnknownHostException {
        this.socket = socket;
        if (serverIpAndPort == null) {
            LOGGER.info("No ip/port given, using localhost");
            this.destinationAddress = InetAddress.getLocalHost().getHostAddress();
            this.destinationPort = 27015;
        } else {
            this.destinationAddress = serverIpAndPort.split(":")[0];
            this.destinationPort = Integer.parseInt(serverIpAndPort.split(":")[1]);
        }
    }

    void requestChallenge() throws IOException {
        byte[] challengeCommandBytes = RconUtils.buildChallengeCommand();

        DatagramPacket packet = new DatagramPacket(
                challengeCommandBytes,
                challengeCommandBytes.length,
                InetAddress.getByName(destinationAddress),
                destinationPort);

        socket.send(packet);
        LOGGER.info("Sent challenge request to " + destinationAddress + ":" + destinationPort);
    }

    void sendConnect(String challenge, String rconPassword) throws IOException {
        byte[] command = RconUtils.buildConnectCommand(challenge, rconPassword);

        DatagramPacket packet = new DatagramPacket(
                command,
                command.length,
                InetAddress.getByName(destinationAddress),
                destinationPort);

        socket.send(packet);
        LOGGER.info("Sent connect command to " + destinationAddress + ":" + destinationPort);
    }

}
