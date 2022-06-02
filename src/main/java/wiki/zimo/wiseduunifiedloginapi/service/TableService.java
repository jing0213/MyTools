package wiki.zimo.wiseduunifiedloginapi.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: jing213
 * @date: 2022/5/27 8:51
 * @description:
 */
@Service
public class TableService {

    @Value("${sjmsValue}")
    private String sjmsValue;
    private final String update_api ="https://authserver-443.webvpn.jxust.edu.cn/authserver/login?service=https%3A%2F%2Fjw-443.webvpn.jxust.edu.cn%2Fjsxsd%2Fframework%2FxsMain.jsp";

    private final String real_table_api = "https://jw-443.webvpn.jxust.edu.cn/jsxsd/framework/main_index_loadkb.jsp";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public String getTable(HttpSession session){

        Map<String,String> params = new HashMap<>();
        params.put("rq", sdf.format(new Date()));
        params.put("sjmsValue",sjmsValue);
        System.out.println(params);
        Map<String,String> cookies = (Map<String, String>) session.getAttribute("cookie");
        Connection.Response update = null;
        Connection.Response update2 = null;
        Document table;
        try {
            update = Jsoup.connect(update_api).ignoreContentType(true).followRedirects(false).method(Connection.Method.GET).cookies(cookies).execute();
            String location = update.header("location");
            System.out.println(location);
            update2 = Jsoup.connect(location).ignoreContentType(true).followRedirects(false).method(Connection.Method.GET).cookies(cookies).execute();
            cookies.putAll(update2.cookies());
            System.out.println(cookies);
            table = Jsoup.connect(real_table_api)
                    .data(params)
                    .followRedirects(false)
                    .cookies(cookies)
                    .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .ignoreContentType(true)
                    .post();
            table = Jsoup.connect(real_table_api)
                    .data(params)
                    .followRedirects(false)
                    .cookies(cookies)
                    .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1")
                    .ignoreContentType(true)
                    .post();
            System.out.println(cookies);
            String html = table.html();
            return html;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
