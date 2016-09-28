package iscas.xpx.devops.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import iscas.xpx.devops.meta.entity.Synonyms;

public interface SynonymsDao extends PagingAndSortingRepository<Synonyms, Long> {

}
