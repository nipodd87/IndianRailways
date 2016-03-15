package com.nitz.studio.indianrailways.parser;

import com.nitz.studio.indianrailways.model.SearchStationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitinpoddar on 11/10/15.
 */
public class SearchStationParser {
        public static List<SearchStationModel> parseFeed(String content) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                List<SearchStationModel> modelList = new ArrayList<>();
                SearchStationModel.responseCode = jsonObject.getInt("response_code");
                SearchStationModel.total = jsonObject.getInt("total");
                JSONArray stationArray = jsonObject.getJSONArray("station");
                for(int i=0;i<SearchStationModel.total;i++){
                    SearchStationModel model = new SearchStationModel();
                    JSONObject stationObj = stationArray.getJSONObject(i);
                    model.setStationCode(stationObj.getString("code"));
                    model.setStationName(stationObj.getString("fullname"));
                    model.setNameCode(model.getStationName()+"-"+model.getStationCode());
                    modelList.add(model);
                }
                return modelList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
}

