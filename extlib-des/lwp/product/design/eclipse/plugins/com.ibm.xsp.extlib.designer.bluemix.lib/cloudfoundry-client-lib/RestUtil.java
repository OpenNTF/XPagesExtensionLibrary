package org.cloudfoundry.client.lib.util;

import static org.apache.http.conn.ssl.SSLSocketFactory.STRICT_HOSTNAME_VERIFIER;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.oauth2.OauthClient;
import org.cloudfoundry.client.lib.rest.CloudControllerClientImpl;
import org.cloudfoundry.client.lib.rest.CloudControllerResponseErrorHandler;
import org.cloudfoundry.client.lib.rest.LoggingRestTemplate;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;


/**
 * Some helper utilities for creating classes used for the REST support.
 *
 * @author Thomas Risberg
 */
public class RestUtil {

	public RestTemplate createRestTemplate(HttpProxyConfiguration httpProxyConfiguration, boolean trustSelfSignedCerts) {
		RestTemplate restTemplate = new LoggingRestTemplate();
		restTemplate.setRequestFactory(createRequestFactory(httpProxyConfiguration, trustSelfSignedCerts));
		restTemplate.setErrorHandler(new CloudControllerResponseErrorHandler());
		restTemplate.setMessageConverters(getHttpMessageConverters());

		return restTemplate;
	}

	public ClientHttpRequestFactory createRequestFactory(HttpProxyConfiguration httpProxyConfiguration, boolean trustSelfSignedCerts) {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		HttpClient httpClient = requestFactory.getHttpClient();

		if (trustSelfSignedCerts) {
			registerSslSocketFactory(httpClient);
		} else {
		    // We don't use self signed certs in Designer so this code will always be executed
		    // TLSv1.0 has been disabled on Bluemix/Datapower 
		    // We need to use TLSv1.2 or TLSv1.1 instead
		    registerSslSocketFactoryTLS(httpClient);
		}
		    
		if (httpProxyConfiguration != null) {
			HttpHost proxy = new HttpHost(httpProxyConfiguration.getProxyHost(), httpProxyConfiguration.getProxyPort());
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		return requestFactory;
	}

	public OauthClient createOauthClient(URL authorizationUrl, HttpProxyConfiguration httpProxyConfiguration, boolean trustSelfSignedCerts) {
		return new OauthClient(authorizationUrl, createRestTemplate(httpProxyConfiguration, trustSelfSignedCerts));
	}

	/*
	 * Tries to register a TLSv1.2 or TLSv1.1 socket factory
	 */
	private void registerSslSocketFactoryTLS(HttpClient httpClient) {
        try {
	        SSLContext sslcontext;
	        try {
                // Try TLSv1.2 first
	            sslcontext = SSLContext.getInstance("TLSv1.2");
	        } catch (NoSuchAlgorithmException e) {
	            // May be running in an older JRE - fallback to TLSv1.1
                sslcontext = SSLContext.getInstance("TLSv1.1");	            
	        }
	        sslcontext.init(null, null, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext);
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
	    } catch (Exception e) {
	        // Ignore Exception - will fallback to original behaviour TLSv1.0
	    }
	}
	
	private void registerSslSocketFactory(HttpClient httpClient)  {
		try {
			SSLSocketFactory socketFactory = new SSLSocketFactory(new TrustSelfSignedStrategy(), STRICT_HOSTNAME_VERIFIER);
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
		} catch (GeneralSecurityException gse) {
			throw new RuntimeException("An error occurred setting up the SSLSocketFactory", gse);
		}
	}

	private List<HttpMessageConverter<?>> getHttpMessageConverters() {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new ByteArrayHttpMessageConverter());
		messageConverters.add(new StringHttpMessageConverter());
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new UploadApplicationPayloadHttpMessageConverter());
		messageConverters.add(getFormHttpMessageConverter());
		messageConverters.add(new MappingJacksonHttpMessageConverter());
		return messageConverters;
	}

	private FormHttpMessageConverter getFormHttpMessageConverter() {
		FormHttpMessageConverter formPartsMessageConverter = new CloudControllerClientImpl.CloudFoundryFormHttpMessageConverter();
		formPartsMessageConverter.setPartConverters(getFormPartsMessageConverters());
		return formPartsMessageConverter;
	}

	private List<HttpMessageConverter<?>> getFormPartsMessageConverters() {
		List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
		stringConverter.setSupportedMediaTypes(Collections.singletonList(JsonUtil.JSON_MEDIA_TYPE));
		stringConverter.setWriteAcceptCharset(false);
		partConverters.add(stringConverter);
		partConverters.add(new ResourceHttpMessageConverter());
		partConverters.add(new UploadApplicationPayloadHttpMessageConverter());
		return partConverters;
	}
}
