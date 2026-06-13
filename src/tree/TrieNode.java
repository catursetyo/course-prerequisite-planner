package tree;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class TrieNode {
    private final Map<Character, TrieNode> children = new LinkedHashMap<>();
    private final Set<String> courseCodes = new LinkedHashSet<>();

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public Set<String> getCourseCodes() {
        return courseCodes;
    }
}
