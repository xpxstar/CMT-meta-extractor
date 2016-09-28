package iscas.xpx.devops.util.entity;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCount {
	public static final String Comparetor = null;
	@Setter @Getter private String tag;
	@Setter @Getter private int count;
	
	public String toString(){
		return tag+"\t"+count+"\n";
	}
	public static class TagCountComparetor implements Comparator<TagCount>{

		@Override
		public int compare(TagCount o1, TagCount o2) {
			// TODO Auto-generated method stub
			return o2.count - o1.count;
		}
		
	}
}           
