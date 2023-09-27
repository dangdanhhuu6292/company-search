package nl.devoorkant.sbdr.business.transfer;

import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.sbdr.data.model.CompanyInfo;

@XmlRootElement(name="CompanyInfo")
public class CompanyInfoEntityTransfer extends CompanyInfo{
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyInfoEntityTransfer.class);

	public CompanyInfoEntityTransfer() {
		
	}
	
	public CompanyInfoEntityTransfer(CompanyInfo companyInfo) {
		try {
			if (companyInfo != null) {
				BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
				BeanUtils.copyProperties(this, companyInfo);
				super.setCreditSafeHeadQuarters(null);
				super.setCreditSafeId(null);
				super.setCreditSafeStatus(null);
			}
		} catch (IllegalAccessException e) {
			LOGGER.error("Cannot create CompanyInfoEntityTransfer object");
		} catch (InvocationTargetException e) {
			LOGGER.error("Cannot create CompanyInfoEntityTransfer object");
		}
	}
	
	@Override
	@XmlElement
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type=String.class)
	public Integer getCompanyInfoId() {
		return super.getCompanyInfoId();
	}

	public void setCompanyInfoId(Integer companyInfoId) {
		super.setCompanyInfoId(companyInfoId);
	}
}
