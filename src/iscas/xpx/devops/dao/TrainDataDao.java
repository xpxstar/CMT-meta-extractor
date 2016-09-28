package iscas.xpx.devops.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import iscas.xpx.devops.meta.entity.TrainData;

public interface TrainDataDao extends PagingAndSortingRepository<TrainData, Long> {

	@Query("select data from TrainData as data where data.category like CONCAT(?1,'%')")
	public List<TrainData> findPosTrainDataByCate(String cate);
	
	@Query("select data from TrainData as data where data.category like CONCAT(?1,'_') or data.category like ?1)")
	public List<TrainData> findExactTrainDataByCate(String cate);
	
	@Query("select data from TrainData as data where data.category like CONCAT(?2,'%') and data.category not like CONCAT(?1,'%')")
	public List<TrainData> findTrainNegDataByCate(String cate,String parent);
	
	@Modifying
	@Query("UPDATE TrainData data  set data.category = :parent where data.category like CONCAT(:cate,'%')")
	public int clearDown30(@Param("cate")String cate,@Param("parent") String parent);
	
	
}
