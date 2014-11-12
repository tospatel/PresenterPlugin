package presenter.shared;

import java.util.List;

public class Traces {
	private List collection;
	private String href;
	private String type;

	public List getCollection() {
		return this.collection;
	}

	public void setCollection(List collection) {
		this.collection = collection;
	}

	public String getHref() {
		return this.href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
