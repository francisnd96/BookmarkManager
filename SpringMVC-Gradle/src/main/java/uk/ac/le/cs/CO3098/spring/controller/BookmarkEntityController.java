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
import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping(value = { "/service" })
public class BookmarkEntityController {
	@Autowired
	BookmarkEntityService bk;

	@Autowired
	BookmarkEntityRepository bkr;

	@RequestMapping(value = "/error")
	public ModelAndView create() {
		return new ModelAndView("error");
	}

	@RequestMapping(value = { "/create" })
	public @ResponseBody boolean create(@RequestParam(value = "folder", required = true) String folder,
			@RequestParam(value = "parent", required = false, defaultValue = "") String parent) {
		//checks if given file path exists
		List<String> l = bkr.checkIfExists(parent + "|" + folder);

		if (!(l.isEmpty())) {
			return false;
		}
		if (!(parent.equals(""))) {
			List<String> l2 = bkr.checkIfParentExists(parent);
			if (l2.isEmpty()) {
				return false;
			}
		}
		//creates folder and parent folder
		//saves to database
		Folder currentFolder = new Folder(folder);
		Folder parentFolder = new Folder(parent);
		currentFolder.setName(folder);
		currentFolder.setParentFolder(parentFolder);
		bk.save(currentFolder);
		return true;
	}


	@RequestMapping(value = { "/delete" })
	public @ResponseBody boolean delete(@RequestParam(value = "folder", required = true) String folder,
			@RequestParam(value = "parent", required = false, defaultValue = "") String parent) {
		//check if given files exists
		List<String> l = bkr.checkIfExists(folder);
		List<String> l2 = bkr.checkIfExists("|" + folder);
		if ((l.isEmpty() && l2.isEmpty())) {
			return false;
		}
		//if it exists delete it and its children
		bkr.deleteByFull_path(folder);
		return true;
	}

	//list to return
	List<BookmarkEntity> list2 = new ArrayList<BookmarkEntity>();

	@RequestMapping(value = { "/structure" })
	public @ResponseBody Object structure(@RequestParam(value = "folder", required = true) String folder)
			throws JsonProcessingException {
		//empty the list
		list2.clear();
		String myPath = "";
		boolean isFirst = false;
		//cut off the last name in the path
		if (!(folder.equals(""))) {
			if (folder.indexOf("|") != -1) {
				myPath = folder.substring(0, folder.lastIndexOf("|"));
				isFirst = true;
			}
		}
		
		//find parent files
		List<BookmarkEntity> list;
		if (isFirst) {
			list = bkr.findAllByParent_path(myPath, folder);
		} else {
			list = bkr.findAllByParent_path(myPath, "|" + folder);
		}

		//loop through parent files 
		for (int i = 0; i < list.size(); i++) {
			BookmarkEntity b = list.get(i);
			List<BookmarkEntity> list3;
			//put children of parent into a list
			if (isFirst) {
				list3 = bkr.findAllByFull_path(b.getFullPath());

			} else {
				list3 = bkr.findAllByFull_path((b.getFullPath()).substring(1));
			}
			//add list to parent subfolders list
			b.setSubfolders(list3);
			//call method to find the children of these children
			getChildren(list3);
			list2.add(b);
		}


		return list2;
	}

	List<BookmarkEntity> getChildren(List<BookmarkEntity> list) {
		//if there are no children return empty list
		if (list.size() == 0) {
			return list;
		}
		//find children of given list
		for (int j = 0; j < list.size(); j++) {
			BookmarkEntity b = list.get(j);
			List<BookmarkEntity> list2 = bkr.findAllByFull_path(b.getFullPath());
			b.setSubfolders(list2);
			getChildren(list2);

		}

		return list2;
	}


	@RequestMapping(value = { "/count" })
	public @ResponseBody Object listAllJson(@RequestParam(value = "folder", required = true) String folder) {
		//create new count object
		Count c = new Count("count");
		
		//set direct children
		c.setDirect(bkr.direct(folder));
		//set indirect children
		c.setIndirect(bkr.indirect(folder));

		return c;
	}

