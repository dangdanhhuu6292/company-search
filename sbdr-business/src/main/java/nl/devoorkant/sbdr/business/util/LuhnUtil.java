package nl.devoorkant.sbdr.business.util;

public class LuhnUtil {

	/** 
	 * Luhn Class is an implementation of the Luhn algorithm that checks validity of a card number.	 
	 * 	 
	 * @author <a href="http://www.chriswareham.demon.co.uk/software/Luhn.java">Chris Wareham</a>	 
	 * @version Checks whether a string of digits is a valid card number according to the Luhn algorithm. 1. Starting with the second to last digit and	 
	 *           moving left, double the value of all the alternating digits. For any digits that thus become 10 or more, add their digits together. For example,	 
	 *           1111 becomes 2121, while 8763 becomes 7733 (from (1+6)7(1+2)3). 2. Add all these digits together. For example, 1111 becomes 2121, then 2+1+2+1 is
	 *           6; while 8763 becomes 7733, then 7+7+3+3 is 20. 3. If the total ends in 0 (put another way, if the total modulus 10 is 0), then the number is valid
	 *           according to the Luhn formula, else it is not valid. So, 1111 is not valid (as shown above, it comes out to 6), while 8763 is valid (as shown
	 *           above, it comes out to 20).
	 * @param ccNumber
	 *            the card number to validate.
	 * @return <b>true</b> if the number is valid, <b>false</b> otherwise.
	 */
	public static boolean Check(Long ccNumber)
	{
		String ccNumberstr = Long.toString(ccNumber);
        int sum = 0;
        boolean alternate = false;

        for (int i = ccNumberstr.length() - 1; i >= 0; i--)
        {
                int n = Integer.parseInt(ccNumberstr.substring(i, i + 1));
                if (alternate)
                {
                        n *= 2;
                        if (n > 9)
                        {
                                n = (n % 10) + 1;
                        }
                }
                sum += n;
                alternate = !alternate;
        }
        return (sum % 10 == 0);
	}	
	
	public static int generateCheckDigit(Long l) {
        String str = Long.toString(l);
        int[] ints = new int[str.length()];
        for(int i = 0;i< str.length(); i++){
            ints[i] = Integer.parseInt(str.substring(i, i+1));
        }
        //for(int i = ints.length-2; i>=0; i=i-2){
        for(int i = ints.length-1; i>=0; i=i-2){	
            int j = ints[i];
            j = j*2;
            if(j>9){
                j = j%10 + 1;
            }
            ints[i]=j;
        }
        int sum=0;
        for(int i = 0;i< ints.length; i++){
            sum+=ints[i];
        }
        if(sum%10==0){
            return 0;
        }else return 10-(sum%10);
    }
	
}
