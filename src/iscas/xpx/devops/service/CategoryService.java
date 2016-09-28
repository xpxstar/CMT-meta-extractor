package iscas.xpx.devops.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import iscas.xpx.devops.config.Configure;
import iscas.xpx.devops.dao.TestDataDao;
import iscas.xpx.devops.dao.TrainDataDao;
import iscas.xpx.devops.meta.entity.Category;
import iscas.xpx.devops.meta.entity.TestData;
import iscas.xpx.devops.meta.entity.TrainData;
@Service
public class CategoryService {
	@Autowired
	TrainDataDao trainDataDao;
	@Autowired
	TestDataDao testDataDao;
	
	private HashMap<String, Category> treeMap = new HashMap<>();
	private Category tree = new Category();
	public HashMap<String, Category> getTreeMap() {
		return treeMap;
	}
	public void setTreeMap(HashMap<String, Category> treeMap) {
		this.treeMap = treeMap;
	}
	public Category getTree() {
		return tree;
	}
	public void setTree(Category tree) {
		this.tree = tree;
	}
	private void genCateTree() throws IOException{
		String line; // 用来保存每行读取的内容
		InputStream is = new FileInputStream(Configure.Category);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一�?
		tree = new Category("root");
		tree.setPath("root");
		tree.setLevel(0);
		treeMap.put("root", tree);
		
		while(null != line )  {
			String[] tokens = line.split(",");
			Category parent = treeMap.get(tokens[0]);
			Category ca = new Category();
			ca.setParent(parent);
			ca.setValue(tokens[1]);
			parent.addChilren(ca);
			ca.setPath(parent.getPath()+" "+tokens[1]);
			ca.setLevel(parent.getLevel()+1);
			treeMap.put(tokens[1], ca);
			line = reader.readLine();
		}
		reader.close();
	}
	//清除小于30的类
	public void cleardown30() throws IOException{
		String line; // 用来保存每行读取的内容
		InputStream is = new FileInputStream(Configure.down30);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一�?
        while(null != line )  {
			int split = line.lastIndexOf(' ');
			String parent = line.substring(0,split);
			
			System.out.println(trainDataDao.clearDown30(line, parent));
			line = reader.readLine();
		}
		reader.close();
	}
	/**
	 * 为 puppet 和ansible 标注类别，注意首先匹配名称，然后是描述和标签。
	 * @param inputFile format like "ansible_data_sim.txt","puppet_data_sim.txt"
	 * @param outType ansible,or puppet ,or cookbook
	 * @throws IOException
	 */
	public void attach(String inputFile,String outType) throws IOException{
		genCateTree();
		String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outType+"_data_cate.txt"));
		OutputStreamWriter writerun = new OutputStreamWriter(new FileOutputStream(outType+"_data_other.txt"));
		line = reader.readLine(); // 读取第一�?
		int maxlevel=0;
		String pathString="other";
		while(null != line )  {
			String[] tokens = line.split(" ");
			if (treeMap.containsKey(tokens[0])) {
				writer.write(treeMap.get(tokens[0]).getPath());
				writer.write("\n");
			}else {
				maxlevel=-1;
				pathString="other";
				for (String token : tokens) {
					if (treeMap.containsKey(token)) {
						Category node = treeMap.get(token);
						if (maxlevel < node.getLevel()) {
							pathString = node.getPath();
							maxlevel = node.getLevel();
						}
					}
				}
				if(maxlevel < 0){
					writerun.write(line);
					writerun.write("\n");
				}
				writer.write(pathString);
				writer.write("\n");
			}
			line = reader.readLine();
		}
		reader.close();
		writer.close();
		writerun.close();
	}
	/**
	 * 为 chef 转换类别为层次类别，只匹配人工标注的部分。
	 * @param inputFile format like "chef_data_sim.txt","puppet_data_sim.txt"
	 * @param outType ansible,or puppet ,or cookbook
	 * @throws IOException
	 */
	public void attachChef(String inputFile,String outType) throws IOException{
		genCateTree();
		String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outType+"_data_cate.txt"));
		OutputStreamWriter writerun = new OutputStreamWriter(new FileOutputStream(outType+"_data_other.txt"));
		line = reader.readLine(); // 读取第一�?
		String pathString="other";
		while(null != line )  {
			String[] tokens = line.split(",");
			if (treeMap.containsKey(tokens[1])) {
				writer.write(treeMap.get(tokens[1]).getPath());
				writer.write("\n");
			}else {
				writerun.write(line+"\n");
				writer.write(pathString);
				writer.write("\n");
			}
			line = reader.readLine();
		}
		reader.close();
		writer.close();
		writerun.close();
	}
	public void genTrainData() throws IOException{
		genCateTree();
		treeMap.remove("root");
		for (Category cate : treeMap.values()) {
			List<TrainData> tains_pos = trainDataDao.findPosTrainDataByCate(cate.getPath());
			List<TrainData> tains_neg = trainDataDao.findTrainNegDataByCate(cate.getPath(),cate.getParent().getPath());
			if (0 == tains_neg.size()) {
				continue;
			}
			float percent = (float)tains_pos.size()/tains_neg.size();
			if (tains_pos.size()<30 ) {
				continue;
			}
			System.out.println(cate.getPath()+"\t"+cate.getLevel()+"\t"+tains_pos.size()+"\t"+tains_neg.size()+"\t"+percent);
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(Configure.Data_Path+"data/"+cate.getPath()+".csv"));
//			OutputStreamWriter writerPosi = new OutputStreamWriter(new FileOutputStream(Configure.Data_Path+"posi/"+cate.getPath()+"_posi.csv"));
			for (TrainData train : tains_pos) {
//				writerPosi.write(train.getData());
//				writerPosi.write(",");
//				writerPosi.write("1");
//				writerPosi.write("\n");
				
				writer.write(train.getData());
				writer.write(",");
				writer.write("1");
				writer.write("\n");
			}
			for (TrainData train : tains_neg) {
				writer.write(train.getData());
				writer.write(",");
				writer.write("0");
				writer.write("\n");
			}
			
			writer.close();
//			writerPosi.close();
		}
	}
	
	public void genTestData() throws IOException{
		genCateTree();
		treeMap.remove("root");
		for (Category cate : treeMap.values()) {
			List<TrainData> test_pos = trainDataDao.findPosTrainDataByCate(cate.getPath());
			List<TrainData> test_neg = trainDataDao.findTrainNegDataByCate(cate.getPath(),cate.getParent().getPath());
			if (0 == test_pos.size()||0 == test_neg.size()) {
				continue;
			}
			float percent = (float)test_pos.size()/test_neg.size();
			System.out.println(cate.getPath()+"\t"+cate.getLevel()+"\t"+test_pos.size()+"\t"+test_neg.size()+"\t"+percent);
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(Configure.Data_Path+"test/"+cate.getPath()+".csv"));
//			OutputStreamWriter writerPosi = new OutputStreamWriter(new FileOutputStream(Configure.Data_Path+"posi/"+cate.getPath()+"_posi.csv"));
			for (TrainData test : test_pos) {
//				writerPosi.write(test.getData());
//				writerPosi.write(",");
//				writerPosi.write("1");
//				writerPosi.write("\n");
				
				writer.write(test.getData());
				writer.write(",");
				writer.write("1");
				writer.write("\n");
			}
			for (TrainData test : test_neg) {
				writer.write(test.getData());
				writer.write(",");
				writer.write("0");
				writer.write("\n");
			}
			
			writer.close();
//			writerPosi.close();
		}
	}
	public void  buildData() throws IOException {
		String line; // 用来保存每行读取的内容
		InputStream is = new FileInputStream(Configure.Category);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一�?
		tree = new Category("root");
		tree.setPath("root");
		tree.setLevel(0);
		treeMap.put("root", tree);
		List<TrainData> all = trainDataDao.findPosTrainDataByCate(tree.getPath());
		List<TestData> alltest = testDataDao.findPosTrainDataByCate(tree.getPath());
		tree.setDatas(all);
		tree.setSize(all.size());	
		tree.setTestDatas(alltest);
		while(null != line )  {
			String[] tokens = line.split(",");
			Category parent = treeMap.get(tokens[0]);
			Category ca = new Category();
			ca.setParent(parent);
			ca.setValue(tokens[1]);
			if (null == parent) {
				line = reader.readLine();
				continue;
			}
			ca.setPath(parent.getPath()+" "+tokens[1]);
			List<TrainData> trains_pos = trainDataDao.findPosTrainDataByCate(ca.getPath());
			List<TestData> test_pos = testDataDao.findPosTrainDataByCate(ca.getPath());
			ca.setDatas(trains_pos);
			ca.setTestDatas(test_pos);
			List<TrainData> trains_exact = trainDataDao.findExactTrainDataByCate(ca.getPath());
			List<TestData> test_exact = testDataDao.findExactTrainDataByCate(ca.getPath());
			if (trains_pos.size()!=trains_exact.size()) {
				Category other = new Category();
				other.setSize(trains_exact.size());
				other.setPath(ca.getPath()+" others");
				other.setValue("others");
				other.setLevel(ca.getLevel()+1);
				other.setParent(ca);
				other.setDatas(trains_exact);
				other.setTestDatas(test_exact);
				ca.addChilren(other);
			
			}
			ca.setSize(trains_pos.size());
			parent.addChilren(ca);
			
			ca.setLevel(parent.getLevel()+1);
			treeMap.put(tokens[1], ca);
			line = reader.readLine();
		}
		reader.close();
	}
	public void genCompTrainData(int param) throws IOException{
		devide( tree.getChildren(),tree.getSize(),param);
	}
	private void devide(ArrayList<Category> cates,int size,int param) throws IOException{
		if (null == cates) {
			return;
		}
		if (cates.size()==1) {
			devide(cates.get(0).getChildren(), cates.get(0).getSize(), param);
			return;
		}
		if (cates.size() < param) {
			for (int i = 0; i < cates.size(); i++) {
				Category tmp = cates.get(i);
				if (tmp.getDatas() == null) {
					break;
				}
//				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(Configure.Data_Path+param+"comp/"+tmp.getPath()+".csv"));
				OutputStreamWriter writertest = new OutputStreamWriter(new FileOutputStream(Configure.Data_Test_Path+param+"comp/"+tmp.getPath()+".csv"));
//				int pos = tmp.getDatas().size();
//				int neg = 0;
				//train data
				/*for (TrainData train : tmp.getDatas()) {
					writer.write(train.getData());
					writer.write(",1\n");
				}
				for (int j = 0; i!=j && j < cates.size(); j++) {
					neg+= cates.get(j).getDatas().size();
					for (TrainData train : cates.get(j).getDatas()) {
						writer.write(train.getData());
						writer.write(",0\n");
					}
				}*/
				//testdata
				int testrest = 0;
				for (TestData test : tmp.getTestDatas()) {
					writertest.write(test.getData());
					writertest.write(",1\n");
				}
				for (int j = 0; i!=j && j < cates.size(); j++) {
					testrest+=cates.get(j).getTestDatas().size();
					for (TestData train : cates.get(j).getTestDatas()) {
						writertest.write(train.getData());
						writertest.write(",0\n");
					}
				}
				
				System.out.println( tmp.getPath()+"\t"+tmp.getLevel()+"\t"+tmp.getTestDatas().size()+"\t"+testrest+"\t"+(float)tmp.getTestDatas().size()/testrest);
				
				
//				writer.close();
				writertest.close();
				devide(tmp.getChildren(), tmp.getSize(), param);
			}
			
			return;
		}
		int total = 0,index=0,rest=0;
		ArrayList<Category> font = new ArrayList<>();
		ArrayList<Category> last = new ArrayList<>();
		String filename = cates.get(0).getParent().getPath();
		while (total*2 < size) {
			font.add(cates.get(index));
			total+=cates.get(index).getSize();
			filename+=" "+cates.get(index).getValue();
			index++;
			if (cates.size() ==2&&index==2) {
				System.out.println("1");
			}
		}
		filename+="#";
		while (index < cates.size()) {
			last.add(cates.get(index));
			rest+=cates.get(index).getSize();
			filename+=" "+cates.get(index).getValue();
			index++;
		}
		//train writer
//		OutputStreamWriter writer2 = new OutputStreamWriter(new FileOutputStream(Configure.Data_Path+param+"comp/"+filename+".csv"));
		OutputStreamWriter writer2 = new OutputStreamWriter(new FileOutputStream(Configure.Data_Test_Path+param+"comp/"+filename+".csv"));
		//train data
		/*for (Category ca : font) {
			for (TrainData train : ca.getDatas()) {
				writer2.write(train.getData());
				writer2.write(",1\n");
			}
		}
		for (Category ca : last) {
			for (TrainData train : ca.getDatas()) {
				writer2.write(train.getData());
				writer2.write(",0\n");
			}
		}*/
		//test data
		int testnum=0,testrest=0;
		for (Category ca : font) {
			testnum+=ca.getTestDatas().size();
			for (TestData train : ca.getTestDatas()) {
				writer2.write(train.getData());
				writer2.write(",1\n");
			}
		}
		for (Category ca : last) {
			testrest+=ca.getTestDatas().size();
			for (TestData train : ca.getTestDatas()) {
				writer2.write(train.getData());
				writer2.write(",0\n");
			}
		}
		System.out.println( filename+"\t"+cates.get(0).getLevel()+"\t"+testnum+"\t"+testrest+"\t"+(float)testnum/testrest);
		
		writer2.close();
		devide(font, total, param);
		
		devide(last, rest, param);
	}
	
}
