package nl.devoorkant.sbdr.business.util;

import java.util.HashMap;
import java.util.Map;

public enum EBedrijfUitvoeringStatus {
    NIET_ACTIEF("", "In surseance van betaling of in faillissement"), // Bedrijfstatus was 'Niet actief'
    ONTBONDEN("Ontbonden", "De bedrijfsvoering is ontbonden"), // Bedrijfstatus
    OPGEHEVEN("Opgeheven", "De bedrijfsvoering is opgeheven"), // Bedrijfstatus
    UITGESCHREVEN("Uitgeschreven", "Het bedrijf uitgeschreven bij KvK"), // Bedrijfstatus
    ACTIEF("Actief", "Bedrijf is actief"), // Bedrijfstatus
    //FAILLIET_SURSEANCE("Surseance + Faillissement", "Het bedrijf is in surseance van betaling en in faillissement"), // Failliet_Surseance
    //FAILLIET("Faillissement", "Het bedrijf is in faillissement"), // Failliet_Surseance
    //SURSEANCE("Surseance", "Het bedrijf is in surseance van betaling"), // Failliet_Surseance
    FAILLIET_SURSEANCE("", "Ja"), // Failliet_Surseance
    FAILLIET("", "Ja"), // Failliet_Surseance
    SURSEANCE("", "Ja"), // Failliet_Surseance
    NIET_FAILLIET_SURSEANCE("Nee", "Geen bijzonderheden"); // Failliet_Surseance

    private static final Map<String, EBedrijfUitvoeringStatus> lookup = new HashMap<String, EBedrijfUitvoeringStatus>();

	private String korteOmschrijving;
    private String omschrijving;
    
	EBedrijfUitvoeringStatus(String korteOmschrijving, String omschrijving) {
		this.korteOmschrijving = korteOmschrijving;
        this.omschrijving = omschrijving;       
	}

    public String getKorteOmschrijving() {
        return korteOmschrijving;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

}

