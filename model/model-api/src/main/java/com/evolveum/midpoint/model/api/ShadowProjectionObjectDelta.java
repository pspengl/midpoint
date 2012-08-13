/**
 * Copyright (c) 2011 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * Portions Copyrighted 2011 [name of copyright owner]
 */
package com.evolveum.midpoint.model.api;

import java.util.ArrayList;
import java.util.Collection;

import com.evolveum.midpoint.prism.Objectable;
import com.evolveum.midpoint.prism.PrismContext;
import com.evolveum.midpoint.prism.PrismObjectDefinition;
import com.evolveum.midpoint.prism.PrismPropertyDefinition;
import com.evolveum.midpoint.prism.PrismPropertyValue;
import com.evolveum.midpoint.prism.PropertyPath;
import com.evolveum.midpoint.prism.delta.ChangeType;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.prism.delta.PropertyDelta;
import com.evolveum.midpoint.prism.polystring.PolyString;

/**
 * @author semancik
 *
 */
public class ShadowProjectionObjectDelta<T extends Objectable> extends ObjectDelta<T> {

	private String resourceOid;
	private String intent;
	
	public ShadowProjectionObjectDelta(Class<T> objectTypeClass, ChangeType changeType) {
		super(objectTypeClass, changeType);
	}

	public String getResourceOid() {
		return resourceOid;
	}

	public void setResourceOid(String resourceOid) {
		this.resourceOid = resourceOid;
	}

	public String getIntent() {
		return intent;
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	@Override
	protected void checkIdentifierConsistence(boolean requireOid) {
		if (requireOid && getResourceOid() == null) {
    		throw new IllegalStateException("Null resource oid in delta "+this);
    	}
	}

	/**
     * Convenience method for quick creation of object deltas that replace a single object property. This is used quite often
     * to justify a separate method. 
     */
    public static <O extends Objectable, X> ShadowProjectionObjectDelta<O> createModificationReplaceProperty(Class<O> type, 
    		String resourceOid, String intent, PropertyPath propertyPath, PrismContext prismContext, X... propertyValues) {
    	ShadowProjectionObjectDelta<O> objectDelta = new ShadowProjectionObjectDelta<O>(type, ChangeType.MODIFY);
    	objectDelta.setResourceOid(resourceOid);
    	objectDelta.setIntent(intent);
    	fillInModificationReplaceProperty(type, objectDelta, propertyPath, prismContext, propertyValues);
    	return objectDelta;
    }

	@Override
	protected String debugName() {
		return "ShadowProjectionObjectDelta";
	}

	@Override
	protected String debugIdentifiers() {
		return "resourceOid="+getResourceOid()+", intent="+getIntent();
	}
	
}
