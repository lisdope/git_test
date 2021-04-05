package mvc.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import mvc.command.NullHandler;

public class ControllerUsingURI extends HttpServlet{
	
	// <커맨드, 핸들러인스턴스> 매핑 정보 저장
	private Map<String, CommandHandler> commandHandlerMap = new HashMap<String, CommandHandler>();

	@Override
	public void init() throws ServletException {
		// configFile 초기화 파라미터 값을 읽어온다.
		String configFile = getInitParameter("configFile");
		// Properties 객체를 참조하는 객체 참조 변수 prop에 Properties를 생성해 준다.		
		Properties prop = new Properties();
		// 설정 파일 경로를 지정해 준다.
		String configFilePath = getServletContext().getRealPath(configFile);
		// 설정 파일로부터 매핑 정보를 읽어와서 Properties 객체에 저장한다. Properties는 목록(이름, 값)을 갖는 클래스로서,
		// 프로퍼티 커맨드 이름으로 사용하고 값을 클래스 이름으로 사용한다.
		try(FileReader fr = new FileReader(configFilePath)) {
			prop.load(fr);			
		} catch(IOException e){
			throw new ServletException(e);
		}
		// Properties에 저장된 각 프로퍼티의 키에 대해 처리 작업을 반복한다.
		Iterator keyIter = prop.keySet().iterator();
		while(keyIter.hasNext()) {
			// 프로퍼티 이름을 커맨드 이름으로 사용한다.
			String command = (String)keyIter.next();
			// 커멘드 이름에 해당하는 핸들러 클래스 이름(handlerClassName)을 Properties에서 구한다.
			String handlerClassName = prop.getProperty(command);
			try {
				// 핸들러 클래스 이름(handlerClassName)을 이용해서 Class 객체를 구한다.
				Class<?> handlerClass = Class.forName(handlerClassName);
				// 앞서 구했던 handlerClass 로부터 핸들러 객체를 생성한다.
				// 즉, 앞서 구했던 핸들러 클래스 이름(handlerClassName)에 해당하는 클래스의 객체를 생성한다.
				CommandHandler handlerInstance = (CommandHandler) handlerClass.newInstance();
				// commandHandlerMap에 (커맨드, 핸들러객체) 매핑 정보를 저장한다.
				commandHandlerMap.put(command, handlerInstance);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
				throw new ServletException(e);
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		process(req, resp);
	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		process(req, resp);
		
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
				
				// 요청 URI를 명령어로 사용하려고 컨트롤러 서블릿의 process() 메서드에서 
				// 앞서 ControllerUsingFile 서블릿의 request.getParameter("cmd") 대신에 다음의 3줄 코드를 적용해 줌
				// ControllerUsingFile 서블릿에서 다음의 아래 77행~80행을 추가해주어 "요청 URI를 명령어로 사용할 수 있게 되었음
				
				
				String command = request.getRequestURI();
				// 본 예제에서  전체 URL경로는 http://localhost:9994/chap18/hello.do
				// 먼저, requestInfo.jsp 파일 실행 확인 바람				
				// 본 예제에서 요청 URI(RequestURI) 값 = /chap18/hello.do
				// 요청 URI = <%= request.getRequestURI() %> <br>
				// 본 예제에서  request.getContextPath() 컨텍스트 경로 값 = /chap18/
				// 컨텍스트 경로 = <%= request.getContextPath() %> <br>
				
				if(command.indexOf(request.getContextPath())==0) {
				// 만약에, command 변수의 request.getContextPath의 indexOf 값을 구하게 되면,
				// 이때, command 변수의 indexOf() 메서드의 request.getContextPath 값이 0과 같다면
					
					command = command.substring(request.getContextPath().length());
					// 위에서 request.getContextPath().length() 크기는 7을 나타냄
					// 컨텍스트 경로의 크기 = <%= request.getContextPath().length() %> 크기는 7이다.
					// 그러므로, command 변수에 있는 컨텍스트 경로를 substring(7)로 해서, 컨텍스트 경로를 잘라줌(제거함)  
					// substring 함수 활용 문자 자르기 = <%= request.getContextPath().substring(7) %>
					// 즉, 요청 URI에서 request.getContextPath() 부분을 잘라줌(제거함)으로써,
					// 웹 어플리케이션 내에서의  요청 URI만을 사용하게 하기 위함임.
				}
				
				
				// commandHandlerMap에서 요청을 처리할 핸들러 객체를 구한다. cmd 파라미터 값을 키로 사용한다.
				CommandHandler handler = commandHandlerMap.get(command);
				// 명령어에 해당하는 핸들러 객체가 존재하지 않을 경우 NullHandler를 사용한다.
				if(handler == null) {
					// NullHandler 클래스는 404 에러를 응답으로 전송하는 핸들러 클래스
					handler = new NullHandler();
				}
				String viewPage = null;
				try {
					// 앞서 핸들러 객체의 process() 메서드를 호출해서 요청을 처리하고,
					// 결과로 보여줄 뷰 페이지 경로를 리턴값으로 전달 받는다.
					// 핸들러 인스턴스인 handler의 process() 메서드는 클라이언트의 요청을 알맞게 처리한 후,
					// 뷰 페이지에 보여줄 결과값을 request(또는 session)의 속성에 저장한다.
					viewPage = handler.process(request, response);
				} catch(Exception e) {
					throw new ServletException(e);
				}
				// viewPage가 null이 아닌 경우, 핸들러 인스턴스가 리턴한 뷰 페이지 viewPage로 이동한다.
				if(viewPage != null) {
					RequestDispatcher dispatcher = request.getRequestDispatcher(viewPage);
					dispatcher.forward(request, response);
				}
	}
	
}
