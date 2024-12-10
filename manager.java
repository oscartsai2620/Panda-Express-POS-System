import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.sql.*;

/**
 * This class contains the entire mainframe of the manager view.
 * It allows the user to access all the different panels, such as
 * the report, the employee editor page, the inventory editor, and 
 * the menu item editor page.
 * 
 * @author David Cheung
 */
public class manager extends JFrame {
    
    private static Connection conn;

    /**
     * This constructor sets up the GUI components,
     * establishes a connection to the PostgreSQL database, 
     * and adds tabs for managing employees, inventory, reports, and menu items.
     */
    public manager() {
        // Establish database connection
        String database_name = "team_5g_db";
        String database_user = "team_5g";
        String database_password = "thindoe99";
        String database_url = String.format("jdbc:postgresql://csce-315-db.engr.tamu.edu/%s", database_name);
        try {
            conn = DriverManager.getConnection(database_url, database_user, database_password);
            JOptionPane.showMessageDialog(null, "Opened database successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error connecting to Database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(0);
        }

        // Create a tabbed pane for different management tasks
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add EmployeeEditor component to the first tab
        EmployeeEditor employeeEditor = new EmployeeEditor(conn);
        tabbedPane.addTab("Employee Editor", employeeEditor);

        // Add InventoryGUI to the second tab
        InventoryGUI inventoryGUI = new InventoryGUI(conn);
        tabbedPane.addTab("Inventory Editor", inventoryGUI);

        // Add ManagerReport to the third tab
        ManagerReport managerReport = new ManagerReport(conn);
        tabbedPane.addTab("Manager Report", managerReport);

        // Add menu_items to the fourth tab
        menu_items menuItems = new menu_items(conn);
        tabbedPane.addTab("Menu Items", menuItems);

        // Add combined report for X and Z
        CombinedReport combined = new CombinedReport(conn);
        tabbedPane.addTab("Reports", combined);

        // Add Exit Manager View tab
        JPanel exitPanel = new JPanel();
        JButton exitButton = new JButton("Exit Manager View");
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the Manager View?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Close the manager window
                login_page loginPage = new login_page();
                loginPage.setVisible(true);
            }
        });
        exitPanel.add(exitButton);
        tabbedPane.addTab("Exit", exitPanel);

        // Add a change listener to the tabbed pane to refresh the inventory when the tab is selected
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();

                if (selectedIndex == 1) { 
                    inventoryGUI.refreshInventory();
                }
            }
        });

        

        // Add the tabbed pane to the main frame
        add(tabbedPane);

        // Set the frame to full screen and define the close operation
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * The main method that launches the application.
     * 
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            manager managerView = new manager();
            managerView.setVisible(true);
        });
    }
}
