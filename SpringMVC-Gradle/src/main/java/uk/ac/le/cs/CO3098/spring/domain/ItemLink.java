package uk.ac.le.cs.CO3098.spring.domain;

import javax.persistence.Entity;

@Entity
public class ItemLink  extends Item{
	
	public ItemLink(String name,String uri, String title) {
		super(name);
		setType(Type.LINK);
		this.uri = uri;
		this.title = title;
	}
	
	public ItemLink(){}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	String uri;
	String title;

}
