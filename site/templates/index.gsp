<%include "header.gsp"%>
  <%include "menu.gsp"%>

  <div class="row">
    <div class="col-md-6 col-xs-6 col-sm-3">
      <img
        src="http://raw.githubusercontent.com/jamming/sabina/gh-pages/sabina-logo.png"
        alt="Project Logo"/>
    </div>
    <div class="col-md-6 col-xs-6 col-sm-3">
      <h1>Sabina</h1>
      <h2>
        A Sinatra inspired micro web framework for quickly creating web applications in Java with
        minimal effort
      </h2>
    </div>
  </div>

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

  <h2>Quick start</h2>

  <p>Add the Sabina dependency and you're ready to go:</p>

  <pre>
    <code class="java">import static sabina.Sabina.*;

public class HiWorld {
    public static void main (String[] args) {
        get(&quot;/hello&quot;, it -&gt; &quot;Hi World!&quot;).start ();
    }
}</code>
  </pre>

  <h2>Ignite and view at</h2>

  <p><code>http://localhost:4567/hello</code></p>

<%include "footer.gsp"%>
