public class Message {
    //TODO:Message
    private char[] teamName;
    private char type;
    private char[] hash;
    private char originalLength;
    private char[] originalStringStart;
    private char[] originalStringEnd;


    private Message() {
    }

    /**
     * Generates new DISCOVER message and returns it
     * @return - Message - new DISCOVER message
     */
    public static Message generateDiscoverMessage(char[] groupName){
        Message newMessage = new Message();
//TODO
        return newMessage;
    }
    /**
     * Generates new OFFER message and returns it
     * @return - Message - new OFFER message
     */
    public static Message generateOfferMessage(char[] groupName){
        Message newMessage = new Message();
//TODO

        return newMessage;
    }
    /**
     * Generates new REQUEST message and returns it
     * @return - Message - new REQUEST message
     */
    public static Message generateRequestMessage(char[] groupName, char[] hash, char originalLength, char[] originalStringStart, char[] originalStringEnd){
        Message newMessage = new Message();
//TODO

        return newMessage;
    }
    /**
     * Generates new ACKNOWLEDGE message and returns it
     * @return - Message - new ACKNOWLEDGE message
     */
    public static Message generateAcknowledgeMessage(char[] groupName, char[] hash, char originalLength, char[] originalStringStart){
        Message newMessage = new Message();
//TODO

        return newMessage;
    }
    /**
     * Generates new NEGATIVE-ACKNOWLEDGE message and returns it
     * @return - Message - new NEGATIVE-ACKNOWLEDGE message
     */
    public static Message generateNegativeAcknowledgeMessage(char[] groupName , char[] hash, char originalLength){
        Message newMessage = new Message();
//TODO

        return newMessage;
    }



}
