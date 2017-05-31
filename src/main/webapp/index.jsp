<%@ page language="java"  contentType="text/html; charset=UTF-8" %>

<html>
<body>
<h2>Hello World!</h2>
<!-- 上传文件 -->
<form name = "form1" action = "/manage/product/upload.do" methon = "post" enctype="multipart/form-data">
	<input type = "file" name = "upload_file"/>
	<input type = "submit" value = "springmvc上传文件"/>
</form>
<!-- 下载文件 -->
<form name = "form2" action = "/manage/product/richtext_img_upload.do" methon = "post" enctype="multipart/form-data">
	<input type = "file" name = "upload_file"/>
	<input type = "submit" value = "富文本上传文件"/>
</form>
</body>
</html>
