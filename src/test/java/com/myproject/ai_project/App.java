package com.myproject.ai_project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

   private static String HF_TOKEN = System.getenv("HF_TOKEN");
    private static final String MODEL = "facebook/bart-large-cnn";

    private TextArea inputArea;
    private TextArea outputArea;
    private Button summarizeBtn;
    private Button loadSampleBtn;
    private Button loadFileBtn;
    private Button copyBtn;
    private Label statusLabel;
    private ProgressIndicator spinner;
    private Slider lengthSlider;
    private Label lengthLabel;
    private ListView<String> historyList;
    private List<String[]> historyData = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        stage.setTitle("AI Text Summarizer — Powered by BART & HuggingFace");

        Label title = new Label("AI Text Summarizer");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #2c3e50;");

        Label subtitle = new Label("Paste any article and get an instant AI summary");
        subtitle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        VBox header = new VBox(4, title, subtitle);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20, 0, 10, 0));

        Label inputLabel = new Label("Input Text");
        inputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        inputArea = new TextArea();
        inputArea.setPromptText("Paste your article here...");
        inputArea.setWrapText(true);
        inputArea.setPrefHeight(180);
        inputArea.setStyle("-fx-font-size: 13px;");

        Label sliderTitle = new Label("Summary Length:");
        sliderTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        lengthSlider = new Slider(50, 250, 150);
        lengthSlider.setMajorTickUnit(100);
        lengthSlider.setMinorTickCount(4);
        lengthSlider.setShowTickMarks(true);
        lengthSlider.setShowTickLabels(true);
        lengthSlider.setPrefWidth(200);

        lengthLabel = new Label("Medium (150)");
        lengthLabel.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");

        lengthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int val = newVal.intValue();
            if (val < 100) lengthLabel.setText("Short (" + val + ")");
            else if (val < 180) lengthLabel.setText("Medium (" + val + ")");
            else lengthLabel.setText("Long (" + val + ")");
        });

        HBox sliderRow = new HBox(10, sliderTitle, lengthSlider, lengthLabel);
        sliderRow.setAlignment(Pos.CENTER_LEFT);

        summarizeBtn = new Button("✦ Summarize");
        summarizeBtn.setStyle("""
            -fx-background-color: #2980b9;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-padding: 8 20;
            -fx-background-radius: 8;
            -fx-cursor: hand;
        """);

        loadSampleBtn = new Button("⟳ Sample Article");
        loadSampleBtn.setStyle("""
            -fx-background-color: #27ae60;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-padding: 8 16;
            -fx-background-radius: 8;
            -fx-cursor: hand;
        """);

        loadFileBtn = new Button("📂 Load File");
        loadFileBtn.setStyle("""
            -fx-background-color: #8e44ad;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-padding: 8 16;
            -fx-background-radius: 8;
            -fx-cursor: hand;
        """);

        copyBtn = new Button("📋 Copy Summary");
        copyBtn.setStyle("""
            -fx-background-color: #e67e22;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-padding: 8 16;
            -fx-background-radius: 8;
            -fx-cursor: hand;
        """);
        copyBtn.setDisable(true);

        spinner = new ProgressIndicator();
        spinner.setVisible(false);
        spinner.setPrefSize(28, 28);

        HBox btnRow = new HBox(10, loadSampleBtn, loadFileBtn, summarizeBtn, spinner);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        Label outputLabel = new Label("Summary");
        outputLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setPrefHeight(130);
        outputArea.setStyle("""
            -fx-font-size: 13px;
            -fx-background-color: #f0f4f8;
        """);

        HBox outputHeader = new HBox(10, outputLabel, copyBtn);
        outputHeader.setAlignment(Pos.CENTER_LEFT);

        Label historyLabel = new Label("History");
        historyLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        historyList = new ListView<>();
        historyList.setPrefHeight(120);
        historyList.setStyle("-fx-font-size: 12px;");
        historyList.setPlaceholder(new Label("No summaries yet..."));

        historyList.setOnMouseClicked(e -> {
            int idx = historyList.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && idx < historyData.size()) {
                inputArea.setText(historyData.get(idx)[0]);
                outputArea.setText(historyData.get(idx)[1]);
                copyBtn.setDisable(false);
            }
        });

        statusLabel = new Label("Ready.");
        statusLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");

        VBox root = new VBox(10,
            header,
            new Separator(),
            inputLabel, inputArea,
            sliderRow,
            btnRow,
            outputHeader, outputArea,
            historyLabel, historyList,
            statusLabel
        );
        root.setPadding(new Insets(20, 30, 20, 30));
        root.setStyle("-fx-background-color: #ffffff;");

        summarizeBtn.setOnAction(e -> summarize());
        loadSampleBtn.setOnAction(e -> loadSample());
        loadFileBtn.setOnAction(e -> loadFile(stage));
        copyBtn.setOnAction(e -> {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(outputArea.getText());
            clipboard.setContent(content);
            statusLabel.setText("Summary copied to clipboard!");
        });

        Scene scene = new Scene(root, 760, 700);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void summarize() {
        String text = inputArea.getText().trim();
        if (text.isEmpty()) {
            statusLabel.setText("Please enter some text first.");
            return;
        }
        if (text.split("\\s+").length < 30) {
            statusLabel.setText("Text is too short. Please enter at least 30 words.");
            return;
        }

        summarizeBtn.setDisable(true);
        copyBtn.setDisable(true);
        spinner.setVisible(true);
        outputArea.setText("");
        statusLabel.setText("Sending to BART model...");

        int maxLen = (int) lengthSlider.getValue();
        int minLen = maxLen / 3;

        Thread thread = new Thread(() -> {
            try {
                String result = callHuggingFace(text, maxLen, minLen);
                Platform.runLater(() -> {
                    outputArea.setText(result);
                    statusLabel.setText("Done! Model: " + MODEL);
                    summarizeBtn.setDisable(false);
                    copyBtn.setDisable(false);
                    spinner.setVisible(false);
                    addToHistory(text, result);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + ex.getMessage());
                    summarizeBtn.setDisable(false);
                    spinner.setVisible(false);
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private String callHuggingFace(String text, int maxLen, int minLen) throws Exception {
    URL url = new URL("https://api-inference.huggingface.co/models/" + MODEL);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Authorization", "Bearer " + HF_TOKEN);
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);
    conn.setConnectTimeout(30000);
    conn.setReadTimeout(60000);

    String escaped = text.replace("\\", "\\\\")
                         .replace("\"", "\\\"")
                         .replace("\n", "\\n")
                         .replace("\r", "");

    String payload = "{\"inputs\": \"" + escaped + "\", "
            + "\"parameters\": {\"max_length\": " + maxLen
            + ", \"min_length\": " + minLen + "}}";

    try (OutputStream os = conn.getOutputStream()) {
        os.write(payload.getBytes(StandardCharsets.UTF_8));
    }

    int code = conn.getResponseCode();
    InputStream is = (code == 200) ? conn.getInputStream() : conn.getErrorStream();

    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();
    String line;

    while ((line = br.readLine()) != null) {
        sb.append(line);
    }
    br.close();

    String response = sb.toString();

    if (response.contains("loading")) {
        return "Model is loading, please wait a few seconds and try again.";
    }

    int startKey = response.indexOf("summary_text");

    if (startKey == -1) {
        return "Unexpected response: " + response;
    }

    int start = response.indexOf(":", startKey) + 1;

    int firstQuote = response.indexOf("\"", start);
    if (firstQuote == -1) return "Parsing error: " + response;

    int secondQuote = response.indexOf("\"", firstQuote + 1);
    if (secondQuote == -1) return "Parsing error: " + response;

    String summary = response.substring(firstQuote + 1, secondQuote);

    return summary.replace("\\n", "\n");
}

    private void addToHistory(String input, String summary) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String preview = input.length() > 50 ? input.substring(0, 50) + "..." : input;
        historyData.add(new String[]{input, summary});
        historyList.getItems().add("[" + time + "] " + preview);
    }

    private void loadSample() {
        inputArea.setText(
            "The Amazon rainforest, often referred to as the lungs of the Earth, " +
            "plays a crucial role in regulating the global climate by absorbing vast " +
            "amounts of carbon dioxide and producing oxygen. Spanning over 5.5 million " +
            "square kilometers across nine countries in South America, it is home to an " +
            "estimated 10% of all species on Earth, many of which have yet to be " +
            "discovered by science. However, the Amazon faces unprecedented threats from " +
            "deforestation, driven largely by agricultural expansion, illegal logging, and " +
            "infrastructure development. Scientists warn that if deforestation continues at " +
            "its current rate, large portions of the forest could reach a tipping point, " +
            "transforming from a carbon sink into a carbon source, with catastrophic " +
            "consequences for both regional and global climates. Conservation efforts, " +
            "including the establishment of protected areas and international agreements, " +
            "are underway, but experts argue that far more aggressive action is needed to " +
            "preserve this irreplaceable ecosystem for future generations."
        );
    }

    private void loadFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append("\n");
                inputArea.setText(sb.toString());
                statusLabel.setText("File loaded: " + file.getName());
            } catch (IOException ex) {
                statusLabel.setText("Error loading file: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
