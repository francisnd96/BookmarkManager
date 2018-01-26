package uk.ac.le.cs.CO3098.spring.repository;

import java.util.List;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.le.cs.CO3098.spring.domain.BookmarkEntity;

@Repository
public interface BookmarkEntityRepository extends CrudRepository<BookmarkEntity,Integer> {
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "delete from bookmark_entity where full_path like :folder%", nativeQuery = true)
	void deleteByFull_path(@Param("folder") String folder);
	
	@Query(value = "select * from bookmark_entity where parent_path = :folder", nativeQuery = true)
	List<BookmarkEntity> findAllByFull_path(@Param("folder") String folder);
	
	@Query(value = "select * from bookmark_entity where full_path like :folder%", nativeQuery = true)
	List<BookmarkEntity> findAllByFull_path2(@Param("folder") String folder);
	
	@Query(value = "select * from bookmark_entity where parent_path = :folder and full_path = :folder2", nativeQuery = true)
	List<BookmarkEntity> findAllByParent_path(@Param("folder") String folder,@Param("folder2") String folder2);
	
	
	@Query(value = "select count(*) from bookmark_entity where parent_path = :folder", nativeQuery = true)
	Integer direct(@Param("folder") String folder);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update bookmark_entity set name = :folder, full_path = concat(:parent_path,:folder)  where full_path = :full_path", nativeQuery = true)
	void edit(@Param("folder") String folder,@Param("parent_path") String parent_path,@Param("full_path") String full_path);
	
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update bookmark_entity set full_path = replace(full_path,:oldName,:folder)", nativeQuery = true)
	void edit2(@Param("folder") String folder,@Param("oldName") String oldName);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update bookmark_entity set parent_path = replace(parent_path,:oldName,:folder)", nativeQuery = true)
	void edit3(@Param("folder") String folder,@Param("oldName") String oldName);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update bookmark_entity set parent_path = :folder where parent_path = :oldName", nativeQuery = true)
	void edit4(@Param("folder") String folder,@Param("oldName") String oldName);
	
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update bookmark_entity set read_only = 1 where full_path like :full_path%", nativeQuery = true)
	void lock(@Param("full_path") String full_path);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update bookmark_entity set read_only = 1 where name = :full_path", nativeQuery = true)
	void lock2(@Param("full_path") String full_path);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update bookmark_entity set read_only = 0 where full_path like :full_path%", nativeQuery = true)
	void unlock(@Param("full_path") String full_path);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update bookmark_entity set read_only = 0 where name = :full_path", nativeQuery = true)
	void unlock2(@Param("full_path") String full_path);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "update bookmark_entity set parent_path = :parent_path where name = :folder", nativeQuery = true)
	void move(@Param("folder") String folder,@Param("parent_path") String parent_path);
	
	@Query(value = "select count(*) from bookmark_entity where parent_path like :folder%", nativeQuery = true)
	Integer indirect(@Param("folder") String folder);
	
	@Query(value = "select uri from bookmark_entity where dtype = 'ItemLink'", nativeQuery = true)
	List<String> getUrls();
	
	@Query(value = "select * from bookmark_entity where full_path = :fullpath", nativeQuery = true)
	List<String> checkIfExists(@Param("fullpath") String fullpath);
	
	@Query(value = "select * from bookmark_entity where full_path like %:parent%", nativeQuery = true)
	List<String> checkIfParentExists(@Param("parent") String parent);
	
	@Query(value = "select full_path from bookmark_entity where parent_path =''", nativeQuery = true)
	String getRootNode();
	
	@Query(value = "select read_only from bookmark_entity where full_path = :parent", nativeQuery = true)
	boolean ifLocked(@Param("parent") String parent);
	
}
