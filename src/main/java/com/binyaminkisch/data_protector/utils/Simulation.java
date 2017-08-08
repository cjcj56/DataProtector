package com.binyaminkisch.data_protector.utils;

import java.util.Random;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.binyaminkisch.data_protector.model.AppConfiguration;



public class Simulation {

	public static void main(String[] args) {
//		DataProtectorImpl dp = new DataProtectorImpl();
		Random r = new Random();
		int n = 10, limit = 99999;
		AppConfiguration[] confs = new AppConfiguration[n];
		for(int i = 0; i < n; ++i) {
			confs[i] = new AppConfiguration();
			confs[i].setConfigurationId(r.nextInt(limit));
			confs[i].setBlockSize(r.nextInt(limit));
			confs[i].setRootDir(""+r.nextInt(limit));
			confs[i].setHashAlgoritm(""+r.nextInt(limit));
			confs[i].setBlockSize(r.nextInt(limit));
			confs[i].setCharset(""+r.nextInt(limit));
			confs[i].setKeySizeBits(r.nextInt(limit));
		}
		
		
//		Configuration hibernateConf =  new Configuration();
//		Configuration hibernateConf2 = hibernateConf.configure();
//		System.out.println("is 'hibernateConf' and 'hibernateConf2' the same? : " + hibernateConf.equals(hibernateConf2));
//		SessionFactory sessionFactory = hibernateConf2.buildSessionFactory();
		SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		for (int i = 0; i < n; ++i) {
			session.save(confs[i]);
		}
		session.getTransaction().commit();
		session.close();
		sessionFactory.close();
		
//		session = sessionFactory.openSession();
//		session.
		
		
//		try {
//			Thread.currentThread().wait();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
