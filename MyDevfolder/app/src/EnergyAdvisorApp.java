import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;


public class EnergyAdvisorApp extends Application {
	
	private void showError(String title, String message) {
	    Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	            Alert alert = new Alert(Alert.AlertType.ERROR);
	            alert.setTitle(title);
	            alert.setHeaderText(null);
	            alert.setContentText(message);
	            alert.showAndWait();
	        }
	    });
	}

    private static boolean isBetaEnabled = false;
    private static Label betaStatusLabel;
    private static ProgressBar progressBar;
    private static Button analyzeButton;
    private static Label panelCountLabel;
    private static Label sunshineTimeLabel;
    private static Label generationLabel;
    private static TextField generationField;
    private static Label locationLabel;
    private static Label installationTimeLabel;
    private static TextField installationTimeField;
    private static Label systemCapacityLabel;
    private static TextField systemCapacityField;
    private static Label purposeLabel;
    private static TextField purposeField;
    private static Slider angleScale;
    private static Label angleLabel;
    private static Label currentAngleLabel;
    private static ComboBox<String> locationComboBox;
    private static int currentYear = Year.now().getValue();
    private static long lastClickTime = 0;
    private static String apiToken = "";
    private String ver = "1.0";
    Random random = new Random();
    int user_id = 1000 + random.nextInt(9000);
    private static String apiUrl = "Gemini";
    private static String UseDiv = "False";
    private static String MainAPIurl ="https://webapi-8trs.onrender.com/chat";

   
    
    public static void show_ui(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
        
    public void userChoiceToUpdate() {
        String versionUrl = "https://gamelist1990.github.io/gamelist1990/version/appver.json";
        try {
            URI uri = new URI(versionUrl);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> versionMap = gson.fromJson(result.toString(), type);
            String currentVersion = ver; 
            for (String version : versionMap.keySet()) {
                if (Float.parseFloat(version) > Float.parseFloat(currentVersion)) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("更新の確認");
                        alert.setHeaderText("新しいバージョン " + version + " が利用可能です。更新しますか？");
                        alert.setContentText("はい / いいえ");

                        ButtonType buttonTypeYes = new ButtonType("はい");
                        ButtonType buttonTypeNo = new ButtonType("いいえ");

                        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                        alert.showAndWait().ifPresent(response -> {
                            if (response == buttonTypeYes) {
                                downloadAndUpdate(versionMap.get(version), version); 
                            } else {
                                show_ui("更新", "更新した方が良いよ...");
                            }
                        });
                    });
                    break; 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 

    public void checkForUpdates() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                String versionUrl = "https://gamelist1990.github.io/gamelist1990/version/appver.json";
                URI uri = new URI(versionUrl);
                URL url = uri.toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> versionMap = gson.fromJson(result.toString(), type);
                String currentVersion = ver; 
                for (String version : versionMap.keySet()) {
                	if (Float.parseFloat(version) > Float.parseFloat(currentVersion)) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("更新の確認");
                            alert.setHeaderText("新しいバージョン " + version + " が利用可能です。更新しますか？");
                            alert.setContentText("はい / いいえ");

                            ButtonType buttonTypeYes = new ButtonType("はい");
                            ButtonType buttonTypeNo = new ButtonType("いいえ");

                            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                            alert.showAndWait().ifPresent(response -> {
                                if (response == buttonTypeYes) {
                                    downloadAndUpdate(versionMap.get(version), version); 
                                } else {
                                    show_ui("更新", "最新バージョン" + currentVersion + "を使用しています");
                                }
                            });
                        });
                        break;
                    } 
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
    }
    
    private void downloadAndUpdate(String downloadUrl, String version) {
        Stage downloadStage = new Stage();
        downloadStage.initStyle(StageStyle.UTILITY); 
        downloadStage.initModality(Modality.APPLICATION_MODAL); 
        downloadStage.setResizable(false); 

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        Label statusLabel = new Label("ダウンロード中...");

        VBox root = new VBox(10, statusLabel, progressBar);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root);
        downloadStage.setScene(scene);
        downloadStage.show();

        analyzeButton.setDisable(true); 

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                Path downloadFolder = Paths.get(System.getProperty("user.home"), "Downloads");
                Path targetPath = downloadFolder.resolve("setup.exe");

                try (InputStream in = new URI(downloadUrl).toURL().openStream()) {
                    long fileSize = in.available();
                    long downloaded = 0;

                    Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);

                    while (downloaded < fileSize) {
                        downloaded += in.transferTo(Files.newOutputStream(targetPath));
                        double progress = (double) downloaded / fileSize;
                        Platform.runLater(() -> progressBar.setProgress(progress));
                    }
                }

                Platform.runLater(() -> {
                    downloadStage.close();
                    show_ui("更新", "インストールが完了しました。: インストールしたver." + version + "のsetup.exeを起動します");

                    PauseTransition delay = new PauseTransition(Duration.seconds(2));
                    delay.setOnFinished(event -> {
                        try {
                            Desktop.getDesktop().open(targetPath.toFile());
                            System.exit(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                            showError("エラー", "setup.exeを開けませんでした。"); 
                        }
                    });
                    delay.play();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    downloadStage.close(); 
                    showError("エラー", "ダウンロード中にエラーが発生しました。");
                    analyzeButton.setDisable(false); 
                });
            }
        });
        executor.shutdown();
    }
    
    
    
    
    
    private void switchAiModel() {
        if (apiUrl.equals("Gemini")) {
            apiUrl = "HuggingChat";
           show_ui("変更","HuggingChatモデルに切り替えました");
        } else {
            apiUrl = "Gemini";
            show_ui("変更","Geminiモデルに切り替えました");
        }
        saveSettings();
    }
    
    private void switchBetaModel() {
        if (UseDiv.equals("False")) {
        	UseDiv = "True";
           show_ui("変更","ストリーミング処理を有効に切り替えました");
        } else {
        	UseDiv = "False";
            show_ui("変更","ストリーミング処理を無効に切り替えました");
        }
        saveSettings();
    }
    
    
    
    private void createApiToken() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("https://webapi-8trs.onrender.com/create_token"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, String>>() {}.getType();
                    Map<String, String> responseMap = gson.fromJson(response.body(), type);
                    apiToken = responseMap.get("access_token");

                    saveSettings();

                    Platform.runLater(() -> {
                        show_ui("APIトークン/生成完了時自動で保存されます", "APIトークンが生成されました: " + apiToken);
                    });
                } else {
                    Platform.runLater(() -> {
                        showError("エラー", "APIトークンの生成に失敗しました。");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    showError("エラー", "APIトークンの生成中にエラーが発生しました。");
                });
            }
        });
        executor.shutdown();
    }





    



    
    
    

    @Override
    public void start(Stage primaryStage) {
        loadSettings();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), ae -> userChoiceToUpdate()));
        timeline.play();

        primaryStage.setTitle("エネルギー効率改善アドバイザー");
        primaryStage.getIcons().add(new Image(EnergyAdvisorApp.class.getResourceAsStream("/icon.png")));

        MenuBar menuBar = new MenuBar();
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        Menu settingsMenu = new Menu("設定");
        MenuItem toggleBetaItem = new MenuItem("AIを有効/無効にする");
        MenuItem createapi = new MenuItem("Tokenを発行");
        MenuItem exitItem = new MenuItem("終了");
        MenuItem aimodel = new MenuItem("AIモデルの切り替え");
        MenuItem stream = new MenuItem("Stream(Beta)の切り替え");
        
        
        stream.setOnAction(e -> switchBetaModel());
        aimodel.setOnAction(e -> switchAiModel());
        exitItem.setOnAction(e -> System.exit(0));
        toggleBetaItem.setOnAction(e -> toggleBeta());
        createapi.setOnAction(e -> createApiToken());
        
        MenuItem checkUpdateItem = new MenuItem("更新を確認");
        checkUpdateItem.setOnAction(e -> checkForUpdates());


        


        MenuItem versionItem = new MenuItem("現在のバージョン"+ver);
        versionItem.setOnAction(e -> showVersion());
        
        
        settingsMenu.getItems().add(aimodel);
        settingsMenu.getItems().add(stream);
        settingsMenu.getItems().add(toggleBetaItem);
        settingsMenu.getItems().add(createapi);
        settingsMenu.getItems().add(checkUpdateItem);
        settingsMenu.getItems().add(versionItem);
        settingsMenu.getItems().add(exitItem);
        
        
        
        Menu viewMenu = new Menu("表示");
        RadioMenuItem normalItem = new RadioMenuItem("通常表示");
        normalItem.setOnAction(e -> {
            System.out.println("通常表示が選択されました");
            primaryStage.setFullScreen(false);
        });
        RadioMenuItem fullscreenItem = new RadioMenuItem("フルスクリーン表示");
        fullscreenItem.setOnAction(e -> {
            System.out.println("フルスクリーン表示が選択されました");
            primaryStage.setFullScreen(true);
        });
        ToggleGroup viewGroup = new ToggleGroup();
        normalItem.setToggleGroup(viewGroup);
        fullscreenItem.setToggleGroup(viewGroup);
        viewMenu.getItems().addAll(normalItem, fullscreenItem);

        menuBar.getMenus().add(viewMenu);
        menuBar.getMenus().add(settingsMenu);

        Menu helpMenu = new Menu("ヘルプ");
        MenuItem helpItem = new MenuItem("使い方");
        helpItem.setOnAction(e -> showHelp());
        helpMenu.getItems().add(helpItem);

        menuBar.getMenus().add(helpMenu);

        betaStatusLabel = new Label("AIの使用: " + (isBetaEnabled ? "有効" : "無効"));
        Label readLabel = new Label("AIの有効/無効は設定から変更できます");
        Label descriptionLabel = new Label("エネルギー使用状況と太陽光発電のデータを入力してください。");
        Label panelLabel = new Label("太陽光パネルの数を指定してください（最大30）：");
        Slider panelScale = new Slider(1, 30, 1);
        panelCountLabel = new Label("現在のパネル数: " + (int) panelScale.getValue());
        Label sunshineLabel = new Label("日照時間を指定してください（最大24時間）：");
        Slider sunshineScale = new Slider(1, 24, 1);
        
        installationTimeLabel = new Label("太陽光発電システムを設置した年を入力してください例,2023：");
        installationTimeField = new TextField();
        
        sunshineTimeLabel = new Label("現在の日照時間: " + (int) sunshineScale.getValue() + "時間");

        generationLabel = new Label("1日の総発電量 (kWh) を入力してください：");
        generationField = new TextField();

        locationLabel = new Label("お住まいの地域を選択してください：");
        String[] prefectures = {"北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県", "茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県", "新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県", "静岡県", "愛知県", "三重県", "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県", "鳥取県", "島根県", "岡山県", "広島県", "山口県", "徳島県", "香川県", "愛媛県", "高知県", "福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県"};
        locationComboBox = new ComboBox<>();
        locationComboBox.getItems().addAll(prefectures);

        angleLabel = new Label("設置角度を指定してください（最大180°）：");
        angleScale = new Slider(0, 180, 0);
        currentAngleLabel = new Label("現在の設置角度: " + (int) angleScale.getValue() + "°");

       

        systemCapacityLabel = new Label("太陽光発電システム全体の容量 (kW) を入力してください：");
        systemCapacityField = new TextField();

        purposeLabel = new Label("アドバイスを求める具体的な目的を簡潔に入力してください（例: 発電量を増やしたい）：");
        purposeField = new TextField();

        Label aiRead = new Label("AIが有効の場合にはAIがアドバイスを生成しますただし少し時間が掛かるよ！（内容によります)");

        progressBar = new ProgressBar();
        progressBar.setVisible(false);
        analyzeButton = new Button("実行");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        gridPane.add(menuBar, 0, 0, 3, 1);

        gridPane.add(betaStatusLabel, 0, 1);
        gridPane.add(readLabel, 0, 2);
        gridPane.add(descriptionLabel, 0, 3);
        gridPane.add(panelLabel, 0, 4);
        gridPane.add(panelScale, 1, 4);
        gridPane.add(panelCountLabel, 2, 4);
        gridPane.add(installationTimeLabel, 0, 6);
        gridPane.add(installationTimeField, 1, 6);
        gridPane.add(sunshineLabel, 0, 5);
        gridPane.add(sunshineScale, 1, 5);
        gridPane.add(sunshineTimeLabel, 2, 5);
        gridPane.add(aiRead, 0, 12);
        gridPane.add(webView, 0, 16, 3, 1);
        gridPane.add(progressBar, 0, 14, 3, 1);
        gridPane.add(analyzeButton, 0, 15, 3, 1);

        descriptionLabel.setWrapText(true);
        panelLabel.setWrapText(true);
        sunshineLabel.setWrapText(true);
        sunshineTimeLabel.setWrapText(true);
        panelCountLabel.setWrapText(true);
        generationLabel.setWrapText(true);
        locationLabel.setWrapText(true);
        angleLabel.setWrapText(true);
        systemCapacityLabel.setWrapText(true);
        purposeLabel.setWrapText(true);
        aiRead.setWrapText(true);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(60);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(20);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(20);
        gridPane.getColumnConstraints().addAll(column1, column2, column3);

        panelScale.valueProperty().addListener((observable, oldValue, newValue) -> panelCountLabel.setText("現在のパネル数: " + newValue.intValue()));
        sunshineScale.valueProperty().addListener((observable, oldValue, newValue) -> sunshineTimeLabel.setText("現在の日照時間: " + newValue.intValue() + "時間"));
        angleScale.valueProperty().addListener((observable, oldValue, newValue) -> currentAngleLabel.setText("現在の設置角度: " + newValue.intValue() + "°"));

        analyzeButton.setOnAction(e -> onAnalyzeButtonClick((int) panelScale.getValue(), (int) sunshineScale.getValue(), webEngine));
        
        String css = getClass().getResource("style.css").toExternalForm();
        gridPane.getStylesheets().add(css);
        StackPane rootPane = new StackPane();
        rootPane.getChildren().add(gridPane);
        rootPane.setBackground(new Background(new BackgroundFill(Color.rgb(240, 240, 240), null, null))); 
        rootPane.getStyleClass().add("pane"); 
        primaryStage.setScene(new Scene(rootPane, 910, 700));
        primaryStage.show();

        if (isBetaEnabled) {
            addBetaComponents(gridPane);
        }
    }

    private void toggleBeta() {
        isBetaEnabled = !isBetaEnabled;
        betaStatusLabel.setText("AIの使用: " + (isBetaEnabled ? "有効" : "無効"));
        
        if (isBetaEnabled) {
            addBetaComponents((GridPane) betaStatusLabel.getParent());
            show_ui("変更","AIの使用を「有効」に切り替えました");
        } else {
            removeBetaComponents((GridPane) betaStatusLabel.getParent());
            show_ui("変更","AIの使用を「無効」に切り替えました");

        }
        
        saveSettings();
    }

    private void addBetaComponents(GridPane gridPane) {
        gridPane.add(locationLabel, 0, 7);
        gridPane.add(locationComboBox, 1, 7);
        gridPane.add(angleLabel, 0, 8);
        gridPane.add(angleScale, 1, 8);
        gridPane.add(currentAngleLabel, 2, 8);
        gridPane.add(generationLabel, 0, 9);
        gridPane.add(generationField, 1, 9);
        gridPane.add(systemCapacityLabel, 0, 10);
        gridPane.add(systemCapacityField, 1, 10);
        gridPane.add(purposeLabel, 0, 11);
        gridPane.add(purposeField, 1, 11);
    }

    private void removeBetaComponents(GridPane gridPane) {
        gridPane.getChildren().removeAll(
            generationLabel,
            generationField,
            locationLabel,
            locationComboBox,
            angleLabel,
            angleScale,
            currentAngleLabel,
            systemCapacityLabel,
            systemCapacityField,
            purposeLabel,
            purposeField
        );
    }


    private void showVersion() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("バージョン情報");
        alert.setHeaderText(null);
        alert.setContentText("バージョン:"+ver+"\n" +
                "①.ストリーミングでエラーが起きる問題を修正。\n" +
                "②. OpenAIが現時点で故障しているため代わりにHuggingChatプロバイダーを\n"+
                "Webapiのストリーミングのサポートに追加させたんでそちらを使用します。\n"+
                "④.その他Beta版からAIにちょい変更しました" +
                "以上Version"+ver+"の更新履歴です");
        alert.showAndWait();
    }

    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ヘルプ");
        alert.setHeaderText(null);
        alert.setContentText("エネルギー効率改善アドバイザーの使い方:\n" +
                "1. 太陽光パネルの数をスライダーで指定します（最大30）。\n" +
                "2. 日照時間をスライダーで指定します（最大24時間）。\n" +
                "3. '実行'ボタンをクリックします。\n" +
                "4. AIがアドバイスを生成し、テキストボックスに表示します。\n" +
                "5.AIは自作のWEBAPIです \n" + 
                "6.自動更新機能でアプデが楽になります。 \n"+
                "注意: AIの使用が有効の場合、AIがより詳細なアドバイスを提供します");
        alert.showAndWait();
    }

    private void loadSettings() {
        File settingsFile = new File("settings.json");
        if (settingsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line).append("\n");
                }

                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> settingsMap = gson.fromJson(jsonContent.toString(), type);

                isBetaEnabled = Boolean.parseBoolean(settingsMap.get("is_beta_enabled")); 
                apiUrl = settingsMap.get("api_url");
                apiToken = settingsMap.get("api_token"); 
                UseDiv = settingsMap.get("usediv"); 

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void saveSettings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("settings.json"))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String settingsJson = gson.toJson(Map.of(
                    "is_beta_enabled", isBetaEnabled,
                    "api_url", apiUrl,
                    "api_token", apiToken,
                    "usediv",UseDiv
            )); 
            writer.write(settingsJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

private void onAnalyzeButtonClick(int panelCount, int sunshineTime, WebEngine webEngine) {
    long currentTime = System.currentTimeMillis();
    if(currentTime - lastClickTime < 5000) {
        long waitTime = 3 - ((currentTime - lastClickTime) / 1000);
        showError("ClickError","再度クリックできるまで後 " + waitTime + " 秒お待ちください");
        return;
    }
    lastClickTime = currentTime;

    analyzeButton.setDisable(true);
    progressBar.setVisible(true);
    Platform.runLater(() -> webEngine.loadContent("<p>ユーザーの回答を処理しています...</p>"));

    String generation = generationField.getText();
    String selectedPrefecture = locationComboBox.getValue();
    int angle = (int) angleScale.getValue();
    String installationYearText = installationTimeField.getText();
    String systemCapacity = systemCapacityField.getText();
    String purpose = purposeField.getText();

    new Thread(() -> {
        try {
            String aiResponse;
            if (isBetaEnabled && UseDiv.equals("True")) {  // isBetaEnabled と UseDiv が両方 true の場合のみストリーミング処理
                 getAdviceFromAI_stream(panelCount, sunshineTime, generation, selectedPrefecture, angle, installationYearText, systemCapacity, purpose, webEngine);
            } else {
                // ストリーミング処理が無効の場合は、従来通り処理
                aiResponse = calculateSolarPower(panelCount, sunshineTime, generation, selectedPrefecture, angle, installationYearText, systemCapacity, purpose);
                String html = markdownToHtml(aiResponse);
                Platform.runLater(() -> webEngine.loadContent(html));
            }
    } catch (Exception e) {
    	System.out.println( e.getMessage());
        showError("エラー", "アドバイスの生成中にエラーが発生しました。");
    }
    progressBar.setVisible(false);
    analyzeButton.setDisable(false);
}).start();

}


private String calculateSolarPower(int panelCount, int sunshineTime, String generation, String selectedPrefecture, int angle, String installationYearText, String systemCapacity, String purpose) throws Exception {
    double powerPerPanel = 0.2; // kW
    double solarPower = panelCount * sunshineTime * powerPerPanel;
    StringBuilder adviceText = new StringBuilder(); // StringBuilderを使用
    int installationYear;

    if (installationYearText.isEmpty()) {
        showError("入力エラー", "設置年を入力してください。");
        return "<h1>再度設置年を入力してからお願いします</h1>"; // 処理を中断し、空文字を返す
    } else {
        installationYear = Integer.parseInt(installationYearText);
    }

    int systemLife = 20; // 太陽光発電システムの耐用年数

    adviceText.append("<p>"); // 最初に<p>タグを追加

    if (solarPower < 1) {
        adviceText.append("現在の発電量は<b>1kWh以下</b>と非常に少ないです。<br>");
        adviceText.append("発電量を増やすための具体的な提案は以下の通りです。<br>");
        adviceText.append("<ul>"); // リスト表示開始
        adviceText.append("<li>パネルの数を増やす</li>");
        adviceText.append("<li>日当たりの良い場所に設置する</li>");
        adviceText.append("<li>パネルの角度を調整する</li>");
        adviceText.append("</ul>"); // リスト表示終了
        adviceText.append("例えば、パネルをあと")
                .append((int) Math.ceil(1 / (sunshineTime * powerPerPanel)))
                .append("枚追加すれば、1kWhの発電が期待できます。<br>");
    } else if (solarPower < 5) {
        adviceText.append("現在の発電量は<b>5kWh以下</b>です。<br>");
        adviceText.append("発電効率を上げるための提案は以下の通りです。<br>");
        adviceText.append("<ul>");
        adviceText.append("<li>パネルの角度を")
                .append(angle > 30 ? "少し下げて" : "少し上げて")
                .append("みる</li>");
        adviceText.append("<li>日陰になる部分を減らす、反射光を利用するなど、設置場所を見直す</li>");
        adviceText.append("<li>")
                .append(installationYear + systemLife - currentYear)
                .append("年以上経過している場合は、最新のパネルに交換する</li>"); 
        adviceText.append("</ul>");
    } else {
        adviceText.append("素晴らしい！あなたの太陽光発電システムは1日あたり約<b>")
                .append(solarPower)
                .append("kWh</b>の電力を生成し、非常に効率的に動作しています。<br>");

        if (2024 - installationYear > systemLife / 2) {
            adviceText.append("ただし、")
                    .append(installationYear)
                    .append("年設置のシステムは、そろそろ交換時期に差し掛かっています。最新のパネルは変換効率が向上しているため、交換を検討してみてはいかがでしょうか。<br>");
        }

        if (purpose.equals("電力自給")) {
            adviceText.append("電力自給率をさらに高めるには、蓄電池の導入を検討するのも良いでしょう。<br>");
        } else if (purpose.equals("売電")) {
            adviceText.append("売電収入を増やすためには、発電量の多い時間帯に電力会社に売電できるプランへの変更も有効です。<br>");
        }
    }

    adviceText.append("資源エネルギー庁の太陽光発電ガイドも参考にしてみてください。</p>"); 

    if (isBetaEnabled) {
        return getAdviceFromAI_streamPost(panelCount, sunshineTime, generation, selectedPrefecture, angle, installationYearText, systemCapacity, purpose);
    } else {
        return adviceText.toString() + "<p>もし詳細な情報を知りたい場合にはAIの使用を有効にしてみてください。現時点2024/7/05時点では機能しています</p>";
    }
}


    private void getAdviceFromAI_stream(int panelCount, int sunshineTime, String generation, String selectedPrefecture, int angle, String installationYearText, String systemCapacity, String purpose, WebEngine webEngine) throws Exception {
        // HTTPリクエストを作成します。
        String question = "質問内容は以下通りです:太陽光発電のデータが次のような場合、どのようなアドバイスがありますか？ パネル数: " + panelCount + ", 日照時間: " + sunshineTime + "時間, 1日の総発電量(kWh): " + generation + ", どこに住んでいるか: " + selectedPrefecture + ", ソーラーパネルの設置角度: " + angle + "°, 設置した年(現在の日付は"+currentYear+"年です):" + installationYearText + ", 太陽光発電システム全体の容量(kW): " + systemCapacity + ", アドバイスを求める目的: " + purpose + "。回答形式：以下の形式に沿ってください<h1>太陽光発電システムのデータ分析とアドバイス</h1><br>" + //
        "<h2>データ:</h2>"+
        "<ul>"+
        "<li>パネル数: </li>"+
        "<li>日照時間: </li>"+
        "<li>1日の総発電量: </li>"+
        "<li>設置場所: </li>"+
        "<li>パネル設置角度: </li>"+
        "<li>設置年: </li>"+
        "<li>システム容量: </li>"+
       "</ul>"+
        "<h2>アドバイス:</h2>"+
        "というような形式であなたはマークダウン形式で出力,セキュリティ関係上これだけしか情報が提供できませんご了承ください";
        URL url = new URL("https://webapi-8trs.onrender.com/stream"); // ストリーミングレスポンスをサポートするURLに変更します。
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set up the connection
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json; utf-8");

        // Define parameters
        String provider = apiUrl;
        String system = "";
        String token = apiToken;
        Platform.runLater(() -> webEngine.loadContent(""));


        // Create JSON string
        String jsonInputString = String.format("{\"provider\": \"%s\", \"prompt\": \"%s\", \"system\": \"%s\", \"token\": \"%s\"}", provider, question, system, token);

        // Send POST request
        try(OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);           
        }

        try {
            // Now read the SSE
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    if (line.startsWith("data: ")) {
                        String jsonString = line.substring(6); // Remove "data: "
                     //   System.out.println(jsonString);
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String responseValue = jsonObject.getString("response");

                     // MarkdownをHTMLに変換します。
                        System.out.println(responseValue);
                        
                        String html = markdownToHtml(responseValue);

                        String script = "document.body.innerHTML += '" + StringEscapeUtils.escapeEcmaScript(html) + "';";
                        Platform.runLater(() -> webEngine.executeScript(script)); 
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("エラーが発生しました: " + e.getMessage());
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

private String getAdviceFromAI_streamPost(int panelCount, int sunshineTime, String generation, String selectedPrefecture, int angle, String installationYearText, String systemCapacity, String purpose) throws Exception {
    // HTTPリクエストを作成します。
    String question = "質問内容は以下通りです:太陽光発電のデータが次のような場合、どのようなアドバイスがありますか？ パネル数: " + panelCount + ", 日照時間: " + sunshineTime + "時間, 1日の総発電量(kWh): " + generation + ", どこに住んでいるか: " + selectedPrefecture + ", ソーラーパネルの設置角度: " + angle + "°, 設置した年(現在の日付は"+currentYear+"年です):" + installationYearText + ", 太陽光発電システム全体の容量(kW): " + systemCapacity + ", アドバイスを求める目的: " + purpose + "。回答形式：以下の形式に沿ってください## 太陽光発電システムのデータ分析とアドバイス\n" + //
    "<h2>データ:</h2>"+
    "<ul>"+
    "<li>パネル数: </li>"+
    "<li>日照時間: </li>"+
    "<li>1日の総発電量: </li>"+
    "<li>設置場所: </li>"+
    "<li>パネル設置角度: </li>"+
    "<li>設置年: </li>"+
    "<li>システム容量: </li>"+
   "</ul>"+
    "<h2>アドバイス:</h2>"+
    "というような形式であなたはマークダウン形式で出力をお願いしますまたセキュリティ関係上これだけしか情報が提供できませんご了承ください";
    URI uri = new URI(MainAPIurl);
    Map<String, String> jsonMap = new HashMap<>();
    jsonMap.put("prompt", question);
    jsonMap.put("provider", apiUrl);
    jsonMap.put("system", "");
    jsonMap.put("token", apiToken);
    String json = new Gson().toJson(jsonMap);

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .header("Content-Type", "application/json")
            .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    // レスポンスヘッダーを取得します。
    HttpHeaders headers = response.headers();
    headers.map().forEach((k, v) -> System.out.println(k + ":" + v));

 // レスポンスボディを取得します。
    String responseBody = response.body();
    System.out.println(responseBody);

    // Gsonを使用してレスポンスボディをJavaオブジェクトに変換します。
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, String>>(){}.getType();
    Map<String, String> myMap = gson.fromJson(responseBody, type);

    // "response"の値を取得します。
    String responseValue = myMap.get("response");

    // MarkdownをHTMLに変換します。
    String html = markdownToHtml(responseValue);

    return html;
}

public static String markdownToHtml(String markdown) {
    // ヘッダーを変換
    markdown = convertHeaders(markdown);

    // 強調、取り消し線、上付き文字、下付き文字を変換
    markdown = convertEmphasis(markdown);

    // リストを変換
    markdown = convertLists(markdown);

    // 引用ブロックを変換
    markdown = convertBlockquotes(markdown);

    // コードブロックを変換
    markdown = convertCodeBlocks(markdown);

    // 水平線を変換
    markdown = convertHorizontalRules(markdown);

    // テーブルを変換
    markdown = convertTables(markdown);

    // リンクと画像を変換
    markdown = convertLinksAndImages(markdown);
    //改行
    markdown = convertNewlines(markdown);
    //チェックボックス
    markdown = convertCheckboxes(markdown);

    return markdown;
}



        

    public static void main(String[] args) {
        launch(args);
        
    }
    
    
    
    
    
    
 // ヘッダーの変換
    private static String convertHeaders(String markdown) {
        Pattern headerPattern = Pattern.compile("^(#{1,6})(.*?)$", Pattern.MULTILINE);
        Matcher headerMatcher = headerPattern.matcher(markdown);
        while (headerMatcher.find()) {
            int level = headerMatcher.group(1).length();
            markdown = markdown.replace(headerMatcher.group(), "<h" + level + ">" + headerMatcher.group(2).trim() + "</h" + level + ">");
        }
        return markdown;
    }


    // 強調、取り消し線、上付き文字、下付き文字の変換
    private static String convertEmphasis(String markdown) {
        markdown = markdown.replaceAll("(\\*\\*|__)(.*?)\\1", "<strong>$2</strong>");
        markdown = markdown.replaceAll("(\\*|_)(.*?)\\1", "<em>$2</em>");
        markdown = markdown.replaceAll("~~(.*?)~~", "<del>$1</del>");
        markdown = markdown.replaceAll("\\^(.*?)\\^", "<sup>$1</sup>");
        markdown = markdown.replaceAll(",,(.*?),,", "<sub>$1</sub>");
        markdown = markdown.replaceAll("\\*(\\S+)\\*", "<em>$1</em>");
        return markdown;
    }
    
    // 改行の変換
    private static String convertNewlines(String markdown) {
        // 改行を タグに変換
        markdown = markdown.replaceAll("(\\r?\\n)", "<br>");

        return markdown;
    }

    // リストの変換
    private static String convertLists(String markdown) {
        markdown = markdown.replaceAll("^\\d+\\.\\s", "<li>");
        markdown = markdown.replaceAll("^\\[\\s*(x| )\\]\\s", "<li><input type=\"checkbox\"$1 checked>");
        markdown = markdown.replaceAll("^[-*+]\\s", "<li>");
        markdown = markdown.replaceAll("<li>", "<ul><li>");
        markdown = markdown.replaceAll("<li>\\s*\\<\\/ul\\>", "</ul></li>");
        markdown = markdown.replaceAll("<li>", "<ol><li>");
        markdown = markdown.replaceAll("<li>\\s*\\<\\/ol\\>", "</ol></li>");
        return markdown;
    }

 // 引用ブロックの変換
    private static String convertBlockquotes(String markdown) {
        Pattern blockquotePattern = Pattern.compile("^>(.*?)$", Pattern.MULTILINE);
        Matcher blockquoteMatcher = blockquotePattern.matcher(markdown);
        while (blockquoteMatcher.find()) {
            markdown = markdown.replace(blockquoteMatcher.group(), "<blockquote>" + blockquoteMatcher.group(1).trim() + "</blockquote>");
        }
        return markdown;
    }
 // コードブロックを変換
    private static String convertCodeBlocks(String markdown) {
        Pattern codeBlockPattern = Pattern.compile("```(.*?)```", Pattern.DOTALL);
        Matcher codeBlockMatcher = codeBlockPattern.matcher(markdown);
        while (codeBlockMatcher.find()) {
            markdown = markdown.replace(codeBlockMatcher.group(), "<pre><code>" + codeBlockMatcher.group(1).trim() + "</code></pre>");
        }
        return markdown;
    }
    
 // チェックボックスの変換
    private static String convertCheckboxes(String markdown) {
        Pattern checkboxPattern = Pattern.compile("\\[([x| ])] (.*?)$", Pattern.MULTILINE);
        Matcher checkboxMatcher = checkboxPattern.matcher(markdown);
        while (checkboxMatcher.find()) {
            String checked = checkboxMatcher.group(1).equals("x") ? " checked" : "";
            markdown = markdown.replace(checkboxMatcher.group(), "<input type=\"checkbox\"" + checked + " disabled> " + checkboxMatcher.group(2));
        }
        return markdown;
    }

    // 水平線の変換
    private static String convertHorizontalRules(String markdown) {
        markdown = markdown.replaceAll("^(?:[-*_]{3,})\\s*$", "<hr>");
        return markdown;
    }

    // テーブルの変換
 // テーブルの変換
    private static String convertTables(String markdown) {
        Pattern tablePattern = Pattern.compile("^\\|(.*)\\|\\n(\\|[-:]+\\|\\n)((\\|.*\\|\\n)*)", Pattern.MULTILINE);
        Matcher tableMatcher = tablePattern.matcher(markdown);
        while (tableMatcher.find()) {
            String tableHeader = tableMatcher.group(1).trim();
            String tableBody = tableMatcher.group(3).trim();
            String htmlTable = "<table><thead><tr><th>" + tableHeader.replace("|", "</th><th>") + "</th></tr></thead><tbody><tr><td>"
                    + tableBody.replace("|", "</td><td>").replace("\n", "</td></tr><tr><td>") + "</td></tr></tbody></table>";
            markdown = markdown.replace(tableMatcher.group(), htmlTable);
        }
        return markdown;
    }

    // リンクと画像を変換
    private static String convertLinksAndImages(String markdown) {
        markdown = markdown.replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\">$1</a>");
        markdown = markdown.replaceAll("\\!\\[(.*?)\\]\\((.*?)\\)", "<img src=\"$2\" alt=\"$1\">");
        return markdown;
    }
}