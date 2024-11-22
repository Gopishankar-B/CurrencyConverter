import java.util.Scanner;

public class CurrencyConverter {

    public static void main(String[] args) {
        // Initialize the Convert object (this will set up the database)
        Convert converter = new Convert();

        // Create a Scanner object for user input
        Scanner scanner = new Scanner(System.in);
        boolean keepRunning = true;

        while (keepRunning) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Add Currency");
            System.out.println("2. Display Available Currencies");
            System.out.println("3. Convert Currency");
            System.out.println("4. Exit");

            // Read user choice
            int choice = scanner.nextInt();

            switch (choice) {
                case 1: // Add Currency
                    System.out.println("Enter currency code (e.g., USD, EUR, GBP):");
                    String currency = scanner.next();
                    System.out.println("Enter the exchange rate to INR:");
                    double rate = scanner.nextDouble();
                    converter.addCurrency(currency, rate);
                    System.out.println("Currency added successfully!");
                    break;

                case 2: // Display Available Currencies
                    converter.displayCurrencies();
                    break;

                case 3: // Convert Currency
                    System.out.println("Enter the currency you want to convert from:");
                    String fromCurrency = scanner.next();
                    System.out.println("Enter the currency you want to convert to:");
                    String toCurrency = scanner.next();
                    System.out.println("Enter the amount to convert:");
                    double amount = scanner.nextDouble();
                    try {
                        double convertedAmount = converter.convert(fromCurrency, toCurrency, amount);
                        System.out.printf("%f %s = %f %s%n", amount, fromCurrency, convertedAmount, toCurrency);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 4: // Exit
                    keepRunning = false;
                    System.out.println("Exiting the program.");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}

