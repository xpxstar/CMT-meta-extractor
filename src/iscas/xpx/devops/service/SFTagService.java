package iscas.xpx.devops.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import iscas.xpx.devops.dao.SFTagDao;
import iscas.xpx.devops.meta.entity.SFTag;

@Service
public class SFTagService {
	@Autowired
	SFTagDao sfTagDao;
	public SFTag getById(Long id){
		return sfTagDao.findOne(id);
	}
	public List<SFTag> getAll(){
		return (List<SFTag>) sfTagDao.findAll();
	}
}
