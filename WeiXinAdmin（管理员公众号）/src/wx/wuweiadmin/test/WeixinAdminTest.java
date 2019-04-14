package wx.wuweiadmin.test;

import net.sf.json.JSONObject;
import wx.wuweiadmin.po.AccessToken;
import wx.wuweiadmin.util.WeixinUtil;

public class WeixinAdminTest {
	public static void main(String[] args) {
		try 
		{
			AccessToken token = WeixinUtil.getAccessToken();
			System.out.println("Ʊ��"+token.getToken());
			System.out.println("��Чʱ��"+token.getExpiresIn());
			
			//�����˵�
			String menu=JSONObject.fromObject(WeixinUtil.initMenu()).toString();
			int res=WeixinUtil.createMenu(token.getToken(),menu);
			if(res==0)
			{
				System.out.println("�����˵��ɹ�");
			}
			else
			{
				System.out.println("�����룺"+res);
			}
			
			//��ӡ�˵�
			JSONObject jsonObject = WeixinUtil.queryMenu(token.getToken());
			System.out.println(jsonObject);//��ӡ�˵������ݽṹ��ʽ
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
