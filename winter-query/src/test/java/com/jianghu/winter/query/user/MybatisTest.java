package com.jianghu.winter.query.user;

import com.alibaba.fastjson.JSON;
import com.jianghu.winter.query.core.PageList;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.Reader;
import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author daniel.hu
 * @date 2019/8/27 10:28
 */
@Slf4j
public class MybatisTest {

    private static SqlSessionFactory sqlSessionFactory;
    private SqlSession sqlSession;
    private UserMapper userMapper;

    @BeforeClass
    public static void init() throws Exception {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        try (Connection connection = sqlSessionFactory.openSession().getConnection()) {
            new ScriptRunner(connection).runScript(Resources.getResourceAsReader("import.sql"));
        }
    }

    @Before
    public void setUp() {
        sqlSession = sqlSessionFactory.openSession();
        userMapper = sqlSession.getMapper(UserMapper.class);
    }

    @After
    public void tearDown() {
        sqlSession.close();
    }

    @Test
    public void test_select() {
        List<UserEntity> entities = userMapper.query(UserQuery.builder().build());
        log.info("query response:\n{}", JSON.toJSONString(entities, true));
        assertEquals(4, entities.size());
    }

    @Test
    public void test_count() {
        assertEquals(4, userMapper.count(UserQuery.builder().build()));
    }


    @Test
    public void test_page() {
        UserQuery query = UserQuery.builder().build();
        query.setPageNumber(0);
        query.setPageSize(2);
        PageList<UserEntity> page = userMapper.page(query);
        log.info("page response:\n{}", JSON.toJSONString(page, true));
        assertEquals(4, page.total);
        assertEquals(2, page.getList().size());
    }

    @Test
    public void test_where() {
        List<UserEntity> entities = userMapper.query(UserQuery.builder().account("daniel").build());
        assertEquals(1, entities.size());
    }

    @Test
    public void test_getById() {
        UserEntity userEntity = userMapper.get(1);
        log.info("entity:\n{}", JSON.toJSONString(userEntity, true));
        assertEquals("daniel", userEntity.getAccount());
    }

    @Test
    public void test_delete() {
        userMapper.delete(1);
        assertNull(userMapper.get(1));
    }
}
