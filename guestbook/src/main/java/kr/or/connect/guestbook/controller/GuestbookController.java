package kr.or.connect.guestbook.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.or.connect.guestbook.argumentresolver.HeaderInfo;
import kr.or.connect.guestbook.dto.Guestbook;
import kr.or.connect.guestbook.service.GuestbookService;

@Controller
public class GuestbookController 
{
	@Autowired // 서비스를 이용하기 위해 DI이용
	GuestbookService guestbookService;
	
	@GetMapping(path="/list") // '/list'로 GET요청이 들어올 때 처리 (@RequestParam으로 start의 값을 꺼내옴, 없으면 0을 디폴트값 설정)
	public String list(@RequestParam(name="start", required=false, defaultValue="0") int start, ModelMap model, 
			HttpServletRequest request, HttpServletResponse response,
			HeaderInfo headerInfo /** 아규먼트 리졸버는 해당 인자가 넘어왔을때만 동작하므로 인자를 넘겨줌 */ ) {
		
		/* 아규먼트 리졸버 동작확인 */
		System.out.println("=========================================");
		System.out.println(headerInfo.get("user-agent"));
		System.out.println("=========================================");
		
		String value = null;
		boolean find = false;
		Cookie[] cookies = request.getCookies(); // 쿠키를 가져옴 (여러 개 있을 때 배열, 없을 때 null)
		
		/** 클라이언트의 요청으로부터 쿠키가 존재하는지 확인 */
		if(cookies != null) // 반드시 이렇게 처리해줘야 처음 요청들어왔을 때 오류방지 가능 (#매우중요!)
		{
			for(Cookie cookie : cookies) // 쿠키의 배열을 탐색
			{
				if("count".contentEquals(cookie.getName())) // 쿠키의 이름이 count인지 확인
				{
					find = true; // 쿠키가 존재
					value = cookie.getValue(); // value를 받아옴
					break; // 원하는 쿠키 찾았으면 반복문 빠져나옴
				}
			}
		}
		
		/** 쿠키를 이용하여 하고싶은 처리수행 */
		if(!find) // 쿠키가 없는 경우 (처음 요청이 들어왔을 때 처리)
			value = "1"; // 쿠키는 String으로된 value를 가질 수 있음
		else // 쿠키가 존재한다면
		{
			try 
			{
				int i = Integer.parseInt(value); // value의 값을 읽어
				value = Integer.toString(++i); // 하나 증가시켜 다시 넣음
			}
			catch (Exception e) { value = "1"; }
		}
		
		/** 여기 부분은 쿠키가 존재하든 안하든 반드시 수행 */
		/* 쿠키의 생성 */
		Cookie cookie = new Cookie("count", value); // 쿠키객체 생성
		
		/* 쿠키의 기본설정 */
		cookie.setMaxAge(60 * 60 * 24 * 365); // 단위는 초 (쿠키 유지시간)
		// 참고) -1이면 브라우저 닫으면 초기화, 0이면 유지시간 없어 삭제됨
		cookie.setPath("/"); // 경로 이하에 모두 쿠키 적용 (http://localhost:8080/guestbooks 이하 경로)
		
		/* 쿠키를 클라이언트에 응답으로 보냄 */
		response.addCookie(cookie); // 응답결과에 쿠키를 싣어서 보냄
		
		/** ============================================================================= */
		
		// start로 시작하는 방명록 목록 구하기
		List<Guestbook> list = guestbookService.getGuestbooks(start);
		
		// 전체 페이지수 구하기
		int count = guestbookService.getCount();
		int pageCount = count / GuestbookService.LIMIT; // 몇 페이지일지
		if(count % GuestbookService.LIMIT > 0)
			pageCount++;
		
		// 페이지 수만큼 start의 값을 리스트로 저장 : 페이지수가 3이면 0, 5, 10 이렇게 저장된다
		// list?start=0 , list?start=5, list?start=10 으로 링크가 걸린다.
		List<Integer> pageStartList = new ArrayList<>();
		for(int i = 0; i < pageCount; i++) 
			pageStartList.add(i * GuestbookService.LIMIT);
		
		// JSP에서 사용할 수 있도록 model에 넣어줌
		model.addAttribute("list", list);
		model.addAttribute("count", count);
		model.addAttribute("pageStartList", pageStartList);
		
		/** 쿠키 값을 JSP에서 출력하기 위해서 설정하는 코드삽입 */
		model.addAttribute("cookieCount", value); // 새롭게 추가!
		
		return "list";
	}
	
	@PostMapping(path="/write")
	public String write(@ModelAttribute Guestbook guestbook, HttpServletRequest request) 
	{
		String clientIp = request.getRemoteAddr();
		System.out.println("clientIp : " + clientIp);
		guestbookService.addGuestbook(guestbook, clientIp);
		return "redirect:list";
	}
	
	@GetMapping(path="/delete") // URL로 요청 = GET방식
	public String delete(@RequestParam(name="id", required=true) Long id,
						 @SessionAttribute("isAdmin") String isAdmin, // 세션에서 값을 꺼냄
						 HttpServletRequest request,
						 RedirectAttributes redirectAttr){
		if(isAdmin == null || !"true".equals(isAdmin)) // 세션값이 true가 아닌 경우
		{
			redirectAttr.addFlashAttribute("errorMessage", "로그인을 하지 않았습니다.");
			return "redirect:loginform"; // flash map에 메세지 담아 넘김
		}
		String clientIp = request.getRemoteAddr(); // 로그에 남기기 위해 요청으로부터 값 추출
		guestbookService.deleteGuestbook(id, clientIp);
		return "redirect:list";
	}
}