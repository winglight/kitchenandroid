package com.syt.health.kitchen.json;

import java.util.Arrays;

/**
 * 带评价的菜品
 * 
 * @author tom
 *
 */
public class EvalCourse {
	private String[] evaluation;	// 对该菜品的评价
	private Course course;			// 菜品
	
	public EvalCourse() {
		super();
	}

	public EvalCourse(String[] evaluation, Course course) {
		super();
		this.evaluation = evaluation;
		this.course = course;
	}

	public String[] getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(String[] evaluation) {
		this.evaluation = evaluation;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	@Override
	public String toString() {
		return "EvalCourse [evaluation=" + Arrays.toString(evaluation)
				+ ", course=" + course + "]";
	}
}
