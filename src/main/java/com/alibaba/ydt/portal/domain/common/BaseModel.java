/*
 * Copyright 2007-2008 Inc OF CCNU OF HUBEI.CHINA.PR.
 * 
 * Licensed under the Inc License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://inc.ccnu.edu.cn/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.ydt.portal.domain.common;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * Base model
 *
 * @author <a href="mailto:huangfengjing@gmail.com>Ivan</a>
 * @since on 2008-12-12
 */
@MappedSuperclass
public abstract class BaseModel implements Serializable {

    private static final long serialVersionUID = -7902612712563179668L;

    /**
     * id
     */
    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "increment_generator")
    @GenericGenerator(name = "increment_generator", strategy = "increment",
            parameters = {@Parameter(name = "unsaved-value", value = "0")})
    protected long dbId = 0;

    /**
     * version
     */
    @Basic
    protected int version;

    /**
     * create time
     */
    @Basic
    @Column(name = "CREATED_ON")
    protected Date createdOn = new Date();

    /**
     * last modify time
     */
    @Basic
    @Column(name = "LAST_MODIFIED_ON")
    protected Date lastModifiedOn = new Date();

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public Date getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues().toString();
    }
}
