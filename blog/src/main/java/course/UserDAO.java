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
import static sabina.util.log.Logger.getLogger;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Random;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import sabina.util.log.Logger;

public class UserDAO {
    private static final Logger LOG = getLogger (UserDAO.class);

    private final MongoCollection<Document> usersCollection;
    private Random random = new SecureRandom ();

    public UserDAO (final MongoDatabase blogDatabase) {
        usersCollection = blogDatabase.getCollection ("users");
    }

    // validates that username is unique and insert into db
    public boolean addUser (String username, String password, String email) {
        String passwordHash = makePasswordHash (password, Integer.toString (random.nextInt ()));

        Document user = new Document ();

        user.append ("_id", username).append ("password", passwordHash);

        if (email != null && !email.equals ("")) {
            // the provided email address
            user.append ("email", email);
        }

        usersCollection.insertOne (user);
        return true;
    }

    private String makePasswordHash (String password, String salt) {
        try {
            String saltedAndHashed = password + "," + salt;
            MessageDigest digest = MessageDigest.getInstance ("MD5");
            digest.update (saltedAndHashed.getBytes ());

            Encoder encoder = Base64.getEncoder ();

            byte hashedBytes[] = (new String (digest.digest (), "UTF-8")).getBytes ();
            return encoder.encodeToString (hashedBytes) + "," + salt;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException ("MD5 is not available", e);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException ("UTF-8 unavailable?  Not a chance", e);
        }
    }

    public Document validateLogin (String username, String password) {
        Document user;

        user = usersCollection.find (eq ("_id", username)).first ();

        if (user == null) {
            LOG.severe ("User not in database");
            return null;
        }

        String hashedAndSalted = user.get ("password").toString ();

        String salt = hashedAndSalted.split (",")[1];

        if (!hashedAndSalted.equals (makePasswordHash (password, salt))) {
            LOG.severe ("Submitted password is not a match");
            return null;
        }

        return user;
    }
}
