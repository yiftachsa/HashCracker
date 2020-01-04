import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Client extends AClient {

    private DatagramSocket UDPSocket;

    @Override
    public boolean startUp() {
        try {
            this.UDPSocket = new DatagramSocket(4061); //Because Merav loves herself
        } catch (SocketException e) {
            return false;
        }
        return true;
    }

    @Override
    public void shutdown() {
        this.UDPSocket.close();
    }

    @Override
    public boolean receiveInputs(String groupname) {
        Scanner inputScanner = new Scanner(System.in);
        char[] hashInput = null;
        int inputLength = -1;

        //Start of program
        System.out.println("Welcome to " + groupname + ".\n");

        String input = "";
        do {
            System.out.println("Please enter the hash:");
            input = inputScanner.nextLine();
            hashInput = input.toCharArray();
        } while (!input.equals("exit") && !isGoodHash(hashInput));

        if (input.equals("exit")) {
            return false;
        }

        do {
            System.out.println("Please enter the input string length:");
            input = inputScanner.nextLine();
        } while (!isGoodInputLength(input));

        inputLength = Integer.parseInt(input);

        this.hash = hashInput;
        this.inputLength = inputLength;
        return true;
    }


    /**
     * Checks if a given char array contains only small letters and is in the correct length.
     *
     * @param hashInput - char[] - character array.
     * @return - boolean - true if hashInput contains only small letters and is in the correct length, else false.
     */
    private boolean isGoodHash(char[] hashInput) {
        final int HASHSIZE = 40;
        boolean isGoodHash = true;
        if (hashInput != null) {
            //length check
            if (hashInput.length != HASHSIZE) {
                isGoodHash = false;
            }
            //individual letters checks
            for (int i = 0; i < 40 && isGoodHash; i++) {
                char currentChar = hashInput[i];
                if (currentChar < '0' || currentChar > 'z' || (currentChar > '9' && currentChar < 'a')) {
                    isGoodHash = false;
                }
            }
        } else {
            isGoodHash = false;
        }
        return isGoodHash;
    }

    /**
     * Checks if a given String contains only numbers.
     *
     * @param strToCheck - String - a string to be checked.
     * @return - boolean - true if strToCheck contains only numbers.
     */
    private boolean isGoodInputLength(String strToCheck) {
        boolean isGoodInputLength = true;
        if (!strToCheck.matches("^[0-9]+$")) {
            isGoodInputLength = false;
        }
        return isGoodInputLength;
    }

    @Override
    public void beginCommunication() {
        try {
            discover();
        } catch (IOException e) {
            System.out.println("Failed to send a response message");
        }

        HashSet<InetAddress> discoveredServers = new HashSet<>();

        byte[] receivedMessageBytes = new byte[HashCracker.MAXMESSAGESIZE];
        DatagramPacket UDPPacket = null;
        long offerCollectionCurrentTime;
        long offerCollectionEndTime = System.currentTimeMillis() + OFFERSTIMEOUT;
        while ((offerCollectionCurrentTime = System.currentTimeMillis()) < offerCollectionEndTime) {
            UDPPacket = new DatagramPacket(receivedMessageBytes, receivedMessageBytes.length);

            try {
                UDPSocket.receive(UDPPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            char messageType = Message.getMessageTypeFromMessage(receivedMessageBytes);
            if (messageType == 2) {
                discoveredServers.add(UDPPacket.getAddress());
            }
            receivedMessageBytes = new byte[HashCracker.MAXMESSAGESIZE];
        }

        InetAddress[] servers = new InetAddress[discoveredServers.size()];
        discoveredServers.toArray(servers);

        int serverIndex = 0;
        String[] domains = divideToDomains(inputLength, discoveredServers.size());
        for (int i = 0; i < domains.length; i = i+2) {
            sendRequestMessage(domains[i], domains[i+1], servers[serverIndex++]);
        }
        //TODO: Recieve messages from servers for TIMEOUT time.
        //TODO:Display results
    }



    /**
     * Sends an Request message to the given IP.
     */
    private void sendRequestMessage(String stringStart, String stringEnd, InetAddress IP) {
        Message requestMessage = Message.generateRequestMessage(HashCracker.GROUPNAME, hash, (char)inputLength, stringStart.toCharArray(),stringEnd.toCharArray());
        sendMessage(IP, requestMessage.convertToByteArray());
    }

    private void sendMessage(InetAddress ip, byte[] messageByteArray) {
        DatagramPacket UDPRequestPacket = new DatagramPacket(messageByteArray, messageByteArray.length, ip, 3117);
        try {
            this.UDPSocket.send(UDPRequestPacket);
        } catch (IOException e) {
            System.out.println("Failed to send a response message");
        }
    }


    private void discover() throws IOException {
        Message discoverMessage = Message.generateDiscoverMessage(HashCracker.GROUPNAME);
        byte[] messageByteArray = discoverMessage.convertToByteArray();

        this.UDPSocket.setBroadcast(true);

        DatagramPacket UDPDiscoverPacket = new DatagramPacket(messageByteArray, messageByteArray.length, InetAddress.getByName("255.255.255.255"), 3117);

        UDPSocket.send(UDPDiscoverPacket);

        this.UDPSocket.setBroadcast(false);

    }


    private static String[] divideToDomains(int stringLength, int numOfServers) {
        String[] domains = new String[numOfServers * 2];

        StringBuilder first = new StringBuilder(); //aaa
        StringBuilder last = new StringBuilder(); //zzz

        for (int i = 0; i < stringLength; i++) {
            first.append("a"); //aaa
            last.append("z"); //zzz
        }

        int total = convertStringToInt(last.toString());
        int perServer = (int) Math.floor(((double) total) / ((double) numOfServers));

        domains[0] = first.toString(); //aaa
        domains[domains.length - 1] = last.toString(); //zzz
        int summer = 0;

        for (int i = 1; i <= domains.length - 2; i += 2) {
            summer += perServer;
            domains[i] = converxtIntToString(summer, stringLength); //end domain of server
            summer++;
            domains[i + 1] = converxtIntToString(summer, stringLength); //start domain of next server
        }

        return domains;
    }

    private static int convertStringToInt(String toConvert) {
        char[] charArray = toConvert.toCharArray();
        int num = 0;
        for (char c : charArray) {
            if (c < 'a' || c > 'z') {
                throw new RuntimeException();
            }
            num *= 26;
            num += c - 'a';
        }
        return num;
    }

    private static String converxtIntToString(int toConvert, int length) {
        StringBuilder s = new StringBuilder(length);
        while (toConvert > 0) {
            int c = toConvert % 26;
            s.insert(0, (char) (c + 'a'));
            toConvert /= 26;
            length--;
        }
        while (length > 0) {
            s.insert(0, 'a');
            length--;
        }
        return s.toString();
    }
}


