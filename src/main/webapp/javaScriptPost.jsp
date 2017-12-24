<%@ page language="java" import="java.util.*" contentType="text/html;charset=UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'javaScriptPost.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" src="https://code.jquery.com/jquery-3.2.0.js">
	</script>
	<script type="text/javascript">
		$(document).ready(function(){
			var max=30000;
			for(var i=1;i<=max;i++){
				$.post({
				url:"http://localhost:8080/redPacket/userRedPacket/grapRandomRedPacketByRedis?redPacketId=33&userId="+i,
				success:function(result){
					document.write("<strong>抢红包成功!</strong>");
				}
				});
			}
		});
	</script>

  </head>
  
  <body>
    并发抢红包！ <br>
  </body>
</html>
