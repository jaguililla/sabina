<nav class="navbar navbar-inverse navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
      <button
        type="button"
        class="navbar-toggle collapsed"
        data-toggle="collapse"
        data-target="#navbar"
        aria-expanded="false"
        aria-controls="navbar">

        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>

      <a class="navbar-brand" href="/">
        <i id="logoSmall" class="glyphicon glyphicon-bullhorn" aria-hidden="true"></i> Blongo
      </a>
    </div>

    <div id="navbar" class="collapse navbar-collapse navbar-right">
      <#if username??>
        Welcome ${username} <a href="/logout">Logout</a>
        <a class="btn btn-primary navbar-btn" href="/newpost">New Post</a>
      <#else>
        <a class="btn btn-primary navbar-btn" href="/login">Login</a>
        <a class="btn btn-success navbar-btn" href="/signup">Signup</a>
      </#if>
    </div>
  </div>
</nav>
