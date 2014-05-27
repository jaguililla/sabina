package spark.examples;

/**
 * Book domain class
 *
 * @author Per Wendel
 */
class Book {

    private String author;
    private String title;

    Book (String author, String title) {
        this.author = author;
        this.title = title;
    }

    String getAuthor () {
        return author;
    }

    String getTitle () {
        return title;
    }

    void setAuthor (String author) {
        this.author = author;
    }

    void setTitle (String title) {
        this.title = title;
    }
}
