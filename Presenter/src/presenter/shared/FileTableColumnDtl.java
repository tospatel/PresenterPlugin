package presenter.shared;

public interface FileTableColumnDtl {

	String id = "Id";
	String name = "Kind";
	String file = "File";
	String location = "Location";
	String code = "Code";
	String appId = "App Id";
	String description = "Description";
	String solution = "Solution";
	String compliance = "Compliance";
	String risk = "Risk";
	String startLine = "Start Line";
	String endLine = "End Line";
	String lineNo = "Line No";
	String colStart = "Col Start";
	String colEnd = "Col End";
	String hash = "Hash";
	String impact = "Impact";
	String classname = "Class";
	String status = "Status";
	String formattedCode = "formattedCode";
	String traceId = "Trace Id";

	int idIndex = 0;
	int traceIdIndex = 1;
	int nameIndex = 2;
	int fileIndex = 3;

	int appIdIndex = 4;
	int statusIndex = 5;
	int classIndex = 6;
	int riskIndex = 7;

	int locationIndex = 8;
	int impactIndex = 9;
	int codeIndex = 10;
	int startLineIndex = 11;
	int endLineIndex = 12;
	int lineNoIndex = 13;
	int colStartIndex = 14;
	int colEndIndex = 15;
	int hashIndex = 16;
	int descriptionIndex = 17;
	int solutionIndex = 18;
	int complianceIndex = 19;
	int formattedCodeIndex = 20;

	int idWidth = 55;
	int statusWidth = 80;
	int nameWidth = 120;
	int traceIdWidth = 60;
	int fileWidth = 150;
	int riskWidth = 80;
	int classWidth = 100;
	int appIdWidth = 55;
	int locationWidth = 0;
	int codeWidth = 0;
	int startLineWidth = 0;
	int endLineWidth = 0;
	int lineNoWidth = 0;
	int hashWidth = 0;

	int descriptionWidth = 0;
	int solutionWidth = 0;
	int complianceWidth = 0;

	int colStartWidth = 0;
	int colEndWidth = 0;
	int impactWidth = 0;
	int formattedCodeWidth = 0;
	// int idWidth = 50;
	// int nameWidth = 110;
	// int fileWidth = 180;
	// int locationWidth = 65;
	// int codeWidth = 250;
	// int startLineWidth = 65;
	// int endLineWidth = 65;
	// int lineNoWidth = 55;
	// int hashWidth = 55;
	// int appIdWidth = 70;
	// int descriptionWidth = 200;
	// int solutionWidth = 100;
	// int complianceWidth = 100;
	// int riskWidth = 100;
	// int colStartWidth = 60;
	// int colEndWidth = 60;
}
