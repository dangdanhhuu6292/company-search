package nl.devoorkant.sbdr.data.model;
// Generated Feb 27, 2017 3:05:41 PM by Hibernate Tools 3.2.4.GA


import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * BriefBatch generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="BriefBatch")
@Table(name="BriefBatch"
)
public class BriefBatch  implements java.io.Serializable {


     private Integer briefBatchId;
     private BriefBatchType briefBatchType;
     private Gebruiker gebruiker;
     private BriefBatchStatus briefBatchStatus;
     private Date datumAangemaakt;
     private Date datumVoltooid;
     private Set<Document> documents = new HashSet<Document>(0);
     private Set<Klant> klanten = new HashSet<Klant>(0);
     private Set<Melding> meldingen = new HashSet<Melding>(0);
     private Set<InternalProcess> internalProcesses = new HashSet<InternalProcess>(0);

    public BriefBatch() {
    }

   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="BriefBatch_ID", unique=true, nullable=false)
    public Integer getBriefBatchId() {
        return this.briefBatchId;
    }
    
    public void setBriefBatchId(Integer briefBatchId) {
        this.briefBatchId = briefBatchId;
    }


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="BriefBatchType", nullable=false)
    public BriefBatchType getBriefBatchType() {
        return this.briefBatchType;
    }
    
    public void setBriefBatchType(BriefBatchType briefBatchType) {
        this.briefBatchType = briefBatchType;
    }



    @Transient
    public String getBriefBatchTypeCode() {
        return this.briefBatchType == null ? null : this.briefBatchType.getCode();
    }

    public void setBriefBatchTypeCode(String code) {
        if (code == null) {
            this.briefBatchType = null;
        } else {
            BriefBatchType obj = new BriefBatchType();
            obj.setCode(code);
            this.briefBatchType = obj;
        }
    }


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="GemaaktDoorGebruiker_ID", nullable=false)
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


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="BriefBatchStatus", nullable=false)
    public BriefBatchStatus getBriefBatchStatus() {
        return this.briefBatchStatus;
    }
    
    public void setBriefBatchStatus(BriefBatchStatus briefBatchStatus) {
        this.briefBatchStatus = briefBatchStatus;
    }



    @Transient
    public String getBriefBatchStatusCode() {
        return this.briefBatchStatus == null ? null : this.briefBatchStatus.getCode();
    }

    public void setBriefBatchStatusCode(String code) {
        if (code == null) {
            this.briefBatchStatus = null;
        } else {
            BriefBatchStatus obj = new BriefBatchStatus();
            obj.setCode(code);
            this.briefBatchStatus = obj;
        }
    }


// @org.codehaus.jackson.map.annotate.JsonSerialize(using=nl.devoorkant.bdr.jackson.JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DatumAangemaakt", nullable=false, length=19)
    public Date getDatumAangemaakt() {
        return this.datumAangemaakt;
    }
    
    public void setDatumAangemaakt(Date datumAangemaakt) {
        this.datumAangemaakt = datumAangemaakt;
    }


// @org.codehaus.jackson.map.annotate.JsonSerialize(using=nl.devoorkant.bdr.jackson.JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DatumVoltooid", length=19)
    public Date getDatumVoltooid() {
        return this.datumVoltooid;
    }
    
    public void setDatumVoltooid(Date datumVoltooid) {
        this.datumVoltooid = datumVoltooid;
    }


@XmlTransient
@JsonIgnore
@OneToMany(fetch=FetchType.LAZY, mappedBy="briefBatch")
    public Set<Document> getDocuments() {
        return this.documents;
    }
    
    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }


@XmlTransient
@JsonIgnore
@OneToMany(fetch=FetchType.LAZY, mappedBy="briefBatch")
    public Set<Klant> getKlanten() {
        return this.klanten;
    }
    
    public void setKlanten(Set<Klant> klanten) {
        this.klanten = klanten;
    }


@XmlTransient
@JsonIgnore
@OneToMany(fetch=FetchType.LAZY, mappedBy="briefBatch")
    public Set<Melding> getMeldingen() {
        return this.meldingen;
    }
    
    public void setMeldingen(Set<Melding> meldingen) {
        this.meldingen = meldingen;
    }


@XmlTransient
@JsonIgnore
@OneToMany(fetch=FetchType.LAZY, mappedBy="briefBatch")
    public Set<InternalProcess> getInternalProcesses() {
        return this.internalProcesses;
    }
    
    public void setInternalProcesses(Set<InternalProcess> internalProcesses) {
        this.internalProcesses = internalProcesses;
    }





}


