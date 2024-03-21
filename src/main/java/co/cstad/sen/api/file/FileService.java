package co.cstad.sen.api.file;

import co.cstad.sen.api.file.web.FileDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    FileDto uploadSingle(MultipartFile file);
    List<FileDto> uploadMultiple(List<MultipartFile> files);
    FileDto findByName(String name) throws IOException;
    void delete(String name);
    Resource download(String name) throws IOException;
}