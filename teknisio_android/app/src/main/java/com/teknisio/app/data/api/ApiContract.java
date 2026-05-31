package com.teknisio.app.data.api;

import com.teknisio.app.BuildConfig;

public final class ApiContract {

    private ApiContract() {
        // Utility class
    }

    /*
     * Railway backend URL.
     */
    public static final String BASE_URL = BuildConfig.BASE_URL;

    public static final String API_PREFIX = "/api";

    public static String bearerToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return "";
        }
        return "Bearer " + token;
    }

    public static final class Auth {
        private Auth() {}

        public static final String LOGIN = API_PREFIX + "/auth/login";
        public static final String REGISTER_CUSTOMER = API_PREFIX + "/auth/register/customer";
        public static final String REGISTER_TECHNICIAN = API_PREFIX + "/auth/register/technician";
        public static final String PROFILE = API_PREFIX + "/auth/profile";
    }

    public static final class DeviceCategories {
        private DeviceCategories() {}

        public static final String LIST = API_PREFIX + "/device-categories";
        public static final String DETAIL = API_PREFIX + "/device-categories/{deviceCategoryId}";
    }

    public static final class TechnicianDeviceCategories {
        private TechnicianDeviceCategories() {}

        public static final String LIST = API_PREFIX + "/technicians/device-categories";
        public static final String ADD = API_PREFIX + "/technicians/device-categories";
        public static final String DELETE = API_PREFIX + "/technicians/device-categories/{deviceCategoryId}";
    }

    public static final class CustomerTechnicians {
        private CustomerTechnicians() {}

        public static final String LIST = API_PREFIX + "/customers/technicians";
        public static final String DETAIL = API_PREFIX + "/customers/technicians/{technicianProfileId}";

        public static final String QUERY_DEVICE_CATEGORY_ID = "deviceCategoryId";
        public static final String QUERY_AVAILABILITY_STATUS = "availabilityStatus";
        public static final String QUERY_SORT = "sort";
    }

    public static final class CustomerServiceRequests {
        private CustomerServiceRequests() {}

        public static final String CREATE = API_PREFIX + "/customers/service-requests";
        public static final String LIST = API_PREFIX + "/customers/service-requests";
        public static final String DETAIL = API_PREFIX + "/customers/service-requests/{serviceRequestId}";
        public static final String CANCEL = API_PREFIX + "/customers/service-requests/{serviceRequestId}/cancel";

        public static final String QUERY_STATUS = "status";
    }

    public static final class TechnicianServiceRequests {
        private TechnicianServiceRequests() {}

        public static final String LIST = API_PREFIX + "/technicians/service-requests";
        public static final String DETAIL = API_PREFIX + "/technicians/service-requests/{serviceRequestId}";
        public static final String ACCEPT = API_PREFIX + "/technicians/service-requests/{serviceRequestId}/accept";
        public static final String REJECT = API_PREFIX + "/technicians/service-requests/{serviceRequestId}/reject";
        public static final String START = API_PREFIX + "/technicians/service-requests/{serviceRequestId}/start";
        public static final String COMPLETE = API_PREFIX + "/technicians/service-requests/{serviceRequestId}/complete";

        public static final String QUERY_STATUS = "status";
        public static final String QUERY_SORT = "sort";
    }

    public static final class RequestStatus {
        private RequestStatus() {}

        public static final String WAITING = "WAITING";
        public static final String ACCEPTED = "ACCEPTED";
        public static final String ON_PROGRESS = "ON_PROGRESS";
        public static final String COMPLETED = "COMPLETED";
        public static final String CANCELLED = "CANCELLED";
        public static final String REJECTED = "REJECTED";
    }

    public static final class TechnicianSort {
        private TechnicianSort() {}

        public static final String LATEST = "latest";
        public static final String OLDEST = "oldest";
    }

    public static final class CustomerTechnicianSort {
        private CustomerTechnicianSort() {}

        public static final String RATING = "rating";
        public static final String TOTAL_JOBS = "totalJobs";
        public static final String NAME = "name";
    }

    public static final class AvailabilityStatus {
        private AvailabilityStatus() {}

        public static final String ONLINE = "ONLINE";
        public static final String OFFLINE = "OFFLINE";
        public static final String BUSY = "BUSY";
        public static final String ON_LEAVE = "ON_LEAVE";
    }
}
