package filename;

import caceresenzo.libs.logger.Logger;

public class MoreDotRemove {
	
	public static void main(String[] args) {
		String[] samples = { //
				"aze ...azae", //
				"aze ..aze", //
				"aze .aze", //
				"aze... azae", //
				"aze.. aze", //
				"aze. aze", //
				"aze ... azae", //
				"aze .. aze", //
				"aze . aze", //
				"aze...azae", //
				"aze..aze", //
				"aze.aze", //
				"Hello...",
		}; //
		
		for (int i = 0; i < samples.length; i++) {
			String sample = samples[i];
			
			Logger.info("------------------------- %s", i);
			Logger.info(" | Sample     : " + sample);
			Logger.info(" | Remplaced  : " + replace(sample));
			Logger.info("-------------------------");
		}
	}
	
	public static String replace(String source) {
		return source.replaceAll("[\\.]{2,}", " ");
	}
	
}
