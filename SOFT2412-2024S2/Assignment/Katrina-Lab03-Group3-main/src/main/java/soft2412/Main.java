package soft2412;

import soft2412.Currency.Currency;
import soft2412.Currency.CurrencyManager;
import soft2412.Currency.Rate;
import soft2412.Users.User;
import soft2412.Displayer.ConversionTable;

import javax.management.InstanceNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SignUpManager signupManager = new SignUpManager();
        LoginManager loginManager = new LoginManager();
        CurrencyManager currencyManager = new CurrencyManager();

        if (args != null && args.length > 0) {
            if (args[0].equals("Testing")){
                currencyManager.setpaths("src/TestDatabase/Currencies","Popular.txt");
            }
        }

        System.out.println("Loading currency data");
        currencyManager.loadCurrencies();
        System.out.println("Currency data loaded");

        ConversionTable currency_table = new ConversionTable(currencyManager);

        User currentUser = null;

        while (true) {
            if (currentUser == null) {
                System.out.println("Choose an option:");
                System.out.println("1. Sign Up");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                int choice;
                String input = scanner.nextLine();

                try {
                    choice = Integer.valueOf(input);
                }
                catch (Exception e){
                    choice = -1;
                }


                switch (choice) {
                    case 1:
                        System.out.print("Enter Username for signup: ");
                        String newUsername = scanner.nextLine();
                        System.out.print("Enter Password for signup: ");
                        String newPassword = scanner.nextLine();
                        signupManager.createUser(newUsername, newPassword);
                        break;
                    case 2:
                        System.out.print("Enter Username for login: ");
                        String username = scanner.nextLine();
                        System.out.print("Enter Password for login: ");
                        String password = scanner.nextLine();
                        currentUser = loginManager.checkUser(username, password);
                        if (currentUser != null) {
                            System.out.println("Login successful!");
                        }
                        break;
                    case 3:
                        System.out.println("Exiting...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid option, please try again.");
                }
            } else {
                // logged in menu
                System.out.println("Logged in as: " + currentUser.getUsername());
                System.out.println("1. Log Out");
                System.out.println("2. Currency Conversion");
                System.out.println("3. Display Popular Currencies");
                System.out.println("4. Print Summary of Conversion Rate History");
                if (currentUser.isAdmin()) {
                    System.out.println("5. Update Popular Currencies");
                    System.out.println("6. Add New Exchange Rate");
                    System.out.println("7. Add New Currency Type");
                }
                
                int userChoice = -1;  // set to default invalid choice
                while (true) {
                    System.out.print("Enter choice: ");
                    try {
                        userChoice = scanner.nextInt();
                        scanner.nextLine();  // clear the newline
                        break;  // break out of the loop as the valid input get
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input! Please enter a number.");
                        scanner.nextLine();  // continue until get the valid input
                    }
                }

                switch (userChoice) {
                    case 1:
                        currentUser = null;
                        System.out.println("Logged out successfully.");
                        break;
                    case 2:
                        // logic for currency conversion
                        try {
                            System.out.print("Enter amount to convert: ");
                            double convertAmount = scanner.nextDouble();
                            if(convertAmount <= 0){
                                System.out.println("Amount must be greater than 0.");
                                break;
                            }
                            scanner.nextLine();  // Consume newline
                            System.out.print("Enter currency from: ");
                            String fromCurrency = scanner.nextLine().toUpperCase();
                            System.out.print("Enter currency to: ");
                            String toCurrency = scanner.nextLine().toUpperCase();
                            ArrayList<Object> resultArray = currencyManager.convertCurrency(convertAmount, fromCurrency, toCurrency);
                            double convertedAmount = (double) resultArray.get(0);
                            String resultSymbol = (String) resultArray.get(1);
                            if(convertedAmount == -1.0){
                                System.out.println("Currency Conversion Failed! One or more of the entered currencies doesn't exist, or there's no existing exchange rate between them.");
                                break;
                            }
                            System.out.println(Double.toString(convertAmount) + " " + fromCurrency + " = " + Double.toString(convertedAmount) + " " + resultSymbol );
                        }
                        catch (Exception e){
                            System.out.println("Invalid input!");
                        }
                        break;
                    case 3:
//                        display popular currencies
                        currency_table.showDisplay();
                        break;
                    case 4:
//                        prints conversion rate history:
                        try {
                            System.out.print("Enter the currency you want to view (From): ");
                            String fromCurrencyHistory = scanner.nextLine().toUpperCase();

                            System.out.print("Enter the currency you want to view (To): ");
                            String toCurrencyHistory = scanner.nextLine().toUpperCase();

                            System.out.print("Enter the start date (YYYY-MM-DD): ");
                            LocalDate startDate = LocalDate.parse(scanner.nextLine());

                            System.out.print("Enter the end date (YYYY-MM-DD): ");
                            LocalDate endDate = LocalDate.parse(scanner.nextLine());


                            // Fetch the currency from the CurrencyManager
                            Currency fromCurrencyObj = currencyManager.getCurrency(fromCurrencyHistory);

                            if (fromCurrencyObj != null) {
                                double latestRate = fromCurrencyObj.getLatestRate(toCurrencyHistory);
                                if (latestRate != -1.0) {
                                    Rate rateObject = fromCurrencyObj.getRates().get(toCurrencyHistory);
                                    String history = rateObject.getHistory(startDate, endDate, fromCurrencyHistory, toCurrencyHistory);
                                    if(history == ""){
                                        System.out.println("No conversion rate history found between the specified dates!");
                                    }
                                    System.out.println(history);
                                } else {
                                    System.out.println("No conversion rate history available for " + fromCurrencyHistory + " to " + toCurrencyHistory);
                                }
                            } else {
                                System.out.println("Currency " + fromCurrencyHistory + " does not exist.");
                            }
                        }
                        catch (Exception e){
                            System.out.println("Invalid input!");
                        }

                        break;
                    case 5:
                        if (currentUser.isAdmin()) {
                            currencyManager.setPopularCurrencies();
                        }
                        break;
                    case 6:
                        if (currentUser.isAdmin()) {
                            //adding new exchange rates
                            currencyManager.processMenuAddRate();
                        }
                        break;
                    case 7:
                        if (currentUser.isAdmin()) {
                            try {
                                //adding new currency types, the original is done by draw0960, need merge to resolve
                                System.out.print("Enter the new currency name: ");
                                String newCurrencyName = scanner.nextLine().toUpperCase();
                                System.out.print("Enter an initial, pre-existing currency to convert to: ");
                                String initialCurrency = scanner.nextLine().toUpperCase();
                                System.out.print("Enter conversion rate between the new currency and the initial Currency: ");
                                double rate = Double.parseDouble(scanner.nextLine());
                                System.out.print("Enter the date (yyyy-MM-dd): ");
                                LocalDate date = LocalDate.parse(scanner.nextLine());
                                currencyManager.addCurrency(newCurrencyName, initialCurrency, rate, date);
                            }
                            catch (InstanceNotFoundException e){
                                System.out.println("Initial currency doesn't exist!");
                            }
                            catch (Exception e){
                                System.out.println("Invalid input!");
                            }
                        }
                        break;
                    default:
                        System.out.println("Invalid option, please try again.");
                }
            }
        }
    }
}
