package auth.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mvc.command.CommandHandler;

public class LogoutHandler implements CommandHandler {

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) 
	throws Exception {
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.invalidate(); // 로그아웃시 세션이 무효화된다.
		}
		res.sendRedirect(req.getContextPath() + "/index.jsp"); // 로그아웃하고 index.jsp로 페이지 리다이렉트
		return null;
	}

}
