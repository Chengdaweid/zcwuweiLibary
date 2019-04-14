package wx.wuwei.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import wx.wuwei.po.Token;
import wx.wuwei.po.WeixinUserInfo;
import wx.wuwei.util.CommonUtil;
import wx.wuwei.util.MyX509TrustManager;

/**
 * ͨ�ù�����
 * @author ZX50j
 *
 */
public class CommonUtil {

    // ƾ֤��ȡ��GET��
    public final static String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    /**
     * ����https����
     * 
     * @param requestUrl �����ַ
     * @param requestMethod ����ʽ��GET��POST��
     * @param outputStr �ύ������
     * @return JSONObject(ͨ��JSONObject.get(key)�ķ�ʽ��ȡjson���������ֵ)
     */
    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        JSONObject jsonObject = null;
        try {
            // ����SSLContext���󣬲�ʹ������ָ�������ι�������ʼ��
            TrustManager[] tm = { new MyX509TrustManager() };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // ������SSLContext�����еõ�SSLSocketFactory����
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // ��������ʽ��GET/POST��
            conn.setRequestMethod(requestMethod);

            // ��outputStr��Ϊnullʱ�������д����
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // ע������ʽ
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            // ����������ȡ��������
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }

            // �ͷ���Դ
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            jsonObject = JSONObject.fromObject(buffer.toString());
        } catch (ConnectException ce) {
            
        } catch (Exception e) {
            
        }
        return jsonObject;
    }

    /**
     * ��ȡ�ӿڷ���ƾ֤
     * 
     * @param appid ƾ֤
     * @param appsecret ��Կ
     * @return
     */
    public static Token getToken(String appid, String appsecret) {
        Token token = null;
        String requestUrl = token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
        // ����GET�����ȡƾ֤
        JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                token = new Token();
                token.setAccessToken(jsonObject.getString("access_token"));
                token.setExpiresIn(jsonObject.getInt("expires_in"));
            } catch (JSONException e) {
                token = null;
                // ��ȡtokenʧ��
               
            }
        }
        return token;
    }
    
    /**
     * URL���루utf-8��
     * 
     * @param source
     * @return
     */
    public static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * �������������ж��ļ���չ��
     * 
     * @param contentType ��������
     * @return
     */
    public static String getFileExt(String contentType) {
        String fileExt = "";
        if ("image/jpeg".equals(contentType))
            fileExt = ".jpg";
        else if ("audio/mpeg".equals(contentType))
            fileExt = ".mp3";
        else if ("audio/amr".equals(contentType))
            fileExt = ".amr";
        else if ("video/mp4".equals(contentType))
            fileExt = ".mp4";
        else if ("video/mpeg4".equals(contentType))
            fileExt = ".mp4";
        return fileExt;
    }
    
    /**
     * ��ȡ�û���Ϣ
     * 
     * @param accessToken �ӿڷ���ƾ֤
     * @param openId �û���ʶ
     * @return WeixinUserInfo
     */
    public static WeixinUserInfo getUserInfo(String accessToken, String openId) {
        WeixinUserInfo weixinUserInfo = null;
        // ƴ�������ַ
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
        // ��ȡ�û���Ϣ
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                weixinUserInfo = new WeixinUserInfo();
                // �û��ı�ʶ
                weixinUserInfo.setOpenId(jsonObject.getString("openid"));
                // ��ע״̬��1�ǹ�ע��0��δ��ע����δ��עʱ��ȡ����������Ϣ
                weixinUserInfo.setSubscribe(jsonObject.getInt("subscribe"));
                // �û���עʱ��
                weixinUserInfo.setSubscribeTime(jsonObject.getString("subscribe_time"));
                // �ǳ�
                weixinUserInfo.setNickname(jsonObject.getString("nickname"));
                // �û����Ա�1�����ԣ�2��Ů�ԣ�0��δ֪��
                weixinUserInfo.setSex(jsonObject.getInt("sex"));
                // �û����ڹ���
                weixinUserInfo.setCountry(jsonObject.getString("country"));
                // �û�����ʡ��
                weixinUserInfo.setProvince(jsonObject.getString("province"));
                // �û����ڳ���
                weixinUserInfo.setCity(jsonObject.getString("city"));
                // �û������ԣ���������Ϊzh_CN
                weixinUserInfo.setLanguage(jsonObject.getString("language"));
                // �û�ͷ��
                weixinUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
            } catch (Exception e) {
                if (0 == weixinUserInfo.getSubscribe()) {
                   //�û���ȡ����ע
                } else {
                    @SuppressWarnings("unused")
					int errorCode = jsonObject.getInt("errcode");
                    @SuppressWarnings("unused")
					String errorMsg = jsonObject.getString("errmsg");
                   //��ȡ�û���Ϣʧ��
                }
            }
        }
        return weixinUserInfo;
    }
    /**
     * ���Ի�ȡ�û���Ϣ
     * @param args
     */
    public static void main(String args[]) {
        // ��ȡ�ӿڷ���ƾ֤
        String accessToken = CommonUtil.getToken("wx1e83e75abd38e250", "9455fcaa649f7528618dbbff146ad627").getAccessToken();
        /**
         * ��ȡ�û���Ϣ
         */
        WeixinUserInfo user = getUserInfo(accessToken, "omwjQ0aZv3eWZVd6QV80zSz9rnvw");//������Ϣ
        System.out.println("OpenID��" + user.getOpenId());
        System.out.println("��ע״̬��" + user.getSubscribe());
        System.out.println("��עʱ�䣺" + user.getSubscribeTime());
        System.out.println("�ǳƣ�" + user.getNickname());
        System.out.println("�Ա�" + user.getSex());
        System.out.println("���ң�" + user.getCountry());
        System.out.println("ʡ�ݣ�" + user.getProvince());
        System.out.println("���У�" + user.getCity());
        System.out.println("���ԣ�" + user.getLanguage());
        System.out.println("ͷ��" + user.getHeadImgUrl());
    }
}
