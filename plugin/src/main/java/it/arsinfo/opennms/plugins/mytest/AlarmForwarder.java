package it.arsinfo.opennms.plugins.mytest;

import java.util.List;
import java.util.Objects;

import org.opennms.integration.api.v1.alarms.AlarmLifecycleListener;
import org.opennms.integration.api.v1.events.EventForwarder;
import org.opennms.integration.api.v1.model.Alarm;
import org.opennms.integration.api.v1.model.immutables.ImmutableEventParameter;
import org.opennms.integration.api.v1.model.immutables.ImmutableInMemoryEvent;
import it.arsinfo.opennms.plugins.mytest.model.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

public class AlarmForwarder implements AlarmLifecycleListener {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmForwarder.class);

    private static final String UEI_PREFIX = "uei.opennms.org/mytestpluginPlugin";
    private static final String SEND_EVENT_FAILED_UEI = UEI_PREFIX + "/sendEventFailed";
    private static final String SEND_EVENT_SUCCESSFUL_UEI = UEI_PREFIX + "/sendEventSuccessful";

    private final MetricRegistry metrics = new MetricRegistry();
    private final Meter eventsForwarded = metrics.meter("eventsForwarded");
    private final Meter eventsFailed = metrics.meter("eventsFailed");

    private final ApiClient apiClient;
    private final EventForwarder eventForwarder;

    public AlarmForwarder(ApiClient apiClient, EventForwarder eventForwarder) {
        this.apiClient = Objects.requireNonNull(apiClient);
        this.eventForwarder = Objects.requireNonNull(eventForwarder);
    }

    @Override
    public void handleNewOrUpdatedAlarm(Alarm alarm) {
        LOG.info("handleNewOrUpdatedAlarm: id:{}, AlarmType:{}, ReductionKey: {}", alarm.getId(), alarm.getType(), alarm.getReductionKey());
    }

    @Override
    public void handleAlarmSnapshot(List<Alarm> alarms) {
        alarms.forEach(alarm ->
                LOG.info("handleAlarmSnapshot: id:{}, AlarmType:{}, ReductionKey: {}", alarm.getId(), alarm.getType(), alarm.getReductionKey())
                );
        // pass
    }

    @Override
    public void handleDeletedAlarm(int alarmId, String reductionKey) {
        LOG.info("handleDeletedAlarm: id:{}, ReductionKey: {}", alarmId, reductionKey);
    }

    public static Alert toAlert(Alarm alarm) {
        Alert alert = new Alert();
        alert.setStatus(toStatus(alarm));
        alert.setDescription(alarm.getDescription());
        return alert;
    }

    private static Alert.Status toStatus(Alarm alarm) {
        if (alarm.isAcknowledged()) {
            return Alert.Status.ACKNOWLEDGED;
        }
        switch (alarm.getSeverity()) {
            case INDETERMINATE:
            case CLEARED:
            case NORMAL:
                return Alert.Status.OK;
            case WARNING:
            case MINOR:
                return Alert.Status.WARNING;
            case MAJOR:
            case CRITICAL:
            default:
                return Alert.Status.CRITICAL;
        }
    }

    public MetricRegistry getMetrics() {
        return metrics;
    }
}
