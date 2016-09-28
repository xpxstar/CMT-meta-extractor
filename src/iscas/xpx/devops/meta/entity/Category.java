package iscas.xpx.devops.meta.entity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
	@Setter @Getter String value;
	@Setter @Getter String path;
	@Setter @Getter int size;
	@Setter @Getter int level;
	@Setter @Getter Category parent;
	@Setter @Getter ArrayList<Category> children;
	@Setter @Getter List<TrainData> datas;
	@Setter @Getter List<TestData> testDatas;
//	@Setter @Getter List<TrainData> others;
	public Category(String value) {
		super();
		this.value = value;
	}
	public void addChilren(Category child) {
		if (null == children) {
			children = new ArrayList<>();
			children.add(child);
		}else {
			int i = 0;
			for (; i < children.size(); i++) {
				if (child.size>children.get(i).size) {
					break;
				}
			}
			children.add(i, child);
		}
	}
	public void removeChild(Category child) {
		children.remove(child);
	}
	
	
	
	
}
