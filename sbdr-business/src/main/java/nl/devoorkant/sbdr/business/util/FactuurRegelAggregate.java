package nl.devoorkant.sbdr.business.util;

import java.math.BigDecimal;

public class FactuurRegelAggregate {
	private String productCode = null;
	private String productOmschrijving = null;
	private int aantal = 0;
	private BigDecimal bedragNetto = null;
	private BigDecimal bedragBruto = null;
	private BigDecimal totaalBedragNetto = null;
	private BigDecimal totaalBedragBruto = null;
	
	
	public FactuurRegelAggregate(String productCode, String productOmschrijving, int aantal, BigDecimal bedragNetto, BigDecimal bedragBruto, BigDecimal totaalBedragNetto, BigDecimal totaalBedragBruto) {
		this.productCode = productCode;
		this.productOmschrijving = productOmschrijving;
		this.aantal = aantal;
		this.bedragBruto = bedragBruto;
		this.bedragNetto = bedragNetto;
		this.totaalBedragBruto = totaalBedragBruto;
		this.totaalBedragNetto = totaalBedragNetto;
	}

	public String getProductOmschrijving() {
		return productOmschrijving;
	}

	public void setProductOmschrijving(String productOmschrijving) {
		this.productOmschrijving = productOmschrijving;
	}

	public int getAantal() {
		return aantal;
	}

	public void setAantal(int aantal) {
		this.aantal = aantal;
	}

	public BigDecimal getBedragNetto() {
		return bedragNetto;
	}

	public void setBedragNetto(BigDecimal bedragNetto) {
		this.bedragNetto = bedragNetto;
	}

	public BigDecimal getBedragBruto() {
		return bedragBruto;
	}

	public void setBedragBruto(BigDecimal bedragBruto) {
		this.bedragBruto = bedragBruto;
	}

	public BigDecimal getTotaalBedragNetto() {
		return totaalBedragNetto;
	}

	public void setTotaalBedragNetto(BigDecimal totaalBedragNetto) {
		this.totaalBedragNetto = totaalBedragNetto;
	}

	public BigDecimal getTotaalBedragBruto() {
		return totaalBedragBruto;
	}

	public void setTotaalBedragBruto(BigDecimal totaalBedragBruto) {
		this.totaalBedragBruto = totaalBedragBruto;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	
}
