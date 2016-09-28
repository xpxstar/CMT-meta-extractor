package iscas.xpx.devops.extract.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import iscas.xpx.devops.config.Configure;
import iscas.xpx.devops.extract.MetaExtract;
import iscas.xpx.devops.meta.entity.Dependency;
import iscas.xpx.devops.meta.entity.Meta;
import iscas.xpx.devops.meta.entity.Tags;
import iscas.xpx.devops.meta.entity.Meta.MetaBuilder;

public class PuppetMetaExtract implements MetaExtract {
	private static  Pattern VERSION = Pattern.compile("\\d\\.\\d\\.\\d");
	private static  Pattern OS_V = Pattern.compile("\\d+");
	public static HashMap<String, Tags> alltags = new HashMap<>(5500);
	
	/** extract puppet metadata.json into Meta Object
	 * @see iscas.xpx.devops.extract.MetaExtract#extract(java.lang.String)
	 */
	@Override
	public Meta extract(String dir) throws IOException {
		InputStream is = new FileInputStream(Configure.Puppet_Path+dir+"/metadata.json");
        String line; // 用来保存每行读取的内�?
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一�?
        StringBuffer buffer = new StringBuffer();
        while (line != null) { // 如果 line 为空说明读完�?
            buffer.append(line); // 将读到的内容添加�? buffer �?
            line = reader.readLine(); // 读取下一�?
        }
        reader.close();
        is.close();
        JSONObject orign = new JSONObject(buffer.toString());
        MetaBuilder mabuilder = Meta.builder();
        ArrayList<Dependency> depend =  new ArrayList<>();
        try {
			JSONArray dens = orign.getJSONArray("dependencies");
			for (Object den : dens) {
				depend.add(parseDependency(den));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        
        ArrayList<Dependency> oslist =  new ArrayList<>();
	    try {
    		JSONArray oss = orign.getJSONArray("operatingsystem_support");
	        for (Object os : oss) {
				oslist.add(parseOs(os));
			}
	    } catch (JSONException e) {
			// TODO: handle exception
		}    
        ArrayList<Dependency> reqlist =  new ArrayList<>();
        try {
        	JSONArray reqs = orign.getJSONArray("requirements");
            for (Object req : reqs) {
				reqlist.add(parseRequirement(req));
			}
		} catch (JSONException e) {
			Dependency req = new Dependency();
			req.setName("puppet");
			reqlist.add(req);
		}
        String nm =  orign.getString("name").replace("/", "-");
        String[] cms = nm.split("-");
        String cm="";
        if (cms.length>1) {
			cm = cms[1];
		}else {
			cm = cms[0];
		}
        HashSet<String> taglist = new HashSet<>();
        
        mabuilder.name(nm)
        .type("puppet")
        .component(cm)
        .path(dir)
        .author(orign.getString("author"))
        .version(parseVersion(orign.getString("version")))
        .summary(orign.getString("summary"))
        .dependencies(depend)
        .os_support(oslist)
        .requirements(reqlist);
        Tags tag = alltags.get(nm);
        if (tag !=null) {
			if ("".equals(tag.getTags())) {
				taglist.addAll(Arrays.asList(tag.getTags().split("/")));
			}
			mabuilder.tags(taglist)
			.download(tag.getDownload())
	        .alldown(tag.getAlldown())
	        .feedback(tag.getFeedback())
	        .score(tag.getScore())
	        .vernum(tag.getVernum())
	        .standard(tag.getStandard());
		}
		return mabuilder.build();
	}
	/**parser jsonobject of dependency to Object Dependency
	 * using ReqExp match
	 * @param o
	 * @return
	 */
	private Dependency parseDependency(Object o){
		Dependency result = new Dependency();
		JSONObject t = (JSONObject)o;
		result.setName(t.getString("name").replace("/", "-").toLowerCase());
		String version=null;
		try {
			version = t.getString("version_requirement");
		} catch (Exception e) {
			System.err.println("JSONObject[version_requirement] not found.");
		} 
		if (version != null) {
			Matcher m = VERSION.matcher(version);
			if(m.find()){
				result.setDownversion(parseVersion(m.group(0)));
			}
			if(m.find()){
				result.setUpversion(parseVersion(m.group(0)));
			}
		}
		return result;
	}
	/**fetch OS Meta that the script HOST_ON 
	 * @param o 
	 * @return
	 */
	private Dependency parseOs(Object o){
		Dependency result = new Dependency();
		JSONObject t = (JSONObject)o;
		result.setName(t.getString("operatingsystem").toLowerCase());
		JSONArray oslist = t.getJSONArray("operatingsystemrelease");
		if(oslist.length() > 0){
			result.setDownversion(parseOSVersion(oslist.getString(0)));
			result.setUpversion(parseOSVersion(oslist.getString(oslist.length()-1)));
		}
		return result;
	}
	/**fetch CMT Tools Meta that the script requires 
	 * @param o 
	 * @return
	 */
	private Dependency parseRequirement(Object o){
		Dependency result = new Dependency();
		JSONObject t = (JSONObject)o;
		result.setName(t.getString("name").toLowerCase());
		String version = t.getString("version_requirement");
		Matcher m = VERSION.matcher(version);
		if(m.find()){
			result.setDownversion(parseVersion(m.group(0)));
		}
		if(m.find()){
			result.setUpversion(parseVersion(m.group(0)));
		}
		return result;
	}
	/**parse String of OS version to long in order to compare
	 * @param version in form of 6.5,or 6 ,12.04.in two segment numbers or something like this.
	 * @return
	 */
	private Long parseOSVersion(String version){
		Long result = 0l;
		Matcher m = OS_V.matcher(version);
		if(m.find()){
			result+=Integer.valueOf(m.group(0));
		}
		result = result<<14;//每14位表示版本号的段,
		if(m.find()){
			result+=Integer.valueOf(m.group(0));
		}
		return result;
	}
	/**parse String of recipe version to long in order to compare
	 * @param version in form of 1.6.5,three segment numbers or something like this.
	 * @return
	 */
	private Long parseVersion(String version){
		Long result = 0l;
		String[] vv = version.split("\\.");
		for (String string : vv) {
			string = string.split("-")[0];
			result = result<<14;//每14位表示版本号的段,
			result+=Integer.valueOf(string);
		}
		return result;
	}
}
