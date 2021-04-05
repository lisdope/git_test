package mvc.command;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CommandHandler {

	
		public String process(HttpServletRequest req,HttpServletResponse res)
		throws Exception;
		
}
