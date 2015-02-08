package sabina.benchmark;

import static java.lang.Integer.parseInt;
import static sabina.Sabina.*;
import static sabina.content.JsonContent.toJson;
import static sabina.view.MustacheView.renderMustache;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import sabina.Exchange;
import sabina.Request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.sql.DataSource;

/**
 * When it is implemented, add this to benchmark_config
 * "fortune_url": "/fortunes",
 * "update_url": "/updates",
 */
final class Application {
    private static final Properties SETTINGS = loadConfiguration ();
    private static final DataSource DATA_SOURCE = createSessionFactory ();
    private static final String SELECT_WORLD = "select * from world where id = ?";
    private static final String UPDATE_WORLD = "update world set randomNumber = ? where id = ?";
    private static final String SELECT_FORTUNES = "select * from fortune";

    private static final int DB_ROWS = 10000;
    private static final String MESSAGE = "Hello, World!";
    private static final String CONTENT_TYPE_TEXT = "text/plain";

    private static Properties loadConfiguration () {
        try {
            Properties settings = new Properties ();
            settings.load (Class.class.getResourceAsStream ("/server.properties"));
            return settings;
        }
        catch (Exception ex) {
            throw new RuntimeException (ex);
        }
    }

    private static DataSource createSessionFactory () {
        try {
            ComboPooledDataSource dataSource = new ComboPooledDataSource ();
            dataSource.setJdbcUrl (SETTINGS.getProperty ("mysql.uri"));
            dataSource.setMinPoolSize (32);
            dataSource.setMaxPoolSize (256);
            dataSource.setCheckoutTimeout (1800);
            dataSource.setMaxStatements (50);
            return dataSource;
        }
        catch (Exception ex) {
            throw new RuntimeException (ex);
        }
    }

    private static int getQueries (final Request request) {
        try {
            String parameter = request.queryParams ("queries");
            if (parameter == null)
                return 1;

            int queries = parseInt (parameter);
            if (queries < 1)
                return 1;
            if (queries > 500)
                return 500;

            return queries;
        }
        catch (NumberFormatException ex) {
            return 1;
        }
    }

    private static Object getJson (Exchange it) {
        it.response.type ("application/json");
        return toJson (new Message ());
    }

    private static Object getDb (Exchange it) {
        final int queries = getQueries (it.request);
        final World[] worlds = new World[queries];

        try (final Connection con = DATA_SOURCE.getConnection ()) {
            final Random random = ThreadLocalRandom.current ();
            final PreparedStatement stmt = con.prepareStatement (SELECT_WORLD);

            for (int ii = 0; ii < queries; ii++) {
                stmt.setInt (1, random.nextInt (DB_ROWS) + 1);
                final ResultSet rs = stmt.executeQuery ();
                while (rs.next ())
                    worlds[ii] = new World (rs.getInt (1), rs.getInt (2));
            }
        }
        catch (SQLException e) {
            e.printStackTrace ();
        }

        it.response.type ("application/json");
        return toJson (it.request.queryParams ("queries") == null? worlds[0] : worlds);
    }

    private static Object getFortunes (Exchange it) {
        final List<Fortune> fortunes = new ArrayList<> ();

        try (final Connection con = DATA_SOURCE.getConnection ()) {
            final ResultSet rs = con.prepareStatement (SELECT_FORTUNES).executeQuery ();
            while (rs.next ())
                fortunes.add (new Fortune (rs.getInt (1), rs.getString (2)));
        }
        catch (SQLException e) {
            e.printStackTrace ();
        }

        fortunes.add (new Fortune (42, "Additional fortune added at request time."));
        fortunes.sort ((a, b) -> a.message.compareTo (b.message));

        it.response.type ("text/html; charset=utf-8");
        return renderMustache ("/fortunes.mustache", fortunes);
    }

    private static Object getUpdates (Exchange it) {
        final int queries = getQueries (it.request);
        final World[] worlds = new World[queries];

        try (final Connection con = DATA_SOURCE.getConnection ()) {
            con.setAutoCommit (false);
            final Random random = ThreadLocalRandom.current ();
            final PreparedStatement stmtSelect = con.prepareStatement (SELECT_WORLD);
            final PreparedStatement stmtUpdate = con.prepareStatement (UPDATE_WORLD);

            for (int ii = 0; ii < queries; ii++) {
                stmtSelect.setInt (1, random.nextInt (DB_ROWS) + 1);
                final ResultSet rs = stmtSelect.executeQuery ();
                while (rs.next ()) {
                    worlds[ii] = new World (rs.getInt (1), rs.getInt (2));
                    stmtUpdate.setInt (1, random.nextInt (DB_ROWS) + 1);
                    stmtUpdate.setInt (2, worlds[ii].id);
                    stmtUpdate.addBatch ();
                }
            }
            stmtUpdate.executeBatch ();
            con.commit ();
        }
        catch (SQLException e) {
            e.printStackTrace ();
        }

        it.response.type ("application/json");
        return toJson (it.request.queryParams ("queries") == null? worlds[0] : worlds);
    }

    private static Object getPlaintext (Exchange it) {
        it.response.type (CONTENT_TYPE_TEXT);
        return MESSAGE;
    }

    private static void addCommonHeaders (Exchange it) {
        it.header ("Server", "Undertow/1.1.2");
        it.response.raw ().addDateHeader ("Date", new Date ().getTime ());
    }

    public static void main (String[] args) {
        get ("/json", Application::getJson);
        get ("/db", Application::getDb);
        get ("/queries", Application::getDb);
        get ("/fortunes", Application::getFortunes);
        get ("/updates", Application::getUpdates);
        get ("/plaintext", Application::getPlaintext);
        after (Application::addCommonHeaders);

        setIpAddress (SETTINGS.getProperty ("web.host"));
        start (parseInt (SETTINGS.getProperty ("web.port")));
    }
}
