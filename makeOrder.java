import java.awt.GridLayout;
import java.sql.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.HashMap;
import javax.swing.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.BorderLayout;

/**
  * makeOrder is the class that allows the cashier to create a new order, 
  * select the size, select the base, select the entree, select the sides, 
  * and select the drinks.
  * A makeOrder object is created when the cashier logs in and selects new order. 
  * It creates a window for doing the functionality of creating and sending and 
  * order to the backend.
  * 
  * @author      Yoichiro Nishino
  */

public class makeOrder extends JFrame implements ActionListener {
    private JPanel checkoutPane;
    private JTextArea orderSummary;
    private JLabel totalPriceLabel;

    Connection conn;
    Statement stmt;
    
    //Hash map used here to store order size and number of bases/entrees on each
    HashMap<String, Integer> orderSize = new HashMap<String, Integer>();
    double total_price;
    int num_bases;
    int num_entrees;
    int num_sides;
    int num_drinks;
    String current_size = "";
    Vector<String> bases = new Vector<String>();
    Vector<String> entrees = new Vector<String>();
    Vector<String> sides = new Vector<String>();
    Vector<String> drinks = new Vector<String>();
    Vector<String> allItems = new Vector<String>();

    /**
      * Constructor for creating the makeOrder object and window 
      * No parameters
      * No return
      */
    public makeOrder() {

      //Constructor for cashier interface which gets called by the login page
      try {
        String database_name = "team_5g_db";
        String database_user = "team_5g";
        String database_password = "thindoe99";
        String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);
        conn = DriverManager.getConnection(database_url, database_user, database_password);
        orderSize.put("Bowl", 11);
        orderSize.put("Plate", 12);
        orderSize.put("Bigger Plate", 13);
        orderSize.put("Family Meal", 69);
        orderSize.put("Cub Bowl", 11);
        orderSize.put("Carte", 1);
        checkoutPane = new JPanel(new BorderLayout());
        orderSummary = new JTextArea(20, 30);
        orderSummary.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(orderSummary);
        totalPriceLabel = new JLabel("Total: $0.00");
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        checkoutPane.add(scrollPane, BorderLayout.CENTER);
        checkoutPane.add(totalPriceLabel, BorderLayout.SOUTH);
        checkoutPane.setBorder(BorderFactory.createTitledBorder("Order Summary"));
        orderSize();
          } catch (SQLException e) {
              e.printStackTrace();
          }
        }
    
    /**
      * Creates window for selecting order size from Bowl, Plate, etc.
      * No parameters
      * No return
      */
    public void orderSize() {

      //Main page for first selecting order size, like Bowl, Plate, etc. Redirects here when you click new order
      Vector<String> columnNames = new Vector<String>();
      Vector<Vector<String>> data = new Vector<Vector<String>>();
      String name = "";

      //The format will be the same for a majority of the functions
      try {

        //Establishes the connection and compiles the data into a vector
        Statement stmt = conn.createStatement();
        String sqlStatement = "SELECT Menu_Name, charge FROM menu_items WHERE Menu_Name IN ('Drink', 'Bowl', 'Plate', 'Bigger Plate', 'Family Meal', 'Cub Bowl', 'Carte', 'Side')";
        ResultSet result = stmt.executeQuery(sqlStatement);
        ResultSetMetaData resultmd = result.getMetaData();
        int numColumns = resultmd.getColumnCount();
        for (int i = 1; i <= numColumns; i++) {
          columnNames.add(resultmd.getColumnName(i));
        }
        while (result.next()) {
          Vector<String> currRow = new Vector<String>();
          for (int i = 1; i <= numColumns; i++) {
            currRow.add(result.getString(i));
          }
          data.add(currRow);
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
      
      if (this != null) {

        //Ensures that the previous page is removed
        this.getContentPane().removeAll();
      }

      //Creates a view window with a grid layout, spacing between the elements (8, 8), and a border
      JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
      p.setBorder(BorderFactory.createEmptyBorder(10, 200, 10, 200));

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.add(p, BorderLayout.CENTER);
      mainPanel.add(checkoutPane, BorderLayout.EAST);

      this.setContentPane(mainPanel);

      for (Vector<String> row : data) {
          name = row.get(0);
          String price = row.get(1);
          String formattedPrice = String.format("%.2f", Double.parseDouble(price));
          JButton button = new JButton(name + " - $" + formattedPrice);

          //The action command acts as an ID of sorts for which button was clicked
          button.setActionCommand("Size " + name);
          button.addActionListener(this);
          button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
          button.setPreferredSize(new Dimension(400, 200));
          button.setBackground(Color.decode("#D61927"));
          button.setForeground(Color.WHITE);
          button.setFont(new Font("Arial", Font.BOLD, 20));
          p.add(button);
      }

      //Sets the view window to the max size of the screen
      this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
      this.add(p);
      this.setExtendedState(JFrame.MAXIMIZED_BOTH);
      this.setVisible(true);
    }
    
    /**
      * Creates window for selecting the base from White Rice, Brown Rice, etc.
      * No parameters
      * No return
      */
    public void pickBase() {

      //Picks fried rice, white rice, etc.
      if (this != null){
        this.getContentPane().removeAll();
      }
      Vector<String> columnNames = new Vector<String>();
      Vector<Vector<String>> data = new Vector<Vector<String>>();
      String name = "";
      try {
        Statement stmt = conn.createStatement();
        String sqlStatement = "SELECT Menu_Name, charge FROM menu_items WHERE Menu_Name IN ('Fried Rice', 'White Rice', 'Brown Rice', 'Chow Mein', 'Super Greens')";
        ResultSet result = stmt.executeQuery(sqlStatement);
        ResultSetMetaData resultmd = result.getMetaData();
        int numColumns = resultmd.getColumnCount();
        for (int i = 1; i <= numColumns; i++) {
          columnNames.add(resultmd.getColumnName(i));
        }
        while (result.next()) {
          Vector<String> currRow = new Vector<String>();
          for (int i = 1; i <= numColumns; i++) {
            currRow.add(result.getString(i));
          }
          data.add(currRow);
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }

      JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
      p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.add(p, BorderLayout.CENTER);
      mainPanel.add(checkoutPane, BorderLayout.EAST);

      this.setContentPane(mainPanel);
      
      for (Vector<String> row : data) {
          name = row.get(0);
          String price = row.get(1);
          String formattedPrice = String.format("%.2f", Double.parseDouble(price));
          JButton button = new JButton(name + " - $" + formattedPrice);
          button.setActionCommand("Base " + name);
          button.addActionListener(this);
          button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
          button.setPreferredSize(new Dimension(400, 200));
          button.setBackground(Color.decode("#D61927"));
          button.setForeground(Color.WHITE);
          button.setFont(new Font("Arial", Font.BOLD, 20));
          p.add(button);
      }

      this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
      this.add(p);
      this.setExtendedState(JFrame.MAXIMIZED_BOTH);
      this.setVisible(true);
    }
    
    /**
      * Creates window for selecting entree from Orange Chicken, Sesame Chicken, etc.
      * No parameters
      * No return
      */
    public void pickEntree() {

      //Same format, this just picks the entrees
      if (this != null) {
        this.getContentPane().removeAll();
      }
      Vector<String> columnNames = new Vector<String>();
      Vector<Vector<String>> data = new Vector<Vector<String>>();
      String name = "";
      try {
        Statement stmt = conn.createStatement();
        String sqlStatement = "SELECT Menu_Name, charge FROM menu_items WHERE Menu_Name NOT IN ('Fried Rice', 'White Rice', 'Brown Rice', 'Chow Mein', 'Super Greens', 'Bowl', 'Plate', 'Bigger Plate', 'Family Meal', 'Cub Bowl', 'Carte', 'Drink', 'Spring Roll', 'Chicken Egg Roll', 'Apple Pie', 'Cream Cheese Rangoon', 'Dessert', 'Side')";
        ResultSet result = stmt.executeQuery(sqlStatement);
        ResultSetMetaData resultmd = result.getMetaData();
        int numColumns = resultmd.getColumnCount();
        for (int i = 1; i <= numColumns; i++) {
          columnNames.add(resultmd.getColumnName(i));
        }
        while (result.next()) {
          Vector<String> currRow = new Vector<String>();
          for (int i = 1; i <= numColumns; i++) {
            currRow.add(result.getString(i));
          }
          data.add(currRow);
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
      JPanel p = new JPanel(new GridLayout(0, 4, 8, 8));
      p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.add(p, BorderLayout.CENTER);
      mainPanel.add(checkoutPane, BorderLayout.EAST);

      this.setContentPane(mainPanel);
      
      for (Vector<String> row : data) {
          name = row.get(0);
          String price = row.get(1);
          String formattedPrice = String.format("%.2f", Double.parseDouble(price));
          JButton button = new JButton(name + " - $" + formattedPrice);
          button.setActionCommand("Entree " + name);
          button.addActionListener(this);
          button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
          button.setPreferredSize(new Dimension(400, 200));
          button.setBackground(Color.decode("#D61927"));
          button.setForeground(Color.WHITE);
          button.setFont(new Font("Arial", Font.BOLD, 20));
          p.add(button);
      }

      this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
      this.add(p);
      this.setExtendedState(JFrame.MAXIMIZED_BOTH);
      this.setVisible(true);
    }
    
    /**
      * Creates window for selecting a side from Spring Roll, Chicken Egg Roll, etc.
      * No parameters
      * No return
      */
    public void pickSides() {

      //Picks sides on order
      if (this != null) {
        this.getContentPane().removeAll();
      }
      Vector<String> columnNames = new Vector<String>();
      Vector<Vector<String>> data = new Vector<Vector<String>>();
      String name = "";
      try {
        Statement stmt = conn.createStatement();
        String sqlStatement = "SELECT Menu_Name, charge FROM menu_items WHERE Menu_Name IN ('Spring Roll', 'Chicken Egg Roll', 'Apple Pie', 'Cream Cheese Rangoon')";
        ResultSet result = stmt.executeQuery(sqlStatement);
        ResultSetMetaData resultmd = result.getMetaData();
        int numColumns = resultmd.getColumnCount();
        for (int i = 1; i <= numColumns; i++) {
          columnNames.add(resultmd.getColumnName(i));
        }
        while (result.next()) {
          Vector<String> currRow = new Vector<String>();
          for (int i = 1; i <= numColumns; i++) {
            currRow.add(result.getString(i));
          }
          data.add(currRow);
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
      JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
      p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

      for (Vector<String> row : data) {
          name = row.get(0);
          String price = row.get(1);
          String formattedPrice = String.format("%.2f", Double.parseDouble(price));
          JButton button = new JButton(name + " - $" + formattedPrice);
          button.setActionCommand("Sides " + name);
          button.addActionListener(this);
          button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
          button.setPreferredSize(new Dimension(400, 200));
          button.setBackground(Color.decode("#D61927"));
          button.setForeground(Color.WHITE);
          button.setFont(new Font("Arial", Font.BOLD, 20));
          p.add(button);
      }

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.add(p, BorderLayout.CENTER);
      mainPanel.add(checkoutPane, BorderLayout.EAST);

      this.setContentPane(mainPanel);

      this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
      this.add(p);
      this.setExtendedState(JFrame.MAXIMIZED_BOTH);
      this.setVisible(true);
    }
    
    /**
      * Creates window for selecting a drink to add to order
      * No parameters
      * No return
      */
    public void pickDrinks() {

      //Picks drinks on order
      if (this != null) {
        this.getContentPane().removeAll();
      }
      Vector<String> columnNames = new Vector<String>();
      Vector<Vector<String>> data = new Vector<Vector<String>>();
      String name = "";
      try {
        Statement stmt = conn.createStatement();
        String sqlStatement = "SELECT Menu_Name, charge FROM menu_items WHERE Menu_Name IN ('Drink')";
        ResultSet result = stmt.executeQuery(sqlStatement);
        ResultSetMetaData resultmd = result.getMetaData();
        int numColumns = resultmd.getColumnCount();
        for (int i = 1; i <= numColumns; i++) {
          columnNames.add(resultmd.getColumnName(i));
        }
        while (result.next()) {
          Vector<String> currRow = new Vector<String>();
          for (int i = 1; i <= numColumns; i++) {
            currRow.add(result.getString(i));
          }
          data.add(currRow);
        }
      } catch (Exception e) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
      JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
      p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

      for (Vector<String> row : data) {
          name = row.get(0);
          String price = row.get(1);
          String formattedPrice = String.format("%.2f", Double.parseDouble(price));
          JButton button = new JButton(name + " - $" + formattedPrice);
          button.setActionCommand("Drink " + name);
          button.addActionListener(this);
          button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
          button.setPreferredSize(new Dimension(400, 200));
          button.setBackground(Color.decode("#D61927"));
          button.setForeground(Color.WHITE);
          button.setFont(new Font("Arial", Font.BOLD, 20));
          p.add(button);
      }

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.add(p, BorderLayout.CENTER);
      mainPanel.add(checkoutPane, BorderLayout.EAST);

      this.setContentPane(mainPanel);

      this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
      this.add(p);
      this.setExtendedState(JFrame.MAXIMIZED_BOTH);
      this.setVisible(true);
    }
    
    public void redirect() {
      if (this != null) {
        this.getContentPane().removeAll();
      }

      JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
      p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

      JButton b = new JButton("Checkout");
      b.addActionListener(this);
      b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      b.setPreferredSize(new Dimension(400, 200));
      b.setBackground(Color.decode("#D61927"));
      b.setForeground(Color.WHITE);
      b.setFont(new Font("Arial", Font.BOLD, 20));
      p.add(b);
      JButton c = new JButton("Add more");
      c.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      c.setPreferredSize(new Dimension(400, 200));
      c.setBackground(Color.decode("#D61927"));
      c.setForeground(Color.WHITE);
      c.setFont(new Font("Arial", Font.BOLD, 20));
      c.addActionListener(this);
      p.add(c);

      JPanel mainPanel = new JPanel(new BorderLayout());
      mainPanel.add(p, BorderLayout.CENTER);
      mainPanel.add(checkoutPane, BorderLayout.EAST);

      this.setContentPane(mainPanel);

      this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
      this.add(p);
      this.setExtendedState(JFrame.MAXIMIZED_BOTH);
      this.setVisible(true);
    }

    /**
      * Creates window for checking out and submitting the order to the database, updating the tables. Also asks for customer information, email and payment method
      * No parameters
      * No return
      */
    public void checkout() {

      //Checkout page. Takes all global variables with stores order data and pushes it to database
      if (this != null) {
        this.getContentPane().removeAll();
      }
      JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
      p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

      Vector<String> selectedItems = new Vector<String>();

      //Takes all the different order data from this current customer and adds it to selectedItems, the total vector
      selectedItems.add(current_size);
      if (!bases.isEmpty()) {
        selectedItems.addAll(bases);
      }
      if (!entrees.isEmpty()) {
        selectedItems.addAll(entrees);
      }
      if (!sides.isEmpty()) {
        selectedItems.addAll(sides);
      }
      if (!allItems.isEmpty()) {
        selectedItems.addAll(allItems);
      }
      
      for (String item : selectedItems) {
        try {
          Statement stmt = conn.createStatement();
          String name = item;
          String sqlStatement = "SELECT charge FROM menu_items WHERE Menu_Name = '" + name + "'";
          ResultSet result = stmt.executeQuery(sqlStatement);
          double price = 0;
          if (result.next()) {
            price = result.getDouble("charge");
          }

          //If it is a size, we don't want to indent it for formatting of size followed by indented items part of it
          if (name.equals("Bowl") || name.equals("Plate") || name.equals("Bigger Plate") || name.equals("Cub Bowl") || name.equals("Family Meal") || name.equals("Carte") || name.equals("Drink") || name.equals("Spring Roll") || name.equals("Chicken Egg Roll") || name.equals("Apple Pie") || name.equals("Cream Cheese Rangoon") || name.equals("Dessert")) {
            JLabel label = new JLabel(String.format(name + " - $%.2f", price));
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            label.setPreferredSize(new Dimension(400, 200));
            label.setOpaque(true);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            p.add(label);
          }
          else {
            JLabel label = new JLabel(String.format("                " + name + " - $%.2f", price));
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            label.setPreferredSize(new Dimension(400, 200));
            label.setOpaque(true);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            p.add(label);
          }
        }
        catch (Exception e) {
          JOptionPane.showMessageDialog(null,"Error accessing Database.");
        }
      }
      
      
      JLabel label = new JLabel(String.format("Total Price: $%.2f", total_price));
      label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      label.setPreferredSize(new Dimension(400, 200));
      label.setOpaque(true);
      label.setFont(new Font("Arial", Font.BOLD, 20));
      p.add(label);

      JButton b = new JButton("New Order");
      b.addActionListener(this);
      b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      b.setPreferredSize(new Dimension(400, 200));
      b.setBackground(Color.decode("#D61927"));
      b.setForeground(Color.WHITE);
      b.setFont(new Font("Arial", Font.BOLD, 20));
      p.add(b);

      JButton c = new JButton("Back to Login");
      c.addActionListener(this);
      c.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      c.setPreferredSize(new Dimension(400, 200));
      c.setBackground(Color.decode("#D61927"));
      c.setForeground(Color.WHITE);
      c.setFont(new Font("Arial", Font.BOLD, 20));
      p.add(c);

      //Customer with different options for payment
      String customer_email = JOptionPane.showInputDialog("Enter your email address: ");
      String[] paymentOptions = {"Card", "Cash", "Dining Dollars", "Retail Swipe"};
      String customer_payment = JOptionPane.showInputDialog(null, "Select payment method: ", "Payment Method", JOptionPane.QUESTION_MESSAGE, null, paymentOptions, paymentOptions[0]).toString();
      int customer_id = 1;
      try {
        Statement stmt = conn.createStatement();
        String sqlStatement = "INSERT INTO customer (Customer_Email, Pay_Method, Paid_Amt) VALUES ('" + customer_email + "', '" + customer_payment + "', '" + total_price + "') RETURNING customer_id;";
        ResultSet res = stmt.executeQuery(sqlStatement);
        if (res.next()) {
          customer_id = res.getInt("customer_id");
        }
      }
      catch (Exception e) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }

      //Entering new order
      DateTimeFormatter dtthis= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      LocalDateTime current_time = LocalDateTime.now();
      String currentTime = dtthis.format(current_time);
      int order_id = 1;
      try {
        Statement stmt = conn.createStatement();
        String sqlStatement = "INSERT INTO order_history (Customer_Id, Date_Time, Price) VALUES ('" + customer_id + "', '" + currentTime + "', '" + total_price + "') RETURNING order_id;";
        ResultSet res = stmt.executeQuery(sqlStatement);
        if (res.next()) {
          order_id = res.getInt("order_id");
        }
      }
      catch (Exception e) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }

      //Entering order items
      for (String item : selectedItems) {
        try {

            //retrieving price
            double price = 0;
            Statement stmt = conn.createStatement();
            String sqlStatement = "SELECT charge FROM menu_items WHERE menu_name = ('" + item + "');";
            stmt.executeQuery(sqlStatement);
            ResultSet res = stmt.executeQuery(sqlStatement);
            if (res.next()) {
                price = res.getInt("charge");
            }
            String sqlInsert = "INSERT INTO order_items (Order_Id, order_item, price) VALUES ('" + order_id + "', '" + item + "', '" + price + "');";
            stmt.executeUpdate(sqlInsert);
        }
        catch (Exception e) {
          JOptionPane.showMessageDialog(null,"Error accessing Database.");
        }
      }

      //Changing inventory
      for (String item : selectedItems) {
        try {
            Statement stmt = conn.createStatement();
            String menuStatement = "SELECT menu_id FROM menu_items WHERE menu_name = ('" + item + "');";
            ResultSet res = stmt.executeQuery(menuStatement);
            int menu_id = 0;
            if (res.next()) {
                menu_id = res.getInt("menu_id");
            }
            Vector<String> ingredients = new Vector<String>();
            String inventory_menu_statement = "SELECT inventory_name FROM inventory_menu WHERE menu_id = ('" + menu_id + "');";
            ResultSet inventory_res = stmt.executeQuery(inventory_menu_statement);
            while (inventory_res.next()) {
                ingredients.add(inventory_res.getString("inventory_name"));
            }
            for (String ingredient : ingredients) {
                String inventoryStatement = "UPDATE inventory SET stock_amt = stock_amt - 1 WHERE inven_name = ('" + ingredient + "');";
                stmt.executeUpdate(inventoryStatement);
            }
        }
        catch (SQLException e) {
          JOptionPane.showMessageDialog(null, e.getMessage());
        }
      }
      this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
      this.add(p);
      this.setExtendedState(JFrame.MAXIMIZED_BOTH);
      this.setVisible(true);
    }
    
    /**
      * Redirects to a new window based on the previous button clicked. Also updates the global variables and ensures proper number of bases and sides are selected for each size
      * @param       e the action event that was triggered by a button click
      * No return
      */
    public void actionPerformed(ActionEvent e){
      String command = e.getActionCommand();
      if (command.startsWith("Size ") || command.startsWith("Base ") || 
          command.startsWith("Entree ") || command.startsWith("Sides ") || 
          command.startsWith("Drinks ")) {
          String item = command.substring(command.indexOf(" ") + 1);
          addToOrder(item);
          updateCheckoutPane();
      }

      //Redirects based on button clicked
      if (command.equals("Close")) {
        System.exit(0);
      }
      else if (command.substring(0,4).equals("Size")) {
        if (!command.substring(5, command.length()).equals("Side") && !command.substring(5, command.length()).equals("Drink")) {
            current_size = command.substring(5);
            num_bases = orderSize.get(current_size)/10;
            num_entrees = orderSize.get(current_size)%10;
            if(command.substring(5, command.length()).equals("Carte")) {
                pickEntree();
            }
            else {
                pickBase();
            }
            updateCheckoutPane();
        }
        else {
            current_size = command.substring(5);
            num_bases = 0;
            num_entrees = 0;
            if(command.substring(5, command.length()).equals("Side")) {
                num_sides = 1;
                pickSides();
            }
            else {
                num_drinks = 1;
                pickDrinks();
            }
            updateCheckoutPane();
        }
      }
      else if (command.substring(0,4).equals("Base")) {
        if (num_bases > 0) {
          bases.add(command.substring(5));
          num_bases--;
          if(num_bases == 0) {
            pickEntree();
          }
          updateCheckoutPane();
        }
        else {
            JOptionPane.showMessageDialog(this, "Maximum number of bases exceeded.", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
      else if (command.substring(0,6).equals("Entree")) {
        if (num_entrees > 0) {
          entrees.add(command.substring(7));
          num_entrees--;
          if (num_entrees == 0) {
            redirect();
          }
          updateCheckoutPane();
        }
        else {
            JOptionPane.showMessageDialog(this, "Maximum number of entrees exceeded.", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
      else if (command.substring(0,5).equals("Sides")) {
        if (num_sides > 0) {
          sides.add(command.substring(6));
          num_sides--;
          if (num_sides == 0) {
            redirect();
          }
          updateCheckoutPane();
        }
        else {
            JOptionPane.showMessageDialog(this, "Maximum number of sides exceeded.", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
      else if (command.substring(0,5).equals("Drink")) {
        if (num_drinks > 0) {
          num_drinks--;
          if (num_drinks == 0) {
            redirect();
          }
          updateCheckoutPane();
        }
        else {
            JOptionPane.showMessageDialog(this, "Maximum number of drinks exceeded.", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
      else if (command.equals("Back to Login")) {
        login_page login = new login_page();
        login.setVisible(true);
        this.dispose();
      }
      else if (command.equals("Checkout")) {
        if (num_entrees > 0 || num_bases > 0 || num_sides > 0 || num_drinks > 0) {
            JOptionPane.showMessageDialog(this, "Not enough items selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            checkout();
        }
      }
      else if (command.equals("New Order")) {

        //When a new order comes in, must ensure everything is cleared so previous items aren't displayed
        allItems.clear();
        bases.clear();
        entrees.clear();
        sides.clear();
        drinks.clear();
        total_price = 0;
        num_bases = 0;
        num_entrees = 0;
        num_sides = 0;
        num_drinks = 0;
        current_size = "";
        orderSummary.setText("");
        updateCheckoutPane();
        orderSize();
      }
      else {
        if (num_entrees > 0 || num_bases > 0 || num_sides > 0 || num_drinks > 0) {
            JOptionPane.showMessageDialog(this, "Not enough items selected.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            allItems.add(current_size);
            if (!bases.isEmpty()) {
              allItems.addAll(bases);
            }
            if (!entrees.isEmpty()) {
              allItems.addAll(entrees);
            }
            if (!sides.isEmpty()) {
              allItems.addAll(sides);
            }
            if (!drinks.isEmpty()) {
              allItems.addAll(drinks);
            }
            bases.clear();
            entrees.clear();
            sides.clear();
            current_size = "";
            updateCheckoutPane();
            orderSize();
        }
      }
    }

    /**
      * Adds the price of the item to the total price of the order
      * @param       item the item to be added to the order
      * No return
      */
    public void addToOrder(String item) {
      try {
        Statement stmt = conn.createStatement();
        String name = item;
        String sqlStatement = "SELECT charge FROM menu_items WHERE Menu_Name = '" + name + "'";
        ResultSet result = stmt.executeQuery(sqlStatement);
        if (result.next()) {
          total_price += result.getDouble("charge");
        }
      }
      catch (Exception e) {
        JOptionPane.showMessageDialog(null,"Error accessing Database.");
      }
    }
    
    /**
      * Updates the checkout pane with the current order summary and total price
      * No parameters
      * No return
      */
    public void updateCheckoutPane() {
        String current_item = "";
        Vector<String> selectedItems = new Vector<String>();
        selectedItems.addAll(allItems);
        if(current_size != ""){
          selectedItems.add(current_size);
        }
        if (!bases.isEmpty()) {
            selectedItems.addAll(bases);
        }
        if (!entrees.isEmpty()) {
            selectedItems.addAll(entrees);
        }
        if (!sides.isEmpty()) {
            selectedItems.addAll(sides);
        }
        if (!drinks.isEmpty()) {
            selectedItems.addAll(drinks);
        }
        for (String item : selectedItems) {
            if(item.equals("Bowl") || item.equals("Plate") || item.equals("Bigger Plate") || item.equals("Family Meal") || item.equals("Cub Bowl") || item.equals("Carte") || item.equals("Side") || item.equals("Drink")) {
                current_item += item + "\n";
            }
            else {
                current_item += "    " + item + "\n";
            }
        }
        orderSummary.setText(current_item);
        totalPriceLabel.setText(String.format("Total: $%.2f", total_price));
    }
    
    /**
      * Main function to allow window for orders to be created by this file itself
      * @param       args the command line arguments
      * No return
      */
    public static void main(String[] args) {

      //Main function for testing purposes
        makeOrder newOrder = new makeOrder();
    }
}
