package com.mrpio.mrpowermanager.Model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class SleepCommand extends Command implements Serializable {
    public SleepCommand(LocalDateTime commandSentDate, LocalDateTime commandScheduledDate) {
        super(commandSentDate, commandScheduledDate,"SleepCommand");
    }
}
