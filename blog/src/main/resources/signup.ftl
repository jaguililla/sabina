<#assign pageTitle = "Sign Up">
<#include "fragments/header.ftl">

<#include "fragments/navbar.ftl">

<div class="container">
  <div class="row">
    <div class="col-xs-12">
      <form method="post" class="form-horizontal">
        <h2 class="col-sm-10 col-md-offset-2">Signup</h2>
        <div class="form-group">
          <label for="txtUsername" class="col-sm-2 control-label">Username</label>
          <div class="col-sm-10">
            <input
              type="text" name="username" class="form-control" id="txtUsername" value="${username}"
              placeholder="Username" required autofocus />
          </div>
        </div>
        <#if username_error?? && username_error != "">
          <div class="col-sm-10 col-md-offset-2 alert alert-danger" role="alert">
            ${username_error}
          </div>
        </#if>
        <div class="form-group">
          <label for="pwdPassword" class="col-sm-2 control-label">Password</label>
          <div class="col-sm-10">
            <input
              type="password" name="password" class="form-control" id="pwdPassword"
              placeholder="Password" required />
          </div>
        </div>
        <#if password_error?? && password_error != "">
          <div class="col-sm-10 col-md-offset-2 alert alert-danger" role="alert">
            ${password_error}
          </div>
        </#if>
        <div class="form-group">
          <label for="pwdVerify" class="col-sm-2 control-label">Verify</label>
          <div class="col-sm-10">
            <input
              type="password" name="verify" class="form-control" id="pwdVerify"
              placeholder="Verify Password" required />
          </div>
        </div>
        <#if verify_error?? && verify_error != "">
          <div class="col-sm-10 col-md-offset-2 alert alert-danger" role="alert">
            ${verify_error}
          </div>
        </#if>
        <div class="form-group">
          <label for="txtEmail" class="col-sm-2 control-label">Email (optional)</label>
          <div class="col-sm-10">
            <input
              type="text" name="email" class="form-control" id="txtEmail" value="${email}"
              placeholder="Email" />
          </div>
        </div>
        <#if email_error?? && email_error != "">
          <div class="col-sm-10 col-md-offset-2 alert alert-danger" role="alert">
            ${email_error}
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
