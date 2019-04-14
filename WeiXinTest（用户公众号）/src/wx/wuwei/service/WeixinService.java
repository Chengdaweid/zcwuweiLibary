package wx.wuwei.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wx.wuwei.util.MessageUtil;

/**
 * ���ķ�����
 * @author ZX50j
 *
 */
public class WeixinService {
	/**
	 * ����΢�ŷ���������
	 * @param request
	 * @return
	 */
	public static String processRequest(HttpServletRequest request) {
		
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
                String eventType = map.get("Event").toString();//�����¼�
                if(MessageUtil.EVENT_SUB.equals(eventType))//��ע  ��ʾ�û���
                {	
                    message = MessageUtil.UserSUB(toUserName, fromUserName);
                }
                else if(MessageUtil.EVENT_UNSUB.equals(eventType))//ȡ����ע 
                {
                	String mycontent = "лл���Ĺ�ע����ӭ��������������������� ";
                    message = MessageUtil.initText(toUserName, fromUserName, mycontent);
                }
                
                else if(MessageUtil.EVENT_CLICK.equals(eventType))//������¼� �˵�
                {
                	String eventKey = map.get("EventKey").toString();  //����˵�    �˵���keyֵ
                	if (eventKey.equals("11")) //�˺Ű�    11�˵�����
                	{  
                		message=MessageUtil.BindUser(toUserName, fromUserName);  //�˺Ű�              	                              				
                	}
                	else if(eventKey.equals("32"))//��������
                	{
                		message=MessageUtil.AboutUs(fromUserName, toUserName);
                	}
                	
                	else if(ShowUserinfoService.judgeUserInfo(fromUserName))//���û�
                	{
                		if(eventKey.equals("12"))//������Ϣ
                		{
                			message=MessageUtil.userInfo(fromUserName, toUserName);
                		}
                	}
                	else//�ǰ��û������ô˹���
                	{
                		message=MessageUtil.HintUserBind(toUserName, fromUserName);//��ʾ��
                	}
				}
                
                else if(MessageUtil.EVENT_VIEW.equals(eventType))//��תURL
                {             	
                	String url = map.get("EventKey").toString();
                	if(ShowUserinfoService.judgeUserInfo(fromUserName))//���û�
                	{
                		message = MessageUtil.initText(toUserName, fromUserName, url);
                	}
                	else
                	{
                		message=MessageUtil.HintUserBind(toUserName, fromUserName);//��ʾ��
                	}

                }
                else if(MessageUtil.MESSAGE_SCANCODE.equals(eventType))//ɨ����¼�
                {
                	if(ShowUserinfoService.judgeUserInfo(fromUserName))//���û�
                	{
                		System.out.println("��ά��mapΪ:"+map);

                		String key = map.get("EventKey").toString();//�õ���ť��keyֵ
                		System.out.println("��ά��˵�keyֵΪ��"+key);

                		if(key.equals("31"))
                		{
                			String ScanCodeInfo= map.get("ScanCodeInfo").toString();
                			String QRcodeInfo=ScanCodeInfo.substring(29,ScanCodeInfo.length()-1);//�õ�ͼ���isbn
                			System.out.println("��ά������Ϊ��"+QRcodeInfo);
                			message=MessageUtil.ScanCodeReading(QRcodeInfo, toUserName, fromUserName);
                		}
                	}
                	else//�ǰ��û�
                	{
                		message=MessageUtil.HintUserBind(toUserName, fromUserName);//��ʾ��
                	}
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
