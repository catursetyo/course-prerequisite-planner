package graph;

import model.Course;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class CourseGraph {
    private static final int TABLE_WIDTH = 119;
    private static final int NAME_WIDTH = 47;
    private static final int CATEGORY_WIDTH = 20;
    private static final int TRACK_WIDTH = 11;
    private static final int DIFFICULTY_WIDTH = 10;

    private final Map<String, Course> courses = new LinkedHashMap<>();
    private final Map<String, List<String>> adjacencyList = new LinkedHashMap<>();
    private final Map<String, List<String>> reverseAdjacencyList = new LinkedHashMap<>();

    public CourseGraph(String coursesCsvPath, String prerequisitesCsvPath) throws IOException {
        loadCoursesFromCsv(coursesCsvPath);
        loadPrerequisitesFromCsv(prerequisitesCsvPath);
    }

    public void loadCoursesFromCsv(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("File courses kosong: " + filePath);
            }

            Map<String, Integer> header = getHeaderIndex(headerLine);
            requireColumns(header, "course_code", "course_name", "semester", "sks", "category", "difficulty");

            String line;
            int row = 1;
            while ((line = reader.readLine()) != null) {
                row++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);
                String code = getRequiredValue(values, header, "course_code", row);
                String name = getRequiredValue(values, header, "course_name", row);
                int semester = parsePositiveInt(getRequiredValue(values, header, "semester", row), "semester", row);
                int credits = parsePositiveInt(getRequiredValue(values, header, "sks", row), "sks", row);
                String category = getRequiredValue(values, header, "category", row);
                String track = header.containsKey("track") ? getRequiredValue(values, header, "track", row) : "-";
                String difficulty = getRequiredValue(values, header, "difficulty", row);

                addCourse(new Course(code, name, semester, credits, category, track, difficulty));
            }
        }
    }

    public void loadPrerequisitesFromCsv(String filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("File prerequisites kosong: " + filePath);
            }

            Map<String, Integer> header = getHeaderIndex(headerLine);
            requireColumns(header, "prerequisite_code", "course_code");

            String line;
            int row = 1;
            while ((line = reader.readLine()) != null) {
                row++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> values = parseCsvLine(line);
                String prerequisiteCode = getRequiredValue(values, header, "prerequisite_code", row);
                String courseCode = getRequiredValue(values, header, "course_code", row);

                addEdge(prerequisiteCode, courseCode);
            }
        }
    }

    public void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Data mata kuliah tidak boleh kosong.");
        }
        if (courses.containsKey(course.getCode())) {
            throw new IllegalArgumentException("Kode mata kuliah sudah ada: " + course.getCode());
        }
        courses.put(course.getCode(), course);
        adjacencyList.putIfAbsent(course.getCode(), new ArrayList<>());
        reverseAdjacencyList.putIfAbsent(course.getCode(), new ArrayList<>());
    }

    public boolean addEdge(String prerequisiteCode, String courseCode) {
        String normalizedPrerequisiteCode = validateCourseExists(prerequisiteCode);
        String normalizedCourseCode = validateCourseExists(courseCode);

        if (normalizedPrerequisiteCode.equals(normalizedCourseCode)) {
            throw new IllegalArgumentException("Mata kuliah tidak boleh menjadi prasyarat dirinya sendiri: " + normalizedCourseCode);
        }

        List<String> nextCourses = adjacencyList.get(normalizedPrerequisiteCode);
        if (nextCourses.contains(normalizedCourseCode)) {
            return false;
        }

        nextCourses.add(normalizedCourseCode);
        reverseAdjacencyList.get(normalizedCourseCode).add(normalizedPrerequisiteCode);

        if (hasCycle()) {
            nextCourses.remove(normalizedCourseCode);
            reverseAdjacencyList.get(normalizedCourseCode).remove(normalizedPrerequisiteCode);
            throw new IllegalArgumentException("Relasi menyebabkan cycle dan dibatalkan: " + normalizedPrerequisiteCode + " -> " + normalizedCourseCode);
        }

        return true;
    }

    public boolean updateCourse(String code, Course updatedCourse) {
        String normalizedCode = validateCourseExists(code);
        if (updatedCourse == null) {
            throw new IllegalArgumentException("Data mata kuliah baru tidak boleh kosong.");
        }
        if (!normalizedCode.equals(updatedCourse.getCode())) {
            throw new IllegalArgumentException("Kode mata kuliah tidak boleh diubah.");
        }

        courses.put(normalizedCode, updatedCourse);
        return true;
    }

    public boolean deleteCourse(String code) {
        String normalizedCode = validateCourseExists(code);

        courses.remove(normalizedCode);
        adjacencyList.remove(normalizedCode);
        reverseAdjacencyList.remove(normalizedCode);

        for (List<String> neighbors : adjacencyList.values()) {
            neighbors.remove(normalizedCode);
        }
        for (List<String> prerequisites : reverseAdjacencyList.values()) {
            prerequisites.remove(normalizedCode);
        }

        return true;
    }

    private String validateCourseExists(String code) {
        String normalizedCode = normalizeCode(code);
        if (!courses.containsKey(normalizedCode)) {
            throw new IllegalArgumentException("Kode mata kuliah tidak ditemukan di courses.csv: " + normalizedCode);
        }
        return normalizedCode;
    }

    public Course getCourse(String code) {
        return courses.get(normalizeCode(code));
    }

    public Map<String, Course> getCourses() {
        return courses;
    }

    public Collection<Course> getAllCourses() {
        return courses.values();
    }

    public Map<String, List<String>> getAdjacencyList() {
        return adjacencyList;
    }

    public Map<String, List<String>> getReverseAdjacencyList() {
        return reverseAdjacencyList;
    }

    public List<String> getDirectPrerequisites(String courseCode) {
        String normalizedCode = validateCourseExists(courseCode);
        return new ArrayList<>(reverseAdjacencyList.get(normalizedCode));
    }

    public int getTotalCourses() {
        return courses.size();
    }

    public int getTotalEdges() {
        int total = 0;
        for (List<String> neighbors : adjacencyList.values()) {
            total += neighbors.size();
        }
        return total;
    }

    public void printAllCourses() {
        printCourseTable(courses.values());
    }

    public void printCourseTable(Collection<Course> courseList) {
        printDivider();
        System.out.printf("%-9s | %-47s | %3s | %3s | %-20s | %-11s | %-10s%n",
                "Kode", "Nama Mata Kuliah", "Sem", "SKS", "Kategori", "Track", "Difficulty");
        printDivider();
        for (Course course : courseList) {
            System.out.printf("%-9s | %-47s | %3d | %3d | %-20s | %-11s | %-10s%n",
                    course.getCode(),
                    truncate(course.getName(), NAME_WIDTH),
                    course.getSemester(),
                    course.getCredits(),
                    truncate(course.getCategory(), CATEGORY_WIDTH),
                    truncate(course.getTrack(), TRACK_WIDTH),
                    truncate(course.getDifficulty(), DIFFICULTY_WIDTH));
        }
        printDivider();
    }

    public void printAdjacencyList() {
        printDivider();
        System.out.println("Adjacency List: prerequisite_code -> course_code");
        printDivider();
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            Course fromCourse = courses.get(entry.getKey());
            System.out.println(entry.getKey() + " - " + fromCourse.getName());

            if (entry.getValue().isEmpty()) {
                System.out.println("  -> Tidak ada mata kuliah lanjutan langsung.");
                continue;
            }

            for (String neighborCode : entry.getValue()) {
                Course neighborCourse = courses.get(neighborCode);
                System.out.println("  -> " + neighborCode + " - " + neighborCourse.getName());
            }
        }
        printDivider();
    }

    public void printDirectPrerequisites(String courseCode) {
        String normalizedCode = validateCourseExists(courseCode);
        List<String> prerequisites = reverseAdjacencyList.get(normalizedCode);

        System.out.println("Prasyarat langsung untuk " + courses.get(normalizedCode));
        if (prerequisites.isEmpty()) {
            System.out.println("- Tidak ada prasyarat langsung.");
            return;
        }

        for (String prerequisiteCode : prerequisites) {
            System.out.println("- " + courses.get(prerequisiteCode));
        }
    }

    public Set<String> getAllPrerequisites(String courseCode) {
        String normalizedCode = validateCourseExists(courseCode);
        Set<String> result = new LinkedHashSet<>();
        dfsPrerequisites(normalizedCode, result);
        return result;
    }

    private void dfsPrerequisites(String courseCode, Set<String> result) {
        for (String prerequisiteCode : reverseAdjacencyList.get(courseCode)) {
            if (result.add(prerequisiteCode)) {
                dfsPrerequisites(prerequisiteCode, result);
            }
        }
    }

    public boolean hasCycle() {
        Map<String, Integer> state = new LinkedHashMap<>();
        for (String code : courses.keySet()) {
            state.put(code, 0);
        }

        for (String code : courses.keySet()) {
            if (state.get(code) == 0 && hasCycleDfs(code, state)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCycleDfs(String code, Map<String, Integer> state) {
        state.put(code, 1);

        for (String neighbor : adjacencyList.get(code)) {
            if (state.get(neighbor) == 1) {
                return true;
            }
            if (state.get(neighbor) == 0 && hasCycleDfs(neighbor, state)) {
                return true;
            }
        }

        state.put(code, 2);
        return false;
    }

    public List<String> topologicalSort() {
        if (hasCycle()) {
            throw new IllegalStateException("Graph memiliki cycle, topological sort tidak bisa dijalankan.");
        }

        Map<String, Integer> indegree = new LinkedHashMap<>();
        for (String code : courses.keySet()) {
            indegree.put(code, 0);
        }

        for (List<String> neighbors : adjacencyList.values()) {
            for (String neighbor : neighbors) {
                indegree.put(neighbor, indegree.get(neighbor) + 1);
            }
        }

        Queue<String> queue = new PriorityQueue<>(courseOrderComparator());
        for (Map.Entry<String, Integer> entry : indegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<String> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            order.add(current);

            for (String neighbor : adjacencyList.get(current)) {
                indegree.put(neighbor, indegree.get(neighbor) - 1);
                if (indegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return order;
    }

    private Comparator<String> courseOrderComparator() {
        return (firstCode, secondCode) -> {
            Course firstCourse = courses.get(firstCode);
            Course secondCourse = courses.get(secondCode);
            int semesterComparison = Integer.compare(firstCourse.getSemester(), secondCourse.getSemester());
            if (semesterComparison != 0) {
                return semesterComparison;
            }
            return firstCode.compareTo(secondCode);
        };
    }

    private Map<String, Integer> getHeaderIndex(String headerLine) {
        List<String> headers = parseCsvLine(headerLine);
        Map<String, Integer> headerIndex = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            headerIndex.put(headers.get(i).trim().toLowerCase(Locale.ROOT), i);
        }
        return headerIndex;
    }

    private void requireColumns(Map<String, Integer> header, String... columns) {
        for (String column : columns) {
            if (!header.containsKey(column)) {
                throw new IllegalArgumentException("Kolom wajib tidak ditemukan: " + column);
            }
        }
    }

    private String getRequiredValue(List<String> values, Map<String, Integer> header, String column, int row) {
        int index = header.get(column);
        if (index >= values.size()) {
            throw new IllegalArgumentException("Baris " + row + " tidak memiliki kolom " + column);
        }

        String value = values.get(index).trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Baris " + row + " memiliki nilai kosong pada kolom " + column);
        }
        return value;
    }

    private int parsePositiveInt(String value, String column, int row) {
        try {
            int number = Integer.parseInt(value);
            if (number <= 0) {
                throw new IllegalArgumentException("Baris " + row + " kolom " + column + " harus lebih dari 0: " + value);
            }
            return number;
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Baris " + row + " kolom " + column + " harus berupa angka: " + value);
        }
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toUpperCase(Locale.ROOT);
    }

    private void printDivider() {
        for (int i = 0; i < TABLE_WIDTH; i++) {
            System.out.print("-");
        }
        System.out.println();
    }

    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        if (maxLength <= 3) {
            return text.substring(0, maxLength);
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuote = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            if (character == '"') {
                if (insideQuote && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    insideQuote = !insideQuote;
                }
            } else if (character == ',' && !insideQuote) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        result.add(current.toString());
        return result;
    }
}
