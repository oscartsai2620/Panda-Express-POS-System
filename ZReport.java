import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The ZReport class generates a Z-Report for sales data.
 * It displays the total sales, payment method counts, unique customers, and repeat customers for the current day.
 */
public class ZReport extends JPanel {
    static DefaultTableModel tableModel;
    static JTable table;
    static Connection conn;
    JScrollPane scrollPane;

    /**
     * Constructs a ZReport panel with the specified database connection.
     *
     * @param conn the database connection
     */
    public ZReport(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Get the current date
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

        JButton reportButton = new JButton("Generate Z-Report for " + formattedDate);
        buttonPanel.add(reportButton);
        add(buttonPanel, BorderLayout.NORTH);

        // Create a non-editable table model
        tableModel = new DefaultTableModel(new String[]{"Metric", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // All cells are non-editable
            }
        };
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);

        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSalesData();
                add(scrollPane, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });
    }

    /**
     * Loads the sales data for the current day and updates the table model.
     */
    public static void loadSalesData() {
        try {
            tableModel.setRowCount(0); // Clear existing data

            // Query to get total sales for the current day
            String sqlTotalSales = 
                "SELECT SUM(price) AS total_sales " +
                "FROM Order_History " +
                "WHERE date_time::date = CURRENT_DATE";

            // Queries to count payment methods for orders placed today
            String totalCardQuery = 
                "SELECT COUNT(*) AS total_card " +
                "FROM Order_History oh " +
                "JOIN customer c ON oh.customer_id = c.customer_id " +
                "WHERE c.pay_method = 'Card' " +
                "AND oh.date_time::date = CURRENT_DATE";

            String totalRetailQuery = 
                "SELECT COUNT(*) AS total_retail " +
                "FROM Order_History oh " +
                "JOIN customer c ON oh.customer_id = c.customer_id " +
                "WHERE c.pay_method = 'Retail Swipe' " +
                "AND oh.date_time::date = CURRENT_DATE";

            String totalDiningDollarsQuery = 
                "SELECT COUNT(*) AS total_dining_dollars " +
                "FROM Order_History oh " +
                "JOIN customer c ON oh.customer_id = c.customer_id " +
                "WHERE c.pay_method = 'Dining Dollars' " +
                "AND oh.date_time::date = CURRENT_DATE";

            String totalCashQuery = 
                "SELECT COUNT(*) AS total_cash " +
                "FROM Order_History oh " +
                "JOIN customer c ON oh.customer_id = c.customer_id " +
                "WHERE c.pay_method = 'Cash' " +
                "AND oh.date_time::date = CURRENT_DATE";

            // Query to count unique customers by email who made a purchase today
            String totalUniqueCustomersQuery = 
                "SELECT COUNT(DISTINCT c.customer_email) AS total_unique_customers " +
                "FROM Order_History oh " +
                "JOIN customer c ON oh.customer_id = c.customer_id " +
                "WHERE oh.date_time::date = CURRENT_DATE";

            // Query to count repeat customers who made a purchase today
            String totalRepeatCustomersQuery = 
                "SELECT COUNT(DISTINCT c.customer_email) AS repeat_customers " +
                "FROM Order_History oh " +
                "JOIN customer c ON oh.customer_id = c.customer_id " +
                "WHERE c.customer_email IN ( " +
                "    SELECT c2.customer_email " +
                "    FROM Order_History oh2 " +
                "    JOIN customer c2 ON oh2.customer_id = c2.customer_id " +
                "    WHERE oh2.date_time::date = CURRENT_DATE " +
                "    GROUP BY c2.customer_email " +
                "    HAVING COUNT(oh2.customer_id) > 1 " +
                ")";

            // Create a statement to execute the queries
            Statement stmt = conn.createStatement();

            // Execute the total sales query
            ResultSet rs = stmt.executeQuery(sqlTotalSales);
            if (rs.next()) {
                double totalSales = rs.getDouble("total_sales");
                // Round totalSales to 2 decimal places
                String formattedTotalSales = String.format("%.2f", totalSales);
                tableModel.addRow(new Object[]{"Total Sales", formattedTotalSales});
            }

            // Execute the total card query
            rs = stmt.executeQuery(totalCardQuery);
            if (rs.next()) {
                int totalCard = rs.getInt("total_card");
                tableModel.addRow(new Object[]{"Total Card Payments", totalCard});
            }

            // Execute the total retail query
            rs = stmt.executeQuery(totalRetailQuery);
            if (rs.next()) {
                int totalRetail = rs.getInt("total_retail");
                tableModel.addRow(new Object[]{"Total Retail Swipe Payments", totalRetail});
            }

            // Execute the total dining dollars query
            rs = stmt.executeQuery(totalDiningDollarsQuery);
            if (rs.next()) {
                int totalDiningDollars = rs.getInt("total_dining_dollars");
                tableModel.addRow(new Object[]{"Total Dining Dollars Payments", totalDiningDollars});
            }

            // Execute the total cash query
            rs = stmt.executeQuery(totalCashQuery);
            if (rs.next()) {
                int totalCash = rs.getInt("total_cash");
                tableModel.addRow(new Object[]{"Total Cash Payments", totalCash});
            }

            // Execute the total unique customers query
            rs = stmt.executeQuery(totalUniqueCustomersQuery);
            if (rs.next()) {
                int totalUniqueCustomers = rs.getInt("total_unique_customers");
                tableModel.addRow(new Object[]{"Total Unique Customers", totalUniqueCustomers});
            }

            // Execute the total repeat customers query
            rs = stmt.executeQuery(totalRepeatCustomersQuery);
            if (rs.next()) {
                int totalRepeatCustomers = rs.getInt("repeat_customers");
                tableModel.addRow(new Object[]{"Total Repeat Customers", totalRepeatCustomers});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error accessing Database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * The main method to run the ZReport application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String database_name = "team_5g_db";
                String database_user = "team_5g";
                String database_password = "thindoe99";
                String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);
                Connection conn = DriverManager.getConnection(database_url, database_user, database_password);

                JFrame frame = new JFrame("Z-Report Generator");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 400);

                frame.add(new ZReport(conn));
                frame.setVisible(true);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}