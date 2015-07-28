<#assign pageTitle = "Welcome">
<#include "fragments/header.ftl">

<div class="container">
  <h1>Welcome ${username}</h1>

  <ul>
    <li><a href="/">Goto Blog Home</a></li>
    <li><a href="/logout">Logout</a></li>
    <li><a href="/newpost">Create a New Post</a></li>
  </ul>
</div>

<#include "fragments/footer.ftl">
