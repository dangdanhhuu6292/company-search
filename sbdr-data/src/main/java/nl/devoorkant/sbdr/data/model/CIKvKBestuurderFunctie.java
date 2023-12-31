package nl.devoorkant.sbdr.data.model;
// Generated Feb 27, 2017 3:05:41 PM by Hibernate Tools 3.2.4.GA


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * CIKvKBestuurderFunctie generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="CIKvKBestuurderFunctie")
@Table(name="CIKvKBestuurderFunctie"
)
public class CIKvKBestuurderFunctie  implements java.io.Serializable {


     private Integer cikvKbestuurderFunctieId;
     private CIKvKBestuurder CIKvKBestuurder;
     private String functie;

    public CIKvKBestuurderFunctie() {
    }

   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="CIKvKBestuurderFunctie_ID", unique=true, nullable=false)
    public Integer getCikvKbestuurderFunctieId() {
        return this.cikvKbestuurderFunctieId;
    }
    
    public void setCikvKbestuurderFunctieId(Integer cikvKbestuurderFunctieId) {
        this.cikvKbestuurderFunctieId = cikvKbestuurderFunctieId;
    }


@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CIKvKBestuurder_ID", nullable=false)
    public CIKvKBestuurder getCIKvKBestuurder() {
        return this.CIKvKBestuurder;
    }
    
    public void setCIKvKBestuurder(CIKvKBestuurder CIKvKBestuurder) {
        this.CIKvKBestuurder = CIKvKBestuurder;
    }



    @Transient
    public Integer getCIKvKBestuurderCikvKbestuurderId() {
        return this.CIKvKBestuurder == null ? null : this.CIKvKBestuurder.getCikvKbestuurderId();
    }

    public void setCIKvKBestuurderCikvKbestuurderId(Integer cikvKbestuurderId) {
        if (cikvKbestuurderId == null) {
            this.CIKvKBestuurder = null;
        } else {
            CIKvKBestuurder obj = new CIKvKBestuurder();
            obj.setCikvKbestuurderId(cikvKbestuurderId);
            this.CIKvKBestuurder = obj;
        }
    }


    
    @Column(name="Functie", nullable=false)
    public String getFunctie() {
        return this.functie;
    }
    
    public void setFunctie(String functie) {
        this.functie = functie;
    }





}


