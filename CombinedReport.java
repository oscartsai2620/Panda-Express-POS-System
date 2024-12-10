import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

/**
 * The combined report page serves as a tab in the manager page to display both the X and Z reports as 
 * to not create so much clutter at the top of the page.
 * 
 * @author Simon Song
 */

public class CombinedReport extends JPanel {
    private Connection conn;
    private XReport xReport;
    private ZReport zReport;

    /**
     * This is the constructor to create a tab that contains both the X and Z reports for the particular day.
     * 
     * @param conn connection to the database
     */
    public CombinedReport(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout());

        // Create instances of XReport and ZReport
        xReport = new XReport(conn);
        zReport = new ZReport(conn);

        // Create a tabbed pane for the reports
        JTabbedPane reportTabs = new JTabbedPane();
        reportTabs.addTab("X-Report", xReport);
        reportTabs.addTab("Z-Report", zReport);

        // Add the tabbed pane to this panel
        add(reportTabs, BorderLayout.CENTER);
    }
}