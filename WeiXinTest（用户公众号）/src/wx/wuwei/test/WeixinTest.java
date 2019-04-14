package wx.wuwei.test;

import net.sf.json.JSONObject;

import wx.wuwei.po.AccessToken;
import wx.wuwei.po.Reader;
import wx.wuwei.service.ShowUserinfoService;
import wx.wuwei.util.CommonUtil;
import wx.wuwei.util.WeixinUtil;

public class WeixinTest {
	public static void main(String[] args) {
		try {
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
			
			
			String url=CommonUtil.urlEncodeUTF8("&");
			System.out.println(url);
			
			/*int r2=WeixinUtil.deleteMenu(token.getToken());
			if(r2==0)
			{
				System.out.println("�˵�ɾ���ɹ�");
			}
			else
			{
				System.out.println("�����룺"+r2);
			}*/
			
			Reader reader=ShowUserinfoService.getUserInfo("omwjQ0aZv3eWZVd6QV80zSz9rnvw");
			if(reader!= null && !reader.equals(""))
			{
				String userid=reader.getReaderId();
				System.out.println(userid);
				
			}
			else
			{
				System.out.println("kong");
			}
			/*
			String ticket=QRCodeService.createPermanentORCode(token.getToken(), "123");
			System.out.println("ticket"+ticket);
			
			String showUrl=QRCodeService.showQRcode(ticket);
			System.out.println("��ά���ַ"+showUrl);
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
