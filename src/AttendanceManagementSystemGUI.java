import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class AttendanceManagementSystemGUI {
    
    private static Connection connect() {
        // SQLite connection string (ensure the path is correct)
        String url = "jdbc:sqlite:attendance.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    // Method to create the attendance table if it does not exist
    private static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS attendance (\n"
                   + "    student_id TEXT PRIMARY KEY,\n"  // Use student_id instead of id
                   + "    name TEXT NOT NULL,\n"
                   + "    date TEXT NOT NULL,\n"
                   + "    status TEXT NOT NULL\n"
                   + ");";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
            System.out.println("Table 'attendance' is ready.");
        } catch (SQLException e) {
            System.out.println("Create table error: " + e.getMessage());
        }
    }

    // Method to add a student's details to the database
    private static void addStudent(String studentId, String name, String date, String status) {
        String sql = "INSERT INTO attendance(student_id, name, date, status) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, name);
            pstmt.setString(3, date);
            pstmt.setString(4, status);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Student " + name + " added successfully.");
            System.out.println("Added student: " + name);
        } catch (SQLException e) {
            System.out.println("Insert error: " + e.getMessage());
        }
    }

    // Method to search and display attendance by student ID
    private static void searchById(String studentId) {
        String sql = "SELECT * FROM attendance WHERE student_id = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, 
                    "Student ID: " + rs.getString("student_id") + 
                    ", Name: " + rs.getString("name") + 
                    ", Date: " + rs.getString("date") + 
                    ", Status: " + rs.getString("status"));
                System.out.println("Found student by ID: " + studentId);
            } else {
                JOptionPane.showMessageDialog(null, "No student found with ID: " + studentId);
                System.out.println("No student found with ID: " + studentId);
            }
        } catch (SQLException e) {
            System.out.println("Search by ID error: " + e.getMessage());
        }
    }

    // Method to search and display attendance by student name
    private static void searchByName(String name) {
        String sql = "SELECT * FROM attendance WHERE name LIKE ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            
            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                result.append("Student ID: ").append(rs.getString("student_id"))
                      .append(", Name: ").append(rs.getString("name"))
                      .append(", Date: ").append(rs.getString("date"))
                      .append(", Status: ").append(rs.getString("status")).append("\n");
            }
            
            if (result.length() > 0) {
                JOptionPane.showMessageDialog(null, result.toString());
                System.out.println("Found student(s) by name: " + name);
            } else {
                JOptionPane.showMessageDialog(null, "No student found with name: " + name);
                System.out.println("No student found with name: " + name);
            }
        } catch (SQLException e) {
            System.out.println("Search by name error: " + e.getMessage());
        }
    }

    // Method to view all students
    private static void viewAllStudents() {
        String sql = "SELECT * FROM attendance";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            StringBuilder result = new StringBuilder("Registered Students:\n");
            while (rs.next()) {
                result.append("Student ID: ").append(rs.getString("student_id"))
                      .append(", Name: ").append(rs.getString("name"))
                      .append(", Date: ").append(rs.getString("date"))
                      .append(", Status: ").append(rs.getString("status")).append("\n");
            }
            
            if (result.length() > "Registered Students:\n".length()) {
                JOptionPane.showMessageDialog(null, result.toString());
                System.out.println(result.toString());
            } else {
                JOptionPane.showMessageDialog(null, "No students have been registered yet.");
                System.out.println("No registered students found.");
            }
        } catch (SQLException e) {
            System.out.println("View all students error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTable(); // Ensure the table is created

        while (true) {
            String[] options = {"Add Student", "Search by ID", "Search by Name", "View All Students", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "Choose an option", "Attendance Management",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

            if (choice == 0) { // Add Student
                String studentId = JOptionPane.showInputDialog("Enter the student's ID:");
                if (studentId == null || studentId.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Student ID cannot be empty.");
                    continue;
                }
                
                String name = JOptionPane.showInputDialog("Enter the student's name:");
                if (name == null || name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Name cannot be empty.");
                    continue;
                }

                String date = JOptionPane.showInputDialog("Enter the date (YYYY-MM-DD):");
                if (date == null || date.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Date cannot be empty.");
                    continue;
                }

                String status = JOptionPane.showInputDialog("Enter the student's status (Present/Absent):");
                if (status == null || status.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Status cannot be empty.");
                    continue;
                }

                addStudent(studentId, name, date, status);
            } else if (choice == 1) { // Search by ID
                String studentId = JOptionPane.showInputDialog("Enter the student ID:");
                if (studentId != null) {
                    searchById(studentId);
                }
            } else if (choice == 2) { // Search by Name
                String name = JOptionPane.showInputDialog("Enter the student's name to search:");
                if (name != null && !name.trim().isEmpty()) {
                    searchByName(name);
                }
            } else if (choice == 3) { // View All Students
                viewAllStudents();
            } else if (choice == 4 || choice == JOptionPane.CLOSED_OPTION) { // Exit
                break;
            }
        }
    }
}
