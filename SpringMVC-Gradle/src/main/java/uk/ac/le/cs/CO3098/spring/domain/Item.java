package uk.ac.le.cs.CO3098.spring.domain;

import javax.persistence.Entity;

@Entity
public class Item extends BookmarkEntity{

	public Item(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	public Item(){}

}
