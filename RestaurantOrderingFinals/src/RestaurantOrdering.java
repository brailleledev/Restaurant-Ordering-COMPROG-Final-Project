import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// One item on the menu
class MenuItem {
    private final int id;
    private final String name;
    private final String category;
    private final double price;

    public MenuItem(int id, String name, String category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return String.format("%-3d %-25s %-10s %,8.2f", id, name, category, price);
    }
}

// One line in an order: an item and how many were ordered
class OrderLine {
    private final MenuItem item;
    private int quantity;

    public OrderLine(MenuItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public MenuItem getItem() { return item; }
    public int getQuantity() { return quantity; }
    public void addQuantity(int amount) { this.quantity += amount; }
    public double getSubtotal() { return item.getPrice() * quantity; }
}

// The customer's current order
class Order {
    private final List<OrderLine> lines = new ArrayList<>();

    // Discount details (only one discount can be active at a time)
    private String discountName = "None";
    private double discountRate = 0.0;   // 0.20 means 20%
    private String discountId = "";

    public void addItem(MenuItem item, int quantity) {
        // If the item is already in the order, just increase the quantity
        for (OrderLine line : lines) {
            if (line.getItem().getId() == item.getId()) {
                line.addQuantity(quantity);
                return;
            }
        }
        lines.add(new OrderLine(item, quantity));
    }

    public boolean removeItem(int itemId) {
        return lines.removeIf(line -> line.getItem().getId() == itemId);
    }

    public boolean isEmpty() { return lines.isEmpty(); }

    // Total BEFORE the discount
    public double getSubtotal() {
        double total = 0;
        for (OrderLine line : lines) {
            total += line.getSubtotal();
        }
        return total;
    }

    // Discount methods
    public void setDiscount(String name, double rate, String id) {
        discountName = name;
        discountRate = rate;
        discountId = id;
    }

    public void clearDiscount() {
        discountName = "None";
        discountRate = 0.0;
        discountId = "";
    }

    public boolean hasDiscount() { return discountRate > 0; }

    public double getDiscountAmount() { return getSubtotal() * discountRate; }

    // Total AFTER the discount (this is what the customer pays)
    public double getTotal() { return getSubtotal() - getDiscountAmount(); }

    // Shows only the last 4 characters of the ID, e.g. ****5678
    private String maskId(String id) {
        if (id.length() <= 4) {
            return id;
        }
        return "****" + id.substring(id.length() - 4);
    }

    public void clear() {
        lines.clear();
        clearDiscount();
    }

    public void display() {
        if (lines.isEmpty()) {
            System.out.println("Your order is empty.");
            return;
        }
        System.out.println("\n--- Your Order ---");
        for (OrderLine line : lines) {
            System.out.printf("%-25s x%-3d %,10.2f%n",
                    line.getItem().getName(), line.getQuantity(), line.getSubtotal());
        }
        System.out.printf("%-30s %,10.2f%n", "Subtotal:", getSubtotal());

        if (hasDiscount()) {
            int percent = (int) Math.round(discountRate * 100);
            System.out.printf("%-30s %,10.2f%n",
                    discountName + " (" + percent + "%):", -getDiscountAmount());
            System.out.println("Discount ID: " + maskId(discountId));
        }
        System.out.printf("%-30s %,10.2f%n", "TOTAL:", getTotal());
    }
}

// The restaurant's menu
class Menu {
    private final List<MenuItem> items = new ArrayList<>();

    public void addItem(int id, String name, String category, double price) {
        items.add(new MenuItem(id, name, category, price));
    }

    public MenuItem findItem(int id) {
        for (MenuItem item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public void display() {
        System.out.println("\n--- Menu ---");
        System.out.printf("%-3s %-25s %-10s %8s%n", "ID", "Item", "Category", "Price");
        for (MenuItem item : items) {
            System.out.println(item);
        }
    }
}

public class RestaurantOrdering {

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

    // Payment methods the customer can choose from (index 0 = Cash on Delivery)
    private static final String[] PAYMENT_METHODS = {"Cash on Delivery", "GCash", "Maya", "Credit/Debit Card"};

    public static void main(String[] args) {
        Menu menu = new Menu();
        Order order = new Order();
        Scanner scanner = new Scanner(System.in);

        // Sample menu - change these to your own dishes
        menu.addItem(1, "Chicken Adobo", "Main", 180);
        menu.addItem(2, "Sizzling Pork Sisig", "Main", 155);
        menu.addItem(3, "Sizzling Bangus", "Main", 130);
        menu.addItem(4, "Bulalo with Bone Marrow", "Main", 240);
        menu.addItem(5, "Sinigang Na Hipon", "Main", 180);
        menu.addItem(6, "Pork Sinigang", "Main", 220);
        menu.addItem(7, "Garlic Rice", "Side", 30);
        menu.addItem(8, "Halo-Halo", "Dessert", 95);
        menu.addItem(9, "Iced Tea", "Drink", 50);

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
                    menu.display();
                    break;

                case 2:
                    menu.display();
                    System.out.print("Item ID: ");
                    MenuItem item = menu.findItem(readInt(scanner));
                    if (item == null) {
                        System.out.println("Item not found.");
                        break;
                    }
                    System.out.print("Quantity: ");
                    int qty = readInt(scanner);
                    if (qty <= 0) {
                        System.out.println("Quantity must be at least 1.");
                        break;
                    }
                    order.addItem(item, qty);
                    System.out.println("Added " + qty + " x " + item.getName());
                    break;

                case 3:
                    order.display();
                    if (!order.isEmpty()) {
                        System.out.print("Item ID to remove: ");
                        if (order.removeItem(readInt(scanner))) {
                            System.out.println("Item removed.");
                        } else {
                            System.out.println("That item is not in your order.");
                        }
                    }
                    break;

                case 4:
                    order.display();
                    break;

                case 5:
                    applyDiscount(order, scanner);
                    break;

                case 6:
                    checkout(order, scanner);
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

    // Lets the customer pick a discount and enter the required ID
    private static void applyDiscount(Order order, Scanner scanner) {
        // Guide for the customer
        System.out.println("\n--- Discounts ---");
        System.out.println("You can inquire about your status discounts by using your Senior Citizen,");
        System.out.println("PWD or Employee Identification Card. Enter the ID number printed on your card.");
        System.out.println("Only one discount can be used per order.");

        if (order.isEmpty()) {
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
            order.clearDiscount();
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

        order.setDiscount(type, DISCOUNT_RATES[index], id);
        showDiscountBanner(type, DISCOUNT_RATES[index]);
        order.display();
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

    // Checkout steps: delivery address -> payment method -> confirm -> receipt
    private static void checkout(Order order, Scanner scanner) {
        if (order.isEmpty()) {
            System.out.println("Your order is empty.");
            return;
        }

        order.display();

        // Reminder in case the customer forgot to use a discount
        if (!order.hasDiscount()) {
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

        double total = order.getTotal();
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
        order.display();
        if (order.hasDiscount()) {
            System.out.printf("You saved %,.2f with your discount!%n", order.getDiscountAmount());
        }
        System.out.println("Deliver to:     " + address);
        System.out.println("Payment method: " + methodName);
        if (isCash) {
            System.out.printf("Cash:           %,.2f%n", cashGiven);
            System.out.printf("Change:         %,.2f%n", change);
        }
        System.out.println("\nThank you for your order! Your food is on the way.");
        order.clear();
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