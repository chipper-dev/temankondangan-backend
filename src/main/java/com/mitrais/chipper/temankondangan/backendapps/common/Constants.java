package com.mitrais.chipper.temankondangan.backendapps.common;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String DEFAULT_IMAGE = "image/defaultprofile.jpg";

    public static class RatingDataKey {

        private RatingDataKey() {
            throw new IllegalStateException("Utility class");
        }

        public static final String AVG = "average";
        public static final String TOT = "total";
    }
}
