package nl.devoorkant.sbdr.data.model;
// Generated Feb 27, 2017 3:05:41 PM by Hibernate Tools 3.2.4.GA


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * CustomMelding generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="CustomMelding")
@Table(name="CustomMelding"
)
public class CustomMelding  implements java.io.Serializable {


     private Integer customMeldingId;
     private Bedrijf bedrijf;
     private CustomMeldingStatus customMeldingStatus;
     private Gebruiker gebruiker;
     private Date datumAangemaakt;
     private Date datumVerwerkt;
     private String signAchternaam;
     private String signAfdeling;
     private String signTelefoonNummer;
     private String signVoornaam;
     private boolean frauduleusBedrijf;
     private boolean dreigendFaillissement;
     private boolean incorrectGegeven;
     private String meldingDetails;
     private boolean faillissementVraag;

    public CustomMelding() {
    }

   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="CustomMelding_ID", unique=true, nullable=false)
    public Integer getCustomMeldingId() {
        return this.customMeldingId;
    }
    
    public void setCustomMeldingId(Integer customMeldingId) {
        this.customMeldingId = customMeldingId;
    }


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="Bedrijf_ID", nullable=false)
    public Bedrijf getBedrijf() {
        return this.bedrijf;
    }
    
    public void setBedrijf(Bedrijf bedrijf) {
        this.bedrijf = bedrijf;
    }



    @Transient
    public Integer getBedrijfBedrijfId() {
        return this.bedrijf == null ? null : this.bedrijf.getBedrijfId();
    }

    public void setBedrijfBedrijfId(Integer bedrijfId) {
        if (bedrijfId == null) {
            this.bedrijf = null;
        } else {
            Bedrijf obj = new Bedrijf();
            obj.setBedrijfId(bedrijfId);
            this.bedrijf = obj;
        }
    }


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CustomMeldingStatusCode", nullable=false)
    public CustomMeldingStatus getCustomMeldingStatus() {
        return this.customMeldingStatus;
    }
    
    public void setCustomMeldingStatus(CustomMeldingStatus customMeldingStatus) {
        this.customMeldingStatus = customMeldingStatus;
    }



    @Transient
    public String getCustomMeldingStatusCode() {
        return this.customMeldingStatus == null ? null : this.customMeldingStatus.getCode();
    }

    public void setCustomMeldingStatusCode(String code) {
        if (code == null) {
            this.customMeldingStatus = null;
        } else {
            CustomMeldingStatus obj = new CustomMeldingStatus();
            obj.setCode(code);
            this.customMeldingStatus = obj;
        }
    }


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="Gebruiker_ID")
    public Gebruiker getGebruiker() {
        return this.gebruiker;
    }
    
    public void setGebruiker(Gebruiker gebruiker) {
        this.gebruiker = gebruiker;
    }



    @Transient
    public Integer getGebruikerGebruikerId() {
        return this.gebruiker == null ? null : this.gebruiker.getGebruikerId();
    }

    public void setGebruikerGebruikerId(int gebruikerId) {
        Gebruiker obj = new Gebruiker();
        obj.setGebruikerId(gebruikerId);
        this.gebruiker = obj;
    }


// @org.codehaus.jackson.map.annotate.JsonSerialize(using=nl.devoorkant.bdr.jackson.JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DatumAangemaakt", length=19)
    public Date getDatumAangemaakt() {
        return this.datumAangemaakt;
    }
    
    public void setDatumAangemaakt(Date datumAangemaakt) {
        this.datumAangemaakt = datumAangemaakt;
    }


// @org.codehaus.jackson.map.annotate.JsonSerialize(using=nl.devoorkant.bdr.jackson.JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DatumVerwerkt", length=19)
    public Date getDatumVerwerkt() {
        return this.datumVerwerkt;
    }
    
    public void setDatumVerwerkt(Date datumVerwerkt) {
        this.datumVerwerkt = datumVerwerkt;
    }


    
    @Column(name="SignAchternaam", nullable=false, length=20)
    public String getSignAchternaam() {
        return this.signAchternaam;
    }
    
    public void setSignAchternaam(String signAchternaam) {
        this.signAchternaam = signAchternaam;
    }


    
    @Column(name="SignAfdeling")
    public String getSignAfdeling() {
        return this.signAfdeling;
    }
    
    public void setSignAfdeling(String signAfdeling) {
        this.signAfdeling = signAfdeling;
    }


    
    @Column(name="SignTelefoonNummer", length=100)
    public String getSignTelefoonNummer() {
        return this.signTelefoonNummer;
    }
    
    public void setSignTelefoonNummer(String signTelefoonNummer) {
        this.signTelefoonNummer = signTelefoonNummer;
    }


    
    @Column(name="SignVoornaam", length=100)
    public String getSignVoornaam() {
        return this.signVoornaam;
    }
    
    public void setSignVoornaam(String signVoornaam) {
        this.signVoornaam = signVoornaam;
    }


    
    @Column(name="FrauduleusBedrijf", nullable=false)
    public boolean isFrauduleusBedrijf() {
        return this.frauduleusBedrijf;
    }
    
    public void setFrauduleusBedrijf(boolean frauduleusBedrijf) {
        this.frauduleusBedrijf = frauduleusBedrijf;
    }


    
    @Column(name="DreigendFaillissement", nullable=false)
    public boolean isDreigendFaillissement() {
        return this.dreigendFaillissement;
    }
    
    public void setDreigendFaillissement(boolean dreigendFaillissement) {
        this.dreigendFaillissement = dreigendFaillissement;
    }


    
    @Column(name="IncorrectGegeven", nullable=false)
    public boolean isIncorrectGegeven() {
        return this.incorrectGegeven;
    }
    
    public void setIncorrectGegeven(boolean incorrectGegeven) {
        this.incorrectGegeven = incorrectGegeven;
    }


    
    @Column(name="MeldingDetails", length=2000)
    public String getMeldingDetails() {
        return this.meldingDetails;
    }
    
    public void setMeldingDetails(String meldingDetails) {
        this.meldingDetails = meldingDetails;
    }


    
    @Column(name="FaillissementVraag", nullable=false)
    public boolean isFaillissementVraag() {
        return this.faillissementVraag;
    }
    
    public void setFaillissementVraag(boolean faillissementVraag) {
        this.faillissementVraag = faillissementVraag;
    }





}


