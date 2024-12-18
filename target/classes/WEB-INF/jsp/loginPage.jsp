<html>
<head>
<title>login</title>
</head>
<body>
<h2>LOGIN</h2>

<!-- Display error message -->
<pre>${errorMessage}</pre>

<form method="POST" action="login"> <!-- Updated action to "login" -->
    Name: <input type="text" name="name">
    Password: <input type="password" name="password">
    <input type="submit" value="Login">
</form>

</body>
</html>
