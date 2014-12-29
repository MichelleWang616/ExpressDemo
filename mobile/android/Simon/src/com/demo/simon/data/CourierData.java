
package com.demo.simon.data;

import java.util.ArrayList;
import java.util.List;

import com.demo.simon.datamodel.Courier;

public class CourierData
{
    private static String sRequestId = "-1";
    private static List<Courier> sRespondedCourier = new ArrayList<Courier>();
    private static int sCurrentCourierIndex = 0;

    private static boolean sSingleView = false;

    public static void setCourierData(List<Courier> courier, String requestId, boolean isSingleView) {
        sRespondedCourier = courier;
        sRequestId = requestId;
        sCurrentCourierIndex = 0;
        sSingleView = isSingleView;
    }

    public static Courier getCurrentCourier() {
        if (sCurrentCourierIndex < 0 || sCurrentCourierIndex >= sRespondedCourier.size()) {
            return null;
        } else {
            return sRespondedCourier.get(sCurrentCourierIndex);
        }
    }

    public static boolean isSingleView() {
        return sSingleView;
    }

    public static boolean hasPreCourier() {
        return sCurrentCourierIndex > 0;
    }

    public static boolean hasNextCourier() {
        return sCurrentCourierIndex < sRespondedCourier.size() - 1;
    }

    public static void moveToPreCourier() {
        sCurrentCourierIndex--;
    }

    public static String getRequestId() {
        return sRequestId;
    }

    public static void moveToNextCourier() {
        sCurrentCourierIndex++;
    }
}
