public class NewsItem {
    private String title;
    private String summary;
    private String link;
    private String imageUrl;
    private String source;
    
    public NewsItem(String title, String summary, String link, 
                    String imageUrl, String source) {
        this.title = title;
        this.summary = summary;
        this.link = link;
        this.imageUrl = imageUrl;
        this.source = source;
    }
    
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public String getLink() { return link; }
    public String getImageUrl() { return imageUrl; }
    public String getSource() { return source; }
    
    public String getShortSummary() {
        if (summary == null || summary.isEmpty()) return "暂无摘要";
        if (summary.length() > 100) return summary.substring(0, 97) + "...";
        return summary;
    }
}