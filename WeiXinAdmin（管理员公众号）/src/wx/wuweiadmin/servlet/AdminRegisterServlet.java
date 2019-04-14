package wx.wuweiadmin.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wx.wuweiadmin.po.Administrator;
import wx.wuweiadmin.sqlhelper.SqlHelper;

/**
 * Servlet implementation class AdminRegisterServlet
 */
@WebServlet("/AdminRegisterServlet")
public class AdminRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminRegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        try
		{
			String adminId = request.getParameter("adminId");//id
			String adminUsername = request.getParameter("adminUsername");//����Ա�˺�
			String adminPassword = request.getParameter("adminPassword");//����Ա����
			Administrator admin = new Administrator();
			admin.setAdminId(adminId);
			admin.setAdminUsername(adminUsername);
			admin.setAdminPassword(adminPassword);
			String sql="select count(*) from administrator where adminUsername='"+adminUsername+"' and adminPassword='"+adminPassword+"'";
			ResultSet rs=SqlHelper.executeQuery(sql);
			int r=0;
			while(rs.next())
			{
				r=rs.getInt(1);
			}
			if(r!=0)
			{
				//д�뵽���ݿ���
				String SQL = "insert into administrator values('"+admin.getAdminId()+"', '', '')";
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
				System.out.println(admin.getAdminId());
				System.out.println(admin.getAdminUsername());
				System.out.println(admin.getAdminPassword());
			}
			else
			{
				PrintWriter out = response.getWriter();
				out.println("���������Ϣ����");
			}
							
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
