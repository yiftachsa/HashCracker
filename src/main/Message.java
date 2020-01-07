import java.util.ArrayList;

public class Message {
    //TODO:Message
    private char[] teamName;
    private char type;
    private char[] hash;
    private char originalLength;
    private char[] originalStringStart;
    private char[] originalStringEnd;


    private Message(char[] groupName, char type, char[] hash, char originalLength, char[] originalStringStart, char[] originalStringEnd) {
        this.teamName = groupName;

        this.type = type;

        if (hash == null) {
            this.hash = new char[40];
        } else {
            this.hash = hash;
        }

        this.originalLength = originalLength;

        if (originalStringStart == null) {
            this.originalStringStart = new char[1];
        } else {
            this.originalStringStart = originalStringStart;
        }


        if (originalStringEnd == null) {
            this.originalStringEnd = new char[1];
        } else {
            this.originalStringEnd = originalStringEnd;
        }

    }

    public char[] getTeamName() {
        return teamName;
    }

    public char getType() {
        return type;
    }

    public char[] getHash() {
        return hash;
    }

    public char getOriginalLength() {
        return originalLength;
    }

    public char[] getOriginalStringStart() {
        return originalStringStart;
    }

    public char[] getOriginalStringEnd() {
        return originalStringEnd;
    }

    /**
     * Generates new DISCOVER message and returns it
     *
     * @return - Message - new DISCOVER message
     */
    public static Message generateDiscoverMessage(char[] groupName) {
        char type = 1;

        Message newMessage = new Message(groupName, type, null, (char) (0), null, null);

        return newMessage;
    }

    /**
     * Generates new OFFER message and returns it
     *
     * @return - Message - new OFFER message
     */
    public static Message generateOfferMessage(char[] groupName) {
        char type = 2;

        Message newMessage = new Message(groupName, type, null, (char) (0), null, null);

        return newMessage;
    }

    /**
     * Generates new REQUEST message and returns it
     *
     * @return - Message - new REQUEST message
     */
    public static Message generateRequestMessage(char[] groupName, char[] hash, char originalLength, char[] originalStringStart, char[] originalStringEnd) {
        char type = 3;

        Message newMessage = new Message(groupName, type, hash, originalLength, originalStringStart, originalStringEnd);

        return newMessage;
    }

    /**
     * Generates new ACKNOWLEDGE message and returns it
     *
     * @return - Message - new ACKNOWLEDGE message
     */
    public static Message generateAcknowledgeMessage(char[] groupName, char[] hash, char originalLength, char[] originalStringStart) {
        char type = 4;

        Message newMessage = new Message(groupName, type, hash, originalLength, originalStringStart, null);

        return newMessage;
    }

    /**
     * Generates new NEGATIVE-ACKNOWLEDGE message and returns it
     *
     * @return - Message - new NEGATIVE-ACKNOWLEDGE message
     */
    public static Message generateNegativeAcknowledgeMessage(char[] groupName, char[] hash, char originalLength) {
        char type = 5;

        Message newMessage = new Message(groupName, type, hash, originalLength, null, null);

        return newMessage;
    }

    /**
     * gets the message type from a byte[] representation of a message
     *
     * @param message - byte[] - a representation of a message
     * @return - char - if the messge is good, the given message type, else 0
     */
    public static char getMessageTypeFromMessage(byte[] message) {
        if (message != null && message.length > 32) {
            return (char) message[32];
        }
        return 0;
    }


    private static char[] getTeamNameFromMessage(byte[] message) {
        char[] teamName = new char[32];

        for (int i = 0; i < teamName.length; i++) {
            teamName[i] = (char) message[i];
        }
        return teamName;
    }

    private static char[] getHashFromMessage(byte[] message) {
        char[] hash = new char[40];

        for (int i = 0; i < hash.length; i++) {
            hash[i] = (char) message[i + 33];
        }
        return hash;
    }

    /**
     * Reconstructs a Message object from message byte[].
     * @param message - Message - Reconstructed Message. null if failed to reconstruct.
     * @return - byte[] - message to reconstruct.
     */
    public static Message getMessageFromBytes(byte[] message) {
        char[] teamName = getTeamNameFromMessage(message);

        char messageType = getMessageTypeFromMessage(message);

        switch ((int)messageType) {
            case 1: {
                return generateDiscoverMessage(teamName);
            }
            case 2: {
                return generateOfferMessage(teamName);
            }
            case 3: {
                char[] messageHash = getHashFromMessage(message);
                char originalMessageLength = (char) message[73];

//                int messageLengthLeft = message.length - 74;
//                int originalStringSize = messageLengthLeft/2;

                char[] originalStringStart = new char[originalMessageLength];
                for (int i = 0; i < originalStringStart.length; i++) {
                    originalStringStart[i] = (char) message[i + 74];
                }

                char[] originalStringEnd = new char[originalMessageLength];
                for (int i = 0; i < originalStringEnd.length; i++) {
                    originalStringEnd[i] = (char) message[i +originalMessageLength+ 74];
                }

                return generateRequestMessage(teamName, messageHash, originalMessageLength, originalStringStart, originalStringEnd);
            }
            case 4: {
                char[] messageHash = getHashFromMessage(message);
                char originalMessageLength = (char) message[74];
                char[] originalStringStart = new char[message.length - 74];
                for (int i = 0; i < originalStringStart.length; i++) {
                    originalStringStart[i] = (char) message[i + 74];
                }
                return generateAcknowledgeMessage(teamName, messageHash, originalMessageLength, originalStringStart);
            }
            case 5: {
                char[] messageHash = getHashFromMessage(message);
                char originalMessageLength = (char) message[74];
                return generateNegativeAcknowledgeMessage(teamName, messageHash, originalMessageLength);
            }
        }
        return null;
    }

    /**
     * Converts all the fields of this message to byte[]
     *
     * @return - byte[] - a byte representation of the message
     */
    public byte[] convertToByteArray() {
        ArrayList<Byte> bytesArrayList = new ArrayList<>();

        for (char currChar : this.teamName) {
            bytesArrayList.add((byte) currChar);
        }
        //Padding with white spaces
        while (bytesArrayList.size() < 32) {
            bytesArrayList.add((byte) ' ');
        }

        bytesArrayList.add((byte) this.type);

        for (char currChar : this.hash) {
            bytesArrayList.add((byte) currChar);
        }

        bytesArrayList.add((byte) this.originalLength);

        for (char currChar : this.originalStringStart) {
            bytesArrayList.add((byte) currChar);
        }
        for (char currChar : this.originalStringEnd) {
            bytesArrayList.add((byte) currChar);
        }

        byte[] messageBytes = new byte[bytesArrayList.size()];
        for (int i = 0; i < messageBytes.length; i++) {
            messageBytes[i] = bytesArrayList.get(i);
        }

        return messageBytes;
    }

}
