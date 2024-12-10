import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * This class is the overall GUI frame for the entire .java file.
 * This page shows the entire order history sorted by date, it shows
 * the inventory stock along with the estimated use per month, and
 * it can take an input time frame and display how many times an item
 * on the menu was ordered.
 * 
 * @author David Wang
 */
public class ManagerReport extends JPanel implements ActionListener {
    JTextArea orderHistory;  
    JTextArea inventoryArea;   
    JTextArea salesArea;
    JTextField startDate;
    JTextField endDate;  
    Connection conn;
    CardLayout cardLayout;
    JPanel cardPanel;

    /**
     * Constructor for the entire frame, when initialized it will
     * set the initial layout and determine the connection with the database
     * 
     * @param conn   the database that corresponds to this frame
     */
    public ManagerReport(Connection conn) {
        this.conn = conn;
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.Y_AXIS));  // Arrange components vertically

        // Order History Text
        orderHistory = new JTextArea(10, 30);
        orderHistory.setEditable(false);

        // Inventory Text
        inventoryArea = new JTextArea(10, 30);
        inventoryArea.setEditable(false);

        //Sales Text
        salesArea = new JTextArea(10,30);
        salesArea.setEditable(false);

        // Scroll Bar
        JScrollPane orderScroll = new JScrollPane(orderHistory, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane invenScroll = new JScrollPane(inventoryArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollPane salesScroll = new JScrollPane(salesArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Time Frame
        startDate = new JTextField("YYYY-MM-DD", 10);
        endDate = new JTextField("YYYY-MM-DD", 10);

        // Button to show the sales
        JButton salesButton = new JButton("Sales by Item");
        salesButton.addActionListener(e -> showSalesByItem());

        // Button to show data
        JButton dataButton = new JButton("Show Order History and Inventory");

        // Button for bar chart
        JButton barButton = new JButton("Show Monthly Order Totals");

        // Once button clicked, do showAllData function
        dataButton.addActionListener(e -> showAllData());

        // Same for bar chart
        barButton.addActionListener(event -> {
            BarChart chart = new BarChart(getMonthlyOrderTotals(), e -> cardLayout.show(cardPanel, "Graph"));
            cardPanel.add(chart, "BarChart");
            cardLayout.show(cardPanel, "BarChart");
        });

        graphPanel.add(new JLabel("Order History"));
        graphPanel.add(orderScroll);  // Add the scroll pane for order history
        graphPanel.add(new JLabel("Inventory Details"));
        graphPanel.add(invenScroll);  // Add the scroll pane for inventory
        graphPanel.add(dataButton);
        graphPanel.add(barButton);  // Add the button to show the bar chart
        graphPanel.add(new JLabel("Sales By Item"));
        graphPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        graphPanel.add(startDate);
        graphPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        graphPanel.add(endDate);
        graphPanel.add(salesButton);
        graphPanel.add(salesScroll);

        cardPanel.add(graphPanel, "Graph");
        cardLayout.show(cardPanel, "Graph");

        setLayout(new BorderLayout());
        add(cardPanel, BorderLayout.CENTER);
    }


    /**
     * This method displays the sales data by item within a specified date range.
     * It retrieves the number of orders per menu item from the database and displays it.
     * 
     * Catches SQLException if any error occurs while fetching data from the database.
     */
    public void showSalesByItem() {
        String query = "SELECT mi.Menu_Name, COUNT(oi.Order_Item) AS order_count " +
                       "FROM Menu_Items mi " +
                       "LEFT JOIN Order_Items oi ON mi.Menu_Name = oi.Order_Item " +
                       "LEFT JOIN Order_History oh ON oi.Order_Id = oh.Order_Id " +
                       "WHERE oh.Date_Time >= ? AND oh.Date_Time <= ? " +
                       "GROUP BY mi.Menu_Name " +
                       "ORDER BY order_count DESC";
    
        // Retrieve the start and end date from the GUI text fields
        String startDateText = startDate.getText().trim();
        String endDateText = endDate.getText().trim();
    
        // Validate date inputs before proceeding
        if (startDateText.isEmpty() || endDateText.isEmpty()) {
            salesArea.setText("Please enter both start and end dates.");
            return;
        }
    
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Convert the input strings to java.sql.Date
            java.sql.Date startDate = java.sql.Date.valueOf(startDateText);
            java.sql.Date endDate = java.sql.Date.valueOf(endDateText);
    
            // Set the date range parameters for the query
            pstmt.setDate(1, startDate);
            pstmt.setDate(2, endDate);
    
            // Execute the query
            ResultSet rs = pstmt.executeQuery();
    
            // StringBuilder to display sales data
            StringBuilder salesText = new StringBuilder();
            salesText.append("Sales by Item (").append(startDateText).append(" to ").append(endDateText).append("):\n");
    
            // Iterate through the result set and append item name and order count
            while (rs.next()) {
                String itemName = rs.getString("Menu_Name");  // Retrieve menu item name
                int orderCount = rs.getInt("order_count");    // Retrieve order count
                salesText.append(String.format("%s: %d orders\n", itemName, orderCount));
            }
    
            // Set the salesArea JTextArea with the results
            salesArea.setText(salesText.toString());
    
            rs.close();  // Close the result set
    
        } catch (SQLException e) {
            // If an error occurs, display it in the sales area
            salesArea.setText("Error fetching sales: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    



    

    /**
     * This method fetches both Order History and Inventory data
     * This is linked to a single button, when the button is clicked
     * the data for Order History will show up sorted by date and
     * the Inventory will show up as well
     * 
     * Catches database error if the data cannot be accessed within the databases
     */
    public void showAllData() {
        StringBuilder orderText = new StringBuilder();
        String orderHistoryQuery = "SELECT * FROM order_history ORDER BY Date_Time;";
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(orderHistoryQuery);
            orderText.append("Order History (Sorted by Date):\n");
            while (result.next()) {
                int orderId = result.getInt("Order_Id");
                int customerId = result.getInt("Customer_Id");
                Timestamp dateTime = result.getTimestamp("Date_Time");
                float price = result.getFloat("price");
                orderText.append(String.format("Order ID: %d, Customer ID: %d, Date: %s, Price: %.2f\n",
                        orderId, customerId, dateTime.toString(), price));
            }
        } catch (Exception e) {
            orderText.append("Error accessing Order History.\n");
        }
        orderHistory.setText(orderText.toString());

        // Fetch and display the Inventory in its own JTextArea
        StringBuilder inventoryText = new StringBuilder();
        String inventoryQuery = "SELECT Inven_Name, Stock_Amt, Use_Per_Month FROM Inventory;";
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(inventoryQuery);
            inventoryText.append("Inventory Details (Stock vs Use per Month):\n");
            while (result.next()) {
                String itemName = result.getString("Inven_Name");
                int stockAmt = result.getInt("Stock_Amt");
                int usePerMonth = result.getInt("Use_Per_Month");
                inventoryText.append(String.format("Item: %s, Stock: %d, Used per Month: %d\n", itemName, stockAmt, usePerMonth));
            }
        } catch (Exception e) {
            inventoryText.append("Error accessing Inventory.\n");
        }
        inventoryArea.setText(inventoryText.toString());
    }

    /**
     * This function has the goal of getting the monthly profits over
     * the last year. 
     * This function will always return the total amount of money earned
     * in a given month and will return the dates as well
     * 
     * @return a map corresponding a date to a number (price)
     * Catches SQLException exception if string formatting is incorrect
     */
    public Map<String, Float> getMonthlyOrderTotals() {
        Map<String, Float> monthlyTotals = new LinkedHashMap<>();

        // Get the most recent date from the Order_History table
        String maxDateQuery = "SELECT MAX(Date_Time) AS MaxDate FROM Order_History;";
        Timestamp latestDate = null;
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(maxDateQuery);
            if (result.next()) {
                latestDate = result.getTimestamp("MaxDate");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (latestDate != null) {
            // Generate the last 12 months
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(latestDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH); 

            // Loop to add the last 12 months to the map
            for (int i = 0; i < 12; i++) {
   
                String yearMonth = String.format("%04d-%02d", year, month + 1); 
                monthlyTotals.put(yearMonth, 0f); 
      
                if (month == 0) {
                    month = 11;
                    year--;
                } else {
                    month--;
                }
            }

            // Now fetch the monthly totals
            String query = "SELECT EXTRACT(YEAR FROM Date_Time) AS Year, EXTRACT(MONTH FROM Date_Time) AS Month, " +
                        "SUM(price) AS Total " +
                        "FROM Order_History " +
                        "WHERE Date_Time >= DATE_TRUNC('month', NOW()) - INTERVAL '12 months' " +
                        "GROUP BY Year, Month " +
                        "ORDER BY Year, Month;";

            try {
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(query);

                while (result.next()) {
                    int yearResult = result.getInt("Year");
                    int monthResult = result.getInt("Month");
                    float total = result.getFloat("Total");

                    // Format year and month as YYYY-MM
                    String yearMonth = String.format("%04d-%02d", yearResult, monthResult);
                    monthlyTotals.put(yearMonth, total); 
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return monthlyTotals;
    }



    /**
     * This function handles what happens when a button is clicked
     * Will either enact one of the functions above or will close the jframe
     * 
     * @param e   the action that the button is supposed to do
     */
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s.equals("Close")) {
            JFrame f = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (f != null) {
                f.dispose();
            }
        }
    }
}

/**
 * This function handles the creation and formatting of the bar chart
 * 
 * @author David Wang
 */
class BarChart extends JPanel {
    private final Map<String, Float> data;

    /**
     * The constructor for the bar chart, will set the initial layout for the bar chart
     * 
     * @param data   The data that the bar chart is using
     * @param backListener   This is used to make sure the back button works
     */
    public BarChart(Map<String, Float> data, ActionListener backListener) {
        this.data = data;
        // Create a back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(backListener);
        this.setLayout(null);
        backButton.setBounds(10, 10, 80, 30);
        this.add(backButton);
    }


    /**
     * This function sets the design, labels, and color for the bar chart
     * 
     * @param g  This is the color scheme and design that the chart uses
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int barWidth = (width - 2 * padding) / data.size();
        int maxBarHeight = height - 3 * padding; 

        // Find the maximum value to scale the bars
        float maxTotal = data.values().stream().max(Float::compare).orElse(0f);

        // Title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Revenue per Month", width / 2 - 70, padding / 2);

        // Draw bars
        int x = padding;
        for (Map.Entry<String, Float> entry : data.entrySet()) {
            String yearMonth = entry.getKey(); // This is now in YYYY-MM format
            float total = entry.getValue();
            int barHeight = (int) ((total / maxTotal) * maxBarHeight);

            g2d.setColor(Color.BLUE);
            g2d.fillRect(x, height - padding - barHeight, barWidth - 10, barHeight);

            g2d.setColor(Color.BLACK);
            g2d.drawString(yearMonth, x, height - padding + 20); // Draw year-month on the x-axis
            g2d.drawString(String.format("$%.2f", total), x, height - padding - barHeight - 5);

            x += barWidth;
        }
    }

    /**
     * This function resizes the overall frame
     * 
     * @return the new dimensions that you want
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 400);
    }
}