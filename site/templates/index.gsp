<%include "header.gsp"%>
  <%include "menu.gsp"%>

  <p id="header">
    <img
      src="http://raw.githubusercontent.com/jamming/sabina/gh-pages/sabina-logo.png"
      alt="Project Logo"/>
    <h1>
      A Sinatra inspired micro web framework for quickly creating web applications in Java with
      minimal effort
    </h1>
  </p>

  <h2>News</h2>

  <p>Sabina 2.0.0 re-written for Java 8 and Lambdas available on Bintray and Maven central!</p>

  <h2>Quick start</h2>

  <p>Add the Sabina maven dependency and you're ready to go:</p>

  <pre>
    <code class="java">
import static sabina.Sabina.*;

public class HelloWorld {
    public static void main(String[] args) {
        get(&quot;/hello&quot;, it -&gt; &quot;Hello World&quot;);
    }
}
    </code>
  </pre>

  <h2>Ignite and view at</h2>

  <p><code>http://localhost:4567/hello</code></p>

<%include "footer.gsp"%>
