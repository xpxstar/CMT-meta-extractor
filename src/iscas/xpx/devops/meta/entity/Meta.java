package iscas.xpx.devops.meta.entity;

import java.util.HashSet;
import java.util.List;

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
public class Meta {
	@Getter @Setter private String name;
	@Getter @Setter private String type;
	@Getter @Setter private String component;
	@Getter @Setter private String author;
	@Getter @Setter private Long version;
	@Getter @Setter private String summary;
	@Getter @Setter private String path;
	@Getter @Setter private int vernum;
	@Getter @Setter private int score;
	@Getter @Setter private int standard;
	@Getter @Setter private int feedback;
	@Getter @Setter private int download;
	@Getter @Setter private int alldown;
	@Getter @Setter private HashSet<String> tags;
	@Getter @Setter private List<Dependency> dependencies;
	@Getter @Setter private List<Dependency> os_support;
	@Getter @Setter private List<Dependency> requirements;
	public String toCsv(){
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(',');
		sb.append(summary);
		sb.append(',');
		if (null != tags) {
			sb.append(String.join(" ", tags));
		}
		return sb.toString();
	}
	public String toCsvShort(){
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(',');
		sb.append(summary);
		return sb.toString();
	}
	
}
