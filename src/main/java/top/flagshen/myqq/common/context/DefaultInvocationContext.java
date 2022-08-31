package top.flagshen.myqq.common.context;

/**
 * @author dengchao
 */
public class DefaultInvocationContext implements InvocationContext {

	private String traceId;

	private String qqNum;


	public DefaultInvocationContext() {
	}


	public DefaultInvocationContext(String traceId, String qqNum) {
		this.traceId = traceId;
		this.qqNum = qqNum;
	}

	@Override
	public String getTraceId() {
		return this.traceId;
	}

	@Override
	public String getQqNum() {
		return this.qqNum;
	}
}
