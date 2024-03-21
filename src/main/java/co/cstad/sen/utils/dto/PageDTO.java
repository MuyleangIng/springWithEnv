package co.cstad.sen.utils.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageDTO<T> {
    private long total;
    private List<T> list;
    private int pages;
    private int pageNum;
    private int pageSize;
    private long startRow;
    private long endRow;
    private boolean hasNext;
    private boolean hasPrev;
    private boolean empty;
    private boolean first;
    private boolean last;
}