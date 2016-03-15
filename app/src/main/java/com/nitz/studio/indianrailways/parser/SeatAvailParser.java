package com.nitz.studio.indianrailways.parser;

import com.nitz.studio.indianrailways.model.SeatAvailModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpoddar on 11/7/15.
 */
public class SeatAvailParser {
    public static List<SeatAvailModel> parseFeed(String content){
        try {
            JSONObject jsonObject;
            jsonObject = new JSONObject(content);

            List<SeatAvailModel> seatAvailModelList = new ArrayList<>();

            SeatAvailModel.responseCode = jsonObject.getInt("response_code");
            SeatAvailModel.trName = jsonObject.getString("train_name");
            SeatAvailModel.trNo = jsonObject.getString("train_number");
            SeatAvailModel.error = jsonObject.getString("error");

            JSONObject classObj = jsonObject.getJSONObject("class");
            SeatAvailModel.classCode = classObj.getString("class_code");
            SeatAvailModel.className = classObj.getString("class_name");

            JSONArray availArray = jsonObject.getJSONArray("availability");

            for(int i=0; i<availArray.length(); i++){
                SeatAvailModel model = new SeatAvailModel();
                JSONObject availObj = availArray.getJSONObject(i);
                model.setTravelDate(availObj.getString("date"));
                model.setStatus(availObj.getString("status"));

                seatAvailModelList.add(model);
            }

            return seatAvailModelList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
