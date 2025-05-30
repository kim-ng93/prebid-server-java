package org.prebid.server.health;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prebid.server.execution.timeout.TimeoutFactory;
import org.prebid.server.geolocation.GeoLocationService;
import org.prebid.server.geolocation.model.GeoInfo;
import org.prebid.server.health.model.StatusResponse;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GeoLocationHealthCheckerTest {

    private static final String NAME = "geolocation";

    @Mock
    private Vertx vertx;

    @Mock
    private GeoLocationService geoLocationService;
    private Clock clock;

    private GeoLocationHealthChecker geoLocationHealthChecker;

    @BeforeEach
    public void setUp() {
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        final TimeoutFactory timeoutFactory = new TimeoutFactory(clock);
        geoLocationHealthChecker = new GeoLocationHealthChecker(vertx, 1L, geoLocationService, timeoutFactory, clock);
    }

    @Test
    public void getCheckNameShouldReturnExpectedResult() {
        assertThat(geoLocationHealthChecker.name()).isEqualTo(NAME);
    }

    @Test
    public void getLastStatusShouldReturnNullStatusIfCheckWasNotInitialized() {
        assertThat(geoLocationHealthChecker.status()).isNull();
    }

    @Test
    public void getLastStatusShouldReturnStatusUpAndLastUpdatedNow() {
        // given
        given(geoLocationService.lookup(any(), any())).willReturn(
                Future.succeededFuture(GeoInfo.builder().vendor("vendor").country("USA").build()));

        // when
        geoLocationHealthChecker.updateStatus();

        // then
        final StatusResponse status = geoLocationHealthChecker.status();
        assertThat(status.getStatus()).isEqualTo("UP");
        assertThat(status.getLastUpdated()).isEqualTo(ZonedDateTime.now(clock));
    }

    @Test
    public void getLastStatusShouldReturnStatusDownAndLastUpdatedNow() {
        // given
        given(geoLocationService.lookup(any(), any())).willReturn(Future.failedFuture("failed"));

        // when
        geoLocationHealthChecker.updateStatus();

        // then
        final StatusResponse lastStatus = geoLocationHealthChecker.status();
        assertThat(lastStatus.getStatus()).isEqualTo("DOWN");
        assertThat(lastStatus.getLastUpdated()).isEqualTo(ZonedDateTime.now(clock));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void initializeShouldMakeOneInitialRequestAndTwoScheduledRequests() {
        // given
        given(vertx.setPeriodic(anyLong(), any())).willReturn(1L);
        given(geoLocationService.lookup(any(), any()))
                .willReturn(
                        Future.succeededFuture(GeoInfo.builder().vendor("vendor").country("USA").build()),
                        Future.failedFuture("failed"),
                        Future.succeededFuture(GeoInfo.builder().vendor("vendor").country("USA").build()));

        // when
        geoLocationHealthChecker.initialize();

        // then
        final ArgumentCaptor<Handler<Long>> handlerArgumentCaptor = ArgumentCaptor.forClass(Handler.class);
        verify(vertx).setPeriodic(anyLong(), handlerArgumentCaptor.capture());

        final Handler<Long> handler = handlerArgumentCaptor.getValue();
        assertThat(geoLocationHealthChecker.status().getStatus()).isEqualTo("UP");

        handler.handle(1L);
        assertThat(geoLocationHealthChecker.status().getStatus()).isEqualTo("DOWN");

        handler.handle(1L);
        assertThat(geoLocationHealthChecker.status().getStatus()).isEqualTo("UP");

        verify(geoLocationService, times(3)).lookup(any(), any());
    }
}
