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

    public RunnableMessageHandler(DatagramSocket UDPSocket, byte[] receivedMessage, DatagramPacket UDPPacket) {
        this.UDPSocket = UDPSocket;
        this.message = receivedMessage;
        this.UDPPacket = UDPPacket;
    }

    @Override
    public void run() {
        char messageType = Message.getMessageTypeFromMessage(message);
        if (messageType == 1) { //Discover
            /*SEND OFFER MESSAGE*/
            sendOfferMessage();
        } else if (messageType == 3) { //Request
            Message receivedMessage = Message.getMessageFromBytes(message);
            String result = tryDeHash(String.copyValueOf(receivedMessage.getOriginalStringStart()),String.copyValueOf(receivedMessage.getOriginalStringEnd()),String.copyValueOf(receivedMessage.getHash())); //FIXME: NO LENGTH
            if (result != null) {
                /*SEND ACK MESSAGE*/
                sendACKMessage(result.toCharArray(), receivedMessage.getHash(),receivedMessage.getOriginalLength());
            } else {
                /*SEND NACK MESSAGE*/
                sendNACKMessage(receivedMessage.getHash(),receivedMessage.getOriginalLength());
            }
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
        int start = convertStringToInt(startRange);
        int end = convertStringToInt(endRange);
        int length = startRange.length();
        for (int i = start; i <= end; i++) {
            String currentString = converxtIntToString(i, length);
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

    private int convertStringToInt(String toConvert) {
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


    private String converxtIntToString(int toConvert, int length) {
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

