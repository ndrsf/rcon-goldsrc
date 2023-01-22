package de.apwolf.rcon;

import com.google.common.base.CharMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;

public class RconConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(RconConnector.class);

    // equal to sv_rcon_minfailures
    private static final int MAX_FAILS_PER_PERIOD = 4;

    // equal to sv_rcon_minfailuretime
    public static final int PERIOD_IN_SECONDS = 31;

    /**
     * Starts a UDP server and sends UDP requests to the Goldsrc server in order to connect to ir - or, if
     * no password to log in with is provided, try brute force
     *
     * @param rconPassword if given, will try to connect with this password. If null, will try brute force
     * @param serverIpAndPort ipv4:port
     * @throws IOException if the connection failed
     */
    public void connect(String rconPassword, String serverIpAndPort) throws IOException {
        boolean crackIt = false;
        if (rconPassword == null) {
            LOGGER.info("No password given, brute forcing...");
            crackIt = true;
        } else {
            LOGGER.info("Password given, only trying " + rconPassword);
        }
        DatagramSocket socket = new DatagramSocket(4445);
        UdpServer server = new UdpServer(socket);
        UdpRconClient client = new UdpRconClient(socket, serverIpAndPort);

        // request challenge
        client.requestChallenge();
        byte[] roughChallengeBytes = server.receiveMessage();
        String challenge = RconUtils.extractChallenge(roughChallengeBytes);
        if (challenge.contains("banned")) {
            throw new RuntimeException("No challenge received - banned");
        }
        if (Long.parseLong(challenge) > 0) {
            LOGGER.info("Found challenge " + challenge);
        } else {
            throw new RuntimeException("Invalid challenge: " + challenge);
        }

        if (crackIt) {
            rconPassword = " ";
        }

        boolean go = true;

        while (go) {
            for (int i = 0; i < MAX_FAILS_PER_PERIOD; i++) {
                client.sendConnect(challenge, rconPassword);
                byte[] connectResponseBytes = server.receiveMessage();
                String status = RconUtils.parseConnectCommandResponse(connectResponseBytes);
                LOGGER.info(status);
                if (status.equals(RconUtils.NO_PASSWORD_SET) || status.equals(RconUtils.UNKNOWN_ERROR) || status.equals(RconUtils.BANNED)) {
                    throw new RuntimeException("Error when trying connect, received error " + status);
                }
                if (status.equals(RconUtils.OK)) {
                    LOGGER.info("Connected with password " + rconPassword);
                    go = false;
                    break;
                }
                if (!crackIt) {
                    go = false;
                    break;
                }
                LOGGER.info(rconPassword + " did not work");
                rconPassword = getNextPassword(rconPassword);
            }
            try {
                LOGGER.info("Sleeping...");
                Thread.sleep(PERIOD_IN_SECONDS * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Increases string to next valid password
     * @param lastTriedPassword string to increase
     * @return lastTriedPassword++
     */
    String getNextPassword(String lastTriedPassword) {
        int length = lastTriedPassword.length();
        char c = lastTriedPassword.charAt(length - 1);

        if (c == 'Z') {
            return length > 1 ? getNextPassword(lastTriedPassword.substring(0, length - 1)) + 'a' : "aa";
        }
        c++;
        while (!isValidRconPasswordChar(c)) {
            c++;
        }
        return lastTriedPassword.substring(0, length - 1) + c;
    }

    /**
     * All chars described below are valid
     * <p>
     * Full char list (including whitespace at the start):
     * <p>
     * !"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz{|}~
     *
     * @param c the char co check
     * @return whether c is a valid character for an RCON password
     */
    boolean isValidRconPasswordChar(char c) {
        return CharMatcher.inRange('a', 'z')
                .or(CharMatcher.inRange('A', 'Z'))
                .or(CharMatcher.inRange('{', '~')) // contains {|}~
                .or(CharMatcher.inRange(' ', '@')) // contains space, !"#$%&'()*+,-./:;<=>?@ and 0-9
                .matches(c);
    }

}
