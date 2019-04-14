package wx.wuweiadmin.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import wx.wuweiadmin.service.ShowAdmininfoService;
import wx.wuweiadmin.sqlhelper.SqlHelper;


/**
 * Servlet implementation class DeleteAdmin
 */
@WebServlet("/DeleteAdmin")
public class DeleteAdmin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteAdmin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String adminId=request.getParameter("adminId");//�ù���Աid
        try 
        {
			if(ShowAdmininfoService.judgeUserInfo(adminId))
			{
				String SQL="delete from administrator where adminId='"+adminId+"'";
				if(SqlHelper.executeUpdate(SQL))
				{  
					request.setAttribute("Info", "�������Ա�󶨳ɹ���");
					request.getRequestDispatcher("WindowClose.jsp").forward(request, response);
					System.out.println("�������Ա�󶨳ɹ���");
				}
				else
				{
					request.setAttribute("Info", "�������Ա��ʧ�ܣ�");
					request.getRequestDispatcher("WindowClose.jsp").forward(request, response);
					System.out.println("�������Ա��ʧ�ܣ�");
				}
			}
			else
			{
				System.out.println("�����ڴ��û�����");
			}
		} 
        catch (SQLException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

}
