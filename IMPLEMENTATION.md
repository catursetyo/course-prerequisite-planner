# IMPLEMENTATION.md

Dokumen ini berisi ringkasan progress Final Project Struktur Data dan OOP untuk topik:

**Topik 9 - Course Prerequisite Planner**

Tujuan file ini adalah membantu anggota kelompok lain memahami kondisi project saat ini dan melanjutkan pekerjaan tanpa harus membaca seluruh source code dari awal.

---

## 1. Status Project Saat Ini

Project sudah memiliki aplikasi CLI Java untuk membuat sistem perencanaan urutan pengambilan mata kuliah berdasarkan relasi prasyarat.

Data mata kuliah dan relasi prasyarat sudah dibaca dari CSV saat runtime:

```text
data/courses.csv
data/prerequisites.csv
```

Dataset saat ini:

```text
Jumlah mata kuliah: 70
Jumlah relasi prasyarat: 40
```

Jumlah ini sudah memenuhi requirement minimal:

```text
Minimal 25 node/vertex
Minimal 40 edge/relasi
```

---

## 2. Struktur Folder Saat Ini

Struktur utama project:

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
├── docs/
├── README.md
└── IMPLEMENTATION.md
```

Catatan:

- Source code utama berada di `src/`.
- Dataset utama berada di `data/courses.csv` dan `data/prerequisites.csv`.
- Folder `docs/` masih belum diisi dokumentasi final.
- File ini hanya menjelaskan progress dan rencana lanjutan.

---

## 3. Cara Compile dan Run

Jalankan dari root repository:

```bash
javac -d out $(find src -name "*.java")
java -cp out Main
```

Jika ingin compile tanpa mengubah folder `out/`, bisa gunakan folder sementara:

```bash
javac -d /tmp/course-prerequisite-planner-out $(find src -name "*.java")
java -cp /tmp/course-prerequisite-planner-out Main
```

---

## 4. File dan Tanggung Jawab Class

### `src/model/Course.java`

Class model untuk menyimpan data satu mata kuliah.

Field yang tersedia:

```text
code
name
semester
credits
category
track
difficulty
```

Catatan:

- `Course` dibuat immutable.
- Update mata kuliah dilakukan dengan membuat objek `Course` baru, lalu mengganti data lama di `Map`.
- Kode mata kuliah tidak boleh diubah karena menjadi key graph.

### `src/graph/CourseGraph.java`

Class utama untuk mengelola directed graph prasyarat.

Struktur data utama:

```java
Map<String, Course> courses;
Map<String, List<String>> adjacencyList;
Map<String, List<String>> reverseAdjacencyList;
```

Arah edge yang digunakan:

```text
prerequisite_code -> course_code
```

Artinya:

```text
A -> B
A adalah prasyarat untuk B
```

Fitur yang sudah tersedia:

```text
Load courses.csv
Load prerequisites.csv
Tambah mata kuliah
Tambah relasi prasyarat
Update mata kuliah
Delete mata kuliah
Tampilkan semua mata kuliah
Tampilkan adjacency list
Tampilkan prasyarat langsung
Tampilkan semua prasyarat dengan DFS
Cycle detection
Topological sort
Hitung total course
Hitung total edge
```

### `src/tree/Trie.java` dan `src/tree/TrieNode.java`

Struktur data Tree yang digunakan adalah Trie.

Fungsi Trie:

```text
Search mata kuliah berdasarkan prefix kode
Search mata kuliah berdasarkan prefix nama
```

Saat ini Trie menyimpan mapping:

```text
prefix -> daftar kode mata kuliah
```

Setelah add/update/delete mata kuliah, Trie dibangun ulang dari seluruh data course agar hasil search tetap sinkron.

### `src/Main.java`

Class CLI utama.

Menu yang tersedia:

```text
1. Tampilkan semua mata kuliah
2. Cari mata kuliah berdasarkan prefix/kode/nama
3. Tampilkan adjacency list
4. Tampilkan prasyarat langsung
5. Tampilkan semua prasyarat tidak langsung
6. Rekomendasi urutan pengambilan mata kuliah
7. Deteksi cycle
8. Tambah mata kuliah
9. Tambah relasi prasyarat
10. Update mata kuliah
11. Delete mata kuliah
0. Keluar
```

---

## 5. Fitur yang Sudah Memenuhi Requirement

### 5.1 Graph

Graph sudah menggunakan directed graph dengan adjacency list.

Representasi:

```text
adjacencyList:
prasyarat -> daftar mata kuliah lanjutan

