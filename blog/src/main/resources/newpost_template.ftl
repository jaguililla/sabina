<!DOCTYPE html>

<html lang="en" class="no-js">
<head>
  <meta charset="utf-8" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />

  <link
    href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.2/css/bootstrap.min.css"
    rel="stylesheet" />
  <title>Create a new post</title>
</head>

<body>
<div class="container">
<#if username??>
    Welcome ${username} <a href="/logout">Logout</a> | <a href="/">Blog Home</a>

    <p>
</#if>
<form action="/newpost" method="POST">
    ${errors!""}
    <h2>Title</h2>
    <input type="text" name="subject" size="120" value="${subject!""}"><br>

    <h2>Blog Entry
        <h2>
            <textarea name="body" cols="120" rows="20">${body!""}</textarea><br>

            <h2>Tags</h2>
            Comma separated, please<br>
            <input type="text" name="tags" size="120" value="${tags!""}"><br>

            <p>
                <input type="submit" value="Submit">
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.2/js/bootstrap.min.js"></script>
</body>
</html>

