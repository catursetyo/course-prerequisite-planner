# Course Prerequisite Planner

**Final Project Struktur Data dan OOP 2026**  
**Topik 9 - Course Prerequisite Planner**

## Identitas Kelompok

| No | Nama | NRP |
| --: | ---- | --- |
| 1 | Catur Setyo Ragil | 502725066 |
| 2 | Aura Syahzanani A | 5027251123 |
| 3 | Donnavie Aulia | 5027251093 |
| 4 | Nur Rizki Syahbana | 5027251095 |
| 5 | Nama anggota ke-5 perlu dilengkapi | NRP perlu dilengkapi |

## Ringkasan Project

Course Prerequisite Planner adalah aplikasi Java berbasis CLI untuk membantu menyusun rekomendasi urutan pengambilan mata kuliah berdasarkan relasi prasyarat. Mata kuliah direpresentasikan sebagai node pada directed graph, sedangkan relasi prasyarat direpresentasikan sebagai edge berarah.

Program menggunakan dua struktur data utama:

| Struktur Data | Implementasi | Fungsi |
| --- | --- | --- |
| Directed Graph | `Map<String, List<String>>` adjacency list dan reverse adjacency list | Menyimpan relasi prasyarat, menampilkan graph, DFS prasyarat, cycle detection, dan topological sort |
| Trie | `Trie` dan `TrieNode` buatan sendiri | Search mata kuliah berdasarkan prefix kode atau nama, menghitung jumlah hasil prefix, dan menghapus indeks saat data dihapus/update |

Program tidak menggunakan database, GUI, Maven/Gradle, ataupun library graph eksternal. Semua data utama dibaca dari CSV saat runtime.

## Deskripsi Masalah

Dalam kurikulum perkuliahan, banyak mata kuliah memiliki prasyarat. Kesalahan dalam membaca relasi prasyarat dapat membuat mahasiswa mengambil mata kuliah lanjutan sebelum fondasinya terpenuhi. Relasi prasyarat juga bisa bermasalah jika membentuk siklus, misalnya mata kuliah A membutuhkan B tetapi B juga membutuhkan A.

Masalah yang diselesaikan program ini:

1. Menampilkan semua mata kuliah beserta atributnya.
2. Mencari mata kuliah berdasarkan prefix kode atau nama.
3. Menampilkan struktur graph prasyarat.
4. Menampilkan prasyarat langsung dan tidak langsung suatu mata kuliah.
5. Memberikan rekomendasi urutan pengambilan mata kuliah.
6. Mendeteksi siklus prasyarat.
7. Mensimulasikan insert, update, delete, dan penambahan relasi prasyarat.

## Dataset

Dataset utama berada di folder `data/`:

```text
data/courses.csv
data/prerequisites.csv
```

Jumlah data yang dibaca program:

| Jenis Data | Jumlah |
| --- | --: |
| Mata kuliah/node | 70 |
| Relasi prasyarat/edge | 40 |

Jumlah tersebut memenuhi ketentuan final project: minimal 25 node dan minimal 40 edge.

### Format `courses.csv`

Kolom yang tersedia:

| Kolom | Keterangan |
| --- | --- |
| `course_code` | Kode unik mata kuliah |
| `course_name` | Nama mata kuliah |
| `semester` | Semester rekomendasi |
| `sks` | Total SKS |
| `theory_sks` | SKS teori |
| `practical_sks` | SKS praktikum |
| `category` | Kategori mata kuliah |
| `track` | Jalur/kelompok mata kuliah |
| `difficulty` | Tingkat kesulitan |
| `is_node` | Penanda bahwa baris adalah node graph |

Contoh:

```csv
course_code,course_name,semester,sks,theory_sks,practical_sks,category,track,difficulty,is_node
ET234103,Algoritma dan Teknik Pemrograman,1,4,3,1,Pemrograman,Core,Dasar,TRUE
```

### Format `prerequisites.csv`

Kolom utama yang dipakai program:

| Kolom | Keterangan |
| --- | --- |
| `prerequisite_code` | Kode mata kuliah prasyarat |
| `course_code` | Kode mata kuliah tujuan |

Kolom lain seperti `edge_id`, `relation_type`, `is_official`, dan `relation_reason` dipakai sebagai dokumentasi dataset.

