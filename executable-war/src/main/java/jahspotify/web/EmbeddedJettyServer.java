package jahspotify.web;

import java.io.File;
import java.net.URL;
import java.security.ProtectionDomain;

import org.mortbay.jetty.*;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class EmbeddedJettyServer
{

    public static void main(String[] args) throws Exception
        {
            int port = Integer.parseInt(System.getProperty("port", "8080"));
            System.out.println("port = " + port);
            Server server = new Server();
            Connector connector = new SelectChannelConnector();
            connector.setPort(port);
            server.addConnector(connector);


            ProtectionDomain domain = EmbeddedJettyServer.class.getProtectionDomain();
            URL location = domain.getCodeSource().getLocation();

            System.out.println("location = " + location);
            WebAppContext webapp = new WebAppContext();
            webapp.setContextPath("/jahspotify");
            webapp.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
            webapp.setServer(server);
            webapp.setWar(location.toExternalForm());

          /*  // (Optional) Set the directory the war will extract to.
            // If not set, java.io.tmpdir will be used, which can cause problems
            // if the temp directory gets cleaned periodically.
            // Your build scripts should remove this directory between deployments
            webapp.setTempDirectory(new File("/path/to/webapp-directory"));*/

            server.setHandler(webapp);
            server.start();
            server.join();
        }
/*
     public static void main(String[] args) {
        try {
            Server server = new Server();
            Connector connector = new SelectChannelConnector();
            connector.setPort(8080);
            server.addConnector(connector);

            URL url = Start.class.getClassLoader().getResource("Start.class");
            File warFile = new File(((JarURLConnection) url.openConnection()).getJarFile().getName());

            WebAppContext context = new WebAppContext(warFile.getAbsolutePath(), "/jahspotify");

            context.setConfigurationClasses(new String[]{
                    "org.mortbay.jetty.webapp.WebInfConfiguration",
                    "org.mortbay.jetty.plus.webapp.EnvConfiguration",
                    "org.mortbay.jetty.plus.webapp.Configuration",
                    "org.mortbay.jetty.webapp.JettyWebXmlConfiguration",
                    "org.mortbay.jetty.webapp.TagLibConfiguration"
                });

            server.setHandler(context);


            server.start();
            server.join();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/
}
