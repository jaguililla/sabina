<#assign pageTitle = "Login">
<#include "fragments/header.ftl">

<#include "fragments/navbar.ftl">

<div class="container">
  <div class="row">
    <div class="col-xs-12">
      <form method="post" class="form-horizontal">
        <h2 class="col-sm-10 col-md-offset-2">Login</h2>
        <div class="form-group">
          <label for="txtUsername" class="col-sm-2 control-label">Username</label>
          <div class="col-sm-10">
            <input
              type="text" name="username" class="form-control" id="txtUsername" value="${username}"
              placeholder="Username" required autofocus />
          </div>
        </div>
        <div class="form-group">
          <label for="pwdPassword" class="col-sm-2 control-label">Password</label>
          <div class="col-sm-10">
            <input
              type="password" name="password" class="form-control" id="pwdPassword"
              placeholder="Password" required />
          </div>
        </div>
        <#if login_error?? && login_error != "">
          <div class="col-sm-10 col-md-offset-2 alert alert-danger" role="alert">
            ${login_error}
          </div>
        </#if>
        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-10">
            <button type="submit" class="btn btn-default">Sign in</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>

<#include "fragments/footer.ftl">
