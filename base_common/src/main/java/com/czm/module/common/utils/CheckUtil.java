package com.czm.module.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {
	
	/**
	 * 校验手机号
	 * @param Mobile
	 * @return
	 */
	public static boolean CheckMobile(String Mobile)
	{
		//Pattern p=Pattern.compile("^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\\d{8})$");
		Pattern p= Pattern.compile("^((1[3-8]{1}[0-9]{1})+\\d{8})$");
		Matcher phoneMatcher= p.matcher(Mobile);
		return phoneMatcher.matches();
	}
	
	/**
	 *  检查是否为数字
	 * @param Value
	 * @return
	 */
	public static boolean isNumberic(String Value)
	{
		Pattern p= Pattern.compile("[0-9]*");
		Matcher numbericMatcher= p.matcher(Value);
		return numbericMatcher.matches();
	}

    /**
     *  检查是否为数字
     * @param Value
     * @return
     */
    public static boolean isNumberDecimal(String Value)
    {
        Pattern p= Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher numbericMatcher= p.matcher(Value);
        return numbericMatcher.matches();
    }
}
