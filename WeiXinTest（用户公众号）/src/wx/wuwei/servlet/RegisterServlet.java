package wx.wuwei.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wx.wuwei.po.Reader;
import wx.wuwei.sqlhelper.SqlHelper;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        // �û�ͬ����Ȩ���ܻ�ȡ��code
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String openId=null;
        
        // �û�ͬ����Ȩ
        if (!"authdeny".equals(code)) {
            // ��ȡ��ҳ��Ȩaccess_token
            WeixinOauth2Token weixinOauth2Token = AdvancedUtil.g  etOauth2AccessToken("wx1e83e75abd38e250", "9455fcaa649f7528618dbbff146ad627", code);
            // �û���ʶopenId
            openId = weixinOauth2Token.getOpenId();
            // ����Ҫ���ݵĲ��� openId
            request.setAttribute("openId", openId);
            
            System.out.println(openId);
        }
        // ��ת��index.jsp
        request.getRequestDispatcher("login.jsp").forward(request, response);
        */
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        Reader reader=new Reader();
        String readerId=null,name=null,sex=null,brithday=null,education=null,isAccept=null,hobby="";
        String year=null,month=null,day=null;
		String[] hobbys;
        
		try
		{
			readerId = request.getParameter("readerId");//���߱��
			name = request.getParameter("name");//��������
			sex = request.getParameter("sex");//�����Ա�
			education=request.getParameter("education");//����ѧ�� 
			
			year = request.getParameter("year");
			month = request.getParameter("month");
			day = request.getParameter("day");
			brithday=year+"��"+month+"��"+day+"��";//��������
			
			if(request.getParameter("isAccept")==null)//Э��
			{
				isAccept="false";
			}
			else
			{
				isAccept = request.getParameter("isAccept");
			}
			
			//������ȡ�����ѡ��ť��ֵ
			if(request.getParameterValues("hobby")==null)
			{
				hobby="��";
			}
			else
			{
				hobbys = request.getParameterValues("hobby");//���߰��� ����
				for(int i=0;i<hobbys.length;i++)
	            {    
					if(i==0)
					{
						hobby=hobbys[0];
					}
					if(i!=0)
					{
						hobby=hobby+" "+hobbys[i];
					}
	            }
			}
			
			reader.setReaderId(readerId);
			reader.setName(name);
			reader.setSex(sex);
			reader.setBrithday(brithday);
			reader.setHobby(hobby);
			reader.setEducation(education);
			if(isAccept.equals("true"))
			{
				reader.setFlag(true);
			}
			else
			{
				reader.setFlag(false);
			}
			//д�뵽���ݿ���
			String SQL = "insert into reader values('"+reader.getReaderId()+"', '"+reader.getName()+"', '"+reader.getSex()+"', '"+reader.getBrithday()+"', '"+reader.getEducation()+"','"+reader.getHobby()+"')";
			boolean result=SqlHelper.executeUpdate(SQL);
			if(result)
			{
				PrintWriter out = response.getWriter();
				out.println("�󶨳ɹ�����");
				System.out.println("�󶨳ɹ�");
			}
			else
			{
				PrintWriter out = response.getWriter();
				out.println("��ʧ�ܣ���");
				System.out.println("��ʧ��");
			}
			
			
			//��ӡ
			System.out.println(reader.getBrithday());
			System.out.println(reader.getEducation());
			System.out.println(reader.getName());
			System.out.println(reader.getReaderId());
			System.out.println(reader.getSex());
			System.out.println(reader.getHobby());
			System.out.println(reader.isFlag());		
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
