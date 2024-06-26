import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
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
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
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
    private String ver = "0.8";
    Random random = new Random();
    int user_id = 1000 + random.nextInt(9000);
    private static String apiUrl = "https://webapi-8trs.onrender.com/chat?provider=Gemini&prompt=";

   
    
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
        // 新しいバージョンが存在するかどうかを確認します。
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
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> versionMap = gson.fromJson(result.toString(), type);
            String currentVersion = ver; // 現在のバージョンを設定します。
            for (String version : versionMap.keySet()) {
                if (Float.parseFloat(version) > Float.parseFloat(currentVersion)) {
                    // 新しいバージョンが存在する場合、ユーザーに更新するかどうかを尋ねます。
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("更新の確認");
                        alert.setHeaderText("新しいバージョン " + version + " が利用可能です。更新しますか？");
                        alert.setContentText("はい！/いいえ");

                        ButtonType buttonTypeYes = new ButtonType("はい");
                        ButtonType buttonTypeNo = new ButtonType("いいえ");

                        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                        alert.showAndWait().ifPresent(response -> {
                            if (response == buttonTypeYes) {
                                checkForUpdates();
                            } else {
                                show_ui("更新","更新した方が良いよ...");
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
                String currentVersion = ver; // 現在のバージョンを設定します。
                for (String version : versionMap.keySet()) {
                    if (Float.parseFloat(version) > Float.parseFloat(currentVersion)) {
                    	show_ui("更新","新しいバージョン"+version+"使用可能な為インストール中です・・・");
                        // ダウンロードフォルダに保存するパスを設定します。
                        Path downloadFolder = Paths.get(System.getProperty("user.home"), "Downloads");
                        Path targetPath = downloadFolder.resolve("setup.exe");

                        // ファイルをダウンロードします。
                        try (InputStream in = new URI(versionMap.get(version)).toURL().openStream()) {
                            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        }


                        show_ui("更新","インストールが完了しました。: インストールしたver."+version+"のsetup.exeを起動します");
                        TimeUnit.SECONDS.sleep(2);  
                        Desktop.getDesktop().open(targetPath.toFile());
                        System.exit(0);
                        break;
                    } else {
                        show_ui("更新","最新バージョン"+currentVersion+"を使用しています");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
    }
    
    private void switchAiModel() {
        if (apiUrl.equals("https://webapi-8trs.onrender.com/gemini?prompt=")) {
            apiUrl = "https://webapi-8trs.onrender.com/chat?provider=OpenAI&prompt=";
           show_ui("変更","OpenAIモデルに切り替えました");
        } else {
            apiUrl = "https://webapi-8trs.onrender.com/chat?provider=Gemini&prompt=";
            show_ui("変更","Geminiモデルに切り替えました");
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
                    // レスポンスからトークンを取得
                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, String>>() {}.getType();
                    Map<String, String> responseMap = gson.fromJson(response.body(), type);
                    apiToken = responseMap.get("access_token");

                    // トークンを保存
                    saveSettings();

                    Platform.runLater(() -> {
                        show_ui("APIトークン", "APIトークンが生成されました: " + apiToken);
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

        // メニューバーの作成
        MenuBar menuBar = new MenuBar();
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        Menu settingsMenu = new Menu("設定");
        MenuItem toggleBetaItem = new MenuItem("Beta版を有効/無効にする");
        MenuItem createapi = new MenuItem("Tokenを発行");
        MenuItem exitItem = new MenuItem("終了");
        MenuItem aimodel = new MenuItem("AIモデルの切り替え");
        
        
        
        aimodel.setOnAction(e -> switchAiModel());
        exitItem.setOnAction(e -> System.exit(0));
        toggleBetaItem.setOnAction(e -> toggleBeta());
        createapi.setOnAction(e -> createApiToken());
        
        MenuItem checkUpdateItem = new MenuItem("更新を確認");
        checkUpdateItem.setOnAction(e -> checkForUpdates());


        


        MenuItem versionItem = new MenuItem("現在のバージョン"+ver);
        versionItem.setOnAction(e -> showVersion());
        
        
        settingsMenu.getItems().add(aimodel);
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

        // コンポーネントの作成
        betaStatusLabel = new Label("Beta版: " + (isBetaEnabled ? "有効" : "無効"));
        Label readLabel = new Label("Beta版の有効/無効は設定から変更できます");
        Label descriptionLabel = new Label("エネルギー使用状況と太陽光発電のデータを入力してください。");
        Label panelLabel = new Label("太陽光パネルの数を指定してください（最大30）：");
        Slider panelScale = new Slider(1, 30, 1);
        panelCountLabel = new Label("現在のパネル数: " + (int) panelScale.getValue());
        Label sunshineLabel = new Label("日照時間を指定してください（最大24時間）：");
        Slider sunshineScale = new Slider(1, 24, 1);
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

        installationTimeLabel = new Label("太陽光発電システムを設置した年を入力してください例,1990年：");
        installationTimeField = new TextField();

        systemCapacityLabel = new Label("太陽光発電システム全体の容量 (kW) を入力してください：");
        systemCapacityField = new TextField();

        purposeLabel = new Label("アドバイスを求める具体的な目的を簡潔に入力してください（例: 発電量を増やしたい）：");
        purposeField = new TextField();

        Label aiRead = new Label("BETA版が有効の場合にはAIがアドバイスを生成しますただし少し時間が掛かるよ！");

        progressBar = new ProgressBar();
        progressBar.setVisible(false);
        analyzeButton = new Button("実行");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        // メニューバーを最初に追加します。
        gridPane.add(menuBar, 0, 0, 3, 1);

        gridPane.add(betaStatusLabel, 0, 1);
        gridPane.add(readLabel, 0, 2);
        gridPane.add(descriptionLabel, 0, 3);
        gridPane.add(panelLabel, 0, 4);
        gridPane.add(panelScale, 1, 4);
        gridPane.add(panelCountLabel, 2, 4);
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
        installationTimeLabel.setWrapText(true);
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

        analyzeButton.setOnAction(e -> onAnalyzeButtonClick((int) panelScale.getValue(), (int) sunshineScale.getValue(), webView, webEngine));
        
        String css = getClass().getResource("style.css").toExternalForm();
        gridPane.getStylesheets().add(css);
        StackPane rootPane = new StackPane();
        rootPane.getChildren().add(gridPane);
        rootPane.setBackground(new Background(new BackgroundFill(Color.rgb(240, 240, 240), null, null))); // グレー風の背景色
        rootPane.getStyleClass().add("pane"); // CSSで定義したクラスを適用
        primaryStage.setScene(new Scene(rootPane, 910, 700));
        primaryStage.show();

        if (isBetaEnabled) {
            addBetaComponents(gridPane);
        }
    }

    private void toggleBeta() {
        isBetaEnabled = !isBetaEnabled;
        betaStatusLabel.setText("Beta版: " + (isBetaEnabled ? "有効" : "無効"));
        
        if (isBetaEnabled) {
            addBetaComponents((GridPane) betaStatusLabel.getParent());
            show_ui("変更","Betaモードを「有効」に切り替えました");
        } else {
            removeBetaComponents((GridPane) betaStatusLabel.getParent());
            show_ui("変更","Betaモードを「無効」に切り替えました");

        }
        
        saveSettings();
    }

    private void addBetaComponents(GridPane gridPane) {
        gridPane.add(generationLabel, 0, 6);
        gridPane.add(generationField, 1, 6);
        gridPane.add(locationLabel, 0, 7);
        gridPane.add(locationComboBox, 1, 7);
        gridPane.add(angleLabel, 0, 8);
        gridPane.add(angleScale, 1, 8);
        gridPane.add(currentAngleLabel, 2, 8);
        gridPane.add(installationTimeLabel, 0, 9);
        gridPane.add(installationTimeField, 1, 9);
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
            installationTimeLabel,
            installationTimeField,
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
                "①. WEBAPIのパラメータの変更に対応。\n" +
                "②. それだけ。\n" +
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
                "注意: Beta版が有効の場合、AIがより詳細なアドバイスを提供します");
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

                isBetaEnabled = Boolean.parseBoolean(settingsMap.get("is_beta_enabled")); // 修正！
                apiUrl = settingsMap.get("api_url");
                apiToken = settingsMap.get("api_token"); // apiToken を読み込む

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 設定を保存するメソッド
    private void saveSettings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("settings.json"))) {
            // GsonBuilderを使って整形オプションを設定
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String settingsJson = gson.toJson(Map.of(
                    "is_beta_enabled", isBetaEnabled,
                    "api_url", apiUrl,
                    "api_token", apiToken
            )); 
            writer.write(settingsJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

private void onAnalyzeButtonClick(int panelCount, int sunshineTime, WebView webView, WebEngine webEngine) {
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
            String aiResponse = calculateSolarPower(panelCount, sunshineTime, generation, selectedPrefecture, angle, installationYearText, systemCapacity, purpose);
            String html = markdownToHtml(aiResponse);
            Platform.runLater(() -> webEngine.loadContent(html));
        } catch (Exception e) {
            showError("エラー", "アドバイスの生成中にエラーが発生しました。");
        }
        progressBar.setVisible(false);
        analyzeButton.setDisable(false);
    }).start();
}


    private String calculateSolarPower(int panelCount, int sunshineTime, String generation, String selectedPrefecture, int angle, String installationYearText, String systemCapacity, String purpose) throws Exception {
        double powerPerPanel = 0.2; // kW
        double solarPower = panelCount * sunshineTime * powerPerPanel;
        String adviceText = "";

        if (solarPower < 1) {
            adviceText = "<p>現在の発電量は<b>1kWh以下</b>となっています。パネルの数や日照時間を増やすことで、発電量を増やすことが可能です。参考になる資料として、<a href='https://www.enecho.meti.go.jp/category/saving_and_new/saiene/renewable/solar/index.html'>資源エネルギー庁の太陽光発電ガイド</a>をご覧ください。</p>";
        } else if (solarPower < 5) {
            adviceText = "<p>現在の発電量は<b>5kWh以下</b>となっています。発電量をさらに向上させるためには、パネルの角度や設置場所を見直すことをお勧めします。参考になる資料として、<a href='https://www.enecho.meti.go.jp/category/saving_and_new/saiene/renewable/solar/index.html'>資源エネルギー庁の太陽光発電ガイド</a>をご覧ください。</p>";
        } else {
            adviceText = "<p>素晴らしい！あなたの太陽光発電システムは非常に効率的に動作しています。さらなる効率化のための情報は、<a href='https://www.enecho.meti.go.jp/category/saving_and_new/saiene/renewable/solar/index.html'>資源エネルギー庁の太陽光発電ガイド</a>をご覧ください。</p>";
        }

        if (isBetaEnabled) {
            return getAdviceFromAI(panelCount, sunshineTime, generation, selectedPrefecture, angle, installationYearText, systemCapacity, purpose);
        } else {
            return "<p>あなたの太陽光発電システムは、1日あたり約<b>" + solarPower + "kWh</b>の電力を生成します。</p>" + adviceText + "<p>もし詳細な情報を知りたい場合にはBeta版を有効にしてみてください。現時点2024/5/28の段階ではまだ未完成です</p>";
        }
    }


    private String getAdviceFromAI(int panelCount, int sunshineTime, String generation, String selectedPrefecture, int angle, String installationYearText, String systemCapacity, String purpose) throws Exception {
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
    "というような形式でマークダウン形式で出力,セキュリティ関係上これだけしか情報が提供できませんご了承ください";
    int userID = user_id;
    String userid = "&user_id="+ userID;
    String token = apiToken;
    String NToken = "&token="+token;
    String encodedQuestion = URLEncoder.encode(question, StandardCharsets.UTF_8.toString());
    String NEWToken = URLEncoder.encode(NToken, StandardCharsets.UTF_8.toString());
    URI uri = new URI(apiUrl + encodedQuestion + userid + NEWToken);
    System.out.println(apiUrl + encodedQuestion + userid + NEWToken);

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .header("Content-Type", "application/json")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
            .GET()
            .build();

    try {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println(response);

        // JSONオブジェクトとしてレスポンスを解析します。
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(response.body(), type);

        // "response"フィールドが存在するかどうかを確認します。
        if (responseMap.containsKey("response")) {
            // 存在する場合はその値を使用します。
            return responseMap.get("response");
        } else {
            // 存在しない場合はデフォルトのメッセージを使用します。
            return "AIの回答に問題が起きています,";
        }
    } catch (Exception e) {
        // エラーメッセージをログに記録または表示します。
        System.err.println("エラーが発生しました: " + e.getMessage());
        // エラーが発生した場合は適切なメッセージを返します。
        return "サーバーからのレスポンスを受け取る際にエラーが発生しました<br>(同じ値での送信をした場合に400エラーが返されますそれ以外にはServer側のAIに異常が起きてエラーになっている可能性があります)<br>追記：Version0.6から発生しているエラーでhttps;//webapi-8trs.onrender.comというのがあるのですが原因不明な為解決に時間を要する可能性があります(現段階では字数制限かも)<br>エラー内容："+e.getMessage()+".";
    }
}
        
    private static String markdownToHtml(String markdown) {

		Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        String html = renderer.render(document);

        // 改行文字をHTMLの改行タグに置き換える
        final String finalHtml = html.replace("\\n", "<br>");
        
		return finalHtml;
	}

    public static void main(String[] args) {
        launch(args);
        
    }
}

