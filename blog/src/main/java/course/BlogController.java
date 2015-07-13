package course;

import static sabina.Sabina.*;
import static sabina.view.FreeMarkerView.renderFreeMarker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.lang3.StringEscapeUtils;

import org.bson.Document;
import sabina.Request;
import sabina.Response;

/**
 * This class encapsulates the controllers for the blog web application.  It delegates all
 * interaction with MongoDB
 * to three Data Access Objects (DAOs).
 * <p>
 * It is also the entry point into the web application.
 */
public class BlogController {
    public static void main (String[] args) throws IOException {
        new BlogController (args.length == 0? "mongodb://localhost" : args[0]);
    }

    private final BlogPostDAO blogPostDAO;
    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;

    public BlogController (String mongoURIString) throws IOException {
        final MongoClient mongoClient = new MongoClient (new MongoClientURI (mongoURIString));
        final MongoDatabase blogDatabase = mongoClient.getDatabase ("blog");

        blogPostDAO = new BlogPostDAO (blogDatabase);
        userDAO = new UserDAO (blogDatabase);
        sessionDAO = new SessionDAO (blogDatabase);

        initializeRoutes ();
        start (8082);
    }

    private void initializeRoutes () throws IOException {
        // this is the blog home page
        get ("/", request -> {
            String username = sessionDAO.findUserNameBySessionId (getSession (request));

            List<Document> posts = blogPostDAO.findByDateDescending (10);
            Map<Object, Object> root = new HashMap<> ();

            root.put ("myposts", posts);
            if (username != null) {
                root.put ("username", username);
            }

            return renderFreeMarker ("blog_template.ftl", root);
        });

        // used to display actual blog post detail page
        get ("/post/:permalink", (Request request, Response response) -> {
            String permalink = request.params (":permalink");

            System.out.println ("/post: get " + permalink);

            Document post = blogPostDAO.findByPermalink (permalink);
            if (post == null) {
                response.redirect ("/post_not_found");
                return "";
            }
            else {
                // empty comment to hold new comment in form at bottom of blog entry detail page
                Map<Object, Object> newComment = new HashMap<> ();
                newComment.put ("name", "");
                newComment.put ("email", "");
                newComment.put ("body", "");

                Map<Object, Object> root = new HashMap<> ();

                root.put ("post", post);
                root.put ("comment", newComment);

                return renderFreeMarker ("entry_template.ftl", root);
            }
        });

        // handle the signup post
        post ("/signup", (Request request, Response response) -> {
            String email = request.queryParams ("email");
            String username = request.queryParams ("username");
            String password = request.queryParams ("password");
            String verify = request.queryParams ("verify");

            Map<String, String> root = new HashMap<> ();
            root.put ("username", StringEscapeUtils.escapeHtml4 (username));
            root.put ("email", StringEscapeUtils.escapeHtml4 (email));

            if (validateSignup (username, password, verify, email, root)) {
                // good user
                System.out.println ("Signup: Creating user with: " + username + " " + password);
                if (!userDAO.addUser (username, password, email)) {
                    // duplicate user
                    root.put ("username_error",
                        "Username already in use, Please choose another");
                    return renderFreeMarker ("signup.ftl", root);
                }
                else {
                    // good user, let's start a session
                    String sessionID = sessionDAO.startSession (username);
                    System.out.println ("Session ID is" + sessionID);

                    response.addCookie (new Cookie ("session", sessionID));
                    response.redirect ("/welcome");
                    return "";
                }
            }
            else {
                // bad signup
                System.out.println ("User Registration did not validate");
                return renderFreeMarker ("signup.ftl", root);
            }
        });

        // present signup form for blog
        get ("/signup", (Request request, Response response) -> {
            HashMap<Object, Object> root = new HashMap<> ();

            // initialize values for the form.
            root.put ("username", "");
            root.put ("password", "");
            root.put ("email", "");
            root.put ("password_error", "");
            root.put ("username_error", "");
            root.put ("email_error", "");
            root.put ("verify_error", "");

            return renderFreeMarker ("signup.ftl", root);
        });

        // will present the form used to process new blog posts
        get ("/newpost", (Request request, Response response) -> {
            // get cookie
            String username = sessionDAO.findUserNameBySessionId (getSession (request));

            if (username == null) {
                // looks like a bad request. user is not logged in
                response.redirect ("/login");
                return "";
            }
            else {
                HashMap<Object, Object> root = new HashMap<> ();
                root.put ("username", username);

                return renderFreeMarker ("newpost_template.ftl", root);
            }
        });

        // handle the new post submission
        post ("/newpost", (Request request, Response response) -> {
            String title = StringEscapeUtils.escapeHtml4 (request.queryParams ("subject"));
            String post = StringEscapeUtils.escapeHtml4 (request.queryParams ("body"));
            String tags = StringEscapeUtils.escapeHtml4 (request.queryParams ("tags"));

            String username = sessionDAO.findUserNameBySessionId (getSession (request));

            if (username == null) {
                response.redirect ("/login");    // only logged in users can post to blog
                return "";
            }
            else if (title.equals ("") || post.equals ("")) {
                // redisplay page with errors
                Map<String, String> root = new HashMap<> ();
                root.put ("errors", "post must contain a title and blog entry.");
                root.put ("subject", title);
                root.put ("username", username);
                root.put ("tags", tags);
                root.put ("body", post);
                return renderFreeMarker ("newpost_template.ftl", root);
            }
            else {
                // extract tags
                ArrayList<String> tagsArray = extractTags (tags);

                // substitute some <p> for the paragraph breaks
                post = post.replaceAll ("\\r?\\n", "<p>");

                String permalink = blogPostDAO.addPost (title, post, tagsArray, username);

                // now redirect to the blog permalink
                response.redirect ("/post/" + permalink);
                return "";
            }
        });

        get ("/welcome", (Request request, Response response) -> {
            String cookie = getSession (request);
            String username = sessionDAO.findUserNameBySessionId (cookie);

            if (username == null) {
                System.out.println ("welcome() can't identify the user, redirecting to signup");
                response.redirect ("/signup");
                return "";
            }
            else {
                HashMap<Object, Object> root = new HashMap<> ();

                root.put ("username", username);

                return renderFreeMarker ("welcome.ftl", root);
            }
        });

        // process a new comment
        post ("/newcomment", (Request request, Response response) -> {
            String name = StringEscapeUtils.escapeHtml4 (request.queryParams ("commentName"));
            String email = StringEscapeUtils.escapeHtml4 (request.queryParams ("commentEmail"));
            String body = StringEscapeUtils.escapeHtml4 (request.queryParams ("commentBody"));
            String permalink = request.queryParams ("permalink");

            Document post = blogPostDAO.findByPermalink (permalink);
            if (post == null) {
                response.redirect ("/post_not_found");
                return "";
            }
            // check that comment is good
            else if (name.equals ("") || body.equals ("")) {
                // bounce this back to the user for correction
                HashMap<Object, Object> root = new HashMap<> ();
                HashMap<Object, Object> comment = new HashMap<> ();

                comment.put ("name", name);
                comment.put ("email", email);
                comment.put ("body", body);
                root.put ("comment", comment);
                root.put ("post", post);
                root.put ("errors", "Post must contain your name and an actual comment");

                return renderFreeMarker ("entry_template.ftl", root);
            }
            else {
                blogPostDAO.addPostComment (name, email, body, permalink);
                response.redirect ("/post/" + permalink);
                return "";
            }
        });

        // present the login page
        get ("/login", request -> {
            HashMap<Object, Object> root = new HashMap<> ();

            root.put ("username", "");
            root.put ("login_error", "");

            return renderFreeMarker ("login.ftl", root);
        });

        // process output coming from login form. On success redirect folks to the welcome page
        // on failure, just return an error and let them try again.
        post ("/login", (Request request, Response response) -> {
            String username = request.queryParams ("username");
            String password = request.queryParams ("password");

            System.out.println ("Login: User submitted: " + username + "  " + password);

            Document user = userDAO.validateLogin (username, password);

            if (user != null) {

                // valid user, let's log them in
                String sessionID = sessionDAO.startSession (user.get ("_id").toString ());

                if (sessionID == null) {
                    response.redirect ("/internal_error");
                    return "";
                }
                else {
                    // set the cookie for the user's browser
                    response.addCookie (new Cookie ("session", sessionID));

                    response.redirect ("/welcome");
                    return "";
                }
            }
            else {
                HashMap<Object, Object> root = new HashMap<> ();

                root.put ("username", StringEscapeUtils.escapeHtml4 (username));
                root.put ("password", "");
                root.put ("login_error", "Invalid Login");
                return renderFreeMarker ("login.ftl", root);
            }
        });

        // Show the posts filed under a certain tag
        get ("/tag/:thetag", request -> {
            String username = sessionDAO.findUserNameBySessionId (getSession (request));
            HashMap<Object, Object> root = new HashMap<> ();

            String tag = StringEscapeUtils.escapeHtml4 (request.params (":thetag"));
            List<Document> posts = blogPostDAO.findByTagDateDescending (tag);

            root.put ("myposts", posts);
            if (username != null) {
                root.put ("username", username);
            }

            return renderFreeMarker ("blog_template.ftl", root);
        });

        // tells the user that the URL is dead
        get ("/post_not_found", request -> {
            HashMap<Object, Object> root = new HashMap<> ();
            return renderFreeMarker ("post_not_found.ftl", root);
        });

        // allows the user to logout of the blog
        get ("/logout", (Request request, Response response) -> {
            String sessionID = getSession (request);

            if (sessionID == null) {
                // no session to end
                response.redirect ("/login");
            }
            else {
                // deletes from session table
                sessionDAO.endSession (sessionID);

                // this should delete the cookie
                Cookie c = getSessionCookie (request);
                if (c != null)
                    c.setMaxAge (0);

                response.addCookie (c);

                response.redirect ("/login");
            }
        });

        // used to process internal errors
        get ("/internal_error", request -> {
            HashMap<Object, Object> root = new HashMap<> ();

            root.put ("error", "System has encountered an error.");
            return renderFreeMarker ("error_template.ftl", root);
        });
    }

