package wx.wuweiadmin.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import wx.wuweiadmin.sqlhelper.SqlHelper;

/**
 * ��ʾ����Ա��Ϣ���ж��Ƿ��ж�
 * @author ZX50j
 *
 */
public class ShowAdmininfoService {
	/**
	 * �ж��Ƿ����û���Ϣ�����Ƿ��
	 * @param fromUserName
	 * @return
	 * @throws SQLException 
	 */
	public static boolean  judgeUserInfo(String fromUserName) throws SQLException
	{
		String SQL = "select count(*)from administrator where adminId='"+fromUserName+"'";//��ѯ����Ա�Ƿ����
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
