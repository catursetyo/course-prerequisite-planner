package tree;

import java.util.LinkedHashSet;
import java.util.Set;

public class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String key, String courseCode) {
        TrieNode current = root;
        String normalizedKey = normalize(key);

        for (int i = 0; i < normalizedKey.length(); i++) {
            char character = normalizedKey.charAt(i);
            current = current.getChildren().computeIfAbsent(character, ignored -> new TrieNode());
            current.getCourseCodes().add(courseCode);
        }
    }

    public Set<String> searchByPrefix(String prefix) {
        TrieNode current = root;
        String normalizedPrefix = normalize(prefix);

        for (int i = 0; i < normalizedPrefix.length(); i++) {
            char character = normalizedPrefix.charAt(i);
            current = current.getChildren().get(character);
            if (current == null) {
                return new LinkedHashSet<>();
            }
        }

        return new LinkedHashSet<>(current.getCourseCodes());
    }

    private String normalize(String text) {
        return text == null ? "" : text.toLowerCase().trim();
    }
}
