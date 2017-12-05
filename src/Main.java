/**
 * Bao Nguyen
 * BCN140030
 * SE 3345.004
 *
 * Project 3
 * Implementing the insert, contains, and print functions of the Red-black Tree
 */

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("1. Insert\n2. Contains\n3. Print tree\n4. Quit");
        int menu;
        RedBlackTree redBlackTree = new RedBlackTree();

        do {
            menu = getInt("\nYour option: ", 1, 4);

            switch (menu) {
                case 1: // insert new node into the tree
                        int element = getInt("Element: ");
                        if(redBlackTree.insert(element))
                            System.out.println("Element inserted.");
                        else
                            System.out.println("Duplicated.");
                        break;
                case 2: // check to see if the element is in the tree
                        int key = getInt("Element: ");
                        System.out.printf("The tree contains %d: %s\n", key, redBlackTree.contains(key));
                        break;
                case 3: // print tree
                        redBlackTree.print();
                        break;
                case 4: // quit
                        System.out.println("Good bye.");
                        break;
                default:
            }

        } while(menu != 4); // loop until the user chooses to quit
    }


    /***
     * method: getInt
     * get an integer from the user
     * @param prompt: message to the users of what to expect them to enter
     * @return result of user's selection
     */
    public static int getInt(String prompt) {
        return getInt(prompt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }


    /***
     * method: getInt
     * overloaded method to get an integer that checks for type & range
     * @param prompt: message to the users of what to expect them to enter
     * @param lowerBound: lower bound integer of the domain of input
     * @param upperBound: upper bound integer of the domain of input
     * @return result of user's selection
     */
    public static int getInt(String prompt, int lowerBound, int upperBound) {
        int number = 0;
        boolean isInt = false;
        Scanner input;
        String data;

        // keep prompting until getting a valid input
        do {
            isInt = true;
            System.out.print(prompt);
            input = new Scanner(System.in);
            data = input.nextLine();

            // parse int from string
            try {
                number = Integer.parseInt(data);
            }
            // user did not enter an integer
            catch (NumberFormatException e) {
                System.out.printf("Enter a positive number between %d and %d\n", lowerBound, upperBound);
                isInt = false;
            }

            // user entered an integer but it is not in the valid range
            if (isInt && (number < lowerBound || number > upperBound)) {
                System.out.printf("Enter a positive number between %d and %d\n", lowerBound, upperBound);
                number = 0;
            }

        } while(number == 0);

        return number;
    }

}
