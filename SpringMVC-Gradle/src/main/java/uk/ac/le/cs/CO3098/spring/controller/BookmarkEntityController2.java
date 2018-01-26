package uk.ac.le.cs.CO3098.spring.controller;

import java.io.File;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.dropbox.core.DbxApiException;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v1.DbxClientV1;
import com.dropbox.core.v1.DbxEntry;
import com.dropbox.core.v1.DbxWriteMode;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.users.FullAccount;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import uk.ac.le.cs.CO3098.spring.domain.BookmarkEntity;
import uk.ac.le.cs.CO3098.spring.domain.Count;
import uk.ac.le.cs.CO3098.spring.domain.Folder;
import uk.ac.le.cs.CO3098.spring.domain.ItemLink;
import uk.ac.le.cs.CO3098.spring.domain.ItemLocation;
import uk.ac.le.cs.CO3098.spring.domain.ItemTextFile;
import uk.ac.le.cs.CO3098.spring.repository.BookmarkEntityRepository;
import uk.ac.le.cs.CO3098.spring.service.BookmarkEntityService;

@Controller
@RequestMapping(value = { "/view" })
public class BookmarkEntityController2 {
	@Autowired
	BookmarkEntityService bk;

	@Autowired
	BookmarkEntityRepository bkr;

	//error page if user does illegal action
	@RequestMapping(value = "/error")
	public ModelAndView create() {
		return new ModelAndView("error");
	}

	//create a folder
	@RequestMapping(value = { "/create" })
	public ModelAndView create(@RequestParam(value = "folder", required = true) String folder,
			@RequestParam(value = "parent", required = false, defaultValue = "") String parent) {
		List<String> l = bkr.checkIfExists(parent + "|" + folder);

		if (!(l.isEmpty())) {
			return new ModelAndView("error.jsp?errorid=1");
		}
		if (!(parent.equals(""))) {
			List<String> l2 = bkr.checkIfParentExists(parent);
			if (l2.isEmpty()) {
				return new ModelAndView("error.jsp?errorid=2");
			}
		}
		Folder currentFolder = new Folder(folder);
		Folder parentFolder = new Folder(parent);
		currentFolder.setName(folder);
		currentFolder.setParentFolder(parentFolder);
		bk.save(currentFolder);
		return new ModelAndView("redirect:./listAll");
	}

	//lock a folder
	@RequestMapping(value = { "/lockfolder" })
	public ModelAndView lock(@RequestParam(value = "full_path", required = true) String full_path) {
		bkr.lock(full_path);
		bkr.lock2(full_path);
		return new ModelAndView("redirect:./listAll");
	}

	//connect to dropbox
	@RequestMapping(value = { "/dropbox" })
	public ModelAndView dropbox() throws DbxApiException, DbxException, FileNotFoundException, IOException {
		final String ACCESS_TOKEN = "YW2wJSuTE1AAAAAAAAAAL3nEy88XxfKzMoiZ8w9_3dkOz8X3N2H667dIN0LbGRmY";
		DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
		DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

		FullAccount account = client.users().getCurrentAccount();
		System.out.println(account.getName().getDisplayName() + "has connected to dropbox");

		return new ModelAndView("redirect:./listAll");
	}
	
	//google treemap
	@RequestMapping(value = { "/treemap" })
	public ModelAndView treemap(){
		return new ModelAndView("createPage");
	}


	//unlock a folder
	@RequestMapping(value = { "/unlockfolder" })
	public ModelAndView unlock(@RequestParam(value = "full_path", required = true) String full_path) {
		bkr.unlock(full_path);
		bkr.unlock2(full_path);
		return new ModelAndView("redirect:./listAll");
	}

	//create a link
	@RequestMapping(value = { "/createlink" })
	public ModelAndView createItem(@RequestParam(value = "folder", required = true) String folder,
			@RequestParam(value = "uri", required = true) String uri,
			@RequestParam(value = "title", required = false, defaultValue = "") String title,
			@RequestParam(value = "parent", required = false, defaultValue = "") String parent) {
		boolean lock = bkr.ifLocked(parent);
		if(lock){
			return new ModelAndView("error.jsp?errorid=3");
		}
		ItemLink currentLink = new ItemLink(folder, uri, title);
		Folder parentFolder = new Folder(parent);
		currentLink.setName(title);
		currentLink.setParentFolder(parentFolder);
		bk.save(currentLink);
		return new ModelAndView("redirect:./listAll");
	}