    private Cookie getSessionCookie (final Request request) {
        if (request.getCookies () == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies ()) {
            if (cookie.getName ().equals ("session")) {
                return cookie;
            }
        }
        return null;
    }

    private String getSession (final Request request) {
        Cookie sessionCookie = getSessionCookie (request);
        return sessionCookie == null? null : sessionCookie.getValue ();
    }

    // validates that the registration form has been filled out right and username conforms
    public boolean validateSignup (
        String username, String password, String verify, String email, Map<String, String> errors) {

        String USER_RE = "^[a-zA-Z0-9_-]{3,20}$";
        String PASS_RE = "^.{3,20}$";
        String EMAIL_RE = "^[\\S]+@[\\S]+\\.[\\S]+$";

        errors.put ("username_error", "");
        errors.put ("password_error", "");
        errors.put ("verify_error", "");
        errors.put ("email_error", "");

        if (!username.matches (USER_RE)) {
            errors.put ("username_error", "invalid username. try just letters and numbers");
            return false;
        }

        if (!password.matches (PASS_RE)) {
            errors.put ("password_error", "invalid password.");
            return false;
        }

        if (!password.equals (verify)) {
            errors.put ("verify_error", "password must match");
            return false;
        }

        if (!email.equals ("")) {
            if (!email.matches (EMAIL_RE)) {
                errors.put ("email_error", "Invalid Email Address");
                return false;
            }
        }

        return true;
    }

    // tags the tags string and put it into an array
    private ArrayList<String> extractTags (String tags) {

        // probably more efficent ways to do this.
        //
        // whitespace = re.compile('\s')

        tags = tags.replaceAll ("\\s", "");
        String tagArray[] = tags.split (",");

        // let's clean it up, removing the empty string and removing dups
        ArrayList<String> cleaned = new ArrayList<> ();
        for (String tag : tagArray) {
            if (!tag.equals ("") && !cleaned.contains (tag)) {
                cleaned.add (tag);
            }
        }

        return cleaned;
    }
}
