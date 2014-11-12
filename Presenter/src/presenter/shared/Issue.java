package presenter.shared;

import java.util.List;

public class Issue {
	private List collection;
	private String href;
	private Page page;
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

	public Page getPage() {
		return this.page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
