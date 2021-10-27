package com.acuity.visualisations.util;

import com.acuity.visualisations.transform.rule.FileType;

public final class ETLUtil {

	private ETLUtil() {
	}

	public static boolean isCumulative(FileType fileType) {
		return fileType == null || fileType.equals(FileType.CUMULATIVE);
	}
}
