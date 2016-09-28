package iscas.xpx.devops.meta.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="synrelation")
public class Synonyms {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter @Getter	Long id;
	@Setter @Getter String name;
	@Setter @Getter String synonyms;
}
