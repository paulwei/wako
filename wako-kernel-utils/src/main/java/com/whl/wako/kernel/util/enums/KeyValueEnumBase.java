package com.whl.wako.kernel.util.enums;

/**
 * 键值对枚举基本接口。
 *
 *
 * @author
 * @version @param <K> 键类型
 * @version @param <V>  值类型
 * @since JDK 1.7
 */
public interface KeyValueEnumBase<K, V> {
	/**
	 * 获取key
	 *
	 * @author bricks.mong@2017年6月13日
	 * @return
	 */
	K getKey();
	/**
	 * 获取value
	 *
	 * @author bricks.mong@2017年6月13日
	 * @return
	 */
	V getValue();

}


