package com.tc.test;

public class ExamInfo extends BaseModel {

	private String examName;

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public ExamInfo(String examName) {
		super();
		this.examName = examName;
	}

	public ExamInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ExamInfo(String indexId, String upIndexId) {
		super(indexId, upIndexId);
		// TODO Auto-generated constructor stub
	}

	public ExamInfo(String indexId, String name, String upIndexId) {
		super(indexId, upIndexId);
		// TODO Auto-generated constructor stub
		this.examName = name;
	}

	@Override
	public boolean condition() {
		// TODO Auto-generated method stub
		return (getId().equals(getUpId()));
	}

}
