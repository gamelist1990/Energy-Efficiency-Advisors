import java.io.IOException;

public class Launcher {
    public static void main(String[] args) {
        try {
            String currentDir = System.getProperty("user.dir"); // 現在のディレクトリを取得
            String modulePath = currentDir + "/lib"; // 'lib'ディレクトリへのパス
            String javaPath = currentDir + "/fulljre/bin/java.exe"; // カスタムJREへのパス
            String jarPath = currentDir + "/application.jar"; // application.jarへのパス

            ProcessBuilder processBuilder = new ProcessBuilder(
                javaPath,
                "--module-path", modulePath,
                "--add-modules", "javafx.controls,javafx.web",
                "--add-exports", "javafx.base/com.sun.javafx.logging=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.glass.ui=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.glass.utils=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.javafx.font=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.javafx.geom.transform=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
                "--add-exports", "javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.javafx.scene.input=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.javafx.text=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.javafx.util=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.prism=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.prism.paint=ALL-UNNAMED",
                "--add-exports", "javafx.graphics/com.sun.scenario.effect=ALL-UNNAMED",
                "-jar", jarPath
            );

            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
