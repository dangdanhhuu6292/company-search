package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlAttribute;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MapElementsBooleanDeserializer.class)
public class MapElementsBoolean {
	  @XmlAttribute public String  key;
	  @XmlAttribute public Boolean value;

	  private MapElementsBoolean() {} //Required by JAXB

	  public MapElementsBoolean(String key, Boolean value)
	  {
	    this.key   = key;
	    this.value = value;
	  }
}
