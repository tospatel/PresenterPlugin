package presenter.shared;

import java.util.List;

public class Collection {
	private Application application;
	private Number application_id;
	private String clas;
	private String class_readable;
	private String closed;
	private String compliance;
	private Dast_classes dast_classes;
	private String description;
	private String first_opened;
	private String found;
	private String href;
	private Number id;
	private Number impact;
	private String impact_readable;
	private boolean include_default_description;
	private boolean include_default_solution;
	private boolean is_locked;
	private Number likelihood;
	private String likelihood_readable;
	private String location;
	private boolean manual;
	private String modified;
	private String opened;
	private String risk;
	private String solution;
	private String status;
	private String status_unpublished;
	private List<Traces> traces;

	public Application getApplication() {
		return this.application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Number getApplication_id() {
		return this.application_id;
	}

	public void setApplication_id(Number application_id) {
		this.application_id = application_id;
	}

	public String getClas() {
		return clas;
	}

	public void setClas(String clas) {
		this.clas = clas;
	}

	public String getClass_readable() {
		return this.class_readable;
	}

	public void setClass_readable(String class_readable) {
		this.class_readable = class_readable;
	}

	public String getClosed() {
		return this.closed;
	}

	public void setClosed(String closed) {
		this.closed = closed;
	}

	public String getCompliance() {
		return this.compliance;
	}

	public void setCompliance(String compliance) {
		this.compliance = compliance;
	}

	public Dast_classes getDast_classes() {
		return this.dast_classes;
	}

	public void setDast_classes(Dast_classes dast_classes) {
		this.dast_classes = dast_classes;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFirst_opened() {
		return this.first_opened;
	}

	public void setFirst_opened(String first_opened) {
		this.first_opened = first_opened;
	}

	public String getFound() {
		return this.found;
	}

	public void setFound(String found) {
		this.found = found;
	}

	public String getHref() {
		return this.href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Number getId() {
		return this.id;
	}

	public void setId(Number id) {
		this.id = id;
	}

	public Number getImpact() {
		return this.impact;
	}

	public void setImpact(Number impact) {
		this.impact = impact;
	}

	public String getImpact_readable() {
		return this.impact_readable;
	}

	public void setImpact_readable(String impact_readable) {
		this.impact_readable = impact_readable;
	}

	public boolean getInclude_default_description() {
		return this.include_default_description;
	}

	public void setInclude_default_description(
			boolean include_default_description) {
		this.include_default_description = include_default_description;
	}

	public boolean getInclude_default_solution() {
		return this.include_default_solution;
	}

	public void setInclude_default_solution(boolean include_default_solution) {
		this.include_default_solution = include_default_solution;
	}

	public boolean getIs_locked() {
		return this.is_locked;
	}

	public void setIs_locked(boolean is_locked) {
		this.is_locked = is_locked;
	}

	public Number getLikelihood() {
		return this.likelihood;
	}

	public void setLikelihood(Number likelihood) {
		this.likelihood = likelihood;
	}

	public String getLikelihood_readable() {
		return this.likelihood_readable;
	}

	public void setLikelihood_readable(String likelihood_readable) {
		this.likelihood_readable = likelihood_readable;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean getManual() {
		return this.manual;
	}

	public void setManual(boolean manual) {
		this.manual = manual;
	}

	public String getModified() {
		return this.modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}

	public String getOpened() {
		return this.opened;
	}

	public void setOpened(String opened) {
		this.opened = opened;
	}

	public String getRisk() {
		return this.risk;
	}

	public void setRisk(String risk) {
		this.risk = risk;
	}

	public String getSolution() {
		return this.solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus_unpublished() {
		return this.status_unpublished;
	}

	public void setStatus_unpublished(String status_unpublished) {
		this.status_unpublished = status_unpublished;
	}

	public List<Traces> getTraces() {
		return traces;
	}

	public void setTraces(List<Traces> traces) {
		this.traces = traces;
	}

}
