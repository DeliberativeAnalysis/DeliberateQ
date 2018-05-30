package com.github.deliberateq.qsort;

import java.util.HashSet;
import java.util.Set;

public class Participant {
	public Participant(String id) {
		super();
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Participant other = (Participant) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private final String id;

	public String getId() {
		return id;
	}

	public Set<String> getTypes() {
		return types;
	}

	private final Set<String> types = new HashSet<String>();
}
