package nl.devoorkant.creditsafe.converter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEventType {
	WERKNEMERS_NO("Aantal werknemers gewijzigd", "Aantal werknemers is gewijzigd"),
	MOBIEL_NO("Mobiel telefoonnummer gewijzigd", "Mobiel telefoonnummer is gewijzigd"),
	WEBSITE("Website adres gewijzigd", "Website adres is gewijzigd"),
	POST_HUISNR("Huisnummer gewijzigd (postadres)", "Huisnummer van postadres is gewijzigd"),
	POST_POSTCODE("Postcode gewijzigd (postadres)", "Postcode van postadres is gewijzigd"),
	POST_PLAATS("Plaats gewijzigd (postadres)", "Plaats van postadres is gewijzigd"),
	POST_HUISNR_TOEVOEGING("Huisnummer toevoeging gewijzigd (postadres)", "Huisnummer toevoeging van postadres is gewijzigd"),
	POST_STRAAT("Straat gewijzigd (postadres)", "Straat van postadres is gewijzigd"),
	POSTCODE("Postcode gewijzigd", "Postcode is gewijzigd"),
	PLAATS("Plaats gewijzigd", "Plaats is gewijzigd"),
	HUISNNR_TOEVOEGING("Huisnummer toevoeging gewijzigd", "Huisnummer toevoeging is gewijzigd"),
	HUISNR("Huisnummer gewijzigd", "Huisnummer is gewijzigd"),
	STRAAT("Straat gewijzigd", "Straat is gewijzigd"),
	JAARVERSLAGDEPOT("Nieuwe jaarverslagen gedeponeerd", "Er zijn nieuwe jaarverslagen gedeponeerd"),
	OPHEFFING("Opheffingsdatum gewijzigd", "De opheffingsdatum is gewijzigd"),
	TELEFOON("Telefoonnummer gewijzigd", "Het telefoonnummer is gewijzigd"),
	JAARVERSLAGVERWERKT("Nieuwe jaarverslagen verwerkt", "Er zijn nieuwe jaarverslagen verwerkt"),
	OPRICHTING("Datum oprichting gewijzigd", "De datum van oprichting is gewijzigd"),
	ANDERS("", "Overig");

	private String code;
	private String omschrijving;

	private static final Map<String, EEventType> lookup = new HashMap<>();

	static {
		for(EEventType e : EnumSet.allOf(EEventType.class)) {
			lookup.put(e.getCode(), e);
		}
	}

	EEventType(String code, String omschrijving) {
		this.code = code;
		this.omschrijving = omschrijving;
	}

	public static EEventType get(String code) {
		if(lookup.containsKey(code)) {
			return lookup.get(code);
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	public String getOmschrijving() {
		return omschrijving;
	}
}