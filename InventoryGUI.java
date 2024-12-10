import java.sql.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.String;

/**
 * Class to generate Inventory GUI.
 * This class will generate the GUI for the inventory panel. The inventory panel will display the inventory in a JTable
 * and display the items that need to be restocked. The user can add, update, and delete items from the inventory.
 * The inventory table will be refreshed everytime an item is added, updated, or deleted.
 * @author Group 5G Oscar Tsai
 */
public class InventoryGUI extends JPanel implements ActionListener {
    private Connection conn;
    public JPanel topPanel;
    public JButton deleteButton = new JButton("Delete Item");
    public JButton addButton = new JButton("Add Item");
    public JButton updateButton = new JButton("Update Item");
    public JButton viewUsageButton = new JButton("View Inventory Usage");

    /** * Constructor.
     * Construct the GUI for the inventory panel.
     * 
     * @param conn Connection to the database
    */ 
    public InventoryGUI(Connection conn) {
        this.conn = conn;  // Set the connection passed from the main GUI
        setLayout(new BorderLayout());  // Set the layout for this panel

        // Initialize the top panel and add buttons
        topPanel = new JPanel();
        addAllButtons(topPanel, this);

        // Display the inventory
        viewInventory();

        addButton.addActionListener(this);
        updateButton.addActionListener(this);
        deleteButton.addActionListener(this);
        viewUsageButton.addActionListener(this);
    }