Contoh:

```csv
edge_id,prerequisite_code,prerequisite_name,course_code,course_name,relation_type,is_official,edge_direction,relation_reason
E003,ET234103,Algoritma dan Teknik Pemrograman,ET234203,Struktur Data dan Pemrograman Berorientasi Objek,OFFICIAL,TRUE,prerequisite_code -> course_code,Prasyarat resmi: Struktur Data dan PBO mensyaratkan Algoritma dan Teknik Pemrograman
```

## Struktur Folder

```text
course-prerequisite-planner/
├── src/
│   ├── Main.java
│   ├── graph/
│   │   └── CourseGraph.java
│   ├── model/
│   │   └── Course.java
│   └── tree/
│       ├── Trie.java
│       └── TrieNode.java
├── data/
│   ├── courses.csv
│   ├── prerequisites.csv
│   └── graph_dataset.csv
├── assets/
│   └── screenshot-*.png
├── docs/
├── README.md
└── TRACING.md
```

## Cara Compile dan Run

Jalankan dari root folder project:

```bash
javac -d out $(find src -name "*.java")
java -cp out Main
```

Jika ingin menggunakan path dataset lain:

```bash
java -cp out Main data/courses.csv data/prerequisites.csv
```

Untuk Windows PowerShell:

```powershell
javac -d out (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
java -cp out Main
```

## Desain Class

| File | Tanggung Jawab |
| --- | --- |
| `src/model/Course.java` | Model immutable untuk menyimpan data mata kuliah. Constructor memvalidasi kode, nama, semester, SKS, kategori, track, dan difficulty. |
| `src/graph/CourseGraph.java` | Membaca CSV, menyimpan directed graph, menambah/menghapus/update course, menambah edge, DFS prasyarat, cycle detection, dan topological sort. |
| `src/tree/Trie.java` | Insert, search prefix, count match, dan delete indeks course berdasarkan kode/nama. |
| `src/tree/TrieNode.java` | Node Trie yang menyimpan child map dan daftar kode mata kuliah pada prefix tertentu. |
| `src/Main.java` | Menu CLI, input user, format tabel, integrasi Trie dan Graph. |

## Representasi Graph

Graph yang digunakan adalah directed graph dengan arah edge:

```text
prerequisite_code -> course_code
```

Artinya:

```text
A -> B
A adalah prasyarat untuk B
```

Implementasi di `CourseGraph`:

```java
Map<String, Course> courses;
Map<String, List<String>> adjacencyList;
Map<String, List<String>> reverseAdjacencyList;
```

Fungsi masing-masing struktur:

| Struktur | Fungsi |
| --- | --- |
| `courses` | Menyimpan detail mata kuliah berdasarkan kode |
| `adjacencyList` | Menyimpan relasi dari prasyarat ke mata kuliah lanjutan |
| `reverseAdjacencyList` | Menyimpan relasi dari mata kuliah ke daftar prasyarat langsung |

Alasan memakai adjacency list:

1. Relasi prasyarat cenderung sparse, sehingga adjacency list lebih hemat memori dibanding adjacency matrix.
2. Traversal DFS dan cycle detection berjalan efisien dalam O(V + E), sedangkan topological sort memakai prioritas semester agar urutan rekomendasi lebih natural.
3. Data mudah ditampilkan dalam bentuk `prasyarat -> daftar mata kuliah lanjutan`.

## Struktur Tree

Tree yang digunakan adalah Trie. Setiap karakter dari kode dan nama mata kuliah disimpan sebagai jalur node. Setiap node menyimpan kumpulan kode mata kuliah yang cocok dengan prefix tersebut.

Contoh penggunaan:

```text
Input prefix: ET2343
Output:
ET234301
ET234302
ET234303
ET234304
ET234305
ET234306
```

Operasi Trie yang diimplementasikan:

| Method | Fungsi | Kompleksitas |
| --- | --- | --- |
| `insert(String key, String courseCode)` | Mengindeks kode atau nama mata kuliah | O(L) |
| `searchByPrefix(String prefix)` | Mengambil semua kode mata kuliah pada prefix | O(L + R) |
| `countMatches(String prefix)` | Menghitung jumlah hasil prefix | O(L + R) |
| `delete(String key, String courseCode)` | Menghapus indeks saat course dihapus/update | O(L) |

