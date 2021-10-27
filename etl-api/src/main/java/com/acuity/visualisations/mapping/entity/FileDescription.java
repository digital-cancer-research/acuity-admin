package com.acuity.visualisations.mapping.entity;

import java.util.ArrayList;
import java.util.List;

public class FileDescription extends MappingEntity implements StaticEntity {

	private String description;
	private String displayName;
	private List<EntityRule> entities = new ArrayList<EntityRule>();
	private Long sectionId;
	private double processOrder;

	public FileDescription() {
	}

	public FileDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FileDescription other = (FileDescription) obj;
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else {
			if (!description.equals(other.description)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "FileDescription [description=" + description + "]";
	}

	public List<EntityRule> getEntities() {
		return entities;
	}

	public void setEntities(List<EntityRule> entities) {
		this.entities = entities;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Long getSectionId() {
		return sectionId;
	}

	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}

	public double getProcessOrder() {
		return processOrder;
	}

	public void setProcessOrder(double processOrder) {
		this.processOrder = processOrder;
	}
}
