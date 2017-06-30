<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE>
<html>
  <head>
    

  </head>
  
  <body>
   <div id="uploader" class="wu-example">  
                                <div class="queueList">  
                                    <div id="dndArea" class="placeholder">  
                                        <div id="filePicker"></div>  
                                        <p>或将照片拖到这里，单次最多可选300张</p>  
                                    </div>  
                                </div>  
                                <div class="statusBar" style="display: none;">  
                                    <div class="progress">  
                                        <span class="text">0%</span> <span class="percentage"></span>  
                                    </div>  
                                    <div class="info"></div>  
                                    <div class="btns">  
                                        <div id="filePicker2"></div>  
                                        <div class="uploadBtn">开始上传</div>  
                                    </div>  
                                </div>  
                            </div>  
  
<!-- webuploader -->  
		<link rel="stylesheet" type="text/css"  
    href="/static/js/webuploader/css.css">  
<link rel="stylesheet" type="text/css"  
    href="/static/js/webuploader/image.css">  
<script type="text/javascript" src="/static/js/webuploader/min.js"></script>  
<script type="text/javascript"  
    src="/static/js/webuploader/upload-image.js"></script>  
  </body>
</html>
