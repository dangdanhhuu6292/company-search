package nl.devoorkant.sbdr.data.specification;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import nl.devoorkant.sbdr.data.model.Bedrijf;

/**
 * A  meta model class used to create type safe queries from bedrijf
 * information.
 */
@StaticMetamodel(Bedrijf.class)
public class Bedrijf_ {
	public static volatile SingularAttribute<Bedrijf, String> bedrijfsNaam;
	public static volatile SingularAttribute<Bedrijf, String> kvkNummer;	
}
