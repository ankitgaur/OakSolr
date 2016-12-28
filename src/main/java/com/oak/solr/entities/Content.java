package com.oak.solr.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.codehaus.jackson.map.ObjectMapper;

public class Content {

	private String id;
	private String status;
	private String content_type;
	private String createdby;
	private long createdon;
	private String createdondt;
	private String description;
	private long hits;
	private String image_id;
	private String intro;
	private String name;
	private String author;
	private List<String> pages;
	private String parent_id;
	private String parent_name;
	private int rating;
	private List<String> sections;
	private List<String> tags;
	private String updatedby;
	private long updatedon;

	@Override
	public String toString() {

		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (IOException e) {
			// TODO : Logging
		}

		return null;
	}

	public Content(SolrDocument doc) {
		super();
		if (doc.getFieldValue("id") != null) {
			this.id = doc.getFieldValue("id").toString();
		}

		if (doc.getFieldValue("status") != null) {
			this.status = doc.getFieldValue("status").toString();
		}
		
		if (doc.getFieldValue("author") != null) {
			this.author = doc.getFieldValue("author").toString();
		}

		if (doc.getFieldValue("content_type") != null) {
			this.content_type = doc.getFieldValue("content_type").toString();
		}

		if (doc.getFieldValue("createdby") != null) {
			this.createdby = doc.getFieldValue("createdby").toString();
		}

		if (doc.getFieldValue("createdon") != null) {
			this.createdon = (long) doc.getFieldValue("createdon");
		}

		if (doc.getFieldValue("createdondt") != null) {
			this.createdondt = doc.getFieldValue("createdondt").toString();
		}

		if (doc.getFieldValue("description") != null) {
			this.description = doc.getFieldValue("description").toString();
		}

		if (doc.getFieldValue("hits") != null) {
			this.hits = (long) doc.getFieldValue("hits");
		}

		if (doc.getFieldValue("image_id") != null) {
			this.image_id = doc.getFieldValue("image_id").toString();
		}

		if (doc.getFieldValue("intro") != null) {
			this.intro = doc.getFieldValue("intro").toString();
		}

		if (doc.getFieldValue("name") != null) {
			this.name = doc.getFieldValue("name").toString();
		}

		if (doc.getFieldValues("pages") != null && doc.getFieldValues("pages").size()>0) {
			this.pages = new ArrayList<String>();
			for(Object value : doc.getFieldValues("pages")){
				pages.add(value.toString());
			}
			
		}

		if (doc.getFieldValue("parent_id") != null) {
			this.parent_id = doc.getFieldValue("parent_id").toString();
		}

		if (doc.getFieldValue("parent_name") != null) {
			this.parent_name = doc.getFieldValue("parent_name").toString();
		}

		if (doc.getFieldValue("rating") != null) {
			this.rating = (int) doc.getFieldValue("rating");
		}
		
		if (doc.getFieldValues("sections") != null && doc.getFieldValues("sections").size()>0) {
			this.sections = new ArrayList<String>();
			for(Object value : doc.getFieldValues("sections")){
				sections.add(value.toString());
			}
			
		}
		
		if (doc.getFieldValues("tags") != null && doc.getFieldValues("tags").size()>0) {
			this.tags = new ArrayList<String>();
			for(Object value : doc.getFieldValues("tags")){
				tags.add(value.toString());
			}
			
		}

		if (doc.getFieldValue("updatedby") != null) {
			this.updatedby = doc.getFieldValue("updatedby").toString();
		}

		if (doc.getFieldValue("updatedon") != null) {
			this.updatedon = (long) doc.getFieldValue("updatedon");
		}
	}

	public Content() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public long getCreatedon() {
		return createdon;
	}

	public void setCreatedon(long createdon) {
		this.createdon = createdon;
	}

	public String getCreatedondt() {
		return createdondt;
	}

	public void setCreatedondt(String createdondt) {
		this.createdondt = createdondt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getHits() {
		return hits;
	}

	public void setHits(long hits) {
		this.hits = hits;
	}

	public String getImage_id() {
		return image_id;
	}

	public void setImage_id(String image_id) {
		this.image_id = image_id;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getParent_name() {
		return parent_name;
	}

	public void setParent_name(String parent_name) {
		this.parent_name = parent_name;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getUpdatedby() {
		return updatedby;
	}

	public void setUpdatedby(String updatedby) {
		this.updatedby = updatedby;
	}

	public long getUpdatedon() {
		return updatedon;
	}

	public void setUpdatedon(long updatedon) {
		this.updatedon = updatedon;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getPages() {
		return pages;
	}

	public void setPages(List<String> pages) {
		this.pages = pages;
	}

	public List<String> getSections() {
		return sections;
	}

	public void setSections(List<String> sections) {
		this.sections = sections;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public SolrInputDocument createSolrInputDoc() {

		SolrInputDocument solrdoc = new SolrInputDocument();

		if (id != null) {
			solrdoc.addField("id", id);
		}

		if (content_type != null) {
			solrdoc.addField("content_type", content_type);
		}

		if (createdby != null) {
			solrdoc.addField("createdby", createdby);
		}

		if (createdon != 0) {
			solrdoc.addField("createdon", createdon);
		}

		if (createdondt != null) {
			solrdoc.addField("createdondt", createdondt);
		}

		if (description != null) {
			solrdoc.addField("description", description);
		}

		if (hits != 0) {
			solrdoc.addField("hits", hits);
		}

		if (image_id != null) {
			solrdoc.addField("image_id", image_id);
		}

		if (intro != null) {
			solrdoc.addField("intro", intro);
		}

		if (name != null) {
			solrdoc.addField("name", name);
		}

		if (pages != null) {
			solrdoc.addField("pages", pages);
		}

		if (parent_id != null) {
			solrdoc.addField("parent_id", parent_id);
		}

		if (parent_name != null) {
			solrdoc.addField("parent_name", parent_name);
		}

		if (rating != 0) {
			solrdoc.addField("rating", rating);
		}

		if (rating != 0) {
			solrdoc.addField("rating", rating);
		}

		if (sections != null) {
			solrdoc.addField("sections", sections);
		}

		if (tags != null) {
			solrdoc.addField("tags", tags);
		}

		if (updatedby != null) {
			solrdoc.addField("updatedby", updatedby);
		}

		if (updatedon != 0) {
			solrdoc.addField("updatedon", updatedon);
		}

		if (status != null) {
			solrdoc.addField("status", status);
		}
		
		if (author != null) {
			solrdoc.addField("author", author);
		}

		return solrdoc;

	}

}
