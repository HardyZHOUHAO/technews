import java.io.*;
import java.net.*;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Web {
    
    private List<NewsItem> allNews;
    
    private String[] comments = {
        "今天的科技新闻真有意思，{keyword} 又有新进展！",
        "这条关于 {keyword} 的新闻，感觉未来已来。",
        "{keyword} 这个话题最近很火，值得关注。"
    };
    
    public Web() {
        this.allNews = new ArrayList<>();
    }
    
    // ========== Guardian API ==========
    
    public List<NewsItem> fetchGuardian() {
    List<NewsItem> news = new ArrayList<>();
    try {
        String apiKey = "5bc21cfa-ec62-47ba-a575-6124bb4b5a81";
        String url = "https://content.guardianapis.com/search?api-key=" + apiKey 
            + "&section=technology&show-fields=trailText&page-size=10";
        Thread.sleep(2000);
        
        URL apiUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        conn.setRequestProperty("User-Agent", "StudentProject/1.0");
        conn.connect();
        
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream()));
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) json.append(line);
        reader.close();
        
        String jsonStr = json.toString();
        String[] parts = jsonStr.split("\"webTitle\":\"");
        for (int i = 1; i < parts.length; i++) {
            String title = parts[i].split("\"")[0];
            String link = "";
            int u = parts[i].indexOf("\"webUrl\":\"");
            if (u >= 0) link = parts[i].substring(u + 10).split("\"")[0];
            String summary = "";
            int t = parts[i].indexOf("\"trailText\":\"");
            if (t >= 0) summary = parts[i].substring(t + 13).split("\"")[0];
            
            // 点进文章页拿图片
            String img = "";
            try {
                Thread.sleep(2000);
                Document articleDoc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0")
                    .timeout(8000)
                    .get();
                Element imgEl = articleDoc.select("meta[property='og:image']").first();
                if (imgEl != null) img = imgEl.attr("content");
                if (img.isEmpty()) {
                    Element firstImg = articleDoc.select("img").first();
                    if (firstImg != null) img = firstImg.absUrl("src");
                }
            } catch (Exception e) { }
            
            news.add(new NewsItem(title, summary, link, img, "The Guardian"));
        }
    } catch (Exception e) {
        System.out.println("Guardian API出错: " + e.getMessage());
    }
    return news;
}
    
    // ========== CNN RSS ==========
    
