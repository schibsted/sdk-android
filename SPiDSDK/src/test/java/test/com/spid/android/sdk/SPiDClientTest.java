package test.com.spid.android.sdk;

import com.spid.android.sdk.SPiDClient;
import com.spid.android.sdk.accesstoken.SPiDAccessToken;
import com.spid.android.sdk.request.SPiDRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SPiDClientTest {

    private SPiDAccessToken accessToken;
    private SPiDRequest spidRequest;


    @Before
    public void setUp() throws Exception {
        accessToken = mock(SPiDAccessToken.class);
        spidRequest = mock(SPiDRequest.class);
    }

    @After
    public void after() throws Exception {
        SPiDClient.getInstance().runWaitingRequests();
    }

    @Test
    public void isAuthorized_withValidToken_returnsTrue() throws Exception {
        Date currentMoment = new Date();
        Date oneHourFromNow = new Date(currentMoment.getTime() + TimeUnit.HOURS.toMillis(1));
        when(accessToken.getExpiresAt()).thenReturn(oneHourFromNow);

        SPiDClient.getInstance().setAccessToken(accessToken);

        assertTrue(SPiDClient.getInstance().isAuthorized());
    }

    @Test
    public void isAuthorized_withExpiredToken_returnsFalse() throws Exception {
        Date currentMoment = new Date();
        Date oneHourAgo = new Date(currentMoment.getTime() - TimeUnit.HOURS.toMillis(1));
        when(accessToken.getExpiresAt()).thenReturn(oneHourAgo);

        SPiDClient.getInstance().setAccessToken(accessToken);

        assertFalse(SPiDClient.getInstance().isAuthorized());
    }

    @Test
    public void getWaitingRequestsQueueSize_addingRequest_incrementsQueueByOne() throws Exception {
        assertTrue(SPiDClient.getInstance().getWaitingRequestsQueueSize() == 0);
        SPiDClient.getInstance().addWaitingRequest(spidRequest);

        assertTrue(SPiDClient.getInstance().getWaitingRequestsQueueSize() == 1);
    }

    @Test
    public void runWaitingRequests_called_clearsWaitingQueue() throws Exception {
        SPiDClient.getInstance().addWaitingRequest(spidRequest);
        SPiDClient.getInstance().addWaitingRequest(spidRequest);
        SPiDClient.getInstance().addWaitingRequest(spidRequest);
        assertTrue(SPiDClient.getInstance().getWaitingRequestsQueueSize() == 3);

        SPiDClient.getInstance().runWaitingRequests();

        assertTrue(SPiDClient.getInstance().getWaitingRequestsQueueSize() == 0);
    }
}