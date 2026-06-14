# Course Prerequisite Planner

Data Structure and OOP Final Project
Topic 9: Course Prerequisite Planner.

Project ini merupakan aplikasi berbasis Java CLI untuk membantu menentukan urutan pengambilan mata kuliah berdasarkan hubungan prasyarat. Project menggunakan struktur data Directed Graph untuk relasi prasyarat dan Trie untuk pencarian mata kuliah berdasarkan prefix kode atau nama. Struktur folder repo menunjukkan file utama berada di ```src/Main.java, src/graph/CourseGraph.java, src/tree/Trie.java, src/tree/TrieNode.java, dan src/model/Course.java```

## Folder Structure

```text
FP_Strukdat_Kelompok9/
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

Run this from the root project folder:

```bash
javac -d out src/Main.java src/model/Course.java src/graph/CourseGraph.java src/tree/Trie.java src/tree/TrieNode.java
```

Or, if you're using Linux, you can do this instead:

```bash
javac -d out $(find src -name "*.java")
```

## How to Run

```bash
java -cp out Main
```

Or, if the dataset path is different:

```bash
java -cp out Main data/courses.csv data/prerequisites.csv
```
## Nama Anggota Kelompok

1. Catur Setyo Ragil - 502725066
2. Aura Syahzanani A - 5027251123
3. ....
4. ....
5. .....

## Deskripsi Masalah

Dalam kurikulum perkuliahan, banyak mata kuliah yang memiliki prasyarat. Contohnya, mahasiswa harus mengambil mata kuliah dasar terlebih dahulu sebelum mengambil mata kuliah lanjutan. Jika hubungan prasyarat tidak dikelola dengan baik, mahasiswa dapat mengalami kesalahan dalam menyusun rencana studi.

Masalah yang diselesaikan oleh program ini adalah:

1. Mencari mata kuliah berdasarkan kode atau nama.
2. Menampilkan prasyarat langsung dari suatu mata kuliah.
3. Menampilkan seluruh rantai prasyarat langsung dan tidak langsung.
4. Membuat rekomendasi urutan pengambilan mata kuliah.
5. Mendeteksi konflik kurikulum berupa siklus prasyarat.
6. Mensimulasikan penambahan, update, dan penghapusan mata kuliah.

Program dibuat dalam bentuk CLI sehingga pengguna dapat memilih fitur melalui menu terminal. Menu program mencakup pencarian mata kuliah, adjacency list, prasyarat langsung, semua prasyarat, topological sort, deteksi cycle, tambah mata kuliah, tambah relasi, update, dan delete mata kuliah

## Dataset

Dataset disimpan dalam file CSV, bukan hardcoded di dalam kode Java. File utama yang digunakan adalah:

```data/courses.csv
data/prerequisites.csv```

Berdasarkan dokumentasi implementasi project, dataset berisi :

| Jenis Data       | Jumlah |
| ---------------- | -----: |
| Mata kuliah      |     70 |
| Relasi prasyarat |     40 |

Jumlah tersebut sudah memenuhi requirement minimal, yaitu minimal 25 node/vertex dan 40 edge/relasi prasyarat.

Data mata kuliah dibaca dari courses.csv, sedangkan data relasi prasyarat dibaca dari prerequisites.csv. Program membaca data tersebut saat runtime menggunakan class CourseGraph

Contoh format data mata kuliah:

| course_code | course_name       | semester | sks | category | track | difficulty |
| ----------- | ----------------- | -------: | --: | -------- | ----- | ---------- |
| ET234103    | Dasar Pemrograman |        1 |   3 | Wajib    | Umum  | Medium     |

Contoh format relasi prasyarat:

| prerequisite_code | course_code |
| ----------------- | ----------- |
| ET234103          | ET234203    |

**Analisis Struktur Data**

Pada sistem **Course Prerequisite Planner**, struktur data utama yang digunakan adalah **Graph directed** dan **Trie**.

- **Graph directed** digunakan untuk merepresentasikan hubungan prasyarat antar mata kuliah.
  - Node/vertex merepresentasikan mata kuliah.
  - Edge merepresentasikan relasi prasyarat.
  - Arah edge menunjukkan ketergantungan, misalnya `A -> B` berarti mata kuliah A harus diambil sebelum B.

- **Trie** digunakan untuk pencarian mata kuliah berdasarkan prefix nama atau kode mata kuliah.
  - Setiap karakter pada kode/nama mata kuliah disimpan sebagai node Trie.
  - Trie memudahkan pencarian prefix seperti `IF`, `IF2`, atau `MAT`.

**Notasi Kompleksitas**

| Simbol | Keterangan |
|---|---|
| `V` | Jumlah vertex/node, yaitu jumlah mata kuliah |
| `E` | Jumlah edge, yaitu jumlah relasi prasyarat |
| `L` | Panjang string kode/nama mata kuliah |
| `K` | Jumlah prasyarat langsung yang diproses |
| `N` | Jumlah data mata kuliah pada Trie |

## Struktur Graph yang Digunakan

Struktur graph yang digunakan adalah Directed Graph.

Pada program, node merepresentasikan mata kuliah, sedangkan edge merepresentasikan relasi prasyarat. Arah edge adalah:

prerequisite_code -> course_code

Artinya:

A -> B

berarti mata kuliah A adalah prasyarat untuk mata kuliah B.

Implementasi graph berada pada file:

src/graph/CourseGraph.java

Struktur data utama yang digunakan dalam CourseGraph adalah:

Map<String, Course> courses;
Map<String, List<String>> adjacencyList;
Map<String, List<String>> reverseAdjacencyList;

Fungsi masing-masing struktur:

| Struktur               | Fungsi                                                         |
| ---------------------- | -------------------------------------------------------------- |
| `courses`              | Menyimpan data mata kuliah berdasarkan kode                    |
| `adjacencyList`        | Menyimpan relasi dari prasyarat ke mata kuliah lanjutan        |
| `reverseAdjacencyList` | Menyimpan relasi dari mata kuliah ke daftar prasyarat langsung |

Program menggunakan adjacencyList untuk topological sort dan cycle detection, sedangkan reverseAdjacencyList digunakan untuk mencari prasyarat langsung maupun tidak langsung.

## Struktur Tree yang Digunakan

Struktur tree yang digunakan adalah Trie.

Trie digunakan untuk fitur pencarian mata kuliah berdasarkan prefix kode atau nama. Implementasinya berada pada:

src/tree/Trie.java
src/tree/TrieNode.java

Pada program, kode dan nama mata kuliah dimasukkan ke Trie. Setiap node pada Trie menyimpan kumpulan kode mata kuliah yang memiliki prefix tersebut.

Contoh:

Input prefix: ET234

Program akan mencari semua mata kuliah yang kode atau namanya diawali dengan ET234.

Method utama pada Trie:

insert(String key, String courseCode)
searchByPrefix(String prefix)

Program melakukan normalisasi input dengan mengubah teks menjadi lowercase dan menghapus spasi di awal/akhir. Setelah prefix ditemukan, program mengembalikan kumpulan kode mata kuliah yang cocok.

## Algoritma yang digunakan

**a. DFS untuk Menampilkan Semua Prasyarat**

DFS digunakan untuk mencari semua prasyarat dari suatu mata kuliah. Program menelusuri reverseAdjacencyList, karena struktur tersebut menyimpan relasi dari mata kuliah ke prasyaratnya

Contoh :

ET234103 -> ET234203 -> ET234305

Jika dicari semua prasyarat dari ET234305, maka hasilnya adalah:

ET234203
ET234103

**b. Cycle Detection**

Cycle detection digunakan untuk mendeteksi konflik prasyarat. Program menggunakan DFS dengan status kunjungan:

| Status | Arti               |
| -----: | ------------------ |
|      0 | Belum dikunjungi   |
|      1 | Sedang dikunjungi  |
|      2 | Selesai dikunjungi |

Jika saat DFS ditemukan node dengan status 1, maka graph memiliki cycle.

A -> B
B -> A

Relasi tersebut tidak valid karena A membutuhkan B, tetapi B juga membutuhkan A.

**c. Topological Sort**

Topological Sort digunakan untuk menentukan rekomendasi urutan pengambilan mata kuliah. Program menggunakan Kahn’s Algorithm, yaitu algoritma yang memanfaatkan indegree dan queue.

Langkah umumnya:

1. Hitung indegree semua mata kuliah.
2. Masukkan mata kuliah dengan indegree 0 ke queue.
3. Ambil satu mata kuliah dari queue.
4. Kurangi indegree mata kuliah lanjutannya.
5. Jika indegree menjadi 0, masukkan ke queue.
6. Ulangi sampai semua mata kuliah diproses.

Jika graph memiliki cycle, program tidak menjalankan topological sort karena urutan pengambilan mata kuliah tidak valid. Implementasi topologicalSort() pada CourseGraph memanggil hasCycle() terlebih dahulu.

## Design Decision Log

| Keputusan Desain                    | Alasan                                                                                               |
| ----------------------------------- | ---------------------------------------------------------------------------------------------------- |
| Menggunakan directed graph          | Relasi prasyarat memiliki arah yang jelas, yaitu dari mata kuliah prasyarat ke mata kuliah lanjutan. |
| Menggunakan adjacency list          | Lebih efisien untuk graph dengan jumlah relasi yang tidak terlalu padat.                             |
| Menggunakan reverse adjacency list  | Memudahkan pencarian prasyarat langsung dan tidak langsung dari suatu mata kuliah.                   |
| Menggunakan Trie                    | Pencarian berdasarkan prefix kode/nama mata kuliah menjadi lebih cepat dan terstruktur.              |
| Menggunakan Kahn’s Algorithm        | Cocok untuk membuat urutan pengambilan mata kuliah pada graph DAG.                                   |
| Menolak edge yang menyebabkan cycle | Agar sistem tidak menghasilkan urutan mata kuliah yang tidak valid.                                  |
| Data disimpan dalam CSV             | Data mudah diubah tanpa perlu mengubah source code Java.                                             |
| Add/update/delete hanya di memori   | Sesuai kebutuhan simulasi penambahan mata kuliah baru; perubahan belum disimpan permanen ke CSV.     |

## Tracing manual