    /**
     * Method to generate the view for inventory in a JTable.
     * This method will display the inventory in a JTable and display the items that need to be restocked.
     * This method will be loaded everytime the InventoryGUI is loaded, and will be refreshed when an item is added, updated, or deleted.
     * Catches exception if there is an error retrieving data from the database. Most likely due to a SQL syntax error.
     */
    public void viewInventory() {
        try {
            Statement stmt = conn.createStatement();

            // Wrapper for the table and header
            JPanel inventoryPanel = new JPanel();
            JPanel restockPanel = new JPanel();
            inventoryPanel.setLayout(new BorderLayout());
            restockPanel.setLayout(new BorderLayout());

            // Generate inventory table
            String allInventoryQuery = "SELECT * FROM inventory ORDER BY Inventory_Id";
            ResultSet result = stmt.executeQuery(allInventoryQuery);

            DefaultTableModel inventoryModel = new DefaultTableModel(
                new Object[]{"Inventory_ID", "Inventory_Name", "Stock_Amount", "Use_Per_Month", "Price", "Employee_ID"}, 0
            );

            while (result.next()) {
                Object[] row = new Object[6];
                row[0] = result.getInt("Inventory_Id");
                row[1] = result.getString("Inven_Name");
                row[2] = result.getInt("Stock_Amt");
                row[3] = result.getInt("Use_Per_Month");
                row[4] = result.getDouble("Price");
                row[5] = result.getInt("Employee_Id");
                inventoryModel.addRow(row);
            }

            JTable inventoryTable = new JTable(inventoryModel);
            JScrollPane inventoryScrollPane = new JScrollPane(inventoryTable);

            // Generate Restock Table
            String restockQuery = "SELECT * FROM inventory WHERE Stock_Amt < Use_Per_Month ORDER BY Inventory_Id";
            ResultSet rs = stmt.executeQuery(restockQuery);

            DefaultTableModel restockModel = new DefaultTableModel(
                new Object[]{"Inventory_ID", "Inventory_Name", "Stock_Amount", "Use_Per_Month", "Price", "Employee_ID"}, 0
            );

            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getInt("Inventory_Id");
                row[1] = rs.getString("Inven_Name");
                row[2] = rs.getInt("Stock_Amt");
                row[3] = rs.getInt("Use_Per_Month");
                row[4] = rs.getDouble("Price");
                row[5] = rs.getInt("Employee_Id");
                restockModel.addRow(row);
            }

            JTable restockTable = new JTable(restockModel);
            JScrollPane restockScrollPane = new JScrollPane(restockTable);
            restockScrollPane.setPreferredSize(new Dimension(400, 100));

            // Create and set up a header to the inventory table
            JLabel inventoryLabel = new JLabel("Inventory");
            JLabel restockLabel = new JLabel("Need to Restock");
            inventoryLabel.setFont(new Font("Arial", Font.BOLD, 20));
            restockLabel.setFont(new Font("Arial", Font.BOLD, 20));
            inventoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
            restockLabel.setHorizontalAlignment(SwingConstants.CENTER);
            inventoryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            restockLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Add the header to the wrappers
            inventoryPanel.add(inventoryLabel, BorderLayout.NORTH);
            inventoryPanel.add(inventoryScrollPane, BorderLayout.CENTER);
            restockPanel.add(restockLabel, BorderLayout.NORTH);
            restockPanel.add(restockScrollPane, BorderLayout.CENTER);

            // Add the inventory panel to the frame
            this.add(inventoryPanel, BorderLayout.CENTER);
            this.add(restockPanel, BorderLayout.EAST);

            // Refresh the frame
            this.revalidate();
            this.repaint();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving inventory data.");
            e.printStackTrace();
        }
    }

    /**
     * Method to add an item to the inventory. Whenever the "Add Item" button is clicked,
     * this method will pop up dialog boxes to prompt the user for the item details.
     * A new item will be inserted into the database with the details provided by the user.
     * The inventory table will be refreshed (run viewInventory()) to display the new item after the item is added.
     * Catches exception if there is an error inserting data into the database. Most likely due to a SQL syntax error.
     */
    public void addItem() {
        // Prompt the user for item details
        String inventoryName = JOptionPane.showInputDialog("Enter Inventory Name:");
        String stockAmountStr = JOptionPane.showInputDialog("Enter Stock Amount:");
        String usePerMonthStr = JOptionPane.showInputDialog("Enter Use Per Month:");
        String priceStr = JOptionPane.showInputDialog("Enter Price:");

        try {

            // Insert new item into the database
            String sqlInsert = "INSERT INTO inventory (Inven_Name, Stock_Amt, Use_Per_Month, Price, Employee_Id) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement pstmt = conn.prepareStatement(sqlInsert);
            pstmt.setString(1, inventoryName);
            pstmt.setInt(2, Integer.parseInt(stockAmountStr));
            pstmt.setInt(3, Integer.parseInt(usePerMonthStr));
            pstmt.setDouble(4, Double.parseDouble(priceStr));
            pstmt.setInt(5, 1); // Employee_Id placeholder, update as necessary
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Item added successfully!");

            // Refresh the inventory table
            this.removeAll();
            addAllButtons(topPanel, this);
            viewInventory();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding item.");
            e.printStackTrace();
        }
    }

    /**
     * Method to update an existing item in the inventory for restocking purposes. 
     * Whenever the "Update Item" button is clicked, this method will pop up dialog boxes to prompt the user
     * for the item details including the amount that is getting restock and the employee that is restocking. 
     * The stock amount of the item will be updated in the database with the details provided by the user.
     * The inventory table will be refreshed (run viewInventory()) to display the updated item after the item is updated.
     * Catches exception if there is an error updating data in the database. Most likely due to a SQL syntax error.
     */

    public void addItem(String itemName) {
        // Prompt the user for item details
        String inventoryName = JOptionPane.showInputDialog("Enter Inventory Name:");
        String stockAmountStr = JOptionPane.showInputDialog("Enter Stock Amount:");
        String usePerMonthStr = JOptionPane.showInputDialog("Enter Use Per Month:");
        String priceStr = JOptionPane.showInputDialog("Enter Price:");

        try {

            // Insert new item into the database
            String sqlInsert = "INSERT INTO inventory (Inven_Name, Stock_Amt, Use_Per_Month, Price, Employee_Id) VALUES (?, ?, ?, ?, ?) returning inventory_id;";
            PreparedStatement pstmt = conn.prepareStatement(sqlInsert);
            pstmt.setString(1, inventoryName);
            pstmt.setInt(2, Integer.parseInt(stockAmountStr));
            pstmt.setInt(3, Integer.parseInt(usePerMonthStr));
            pstmt.setDouble(4, Double.parseDouble(priceStr));
            pstmt.setInt(5, 1); // Employee_Id placeholder, update as necessary
            ResultSet inventory_res = pstmt.executeQuery();
            inventory_res.next();
            int inventory_id = inventory_res.getInt("inventory_id");
            Statement stmt = conn.createStatement();
            String menuStatement = "SELECT menu_id FROM menu_items WHERE menu_name = ('" + itemName + "');";
            ResultSet res = stmt.executeQuery(menuStatement);
            res.next();
            int menu_id = res.getInt("menu_id");
            String secondSqlInsert = "INSERT INTO inventory_menu (inventory_name, menu_id, inventory_id) VALUES ('" + inventoryName + "', '" + menu_id + "', '" + inventory_id + "');";
            stmt.executeUpdate(secondSqlInsert);


            JOptionPane.showMessageDialog(null, "Item added successfully!");

            // Refresh the inventory table
            this.removeAll();
            addAllButtons(topPanel, this);
            viewInventory();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding item.");
            e.printStackTrace();
        }
    }


    /**
     * Method to update an existing item in the inventory for restocking purposes. 
     * Whenever the "Update Item" button is clicked, this method will pop up dialog boxes to prompt the user
     * for the item details including the amount that is getting restock and the employee that is restocking. 
     * The stock amount of the item will be updated in the database with the details provided by the user.
     * The inventory table will be refreshed (run viewInventory()) to display the updated item after the item is updated.
     * Catches exception if there is an error updating data in the database. Most likely due to a SQL syntax error.
     */
    public void updateItem() {
        String inventoryIdStr = JOptionPane.showInputDialog("Enter Inventory ID to update:");
        String restockAmountStr = JOptionPane.showInputDialog("Enter Amount Restocking:");
        String employeeIdStr = JOptionPane.showInputDialog("Enter Employee ID:");

        try {
            // Update the item's stock amount
            String sqlUpdate = "UPDATE inventory SET Stock_Amt = Stock_Amt + ?, Employee_Id = ? WHERE Inventory_Id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sqlUpdate);
            pstmt.setInt(1, Integer.parseInt(restockAmountStr));
            pstmt.setInt(2, Integer.parseInt(employeeIdStr));
            pstmt.setInt(3, Integer.parseInt(inventoryIdStr));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Item updated successfully!");

            // Refresh the inventory table
            this.removeAll();
            addAllButtons(topPanel, this);
            viewInventory();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating item.");
            e.printStackTrace();
        }
    }

    /**
     * Method to delete an item from the inventory. Whenever the "Delete Item" button is clicked,
     * this method will pop up dialog boxes to prompt the user for the item ID to delete.
     * The item will be deleted from the database with the details provided by the user.
     * The inventory table will be refreshed (run viewInventory()) to display the updated inventory after the item is deleted.
     * Catches exception if there is an error deleting data from the database. Most likely due to a SQL syntax error.
     */
    public void deleteItem() {
        String inventoryIdStr = JOptionPane.showInputDialog("Enter Inventory ID to delete:");

        try {
            // Delete the item
            String sqlDelete = "DELETE FROM inventory WHERE Inventory_Id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sqlDelete);
            pstmt.setInt(1, Integer.parseInt(inventoryIdStr));
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Item deleted successfully!");

            // Refresh the inventory table
            this.removeAll();
            addAllButtons(topPanel, this);
            viewInventory();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting item.");
            e.printStackTrace();
        }
    }

    String startDateStr = "";
    String endDateStr = "";
    /**
     * Method to view the inventory usage.
     * This method will display the inventory usage given a time range or search item in a JTable.
     * The user can search for a specific item or view the inventory usage within a time range.
     * The inventory usage table will be refreshed everytime the user searches for a specific item or time range.
     * Catches exception if there is an error retrieving data from the database. Most likely due to a SQL syntax error.
     * @param isSearch If the user is searching for a specific item.
     */
    public void viewInventoryUsage(boolean isSearch){
        try {
            Statement stmt = conn.createStatement();
            String searchItem = "";
            if(isSearch){
                searchItem = JOptionPane.showInputDialog("Enter Item Name:");
                if(searchItem == null || searchItem.equals("")){
                    JOptionPane.showMessageDialog(null, "Invalid search item. Please enter an item name.");
                    return;
                }
            }
            else{
                startDateStr = JOptionPane.showInputDialog("Enter Start Date (YYYY-MM-DD):");
                endDateStr = JOptionPane.showInputDialog("Enter End Date (YYYY-MM-DD):");
            }
    
            if ((!isValidDate(startDateStr) || !isValidDate(endDateStr)) && !isSearch) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please enter the date in YYYY-MM-DD format.");
                return;
            }
    
            // Create a new JFrame to display inventory usage
            JFrame usageFrame = new JFrame("Inventory Usage");
            usageFrame.setSize(600, 400);
            usageFrame.setLayout(new BorderLayout());
    
            // Wrapper for the table and header
            JPanel usagePanel = new JPanel();
            usagePanel.setLayout(new BorderLayout());
    
            // Generate inventory usage table
            String allInventoryUsageQuery = "";
            if(!isSearch){
                allInventoryUsageQuery = 
                "SELECT inventory_id, inventory_name, Amount_Used_From_Start_to_Finish " +
                "FROM ( " +
                "SELECT inventory_name, COUNT(inventory_name) AS Amount_Used_From_Start_to_Finish " +
                "FROM ( " +
                "SELECT OH.order_id, OH.customer_id, DATE(OH.date_time) AS order_date, OH.price AS order_price, " +
                "MI.menu_id AS menu_id, OI.order_item AS order_item, OI.price AS item_price " +
                "FROM Order_History OH " +
                "JOIN Order_Items OI ON OH.order_id = OI.order_id " +
                "JOIN Menu_Items MI ON OI.order_item = MI.menu_name) AS t1 " +
                "JOIN Inventory_Menu IM ON t1.menu_id = IM.menu_id " +
                "WHERE order_date >= '" + startDateStr + "' " +
                "AND order_date <= '" + endDateStr + "' " +
                "GROUP BY inventory_name) AS t2 " +
                "JOIN Inventory ON t2.inventory_name = inven_name;";
            }
            else{
                allInventoryUsageQuery = 
                "SELECT inventory_id, inventory_name, Amount_Used_From_Start_to_Finish " +
                "FROM ( " +
                "SELECT inventory_name, COUNT(inventory_name) AS Amount_Used_From_Start_to_Finish " +
                "FROM ( " +
                "SELECT OH.order_id, OH.customer_id, DATE(OH.date_time) AS order_date, OH.price AS order_price, " +
                "MI.menu_id AS menu_id, OI.order_item AS order_item, OI.price AS item_price " +
                "FROM Order_History OH " +
                "JOIN Order_Items OI ON OH.order_id = OI.order_id " +
                "JOIN Menu_Items MI ON OI.order_item = MI.menu_name) AS t1 " +
                "JOIN Inventory_Menu IM ON t1.menu_id = IM.menu_id " +
                "WHERE order_date >= '" + startDateStr + "' " +
                "AND order_date <= '" + endDateStr + "' " +
                "AND inventory_name = '" + searchItem + "' " +
                "GROUP BY inventory_name) AS t2 " +
                "JOIN Inventory ON t2.inventory_name = inven_name;";
            }
    
            ResultSet result = stmt.executeQuery(allInventoryUsageQuery);
    
            DefaultTableModel inventoryUsageModel = new DefaultTableModel(
                new Object[]{"Inventory_ID", "Inventory_Name", "Amount_Used_From_Start_to_Finish "}, 0
            );
    
            while (result.next()) {
                Object[] row = new Object[3];
                row[0] = result.getInt("Inventory_Id");
                row[1] = result.getString("Inventory_Name");
                row[2] = result.getInt("amount_used_from_start_to_finish");
                inventoryUsageModel.addRow(row);
            }
    
            JTable inventoryUsageTable = new JTable(inventoryUsageModel);
            JScrollPane inventoryUsageScrollPane = new JScrollPane(inventoryUsageTable);
    
            // Create and set up a header to the inventory table
            JLabel inventoryUsageLabel = new JLabel("Inventory Usage");
            inventoryUsageLabel.setFont(new Font("Arial", Font.BOLD, 20));
            inventoryUsageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            inventoryUsageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
            // Add the header and table to the usagePanel
            usagePanel.add(inventoryUsageLabel, BorderLayout.NORTH);
            usagePanel.add(inventoryUsageScrollPane, BorderLayout.CENTER);
    
            // Add the usagePanel to the new frame
            usageFrame.add(usagePanel, BorderLayout.CENTER);
    
            // Create a back button to close the window
            JButton backButton = new JButton("Back");
            backButton.setBounds(10, 10, 80, 30);
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    usageFrame.dispose(); 
                }
            });
            JButton searchButton = new JButton("Search");
            searchButton.setBounds(10, 10, 80, 30);
            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewInventoryUsage(true);
                }
            });
    
            // Add the back button to the bottom of the frame
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(backButton);
            buttonPanel.add(searchButton);
            

            usageFrame.add(buttonPanel, BorderLayout.NORTH);
    
            // Set the frame to be visible
            usageFrame.setVisible(true);
    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    /**
     * Method to check if the date is in the correct format.
     * This method will check if the date is in the correct format of "YYYY-MM-DD".
     * @param date The date to be checked.
     * @return boolean True if the date is in the correct format, false otherwise.
     */
    public boolean isValidDate(String date) {
        if (date == null || date.length() != 10) {
            return false;
        }

        String[] dateParts = date.split("-");
        if (dateParts.length != 3) {
            return false;
        }

        if (dateParts[0].length() != 4 || dateParts[1].length() != 2 || dateParts[2].length() != 2) {
            return false;
        }

        return true;
    }

    /**
     * Method to refresh the inventory table.
     * This method will remove all the components from the panel and add the buttons and inventory table again.
     * This method will be called everytime an item is added, updated, or deleted.
     */
    public void refreshInventory(){
        this.removeAll();
        addAllButtons(topPanel, this);
        viewInventory();
    }
    /**
     * Method to listen to the buttons on the top panel.
     * This method will listen to the "Add Item", "Update Item", and "Delete Item" buttons.
     * When the buttons are clicked, the corresponding methods will be called.
     * @param e The event that is triggered by the button click.
     */
    // Action listener for button clicks
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Add Item")) {
            addItem();
        }
        else if (s.equals("Update Item")) {
            updateItem();
        }
        else if (s.equals("Delete Item")) {
            deleteItem();
        }
        else if(s.equals("View Inventory Usage")){
            viewInventoryUsage(false);
        }
    }

    /**
     * Method to add buttons to the top panel.
     * This method will add the "Add Item", "Update Item", and "Delete Item" buttons to the top panel.
     * The top panel will be added to the parent panel, and the panels will be refreshed every time the tables are updated.
     * 
     * @param topPanel The panel that the buttons will be added to.
     * @param parentPanel The parent panel that the top panel will be added to.
     */
    // Method to add buttons to the top panel
    public void addAllButtons(JPanel topPanel, JPanel parentPanel) {
        topPanel.removeAll();
        topPanel.setLayout(new GridLayout(1, 6));
        topPanel.add(new JLabel()); // Placeholder label
        topPanel.add(addButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);
        topPanel.add(viewUsageButton);
        topPanel.add(new JLabel()); // Placeholder label

        parentPanel.add(topPanel, BorderLayout.NORTH);
        parentPanel.revalidate();
        parentPanel.repaint();
    }
}