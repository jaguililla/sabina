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
    </div>
  </div>

  <h2>Quick start</h2>

  <p>Add the Sabina dependency and you're ready to go:</p>

<pre><code class="java">import static sabina.Sabina.*;

public class HiWorld {
    public static void main (String[] args) {
        get(&quot;/hello&quot;, it -&gt; &quot;Hi World!&quot;).start ();
    }
}</code></pre>

  <h2>Ignite and view at</h2>

  <p><code>http://localhost:4567/hello</code></p>

<%include "footer.gsp"%>
