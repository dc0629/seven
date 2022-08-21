package top.flagshen.myqq.common.druid;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * @author lishiyu
 */
@WebServlet(urlPatterns="/druid/*",
initParams={
         @WebInitParam(name="resetEnable",value="false")// 禁用HTML页面上的“Reset All”功能

}
)
public class DruidStatViewServlet extends StatViewServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
