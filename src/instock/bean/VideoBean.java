package instock.bean;
/**
 * t_video表bean类
 * @作者 lichao
 * @时间 2016年10月9日 上午8:34:16
 * @说明
 */
public class VideoBean implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 主键
	 */
	private String video_key;
	/**
	 * 名称
	 */
	private String video_name;
	/**
	 * 频道id
	 */
	private String channel_key;
	private String column_key;
	/**
	 * 节目时长
	 */
	private String video_long;
	/**
	 * 待审核waitExamine，审核通过examineYes，审核未通过examineNo，发布publish，禁用disable
	 */
	private String status;
	/**
	 * 介绍
	 */
	private String description;
	/**
	 * 创建时间
	 */
	private String create_time;
	/**
	 * 创建人
	 */
	private String creator;
	/**
	 * 剧照
	 */
	private String video_img;
	/**
	 * 主服务器视频地址
	 */
	private String video_url;
	/**
	 * 所属产品
	 */
	private String app_information_key;
	private String channel_name;
	private String column_name;
	private String play_time;
	/**
	 * 标签
	 */
	private String tag;
	private String app_component_key;
	private String _id;
	/**
	 * 主服务器视频地址
	 */
	private String play_url;
	/**
	 * 1是批量增加 2 手动添加
	 */
	private String type;
	/**
	 * 电视点播：video  分类视频：categoryVideo
	 */
	private String content_type;
	/**
	 * 单位：M
	 */
	private String file_size;
	
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
	public String getChannel_key() {
		return channel_key;
	}
	public void setChannel_key(String channel_key) {
		this.channel_key = channel_key;
	}
	public String getColumn_key() {
		return column_key;
	}
	public void setColumn_key(String column_key) {
		this.column_key = column_key;
	}
	public String getVideo_long() {
		return video_long;
	}
	public void setVideo_long(String video_long) {
		this.video_long = video_long;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
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
	public String getChannel_name() {
		return channel_name;
	}
	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}
	public String getColumn_name() {
		return column_name;
	}
	public void setColumn_name(String column_name) {
		this.column_name = column_name;
	}
	public String getPlay_time() {
		return play_time;
	}
	public void setPlay_time(String play_time) {
		this.play_time = play_time;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getApp_component_key() {
		return app_component_key;
	}
	public void setApp_component_key(String app_component_key) {
		this.app_component_key = app_component_key;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getPlay_url() {
		return play_url;
	}
	public void setPlay_url(String play_url) {
		this.play_url = play_url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent_type() {
		return content_type;
	}
	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}
	public String getFile_size() {
		return file_size;
	}
	public void setFile_size(String file_size) {
		this.file_size = file_size;
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
