/*
 * Copyright © 2015 Juan José Aguililla. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

package sabina.benchmark;

import static java.lang.Integer.parseInt;
import static sabina.benchmark.Application.DB_ROWS;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.mongodb.*;

final class MongoDbRepository implements Repository {
    private static final String [] FORTUNES = {
        "fortune: No such file or directory",
        "A computer scientist is someone who fixes things that aren''t broken.",
        "After enough decimal places, nobody gives a damn.",
        "A bad random number generator: 1, 1, 1, 1, 1, 4.33e+67, 1, 1, 1",
        "A computer program does what you tell it to do, not what you want it to do.",
        "Emacs is a nice operating system, but I prefer UNIX. — Tom Christaensen",
        "Any program that runs right is obsolete.",
        "A list is only as strong as its weakest link. — Donald Knuth",
        "Feature: A bug with seniority.",
        "Computers make very fast, very accurate mistakes.",
        "<script>alert(\"This should not be displayed in a browser alert box.\");</script>",
        "フレームワークのベンチマーク"
    };

    private DBCollection worldCollection;
    private DBCollection fortuneCollection;

    MongoDbRepository (Properties settings) {
        try {
            final int PORT = parseInt (settings.getProperty ("mongodb.port"));
            final String HOST = settings.getProperty ("mongodb.host");
            final String DATABASE = settings.getProperty ("mongodb.database");
            final String WORLD = settings.getProperty ("mongodb.world.collection");
            final String FORTUNE = settings.getProperty ("mongodb.fortune.collection");

            MongoClient mongoClient = new MongoClient (HOST, PORT);
            DB db = mongoClient.getDB (DATABASE);
            worldCollection = db.getCollection (WORLD);
            fortuneCollection = db.getCollection (FORTUNE);

            loadData ();
        }
        catch (UnknownHostException e) {
            throw new RuntimeException (e);
        }
    }

    private void loadData () {
        if (fortuneCollection.count () == 0) {
            int id = 0;
            for (String fortune : FORTUNES) {
                fortuneCollection.insert (
                    new BasicDBObject ("_id", ++id).append ("message", fortune)
                );
            }
        }

        if (worldCollection.count () == 0) {
            final Random random = ThreadLocalRandom.current ();
            for (int ii = 1; ii <= DB_ROWS; ii++) {
                int randomNumber = random.nextInt (DB_ROWS) + 1;
                worldCollection.insert (
                    new BasicDBObject ("_id", ii).append ("randomNumber", randomNumber)
                );
            }
        }
    }

    @Override public List<Fortune> getFortunes () {
        List<Fortune> fortunes = new ArrayList<> ();

        fortuneCollection.find ().forEach (dbo ->
            fortunes.add (new Fortune ((Integer)dbo.get ("_id"), (String)dbo.get ("message")))
        );

        return fortunes;
    }

    @Override public World[] getWorlds (int queries, boolean update) {
        final World[] worlds = new World[queries];
        final Random random = ThreadLocalRandom.current ();

        for (int ii = 0; ii < queries; ii++) {
            int id = random.nextInt (DB_ROWS) + 1;
            worlds[ii] = update? updateWorld (id, random.nextInt (DB_ROWS) + 1) : findWorld (id);
        }

        return worlds;
    }

    private World findWorld (int id) {
        return createWorld (worldCollection.findOne (id));
    }

    private World createWorld (DBObject world) {
        return new World ((Integer)world.get ("_id"), (Integer)world.get ("randomNumber"));
    }

    public World updateWorld (int id, int random) {
        worldCollection.findAndModify (
            new BasicDBObject ("_id", id),
            new BasicDBObject ("$set", new BasicDBObject ().append ("randomNumber", random))
        );

        return new World (id, random);
    }
}
