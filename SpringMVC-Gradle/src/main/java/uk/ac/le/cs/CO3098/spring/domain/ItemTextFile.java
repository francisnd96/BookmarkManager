package uk.ac.le.cs.CO3098.spring.domain;

import javax.persistence.Entity;

import uk.ac.le.cs.CO3098.spring.domain.BookmarkEntity.Type;

@Entity
public class ItemTextFile extends Item{
	
	public ItemTextFile(){}
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ItemTextFile(String title, String text) {
		super(title);
		setType(Type.TEXTFILE);
		this.text = text;
	}
	String text;


}
