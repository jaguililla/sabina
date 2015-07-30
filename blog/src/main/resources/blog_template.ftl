<#assign pageTitle = "Blongo">
<#include "fragments/header.ftl">

<#include "fragments/navbar.ftl">

<div class="container">
  <div class="jumbotron">
    <div class="row">
      <div id="logoCell" class="col-xs-3">
        <span id="logo" class="glyphicon glyphicon-bullhorn" aria-hidden="true" />
      </div>
      <div class="col-xs-9">
        <h1>Blongo</h1>

        <p>
          This is a <a href="http://there4.co/sabina">Sabina Framework</a> sample
          project. It is a blog used in MongoDB's course
          <a href="https://university.mongodb.com/courses/M101J/about">"M101J:
          MongoDB for Java Developers"</a>.
        </p>
      </div>
    </div>
  </div>

  <#list myposts as post>
    <div class="row">
      <div class="col-xs-12">
        <h2><a href="/post/${post["permalink"]}">${post["title"]}</a></h2>

        <p>
          Posted ${post["date"]?datetime} <i>By ${post["author"]}</i>
          <br />
          Comments:

          <#if post["comments"]??>
            <#assign numComments = post["comments"]?size>
          <#else>
            <#assign numComments = 0>
          </#if>

          <a href="/post/${post["permalink"]}">${numComments}</a>
        </p>

        <hr />

        <p>${post["body"]!""}</p>

        <p>
          <em>Filed Under</em>:
          <#if post["tags"]??>
            <#list post["tags"] as tag>
              <a href="/tag/${tag}">${tag}</a>
            </#list>
          </#if>
        </p>
      </div>
    </div>
  </#list>
</div>

<#include "fragments/footer.ftl">
