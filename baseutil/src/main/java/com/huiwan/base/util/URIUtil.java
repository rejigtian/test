package com.huiwan.base.util;


public class URIUtil {

	public static final String PREF_VIP = "vip";

	public static final String PREF_AVATAR_SQUARE_FEED = "avatar_square_feed";

	public static String url2LocalPath(String url, String pref) {
		String path = "";
		if (url != null) {
			int len = url.length();
			int index = url.lastIndexOf("/");

			index = index + 1 > len ? len : index;
			String last = url.substring(index + 1);

			index = last.lastIndexOf(".");
			index = index == -1 ? last.length() : index;
			last = last.substring(0, index);

			path = pref + last + ".a";
		}
		return path;
	}
}
