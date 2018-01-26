package uk.ac.le.cs.CO3098.spring.service;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.le.cs.CO3098.spring.domain.BookmarkEntity;
import uk.ac.le.cs.CO3098.spring.repository.BookmarkEntityRepository;

@Service
public class BookmarkEntityService {
	
	@Autowired
	private BookmarkEntityRepository bookmarkEntityRepository;
	
	public void save(BookmarkEntity a){
		bookmarkEntityRepository.save(a);
	}
	
	public Object findAllBookmarkEntities(){
		return bookmarkEntityRepository.findAll();
	}
	

}