Keterangan:

- L = panjang kode/nama/prefix.
- R = jumlah hasil yang dikembalikan.

## Algoritma Graph

### 1. DFS untuk Semua Prasyarat

DFS digunakan untuk mencari seluruh prasyarat langsung dan tidak langsung. Traversal dilakukan pada `reverseAdjacencyList` karena struktur tersebut menyimpan relasi dari mata kuliah ke prasyaratnya.

Contoh:

```text
ET234103 -> ET234203 -> ET234305
```

Jika dicari semua prasyarat `ET234305`, maka DFS pada reverse graph menemukan:

```text
ET234203
ET234103
```

### 2. Cycle Detection

Cycle detection menggunakan DFS dengan status kunjungan:

| Status | Arti |
| --: | --- |
| 0 | Belum dikunjungi |
| 1 | Sedang dikunjungi |
| 2 | Selesai dikunjungi |

Jika DFS menemukan edge menuju node berstatus `1`, maka graph memiliki cycle. Program menolak relasi baru yang menyebabkan cycle dan melakukan rollback edge.

### 3. Topological Sort

Topological sort menggunakan Kahn's Algorithm. Implementasi memakai `PriorityQueue` berdasarkan semester rekomendasi dan kode mata kuliah agar course semester rendah diprioritaskan ketika beberapa node sama-sama sudah bebas prasyarat.

1. Hitung indegree setiap mata kuliah.
2. Masukkan semua mata kuliah dengan indegree 0 ke priority queue.
3. Ambil node prioritas tertinggi dari queue dan masukkan ke hasil urutan.
4. Kurangi indegree semua tetangga keluar.
5. Jika indegree tetangga menjadi 0, masukkan ke queue.
6. Ulangi sampai queue kosong.

Program selalu menjalankan cycle detection sebelum topological sort. Jika ada cycle, rekomendasi urutan tidak dibuat.

## Fitur Program

Menu CLI:

```text
1. Tampilkan semua mata kuliah
2. Search prefix kode/nama
3. Tampilkan adjacency list graph
4. Tampilkan prasyarat langsung
5. Tampilkan semua prasyarat DFS
6. Rekomendasi topological sort
7. Deteksi siklus prasyarat
8. Tambah mata kuliah
9. Tambah relasi prasyarat
10. Update mata kuliah
11. Delete mata kuliah
0. Keluar
```

Fitur yang menggabungkan Tree dan Graph terdapat pada menu search prefix. Trie dipakai untuk menemukan kode mata kuliah berdasarkan prefix, lalu Graph dipakai untuk mengambil detail mata kuliah, jumlah prasyarat langsung, dan jumlah mata kuliah lanjutan.

## Design Decision Log

| No | Keputusan | Alternatif | Alasan Memilih | Risiko/Kelemahan |
| --: | --- | --- | --- | --- |
| 1 | Directed graph | Undirected graph | Relasi prasyarat punya arah yang jelas dari prasyarat ke mata kuliah tujuan | Salah arah edge membuat topological sort tidak valid |
| 2 | Adjacency list | Adjacency matrix | Lebih hemat memori untuk graph sparse | Cek edge langsung perlu scan list tetangga |
| 3 | Reverse adjacency list | Traversal mundur dari adjacency normal | Prasyarat langsung dan DFS prasyarat jadi lebih sederhana | Memori bertambah O(E) |
| 4 | Trie untuk search prefix | HashMap biasa | HashMap tidak efisien untuk prefix search | Trie memakai memori lebih besar |
| 5 | Kahn's Algorithm dengan prioritas semester | DFS-based topological sort | Mudah dijelaskan dengan indegree dan hasil lebih natural untuk rekomendasi semester | Operasi queue menjadi O(log V) |
| 6 | Reject edge yang menyebabkan cycle | Membiarkan cycle lalu hanya memberi peringatan | Menjaga data prasyarat tetap valid | Add edge menjadi lebih mahal karena ada cycle check |
| 7 | CSV runtime | Hardcode Java | Dataset bisa diubah tanpa mengubah source code | Format CSV harus divalidasi |
| 8 | Kode course tidak boleh diupdate | Mengubah semua key graph | Kode adalah identitas node dan key relasi | Jika kode salah, perlu delete lalu insert ulang |

