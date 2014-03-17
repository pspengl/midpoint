/*
 * Copyright (c) 2010-2013 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.repo.sql.data.common.any;

import com.evolveum.midpoint.repo.sql.data.common.id.RAClobId;
import com.evolveum.midpoint.repo.sql.util.RUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;

/**
 * @author lazyman
 */
@Entity
@IdClass(RAClobId.class)
@Table(name = "m_a_clob")
public class RAClob implements RAExtensionValue {

    //owner entity
    private RAssignmentExtension anyContainer;
    private String ownerOid;
    private Short ownerId;

    private boolean dynamic;
    private String name;
    private String type;
    private RValueType valueType;
    private String checksum;

    public RAClob() {
    }

    public RAClob(String value) {
        setValue(value);
    }

    @ForeignKey(name = "fk_a_clob")
    @MapsId("owner")
    @ManyToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumns({
            @PrimaryKeyJoinColumn(name = "anyContainer_owner_owner_oid", referencedColumnName = "ownerOid"),
            @PrimaryKeyJoinColumn(name = "anyContainer_owner_id", referencedColumnName = "ownerId")
    })
    public RAssignmentExtension getAnyContainer() {
        return anyContainer;
    }

    @Id
    @Column(name = "anyContainer_owner_owner_oid", length = RUtil.COLUMN_LENGTH_OID)
    public String getOwnerOid() {
        if (ownerOid == null && anyContainer != null) {
            ownerOid = anyContainer.getOwnerOid();
        }
        return ownerOid;
    }

    @Id
    @Column(name = "anyContainer_owner_id")
    public Short getOwnerId() {
        if (ownerId == null && anyContainer != null) {
            ownerId = anyContainer.getOwnerId();
        }
        return ownerId;
    }

    /**
     * This method is used for content comparing when querying database (we don't want to compare clob values).
     *
     * @return md5 hash of {@link com.evolveum.midpoint.repo.sql.data.common.any.RAnyClob#getValue()}
     */
    @Id
    @Column(length = 32, name = "checksum")
    public String getChecksum() {
        return checksum;
    }

    @Id
    @Column(name = "eName", length = RUtil.COLUMN_LENGTH_QNAME)
    public String getName() {
        return name;
    }

    @Id
    @Column(name = "eType", length = RUtil.COLUMN_LENGTH_QNAME)
    public String getType() {
        return type;
    }

    @Enumerated(EnumType.ORDINAL)
    public RValueType getValueType() {
        return valueType;
    }

    /**
     * @return true if this property has dynamic definition
     */
    @Column(name = "dynamicDef")
    public boolean isDynamic() {
        return dynamic;
    }

    @Transient
    public String getValue() {
        return null;
    }

    public void setChecksum(String checksum) {
        //checksum is always computed from value, this setter is only for hibernate satisfaction
        this.checksum = checksum;
    }

    public void setValue(String value) {
        checksum = StringUtils.isNotEmpty(value) ? DigestUtils.md5Hex(value) : "";
    }

    public void setValueType(RValueType valueType) {
        this.valueType = valueType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public void setAnyContainer(RAssignmentExtension anyContainer) {
        this.anyContainer = anyContainer;
    }

    public void setOwnerOid(String ownerOid) {
        this.ownerOid = ownerOid;
    }

    public void setOwnerId(Short ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RAClob that = (RAClob) o;

        if (dynamic != that.dynamic) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (valueType != that.valueType) return false;
        if (checksum != null ? !checksum.equals(that.checksum) : that.checksum != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (dynamic ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
        result = 31 * result + (checksum != null ? checksum.hashCode() : 0);
        return result;
    }
}
