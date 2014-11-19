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

import java.io.Serializable;
import java.util.Date;


/**
 * Base model
 *
 * @author <a href="mailto:huangfengjing@gmail.com>Ivan</a>
 * @since on 2008-12-12
 */
public abstract class BaseModel implements Serializable {

    private static final long serialVersionUID = -7902612712563179668L;

    /**
     * id
     */
    protected long dbId = 0;

    /**
     * version
     */
    protected int version;

    /**
     * create time
     */
    protected Date createdOn = new Date();

    /**
     * last modify time
     */
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
}
