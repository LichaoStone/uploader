<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="Utils.PropertiesUtil" %>
<%
    String path = request.getContextPath();
	String basePath =request.getScheme() 
						+ "://"
						+ request.getServerName()
						+":"+request.getServerPort()
						+ path + "/";
	
	//basePath="http://uploader.qingk.cn:443/"; //测试数据
	String filterPortOfBasePath = PropertiesUtil.init("uploader.properties").getProperty("filterPortOfBasePath");
	String[] filterPort=filterPortOfBasePath.split(",");
	if(basePath.indexOf(".qingk.")!=-1){//IP地址或者localhost,带端口，否则去掉端口（另：http访问80端口；https访问443端口）
		for(int i=0;i<filterPort.length;i++){
			if(basePath.indexOf(":"+filterPort[i])!=-1){
				 basePath=basePath.replace(":"+filterPort[i],"");
			}
		}
	}
	pageContext.setAttribute("path", path);
	
	//线路下拉菜单
	String cmcc_url = PropertiesUtil.init("uploader.properties").getProperty("cmcc");//移动url
	String unicom_url = PropertiesUtil.init("uploader.properties").getProperty("unicom");//联通url
	String telecom_url = PropertiesUtil.init("uploader.properties").getProperty("telecom");//电信url
	String recommend_url = PropertiesUtil.init("uploader.properties").getProperty("recommend");//推荐url
	String telecomHttps_url = PropertiesUtil.init("uploader.properties").getProperty("telecomHttps");//其他THTTPS
	
	request.setAttribute("cmcc_url", cmcc_url);
	request.setAttribute("unicom_url", unicom_url);
	request.setAttribute("telecom_url", telecom_url);
	request.setAttribute("recommend_url", recommend_url);
	request.setAttribute("telecomHttps_url", telecomHttps_url);
%>
<!DOCTYPE HTML>
<html>
<head>
<!--引入CSS-->
<link rel="stylesheet" type="text/css" href="${path}/js/webuploader.css">
<link rel="stylesheet" type="text/css" href="${path}/css/index.css">
<script type="text/javascript" src="${path}/js/jquery-1.10.2.min.js"></script>
<!--引入JS-->
<script type="text/javascript" src="${path}/js/webuploader.js"></script>
<script type="text/javascript" src="${path}/js/HashMap.js"></script>
<script type="text/javascript" src="${path}/js/json2.js"></script>
<script type="text/javascript">
// 初始化页面
$(document).ready(function(){
	init_network_select();
});

var parent={
		dnd : '#selectort',
		disableGlobalDnd : true,
		swf : '${path}/js/Uploader.swf',
		server : '${path}/servlet/Upload',
		auto:false,
		//内部根据当前运行是创建，可能是input元素，也可能是flash.
		pick :{id:'#picker',innerHTML:'<div class="pickbtn">上 传</div>'} ,
		accept :{extensions:'${exts}'},
		chunked : true,
		formData : {
			"access_token":"${param.access_token}"
		},
		threads :1,
		//sendAsBinary:true,
		//不压缩image,默认如果是jpeg,文件上传前会压缩一把再上传！
		resize : false,
		fileNumLimit:'${limit}'
}

/*创建webuploader子实例参数配置*/
var childt={
		swf : '${path}/js/Uploader.swf',
		chunked:true,
		server :'${path}/servlet/Upload',
		formData :{
			"access_token":"${param.access_token}"
		},
		threads :1,
		resize : false,
		fileNumLimit:1,
		chunkSize:1048576,
		chunkRetry:5,
		auto:false
};

	

var map=new HashMap();
var ingNum=0;
var queueNum=0;
var fidandTimer=new HashMap();
var ManiCount=2;
var fileChunk=new HashMap();

