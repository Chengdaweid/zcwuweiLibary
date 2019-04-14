package wx.wuweiadmin.service;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wx.wuweiadmin.sqlhelper.SqlHelper;
import wx.wuweiadmin.util.MessageUtil;

public class WeixinAdminService {
	/**
	 * ����΢�ŷ���������
	 * @param request
	 * @return
	 */
	public static String processRequest (HttpServletRequest request) {
		
		// xml��ʽ����Ϣ����
		String message = null;
		
		try 
        {
            Map<String,Object> map = MessageUtil.xmlToMap(request);
            String toUserName = map.get("ToUserName").toString();
            String fromUserName = map.get("FromUserName").toString();
            String msgType = map.get("MsgType").toString();
            String content=null;
            if(map.get("Content")!=null)
            {
            	content = map.get("Content").toString();   
            }
            

            if(MessageUtil.MESSAGE_TEXT.equals(msgType))//�ı���Ϣ
            {    	
            	String mycontent = "��ظ�����Ϣ�ǣ� " + content;
                message = MessageUtil.initText(toUserName, fromUserName, mycontent);
            }
            
            else if(MessageUtil.MESSAGE_EVENT.equals(msgType))//�¼�
            {
            	String eventType = map.get("Event").toString();//�����¼�����
            	
                if(MessageUtil.EVENT_SUB.equals(eventType))//��ע  ��ʾ�û���
                {	
                	message=MessageUtil.AdminSUB(toUserName, fromUserName);
                }
                else if(MessageUtil.EVENT_UNSUB.equals(eventType))//ȡ����ע  ����û���
                {
                	//MessageUtil.AdminUNSUB(fromUserName);
                }
                else if(MessageUtil.EVENT_CLICK.equals(eventType))//������¼� �˵�
                {
                  	String eventKey = map.get("EventKey").toString();//��ȡ����¼�keyֵ
                  	if (eventKey.equals("11")) //�˺Ű�    11�˵������˺Ű�
                    {  
                  		message=MessageUtil.BindAdmin(toUserName, fromUserName);
                    }
                }
                
                else if(ShowAdmininfoService.judgeUserInfo(fromUserName))//�ж��û��Ƿ��(�Ѱ�)
                {
                	if(MessageUtil.MESSAGE_SCANCODE.equals(eventType))//ɨ����¼�
                    {
                    	System.out.println("��ά��mapΪ:"+map);
                        	
                    	String eventKey = map.get("EventKey").toString();//�õ���ť��keyֵ
                    	System.out.println("��ά��˵�keyֵΪ��"+eventKey);
        					
                    	if(eventKey.equals("21"))//����
                    	{
                    		String ScanCodeInfo= map.get("ScanCodeInfo").toString();
                    		String QRcodeInfo=ScanCodeInfo.substring(29,ScanCodeInfo.length()-1);
                    		System.out.println("�����ά������Ϊ��"+QRcodeInfo);
                    		message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.AdminBorrowMessage(QRcodeInfo));
                    	}
                    	/*if(eventKey.equals("22"))//ԤԼ
                    	{
                    		String ScanCodeInfo= map.get("ScanCodeInfo").toString();
                    		String QRcodeInfo=ScanCodeInfo.substring(29,ScanCodeInfo.length()-1);
                    		System.out.println("ԤԼ��ά������Ϊ��"+QRcodeInfo);
                    		String SQL="select bookId from book where ISBN='"+QRcodeInfo+"'";
                    		ResultSet rs=SqlHelper.executeQuery(SQL);
                    		int bookid=0;
                    		try
                            {
                    			 while (rs.next()) 
                    			 {
                    				bookid=rs.getInt("bookid");
                                 }
                    		}
                    		catch(Exception ex)
                    		{
                    			ex.printStackTrace();
                    		}
                    		Date now = new Date();
                    		String ordertime;
                    		String returntime;
                    		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    		ordertime=dateFormat.format(now);
                    		Calendar rightNow = Calendar.getInstance();
                    	    rightNow.setTime(now);
                    	    rightNow.add(Calendar.DAY_OF_YEAR,10);//���ڼ�10��
                    	    now = rightNow.getTime();
                    	    returntime = dateFormat.format(now);
                    		String sql="delete from BookReserve from BookReserve join Book on Book.bookID=BookReserve.bookId where isbn='"+QRcodeInfo+"';insert into bookBorrow('"+fromUserName+"',"+bookid+",'"+ordertime+"','"+returntime+"')";
                    		System.out.print(sql);
                			SqlHelper.executeUpdate(sql);
                			message="Ԥ������ɹ�!!";
                			message = MessageUtil.initText(toUserName, fromUserName, message);
                    	}*/
                    	if(eventKey.equals("31"))//����
                    	{
                    		String ScanCodeInfo= map.get("ScanCodeInfo").toString();
                    		String QRcodeInfo=ScanCodeInfo.substring(29,ScanCodeInfo.length()-1);
                    		System.out.println("�����ά������Ϊ��"+QRcodeInfo);
                    		message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.AdminReturnMessage(QRcodeInfo));
                    	}
                    }
                }
                else//δ��
                {
                	message=MessageUtil.HintAdminBind(toUserName, fromUserName);//��ʾ�û��󶨲����ô˹���
                }
           }         
            System.out.println(message);
        } 
        catch (Exception e) 
        {
            // TODO: handle exception
        	e.printStackTrace();
        } 
		return message;
	}
}
