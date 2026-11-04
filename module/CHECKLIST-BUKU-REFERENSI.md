# Checklist buku referensi dan buku ajar: Rekayasa Perangkat Lunak Berbasis Model untuk DevOps

Status per 4 November 2026, untuk naskah di `module/advanced-software-engineering.tex`.
Naskah ini disiapkan agar memenuhi syarat buku referensi sekaligus buku ajar. Satu buku
hanya dapat diajukan pada satu kategori angka kredit: buku referensi 40 AK di unsur
penelitian, buku ajar 20 AK di unsur pendidikan, masing-masing satu buku per tahun.

## Syarat buku referensi

- [x] Membahas satu bidang ilmu sesuai kompetensi penulis, yaitu rekayasa perangkat lunak, DevOps, dan rekayasa berbasis model
- [x] Mencakup beberapa topik dalam bidang itu, sehingga berbeda dari monograf
- [x] Memuat pemikiran original penulis: urutan dari sistem berjalan menuju generasi artefak, satu metamodel yang dipakai lintas enam bab, dan perbandingan antar perkakas pada tugas yang sama
- [x] Rumusan masalah bernilai kebaruan, ada di Pendahuluan, tiga pertanyaan
- [x] Metodologi pemecahan masalah, ada di Pendahuluan, enam langkah penyusunan
- [x] Kesimpulan, ada di Penutup: sintesis, jawaban atas rumusan masalah, kontribusi, keterbatasan, dan arah pengembangan
- [x] Naskah tidak berasal dari disertasi atau tesis penulis
- [x] Tebal 367 halaman, di atas 40 halaman cetak format UNESCO maupun 80 halaman isi yang diminta sebagian panduan penerbit
- [x] Ditulis satu kolom
- [x] Dukungan teori mutakhir di seluruh naskah. Setiap bab memuat tiga sampai lima sitasi pada bagian Rujukan di akhir bab, ditambah sembilan rujukan pada Pendahuluan dan Penutup
- [x] Daftar pustaka hanya memuat sumber yang relevan. Dari 326 entri warisan, kini tersisa 45 entri yang seluruhnya benar-benar dikutip di naskah
- [ ] ISBN, wajib untuk buku referensi
- [ ] Nama penerbit, tahun terbit, editor, desainer, dan pemegang hak cipta pada halaman hak cipta. Tempatnya sudah disiapkan dan ditandai merah
- [ ] Buku terbit dan beredar

## Syarat buku ajar

- [x] Buku pegangan satu mata kuliah, yaitu IT30213 Advanced Software Engineering and DevOps, ditulis pengampunya
- [x] Isi diturunkan dari capaian pembelajaran mata kuliah, dengan empat belas bab untuk empat belas pertemuan
- [x] Tiap bab dibuka tujuan pembelajaran
- [x] Tiap bab ditutup ringkasan atau kesimpulan
- [x] Tiap bab memuat latihan. Empat belas bagian Latihan ditambahkan, masing-masing lima butir yang dikerjakan di komputer pembaca
- [x] Memuat ilustrasi, contoh kasus, dan kode yang dapat dijalankan
- [x] Memuat tugas mata kuliah, dua pilihan, lengkap dengan rubrik dan struktur pelaporan
- [ ] Buku diterbitkan resmi dan disebarluaskan kepada mahasiswa

## Susunan naskah

- [x] Sampul memuat judul, subjudul, dan penulis, tanpa label "Course Module" yang sebelumnya terpasang
- [x] Halaman judul penuh dan halaman hak cipta, sesuai Permendikbudristek 22/2022 Lampiran III Tabel 1
- [x] Daftar isi, daftar gambar, dan daftar tabel, seluruhnya berbahasa Indonesia
- [x] Prakata
- [x] Pendahuluan terpisah dari Prakata, memuat latar belakang, rumusan masalah, tujuan dan manfaat, ruang lingkup, metodologi, kebaruan, sistematika, isi mata kuliah, dan tugas
- [x] Bagian isi: empat belas bab bernomor 1 sampai 14
- [x] Penutup tanpa nomor bab
- [x] Glosarium, 46 istilah
- [x] Daftar pustaka
- [x] Indeks subjek, 113 penanda untuk 34 istilah kunci
- [x] Daftar Listing, bersebelahan dengan Daftar Gambar dan Daftar Tabel
- [x] Riwayat penulis, wajib menurut Permendikbudristek 22/2022 Lampiran III Tabel 3
- [x] Ukuran buku 17,6 × 25 cm (B5 ISO), ukuran terbesar yang disebut penerbit sebagai alternatif yang diterima untuk pengajuan angka kredit, di samping ukuran UNESCO 15,5 × 23 cm. Margin dalam 2,5 cm, luar 2,0 cm, atas dan bawah 2,5 cm

