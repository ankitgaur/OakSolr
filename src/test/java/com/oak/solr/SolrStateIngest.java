package com.oak.solr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.solr.client.solrj.SolrServerException;
import org.codehaus.jackson.map.ObjectMapper;

import com.oak.solr.citizenx.vo.StateVO;
import com.oak.solr.entities.Content;
import com.oak.solr.services.ContentService;

public class SolrStateIngest {

	public static void main(String[] args) throws IOException, SolrServerException {
		File file = new File(".");
		
		System.out.println(file.getAbsolutePath());
		
		BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/states.dat"));
		
		String line = reader.readLine();
		
		int count = 0;
		
		while(line!=null){
			count++;
			String []arr = line.split("\\|");
			
			ObjectMapper mapper = new ObjectMapper();
			String []govts = mapper.readValue(arr[5], String[].class);
			
			StateVO state = new StateVO();
			state.setAbbr(arr[1]);
			state.setName(arr[0]);
			state.setGovts(govts);
			state.setCapital(arr[4]);
			
			Content content = new Content();
			content.setContent_type("states");
			content.setName(state.getName());
			content.setDescription(state.toString());
			//content.setStatus(ContentService.NEW);
			content.setCreatedon(new Date().getTime());
			
			System.out.println(content);
			
			ContentService.add(content.createSolrInputDoc());
			
			line = reader.readLine();
		}
		
		reader.close();
		System.out.println("Processed "+ count + " states");

	}

}
