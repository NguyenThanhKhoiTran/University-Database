import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

/*****************************************************************************
 * Create University database using JDBC and SQLite
 *
 * @author Nguyen Thanh Khoi Tran
 * @version proj_v01
 * @date April 02nd, 2024
 *****************************************************************************/

public class UniversityDatabase {
    public static void main(String[] args) {

        // Prompt user which choice that user want to know
        Scanner sc = new Scanner(System.in);
        boolean done = false;
        int choice = 0;

        System.out.println("Which option do you expect to know? - Type number only");
        System.out.println("1 - Create database schema \"advisor\" and input provided instances, then print");
        System.out.println("2 - Assuming tax amount is 10%, print tax amount for each instructor");
        System.out.println("3 - Print student get highest grade for each semester");
        System.out.println("4 - Insert a new student");
        System.out.println("5 - Delete an existed student");
        System.out.println("6 - Insert a new instructor");
        System.out.println("7 - Delete an existed instructor");
        System.out.println("8 - Change a department of student");
        do {
            try {
                System.out.print("\n\nYOUR CHOICE ==> ");
                choice = sc.nextInt();
                System.out.println();

                if (choice < 0 && choice > 9) {
                    throw new Exception();
                }
                done = true;
            } catch (Exception e) {
                System.err.println("Invalid input - Try again!!!");
            }
        } while (!done);

        Connection conn = null;
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to a database
            conn = DriverManager.getConnection("jdbc:sqlite:UnivesityDB.db");

            // Create a statement
            Statement stmt = conn.createStatement();

            // If choice = 1
            switch (choice) {
                // Create database schema "advisor" and input provided instances, then print
                case 1:
                    try {
                        File myFile = new File("advisor_value.txt");
                        String value = "";
                        Scanner readFile = new Scanner(myFile);

                        // Check if the advisor relation already existed, DROP it
                        stmt.executeUpdate("DROP TABLE IF EXISTS advisor");

                        String query1 = "CREATE TABLE advisor";
                        query1 += "(s_id varchar (5), i_id varchar (5),";
                        query1 += "primary key (s_id),";
                        query1 += "foreign key (i_id) references instructor (ID) on delete set null,";
                        query1 += "foreign key (s_id) references student (ID) on delete cascade);";

                        // Execute the query statement
                        stmt.execute(query1);

                        // Input all value of it
                        while (readFile.hasNextLine()) {
                            value += readFile.nextLine();
                        }
                        stmt.executeUpdate(value);
                        readFile.close();

                        // Loop through the result set and print the results
                        String printTable = "SELECT * FROM advisor";
                        ResultSet rs = stmt.executeQuery(printTable);

                        while (rs.next()) {
                            System.out.printf("Student ID: %4d \t Instructor ID: %4d \n", rs.getInt("s_id"),
                                    rs.getInt("i_id"));
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    // Assuming tax amount is 10%, print tax amount for each instructor
                case 2:
                    String query2 = "SELECT ID, name, salary, salary*0.1 from instructor";
                    ResultSet rs = stmt.executeQuery(query2);

                    while (rs.next()) {
                        String result = String.format("ID: %4d", rs.getInt("ID"));
                        result += String.format("\t\t\tname: %20s", rs.getString("name"));
                        result += String.format("\t\t\tsalary: %4.2f", rs.getDouble("salary"));
                        result += String.format("\t\t\ttax amount: %4.2f", rs.getDouble("salary*0.1"));

                        System.out.println(result);
                    }

                    // Print student get highest grade for each semester
                case 3:
                    // Min grade as the grade is sorted alphabetically
                    String query3 = "SELECT takes.ID, student.name, takes.semester, MIN(takes.grade) ";
                    query3 += "FROM takes, student ";
                    query3 += "WHERE student.ID = takes.ID ";
                    query3 += "GROUP BY semester";

                    ResultSet rs3 = stmt.executeQuery(query3);

                    while (rs3.next()) {
                        String result3 = String.format("ID: %4d", rs3.getInt(1));
                        result3 += String.format("\t\t\tname: %4s", rs3.getString(2));
                        result3 += String.format("\t\tsemester: %10s", rs3.getString(3));
                        result3 += String.format("\t\t\tGrade: %4s", rs3.getString(4));

                        System.out.println(result3);
                    }

                    // Insert a new student
                case 4:
                    String name, dept_name;
                    int id = 0;

                    // Consume previous input
                    sc.nextLine();

                    System.out.print("Please provide student name ==> ");
                    name = sc.nextLine();

                    do {
                        System.out.print("Please provide student ID ==> ");
                        id = sc.nextInt();
                        sc.nextLine();
                        if (id <= 0)
                            System.out.println("TRY AGAIN, ID MUST BE GREATER THAN 0");
                    } while (id <= 0);

                    System.out.print("Please provide department\' student name ==> ");
                    dept_name = sc.nextLine();

                    String query4 = "INSERT INTO student (id, name, dept_name)";
                    query4 += "values(\"" + id + "\",\"" + name + "\",\"" + dept_name + "\")";
                    stmt.executeUpdate(query4);
                    System.out.println("INSERT SUCCESSFULLY");

                    // Delete an existed student
                case 5:
                    String deleteName;

                    // Consume previous input
                    sc.nextLine();

                    System.out.print("Please provide student name that you want to delete ==> ");
                    deleteName = sc.nextLine();

                    String query5 = "DELETE FROM student ";
                    query5 += "where name = \"" + deleteName + "\"";
                    stmt.executeUpdate(query5);
                    System.out.println("DELETE SUCCESSFULLY");

                    // Insert a new instructor
                case 6:
                    String in_name, in_dept_name;
                    int i_id = 0;

                    // Consume previous input
                    sc.nextLine();

                    System.out.print("Please provide instructor name ==> ");
                    in_name = sc.nextLine();

                    do {
                        System.out.print("Please provide instructor ID ==> ");
                        i_id = sc.nextInt();
                        sc.nextLine();
                        if (i_id <= 0)
                            System.out.println("TRY AGAIN, ID MUST BE GREATER THAN 0");
                    } while (i_id <= 0);

                    System.out.print("Please provide department\' instructor name ==> ");
                    in_dept_name = sc.nextLine();

                    String query6 = "INSERT INTO instructor (id, name, dept_name)";
                    query6 += "values(\"" + i_id + "\",\"" + in_name + "\",\"" + in_dept_name + "\")";
                    stmt.executeUpdate(query6);
                    System.out.println("INSERT SUCCESSFULLY");

                    // Delete an existed instructor
                case 7:
                    String deleteInstructorName;

                    // Consume previous input
                    sc.nextLine();

                    System.out.print("Please provide instructor name that you want to delete ==> ");
                    deleteInstructorName = sc.nextLine();

                    String query7 = "DELETE FROM instructor ";
                    query7 += "where name = \"" + deleteInstructorName + "\"";
                    stmt.executeUpdate(query7);
                    System.out.println("DELETE SUCCESSFULLY");

                    // Change a department of student
                case 8:
                    String updateDept, s_name;

                    // Consume previous input
                    sc.nextLine();

                    System.out.print("Please provide student name that you want to change dept_name ==> ");
                    s_name = sc.nextLine();

                    System.out.print("Please provide dept name that you want to change ==> ");
                    updateDept = sc.nextLine();

                    String query8 = "UPDATE student ";
                    query8 += "set dept_name = \"" + updateDept + "\" where name = \"" + s_name + "\"";
                    stmt.executeUpdate(query8);
                    System.out.println("CHANGE SUCCESSFULLY");
                default:
                    break;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                    sc.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
