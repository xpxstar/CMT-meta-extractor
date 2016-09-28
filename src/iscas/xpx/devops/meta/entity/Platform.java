package iscas.xpx.devops.meta.entity;

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
public class Platform {
	@Getter @Setter private String release;
	@Getter @Setter private String name;
}
