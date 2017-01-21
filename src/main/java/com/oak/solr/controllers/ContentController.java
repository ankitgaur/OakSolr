package com.oak.solr.controllers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.oak.solr.entities.Content;
import com.oak.solr.entities.User;
import com.oak.solr.services.ContentService;
import com.oak.solr.services.UserService;
import com.oak.solr.vo.ContentResponseVO;

@RestController
public class ContentController {

	@CrossOrigin
	@RequestMapping(value = "/content/{id}", produces = "application/json", method = RequestMethod.GET)
	public ContentResponseVO getContentById(@PathVariable String id) throws SolrServerException, IOException {
		
		try{
			ContentService.incrHits(id);
		}
		catch(Exception e){
			//TODO : Logging
		}
		
		return ContentService.searchById(id);
	}

	@CrossOrigin
	@RequestMapping(value = "/image/{id}", produces = "application/json", method = RequestMethod.GET)
	public byte[] getImageById(@PathVariable String id) throws SolrServerException, IOException {

		String encoded = ContentService.searchById(id).getResults().get(0).getDescription();
		byte[] content = Base64.getDecoder().decode(encoded);
		return content;
	}
	
	@CrossOrigin
	@RequestMapping(value = "/image/{id}/{size}", produces = "application/json", method = RequestMethod.GET)
	public byte[] getImageByIdSize(@PathVariable String id,@PathVariable String size) throws SolrServerException, IOException {

		//wxh
		
		String []arr = size.split("x");
		
		int w = Integer.parseInt(arr[0].trim());
		int h = Integer.parseInt(arr[1].trim());

		String encoded = ContentService.searchById(id).getResults().get(0).getDescription();
		byte[] content = Base64.getDecoder().decode(encoded);
		
		BufferedImage resizedImage = new BufferedImage(w, h,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(ImageIO.read(new ByteArrayInputStream(content)), 0, 0, w, h, null);
		g.dispose();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resizedImage, "png", baos );
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		
		return imageInByte;

	}

	@CrossOrigin
	@RequestMapping(value = "/content/type/{type}", produces = "application/json", method = RequestMethod.GET)
	public ContentResponseVO getContentByTypeAndTags(@PathVariable String type,
			@RequestParam(name = "tags", required = false) String tags,
			@RequestParam(name = "start", required = false) Integer start) throws SolrServerException, IOException {

		if(start==null){
			start=0;
		}
		
		//System.out.println("tags = "+tags);
		
		if (tags == null || tags.isEmpty()) {
			return ContentService.searchByType(type, start);
		}

		return ContentService.searchByTypeAndTag(type, new ArrayList<String>(Arrays.asList(tags.split(" "))), start);
	}

	@CrossOrigin
	@RequestMapping(value = "/content/popular/{type}", produces = "application/json", method = RequestMethod.GET)
	public ContentResponseVO getPopularContentByTypeAndTags(@PathVariable String type,
			@RequestParam(name = "tags", required = false) String tags,
			@RequestParam(name = "start", required = false) Integer start) throws SolrServerException, IOException {

		if(start==null){
			start=0;
		}
		
		if (tags == null || tags.isEmpty()) {
			return ContentService.searchPopularByType(type, start);
		}
		
		return ContentService.searchPopularByTypeAndTag(type, new ArrayList<String>(Arrays.asList(tags.split(" "))),
				start);
	}

	@CrossOrigin
	@RequestMapping(value = "/content/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> deleteContent(@PathVariable("id") String id) throws SolrServerException, IOException {
		ContentService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@CrossOrigin
	@RequestMapping(value = "/content", method = RequestMethod.POST)
	public ResponseEntity<Void> createArticle(@RequestParam("tags") String tags, @RequestParam("title") String title,
			@RequestParam("content_type") String content_type, @RequestParam("content") String content,
			@RequestParam("intro") String intro,
			@RequestParam(name = "displayImage", required = false) MultipartFile displayImage)
			throws ParseException, IOException, SolrServerException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();

		User user = UserService.searchById(id).getResults().get(0);

		List<String> taglist = null;

		if (tags != null && !tags.isEmpty()) {
			taglist = new ArrayList<String>(Arrays.asList(tags.split(" ")));
		}

		String image_id = null;

		if (displayImage != null && displayImage.getBytes() != null && displayImage.getBytes().length > 0) {
			image_id = ContentService.uploadImage(user, taglist, displayImage);
		}

		Content ct = new Content();
		ct.setContent_type(content_type);
		ct.setName(title);
		ct.setDescription(content);
		ct.setIntro(intro);
		ct.setCreatedby(user.getId());
		ct.setAuthor(user.getUsername());
		ct.setCreatedon(new Date().getTime());
		ct.setTags(taglist);
		ct.setImage_id(image_id);

		ContentService.add(ct.createSolrInputDoc());
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@CrossOrigin
	@RequestMapping(value = "/image", method = RequestMethod.POST)
	public ResponseEntity<Void> uploadImage(@RequestParam("tags") String tags,
			@RequestParam(name = "displayImage", required = true) MultipartFile displayImage)
			throws ParseException, IOException, SolrServerException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();

		User user = UserService.searchById(id).getResults().get(0);

		List<String> taglist = null;

		if (tags != null && !tags.isEmpty()) {
			taglist = new ArrayList<String>(Arrays.asList(tags.split(" ")));
		}

		ContentService.uploadImage(user, taglist, displayImage);

		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@CrossOrigin
	@RequestMapping(value = "/content/{id}", method = RequestMethod.POST)
	public ResponseEntity<Void> updateArticle(@PathVariable("id") String id, @RequestParam(name = "tags", required = false) String tags, @RequestParam(name = "title", required = false) String title,
			 @RequestParam(name = "content", required = false) String content,
			@RequestParam(name = "intro", required = false) String intro,
			@RequestParam(name = "displayImage", required = false) MultipartFile displayImage) throws ParseException, SolrServerException, IOException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String uid = authentication.getName();

		User user = UserService.searchById(uid).getResults().get(0);
		
		ContentService.searchById(id);
		
		if(tags!=null){
			String []tarr = tags.split(" ");
			
			if(tarr!=null && tarr.length > 0){
				ContentService.update(id, "tags", new ArrayList<String>(Arrays.asList(tags.split(" "))));
			}
			
			
		}
		
		else if(title!=null){
			ContentService.update(id, "name", title);
		}
		
		else if(content!=null){
			ContentService.update(id, "description", content);
		}
		
		else if(intro!=null){
			ContentService.update(id, "intro", intro);
		}
		
		else if(displayImage!=null){
			String image_id = null;
			
			Content c = ContentService.searchById(id).getResults().get(0);

			if (displayImage != null && displayImage.getBytes() != null && displayImage.getBytes().length > 0) {
				image_id = ContentService.uploadImage(user, c.getTags(), displayImage);
			}
			ContentService.update(id, "image_id", image_id);
		}

		return new ResponseEntity<Void>(HttpStatus.OK);

	}
	
