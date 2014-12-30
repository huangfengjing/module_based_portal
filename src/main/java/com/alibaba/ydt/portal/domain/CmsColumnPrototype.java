package com.alibaba.ydt.portal.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 列原型
 */
@Entity
@Table(name = "portal_cms_column_prototype")
public class CmsColumnPrototype extends BaseCmsPrototype {

    public static final String CONTENT_PLACEHOLDER = "${column_content}";
}
