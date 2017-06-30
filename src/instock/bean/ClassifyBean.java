package instock.bean;

import java.util.Date;

/**
 * 类别实体类（点播：频道栏目； 资讯 直播：分类）
 * @作者 lichao
 * @时间 2016年10月17日 上午10:23:15
 * @说明
 */
public class ClassifyBean  implements java.io.Serializable{
	
	private static final long serialVersionUID = 5633520757968482136L;
	/**
	 * 主键
	 */
	private String classify_key;
	/**
	 * 应用ID
	 */
	private String app_information_key;
	/**
	 * 分类名称（频道、栏目）
	 */
	private String classify_name;
	/**
	 * 频道ID
	 */
	private String  parent_key;
	/**
	 * 创建者
	 */
	private String  creator;
	/**
	 * 创建时间
	 */
	private Date  create_time;
	/**
	 * 类别
	 */
	private Integer  type;
	/**
	 * 序号
	 */
	private Integer ordernum;
	/**
	 *控件表主键
	 */
	private String app_component_key;
	/**
	 * 栏目上传图片
	 */
	private String img;
	/**
	 *  是否是默认分类
	 */
	private Integer is_default;
	/**
	 * 状态
	 */
	private String status;
	// 扩展字段
	private String isAddEdit;
	/**
	 * 页面上用的层号
	 */
	private String laynum;
	/**
	 * 角色分类
	 */
	private String roleType;
	/**
	 * 是否按月分组
	 */
	private Integer is_month;
	
	public Integer getIs_month() {
		return is_month;
	}
	public void setIs_month(Integer is_month) {
		this.is_month = is_month;
	}
	public String getRoleType() {
		return roleType;
	}
	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}
	public String getIsAddEdit() {
		return isAddEdit;
	}
	public void setIsAddEdit(String isAddEdit) {
		this.isAddEdit = isAddEdit;
	}
	public String getApp_component_key() {
		return app_component_key;
	}
	public void setApp_component_key(String appComponentKey) {
		app_component_key = appComponentKey;
	}
	public String getClassify_key() {
		return classify_key;
	}
	public void setClassify_key(String classifyKey) {
		classify_key = classifyKey;
	}
	public String getApp_information_key() {
		return app_information_key;
	}
	public void setApp_information_key(String appInformationKey) {
		app_information_key = appInformationKey;
	}
	public String getClassify_name() {
		return classify_name;
	}
	public void setClassify_name(String classifyName) {
		classify_name = classifyName;
	}
	public String getParent_key() {
		return parent_key;
	}
	public void setParent_key(String parentKey) {
		parent_key = parentKey;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date createTime) {
		create_time = createTime;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Integer getOrdernum() {
		return ordernum;
	}
	public void setOrdernum(Integer ordernum) {
		this.ordernum = ordernum;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getLaynum() {
		return laynum;
	}
	public void setLaynum(String laynum) {
		this.laynum = laynum;
	}
	public Integer getIs_default() {
		return is_default;
	}
	public void setIs_default(Integer is_default) {
		this.is_default = is_default;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
