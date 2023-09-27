package nl.devoorkant.sbdr.cir.data.model;
// Generated Apr 14, 2016 7:26:47 PM by Hibernate Tools 3.2.4.GA


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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * CirCbv generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="CirCirCbv")
@Table(name="Cir_Cbv"
)
public class CirCbv  implements java.io.Serializable {


     private Integer id;
     private CirAdres cirAdres;
     private CirCbvtype cirCbvtype;
     private Date datumBegin;
     private Date datumEind;
     private String titulatuur;
     private String voorletters;
     private String voorvoegsel;
     private String naam;
     private Set<CirInsolventie> cirInsolventies = new HashSet<CirInsolventie>(0);

    public CirCbv() {
    }

   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="ID", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="Adres_ID", nullable=false)
    public CirAdres getCirAdres() {
        return this.cirAdres;
    }
    
    public void setCirAdres(CirAdres cirAdres) {
        this.cirAdres = cirAdres;
    }



    @Transient
    public Integer getCirAdresId() {
        return this.cirAdres == null ? null : this.cirAdres.getId();
    }

    public void setCirAdresId(Integer id) {
        if (id == null) {
            this.cirAdres = null;
        } else {
            CirAdres obj = new CirAdres();
            obj.setId(id);
            this.cirAdres = obj;
        }
    }


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CodeCBVType", nullable=false)
    public CirCbvtype getCirCbvtype() {
        return this.cirCbvtype;
    }
    
    public void setCirCbvtype(CirCbvtype cirCbvtype) {
        this.cirCbvtype = cirCbvtype;
    }



    @Transient
    public String getCirCbvtypeCode() {
        return this.cirCbvtype == null ? null : this.cirCbvtype.getCode();
    }

    public void setCirCbvtypeCode(String code) {
        if (code == null) {
            this.cirCbvtype = null;
        } else {
            CirCbvtype obj = new CirCbvtype();
            obj.setCode(code);
            this.cirCbvtype = obj;
        }
    }


// @org.codehaus.jackson.map.annotate.JsonSerialize(using=nl.devoorkant.bdr.jackson.JsonDateSerializer.class)
    @Temporal(TemporalType.DATE)
    @Column(name="DatumBegin", nullable=false, length=10)
    public Date getDatumBegin() {
        return this.datumBegin;
    }
    
    public void setDatumBegin(Date datumBegin) {
        this.datumBegin = datumBegin;
    }


// @org.codehaus.jackson.map.annotate.JsonSerialize(using=nl.devoorkant.bdr.jackson.JsonDateSerializer.class)
    @Temporal(TemporalType.DATE)
    @Column(name="DatumEind", length=10)
    public Date getDatumEind() {
        return this.datumEind;
    }
    
    public void setDatumEind(Date datumEind) {
        this.datumEind = datumEind;
    }


    
    @Column(name="Titulatuur", length=10)
    public String getTitulatuur() {
        return this.titulatuur;
    }
    
    public void setTitulatuur(String titulatuur) {
        this.titulatuur = titulatuur;
    }


    
    @Column(name="Voorletters", length=20)
    public String getVoorletters() {
        return this.voorletters;
    }
    
    public void setVoorletters(String voorletters) {
        this.voorletters = voorletters;
    }


    
    @Column(name="Voorvoegsel", length=20)
    public String getVoorvoegsel() {
        return this.voorvoegsel;
    }
    
    public void setVoorvoegsel(String voorvoegsel) {
        this.voorvoegsel = voorvoegsel;
    }


    
    @Column(name="Naam", nullable=false, length=200)
    public String getNaam() {
        return this.naam;
    }
    
    public void setNaam(String naam) {
        this.naam = naam;
    }


@XmlTransient
@JsonIgnore
@ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="Cir_Insolventie_Cir_Cbv", catalog="cir", joinColumns = { 
        @JoinColumn(name="CBV_ID", nullable=false, updatable=false) }, inverseJoinColumns = { 
        @JoinColumn(name="Insolventie_ID", nullable=false, updatable=false) })
    public Set<CirInsolventie> getCirInsolventies() {
        return this.cirInsolventies;
    }
    
    public void setCirInsolventies(Set<CirInsolventie> cirInsolventies) {
        this.cirInsolventies = cirInsolventies;
    }





}

