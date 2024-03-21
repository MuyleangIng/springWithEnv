
package co.cstad.sen.api.file.web;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileDto {
    // Add getter methods for individual fields
    private final String extension;
    private final String name;
    private final Long size;
    private final String url;
    private final String downloadUrl;
    private final String additionalInfo;

}