	//create a location
	@RequestMapping(value = { "/createlocation" })
	public ModelAndView createLocation(@RequestParam(value = "folder", required = true) String folder,
			@RequestParam(value = "latitude", required = true) double latitude,
			@RequestParam(value = "longitude", required = false, defaultValue = "") double longitude,
			@RequestParam(value = "parent", required = false, defaultValue = "") String parent) {
		boolean lock = bkr.ifLocked(parent);
		if(lock){
			return new ModelAndView("error.jsp?errorid=3");
		}
		ItemLocation currentLocation = new ItemLocation(folder, latitude, longitude);
		Folder parentFolder = new Folder(parent);
		currentLocation.setName(folder);
		currentLocation.setParentFolder(parentFolder);
		bk.save(currentLocation);
		return new ModelAndView("redirect:./listAll");
	}

	//create a textfile
	@RequestMapping(value = { "/createtextfile" })
	public ModelAndView createText(@RequestParam(value = "folder", required = true) String folder,
			@RequestParam(value = "text", required = true) String text,
			@RequestParam(value = "parent", required = false, defaultValue = "") String parent) {
		boolean lock = bkr.ifLocked(parent);
		if(lock){
			return new ModelAndView("error.jsp?errorid=3");
		}
		ItemTextFile currentTextFile = new ItemTextFile(folder, text);
		Folder parentFolder = new Folder(parent);
		currentTextFile.setName(folder);
		currentTextFile.setParentFolder(parentFolder);
		bk.save(currentTextFile);
		return new ModelAndView("redirect:./listAll");
	}

	//delete a folder/item
	@RequestMapping(value = { "/delete" })
	public ModelAndView delete(@RequestParam(value = "folder", required = true) String folder,
			@RequestParam(value = "parent", required = false, defaultValue = "") String parent) {
		bkr.deleteByFull_path(folder);
		return new ModelAndView("redirect:./listAll");
	}

	//edit a folder/item name
	@RequestMapping(value = { "/edit" })
	public ModelAndView edit(@RequestParam(value = "folder", required = true) String folder,
			@RequestParam(value = "full_path", required = true) String full_path) {
		boolean lock = bkr.ifLocked(full_path);
		if(lock){
			return new ModelAndView("error.jsp?errorid=3");
		}
		String parentPath = full_path.substring(0, full_path.lastIndexOf("|")) + "|";
		String oldName = full_path.substring(full_path.lastIndexOf("|")+1);
		bkr.edit(folder, parentPath, full_path);
		bkr.edit2("|"+folder+"|","|"+ oldName + "|");
		bkr.edit3("|"+ folder + "|","|"+ oldName + "|");
		bkr.edit3("|" +folder,"|" + oldName);
		bkr.edit2(folder+"|",oldName + "|");
		bkr.edit3(folder + "|",oldName + "|");
		bkr.edit4(folder, oldName);
		return new ModelAndView("redirect:./listAll");
	}

	List<BookmarkEntity> list2 = new ArrayList<BookmarkEntity>();


	List<BookmarkEntity> getChildren(List<BookmarkEntity> list) {
		if (list.size() == 0) {
			return list;
		}
		for (int j = 0; j < list.size(); j++) {
			BookmarkEntity b = list.get(j);
			List<BookmarkEntity> list2 = bkr.findAllByFull_path(b.getFullPath());
			b.setSubfolders(list2);
			getChildren(list2);

		}

		return list2;
	}

	@RequestMapping(value = { "/listAll" })
	public @ResponseBody Object listAll() throws JsonProcessingException {
		list2.clear();
		String myPath = "";
		String folder;
		if(bkr.getRootNode() == null){
			folder = "";
		}else{
			folder = bkr.getRootNode().substring(1);
		}
		boolean isFirst = false;
		if (!(folder.equals(""))) {
			if (folder.indexOf("|") != -1) {
				myPath = folder.substring(0, folder.lastIndexOf("|"));
				isFirst = true;
			}
		}

		List<BookmarkEntity> list;
		if (isFirst) {
			list = bkr.findAllByParent_path(myPath, folder);
		} else {
			list = bkr.findAllByParent_path(myPath, "|" + folder);
		}

		for (int i = 0; i < list.size(); i++) {
			BookmarkEntity b = list.get(i);
			List<BookmarkEntity> list3;
			if (isFirst) {
				list3 = bkr.findAllByFull_path(b.getFullPath());

			} else {
				list3 = bkr.findAllByFull_path((b.getFullPath()).substring(1));
			}
			b.setSubfolders(list3);
			getChildren(list3);
			list2.add(b);
			Object o = list2;
		}
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = mapper.writeValueAsString(list2);

		return new ModelAndView("show", "json", jsonInString);
	}

}
