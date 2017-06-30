<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
	pageContext.setAttribute("path", path);
%>

<!DOCTYPE HTML>
<html>
<head>
<style type="text/css">
.progress-bar {
	height: 4px;
	background-color: yellow;
	max-width: 30%;
}

* {
	font-family: 微软雅黑;
	white-space: nowrap;
}
	  ::-webkit-scrollbar {

		width: 8px;

		height: 0px;

}
	/*  	#selectort::-webkit-scrollbar-button    {
        
    }
    #selectort::-webkit-scrollbar-track     {
       
    }
    #selectort::-webkit-scrollbar-track-piece {
        
    }
       #selectort::-webkit-scrollbar-corner {
     
    }  */
     #selectort::-webkit-scrollbar-thumb{
       	background-image:url("js/bgpostintro.png");
        border-radius:4px;
    }
   .realpickfuck{
   	width: 160px;
 	height:100px;		
	margin-top: 10px;
	margin-left: 15px;
	display: inline-block;
	float: left;
	background-image: url("js/im_shipintianjia.png");
	border: 1px solid #dedede;
	background-position: center;
   }

</style>
<!--引入CSS-->
<link rel="stylesheet" type="text/css" href="${path}/js/webuploader.css">
<script type="text/javascript" src="${path}/js/jquery-1.10.2.min.js"></script>
<!--引入JS-->
<script type="text/javascript" src="${path}/js/webuploader.js"></script>
<script type="text/javascript">
var childt={swf : '${path}/js/Uploader.swf',chunked:true,server : '${path}/servlet/Upload',
		formData : {
			
			"access_token":"${param.access_token}"
		},
		resize : false,
		fileNumLimit:1
		
}
var arry=new Object();
</script>
<title>qk-cdn</title>
</head>

