public class HashCracker {

    public static final char[] GROUPNAME = (new String("we don't have time for this     ")).toCharArray();
    private static final int NUMBEROFSERVERS = 1; //FIXME: Maybe move to main
    public static final int APPLICATIONPORT = 3117;
    public static final int MAXMESSAGESIZE = 586;

    public static void main(String[] args) {

        System.out.println("Creating the Servers");
        AServer[] servers = new Server[NUMBEROFSERVERS];
        for (int i = 0; i < servers.length; i++) {
            servers[i] = new Server();
        }

        System.out.println("Creating Client");
        AClient client = new Client();

        //Start up
        boolean startUpSucceeded = startUp(client, servers);
        if (!startUpSucceeded) {
            System.out.println("start up failed");
            return;
        }

        //Try to receive input
        if (!client.receiveInputs(new String(GROUPNAME))) {
            shutdown(client, servers);
            return;
        }
        //From now on good inputs are assumed
        client.beginCommunication();


        shutdown(client, servers);
    }

    public static void clientStation(int port) {
        System.out.println("Raising the Client (like the baby ANTICHRIST)");
        AClient client = new Client();
        if (!client.startUp(port)) {
            System.out.println("client died - sad, so sad ~~> killing the servers and exiting");
            return;
        }
        if (!client.receiveInputs(new String(GROUPNAME))) {
            client.shutdown();
            return;
        }
        client.beginCommunication();
        client.shutdown();

    }

    public static void serverStation(/*long upTime*/) {
        System.out.println("Raising the Servers (from HELL)");
        AServer server = new Server();
        if (!server.startUp()) {
            System.out.println("start up failed");
            return;
        }
/*
        //Let the server live for upTime minutes
        try {
            Thread.sleep(upTime);//180000
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.shutdown();
*/

    }


    /**
     * Shuts down the given client and all the servers.
     *
     * @param client  - AClient - client to shut down
     * @param servers - AServer[] - servers to shut down
     */
    private static void shutdown(AClient client, AServer[] servers) {
        client.shutdown();
        for (int i = 0; i < servers.length; i++) {
            servers[i].shutdown();
        }
    }

    /**
     * Tries to start up all the servers and the client.
     * If one fails then all the previously raised elements will be shut down.
     *
     * @param client  - AClient - client to start up.
     * @param servers - AServer - servers to start up.
     * @return - boolean - true if all the servers and the client started up successfully, else false.
     */
    private static boolean startUp(AClient client, AServer[] servers) {
        boolean isGoodStartUp = true;

        System.out.println("Raising the Servers (from HELL)");
        for (int i = 0; i < servers.length && isGoodStartUp; i++) {
            if (!servers[i].startUp()) {
                isGoodStartUp = false;
                //Shutting down all the servers we already raised
                for (int j = 0; j < i; j++) {
                    servers[j].shutdown();
                }
            }
        }

        if (isGoodStartUp) {
            System.out.println("Raising the Client (like the baby ANTICHRIST)");
            if (!client.startUp(4062)) {
                System.out.println("client died - sad, so sad ~~> killing the servers and exiting");
                isGoodStartUp = false;
                //Shutting down all the servers we already raised
                for (int i = 0; i < servers.length; i++) {
                    servers[i].shutdown();
                }
            }
        }
        return isGoodStartUp;
    }

}
