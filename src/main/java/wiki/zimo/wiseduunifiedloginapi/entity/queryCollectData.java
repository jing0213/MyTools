package wiki.zimo.wiseduunifiedloginapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: jing213
 * @date: 2022/6/2 15:10
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class queryCollectData implements Serializable {
    private String formWid;
    private String isHandled;
    private String priority;
    private String isRead;
    private String wid;
    private String currentTime;
    private String createTime;
    private String subject;
    private String endTime;
    private String senderUserName;
    private String content;
    private String instanceWid;
}
