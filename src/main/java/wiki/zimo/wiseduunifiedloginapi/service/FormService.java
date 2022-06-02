package wiki.zimo.wiseduunifiedloginapi.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import wiki.zimo.wiseduunifiedloginapi.entity.CpdailyDefaults;
import wiki.zimo.wiseduunifiedloginapi.entity.FormDatas;
import wiki.zimo.wiseduunifiedloginapi.entity.queryCollectData;
import wiki.zimo.wiseduunifiedloginapi.helper.TodayEncryptHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

/**
 * @author: jing213
 * @date: 2022/5/28 11:23
 * @description:
 */
@Service
public class FormService {
    //查询最新表单的api
    private static final String update_api = "https://jxust.campusphere.net/wec-counselor-collector-apps/stu/collector/queryCollectorProcessingList";
    //获取schooltaskwid的api
    private static final String detail_api = "https://jxust.campusphere.net/wec-counselor-collector-apps/stu/collector/detailCollector";
    //获取表单的api
    private static final String form_api = "https://jxust.campusphere.net/wec-counselor-collector-apps/stu/collector/getFormFields";
    //提交表单的api
    private static final String submit_api = "https://jxust.campusphere.net/wec-counselor-collector-apps/stu/collector/submitForm";
    private queryCollectData rowsData = null;
    private FormDatas formDatas = null;
    private String address = "江西省南昌市青山湖区青扬路";
    private List<CpdailyDefaults.Default> defaults = CpdailyDefaults.getDefaults();


