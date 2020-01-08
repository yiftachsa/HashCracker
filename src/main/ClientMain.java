
public class ClientMain {
    public static void main(String[] args) {
        /*
        System.out.println("test");
        Message message = Message.generateRequestMessage(HashCracker.GROUPNAME, ("006345b12ad566bf7891be05cef5909df928cbcd").toCharArray(), (char) 6, ("aaaaaa").toCharArray(), ("zzzzzz").toCharArray());

        byte[] messageBytes = message.convertToByteArray();

        Message reconstructedMessage = Message.getMessageFromBytes(messageBytes);

        System.out.println("check")*/

        //voodoo -> 006345b12ad566bf7891be05cef5909df928cbcd
        //child ->0e93069c40111cd62dac2cd02cd71daffdb01cc0
        //one -> fe05bcdcdc4928012781a5f1a2a77cbb5398e106
        //word -> 3cbcd90adc4b192a87a625850b7f231caddf0eb3
        HashCracker.clientStation();


    }
}
