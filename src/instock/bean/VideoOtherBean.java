package instock.bean;


public class VideoOtherBean implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	String video_key;
	/**
	 * 名称
	 */
	String video_name;
	/**
	 * 介绍
	 */
	String description;
	/**
	 * 创建时间
	 */
	String create_time;
	/**
	 * 创建人
	 */
	String creator;
	/**
	 * 剧照
	 */
	String video_img;
	/**
	 * 主服务器视频地址
	 */
	String video_url;
	/**
	 * 所属产品
	 */
	String app_information_key;
	String  play_time;
	/**
	 * 标签
	 */
	String tag;
	/**
	 * 主服务器视频地址
	 */
	String play_url;
	/**
	 * 单位：M
	 */
	String file_size;
	/**
	 * 自增键值
	 */
	String _id;
	
	/**
	 * 分辨率 例:200x300
	 */
    private String resolution;
    
    /**
     * 码率 视频码率+音频码率
     */
	private String bitrate;
	
	/**
	 * 转码状态 uploader组件 一般都插入needless
	 */
	private String transcode_status;
	
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getPlay_time() {
		return play_time;
	}
	public void setPlay_time(String play_time) {
		this.play_time = play_time;
	}
	public String getVideo_long() {
		return video_long;
	}
	public void setVideo_long(String video_long) {
		this.video_long = video_long;
	}
	String file_type;
	String video_long;
	public String getVideo_key() {
		return video_key;
	}
	public void setVideo_key(String video_key) {
		this.video_key = video_key;
	}
	public String getVideo_name() {
		return video_name;
	}
	public void setVideo_name(String video_name) {
		this.video_name = video_name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getVideo_img() {
		return video_img;
	}
	public void setVideo_img(String video_img) {
		this.video_img = video_img;
	}
	public String getVideo_url() {
		return video_url;
	}
	public void setVideo_url(String video_url) {
		this.video_url = video_url;
	}
	public String getApp_information_key() {
		return app_information_key;
	}
	public void setApp_information_key(String app_information_key) {
		this.app_information_key = app_information_key;
	}

	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getPlay_url() {
		return play_url;
	}
	public void setPlay_url(String play_url) {
		this.play_url = play_url;
	}
	public String getFile_size() {
		return file_size;
	}
	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getFile_type() {
		return file_type;
	}
	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getBitrate() {
		return bitrate;
	}
	public void setBitrate(String bitrate) {
		this.bitrate = bitrate;
	}
	public String getTranscode_status() {
		return transcode_status;
	}
	public void setTranscode_status(String transcode_status) {
		this.transcode_status = transcode_status;
	}
	
}
