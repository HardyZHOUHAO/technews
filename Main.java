import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Web web = new Web();
        
        System.out.println("抓取中，请稍候...\n");
        List<NewsItem> newsList = web.aggregate();
        
        for (int i = 0; i < newsList.size(); i++) {
            NewsItem item = newsList.get(i);
            System.out.println("【" + (i + 1) + "】" + item.getTitle());
            System.out.println("   来源: " + item.getSource() + "\n");
        }
        
        System.out.println("💬 " + web.generateComment());
        
        web.generateHTML(newsList);
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n输入单词 (quit退出): ");
        while (scanner.hasNext()) {
            String word = scanner.nextLine();
            if (word.equalsIgnoreCase("quit")) break;
            System.out.println(web.checkSpelling(word));
            System.out.print("输入单词: ");
        }
        scanner.close();
    }
}