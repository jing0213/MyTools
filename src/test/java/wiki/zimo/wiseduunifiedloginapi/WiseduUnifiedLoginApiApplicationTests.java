package wiki.zimo.wiseduunifiedloginapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import wiki.zimo.wiseduunifiedloginapi.entity.CpdailyDefaults;
import wiki.zimo.wiseduunifiedloginapi.helper.TodayEncryptHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
class WiseduUnifiedLoginApiApplicationTests {

    @Test
    void contextLoads() {
        List<CpdailyDefaults.Default> defaults = CpdailyDefaults.getDefaults();
        System.out.println(defaults);

    }

}
