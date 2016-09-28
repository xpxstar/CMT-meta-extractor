package iscas.xpx.devops.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import iscas.xpx.devops.dao.TagsDao;
import iscas.xpx.devops.meta.entity.Tags;

@Service
public class MetaService {
	@Autowired
	TagsDao tagsDao;
	public Tags getById(Long id){
		return tagsDao.findOne(id);
	}
	public List<Tags> getAll(){
		return (List<Tags>) tagsDao.findAll();
	}
}