reverseAdjacencyList:
mata kuliah -> daftar prasyarat langsung
```

Contoh:

```text
ET234103 -> ET234203
```

Artinya:

```text
ET234103 adalah prasyarat untuk ET234203
```

### 5.2 Algoritma Graph

Algoritma graph yang sudah diimplementasikan:

```text
DFS untuk mencari semua prasyarat langsung dan tidak langsung
Cycle Detection dengan status 0, 1, 2
Topological Sort dengan algoritma Kahn
```

Requirement minimal 2 algoritma graph sudah terpenuhi.

### 5.3 Tree

Tree yang digunakan adalah Trie.

Fitur Trie:

```text
Insert kode mata kuliah
Insert nama mata kuliah
Search berdasarkan prefix
```

Requirement minimal 1 struktur data Tree sudah terpenuhi.

### 5.4 Dataset

Dataset sudah berada di file CSV, bukan hardcoded di Java.

File utama:

```text
data/courses.csv
data/prerequisites.csv
```

Jumlah data:

```text
70 mata kuliah
40 relasi prasyarat
```

### 5.5 CRUD dan Simulasi

Program sudah memiliki:

```text
Insert mata kuliah
Insert relasi prasyarat
Update mata kuliah
Delete mata kuliah
```

Catatan penting:

Perubahan add/update/delete saat ini hanya berlaku selama program berjalan di memori. Program belum menyimpan perubahan tersebut kembali ke CSV.

Untuk requirement simulasi, kondisi ini sudah cukup. Jika dosen meminta data tersimpan permanen, perlu ditambahkan fitur save CSV.

---

## 6. Validasi dan Edge Case yang Sudah Ditangani

Edge case yang sudah ditangani:

```text
File CSV kosong
Header CSV wajib tidak ada
Semester/SKS bukan angka
Kode mata kuliah duplikat saat load atau insert
Relasi prasyarat duplikat
Relasi ke mata kuliah yang tidak ada
Mata kuliah menjadi prasyarat dirinya sendiri
Penambahan relasi yang menyebabkan cycle
Search prefix tanpa hasil
Input kode tidak ditemukan pada menu prasyarat
Delete mata kuliah beserta edge masuk/keluar
Topological sort dicegah jika graph memiliki cycle
```

Validasi cycle saat tambah relasi bekerja dengan cara:

```text
1. Tambahkan edge sementara.
2. Jalankan hasCycle().
3. Jika cycle ditemukan, hapus edge tadi.
4. Tampilkan pesan bahwa relasi dibatalkan.
```

---

## 7. Progress Testing Manual

Testing manual yang sudah pernah dilakukan:

### 7.1 Compile

```bash
javac -d /tmp/course-prerequisite-planner-out $(find src -name "*.java")
```

Hasil:

```text
Berhasil tanpa error.
```

### 7.2 Run Awal

Input:

```text
0
```

Output utama:

```text
Jumlah mata kuliah: 70
Jumlah relasi prasyarat: 40
```

### 7.3 Search Prefix

Input menu:

```text
2
ET2343
0
```

Hasil:

```text
Menampilkan beberapa mata kuliah semester 3 dengan prefix ET2343.
```

### 7.4 Prasyarat Langsung

Input menu:

```text
4
ET234203
0
```

Hasil:

```text
Menampilkan ET234103 sebagai prasyarat langsung.
```

### 7.5 Semua Prasyarat dengan DFS

Input menu:

```text
5
ET234305
0
```

Hasil:

```text
Menampilkan ET234203 dan ET234103 sebagai rantai prasyarat.
```

### 7.6 Cycle Detection

Input menu:

```text
7
0
```

Hasil:

```text
Graph tidak memiliki cycle.
```

### 7.7 Topological Sort

Input menu:

```text
6
0
```

Hasil:

```text
Menampilkan rekomendasi urutan pengambilan mata kuliah.
```

### 7.8 Tambah Relasi yang Menyebabkan Cycle

Input menu:

```text
9
ET234305
ET234103
7
0
```

Hasil:

```text
Relasi ditolak karena menyebabkan cycle.
Graph tetap tidak memiliki cycle.
Jumlah edge tetap 40.
```

---

## 8. Hal yang Masih Perlu Ditambahkan

Bagian ini adalah daftar pekerjaan lanjutan yang perlu diselesaikan oleh anggota kelompok.

### 8.1 Dokumentasi Final

Belum ada dokumen laporan final di folder `docs/`.

Yang perlu dibuat:

```text
docs/laporan.md
docs/tracing.md
```

Setelah selesai, bisa dikonversi menjadi:

```text
docs/laporan.pdf
docs/tracing.pdf
```

Isi minimal `docs/laporan.md`:

```text
1. Judul project
2. Identitas kelompok
3. Deskripsi masalah
4. Alasan menggunakan directed graph
5. Alasan menggunakan adjacency list
6. Alasan menggunakan reverse adjacency list
7. Alasan menggunakan Trie
8. Penjelasan dataset
9. Penjelasan fitur program
10. Penjelasan DFS prasyarat
11. Penjelasan cycle detection
12. Penjelasan topological sort
13. Kompleksitas waktu dan ruang
14. Edge case yang ditangani
15. Screenshot atau contoh output
16. Kesimpulan
```

Isi minimal `docs/tracing.md`:

```text
1. Tracing DFS prasyarat
2. Tracing cycle detection
3. Tracing topological sort
4. Tracing insert relasi yang menyebabkan cycle
5. Tracing delete mata kuliah dan penghapusan edge terkait
```

### 8.2 Perbarui README

README saat ini masih sederhana.

Isi yang perlu ditambahkan ke `README.md`:

```text
1. Deskripsi singkat project
2. Requirement Java
3. Command compile dan run
4. Struktur folder
5. Format CSV
6. Daftar menu aplikasi
7. Contoh input dan output
8. Penjelasan singkat graph dan Trie
9. Catatan bahwa data dibaca dari CSV
```

Jangan lupa gunakan command compile sesuai struktur package:

```bash
javac -d out $(find src -name "*.java")
java -cp out Main
```

### 8.3 Tambahkan Testing Manual yang Lebih Rapi

Belum ada file khusus berisi daftar skenario test.

Rekomendasi:

```text
docs/manual-testing.md
```

Isi yang disarankan:

```text
Test 1: Load CSV
Test 2: Tampilkan semua mata kuliah
Test 3: Search prefix kode
Test 4: Search prefix nama
Test 5: Tampilkan adjacency list
Test 6: Tampilkan prasyarat langsung
Test 7: Tampilkan semua prasyarat
Test 8: Topological sort
Test 9: Cycle detection
Test 10: Tambah course valid
Test 11: Tambah course duplikat
Test 12: Tambah edge valid
Test 13: Tambah edge duplikat
Test 14: Tambah edge self-loop
Test 15: Tambah edge yang menyebabkan cycle
Test 16: Update course
Test 17: Delete course
Test 18: Search data yang tidak ada
```

### 8.4 Pertimbangkan Fitur Save CSV

Saat ini add/update/delete hanya memodifikasi data di memori.

Jika ingin perubahan tersimpan permanen ke CSV, perlu ditambahkan method:

```java
public void saveCoursesToCsv(String path) throws IOException
public void savePrerequisitesToCsv(String path) throws IOException
```

Lalu tambahkan menu opsional:

```text
12. Simpan perubahan ke CSV
```

Namun, fitur ini tidak wajib jika requirement yang diminta hanya simulasi penambahan data dan relasi.

### 8.5 Rapikan Output Topological Sort

Topological sort saat ini sudah benar secara graph, tetapi urutan output tidak selalu berurutan berdasarkan semester karena topological sort mengikuti dependency dan urutan data.

Jika ingin output lebih enak dibaca, bisa ditambahkan sorting prioritas pada queue:

```text
Prioritas 1: semester lebih kecil
Prioritas 2: kode mata kuliah
```

Caranya:

```text
Ganti Queue<String> dengan PriorityQueue<String>
Comparator melihat semester dari courses.get(code).getSemester()
Jika semester sama, bandingkan code
```

Catatan:

Perubahan ini bukan kewajiban. Secara algoritma, topological sort sekarang sudah valid.

### 8.6 Tambahkan Method `search` dan `startsWith` di Trie Jika Diminta Dosen

Saat ini Trie memakai:

```java
insert(String key, String courseCode)
searchByPrefix(String prefix)
```

Jika ingin mengikuti method yang disarankan di AGENTS.md secara eksplisit, bisa ditambahkan wrapper:

```java
public void insert(String key)
public boolean search(String key)
public List<String> startsWith(String prefix)
```

Namun fitur search prefix sudah berjalan dengan method `searchByPrefix`.

---

## 9. Kompleksitas yang Perlu Ditulis di Laporan

Gunakan:

```text
V = jumlah mata kuliah
E = jumlah relasi prasyarat
L = panjang kode atau nama
R = jumlah hasil pencarian Trie
```

Kompleksitas:

```text
Load courses.csv: O(V)
Load prerequisites.csv: O(E * (V + E)) pada implementasi saat ini karena addEdge mengecek cycle setiap relasi
Insert course ke Map: O(1)
Add edge tanpa cycle check: O(k), k = jumlah neighbor dari prerequisite
Add edge dengan cycle check: O(V + E)
DFS prasyarat: O(V + E)
Cycle Detection: O(V + E)
Topological Sort: O(V + E)
Insert ke Trie: O(L)
Search prefix Trie: O(L + R)
Update course: O(1), lalu rebuild Trie O(V * L)
Delete course: O(V + E), lalu rebuild Trie O(V * L)
```

Catatan:

Karena `addEdge` sekarang selalu mengecek cycle, load prerequisites ikut melakukan validasi cycle per edge. Ini aman untuk correctness dan dataset project masih kecil.

---

## 10. Demo yang Disarankan

Saat presentasi, urutan demo yang disarankan:

```text
1. Jalankan program dan tunjukkan jumlah course 70 dan edge 40.
2. Menu 1: tampilkan semua mata kuliah.
3. Menu 2: search prefix ET2343.
4. Menu 3: tampilkan adjacency list.
5. Menu 4: prasyarat langsung ET234203.
6. Menu 5: semua prasyarat ET234305.
7. Menu 6: rekomendasi urutan pengambilan mata kuliah.
8. Menu 7: deteksi cycle normal.
9. Menu 9: coba tambah edge ET234305 -> ET234103.
10. Tunjukkan bahwa edge ditolak karena menyebabkan cycle.
11. Menu 8: tambah mata kuliah baru.
12. Menu 9: tambah relasi valid ke mata kuliah baru.
13. Menu 10: update mata kuliah.
14. Menu 11: delete mata kuliah.
```

Contoh input untuk cycle:

```text
9
ET234305
ET234103
```

Expected output:

```text
Gagal menambah relasi: Relasi menyebabkan cycle dan dibatalkan: ET234305 -> ET234103
```

---

## 11. Catatan untuk Anggota Kelompok

Hal yang jangan diubah tanpa alasan kuat:

```text
Arah edge prerequisite_code -> course_code
Dataset utama di data/courses.csv dan data/prerequisites.csv
Struktur package model, graph, tree
Penggunaan adjacency list dan reverse adjacency list
Penggunaan Trie sebagai struktur data Tree
Cycle detection sebelum topological sort
```

Hal yang boleh dilanjutkan:

```text
Menulis laporan dan tracing
Menambahkan save CSV jika ingin persistence
Merapi output topological sort
Menambah test manual
Memperbaiki README
Menambah validasi input CLI agar lebih nyaman
```

Prioritas paling penting berikutnya:

```text
1. Buat laporan final.
2. Buat tracing algoritma.
3. Siapkan screenshot atau contoh output.
4. Jalankan ulang semua skenario demo.
5. Pastikan compile bersih sebelum submit.
```
