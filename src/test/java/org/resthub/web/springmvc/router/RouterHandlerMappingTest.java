package org.resthub.web.springmvc.router;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import static org.fest.assertions.api.Assertions.*;
import org.resthub.web.springmvc.router.Router.Route;
import org.resthub.web.springmvc.router.support.RouterHandler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath*:routerTestContext.xml" })
public class RouterHandlerMappingTest extends AbstractTestNGSpringContextTests {

	private String LOCATION = "/routerTestContext.xml";
	
	private XmlWebApplicationContext wac;
	
	@Inject
	@Named("myTestController")
	private MyTestController testController;
	
	private String handlerName = "myTestController";
        
        private String sampleHost = "samplehost.org";
	
	private HandlerMapping hm;
	
	/**
	 * Setup a MockServletContext configured by routerTestContext.xml
	 */
	@BeforeClass
	public void setUp() {

		MockServletContext sc = new MockServletContext("");
		this.wac = new XmlWebApplicationContext();
		this.wac.setServletContext(sc);
		this.wac.setConfigLocations(new String[] {LOCATION});
		this.wac.refresh();
		
		this.hm = (HandlerMapping) this.wac.getBean("handlerMapping");
	}
	
	/**
	 * Test route:
	 * GET     /simpleaction    myTestController.simpleAction
	 * @throws Exception
	 */
	@Test
	public void testSimpleRoute() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/simpleaction");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".simpleAction");
	}
        
        /**
	 * Test route:
	 * GET     /additionalroute       myTestController.additionalRouteFile
	 * @throws Exception
	 */
	@Test
	public void additionalRouteFile() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/additionalroute");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".additionalRouteFile");
	}
        
        /**
	 * Test route:
	 * GET     /caseinsensitive            MyTestCONTROLLER.caseInsensitive
	 * @throws Exception
	 */
	@Test
	public void caseInsensitiveRoute() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/caseinsensitive");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualToIgnoringCase(this.handlerName+".caseInsensitive");
	}
        
        /**
	 * Test routes:
	 * GET     /wildcard-a       myTestController.wildcardA
         * GET     /wildcard-b       myTestController.wildcardB
	 * @throws Exception
	 */
	@Test
	public void wildcardRouteFiles() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/wildcard-a");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".wildcardA");
                
		request = new MockHttpServletRequest("GET", "/wildcard-b");
		request.addHeader("host", sampleHost);
		chain = this.hm.getHandler(request);
		
		handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".wildcardB");
	}        
        
	/**
	 * Test route with HTTP method overriding (HEAD -> GET):
	 * GET     /simpleaction    myTestController.simpleAction
	 * @throws Exception
	 */
	@Test
	public void testHeadMethod() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("HEAD", "/simpleaction");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".simpleAction");
	}
	
	/**
	 * Test route:
	 * GET      /param     myTestController.paramAction(param:'default')
	 * @throws Exception
	 */
	@Test
	public void testDefaultParamAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/param");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".paramAction");
	}
	
	/**
	 * Test route:
	 * GET     /param/{param}      myTestController.paramAction
	 * @throws Exception
	 */
	@Test
	public void testParamAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/param/myparam");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".paramAction");
	}	

	/**
	 * Test route:
	 * GET     /http       myTestController.httpAction(type:'GET')
	 * @throws Exception
	 */
	@Test
	public void testGETHTTPAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/http");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".httpAction");
	}

	/**
	 * Test route:
	 * PUT     /http       myTestController.httpAction(type:'PUT')
	 * @throws Exception
	 */
	@Test
	public void testPUTHTTPAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/http");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".httpAction");
	}
	
	
	/**
	 * Test route:
	 * POST     /http       myTestController.httpAction(type:'POST')
	 * @throws Exception
	 */
	@Test
	public void testPOSTHTTPAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/http");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
		assertThat(route.action).isEqualTo(this.handlerName+".httpAction");
	}

	
	/**
	 * Test route:
	 * DELETE     /http       myTestController.httpAction(type:'DELETE')
	 * @throws Exception
	 */
	@Test
	public void testDELETEHTTPAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/http");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
		assertThat(route.action).isEqualTo(this.handlerName+".httpAction");
	}

        /**
	 * Test route:
	 * PUT     /http       myTestController.httpAction(type:'PUT')
         * with a GET request and a "x-http-method-override" header
	 * @throws Exception
	 */
	@Test
	public void testOverrideHeaderHTTPAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/http");
		request.addHeader("host", sampleHost);
                request.addHeader("x-http-method-override","PUT");
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
		assertThat(route.action).isEqualTo(this.handlerName+".httpAction");
	}
        
        /**
	 * Test route:
	 * PUT     /http       myTestController.httpAction(type:'PUT')
         * with a GET request and a "x-http-method-override" argument in queryString
	 * @throws Exception
	 */
	@Test
	public void testOverrideQueryStringHTTPAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/http");
                request.setQueryString("x-http-method-override=PUT");
                request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
		assertThat(route.action).isEqualTo(this.handlerName+".httpAction");
	}        
        
	
	/**
	 * Test route:
	 * GET     /regex/{<[0-9]+>number}          myTestController.regexNumberAction
	 * @throws Exception
	 */
	@Test
	public void testregexNumberAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/regex/42");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".regexNumberAction");
	}
	
	/**
	 * Test route:
	 * GET     /regex/{<[a-z]+>string}      myTestController.regexStringAction
	 * @throws Exception
	 */
	@Test
	public void testregexStringAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/regex/marvin");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".regexStringAction");
	}
	
	
	/**
	 * Test route:
	 * GET     /host          myTestController.hostAction
	 * @throws Exception
	 */
	@Test
	public void testhostAction() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/host");
		request.addHeader("host", sampleHost);
		HandlerExecutionChain chain = this.hm.getHandler(request);
		
		RouterHandler handler = (RouterHandler)chain.getHandler();
		assertThat(handler).isNotNull();
		
		Route route = handler.getRoute();
		assertThat(route).isNotNull();
                assertThat(route.action).isEqualTo(this.handlerName+".hostAction");
	}
    
    //--------------------------------
    // querystring params tests
    
    /**
     * Test route:
     * GET     /qsparampresence [qsParamA]        myTestController.qsParamPresence
     * @throws Exception
     */
    @Test
    public void qsParamPresence() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/qsparampresence");
        request.addHeader("host", sampleHost);
        
        // No querystring at all
        HandlerExecutionChain chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
        
        // Wrong parameter name
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("qsParamWrongName", "abc");
        params.add(param);
        String querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);
        
        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
    }
    
    /**
     * Test route:
     * GET     /qsparamnegatepresence [!qsParamA]                   myTestController.qsParamNegatePresence
     * @throws Exception
     */
    @Test
    public void qsParamNegatePresence() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/qsparamnegatepresence");
        request.addHeader("host", sampleHost);
        
        // param not there, ok
        HandlerExecutionChain chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        RouterHandler handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        Route route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamNegatePresence");
        
        // param empty value
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("qsParamA", "");
        params.add(param);
        String querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
        
        // param random value
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "abc");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
    }
    
    /**
     * Test route:
     * GET     /qsparampresence [qsParamA]        myTestController.qsParamPresence
     * @throws Exception
     */
    @Test
    public void qsParamRequired() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/qsparampresence");
        request.addHeader("host", sampleHost);
        
        // Random value
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("qsParamA", "abc");
        params.add(param);
        String querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        HandlerExecutionChain chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        RouterHandler handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        Route route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamPresence");
        
        // Empty value should be OK too
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamPresence");
    }
    
    /**
     * Test route:
     * GET     /qsparamemptyvaluerequired [qsParamA=]      myTestController.qsParamEmptyValueRequired
     * @throws Exception
     */
    @Test
    public void qsParamValueMustBeEmpty() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/qsparamemptyvaluerequired");
        request.addHeader("host", sampleHost);
        
        // empty value shoudl be accepted
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("qsParamA", "");
        params.add(param);
        String querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        HandlerExecutionChain chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        RouterHandler handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        Route route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamEmptyValueRequired");
        
        
        // Not empty value should be accepted
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "abc");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
    }    
    
    /**
     * Test route:
     * GET     /qsparamspecificvaluerequired [qsParamA=abc]           myTestController.qsParamSpecificValueRequired
     * @throws Exception
     */
    @Test
    public void qsParamSpecificValueRequired() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/qsparamspecificvaluerequired");
        request.addHeader("host", sampleHost);
        
        // expected value
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("qsParamA", "abc");
        params.add(param);
        String querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        HandlerExecutionChain chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        RouterHandler handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        Route route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamSpecificValueRequired");
        
        // wrong value
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "wrongvalue");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
        
        // empty value
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
    }    
    
    /**
     * Test route:
     * POST    /qsparamspecificvaluerequiredpost [qsParamA=abc]    myTestController.qsParamSpecificValueRequiredPost
     * @throws Exception
     */
    @Test
    public void qsParamSpecificValueRequiredPost() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/qsparamspecificvaluerequiredpost");
        request.addHeader("host", sampleHost);
        
        // expected value
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("qsParamA", "abc");
        params.add(param);
        String querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        HandlerExecutionChain chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        RouterHandler handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        Route route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamSpecificValueRequiredPost");
        
        // wrong value
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "wrongvalue");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
        
        // empty value
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
    }    
    
    /**
     * Test route:
     * GET     /qsparamnegatespecificvalue [qsParamA!=abc]           myTestController.qsParamNegateSpecificValue
     * @throws Exception
     */
    @Test
    public void qsParamNegateSpecificValue() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/qsparamnegatespecificvalue");
        request.addHeader("host", sampleHost);
        
        // prohibited value
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("qsParamA", "abc");
        params.add(param);
        String querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        HandlerExecutionChain chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
        
        // empty value : ok
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        RouterHandler handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        Route route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamNegateSpecificValue");
        
        // random value : ok
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "someRandomValue");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);

        chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamNegateSpecificValue");
        
        // no param at all : should fail! 
        // "qsParamA!=abc" mean qsParama *must exists* but not have a value of "abc"
        request.setQueryString("");

        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
    }
    
    /**
     * Test route:
     * GET     /qsparamtwoparamsrequired [qsParamA=abc,qsParamB]     myTestController.qsParamTwoParamsRequired
     * @throws Exception
     */
    @Test
    public void qsParamTwoParamsRequired() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/qsparamtwoparamsrequired");
        request.addHeader("host", sampleHost);

        // no param at all
        HandlerExecutionChain chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
        
        // one required param only
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("qsParamA", "abc");
        params.add(param);
        String querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);
        
        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
        
        // all required params but invalid values
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "wrongValue");
        params.add(param);
        param = new BasicNameValuePair("qsParamB", "");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);
        
        chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
        
        // all required params with valid values : ok
        params = new ArrayList<NameValuePair>();
        param = new BasicNameValuePair("qsParamA", "abc");
        params.add(param);
        param = new BasicNameValuePair("qsParamB", "");
        params.add(param);
        querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);
        
        chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        RouterHandler handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        Route route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamTwoParamsRequired");
    }
        
    /**
     * Test route:
     * GET     /qsparamencodedvalueandrandomspaces [ qsParamA=%20   qsParamB=a+b   qsParamC=%C3%A9t%C3%A9  ] myTestController.qsParamEncodedValueAndRandomSpaces
     * @throws Exception
     */
    @Test
    public void qsParamEncodedValueAndRandomSpaces() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/qsparamencodedvalueandrandomspaces");
        request.addHeader("host", sampleHost);
        
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("qsParamA", " ");
        params.add(param);
        param = new BasicNameValuePair("qsParamB", "a b");
        params.add(param);
        param = new BasicNameValuePair("qsParamC", "été");
        params.add(param);
        String querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);
        
        HandlerExecutionChain chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        RouterHandler handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        Route route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamEncodedValueAndRandomSpaces");
    }
    
    /**
     * Test route:
     * GET     /qsparamplaynicewithotherroutingfeatures/name/{<[a-z]+>myName} [qsParamA=abc] bindTestController.qsParamPlayNiceWithOtherRoutingFeatures(myStaticArg:'So Long, and Thanks for All the Fish')
     * @throws Exception
     */
    @Test
    public void qsParamPlayNiceWithOtherRoutingFeatures() throws Exception {
        
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/qsparamplaynicewithotherroutingfeatures/name/stromgol");
        request.addHeader("host", sampleHost);
        
        // no qsParam
        HandlerExecutionChain chain = this.hm.getHandler(request);
        assertThat(chain).isNull();
        
        // with qsParam
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("qsParamA", "abc");
        params.add(param);
        String querystring = URLEncodedUtils.format(params, "utf-8");
        request.setQueryString(querystring);
        
        chain = this.hm.getHandler(request);
        assertThat(chain).isNotNull();
        
        RouterHandler handler = (RouterHandler)chain.getHandler();
        assertThat(handler).isNotNull();
        
        Route route = handler.getRoute();
        assertThat(route).isNotNull();
        assertThat(route.action).isEqualTo(this.handlerName+".qsParamPlayNiceWithOtherRoutingFeatures");
    }

}
