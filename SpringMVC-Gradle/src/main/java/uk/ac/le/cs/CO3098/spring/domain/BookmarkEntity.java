package uk.ac.le.cs.CO3098.spring.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity
public abstract class BookmarkEntity {

	enum Type {
		FOLDER, LINK, LOCATION, TEXTFILE
	}

	public BookmarkEntity(String name) {
		super();
		this.name = name;
	}

	public BookmarkEntity() {
		// TODO Auto-generated constructor stub
	}

	@JsonIgnore
	public String getPath() {
		return parentPath;
	}

	@JsonIgnore
	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@JsonIgnore
	public BookmarkEntity getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(BookmarkEntity parentFolder) {
		this.parentFolder = parentFolder;

		if (parentFolder.getType() == Type.FOLDER) {
			((Folder) parentFolder).add(this);
		}

		String path = toString();
		this.parentPath = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	@JsonIgnore
	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	@Override
	public String toString() {

		StringBuilder fullpath = new StringBuilder();
		String parentPath = "";

		BookmarkEntity current = this;

		boolean first = true;

		while (current != null) {
			if (first) {
				fullpath.insert(0, current.getName());
				first = false;
			} else {
				fullpath.insert(0, current.getName() + "|");
			}

			current = current.getParentFolder();
		}

		if (!(fullpath.equals(""))) {
			if (fullpath.indexOf("|") != -1) {
				parentPath = fullpath.substring(0, fullpath.lastIndexOf("|"));
			}
		}

		fullPath = fullpath.toString();

		return parentPath;

	}

	@Id
	@GeneratedValue
	@Column(name = "id")
	Integer id;

	@Column(name = "name")
	String name;

	@Column(name = "readOnly")
	boolean readOnly;

	@Column(name = "parent_path")
	String parentPath;

	@Column(name = "full_path")
	String fullPath;

	@Transient
	BookmarkEntity parentFolder;

	@Column(name = "type")
	Type type;

	@Transient
	List<BookmarkEntity> subfolders = new ArrayList<BookmarkEntity>();

	public List<BookmarkEntity> getSubfolders() {
		return subfolders;
	}

	public void setSubfolders(List<BookmarkEntity> subfolders) {
		this.subfolders = subfolders;
	}

}
