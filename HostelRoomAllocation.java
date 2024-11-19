import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class HostelRoomAllocation {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hostel";
    private static final String DB_USER = "root"; // Your database username
    private static final String DB_PASSWORD = "kongu@2024"; // Your database password

    public static void main(String[] args) {
        
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connection successful!");
            ArrayList<Integer> availableRooms = fetchAvailableRooms(connection);

            if (availableRooms.isEmpty()) {
                System.out.println("No rooms available for allocation.");
                return;
            }

            
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the number of students: ");
            int numberOfStudents = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            ArrayList<String> studentNames = new ArrayList<>();
            for (int i = 0; i < numberOfStudents; i++) {
                System.out.print("Enter name of student " + (i + 1) + ": ");
                studentNames.add(scanner.nextLine());
            }

            
            allocateRoomsToStudents(connection, availableRooms, studentNames);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<Integer> fetchAvailableRooms(Connection connection) throws SQLException {
        ArrayList<Integer> rooms = new ArrayList<>();
        String query = "SELECT room_number FROM rooms";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                rooms.add(resultSet.getInt("room_number"));
            }
}
       
        
        System.out.println("Available rooms: " + rooms);
        return rooms;
    }

    private static void allocateRoomsToStudents(Connection connection, ArrayList<Integer> availableRooms, ArrayList<String> studentNames) throws SQLException {
        Random random = new Random();

        for (String student : studentNames) {
            if (availableRooms.isEmpty()) {
                System.out.println("No more rooms available for allocation.");
                break;
            }

           
            int randomIndex = random.nextInt(availableRooms.size());
            int roomNumber = availableRooms.remove(randomIndex);

           
            System.out.println(student + " is allocated to Room " + roomNumber);

            
            storeAllocationInDatabase(connection, student, roomNumber);

          
            removeRoomFromDatabase(connection, roomNumber);
        }
    }

    private static void storeAllocationInDatabase(Connection connection, String studentName, int roomNumber) throws SQLException {
        String query = "INSERT INTO allocations (student_name, room_number) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, studentName);
            preparedStatement.setInt(2, roomNumber);
            preparedStatement.executeUpdate();
            System.out.println("Allocation for " + studentName + " stored in database.");
        }
    }

    private static void removeRoomFromDatabase(Connection connection, int roomNumber) throws SQLException {
        String query = "DELETE FROM rooms WHERE room_number = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, roomNumber);
            preparedStatement.executeUpdate();
        }
    }
}
