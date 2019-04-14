package wx.wuwei.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import wx.wuwei.po.News;
import wx.wuwei.po.NewsMessage;
import wx.wuwei.po.Reader;
import wx.wuwei.po.TextMessage;
import wx.wuwei.service.ShowUserinfoService;
import wx.wuwei.sqlhelper.SqlHelper;
import wx.wuwei.util.MessageUtil;

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
    @SuppressWarnings({ "unchecked", "rawtypes" })
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

                @SuppressWarnings({  "rawtypes" })
                public void startNode(String name, Class clazz) {
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
     * ��ʾ�û�������Ϣ
     * @param fromUserName
     * @return
     */
    public static String userInfo(String fromUserName,String toUserName){
    	String message = null;
    	Reader reader=ShowUserinfoService.getUserInfo(fromUserName);
        StringBuffer sb = new StringBuffer();
        sb.append("������"+reader.getName()+"\n");
        sb.append("֤�ţ�"+reader.getReaderId()+"\n");  
        sb.append("�Ա�"+reader.getSex()+"\n");
        sb.append("���գ�"+reader.getBrithday()+"\n");
        sb.append("ѧ����"+reader.getEducation()+"\n");
        sb.append("���ã�"+reader.getHobby()+"\n\n");
        sb.append("��������Ϣ�����뼰ʱ��������ϵ��\n");
        
        //ͼ����Ϣ
		List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		
		News news = new News();
		news.setTitle("���ĸ�����Ϣ");
		news.setDescription("�û���Ϣ");
		news.setPicUrl("http://123.206.205.38/WeiXinTest/image/timg.jpg");
		//news.setUrl("");   
		
		News news2 = new News();
		news2.setTitle(sb.toString());
		news2.setDescription("������Ϣ���");
		//news2.setPicUrl("");
		//news2.setUrl("");
		
		newsList.add(news);
		newsList.add(news2);
		
		newsMessage.setToUserName(fromUserName);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType("news");
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());
		message = MessageUtil.newsMessageToXml(newsMessage);
        
        return message;       
    }
    /**
     * ��������
     * @param fromUserName
     * @param toUserName
     * @return
     */
    public static String AboutUs(String fromUserName,String toUserName)
    {
    	String message=null;
    	StringBuffer sb = new StringBuffer();
    	sb.append("����!��ӭʹ����΢�����Ľ��İ��£���\n\n");
    	sb.append("������ʹ�ù�������ʲô���ʣ�����ϵ���ǡ�\n");  
    	sb.append("���䣺1124588341@qq.com\n");
    	sb.append("лл����ʹ�ã���\n\n");
    	sb.append("��������������ʯ�ʹ�ѧ��BYT�Ŷӡ�\n");
    	
    	List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		
		News news = new News(); 
		news.setTitle("��������");
		
		News news2 = new News();
		news2.setTitle(sb.toString());
		news2.setDescription("��������");

		
		newsList.add(news);
		newsList.add(news2);
		
		newsMessage.setToUserName(fromUserName);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType("news");
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());
		message = MessageUtil.newsMessageToXml(newsMessage);	
    	return message;
    }
    
    /**
     * ���û�
     * @param toUserName
     * @param fromUserName
     * @return
     * @throws SQLException
     */
    public static String BindUser(String toUserName,String fromUserName) throws SQLException
    {
    	String message = null;
    	if(!ShowUserinfoService.judgeUserInfo(fromUserName))
		{
			//ͼ����Ϣ  δ��
    		List<News> newsList = new ArrayList<News>();
    		NewsMessage newsMessage = new NewsMessage();
    		
    		News news = new News();
    		news.setTitle("����Ϣ");
    		news.setDescription("���û�΢���빫�ں�");
    		news.setPicUrl("http://123.206.205.38/WeiXinTest/image/timg.jpg");  
    		
    		News news2 = new News();
    		news2.setTitle("�����˺���δ�󶨣�������������˺Ű󶨣�");
    		news2.setDescription("�˺Ű�");
    		news2.setPicUrl("http://123.206.205.38/WeiXinTest/image/timg.jpg");
    		news2.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1e83e75abd38e250&redirect_uri=http%3A%2F%2F123.206.205.38%2FWeiXinTest%2Flogin.jsp%3FreaderId%3D"+fromUserName+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
    		
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
    		news.setDescription("������û�΢���빫�ں�");
    		news.setPicUrl("http://123.206.205.38/WeiXinTest/image/timg.jpg");                   		 
    		
    		News news2 = new News();
    		news2.setTitle("�����˺��Ѱ�,������ｫ����󶨣�");
    		news2.setDescription("�˺Ž����");
    		news2.setPicUrl("http://123.206.205.38/WeiXinTest/image/timg.jpg");
    		news2.setUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1e83e75abd38e250&redirect_uri=http%3A%2F%2F123.206.205.38%2FWeiXinTest%2FDeleteUser%3FreaderId%3D"+fromUserName+"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
    		
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
    public static String UserSUB(String toUserName,String fromUserName) throws SQLException
    {
    	String message = null;
    	if(!ShowUserinfoService.judgeUserInfo(fromUserName))
    	{
    		//ͼ����Ϣ ��ʾ�û���
    		List<News> newsList = new ArrayList<News>();
    		NewsMessage newsMessage = new NewsMessage();
    		
    		News news1 = new News();
    		news1.setTitle("��л��ע��΢�����Ľ��İ��£�");
    		news1.setDescription("��л��ע���ں�");
    		news1.setPicUrl("http://123.206.205.38/WeiXinTest/image/timg.jpg");  
    		
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
    		news1.setTitle("��л��ע��΢�����Ľ��İ��£�ף���и��õĽ������飡");
    		news1.setDescription("��л��ע���ں�");
    		news1.setPicUrl("http://123.206.205.38/WeiXinTest/image/timg.jpg");  
    		
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
     * ��ʾ�û��󶨲����ô˹���
     * @param toUserName
     * @param fromUserName
     * @return
     * @throws SQLException
     */
    public static String HintUserBind(String toUserName,String fromUserName) throws SQLException
    {
    	String message = null;
    	StringBuffer sb = new StringBuffer();
    	sb.append("����!\n\n");
    	sb.append("����΢�ź���δ���а󶨣�δ���û����ֹ��ܲ���ʹ�ã��뾡��󶨡�\n");  
    	sb.append("����û�->�˻��� �����˺Ű󶨣�\n\n");
    	sb.append("лл��������\n");
    	
    	List<News> newsList = new ArrayList<News>();
		NewsMessage newsMessage = new NewsMessage();
		
		News news = new News(); 
		news.setTitle("��ʾ�󶨣�");
		
		News news2 = new News();
		news2.setTitle(sb.toString());
		news2.setDescription("��ʾ�󶨣�");

		
		newsList.add(news);
		newsList.add(news2);
		
		newsMessage.setToUserName(fromUserName);
		newsMessage.setFromUserName(toUserName);
		newsMessage.setCreateTime(new Date().getTime());
		newsMessage.setMsgType("news");
		newsMessage.setArticles(newsList);
		newsMessage.setArticleCount(newsList.size());
		message = MessageUtil.newsMessageToXml(newsMessage);	
    	return message;
    }
    /**
     * ɨ�����
     * @param QRcodeInfo
     * @return
     */
    public static String ScanCodeReading(String QRcodeInfo,String toUserName,String fromUserName)
    {
    	String message=null;
    	String ISBN = QRcodeInfo;
    	String SQL="select bookId from book where ISBN='"+ISBN+"'";
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
		String sql1="select count(*) from BookReserve where readerid='"+fromUserName+"' and bookid="+bookid;
		ResultSet rs1=SqlHelper.executeQuery(sql1);
		int r=0;
		try 
		{
			while(rs1.next())
			{
				r=rs1.getInt(1);
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(r!=0)
		{
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
    		String sql="delete from BookReserve  where bookid="+bookid+" and readerid='"+fromUserName+"';insert into bookBorrow('"+fromUserName+"',"+bookid+",'"+ordertime+"','"+returntime+"')";
    		System.out.print(sql);
			SqlHelper.executeUpdate(sql);
			message="Ԥ������ɹ�!!";
			message = MessageUtil.initText(toUserName, fromUserName, message);
		}
		else
		{
	    	String url="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1e83e75abd38e250&redirect_uri=http%3A%2F%2F123.206.205.38%2FWeiXinTest%2FScanCodeReading%3FISBN%3Disbninfo&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
	    	String newurl=url.replace("isbninfo", QRcodeInfo);
	    	StringBuffer sb = new StringBuffer();
	        sb.append("���ã�\n\n");
	        sb.append("��ϸͼ����Ϣ��������");
	        sb.append("<a href='"+newurl+"'>ͼ����Ϣ</a>\n\n");
	        sb.append("лл��������\n");
			message = MessageUtil.initText(toUserName, fromUserName, sb.toString());	
		}
    	return message;
    } 
    /**
     * ��������
     * @param info
     * @param toUserName
     * @param fromUserName
     * @return
     */
    public static String Remind(String message)
    {
    	return message;
    }
} 
