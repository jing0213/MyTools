package wiki.zimo.wiseduunifiedloginapi.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: jing213
 * @date: 2022/6/2 15:34
 * @description:
 */
@Configuration
@ConfigurationProperties(prefix = "cpdaily")
public class CpdailyDefaults {
    private static List<Default> defaults = new ArrayList<>();

    public static List<Default> getDefaults() {
        return defaults;
    }

    public  void setDefaults(List<Default> defaults) {
        this.defaults = defaults;
    }
    @Data
    @ToString
    public static class Default{
        private String type;
        private String title;
        private String value;
    }
}
