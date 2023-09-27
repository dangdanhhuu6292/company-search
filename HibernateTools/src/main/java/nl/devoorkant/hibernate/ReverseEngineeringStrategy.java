package nl.devoorkant.hibernate;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.ForeignKey;

import java.util.List;

public class ReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {
	static {
		System.out.println("***** ReverseEngineeringStrategy static 1");
	}

	public ReverseEngineeringStrategy(org.hibernate.cfg.reveng.ReverseEngineeringStrategy delegate) {
		super(delegate);
	}

	//    @Override
	//    public String foreignKeyToEntityName(String keyname, TableIdentifier fromTable, List fromColumnNames, TableIdentifier referencedTable, List referencedColumnNames, boolean uniqueReference) {
	//        String result = super.foreignKeyToEntityName(keyname, fromTable, fromColumnNames, referencedTable, referencedColumnNames, uniqueReference);
	//
	//        return result;
	//    }

	//    @Override
	//    public String tableToCompositeIdName(TableIdentifier identifier) {
	//        String result = super.tableToCompositeIdName(identifier);
	//
	//        System.out.println("* tableToCompositeIdName: " + result);
	//
	//        return result;
	//    }

	@Override /**/
	public String foreignKeyToCollectionName(String keyname, TableIdentifier fromTable, List fromColumns, TableIdentifier referencedTable, List referencedColumns, boolean uniqueReference) {
		String result = super.foreignKeyToCollectionName(keyname, fromTable, fromColumns, referencedTable, referencedColumns, uniqueReference);

		System.out.println("* ReverseEngineeringStrategy.foreignKeyToCollectionName" + "-- keyname = [" + keyname + "], fromTable = [" + fromTable + "], fromColumns = [" + fromColumns + "], referencedTable = [" + referencedTable + "], referencedColumns = [" + referencedColumns + "], uniqueReference = [" + uniqueReference + "]: " + result);

		result = pluralizeDutch(result);

		return result;
	}

	@Override
	public String tableToClassName(TableIdentifier tableIdentifier) {
		String result = super.tableToClassName(tableIdentifier);

		result = camelCase(result);

		System.out.println("* ReverseEngineeringStrategy.tableToClassName" + "-- tableIdentifier = [" + tableIdentifier + "]: " + result);

		return result;
	}

	@Override
	public String tableToIdentifierPropertyName(TableIdentifier tableIdentifier) {
		String result = super.tableToIdentifierPropertyName(tableIdentifier);

		System.out.println("* ReverseEngineeringStrategy.tableToIdentifierPropertyName" + "-- tableIdentifier = [" + tableIdentifier + "]: " + result);

		result = camelCase(result);

		return result;
	}

	@Override
	public String tableToCompositeIdName(TableIdentifier identifier) {
		String result = super.tableToCompositeIdName(identifier);

		System.out.println("* ReverseEngineeringStrategy.tableToCompositeIdName" + "-- identifier = [" + identifier + "]: " + result);

		result = camelCase(result);

		return result;
	}

	@Override /**/ public String foreignKeyToManyToManyName(ForeignKey fromKey, TableIdentifier middleTable, ForeignKey toKey, boolean uniqueReference) {
		String result = super.foreignKeyToManyToManyName(fromKey, middleTable, toKey, uniqueReference);

		System.out.println("* ReverseEngineeringStrategy.foreignKeyToManyToManyName" + "-- fromKey = [" + fromKey + "], middleTable = [" + middleTable + "], toKey = [" + toKey + "], uniqueReference = [" + uniqueReference + "]:" + result);

		result = pluralizeDutch(result);

		return result;
	}

