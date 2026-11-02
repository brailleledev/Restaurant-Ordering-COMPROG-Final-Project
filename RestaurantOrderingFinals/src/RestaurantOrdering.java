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
        return String.format("%-3d %-18s %-10s %,8.2f", id, name, category, price);
    }
}// One line in an order: an item and how many were ordered
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
 
    public void addItem(MenuItem item, int quantity) {// If the item is already in the order, just increase the quantity
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
 
    public double getTotal() {
        double total = 0;
        for (OrderLine line : lines) {
            total += line.getSubtotal();
        }
        return total;
    }public void clear() { lines.clear(); }
 
    public void display() {
        if (lines.isEmpty()) {
            System.out.println("Your order is empty.");
            return;
        }
        System.out.println("\n--- Your Order ---");
        for (OrderLine line : lines) {
            System.out.printf("%-18s x%-3d %,10.2f%n",
                    line.getItem().getName(), line.getQuantity(), line.getSubtotal());
        }
        System.out.printf("%-23s %,10.2f%n", "TOTAL:", getTotal());
    }
}
 
// The restaurant's menu
class Menu {
    private final List<MenuItem> items = new ArrayList<>();
 
    public void addItem(int id, String name, String category, double price) {
        items.add(new MenuItem(id, name, category, price));
    }public MenuItem findItem(int id) {
        for (MenuItem item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
 
    public void display() {
        System.out.println("\n--- Menu ---");
        System.out.printf("%-3s %-18s %-10s %8s%n", "ID", "Item", "Category", "Price");
        for (MenuItem item : items) {
            System.out.println(item);
        }
    }
}
 
public class RestaurantOrdering {
    public static void main(String[] args) {
        Menu menu = new Menu();
        Order order = new Order();
        Scanner scanner = new Scanner(System.in);
 
        // Sample menu - change these to your own dishes
        menu.addItem(1, "Chicken Adobo", "Main", 180);
        menu.addItem(2, "Pork Sinigang", "Main", 200);
        menu.addItem(3, "Garlic Rice", "Side", 40);
        menu.addItem(4, "Halo-Halo", "Dessert", 95);
        menu.addItem(5, "Iced Tea", "Drink", 50);
 
        while (true) {
            System.out.println("\n===== RESTAURANT ORDERING =====");
            System.out.println("1. View menu");
            System.out.println("2. Add item to order");
            System.out.println("3. Remove item from order");
            System.out.println("4. View my order");
            System.out.println("5. Checkout");
            System.out.println("6. Exit");
            System.out.print("Choose: ");
 
            int choice = readInt(scanner);
 
            switch (choice) {
                case 1:
                    menu.display();
                    break;case 2:
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
                        if (order.removeItem(readInt(scanner))) {System.out.println("Item removed.");
                        } else {
                            System.out.println("That item is not in your order.");
                        }
                    }
                    break;
 
                case 4:
                    order.display();
                    break;
 
                case 5:
                    if (order.isEmpty()) {
                        System.out.println("Your order is empty.");
                        break;
                    }
                    order.display();
                    System.out.println("\nThank you for your order!");
                    order.clear();
                    break;
 
                case 6:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;default:
                    System.out.println("Invalid choice.");
            }
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