/*
 * Copyright © 2011 Juan José Aguililla. All rights reserved.
 *
 * This program comes WITHOUT ANY WARRANTY; It is free software, you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.0.
 * You may obtain a copy of the License at: http://www.gnu.org/licenses/gpl.html
 */

package sabina.util.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * TODO .
 *
 * @author jamming
 */
public class ShortFormat extends Formatter {
    /** {@inheritDoc} */
    @Override public String format (LogRecord aRecord) {
        return String.format ("%d %s%n", aRecord.getMillis (), aRecord.getMessage ());
    }
}
