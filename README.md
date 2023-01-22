# What is this?

This is an extremely crude piece of code to connect to a Goldsrc 
server via RCON. If no password is given, the tool will try brute force
to find the password.

# How does it work?
The RCON login mechanism is a simple challenge / response protocol.  
We request a challenge and then send the challenge and the password
in cleartext back to the server (see RconUtils.java for the exact
encoding).

Since communication is done over UDP, we start a simple UDP
server to receive answers.

We parse the responses of the server to check if we successfully
connected or if it failed (or if we got banned).

# How good does it work?

Cracking will take ages as Goldsrc bans you after too many failed logins
in a certain amount of time.
A timeout will reset the threshold, so this tool just tries a few
times, waits until the failed attempts are reset and tries some more.

By default, your IP will get banned forever if you overdo it.

So this tool is not really useful for serious attacks.

# How did the tool get built?

The Goldsrc server code was reverse engineered 
(hlds.exe, hw.dll, sw.dll, swds.dll)
in order to find things like the allowed characters
and interesting config parameters:
* sv_rcon_banpenalty <mins>:
  * Number of minutes to ban users who fail rcon authentication. Default: 0
* sv_rcon_maxfailures <0-20>: 
  * Max number of times a user can fail rcon authentication before being banned. Default: 10
* sv_rcon_minfailures <0-20>: 
  * Number of times a user can fail rcon authentication in sv_rcon_minfailuretime before being banned. Default: 5
* sv_rcon_minfailuretime <seconds>: 
  * Number of seconds to track failed rcon authentications. Default: 30

The UDP protocol is unencrypted, so all traffic was just sniffed
with Wireshark.