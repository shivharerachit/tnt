// Create a program to calculate the sum of even numbers from 1 to 10 using a while loop.

public class SumOfEven {
    public static void main(String[] args) {
        int sum = 0;
        int number = 1;

        while (number <= 10) {
            if (number % 2 == 0) {
                sum += number;
            }
            number++;
        }

        System.out.println("The sum of even numbers from 1 to 10 is: " + sum);
    }
}