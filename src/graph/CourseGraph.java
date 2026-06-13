package graph;

import model.Course;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Collection;
import java.util.Set;

public class CourseGraph {
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
                int semester = parseInt(getRequiredValue(values, header, "semester", row), "semester", row);
                int credits = parseInt(getRequiredValue(values, header, "sks", row), "sks", row);
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
        validateCourseExists(prerequisiteCode);
        validateCourseExists(courseCode);

        if (prerequisiteCode.equals(courseCode)) {
            throw new IllegalArgumentException("Mata kuliah tidak boleh menjadi prasyarat dirinya sendiri: " + courseCode);
        }

        List<String> nextCourses = adjacencyList.get(prerequisiteCode);
        if (nextCourses.contains(courseCode)) {
            return false;
        }

        nextCourses.add(courseCode);
        reverseAdjacencyList.get(courseCode).add(prerequisiteCode);

        if (hasCycle()) {
            nextCourses.remove(courseCode);
            reverseAdjacencyList.get(courseCode).remove(prerequisiteCode);
            throw new IllegalArgumentException("Relasi menyebabkan cycle dan dibatalkan: " + prerequisiteCode + " -> " + courseCode);
        }

        return true;
    }

    public boolean updateCourse(String code, Course updatedCourse) {
        validateCourseExists(code);
        if (updatedCourse == null) {
            throw new IllegalArgumentException("Data mata kuliah baru tidak boleh kosong.");
        }
        if (!code.equals(updatedCourse.getCode())) {
            throw new IllegalArgumentException("Kode mata kuliah tidak boleh diubah.");
        }

        courses.put(code, updatedCourse);
        return true;
    }

    public boolean deleteCourse(String code) {
        validateCourseExists(code);

        courses.remove(code);
        adjacencyList.remove(code);
        reverseAdjacencyList.remove(code);

        for (List<String> neighbors : adjacencyList.values()) {
            neighbors.remove(code);
        }
        for (List<String> prerequisites : reverseAdjacencyList.values()) {
            prerequisites.remove(code);
        }

        return true;
    }

    private void validateCourseExists(String code) {
        if (!courses.containsKey(code)) {
            throw new IllegalArgumentException("Kode mata kuliah tidak ditemukan di courses.csv: " + code);
        }
    }

    public Course getCourse(String code) {
        return courses.get(code);
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
        validateCourseExists(courseCode);
        return new ArrayList<>(reverseAdjacencyList.get(courseCode));
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
        for (Course course : courses.values()) {
            System.out.println(course);
        }
    }

    public void printAdjacencyList() {
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            Course fromCourse = courses.get(entry.getKey());
            System.out.print(entry.getKey() + " (" + fromCourse.getName() + ") -> ");

            if (entry.getValue().isEmpty()) {
                System.out.println("[]");
                continue;
            }

            List<String> formattedNeighbors = new ArrayList<>();
            for (String neighborCode : entry.getValue()) {
                Course neighborCourse = courses.get(neighborCode);
                formattedNeighbors.add(neighborCode + " (" + neighborCourse.getName() + ")");
            }
            System.out.println(formattedNeighbors);
        }
    }

    public void printDirectPrerequisites(String courseCode) {
        validateCourseExists(courseCode);
        List<String> prerequisites = reverseAdjacencyList.get(courseCode);

        System.out.println("Prasyarat langsung untuk " + courses.get(courseCode));
        if (prerequisites.isEmpty()) {
            System.out.println("- Tidak ada prasyarat langsung.");
            return;
        }

        for (String prerequisiteCode : prerequisites) {
            System.out.println("- " + courses.get(prerequisiteCode));
        }
    }

    public Set<String> getAllPrerequisites(String courseCode) {
        validateCourseExists(courseCode);
        Set<String> result = new LinkedHashSet<>();
        dfsPrerequisites(courseCode, result);
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

        Queue<String> queue = new ArrayDeque<>();
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

    private Map<String, Integer> getHeaderIndex(String headerLine) {
        List<String> headers = parseCsvLine(headerLine);
        Map<String, Integer> headerIndex = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            headerIndex.put(headers.get(i).trim(), i);
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

    private int parseInt(String value, String column, int row) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Baris " + row + " kolom " + column + " harus berupa angka: " + value);
        }
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