## Catatan teknis

- [x] Naskah terkompilasi tanpa galat, tanpa sitasi yang tidak terdefinisi, dan tanpa acuan silang yang menggantung, 367 halaman
- [x] Baris `\cite{020practical}` yang berdiri sendiri di badan dokumen sudah dihapus
- [x] Penanda indeks tidak disisipkan ke dalam blok kode, judul bagian, maupun argumen perintah seperti `\ref`

## Gambar, tabel, dan listing

- [x] 25 gambar: seluruhnya punya caption, label, dan diacu di dalam teks
- [x] 26 tabel: seluruhnya punya caption, label, dan diacu di dalam teks
- [x] 300 listing: seluruhnya punya caption, label, dan diacu di dalam teks. Caption untuk 215 listing di Bab 1 sampai 7 diturunkan dari isi listing-nya, bukan dari kalimat umum
- [x] Kalimat acuan diragamkan menurut jenis listing, yaitu perintah, isi berkas, potongan kode, atau struktur direktori, agar prosanya tidak seragam
- [x] Tidak ada lingkungan algoritma maupun persamaan di naskah ini, sehingga keduanya tidak memerlukan caption dan acuan

## Pemeriksaan margin

- [x] Seluruh 367 halaman dipindai piksel per piksel: tidak ada gambar, tabel, atau listing yang tintanya masuk ke area margin
- [x] Nomor baris listing ditarik masuk dengan xleftmargin, sebelumnya tercetak di luar blok teks pada 126 halaman
- [x] Gambar TikZ dan tabel lebar dibungkus adjustbox dengan batas lebar teks, sehingga hanya mengecil bila perlu, 19 gambar dan 26 tabel
- [x] Nomor halaman romawi panjang di daftar isi diberi kotak lebih lebar
- [x] Sampul memang memakai latar penuh halaman, sehingga tintanya sengaja melewati margin

## Verifikasi rujukan

- [x] Seluruh 36 tautan rujukan diperiksa lewat HTTP pada 4 November 2026: status 200 dan judul halaman cocok dengan entri daftar pustaka
- [x] Satu calon rujukan, artikel Borg, Omega, and Kubernetes di ACM Queue, tidak dipakai karena aksesnya ditolak otomatis (HTTP 403)
- [x] Dua sitasi warisan yang tidak berkaitan dengan teksnya dicabut, yaitu buku tata kelola data perusahaan di Pendahuluan dan makalah penyiram tanaman berbasis IoT di Bab 12
- [x] URL repositori kode dicantumkan di Prakata, Pendahuluan, dan halaman hak cipta

## Catatan tata letak

- [x] Judul bagian yang terlalu panjang diberi versi pendek untuk header halaman dan daftar isi, 43 judul
- [x] Tersisa 35 baris yang melewati batas kolom, dua di antaranya lebih dari 20 pt dan yang terparah 33,4 pt, turun dari 1961 di awal. Perbaikannya: pemenggalan bahasa Indonesia, pemenggalan di dalam \texttt, microtype, judul header yang dipendekkan, nomor baris listing ditarik ke dalam blok teks, gambar dan tabel lebar dibungkus adjustbox, serta ukuran halaman B5

## Sumber

- [PO PAK 2019](https://kepegawaian.itera.ac.id/wp-content/uploads/2021/09/PO-PAK-2019_MULAI-BERLAKU-APRIL-2019-1.pdf), definisi dan angka kredit buku referensi, monograf, dan buku ajar
- [Permendikbudristek 22/2022](https://peraturan.bpk.go.id/Download/219102/Salinan%20permendikbudristek%20nomor%2022%20tahun%202022.pdf), susunan bagian awal dan bagian akhir buku
- [Pedoman Membuat Buku Referensi dan Buku Monograf, Esa Unggul University Press](https://universitypress.esaunggul.ac.id/wp-content/uploads/2025/03/Pedoman-Membuat-Buku-Referensi-Buku-Monograf-EDIT.pdf), ketentuan tebal, ukuran, dan kelengkapan naskah
- [Sistematika Menulis Buku Referensi, Penerbit Deepublish](https://penerbitdeepublish.com/publikasi-buku/sistematika-menulis-buku-referensi/), susunan bagian awal, isi, dan bagian akhir
