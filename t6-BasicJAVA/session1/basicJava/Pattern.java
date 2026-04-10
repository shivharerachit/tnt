// Use loops to print patterns like a triangle or square.
import java.util.Scanner;

public class Pattern {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter the size of the pattern: ");
		int n = sc.nextInt();

		if (n <= 0) {
			System.out.println("Please enter a positive number.");
			sc.close();
			return;
		}

		System.out.println("\nSquare Pattern:");
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= n; j++) {
				System.out.print("* ");
			}
			System.out.println();
		}

		System.out.println("\nRight-Angled Triangle Pattern:");
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= i; j++) {
				System.out.print("* ");
			}
			System.out.println();
		}

		sc.close();
	}
}
