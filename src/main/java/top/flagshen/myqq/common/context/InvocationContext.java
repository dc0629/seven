package top.flagshen.myqq.common.context;

import java.io.Serializable;

/**
 *
 * @author dengchao
 */
public interface InvocationContext extends Cloneable, Serializable {
	/**
	 * 回溯id
	 *
	 * @return the trace id
	 */
	String getTraceId();

	/**
	 *
	 */
	String getQqNum();

	/**
	 * 是否是测试
	 * @return
	 */
	Integer getIsTest();

}
