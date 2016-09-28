package iscas.xpx.devops.extract.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.map.ObjectMapper;

import iscas.xpx.devops.meta.entity.Dependency;
import iscas.xpx.devops.meta.entity.Meta;
import iscas.xpx.devops.meta.entity.Meta.MetaBuilder;
import iscas.xpx.devops.meta.entity.Platform;
import iscas.xpx.devops.meta.entity.Role;
import iscas.xpx.devops.meta.entity.RoleDependency;
import iscas.xpx.devops.meta.entity.Tags;
import iscas.xpx.devops.meta.entity.Version;

public class AnsibleMetaExtract {

	private static  Pattern VERSION = Pattern.compile("\\d\\.\\d\\.\\d");
	private static  Pattern OS_V = Pattern.compile("\\d+");
	ObjectMapper objectMapper = new ObjectMapper();
	
	/** extract ansible metadata.json into Meta Object
	 * @see iscas.xpx.devops.extract.MetaExtract#extract(java.lang.String)
	 */
	public Meta extract(String line) throws IOException {
		MetaBuilder mabuilder = Meta.builder();
		Role role = objectMapper.readValue(line, Role.class);
		mabuilder.name(role.getName())
	       .type("ansible")
	       .author(role.getUsername())
	       .download(role.getDownload_count())
	       .component(role.getName())
	       .path(role.getGithub_repo())
	       .author(role.getUsername())
//	       .version(parseVersion(role.getVersions()))
	       .summary(role.getDescription())
	       .tags(role.getTags());
//	       .dependencies(parseDependency(role.getDependencies()))
//	       .os_support(parseOs(role.getPlatform_details()))
//	       .requirements(parseRequirement(role.getMin_ansible_version()));
        
		return mabuilder.build();
	}
	/**parser jsonobject of dependency to Object Dependency
	 * using ReqExp match
	 * @param o
	 * @return
	 */
	private List<Dependency> parseDependency(List<RoleDependency> depens){
		List<Dependency> result = new ArrayList<>();
		for (RoleDependency dependency : depens) {
			result.add(new Dependency(dependency.getNamespace()+'-'+dependency.getName(), 0l, 0l));
		}
		return result;
	}
	/**fetch OS Meta that the script HOST_ON 
	 * @param o 
	 * @return
	 */
	private List<Dependency> parseOs(List<Platform> platforms){
		List<Dependency> result = new ArrayList<Dependency>();
		for (Platform platform : platforms) {
			result.add(new Dependency(platform.getName(),0l, 0l));
		}
		return result;
	}
	/**fetch CMT Tools Meta that the script requires 
	 * @param o 
	 * @return
	 */
	private List<Dependency> parseRequirement(String version){
		List<Dependency> result = new ArrayList<>();
		Dependency den = new Dependency("ansible",0l,parseVersion(version));
		result.add(den);
		return result;
	}
	/**parse String of OS version to long in order to compare
	 * @param version in form of 6.5,or 6 ,12.04.in two segment numbers or something like this.
	 * @return
	 */
	private Long parseVersion(String version){
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
	private Long parseVersion(List<Version> versions){
		Long result = 0l;
		if (null == versions || versions.size()<1) {
			return result;
		}
		String[] vv = versions.get(0).getName().split("\\.");
		for (String string : vv) {
			string = string.split("-")[0];
			result = result<<14;//每14位表示版本号的段,
			result+=Integer.valueOf(string);
		}
		return result;
	}
}
