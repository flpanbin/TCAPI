package com.tc.test;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class JsonEncap {

	private List<BaseModel> modelList = new ArrayList<BaseModel>();

	public List<BaseModel> getModelList() {
		return modelList;
	}

	public JsonEncap(List<BaseModel> modelList) {
		super();
		this.modelList = (List<BaseModel>) modelList;
	}

	public JsonEncap() {
		super();
	}

	public String toJson() {
		List<BaseModel> removeModels = new ArrayList<BaseModel>();
		for (int i = 0; i < modelList.size(); i++) {
			BaseModel tempModel = modelList.get(i);

			// if (tempModel.getId().equals(tempModel.getUpId()))
			if (tempModel.condition())
				continue;
			String upIndexId = tempModel.getUpId();
			BaseModel upTempExam = new BaseModel(upIndexId);
			BaseModel upModel = modelList.get(modelList.indexOf(upTempExam));

			List<BaseModel> subBaseModels = upModel.getSubModels();
			if (subBaseModels == null) {
				subBaseModels = new ArrayList<BaseModel>();
			}
			subBaseModels.add(tempModel);
			upModel.setSubModels(subBaseModels);
			removeModels.add(tempModel);
		}

		for (BaseModel i : removeModels) {
			modelList.remove(i);
		}

		Gson gson = new Gson();
		String jsonString = gson.toJson(modelList);
		return jsonString;
	}

}
