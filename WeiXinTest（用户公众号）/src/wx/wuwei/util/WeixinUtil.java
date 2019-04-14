package wx.wuwei.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import wx.wuwei.menu.Button;
import wx.wuwei.menu.ClickButton;
import wx.wuwei.menu.Menu;
import wx.wuwei.menu.ViewButton;
import wx.wuwei.po.AccessToken;

/**
 * ΢�Ź�����
 * @author ZX50j
 *
 */
public class WeixinUtil {
	private static final String APPID = "wx1e83e75abd38e250";
	private static final String APPSECRET = "9455fcaa649f7528618dbbff146ad627";
	
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	
	private static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	
	private static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	
	private static final String QUERY_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	
	private static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	/**
	 * get����
	 * @param url
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static JSONObject doGetStr(String url) throws ParseException, IOException{
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		JSONObject jsonObject = null;
		HttpResponse httpResponse = client.execute(httpGet);
		HttpEntity entity = httpResponse.getEntity();
		if(entity != null){
			String result = EntityUtils.toString(entity,"UTF-8");
			jsonObject = JSONObject.fromObject(result);
		}
		return jsonObject;
	}
	
	/**
	 * POST����
	 * @param url
	 * @param outStr
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static JSONObject doPostStr(String url,String outStr) throws ParseException, IOException{
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(url);
		JSONObject jsonObject = null;
		httpost.setEntity(new StringEntity(outStr,"UTF-8"));
		HttpResponse response = client.execute(httpost);
		String result = EntityUtils.toString(response.getEntity(),"UTF-8");
		jsonObject = JSONObject.fromObject(result);
		return jsonObject;
	}
	
	/**
	 * �ļ��ϴ�
	 * @param filePath
	 * @param accessToken
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws KeyManagementException
	 */
	public static String upload(String filePath, String accessToken,String type) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new IOException("�ļ�������");
		}

		String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE",type);
		
		URL urlObj = new URL(url);
		//����
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

		con.setRequestMethod("POST"); 
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false); 

		//��������ͷ��Ϣ
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");

		//���ñ߽�
		String BOUNDARY = "----------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");

		byte[] head = sb.toString().getBytes("utf-8");

		//��������
		OutputStream out = new DataOutputStream(con.getOutputStream());
		//�����ͷ
		out.write(head);

		//�ļ����Ĳ���
		//���ļ������ļ��ķ�ʽ ���뵽url��
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();

		//��β����
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//����������ݷָ���

		out.write(foot);

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String result = null;
		try {
			//����BufferedReader����������ȡURL����Ӧ
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		JSONObject jsonObj = JSONObject.fromObject(result);
		System.out.println(jsonObj);
		String typeName = "media_id";
		if(!"image".equals(type)){
			typeName = type + "_media_id";
		}
		String mediaId = jsonObj.getString(typeName);
		return mediaId;
	}
	
	/**
	 * ��ȡaccessToken
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static AccessToken getAccessToken() throws ParseException, IOException{
		AccessToken token = new AccessToken();
		String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		JSONObject jsonObject = doGetStr(url);
		if(jsonObject!=null){
			token.setToken(jsonObject.getString("access_token"));
			token.setExpiresIn(jsonObject.getInt("expires_in"));
		}
		return token;//��ȡƾ֤
	}
	
	/**
	 * ��װ�˵�
	 * @return
	 */
	public static Menu initMenu(){
		Menu menu = new Menu();
		ClickButton button11 = new ClickButton();
		button11.setName("�˻���");
		button11.setType("click");
		button11.setKey("11");
		
		ClickButton button12 = new ClickButton();
		button12.setName("������Ϣ");
		button12.setType("click");
		button12.setKey("12");
		
		ViewButton button13 = new ViewButton();
		button13.setName("�������");
		button13.setType("view");
		button13.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+APPID+"&redirect_uri=http%3A%2F%2F123.206.205.38%2FWeiXinTest%2FMyBorrowBook&response_type=code&scope=snsapi_base&state=123#wechat_redirect");	
		
		ViewButton button21 = new ViewButton();
		button21.setName("����");
		button21.setType("view");
		button21.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+APPID+"&redirect_uri=http%3A%2F%2F123.206.205.38%2FWeiXinTest%2FMyOrder&response_type=code&scope=snsapi_base&state=123#wechat_redirect");	
		
		ViewButton button22 = new ViewButton();
		button22.setName("ͼ���ͼ");
		button22.setType("view");
		button22.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+APPID+"&redirect_uri=http%3A%2F%2F123.206.205.38%2FWeiXinTest%2FBaiDuMap&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
		
		ViewButton button23 = new ViewButton();
		button23.setName("ͼ������");
		button23.setType("view");
		button23.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+APPID+"&redirect_uri=http%3A%2F%2F123.206.205.38%2FWeiXinTest%2FLibaryList&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
		
		ClickButton button31 = new ClickButton();//����
		button31.setName("ɨ�����");
		button31.setType("scancode_waitmsg");
		button31.setKey("31");
		
		ClickButton button32 = new ClickButton();
		button32.setName("��������");
		button32.setType("click");
		button32.setKey("32");
		
		//��ɲ˵�
		Button button1 = new Button();//һ���˵��°���5�������˵�
		button1.setName("�û�");
		button1.setSub_button(new Button[]{button11,button12,button13});
		
		Button button2 = new Button();//һ���˵��°���4�������˵�
		button2.setName("ͼ�����");
		button2.setSub_button(new Button[]{button21,button22,button23});
		
		Button button3 = new Button();//һ���˵��°���3�������˵�
		button3.setName("����");
		button3.setSub_button(new Button[]{button31,button32});
		
		menu.setButton(new Button[]{button1,button2,button3});//��������һ���˵�
		return menu;
	}
	/**
	 * ͨ��post��������˵��ӿڵ�ַ  �����˵�
	 * @param token
	 * @param menu
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static int createMenu(String token,String menu) throws ParseException, IOException{
		int result = 0;
		String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doPostStr(url, menu);
		if(jsonObject != null){
			result = jsonObject.getInt("errcode");
		}
		return result;
	}
	/**
	 * ��json��ʽ���أ���ѯ�˵�
	 * @param token
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static JSONObject queryMenu(String token) throws ParseException, IOException{
		String url = QUERY_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		return jsonObject;
	}
	/**
	 * �˵���ɾ��
	 * @param token
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static int deleteMenu(String token) throws ParseException, IOException{
		String url = DELETE_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		int result = 0;
		if(jsonObject != null){
			result = jsonObject.getInt("errcode");//errcode=0ɾ���ɹ�
		}
		return result;
	}

}
