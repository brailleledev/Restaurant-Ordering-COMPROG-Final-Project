import java.util.Scanner;

public class RestaurantOrdering {
	
	// UPDATE: Added dishes with prices on the menu, adjusted the widths of the menu texts

    // ===================== MENU (parallel arrays) =====================
    // The same position in each array belongs to the same dish.
    // Example: position 0 = Chicken Adobo, Main, 180. The menu ID is position + 1.
    private static final String[] ITEM_NAMES = {
        "Chicken Adobo", "Sizzling Pork Sisig", "Sizzling Bangus", "Bulalo with Bone Marrow", // Main IDs 1-4
        "Sinigang Na Hipon", "Pork Sinigang", "Kare-kare", // Main IDs 5-7
        "Afritadang Manok", "Pritong Letchon", "Bicol Express", // Main IDs 8-10
        "Chopsuey", "Pinakbet", // Main IDs 11-12
        "Plain Rice", "Garlic Fried Rice", "Lumpiang Shanghai", "Lumpiang Togue", "Tortang Talong", // Sides IDs 13-17
        "Halo-Halo", "Leche Flan", "Ube Halaya", "Turon", "Buko Pie", "Maja Blanca", // Dessert IDs 18-23
        "Salabat (Hot/Cold)", "Sago't Gulaman", "Calamansi Juice", "Buko Juice", "Kapeng Barako", "Tsokolate (Tablea Hot Choco)", "Taho"// Drinks 24-30
    };
    private static final String[] ITEM_CATEGORIES = {
        "Main", "Main", "Main", "Main", "Main", // Main IDs 1-5
        "Main", "Main", "Main", "Main", "Main", "Main", "Main", // Main IDs 6-12
        "Side", "Side", "Side", "Side", "Side", // Side IDs 13-17
        "Dessert", "Dessert", "Dessert", "Dessert", "Dessert", "Dessert", // Dessert IDs 18-23
        "Drinks", "Drinks", "Drinks", "Drinks", "Drinks", "Drinks", "Drinks" // Drink IDs 24-30
    };
    private static final double[] ITEM_PRICES = {180, 155, 130, 240, 180, 220, // Main Category Prices ID 1-6
    		210, 150, 210, 160, 130, 150, // Main Category Prices ID 7-12
    		50, 45, 80, 50, 70,// Side Category Prices ID 13-17
    		100, 150, 160, 145, 200, 180, // Dessert ID Category Prices 18-23
    		70, 90, 80, 75, 100, 130, 95 // Drink IDs 24-30
    }; 
    		
    
    private static final String[] ITEM_UNITS = { "", "", "", "", "", "", "", "", "", "", "", "",
    		"/ cup", "/ cup", "/ serving", "/ piece", "/ piece",
    		"/ serving", "/ serving", "/ serving", "/ serving", "/ serving", "/ serving",
    		"", "", "", "", "", "", "", ""
    };

    // ===================== THE CUSTOMER'S ORDER =====================
    // How many of each dish was ordered. Same positions as the menu arrays,
    // so orderQuantities[3] is the quantity of ITEM_NAMES[3] (Bulalo with Bone Marrow).
    private static final int[] orderQuantities = new int[ITEM_NAMES.length];

    // ===================== DISCOUNTS =====================
    // Discount types and their rates. Both arrays use the SAME positions:
    // index 0 = None (0%), index 1 = Senior Citizen (20%), and so on.
    private static final String[] DISCOUNT_NAMES = {"None", "Senior Citizen", "PWD", "Employee"};
    private static final double[] DISCOUNT_RATES = {0.00, 0.20, 0.20, 0.10};

    // Registered (saved) IDs. Only IDs found in these arrays get a discount.
    // Keep them in UPPERCASE because the program converts the typed ID to uppercase.
    private static final String[] SENIOR_IDS = {"SC-100234", "SC-100567", "SC-100890"};
    private static final String[] PWD_IDS = {"PWD-200111", "PWD-200222", "PWD-200333"};
    private static final String[] EMPLOYEE_IDS = {"EMP-1001", "EMP-1002", "EMP-1003"};

    // The customer only gets this many tries to enter a valid, registered ID
    private static final int MAX_ATTEMPTS = 3;

    // The discount currently applied to the order (only one can be active at a time)
    private static String discountName = "None";
    private static double discountRate = 0.0;   // 0.20 means 20%
    private static String discountId = "";

    // ===================== PAYMENT =====================
    // Payment methods the customer can choose from (index 0 = Cash on Delivery)
    private static final String[] PAYMENT_METHODS = {"Cash on Delivery", "GCash", "Maya", "Credit/Debit Card"};

