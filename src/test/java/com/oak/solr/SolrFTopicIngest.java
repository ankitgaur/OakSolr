package com.oak.solr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.solr.client.solrj.SolrServerException;

import com.oak.solr.entities.Content;
import com.oak.solr.services.ContentService;

public class SolrFTopicIngest {

	public static void main(String[] args) throws IOException, SolrServerException {
		File file = new File(".");
		
		System.out.println(file.getAbsolutePath());
		
		BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/topics.dat"));
		
		String line = reader.readLine();
		
		int count = 0;
		
		while(line!=null){
			count++;
			
			String []arr = line.split("\\|");
			
			Content content = new Content();
			content.setContent_type("forum_topics");
			content.setName(arr[2].trim());
			content.setDescription(arr[3].trim());
			//content.setStatus(ContentService.NEW);
			content.setCreatedon(new Date().getTime());
			content.setCreatedby(arr[4].trim());
			content.setParent_id(arr[0].trim());
			content.setParent_name(arr[1].trim());

			String id = ContentService.add(content.createSolrInputDoc());
			
			System.out.println(id+"|"+line.trim());
			
			line = reader.readLine();
		}
		
		reader.close();
		System.out.println("Processed "+ count + " Forum Topics");

	}

}
