package wiki.zimo.wiseduunifiedloginapi.controller;

/**
 * @author: jing213
 * @date: 2022/5/26 17:08
 * @description:
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import wiki.zimo.wiseduunifiedloginapi.service.LoginService;
import wiki.zimo.wiseduunifiedloginapi.service.TableService;
import wiki.zimo.wiseduunifiedloginapi.service.impl.LoginServiceImpl;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private LoginServiceImpl loginService;
    @Autowired
    private TableService tableService;

    @GetMapping("/notlogin")
    public String slogin(){
        return "/login";
    }
    @PostMapping("/notlogin")
    public String login(String username, String password, Model model, HttpSession session){
        Map<String, String> cookies = new HashMap<>();
        try {
            cookies = loginService.login(username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.setAttribute("cookie",cookies);
        model.addAttribute("cookie",cookies);
        return "/cookie";
    }
    //获取课表
    @GetMapping("/getTable")
    public String table(HttpSession session,Model model){
        String table = tableService.getTable(session);
        model.addAttribute("table",table);
        return "/cookie";
    }
    //疫情上报

}
