import java.awt.BorderLayout;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * The menu_items class manages a Java Swing interface to display, add, update, 
 * and delete items from the menu. It interacts with a PostgreSQL database using JDBC 
 * to manage the menu items and prices.
 * 
 * @author Simon Song
 */
public class menu_items extends JPanel implements ActionListener {
    static DefaultTableModel tableModel;
    static JTable table;
    static JTextField itemField, priceField;
    static Connection conn;

     /**
     * Constructor for menu_items class
     * Initializes a Java Swing page with a table of all the menu_items and their prices.
     *
     * @param conn the active database connection to be used for querying the menu items.
     */
    public menu_items(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Item", "Price"}, 0);
        table = new JTable(tableModel);
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        itemField = new JTextField(10);
        priceField = new JTextField(5);
        inputPanel.add(new JLabel("Item:"));
        inputPanel.add(itemField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);

        JButton addButton = new JButton("Add Item");
        JButton updateButton = new JButton("Update Item");
        JButton deleteButton = new JButton("Delete Item");
        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this); 
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        loadMenuData();
    }

    /**
     * Loads menu data from the database into the Swing table.
     * Retrieves all menu items and their prices from the `menu_items` table.
     */
    public static void loadMenuData() {
        try {
            tableModel.setRowCount(0);

            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT menu_name, charge FROM menu_items";
            ResultSet result = stmt.executeQuery(sqlStatement);

            while (result.next()) {
                String itemName = result.getString("menu_name");
                double charge = result.getDouble("charge");
                tableModel.addRow(new Object[]{itemName, charge});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error accessing Database: " + e.getMessage());
        }
    }

    /**
     * Adds a new item to the menu by inserting it into the database.
     * This method reads the item name and price from user input and inserts them into the database.
     * After the item is added successfully, it opens the Add Ingredients window.
     */
    public void addItem() {
        String itemName = itemField.getText().trim();
        String priceText = priceField.getText().trim();

        if (itemName.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter both item name and price.");
            return;
        }

        try {
            double charge = Double.parseDouble(priceText);
            String query = "INSERT INTO menu_items (menu_name, charge) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, itemName);
            pstmt.setDouble(2, charge);
            pstmt.executeUpdate();
            tableModel.addRow(new Object[]{itemName, charge});
            JOptionPane.showMessageDialog(null, "Item added successfully.");

            // Call the method to open the Add Ingredients window
            showAddIngredientsWindow(itemName);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number for the price.");
        }
    }


    /**
     * Deletes the selected menu item from both the table and the database.
     * The user must select an item in the table to delete.
     */
    public void deleteItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an item to delete.");
            return;
        }

        String itemName = (String) tableModel.getValueAt(selectedRow, 0);

        try {
            String query = "DELETE FROM menu_items WHERE menu_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, itemName);
            pstmt.executeUpdate();
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(null, "Item deleted successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates the selected menu item in both the table and the database.
     * The user can update either the item name or the price, or both.
     */
    public void updateItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an item to update.");
            return;
        }

        String itemName = (String) tableModel.getValueAt(selectedRow, 0);
        if (!itemField.getText().trim().isEmpty()) {
            itemName = itemField.getText().trim();
        }
        String priceText = priceField.getText().trim();

        try {
            double charge = Double.parseDouble(priceText);
            String query = "UPDATE menu_items SET charge = ? WHERE menu_name = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setDouble(1, charge);
            pstmt.setString(2, itemName);
            pstmt.executeUpdate();
            tableModel.setValueAt(charge, selectedRow, 1);  // Update the table display
            JOptionPane.showMessageDialog(null, "Item updated successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number for the price.");
        }
    }

    /**
     * Allows user to input any new ingredients that should be added with the addition of a menu items. 
     * The user can add up to as many inventory items that the new item will use.
     */
    public void showAddIngredientsWindow(String itemName) {
        JFrame frame = new JFrame("Add Ingredients");
        frame.setLayout(new BorderLayout());

        InventoryGUI inventoryGUI = new InventoryGUI(conn);

        JButton addMoreButton = new JButton("Add More Ingredients");
        addMoreButton.addActionListener(e -> inventoryGUI.addItem(itemName));

        inventoryGUI.viewInventory();

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addMoreButton);
        buttonPanel.add(closeButton);

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Handles the action performed when a button is clicked (add, update, or delete).
     * 
     * @param e the ActionEvent triggered by the button click
     */
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("Add Item")) {
            addItem();
        } else if (action.equals("Update Item")) {
            updateItem();
        } else if (action.equals("Delete Item")) {
            deleteItem();
        }
        
    }
}