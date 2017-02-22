package com.spid.android.sdk;

import com.spid.android.sdk.accesstoken.SPiDAccessToken;
import com.spid.android.sdk.request.SPiDRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SPiDClientTest {

    private SPiDRequest spidRequest;
    private SPiDAccessToken validAccessToken;
    private SPiDAccessToken invalidAccessToken;

    @Before
    public void setUp() throws Exception {
        spidRequest = mock(SPiDRequest.class);
        validAccessToken = mock(SPiDAccessToken.class);
        invalidAccessToken = mock(SPiDAccessToken.class);

        Date currentMoment = new Date();
        Date inOneHour = new Date(currentMoment.getTime() + TimeUnit.HOURS.toMillis(1));
        when(validAccessToken.getExpiresAt()).thenReturn(inOneHour);

        Date oneHourAgo = new Date(currentMoment.getTime() - TimeUnit.HOURS.toMillis(1));
        when(invalidAccessToken.getExpiresAt()).thenReturn(oneHourAgo);
    }

    @After
    public void after() throws Exception {
        SPiDClient.getInstance().runWaitingRequests();
    }

    @Test
    public void isAuthorized_withValidToken_returnsTrue() throws Exception {
        SPiDClient sPiDClient = mock(SPiDClient.class);

        doNothing().when(sPiDClient).broadcastUserId(Matchers.anyString());
        doCallRealMethod().when(sPiDClient).setAccessToken(Matchers.any(SPiDAccessToken.class));
        doCallRealMethod().when(sPiDClient).isAuthorized();

        sPiDClient.setAccessToken(validAccessToken);
        assertTrue(sPiDClient.isAuthorized());
    }

    @Test
    public void isAuthorized_withExpiredToken_returnsFalse() throws Exception {
        SPiDClient sPiDClient = mock(SPiDClient.class);

        doNothing().when(sPiDClient).broadcastUserId(Matchers.anyString());
        doCallRealMethod().when(sPiDClient).setAccessToken(Matchers.any(SPiDAccessToken.class));
        doCallRealMethod().when(sPiDClient).isAuthorized();

        sPiDClient.setAccessToken(invalidAccessToken);

        assertFalse(sPiDClient.isAuthorized());
    }

    @Test
    public void getWaitingRequestsQueueSize_addingRequest_incrementsQueueByOne() throws Exception {
        SPiDClient sPiDClient = new SPiDClient();

        assertTrue(sPiDClient.getWaitingRequestsQueueSize() == 0);
        sPiDClient.addWaitingRequest(spidRequest);

        assertTrue(sPiDClient.getWaitingRequestsQueueSize() == 1);
    }

    @Test
    public void runWaitingRequests_called_clearsWaitingQueue() throws Exception {
        TestSPIDClass sPiDClient = new TestSPIDClass();
        sPiDClient.setAccessToken(validAccessToken);

        sPiDClient.addWaitingRequest(spidRequest);
        sPiDClient.addWaitingRequest(spidRequest);
        sPiDClient.addWaitingRequest(spidRequest);
        assertTrue(sPiDClient.getWaitingRequestsQueueSize() == 3);

        sPiDClient.runWaitingRequests();

        assertTrue(sPiDClient.getWaitingRequestsQueueSize() == 0);
    }

    class TestSPIDClass extends SPiDClient {
        @Override
        protected void broadcastUserId(String userId) { }
    }
}