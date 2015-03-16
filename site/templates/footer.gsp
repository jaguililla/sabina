      </div>
      <div id="push"></div>
    </div>

    <footer id="footer">
      <div class="container">
        <p class="muted credit">
          &copy; 2014 | Mixed with <a href="http://getbootstrap.com/">Bootstrap v3.1.1</a>
          | Baked with <a href="http://jbake.org">JBake ${version}</a>
          | Founded by <a href="//www.linkedin.com/in/jaguililla">Juanjo Aguililla</a>
          | Based in <a href="//www.sparkjava.com">Spark</a>
        </p>
        <p>
          <div id="fb-root"></div>
          <script>
            (function(d, s, id) {
              var js, fjs = d.getElementsByTagName(s)[0];
              if (d.getElementById(id)) return;
              js = d.createElement(s); js.id = id;
              js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.0";
              fjs.parentNode.insertBefore(js, fjs);
            }(document, 'script', 'facebook-jssdk'));
          </script>

          <script src="https://platform.linkedin.com/in.js" type="text/javascript">
            lang: en_US
          </script>
          <script type="IN/Share"></script>

          <a
            href="https://twitter.com/share"
            class="twitter-share-button"
            data-via="jaguililla"
            data-count="none">

            Tweet
          </a>
          <script>
            !function(d,s,id){
            var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';
            if(!d.getElementById(id)){js=d.createElement(s);
            js.id=id;js.src=p+'://platform.twitter.com/widgets.js';
            fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');
          </script>

          <div class="g-plus" data-action="share" data-annotation="none"></div>
        </p>
      </div>
    </footer>

    <script src="${config.cloudflare}/jquery/2.0.3/jquery.min.js"></script>
    <script src="${config.cloudflare}/twitter-bootstrap/3.1.0/js/bootstrap.min.js"></script>
    <script src="${config.cloudflare}/prettify/r298/prettify.min.js"></script>
    <!-- For Google plus link -->
    <script src="https://apis.google.com/js/platform.js" async defer></script>
    <!-- For Github links -->
    <script async defer id="github-bjs" src="https://buttons.github.io/buttons.js"></script>
  </body>
</html>
