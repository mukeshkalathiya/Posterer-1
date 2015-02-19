/*
 * Copyright 2014 technosf [https://github.com/technosf]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.technosf.posterer.models.base;

import java.util.concurrent.TimeUnit;

import javafx.concurrent.Task;

import com.github.technosf.posterer.models.RequestBean;
import com.github.technosf.posterer.models.ResponseModel;

/**
 * Basic implementation of {@code ResponseModel} common methods as a JavaFX
 * {@code Task}
 * <p>
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 * @param <T>
 *            the response type used by the implementation
 */
public abstract class AbstractResponseModelTask<T>
        extends Task<T>
        implements ResponseModel
{

    protected final int requestId;
    protected final RequestBean requestBean;

    protected String body;
    protected String headers;
    private long elapsedTime;
    protected T response;
    private boolean completed;


    /**
     * @param requestId
     * @param uri
     * @param method
     * @param contentType
     * @param timeout
     * @param encode
     * @param user
     * @param password
     */
    protected AbstractResponseModelTask(final int requestId,
            final RequestBean requestBean)
    {
        this.requestId = requestId;
        this.requestBean = requestBean;
    }


    /*
     * ------------------------------------------------------------------------
     */

    /**
     * 
     */
    protected abstract void prepareClient();


    /**
     * @return
     * @throws Exception
     */
    protected abstract T getReponse() throws Exception;


    /**
     * 
     */
    protected abstract void processResponse();


    /**
     * 
     */
    protected abstract void closeClient();


    /*
     * ------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     * 
     * @see javafx.concurrent.Task#call()
     */
    @Override
    protected final T call() throws Exception
    {
        T response = null;

        prepareClient();

        long startTime = System.nanoTime();

        try
        {
            response = getReponse();
        }
        finally
        {

            elapsedTime =
                    TimeUnit.NANOSECONDS
                            .toMillis(System.nanoTime() - startTime);
        }

        return response;
    }


    /*
     * ------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getReferenceId()
     */
    @Override
    public final int getReferenceId()
    {
        return requestId;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getElaspedTimeMili()
     */
    @Override
    public final long getElaspedTimeMili()
    {
        return elapsedTime;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getResponse()
     */
    @Override
    public final String getResponse()
    {
        return java.util.Objects.toString(response);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.ResponseModel#getRequestBean()
     */
    @Override
    public final RequestBean getRequestBean()
    {
        return requestBean;
    }


    /*
     * ------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getHeaders()
     */
    @Override
    public final String getHeaders()
    {
        processResponse();
        return headers;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getBody()
     */
    @Override
    public String getBody()
    {
        processResponse();
        return body;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#isComplete()
     */
    @Override
    public final boolean isComplete()
    {
        processResponse();
        return completed;
    }

}
