# Dibaj Board Game Buddy

Aplikasi asisten panduan dan evaluasi aturan board game berbasis **Spring AI** yang mengimplementasikan materi dari buku **Spring AI in Action** (Craig Walls) untuk **Chapter 1** dan **Chapter 2**.

---

## 🎯 Fitur Implementasi

### 1. Chapter 1: Tanya Jawab Aturan Permainan
- Menggunakan `ChatClient` dari Spring AI untuk menghubungkan model bahasa lokal (Ollama / `llama3.2`) atau OpenAI.
- Endpoint: `POST /ask`
- Input: JSON `{"question": "How do I play Ticket to Ride?"}`
- Output: `{"answer": "..."}`

### 2. Chapter 2: Evaluasi & Validasi Respons
- **Self-Evaluation saat Runtime (`POST /ask/evaluated`)**: Menghasilkan respons lalu memvalidasi relevansi topik secara otomatis. Jika tidak relevan, memicu mekanisme retry (`@Retryable`).
- **Uji Relevansi Jawaban (`POST /evaluate/relevancy`)**: Memvalidasi apakah sebuah jawaban sesuai dengan pertanyaan dan konteks dokumen acuan.
- **Verifikasi Akurasi Fakta (`POST /evaluate/factual`)**: Menilai klaim terhadap dokumen acuan untuk mencegah halusinasi data (*grounded fact checking*).

---

## 🏗️ Struktur Arsitektur & Class

```
com.dibaj.dibajboardgamebuddy
├── DibajBoardGameBuddyApplication.java   # Main Spring Boot Entry Point
├── AskController.java                    # REST Controller (/ask, /ask/evaluated)
├── EvaluationController.java             # REST Controller (/evaluate/relevancy, /evaluate/factual)
├── BoardGameService.java                 # Service Interface
├── SpringAiBoardGameService.java         # Chapter 1 Implementation
├── SelfEvaluatingBoardGameService.java   # Chapter 2 Self-Evaluation Implementation
├── EvaluationService.java               # Chapter 2 Evaluation Service (Relevancy & Fact-Checking)
├── Answer.java                           # Record Data Model
├── Question.java                         # Record Data Model
├── EvaluationInput.java                  # Record Data Model
├── EvaluationResult.java                 # Record Data Model
└── AnswerNotRelevantException.java       # Custom Runtime Exception
```

---

## 🚀 Menjalankan Aplikasi

### Prasyarat
1. **Java 21** (JDK 21)
2. **Ollama** dengan model `llama3.2` terpasang (`ollama run llama3.2`)

### Menjalankan Server
```powershell
.\gradlew.bat bootRun
```

Aplikasi akan berjalan di:
👉 **http://localhost:8080**

Tersedia antarmuka web modern dengan 4 mode pengujian interaktif untuk Chapter 1 dan Chapter 2.

---

## 🧪 Pengujian via Terminal / cURL

### 1. Tanya Jawab (Chapter 1)
```powershell
curl.exe -X POST http://localhost:8080/ask `
  -H "Content-Type: application/json" `
  -d '{"question":"How do I play Ticket to Ride?"}'
```

### 2. Self-Evaluation (Chapter 2)
```powershell
curl.exe -X POST http://localhost:8080/ask/evaluated `
  -H "Content-Type: application/json" `
  -d '{"question":"What are the rules of Catan?"}'
```

### 3. Relevancy Evaluator (Chapter 2)
```powershell
curl.exe -X POST http://localhost:8080/evaluate/relevancy `
  -H "Content-Type: application/json" `
  -d '{"question":"What is Ticket to Ride?","answer":"Ticket to Ride is a railway-themed board game.","context":["Ticket to Ride is a railway board game where players collect train cards."]}'
```

### 4. Fact-Checking Evaluator (Chapter 2)
```powershell
curl.exe -X POST http://localhost:8080/evaluate/factual `
  -H "Content-Type: application/json" `
  -d '{"question":"What is Ticket to Ride?","answer":"Ticket to Ride is a railway-building board game.","context":["Ticket to Ride is a railway-building board game where players collect train cards and claim routes."]}'
```

---

## 👥 Pengembang & Lisensi
- Mahasiswa: Dibaj
- Repositori: [https://github.com/babaj-dibaj/dibaj-board-game-buddy](https://github.com/babaj-dibaj/dibaj-board-game-buddy)
- Referensi Buku: *Spring AI in Action* oleh Craig Walls
