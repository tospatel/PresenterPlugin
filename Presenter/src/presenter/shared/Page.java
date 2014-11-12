package presenter.shared;


public class Page {
	private Number limit;
	private Number offset;
	private Number total;

	public Number getLimit() {
		return this.limit;
	}

	public void setLimit(Number limit) {
		this.limit = limit;
	}

	public Number getOffset() {
		return this.offset;
	}

	public void setOffset(Number offset) {
		this.offset = offset;
	}

	public Number getTotal() {
		return this.total;
	}

	public void setTotal(Number total) {
		this.total = total;
	}
}