<body>





	<div
		style="margin-left: 20px;margin-right: 20px;height:300px;
	
		border: 1px solid #DDDDDD;overflow:scroll;overflow-y :yes;overflow-x:no;position: relative;padding-bottom: 16px;"
		id="selectort">
		<div id="picker" style="width: 160px;height: 100px;
		position: absolute;top: 24px;left: 65px;opacity:0;"><br/>&nbsp;&nbsp;上传按钮&nbsp;&nbsp;&nbsp;<br/>&nbsp;</div>
		<div class="realpickfuck"></div>
		</div>
		<div style="margin-top: 20px;">
		<div
			style="width: 76px;height:26px;
	background-color: #F7F7F7;
	line-height: 26px;text-align: center;letter-spacing:2px;
	float: left;margin-left: 30px;font-size: 12px;border: 1px solid #dddddd;color: #575757"
			id="ctlBtn">开始上传</div>
	
	
	
		<!-- <div id="picker" style="float: right;margin-right: 20px;">选择文件</div> -->
	</div>
	<div style="width: 100%;height:80px;"></div>
	<div id="fackerModel" style="display: none;">

		<div id="pane___fileid"
			style="width: 160px;height:130px;
		
		margin-top: 10px;margin-left: 15px;display: inline-block;float: left;">

			
		<div style="position: absolute;z-index: 1000;width: 30px;height: 30px;border: 1px solid red;" onclick="toStart('__fileid')"></div>
			<div style="position: absolute;z-index: 1000;width: 30px;height: 30px;border: 1px solid blue;margin-top: 30px;" onclick="toStop('__fileid')"></div>
			 <img onerror='this.src="js/bg.png"' src="js/bg.png"
				style="width: 160px;height: 100px;border: 1px solid #DDDDDD;"  id="iconv___fileid"/>
			<div>
			<img src="js/im_queding.png" style="margin-top: -30px;
			display: block;position: absolute;display: none;" id="successright___fileid"/>
			</div>
			<div id="processpanel___fileid" style="width: 161px;
			height: 101px;background-color:rgba(27,27,27,0.5);margin-top: -105px;position: absolute;">
			<div
				style="margin-left:16px;margin-right: 16px;
				margin-top: 39px;height: 13px;border-radius:8px;
				background-image: url('js/jindutiao_di.png');background-repeat: no-repeat;
				"
				id="initBar___fileid">
				
				</div>
			<div
				style="width: 0%;height: 7px;
				background-color: #18F9CD;margin-top: -10px;
				margin-left: 19px;max-width:119px;border-radius:8px;"
				id="processBar___fileid"></div>
			
			</div>
			
			
			
			<div class="v_title"
			style="
			font-size: 12px;height: 16px;overflow: hidden;line-height: 14px;
			margin-left: 6px;margin-right: 6px;width: 146px; white-space:nowrap; 
			text-overflow:ellipsis; /* for internet explorer */ 
			overflow:hidden;  display:block;margin-top:6px;  " id="titev___fileid">__filename</div>
			
			<!-- <div
				style="font-size: 12px;
			color: #DDDDDD;display: inline-block;
			text-align: right;float: right;margin-right: 10px;margin-top: 4px;display: none;"
				id="statusT___fileid" class="__statusClass">__statustext</div>
			 -->

		
		</div>

	</div>
	<script type="text/javascript">
	var k=0;
		var dingweifirst=function(){
			
			if($(".realpickfuck").length>0){
				
			var topT=$(".realpickfuck")[0].offsetTop;
			var leftT=$(".realpickfuck")[0].offsetLeft;
			$("#picker").css("left",leftT+49+"px");
			$("#picker").css("top",topT+16+"px");
			
			}
		};
		setInterval(dingweifirst,100);
		var Opflag = 0;
		
		var uploader;
		
		
		var queuenumberR = Date.parse(new Date())+""
		+parseInt(Math.random()*10)+""
		+parseInt(Math.random()*10)
		+""+parseInt(Math.random()*10); 
		parseInt(Math.random()*10);
		
		initOn();
		function initOn(){
		uploader= WebUploader.create({
			dnd : '#selectort',
			disableGlobalDnd : true,
			// swf文件路径

			swf : '${path}/js/Uploader.swf',

			// 文件接收服务端。
			server : '${path}/servlet/Upload',

			// 选择文件的按钮。可选。
			// 内部根据当前运行是创建，可能是input元素，也可能是flash.

			pick :{id:'#picker'} ,
			
			accept :{extensions:'${exts}'},
		
			chunked : true,
			formData : {
				
				"access_token":"${param.access_token}"
			},
			threads :2,
			// sendAsBinary:true,
			// 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
			resize : false,
			fileNumLimit:${limit}
		});

		uploader.on("uploadFinished", function() {
			$("#ctlBtn").html("开始上传");
		})
		uploader.on("stopUpload", function() {

			var objs = $(".statusClass");
			if (objs != null && objs.length > 0) {
				for (var i = 0; i < objs.length; i++)
					if (objs[i].innerHTML == "上传中") {
						objs[i].innerHTML = "暂停中"
					}
			}
		})

		uploader.on('fileQueued', function(file) {
			
			var htm = $("#fackerModel").html();

			htm = replaceAll(htm, "__fileid", file.id);

			var name = file.name;
			htm = replaceAll(htm, "__filename", name);
			htm = replaceAll(htm, "__statustext", "准备上传");
			htm = replaceAll(htm, "__statusClass", "statusClass");
			$(".realpickfuck").remove();
			$("#selectort").append(htm);
			
			$("#selectort").append("<div class='realpickfuck'></div>")
			
		
			var stats=uploader.getStats();
			var pnum=stats.progressNum;//进行中的number
			var qnum=stats.queueNum;//队列中的num
			var inrNum=stats.interruptNum;
			/* if(uploader.isInProgress()&&inrNum==0){
				uploader.upload(file);
			} */
			
			var uppchild=WebUploader.create(childt);
			
			uppchild.addFiles(file);
			arry[file.id]=uppchild;
			uppchild.on('uploadProgress', function(file, percentage) {

				//$("#statusT_" + file.id).html("上传中");
				var wid = $("#initBar_" + file.id).width();
				$("#processBar_" + file.id).css('width', wid * percentage + "px");
				
			});
			
			uppchild.on('uploadSuccess', function(file,response) {

				//$("#statusT_" + file.id).html("上传完成");
				
				var icon=decodeURIComponent(decodeURIComponent(response.imgurl));
				var name=response.realname;
				if(icon!=null)
				{
				$("#iconv_"+file.id).attr("src",decodeURIComponent(icon));
				}
				if(name!=null){
				$("#titev_"+file.id).html(name);
				}
				
				$("#processpanel_"+file.id).hide();
				if(response.error==null||response.error==""){
				$("#successright_"+file.id).show();
				
				}else{
					$("#successright_"+file.id).hide();
				}
				k++;
				location.hash="#"+k;
			
			});
			//uppchild.upload();
		});

		uploader.on('uploadProgress', function(file, percentage) {

			//$("#statusT_" + file.id).html("上传中");
			var wid = $("#initBar_" + file.id).width();
			$("#processBar_" + file.id).css('width', wid * percentage + "px");
			
		});

		uploader.on('uploadSuccess', function(file,response) {

			//$("#statusT_" + file.id).html("上传完成");
			
			var icon=decodeURIComponent(decodeURIComponent(response.imgurl));
			var name=response.realname;
			if(icon!=null)
			{
			$("#iconv_"+file.id).attr("src",decodeURIComponent(icon));
			}
			if(name!=null){
			$("#titev_"+file.id).html(name);
			}
			
			$("#processpanel_"+file.id).hide();
			if(response.error==null||response.error==""){
			$("#successright_"+file.id).show();
			
			}else{
				$("#successright_"+file.id).hide();
			}
		
		});

		uploader.on('uploadError', function(file) {
			//$("#statusT_" + file.id).html("上传出错");
		});

		uploader.on('uploadComplete', function(file) {

		});
		
		$("#ctlBtn").bind("click", function() {
			if (Opflag == 0) {
				Opflag = 1;
				if ($("#ctlBtn").html() == "开始上传") {
					$("#ctlBtn").html("暂停");
					uploader.upload();

				} else {
					$("#ctlBtn").html("开始上传");
					uploader.stop(true);

				}
				setTimeout(function() {
					Opflag = 0
				}, 200)
			}

			

		})
		}
		function replaceAll(s, s1, s2) {

			return s.replace(new RegExp(s1, "gm"), s2);
		}

		function cancelFile(fid) {

			uploader.cancelFile(fid);

			$("#pane_" + fid).remove();
		}
		function toStart(fid){
			
			arry[fid].upload();
		}
		function toStop(fid){
			arry[fid].stop(true);
		}
	</script>
</body>
</html>
