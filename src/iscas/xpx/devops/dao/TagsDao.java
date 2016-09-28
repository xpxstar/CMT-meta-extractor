package iscas.xpx.devops.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import iscas.xpx.devops.meta.entity.Tags;

public interface TagsDao extends PagingAndSortingRepository<Tags, Long> {

	
}
