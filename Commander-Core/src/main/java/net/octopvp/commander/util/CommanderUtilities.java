/*
 * Copyright (c) Badbird5907 2022.
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.octopvp.commander.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommanderUtilities {
    private static final Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);

    /**
     * @author Don't know who to give credit to for this method, found it somewhere a while ago.
     * @param time
     * @param future
     * @return
     * @throws Exception
     */
    public static long parseTime(String time, boolean future) throws Exception {
        Matcher matcher = timePattern.matcher(time);

        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        boolean found = false;

        while (matcher.find()) {
            if (matcher.group() == null || matcher.group().isEmpty()) continue;

            for (int c = 0; c < matcher.groupCount(); ++c) {
                if (matcher.group(c) == null || matcher.group(c).isEmpty()) continue;

                found = true;
                break;
            }

            if (!found) continue;

            if (matcher.group(1) != null && !matcher.group(1).isEmpty()) years = Integer.parseInt(matcher.group(1));
            if (matcher.group(2) != null && !matcher.group(2).isEmpty()) months = Integer.parseInt(matcher.group(2));
            if (matcher.group(3) != null && !matcher.group(3).isEmpty()) weeks = Integer.parseInt(matcher.group(3));
            if (matcher.group(4) != null && !matcher.group(4).isEmpty()) days = Integer.parseInt(matcher.group(4));
            if (matcher.group(5) != null && !matcher.group(5).isEmpty()) hours = Integer.parseInt(matcher.group(5));
            if (matcher.group(6) != null && !matcher.group(6).isEmpty()) minutes = Integer.parseInt(matcher.group(6));
            if (matcher.group(7) == null || matcher.group(7).isEmpty()) break;

            seconds = Integer.parseInt(matcher.group(7));
            break;
        }

        if (!found) throw new Exception("Illegal Date");

        GregorianCalendar calendar = new GregorianCalendar();

        int futureMultiplier = future ? 1 : -1;

        if (years > 0) calendar.add(Calendar.YEAR, years * futureMultiplier);
        if (months > 0) calendar.add(Calendar.MONTH, months * futureMultiplier);
        if (weeks > 0) calendar.add(Calendar.WEEK_OF_YEAR, weeks * futureMultiplier);
        if (days > 0) calendar.add(Calendar.DATE, days * futureMultiplier);
        if (hours > 0) calendar.add(Calendar.HOUR_OF_DAY, hours * futureMultiplier);
        if (minutes > 0) calendar.add(Calendar.MINUTE, minutes * futureMultiplier);
        if (seconds > 0) calendar.add(Calendar.SECOND, seconds * futureMultiplier);

        GregorianCalendar max = new GregorianCalendar();

        max.add(Calendar.YEAR, 10);
        return calendar.after(max) ? max.getTimeInMillis() : calendar.getTimeInMillis();
    }

}
