package wx.wuwei.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import wx.wuwei.po.Reader;
import wx.wuwei.sqlhelper.SqlHelper;
/**
 * ��ʾ�û���Ϣ����
 * @author ZX50j
 *
 */
public class ShowUserinfoService {
	/**
	 * �õ��û������ݿ��е���Ϣ
	 * @param fromUserName
	 * @return
	 */
	public static Reader getUserInfo(String fromUserName)
	{
		String SQL = "select *from reader where readerId='"+fromUserName+"'";//��ѯ�û�������Ϣ
		ResultSet res=SqlHelper.executeQuery(SQL);
		Reader reader= new Reader();
		try
		{
			while(res.next())
			{
				String readerId = res.getString("readerId");
				String name = res.getString("name");//��ȡname�е�Ԫ��
				String sex = res.getString("sex");
				String brithday = res.getString("brithday");
				String education = res.getString("education");
				String hobby = res.getString("hobby");
				
				reader.setReaderId(readerId);
				reader.setName(name);
				reader.setSex(sex);
				reader.setBrithday(brithday);
				reader.setEducation(education);
				reader.setHobby(hobby);				
				
			}	
			return reader;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * �ж��Ƿ����û���Ϣ�����Ƿ��
	 * @param fromUserName
	 * @return
	 * @throws SQLException 
	 */
	public static boolean  judgeUserInfo(String fromUserName) throws SQLException
	{
		String SQL = "select count(*)from reader where readerId='"+fromUserName+"'";//��ѯ�û�������Ϣ
		ResultSet res=SqlHelper.executeQuery(SQL);
		int result=0;
		if(res.next())
		{
			result = res.getInt(1);
		}
		if(result ==1)
		{
			return true;
		}
		
		return false;
	}
}
