import java.util.*;
import java.util.stream.*;

class Employee {
    int id;
    String name;
    int age;
    double salary;
    Employee(int id, String name, int age, double salary) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.salary = salary;
    }
    public String toString() {
        return id + " " + name + " " + age + " " + salary;
    }
}

class Student {
    int id;
    String name;
    double marks;
    Student(int id, String name, double marks) {
        this.id = id;
        this.name = name;
        this.marks = marks;
    }
    public String toString() {
        return id + " " + name + " " + marks;
    }
}

class Product {
    int id;
    String category;
    double price;
    Product(int id, String category, double price) {
        this.id = id;
        this.category = category;
        this.price = price;
    }
    public String toString() {
        return id + " " + category + " " + price;
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("Part A: Sorting Employees Using Lambda Expressions");
        List<Employee> employees = Arrays.asList(
                new Employee(1, "Alice", 25, 50000),
                new Employee(2, "Bob", 30, 60000),
                new Employee(3, "Charlie", 22, 45000)
        );
        System.out.println("Sorted by Name:");
        employees.stream().sorted((e1, e2) -> e1.name.compareTo(e2.name)).forEach(System.out::println);
        System.out.println("Sorted by Age:");
        employees.stream().sorted((e1, e2) -> e1.age - e2.age).forEach(System.out::println);
        System.out.println("Sorted by Salary:");
        employees.stream().sorted((e1, e2) -> Double.compare(e1.salary, e2.salary)).forEach(System.out::println);

        System.out.println("\nPart B: Filtering and Sorting Students Using Streams");
        List<Student> students = Arrays.asList(
                new Student(1, "Aman", 80),
                new Student(2, "Riya", 70),
                new Student(3, "Karan", 90),
                new Student(4, "Tina", 60)
        );
        students.stream()
                .filter(s -> s.marks > 75)
                .sorted((s1, s2) -> Double.compare(s2.marks, s1.marks))
                .map(s -> s.name)
                .forEach(System.out::println);

        System.out.println("\nPart C: Stream Operations on Product Dataset");
        List<Product> products = Arrays.asList(
                new Product(1, "Electronics", 1200),
                new Product(2, "Clothing", 800),
                new Product(3, "Electronics", 1500),
                new Product(4, "Groceries", 200),
                new Product(5, "Clothing", 1000)
        );

        Map<String, List<Product>> grouped = products.stream().collect(Collectors.groupingBy(p -> p.category));
        System.out.println("Grouped by Category:");
        grouped.forEach((k, v) -> System.out.println(k + ": " + v));

        Optional<Product> maxPrice = products.stream().max(Comparator.comparingDouble(p -> p.price));
        System.out.println("Most Expensive Product: " + maxPrice.get());

        Map<String, Double> avgPrice = products.stream()
                .collect(Collectors.groupingBy(p -> p.category, Collectors.averagingDouble(p -> p.price)));
        System.out.println("Average Price per Category:");
        avgPrice.forEach((k, v) -> System.out.println(k + ": " + v));
    }
}