## Edge Case yang Ditangani

| Edge Case | Penanganan Program |
| --- | --- |
| File CSV tidak ditemukan | Ditangkap sebagai `IOException`, program menampilkan pesan error |
| Header CSV tidak sesuai | Program menolak load dan menyebut kolom wajib yang hilang |
| Semester/SKS tidak valid | Program menolak angka kosong, non-numerik, nol, atau negatif |
| Kode mata kuliah duplikat | Program menolak data course duplikat |
| Relasi prasyarat duplikat | Program tidak menambahkan edge yang sama dua kali |
| Relasi ke course tidak ada | Program menolak edge |
| Course menjadi prasyarat dirinya sendiri | Program menolak edge |
| Relasi baru menyebabkan cycle | Program rollback edge dan menampilkan error |
| Prefix tidak ditemukan | Program menampilkan warning dan kembali ke menu |
| Delete course yang punya banyak edge | Program menghapus edge masuk dan keluar terkait course tersebut |
| Topological sort saat ada cycle | Program membatalkan rekomendasi |

## Analisis Kompleksitas

Notasi:

```text
V = jumlah mata kuliah/node
E = jumlah relasi prasyarat/edge
L = panjang kode atau nama mata kuliah
R = jumlah hasil prefix
k = jumlah tetangga pada list adjacency
```

| Operasi | Struktur/Algoritma | Kompleksitas | Alasan |
| --- | --- | --- | --- |
| Load `courses.csv` | Map | O(V) | Membaca setiap baris course satu kali |
| Load `prerequisites.csv` | Graph + cycle check | O(E x (V + E)) | Setiap edge ditambahkan lalu dicek cycle agar data tetap valid |
| Insert course | Map + adjacency list | O(1) rata-rata | Insert ke `LinkedHashMap` dan inisialisasi list |
| Add edge | Adjacency list + DFS cycle | O(k + V + E) | Cek duplikat pada list tetangga, lalu cycle detection |
| Direct prerequisite | Reverse adjacency list | O(k) | Mengambil list prasyarat langsung |
| All prerequisite | DFS reverse graph | O(V + E) | Menelusuri node dan edge yang relevan |
| Cycle detection | DFS status 0/1/2 | O(V + E) | Setiap node dan edge dikunjungi maksimal sekali |
| Topological sort | Kahn's Algorithm + PriorityQueue | O((V + E) log V) | Setiap node masuk/keluar priority queue, dan setiap edge diproses saat indegree dikurangi |
| Insert Trie | Trie | O(L) | Menelusuri karakter key |
| Search prefix Trie | Trie | O(L + R) | Menelusuri prefix dan menyalin hasil |
| Delete dari Trie | Trie | O(L) | Menghapus course code di jalur prefix dan pruning node kosong |
| Delete course | Graph | O(V + E) | Menghapus node dan semua edge masuk/keluar terkait |

## Skenario Pengujian

### Skenario Normal

| No | Skenario | Expected Output |
| --: | --- | --- |
| 1 | Jalankan program | Muncul jumlah mata kuliah 70 dan relasi 40 |
| 2 | Search prefix `ET2343` | Muncul 6 mata kuliah semester 3 |
| 3 | Tampilkan prasyarat langsung `ET234203` | Muncul `ET234103` sebagai prasyarat |
| 4 | Tampilkan semua prasyarat `ET234801` | Muncul rantai prasyarat menuju Tugas Akhir |
| 5 | Jalankan topological sort | Muncul urutan mata kuliah dari dasar ke lanjutan |
| 6 | Jalankan cycle detection | Muncul status graph tidak memiliki siklus |

### Skenario Edge Case

| No | Skenario | Expected Output |
| --: | --- | --- |
| 1 | Tambah relasi `ET234801 -> ET234103` | Ditolak karena menyebabkan cycle |
| 2 | Search prefix yang tidak ada | Muncul warning tidak ada mata kuliah cocok |
| 3 | Tambah mata kuliah dengan kode yang sudah ada | Ditolak karena kode sudah ada |
| 4 | Tambah relasi ke kode yang tidak ada | Ditolak karena course tidak ditemukan |
| 5 | Delete mata kuliah yang punya edge | Course dan semua edge terkait terhapus |

## Screenshot Hasil Program

