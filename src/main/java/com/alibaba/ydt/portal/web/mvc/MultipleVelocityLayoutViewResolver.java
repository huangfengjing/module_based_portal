package com.alibaba.ydt.portal.web.mvc;

import com.alibaba.ydt.portal.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.velocity.VelocityLayoutView;
import org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支持多个 layout 映射的视图解析器
 * </p>
 * Time: 12-7-30 下午1:46
 *
 * @author <a href="mailto:huangfengjing@gmail.com">Ivan</a>
 * @version 1.0
 */
public class MultipleVelocityLayoutViewResolver extends VelocityLayoutViewResolver {

    private static final Log logger = LogFactory.getLog(MultipleVelocityLayoutViewResolver.class);

    private Map<String, String> mappings = new HashMap<String, String>();

    private String layoutKey;

    private String screenContentKey;

    /**
     * Requires VelocityLayoutView.
     *
     * @see org.springframework.web.servlet.view.velocity.VelocityLayoutView
     */
    protected Class<?> requiredViewClass() {
        return VelocityLayoutView.class;
    }

    /**
     * Set the context key used to specify an alternate layout to be used instead
     * of the default layout. Screen content templates can override the layout
     * template that they wish to be wrapped with by setting this value in the
     * template, for example:<br>
     * <code>#set( $layout = "MyLayout.vm" )</code>
     * <p>The default key is "layout", as illustrated above.
     *
     * @param layoutKey the name of the key you wish to use in your
     *                  screen content templates to override the layout template
     * @see org.springframework.web.servlet.view.velocity.VelocityLayoutView#setLayoutKey
     */
    public void setLayoutKey(final String layoutKey) {
        this.layoutKey = layoutKey;
    }

    /**
     * Set the name of the context key that will hold the content of
     * the screen within the layout template. This key must be present
     * in the layout template for the current screen to be rendered.
     * <p>Default is "screen_content": accessed in VTL as
     * <code>$screen_content</code>.
     *
     * @param screenContentKey the name of the screen content key to use
     * @see org.springframework.web.servlet.view.velocity.VelocityLayoutView#setScreenContentKey
     */
    public void setScreenContentKey(final String screenContentKey) {
        this.screenContentKey = screenContentKey;
    }

    protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Building view using multiple layout resolver. View name is " + viewName);
        }

        VelocityLayoutView view = (VelocityLayoutView) super.buildView(viewName);

        if (this.layoutKey != null) {
            view.setLayoutKey(this.layoutKey);
        }

        if (this.screenContentKey != null) {
            view.setScreenContentKey(this.screenContentKey);
        }

        if (!this.mappings.isEmpty()) {
            for (Map.Entry<String, String> entry : this.mappings.entrySet()) {

                // Correct wildcards so that we can use the matches method in the String object
                String mappingRegexp = StringUtils.replace(entry.getKey(), "*", ".*");

                // If the view matches the regexp mapping
                String tmp = viewName;
                if(tmp.startsWith("/")) {
                    tmp = tmp.substring(1);
                }
                if (tmp.matches(mappingRegexp)) {
                    if (logger.isDebugEnabled())
                        logger.debug("  Found matching view. Setting layout url to " + entry.getValue());
                    view.setLayoutUrl(entry.getValue());
                    return view;
                }
            }
        }
        return view;
    }

    public void setMappings(Map<String, String> mappings) {
        this.mappings = mappings;
    }
}
