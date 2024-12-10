import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The login_page authenticates users to see whether they are a manager or just a regular employee/cashier 
 * It also serves as a connection to both the UI for the manager and the cashier
 * 
 * @author Simon Song
 */

public class login_page extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    private static final String DB_URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_5g_db";
    private static final String DB_USER = "team_5g";
    private static final String DB_PASSWORD = "thindoe99";

    /**
     * Constructor for login_page creates an interactive interface that authenticates users as either
     * manager or cashier than directs them towards the correct page.
     */

    public login_page() {
        // Basic window setup
        setTitle("Panda Express Login");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(208, 43, 46)); // Panda Express red

        // Logo
        ImageIcon logo = new ImageIcon("panda_express_logo.png");
        Image scaledLogo = logo.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        add(logoLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 24));
        formPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 24));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        loginButton = new JButton("Login");
        loginButton.setBackground(Color.WHITE);
        loginButton.setForeground(new Color(208, 43, 46));
        loginButton.setFont(new Font("Arial", Font.BOLD, 24));
        
        formPanel.add(loginButton, gbc);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.WHITE);
        
        add(formPanel, BorderLayout.CENTER);
    

        loginButton.addActionListener(e -> authenticateUser());
    }

    /**
     * The authenticateUser() function checks the fields that were entered and validates whether
     * the user is a regular employee, manager, or they entered something that was not 
     * in our database.
     */
    private void authenticateUser() {
       String email = emailField.getText();
       String password = new String(passwordField.getPassword());

       try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
           String query = "SELECT Employee_Id, Employee_Name, Manager_Id FROM Employee WHERE Email = ? AND Pword = ?";
           PreparedStatement stmt = conn.prepareStatement(query);
           stmt.setString(1, email);
           stmt.setString(2, password);
           ResultSet rs = stmt.executeQuery();

           if (rs.next()) {
               String employeeName = rs.getString("Employee_Name");
               Integer managerId = rs.getObject("Manager_Id", Integer.class);

               if (managerId != null && managerId == 1) {
                   openManagerView();
               } else {
                   openCashierView(employeeName);
               }
           } else {
               // Display popup for invalid credentials
                JOptionPane.showMessageDialog(this,
                    "Invalid email or password. Please try again.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
                // Clear the password field
                passwordField.setText("");
           }

           rs.close();
           stmt.close();
           
       } catch (Exception e) {
           e.printStackTrace();
           JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
       }
   }

    /**
     * If user is a manager opens manager view by calling on the manager file and constructing
     * a manager page.
     */
   private void openManagerView() {
      // Open the ManagerView
      manager managerView = new manager();
      managerView.setVisible(true);
      this.setVisible(false);
   }

    /**
     * If user is a employee opens the cashier view by calling on the makeOrder file and constructing
     * the respective page.
     * 
     * @param employeeName to see which employee is checking out the order
     */
   private void openCashierView(String employeeName) {
      makeOrder cashierView = new makeOrder();
      cashierView.setVisible(true);
      this.dispose();
   }

    /**
     * Constructs a login_page and displays it for the user to use. 
     */
   public static void main(String[] args) {
      SwingUtilities.invokeLater(() -> {
          login_page loginPage = new login_page();
          loginPage.setVisible(true);
      });
   }
}