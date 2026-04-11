// Use a for loop to print a multiplication table.
import java.util.Scanner;

public class MultiplicationTable {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter a number to display its multiplication table: ");
        int number = sc.nextInt();

        System.out.println("Multiplication Table for "  + number + ":");
        for(int i = 1; i <= 10; i++) {
            System.out.println(number + " x " + i + " = " + (number * i));
        }
        sc.close();
    }
}