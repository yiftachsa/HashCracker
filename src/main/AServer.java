public abstract class AServer {
    protected static final long SOLVETIMEOUT = 100000; //FIXME: Set a timer

    /**
     * Start up the server's communication socket.
     * @return - boolean - true if the server started up successfully.
     */
    public abstract boolean startUp();
    /**
     * Shutting down the server's communication socket.
     */
    public abstract void shutdown();
}
