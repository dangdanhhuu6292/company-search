package nl.devoorkant.sbdr.business.transfer;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapterOverview extends XmlAdapter<String, Date> {

    // the desired format
    private String pattern = "dd-MM-yyyy";

    public String marshal(Date date) throws Exception {
        return new SimpleDateFormat(pattern).format(date);
    }

    public Date unmarshal(String dateString) throws Exception {
        return new SimpleDateFormat(pattern).parse(dateString);
    }   
}
