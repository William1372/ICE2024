import java.util.Scanner;

public class TextUI {
    private Scanner scanner = new Scanner(System.in);


    public void displayMsg (String msg){
        System.out.println(msg);
    }

    public String promptText (String msg){
        System.out.println(msg);
        String input = scanner.nextLine();
        return input;
    }

    public int promptNumeric (String msg){
        System.out.println(msg);
        String input = scanner.nextLine();
        int number;

        try {
            number = Integer.parseInt(input);
        }
        catch (NumberFormatException e){
            displayMsg("Please only enter numbers");
            number = promptNumeric (msg);
        }
        return number;
    }
}
