package com.oak.solr.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.codehaus.jackson.map.ObjectMapper;

public class User {

	private String id;
	private String name;
	private String username;
	private String status;
	private String intro;
	private String email;
	private String password;
	private String jwt;
	private String createdby;
	private long createdon;
	private String updatedby;
	private long updatedon;
	private long points;
	private long points_redeemed;
	private String image_id;
	private List<String> groups;
	private int rating;
	private List<String> tags;

	@Override
	public String toString() {

		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (IOException e) {
			// TODO : Logging
		}

		return null;
	}

	public User() {

	}
	
	public User(SolrDocument doc) {
		super();
		if (doc.getFieldValue("id") != null) {
			this.id = doc.getFieldValue("id").toString();
		}

		if (doc.getFieldValue("status") != null) {
			this.status = doc.getFieldValue("status").toString();
		}

		if (doc.getFieldValue("username") != null) {
			this.username = doc.getFieldValue("username").toString();
		}
		
		if (doc.getFieldValue("email") != null) {
			this.email = doc.getFieldValue("email").toString();
		}
		
		if (doc.getFieldValue("password") != null) {
			this.password = doc.getFieldValue("password").toString();
		}
		
		if (doc.getFieldValue("jwt") != null) {
			this.jwt = doc.getFieldValue("jwt").toString();
		}

		if (doc.getFieldValue("createdby") != null) {
			this.createdby = doc.getFieldValue("createdby").toString();
		}

		if (doc.getFieldValue("createdon") != null) {
			this.createdon = (long) doc.getFieldValue("createdon");
		}

		if (doc.getFieldValue("points") != null) {
			this.points = (long) doc.getFieldValue("points");
		}
		
		if (doc.getFieldValue("points_redeemed") != null) {
			this.points_redeemed = (long) doc.getFieldValue("points_redeemed");
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

		if (doc.getFieldValues("groups") != null && doc.getFieldValues("groups").size() > 0) {
			this.groups = new ArrayList<String>();
			for (Object value : doc.getFieldValues("groups")) {
				groups.add(value.toString());
			}

		}

		if (doc.getFieldValue("rating") != null) {
			this.rating = (int) doc.getFieldValue("rating");
		}

		if (doc.getFieldValues("tags") != null && doc.getFieldValues("tags").size() > 0) {
			this.tags = new ArrayList<String>();
			for (Object value : doc.getFieldValues("tags")) {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
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

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	public long getPoints_redeemed() {
		return points_redeemed;
	}

	public void setPoints_redeemed(long points_redeemed) {
		this.points_redeemed = points_redeemed;
	}

	public String getImage_id() {
		return image_id;
	}

	public void setImage_id(String image_id) {
		this.image_id = image_id;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public SolrInputDocument createSolrInputDoc() {

		SolrInputDocument solrdoc = new SolrInputDocument();

		if (id != null) {
			solrdoc.addField("id", id);
		}

		if (username != null) {
			solrdoc.addField("username", username);
		}
		
		if (email != null) {
			solrdoc.addField("email", email);
		}
		
		if (password != null) {
			solrdoc.addField("password", password);
		}
		
		if (jwt != null) {
			solrdoc.addField("jwt", jwt);
		}

		if (createdby != null) {
			solrdoc.addField("createdby", createdby);
		}

		if (createdon != 0) {
			solrdoc.addField("createdon", createdon);
		}

		if (points != 0) {
			solrdoc.addField("points", points);
		}
		
		if (points_redeemed != 0) {
			solrdoc.addField("points_redeemed", points_redeemed);
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

		if (groups != null) {
			solrdoc.addField("groups", groups);
		}

		if (rating != 0) {
			solrdoc.addField("rating", rating);
		}

		if (rating != 0) {
			solrdoc.addField("rating", rating);
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

		return solrdoc;

	}

}
