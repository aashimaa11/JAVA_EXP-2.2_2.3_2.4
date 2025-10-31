import java.io.*;
import java.util.*;

class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    int id;
    String name;
    double marks;
    Student(int id, String name, double marks) {
        this.id = id;
        this.name = name;
        this.marks = marks;
    }
}

class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    int id;
    String name;
    double salary;
    Employee(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }
}

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Part A: Sum of Integers Using Autoboxing/Unboxing");
        System.out.print("Enter integers separated by space: ");
        String[] input = sc.nextLine().split(" ");
        List<Integer> list = new ArrayList<>();
        for (String s : input) list.add(Integer.parseInt(s));
        int sum = 0;
        for (Integer i : list) sum += i;
        System.out.println("Sum: " + sum);

        System.out.println("\nPart B: Serialization and Deserialization");
        Student s1 = new Student(1, "Alice", 85.5);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("student.ser"))) {
            oos.writeObject(s1);
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("student.ser"))) {
            Student s2 = (Student) ois.readObject();
            System.out.println("Deserialized Student: ID=" + s2.id + " Name=" + s2.name + " Marks=" + s2.marks);
    }

        System.out.println("\nPart C: Employee Management System");
        while (true) {
            System.out.println("1. Add Employee\n2. Display Employees\n3. Exit");
            System.out.print("Enter choice: ");
            int ch = sc.nextInt();
            if (ch == 1) {
                System.out.print("Enter ID: ");
                int id = sc.nextInt();
                sc.nextLine();
                System.out.print("Enter Name: ");
                String name = sc.nextLine();
                System.out.print("Enter Salary: ");
                double sal = sc.nextDouble();
                Employee e = new Employee(id, name, sal);
                FileOutputStream fos = new FileOutputStream("employees.dat", true);
                ObjectOutputStream out;
                File empFile = new File("employees.dat");
                if (!empFile.exists() || empFile.length() == 0)
                    out = new ObjectOutputStream(fos);
                else
                    out = new AppendableObjectOutputStream(fos);
                try (ObjectOutputStream o = out) {
                    o.writeObject(e);
                }
            } else if (ch == 2) {
                if (new File("employees.dat").exists()) {
                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("employees.dat"))) {
                        while (true) {
                            Employee e = (Employee) in.readObject();
                            System.out.println("ID=" + e.id + " Name=" + e.name + " Salary=" + e.salary);
                        }
                    } catch (EOFException ex) {
                        // reached end of file
                    }
                } else {
                    System.out.println("No employees recorded yet.");
                }
            } else if (ch == 3) break;
    }
    sc.close();
    }
}

class AppendableObjectOutputStream extends ObjectOutputStream {
    public AppendableObjectOutputStream(OutputStream out) throws IOException { super(out); }
    @Override
    protected void writeStreamHeader() throws IOException {
        // do not write a header when appending
    }
}

