import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RunnableMessageHandler implements Runnable {

    private DatagramSocket UDPSocket;
    private DatagramPacket UDPPacket;
    private byte[] message;
    private long solveTimeout;

    public RunnableMessageHandler(DatagramSocket UDPSocket, byte[] receivedMessage, DatagramPacket UDPPacket, long solveTimeout) {
        this.UDPSocket = UDPSocket;
        this.message = receivedMessage;
        this.UDPPacket = UDPPacket;
        this.solveTimeout = solveTimeout;
    }

    @Override
    public void run() {
        System.out.println("Received message from IP: "+UDPPacket.getAddress() + " Port: "+ UDPPacket.getPort() + " Length: "+message.length);
        try {
            char messageType = Message.getMessageTypeFromMessage(message);
            if (messageType == 1) { //Discover
                /*SEND OFFER MESSAGE*/
                sendOfferMessage();
            } else if (messageType == 3) { //Request
                Message receivedMessage = Message.getMessageFromBytes(message);
                String result = null;
                if (receivedMessage !=null){
                    result = tryDeHash(String.copyValueOf(receivedMessage.getOriginalStringStart()),String.copyValueOf(receivedMessage.getOriginalStringEnd()),String.copyValueOf(receivedMessage.getHash()));
                }
                if (result != null) {
                    /*SEND ACK MESSAGE*/
                    sendACKMessage(result.toCharArray(), receivedMessage.getHash(),receivedMessage.getOriginalLength());
                    System.out.println("Sent ACK - result: "+ result + " from IP: "+UDPPacket.getAddress()+ " Group: "+ String.copyValueOf(receivedMessage.getTeamName()));
                } else {
                    /*SEND NACK MESSAGE*/
                    sendNACKMessage(receivedMessage.getHash(),receivedMessage.getOriginalLength());
                    System.out.println("Sent NACK - from IP: "+UDPPacket.getAddress()+ " Group: "+ String.copyValueOf(receivedMessage.getTeamName()));
                }
            }
        } catch (Exception e){
            System.out.println("Bad message");
            System.out.println("from IP: "+UDPPacket.getAddress() + " Port: "+ UDPPacket.getPort() + " Length: "+message.length);
        }
    }

    /**
     * Sends a NACK message to the sender of the received packet.
     */
    private void sendNACKMessage(char[] hash, char length) {
        Message responseMessage = Message.generateNegativeAcknowledgeMessage(HashCracker.GROUPNAME,hash ,length);
        sendMessage(responseMessage.convertToByteArray());
    }

    /**
     * Sends an ACK message to the sender of the received packet.
     */
    private void sendACKMessage(char[] result, char[] hash, char length) {
        Message responseMessage = Message.generateAcknowledgeMessage(HashCracker.GROUPNAME, hash, length, result);
        sendMessage(responseMessage.convertToByteArray());
    }

    /**
     * Sends an offer message to the sender of the received packet.
     */
    private void sendOfferMessage() {
        Message responseMessage = Message.generateOfferMessage(HashCracker.GROUPNAME);
        sendMessage(responseMessage.convertToByteArray());
    }

    /**
     * Receives a message as a byte array and sends it to the sender of the received packet.
     * @param messageByteArray - byte[] - message to send
     */
    private void sendMessage(byte[] messageByteArray) {
        DatagramPacket UDPResponsePacket = new DatagramPacket(messageByteArray, messageByteArray.length, UDPPacket.getAddress(), UDPPacket.getPort());
        try {
            UDPSocket.send(UDPResponsePacket);
        } catch (IOException e) {
            System.out.println("Failed to send a response message");
        }
    }


    private String tryDeHash(String startRange, String endRange, String originalHash) {
        BigInteger start;
        BigInteger end;
        try {
            start = convertStringToInt(startRange);
            end = convertStringToInt(endRange);

        }catch (RuntimeException e){
            System.out.println("DeHash problem - Hash: "+ originalHash);
            System.out.println("startRange: "+ originalHash + "endRange: "+endRange);
            return null;
        }
        int length = startRange.length();


        long endTime = System.currentTimeMillis() + this.solveTimeout;

        for (BigInteger i = start; i.compareTo(end) <=0 && (System.currentTimeMillis() < endTime); i=i.add(new BigInteger("1"))) {
            String currentString = convertIntToString(i, length);
            String hash = hash(currentString);
            if (originalHash.equals(hash)) {
                return currentString;
            }
        }
        return null;
    }

    private String hash(String toHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(toHash.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashText = new StringBuilder(no.toString(16));
            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }
            return hashText.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    private BigInteger convertStringToInt(String toConvert)  throws RuntimeException{
        char[] charArray = toConvert.toCharArray();
        BigInteger num = new BigInteger("0");
        for (char c : charArray) {
            if (c < 'a' || c > 'z') {
                throw new RuntimeException();
            }
            num = num.multiply(new BigInteger("26"));
            int x = c - 'a';
            num = num.add(new BigInteger(Integer.toString(x)));
        }
        return num;
    }


    private String convertIntToString(BigInteger toConvert, int length) {
        StringBuilder s = new StringBuilder(length);
        while (toConvert.compareTo(new BigInteger("0")) > 0) {
            BigInteger c = toConvert.mod(new BigInteger("26"));
            s.insert(0, (char) (c.intValue() + 'a'));
            toConvert = toConvert.divide(new BigInteger("26"));
            length--;
        }
        while (length > 0) {
            s.insert(0, 'a');
            length--;
        }
        return s.toString();
    }
}

