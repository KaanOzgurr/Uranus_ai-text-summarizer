# 🤖 AI Text Summarizer
> A production-ready desktop application that leverages **Facebook's BART-large-CNN** model via HuggingFace Inference API to summarize any text instantly.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=flat-square)
![HuggingFace](https://img.shields.io/badge/HuggingFace-BART-yellow?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-3.x-red?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)

---

## 📸 Preview

The application features a clean, modern JavaFX interface with real-time AI summarization powered by the `facebook/bart-large-cnn` NLP model.

---

## ✨ Features

- **AI-Powered Summarization** — Uses Facebook's BART-large-CNN model via HuggingFace Inference API
- **Adjustable Summary Length** — Slider control for Short / Medium / Long summaries (50–250 tokens)
- **Load from File** — Import any `.txt` file directly into the app
- **Sample Article** — Built-in demo article for quick testing
- **Copy to Clipboard** — One-click copy of the generated summary
- **History Panel** — Keeps track of all previous summaries in the current session
- **Async Processing** — Non-blocking UI with loading indicator during API calls

---

## 🛠️ Tech Stack

| Technology | Purpose |
|-----------|---------|
| Java 17 | Core language |
| JavaFX 21 | Desktop GUI framework |
| Maven | Dependency management & build tool |
| HuggingFace Inference API | NLP model hosting |
| facebook/bart-large-cnn | Summarization model |
| Weka 3.8.6 | ML library (extensible) |
| OpenCSV 5.7.1 | Dataset/CSV processing |

---

## 📦 Prerequisites

- Java 17 or higher
- Maven 3.x
- Eclipse IDE (recommended) or any Java IDE
- A free [HuggingFace account](https://huggingface.co) with an API token

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/KaanOzgurr/Uranus_ai-text-summarizer.git
cd Uranus_ai-text-summarizer
```

### 2. Get a HuggingFace API Token

1. Go to [https://huggingface.co/settings/tokens](https://huggingface.co/settings/tokens)
2. Click **New token** → Select **Fine-grained**
3. Under **Inference**, check **Make calls to Inference Providers**
4. Click **Create token** and copy it

### 3. Add Your Token

Open `src/main/java/com/myproject/ai_project/App.java` and replace:

```java
private static String HF_TOKEN = "YOUR_HF_TOKEN_HERE";
```

with your actual token:

```java
private static String HF_TOKEN = "hf_your_actual_token";
```

### 4. Run the Application in Eclipse

1. Right-click on `App.java` → **Run As → Java Application**
2. Add the following VM arguments in **Run Configurations → Arguments → VM Arguments**:

```
--module-path "PATH_TO_YOUR_JAVAFX_JARS" --add-modules javafx.controls,javafx.fxml
```

> Replace `PATH_TO_YOUR_JAVAFX_JARS` with the path to your local JavaFX jars (usually in your `.m2` Maven repository).

---

## 🎮 How to Use

1. **Paste** any article or text into the **Input Text** area (minimum 30 words)
2. **Adjust** the **Summary Length** slider to your preference (Short / Medium / Long)
3. Click **✦ Summarize** and wait for the AI to process
4. The summary appears in the **Summary** section
5. Click **📋 Copy Summary** to copy the result to clipboard
6. Use **📂 Load File** to import a `.txt` file
7. Previous summaries are saved in the **History** panel — click any to reload

---

## 📁 Project Structure

```
ai-text-summarizer/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/myproject/ai_project/
│   │           └── App.java          # Main application
│   └── test/
│       └── java/
│           └── com/myproject/ai_project/
│               └── AppTest.java      # Unit tests
├── pom.xml                           # Maven dependencies
├── .gitignore
└── README.md
```

---

## 🔒 Security Note

**Never commit your HuggingFace API token to GitHub.**

This repository uses `YOUR_HF_TOKEN_HERE` as a placeholder. Always keep your real token local. Consider using environment variables or a `config.properties` file (added to `.gitignore`) for production use.

---

## 🧠 How It Works

```
User Input Text
      ↓
JavaFX UI (App.java)
      ↓
HTTP POST → HuggingFace Inference API
      ↓
facebook/bart-large-cnn model
      ↓
JSON Response parsed
      ↓
Summary displayed in UI
```

The app sends a POST request to the HuggingFace Router endpoint with the input text and parameters (max/min token length). The BART model processes the text and returns a JSON response containing the `summary_text` field, which is then displayed in the UI.

---

## 📄 License

This project is licensed under the MIT License — feel free to use, modify, and distribute.

---

## 🙋‍♂️ Author

**Kaan Ozgur**  
GitHub: [@KaanOzgurr](https://github.com/KaanOzgurr)

---

> Built with ☕ Java, 🤗 HuggingFace, and JavaFX
