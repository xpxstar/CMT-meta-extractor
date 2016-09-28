package iscas.xpx.util;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.thrift.protocol.TMap;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import iscas.xpx.devops.config.Configure;
import iscas.xpx.devops.extract.impl.PuppetMetaExtract;
import iscas.xpx.devops.meta.entity.SFTag;
import iscas.xpx.devops.meta.entity.Tags;
import iscas.xpx.devops.service.CategoryService;
import iscas.xpx.devops.service.MetaService;
import iscas.xpx.devops.service.SFTagService;
import iscas.xpx.devops.service.SynonymsService;
import iscas.xpx.devops.util.entity.TagCount;
import iscas.xpx.devops.util.entity.TagPair;
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("production")
@ContextConfiguration("/application_context.xml")
@Transactional
@TransactionConfiguration(defaultRollback=false)
public class Executement {
	@Autowired
	private MetaService metaService;
	@Autowired
	private SynonymsService synonymsService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private SFTagService tagService;
//	PuppetMetaBuilder pmb = new PuppetMetaBuilder();
	
	
	@Test
	@Ignore
	public void testMatch() {
		// TODO Auto-generated method stub
		List<Tags> list = metaService.getAll();
		Map<String, Tags> alltags = PuppetMetaExtract.alltags;
		Map<String,Integer> tagSet = new HashMap<>(5000);
		for (Tags tags : list) {
//			alltags.put(tags.genKey(),tags);
			for (String string : tags.getTaglist()) {
				if (tagSet.containsKey(string)) {
					tagSet.put(string,tagSet.get(string)+1);
				}else
					tagSet.put(string,1);
			}
		}
//		PuppetMetaBuilder pmb = new PuppetMetaBuilder(); 
//		pmb.buildBaseData("new_puppet.json");
		int count = 0; 
		for (Map.Entry<String,Integer> string : tagSet.entrySet()) {
			System.out.println(string.getKey()+":"+string.getValue());
			count+=string.getValue();
		}
		System.out.println(count);
	}
	@Test
	@Ignore
	public void tagsPuppetStackof() {
		List<Tags> list = metaService.getAll();
		Map<String,Integer> tagSet = new HashMap<>(5000);
		for (Tags tags : list) {
			if (tagSet.containsKey(tags.getModule())) {
				tagSet.put(tags.getModule(),tagSet.get(tags.getModule())+1);
			}else
				tagSet.put(tags.getModule(),1);
		}
//		PuppetMetaBuilder pmb = new PuppetMetaBuilder(); 
//		pmb.buildBaseData("new_puppet.json");
		List<SFTag> listsf = tagService.getAll();
		int count = 0; 
		for (SFTag tags : listsf) {
			if(tagSet.containsKey(tags.getTag())){
				System.out.println(tags.getTag());
				count += tagSet.get(tags.getTag());
			}
		}
		System.out.println(count);
	}
	@Test
	@Ignore
	public void modulePuppet() {
		List<Tags> list = metaService.getAll();
		Map<String,Integer> tagSet = new HashMap<>(5000);
		for (Tags tags : list) {
			if (tagSet.containsKey(tags.getModule())) {
				tagSet.put(tags.getModule(),tagSet.get(tags.getModule())+1);
			}else
				tagSet.put(tags.getModule(),1);
		}
//		PuppetMetaBuilder pmb = new PuppetMetaBuilder(); 
//		pmb.buildBaseData("new_puppet.json");
		for (Map.Entry<String, Integer> entry:tagSet.entrySet()){
			System.out.println(entry.getKey()+","+entry.getValue());
		}
	}
	@Test
	@Ignore
	public void allTags() throws IOException{
		List<Tags> list = metaService.getAll();
		OutputStreamWriter csvnamewriter = new OutputStreamWriter(new FileOutputStream("puppetTags.txt"));
		for (Tags tags:list) {
			csvnamewriter.write(tags.getModule()+" "+tags.getTags()+"\n");
		}
		csvnamewriter.close();
	
	}
	@Test
	@Ignore
	public void countPuppTags() throws IOException{
		List<Tags> list = metaService.getAll();
		
		OutputStreamWriter csvnamewriter = new OutputStreamWriter(new FileOutputStream("pupptagcount.txt"));
		
		OutputStreamWriter validwriter5 = new OutputStreamWriter(new FileOutputStream("pupptagvalid50.txt"));
		OutputStreamWriter validwriter75 = new OutputStreamWriter(new FileOutputStream("pupptagvalid75.txt"));
		OutputStreamWriter validwriter8 = new OutputStreamWriter(new FileOutputStream("pupptagvalid8.txt"));
		OutputStreamWriter validwriter85 = new OutputStreamWriter(new FileOutputStream("pupptagvalid85.txt"));
		OutputStreamWriter validwriter9 = new OutputStreamWriter(new FileOutputStream("pupptagvalid9.txt"));
		OutputStreamWriter validwriter95 = new OutputStreamWriter(new FileOutputStream("pupptagvalid95.txt"));
		Map<String,Integer> alltags = new HashMap<>(5000);
		
		for (Tags tags:list) {
				for(String tag:tags.getTaglist()){
					if (alltags.containsKey(tag)) {
						alltags.put(tag, alltags.get(tag)+1);
					}else {
						alltags.put(tag, 1);
					}
				}
		}
		for (Map.Entry<String, Integer> total: alltags.entrySet()) {
			System.out.println(total.getKey()+": "+total.getValue());
		}
		int combaincount = 0;
		for (Map.Entry<String, Integer> tagpair: alltags.entrySet()) {
			if (tagpair.getValue() < 10) continue;
			for (Map.Entry<String, Integer> pair: alltags.entrySet()) {
				if(pair.getValue() < tagpair.getValue()){
					if (pair.getValue() < 5) continue;
					combaincount=0;
					for (Tags scan:list) {
						Set<String> tmptags = scan.getTaglist();
						if (null != tmptags) {
							if(tmptags.contains(tagpair.getKey()) && tmptags.contains(pair.getKey())){
								combaincount++;
							}
						}
						
					}
					if (combaincount > 0) {
						float percent = ((float)combaincount/(float)pair.getValue());
						csvnamewriter.write(tagpair.getKey()+": "+tagpair.getValue()+" "+pair.getKey()+": "+pair.getValue()+"\t" +combaincount+":"+percent+"\n");
						if(pair.getValue() < 10)continue;
						if (percent >= 0.5 ) {
							validwriter5.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
						}
						if (percent >= 0.75) {
							validwriter75.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
						}
						if (percent >= 0.8 ) {
							validwriter8.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
						}
						if (percent >= 0.85) {
							validwriter85.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
						}
						if (percent >= 0.9) {
							validwriter9.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
						}
						if (percent >= 0.95) {
							validwriter95.write(tagpair.getKey()+" "+pair.getKey()+":"+percent+"\n");;
						}
					}
					
				}
			}
			
		}
		csvnamewriter.close();
		validwriter5.close();
		validwriter75.close();
		validwriter8.close();
		validwriter85.close();
		validwriter9.close();
		validwriter95.close();
	}
	
	
	