	@CrossOrigin // (methods={RequestMethod.POST,RequestMethod.OPTIONS,RequestMethod.GET})
	@RequestMapping(value = "/ckimages", method = RequestMethod.POST)
	public String uploadCKImage(@RequestParam("upload") MultipartFile image, 
			HttpServletRequest request) throws IOException, SolrServerException {

		String funcNum = request.getParameter("CKEditorFuncNum");
		String tags = request.getParameter("tags");

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String id = authentication.getName();

		User user = UserService.searchById(id).getResults().get(0);

		List<String> taglist = null;

		if (tags != null && !tags.isEmpty()) {
			taglist = new ArrayList<String>(Arrays.asList(tags.split(" ")));
		}

		String imgid = ContentService.uploadImage(user, taglist, image);

		String imgurl = "api/image/" + imgid;

		String message = "Image was uploaded successfully";
		String resp = "<script type='text/javascript'>window.parent.CKEDITOR.tools.callFunction(" + funcNum + ", '"
				+ imgurl.toString().trim() + "', '" + message + "');</script>";

		return resp;
	}

	@CrossOrigin
	@RequestMapping(value = "/image/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] downloadPicture(@PathVariable String id) throws IOException, SolrServerException {
		byte[] content = getImageById(id);
		return content;
	}

	@CrossOrigin // (methods={RequestMethod.POST,RequestMethod.OPTIONS,RequestMethod.GET})
	@RequestMapping(value = "/ckimages", method = RequestMethod.GET)
	public String browseImages(HttpServletRequest request) throws SolrServerException, IOException {
		
		ContentResponseVO cvo = getContentByTypeAndTags("image", null, 0);
		
		String funcNum = request.getParameter("CKEditorFuncNum");
		
		StringBuilder htmlBuilder = new StringBuilder("<html><head>");

		htmlBuilder.append("<style> div { font-size: 0; }");

		htmlBuilder.append(
				" a { font-size: 16px; display: inline-block; margin-bottom: 8px; width: calc(50% - 4px); margin-right: 8px;}");

		htmlBuilder.append(" a:nth-of-type(2n) { margin-right: 0; }");

		htmlBuilder.append(
				" @media screen and (min-width: 50em) { a { width: calc(25% - 6px); } a:nth-of-type(2n) { margin-right: 8px; } a:nth-of-type(4n) { margin-right: 0; } }");

		htmlBuilder.append("</style><script>");

		htmlBuilder.append("function returnFileUrl(fileUrl) { var funcNum = ");
		htmlBuilder.append(funcNum);

		htmlBuilder.append("; window.opener.CKEDITOR.tools.callFunction( funcNum, fileUrl ); window.close();}");
		htmlBuilder.append("</script></head><body>");

		String imgurl = "image/";

		for (Content img : cvo.getResults()) {
			String thisimgurl = imgurl + img.getId();
			htmlBuilder.append("<div><a onclick='returnFileUrl(\"" + thisimgurl + "\")'><figure><img src='" + thisimgurl
					+ "' style='width: 300px;'></figure></a></div>");

		}

		htmlBuilder.append("</body></html>");
		return  htmlBuilder.toString();
	}
	

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody String handleException(Exception ex) {
		if (ex.getMessage().contains("UNIQUE KEY"))
			return "The submitted item already exists.";
		else
			System.out.println(this.getClass() + ": need handleException for: " + ex.getMessage());
		return ex.getMessage();
	}

}
