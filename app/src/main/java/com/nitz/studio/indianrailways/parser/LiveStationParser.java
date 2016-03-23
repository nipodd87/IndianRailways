package com.nitz.studio.indianrailways.parser;

import com.nitz.studio.indianrailways.model.LiveStationModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpoddar on 3/18/16.
 */
public class LiveStationParser {

    public static List<LiveStationModel> parseFeed(String content){
        try {
            List<LiveStationModel> liveStationModelList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(content);
            LiveStationModel.responseCode = jsonObject.getInt("response_code");
            LiveStationModel.totalTrain = jsonObject.getInt("total");
            JSONArray liveStationArray = jsonObject.getJSONArray("train");

            for(int i=0;i<LiveStationModel.totalTrain;i++){
                StringBuffer sb = new StringBuffer();
                LiveStationModel model = new LiveStationModel();
                JSONObject trainObj = liveStationArray.getJSONObject(i);
                model.setTrainName(trainObj.getString("name"));
                model.setTrainNumber(trainObj.getString("number"));
                model.setSchArrival(trainObj.getString("scharr"));
                model.setSchDept(trainObj.getString("schdep"));
                model.setActualArrival(trainObj.getString("actarr"));
                model.setActualDept(trainObj.getString("actdep"));
                liveStationModelList.add(model);
            }
            return liveStationModelList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
