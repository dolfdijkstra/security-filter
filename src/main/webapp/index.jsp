<html>
<body>
<h2>Test Forms!</h2>
<p>Get</p>
<a href="HelloWorld?c=Page">HelloWorld</a>
<br />
<p>Get via form</p>
<form action="HelloWorld"><input type="text" name="c"
	value="Page" /><input type="submit" />
	</form>
<br />
<p>Post</p>
<form action="HelloWorld" method="post"><input type="text"
	name="c" value="Page" /> <input type="submit" /></form>
<br />
<p>multipart post</p>
<form action="HelloWorld" method="post" enctype="multipart/form-data">
<input type="text" name="c" value="Page" /> <input type="file"
	enctype="multipart/form-data" name="file" /> <input type="submit" />

</form>

</body>
</html>
