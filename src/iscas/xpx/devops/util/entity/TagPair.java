package iscas.xpx.devops.util.entity;

import java.util.Comparator;

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
public class TagPair {
	public static final String Comparetor = null;
	@Setter @Getter private String bigger;
	@Setter @Getter private String smaller;
	@Setter @Getter private int count;
	@Setter @Getter private float support;
	@Setter @Getter private float confidence;
	
	public String toString(){
		return bigger+"\t"+smaller+"\t"+count+"\t"+support+"\t"+confidence+"\n";
	}
	public static class TagComparetor implements Comparator<TagPair>{

		@Override
		public int compare(TagPair o1, TagPair o2) {
			if (o1.confidence -o2.confidence>0) {
				return -1;
			}else {
				return 1;
			}
		}
		
	}
}           
