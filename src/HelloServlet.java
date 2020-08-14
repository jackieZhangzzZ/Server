

import java.io.PrintWriter;
import java.util.Date;

import com.yc.javax.servlet.http.HttpServlet;
import com.yc.javax.servlet.http.HttpServletRequest;
import com.yc.javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet{
	public HelloServlet(){
		super();
		System.out.println("HelloServlet的构造方法");
	}
	@Override
	public void init() {
		super.init();
		System.out.println("init方法");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("doGet()");
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("doPost被调用了");
		int r = 5/2;
		String result="时间:"+new Date().toString()+"结果为:"+r+"\r\n";
		
		PrintWriter out = response.getWriter();
		StringBuffer sb = new StringBuffer();
		sb.append("HTTP/1.0 200 OK\r\nContent-Type: text/html\r\nContent-Length:"
				+result.getBytes().length+"\r\n\r\n");
		out.println(sb.toString());
		out.println(result);
		out.flush();
		out.close();
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("service被调用了");
		super.service(request, response);
	}
	
}
