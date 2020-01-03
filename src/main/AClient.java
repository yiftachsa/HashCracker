public abstract class AClient {

    private static final double SERVERTIMEOUT = 1000000; //FIXME: Set a timer

    protected char[] hash;

    protected int inputLength;

    /**
     * Start up the client's communication socket.
     * @return - boolean - true if the client started up successfully.
     */
    public abstract boolean startUp();

    /**
     * Shutting down the client's communication socket.
     */
    public abstract void shutdown();

    /**
     * Receives inputs from the user.
     * @param groupname - String - the name of the creators group
     * @return - boolean - true if all inputs were successfully received
     */
    public abstract boolean receiveInputs(String groupname);
}
