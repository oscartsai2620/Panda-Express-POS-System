import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;

/**
 * The XReport class generates a sales report showing sales per hour for the current day,
 * including totals for different payment methods.
 * It interacts with a PostgreSQL database using JDBC to retrieve sales data and displays 
 * the results in a Java Swing table.
 * 
 * The report can be run as often as desired without side effects.
 *
 * @author Simon
 */
public class XReport extends JPanel {
    static DefaultTableModel tableModel;
    static JTable table;
    static Connection conn;
    JScrollPane scrollPane;

    /**
     * Constructor for XReport class
     * Initializes the Swing interface, adds a button to generate the report, and sets up
     * an event listener for displaying the report in the same window.
     * 
     * @param conn the active database connection to be used for querying sales data.
     */
    public XReport(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton reportButton = new JButton("Generate X-Report");
        buttonPanel.add(reportButton);
        add(buttonPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Hour", "Total Sales", "Card", "Retail Swipe", "Dining Dollars", "Cash"}, 0);
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
     * Loads hourly sales data from the database into the Swing table,
     * including totals for different payment methods.
     */
    public static void loadSalesData() {
        try {
            tableModel.setRowCount(0);

            String sql = "SELECT EXTRACT(HOUR FROM oh.Date_Time) AS hour, " +
                         "SUM(oh.price) AS total_sales, " +
                         "SUM(CASE WHEN c.Pay_Method = 'Card' THEN oh.price ELSE 0 END) AS card_total, " +
                         "SUM(CASE WHEN c.Pay_Method = 'Retail Swipe' THEN oh.price ELSE 0 END) AS retail_swipe_total, " +
                         "SUM(CASE WHEN c.Pay_Method = 'Dining Dollars' THEN oh.price ELSE 0 END) AS dining_dollars_total, " +
                         "SUM(CASE WHEN c.Pay_Method = 'Cash' THEN oh.price ELSE 0 END) AS cash_total " +
                         "FROM Order_History oh " +
                         "JOIN Customer c ON oh.Customer_Id = c.Customer_Id " +
                         "WHERE oh.Date_Time >= CURRENT_DATE " +
                         "GROUP BY EXTRACT(HOUR FROM oh.Date_Time) " +
                         "ORDER BY hour";

            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);

            while (result.next()) {
                int hour = result.getInt("hour");
                double totalSales = result.getDouble("total_sales");
                double cardTotal = result.getDouble("card_total");
                double retailSwipeTotal = result.getDouble("retail_swipe_total");
                double diningDollarsTotal = result.getDouble("dining_dollars_total");
                double cashTotal = result.getDouble("cash_total");

                tableModel.addRow(new Object[]{
                    String.format("%02d:00", hour),
                    String.format("%.2f", totalSales),
                    String.format("%.2f", cardTotal),
                    String.format("%.2f", retailSwipeTotal),
                    String.format("%.2f", diningDollarsTotal),
                    String.format("%.2f", cashTotal)
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error accessing Database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main method to run the XReport application with a button to generate the report.
     * Initializes the database connection and sets up the Swing UI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String database_name = "team_5g_db";
                String database_user = "team_5g";
                String database_password = "thindoe99";
                String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);
                Connection conn = DriverManager.getConnection(database_url, database_user, database_password);

                JFrame frame = new JFrame("X-Report Generator");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 600);  // Increased window size to accommodate more columns

                frame.add(new XReport(conn));
                frame.setVisible(true);

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}