var basePath="<%=basePath%>";
var path="<%=path%>";
var cmcc_url = "<%=cmcc_url%>"; 
var unicom_url = "<%=unicom_url%>"; 
var telecom_url = "<%=telecom_url%>"; 
var recommend_url = "<%=recommend_url%>"; 
var telecomHttps_url = "<%=telecomHttps_url%>"; 
</script>
<title>上传管理</title>
</head>
<body>
<div style="margin-bottom: 10px;">
	<!-- 标题 -->
	<div style="margin-top: 20px;text-indent:6px;height: 20px;">
		<div style="float: left;width: 2px;height: 16px;background-color: #1ABC9C;margin-left: 20px;margin-top: 3px;">
		</div> 
	           上传管理
	</div>
	
	<!-- 线路选择 -->
	<div class="form-group" id="NetWorkDiv" style="margin-top:20px;">
		<div class="col-lg-7 col-md-offset-2">
			<div class="input-group" style="width:400px;margin-left:20px;">
				<span class="network_info">线路选择</span> 
				<select id="network" name="network" style="	width:162px;height:30px;margin-left:18px;border-radius:4px"
					class="search-select"  onchange = "networkChange(this);">
			    </select>
			</div>
		</div>
	</div>
	
	<!-- 横线 -->
	<div class="line-one"></div>
	
	<!-- 说明文字 -->
	<%String ext=(request.getAttribute("exts")==null||"".equals(request.getAttribute("exts")))?"mp4":
		request.getAttribute("exts").toString().toLowerCase();%>
	<%if ("mp3".equals(ext)) {%>
		<div class="font-explain">
			您可以拖拽文件或文件夹上传多个音频，音频文件格式要求如下：<br/>
			音频文件：mp3，小于100M，AAC音频编码，32Kbps码率，44.1KHz采样率<br/>
			可上传多个音频，可上传文件夹（拖拽文件夹）
		</div>
	<%}else{%>
		<div class="font-explain">
			您可以拖拽文件或文件夹上传多个视频，视频文件格式要求如下：<br/>
			视频文件：mp4，小于500M，H264视频编码，848x480，640Kbps码率，25fps帧率  AAC音频编码，48Kbps码率，48KHz采样率<br/>
			可上传多个视频，可上传文件夹（拖拽文件夹）
		</div>
	<%}%>
</div>

<div id="selectort" >
	 <!--上传按钮 -->
	 <div id="picker" style="width: 110px;"></div>
	
	 <div class="top-warm">
	        上传过程中不要关闭窗口
	 </div>
	
	 <!-- 上传图片按钮 -->
	 <div id="selectFrame" class="select-frame">
		 <div id="bginner" style="text-align: center;" >
			 <img  src="js/bg.png" class="drafting-img">
			 <div class="drafting-box">
			           支持拖拽文件或文件夹上传
			 </div>
		 </div>
	 </div>
</div>

<!-- 上传盒子 -->
<div id="fackerModel" style="display: none;">
	 <div id="pane___fileid" style="padding-top:10px;border-bottom:1px solid #DDDDDD;padding-bottom:28px;" >
	 		<!-- 文件名称 -->
	 		<div class="filename-div" id="titev___fileid">__filename</div>
	 		
	 		<!-- 上传类型:其他影音等 -->
	 		<div class="chname-fileid-box" id="chname___fileid"></div>	
	 		
	 		<!-- 进度条 -->
	 		<div class="initBar-fileid" id="initBar___fileid">
	 			<div class="processBar-fileid" id="processBar___fileid" inp="0"></div>
	 		</div>
	 		
	 		<!-- 文件大小(例如:16.94M) -->
	 		<div class="fileid-div">
	 		    <span id="finishSize___fileid" class="file_finish" fid="__fileid"></span>
	 		</div>
	 		
	 		<!-- 上传速度(例如:1.54M/s) -->
	 		<div class="speed_cls_box">
	 		    <span id="speed___fileid" class="__speed_cls"></span>
	 		</div>
	 		
	 		<!-- 状态:准备上传、上传中、成功、上传失败、文件格式错误等 -->
	 		<div class="pstatusText-fileid" id="pstatusText___fileid">
		 		<div  style="height: 20px;line-height: 20px;overflow: hidden;"  class="__statusClass" id="statusText___fileid">
		 		    __statustext
		 		</div>
	 		</div>
	 		
			<!--删除和开始、暂停按钮 -->
	 		<div class="btn-box">
	 			<div id="cancelFile___fileid" style="float:right;margin-right:10px;display: none;" onclick="cancelFile('__fileid')" >
	 			    <img src="js/xx.png" style="display: block;" class="xx"/>
	 			</div>
	 			
	 			<div id="startOpStopbtn___fileid" onclick="startOpStop('__fileid')" class="startOpStop">
	 			    <img src="js/pause.png" style="display: block;" class="pauseOrst"/>
	 			</div>
	 		</div>
	 </div>
