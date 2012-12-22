package com.jcm.htmpbean.tagparser;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

public class TagParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void testParser() {
		SqlSessionFactoryBuilder factoryBuilder=new SqlSessionFactoryBuilder();
		SqlSessionFactory sqlSessionFactory= factoryBuilder.build(TagParserTest.class.getResourceAsStream("/ibatisconf/configuration.xml"));
		SqlSession session = sqlSessionFactory.openSession();
		try {
		Object o=session.selectOne("selectRegexpUrl","http://dealer.autohome.com.cn/3969/info.html");
		System.out.println(o);
		} finally {
		session.close();
		}
	}

}
