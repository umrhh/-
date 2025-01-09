import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WebPageWordCounter {
    public static void main(String[] args) {
        System.out.println("网站\"https://www.wikihow.com/Main-Page\"单词出现频率降序如下:");
        String webpageUrl = "https://www.wikihow.com/Main-Page"; // 读取用户输入的网页URL

        // 获取单词出现频次
        Map<String, Integer> wordCount = getWordCountFromWebpage(webpageUrl);

        // 根据频次按降序排列
        wordCount = wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // 如果键冲突，保留第一个值
                        LinkedHashMap::new // 使用LinkedHashMap保持插入顺序
                ));

        wordCount.forEach((word, count) -> System.out.println(word + ": " + count));
    }

    public static Map<String, Integer> getWordCountFromWebpage(String url) {
        Map<String, Integer> wordCount = new HashMap<>();

        try {
            // 下载网页内容
            String content = downloadWebpageContent(url);

            // 提取文本内容
            String text = extractTextContent(content);

            // 统计单词频次
            String[] words = text.replaceAll("[^a-zA-Z0-9 ]", " ") // 替换非字母数字的字符为空格
                    .toLowerCase() // 转为小写
                    .trim() // 去除首尾空格
                    .split("\\s+"); // 按空白字符分割

            for (String word : words) {
                if (!word.isEmpty()) { // 排除空单词
                    wordCount.merge(word, 1, Integer::sum); // 统计单词出现频次
                }
            }
        } catch (IOException e) {
            System.err.println("处理网页时出错: " + e.getMessage());
        }

        return wordCount;
    }

    private static String downloadWebpageContent(String urlString) throws IOException {
        URL url = new URL(urlString); // 创建URL对象
        HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // 打开连接
        connection.setRequestMethod("GET"); // 设置请求方法

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return in.lines().collect(Collectors.joining("\n")); // 读取网页内容
        }
    }

    private static String extractTextContent(String html) {
        // 替换掉<script>和<style>标签内的内容以及其它HTML标签
        return html.replaceAll("(?s)<script[^>]*>.*?</script>", "")
                .replaceAll("(?s)<style[^>]*>.*?</style>", "")
                .replaceAll("(?s)<[^>]+>", "")
                .trim(); // 去除首尾空白
    }
}