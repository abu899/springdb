# 데이터 접근 활용 기술

## JdbcTemplate

- JdbcTemplate
- NamedJdbcTemplate
    - 이름 기반 파라미터 바인딩
- SimpleJdbcInsert
- SimpleJdbcCall

`JdbcTemplate`은 간단하고 실용적으로 SQL 을 사용할 때 사용하면 된다. 하지만, `동적 쿼리 문제`를 해결하지 못한다는 단점이 존재한다. 또한 SQL 을 자바 String 으로 직접 작성해야하기 때문에
띄어쓰기 같은 부분에 주의를 기울여야 한다.

## Test in @Transactional

<p align="center"><img src="./img/transactional_in_test.png" width="80%"></p>

`@Transactional` 어노테이션은 정상적으로 로직이 동작하면 트랜잭션을 커밋하게 된다. 하지만 테스트에서의 `@Transactional`은 테스트가 끝나면 트랜잭션을 자동으로 롤백시켜 버린다. 따라서
트랜잭션 범위 안에서 테스트를 진행하기 때문에 다른 테스트간에 영향을 주지 않으며, 커밋을 하지 않기 때문에 데이터가 자동으로 롤백되어 편리함이 증진된다.

> 만약 테스트에서 데이터가 제대로 저장되는지 보고 싶을때는 @Commit 어노테이션을 붙여줌으로써 확인할 수 있다.

### Embedded DB in Spring

임베디드 모드로 DB 를 만들어서 테스트를 하면 굳이 DB를 띄우지 않고도 테스트가 가능하다. 스프링에서는 테스트의 `application.properties`에 DB 설정 정보가 따로 없다면 임베디드 모드로
접근하는 `DataSource`를 만들어서 제공한다.

```properties
spring.profiles.active=test
# spring.datasource.url=jdbc:h2:tcp://localhost/~/test
# spring.datasource.username=sa
# spring.datasource.password=
logging.level.org.springframework.jdbc=debug
```

## MyBatis

JdbcTemplate 보다 더 많은 기능을 제공하는 `SQL Mapper`이다. JdbcTemplate 에 비해 SQL 을 XML에 편리하게 작성할 수 있고, 동적 쿼리를 편리하게 작성할 수 있는 장점이 있다.
다만 스프링에 내장된 기능이 아니기 때문에 MyBatis 를 위한 별도의 설정이 필요하다.

```xml
<!--SQL query-->
<update id="update">
    update item
    set item_name=#{itemName},
    price=#{price},
    quantity=#{quantity}
    where id = #{id}
</update>
```
```xml
<!--동적 쿼리-->
<select id="findAll" resultType="Item">
    select id, item_name, price, quantity
    from item
    <where>
        <if test="itemName != null and itemName != ''">
            and item_name like concat('%',#{itemName},'%')
        </if>
        <if test="maxPrice != null">
            and price &lt;= #{maxPrice}
        </if>
    </where>
</select>
```

### MyBatis ItemMapper
MyBatis 인터페이스를 만들며 따로 구현체를 만들어주지 않았지만 정상적으로 동작한다. 어떻게 이렇게 될까?

<p align="center"><img src="./img/mybatis.png" width="80%"></p>

어플리케이션 로딩 시점에 MyBatis 스프링 연동 모듈은 `@Mapper` 어노테이션이 붙은 인터페이스를 찾는다.
찾은 인터페이스를 `동적 프록시` 기술을 이용해 `ItemMapper`구현체를 만들어 내고 이 구현체를 빈으로 등록한다.

- 즉, 매퍼 구현체로 스프링과 편리하게 연동가능하고, 추가적으로 예외 추상화도 함께 적용시켜준다.

## JPA (Java Persistence API)

JdbcTemplate 이나 MyBatis 같은 `SQL Mapper` 기술은 직접 SQL 을 개발자가 작성해야하지만,
JPA 는 `ORM데이터 접근 기술`을 이용하여 JPA 가 대신 작성하고 처리해준다. 즉, 객체와 DB 간의 패러다임 불일치를 해결해준다.
다만, JPA 단독으로만 사용시에는 동적 쿼리 작성에 어려운 점이 존재한다.

- 객체를 자바 컬렉션에 저장하듯이 DB 에 저장하게 도와준다
- ORM(Object Relational Mapping)
  - 객체는 객체대로 설계
  - 관계형 데이터베이스는 관계형 데이터베이스대로 설계
  - ORM 프레임워크가 중간에서 객체와 DB 를 매핑해준다

자세한 내용은 github 내 jpa_basic repo 를 보자!

### JPA 예외 변환

<p align="center"><img src="./img/jpa_ex.png" width="80%"></p>

JPA 에서 예외가 발생하는 경우, JPA 예외가 발생한다. 왜냐면 `EntityManager`의 경우 JPA 기술이고 스프링과는 관계가 없는 기술이기에
JPA 기술에 의존적인 예외가 발생하게 된다. JPA 예외는 `PersistenceException`과 그 하위 예외를 발생시킨다.
기술에 의존적이지 않은 예외를 발생시키려면 스프링 예외 추상화를 사용해야되는데 이를 어떻게 변환시킬까?

<p align="center"><img src="./img/jpa_ex_1.png" width="80%"></p>

- @Repository

`@Repository`는 컴포넌트 스캔의 대상이 되는 것 뿐만 아니라, `예외 변환 AOP의 적용 대상`이 된다.
즉, JPA 와 함께 사용하는 경우, 스프링은 JPA 예외 변환기를 등록하고 예외가 발생 시 예외 변환기를 통해 스프링 데이터 접근 예외로 변경 시킨다.