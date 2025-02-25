package kr.or.connect.guestbook.argumentresolver;

import java.util.Iterator;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class HandlerMapArgumentResolver implements HandlerMethodArgumentResolver 
{
	@Override 
	public boolean supportsParameter(MethodParameter parameter) 
	{
		// 컨트롤러 메소드의 인자가 4개일 경우 supportsParameter메소드가 매번 호출
		// supportsParameter는 인자의 정보를 파라미터로 전달
		// 해당 파라미터 정보에 원하는 정보가 있다면 true를 없다면 false를 반환함
		
		// 파라미터 타입이 HeaderInfo랑 같은 타입이면 true를 반환, 아니면 false를 반환
		return parameter.getParameterType() == HeaderInfo.class;
	}

	// supportsParameter가 true를 리턴할 경우에만 resolveArgument가 호출
	// resolveArgument가 리턴한 값은 컨트롤러 메소드의 인자로 전달
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, 
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HeaderInfo headerInfo = new HeaderInfo(); // 정보를 담을 공간
		
		Iterator<String> headerNames = webRequest.getHeaderNames();
		while(headerNames.hasNext())
		{
			String headerName = headerNames.next(); // 헤더이름 얻음
			String headerValue = webRequest.getHeader(headerName); // 헤더값 얻음
			System.out.println(headerName + " : " + headerValue);
			headerInfo.put(headerName, headerValue); // 값을 담음
		}
		return headerInfo;
	}

}
