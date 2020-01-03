public class HashCracker {

    private static final String GROUPNAME = "we don't have time for this"; //FIXME: Maybe move to main

    private static final int NUMBEROFSERVERS = 2; //FIXME: Maybe move to main

    public static void main(String[] args) {

        System.out.println("Creating the Servers");
        AServer[] servers = new Server[NUMBEROFSERVERS];
        for (int i = 0; i < servers.length; i++) {
            servers[i] = new Server();
        }

        System.out.println("Creating Client");
        AClient client = new Client();

        boolean startUpSucceeded = startUp(client, servers);
        if(!startUpSucceeded){
            System.out.println("start up failed");
            return;
        }

        //Try to receive input
        if(!client.receiveInputs(GROUPNAME)){
            //TODO:ShutDown everything
            shutdown(client,servers);
            return;
        }
        //From now on good inputs are assumed
        //TODO:Begin Communication


    }

    /**
     * Shuts down the given client and all the servers.
     * @param client - AClient - client to shut down
     * @param servers - AServer[] - servers to shut down
     */
    private static void shutdown(AClient client, AServer[] servers) {
        //TODO
    }

    /**
     * Tries to start up all the servers and the client.
     * If one fails then all the previously raised elements will be shut down.
     * @param client - AClient - client to start up.
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

        System.out.println("Raising the Client (like the baby ANTICHRIST)");
        if (isGoodStartUp) {
            if (!client.startUp()) {
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
