/*
 * Copyright 2015 Martin Kouba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.engine.convert;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.trimou.exception.MustacheException;
import org.trimou.exception.MustacheProblem;

/**
 * Converts {@link Object} to {@link Date} if possible, i.e. if the object is an
 * instance of {@link Date}, {@link Calendar}, {@link LocalDateTime},
 * {@link LocalDate}, {@link Long} or {@link String} (if the pattern is set).
 *
 * @author Martin Kouba
 */
public class ObjectToDateConverter implements Converter<Object, Date> {

    private final String pattern;

    public ObjectToDateConverter() {
        this(null);
    }

    public ObjectToDateConverter(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Date convert(Object value) {
        Date converted = null;
        if (value instanceof Date) {
            converted = (Date) value;
        } else if (value instanceof Calendar) {
            converted = ((Calendar) value).getTime();
        } else if (value instanceof LocalDateTime) {
            converted = Date.from(((LocalDateTime) value)
                    .atZone(ZoneId.systemDefault()).toInstant());
        } else if (value instanceof LocalDate) {
            converted = Date.from(((LocalDate) value)
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else if (value instanceof Long) {
            converted = new Date((Long) value);
        } else if (value instanceof BigDecimal) {
            converted = new Date(((BigDecimal) value).longValue());
        } else if (value instanceof String && pattern != null) {
            try {
                converted = new SimpleDateFormat(pattern).parse((String) value);
            } catch (ParseException e) {
                throw new MustacheException(
                        MustacheProblem.RENDER_GENERIC_ERROR,
                        "Unable to parse the string value %s using pattern %s",
                        value, pattern);
            }
        }
        return converted;
    }

}
