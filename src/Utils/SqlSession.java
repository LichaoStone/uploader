package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * JDBC连接
 * @作者 lichao
 * @时间 2016年10月9日 上午7:58:59
 * @说明
 */
public class SqlSession {
	    private static final Logger logger = Logger.getLogger(SqlSession.class);
	    public static  SqlSession sqlSession=new SqlSession();
	    
	    public static SqlSession getSqlSession() {
			return sqlSession;
		}
          
		public static void setSqlSession(SqlSession sqlSession) {
			SqlSession.sqlSession = sqlSession;
		}

		/**
	     * 插入
	     */
	    public  int insert(String sql){
	    	logger.info("【插入】sql="+sql);
	    	Connection conn = getConnection();
	        int i = 0;
	        //String sql = "insert into students (Name,Sex,Age) values(?,?,?)";
	        PreparedStatement pstmt=null;
	        try {
	            pstmt = (PreparedStatement) conn.prepareStatement(sql);
	            
	            //for(int j=1;j<=str.length;j++){
	            //	pstmt.setString(j, str[j-1]);
	            //}
	            i = pstmt.executeUpdate();
	            conn.commit();
	        } catch (SQLException e) {
	        	try {
	        		logger.error("【插入】插入失败：",e);
					conn.rollback();
				} catch (SQLException e1) {
					logger.error("【插入】回滚失败：",e);
				}
	        }finally {
	        	if (pstmt!=null) {
	        		 try {
	 	        		pstmt.close();
	 				} catch (SQLException e) {
	 					logger.error("【插入】关闭数据库链接失败：",e);
	 				}
				}
	        	if (conn!=null) {
	        		try {
						conn.close();
					} catch (SQLException e) {
						logger.error("【插入】关闭conn失败：",e);
					}
				}
	        	 
			}
	        
	        logger.info("【插入】i="+i);
	        return i;
	    } 
	    
	    /**
	     * 插入并返回自增主键
	     */
	    public  int insertAndReturnPK(String sql){
	    	logger.info("【插入返回主键】sql="+sql);
	    	Connection conn = getConnection();
	        int i = 0;
	        int resultkey=0;
	        //String sql = "insert into students (Name,Sex,Age) values(?,?,?)";
	        PreparedStatement pstmt=null;
	        ResultSet rs=null;
	        try {
	            pstmt = (PreparedStatement) conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
	            i = pstmt.executeUpdate();
	            rs = pstmt.getGeneratedKeys ();  
	            if (rs.next()) {
	            	resultkey= rs.getInt(i);  
				}
	            //for(int j=1;j<=str.length;j++){
	            //	pstmt.setString(j, str[j-1]);
	            //}
	            conn.commit();
	        } catch (SQLException e) {
	        	try {
	        		logger.error("【插入返回主键】插入失败：",e);
					conn.rollback();
				} catch (SQLException e1) {
					logger.error("【插入返回主键】回滚失败：",e);
				}
	        }finally {
	        	if (pstmt!=null) {
	        		 try {
	 	        		pstmt.close();
	 				} catch (SQLException e) {
	 					logger.error("【插入返回主键】关闭pstmt失败：",e);
	 				}
				}
	        	if (conn!=null) {
	        		try {
						conn.close();
					} catch (SQLException e) {
						logger.error("【插入返回主键】关闭conn失败：",e);
					}
				}
			}
	        
	        logger.info("【插入并返回主键】i="+i+";resultkey="+resultkey);
	        return resultkey;
	    } 
	    
	    /**
	     * 修改
	     * @param sql
	     * @return
	     */
	    public  int  update(String sql){
	    	    logger.info("【修改】sql="+sql);
	    	    Connection conn = getConnection();
			    int i = 0;
			    PreparedStatement pstmt=null;
			    try {
			        pstmt = (PreparedStatement) conn.prepareStatement(sql);
			        i = pstmt.executeUpdate();
			        conn.commit();
			    } catch (SQLException e) {
			    	try {
			    		logger.error("【修改】修改数据失败：",e);
						conn.rollback();
					} catch (SQLException e1) {
						logger.error("【修改】回滚数据失败：",e1);
					}
			    }finally {
			    	if (pstmt!=null) {
			    		try {
							pstmt.close();
						} catch (SQLException e) {
							logger.error("【修改】关闭pstmt失败：",e);
						}
					}
			        if (conn!=null) {
			        	try {
							conn.close();
						} catch (SQLException e) {
							logger.error("【修改】关闭conn失败：",e);
						}
					}
				    
				}
			    logger.info("【修改】i="+i);
			    return i;
			}
		/**
		 * 删除
		 * @throws SQLException 
		 */
	    public  int  delete(String sql){
	    	    logger.info("【删除】sql="+sql);
			    Connection conn = getConnection();
			    int i = 0;
			    PreparedStatement pstmt=null;
			    try {
			        pstmt = (PreparedStatement) conn.prepareStatement(sql);
			        i = pstmt.executeUpdate();
			        conn.commit();
			    } catch (SQLException e) {
			    	try {
			    		logger.error("【删除】失败：",e);
						conn.rollback();
					} catch (SQLException e1) {
						logger.error("【删除】回滚失败：",e1);
					}
			    }finally {
			    	if (pstmt!=null) {
			    		 try {
							pstmt.close();
						} catch (SQLException e) {
							logger.error("【删除】关闭pstmt失败：",e);
						}
					}
			    	if (conn!=null) {
			    		try {
							conn.close();
						} catch (SQLException e) {
							logger.error("【删除】关闭conn失败：",e);
						}
					}
				}
			    
			    logger.info("【删除】i="+i);
			    return i;
		}
		