public List<NewsItem> fetchCNN() {
    List<NewsItem> news = new ArrayList<>();
    try {
        String rssUrl = "http://rss.cnn.com/rss/edition_technology.rss";
        Thread.sleep(3000);
        
        Document doc = Jsoup.connect(rssUrl)
            .userAgent("Mozilla/5.0")
            .timeout(10000)
            .ignoreContentType(true)
            .get();
        
        Elements items = doc.select("item");
        for (Element item : items) {
            String title = item.select("title").first().text();
            String link = item.select("link").first().text();
            String summary = "";
            Element descEl = item.select("description").first();
            if (descEl != null) {
                summary = Jsoup.parse(descEl.text()).text();
                if (summary.length() > 100) summary = summary.substring(0, 97) + "...";
            }
            
            // 点进文章页拿图片
            String img = "";
            try {
                Thread.sleep(2000);
                Document articleDoc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0")
                    .timeout(8000)
                    .get();
                Element imgEl = articleDoc.select("meta[property='og:image']").first();
                if (imgEl != null) img = imgEl.attr("content");
                if (img.isEmpty()) {
                    Element firstImg = articleDoc.select("img").first();
                    if (firstImg != null) img = firstImg.absUrl("src");
                }
            } catch (Exception e) {
                // 拿不到图片就算了
            }
            
            news.add(new NewsItem(title, summary, link, img, "CNN"));
        }
    } catch (Exception e) {
        System.out.println("CNN出错: " + e.getMessage());
    }
    return news;
}
    
    // ========== BBC RSS ==========
    
   public List<NewsItem> fetchBBC() {
    List<NewsItem> news = new ArrayList<>();
    try {
        String rssUrl = "https://feeds.bbci.co.uk/news/technology/rss.xml";
        Thread.sleep(3000);
        
        Document doc = Jsoup.connect(rssUrl)
            .userAgent("Mozilla/5.0")
            .timeout(10000)
            .ignoreContentType(true)
            .get();
        
        Elements items = doc.select("item");
        int count = 0;
        for (Element item : items) {
            if (count >= 5) break; // 只取前5条，节省时间
            String title = item.select("title").first().text();
            String link = item.select("link").first().text();
            String summary = "";
            Element descEl = item.select("description").first();
            if (descEl != null) {
                summary = Jsoup.parse(descEl.text()).text();
                if (summary.length() > 100) summary = summary.substring(0, 97) + "...";
            }
            
            // 进文章页拿图
            String img = "";
            try {
                Thread.sleep(2000);
                Document articleDoc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0")
                    .timeout(8000)
                    .get();
                Element imgEl = articleDoc.select("meta[property='og:image']").first();
                if (imgEl != null) img = imgEl.attr("content");
                if (img.isEmpty()) {
                    Element firstImg = articleDoc.select("img").first();
                    if (firstImg != null) img = firstImg.absUrl("src");
                }
            } catch (Exception e) { }
            
            news.add(new NewsItem(title, summary, link, img, "BBC News"));
            count++;
        }
    } catch (Exception e) {
        System.out.println("BBC出错: " + e.getMessage());
    }
    return news;
}
    
    // ========== 聚合 ==========
    
    public List<NewsItem> aggregate() {
        allNews.clear();
        allNews.addAll(fetchGuardian());
        allNews.addAll(fetchCNN());
        allNews.addAll(fetchBBC());
        return allNews;
    }
    
    // ========== 随机评论 ==========
    
    public String generateComment() {
        if (allNews.isEmpty()) return "今日暂无新闻。";
        Random rand = new Random();
        NewsItem item = allNews.get(rand.nextInt(allNews.size()));
        String[] words = item.getTitle().split(" ");
        String keyword = words.length > 2 ? words[words.length / 2] : "科技";
        return comments[rand.nextInt(comments.length)].replace("{keyword}", keyword);
    }
    
    // ========== 拼写检查 ==========
    
    public String checkSpelling(String word) {
        try {
            String url = "https://api.datamuse.com/words?sp=" + 
                URLEncoder.encode(word, "UTF-8") + "&max=1";
            URL apiUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            reader.close();
            if (response != null && response.contains("\"word\":\"" + word.toLowerCase() + "\"")) {
                return "✅ \"" + word + "\" 拼写正确";
            } else if (response != null && response.contains("\"word\":\"")) {
                int start = response.indexOf("\"word\":\"") + 8;
                String suggestion = response.substring(start).split("\"")[0];
                return "❌ 找不到 \"" + word + "\"，您是否要找：" + suggestion + "？";
            }
            return "❌ 找不到 \"" + word + "\"";
        } catch (Exception e) {
            return "检查出错";
        }
    }
    
    // ========== 生成HTML网页 ==========
    
    public void generateHTML(List<NewsItem> newsList) {
        try {
            FileWriter writer = new FileWriter("news.html");
            writer.write("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            writer.write("<title>TechNews</title>");
            writer.write("<style>");
            writer.write("*{margin:0;padding:0;box-sizing:border-box;}");
            writer.write("body{font-family:Arial;background:#f5f5f5;max-width:900px;margin:0 auto;padding:20px;}");
            writer.write(".header{text-align:center;padding:30px 0;border-bottom:3px solid #2196F3;margin-bottom:20px;}");
            writer.write(".header h1{font-size:2em;color:#1565C0;}");
            writer.write(".comment-box{background:#E3F2FD;padding:15px;border-radius:8px;margin-bottom:20px;}");
            writer.write(".sound-link{margin-bottom:20px;}");
            writer.write(".sound-link a{color:#2196F3;text-decoration:none;}");
            writer.write(".news-card{background:#fff;border-radius:10px;display:flex;margin-bottom:20px;box-shadow:0 2px 8px rgba(0,0,0,0.1);overflow:hidden;}");
            writer.write(".img-container{width:200px;min-width:200px;}");
            writer.write(".news-img{width:100%;height:150px;object-fit:cover;transition:transform 0.3s;cursor:pointer;}");
            writer.write(".news-img:hover{transform:scale(1.2);}");
            writer.write(".news-img.zoomed{position:fixed;top:50%;left:50%;transform:translate(-50%,-50%) scale(2.5);z-index:999;}");
            writer.write(".news-content{padding:15px;}");
            writer.write(".news-content h3 a{color:#1565C0;text-decoration:none;}");
            writer.write(".summary{color:#666;margin:8px 0;}");
            writer.write(".source{color:#999;font-size:0.85em;}");
            writer.write(".spell-box{margin-top:40px;padding:20px;background:#fff;border-radius:10px;text-align:center;}");
            writer.write(".spell-box input{padding:10px;width:250px;font-size:1em;}");
            writer.write(".spell-box button{padding:10px 20px;background:#2196F3;color:#fff;border:none;border-radius:5px;cursor:pointer;margin-left:10px;}");
            writer.write("@media(max-width:600px){.news-card{flex-direction:column;}.img-container{width:100%;}}");
            writer.write("</style></head><body>");
            
            writer.write("<div class='header'><h1>📰 TechNews</h1>");
            writer.write("<p>Guardian · CNN · BBC News</p></div>");
            writer.write("<div class='comment-box'><p>💬 " + generateComment() + "</p></div>");
            writer.write("<div class='sound-link'><a href='#' onclick=\"new Audio('https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3').play();return false;\">🔊 点击播放提示音</a></div>");
            
            for (NewsItem item : newsList) {
                writer.write("<div class='news-card'>");
                if (!item.getImageUrl().isEmpty()) {
                    writer.write("<div class='img-container'><img src='" + item.getImageUrl() + "' class='news-img' onclick='this.classList.toggle(\"zoomed\")'></div>");
                } else {
                     writer.write("<div class='img-container' style='background:#e0e0e0;display:flex;align-items:center;justify-content:center;height:150px;'><span style='color:#999;font-size:0.9em;'>No pictuers</span></div>");
}
                writer.write("<div class='news-content'>");
                writer.write("<h3><a href='" + item.getLink() + "' target='_blank'>" + item.getTitle() + "</a></h3>");
                writer.write("<p class='summary'>" + item.getShortSummary() + "</p>");
                writer.write("<span class='source'>📍 " + item.getSource() + "</span>");
                writer.write("</div></div>");
            }
            
            writer.write("<div class='spell-box'><h3>📝 拼字检查</h3>");
            writer.write("<input id='word' placeholder='输入单字'>");
            writer.write("<button onclick=\"fetch('https://api.datamuse.com/words?sp='+document.getElementById('word').value+'&max=1').then(r=>r.json()).then(d=>{var r=document.getElementById('result');if(d.length&&d[0].word==document.getElementById('word').value.toLowerCase())r.innerHTML='✅ 正确';else if(d.length)r.innerHTML='❌ 建议：'+d[0].word;else r.innerHTML='❌ 未找到';})\">检查</button>");
            writer.write("<p id='result'></p></div>");
            
            writer.write("</body></html>");
            writer.close();
            System.out.println("✅ news.html 已生成");
        } catch (Exception e) {
            System.out.println("生成失败: " + e.getMessage());
        }
    }
}