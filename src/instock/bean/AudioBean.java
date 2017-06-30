package instock.bean;

public class AudioBean implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键
	 */
	private String audio_key;
	/**
	 * 名称
	 */
	private String audio_name;
	/**
	 * 主服务器地址
	 */
	private String audio_url;
	/**
	 * 节目时长
	 */
	private String audio_long;
	/**
	 * 待审核waitExamine，审核通过examineYes，审核未通过examineNo，发布publish，禁用disable
	 */
	private String status;
	/**
	 * 描述
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
	 * 图标
	 */
	private String audio_img;
	/**
	 * 所属产品
	 */
	private String app_information_key;
	private String channel_key;
	private String channel_name;
	private String column_name;
	private String column_key;
	private String play_time;
	private String tag;
	private String app_component_key;
	private String _id;
	/**
	 * 主服务器地址
	 */
	private String play_url;
	private String type;
	/**
	 * 单位：M
	 */
	private String file_size;
	
    /**
     * 码率 视频码率+音频码率
     */
	private String bitrate;
	
	/**
	 * 转码状态 uploader组件 一般都插入needless
	 */
	private String transcode_status;
	
	
	public String getAudio_key() {
		return audio_key;
	}
	public void setAudio_key(String audio_key) {
		this.audio_key = audio_key;
	}
	public String getAudio_name() {
		return audio_name;
	}
	public void setAudio_name(String audio_name) {
		this.audio_name = audio_name;
	}
	public String getAudio_url() {
		return audio_url;
	}
	public void setAudio_url(String audio_url) {
		this.audio_url = audio_url;
	}
	public String getAudio_long() {
		return audio_long;
	}
	public void setAudio_long(String audio_long) {
		this.audio_long = audio_long;
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
	public String getAudio_img() {
		return audio_img;
	}
	public void setAudio_img(String audio_img) {
		this.audio_img = audio_img;
	}
	public String getApp_information_key() {
		return app_information_key;
	}
	public void setApp_information_key(String app_information_key) {
		this.app_information_key = app_information_key;
	}
	public String getChannel_key() {
		return channel_key;
	}
	public void setChannel_key(String channel_key) {
		this.channel_key = channel_key;
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
	public String getColumn_key() {
		return column_key;
	}
	public void setColumn_key(String column_key) {
		this.column_key = column_key;
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
	public String getFile_size() {
		return file_size;
	}
	public void setFile_size(String file_size) {
		this.file_size = file_size;
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
