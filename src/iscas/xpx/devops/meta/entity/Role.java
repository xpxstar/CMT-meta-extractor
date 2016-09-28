package iscas.xpx.devops.meta.entity;

import java.util.HashSet;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {
	@Getter @Setter private int role_id;
	@Getter @Setter private String username;
	@Getter @Setter private String name;
	@Getter @Setter private String description;
	@Getter @Setter private String tags_autocomplete;
	@Getter @Setter private HashSet<String> tags;
	@Getter @Setter private List<String> platforms;
	@Getter @Setter private List<Platform> platform_details;
	@Getter @Setter private List<Version> versions;
	@Getter @Setter private List<RoleDependency> dependencies;
	@Getter @Setter private int download_count;
	@Getter @Setter private String min_ansible_version;
	@Getter @Setter private String github_repo;
	public String toCsv(){
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(',');
//		sb.append(description);
//		sb.append(',');
		sb.append(tags_autocomplete);
		return sb.toString();
	}
	
}