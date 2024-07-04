import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.html.HTMLEditorKit;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;




public class EnergyAdvisorApp {

    private static boolean isBetaEnabled = false;
    private static JLabel betaStatusLabel;
    private static JTextPane adviceText;
    private static JProgressBar progressBar;
    private static JButton analyzeButton;
    private static JLabel panelCountLabel;
    private static JLabel sunshineTimeLabel;
    private static JLabel generationLabel;
    private static JTextField generationField;
    private static JLabel locationLabel;
    private static JLabel installationTimeLabel;
    private static JTextField installationTimeField;
    private static JLabel systemCapacityLabel;
    private static JTextField systemCapacityField;
    private static JLabel purposeLabel;
    private static JTextField purposeField;
    private static JSlider angleScale;
    private static JLabel angleLabel;
    private static JLabel currentAngleLabel;
    private static JComboBox<String> locationComboBox;
    private static int currentYear = Year.now().getValue();
    
    

    public static void main(String[] args) {
        loadSettings();
        SwingUtilities.invokeLater(EnergyAdvisorApp::createAndShowGUI);
        
        
        
        
    }

    private static void createAndShowGUI() {
    	
        JFrame frame = new JFrame("エネルギー効率改善アドバイザー");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        
        // アイコンの設定
        String iconUrl = "https://raw.githubusercontent.com/Gamelist2023/lol/main/app.ico";
        try {
            URL url = new URL(iconUrl);
            ImageIcon icon = new ImageIcon(url);
            frame.setIconImage(icon.getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // メニューバーの作成
        JMenuBar menuBar = new JMenuBar();
        JMenu settingsMenu = new JMenu("設定");
        JMenuItem toggleBetaItem = new JMenuItem("Beta版を有効/無効にする");
        toggleBetaItem.addActionListener(e -> toggleBeta());
        settingsMenu.add(toggleBetaItem);
        JMenuItem versionItem = new JMenuItem("現在のバージョン永遠の0.2");
        versionItem.addActionListener(e -> showVersion());
        settingsMenu.add(versionItem);
        menuBar.add(settingsMenu);
        

        JMenu helpMenu = new JMenu("ヘルプ");
        JMenuItem helpItem = new JMenuItem("使い方");
        helpItem.addActionListener(e -> showHelp());
        helpMenu.add(helpItem);
        menuBar.add(helpMenu);

        frame.setJMenuBar(menuBar);
        
        
        
   
        


        // コンポーネントの作成
        //beta無効の場合はここから beta有効の場合は全て
        betaStatusLabel = new JLabel("Beta版: " + (isBetaEnabled ? "有効" : "無効"));
        JLabel readLabel = new JLabel("Beta版の有効/無効は設定から変更できます");
        JLabel descriptionLabel = new JLabel("エネルギー使用状況と太陽光発電のデータを入力してください。");
        JLabel panelLabel = new JLabel("太陽光パネルの数を指定してください（最大30）：");
        JSlider panelScale = new JSlider(1, 30);
        panelCountLabel = new JLabel("現在のパネル数: " + panelScale.getValue());
        JLabel sunshineLabel = new JLabel("日照時間を指定してください（最大24時間）：");
        JSlider sunshineScale = new JSlider(1, 24);
        sunshineTimeLabel = new JLabel("現在の日照時間: " + sunshineScale.getValue() + "時間");
        
        //ここまで
        

        generationLabel = new JLabel("1日の総発電量 (kWh) を入力してください：");
        generationLabel.setVisible(isBetaEnabled);
        generationField = new JTextField();
        generationField.setVisible(isBetaEnabled);
        

       

        // お住まいの地域を選択する部分
        locationLabel = new JLabel("お住まいの地域を選択してください：");
        locationLabel.setVisible(isBetaEnabled);
        String[] prefectures = {"北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県", "茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県", "新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県", "岐阜県", "静岡県", "愛知県", "三重県", "滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県", "鳥取県", "島根県", "岡山県", "広島県", "山口県", "徳島県", "香川県", "愛媛県", "高知県", "福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県"};
        locationComboBox = new JComboBox<>(prefectures);;
        locationComboBox.setVisible(isBetaEnabled);
        locationComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selectedPrefecture = (String)e.getItem();
                    
                }
            }
        });

       

        // 設置角度を指定する部分
        angleLabel = new JLabel("設置角度を指定してください（最大180°）：");
        angleLabel.setVisible(isBetaEnabled);
        angleScale = new JSlider(0, 180);
        angleScale.setVisible(isBetaEnabled);
        currentAngleLabel = new JLabel("現在の設置角度: " + angleScale.getValue() + "°");
        currentAngleLabel.setVisible(isBetaEnabled);


        installationTimeLabel = new JLabel("太陽光発電システムを設置した年を入力してください例,1990年：");
        installationTimeLabel.setVisible(isBetaEnabled);
        installationTimeField = new JTextField();
        if (installationTimeField != null) {
            installationTimeField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    String input = installationTimeField.getText();
                    if (!input.matches("^\\d{4}年$")) {
                        JOptionPane.showMessageDialog(null, "入力は4桁の数字で始まり、「年」で終わる必要があります。例：1990年");
                        installationTimeField.requestFocus();
                    }
                }
            });
        }
        installationTimeField.setVisible(isBetaEnabled);

        

        systemCapacityLabel = new JLabel("太陽光発電システム全体の容量 (kW) を入力してください：");
        systemCapacityLabel.setVisible(isBetaEnabled);
        systemCapacityField = new JTextField();
        systemCapacityField.setVisible(isBetaEnabled);

        purposeLabel = new JLabel("アドバイスを求める具体的な目的を入力してください（例: 発電量を増やしたい、売電収入を増やしたい、故障が心配）：");
        purposeLabel.setVisible(isBetaEnabled);
        purposeField = new JTextField(30);
        purposeField.setVisible(isBetaEnabled);

        JLabel aiRead = new JLabel("BETA版が有効の場合にはAIがアドバイスを生成しますただし少し時間が掛かるよ！");
        adviceText = new JTextPane();
        adviceText.setEditorKit(new HTMLEditorKit());
        adviceText.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(adviceText);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        analyzeButton = new JButton("実行");
        

        // パネル
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(betaStatusLabel);
        panel.add(readLabel);
        panel.add(descriptionLabel);
        panel.add(panelLabel);
        panel.add(panelScale);
        panel.add(panelCountLabel);
        panel.add(sunshineLabel);
        panel.add(sunshineScale);
        panel.add(sunshineTimeLabel);
        panel.add(generationLabel); 
        panel.add(generationField); 
        panel.add(locationLabel); 
        panel.add(locationComboBox); 
        panel.add(angleLabel); 
        panel.add(angleScale); 
        panel.add(currentAngleLabel); 
        panel.add(installationTimeLabel); 
        panel.add(installationTimeField); 
        panel.add(systemCapacityLabel); 
        panel.add(systemCapacityField);
        panel.add(purposeLabel); 
        panel.add(purposeField); 
        panel.add(aiRead);
        panel.add(scrollPane);
        panel.add(progressBar);
        panel.add(analyzeButton);
        
        Dimension maxFieldSize = new Dimension(200, 20);
        Dimension ps = new Dimension(500, 100);
        
        locationComboBox.setMaximumSize(maxFieldSize);
        generationField.setMaximumSize(maxFieldSize);
        installationTimeField.setMaximumSize(maxFieldSize);
        systemCapacityField.setMaximumSize(maxFieldSize);
        purposeField.setMaximumSize(ps);



        panelScale.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                panelCountLabel.setText("現在のパネル数: " + ((JSlider)e.getSource()).getValue());
            }
        });

        sunshineScale.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                sunshineTimeLabel.setText("現在の日照時間: " + ((JSlider)e.getSource()).getValue() + "時間");
            }
        });
        
        angleScale.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		currentAngleLabel.setText("現在の設置角度: " + ((JSlider)e.getSource()).getValue() + "°");
            }
        });
        
  

        analyzeButton.addActionListener(e -> onAnalyzeButtonClick(panelScale.getValue(), sunshineScale.getValue()));
        analyzeButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                analyzeButton.setBackground(Color.GRAY);
            }
            public void mouseReleased(MouseEvent me) {
                analyzeButton.setBackground(UIManager.getColor("control"));
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void toggleBeta() {
        isBetaEnabled = !isBetaEnabled;
        betaStatusLabel.setText("Beta版: " + (isBetaEnabled ? "有効" : "無効"));
        generationLabel.setVisible(isBetaEnabled);
        generationField.setVisible(isBetaEnabled);
        locationLabel.setVisible(isBetaEnabled);
        installationTimeLabel.setVisible(isBetaEnabled);
        installationTimeField.setVisible(isBetaEnabled);
        systemCapacityLabel.setVisible(isBetaEnabled);
        systemCapacityField.setVisible(isBetaEnabled);
        purposeLabel.setVisible(isBetaEnabled);
        locationComboBox.setVisible(isBetaEnabled);
        purposeField.setVisible(isBetaEnabled);
        saveSettings();
    }


    private static void showVersion() {
        JOptionPane.showMessageDialog(null, "バージョン: 1.0", "バージョン情報", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showHelp() {
        String helpText = "エネルギー効率改善アドバイザーの使い方:\n" +
            "1. 太陽光パネルの数をスライダーで指定します（最大30）。\n" +
            "2. 日照時間をスライダーで指定します（最大24時間）。\n" +
            "3. '実行'ボタンをクリックします。\n" +
            "4. AIがアドバイスを生成し、テキストボックスに表示します。\n" +
            "注意: Beta版が有効の場合、AIがより詳細なアドバイスを提供します。\n" +
            "5. AIは自作のWEBAPIです\n";
        JOptionPane.showMessageDialog(
            null, 
            helpText, 
            "ヘルプ", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    

    private static void loadSettings() {
        File settingsFile = new File("settings.json");
        if (settingsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                String settings = content.toString();
                isBetaEnabled = settings.contains("\"is_beta_enabled\":true");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveSettings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("settings.json"))) {
            writer.write("{\"is_beta_enabled\":" + isBetaEnabled + "}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void onAnalyzeButtonClick(int panelCount, int sunshineTime) {
        analyzeButton.setEnabled(false);
        progressBar.setVisible(true);
        
        
        String generation = generationField.getText();
        String selectedPrefecture = (String)locationComboBox.getSelectedItem();
        int angle = angleScale.getValue();
        String installationYearText = installationTimeField.getText();
        String systemCapacity = systemCapacityField.getText();
        String purpose = purposeField.getText();

        SwingWorker<String, String> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return calculateSolarPower(panelCount, sunshineTime, generation, selectedPrefecture, angle, installationYearText, systemCapacity, purpose);
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    adviceText.setText(adviceText.getText() + chunk);
                }
            }

            @Override
            protected void done() {
                try {
                    String aiResponse = get();
                    // AIの回答をHTMLとして解釈して表示する
                    adviceText.setContentType("text/html");
                    // AIの回答をエスケープせずに表示する
                    adviceText.setText("<html>" + aiResponse + "</html>");
                } catch (InterruptedException | ExecutionException e) {
                    adviceText.setText("エラーが発生しました。");
                }
                progressBar.setVisible(false);
                analyzeButton.setEnabled(true);
            }

        };

        worker.execute();
    }


    private static String calculateSolarPower(int panelCount, int sunshineTime, String generation, String selectedPrefecture, int angle, String installationYearText, String systemCapacity, String purpose) throws Exception {
        double powerPerPanel = 0.2; // kW
        double solarPower = panelCount * sunshineTime * powerPerPanel;
        if (isBetaEnabled) {
        	return getAdviceFromAI(panelCount, sunshineTime, generation, selectedPrefecture, angle, installationYearText, systemCapacity, purpose);
        } else {
            return "あなたの太陽光発電システムは、1日あたり約" + solarPower + "kWhの電力を生成します。もし詳細な情報を知りたい場合にはBeta版を有効にしてみてください。現時点2024/5/22の段階ではまだ未完成です";
        }
    }

    private static String getAdviceFromAI(int panelCount, int sunshineTime, String generation, String selectedPrefecture, int angle, String installationYearText, String systemCapacity, String purpose)  throws Exception {
        String apiUrl = "https://webapi-8trs.onrender.com/chat?provider=GeminiPro&prompt=";
        
        String question = "質問内容は以下通りです:太陽光発電のデータが次のような場合、どのようなアドバイスがありますか？ パネル数: " + panelCount + ", 日照時間: " + sunshineTime + "時間, 1日の総発電量(kWh): " + generation + ", どこに住んでいるか: " + selectedPrefecture + ", ソーラーパネルの設置角度: " + angle + "°, 設置した年(現在の日付は"+currentYear+"年です):" + installationYearText + ", 太陽光発電システム全体の容量(kW): " + systemCapacity + ", アドバイスを求める具体的な目的: " + purpose + "。回答形式：この回答ではユーザーが分かりやすくする為リストアップ形式で出力またできるだけ分かりやすいようにまたセキュリティ関係上これだけしか情報が提供できませんご了承ください";



        String encodedQuestion = URLEncoder.encode(question, StandardCharsets.UTF_8.toString());
        URL url = new URL(apiUrl + encodedQuestion);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
                publish(responseLine);
            }
            String markdownResponse = response.toString();
            
            String htmlResponse = markdownToHtml(markdownResponse);
            
            return htmlResponse;
        }
    }

	private static void publish(String responseLine) {
		
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


}