package com.nitz.studio.indianrailways.parser;

import com.nitz.studio.indianrailways.model.TrainRouteStn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpoddar on 11/1/15.
 */
public class TrainRouteParser {
    public static List<TrainRouteStn> parseFeed(String content){
        try {
            List<TrainRouteStn> trainRouteStnList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(content);
            TrainRouteStn.responseCode = jsonObject.getInt("response_code");

            JSONObject train = jsonObject.getJSONObject("train");
            TrainRouteStn.trainName = train.getString("name");

            JSONArray runningDays = train.getJSONArray("days");
            StringBuffer sb = new StringBuffer();
            String prefix = "";
            for(int i=0; i<runningDays.length();i++){
                JSONObject runDay = runningDays.getJSONObject(i);
                if(runDay.getString("runs").equals("Y")) {
                    sb.append(prefix);
                    prefix = ",";
                    sb.append(runDay.getString("day-code"));
                }
            }
            TrainRouteStn.days = sb.toString();

            JSONArray routeArray = jsonObject.getJSONArray("route");
            TrainRouteStn.fromStation = routeArray.getJSONObject(0).getString("fullname");
            for(int i=0;i<routeArray.length();i++) {
                TrainRouteStn trainRouteStn = new TrainRouteStn();
                JSONObject routeObject = routeArray.getJSONObject(i);

                trainRouteStn.setSerialNo(routeObject.getInt("no"));
                trainRouteStn.setStationName(routeObject.getString("fullname"));
                trainRouteStn.setDay(routeObject.getInt("day"));
                trainRouteStn.setDistance(routeObject.getInt("distance"));
                trainRouteStn.setScheduleArrival(routeObject.getString("scharr"));
                trainRouteStn.setScheduleDept(routeObject.getString("schdep"));

                trainRouteStnList.add(trainRouteStn);
            }
            return trainRouteStnList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
