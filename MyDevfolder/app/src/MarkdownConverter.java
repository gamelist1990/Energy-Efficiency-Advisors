import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownConverter {

    public static void main(String[] args) {
        String markdown = ""
        		+ "##太陽光発電システムのデータ分析とアドバイス\n"
                + "\n"
                + "**データ:**\n"
                + "\n"
                + "* パネル数: 1枚\n"
                + "* 日照時間: 1時間\n"
                + "* 1日の総発電量: 不明\n"
                + "* 設置場所: 不明\n"
                + "* パネル設置角度: 0度\n"
                + "* 設置年: 不明\n"
                + "* システム容量: 不明\n"
                + "\n"
                + "**アドバイス:**\n"
                + "\n"
                + "上記の情報では、太陽光発電システムの分析やアドバイスが困難です。発電量を評価するには、以下の情報が必要です。\n"
                + "\n"
                + "* **1日の総発電量:** システム効率と発電量予測のための重要な指標\n"
                + "* **設置場所:** 日射量によって発電量が大きく異なるため、年間発電量予測に必要\n"
                + "* **システム容量:** パネル枚数と出力から決定され、システム規模を把握できる\n"
                + "\n"
                + "さらに詳細な分析やアドバイスには、以下の情報も有用です。\n"
                + "\n"
                + "* パネルの種類: 発電効率と性能に影響\n"
                + "* パワーコンディショナーの種類: 変換効率が異なる\n"
                + "* 設置状況: 日当たりや影の影響を考慮\n"
                + "\n"
                + "**情報収集方法:**\n"
                + "\n"
                + "* **太陽光発電システムのモニタリングシステム:** 発電量や日射量を記録\n"
                + "* **電力会社:** 太陽光発電データを提供する場合がある\n"
                + "* **専門業者:** システム分析とアドバイスを提供\n"
                + "\n"
                + "**アドバイス:**\n"
                + "\n"
                + "* **発電量改善策:** 日射量が少ない場合は角度調整や影の除去、システム増設や高効率パネルへの交換を検討\n"
                + "* **メンテナンス:** 定期的なメンテナンスで性能維持。パネル清掃やパワーコンディショナーの点検など\n"
                + "* **補助金・融資:** 導入には国や自治体からの補助金や金融機関の融資を検討\n"
                + "\n"
                + "**その他:** 環境に優しい発電方法として注目。環境影響も考慮し有効活用を\n";

        System.out.println(markdownToHtml(markdown));
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