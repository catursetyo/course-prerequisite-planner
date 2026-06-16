package tree;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String key, String courseCode) {
        TrieNode current = root;
        String normalizedKey = normalizeKey(key);
        String normalizedCourseCode = normalizeCourseCode(courseCode);

        if (normalizedKey.isEmpty() || normalizedCourseCode.isEmpty()) {
            return;
        }

        for (int i = 0; i < normalizedKey.length(); i++) {
            char character = normalizedKey.charAt(i);
            current = current.getChildren().computeIfAbsent(character, ignored -> new TrieNode());
            current.getCourseCodes().add(normalizedCourseCode);
        }
    }

    public Set<String> searchByPrefix(String prefix) {
        TrieNode current = root;
        String normalizedPrefix = normalizeKey(prefix);

        if (normalizedPrefix.isEmpty()) {
            return new LinkedHashSet<>();
        }

        for (int i = 0; i < normalizedPrefix.length(); i++) {
            char character = normalizedPrefix.charAt(i);
            current = current.getChildren().get(character);
            if (current == null) {
                return new LinkedHashSet<>();
            }
        }

        return new LinkedHashSet<>(current.getCourseCodes());
    }

    public int countMatches(String prefix) {
        return searchByPrefix(prefix).size();
    }

    public boolean delete(String key, String courseCode) {
        String normalizedKey = normalizeKey(key);
        String normalizedCourseCode = normalizeCourseCode(courseCode);

        if (normalizedKey.isEmpty() || normalizedCourseCode.isEmpty()) {
            return false;
        }

        TrieNode current = root;
        List<TrieNode> path = new ArrayList<>();
        for (int i = 0; i < normalizedKey.length(); i++) {
            current = current.getChildren().get(normalizedKey.charAt(i));
            if (current == null) {
                return false;
            }
            path.add(current);
        }

        boolean removed = false;
        for (TrieNode node : path) {
            removed = node.getCourseCodes().remove(normalizedCourseCode) || removed;
        }

        for (int i = normalizedKey.length() - 1; i >= 0; i--) {
            TrieNode child = path.get(i);
            if (!child.getChildren().isEmpty() || !child.getCourseCodes().isEmpty()) {
                break;
            }

            TrieNode parent = i == 0 ? root : path.get(i - 1);
            parent.getChildren().remove(normalizedKey.charAt(i));
        }

        return removed;
    }

    private String normalizeKey(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT).trim();
    }

    private String normalizeCourseCode(String courseCode) {
        return courseCode == null ? "" : courseCode.toUpperCase(Locale.ROOT).trim();
    }
}
