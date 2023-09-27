package nl.devoorkant.sbdr.business.transfer;

import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Sort;

public class PageTransfer<T> implements Iterable<T> {
	private int number;
	private int size;
	private int totalPages;
	private int numberOfElements;
	private long totalElements;
	private boolean hasPrevious;
	private boolean isFirstPage;
	private boolean hasNextPage;
	private boolean isLastPage;
	private List<T> content;
	private boolean hasContent;
	private Sort sort;
	
	public int getNumber() {
		return number;
	}


	public void setNumber(int number) {
		this.number = number;
	}


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


	public int getTotalPages() {
		return totalPages;
	}


	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}


	public int getNumberOfElements() {
		return numberOfElements;
	}


	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}


	public long getTotalElements() {
		return totalElements;
	}


	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}


	public boolean isHasPreviousPage() {
		return hasPrevious;
	}


	public void setHasPreviousPage(boolean hasPrevious) {
		this.hasPrevious = hasPrevious;
	}


	public boolean isFirstPage() {
		return isFirstPage;
	}


	public void setFirstPage(boolean isFirstPage) {
		this.isFirstPage = isFirstPage;
	}


	public boolean isHasNextPage() {
		return hasNextPage;
	}


	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}


	public boolean isLastPage() {
		return isLastPage;
	}


	public void setLastPage(boolean isLastPage) {
		this.isLastPage = isLastPage;
	}


	public boolean isHasContent() {
		return hasContent;
	}


	public void setHasContent(boolean hasContent) {
		this.hasContent = hasContent;
	}


	public Sort getSort() {
		return sort;
	}


	public void setSort(Sort sort) {
		this.sort = sort;
	}


	@Override
	public Iterator<T> iterator() {
		if (content != null)
			return content.iterator();
		else
			return null;
	}	
	
	public List<T> getContent()
	{
		return content;
	}
	
	public void setContent(List<T> content)
	{
		this.content = content;
	}
}