    // ===================== MAIN MENU =====================
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== RESTAURANT ORDERING =====");
            System.out.println("1. View menu");
            System.out.println("2. Add item to order");
            System.out.println("3. Remove item from order");
            System.out.println("4. View my order");
            System.out.println("5. Apply discount (Senior / PWD / Employee)");
            System.out.println("6. Checkout");
            System.out.println("7. Exit");
            System.out.print("Choose: ");

            int choice = readInt(scanner);

            switch (choice) {
                case 1:
                    displayMenu();
                    break;

                case 2:
                    addItemToOrder(scanner);
                    break;

                case 3:
                    removeItemFromOrder(scanner);
                    break;

                case 4:
                    displayOrder();
                    break;

                case 5:
                    applyDiscount(scanner);
                    break;

                case 6:
                    checkout(scanner);
                    break;

                case 7:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ===================== MENU METHODS =====================

    // Prints every dish: ID, name, category and price
    private static void displayMenu() {
        System.out.println("\n--- Menu ---");
        System.out.printf("%-3s %-30s %-10s %8s%n", "ID", "Item", "Category", "Price");
        for (int i = 0; i < ITEM_NAMES.length; i++) {
        	System.out.printf("%-3d %-30s %-10s %,8.2f %s%n",
        	        i + 1, ITEM_NAMES[i], ITEM_CATEGORIES[i], ITEM_PRICES[i], ITEM_UNITS[i]);
        }
    }

    // ===================== ORDER METHODS =====================

    // Asks for a menu ID and quantity, then adds it to the order
    private static void addItemToOrder(Scanner scanner) {
        displayMenu();
        System.out.print("Item ID: ");
        int id = readInt(scanner);

        // Validation: the ID must match a dish on the menu
        if (id < 1 || id > ITEM_NAMES.length) {
            System.out.println("Item not found.");
            return;
        }

        System.out.print("Quantity: ");
        int qty = readInt(scanner);
        if (qty <= 0) {
            System.out.println("Quantity must be at least 1.");
            return;
        }

        // If the dish is already in the order, this just increases its quantity
        orderQuantities[id - 1] += qty;
        System.out.println("Added " + qty + " x " + ITEM_NAMES[id - 1]);
    }

    // Removes a dish from the order (sets its quantity back to 0)
    private static void removeItemFromOrder(Scanner scanner) {
        displayOrder();
        if (isOrderEmpty()) {
            return;   // displayOrder() already said the order is empty
        }

        System.out.print("Item ID to remove: ");
        int id = readInt(scanner);

        // The range check comes first, so we never read outside the array
        if (id >= 1 && id <= ITEM_NAMES.length && orderQuantities[id - 1] > 0) {
            orderQuantities[id - 1] = 0;
            System.out.println("Item removed.");
        } else {
            System.out.println("That item is not in your order.");
        }
    }

    // True if no dish has been ordered yet
    private static boolean isOrderEmpty() {
        for (int i = 0; i < orderQuantities.length; i++) {
            if (orderQuantities[i] > 0) {
                return false;
            }
        }
        return true;
    }

    // Total BEFORE the discount
    private static double getSubtotal() {
        double total = 0;
        for (int i = 0; i < ITEM_PRICES.length; i++) {
            total += ITEM_PRICES[i] * orderQuantities[i];
        }
        return total;
    }

    private static boolean hasDiscount() {
        return discountRate > 0;
    }

    private static double getDiscountAmount() {
        return getSubtotal() * discountRate;
    }

    // Total AFTER the discount (this is what the customer pays)
    private static double getTotal() {
        return getSubtotal() - getDiscountAmount();
    }

    private static void clearDiscount() {
        discountName = "None";
        discountRate = 0.0;
        discountId = "";
    }

    // Empties the order and removes the discount (used after checkout)
    private static void clearOrder() {
        for (int i = 0; i < orderQuantities.length; i++) {
            orderQuantities[i] = 0;
        }
        clearDiscount();
    }

    // Shows only the last 4 characters of the ID, e.g. ****5678
    private static String maskId(String id) {
        if (id.length() <= 4) {
            return id;
        }
        return "****" + id.substring(id.length() - 4);
    }

    // Prints the dishes ordered, the subtotal, the discount and the total
    private static void displayOrder() {
        if (isOrderEmpty()) {
            System.out.println("Your order is empty.");
            return;
        }
        System.out.println("\n--- Your Order ---");
        for (int i = 0; i < ITEM_NAMES.length; i++) {
            if (orderQuantities[i] > 0) {
                System.out.printf("%-30s x%-3d %,10.2f%n",              
                        ITEM_NAMES[i], orderQuantities[i], ITEM_PRICES[i] * orderQuantities[i]);
            }
        }
        System.out.printf("%-35s %,10.2f%n", "Subtotal:", getSubtotal());   

        if (hasDiscount()) {
            int percent = (int) Math.round(discountRate * 100);
            System.out.printf("%-35s %,10.2f%n",                        
                    discountName + " (" + percent + "%):", -getDiscountAmount());
            System.out.println("Discount ID: " + maskId(discountId));
        }
        System.out.printf("%-35s %,10.2f%n", "TOTAL:", getTotal());         
    }

    // ===================== DISCOUNT METHODS =====================

    // Lets the customer pick a discount and enter the required ID
    private static void applyDiscount(Scanner scanner) {
        // Guide for the customer
        System.out.println("\n--- Discounts ---");
        System.out.println("You can inquire about your status discounts by using your Senior Citizen,");
        System.out.println("PWD or Employee Identification Card. Enter the ID number printed on your card.");
        System.out.println("Only one discount can be used per order.");

        if (isOrderEmpty()) {
            System.out.println("\nAdd items to your order first.");
            return;
        }

        System.out.println();
        for (int i = 0; i < DISCOUNT_NAMES.length; i++) {
            System.out.printf("%d. %-15s %3.0f%%%n", i + 1, DISCOUNT_NAMES[i], DISCOUNT_RATES[i] * 100);
        }
        System.out.print("Choose discount: ");
        int choice = readInt(scanner);

        // Validation: the choice must match one of the options
        if (choice < 1 || choice > DISCOUNT_NAMES.length) {
            System.out.println("Invalid discount choice.");
            return;
        }

        int index = choice - 1;
        String type = DISCOUNT_NAMES[index];

        // "None" removes any discount that was applied before
        if (index == 0) {
            clearDiscount();
            System.out.println("Discount removed.");
            return;
        }

        // Ask for the ID (limited tries, or the customer types 0 to cancel)
        String id = "";
        boolean verified = false;
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS && !verified) {
            System.out.print("Enter " + type + " ID or 0 to cancel: ");
            id = scanner.nextLine().trim().toUpperCase();

            if (id.equals("0")) {
                System.out.println("Discount cancelled.");
                return;
            }

            attempts++;

            if (!isValidId(type, id)) {
                System.out.println("Invalid ID format.");
            } else if (!isRegisteredId(type, id)) {
                System.out.println("ID not found in our records.");
            } else {
                verified = true;
            }

            if (!verified && attempts < MAX_ATTEMPTS) {
                System.out.println("Attempts left: " + (MAX_ATTEMPTS - attempts));
            }
        }

        if (!verified) {
            System.out.println("Too many failed attempts. Discount not applied.");
            return;
        }

        // Save the discount so displayOrder() and getTotal() can use it
        discountName = type;
        discountRate = DISCOUNT_RATES[index];
        discountId = id;

        showDiscountBanner(type, DISCOUNT_RATES[index]);
        displayOrder();
    }

