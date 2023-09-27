package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import nl.devoorkant.sbdr.idobfuscator.util.ObfuscatorUtils;

public class IdentifierAdapter extends XmlAdapter<String, Integer> {
	public String marshal(Integer id) throws Exception {
       // return "\"" + ObfuscatorUtils.obfuscateInteger(id) + "\"";
		return  ObfuscatorUtils.obfuscateInteger(id);
    }

	public Integer unmarshal(String idString) throws Exception {
        return ObfuscatorUtils.deofuscateInteger(idString);
    }
}
