package instock.bean;

public class TransCodingBean implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private String transcoding_id;
	/**
	 * 所属产品
	 */
    private String app_information_key;
    /**
     * 创建时间
     */
    private String create_time;
    /**
     * 开始等待转码时间(一般与创建时间相同)
     */
    private String wait_time;
    /**
     * 转码开始时间
     */
    private String begin_time;
    /**
     * 转码结束时间
     */
    private String end_time;
    /**
     * 处理时长
     */
    private String duration;
    /**
     * 文件名
     */
    private String file_name;
    /**
     * 文件大小
     */
    private String file_size;
    /**
     * 文件时长
     */
    private String file_long;
    
    private String file_type;
    /**
     * 所属模块
     */
    private String component;
    /**
     * 所属分类
     */
    private String classify;
    /**
     * 状态
     */
    private String status;
    private String file_original_name;
    private String msg;
    private String s_list_imgname;
    private String sequenceid;
    /**
     * 创建人
     */
    private String creator;
    private String media_type;
	public String getTranscoding_id() {
		return transcoding_id;
	}
	public void setTranscoding_id(String transcoding_id) {
		this.transcoding_id = transcoding_id;
	}
	public String getApp_information_key() {
		return app_information_key;
	}
	public void setApp_information_key(String app_information_key) {
		this.app_information_key = app_information_key;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getWait_time() {
		return wait_time;
	}
	public void setWait_time(String wait_time) {
		this.wait_time = wait_time;
	}
	public String getBegin_time() {
		return begin_time;
	}
	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public String getFile_size() {
		return file_size;
	}
	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}
	public String getFile_long() {
		return file_long;
	}
	public void setFile_long(String file_long) {
		this.file_long = file_long;
	}
	public String getFile_type() {
		return file_type;
	}
	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFile_original_name() {
		return file_original_name;
	}
	public void setFile_original_name(String file_original_name) {
		this.file_original_name = file_original_name;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getS_list_imgname() {
		return s_list_imgname;
	}
	public void setS_list_imgname(String s_list_imgname) {
		this.s_list_imgname = s_list_imgname;
	}
	public String getSequenceid() {
		return sequenceid;
	}
	public void setSequenceid(String sequenceid) {
		this.sequenceid = sequenceid;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getMedia_type() {
		return media_type;
	}
	public void setMedia_type(String media_type) {
		this.media_type = media_type;
	}
    
}
