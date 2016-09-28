package iscas.xpx.devops.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import iscas.xpx.devops.meta.entity.TestData;

public interface TestDataDao extends PagingAndSortingRepository<TestData, Long> {

	@Query("select data from TestData as data where data.category like CONCAT(?1,'%')")
	public List<TestData> findPosTrainDataByCate(String cate);
	
	@Query("select data from TestData as data where data.category like CONCAT(?1,'_') or data.category like ?1)")
	public List<TestData> findExactTrainDataByCate(String cate);
	
	@Query("select data from TestData as data where data.category like CONCAT(?2,'%') and data.category not like CONCAT(?1,'%')")
	public List<TestData> findTrainNegDataByCate(String cate,String parent);
	
	@Modifying
	@Query("UPDATE TestData data  set data.category = :parent where data.category like CONCAT(:cate,'%')")
	public int clearDown30(@Param("cate")String cate,@Param("parent") String parent);
	
	
}
