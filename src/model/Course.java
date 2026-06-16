package model;

public class Course {
    private final String code;
    private final String name;
    private final int semester;
    private final int credits;
    private final String category;
    private final String track;
    private final String difficulty;

    public Course(String code, String name, int semester, int credits, String category, String track, String difficulty) {
        this.code = requireText(code, "Kode mata kuliah").toUpperCase();
        this.name = requireText(name, "Nama mata kuliah");
        if (semester <= 0) {
            throw new IllegalArgumentException("Semester harus lebih dari 0.");
        }
        if (credits <= 0) {
            throw new IllegalArgumentException("SKS harus lebih dari 0.");
        }
        this.semester = semester;
        this.credits = credits;
        this.category = requireText(category, "Kategori");
        this.track = requireText(track, "Track");
        this.difficulty = requireText(difficulty, "Difficulty");
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getSemester() {
        return semester;
    }

    public int getCredits() {
        return credits;
    }

    public String getCategory() {
        return category;
    }

    public String getTrack() {
        return track;
    }

    public String getDifficulty() {
        return difficulty;
    }

    @Override
    public String toString() {
        return code + " - " + name + " | Sem " + semester + " | " + credits + " SKS | " + category + " | " + track + " | " + difficulty;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " tidak boleh kosong.");
        }
        return value.trim();
    }
}
