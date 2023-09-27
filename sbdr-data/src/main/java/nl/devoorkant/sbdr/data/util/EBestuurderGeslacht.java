package nl.devoorkant.sbdr.data.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EBestuurderGeslacht {
	ONBEKEND((byte)0, "Onbekend"),
	MAN((byte)1, "Man"),
	VROUW((byte)2, "Vrouw");

	private static final Map<Byte, EBestuurderGeslacht> lookup = new HashMap<Byte, EBestuurderGeslacht>();

	static{
		for(EBestuurderGeslacht bgeslacht : EnumSet.allOf(EBestuurderGeslacht.class)){
			lookup.put(bgeslacht.getCode(), bgeslacht);
		}
	}

	private Byte code;
	private String omschrijving;

	EBestuurderGeslacht(Byte code, String omschrijving){
		this.code = code;
		this.omschrijving = omschrijving;
	}

	public Byte getCode(){return code;}
	public String getOmschrijving(){return omschrijving;}

	public static EBestuurderGeslacht get(Byte code){
		if(lookup.containsKey(code)){
			return lookup.get(code);
		}
		return null;
	}
}
