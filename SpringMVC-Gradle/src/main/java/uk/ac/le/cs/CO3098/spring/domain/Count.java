package uk.ac.le.cs.CO3098.spring.domain;

import java.util.List;
import java.util.Vector;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Count extends BookmarkEntity {
	
	
	int direct;
	int indirect;
	

	public Count(String name) {
		super(name);
	}
	
	public int getDirect() {
		return direct;
	}

	public void setDirect(int direct) {
		this.direct = direct;
	}

	public int getIndirect() {
		return indirect;
	}

	public void setIndirect(int indirect) {
		this.indirect = indirect;
	}
	
	@JsonIgnore
	public String getName() {
		return name;
	}
	
	@JsonIgnore
	public List<BookmarkEntity> getSubfolders() {
		return subfolders;
	}
	

}
