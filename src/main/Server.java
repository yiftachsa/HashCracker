import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server extends AServer {

    private static volatile DatagramSocket UDPSocket;
    private Thread serverThread;

    @Override
    public boolean startUp() {
        //Create a socket to listen at port 3117
        if (this.UDPSocket == null) {
            try {
                this.UDPSocket = new DatagramSocket(3117);
            } catch (SocketException e) {
                return false;
            }
        }

        //serverThread = new Thread(() -> {
        byte[] receivedMessage = new byte[HashCracker.MAXMESSAGESIZE];

        DatagramPacket UDPPacket = null;
        while (true) {

            UDPPacket = new DatagramPacket(receivedMessage, receivedMessage.length);

            try {
                System.out.println("Server - Before receiving message");
                UDPSocket.receive(UDPPacket);
                System.out.println("Server - After receiving message");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //receive holds the newly received message
            new Thread(new RunnableMessageHandler(UDPSocket, receivedMessage, UDPPacket,SOLVETIMEOUT)).start();

            // Clear the buffer after every message.
            receivedMessage = new byte[HashCracker.MAXMESSAGESIZE];

            //});
            //serverThread.start();
            //System.out.println("Server - thread started");
        }

    }


        @Override
        public void shutdown () {
            serverThread.interrupt();
            serverThread.stop();

            if (this.UDPSocket != null) {
                this.UDPSocket.close();
                this.UDPSocket = null;
            }


        }
    }
