import java.io.*;
import java.net.*;
import java.util.*;
import com.sun.net.httpserver.*;

public class Server {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                Web web = new Web();
                List<NewsItem> newsList = web.aggregate();
                
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
                html.append("<title>TechNews</title>");
                html.append("<link rel='icon' href='data:,'>"); // 防止二次请求
                html.append("<style>");
                html.append("*{margin:0;padding:0;box-sizing:border-box;}");
                html.append("body{font-family:Arial;background:#f5f5f5;max-width:900px;margin:0 auto;padding:20px;}");
                html.append(".header{text-align:center;padding:30px 0;border-bottom:3px solid #2196F3;margin-bottom:20px;}");
                html.append(".header h1{font-size:2em;color:#1565C0;}");
                html.append(".comment-box{background:#E3F2FD;padding:15px;border-radius:8px;margin-bottom:20px;}");
                html.append(".sound-link{margin-bottom:20px;}");
                html.append(".sound-link a{color:#2196F3;text-decoration:none;}");
                html.append(".news-card{background:#fff;border-radius:10px;display:flex;margin-bottom:20px;box-shadow:0 2px 8px rgba(0,0,0,0.1);overflow:hidden;}");
                html.append(".img-container{width:200px;min-width:200px;}");
                html.append(".news-img{width:100%;height:150px;object-fit:cover;transition:transform 0.3s;cursor:pointer;}");
                html.append(".news-img:hover{transform:scale(1.2);}");
                html.append(".news-img.zoomed{position:fixed;top:50%;left:50%;transform:translate(-50%,-50%) scale(2.5);z-index:999;}");
                html.append(".news-content{padding:15px;}");
                html.append(".news-content h3 a{color:#1565C0;text-decoration:none;}");
                html.append(".summary{color:#666;margin:8px 0;}");
                html.append(".source{color:#999;font-size:0.85em;}");
                html.append(".spell-box{margin-top:40px;padding:20px;background:#fff;border-radius:10px;text-align:center;}");
                html.append(".spell-box input{padding:10px;width:250px;font-size:1em;}");
                html.append(".spell-box button{padding:10px 20px;background:#2196F3;color:#fff;border:none;border-radius:5px;cursor:pointer;margin-left:10px;}");
                html.append("@media(max-width:600px){.news-card{flex-direction:column;}.img-container{width:100%;}}");
                html.append("</style></head><body>");
                
                html.append("<div class='header'><h1>📰 TechNews</h1>");
                html.append("<p>Guardian · CNN · BBC News</p></div>");
                html.append("<div class='comment-box'><p>💬 ").append(web.generateComment()).append("</p></div>");
                html.append("<div class='sound-link'><a href='#' onclick=\"new Audio('https://www.soundjay.com/buttons/sounds/button-09.mp3').play();return false;\">🔊 点击播放提示音</a></div>");
                
                for (NewsItem item : newsList) {
                    html.append("<div class='news-card'>");
                    if (!item.getImageUrl().isEmpty()) {
                        html.append("<div class='img-container'><img src='").append(item.getImageUrl()).append("' class='news-img' onclick='this.classList.toggle(\"zoomed\")'></div>");
                    } else {
                        html.append("<div class='img-container' style='background:#e0e0e0;display:flex;align-items:center;justify-content:center;height:150px;'><span style='color:#999;'>暂无图片</span></div>");
                    }
                    html.append("<div class='news-content'>");
                    html.append("<h3><a href='").append(item.getLink()).append("' target='_blank'>").append(item.getTitle()).append("</a></h3>");
                    html.append("<p class='summary'>").append(item.getShortSummary()).append("</p>");
                    html.append("<span class='source'>📍 ").append(item.getSource()).append("</span>");
                    html.append("</div></div>");
                }
                
                html.append("<div class='spell-box'><h3>📝 拼字检查</h3>");
                html.append("<input id='word' placeholder='输入单字'>");
                html.append("<button onclick=\"fetch('https://api.datamuse.com/words?sp='+document.getElementById('word').value+'&max=1').then(r=>r.json()).then(d=>{var r=document.getElementById('result');if(d.length&&d[0].word==document.getElementById('word').value.toLowerCase())r.innerHTML='✅ 正确';else if(d.length)r.innerHTML='❌ 建议：'+d[0].word;else r.innerHTML='❌ 未找到';})\">检查</button>");
                html.append("<p id='result'></p></div>");
                
                html.append("</body></html>");
                
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, html.toString().getBytes("UTF-8").length);
                OutputStream os = exchange.getResponseBody();
                os.write(html.toString().getBytes("UTF-8"));
                os.close();
            }
        });
        
        server.start();
        System.out.println("服务器已启动: http://localhost:8080");
    }
}