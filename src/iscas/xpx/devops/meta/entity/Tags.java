package iscas.xpx.devops.meta.entity;

import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name="modules")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tags {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private Long id;
	@Getter @Setter private String source;
	@Getter @Setter private String name;
	@Getter @Setter private String module;
	@Getter @Setter private int score;
	@Getter @Setter private int feedback;
	@Getter @Setter private String tags;
	@Transient 
	private HashSet<String> taglist;
	@Getter @Setter private int vernum;
	@Getter @Setter private int download;
	@Getter @Setter private int alldown;
	/**
	 * 0 normal£¬1£¬approved 2 supported
	 */
	@Getter @Setter private int standard;
	
	public String genKey(){
		return source+"-"+name;
	}

	public HashSet<String> getTaglist() {
		HashSet<String> taglist = new HashSet<>(8);
		for (String string : tags.split("/")) {
			taglist.add(string);
		}
		this.taglist = taglist;
		return taglist;
	}
	
}
