<#assign pageTitle = "Sign Up">
<#include "fragments/header.ftl">

<#include "fragments/navbar.ftl">

<div class="container">
  <h2>Signup</h2>

  <form method="post">
    <table>
      <tr>
        <td>Username</td>
        <td><input type="text" name="username" value="${username}" /></td>
        <td>${username_error!""}</td>
      </tr>

      <tr>
        <td>Password</td>
        <td><input type="password" name="password" value="" /></td>
        <td>${password_error!""}</td>
      </tr>

      <tr>
        <td>Verify Password</td>
        <td><input type="password" name="verify" value="" /></td>
        <td>${verify_error!""}</td>
      </tr>

      <tr>
        <td>Email (optional)</td>
        <td><input type="text" name="email" value="${email}" /></td>
        <td>${email_error!""}</td>
      </tr>
    </table>

    <input type="submit">
  </form>
</div>

<#include "fragments/footer.ftl">
