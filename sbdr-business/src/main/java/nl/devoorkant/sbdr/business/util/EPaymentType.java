package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EPaymentType {
    ONE_OFF_PAYMENT(0, "Eenmalige betaling"),
    RECURRENT_PAYMENT(1, "Terugkerende betaling");

    private static final Map<Integer, EPaymentType> lookup = new HashMap<Integer, EPaymentType>();

    static {
        for (EPaymentType g : EnumSet.allOf(EPaymentType.class)) {
            lookup.put(g.getCode(), g);
        }
    }

	private Integer code;
    private String omschrijving;
    
	EPaymentType(Integer code, String omschrijving) {
		this.code = code;
        this.omschrijving = omschrijving;       
	}

    public Integer getCode() {
        return code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public static EPaymentType get(Integer code) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(code)) {
        	return lookup.get(code);
        }
        return null;
    }
}

