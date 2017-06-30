package uploader;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import Utils.JR;
import Utils.RedisClusterFactory;

/**
 * 上传页面入口Servlet
 * @作者 lichao
 * @时间 2016年10月13日 下午6:41:18
 * @说明
 */
public class QkCdn extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(QkCdn.class);

	/**
	 * Constructor of the object.
	 */
	public QkCdn() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); 
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("【Qkcdn】进入上传页面");
		
		String token = request.getParameter("access_token");
		if (token == null || token.length() < 10) {
			log.error("access_token获取为空或长度小于10");
			request.getRequestDispatcher("404.html").forward(request, response);
			return;
		} else {
			Jedis jd = null;
			try {
				jd = JR.getJd();
				String isActive = jd.get("v:upload:isactive:" + token);//验证页面跳转是否传值,无值则跳转登录页面
				if(null==isActive||"".equals(isActive)){
					JR.close(jd);
					request.getRequestDispatcher("404.html").forward(request,response);
					return;
				} else {
					jd.setex("v:upload:isactive:" + token, 60 * 60 * 24, "2");
					String jobj = jd.get("v:upload:config:" + token);
					if (isnull(jobj)) {
						jobj = "{}";
					}
					
					JSONObject jo = JSONObject.fromObject(jobj);
					if (isnull(jo.get("count"))) {
						request.setAttribute("limit", "100");
					} else {
						request.setAttribute("limit", jo.get("count"));
					}
					if (isnull(jo.get("exts"))) {
						request.setAttribute("exts", "*");
					} else {
						request.setAttribute("exts", jo.get("exts"));
					}
					if (!isnull(jo.get("process_url"))) {
						request.setAttribute("process_url",
								jo.get("process_url"));
					}
					if (!isnull(jo.get("del_url"))) {
						request.setAttribute("del_url",
								jo.get("del_url"));
					}
					if(!isnull(jo.get("bit_rate_v"))) {
						String bit_rate_v=jo.get("bit_rate_v").toString();
						if(bit_rate_v.indexOf("-")!=-1){
							String[] strs=bit_rate_v.split("-");
							int a1=Integer.valueOf(strs[0])/1000;
							int a2=Integer.valueOf(strs[1])/1000;
							request.setAttribute("bit_str",a1+"-"+a2);
						}else{
							request.setAttribute("bit_str",Integer.valueOf(bit_rate_v)/1000);
						}
					}else{
						request.setAttribute("bit_str","200-300");
					}
					
					request.setAttribute("back_url", jo.get("back_url"));
					request.setAttribute("acess_token", token);
					
					JR.close(jd);
					request.getRequestDispatcher("index.jsp").forward(request,response);
					return;
				}
			} catch (Exception e) {
				log.error("【Qkcdn】redis-->获取失败",e);
				request.getRequestDispatcher("404.html").forward(request,response);
			} finally {
				JR.close(jd);
			}
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		try {
			RedisClusterFactory.init();
		} catch (Exception e) {
			log.error("redis初始化失败-->",e);
		}
	}
    
	/**
	 * 判断是否为空
	 *    
	 * @param o
	 * @return
	 */
	public boolean isnull(Object o) {
		return o == null || "".equals(o.toString())|| "null".equals(o.toString()) ? true : false;
	}
}
