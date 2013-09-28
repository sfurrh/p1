package com.jorneo.util;

public class StringUtil {

	public static String join(String[] arr, String delim) {
		StringBuffer out = new StringBuffer();
		if(arr!=null && arr.length>1) {
			out.append(arr[0]);
			for(int i=1;i<arr.length;i++) {
				out.append(delim);
				out.append(arr[i]);
			}
		}
		return out.toString();
	}
}
