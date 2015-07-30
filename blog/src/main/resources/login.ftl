<#assign pageTitle = "Login">
<#include "fragments/header.ftl">

<#include "fragments/navbar.ftl">

<div class="container">
  <h2>Login</h2>

  <form method="post" class="form-horizontal">
    <div class="form-group">
      <label for="txtUsername" class="col-sm-2 control-label">Username</label>
      <div class="col-sm-10">
        <input
          type="text"
          name="username"
          class="form-control"
          id="txtUsername"
          placeholder="Username" />
      </div>
    </div>
    <div class="form-group">
      <label for="pwdPassword" class="col-sm-2 control-label">Password</label>
      <div class="col-sm-10">
        <input
          type="password"
          name="password"
          class="form-control"
          id="pwdPassword"
          placeholder="Password" />
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
