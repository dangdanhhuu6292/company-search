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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Batch generated by hbm2java
 */

@XmlRootElement
// @org.jboss.resteasy.annotations.providers.jaxb.IgnoreMediaTypes("application/*+json")
// @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity(name="CirBatch")
@Table(name="Batch"
)
public class Batch  implements java.io.Serializable {

	private Integer batchId;
     private String externeReferentie;
     private Date tijdOntvangen;
     private Date tijdVerwerkt;
     private Set<Input> inputs = new HashSet<Input>(0);

    public Batch() {
    }

   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="Batch_ID", unique=true, nullable=false)
    public Integer getBatchId() {
        return this.batchId;
    }
    
    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }


    
    @Column(name="ExterneReferentie", length=40)
    public String getExterneReferentie() {
        return this.externeReferentie;
    }
    
    public void setExterneReferentie(String externeReferentie) {
        this.externeReferentie = externeReferentie;
    }


// @org.codehaus.jackson.map.annotate.JsonSerialize(using=nl.devoorkant.bdr.jackson.JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="TijdOntvangen", nullable=false, length=19)
    public Date getTijdOntvangen() {
        return this.tijdOntvangen;
    }
    
    public void setTijdOntvangen(Date tijdOntvangen) {
        this.tijdOntvangen = tijdOntvangen;
    }


// @org.codehaus.jackson.map.annotate.JsonSerialize(using=nl.devoorkant.bdr.jackson.JsonDateSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="TijdVerwerkt", length=19)
    public Date getTijdVerwerkt() {
        return this.tijdVerwerkt;
    }
    
    public void setTijdVerwerkt(Date tijdVerwerkt) {
        this.tijdVerwerkt = tijdVerwerkt;
    }


@XmlTransient
@JsonIgnore
@OneToMany(fetch=FetchType.LAZY, mappedBy="batch")
    public Set<Input> getInputs() {
        return this.inputs;
    }
    
    public void setInputs(Set<Input> inputs) {
        this.inputs = inputs;
    }





}


