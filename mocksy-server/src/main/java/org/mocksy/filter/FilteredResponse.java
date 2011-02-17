package org.mocksy.filter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.mocksy.Response;

/*
 * Copyright 2011, PayPal
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

public class FilteredResponse implements Response {
	private Response baseResponse;
	private List<ResponseFilter> filters;
	private String newContentType;

	/**
	 * Creates a filtered response based on the given {@link Response} object.
	 * 
	 * @param response the base {@link Response} to filter
	 */
	public FilteredResponse(Response response) {
		this.baseResponse = response;
		this.filters = new LinkedList<ResponseFilter>();
	}
	
	/**
	 * Creates a filtered response based on the given {@link Response} object
	 * along with an initial set of {@link ResponseFilter}s.  The filters
	 * will be applied in the order they are provided, so the last one in the
	 * list will be the last one applied.
	 * 
	 * @param response the base {@link Response} to filter
	 * @param filters the initial list of {@link ResponseFilter}s to apply
	 */
	public FilteredResponse(Response response, ResponseFilter...filters) {
		this(response);
		for (ResponseFilter filter : filters) {
			this.addFilter( filter );
		}
	}
	
	@Override
    public String getContentType() {
		if (this.newContentType == null) {
			return this.baseResponse.getContentType();
		}
		return this.newContentType;
    }

	@Override
    public String getId() {
	    return "filtered-"+this.baseResponse.getId();
    }

	/**
	 * Adds a new filter to the end of the list.  Filters are applied 
	 * in the order they are added, so the last filter added will be the last
	 * filter applied.
	 * 
	 * @param filter the ResponseFilter to add to the end of the list.
	 */
	public void addFilter(ResponseFilter filter) {
		this.filters.add( filter );
		if (filter.getNewContentType() != null) {
			this.newContentType = filter.getNewContentType();
		}
	}
	
	@Override
	/**
	 * Returns the response contents as a byte[] after applying the filters.
	 * 
	 * @return response contents as a byte array
	 * @throws IOException if there's a problem producing the content
	 * @throws  \--FilterException if there's a problem filtering the response
	 */
    public byte[] toByteArray() throws IOException {
	    // TODO Auto-generated method stub
	    return null;
    }

}
