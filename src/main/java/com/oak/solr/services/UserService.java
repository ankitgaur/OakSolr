package com.oak.solr.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

import com.oak.solr.entities.User;
import com.oak.solr.vo.UserResponseVO;

public class UserService {

	private static String urlString = "http://dev.insodel.com/solr/user";
	public static final String NEW = "NEW";
	public static final String FORGOTPASSWORD = "FORGOTPASSWORD";
	public static final String DELETED = "DELETED";
	public static final String ACTIVATED = "ACTIVATED";

	public static String add(SolrInputDocument document) throws SolrServerException, IOException {

		String id = UUID.randomUUID().toString();

		document.addField("id", id);

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		try {
			UpdateResponse response = solr.add(document);

			if (response.getStatus() == 0) {

				solr.commit();
				solr.close();
			}
		} catch (Exception e) {
			// TODO : Logging
			return null;
		}

		return id;
	}

	public static UserResponseVO listAll(long start) throws SolrServerException, IOException {

		UserResponseVO uvo = null;
		List<User> cl = null;

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		SolrQuery query = new SolrQuery();
		try {
			query.set("q", "*:*");

			query.set("fq", "!status:" + DELETED);
			query.set("sort", "createdon desc");
			query.set("start", "" + start);

			QueryResponse response = solr.query(query);

			if (response.getStatus() == 0 && response.getResults().size() > 0) {

				uvo = new UserResponseVO();
				cl = new ArrayList<User>();

				uvo.setResults(cl);
				uvo.setStatus_code(0);
				uvo.setTotal(response.getResults().getNumFound());
				
				for (SolrDocument doc : response.getResults()) {
					if (doc != null) {
						cl.add(new User(doc));
					}
				}
				
				

			}
		} finally {
			solr.close();
		}

		return uvo;

	}

	public static void delete(String id) throws SolrServerException, IOException {

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		// create the document
		SolrInputDocument sdoc = new SolrInputDocument();
		sdoc.addField("id", id);
		Map<String, Object> fieldModifier = new HashMap<>(1);
		fieldModifier.put("set", DELETED);
		sdoc.addField("status", fieldModifier); // add the map as the field
												// value

		solr.add(sdoc); // send it to the solr server
		solr.commit();
		solr.close();

	}
	
	public static void deleteToken(String jwt) throws SolrServerException, IOException {

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		// create the document
		SolrInputDocument sdoc = new SolrInputDocument();
		
		UserResponseVO uvo = searchByToken(jwt);
		if(uvo!=null && uvo.getResults()!=null && uvo.getResults().size()>0){
			User user = uvo.getResults().get(0);
			
			update(user.getId(),"jwt",null);

			solr.add(sdoc); // send it to the solr server
			solr.commit();
		}
		
		solr.close();

	}

	public static void incrPoints(String id, int points) throws SolrServerException, IOException {

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		// create the document
		SolrInputDocument sdoc = new SolrInputDocument();
		sdoc.addField("id", id);
		Map<String, Object> fieldModifier = new HashMap<>(1);
		fieldModifier.put("inc", points);
		sdoc.addField("points", fieldModifier); // add the map as the field
												// value

		solr.add(sdoc); // send it to the solr server
		solr.commit();
		solr.close();

	}

	public static void incrPointsRedeemed(String id, int points) throws SolrServerException, IOException {

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		// create the document
		SolrInputDocument sdoc = new SolrInputDocument();
		sdoc.addField("id", id);
		Map<String, Object> fieldModifier = new HashMap<>(1);
		fieldModifier.put("inc", points);
		sdoc.addField("points_redeemed", fieldModifier); // add the map as the
															// field value

		solr.add(sdoc); // send it to the solr server
		solr.commit();
		solr.close();

	}

	public static void update(String id, String field, String value) throws SolrServerException, IOException {

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		// create the document
		SolrInputDocument sdoc = new SolrInputDocument();
		sdoc.addField("id", id);
		Map<String, Object> fieldModifier = new HashMap<>(1);
		fieldModifier.put("set", value);
		sdoc.addField(field, fieldModifier); // add the map as the field value

		solr.add(sdoc); // send it to the solr server
		solr.commit();
		solr.close();

	}

	public static void addTags(String id, List<String> values) throws SolrServerException, IOException {

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		// create the document
		SolrInputDocument sdoc = new SolrInputDocument();
		sdoc.addField("id", id);
		Map<String, Object> fieldModifier = new HashMap<>(1);
		fieldModifier.put("add", values);
		sdoc.addField("tags", fieldModifier); // add the map as the field value

		solr.add(sdoc); // send it to the solr server
		solr.commit();
		solr.close();

	}

	public static void addGroups(String id, List<String> values) throws SolrServerException, IOException {

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		// create the document
		SolrInputDocument sdoc = new SolrInputDocument();
		sdoc.addField("id", id);
		Map<String, Object> fieldModifier = new HashMap<>(1);
		fieldModifier.put("add", values);
		sdoc.addField("groups", fieldModifier); // add the map as the field
												// value

		solr.add(sdoc); // send it to the solr server
		solr.commit();
		solr.close();

	}

	public static UserResponseVO searchById(String id) throws SolrServerException, IOException {

		UserResponseVO uvo = null;

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		SolrQuery query = new SolrQuery();

		try {
			query.set("q", "id:" + id);

			QueryResponse response = solr.query(query);

			if (response.getStatus() == 0 && response.getResults().size() > 0) {

				SolrDocument doc = response.getResults().get(0);
				solr.close();
				if (doc != null) {

					uvo = new UserResponseVO();
					uvo.setStatus_code(response.getStatus());
					uvo.setTotal(1);

					List<User> results = new ArrayList<User>();
					uvo.setResults(results);
					
					results.add(new User(doc));

				}
			}
		} finally {
			solr.close();
		}

		return uvo;

	}
	
	public static UserResponseVO searchByToken(String jwt) throws SolrServerException, IOException {

		UserResponseVO uvo = null;

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		SolrQuery query = new SolrQuery();

		try {
			query.set("q", "jwt:" + jwt);

			QueryResponse response = solr.query(query);

			if (response.getStatus() == 0 && response.getResults().size() > 0) {

				SolrDocument doc = response.getResults().get(0);
				solr.close();
				if (doc != null) {

					uvo = new UserResponseVO();
					uvo.setStatus_code(response.getStatus());
					uvo.setTotal(1);

					List<User> results = new ArrayList<User>();
					uvo.setResults(results);
					
					results.add(new User(doc));

				}
			}
		} finally {
			solr.close();
		}

		return uvo;

	}
	
	public static UserResponseVO searchByEmail(String email) throws SolrServerException, IOException {

		UserResponseVO uvo = null;

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		SolrQuery query = new SolrQuery();

		try {
			query.set("q", "email:" + email);

			QueryResponse response = solr.query(query);

			if (response.getStatus() == 0 && response.getResults().size() > 0) {

				SolrDocument doc = response.getResults().get(0);
				solr.close();
				if (doc != null) {

					uvo = new UserResponseVO();
					uvo.setStatus_code(response.getStatus());
					uvo.setTotal(1);

					List<User> results = new ArrayList<User>();
					uvo.setResults(results);
					
					results.add(new User(doc));

				}
			}
		} finally {
			solr.close();
		}

		return uvo;

	}

}
