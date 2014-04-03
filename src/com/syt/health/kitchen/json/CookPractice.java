package com.syt.health.kitchen.json;

import java.io.Serializable;

/**
 * 烹饪步骤
 * 
 * @author tom
 *
 */
public class CookPractice implements Serializable{
	public static final String STEP_FIELD = "step";
	public static final String DESC_FIELD = "desc";
	public static final String USETIME_FIELD = "usetime";
	
	private int step;		// 步骤
	private String desc;	// 烹饪步骤详述
	private int usetime;	// 耗时。单位分钟
	
	public CookPractice() {
		super();
	}

	public CookPractice(int step, String desc, int usetime) {
		super();
		this.step = step;
		this.desc = desc;
		this.usetime = usetime;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getUsetime() {
		return usetime;
	}

	public void setUsetime(int usetime) {
		this.usetime = usetime;
	}

	@Override
	public boolean equals(Object obj) {
		CookPractice cookpractice = (CookPractice)obj;
		
		return cookpractice.getStep() == step
				&& cookpractice.getDesc().equalsIgnoreCase(desc)
				&& cookpractice.getUsetime() == usetime;
	}

	@Override
	public String toString() {
		return "CookPractice [step=" + step + ", desc=" + desc + ", usetime="
				+ usetime + "]";
	}
}
