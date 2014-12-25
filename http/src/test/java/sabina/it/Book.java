/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package sabina.it;

/**
 * TODO .
 *
 * @author jam
 */
class Book {
    private String author;
    private String title;

    Book (String author, String title) {
        this.author = author;
        this.title = title;
    }

    String getAuthor () { return author; }
    String getTitle () { return title; }
    void setAuthor (String author) { this.author = author; }
    void setTitle (String title) { this.title = title; }
}
