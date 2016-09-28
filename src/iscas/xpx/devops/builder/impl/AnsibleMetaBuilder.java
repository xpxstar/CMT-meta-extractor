package iscas.xpx.devops.builder.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import iscas.xpx.devops.builder.BaseBuilder;
import iscas.xpx.devops.config.Configure;
import iscas.xpx.devops.extract.impl.AnsibleMetaExtract;
import iscas.xpx.devops.meta.entity.Meta;
import iscas.xpx.devops.meta.entity.Tags;

public class AnsibleMetaBuilder implements BaseBuilder {
	public static HashMap<String,Integer> alltags = new HashMap<>(500);
	
	public void buidBaseData() throws IOException{
		AnsibleMetaExtract ame = new AnsibleMetaExtract();
//		File json = new File("ansible.json");
		File target = new File("ansible_short.csv");
//		File targetname = new File("rolename.csv");
		if(!target.exists())target.createNewFile();
//		OutputStream out = new FileOutputStream(json);
//		OutputStreamWriter writer = new OutputStreamWriter(out);
		OutputStream csv = new FileOutputStream(target);
		OutputStreamWriter csvwriter = new OutputStreamWriter(csv);
//		OutputStream csvname = new FileOutputStream(targetname);
//		OutputStreamWriter csvnamewriter = new OutputStreamWriter(csvname);
		String line; // 用来保存每行读取的内�?
		InputStream is = new FileInputStream(Configure.Ansible_role_static);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一�?
		while(null != line )  {
				Meta meta = ame.extract(line);
				System.out.println(meta.getName());
				try {
//					JSONObject mjson = new JSONObject(meta);
//					writer.write(mjson.toString());
//					writer.write('\n');
//					csvnamewriter.write(meta.getName());
//					csvnamewriter.write('\n');
					csvwriter.write(meta.toCsvShort());
					csvwriter.write('\n');
				} catch (IOException e) {
					e.printStackTrace();
				}
				line = reader.readLine();
		}
//		writer.close();
		csvwriter.close();
//		csvnamewriter.close();
//		out.close();
		reader.close();
		csv.close();
//		csvname.close();
		
	}
	public void fetchTags() throws IOException{
			AnsibleMetaExtract ame = new AnsibleMetaExtract();
			
			OutputStreamWriter tagsFile = new OutputStreamWriter(new FileOutputStream("ansibleTags.txt"));
			String line; // 用来保存每行读取的内�?
			InputStream is = new FileInputStream(Configure.Ansible_role_static);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        line = reader.readLine(); // 读取第一�?
	        
			while(null != line )  {
					Meta meta = ame.extract(line);
					line = reader.readLine();
					if(null != meta.getTags()){
						tagsFile.write(meta.getComponent()+" "+String.join(" ",  meta.getTags())+"\n");
					}
			}
			tagsFile.close();
			reader.close();
	}
	public void countTags() throws IOException{
		AnsibleMetaExtract ame = new AnsibleMetaExtract();
//		OutputStreamWriter csvnamewriter = new OutputStreamWriter(new FileOutputStream("tagcount.txt"));
		
		OutputStreamWriter validwriter8 = new OutputStreamWriter(new FileOutputStream("tagvalid50.txt"));
//		OutputStreamWriter validwriter75 = new OutputStreamWriter(new FileOutputStream("tagvalid75.txt"));
//		OutputStreamWriter validwriter85 = new OutputStreamWriter(new FileOutputStream("tagvalid85.txt"));
//		OutputStreamWriter validwriter9 = new OutputStreamWriter(new FileOutputStream("tagvalid9.txt"));
//		OutputStreamWriter validwriter95 = new OutputStreamWriter(new FileOutputStream("tagvalid95.txt"));
		
		String line; // 用来保存每行读取的内�?
		InputStream is = new FileInputStream(Configure.Ansible_role_static);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一�?
        List<Meta> metas = new ArrayList<>(5500);
        
		while(null != line )  {
				Meta meta = ame.extract(line);
				line = reader.readLine();
				if(null == meta.getTags()){
					continue;
				}
				for(String tag:meta.getTags()){
					if (alltags.containsKey(tag)) {
						alltags.put(tag, alltags.get(tag)+1);
					}else {
						alltags.put(tag, 1);
					}
				}
				metas.add(meta);
		}
		for (Map.Entry<String, Integer> total: alltags.entrySet()) {
			System.out.println(total.getKey()+": "+total.getValue());
		}
		int combaincount = 0;
		for (Map.Entry<String, Integer> tagpair: alltags.entrySet()) {
			if (tagpair.getValue() < 10) continue;
			for (Map.Entry<String, Integer> pair: alltags.entrySet()) {
				if(pair.getValue() < tagpair.getValue()){
					combaincount=0;
					for (Meta scan : metas) {
						Set<String> tmptags = scan.getTags();
						if (null != tmptags) {
							if(tmptags.contains(tagpair.getKey()) && tmptags.contains(pair.getKey())){
								combaincount++;
							}
						}
						
					}
					if (combaincount > 0) {
						float percent = ((float)combaincount/(float)pair.getValue());
//						csvnamewriter.write(tagpair.getKey()+": "+tagpair.getValue()+" "+pair.getKey()+": "+pair.getValue()+"\t" +combaincount+":"+percent+"\n");
						if(pair.getValue() < 10)continue;
						if (percent >= 0.5 ) {
							validwriter8.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
						}
//						if (percent >= 0.8) {
//							validwriter85.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
//						}
//						if (percent >= 0.9) {
//							validwriter9.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
//						}
//						if (percent >= 0.75) {
//							validwriter75.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
//						}
//						if (percent >= 0.95) {
//							validwriter95.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
//						}
					}
					
				}
			}
			
		}
//		csvnamewriter.close();
//		validwriter75.close();
		validwriter8.close();
//		validwriter85.close();
//		validwriter9.close();
//		validwriter95.close();
		reader.close();
	}
	public static void main(String[] arg ) throws IOException{
		AnsibleMetaBuilder pmb = new AnsibleMetaBuilder();
		pmb.fetchTags();
	}
}
