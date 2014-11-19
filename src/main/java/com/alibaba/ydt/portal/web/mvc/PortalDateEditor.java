package com.alibaba.ydt.portal.web.mvc;

import org.springframework.beans.propertyeditors.CustomDateEditor;

import java.text.DateFormat;
import java.text.ParseException;

/**
 * <p>
 * 类说明
 * </p>
 * Time: 12-2-29 下午6:08
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class PortalDateEditor extends CustomDateEditor {

    private final DateFormat dateFormat;

    private final boolean allowEmpty;

    private final int exactDateLength;


    /**
     * Create a new CustomDateEditor instance, using the given DateFormat
     * for parsing and rendering.
     * <p>The "allowEmpty" parameter states if an empty String should
     * be allowed for parsing, i.e. get interpreted as null value.
     * Otherwise, an IllegalArgumentException gets thrown in that case.
     *
     * @param dateFormat DateFormat to use for parsing and rendering
     * @param allowEmpty if empty strings should be allowed
     */
    public PortalDateEditor(DateFormat dateFormat, boolean allowEmpty) {
        super(dateFormat, allowEmpty, -1);
        this.dateFormat = dateFormat;
        this.allowEmpty = allowEmpty;
        this.exactDateLength = -1;
    }

    /**
     * Create a new CustomDateEditor instance, using the given DateFormat
     * for parsing and rendering.
     * <p>The "allowEmpty" parameter states if an empty String should
     * be allowed for parsing, i.e. get interpreted as null value.
     * Otherwise, an IllegalArgumentException gets thrown in that case.
     * <p>The "exactDateLength" parameter states that IllegalArgumentException gets
     * thrown if the String does not exactly match the length specified. This is useful
     * because SimpleDateFormat does not enforce strict parsing of the year part,
     * not even with <code>setLenient(false)</code>. Without an "exactDateLength"
     * specified, the "01/01/05" would get parsed to "01/01/0005".
     *
     * @param dateFormat      DateFormat to use for parsing and rendering
     * @param allowEmpty      if empty strings should be allowed
     * @param exactDateLength the exact expected length of the date String
     */
    public PortalDateEditor(DateFormat dateFormat, boolean allowEmpty, int exactDateLength) {
        super(dateFormat, allowEmpty, exactDateLength);
        this.dateFormat = dateFormat;
        this.allowEmpty = allowEmpty;
        this.exactDateLength = exactDateLength;
    }


    /**
     * Parse the Date from the given text, using the specified DateFormat.
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (this.allowEmpty && !org.springframework.util.StringUtils.hasText(text)) {
            setValue(null);
        } else if (text != null && this.exactDateLength >= 0 && text.length() != this.exactDateLength) {
            setValue(null);
        } else {
            try {
                setValue(this.dateFormat.parse(text));
            } catch (ParseException ex) {
                setValue(null);
            }
        }
    }
}
