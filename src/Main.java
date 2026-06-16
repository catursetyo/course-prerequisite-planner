import graph.CourseGraph;
import model.Course;
import tree.Trie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final String DEFAULT_COURSES_FILE = "data/courses.csv";
    private static final String DEFAULT_PREREQUISITES_FILE = "data/prerequisites.csv";
    private static final int MENU_WIDTH = 96;

    public static void main(String[] args) {
        String coursesFile = args.length >= 1 ? args[0] : DEFAULT_COURSES_FILE;
        String prerequisitesFile = args.length >= 2 ? args[1] : DEFAULT_PREREQUISITES_FILE;

        try {
            CourseGraph graph = new CourseGraph(coursesFile, prerequisitesFile);
            Trie trie = buildTrie(graph);
            printStartupSummary(graph, coursesFile, prerequisitesFile);
            runMenu(graph, trie);
        } catch (IOException error) {
            printError("Gagal membaca file CSV: " + error.getMessage());
        } catch (IllegalArgumentException error) {
            printError("Data CSV tidak valid: " + error.getMessage());
        }
    }

    private static Trie buildTrie(CourseGraph graph) {
        Trie trie = new Trie();
        for (Course course : graph.getAllCourses()) {
            indexCourseInTrie(trie, course);
        }
        return trie;
    }

    private static void indexCourseInTrie(Trie trie, Course course) {
        trie.insert(course.getCode(), course.getCode());
        trie.insert(course.getName(), course.getCode());
    }

    private static void removeCourseFromTrie(Trie trie, Course course) {
        trie.delete(course.getCode(), course.getCode());
        trie.delete(course.getName(), course.getCode());
    }

    private static void runMenu(CourseGraph graph, Trie trie) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu(graph);
            System.out.print("Pilih menu: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    printSection("Daftar Mata Kuliah");
                    graph.printAllCourses();
                    break;
                case "2":
                    searchCourse(scanner, graph, trie);
                    break;
                case "3":
                    printSection("Struktur Graph");
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
                    showCycleStatus(graph);
                    break;
                case "8":
                    addCourse(scanner, graph, trie);
                    break;
                case "9":
                    addPrerequisiteRelation(scanner, graph);
                    break;
                case "10":
                    updateCourse(scanner, graph, trie);
                    break;
                case "11":
                    deleteCourse(scanner, graph, trie);
                    break;
                case "0":
                    printSuccess("Program selesai.");
                    return;
                default:
                    printWarning("Menu tidak valid. Pilih angka 0 sampai 11.");
            }

            System.out.println();
        }
    }

    private static void printStartupSummary(CourseGraph graph, String coursesFile, String prerequisitesFile) {
        printLine('=');
        System.out.println(centerText("COURSE PREREQUISITE PLANNER", MENU_WIDTH));
        System.out.println(centerText("Topik 9 - Struktur Data dan OOP", MENU_WIDTH));
        printLine('=');
        System.out.println("Dataset mata kuliah : " + coursesFile);
        System.out.println("Dataset prasyarat   : " + prerequisitesFile);
        System.out.println("Jumlah mata kuliah  : " + graph.getTotalCourses());
        System.out.println("Jumlah relasi       : " + graph.getTotalEdges());
        System.out.println("Status graph        : " + (graph.hasCycle() ? "Memiliki siklus" : "Tidak ada siklus"));
        printLine('=');
    }

    private static void printMenu(CourseGraph graph) {
        System.out.println();
        printLine('=');
        System.out.println(centerText("MENU UTAMA", MENU_WIDTH));
        printLine('=');
        System.out.printf("Mata kuliah: %-3d | Relasi prasyarat: %-3d | Cycle: %s%n",
                graph.getTotalCourses(),
                graph.getTotalEdges(),
                graph.hasCycle() ? "YA" : "TIDAK");
        printLine('-');
        System.out.println(" 1. Tampilkan semua mata kuliah          7. Deteksi siklus prasyarat");
        System.out.println(" 2. Search prefix kode/nama              8. Tambah mata kuliah");
        System.out.println(" 3. Tampilkan adjacency list graph       9. Tambah relasi prasyarat");
        System.out.println(" 4. Tampilkan prasyarat langsung        10. Update mata kuliah");
        System.out.println(" 5. Tampilkan semua prasyarat DFS       11. Delete mata kuliah");
        System.out.println(" 6. Rekomendasi topological sort         0. Keluar");
        printLine('-');
    }

    private static void searchCourse(Scanner scanner, CourseGraph graph, Trie trie) {
        printSection("Search Prefix dengan Trie");
        String prefix = readRequiredText(scanner, "Masukkan prefix kode/nama: ");
        Set<String> results = trie.searchByPrefix(prefix);

        if (results.isEmpty()) {
            printWarning("Tidak ada mata kuliah yang cocok untuk prefix: " + prefix.trim());
            return;
        }

        System.out.println("Prefix       : " + prefix.trim());
        System.out.println("Jumlah hasil : " + trie.countMatches(prefix));
        System.out.println("Sumber data  : Trie untuk pencarian, Graph untuk detail dan relasi");

        List<Course> matchedCourses = new ArrayList<>();
        for (String code : results) {
            Course course = graph.getCourse(code);
            if (course != null) {
                matchedCourses.add(course);
            }
        }
        graph.printCourseTable(matchedCourses);

        System.out.println("Ringkasan relasi hasil search:");
        for (Course course : matchedCourses) {
            int directPrerequisites = graph.getDirectPrerequisites(course.getCode()).size();
            int dependentCourses = graph.getAdjacencyList().get(course.getCode()).size();
            System.out.printf("- %-9s | prasyarat langsung: %d | mata kuliah lanjutan: %d%n",
                    course.getCode(), directPrerequisites, dependentCourses);
        }
    }

    private static void showDirectPrerequisites(Scanner scanner, CourseGraph graph) {
        printSection("Prasyarat Langsung");
        String courseCode = readRequiredText(scanner, "Masukkan kode mata kuliah: ");
        Course target = graph.getCourse(courseCode);
        if (target == null) {
            printWarning("Kode mata kuliah tidak ditemukan: " + courseCode.trim().toUpperCase());
            return;
        }

        graph.printDirectPrerequisites(courseCode);
    }

    private static void showAllPrerequisites(Scanner scanner, CourseGraph graph) {
        printSection("Semua Prasyarat dengan DFS");
        String courseCode = readRequiredText(scanner, "Masukkan kode mata kuliah: ");
        Course target = graph.getCourse(courseCode);
        if (target == null) {
            printWarning("Kode mata kuliah tidak ditemukan: " + courseCode.trim().toUpperCase());
            return;
        }

        Set<String> prerequisites = graph.getAllPrerequisites(courseCode);
        System.out.println("Target: " + target);
        if (prerequisites.isEmpty()) {
            printInfo("Mata kuliah ini tidak memiliki prasyarat.");
            return;
        }

        List<Course> prerequisiteCourses = new ArrayList<>();
        for (String prerequisiteCode : prerequisites) {
            prerequisiteCourses.add(graph.getCourse(prerequisiteCode));
        }
        graph.printCourseTable(prerequisiteCourses);
    }

    private static void showTopologicalSort(CourseGraph graph) {
        printSection("Rekomendasi Urutan Pengambilan Mata Kuliah");
        List<String> order;
        try {
            order = graph.topologicalSort();
        } catch (IllegalStateException error) {
            printError("Tidak bisa membuat rekomendasi karena terdapat siklus prasyarat.");
            return;
        }

        System.out.printf("%-4s | %-9s | %-47s | %3s | %3s%n", "No", "Kode", "Nama Mata Kuliah", "Sem", "SKS");
        printLine('-');
        for (int i = 0; i < order.size(); i++) {
            Course course = graph.getCourse(order.get(i));
            System.out.printf("%04d | %-9s | %-47s | %3d | %3d%n",
                    i + 1,
                    course.getCode(),
                    truncate(course.getName(), 47),
                    course.getSemester(),
                    course.getCredits());
        }
    }

    private static void showCycleStatus(CourseGraph graph) {
        printSection("Cycle Detection");
        if (graph.hasCycle()) {
            printError("Graph memiliki siklus. Topological sort tidak valid sampai siklus dihapus.");
        } else {
            printSuccess("Graph tidak memiliki siklus. Topological sort aman dijalankan.");
        }
    }

    private static void addCourse(Scanner scanner, CourseGraph graph, Trie trie) {
        printSection("Tambah Mata Kuliah");
        String code = readRequiredText(scanner, "Kode: ").toUpperCase();
        if (graph.getCourse(code) != null) {
            printWarning("Kode mata kuliah sudah ada: " + code);
            return;
        }

        String name = readRequiredText(scanner, "Nama: ");
        int semester = readPositiveInt(scanner, "Semester rekomendasi: ");
        int credits = readPositiveInt(scanner, "SKS: ");
        String category = readRequiredText(scanner, "Kategori: ");
        String track = readRequiredText(scanner, "Track: ");
        String difficulty = readRequiredText(scanner, "Difficulty: ");

        try {
            Course newCourse = new Course(code, name, semester, credits, category, track, difficulty);
            graph.addCourse(newCourse);
            indexCourseInTrie(trie, newCourse);
            printSuccess("Mata kuliah berhasil ditambahkan dan diindeks ke Trie.");
        } catch (IllegalArgumentException error) {
            printError("Gagal menambah mata kuliah: " + error.getMessage());
        }
    }

    private static void addPrerequisiteRelation(Scanner scanner, CourseGraph graph) {
        printSection("Tambah Relasi Prasyarat");
        String prerequisiteCode = readRequiredText(scanner, "Kode prasyarat: ").toUpperCase();
        String courseCode = readRequiredText(scanner, "Kode mata kuliah tujuan: ").toUpperCase();

        try {
            boolean added = graph.addEdge(prerequisiteCode, courseCode);
            if (added) {
                printSuccess("Relasi berhasil ditambahkan: " + prerequisiteCode + " -> " + courseCode);
            } else {
                printInfo("Relasi sudah ada, tidak ditambahkan ulang.");
            }
        } catch (IllegalArgumentException error) {
            printError("Gagal menambah relasi: " + error.getMessage());
        }
    }

    private static void updateCourse(Scanner scanner, CourseGraph graph, Trie trie) {
        printSection("Update Mata Kuliah");
        String code = readRequiredText(scanner, "Kode mata kuliah yang akan diupdate: ").toUpperCase();
        Course current = graph.getCourse(code);
        if (current == null) {
            printWarning("Kode mata kuliah tidak ditemukan: " + code);
            return;
        }

        System.out.println("Data saat ini: " + current);
        String name = readOptionalText(scanner, "Nama baru", current.getName());
        int semester = readOptionalPositiveInt(scanner, "Semester rekomendasi baru", current.getSemester());
        int credits = readOptionalPositiveInt(scanner, "SKS baru", current.getCredits());
        String category = readOptionalText(scanner, "Kategori baru", current.getCategory());
        String track = readOptionalText(scanner, "Track baru", current.getTrack());
        String difficulty = readOptionalText(scanner, "Difficulty baru", current.getDifficulty());

        try {
            Course updatedCourse = new Course(current.getCode(), name, semester, credits, category, track, difficulty);
            graph.updateCourse(current.getCode(), updatedCourse);
            removeCourseFromTrie(trie, current);
            indexCourseInTrie(trie, updatedCourse);
            printSuccess("Mata kuliah berhasil diupdate dan indeks Trie diperbarui.");
        } catch (IllegalArgumentException error) {
            printError("Gagal update mata kuliah: " + error.getMessage());
        }
    }

    private static void deleteCourse(Scanner scanner, CourseGraph graph, Trie trie) {
        printSection("Delete Mata Kuliah");
        String code = readRequiredText(scanner, "Kode mata kuliah yang akan dihapus: ").toUpperCase();
        Course course = graph.getCourse(code);
        if (course == null) {
            printWarning("Kode mata kuliah tidak ditemukan: " + code);
            return;
        }

        System.out.println("Data yang akan dihapus: " + course);
        System.out.print("Yakin hapus mata kuliah dan semua edge terkait? (y/n): ");
        String confirmation = scanner.nextLine().trim();
        if (!confirmation.equalsIgnoreCase("y")) {
            printInfo("Delete dibatalkan.");
            return;
        }

        try {
            graph.deleteCourse(course.getCode());
            removeCourseFromTrie(trie, course);
            printSuccess("Mata kuliah berhasil dihapus dari graph dan Trie.");
        } catch (IllegalArgumentException error) {
            printError("Gagal delete mata kuliah: " + error.getMessage());
        }
    }

    private static String readRequiredText(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            printWarning("Input tidak boleh kosong.");
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
            printWarning("Input harus berupa angka positif.");
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
            printWarning("Input harus berupa angka positif atau kosong untuk mempertahankan nilai lama.");
        }
    }

    private static void printSection(String title) {
        printLine('=');
        System.out.println(title);
        printLine('=');
    }

    private static void printSuccess(String message) {
        System.out.println("[OK] " + message);
    }

    private static void printWarning(String message) {
        System.out.println("[WARN] " + message);
    }

    private static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    private static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    private static void printLine(char character) {
        for (int i = 0; i < MENU_WIDTH; i++) {
            System.out.print(character);
        }
        System.out.println();
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int leftPadding = (width - text.length()) / 2;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < leftPadding; i++) {
            builder.append(' ');
        }
        builder.append(text);
        return builder.toString();
    }

    private static String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        if (maxLength <= 3) {
            return text.substring(0, maxLength);
        }
        return text.substring(0, maxLength - 3) + "...";
    }
}
