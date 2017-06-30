package uploader;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * 解决Ajax跨域请求问题
 * @作者 lichao
 * @时间 2016年12月29日 下午1:49:02
 * @说明
 */
public class CORSFilter implements Filter{
	    private static final Logger logger = Logger.getLogger(CORSFilter.class);
	    public void init(FilterConfig filterConfig) throws ServletException {
	 
	    }
	    
	    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
	    	HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
	        httpResponse.addHeader("Access-Control-Allow-Origin", "*");//ajax请求头部信息允许所有IP地址或者域名
	        httpResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");//请求方式
	        filterChain.doFilter(servletRequest, servletResponse);
	    }
	 
	    public void destroy() {
	 
	    }
}
