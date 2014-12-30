package com.alibaba.ydt.portal.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 布局原型
 */
@Entity
@Table(name = "portal_cms_layout_prototype")
public class CmsLayoutPrototype extends BaseCmsPrototype {

    public static final String CONTENT_PLACEHOLDER = "${layout_content}";
}
