package nl.devoorkant.sbdr.business.util;

import nl.devoorkant.sbdr.business.service.ServiceException;
import nl.devoorkant.sbdr.data.util.EDocumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DocumentUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentUtil.class);

	private static String root = "/var/sbdrDocuments";

	public static File getPathByDate(Calendar now){
		if(root == null || root.equals("null")) root = "sbdr";
		String year = String.valueOf(now.get(Calendar.YEAR));
		String month = String.valueOf(theMonth(now.get(Calendar.MONTH)));
		String rootpath = root + File.separator + year + File.separator + month;
		File dir = new File(rootpath);

		LOGGER.debug("RootPath: " + rootpath);

		if(!dir.exists()) dir.mkdirs();

		return dir;
	}

	public static File getRootPath() {
		return getPathByDate(new GregorianCalendar());
	}

	public static File getAttachmentRootPath() {
		File rootPath = new File(getRootPath() + File.separator + "SupportAttachments");
		if(!rootPath.exists()) rootPath.mkdirs();
		return rootPath;
	}

	private static String theMonth(int month) {
		String[] monthNames = {"januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december"};
		return monthNames[month];
	}

	public static void saveFile(byte[] data, String filename) throws ServiceException {

		// Create the file on server
		try {
			File dir = DocumentUtil.getRootPath();

			File serverFile = new File(dir.getAbsolutePath() + File.separator + filename);
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
			stream.write(data);
			stream.close();

			LOGGER.info("Created document '" + filename + "' Server File Location=" + serverFile.getAbsolutePath());
		} catch(Exception e) {
			LOGGER.error("Cannot create file: " + filename + ": " + e.getMessage());
			throw new ServiceException("Cannot create file: " + e.getMessage());
		}
	}
	
	public static String generateFilename(EDocumentType doctype) throws ServiceException {
		String filename = null;

		boolean fileExists = true;

		try {
			File dir = getRootPath();

			if(dir.exists()) {
				while(fileExists) {
					filename = doctype.getCode() + SerialNumber.generateRandomSerialNumber8_32();
					File f = new File(dir.getAbsolutePath() + File.separator + filename);
					if(!f.isFile()) fileExists = false;
				}
			} else filename = doctype.getCode() + SerialNumber.generateRandomSerialNumber8_32();
		} catch(Exception e) {
			throw new ServiceException("Cannot create filename! " + e.getMessage());
		}

		return filename;
	}

//	public static String generateAttachmentFilename(String refNo) throws ServiceException {
//		String filename = null;
//		boolean fileExists = true;
//
//		try {
//			File dir = getAttachmentRootPath();
//			if(dir.exists()) {
//				while(fileExists) {
//					filename = refNo;
//					File f = new File(dir.getAbsolutePath() + File.separator + filename);
//					if(!f.isFile()) fileExists = false;
//				}
//			} else filename = dir.getAbsolutePath() + File.separator + refNo;
//		} catch(Exception e) {
//			throw new ServiceException("Cannot create filename! " + e.getMessage());
//		}
//
//		return filename;
//	}
}
