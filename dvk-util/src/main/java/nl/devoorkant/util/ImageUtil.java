package nl.devoorkant.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import nl.devoorkant.exception.DVKException;

public class ImageUtil {
  public static final int THUMBNAIL_DEFAULT_WIDTH = 96;
  
  public static final int THUMBNAIL_DEFAULT_HEIGHT = 96;
  
  public static final String THUMBNAIL_DEFAULT_FORMAT = "jpg";
  
  public static BufferedImage loadImage(String pstrValue) throws DVKException {
    try {
      return ImageIO.read(new File(pstrValue));
    } catch (IOException loEx) {
      throw new DVKException("Unable to load " + pstrValue + " as a File");
    } 
  }
  
  public static BufferedImage loadImage(InputStream poInputStream) throws DVKException {
    try {
      return ImageIO.read(poInputStream);
    } catch (IOException loEx) {
      throw new DVKException("Unable to load the InputStream as a File");
    } 
  }
  
  public static byte[] convertImageToByteArray(BufferedImage poImage, String pstrFormatName) throws DVKException {
    if (poImage != null && StringUtil.isNotEmptyOrNull(pstrFormatName))
      try {
        ByteArrayOutputStream loByteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(poImage, pstrFormatName, loByteArrayOutputStream);
        return loByteArrayOutputStream.toByteArray();
      } catch (IOException loEx) {
        throw new DVKException("Unable to convert passed image to byte array. " + loEx.getMessage());
      }  
    throw new DVKException("No image passed.");
  }
  
  public static BufferedImage convertByteArrayToImage(byte[] poaValue) throws DVKException {
    if (poaValue != null)
      try {
        ByteArrayInputStream loByteArrayInputStream = new ByteArrayInputStream(poaValue);
        return ImageIO.read(loByteArrayInputStream);
      } catch (IOException loEx) {
        throw new DVKException("Unable to convert passed byte array to image. " + loEx.getMessage());
      }  
    throw new DVKException("No byte array passed.");
  }
  
  public static byte[] resizeToThumbnail(byte[] poaValue) throws DVKException {
    return resize(poaValue, "jpg", 96, 96);
  }
  
  public static byte[] resizeToThumbnail(byte[] poaValue, String pstrFormatName) throws DVKException {
    return resize(poaValue, pstrFormatName, 96, 96);
  }
  
  public static byte[] resize(byte[] poaValue, String pstrFormatName, int pnWidth, int pnHeight) throws DVKException {
    byte[] loResult = null;
    try {
      BufferedImage loOriginalImage = convertByteArrayToImage(poaValue);
      BufferedImage loResizedImage = resizeImage(loOriginalImage, pnWidth, pnHeight);
      loResult = convertImageToByteArray(loResizedImage, pstrFormatName);
    } catch (IOException loEx) {
      throw new DVKException("Unable to create thumbnail: " + loEx.getMessage());
    } 
    return loResult;
  }
  
  public static BufferedImage resizeImage(BufferedImage poImage, int pnWidth, int pnHeight) throws IOException {
    float lnFactorX = pnWidth / poImage.getWidth();
    float lnFactorY = pnHeight / poImage.getHeight();
    float lnFactor = Math.max(lnFactorX, lnFactorY);
    int lnWidth = Math.round(lnFactor * poImage.getWidth());
    int lnHeight = Math.round(lnFactor * poImage.getHeight());
    int lnX = (pnWidth - lnWidth) / 2;
    int lnY = (pnHeight - lnHeight) / 2;
    BufferedImage loResizedImage = new BufferedImage(pnWidth, pnHeight, 1);
    Graphics2D loGraphics = loResizedImage.createGraphics();
    loGraphics.setComposite(AlphaComposite.Src);
    loGraphics.drawImage(poImage, lnX, lnY, lnWidth, lnHeight, null);
    loGraphics.dispose();
    return loResizedImage;
  }
}
