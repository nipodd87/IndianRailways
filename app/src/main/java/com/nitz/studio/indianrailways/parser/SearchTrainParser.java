package com.nitz.studio.indianrailways.parser;

import com.nitz.studio.indianrailways.model.TrainModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpoddar on 11/3/15.
 */
public class SearchTrainParser {

    public static List<TrainModel> parseFeed(String content){
        try {
            List<TrainModel> trainModelList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(content);
            TrainModel.responseCode = jsonObject.getInt("response_code");
            TrainModel.totalTrain = jsonObject.getInt("total");


            for(int i=0;i<TrainModel.totalTrain;i++){
                StringBuffer sb = new StringBuffer();
                TrainModel model = new TrainModel();
                JSONArray trainArray = jsonObject.getJSONArray("train");
                JSONObject trainObj = trainArray.getJSONObject(i);
                model.setSerialNo(trainObj.getInt("no"));
                model.setTrainNumber(trainObj.getString("number"));
                model.setTrainName(trainObj.getString("name"));
                model.setArrivalTime(trainObj.getString("dest_arrival_time"));
                model.setDeptTime(trainObj.getString("src_departure_time"));
                JSONArray days = trainObj.getJSONArray("days");
                String prefix = "";
                for(int x=0;x<7;x++){
                    JSONObject dayObj = days.getJSONObject(x);
                    if (dayObj.getString("runs").equals("Y")){
                        sb.append(prefix);
                        prefix = ",";
                        sb.append(dayObj.getString("day-code"));
                    }
                    model.setDaysRun(sb.toString());
                }
                trainModelList.add(model);
            }
            return trainModelList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