    // Picks the saved-ID array that matches the discount type
    private static String[] getRegisteredIds(String type) {
        if (type.equals("Senior Citizen")) {
            return SENIOR_IDS;
        } else if (type.equals("PWD")) {
            return PWD_IDS;
        } else {
            return EMPLOYEE_IDS;
        }
    }

    // Searches the saved IDs (linear search) to see if the typed ID is registered
    private static boolean isRegisteredId(String type, String id) {
        String[] registered = getRegisteredIds(type);
        for (int i = 0; i < registered.length; i++) {
            if (registered[i].equals(id)) {
                return true;
            }
        }
        return false;
    }

    // Checks the ID format depending on the discount type
    private static boolean isValidId(String type, String id) {
        if (type.equals("Employee")) {
            // Must start with "EMP-" followed by at least 3 digits
            if (!id.startsWith("EMP-") || id.length() < 7) {
                return false;
            }
            String digits = id.substring(4);
            for (int i = 0; i < digits.length(); i++) {
                if (!Character.isDigit(digits.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        // Senior Citizen and PWD: 6 to 15 characters, letters, digits or dashes only
        if (id.length() < 6 || id.length() > 15) {
            return false;
        }
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '-') {
                return false;
            }
        }
        return true;
    }

    // Prints a banner in the console after a discount is successfully applied
    private static void showDiscountBanner(String type, double rate) {
        int percent = (int) Math.round(rate * 100);
        String message = type.toUpperCase() + " DISCOUNT APPLIED (" + percent + "% OFF)";

        // Build a border line as long as the message (plus the "* " and " *" around it)
        String border = "";
        for (int i = 0; i < message.length() + 4; i++) {
            border += "*";
        }

        System.out.println("\n" + border);
        System.out.println("* " + message + " *");
        System.out.println(border);
    }

    // ===================== CHECKOUT METHODS =====================

    // Checkout steps: delivery address -> payment method -> confirm -> receipt
    private static void checkout(Scanner scanner) {
        if (isOrderEmpty()) {
            System.out.println("Your order is empty.");
            return;
        }

        displayOrder();

        // Reminder in case the customer forgot to use a discount
        if (!hasDiscount()) {
            System.out.println("Tip: choose option 5 first if you have a Senior Citizen, PWD or Employee ID.");
        }

        System.out.println("\n--- Delivery and Payment Details ---");

        // Step 1: delivery address
        String address = askAddress(scanner);
        if (address.isEmpty()) {
            System.out.println("Checkout cancelled. Your order is still saved.");
            return;
        }

        // Step 2: payment method
        int method = askPaymentMethod(scanner);
        if (method == -1) {
            System.out.println("Checkout cancelled. Your order is still saved.");
            return;
        }
        String methodName = PAYMENT_METHODS[method];
        boolean isCash = (method == 0);   // index 0 is Cash on Delivery

        double total = getTotal();
        double cashGiven = 0;
        double change = 0;

        // Cash on Delivery: ask how much cash the customer will pay with
        if (isCash) {
            cashGiven = askCashAmount(scanner, total);
            if (cashGiven < 0) {
                System.out.println("Checkout cancelled. Your order is still saved.");
                return;
            }
            change = cashGiven - total;
        }

        // Step 3: review and confirm
        System.out.println("\n--- Please Review ---");
        System.out.println("Deliver to: " + address);
        System.out.println("Payment:    " + methodName);
        System.out.printf("Total:      %,.2f%n", total);

        String answer;
        do {
            System.out.print("Confirm order? (Y/N): ");
            answer = scanner.nextLine().trim();
        } while (!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N"));

        if (answer.equalsIgnoreCase("N")) {
            System.out.println("Checkout cancelled. Your order is still saved.");
            return;
        }

        // Receipt
        System.out.println("\n========== ORDER CONFIRMED ==========");
        displayOrder();
        if (hasDiscount()) {
            System.out.printf("You saved %,.2f with your discount!%n", getDiscountAmount());
        }
        System.out.println("Deliver to:     " + address);
        System.out.println("Payment method: " + methodName);
        if (isCash) {
            System.out.printf("Cash:           %,.2f%n", cashGiven);
            System.out.printf("Change:         %,.2f%n", change);
        }
        System.out.println("\nThank you for your order! Your food is on the way.");
        clearOrder();
    }

    // Asks for the delivery address. Returns "" if the customer cancels.
    private static String askAddress(Scanner scanner) {
        while (true) {
            System.out.print("Delivery address (house/unit no., street, barangay, city) or 0 to cancel: ");
            String address = scanner.nextLine().trim();

            if (address.equals("0")) {
                return "";
            }
            if (address.length() >= 10) {
                return address;
            }
            System.out.println("Address is too short. Please enter your complete address.");
        }
    }

    // Shows the payment methods. Returns the chosen index, or -1 if cancelled.
    private static int askPaymentMethod(Scanner scanner) {
        System.out.println("\n--- Payment Method ---");
        for (int i = 0; i < PAYMENT_METHODS.length; i++) {
            System.out.println((i + 1) + ". " + PAYMENT_METHODS[i]);
        }
        System.out.println("0. Cancel");

        while (true) {
            System.out.print("Choose payment method: ");
            int choice = readInt(scanner);

            if (choice == 0) {
                return -1;
            }
            if (choice >= 1 && choice <= PAYMENT_METHODS.length) {
                return choice - 1;
            }
            System.out.println("Invalid choice. Please try again.");
        }
    }

    // Asks how much cash will be paid. Must be at least the total. Returns -1 if cancelled.
    private static double askCashAmount(Scanner scanner, double total) {
        System.out.printf("%nTotal to pay: %,.2f%n", total);
        while (true) {
            System.out.print("Cash you will pay with (0 to cancel): ");
            double amount = readDouble(scanner);

            if (amount == 0) {
                return -1;
            }
            if (amount >= total) {
                return amount;
            }
            System.out.println("Amount is not enough.");
        }
    }

    // ===================== INPUT HELPERS =====================

    // Same idea as readInt, but for amounts with decimals (e.g. 500 or 500.50)
    private static double readDouble(Scanner scanner) {
        while (true) {
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (!Double.isNaN(value) && !Double.isInfinite(value)) {
                    return value;
                }
            } catch (NumberFormatException e) {
                // Not a number, so fall through and ask again
            }
            System.out.print("Please enter a valid amount: ");
        }
    }

    // Keeps the program from crashing if the user types letters instead of numbers
    private static int readInt(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a whole number: ");
            }
        }
    }
}