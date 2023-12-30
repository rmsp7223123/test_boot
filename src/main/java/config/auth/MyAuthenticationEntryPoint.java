package config.auth;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
	
	// 인증되지 않은 사용자가 보호된 리소스에 액세스하려고 할 때 호출

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// 메세지 출력 후 홈으로 redirect
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		pw.println("<script>alert('로그인한 유저만 가능합니다!'); location.href='/users/login';</script>");
		pw.flush();
	}
}
