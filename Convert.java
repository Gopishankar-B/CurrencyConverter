

import java.sql.*;

public class Convert {
    private final String dbUrl = "jdbc:sqlite:currency_rates.db";

    // Constructor - Initializes the SQLite database and creates the table if not exists
    public Convert() {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {

            // Create the table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS currency_rates (" +
                                    "currency_code TEXT PRIMARY KEY, " +
                                    "rate REAL NOT NULL)";
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    // Add or update a currency with its rate in the database
    public void addCurrency(String currency, double rate) {
        String upsertSQL = "INSERT INTO currency_rates (currency_code, rate) " +
                           "VALUES (?, ?) " +
                           "ON CONFLICT(currency_code) DO UPDATE SET rate = excluded.rate";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(upsertSQL)) {
            pstmt.setString(1, currency);
            pstmt.setDouble(2, rate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding currency: " + e.getMessage());
        }
    }

    // Convert currency
    public double convert(String fromCurrency, String toCurrency, double amount) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT rate FROM currency_rates WHERE currency_code = ?")) {

            // Get the rate for the source currency
            pstmt.setString(1, fromCurrency);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) throw new IllegalArgumentException(fromCurrency + " is not supported.");
            double fromRate = rs.getDouble("rate");

            // Get the rate for the target currency
            pstmt.setString(1, toCurrency);
            rs = pstmt.executeQuery();
            if (!rs.next()) throw new IllegalArgumentException(toCurrency + " is not supported.");
            double toRate = rs.getDouble("rate");

            // Convert and return the result
            double amountInINR = amount / fromRate;
            return amountInINR * toRate;
        } catch (SQLException e) {
            throw new RuntimeException("Error converting currency: " + e.getMessage());
        }
    }

    // Display available currencies from the database
    public void displayCurrencies() {
        String querySQL = "SELECT currency_code, rate FROM currency_rates";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No currencies available.");
                return;
            }

            System.out.println("Available currencies and their rates (to INR):");
            while (rs.next()) {
                System.out.printf("%s: %.2f%n", rs.getString("currency_code"), rs.getDouble("rate"));
            }
        } catch (SQLException e) {
            System.err.println("Error displaying currencies: " + e.getMessage());
        }
    }
}
