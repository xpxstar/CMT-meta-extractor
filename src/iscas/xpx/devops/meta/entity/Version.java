package iscas.xpx.devops.meta.entity;

import java.util.List;

import iscas.xpx.devops.meta.entity.Meta.MetaBuilder;
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
public class Version {
	@Getter @Setter private String release_date;
	@Getter @Setter private String name;
}	
