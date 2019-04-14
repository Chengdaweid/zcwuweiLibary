package wx.wuweiadmin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import wx.wuweiadmin.po.News;
import wx.wuweiadmin.po.NewsMessage;
import wx.wuweiadmin.po.TextMessage;
import wx.wuweiadmin.service.ShowAdmininfoService;
import wx.wuweiadmin.sqlhelper.SqlHelper;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class MessageUtil {
	
	public static final String MESSAGE_TEXT = "text";//�ı���Ϣ
    public static final String MESSAGE_NEWS = "news";//ͼ����Ϣ
    public static final String MESSAGE_LINK = "link";//������Ϣ
    public static final String MESSAGE_LOCATION = "location";//����λ����Ϣ
    public static final String MESSAGE_EVENT = "event";//�¼�
    public static final String EVENT_SUB = "subscribe";//��ע
    public static final String EVENT_UNSUB = "unsubscribe";//ȡ��
    public static final String EVENT_CLICK = "CLICK";//������¼�
    public static final String EVENT_VIEW = "VIEW";//��תURL
    public static final String MESSAGE_SCANCODE= "scancode_waitmsg";//ɨ����¼�
    
    /**
     * xmlתΪmap����
     * @param request
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static Map<String, Object> xmlToMap(HttpServletRequest request) throws IOException, DocumentException{ 
    	SAXReader reader = new SAXReader();
        InputStream ins = request.getInputStream();
        Document doc = reader.read(ins);
        Map<String, Object> map = new HashMap<String, Object>(); 
        if(doc == null) 
            return map; 
        Element root = doc.getRootElement(); 
        for (@SuppressWarnings("rawtypes")
		Iterator iterator = root.elementIterator(); iterator.hasNext();) { 
            Element e = (Element) iterator.next(); 
            @SuppressWarnings("rawtypes")
			List list = e.elements(); 
            if(list.size() > 0){ 
                map.put(e.getName(), Dom2Map(e)); 
            }else 
                map.put(e.getName(), e.getText()); 
        } 
        return map; 
    } 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map Dom2Map(Element e){ 
        Map map = new HashMap(); 
        List list = e.elements(); 
        if(list.size() > 0){ 
            for (int i = 0;i < list.size(); i++) { 
                Element iter = (Element) list.get(i); 
                List mapList = new ArrayList(); 
                 
                if(iter.elements().size() > 0){ 
                    Map m = Dom2Map(iter); 
                    if(map.get(iter.getName()) != null){ 
                        Object obj = map.get(iter.getName()); 
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){ 
                            mapList = new ArrayList(); 
                            mapList.add(obj); 
                            mapList.add(m); 
                        } 
                        if(obj.getClass().getName().equals("java.util.ArrayList")){ 
                            mapList = (List) obj; 
                            mapList.add(m); 
                        } 
                        map.put(iter.getName(), mapList); 
                    }else 
                        map.put(iter.getName(), m); 
                } 
                else{ 
                    if(map.get(iter.getName()) != null){ 
                        Object obj = map.get(iter.getName()); 
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){ 
                            mapList = new ArrayList(); 
                            mapList.add(obj); 
                            mapList.add(iter.getText()); 
                        } 
                        if(obj.getClass().getName().equals("java.util.ArrayList")){ 
                            mapList = (List) obj; 
                            mapList.add(iter.getText()); 
                        } 
                        map.put(iter.getName(), mapList); 
                    }else 
                        map.put(iter.getName(), iter.getText()); 
                } 
            } 
        }else 
            map.put(e.getName(), e.getText()); 
        return map; 
    } 
    
    /**
     * ��չxstreamʹ��֧��CDATA
     */
    @SuppressWarnings("unused")
	private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // ������xml�ڵ��ת��������CDATA���
                boolean cdata = true;

               
                public void startNode(String name, @SuppressWarnings("rawtypes") Class clazz) {
                    super.startNode(name, clazz);
                }

                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });
    
    /**
     * ���ı���Ϣ����ת��Ϊxml
     * @param textMessage
     * @return
     */
    public static String textMessageToXml(TextMessage textMessage){
        XStream xstream = new XStream();
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }
    
    /**
     * ͼ����ϢתΪXML
     * @param newsMessage
     * @return
     */
    public static String newsMessageToXml(NewsMessage newsMessage){
        XStream xstream = new XStream();
        xstream.alias("xml", newsMessage.getClass());
        xstream.alias("item", new News().getClass());
        return xstream.toXML(newsMessage);
    } 
    
    /**
	  * ��װ�ı���Ϣ
	  * @param toUserName
	  * @param fromUserName
	  * @param content
	  * @return
	  */
		public static String initText(String toUserName, String fromUserName, String content){
	        TextMessage text = new TextMessage();
	        text.setFromUserName(toUserName);
	        text.setToUserName(fromUserName);
	        text.setMsgType(MessageUtil.MESSAGE_TEXT);
	        text.setCreateTime(new Date().getTime());
	        text.setContent(content);
	        return textMessageToXml(text);
	    }
		
		/**
	     * ����Ա������Ϣ
	     * @param url
	     * @return
	     */
	    public static String AdminBorrowMessage(String url)
	    {
	    	String newurl=url.replace("wx1e83e75abd38e250", "wx96b86ea8fdffd49f").replace("http%3A%2F%2F123.206.205.38%2FWeiXinTest%2FManagerScan", "http%3A%2F%2F123.206.205.38%2FWeiXinAdmin%2FManagerScan");
	    	StringBuffer sb = new StringBuffer();
	        sb.append("���ã�\n\n");
	        sb.append("��ϸ������Ϣ��������");
	        sb.append("<a href='"+newurl+"'>��ϸ������Ϣ</a>\n\n");
	        sb.append("лл��������\n");
	        return sb.toString();  
	    }
	    /**
	     * ����Ա������Ϣ
	     * @param url
	     * @return
	     */
	    public static String AdminReturnMessage(String url)
	    {
	    	String newurl=url.replace("wx1e83e75abd38e250", "wx96b86ea8fdffd49f").replace("http%3A%2F%2F123.206.205.38%2FWeiXinTest%2FManagerScan", "http%3A%2F%2F123.206.205.38%2FWeiXinAdmin%2FManageReturn");
	    	StringBuffer sb = new StringBuffer();
	        sb.append("���ã�\n\n");
	        sb.append("��ϸ������Ϣ��������");
	        sb.append("<a href='"+newurl+"'>��ϸ������Ϣ</a>\n\n");
	        sb.append("лл��������\n");
	        return sb.toString();  
	    }
	    
	    /**
	     * ���û�
	     * @param toUserName
	     * @param fromUserName
	     * @return
	     * @throws SQLException
	     */
	    public static String BindAdmin(String toUserName,String fromUserName) throws SQLException
	    {
	    	String message = null;
	    	if(!ShowAdmininfoService.judgeUserInfo(fromUserName))
			{
				//ͼ����Ϣ  δ��
	    		List<News> newsList = new ArrayList<News>();
	    		NewsMessage newsMessage = new NewsMessage();
	    		
	    		News news = new News();
	    		news.setTitle("����Ϣ");
	    		news.setDescription("�󶨹���Ա΢���빫�ں�");
	    		news.setPicUrl("http://123.206.205.38/WeiXinAdmin/images/timg.jpg");  
	    		
	    		News news2 = new News();
	    		news2.setTitle("�����˺���δ�󶨣�������������˺Ű󶨣�");
	    		news2.setDescription("�˺Ű�");
	    		news2.setPicUrl("http://123.206.205.38/WeiXinAdmin/images/timg.jpg");
	    		news2.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx96b86ea8fdffd49f&redirect_uri=http%3A%2F%2F123.206.205.38%2FWeiXinAdmin%2Fadminlogin.jsp%3FadminId%3D"+fromUserName+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
	    		
	    		newsList.add(news);
	    		newsList.add(news2);
	    		
	    		newsMessage.setToUserName(fromUserName);
	    		newsMessage.setFromUserName(toUserName);
	    		newsMessage.setCreateTime(new Date().getTime());
	    		newsMessage.setMsgType("news");
	    		newsMessage.setArticles(newsList);
	    		newsMessage.setArticleCount(newsList.size());
	    		message = MessageUtil.newsMessageToXml(newsMessage);
			}
			else
			{
				//ͼ����Ϣ ��
	    		List<News> newsList = new ArrayList<News>();
	    		NewsMessage newsMessage = new NewsMessage();
	    		
	    		News news = new News();
	    		news.setTitle("����Ϣ");
	    		news.setDescription("����󶨹���Ա΢���빫�ں�");
	    		news.setPicUrl("http://123.206.205.38/WeiXinAdmin/images/timg.jpg");                   		 
	    		
	    		News news2 = new News();
	    		news2.setTitle("�����˺��Ѱ�,������ｫ����󶨣�");
	    		news2.setDescription("�˺Ž����");
	    		news2.setPicUrl("http://123.206.205.38/WeiXinAdmin/images/timg.jpg");
	    		news2.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx96b86ea8fdffd49f&redirect_uri=http%3A%2F%2F123.206.205.38%2FWeiXinAdmin%2FDeleteAdmin%3FadminId%3D"+fromUserName+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
	    		
	    		newsList.add(news);
	    		newsList.add(news2);
	    		
	    		newsMessage.setToUserName(fromUserName);
	    		newsMessage.setFromUserName(toUserName);
	    		newsMessage.setCreateTime(new Date().getTime());
	    		newsMessage.setMsgType("news");
	    		newsMessage.setArticles(newsList);
	    		newsMessage.setArticleCount(newsList.size());
	    		message = MessageUtil.newsMessageToXml(newsMessage);
			}
	    	return message;
	    }
	    
	    /**
	     * ��ע  ��ʾ�û���
	     * @param toUserName
	     * @param fromUserName
	     * @return
	     * @throws SQLException 
	     */
	    public static String AdminSUB(String toUserName,String fromUserName) throws SQLException
	    {
	    	String message = null;
	    	if(!ShowAdmininfoService.judgeUserInfo(fromUserName))
	    	{
	    		//ͼ����Ϣ ��ʾ�û���
	    		List<News> newsList = new ArrayList<News>();
	    		NewsMessage newsMessage = new NewsMessage();
	    		
	    		News news1 = new News();
	    		news1.setTitle("��л��ע��΢�����Ľ��İ��¹���Ա�ˣ�");
	    		news1.setDescription("��л��ע���ں�");
	    		news1.setPicUrl("http://123.206.205.38/WeiXinAdmin/images/timg.jpg");  
	    		
	    		News news2 = new News();
	    		news2.setTitle("����΢�ź���δ�󶨣������û�->�˻��� �����˺Ű󶨣�");
	    		news2.setDescription("�˺Ű�");
	    		
	    		newsList.add(news1);
	    		newsList.add(news2);
	    	
	    		newsMessage.setToUserName(fromUserName);
	    		newsMessage.setFromUserName(toUserName);
	    		newsMessage.setCreateTime(new Date().getTime());
	    		newsMessage.setMsgType("news");
	    		newsMessage.setArticles(newsList);
	    		newsMessage.setArticleCount(newsList.size());
	    		message = MessageUtil.newsMessageToXml(newsMessage);
	    	}
	    	else
	    	{
	    		List<News> newsList = new ArrayList<News>();
	    		NewsMessage newsMessage = new NewsMessage();
	    		
	    		News news1 = new News();
	    		news1.setTitle("��л��ע��΢�����Ľ��İ��¹���Ա�ˣ�ף���и��õĹ������飡");
	    		news1.setDescription("��л��ע���ں�");
	    		news1.setPicUrl("http://123.206.205.38/WeiXinAdmin/images/timg.jpg");  
	    		
	    		newsList.add(news1);
	    	
	    		newsMessage.setToUserName(fromUserName);
	    		newsMessage.setFromUserName(toUserName);
	    		newsMessage.setCreateTime(new Date().getTime());
	    		newsMessage.setMsgType("news");
	    		newsMessage.setArticles(newsList);
	    		newsMessage.setArticleCount(newsList.size());
	    		message = MessageUtil.newsMessageToXml(newsMessage);
	    	}
	    	return message;
	    }
	    
	    /**
	     * ȡ��   ����û���
	     * @param fromUserName
	     * @throws SQLException
	     */
	    public static void AdminUNSUB(String fromUserName) throws SQLException
	    {
	    	if(ShowAdmininfoService.judgeUserInfo(fromUserName))
	    	{
	    		String SQL="delete from administrator where adminId='"+fromUserName+"'";
	    		if(SqlHelper.executeUpdate(SQL))
	    		{
	        		System.out.println("����û��󶨳ɹ���");
	    		}
	    		else
	    		{
	    			System.out.println("����û���ʧ�ܣ�");
	    		}
	    	}
	    	else
	    	{
	    		System.out.println("�����ڴ��û�����");
	    	}
	    }
	    
	    /**
	     * ��ʾ�û��󶨲����ô˹���
	     * @param toUserName
	     * @param fromUserName
	     * @return
	     * @throws SQLException
	     */
	    public static String HintAdminBind(String toUserName,String fromUserName) throws SQLException
	    {
	    	String message = null;
	    	StringBuffer sb = new StringBuffer();
	    	sb.append("����!\n\n");
	    	sb.append("�ǹ���Ա�û�����ʹ�ã�\n");  
	    	sb.append("�����û�->�˻��� �����˺Ű󶨣�\n");
	    	sb.append("лл��������\n");
	    	message = MessageUtil.initText(toUserName, fromUserName, sb.toString());
	    	return message;
	    }
}
