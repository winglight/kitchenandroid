package com.syt.health.kitchen.db.common;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "nutrient")
public class NutrientModel implements Serializable {

	public static final String FIELD_ID = "id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_UNIT = "unit";
	public static final String FIELD_NORMAL = "normal";
	public static final String FIELD_START = "start";
	public static final String FIELD_GOOD = "good";
	public static final String FIELD_PERFECT = "perfect";
	public static final String FIELD_EXCESS = "excess";
	public static final String FIELD_MAX = "maxexcess";
	
	public static final String NUTRIENT_B1 = "维B1";
	public static final String NUTRIENT_B2 = "维B2";

	@DatabaseField(id = true, index = true, columnName = FIELD_ID)
	private int id = -1;
	@DatabaseField(columnName = FIELD_NAME)
	private String name;
	@DatabaseField(columnName = FIELD_UNIT)
	private String unit;
	@DatabaseField(columnName = FIELD_NORMAL)
	private double normal;
	@DatabaseField(columnName = FIELD_START)
	private double start;
	@DatabaseField(columnName = FIELD_GOOD)
	private double good;
	@DatabaseField(columnName = FIELD_PERFECT)
	private double perfect;
	@DatabaseField(columnName = FIELD_EXCESS)
	private double excess;
	@DatabaseField(columnName = FIELD_MAX)
	private double max;

	private double content = -1;

	public NutrientModel() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getNormal() {
		return normal;
	}

	public void setNormal(double normal) {
		this.normal = normal;
	}

	public double getStart() {
		return start;
	}

	public void setStart(double start) {
		this.start = start;
	}

	public double getGood() {
		return good;
	}

	public void setGood(double good) {
		this.good = good;
	}

	public double getPerfect() {
		return perfect;
	}

	public void setPerfect(double perfect) {
		this.perfect = perfect;
	}

	public double getExcess() {
		return excess;
	}

	public void setExcess(double excess) {
		this.excess = excess;
	}

	public double getContent() {
		return content;
	}

	public void setContent(double content) {
		this.content = content;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final NutrientModel other = (NutrientModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getCurrentPositionDesc(int person) {
		double event = content / person;
		if (event < 0) {
			return FIELD_START;
		}
		if (this.good < 0 || this.excess < 0) {
			if (event < this.start) {
				return FIELD_START;
			} else if (event >= this.start && event < this.perfect) {
				return FIELD_PERFECT;
			} else {
				return FIELD_GOOD;
			}
		} else {
			if (event < this.start) {
				return FIELD_START;
			} else if (event >= this.start && event < this.good) {
				return FIELD_GOOD;
			} else if (event >= this.good && event < this.perfect) {
				return FIELD_PERFECT;
			} else if (event >= this.perfect && event < this.excess) {
				return FIELD_GOOD;
			} else {
				return FIELD_EXCESS;
			}
		}
	}

	public int getCurrentValue(int person) {
		double event = content / person;
		if (event <= 0) {
			return 0;
		}
		if (event >= this.max) {
			return 100;
		}
		int value = 0;
		if (this.good < 0 || this.excess < 0) {
			if (event < this.start) {
				return (int) (event*100/(this.start*3));
			}
			if (event >= this.start && event < this.perfect) {
				return (int) (34 + ((event-this.start)*100/((this.perfect - this.start)*3)));
			}
			if (event >= this.excess) {
				return (int) (67 + ((event-this.excess)*100/((this.max - this.excess)*3)));
			}
		} else {
			if (event < this.start) {
				return (int) (event*100/(this.start*5));
			}
			if (event >= this.start && event < this.good) {
				return (int) (20 + ((event-this.start)*100/((this.good - this.start)*5)));
			}
			if (event >= this.good && event < this.perfect) {
				return (int) (40 + ((event-this.good)*100/((this.perfect - this.good)*5)));
			}
			if (event >= this.perfect && event < this.excess) {
				return (int) (60 + ((event-this.perfect)*100/((this.excess - this.perfect)*5)));
			}
			if (event >= this.excess) {
				return (int) (80 + ((event-this.excess)*100/((this.max - this.excess)*5)));
			}
		}
		return value;
	}

	public String getRangeDesc(int person) {
		String desc = "标准: ";
		if (this.good < 0 || this.excess < 0) {
//			desc += "小于" + this.start + this.unit + " 缺乏\n";
			desc += "" + String.format("%.2f", this.start*person) + "-" + String.format("%.2f", this.perfect*person)
					 + "";
//			desc += "大于" + this.perfect + this.unit + " 良好";
		} else {
//			desc += "小于" + this.start + this.unit + " 缺乏\n";
//			desc += "大于" + this.start + this.unit + ", 并且小于" + this.good
//					+ this.unit + " 良好\n";
			desc += "" + (int)(this.good*person) + "-" + (int)(this.perfect*person)
					+  "";
//			desc += "大于" + this.perfect + this.unit + ", 并且小于" + this.excess
//					+ this.unit + " 良好\n";
//			desc += "大于" + this.excess + this.unit + " 过量";
		}
		return desc;
	}
	
	public static void copy(NutrientModel destNm, NutrientModel orgNm){
		destNm.setContent(orgNm.getContent());
		destNm.setExcess(orgNm.getExcess());
		destNm.setGood(orgNm.getGood());
		destNm.setMax(orgNm.getMax());
		destNm.setName(orgNm.getName());
		destNm.setNormal(orgNm.getNormal());
		destNm.setPerfect(orgNm.getPerfect());
		destNm.setStart(orgNm.getStart());
		destNm.setUnit(orgNm.getUnit());
	}
}