	@Test
	@Ignore
	public void simplyfyTrainData() throws IOException{
		synonymsService.fillMap();
		synonymsService.simplify("allTags.txt","allTags_sim.txt");
	}
	@Test
	@Ignore
	public void attatchPuppet() throws IOException{
		categoryService.attachChef("chef_data_sim.txt","chef");
	}
	@Test
	@Ignore
	public void genTrainData() throws IOException{
		categoryService.genTrainData();
	}
	@Test
	@Ignore
	public void genTestData() throws IOException{
		categoryService.genTestData();
	}
	@Test
	@Ignore
	public void clearDown30() throws IOException{
		categoryService.cleardown30();
	}
	@Test
	@Ignore
	public void genCompTrainData() throws IOException{
		categoryService.buildData();
//		categoryService.genCompTrainData(2);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(3);
//		System.out.println("\n\n");
		categoryService.genCompTrainData(10);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(5);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(6);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(7);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(8);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(9);
	}
	@Test
	@Ignore
	public void genCompTempData() throws IOException{
		categoryService.buildData();
//		categoryService.genCompTrainData(2);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(3);
//		System.out.println("\n\n");
		categoryService.genCompTrainData(10);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(5);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(6);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(7);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(8);
//		System.out.println("\n\n");
//		categoryService.genCompTrainData(9);
	}
	//
	@Test
	public void genCompTempTestData() throws IOException{
		categoryService.buildData();
		categoryService.genCompTrainData(2);
		System.out.println("2\n\n");
		categoryService.genCompTrainData(3);
		System.out.println("3\n\n");
		categoryService.genCompTrainData(4);
		System.out.println("4\n\n");
		categoryService.genCompTrainData(5);
		System.out.println("5\n\n");
//		categoryService.genCompTrainData(6);
//		System.out.println("6\n\n");
//		categoryService.genCompTrainData(7);
//		System.out.println("7\n\n");
//		categoryService.genCompTrainData(8);
//		System.out.println("8\n\n");
		categoryService.genCompTrainData(9);
		System.out.println("9\n\n");
		categoryService.genCompTrainData(10);
	}
	@Test
	@Ignore
	public void buildHierarchy() throws IOException{
		
		List<HashSet<String>> list = new ArrayList<HashSet<String>>();
		Map<String,Integer> alltags = new HashMap<>(5000);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("allTags.txt")));
		
		String line = reader.readLine(); // 读取第一�?
        
		while(null != line )  {
			HashSet<String> tmp = new HashSet<>();
			for(String tag:line.split(" ")){
				tmp.add(tag);
				if (alltags.containsKey(tag)) {
					alltags.put(tag, alltags.get(tag)+1);
				}else {
					alltags.put(tag, 1);
				}
			}
			list.add(tmp);
			line = reader.readLine();
		}
		reader.close();
		int countup5 = 0;
		PriorityQueue<TagCount> tagCounts = new PriorityQueue<>(alltags.size()/2, new TagCount.TagCountComparetor() );
		for (Map.Entry<String, Integer> total: alltags.entrySet()) {
			if(total.getValue() >5 ){tagCounts.add(new TagCount(total.getKey(), total.getValue()));countup5++;}
			System.out.println(total.getKey()+"\t"+total.getValue());
		}
