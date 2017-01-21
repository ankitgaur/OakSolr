package com.oak.solr.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
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
import org.springframework.web.multipart.MultipartFile;

import com.oak.solr.entities.Content;
import com.oak.solr.entities.User;
import com.oak.solr.vo.ContentResponseVO;

public class ContentService {

	private static String urlString = "http://dev.insodel.com/solr/content";
	public static final String NEW = "NEW";
	public static final String DELETED = "DELETED";

	public static String add(SolrInputDocument document) throws SolrServerException, IOException {
		
		document.addField("status", NEW);

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
			System.out.println(e + " : " + e.getMessage());
			return null;
		}

		return id;
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

	public static void incrHits(String id) throws SolrServerException, IOException {

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		// create the document
		SolrInputDocument sdoc = new SolrInputDocument();
		sdoc.addField("id", id);
		Map<String, Object> fieldModifier = new HashMap<>(1);
		fieldModifier.put("inc", 1);
		sdoc.addField("hits", fieldModifier); // add the map as the field value

		solr.add(sdoc); // send it to the solr server
		solr.commit();
		solr.close();

	}

	public static void update(String id, String field, Object value) throws SolrServerException, IOException {

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

	public static ContentResponseVO searchById(String id) throws SolrServerException, IOException {

		ContentResponseVO crvo = null;

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		try {
			SolrQuery query = new SolrQuery();
			query.set("q", "id:" + id);

			QueryResponse response = solr.query(query);

			if (response.getStatus() == 0 && response.getResults().size() > 0) {
				SolrDocument doc = response.getResults().get(0);
				if (doc != null) {
					crvo = new ContentResponseVO();
					crvo.setStatus_code(0);
					crvo.setTotal(1);
					List<Content> results = new ArrayList<Content>();
					crvo.setResults(results);
					results.add(new Content(doc));
				}
			}
		} finally {
			solr.close();
		}

		return crvo;

	}

	public static ContentResponseVO searchByTypeAndTag(String content_type, List<String> tags, long start)
			throws SolrServerException, IOException {

		ContentResponseVO crvo = null;
		List<Content> cl = null;

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		SolrQuery query = new SolrQuery();
		try {
			StringBuilder builder = new StringBuilder();

			if (content_type != null) {
				builder.append("content_type:" + content_type);
			}

			if (tags != null && !tags.isEmpty()) {
				for (String tag : tags) {
					if (builder.length() == 0) {
						builder.append("tags:" + tag);
					} else {
						builder.append(" AND tags:" + tag);
					}

				}
			}
			
			//System.out.println(builder.toString());

			query.set("q", builder.toString());

			query.set("fq", "!status:" + DELETED);
			query.set("sort", "createdon desc");
			query.set("start", "" + start);

			QueryResponse response = solr.query(query);

			if (response.getStatus() == 0 && response.getResults().size() > 0) {

				cl = new ArrayList<Content>();
				crvo = new ContentResponseVO();
				crvo.setStatus_code(response.getStatus());
				crvo.setTotal(response.getResults().getNumFound());

				crvo.setResults(cl);

				for (SolrDocument doc : response.getResults()) {
					if (doc != null) {
						cl.add(new Content(doc));
					}
				}

			}
		} finally {
			solr.close();
		}
		return crvo;

	}

	public static ContentResponseVO searchByTypeAndName(String content_type, String name, long start)
			throws SolrServerException, IOException {

		ContentResponseVO crvo = null;
		List<Content> cl = null;

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		SolrQuery query = new SolrQuery();
		try {
			query.set("q", "content_type:" + content_type+ " AND name:\""+name+"\"");

			query.set("fq", "!status:" + DELETED);
			query.set("sort", "createdon desc");
			query.set("start", "" + start);

			QueryResponse response = solr.query(query);

			if (response.getStatus() == 0 && response.getResults().size() > 0) {

				cl = new ArrayList<Content>();
				crvo = new ContentResponseVO();
				crvo.setStatus_code(response.getStatus());
				crvo.setTotal(response.getResults().getNumFound());

				crvo.setResults(cl);

				for (SolrDocument doc : response.getResults()) {
					if (doc != null) {
						cl.add(new Content(doc));
					}
				}

			}
		} finally {
			solr.close();
		}
		return crvo;

	}

	public static ContentResponseVO searchPopularByTypeAndTag(String content_type, List<String> tags, long start)
			throws SolrServerException, IOException {

		ContentResponseVO crvo = null;
		List<Content> cl = null;

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		SolrQuery query = new SolrQuery();
		try {
			StringBuilder builder = new StringBuilder();

			if (content_type != null) {
				builder.append("content_type:" + content_type);
			}

			if (tags != null && tags.isEmpty()) {
				for (String tag : tags) {
					if (builder.length() == 0) {
						builder.append("tags:" + tag);
					} else {
						builder.append(" AND tags:" + tag);
					}

				}
			}

			query.set("q", builder.toString());

			query.set("fq", "!status:" + DELETED);
			query.set("sort", "hits desc");
			query.set("start", "" + start);

			QueryResponse response = solr.query(query);

			if (response.getStatus() == 0 && response.getResults().size() > 0) {

				cl = new ArrayList<Content>();
				crvo = new ContentResponseVO();
				crvo.setStatus_code(response.getStatus());
				crvo.setTotal(response.getResults().getNumFound());

				crvo.setResults(cl);

				for (SolrDocument doc : response.getResults()) {
					if (doc != null) {
						cl.add(new Content(doc));
					}
				}

			}
		} finally {
			solr.close();
		}
		return crvo;

	}

	public static ContentResponseVO searchByType(String content_type, long start)
			throws SolrServerException, IOException {

		return searchByTypeAndTag(content_type, null, start);

	}

	public static ContentResponseVO searchPopularByType(String content_type, long start)
			throws SolrServerException, IOException {

		return searchPopularByTypeAndTag(content_type, null, start);

	}

	public static String uploadImage(User user, List<String> taglist, MultipartFile displayImage)
			throws IOException, SolrServerException {

		String id = null;

		String content = null;

		try {
			byte[] imgbytes = displayImage.getBytes();
			if (imgbytes != null && imgbytes.length > 0) {
				content = Base64.getEncoder().encodeToString(imgbytes);
			}
		} catch (NullPointerException npe) {
			// do nothing
		}

		Content ct = new Content();
		ct.setContent_type("image");
		ct.setName(displayImage.getName());
		ct.setDescription(content);

		ct.setCreatedby(user.getId());
		ct.setAuthor(user.getUsername());
		ct.setCreatedon(new Date().getTime());
		ct.setTags(taglist);

		id = add(ct.createSolrInputDoc());

		return id;

	}

}
