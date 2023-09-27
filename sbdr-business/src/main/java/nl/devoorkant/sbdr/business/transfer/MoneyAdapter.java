package nl.devoorkant.sbdr.business.transfer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MoneyAdapter extends XmlAdapter<String, BigDecimal> {

    // the desired format
    private DecimalFormat nf = null;

    public MoneyAdapter() {
    	DecimalFormatSymbols nfs = new DecimalFormatSymbols();
    	
    	nfs.setGroupingSeparator(',');
    	nfs.setDecimalSeparator('.');
    	String pattern = "#,##0.0#";
    	
    	nf = new DecimalFormat(pattern, nfs);
    	nf.setParseBigDecimal(true);
    }
    
    public String marshal(BigDecimal money) throws Exception {
    	//return nf.format(money);     
    	if (money != null)
    		return money.toString();
    	else
    		return null;
    }

    public BigDecimal unmarshal(String moneyString) throws Exception {

		return (BigDecimal) nf.parse(moneyString);
    }   
}