    //查询是否有未提交表单：有，顺便把value赋给一个对象
    public boolean hasForm(HttpClient Client){
        HttpPost httpPost = new HttpPost(update_api);
        httpPost.setHeader("Accept","application/json, text/plain, */*");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 (4388145152)cpdaily/9.0.12  wisedu/9.0.12");
        httpPost.setHeader("content-type","application/json");
        httpPost.setHeader("Accept-Encoding","gzip,deflate");
        httpPost.setHeader("Accept-Language","zh-CN,en-US;q=0.8");
        httpPost.setHeader("Content-Type","application/json;charset=UTF-8");
        HttpClient httpClient = Client;
        JSONObject params = new JSONObject();
        params.put("pageSize",6);
        params.put("pageNumber",1);
        //接受响应的json数据
        JSONObject response = null;

        try {
            StringEntity stringEntity = new StringEntity(params.toString());
            stringEntity.setContentEncoding("UTF-8");
            //发送json数据需要设置contentTyp
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            HttpResponse res = httpClient.execute(httpPost);
            // 返回json格式：
            String result = EntityUtils.toString(res.getEntity());
            response = JSONObject.parseObject(result);

            JSONObject datas = response.getJSONObject("datas");


            JSONArray rows = datas.getJSONArray("rows");
            if(rows.size()>=1){
                rowsData = rows.getObject(0, queryCollectData.class);
            }

            if (rows.size() < 1) {
                System.out.println("当前暂无问卷提交任务");
                return false;
            } else if (rowsData.getIsHandled() .equals("1") ) {
                System.out.println("今日已提交");
                return false;
            } else {
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    //获取表单
    public void detailCollector(HttpClient Client){
        HttpPost httpPost = new HttpPost(detail_api);
        httpPost.setHeader("Accept","application/json, text/plain, */*");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 (4388145152)cpdaily/9.0.12  wisedu/9.0.12");
        httpPost.setHeader("content-type","application/json");
        httpPost.setHeader("Accept-Encoding","gzip,deflate");
        httpPost.setHeader("Accept-Language","zh-CN,en-US;q=0.8");
        httpPost.setHeader("Content-Type","application/json;charset=UTF-8");

        JSONObject datas = new JSONObject();
        datas.put("collectorWid",rowsData.getWid());
        datas.put("instanceWid",rowsData.getInstanceWid());

        StringEntity stringEntity = null;
        try {

            stringEntity = new StringEntity(datas.toString());
            stringEntity.setContentEncoding("UTF-8");
            //发送json数据需要设置contentTyp
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);

            HttpResponse response = Client.execute(httpPost);
            JSONObject formData = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));

            JSONObject collectorDatas = formData.getJSONObject("datas").getJSONObject("collector");

            String schoolTaskWid = collectorDatas.getString("schoolTaskWid");

            FormDatas formDatas1 = new FormDatas();
            formDatas1.setAddress(address);
            formDatas1.setSchoolTaskWid(schoolTaskWid);
            formDatas = formDatas1;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取待提交表单数据并处理未提交表单
    public JSONArray getFormFields(HttpClient httpClient){

        JSONArray jsonArray = null;

        HttpPost httpPost = new HttpPost(form_api);
        httpPost.setHeader("Accept","application/json, text/plain, */*");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 (4388145152)cpdaily/9.0.12  wisedu/9.0.12");
        httpPost.setHeader("content-type","application/json");
        httpPost.setHeader("Accept-Encoding","gzip,deflate");
        httpPost.setHeader("Accept-Language","zh-CN,en-US;q=0.8");
        httpPost.setHeader("Content-Type","application/json;charset=UTF-8");

        JSONObject params = new JSONObject();
        params.put("pageSize","9999");
        params.put("pageNumber","1");
        params.put("formWid",rowsData.getFormWid());
        params.put("collectorWid",rowsData.getWid());



        try {

            StringEntity stringEntity = new StringEntity(params.toString());
            stringEntity.setContentType("application/json");
            stringEntity.setContentEncoding("UTF-8");
            httpPost.setEntity(stringEntity);

            HttpResponse response = httpClient.execute(httpPost);

            String res = EntityUtils.toString(response.getEntity());

            JSONObject formList = JSONObject.parseObject(res);

            //表单数据
            jsonArray = formList.getJSONObject("datas").getJSONArray("rows");

            //处理表单数据
            for (int i = 0; i < jsonArray.size(); i++) {

                JSONObject obj = jsonArray.getJSONObject(i);
                //过滤fieldItems，既选择题目
                JSONArray fieldItems = obj.getJSONArray("fieldItems");
                CpdailyDefaults.Default udefault = defaults.get(i);
                //代表是文本呢
                if(obj.getString("fieldType").equals("5") || obj.getString("fieldType").equals("1")){
                    obj.put("value",udefault.getValue());
                }
                //代表是单选框
                if(obj.getString("fieldType").equals("2")){
                    obj.put("value",udefault.getValue());
                    for (int j = 0; j < fieldItems.size(); j++) {
                        if (!fieldItems.getJSONObject(j).getString("content").equals(udefault.getValue())) {
                            //todo 移除可能出现错误
                            jsonArray.getJSONObject(i).getJSONArray("fieldItems").remove(j);
                        }
                    }
                }
                //过滤多选题
                if(obj.getString("fieldType").equals("3")){
                    String defaultvalue = udefault.getValue();
                    for (int j = 0; j < fieldItems.size(); j++) {
                        if (!fieldItems.getJSONObject(j).getString("content").equals(udefault.getValue())) {
                            //todo 移除可能出现错误
                            jsonArray.getJSONObject(i).getJSONArray("fieldItems").remove(j);
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    /**
     * 这里要发送邮箱
     * @param httpClient
     */
    public void  submitForm(HttpClient httpClient) throws Exception {
        System.out.println("submitForm");
        boolean flag = this.hasForm(httpClient);
        if (flag == false) {
            return;
        }
        this.detailCollector(httpClient);
        JSONObject forms = new JSONObject();
        JSONArray form = this.getFormFields(httpClient);
        forms.put("form",form);
        forms.put("formWid",rowsData.getFormWid());
        forms.put("address",address);
        forms.put("collectWid",rowsData.getWid());
        forms.put("schoolTaskWid",formDatas.getSchoolTaskWid());
        forms.put("uaIsCpadaily",true);
        forms.put("latitude","28.74551790");
        forms.put("longitude","115.88237831");
        forms.put("instanceWid",rowsData.getInstanceWid());

        String bodyString = TodayEncryptHelper.encrypt_BodyString(forms.toJSONString());
        JSONObject submitData = new JSONObject();
        submitData.put("lon","115.88237831");
        submitData.put("version","first_v3");
        submitData.put("calVersion","firstv");
        submitData.put("deviceId","E6E8CCD7-9F0D-445D-9C2D-35162A57327F");
        submitData.put("userId","5720191704");
        submitData.put("systemName","iOS");
        submitData.put("bodyString",bodyString);
        submitData.put("lat","28.74551790");
        submitData.put("systemVersion","12.2");
        submitData.put("appVersion","9.0.20");
        submitData.put("model","iPad mini 2");
        submitData.put("sign",TodayEncryptHelper.signAbstract(submitData.toJavaObject(HashMap.class)));
        HttpPost httpPost = new HttpPost(submit_api);
        httpPost.setHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 12_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 (4388145152)cpdaily/9.0.12  wisedu/9.0.12");
        httpPost.setHeader("Content-Type","application/json; charset=UTF-8");
        httpPost.setHeader("Accept-Encoding","gzip");
        httpPost.setHeader("Cpdaily-Extension", "Ew9uONYq03Siz+VLCzZ4RiWRaXXBubIGc1d7ecaS2YmSDf1+elDL0gdwAw977HbPzvgR3pkeyW3djmnPOMxYro3Tps7PNmLoqfNTAECZqcM1LAyx+2zTfDExNa4yDWs83AyTnSKXs7oHQvFOfXhKNY1OXVzIdnwOkgaNw7XxzM1+2efCWAJgUBoHNV3n3MayLqOwPvSCvBke+SHC/Hy/53+ehU9A1lst6JlpGiFhlEOUybo5s5/o+b/XLUexuEE50IQgdPL4Hi4vPe4yVzA8QLpIMKSFIaRm");
        httpPost.setHeader("Connection","Keep-Alive");


        StringEntity stringEntity = null;



        try {

            stringEntity = new StringEntity(submitData.toJSONString(),"UTF-8");
            stringEntity.setContentType("application/json");
            stringEntity.setContentEncoding("UTF-8");


            httpPost.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(httpPost);
            String jsonBody = EntityUtils.toString(response.getEntity());
            JSONObject message = JSONObject.parseObject(jsonBody);
            String status = message.getString("message");
            if("SUCCESS".equals(status)){
//                SimpleMailMessage mailMessage = new SimpleMailMessage();
//                mailMessage.setSubject("今日校园提交成功");
//                mailMessage.setText("今日提交成功！24小时后，脚本将再次自动提交");
//                mailMessage.setTo();
//                mailMessage.setFrom("wdy668@vip.qq.com");
//                mailSender.send(mailMessage);
//                System.out.println("今日提交成功！24小时后，脚本将再次自动提交");
            } else if ("该收集已填写无需再次填写".equals(status)) {
                System.out.println("该收集已填写无需再次填写");
            } else {
//                SimpleMailMessage mailMessage = new SimpleMailMessage();
//                mailMessage.setSubject("今日校园提交出错，请手动提交！！！");
//                mailMessage.setText("今日校园提交出错，请手动提交！！！");
//                mailMessage.setTo(user.getEmailAdress());
//                mailMessage.setFrom("wdy668@vip.qq.com");
//                mailSender.send(mailMessage);
//                System.out.println("提交出错，请手动提交！！！");
            }


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
}
