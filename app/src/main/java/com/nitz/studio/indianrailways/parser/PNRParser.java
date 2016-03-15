package com.nitz.studio.indianrailways.parser;

import com.nitz.studio.indianrailways.model.PNRStatusModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpoddar on 10/28/15.
 */
public class PNRParser {
    public static List<PNRStatusModel> parseFeed(String content) {

        try {
            JSONObject jsonObject = new JSONObject(content);

            PNRStatusModel.response_code = jsonObject.getInt("response_code");
            PNRStatusModel.error = jsonObject.getBoolean("error");
            PNRStatusModel.chart_prepared = jsonObject.getString("chart_prepared");
            PNRStatusModel.class1 = jsonObject.getString("class");
            PNRStatusModel.total_passengers = jsonObject.getInt("total_passengers");
            PNRStatusModel.train_name = jsonObject.getString("train_name");
            PNRStatusModel.train_num = jsonObject.getString("train_num");
            PNRStatusModel.doj = jsonObject.getString("doj");

            JSONObject from_station = jsonObject.getJSONObject("from_station");
            PNRStatusModel.from_station_name = from_station.getString("code");

            JSONObject to_station = jsonObject.getJSONObject("to_station");
            PNRStatusModel.to_station_name = to_station.getString("code");

            JSONObject boarding_point = jsonObject.getJSONObject("boarding_point");
            PNRStatusModel.boarding_point_name = boarding_point.getString("name");

            JSONObject reservation_upto = jsonObject.getJSONObject("reservation_upto");
            PNRStatusModel.reserved_upto_name = reservation_upto.getString("name");

            List<PNRStatusModel> pnrStatusModelList = new ArrayList<>();

            for(int i=0;i<PNRStatusModel.total_passengers;i++){
                    PNRStatusModel model = new PNRStatusModel();
                    JSONArray passengers = jsonObject.getJSONArray("passengers");
                    JSONObject passObj = passengers.getJSONObject(i);
                    model.setSerialNo(passObj.getInt("no"));
                    model.setBookingStatus(passObj.getString("booking_status"));
                    model.setCurrentStatus(passObj.getString("current_status"));
                pnrStatusModelList.add(model);
            }
            return pnrStatusModelList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
