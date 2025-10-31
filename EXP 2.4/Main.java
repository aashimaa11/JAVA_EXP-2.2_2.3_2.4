import java.sql.*;
import java.util.*;

class DBConnection {
    static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "password");
    }
}

class Product {
    int id;
    String name;
    double price;
    Product(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}

class ProductCRUD {
    static void create(Product p) throws Exception {
        String q = "INSERT INTO product VALUES(?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, p.id);
            ps.setString(2, p.name);
            ps.setDouble(3, p.price);
            ps.executeUpdate();
        }
    }

    static void read() throws Exception {
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM product")) {
            while (rs.next()) System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getDouble(3));
        }
    }

    static void update(int id, double price) throws Exception {
        String q = "UPDATE product SET price=? WHERE id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(q)) {
            ps.setDouble(1, price);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    static void delete(int id) throws Exception {
        String q = "DELETE FROM product WHERE id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(q)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}

class Student {
    int id;
    String name;
    int age;
    double marks;
    Student(int id, String name, int age, double marks) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.marks = marks;
    }
}

class StudentDAO {
    void addStudent(Student s) throws Exception {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("INSERT INTO students VALUES(?,?,?,?)")) {
            ps.setInt(1, s.id);
            ps.setString(2, s.name);
            ps.setInt(3, s.age);
            ps.setDouble(4, s.marks);
            ps.executeUpdate();
        }
    }

    List<Student> getAllStudents() throws Exception {
        List<Student> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM students")) {
            while (rs.next()) list.add(new Student(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDouble(4)));
        }
        return list;
    }
}

class StudentView {
    void displayStudents(List<Student> list) {
        for (Student s : list) System.out.println(s.id + " " + s.name + " " + s.age + " " + s.marks);
    }
}

class StudentController {
    private StudentDAO dao;
    private StudentView view;
    StudentController(StudentDAO dao, StudentView view) {
        this.dao = dao;
        this.view = view;
    }
    void addStudent(Student s) throws Exception {
        dao.addStudent(s);
    }
    void showAllStudents() throws Exception {
        List<Student> list = dao.getAllStudents();
        view.displayStudents(list);
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Part A: Connecting to MySQL and Fetching Data");
        try (Connection con = DBConnection.getConnection(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM employee")) {
            while (rs.next()) System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getDouble(3));
        }

    System.out.println("\nPart B: CRUD Operations on Product Table");
    Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1.Add 2.View 3.Update 4.Delete 5.Exit");
            int ch = sc.nextInt();
            if (ch == 1) {
                System.out.print("Enter ID, Name, Price: ");
                Product p = new Product(sc.nextInt(), sc.next(), sc.nextDouble());
                ProductCRUD.create(p);
            } else if (ch == 2) ProductCRUD.read();
            else if (ch == 3) {
                System.out.print("Enter ID and New Price: ");
                ProductCRUD.update(sc.nextInt(), sc.nextDouble());
            } else if (ch == 4) {
                System.out.print("Enter ID to Delete: ");
                ProductCRUD.delete(sc.nextInt());
            } else break;
        }

        System.out.println("\nPart C: Student Management Using MVC");
        StudentDAO dao = new StudentDAO();
        StudentView view = new StudentView();
        StudentController controller = new StudentController(dao, view);
        controller.addStudent(new Student(1, "Riya", 20, 88.5));
        controller.addStudent(new Student(2, "Aman", 21, 92.3));
        controller.showAllStudents();
        sc.close();
    }
}
