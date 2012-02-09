package jahspotify.client;

import java.net.*;
import java.util.*;

import com.google.gson.Gson;
import jahspotify.web.system.ServerIdentity;

/**
 * @author Johan Lindquist
 */
public class ServerBroadcasterClient
{

    public static void main(String[] args) throws Exception
    {
        final Set<ServerIdentity> serverIdentities = ServerBroadcasterClient.discoverServers("224.0.0.1", 9764, 10000, new ServerFoundListener()
        {

            @Override
            public void serverFound(final ServerIdentity serverIdentity)
            {
                System.out.println("serverIdentity = " + serverIdentity);
            }
        });



    }

    public static Set<ServerIdentity> discoverServers(String address, int port, int timeout, final ServerFoundListener serverFoundListener) throws Exception
    {
        Set<ServerIdentity> serversFound = new HashSet<ServerIdentity>();
        InetAddress group = InetAddress.getByName(address);
        MulticastSocket multicastSocket = new MulticastSocket(port);
        multicastSocket.joinGroup(group);

        long s1 = System.currentTimeMillis();
        while (s1 + timeout > System.currentTimeMillis())
        {
            // get their responses!
            byte[] buf = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buf, buf.length);
            multicastSocket.receive(recv);
            String message = new String(recv.getData(), recv.getOffset(), recv.getLength());
            Gson gson = new Gson();
            final ServerIdentity serverIdentity = gson.fromJson(message, ServerIdentity.class);
            if (serverIdentity.getServerWebAddress() == null)
            {
                serverIdentity.setServerWebAddress(recv.getAddress().getHostAddress());
            }
            if (serversFound.add(serverIdentity) && serverFoundListener != null)
            {
                serverFoundListener.serverFound(serverIdentity);
            }
            serversFound.add(serverIdentity);
        }


        // OK, I'm done talking - leave the group...
        multicastSocket.leaveGroup(group);

        return serversFound;

    }

    public static interface ServerFoundListener
    {
        public void serverFound(ServerIdentity serverIdentity);
    }
}
