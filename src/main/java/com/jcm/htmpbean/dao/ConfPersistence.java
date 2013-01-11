package com.jcm.htmpbean.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hsqldb.cmdline.SqlFile;

import com.sun.org.apache.bcel.internal.generic.NEW;


public class ConfPersistence {
	private static SqlSessionFactory sessionFactory;
	private static SqlSession session;
	private static ThreadLocal<SqlSession> threadLocal=new ThreadLocal<SqlSession>();
	 static {
		SqlSessionFactoryBuilder factoryBuilder=new SqlSessionFactoryBuilder();
		sessionFactory= factoryBuilder.build(ConfPersistence.class.getResourceAsStream("/ibatisconf/configuration.xml"));
		session=sessionFactory.openSession(true);
		SqlFile sqlFile ;
		try {
			File file=new File("script.sql");
			FileWriter fileWriter=new FileWriter(file);
			InputStream InputStream=ConfPersistence.class.getResourceAsStream("/ibatisconf/script.sql");
		   InputStreamReader inputStreamReader=new InputStreamReader(InputStream);
		   BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
		   while(bufferedReader.ready())
		   {
			   String str=bufferedReader.readLine();
			   fileWriter.write(str);
			   System.out.println(str);
		   }
			bufferedReader.close();
			inputStreamReader.close();
			InputStream.close();
			fileWriter.close();
			sqlFile = new SqlFile(file);
			sqlFile.setConnection(session.getConnection());
			sqlFile.execute();
			file.deleteOnExit();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	 public  static SqlSession openSession()
	 {
		 SqlSession sqlSession=threadLocal.get();
		 if(sqlSession == null)
		 {
			 session=sessionFactory.openSession(true);
			 threadLocal.set(session);
		 }
		 return threadLocal.get();
	 }
	 public  static void closeSession()
	 {
		  session.close();
	 }
}
