package com.sip.softphone.bean;

import org.pjsip.pjsua2.CallInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hari on 11/4/17.
 */

public class CallerInfo {

    private static final String UNKNOWN = "Unknown";
    private final Pattern displayNameAndRemoteUriPattern = Pattern.compile("^\"([^\"]+).*?sip:(.*?)>$");
    private final Pattern remoteUriPattern = Pattern.compile("^.*?sip:(.*?)>$");
    private String displayName;
    private String remoteUri;

    public CallerInfo(final CallInfo callInfo) {
        String temp = callInfo.getRemoteUri();

        if (temp == null || temp.isEmpty()) {
            displayName = remoteUri = UNKNOWN;
            return;
        }

        Matcher completeInfo = displayNameAndRemoteUriPattern.matcher(temp);
        if (completeInfo.matches()) {
            displayName = completeInfo.group(1);
            remoteUri = completeInfo.group(2);

        } else {
            Matcher remoteUriInfo = remoteUriPattern.matcher(temp);
            if (remoteUriInfo.matches()) {
                displayName = remoteUri = remoteUriInfo.group(1);
            } else {
                displayName = remoteUri = UNKNOWN;
            }
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRemoteUri() {
        return remoteUri;
    }
}