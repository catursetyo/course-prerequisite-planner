import java.util.*;

public class CourseGraphAdjacencyList {
    private final Map<String, List<String>> adjacencyList = new LinkedHashMap<>();

    public CourseGraphAdjacencyList() {
        adjacencyList.put("ET234101", new ArrayList<>(Arrays.asList("ET234405", "ET234711")));
        adjacencyList.put("ET234102", new ArrayList<>(Arrays.asList("ET234201")));
        adjacencyList.put("ET234103", new ArrayList<>(Arrays.asList("ET234203", "ET234303", "ET234305")));
        adjacencyList.put("EE234101", new ArrayList<>());
        adjacencyList.put("SM234101", new ArrayList<>(Arrays.asList("SM234201")));
        adjacencyList.put("SF234204", new ArrayList<>());
        adjacencyList.put("ET234104", new ArrayList<>());
        adjacencyList.put("ET234201", new ArrayList<>(Arrays.asList("ET234301", "ET234303", "ET234306", "ET234712")));
        adjacencyList.put("SM234201", new ArrayList<>());
        adjacencyList.put("ET234202", new ArrayList<>());
        adjacencyList.put("ET234203", new ArrayList<>(Arrays.asList("ET234305", "ET234506")));
        adjacencyList.put("ET234204", new ArrayList<>(Arrays.asList("ET234404", "ET234711")));
        adjacencyList.put("ET234301", new ArrayList<>(Arrays.asList("ET234713")));
        adjacencyList.put("ET234302", new ArrayList<>(Arrays.asList("ET234615", "ET234717", "ET234713")));
        adjacencyList.put("ET234303", new ArrayList<>(Arrays.asList("ET234504", "ET234816")));
        adjacencyList.put("ET234304", new ArrayList<>());
        adjacencyList.put("ET234305", new ArrayList<>(Arrays.asList("ET234502", "ET234602", "ET234712", "ET234506")));
        adjacencyList.put("ET234306", new ArrayList<>(Arrays.asList("ET234615", "ET234616")));
        adjacencyList.put("ET234401", new ArrayList<>(Arrays.asList("ET234714")));
        adjacencyList.put("ET234402", new ArrayList<>(Arrays.asList("ET234714")));
        adjacencyList.put("ET234403", new ArrayList<>(Arrays.asList("ET234505", "ET234718")));
        adjacencyList.put("ET234404", new ArrayList<>(Arrays.asList("ET234711")));
        adjacencyList.put("ET234405", new ArrayList<>(Arrays.asList("ET234814")));
        adjacencyList.put("ET234406", new ArrayList<>(Arrays.asList("ET234602")));
        adjacencyList.put("ET234501", new ArrayList<>(Arrays.asList("ET234813")));
        adjacencyList.put("ET234502", new ArrayList<>());
        adjacencyList.put("ET234503", new ArrayList<>());
        adjacencyList.put("ET234504", new ArrayList<>(Arrays.asList("ET234814", "ET234816")));
        adjacencyList.put("ET234505", new ArrayList<>(Arrays.asList("ET234718")));
        adjacencyList.put("ET234506", new ArrayList<>());
        adjacencyList.put("UG234905", new ArrayList<>());
        adjacencyList.put("UG234904", new ArrayList<>());
        adjacencyList.put("UG234901", new ArrayList<>());
        adjacencyList.put("UG234903", new ArrayList<>());
        adjacencyList.put("UG234902", new ArrayList<>());
        adjacencyList.put("UG234906", new ArrayList<>());
        adjacencyList.put("UG234913", new ArrayList<>());
        adjacencyList.put("UG234916", new ArrayList<>());
        adjacencyList.put("ET234601", new ArrayList<>());
        adjacencyList.put("ET234602", new ArrayList<>(Arrays.asList("ET234801")));
        adjacencyList.put("ET234611", new ArrayList<>());
        adjacencyList.put("ET234612", new ArrayList<>());
        adjacencyList.put("ET234614", new ArrayList<>());
        adjacencyList.put("ET234615", new ArrayList<>());
        adjacencyList.put("ET234616", new ArrayList<>());
        adjacencyList.put("ET234701", new ArrayList<>());
        adjacencyList.put("UG234911", new ArrayList<>());
        adjacencyList.put("UG234914", new ArrayList<>());
        adjacencyList.put("UG234912", new ArrayList<>());
        adjacencyList.put("UG234915", new ArrayList<>());
        adjacencyList.put("ET234718", new ArrayList<>());
        adjacencyList.put("ET234702", new ArrayList<>(Arrays.asList("ET234801")));
        adjacencyList.put("ET234703", new ArrayList<>());
        adjacencyList.put("ET234711", new ArrayList<>());
        adjacencyList.put("ET234712", new ArrayList<>());
        adjacencyList.put("ET234713", new ArrayList<>());
        adjacencyList.put("ET234714", new ArrayList<>());
        adjacencyList.put("ET234715", new ArrayList<>());
        adjacencyList.put("ET234716", new ArrayList<>());
        adjacencyList.put("ET234717", new ArrayList<>());
        adjacencyList.put("ET234801", new ArrayList<>());
        adjacencyList.put("ET234811", new ArrayList<>());
        adjacencyList.put("ET234812", new ArrayList<>());
        adjacencyList.put("ET234813", new ArrayList<>());
        adjacencyList.put("ET234814", new ArrayList<>());
        adjacencyList.put("ET234815", new ArrayList<>());
        adjacencyList.put("ET234816", new ArrayList<>());
        adjacencyList.put("ET234821", new ArrayList<>());
        adjacencyList.put("ET234822", new ArrayList<>());
        adjacencyList.put("ET234823", new ArrayList<>());
    }

    public Map<String, List<String>> getAdjacencyList() {
        return adjacencyList;
    }

    public void printAdjacencyList() {
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        CourseGraphAdjacencyList graph = new CourseGraphAdjacencyList();
        graph.printAdjacencyList();
    }
}
