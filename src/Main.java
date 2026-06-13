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
        for (Course course : graph.getAllCourses()) {
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
                case "8":
                    if (addCourse(scanner, graph)) {
                        trie = buildTrie(graph);
                    }
                    break;
                case "9":
                    addPrerequisiteRelation(scanner, graph);
                    break;
                case "10":
                    if (updateCourse(scanner, graph)) {
                        trie = buildTrie(graph);
                    }
                    break;
                case "11":
                    if (deleteCourse(scanner, graph)) {
                        trie = buildTrie(graph);
                    }
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
        System.out.println("8. Tambah mata kuliah");
        System.out.println("9. Tambah relasi prasyarat");
        System.out.println("10. Update mata kuliah");
        System.out.println("11. Delete mata kuliah");
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
        String courseCode = scanner.nextLine().trim().toUpperCase();
        Course target = graph.getCourse(courseCode);
        if (target == null) {
            System.out.println("Kode mata kuliah tidak ditemukan: " + courseCode);
            return;
        }

        graph.printDirectPrerequisites(courseCode);
    }

    private static void showAllPrerequisites(Scanner scanner, CourseGraph graph) {
        System.out.print("Masukkan kode mata kuliah: ");
        String courseCode = scanner.nextLine().trim().toUpperCase();
        Course target = graph.getCourse(courseCode);
        if (target == null) {
            System.out.println("Kode mata kuliah tidak ditemukan: " + courseCode);
            return;
        }

        Set<String> prerequisites = graph.getAllPrerequisites(courseCode);
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
        List<String> order;
        try {
            order = graph.topologicalSort();
        } catch (IllegalStateException error) {
            System.out.println("Tidak bisa membuat rekomendasi karena terdapat siklus prasyarat.");
            return;
        }

        System.out.println("Rekomendasi urutan pengambilan mata kuliah:");
        for (int i = 0; i < order.size(); i++) {
            System.out.printf("%02d. %s%n", i + 1, graph.getCourse(order.get(i)));
        }
    }

    private static boolean addCourse(Scanner scanner, CourseGraph graph) {
        System.out.println("Tambah mata kuliah baru");
        String code = readRequiredText(scanner, "Kode: ").toUpperCase();
        if (graph.getCourse(code) != null) {
            System.out.println("Kode mata kuliah sudah ada: " + code);
            return false;
        }

        String name = readRequiredText(scanner, "Nama: ");
        int semester = readPositiveInt(scanner, "Semester rekomendasi: ");
        int credits = readPositiveInt(scanner, "SKS: ");
        String category = readRequiredText(scanner, "Kategori: ");
        String track = readRequiredText(scanner, "Track: ");
        String difficulty = readRequiredText(scanner, "Difficulty: ");

        try {
            graph.addCourse(new Course(code, name, semester, credits, category, track, difficulty));
            System.out.println("Mata kuliah berhasil ditambahkan.");
            return true;
        } catch (IllegalArgumentException error) {
            System.out.println("Gagal menambah mata kuliah: " + error.getMessage());
            return false;
        }
    }

    private static void addPrerequisiteRelation(Scanner scanner, CourseGraph graph) {
        System.out.println("Tambah relasi prasyarat");
        String prerequisiteCode = readRequiredText(scanner, "Kode prasyarat: ").toUpperCase();
        String courseCode = readRequiredText(scanner, "Kode mata kuliah tujuan: ").toUpperCase();

        try {
            boolean added = graph.addEdge(prerequisiteCode, courseCode);
            if (added) {
                System.out.println("Relasi berhasil ditambahkan: " + prerequisiteCode + " -> " + courseCode);
            } else {
                System.out.println("Relasi sudah ada, tidak ditambahkan ulang.");
            }
        } catch (IllegalArgumentException error) {
            System.out.println("Gagal menambah relasi: " + error.getMessage());
        }
    }

    private static boolean updateCourse(Scanner scanner, CourseGraph graph) {
        System.out.println("Update mata kuliah");
        String code = readRequiredText(scanner, "Kode mata kuliah yang akan diupdate: ").toUpperCase();
        Course current = graph.getCourse(code);
        if (current == null) {
            System.out.println("Kode mata kuliah tidak ditemukan: " + code);
            return false;
        }

        System.out.println("Data saat ini: " + current);
        String name = readOptionalText(scanner, "Nama baru", current.getName());
        int semester = readOptionalPositiveInt(scanner, "Semester rekomendasi baru", current.getSemester());
        int credits = readOptionalPositiveInt(scanner, "SKS baru", current.getCredits());
        String category = readOptionalText(scanner, "Kategori baru", current.getCategory());
        String track = readOptionalText(scanner, "Track baru", current.getTrack());
        String difficulty = readOptionalText(scanner, "Difficulty baru", current.getDifficulty());

        Course updatedCourse = new Course(code, name, semester, credits, category, track, difficulty);
        try {
            graph.updateCourse(code, updatedCourse);
            System.out.println("Mata kuliah berhasil diupdate.");
            return true;
        } catch (IllegalArgumentException error) {
            System.out.println("Gagal update mata kuliah: " + error.getMessage());
            return false;
        }
    }

    private static boolean deleteCourse(Scanner scanner, CourseGraph graph) {
        System.out.println("Delete mata kuliah");
        String code = readRequiredText(scanner, "Kode mata kuliah yang akan dihapus: ").toUpperCase();
        Course course = graph.getCourse(code);
        if (course == null) {
            System.out.println("Kode mata kuliah tidak ditemukan: " + code);
            return false;
        }

        System.out.println("Data yang akan dihapus: " + course);
        System.out.print("Yakin hapus mata kuliah dan semua edge terkait? (y/n): ");
        String confirmation = scanner.nextLine().trim();
        if (!confirmation.equalsIgnoreCase("y")) {
            System.out.println("Delete dibatalkan.");
            return false;
        }

        try {
            graph.deleteCourse(code);
            System.out.println("Mata kuliah berhasil dihapus beserta edge terkait.");
            return true;
        } catch (IllegalArgumentException error) {
            System.out.println("Gagal delete mata kuliah: " + error.getMessage());
            return false;
        }
    }

    private static String readRequiredText(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input tidak boleh kosong.");
        }
    }

    private static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                int number = Integer.parseInt(value);
                if (number > 0) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // handled by message below
            }
            System.out.println("Input harus berupa angka positif.");
        }
    }

    private static String readOptionalText(Scanner scanner, String label, String currentValue) {
        System.out.print(label + " [" + currentValue + "]: ");
        String value = scanner.nextLine().trim();
        return value.isEmpty() ? currentValue : value;
    }

    private static int readOptionalPositiveInt(Scanner scanner, String label, int currentValue) {
        while (true) {
            System.out.print(label + " [" + currentValue + "]: ");
            String value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                return currentValue;
            }

            try {
                int number = Integer.parseInt(value);
                if (number > 0) {
                    return number;
                }
            } catch (NumberFormatException ignored) {
                // handled by message below
            }
            System.out.println("Input harus berupa angka positif atau kosong untuk mempertahankan nilai lama.");
        }
    }
}
