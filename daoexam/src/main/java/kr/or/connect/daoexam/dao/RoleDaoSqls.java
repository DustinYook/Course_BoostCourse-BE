package kr.or.connect.daoexam.dao;

public class RoleDaoSqls // 쿼리문을 가지고 있는 객체
{
	// 상수는 모두 대문자로 쓰는 것이 관례, 두 단어 이상인 경우 언더바로 구분
	// 쿼리문 적을 때 SELECT부분에 *를 쓰는 것보다 속성명을 나열하는 것이 의미가 명확히 전달될 수 있음
	// :description 부분 : 파라미터로 가져온 값으로 바인딩 (?과 동일)
	
	/** SELECT_ALL */
	public static final String SELECT_ALL = "SELECT role_id, description FROM role ORDER BY role_id";
	
	/** UPDATE */
	public static final String UPDATE = "UPDATE role SET description = :description WHERE role_id = :roleId";
	
	/** SELECT_BY_ROLE_ID */
	public static final String SELECT_BY_ROLE_ID = "SELECT role_id, description FROM role WHERE role_id = :roleId";
	
	/** DELETE_BY_ROLE_ID */
	public static final String DELETE_BY_ROLE_ID = "DELETE FROM role WHERE role_id = :roleId";
}