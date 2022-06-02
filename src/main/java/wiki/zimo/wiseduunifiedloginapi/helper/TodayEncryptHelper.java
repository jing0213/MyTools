package wiki.zimo.wiseduunifiedloginapi.helper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static javax.xml.crypto.dsig.DigestMethod.SHA1;

/**
 * @author: jing213
 * @date: 2022/6/2 13:34
 * @description:
 */
public class TodayEncryptHelper {

    public static String signAbstract(Map<String,String> dict){
        String key = "SASEoK4Pa5d4SssO";
        List<String> abstractKey = Arrays.asList("appVersion", "bodyString", "deviceId", "lat",
                "lon", "model", "systemName", "systemVersion", "userId");
        HashMap<String,String> abstractSubmitData = new HashMap<>();
        for(String k:dict.keySet()){
            if(abstractKey.contains(k))
                abstractSubmitData.put(k, dict.get(k));
        }
        String abstracts = asUrlParams(abstractSubmitData)+"&"+key;
        return DigestUtils.md5Hex(abstracts);
    }
    public static String pkcs7padding(String text){
        int remainder = 16 - text.length() % 16;
        String tmp = String.valueOf((char)remainder);
        for (int i = 1; i < remainder; i++) {
            tmp+=tmp.charAt(0);
        }
        return text+tmp;
    }
    public static String encrypt_BodyString(String text) throws Exception {
        String key = "SASEoK4Pa5d4SssO";
        String randomIv = AESHelper.randomString(16);
        text = pkcs7padding(text);
        text = AESHelper.Base64Encrypt(AESHelper.AESEncrypt(text, key, randomIv));
        return new String(AESHelper.Base64Decrypt(text));
    }
    public static String encrypt_CpdailyExtension(String text) throws UnsupportedEncodingException {
        String key = "XCE927==";
        text = encrypt(key, text);
        //这里的解码是什么鬼啊
        return new String(AESHelper.Base64Decrypt(text));
    }
    public static String asUrlParams(Map<String, String> source){
        Iterator<String> it = source.keySet().iterator();
        StringBuilder paramStr =new StringBuilder();
        while(it.hasNext()){
            String key = it.next();
            String value = source.get(key);
            if(StringUtils.isBlank(value)){
                continue;
            }
            try{
                value = URLEncoder.encode(value,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            paramStr.append("&").append(key).append("=").append(value);
        }
        return paramStr.substring(1);
    }
    /**
     * 偏移变量，固定占8位字节
     */
    private final static String IV_PARAMETER = "12345678";
    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "DES";
    /**
     * 加密/解密算法-工作模式-填充模式
     */
    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
    /**
     * 默认编码
     */
    private static final String CHARSET = "utf-8";

    /**
     * 生成key
     *
     * @param password
     * @return
     * @throws Exception
     */
    private static Key generateKey(String password) throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes(CHARSET));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }

    /**
     * DES加密字符串
     *
     * @param password 加密密码，长度不能够小于8位
     * @param data 待加密字符串
     * @return 加密后内容
     */
    public static String encrypt(String password, String data) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));

            //JDK1.8及以上可直接使用Base64，JDK1.7及以下可以使用BASE64Encoder
            //Android平台可以使用android.util.Base64
            return new String(Base64.getEncoder().encode(bytes));

        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }
}
