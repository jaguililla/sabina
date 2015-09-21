/*
 * POPAPP Copyright © 2011 Juan José Aguililla. All rights reserved.
 *
 * This program comes WITHOUT ANY WARRANTY; It is free software, you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.0.
 * You may obtain a copy of the License at: http://www.gnu.org/licenses/gpl.html
 */

package sabina.util.log;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * TODO .
 *
 * @author jamming
 */
public final class DailyFileHandler extends StreamHandler {
    private volatile LocalDate logDate = LocalDate.now ();
    private String fileName = "application-%tF.log"; // Eval by String::format against a date
    private int fileCount = 5;

    public DailyFileHandler () throws IOException {
        open ();
    }

    private void open () throws FileNotFoundException {
        final FileOutputStream out = new FileOutputStream (String.format (fileName, logDate), true);
        final BufferedOutputStream bufOutput = new BufferedOutputStream (out);
        setOutputStream (bufOutput);
    }

    @Override public synchronized void publish (LogRecord record) {
        if (LocalDate.now ().isAfter (logDate)) {
            // Rotate
            // Close
            // Create new
            // Remove extra files
//            setOutputStream (new FileOutputStream (""));
        }
        super.publish (record);
    }
}
