package com.xxl.job.admin.core.alarm.impl;

import com.loctek.internalservice.alfred.remote.AlarmRemoteService;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Component
public class AlfredAlarm implements JobAlarm {
    private static Logger log = LoggerFactory.getLogger(EmailJobAlarm.class);

    @DubboReference(group = "loctek-alfred", check = false)
    private AlarmRemoteService alarmRemoteService;

    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        Date time = Optional.ofNullable(jobLog.getHandleTime()).orElse(jobLog.getTriggerTime());
        String message = Optional.ofNullable(jobLog.getHandleMsg()).orElse(jobLog.getTriggerMsg());
        try {
            String empNoStr = info.getAlarmEmail();
            if (StringUtils.isBlank(empNoStr)){
                return true;
            }
            String[] empNos = empNoStr.split(",");
            alarmRemoteService.alarm(jobLog.getExecutorHandler(),String.valueOf(jobLog.getId()),time, Arrays.asList(empNos),message);
        } catch (Exception e) {
            log.error("消息发送失败",e);
        }
        return true;
    }
}