### Menu Utama

![Menu Utama](assets/screenshot-1-menu-utama.png)

### Search Prefix

![Search Prefix](assets/screenshot-2-search.png)

### Prasyarat Langsung

![Prasyarat Langsung](assets/screenshot-3-semua-prasyarat.png)

### Semua Prasyarat dengan DFS

![Semua Prasyarat DFS](assets/screenshot-4-semua-prasyarat-dfs.png)

### Topological Sort

![Topological Sort 1](assets/screenshot-5-topological-sort.png)

![Topological Sort 2](assets/screenshot-5-topological-sort2.png)

![Topological Sort 3](assets/screenshot-5-topological-sort3.png)

### Cycle Detection

![Cycle Detection](assets/screenshot-6-cycle-detection.png)

### Relasi Ditolak karena Cycle

![Cycle Ditolak](assets/screenshot-7-cycle-ditolak.png)

## What-if Analysis

### 1. Apa yang terjadi jika jumlah node naik menjadi 10.000?

DFS dan cycle detection tetap berskala linear O(V + E). Topological sort memakai priority queue sehingga kompleksitasnya O((V + E) log V), tetapi hasilnya lebih natural karena memprioritaskan semester rekomendasi lebih kecil. Bottleneck utama adalah proses `addEdge` saat load karena program mengecek cycle setiap edge. Untuk dataset sangat besar, optimasi yang lebih baik adalah membaca semua edge terlebih dahulu, lalu menjalankan satu cycle detection global.

### 2. Apa yang terjadi jika edge tertentu dihapus?

Indegree mata kuliah tujuan akan berkurang. Hasil topological sort dapat berubah karena mata kuliah yang sebelumnya bergantung pada prasyarat tertentu bisa muncul lebih awal. Jika edge yang dihapus adalah bagian dari cycle, maka cycle dapat hilang.

### 3. Apa yang terjadi jika bobot edge ditambahkan?

Topological sort tidak membutuhkan bobot, sehingga rekomendasi prasyarat tetap bisa berjalan. Namun jika bobot dipakai untuk menghitung tingkat kesulitan transisi atau prioritas pengambilan, struktur edge perlu diubah dari `List<String>` menjadi list object `Edge` yang menyimpan tujuan dan bobot.

### 4. Apa yang terjadi jika input user tidak ditemukan?

Program menampilkan pesan warning dan kembali ke menu. Operasi graph tidak dijalankan pada kode yang tidak ada, sehingga program tidak crash.

### 5. Apa yang terjadi jika data duplikat dimasukkan?

Course dengan kode duplikat ditolak karena kode adalah key graph. Relasi prasyarat duplikat tidak ditambahkan ulang. Penanganan ini menjaga hasil DFS dan topological sort agar tidak terdistorsi oleh edge ganda.

### 6. Apa yang terjadi jika graph tidak terhubung?

Program tetap berjalan. Cycle detection mengecek semua komponen, sedangkan topological sort memasukkan semua node dengan indegree 0 ke queue. Mata kuliah yang tidak punya relasi prasyarat akan tetap muncul dalam rekomendasi.

## Tracing Manual

Tracing manual dipisahkan ke file:

```text
TRACING.md
```

File tersebut dapat dikonversi menjadi `tracing.pdf`. Pemisahan ini mengikuti kebutuhan deliverable agar laporan utama dan tracing manual dapat dikumpulkan sebagai dokumen berbeda.

## Kesimpulan

Course Prerequisite Planner berhasil memenuhi requirement final project Topik 9. Program menggunakan directed graph untuk merepresentasikan hubungan prasyarat, Trie untuk pencarian prefix, serta tiga algoritma graph utama: DFS, cycle detection, dan topological sort.

Dataset dibaca dari CSV dan berisi 70 mata kuliah serta 40 relasi prasyarat. Program tidak hanya melakukan CRUD, tetapi juga memberikan rekomendasi urutan pengambilan mata kuliah, mendeteksi konflik kurikulum berupa cycle, dan menggabungkan hasil search Trie dengan informasi relasi Graph.

Dengan validasi input, rollback edge yang menyebabkan cycle, dan dokumentasi kompleksitas, aplikasi ini dapat digunakan sebagai simulasi perencanaan studi berbasis struktur data Tree dan Graph.
