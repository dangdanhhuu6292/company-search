package nl.devoorkant.sbdr.ws.transfer;


import javax.ws.rs.QueryParam;


public class CompanyFilterCriteria {
	@QueryParam("pageNumber") public Integer pageNumber;
	@QueryParam("sortDir") public String sortDir; // asc, desc
	@QueryParam("sortedBy") public String sortedBy;
	@QueryParam("filterValue") public String filterValue; 	
}
