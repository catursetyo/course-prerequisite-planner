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
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.credits = credits;
        this.category = category;
        this.track = track;
        this.difficulty = difficulty;
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
        return code + " - " + name + " | Sem " + semester + " | " + credits + " SKS | " + category + " | " + difficulty;
    }
}
