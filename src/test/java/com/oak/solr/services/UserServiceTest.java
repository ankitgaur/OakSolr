package com.oak.solr.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

import com.oak.solr.entities.User;

public class UserServiceTest {

	private static String id = null;

	@Test
	public void runTestCases() throws SolrServerException, IOException {

		//testUpdate("0f1aa559-5fe8-4cc5-936d-34585d66f719");
		
		//testAdd();

		//testSearchById(id);

		// testListAll();

		/*
		 * if (id != null) {
		 * 
		 * testSearchById(id);
		 * 
		 * testUpdate(id);
		 * 
		 * testAddTag(id);
		 * 
		 * testAddGroup(id);
		 * 
		 * testIncrPoints(id);
		 * 
		 * testSearchById(id);
		 * 
		 * testDelete();
		 * 
		 * testListAll(); }
		 */
	}

	public void testListAll() throws SolrServerException, IOException {

		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();
		List<User> users = UserService.listAll(0).getResults();

		for (User user : users) {
			System.out.println(user);
		}

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testDelete() throws SolrServerException, IOException {

		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		List<User> users = UserService.listAll(0).getResults();
		for (User user : users) {
			UserService.delete(user.getId());
		}

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testSearchById(String id) throws SolrServerException, IOException {
		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		System.out.println(UserService.searchById(id).getResults().get(0));

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testAddTag(String id) throws SolrServerException, IOException {
		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		ArrayList<String> list2 = new ArrayList<String>();
		list2.add("newtag1");
		list2.add("newtag2");

		UserService.addTags(id, list2);

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testAddGroup(String id) throws SolrServerException, IOException {
		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		ArrayList<String> list2 = new ArrayList<String>();
		list2.add("newgrp1");
		list2.add("newgrp2");

		UserService.addGroups(id, list2);

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testUpdate(String id) throws SolrServerException, IOException {
		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		UserService.update(id, "status", UserService.ACTIVATED);

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testAdd() throws SolrServerException, IOException {

		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		ArrayList<String> groups = new ArrayList<String>();
		groups.add("registered");
		groups.add("admin");

		// for(int i = 0; i<5;i++){
		User user = new User();
		user.setName("Ifedayo Oluwadamilola");
		user.setUsername("ifedayo");
		user.setEmail("sureife@gmail.com");
		user.setPassword("dGVzdDEyMw==");
		user.setStatus(UserService.ACTIVATED);
		// user.setTags(list);
		user.setGroups(groups);
		user.setCreatedon(new Date().getTime());
		id = UserService.add(user.createSolrInputDoc());
		// }

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");

	}

	public void testIncrPoints(String id) throws SolrServerException, IOException {
		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		UserService.incrPoints(id, 1);
		UserService.incrPointsRedeemed(id, 1);

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

}
