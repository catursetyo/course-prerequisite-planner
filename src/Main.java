import graph.CourseGraph;
import model.Course;
import tree.Trie;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final String DEFAULT_COURSES_FILE = "data/courses.csv";
    private static final String DEFAULT_PREREQUISITES_FILE = "data/prerequisites.csv";

    public static void main(String[] args) {
        String coursesFile = args.length >= 1 ? args[0] : DEFAULT_COURSES_FILE;
        String prerequisitesFile = args.length >= 2 ? args[1] : DEFAULT_PREREQUISITES_FILE;

        try {
            CourseGraph graph = new CourseGraph(coursesFile, prerequisitesFile);
            Trie trie = buildTrie(graph);
            runMenu(graph, trie);
        } catch (IOException error) {
            System.out.println("Gagal membaca file CSV: " + error.getMessage());
        } catch (IllegalArgumentException error) {
            System.out.println("Data CSV tidak valid: " + error.getMessage());
        }
    }

    private static Trie buildTrie(CourseGraph graph) {
        Trie trie = new Trie();
        for (Course course : graph.getCourses().values()) {
            trie.insert(course.getCode(), course.getCode());
            trie.insert(course.getName(), course.getCode());
        }
        return trie;
    }

    private static void runMenu(CourseGraph graph, Trie trie) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu(graph);
            System.out.print("Pilih menu: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    graph.printAllCourses();
                    break;
                case "2":
                    searchCourse(scanner, graph, trie);
                    break;
                case "3":
                    graph.printAdjacencyList();
                    break;
                case "4":
                    showDirectPrerequisites(scanner, graph);
                    break;
                case "5":
                    showAllPrerequisites(scanner, graph);
                    break;
                case "6":
                    showTopologicalSort(graph);
                    break;
                case "7":
                    System.out.println(graph.hasCycle() ? "Graph memiliki cycle." : "Graph tidak memiliki cycle.");
                    break;
                case "0":
                    System.out.println("Program selesai.");
                    return;
                default:
                    System.out.println("Menu tidak valid.");
            }

            System.out.println();
        }
    }

    private static void printMenu(CourseGraph graph) {
        System.out.println("=== Course Prerequisite Planner ===");
        System.out.println("Jumlah mata kuliah: " + graph.getTotalCourses());
        System.out.println("Jumlah relasi prasyarat: " + graph.getTotalEdges());
        System.out.println("1. Tampilkan semua mata kuliah");
        System.out.println("2. Cari mata kuliah berdasarkan prefix/kode/nama");
        System.out.println("3. Tampilkan adjacency list");
        System.out.println("4. Tampilkan prasyarat langsung");
        System.out.println("5. Tampilkan semua prasyarat tidak langsung");
        System.out.println("6. Rekomendasi urutan pengambilan mata kuliah");
        System.out.println("7. Deteksi cycle");
        System.out.println("0. Keluar");
    }

    private static void searchCourse(Scanner scanner, CourseGraph graph, Trie trie) {
        System.out.print("Masukkan prefix kode/nama: ");
        String prefix = scanner.nextLine();
        Set<String> results = trie.searchByPrefix(prefix);

        if (results.isEmpty()) {
            System.out.println("Tidak ada mata kuliah yang cocok.");
            return;
        }

        for (String code : results) {
            System.out.println("- " + graph.getCourse(code));
        }
    }

    private static void showDirectPrerequisites(Scanner scanner, CourseGraph graph) {
        System.out.print("Masukkan kode mata kuliah: ");
        String courseCode = scanner.nextLine().trim();
        graph.printDirectPrerequisites(courseCode);
    }

    private static void showAllPrerequisites(Scanner scanner, CourseGraph graph) {
        System.out.print("Masukkan kode mata kuliah: ");
        String courseCode = scanner.nextLine().trim();
        Set<String> prerequisites = graph.getAllPrerequisites(courseCode);

        Course target = graph.getCourse(courseCode);
        System.out.println("Semua prasyarat untuk " + target);
        if (prerequisites.isEmpty()) {
            System.out.println("- Tidak ada prasyarat.");
            return;
        }

        for (String prerequisiteCode : prerequisites) {
            System.out.println("- " + graph.getCourse(prerequisiteCode));
        }
    }

    private static void showTopologicalSort(CourseGraph graph) {
        List<String> order = graph.topologicalSort();
        System.out.println("Rekomendasi urutan pengambilan mata kuliah:");
        for (int i = 0; i < order.size(); i++) {
            System.out.printf("%02d. %s%n", i + 1, graph.getCourse(order.get(i)));
        }
    }
}
