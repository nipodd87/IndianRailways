package com.nitz.studio.indianrailways.parser;


import com.nitz.studio.indianrailways.model.FareTrainModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpoddar on 11/5/15.
 */
public class FareTrainParser {

    public static List<FareTrainModel> parseFeed(String content){
        try {

            JSONObject jsonObject = new JSONObject(content);

            List<FareTrainModel> fareTrainModelList = new ArrayList<>();


            FareTrainModel.responseCode = jsonObject.getInt("response_code");

            JSONObject trainObject = jsonObject.getJSONObject("train");

            FareTrainModel.trainName = trainObject.getString("name");
            FareTrainModel.trainNo = trainObject.getString("number");

            JSONArray fareArray = jsonObject.getJSONArray("fare");
            for(int i=0;i<fareArray.length();i++){
                FareTrainModel model = new FareTrainModel();
                JSONObject fareObject = fareArray.getJSONObject(i);
                model.setClassCode(fareObject.getString("code"));
                model.setClassFare(fareObject.getString("fare"));
                model.setClassName(fareObject.getString("name"));
                fareTrainModelList.add(model);
            }

            return fareTrainModelList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
