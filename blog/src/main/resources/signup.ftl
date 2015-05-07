<!DOCTYPE html>

<html lang="en" class="no-js">
<head>
  <meta charset="utf-8" />
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />

  <link
    href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.2/css/bootstrap.min.css"
    rel="stylesheet" />

  <title>Sign Up</title>
  <style type="text/css">
    .label {text-align: right}
    .error {color: red}
  </style>
</head>

  <body>
    <div class="container">
    Already a user? <a href="/login">Login</a><p>
    <h2>Signup</h2>
    <form method="post">
      <table>
        <tr>
          <td class="label">
            Username
          </td>
          <td>
            <input type="text" name="username" value="${username}">
          </td>
          <td class="error">
	    ${username_error!""}

          </td>
        </tr>

        <tr>
          <td class="label">
            Password
          </td>
          <td>
            <input type="password" name="password" value="">
          </td>
          <td class="error">
	    ${password_error!""}

          </td>
        </tr>

        <tr>
          <td class="label">
            Verify Password
          </td>
          <td>
            <input type="password" name="verify" value="">
          </td>
          <td class="error">
	    ${verify_error!""}

          </td>
        </tr>

        <tr>
          <td class="label">
            Email (optional)
          </td>
          <td>
            <input type="text" name="email" value="${email}">
          </td>
          <td class="error">
	    ${email_error!""}

          </td>
        </tr>
      </table>

      <input type="submit">
    </form>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.2/js/bootstrap.min.js"></script>
  </body>

</html>
