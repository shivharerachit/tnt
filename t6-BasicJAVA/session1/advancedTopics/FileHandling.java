// Implement a simple file I/O operation to read data from a text file.
import java.io.File;
import java.util.Scanner;

public class FileHandling {
    public static void main(String[] args) {
        try {
            File file = new File("example.txt");
            if (file.exists()) {
                System.out.println("File exists: " + file.getAbsolutePath());
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()) {
                    System.out.println(sc.nextLine());
                }
                sc.close();
            } else {
                System.out.println("File does not exist.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}