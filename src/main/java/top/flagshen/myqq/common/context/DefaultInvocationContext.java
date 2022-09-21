package top.flagshen.myqq.common.context;

/**
 * @author dengchao
 */
public class DefaultInvocationContext implements InvocationContext {

	private String traceId;

	private String qqNum;

	private Integer isTest;


	public DefaultInvocationContext() {
	}


	public DefaultInvocationContext(String traceId, String qqNum, Integer isTest) {
		this.traceId = traceId;
		this.qqNum = qqNum;
		this.isTest = isTest;
	}

	@Override
	public String getTraceId() {
		return this.traceId;
	}

	@Override
	public String getQqNum() {
		return this.qqNum;
	}

	@Override
	public Integer getIsTest() {
		return this.isTest;
	}
}