	@RequestMapping(value = { "/createStructure" })
	public @ResponseBody boolean createStructure(@RequestParam(value = "tree", required = true) String tree,
			@RequestParam(value = "root", required = true) String root) {

		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		int node = 0;
		final String[] EMPTY_STRING_ARRAY = new String[0];
		boolean end = false;
		//number of brace pairs
		int numBracePairs = tree.length() - tree.replace("[", "").length();
		//loop the number of brace pairs
		//find the string in between the first two [ [ and the last two ] ] and put them together
		//in a string separated by a comma. Then put that in a hash map with key 0. Then substring those bits
		//off the original string. Repeat the process and put string in a hash map with key 1, then 2 etc
		for (int i = 0; i < numBracePairs; i++) {
			if (tree.charAt(0) == '[') {
				int p = tree.lastIndexOf(']');
				String element;
				if (tree.indexOf('[', 1) == -1) {
					element = tree.substring(1, tree.indexOf('|', 1));
					end = true;
				} else {
					element = tree.substring(1, tree.indexOf('[', 1)) + "*";
				}
				String removeLastBrace = tree.substring(0, tree.lastIndexOf(']'));
				String element2;
				if (tree.indexOf('[', 1) == -1) {
					element2 = tree.substring(removeLastBrace.lastIndexOf('|') + 1, tree.lastIndexOf(']'));
				} else {
					element2 = tree.substring(removeLastBrace.lastIndexOf(']') + 1, tree.lastIndexOf(']'));
				}
				if (element.indexOf('|', 1) != -1) {
					element = element.replace('|', ',');
				}

				if (element2.indexOf('|', 0) != -1) {
					element2 = element2.replace('|', ',');
				}
				String level = element + "," + element2;
				hm.put(node, level);
				node++;
			}
			if (!end) {
				String remainder = tree.substring(tree.indexOf('[', i + 1));
				String removeLastBrace2 = remainder.substring(0, remainder.lastIndexOf(']'));
				String remainder2 = remainder.substring(0, removeLastBrace2.lastIndexOf(']') + 1);
				tree = remainder2;
			} else {
				break;
			}
		}
		String pfolder = root;
		for (int i = 0; i < node; i++) {
			String s = "node" + Integer.toString(i);
			String[] spot = hm.get(i).toString().split(",");
			for (int j = 0; j < spot.length; j++) {
				if (spot[j].equals("")) {
					List<String> list = new ArrayList<>();
					Collections.addAll(list, spot);
					list.removeAll(Arrays.asList(""));
					spot = list.toArray(EMPTY_STRING_ARRAY);
				}
			}
			String cfolder = "";
			
			//check to see if folder already exists or if parent path exists 
			for (int k = 0; k < spot.length; k++) {
				if (spot[k].contains("*")) {
					cfolder = spot[k].substring(0, spot[k].length() - 1);
				} else {
					cfolder = spot[k];
				}
				List<String> l = bkr.checkIfExists(pfolder + "|" + cfolder);

				if (!(l.isEmpty())) {
					return false;
				}
				if (!(pfolder.equals(""))) {
					List<String> l2 = bkr.checkIfParentExists(pfolder);
					if (l2.isEmpty()) {
						return false;
					}
				}
				//create folders and save to database
				Folder currentFolder = new Folder(cfolder);
				Folder parentFolder = new Folder(pfolder);
				currentFolder.setName(cfolder);
				currentFolder.setParentFolder(parentFolder);
				bk.save(currentFolder);

			}
			//the parent path should be the child that has a star, which means it goes down
			//a further level
			for (int l = 0; l < spot.length; l++) {
				if (spot[l].contains("*")) {
					cfolder = spot[l].substring(0, spot[l].length() - 1);
				}
			}
			pfolder = pfolder + "|" + cfolder;

		}

		return true;
	}

}
