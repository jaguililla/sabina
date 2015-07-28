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

      <a class="navbar-brand" href="/">My Blog</a>
    </div>

    <div id="navbar" class="collapse navbar-collapse">
      <#if username??>
        <p class="navbar-text">
          Welcome ${username} <a href="/logout">Logout</a> | <a href="/newpost">New Post</a>
        </p>
      </#if>

      <ul class="nav navbar-nav">
        <li class="active"><a href="#">item</a></li>
        <li><a href="#signup">Signup</a></li>
        <li><a href="#login">Login</a></li>
      </ul>
    </div>
  </div>
</nav>
