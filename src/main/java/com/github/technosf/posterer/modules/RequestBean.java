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
package com.github.technosf.posterer.modules;

import static org.apache.commons.lang3.StringEscapeUtils.escapeXml11;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeXml;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a {@code Request} as a java bean.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public final class RequestBean implements Request
{
    private static final Logger LOG = LoggerFactory
            .getLogger(RequestBean.class);

    /*
     * {@code Request} fields
     */
    private String endpoint;

    private String payload;

    private String method;

    private String contentType;

    private boolean base64;

    private String httpUser;

    private String httpPassword;

    /*
     * Session and derived fields
     */
    //@Nullable
    private URI uri;

    private int timeout;


    /**
     * Default, blank, {@code RequestBean}
     */
    public RequestBean()
    {
        this.uri = null;
        this.endpoint = "";
        this.payload = "";
        this.method = "";
        this.contentType = "";
        this.httpUser = "";
        this.httpPassword = "";
    }


    /**
     * Instantiation of a {@code RequestBean} as a copy from a {@code Request}
     * implementation.
     * 
     * @param propertiesData
     *            the {@code Request} to copy
     */
    public RequestBean(Request propertiesData)
    {
        this(propertiesData.getEndpoint(),
                propertiesData.getPayload(),
                propertiesData.getMethod(),
                propertiesData.getContentType(),
                propertiesData.getBase64(),
                propertiesData.getHttpUser(),
                propertiesData.getHttpPassword());
    }


    /**
     * Instantiates a bean from component values.
     * 
     * @param endpoint
     * @param payload
     * @param method
     * @param contentType
     * @param base64
     */
    public RequestBean(String endpoint, String payload, String method,
            String contentType, Boolean base64, String httpUser,
            String httpPassword)
    {
        this.endpoint = endpoint;
        this.payload = payload;
        this.method = method;
        this.contentType = contentType;
        this.base64 = base64;
        this.httpUser = httpUser;
        this.httpPassword = httpPassword;
        this.uri = constructUri(endpoint);
    }


    /* -------------  Sessions and derived Getters ------------------ */

    /**
     * Returns the endpoint as an {@code URI}
     * 
     * @return the endpoint
     */
    //@Nullable
    public URI getUri()
    {
        return uri;
    }


    /**
     * Returns the timeout set for this {@code Request} in this session
     * 
     * @return the timeout
     */
    public int getTimeout()
    {
        return timeout;
    }


    /* -------------  Request Getters and Setters  ------------------ */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.modules.Request#getEndpoint()
     */
    @Override
    public String getEndpoint()
    {
        return endpoint;
    }


    /**
     * @param endpoint
     *            the endpoint to set
     */
    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
        this.uri = constructUri(endpoint);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.modules.Request#getPayload()
     */
    //@SuppressWarnings("null")
    @Override
    public String getPayload()
    {
        return unescapeXml(payload);
    }


    /**
     * @return
     */
    public String getPayloadRaw()
    {
        return payload;
    }


    /**
     * @param payload
     *            the request to set
     */
    //@SuppressWarnings("null")
    public void setPayload(String payload)
    {
        this.payload = escapeXml11(payload);
    }


    /**
     * @param method
     *            the method to set
     */
    public void setMethod(String method)
    {
        this.method = method;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.modules.Request#getMethod()
     */
    @Override
    public String getMethod()
    {
        return method;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.modules.Request#getContentType()
     */
    @Override
    public String getContentType()
    {
        return contentType;
    }


    /**
     * @param contentType
     *            the contentType to set
     */
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.modules.Request#getBase64()
     */
    @Override
    public Boolean getBase64()
    {
        return base64;
    }


    /**
     * @param base64
     *            the base64 to set
     */
    public void setBase64(boolean base64)
    {
        this.base64 = base64;
    }


    /**
     * @return the httpPassword
     */
    @Override
    public String getHttpPassword()
    {
        return httpPassword;
    }


    /**
     * @param httpPassword
     *            the httpPassword to set
     */
    public void setHttpPassword(String httpPassword)
    {
        this.httpPassword = httpPassword;
    }


    /**
     * @return the httpUser
     */
    @Override
    public String getHttpUser()
    {
        return httpUser;
    }


    /**
     * @param httpUser
     *            the httpUser to set
     */
    public void setHttpUser(String httpUser)
    {
        this.httpUser = httpUser;
    }


    /* ------------------  Object functions  ------------------------ */

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return hashCode(this);
    }


    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj != null)
        {
            return Request.class.isInstance(obj)
                    && hashCode() == hashCode((Request) obj);
        }
        return false;
    }


    /* ----------------  Helpers  ---------------------- */

    /**
     * Returns a copy of the current bean.
     * 
     * @return a copy of the bean
     */
    public RequestBean copy()
    {
        return new RequestBean(this);
    }


    /**
     * Test for {@code Request} actionability.
     * 
     * @return True if {@code Request} can sent via HTTP
     */
    public boolean isActionable()
    {
        return isActionable(this);
    }


    /**
     * Test for {@code Request} actionability.
     * 
     * @return True if {@code Request} can sent via HTTP
     */
    public static boolean isActionable(final Request request)
    {
        if (request == null)
        {
            return false;
        }

        return isNotBlank(request.getEndpoint())
                && isNotBlank(request.getMethod())
                && isNotBlank(request.getContentType());
    }


    /**
     * Create a formatted {@code String} object for the {@code Request}
     * 
     * @param format
     *            The format to apply to the {@code Request} components
     * @return The {@code Request} as a {@code String}
     */
    //@SuppressWarnings("null")
    public static String toString(final String format,
            final Request request)
    {
        if (format == null || request == null)
        {
            return "";
        }

        return String.format(format,
                request.getEndpoint(),
                request.getPayload(),
                request.getMethod(),
                request.getContentType(),
                request.getBase64(),
                request.getHttpUser(),
                request.getHttpPassword());
    }


    /**
     * Create a hashcode for the {@code Request}
     * 
     * @param request
     * @return
     */
    private static int hashCode(final Request request)
    {
        if (request == null)
        {
            return 0;
        }

        return Objects.hash(
                Objects.toString(request.getEndpoint()),
                Objects.toString(request.getPayload()),
                Objects.toString(request.getMethod()),
                Objects.toString(request.getContentType()),
                Objects.toString(request.getBase64()),
                Objects.toString(request.getHttpUser()),
                Objects.toString(request.getHttpPassword()));
    }


    /**
     * Returns the given endpoint as an {@code URI} object.
     * 
     * @param endpoint
     *            the endpoint
     * @return the {@code URI} of the endpoint
     */
    //@Nullable
    private static URI constructUri(final String endpoint)
    {
        URI uri = null;

        try
        {
            uri = new URI(endpoint);
        }
        catch (URISyntaxException | NullPointerException e)
        {
            LOG.debug("Bad endpoint: {}", endpoint);
        }

        return uri;
    }


    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    public final String toString()
    {
        return String.format("%1$s\n%2$s\n%3$s\n%4$s\n%5$s\n%6$s\n%7$s",
                hashCode(this), getEndpoint(),
                //request.getPayload(),
                getMethod(),
                getContentType(),
                getBase64(),
                getHttpUser(),
                getHttpPassword());
    }

}