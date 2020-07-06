package com.atguigu.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oujh5
 *
 */
public class BeanCopyUtils {

	private static final Logger logger = LoggerFactory.getLogger(BeanCopyUtils.class);

	public static <T> T copyProperties(Object source, Class<T> targetClazz){
		T t;
		try {
			t = targetClazz.newInstance();
			BeanUtils.copyProperties(source, t);
			return t;
		} catch (Exception e) {
			logger.error("复制bean属性异常", e);
		}
		return null;
	}

	public static <T> List<T> copyProperties(List<? extends Object> sources, Class<T> targetClazz){
		List<T> targets = new ArrayList<T>(10);
		try {
			for(Object source : sources){
				T t = targetClazz.newInstance();
				BeanUtils.copyProperties(source, t);
				targets.add(t);
			}
		} catch (Exception e) {
			logger.error("复制bean属性异常", e);
		}
		return targets;
	}

}
