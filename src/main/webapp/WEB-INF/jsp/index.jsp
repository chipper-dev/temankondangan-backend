<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>JSP Page</title>
</head>

<body>
	<h1>File Upload Example</h1>
	<br>
	<hr>
	<h4 style="color: red;">${message}</h4>
	<form action="/profile/fileupload" method="POST" enctype="multipart/form-data">
		<table>
			<tr>
				<td><input type="text" name="name" required></td>
			</tr>
			<tr>
				<td><input type="file" name="file" required></td>
			</tr>
			<tr>
				<td><input type="submit"></td>
			</tr>
		</table>
	</form>
</body>

</html>