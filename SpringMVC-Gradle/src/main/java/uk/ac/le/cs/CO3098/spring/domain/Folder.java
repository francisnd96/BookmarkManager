package uk.ac.le.cs.CO3098.spring.domain;

import java.util.Vector;

import javax.persistence.Entity;

@Entity
public class Folder extends BookmarkEntity {
	
	
	Vector<BookmarkEntity> list=new 	Vector<>();
	

	public Folder(String name) {
		super(name);
		setType(Type.FOLDER);
	}
	
	public Folder(){
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

	public void add(BookmarkEntity f) {
		list.add(f);
	}
	
	public void remove(BookmarkEntity f) {
		list.remove(f);
	}

	

}
