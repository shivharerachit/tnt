// Create a program to calculate the sum of even numbers from 1 to 10 using a while loop.
import java.util.Scanner;

public class ArrayAverage {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of elements in the array: ");
        int n = sc.nextInt();

        double[] numbers = new double[n];
        double sum = 0;

        System.out.println("Enter " + n + " numbers:");
        for(int i = 0 ; i < n ; i++) {
            numbers[i] = sc.nextDouble();
            sum += numbers[i];
        }

        double average = sum / n;
        System.out.println("The average of the numbers is: " + average);
        sc.close();
    }
}