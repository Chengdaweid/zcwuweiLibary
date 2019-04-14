package wx.wuweiadmin.util;

import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import wx.wuweiadmin.menu.Button;
import wx.wuweiadmin.menu.ClickButton;
import wx.wuweiadmin.menu.Menu;
import wx.wuweiadmin.po.AccessToken;

/**
 * ΢�Ź�����
 * @author ZX50j
 *
 */
public class WeixinUtil {
	private static final String APPID = "wx96b86ea8fdffd49f";
	private static final String APPSECRET = "d4490f914eb26e47addbca42c8c2bc92";
	
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	
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
		
		ClickButton button21 = new ClickButton();//����
		button21.setName("����ɨ��");
		button21.setType("scancode_waitmsg");
		button21.setKey("21");
		
		/*ClickButton button22 = new ClickButton();//����
		button22.setName("ԤԼɨ��");
		button22.setType("scancode_waitmsg");
		button22.setKey("22");*/
		
		ClickButton button31 = new ClickButton();//����
		button31.setName("����ɨ��");
		button31.setType("scancode_waitmsg");
		button31.setKey("31");
		
		//��ɲ˵�
		Button button1 = new Button();//һ���˵��°���5�������˵�
		button1.setName("�û�");
		button1.setSub_button(new Button[]{button11});
		
		Button button2 = new Button();//һ���˵��°���4�������˵�
		button2.setName("����");
		button2.setSub_button(new Button[]{button21});
		
		Button button3 = new Button();//һ���˵��°���3�������˵�
		button3.setName("����");
		button3.setSub_button(new Button[]{button31});
		
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
