package nl.devoorkant.sbdr.idobfuscator.util;

import java.math.BigDecimal;

import com.eatthepath.idobfuscator.AlphabetCodec;
import com.eatthepath.idobfuscator.BitRotationIntegerTransformer;
import com.eatthepath.idobfuscator.IntegerObfuscationPipeline;
import com.eatthepath.idobfuscator.MultiplicativeInverseIntegerTransformer;
import com.eatthepath.idobfuscator.OffsetIntegerTransformer;
import com.eatthepath.idobfuscator.XorIntegerTransformer;
import com.eatthepath.idobfuscator.util.AlphabetBuilder;

public class ObfuscatorUtils {
	private static final AlphabetCodec codec = new AlphabetCodec(new AlphabetBuilder()
	        .includeUppercaseLatinLetters()
	        .excludeVowels()
	        .excludeVisuallySimilarCharacters()
	        .shuffleWithRandomSeed(95795362)
	        .build());

	private static final BitRotationIntegerTransformer rotate = new BitRotationIntegerTransformer(17);
	private static final OffsetIntegerTransformer offset = new OffsetIntegerTransformer(698745908);
	private static final XorIntegerTransformer xor = new XorIntegerTransformer(6666356);
	private static final MultiplicativeInverseIntegerTransformer inverse = new MultiplicativeInverseIntegerTransformer(5324857);

	private static final IntegerObfuscationPipeline pipeline = new IntegerObfuscationPipeline(Integer.SIZE, codec,
	        rotate, offset, xor, inverse);

	public static final String obfuscateInteger(Integer id) {
		if (id != null)
			return pipeline.obfuscate(id.longValue());
		else
			return null;
	}
	
	public static final Integer deofuscateInteger(String id) {
		if (id != null)
			return new Integer(new BigDecimal(pipeline.deobfuscate(id)).intValueExact());
		else
			return null;
	}
}
