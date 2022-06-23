# 데이터 접근 활용 기술

## JdbcTemplate

- JdbcTemplate
- NamedJdbcTemplate
  - 이름 기반 파라미터 바인딩
- SimpleJdbcInsert
- SimpleJdbcCall

`JdbcTemplate`은 간단하고 실용적으로 SQL 을 사용할 때 사용하면 된다. 하지만, `동적 쿼리 문제`를 해결하지 못한다는 단점이 존재한다.
또한 SQL 을 자바 String 으로 직접 작성해야하기 때문에 띄어쓰기 같은 부분에 주의를 기울여야 한다.