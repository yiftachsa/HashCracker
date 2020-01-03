import java.util.Scanner;

public class Client extends AClient {


    @Override
    public boolean startUp() {
        return true;
    }

    @Override
    public void shutdown() {

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
     * @param strToCheck - String - a string to be checked.
     * @return - boolean - true if strToCheck contains only numbers.
     */
    private boolean isGoodInputLength(String strToCheck) {
        boolean isGoodInputLength = true;
        if(!strToCheck.matches("^[0-9]+$")){
            isGoodInputLength = false;
        }
        return isGoodInputLength;
    }
}