//		int size = alltags.size();
		Comparator<TagPair> tagcom = new TagPair.TagComparetor();
		PriorityQueue<TagPair> tagPairs = new PriorityQueue<>(countup5*countup5, tagcom );
		int combaincount = 0;
		for (Map.Entry<String, Integer> tagpair: alltags.entrySet()) {
			if (tagpair.getValue() < 10) continue;
			for (Map.Entry<String, Integer> pair: alltags.entrySet()) {
				if(pair.getValue() < tagpair.getValue()){
					if (pair.getValue() < 5) continue;
					combaincount=0;
					for (HashSet<String> scan:list) {
						if(scan.contains(tagpair.getKey()) && scan.contains(pair.getKey())){
							combaincount++;
						}
					}
					if (combaincount > 5) {
						TagPair tagTmp = new TagPair();
						tagTmp.setBigger(tagpair.getKey());
						tagTmp.setSmaller(pair.getKey());
						tagTmp.setCount(combaincount);
						float percent = ((float)combaincount/(float)pair.getValue());
						tagTmp.setConfidence(percent);
						tagTmp.setSupport((float)combaincount/countup5);
						tagPairs.add(tagTmp);
					}
					
				}
			}
		}
		OutputStreamWriter confident = new OutputStreamWriter(new FileOutputStream("tagHierarchy.txt"));
		while (!tagPairs.isEmpty()) {
			confident.write(tagPairs.poll().toString());
		}
		confident.close();
		
	}
}
