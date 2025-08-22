package com.example.ei;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import java.io.File;

public class APIControlMediator extends AbstractMediator {

    @Override
    public boolean mediate(MessageContext context) {

        String apiName = (String) context.getProperty("apiName");
        String action = (String) context.getProperty("action");
        String carbonHome = System.getProperty("carbon.home");

        String apiPath = carbonHome + "/repository/deployment/server/synapse-configs/default/api/" + apiName + ".xml";
        String disabledPath = carbonHome + "/repository/deployment/server/synapse-configs/default/api-disabled/" + apiName + ".xml";

        File apiFile = new File(apiPath);
        File disabledFile = new File(disabledPath);

        String status;

        try {
            if ("disable".equalsIgnoreCase(action)) {
                if (apiFile.exists()) {
                    apiFile.renameTo(disabledFile);
                    status = "DISABLED";
                } else if (disabledFile.exists()) {
                    status = "ALREADY_DISABLED";
                } else {
                    status = "NOT_FOUND";
                }
            } else if ("enable".equalsIgnoreCase(action)) {
                if (disabledFile.exists()) {
                    disabledFile.renameTo(apiFile);
                    status = "ENABLED";
                } else if (apiFile.exists()) {
                    status = "ALREADY_ENABLED";
                } else {
                    status = "NOT_FOUND";
                }
            } else {
                status = "UNKNOWN_ACTION";
            }

            context.setProperty("apiStatus", status);
            context.setProperty("apiName", apiName);

        } catch (Exception e) {
            context.setProperty("apiStatus", "ERROR: " + e.getMessage());
            context.setProperty("apiName", apiName);
        }

        return true;
    }
}
