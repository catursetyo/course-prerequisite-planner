# Course Prerequisite Planner

Data Structure and OOP Final Project, Topic 9: Course Prerequisite Planner.

## Folder Structure

```text
FP_Strukdat_KelompokXX/
├── src/
│   ├── Main.java
│   ├── graph/
│   │   └── CourseGraph.java
│   ├── tree/
│   │   ├── Trie.java
│   │   └── TrieNode.java
│   └── model/
│       └── Course.java
├── data/
│   ├── courses.csv
│   └── prerequisites.csv
├── docs/
│   └── README_docs.txt
└── README.md
```

## How to Compile

Run this from the `FP_Strukdat_KelompokXX` folder:

```bash
javac -d out src/Main.java src/model/Course.java src/graph/CourseGraph.java src/tree/Trie.java src/tree/TrieNode.java
```

## How to Run

```bash
java -cp out Main
```

Or, if the dataset path is different:

```bash
java -cp out Main data/courses.csv data/prerequisites.csv
```
