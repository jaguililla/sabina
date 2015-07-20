<%include "header.gsp"%>
  <%include "menu.gsp"%>

  <div class="row">
    <div class="col-md-4 col-xs-4 col-sm-2 text-right">
      <img
        src="http://raw.githubusercontent.com/jamming/sabina/gh-pages/sabina-black.png"
        alt="Project Logo"/>
    </div>
    <div class="col-md-8 col-xs-8 col-sm-4">
      <h1 id="project-title">Sabina</h1>
      <h2>
        A Sinatra inspired micro web framework for quickly creating web applications in Java with
        minimal effort
      </h2>
      <p>
        <a href="https://travis-ci.org/jamming/sabina">
          <img
            src="https://travis-ci.org/jamming/sabina.svg?branch=master"
            alt="Build Img"
            style="max-width:100%;">
        </a>
        <a href="https://coveralls.io/r/jamming/sabina">
          <img
            src="https://img.shields.io/coveralls/jamming/sabina.svg"
            alt="Coverage Img"
            style="max-width:100%;">
        </a>
      </p>
      <p>
        <a
          class="github-button"
          href="https://github.com/jamming/sabina"
          data-icon="octicon-star"
          data-count-href="/jamming/sabina/stargazers"
          data-count-api="/repos/jamming/sabina#stargazers_count">Star</a>
        <a
          class="github-button"
          href="https://github.com/jamming/sabina/fork"
          data-icon="octicon-git-branch"
          data-count-href="/jamming/sabina/network"
          data-count-api="/repos/jamming/sabina#forks_count">Fork</a>
        <a
          class="github-button"
          href="https://github.com/jamming/sabina/issues"
          data-icon="octicon-issue-opened"
          data-count-api="/repos/jamming/sabina#open_issues_count">Issue</a>
      </p>
    </div>
  </div>

  <h2>Add the dependency</h2>

  <p>
    First, you need to set up the <a href="https://bintray.com/bintray/jcenter">JCenter repository
    </a>. Then you can add the dependency:
  </p>

  <h4>Gradle</h4>

<pre><code class="groovy">dependencies {
    compile ('sabina:http:${config.projectVersion}') { transitive = false }
    // Import the backend you are going to use
    compile 'io.undertow:undertow-servlet:1.2.8.Final'
}</code></pre>

  <h4>Maven</h4>

<pre><code class="xml">&lt;dependency&gt;
    &lt;groupId&gt;sabina&lt;/groupId&gt;
    &lt;artifactId&gt;http&lt;/artifactId&gt;
    &lt;version&gt;${config.projectVersion}&lt;/version&gt;
&lt;/dependency&gt;</code></pre>

  <h2>Write the code</h2>

<pre><code class="java">import static sabina.Sabina.*;

public class HiWorld {
    public static void main (String[] args) {
        get(&quot;/hello&quot;, it -&gt; &quot;Hi World!&quot;).start ();
    }
}</code></pre>

  <h2>Ignite and view at</h2>

  <p><code>http://localhost:4567/hello</code></p>

<%include "footer.gsp"%>
