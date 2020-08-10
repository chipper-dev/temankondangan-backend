package com.mitrais.chipper.temankondangan.backendapps.common;

import java.time.LocalDateTime;

public final class CommonFunction {

    private CommonFunction() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isEventFinished(LocalDateTime startDateTime, LocalDateTime finishDateTime) {
        return (finishDateTime != null && LocalDateTime.now().isAfter(finishDateTime))
                || (finishDateTime == null && LocalDateTime.now().isAfter(startDateTime));
    }

}
