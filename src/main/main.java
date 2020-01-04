
public class main {
    public static void main(String[] args) {
        System.out.println("test");
        Message message = Message.generateRequestMessage(HashCracker.GROUPNAME, ("006345b12ad566bf7891be05cef5909df928cbcd").toCharArray(), (char) 6, ("aaaaaa").toCharArray(), ("zzzzzz").toCharArray());

        byte[] messageBytes = message.convertToByteArray();

        Message reconstructedMessage = Message.getMessageFromBytes(messageBytes);

        System.out.println("check");
        //voodoo -> 006345b12ad566bf7891be05cef5909df928cbcd
    }
}
