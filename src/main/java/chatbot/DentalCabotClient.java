package chatbot;

import chatbot.message.Message;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @author dzhang
 */
public class DentalCabotClient {

    private HttpClient httpclient = HttpClients.createDefault();

    private static String webHookPrefix = "https://oapi.dingtalk.com/robot/send?access_token=";

    private SendResult push(String webHook, Message message) throws IOException {
        HttpPost httppost = new HttpPost(webHook);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        StringEntity se = new StringEntity(message.toJsonString(), "utf-8");
        httppost.setEntity(se);

        SendResult sendResult = new SendResult();
        HttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity());
            JSONObject obj = JSONObject.parseObject(result);

            Integer errCode = obj.getInteger("errcode");
            sendResult.setErrorCode(errCode);
            sendResult.setErrorMsg(obj.getString("errmsg"));
            sendResult.setIsSuccess(errCode.equals(0));
        }
        return sendResult;
    }

    public void send(String tokens, Message message) throws IOException {
        for (String token : parseToken(tokens)) {
            this.push(webHookPrefix + token, message);
        }
    }

    private String[] parseToken(String tokens) {
        if (StringUtils.isNotEmpty(tokens)) {
            return tokens.split("\\|");
        }
        return new String[]{};
    }

}

