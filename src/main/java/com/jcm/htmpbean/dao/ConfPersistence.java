package com.jcm.htmpbean.dao;

import java.io.File;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hsqldb.cmdline.SqlFile;


public class ConfPersistence {
	private static SqlSessionFactory sessionFactory;
	 static {
		SqlSessionFactoryBuilder factoryBuilder=new SqlSessionFactoryBuilder();
		sessionFactory= factoryBuilder.build(ConfPersistence.class.getResourceAsStream("/ibatisconf/configuration.xml"));
		SqlFile sqlFile ;
		try {
			sqlFile = new SqlFile(new File(ConfPersistence.class.getResource("/ibatisconf/script.sql").getPath()));
			sqlFile.setConnection(sessionFactory.openSession().getConnection());
			sqlFile.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 public  SqlSession openSession()
	 {
		 return sessionFactory.openSession(true);
	 }
}
