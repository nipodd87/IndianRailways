package com.nitz.studio.indianrailways.parser;

import com.nitz.studio.indianrailways.model.TrainStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpoddar on 10/31/15.
 */
public class TrainStatusParser {

    public static List<TrainStatus> parseFeed(String content){
        try {
            JSONObject jsonObject = new JSONObject(content);
            List<TrainStatus> trainList = new ArrayList<TrainStatus>();

            TrainStatus.responseCode = jsonObject.getInt("response_code");
            TrainStatus.position = jsonObject.getString("position");

            JSONArray route = jsonObject.getJSONArray("route");

            for(int i=0;i<route.length();i++){
                TrainStatus trainStatus = new TrainStatus();
                JSONObject routeObject = route.getJSONObject(i);
                trainStatus.setSerialNo(routeObject.getInt("no"));
                trainStatus.setScheduleArrival(routeObject.getString("scharr"));
                trainStatus.setActualArrival(routeObject.getString("actarr"));
                trainStatus.setStationName(routeObject.getString("station"));
                trainStatus.setStatusOfArrival(routeObject.getString("status"));
                trainStatus.setHasArrived(routeObject.getBoolean("has_arrived"));
                trainStatus.setHasDeparted(routeObject.getBoolean("has_departed"));
                trainStatus.setLatemin(routeObject.getInt("latemin"));
                trainStatus.setActualArrivalDate(routeObject.getString("actarr_date"));
                trainStatus.setScheduleArrivalDate(routeObject.getString("scharr_date"));
                trainList.add(trainStatus);
            }
            return trainList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