</div>
<script type="text/javascript">
	    var k=0;
		var uploader;
		var initConfirm=true;
		
		initOn();
		function initOn(){
			uploader= WebUploader.create(parent);
			
			//给拖拽图片添加文件按钮
			uploader.addButton({
			    id: "#bginner"
			});
			
			//步骤一:文件上传前
			uploader.on('beforeFileQueued', function(file) {
				if(file.ext.toLowerCase()=="mp4"){
					if(file.size>524288000){
						alert("文件过大");
						return false;
					}else{
						true;
					}
				}else if(file.ext.toLowerCase()=="mp3"){
					if(file.size>104857600){
						alert("文件过大");
						return false;
					}else{
						true;
					}
				}else{ 
					alert("文件格式不对");	
					return false ;
				}
				
				if(initConfirm){
					initConfirm=false;
					alert("请确认您上传的文件符合格式要求。如不符合要求，系统会自动转码，需等待较长时间完成。");	
				}
				return true;
			});
		
		
		//步骤二:文件加入队列之后
		uploader.on('fileQueued', function(file) {
				if(document.getElementById("bginner")!=null){
					$("#bginner").remove();
				}
				
				queueNum++;
				var htm = $("#fackerModel").html();
	
				htm = replaceAll(htm, "__fileid", file.id);
	
				var name = file.name;
				htm = replaceAll(htm, "__filename", name);
				htm = replaceAll(htm, "__statustext", "准备上传");
				htm = replaceAll(htm, "__statusClass", "statusClass");
				htm = replaceAll(htm, "__speed_cls", "speed_cls");
				
				$("#selectFrame").append(htm);
				
				document.getElementById("speed_"+file.id).setAttribute("ip", 0);
				
				var timestamp=new Date().getTime();
				var fid=file.id+"_"+timestamp;
				
				
				childt.formData.fid=fid;
				childt.formData.ext=file.ext;
				childt.formData.hash=file.__hash;
				childt.formData.cur_network=$("#network").val();;
				fidandTimer.put(file.id,fid);
				
				
				//第一个切片应以切换网络上传
                childt.server=$("#network").val()+path+"/servlet/Upload";

				
				 //创建上传实例
				 var uppchild=WebUploader.create(childt);
				
				 uppchild.addFiles(file);
				 map.put(file.id,uppchild);
				
				 var urlme=basePath+"business?access_token=${param.access_token}&fid="+fid+"&ext="+file.ext+"&name="+file.name+"&hash="+file.__hash;
				
				 $.ajax({ 
					    url: urlme, 
					    success: function(obj){  
						 	obj=JSON.parse(obj);
							var chname=decodeURI(obj.chname);
							
							$("#chname_"+obj.id).html(chname);
							$("#speed_"+obj.id).attr("in",1);
							
							fileChunk.put(obj.id,obj.chuncksval);
							
							var size=obj.size;
							var filesize=map.get(obj.id).getFiles()[0].size;
							var initPercent=size/filesize;
							var wid = $("#initBar_" + obj.id).width();
							$("#processBar_" + obj.id).css('width',(initPercent*100) + "%");
							$("#processBar_"+obj.id).attr("inp",initPercent);
					    }
			        });
			 
			 //步骤三：
		     postProcess(); 
		
			 //步骤四：上传过程中创建进度条实时显示。
			 uppchild.on('uploadProgress', function(file, percentage) {
					var wid = $("#initBar_" + file.id).width();
					var initper=$("#processBar_" + file.id).attr("inp");
					var per=(initper*1000000+(percentage*1000000));
					
					//进度条显示
					$("#processBar_" + file.id).css('width', (per/10000)+ "%");
					
					var uppchild=map.get(file.id);
					var stats=uppchild.getStats();
					var pnum=stats.progressNum;//进行中的number
					var inrNum=stats.interruptNum;
				
					//状态：上传中
					$("#statusText_" + file.id).html("上传中");
				
					document.getElementById("speed_"+file.id).setAttribute("iz",percentage*1000);
					
					if(parseInt((per/1000))>990){
						 $("#startOpStopbtn_"+file.id).hide();
					}
					
				});
				
			   //步骤五：上传成功
			   uppchild.on('uploadSuccess', function(file,response) {
				    console.log("文件上传完成");
					if(response.error!=null&&response.error!=""&&response.error!=undefined){
						$("#statusText_" + file.id).html("<img class='right' src='js/wrong.png'/>"+response.error);
						//postDel(file.id);
					}else{
						var issuc=response.issuccess;
						if(issuc!=null&&issuc==1){
							if(response.duration==0){
								$("#statusText_" + file.id).html("<img class='right' src='js/wrong.png'/>文件格式错误");
								//postDel(file.id);
							}else{
								var fid=fidandTimer.get(file.id);
								var name=file.name;
								var data={
										process	:"isTransCoding",
										fid:fid,
										access_token:"${param.access_token}",
										name:encodeURIComponent(encodeURIComponent(name))
								};
								
								$.ajax( {
									type : "POST",
									url : basePath+"business?process=isTransCoding&fid=" + fid +"&access_token=" + "${param.access_token}" + "&name=" + encodeURIComponent(encodeURIComponent(name)),
									timeout : 3600000,
									success : function(result) {
										  var res=new Array();
										  res=result.split(",");
										  if(res[0]=="1"){//需要转码
											  $("#statusText_" + file.id).html("<img class='right' src='js/ic_likeshuaxin.png'/>转码中");
										      console.log("coding... ...");
										      toCoding(file.id,file.name,res[1]);
										  }else{//不需要转码
											  $("#statusText_" + file.id).html("<img class='right' src='js/ic_likeshuaxin.png'/>文件复制中");
											  console.log("copying... ...");
											  toCopyFile(file.id,file.name,res[1]);
										  }
								    },
								    error : function(result) {
									   alert("Ajax请求失败，请稍后再试！");
								    }
								});
								
							}
						}else{
							$("#statusText_" + file.id).html("<img class='right' src='js/wrong.png'/>上传失败");
						}
					}
					 
					var icon=decodeURIComponent(decodeURIComponent(response.imgurl));
					var name=response.realname;
					if(icon!=null){}
					if(name!=null){}
					
					$("#startOpStopbtn_"+file.id).html("");
					$("#startOpStopbtn_"+file.id).hide();
				
					ingNum=ingNum-1;
					document.getElementById("speed_"+file.id).setAttribute("iz",1000);
					postProcess();
				});
				
			   
			    //暂停上传
				uppchild.on("stopUpload", function() {
					console.log("暂停上传");
					ingNum=ingNum-1;
					var tid=uppchild.getFiles()[0].id;
					document.getElementById("speed_"+tid).setAttribute("st",2);
				})
			
				
				//开始上传：当开始上传流程时触发
				uppchild.on("startUpload", function() {
					console.log("开始上传");
					
					ingNum=ingNum+1;
					var id=uppchild.getFiles()[0].id;
					queueNum--;
					var timestamp=new Date().getTime();
					
					document.getElementById("speed_"+file.id).setAttribute("sz",parseInt(file.size/1000));
					document.getElementById("speed_"+file.id).setAttribute("iz",0);
					//document.getElementById("speed_"+file.id).setAttribute("iz2",0);
					document.getElementById("speed_"+file.id).setAttribute("st",1);
				})
			
			    //大文件在开起分片上传的前提下此事件可能会触发多次。
				uppchild.on("uploadBeforeSend",function(block,data,headers){
					//更改上传线路
					var focusNetwork=$("#network").val();
					var cur_network=data.cur_network;

					uppchild.option("server",focusNetwork+path+"/servlet/Upload");
					basePath=focusNetwork+path+"/";
				});
			    
			    //上传失败
				uppchild.on("uploadError",function(file,reson){
                    console.error("上传失败："+reson);					
					$("#statusText_" + file.id).html("<img class='right' src='js/wrong.png'/>上传失败");
					ingNum=ingNum-1;
					$("#speed_" + file.id).html("");
					
					//uppchild.retry(file);
				});
		});
	}
		
		
	function replaceAll(s, s1, s2) {
		return s.replace(new RegExp(s1, "gm"), s2);
	}

	/*将上传文件移除出队列*/	
	function cancelFile(fid) {
			if(confirm("确认是否删除")){
				var uppchild=map.get(fid);
				var stats=uppchild.getStats();
				
				var pnum=stats.progressNum;//进行中的number
				var quen=stats.queueNum;
				if(quen>0){
					queueNum--;
				}else if(document.getElementById("speed_"+fid).status==1){
					ingNum--;
				}
				
				uploader.cancelFile(fid);
				uppchild.cancelFile(fid);
				
				$("#pane_" + fid).remove();
				
				postProcess();
				//postDel(fid);//不需要物理删除,只需要移除上传队列即可 4.0需求 栗超 20170322
			}
		}
		
		function postProcess(){
			var total=$(".speed_cls").length;
			var suc=0;
			$(".speed_cls").each(function(){
				var st=this.getAttribute("st");
				var iz=this.getAttribute("iz");
				if(st==1&&iz>999)suc++;
				
			});
			
			var urlt="${process_url}";
			if("${process_url}"==""){
				return;
			}else{
				if(urlt.indexOf("?")!=-1){
					urlt=urlt+"&process="+suc+"-"+total;
				}else{
					urlt=urlt+"?process="+suc+"-"+total;
				}
				
				var urlme=basePath+"business?process=process&count="+(suc+"-"+total)+"&access_token=${param.access_token}";
				$.ajax({
						url: urlme, 
						success: function(obj){
							}
					  });
			}
		}
		
		
		/*转码*/
		function toCoding(id,name,pk){
			var fid=fidandTimer.get(id);
			var data={
					process	:"coding",
					fid:fid,
					access_token:"${param.access_token}",
					name:encodeURIComponent(encodeURIComponent(name)),
					pk:pk
			};
			
			$.ajax({
				url: basePath+"business?process=coding&fid=" + fid + "&access_token=" + "${param.access_token}" + "&name=" + encodeURIComponent(encodeURIComponent(name)) + "&pk=" + pk,
				type:"post",
				success: function(obj){
						if(obj==0){	
							$("#statusText_" + id).html("<img class='right' src='js/right.png'/>成功");
						}else if(obj==1){
							$("#statusText_" + id).html("<img class='right' src='js/wrong.png'/>上传失败");
						}else {
							$("#statusText_" + id).html("<img class='right' src='js/wrong.png'/>转码失败");
						}
					}
				});
		}
			
		/*复制文件*/	
		function toCopyFile(id,name,pk){
			var fid=fidandTimer.get(id);
			var data={
					process	:"copyFile",
					fid:fid,
					access_token:"${param.access_token}",
					name:encodeURIComponent(encodeURIComponent(name)),
					pk:pk
			};
			
			$.ajax({
				url: basePath+"business?process=copyFile&fid=" + fid + "&access_token=" + "${param.access_token}" + "&name=" + encodeURIComponent(encodeURIComponent(name)) + "&pk=" + pk,
				type:"post",
				success: function(obj){
					if(obj==0){	
						$("#statusText_" + id).html("<img class='right' src='js/right.png'/>成功");
					}else if(obj==1){
						$("#statusText_" + id).html("<img class='right' src='js/wrong.png'/>入库失败");
					}else {
						$("#statusText_" + id).html("<img class='right' src='js/wrong.png'/>文件复制失败");
					}
				}
			});
		}
			
		
		/*开始或暂停按钮*/
		function startOpStop(fid){
			var up=map.get(fid);
			var stats=up.getStats();
			var speed=document.getElementById("speed_"+fid);
			var isp= speed.getAttribute("ip");
			var st=speed.getAttribute("st");
			var pnum=stats.progressNum;
			var inrNum=stats.interruptNum;//暂停number
			
			if(isp==0){
				if(st==null){
					//尚在队列;
					speed.setAttribute("ip",1);
					$("#startOpStopbtn_"+fid).html("<img src='js/start.png' class='pauseOrst' style='display:block'/>");
					$("#statusText_"+fid).hide();
					$("#cancelFile_"+fid).show();//展示删除按钮
				}else{
					if(pnum==1){
						speed.setAttribute("ip",1);
						$("#startOpStopbtn_"+fid).html("<img src='js/start.png' class='pauseOrst' style='display:block'/>");
						$("#statusText_"+fid).hide();
						$("#cancelFile_"+fid).show();//展示删除按钮
						up.stop(true);
					}
				}
			}else{
				if(st==null){
					//尚在队列
					speed.setAttribute("ip",0);
					$("#startOpStopbtn_"+fid).html("<img src='js/pause.png' class='pauseOrst' style='display:block'/>");
					$("#statusText_"+fid).show();
					$("#cancelFile_"+fid).hide();//隐藏删除按钮
				}else{
					if(inrNum==1){
						if(ingNum<ManiCount&&fileChunk.get(fid)!=null){
							speed.setAttribute("ip",0);	
							$("#startOpStopbtn_"+fid).html("<img src='js/pause.png' class='pauseOrst' style='display:block'/>");
							$("#statusText_"+fid).show();
							$("#cancelFile_"+fid).hide();//隐藏删除按钮
							up.upload();
						}
					}
				}
				
			}
		}
		
		/*更新上传速度*/
		setInterval(function(){
			if(ingNum<ManiCount){
				$(".speed_cls").each(function(){
					var obj=this;
					if(obj.getAttribute("st")==null&&obj.getAttribute("ip")==0&&
							obj.getAttribute("in")==1&&fileChunk.get(id)!=null){
						var id=obj.id.split("speed_")[1];
						map.get(id).upload();
						return false;
					}
				});	
			}
		},2500);
		
		
	   function compareSpeed(x,y,z,fid){
			 var ext="k/s";
			 var speed=parseInt((x*y/z/2));//(k/s);
			 var mySpeed=speed;
			 if(speed>=1024){
				 speed=(speed/1024).toFixed(2);//(M/s)
				 ext="M/s";
			 }
				
			var attrs=$("#speed_"+fid).attr("spes");
			if(attrs==null)attrs="";
			
			attrs=attrs+";"+mySpeed;
			
			if(attrs[0]==";"){
			   attrs=attrs.substring(1, attrs.length);
			}
		
			var arr=attrs.split(";");
				
			if(arr.length>2){
				var len=arr.length;
				var s1=parseInt(arr[len-1]);
				var s2=parseInt(arr[len-2]);
				var s3=parseInt(arr[len-3]);
				var avg=parseInt((s1+s2+s3)/3);
			
				if(arr.length>5){
					attrs=arr[len-3]+";"+arr[len-2]+";"+arr[len-1]
					}	
				}
				$("#speed_"+fid).attr("spes",attrs);
				return speed+""+ext;
		 }	
		
		function getProcessBy(fid){
		    var initWid=document.getElementById("processBar_"+fid).style.width;
			try{
			    initWid=initWid.split("%")[0];
			}catch(e){
				initWid=0;
			}
			return parseFloat(initWid).toFixed(2);
		}
		
		
		function compareSize(fid){
				var size= $("#speed_"+fid).attr("sz");
				var percent=document.getElementById("processBar_"+fid).style.width;
				percent=percent.split("%")[0];
			
				var allfix=	(size*percent/100/1024).toFixed(2);
				var ext="M";
				if(allfix>1024){
					allfix=(allfix/1024).toFixed(2);
					 ext="G";
				}
				
				if(allfix!=null&&!isNaN(allfix))
			 	return allfix+ext;
				else return null;
		 }
	  
		 
	  
	   /*跟新上传速度*/ 
	   setInterval(function(){
		 $(".speed_cls").each(function(){
			var fid=this.id.split("speed_")[1];
			var st=this.getAttribute("st");
			var iz=getProcessBy(fid)*10;
			
			if(st==1&&iz<998){
			    var _t=1000;
				var p=this.getAttribute("iz");
				var p1=this.getAttribute("iz2");
				if(p1==null){p1=0};
				
				var _p=p-p1;
				if(_p<0){_p=0};
				
				var s=this.getAttribute("sz");
				var str=compareSpeed(s,_p,_t,fid);
			
				if(p==1000){
					$(this).html("");	
				}else{
					$(this).html(str);
				}
				this.setAttribute("iz2",p);
			}else{
				$(this).html("");
			}
		 });
	 },2000);
	 
	 /*更新已上传的文件大小*/
	 setInterval(function(){
		 $(".file_finish").each(function(){
			 var finshiSize=compareSize(this.getAttribute("fid"));
			 if(finshiSize!=null)
			 this.innerHTML=finshiSize
		 });
	 },1000)
	 
	 window.onbeforeunload=function(event){
		 var total=$(".speed_cls").length;
		 var suc=0;
		 $(".speed_cls").each(function(){
			 var st=this.getAttribute("st");
			 var iz=getProcessBy(this.getAttribute("id").split("speed_")[1])*10;
			 if(st==1&&iz>999){suc++};			
		 });
		
		 if(total>suc){
			 return "有文件尚未完成";
		 }else{
			 return "";
		 }
     }
     
	 /*初始化线路选择下拉框*/
	 function init_network_select() {
		
		console.group("线路选择下拉菜单");
			console.log("推荐网络："+recommend_url);
			console.log("移动网络："+cmcc_url);
			console.log("联通网络："+unicom_url);
			console.log("电信线路:"+telecom_url);
			console.log("HTTPS线路:"+telecomHttps_url);
		console.groupEnd();
		
		var data=new Array();
		if (recommend_url != "null" && recommend_url.length>0) {
			$("#network").append("<option value='"+recommend_url+"'>推荐线路</option>");
		}
		if (cmcc_url != "null" && cmcc_url.length>0) {
			$("#network").append("<option value='"+cmcc_url+"'>移动线路</option>");
		}
		if (unicom_url != "null" && unicom_url.length>0) {
			$("#network").append("<option value='"+unicom_url+"'>联通线路</option>");
		}
		if (telecom_url != "null" && telecom_url.length>0) {
			$("#network").append("<option value='"+telecom_url+"'>电信线路</option>");
		}
		
		if (telecomHttps_url != "null" && telecomHttps_url.length>0) {
			$("#network").append("<option value='"+telecomHttps_url+"'>https线路</option>");
		}
	}
	    
    /*切换线路*/
	function networkChange(element) {
		if(confirm("文件上传中更新线路，可能会造成无法续传的情况。确定更换线路吗？")){
		}else{
			return false;
		}
	}
</script>
</body>
</html>
