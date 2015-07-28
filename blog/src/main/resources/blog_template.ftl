<#assign pageTitle = "My Blog">
<#include "fragments/header.ftl">

<#include "fragments/navbar.ftl">

<div class="container">
  <h1>My Blog</h1>

  <#list myposts as post>
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
  </#list>
</div>

<#include "fragments/footer.ftl">
