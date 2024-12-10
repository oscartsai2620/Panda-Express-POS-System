import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Date;
// ------------------------------------------------------------------------------------------
/*
    NOTES:
    - 'employee_id' is made so that it is uneditable to prevent issues with database.
    - 'PreparedStatement' is part of JDBC API. Allows execution of parameterized SQL queries.
    - Remember to update / refresh the table every time the user makes a change so that it updates live.
*/
// ------------------------------------------------------------------------------------------
/** 
 * This class represents the Employee editor.
 * It contains a table of employee data, add, remove, and save buttons,
 * and a form to add new employees.
 * 
 * @author David Cheung
 */
public class EmployeeEditor extends JPanel {
    /**
     * The table displaying employee data.
     */
    public JTable table;

    /**
     * The table model for the employee data table.
     */
    public DefaultTableModel tableModel;

    /**
     * The database connection.
     */
    public Connection conn;

    /**
     * Constructs the Employee editor window.
     * 
     * @param conn Connection object to the database
     */
    public EmployeeEditor(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout());

        // Create table for employee credentials
        tableModel = new DefaultTableModel(new String[]{"employee_id", "employee_name", "manager_id", "ssn", "dob", "phone_num", "salary", "email", "pword"}, 0) {
            @Override
            // Make sure employee_id is not editable bc that will mess up the database
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        table = new JTable(tableModel);

        loadEmployeeData();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });
        add(saveButton, BorderLayout.SOUTH);

        // Employee Form
        JPanel formPanel = new JPanel(new GridLayout(0, 2));
        JTextField employeeNameField = new JTextField();
        JTextField managerIdField = new JTextField();
        JTextField ssnField = new JTextField();
        JTextField dobField = new JTextField();
        JTextField phoneNumField = new JTextField();
        JTextField salaryField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField pwordField = new JTextField();
        JButton addButton = new JButton("Add Employee");

        formPanel.add(new JLabel("Employee Name:"));
        formPanel.add(employeeNameField);
        formPanel.add(new JLabel("Manager ID:"));
        formPanel.add(managerIdField);
        formPanel.add(new JLabel("SSN:"));
        formPanel.add(ssnField);
        formPanel.add(new JLabel("DOB (YYYY-MM-DD):"));
        formPanel.add(dobField);
        formPanel.add(new JLabel("Phone Number:"));
        formPanel.add(phoneNumField);
        formPanel.add(new JLabel("Salary:"));
        formPanel.add(salaryField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(pwordField);
        formPanel.add(addButton);

        add(formPanel, BorderLayout.NORTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee(employeeNameField.getText(), managerIdField.getText(), ssnField.getText(), dobField.getText(), phoneNumField.getText(), salaryField.getText(), emailField.getText(), pwordField.getText());
            }
        });

        JButton removeButton = new JButton("Remove Employee");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeEmployee();
            }
        });
        add(removeButton, BorderLayout.EAST);
    }
// ------------------------------------------------------------------------------------------
    /**
     * Loads the Employee data from the database into the EmployeeEditor table.
     *
     *exception if there is an error accessing the database
     */
    public void loadEmployeeData() {
        String query = "SELECT * FROM employee ORDER BY employee_id";
        Statement stmt = null;
        ResultSet result = null;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);

            while (result.next()) {
                tableModel.addRow(new Object[]{
                        result.getInt("employee_id"),
                        result.getString("employee_name"),
                        result.getInt("manager_id"),
                        result.getString("ssn"),
                        result.getDate("dob"),
                        result.getString("phone_num"),
                        result.getInt("salary"),
                        result.getString("email"),
                        result.getString("pword")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error accessing Database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
// ------------------------------------------------------------------------------------------
    /** 
     * Updates the Employee table in the database with the values in the tableModel.
     * 
     * SQLException if there is an error saving changes to the database
     */
    public void saveChanges() {
        String query = "UPDATE employee SET employee_name=?, manager_id=?, ssn=?, dob=?, phone_num=?, salary=?, email=?, pword=? WHERE employee_id=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(query);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                pstmt.setString(1, (String) tableModel.getValueAt(i, 1));
                pstmt.setInt(2, Integer.parseInt(tableModel.getValueAt(i, 2).toString()));
                pstmt.setString(3, (String) tableModel.getValueAt(i, 3));
                pstmt.setDate(4, Date.valueOf(tableModel.getValueAt(i, 4).toString()));
                pstmt.setString(5, (String) tableModel.getValueAt(i, 5));
                pstmt.setInt(6, Integer.parseInt(tableModel.getValueAt(i, 6).toString()));
                pstmt.setString(7, (String) tableModel.getValueAt(i, 7));
                pstmt.setString(8, (String) tableModel.getValueAt(i, 8));
                pstmt.setInt(9, Integer.parseInt(tableModel.getValueAt(i, 0).toString()));
                pstmt.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Changes saved successfully.", "Success!", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving changes to Database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } 
        finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }   
// ------------------------------------------------------------------------------------------
    /**
     * Inserts an employee into the database based on the input fields.
     * 
     * @param employeeName Name of the employee
     * @param managerId ID of the manager
     * @param ssn Social Security Number
     * @param dob Date of Birth
     * @param phoneNum Phone Number
     * @param salary Salary
     * @param email Email
     * @param pword Password
     * SQLException if there is an error adding the employee
     */
    public void addEmployee(String employeeName, String managerId, String ssn, String dob, String phoneNum, String salary, String email, String pword) {
        int newEmployeeId = getNewEmployeeId();
        String query = "INSERT INTO employee (employee_id, employee_name, manager_id, ssn, dob, phone_num, salary, email, pword) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, newEmployeeId);
            pstmt.setString(2, employeeName);
            pstmt.setInt(3, Integer.parseInt(managerId));
            pstmt.setString(4, ssn);
            pstmt.setDate(5, Date.valueOf(dob));
            pstmt.setString(6, phoneNum);
            pstmt.setInt(7, Integer.parseInt(salary));
            pstmt.setString(8, email);
            pstmt.setString(9, pword);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Employee added successfully.", "Success!", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the table [DC ~ This is important so the addition is shown in the table after the user presses the button for adding an employee]
            tableModel.setRowCount(0);
            loadEmployeeData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding employee to Database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
// ------------------------------------------------------------------------------------------
    /**
     * Removes an employee from the database based on the selected row in the table.
     * 
     * SQLException if there is an error removing the employee
     */
    public void removeEmployee() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int employeeId = (int) tableModel.getValueAt(selectedRow, 0);
            String query = "DELETE FROM employee WHERE employee_id=?";
            PreparedStatement pstmt = null;
            try {
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, employeeId);
                pstmt.executeUpdate();

                // Refresh the table (again)
                tableModel.setRowCount(0);
                loadEmployeeData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error removing employee from Database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
// ------------------------------------------------------------------------------------------
    /** 
     * Generates a new employee ID based on the current maximum employee ID present within the database.
     * 
     * @return the new employee ID
     * SQLException if there is an error generating the new employee ID
     */ 
    public int getNewEmployeeId() {
        String query = "SELECT MAX(employee_id) AS max_id FROM employee";
        Statement stmt = null;
        ResultSet result = null;
        int newId = 0;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
            if (result.next()) {
                newId = result.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error generating new employee ID.", "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return newId;
    }
}