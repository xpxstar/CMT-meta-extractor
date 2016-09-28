package iscas.xpx.devops.builder.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import iscas.xpx.devops.builder.BaseBuilder;
import iscas.xpx.devops.config.Configure;
import iscas.xpx.devops.extract.impl.AnsibleMetaExtract;
import iscas.xpx.devops.extract.impl.PuppetMetaExtract;
import iscas.xpx.devops.meta.entity.Meta;

public class PuppetMetaBuilder implements BaseBuilder {
	
	public void buildBaseData(String filePath) {
		File[] filelist = new File(Configure.Puppet_Path).listFiles();
		PuppetMetaExtract pe = new PuppetMetaExtract();
		File target = new File(filePath);
		OutputStream out = null;
		try {
			if(!target.exists())
				target.createNewFile();
			out = new FileOutputStream(target);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		OutputStreamWriter writer = new OutputStreamWriter(out);
		for (File file : filelist) {
			if(file.isDirectory()){
				System.out.println(file.getName());
				try {
					Meta m = pe.extract(file.getName());
					JSONObject mjson = new JSONObject(m);
					writer.write(mjson.toString());
					writer.write('\n');
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			writer.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] arg ) throws IOException{
		PuppetMetaBuilder pmb = new PuppetMetaBuilder();
		pmb.buildBaseData("puppet.json");
	}
}