	private String camelCase(String value) {
		String result = value;

		if("nl.devoorkant.sbdr.data.model.Alertview".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.AlertView";
		if("nl.devoorkant.sbdr.data.model.Bedrijfhistorie".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.BedrijfHistorie";
		if("nl.devoorkant.sbdr.data.model.Bedrijfrol".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.BedrijfRol";
		if("nl.devoorkant.sbdr.data.model.Bedrijfstatus".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.BedrijfStatus";
		//if ("nl.devoorkant.sbdr.data.model.Cikvkdossier".equalsIgnoreCase(value))
			//result = "nl.devoorkant.sbdr.data.model.CIKvKDossier";
		//if ("nl.devoorkant.sbdr.data.model.Cikvkdossierhistorie".equalsIgnoreCase(value))
			//result = "nl.devoorkant.sbdr.data.model.CIKvKDossierHistorie";
		if("nl.devoorkant.sbdr.data.model.CikvKdossier".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CIKvKDossier";
		if("nl.devoorkant.sbdr.data.model.CikvKdossierHistorie".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CIKvKDossierHistorie";
		if("nl.devoorkant.sbdr.data.model.Companyinfo".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CompanyInfo";
		if("nl.devoorkant.sbdr.data.model.Factuurregel".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.FactuurRegel";
		if("nl.devoorkant.sbdr.data.model.Gebruikerbasis".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.GebruikerBasis";
		if("nl.devoorkant.sbdr.data.model.Gebruikereigenschap".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.GebruikerEigenschap";
		if("nl.devoorkant.sbdr.data.model.Gebruikergroep".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.GebruikerGroep";
		if("nl.devoorkant.sbdr.data.model.Klantstatus".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.KlantStatus";
		if("nl.devoorkant.sbdr.data.model.Klanttype".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.KlantType";
		if("nl.devoorkant.sbdr.data.model.Meldingstatus".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.MeldingStatus";
		if("nl.devoorkant.sbdr.data.model.Monitoringstatus".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.MonitoringStatus";
		if("nl.devoorkant.sbdr.data.model.RemovedBedrijf".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.RemovedBedrijf";
		if("nl.devoorkant.sbdr.data.model.SearchResult".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.SearchResult";
		if("nl.devoorkant.sbdr.data.model.Wachtwoordstatus".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.WachtwoordStatus";
		if("nl.devoorkant.sbdr.data.model.CikvKbestuurder".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CIKvKBestuurder";
		if("nl.devoorkant.sbdr.data.model.CikvKbestuurderFunctie".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CIKvKBestuurderFunctie";
		if("nl.devoorkant.sbdr.data.model.CikvKhistorieBestuurder".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CIKvKHistorieBestuurder";
		if("nl.devoorkant.sbdr.data.model.CikvKhistorieBestuurderFunctie".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CIKvKHistorieBestuurderFunctie";
		if("nl.devoorkant.sbdr.data.model.CikvKaandeelhouder".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CIKvKAandeelhouder";
		if("nl.devoorkant.sbdr.data.model.CikvKcurator".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CIKvKCurator";
		if("nl.devoorkant.sbdr.data.model.CikvKhistorieAandeelhouder".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CIKvKHistorieAandeelhouder";
		if("nl.devoorkant.sbdr.data.model.CikvKhistorieCurator".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.CIKvKHistorieCurator";
		if("nl.devoorkant.sbdr.data.model.MobileGebruiker".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.MobileGebruiker";
		if("nl.devoorkant.sbdr.data.model.BedrijfManaged".equalsIgnoreCase(value))
			result = "nl.devoorkant.sbdr.data.model.BedrijfManaged";
		
		return result;
	}

	private String pluralizeDutch(String value) {
		String result = value;

		if("bedrijfs".equals(value)) result = "bedrijven";
		if("bedrijfRols".equals(value)) result = "bedrijfRollen";
		if("bevoegdheids".equals(value)) result = "bevoegdheden";
		if("factuurs".equals(value)) result = "facturen";
		if("gebruikerEigenschaps".equals(value)) result = "gebruikerEigenschappen";
		if("gebruikerGroeps".equals(value)) result = "gebruikerGroepen";
		if("klants".equals(value)) result = "klanten";
		if("meldings".equals(value)) result = "meldingen";
		if("meldingsForGeaccordeerdDoorGebruikerId".equals(value)) result = "meldingenVoorGeaccordeerdDoorGebruikerId";
		if("meldingsForMeldingDoorGebruikerId".equals(value)) result = "meldingenVoorMeldingDoorGebruikerId";
		if("meldingsForVerwijderdDoorGebruikerId".equals(value)) result = "meldingenVoorVerwijderdDoorGebruikerId";
		if("monitorings".equals(value)) result = "monitoringen";
		if("persoons".equals(value)) result = "personen";
		if("rols".equals(value)) result = "rollen";
		if("tariefs".equals(value)) result = "tarieven";
		if("wachtwoords".equals(value)) result = "wachtwoorden";
		if("batchs".equals(value)) result = "batches";
		if("bedrijfManagedsForGebruikerId".equals(value)) result = "bedrijvenManagedDoorGebruikerId";

		return result;
	}

	//    @Override
	//    public String foreignKeyToInverseEntityName(String keyname, TableIdentifier fromTable, List fromColumnNames, TableIdentifier referencedTable, List referencedColumnNames, boolean uniqueReference) {
	//        String result = super.foreignKeyToInverseEntityName(keyname, fromTable, fromColumnNames, referencedTable, referencedColumnNames, uniqueReference);
	//
	//        System.out.println("ReverseEngineeringStrategy.foreignKeyToInverseEntityName" + "keyname = [" + keyname + "], fromTable = [" + fromTable + "], fromColumnNames = [" + fromColumnNames + "], referencedTable = [" + referencedTable + "], referencedColumnNames = [" + referencedColumnNames + "], uniqueReference = [" + uniqueReference + "]: " + result);
	//
	//        return result;
	//    }
}