	    /**
	     * 查询 返回List集合
	     * @param sql
	     * @return
	     */
	    public  List<?> selectList(String sql){
	    	logger.info("【查询selectList】sql="+sql);
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			Connection conn=getConnection();
			Statement stmt=null;
			ResultSet rs=null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);

				// 获得数据的列标题
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] name = new String[count];
				for (int i = 0; i < count; i++) {
					name[i] = rsmd.getColumnName(i + 1);
				}
				rs.last();

				// 获取resultSet的大小
				int rows = rs.getRow();
				rs.beforeFirst();
				while (rs.next()) {
					HashMap<String, String> hashMap = new HashMap<String, String>();
					String tmp = rs.getString(1);
					try {
						for (int i = 1; i <= count; i++) {
							hashMap.put(name[i - 1], rs.getString(i));
						}
					} catch (Exception e) {

					}
					list.add(hashMap);
				}
			} catch (SQLException e) {
				logger.error("【查询selectList】失败:",e);
				list = null;
			}finally {
				
				if (rs!=null) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.error("【查询selectList】关闭rs失败:",e);
					}
				}
				
				if (stmt!=null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.error("【查询selectList】关闭stmt失败:",e);
					}
				}
				
				if (conn!=null) {
					try {
						conn.close();
					} catch (SQLException e) {
						logger.error("【查询selectList】关闭conn失败:",e);
					}
				}
			
			}
			
			logger.info("【查询selectList】结果："+list);
			return list;
		}
		
	    
	    /**
		 * 查询 返回Map集合
		 */
	    public  Map<String,Object> selectMap(String sql){
			logger.info("【查询返回Map】sql="+sql);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			Connection conn=getConnection();
			Statement stmt=null;
			ResultSet rs=null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);

				// 获得数据的列标题
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] name = new String[count];
				for (int i = 0; i < count; i++) {
					name[i] = rsmd.getColumnName(i + 1);
				}
				rs.last();

				// 获取resultSet的大小
				int rows = rs.getRow();
				rs.beforeFirst();
				while (rs.next()) {
					HashMap<String, String> hashMap = new HashMap<String, String>();
					String tmp = rs.getString(1);
					try {
						for (int i = 1; i <= count; i++) {
							resultMap.put(name[i - 1], rs.getString(i));
						}
					} catch (Exception e) {

					}
				}
			} catch (SQLException e) {
				logger.error("【查询返回Map】失败：",e);
				resultMap.clear();
			}finally {
				if (rs!=null) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.error("【查询返回Map】关闭rs失败：",e);
					}
				}
				
				if (stmt!=null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.error("【查询返回Map】关闭stmt失败：",e);
					}
				}
				
				if (conn!=null) {
					try {
						conn.close();
					} catch (SQLException e) {
						logger.error("【查询返回Map】关闭conn失败：",e);
					}
				}
			
			}
			
			return resultMap;
		}
	    
		/**
		 * 查询一条数据
		 */
	    public  String selectOne(String sql){
	    	logger.info("【查询One】sql="+sql);
			String resultStr="";
	    	Connection conn=getConnection();
	    	Statement stmt=null;
	    	ResultSet rs=null;
	    	try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				while(rs.next()){
					resultStr=rs.getString(1);
				}
			} catch (SQLException e) {
				logger.error("【查询One】失败:",e);
			}finally {
				if (rs!=null) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.error("【查询One】关闭rs失败:",e);
					}
				}
				
				if (stmt!=null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.error("【查询One】关闭stmt失败:",e);
					}
				}
				
				if (conn!=null) {
					try {
						conn.close();
					} catch (SQLException e) {
						logger.error("【查询One】关闭conn失败:",e);
					}
				}
			}
			return resultStr;
		}
	    
		/**
	     * 初始化数据库连接
	     * @return
	     */
	    private static Connection getConnection(){
	    	
	    	String driver=PropertiesUtil.init("jdbc.properties").getProperty("driver");
	    	String url=PropertiesUtil.init("jdbc.properties").getProperty("url");
	    	String username=PropertiesUtil.init("jdbc.properties").getProperty("username");
	    	String password=PropertiesUtil.init("jdbc.properties").getProperty("password");
	    	
	    	//logger.info("【初始化数据库连接】driver="+driver);
	    	//logger.info("【初始化数据库连接】url="+url);
	    	//logger.info("【初始化数据库连接】username="+username);
	    	//logger.info("【初始化数据库连接】password="+password);
		    
	       Connection conn = null;
		   try {
		        Class.forName(driver); //classLoader,加载对应驱动
		        conn = (Connection) DriverManager.getConnection(url, username, password);
		        conn.setAutoCommit(false);
		    } catch (ClassNotFoundException e1) {
		    	logger.error("【初始化数据库连接】ClassNotFoundException：",e1);
		    } catch (SQLException e2) {
		    	logger.error("【初始化数据库连接】SQLException：",e2);
		    }
		    
		    return conn;
	    }
	    
	    
	    public static void main(String[] args) {
	    		
	    	     //查询,List
	    	     List list=SqlSession.getSqlSession().selectList("select * from t_video");
	    		 System.out.println("list[0]="+list.get(0));
	    		 
	    		//查询,One
	    	     String str=SqlSession.getSqlSession().selectOne("select content_auditing as status from t_menu_app where app_component_key='2d107d470099b48208ac13e309c07ee4'");
	    		 System.out.println("str="+str);
	    		 
	    		 //查询,Map
	    	     Map<String,Object> resultMap=SqlSession.getSqlSession().selectMap("select * from t_menu_app where app_component_key='2d107d470099b48208ac13e309c07ee4'");
	    		 System.out.println("Map="+resultMap);
	    		 
	    		 //插入
	    		 System.out.println("时间："+DateUtil.getTimeToSec());
	    		 String insertSql="insert into t_livestream_lowestprice (create_time,lowest_price) values('"+DateUtil.getTimeToSec()+"','111')";
	    		 System.out.println("insertSql="+insertSql);
	    		 int resFlag=SqlSession.getSqlSession().insert(insertSql);
	    		 System.out.println("resFlag="+resFlag);
	    		 
	    		 
	    		 //插入并返回自增主键
	    		 String insertSql2="insert into t_livestream_lowestprice (create_time,lowest_price) values('"+DateUtil.getTimeToSec()+"','111')";
	    		 int returnKey=SqlSession.getSqlSession().insertAndReturnPK(insertSql2);
	    		 System.out.println(returnKey);
	    		 
	    		 /*
	    		 //插入并返回UUID主键
	    		 StringBuffer insertSql3=new StringBuffer();
	    		 insertSql3.append("insert into t_video (video_key,video_name,status,create_time,creator,play_time,tag,type) values(")
	    		 .append("'").append(UUIDGenerator.getTablePk()).append("',")
	    		 .append("'").append("栗超").append("',")
	    		 .append("'").append("1").append("',")
	    		 .append("'").append(DateUtil.getTimeToSec()).append("',")
	    		 .append("'").append("栗超").append("',")
	    		 .append("'").append(DateUtil.getTimeToSec()).append("',")
	    		 .append("'").append("测试").append("',")
	    		 .append("'").append("1").append("'")
	    		 .append(")");
	    		 
	    		 System.out.println("insertSql3="+insertSql3);
	    		 String returnUUIDKey=SqlSession.getSqlSession().insertAndReturnUUIDPk(insertSql3.toString());
	    		 System.out.println("returnUUIDKey="+returnUUIDKey);
	    		 */
		}
	    
	    
	    
	    /**
	     * 查询 返回List集合
	     * @param sql
	     * @return
	     */
	    public  List<Map<String,Object>> selectGerman(String sql){
	    	logger.info("【查询selectList】sql="+sql);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			Connection conn=getConnection();
			Statement stmt=null;
			ResultSet rs=null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);

				// 获得数据的列标题
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				String[] name = new String[count];
				for (int i = 0; i < count; i++) {
					name[i] = rsmd.getColumnName(i + 1);
				}
				rs.last();

				// 获取resultSet的大小
				int rows = rs.getRow();
				rs.beforeFirst();
				while (rs.next()) {
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					String tmp = rs.getString(1);
					try {
						for (int i = 1; i <= count; i++) {
							hashMap.put(name[i - 1], rs.getString(i));
						}
					} catch (Exception e) {

					}
					list.add(hashMap);
				}
			} catch (SQLException e) {
				logger.error("【查询selectList】失败:",e);
				list = null;
			}finally {
				
				if (rs!=null) {
					try {
						rs.close();
					} catch (SQLException e) {
						logger.error("【查询selectList】关闭rs失败:",e);
					}
				}
				
				if (stmt!=null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						logger.error("【查询selectList】关闭stmt失败:",e);
					}
				}
				
				if (conn!=null) {
					try {
						conn.close();
					} catch (SQLException e) {
						logger.error("【查询selectList】关闭conn失败:",e);
					}
				}
			
			}
			
			logger.info("【查询selectList】结果："+list);
			return list;
		}
	    
}
