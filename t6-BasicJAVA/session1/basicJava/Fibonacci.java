// Write a program to print the Fibonacci sequence up to a specified number.
import java.util.Scanner;

public class Fibonacci {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter the upper limit: ");
		int limit = sc.nextInt();

		int first = 0;
		int second = 1;

		System.out.print("Fibonacci sequence up to " + limit + ": ");

		while (first <= limit) {
			System.out.print(first + " ");

			int next = first + second;
			first = second;
			second = next;
		}

		System.out.println();
		sc.close();
	}
}