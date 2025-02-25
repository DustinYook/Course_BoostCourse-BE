package kr.or.connect.guestbook.config;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration // 설정파일
@EnableTransactionManagement // 트랜잭션 관련 설정 자동처리
public class DBConfig implements TransactionManagementConfigurer 
{
	// 사용자 간 트랜잭션 처리 위한 PlatformTransactionManager를 설정하는 방법
	// 1) TransactionManagementConfigurer 인터페이스 구현
	// 2) annotationDrivenTransactionManager() 메소드를 오버라이딩
	// 3) 2의 메소드에서 PlatformTransactionManager 객체를 반환
	
	/** 스프링에게 알려줘야 할 정보 */
	private String driverClassName = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://localhost:3306/connectdb?useUnicode=true&characterEncoding=utf8";
	private String username = "user";
	private String password = "connect123!@#";
	
	@Bean
	public DataSource dataSource()
	{
		// DB를 사용하기 위해 커넥션 관리하는 DataSource 객체 생성
		BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
	}

	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() 
	{
		return transactionManager();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager()
	{
		return new DataSourceTransactionManager(dataSource());
	}
}
