package de.apwolf.rcon;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class RconUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RconUtils.class);

    static final String OK = "OK";

    static final String NO_PASSWORD_SET = "NO_PASSWORD_SET";

    static final String WRONG_PASSWORD = "WRONG_PASSWORD";

    static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";

    static final String BANNED = "BANNED";

    private static final byte[] RCON_PREFIX = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};

    private static final String CHALLENGE_RCON_STRING = "challenge rcon";

    private static final String NEGATIVE_RESPONSE_NO_RCON_SET = "No password set";

    private static final String RESPONSE_BAD_PASSWORD = "lBad rcon_password.";

    static final String RESPONSE_BANNED = "lYou have been banned from this server.";

    private static byte[] NEGATIVE_RESPONSE_WRONG_PASSWORD;

    private static final byte[] RESPONSE_SUFFIX = new byte[]{(byte) 0x6c, (byte) 0x00, (byte) 0x00};

    static {
        NEGATIVE_RESPONSE_WRONG_PASSWORD = ArrayUtils.addAll(RESPONSE_BAD_PASSWORD.getBytes(),
                new byte[]{(byte) 0x0a, 0x00, 0x00});
        NEGATIVE_RESPONSE_WRONG_PASSWORD = ArrayUtils.addAll(RCON_PREFIX, NEGATIVE_RESPONSE_WRONG_PASSWORD);

    }

    public static byte[] addRconPrefix(byte[] array) {
        return ArrayUtils.addAll(RCON_PREFIX, array);
    }

    public static byte[] removeRconPrefix(byte[] array) {
        if (!Arrays.equals(ArrayUtils.subarray(array, 0, RCON_PREFIX.length), RCON_PREFIX)) {
            throw new RuntimeException("Checked array does not include RCON prefix");
        }
        return ArrayUtils.subarray(array, 4, array.length);
    }

    public static byte[] buildChallengeCommand() {
        return addRconPrefix(CHALLENGE_RCON_STRING.getBytes());
    }

    public static String extractChallenge(byte[] roughChallenge) {
        byte[] challengeWithoutPrefix = removeRconPrefix(roughChallenge);
        byte[] challengeWithoutSuffix = ArrayUtils.subarray(challengeWithoutPrefix, 0, challengeWithoutPrefix.length - 2);
        byte[] challenge = ArrayUtils.subarray(challengeWithoutSuffix, CHALLENGE_RCON_STRING.getBytes().length, challengeWithoutSuffix.length);

        return new String(challenge).trim();
    }

    public static byte[] buildConnectCommand(String challenge, String rconPassword) {
        String command = "rcon " + challenge + " \"" + rconPassword + "\"";
		return addRconPrefix(command.getBytes());
    }

    public static String parseConnectCommandResponse(byte[] response) {
        String responseString = new String(response);
        if (Arrays.equals(response, ArrayUtils.addAll(RCON_PREFIX, RESPONSE_SUFFIX))) {
            return OK;
        } else if (responseString.contains(NEGATIVE_RESPONSE_NO_RCON_SET)) {
            return NO_PASSWORD_SET;
        } else if (Arrays.equals(response, NEGATIVE_RESPONSE_WRONG_PASSWORD)) {
            return WRONG_PASSWORD;
        } else if (responseString.contains(RESPONSE_BANNED)) {
            return BANNED;
        } else {
            LOGGER.error("Received unknown error upon connecting: " + responseString);
            return UNKNOWN_ERROR;
        }
    }

}
