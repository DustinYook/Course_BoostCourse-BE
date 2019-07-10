/** DispatcherServlet이 읽어들일 설정파일 */
package kr.or.connect.guestbook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration // 설정파일
@EnableWebMvc // 웹 필요 Bean 자동설정
@ComponentScan(basePackages = {"kr.or.connect.guestbook.controller"}) // 컨트롤러 등 자동 읽기
public class WebMvcContextConfiguration extends WebMvcConfigurerAdapter
{
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) 
	{
		// 리소스 요청 시 해당 폴더에서 읽어오도록 처리위한 코드 
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/META-INF/resources/webjars/").setCachePeriod(31556926);
        registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(31556926);
        registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(31556926);
        registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);
    }
    
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) 
    {
    	// DefaultServletHandler를 사용하도록 설정
    	// 매핑정보가 없는 URL요청이 들어올 때 WAS의 DefaultServlet이 static리소스를 읽어 보여주게 함
        configurer.enable(); 
    }
   
    @Override
    public void addViewControllers(final ViewControllerRegistry registry) 
    {
    	// 특정 URL에 대한 처리를 컨트롤러 클래스를 작성하지 않고 매핑할 수 있도록 해줌
    	System.out.println("addViewControllers가 호출됩니다. ");
    	// 요청이 '/'로 들어오면 main이라는 뷰로 보여주도록 설정, 이후 ViewResolver가 뷰 이름을 이용하여 적절한 뷰를 찾아냄
        registry.addViewController("/").setViewName("index");
    }
    
    @Bean
    public InternalResourceViewResolver getInternalResourceViewResolver() 
    {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/"); // prefix 지정
        resolver.setSuffix(".jsp"); // suffix 지정
        return resolver;  // '/WEB-INF/views/뷰 이름.jsp'로 변환한 결과를 리턴
    }
}