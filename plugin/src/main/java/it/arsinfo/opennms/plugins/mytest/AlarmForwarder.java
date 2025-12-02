package it.arsinfo.opennms.plugins.mytest;

import org.opennms.integration.api.v1.alarms.AlarmLifecycleListener;
import org.opennms.integration.api.v1.model.Alarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AlarmForwarder implements AlarmLifecycleListener {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmForwarder.class);



    public AlarmForwarder() {
    }

    @Override
    public void handleNewOrUpdatedAlarm(Alarm alarm) {
        LOG.info("handleNewOrUpdatedAlarm: id:{}, AlarmType:{}, ReductionKey: {}", alarm.getId(), alarm.getType(), alarm.getReductionKey());
        LOG.info("handleNewOrUpdatedAlarm: id:{}, isAck:{}", alarm.getId(), alarm.isAcknowledged());
        LOG.info("handleNewOrUpdatedAlarm: id:{}, MoI:{}", alarm.getId(), alarm.getManagedObjectInstance());
        LOG.info("handleNewOrUpdatedAlarm: id:{}, MoT:{}", alarm.getId(), alarm.getManagedObjectType());
    }

    @Override
    public void handleAlarmSnapshot(List<Alarm> alarms) {
        alarms.forEach(alarm -> {
                LOG.info("handleAlarmSnapshot: id:{}, AlarmType:{}, ReductionKey: {}", alarm.getId(), alarm.getType(), alarm.getReductionKey());
                LOG.info("handleAlarmSnapshot: id:{}, isAck:{}", alarm.getId(), alarm.isAcknowledged());
                LOG.info("handleAlarmSnapshot: id:{}, MoI:{}", alarm.getId(), alarm.getManagedObjectInstance());
                LOG.info("handleAlarmSnapshot: id:{}, MoT:{}", alarm.getId(), alarm.getManagedObjectType());
                }
                );
        // pass
    }

    @Override
    public void handleDeletedAlarm(int alarmId, String reductionKey) {
        LOG.info("handleDeletedAlarm: id:{}, ReductionKey: {}", alarmId, reductionKey);
    }
}
