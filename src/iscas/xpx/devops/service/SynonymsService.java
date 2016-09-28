package iscas.xpx.devops.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import iscas.xpx.devops.config.Configure;
import iscas.xpx.devops.dao.SynonymsDao;
import iscas.xpx.devops.meta.entity.Synonyms;
@Service
public class SynonymsService {
	@Autowired
	SynonymsDao synonymsDao;
	
	public Map<String, String> smap = new HashMap<>();
	public void fillMap(){
		List<Synonyms> synonymsList=  (List<Synonyms>) synonymsDao.findAll();
		for (Synonyms synonyms : synonymsList) {
			smap.put(synonyms.getSynonyms(), synonyms.getName());
		}
	}
	public void simplify(String source,String target ) throws IOException{
		String line; // 用来保存每行读取的内容
		InputStream is = new FileInputStream(source);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(target));
		line = reader.readLine(); // 读取第一�?
		while(null != line )  {
			String[] tokens = line.split(" ");
			StringBuffer sb = new StringBuffer(line.length());
			for (String token : tokens) {
				if(smap.containsKey(token)){
					sb.append(smap.get(token));
				}else {
					sb.append(token);
				}
				sb.append(" ");
			}
			sb.append("\n");
			writer.write(sb.toString());
			line = reader.readLine();
		}
		writer.close();
		reader.close();
	}
}
