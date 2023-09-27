package nl.devoorkant.sbdr.business.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EExactOnlineRestApi {
	CurrentDivision("ExactOnlineCurrentDivisionRequest", "/Me?$select=CurrentDivision,FullName,Email", "GET"),
	StoreAccount("ExactOnlineStoreAccountRequest", "/crm/Accounts", "POST"),
	StoreContact("ExactOnlineStoreContactRequest", "/crm/Contacts", "POST"),
	StoreBankAccount("ExactOnlineStoreBankAccountRequest", "/crm/BankAccounts", "POST"),
	GLAccountSalesSBDR("ExactOnlineGLAccountSalesSBDRRequest", "/financial/GLAccounts?$filter=Code eq '8000'&$select=ID,BalanceSide,BalanceType,Code,VATCode", "GET"),
	StoreSalesEntry("ExactOnlineStoreSalesEntry", "/salesentry/SalesEntries", "POST"),
	VatCodes("ExactOnlineVatCodesRequest", "/vat/VATCodes?select=ID, Code,Description,Percentage", "GET"),
	//GetDirectDebitMandates("ExactOnlineDirectDebitMandatesRequest", "/cashflow/DirectDebitMandates?filter=ID eq guid':id'&select Account", "GET"),
	StoreDirectDebitMandates("ExactOnlineStoreDirectDebitMandatesRequest", "/cashflow/DirectDebitMandates", "POST");
	

    private static final Map<String, EExactOnlineRestApi> lookup = new HashMap<String, EExactOnlineRestApi>();

    static {
        for (EExactOnlineRestApi g : EnumSet.allOf(EExactOnlineRestApi.class)) {
            lookup.put(g.getCallId(), g);
        }
    }

	private String callId;
    private String path;
    private String type;
    
	EExactOnlineRestApi(String callId, String path, String type) {
		this.callId = callId;
		this.path = path;
        this.type = type;       
	}

    public String getCallId() {
        return callId;
    }
    
    public String getPath() {
    	return path;
    }

    public String getType() {
        return type;
    }

    public static EExactOnlineRestApi get(String callId) {

        // Check if the key exists in the look-up table.
        if (lookup.containsKey(callId)) {
        	return lookup.get(callId);
        }
        return null;
    }
}
