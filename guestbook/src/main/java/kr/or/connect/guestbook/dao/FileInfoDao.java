package kr.or.connect.guestbook.dao;

import static kr.or.connect.guestbook.dao.FileInfoDaoSqls.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import kr.or.connect.guestbook.dto.FileInfo;

@Repository
public class FileInfoDao 
{
	private NamedParameterJdbcTemplate jdbc; // ? 대신 이름으로 바인딩 가능 (결과값 가져올 수도 있음)
    private SimpleJdbcInsert insertAction;
    private RowMapper<FileInfo> rowMapper = BeanPropertyRowMapper.newInstance(FileInfo.class);
	// BeanPropertyRowMapper객체를 통해 속성의 값을 자동으로 DTO에 담아주게 됨
	// DBMS에서는 단어와 단어를 구분할 때 언더바 사용 - ex) role_id
	// Java에서는 단어와 단어를 구분할 때 카멜 표기법을 사용 - ex) roleId
	// BeanPropertyRowMapper는 DBMS와 Java의 이름규칙을 맞춰주는 기능을 지님
    
    public FileInfoDao(DataSource dataSource) 
    {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
        this.insertAction = new SimpleJdbcInsert(dataSource)
                .withTableName("file_info")
                .usingGeneratedKeyColumns("file_id"); // ID가 자동으로 입력되게 하는 설정
    }

    /** INSERT */
	public Long insert(FileInfo fileInfo) 
	{
		// INSERT문 같은 경우, PK를 자동으로 생성해야 하는 경우도 존재 
		// 생성된 PK값을 다시 읽어오는 부분 필요  (SimpleJdbcInsert 객체가 대신 수행)
		// 일단 여기서는 PK값을 우리가 직접 넣음
		
		// Role객체에 있는 값을 Map으로 바꿔줌
		SqlParameterSource params = new BeanPropertySqlParameterSource(fileInfo);
		
		// executeAndReturnKey(params) : INSERT를 실행하고 자동 생성된 ID 값을 리턴
		return insertAction.executeAndReturnKey(params).longValue();
	}
	
	/** SELECT_ALL */
	public List<FileInfo> selectAll()
	{
		return jdbc.query(SELECT_ALL, Collections.emptyMap(), rowMapper); // 비어있는 map객체를 하나 선언
		// static import를 쓰면 패키지 명 없이 static 멤버를 사용할 수 있음
		// SQL문에 바인딩 할 것이 있는 경우 바인딩할 값을 전달할 목적으로 사용하는 객체
		// SELECT 건 당 정보를 DTO에 저장하는 목적으로 사용
		// query() : 결과가 여러 건이었을 때 내부적으로 반복하면서 DTO를 생성, 그 결과를 리스트에 담아줌
	}
	
	/** SELECT_BY_ID */
	public FileInfo selectById(Long fileId) 
	{
		try 
		{
			Map<String, ?> params = Collections.singletonMap("file_id", fileId);
			return jdbc.queryForObject(SELECT_BY_ID, params, rowMapper);
		}
		catch(EmptyResultDataAccessException e) // 조건에 맞는 값이 없는 경우
		{
			// 주의해서 사용
			return null; 
		} 
	}
}