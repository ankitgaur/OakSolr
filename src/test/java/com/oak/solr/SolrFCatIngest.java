package com.oak.solr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.solr.client.solrj.SolrServerException;

import com.oak.solr.entities.Content;
import com.oak.solr.services.ContentService;

public class SolrFCatIngest {

	public static void main(String[] args) throws IOException, SolrServerException {
		File file = new File(".");
		
		System.out.println(file.getAbsolutePath());
		
		BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/fcat.dat"));
		
		String line = reader.readLine();
		
		int count = 0;
		
		while(line!=null){
			count++;
			
			String []arr = line.split("\\|");
			
			Content content = new Content();
			content.setContent_type("forum_categories");
			content.setName(arr[1].trim());
			content.setDescription("");
			//content.setStatus(ContentService.NEW);
			content.setCreatedon(new Date().getTime());

			String id = ContentService.add(content.createSolrInputDoc());
			
			System.out.println(id+"|"+line.trim());
			
			line = reader.readLine();
		}
		
		reader.close();
		System.out.println("Processed "+ count + " Forum categories");

	}

}
