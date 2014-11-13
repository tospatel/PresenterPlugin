package presenter.util;

public class StringUtility {
	private StringUtility() {
	}

	public static String checkIfNullThenEmpty(Object value) {
		return value != null ? value.toString() : "";
	}
}
