package de.apwolf.rcon;

import java.io.IOException;

public class Main {

    /**
     * rcon_password: Set rcon passsword. Leave blank to disable rcon <br>
     * sv_rcon_banpenalty <mins>: Number of minutes to ban users who fail rcon authentication. Default: 0 <br>
     * sv_rcon_maxfailures <0-20>: Max number of times a user can fail rcon authentication before being banned. Default: 10 <br>
     * sv_rcon_minfailures <0-20>: Number of times a user can fail rcon authentication in sv_rcon_minfailuretime before being banned. Default: 5 <br>
     * sv_rcon_minfailuretime <seconds>: Number of seconds to track failed rcon authentications. Default: 30
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String serverIpAndPort = "127.0.0.1:27021";
        RconConnector connector = new RconConnector();
        connector.connect(null, serverIpAndPort);
    }

}
