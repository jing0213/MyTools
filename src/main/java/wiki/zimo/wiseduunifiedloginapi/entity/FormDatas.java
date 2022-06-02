package wiki.zimo.wiseduunifiedloginapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: jing213
 * @date: 2022/6/2 15:25
 * @description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FormDatas implements Serializable {
    private String address;
    private String schoolTaskWid;
}
