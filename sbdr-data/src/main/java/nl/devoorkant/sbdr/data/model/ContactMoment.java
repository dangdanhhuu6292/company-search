package nl.devoorkant.sbdr.data.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
@Entity(name="ContactMoment")
@Table(name="ContactMoment"
)
public class ContactMoment implements java.io.Serializable{
	private Integer contactMomentId;
	private Melding melding;
	private Gebruiker gebruiker;
	private Notitie notitie;
	private Date datumContact;
	private Date datumContactTerug;
	private String contactWijze;
	private String contactGegevens;
	private String beantwoord;
	private String notitieIntern;
	
    public ContactMoment() {
    }

   
    @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="ContactMoment_ID", unique=true, nullable=false)
    public Integer getContactMomentId() {
        return this.contactMomentId;
    }
    
    public void setContactMomentId(Integer contactMomentId) {
        this.contactMomentId = contactMomentId;
    }

@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="Melding_ID")
    public Melding getMelding() {
		return melding;
	}

	public void setMelding(Melding melding) {
		this.melding = melding;
	}
	
	@Transient
	public Integer getMeldingMeldingId() {
	    return this.melding == null ? null : this.melding.getMeldingId();
	}
	
	public void setMeldingMeldingId(Integer MeldingId) {
	    if (MeldingId == null) {
	        this.melding = null;
	    } else {
	        Melding obj = new Melding();
	        obj.setMeldingId(MeldingId);
	        this.melding = obj;
	    }
	}	    
    

@XmlTransient
@JsonIgnore
@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="Gebruiker_ID")	
	public Gebruiker getGebruiker() {
		return gebruiker;
	}


	public void setGebruiker(Gebruiker gebruiker) {
		this.gebruiker = gebruiker;
	}
	
    @Transient
    public Integer getGebruikerId() {
        return this.gebruiker == null ? null : this.gebruiker.getGebruikerId();
    }

    public void setGebruikerGebruikerId(Integer gebruikerId) {
        if (gebruikerId == null) {
            this.gebruiker = null;
        } else {
            Gebruiker obj = new Gebruiker();
            obj.setGebruikerId(gebruikerId);
            this.gebruiker = obj;
        }
    }

@XmlTransient
@JsonIgnore
@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
    @JoinColumn(name="Notitie_ID")	
	public Notitie getNotitie() {
		return notitie;
	}


	public void setNotitie(Notitie notitie) {
		this.notitie = notitie;
	}
	
    @Transient
    public Integer getNotitieId() {
        return this.notitie == null ? null : this.notitie.getNotitieId();
    }

    public void setNotitieNotitieId(Integer notitieId) {
        if (notitieId == null) {
            this.notitie = null;
        } else {
            Notitie obj = new Notitie();
            obj.setNotitieId(notitieId);
            this.notitie = obj;
        }
    }

	public Date getDatumContact() {
		return datumContact;
	}


	public void setDatumContact(Date datumContact) {
		this.datumContact = datumContact;
	}


	public Date getDatumContactTerug() {
		return datumContactTerug;
	}


	public void setDatumContactTerug(Date datumContactTerug) {
		this.datumContactTerug = datumContactTerug;
	}


	public String getContactWijze() {
		return contactWijze;
	}


	public void setContactWijze(String contactWijze) {
		this.contactWijze = contactWijze;
	}
	
	public String getContactGegevens() {
		return contactGegevens;
	}

	public void setContactGegevens(String contactGegevens) {
		this.contactGegevens = contactGegevens;
	}


	public String getBeantwoord() {
		return beantwoord;
	}


	public void setBeantwoord(String beantwoord) {
		this.beantwoord = beantwoord;
	}


	public String getNotitieIntern() {
		return notitieIntern;
	}


	public void setNotitieIntern(String notitieIntern) {
		this.notitieIntern = notitieIntern;
	}
    
    
    	
}
