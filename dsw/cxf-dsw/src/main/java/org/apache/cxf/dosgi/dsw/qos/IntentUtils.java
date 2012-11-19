package org.apache.cxf.dosgi.dsw.qos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.dosgi.dsw.util.Utils;
import org.osgi.framework.BundleContext;
import org.osgi.service.remoteserviceadmin.RemoteConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntentUtils {
    private static final Logger LOG = LoggerFactory.getLogger(IntentUtils.class);

    public static String formatIntents(String[] intents) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String intent : intents) {
            if (first) {
                first = false;
            } else {
                sb.append(' ');
            }
            sb.append(intent);
        }
        return sb.toString();
    }

    public static String[] parseIntents(String intentsSequence) {
        return intentsSequence == null ? new String[] {} : intentsSequence.split(" ");
    }

    public static IntentMap getIntentMap(BundleContext bundleContext) {
        IntentMap im = null;
        try {
            im = IntentUtils.readIntentMap(bundleContext);
        } catch (Throwable t) {
            LOG.warn("Intent map load failed: ", t);
        }
        if (im == null) {
            // Couldn't read an intent map
            LOG.debug("Using default intent map");
            im = new IntentMap();
            im.setIntents(new HashMap<String, Object>());
        }
        return im;
    }

    
    
    public static IntentMap readIntentMap(BundleContext bundleContext) {
        return new DefaultIntentMapFactory().create();
    }

    @SuppressWarnings("rawtypes")
    public static String[] getAllRequiredIntents(Map serviceProperties) {
        // Get the intents that need to be supported by the RSA
        String[] requiredIntents = Utils.normalizeStringPlus(serviceProperties.get(RemoteConstants.SERVICE_EXPORTED_INTENTS));
        if (requiredIntents == null) {
            requiredIntents = new String[0];
        }

        { // merge with extra intents;
            String[] requiredExtraIntents = Utils.normalizeStringPlus(serviceProperties.get(RemoteConstants.SERVICE_EXPORTED_INTENTS_EXTRA));
            if (requiredExtraIntents != null && requiredExtraIntents.length > 0) {

                requiredIntents = IntentUtils.mergeArrays(requiredIntents, requiredExtraIntents);
            }
        }

        return requiredIntents;
    }

    @SuppressWarnings("rawtypes")
    public static String[] getInetntsImplementedByTheService(Map serviceProperties) {
        // Get the Intents that are implemented by the service
        String[] serviceIntents = Utils.normalizeStringPlus(serviceProperties.get(RemoteConstants.SERVICE_INTENTS));

        return serviceIntents;
    }

    public static String[] mergeArrays(String[] a1, String[] a2) {
        if(a1==null) return a2;
        if(a2==null) return a1;

        List<String> list = new ArrayList<String>(a1.length + a2.length);

        for (String s : a1) {
            list.add(s);
        }

        for (String s : a2) {
            if (!list.contains(s))
                list.add(s);
        }

        return list.toArray(new String[list.size()]);
    }

}