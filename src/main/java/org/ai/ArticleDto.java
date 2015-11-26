package org.ai;

import java.util.Date;

public class ArticleDto {
	
	private int id;
	private String title;
	private int userid;
	private String username;
	private String content;
	private Date pdate;
	private boolean enable;
	private String image;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public boolean getEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getPdate() {
		return pdate;
	}
	public void setPdate(Date pdate) {
		this.pdate = pdate;
	}
	@Override
	public String toString() {
		return "ArticleDto [id=" + id + ", title=" + title + ", userid=" + userid + ", username=" + username
				+ ", content=" + content + ", pdate=" + pdate + ", enable=" + enable + ", image=" + image + "]";
	}	
}
