/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package course;

import static com.mongodb.client.model.Filters.eq;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.slf4j.Logger;

public class BlogPostDAO {
    private static final Logger LOG = getLogger (BlogPostDAO.class);

    MongoCollection<Document> postsCollection;

    public BlogPostDAO (final MongoDatabase blogDatabase) {
        postsCollection = blogDatabase.getCollection ("posts");
    }

    public Document findByPermalink (String permalink) {
        return postsCollection.find (eq ("permalink", permalink)).first ();
    }

    public List<Document> findByDateDescending (int limit) {
        return postsCollection.find ()
            .sort (new Document ("date", -1))
            .limit (limit)
            .into (new ArrayList<> ());
    }

    public List<Document> findByTagDateDescending (final String tag) {
        Document query = new Document ("tags", tag);
        LOG.info ("/tag query: " + query.toString ());

        return postsCollection.find (query)
            .sort (new Document ("date", -1))
            .limit (10)
            .into (new ArrayList<> ());
    }

    public String addPost (String title, String body, List<?> tags, String username) {
        LOG.info ("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll ("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll ("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase ();

        Document post = new Document ("title", title);
        post.append ("author", username);
        post.append ("body", body);
        post.append ("permalink", permalink);
        post.append ("tags", tags);
        post.append ("comments", new ArrayList<> ());
        post.append ("date", new Date ());

        try {
            postsCollection.insertOne (post);
            LOG.info ("Inserting blog post with permalink " + permalink);
        }
        catch (Exception e) {
            LOG.error ("Error inserting post", e);
            return null;
        }

        return permalink;
    }

    public void addPostComment (
        final String name, final String email, final String body, final String permalink) {

        Document comment = new Document ("author", name).append ("body", body);
        if (email != null && !email.equals ("")) {
            comment.append ("email", email);
        }

        postsCollection.updateOne (
            eq ("permalink", permalink),
            new Document ("$push", new Document ("comments", comment)),
            new UpdateOptions ().upsert (false));
    }
}
