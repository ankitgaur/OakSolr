package com.oak.solr.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

import com.oak.solr.entities.Content;
import com.oak.solr.services.ContentService;

public class ContentServiceTest {

	private static String id = null;

	@Test
	public void runTestCases() throws SolrServerException, IOException {

		
		testDelete("ce14cd43-67d9-4c19-a45c-705ce8ad2f35");
		
		//testUpdate("2f97ea92-9d43-4e54-a4da-95bf9243fddc");
		
		/*testAdd();

		testSearchByTypeAndTag();

		testSearchByType();

		if (id != null) {

			testUpdate(id);

			testAddTag(id);

			testIncrHits(id);

			testSearchById(id);

			testDelete(id);

			testSearchById(id);
		}*/
	}

	public void testDelete(String id) throws SolrServerException, IOException {

		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		ContentService.delete(id);

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testSearchById(String id) throws SolrServerException, IOException {
		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		System.out.println(ContentService.searchById(id).getResults().get(0));

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testSearchByTypeAndTag() throws SolrServerException, IOException {

		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();
		ArrayList<String> list = new ArrayList<String>();
		list.add("Entertainment");
		list.add("testtag");

		List<Content> articles = ContentService.searchByTypeAndTag("test_article", list, 0).getResults();

		for (Content article : articles) {
			System.out.println(article);
		}

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testSearchByType() throws SolrServerException, IOException {

		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();
		List<Content> articles = ContentService.searchByType("test_article", 0).getResults();

		for (Content article : articles) {
			System.out.println(article);
		}

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

		ContentService.addTags(id, list2);

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testUpdate(String id) throws SolrServerException, IOException {
		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		ContentService.update(id, "image_id", "f28a0ecd-77e1-4b7a-9a2f-36ae9852c8d6");

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

	public void testAdd() throws SolrServerException, IOException {

		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();
		ArrayList<String> list = new ArrayList<String>();
		list.add("Entertainment");
		list.add("testtag");

		Content content = new Content();
		content.setContent_type("test_article");
		content.setName("Test Article");
		content.setDescription("Test Article Description");
		content.setStatus(ContentService.NEW);
		content.setTags(list);
		content.setCreatedon(new Date().getTime());
		id = ContentService.add(content.createSolrInputDoc());

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");

	}

	public void testIncrHits(String id) throws SolrServerException, IOException {
		System.out.println("Starting " + Thread.currentThread().getStackTrace()[1]);
		long start = new Date().getTime();

		ContentService.incrHits(id);

		long end = new Date().getTime();

		long diff = end - start;
		System.out.println("Took " + diff + " ms");
	}

}
