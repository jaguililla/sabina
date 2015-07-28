<#assign pageTitle = "Login">
<#include "fragments/header.ftl">

<div class="container">
  <p>Need to Create an account? <a href="/signup">Signup</a></p>

  <h2>Login</h2>

  <form method="post" class="form-horizontal">
    <div class="form-group">
      <label for="inputEmail3" class="col-sm-2 control-label">Username</label>
      <div class="col-sm-10">
        <input type="email" class="form-control" id="inputEmail3" placeholder="Username">
      </div>
    </div>
    <div class="form-group">
      <label for="inputPassword3" class="col-sm-2 control-label">Password</label>
      <div class="col-sm-10">
        <input type="password" class="form-control" id="inputPassword3" placeholder="Password">
      </div>
    </div>
    <div class="form-group">
      <div class="col-sm-offset-2 col-sm-10">
        <button type="submit" class="btn btn-default">Sign in</button>
      </div>
    </div>
  </form>

  <form method="post">
    <table>
      <tr>
        <td>Username</td>
        <td><input type="text" name="username" value="${username}" /></td>
        <td></td>
      </tr>

      <tr>
        <td>Password</td>
        <td><input type="password" name="password" value="" /></td>
        <td>${login_error}</td>
      </tr>
    </table>
    <input type="submit" />
  </form>
</div>

<#include "fragments/footer.ftl">
